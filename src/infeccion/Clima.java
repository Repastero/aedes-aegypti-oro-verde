package infeccion;

/**
 * La clase {@code Clima} contiene metodos para obtener los valores relacionados con el clima del dia
 * (temperatura, humedad, precipitacion, viento y evaporacion).<p>
 * Tambien calcula diariamente los valores de Desarrollo Metabolico y Probabilidad de Mortalidad de Acuaticos;
 * e implementa metodos para acceder a los mismos.
 */
public final class Clima {
	public static int runNumber = 0; //numero de corridas ejecutadas en ela UI
	public static int dayNumber = 0; //Prop + actualisador diario
	
	private static double dmdHuevo;
	private static double dmdLarva;
	private static double dmdPupa;
	private static double ddAdulto;
	
	private static double mdAcuatico;
	
	private static double temperatureDay; // Temperatura Diaria para trabajar dentro de la clase
	private static double precipitationDay; // Precipitacion Diaria para trabajar dentro de la clase
	private static double humidityDay; // Precipitacion Diaria para trabajar dentro de la clase
	private static double vientoDiario; // Viento Diaria para trabajar dentro de la clase
	private static double evaporaionDay; // Evaporacion Diaria en ml para trabajar dentro de la clase
	
	private static final double[] Temperaturas = DataSet.CLIMA_TEMPERATURA;
	private static final double[] Precipitaciones = DataSet.CLIMA_PRECIPITACION;
	private static final double[] Hums = DataSet.CLIMA_HUMEDAD;
	private static final double[] vientos = DataSet.CLIMA_VIENTO;
	
	public static void initDayNumber() {
		++runNumber;
		dayNumber = 0;
		
		DataSet.mortalidadDiariaAdultos	= 0.01d;
		DataSet.mortalidadDiariaHuevos	= 0.07d;
	}
	
	/**
	 * Setea los valore diarios del clima y actualiza el valor de agua evaporada (Containers), 
	 * desarrollo metabolico (Acuaticos) y tasa de mortalidad (Acuaticos).
	 */
	public static void updateClima() {
		temperatureDay = getTemperature(dayNumber);
		precipitationDay = getPrecipitation(dayNumber);
		humidityDay = getHumidity(dayNumber);
		vientoDiario = getWind(dayNumber);
		updateEvaporacionDiario();
		updateDesarrollosMetabolicoDiario();
		updateDesarrolloAdultoDiario();
		updateMortalidadDiariaAcuatico();
		updateMortalidadDiariaGlobal();
		++dayNumber; // 12 ticks por dia
	}

	public  static double getTemperature(int dia)		{ return Temperaturas[dia]; }
	public 	static double getCurrentTemperature()		{ return temperatureDay; }
	public 	static double getCurrentTemperatureK()		{ return temperatureDay + 273; }
	private static double getPrecipitation (int dia)	{ return Precipitaciones[dia]; }
	public  static double getCurrentPrecipitation()		{ return precipitationDay; }
	public  static double getHumidity(int dia)			{ return Hums[dia]; }
	public  static double getCurrentHumidity()			{ return humidityDay; }
	public  static double getWind(int dia)				{ return vientos[dia]; }
	public  static double getCurrentWind()				{ return vientoDiario; }
	public  static double getEvaporation()				{ return evaporaionDay; }
	
	public  static double getDMDHuevo()	{ return dmdHuevo; }
	public  static double getDMDLarva()	{ return dmdLarva; }
	public  static double getDMDPupa()	{ return dmdPupa; }
	public  static double getDDAdulto() { return ddAdulto; }
	
	public  static double getMDAcuatico()	{ return mdAcuatico; }

	public static void updateEvaporacionDiario() {
		final double T = getCurrentTemperatureK();
		final double viento = getCurrentWind() * 0.7d;
		final double P  = Math.pow(10, 8.07131d - 1730.63d / (233.426d + getCurrentTemperature())); // presion del vapor. log(P) = 8.07131 - ((1730.63)/(233.426 + T))
		final double U  = Math.pow(viento / 1.61d, 0.78d); // U  Velocidad del viento [kmph/1.61],
		final double H  = DataSet.CLIMA_EVAPORACION; // H  Exposicion Solar
		final double MW = 6.87217184640305d; // = Math.pow(18.01528d, 2d/3d) // MW Peso molecular del agua [g/mol] // Antes 1.0 / Ahora 6.87217184640305
		final double A  = 934.579439252d; // = 1d/0.00107d // Superficie expuesta del contenedor en centimetros cuadrados
		final double k  = 26000d; // constante
		// Ema: A Model to Predict Evaporation Rates in Habitats Used by Container-Dwelling Mosquitoes, Bartlett-Healy (2011)
		final double evaporationInMl = (U*MW*A*H*P*H)/(k*T); // evaporation rate [ml/dia]
		evaporaionDay = evaporationInMl * 10d; // evaporationInMmOfHigh - siendo una superficie de 10[cm]*10[cm]*10[cm]
	}
	
	private static void updateDesarrollosMetabolicoDiario() {
		final double R = 0.98588d;
		// Huevo
		double Rdk = 0.24d;
		double Ha  = 10798d;
		double Hh  = 100000d;
		double T12 = 14184d;
		dmdHuevo = getDesarrolloMetabolicodiario(R, Rdk, Ha, Hh, T12);
		// Larva
		Rdk = 0.2088d;
		Ha  = 26018d;
		Hh  = 55990d;
		T12 = 304.6d;
		dmdLarva = getDesarrolloMetabolicodiario(R, Rdk, Ha, Hh, T12);
		// Pupa
		Rdk = 0.384d;
		Ha  = 14931d;
		Hh  = -472379d;
		T12 = 148d;
		dmdPupa = getDesarrolloMetabolicodiario(R, Rdk, Ha, Hh, T12);
	}
	
	private static double getDesarrolloMetabolicodiario(double R, double Rdk, double Ha, double Hh, double T12) {
		//Ema: [3]Schoofield, R.M., Sharpe, P.J.H., Magnuson, C.E., 1981. Non-linear regression of biological temperature-dependent rate models based on absolute reaction-rate theory.
		final double T = getCurrentTemperatureK();
		final double desMetabolico = Rdk*(T/298) * Math.exp((Ha/R)*(1d/298  - 1/T)) / (1 + Math.exp((Hh/R)*(1/T12  - 1/T)));
		return desMetabolico;
	}
	
	private static void updateMortalidadDiariaAcuatico() {
		// Ema: Marcelo Otero, Hernán G Solari, and Nicolás Schweigmann. A stochastic population dynamics model for
		// Aedes aegypti: formulation and application to a city with temperate climate. Bulletin of mathematical biology,(2006)
		final double T = getCurrentTemperatureK();
		mdAcuatico = 0.01 + 0.9725 * Math.exp(-(T-278) / 2.7035);
	}
	
	private static void updateDesarrolloAdultoDiario() {
		final double temp = Clima.getCurrentTemperature();
		final double hum  = Clima.getCurrentHumidity();
		// MaxiF: Genero una normal con medias y std segun el trabajo de Ivan P. basado en un paper.
		// MaxiF: Esta normal se usa para obtener la vida del mosquito.
		// Ema: Ivan P se basa en Lewis (1933) OBSERVATIONS ON AEDES AEGYPTI, L. (DIPT. CULIC.) UNDER CONTROLLED ATMOSPHERIC CONDITIONS.
		ddAdulto = 33.29 - 2.0307*temp -  0.03654*hum + 0.04054*temp*temp +  0.001703*temp*hum + 0.0004375*hum*hum;
		//-final double lifeStd  = 12.51 - 0.9804*temp + 0.009028*hum + 0.01958*temp*temp - 0.0007759*temp*hum + 0.0002305*hum*hum;
	}
	
	private static void updateMortalidadDiariaGlobal() {
		// Incrementa la trasa de mortalidad, a partir del mes de abril
		if (Clima.dayNumber > 250) {
			DataSet.mortalidadDiariaAdultos += 0.0015d;
			DataSet.mortalidadDiariaHuevos += 0.0015d;
		}
	}
}
