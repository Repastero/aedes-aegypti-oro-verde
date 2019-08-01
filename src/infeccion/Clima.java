package infeccion

import infeccion.*
import static repast.simphony.essentials.RepastEssentials.*;
import repast.simphony.engine.environment.RunEnvironment
import repast.simphony.engine.schedule.*
import repast.simphony.parameter.*
import repast.simphony.ui.probe.*

public class Clima {

	private static final long serialVersionUID = 1L;
	protected static long agentIDCounter = 1;
	protected String agentID = "Clima " + (agentIDCounter++);

	@ScheduledMethod(
		start = 0d,
		interval = 12d,
		priority = 0.99d,
		shuffle = true
	)
	public void UpdateClima(){ 							//Prioridad 1 : mayor que todos los metodos del proyecto!
		this.setDayNumber();
		this.updateTemperatureDay();
		this.updatePrecipitationDay();
		this.updateHumidityDay();
		this.updateVientoDiario();
	}
	
	private static int      runNumber = 0; //numero de corridas ejecutadas en ela UI
	private static int   	dayNumber = 0;									 //Prop + actualisador diario								
	private static int  getDayNumber() {return this.dayNumber}
	private static void setDayNumber(int newValue) {this.dayNumber =  newValue;}
	private static void setDayNumber(){
		def time = (int)GetTickCount();
		if (time == 0){
			this.runNumber++;
			this.setDayNumber(1);
		}else if(time%12 == 0 ) {
			int dayNumber = time/12+1;
			this.setDayNumber(dayNumber);
		}		
	}
	
	//Lista de temperaturas, empezando desde julio 2016
	private static double[]     Temperaturas = dataSet.getClima_TemperaturaJulio2016aJunio2017();
	public  static double    getTemp(int dia) {return Temperaturas[dia]}
	private static double       temperatureDay;	 // Temperatura Diaria para trabajar dentro de la clase
	public 	static double    getTemperatureDay() { return temperatureDay; }
	private static void      setTemperatureDay(double newValue) {this.temperatureDay = newValue;}
	private static  void   updateTemperatureDay() {
		def currentTime = (int)GetTickCount();
		if ( (int)currentTime % 12 == 0){
			 setTemperatureDay(getTemp(this.getDayNumber()));
		}
	}

	private static double[] 	Precipitaciones = dataSet.getClima_PrecipitacionesJulio2016aJunio2017();
	private static double    getPrecip (int dia) { return Precipitaciones[dia] }
	private static double       precipitationDay;    						// Precipitacion Diaria para trabajar dentro de la clase
	public  static double    getPrecipitationDay(){return this.precipitationDay}
	private static void      setPrecipitationDay(double newValue){ this.precipitationDay = newValue;}
	private static void   updatePrecipitationDay(){
		def currentTime = (int)GetTickCount();
		if( (int)currentTime %12 == 0){
			this.setPrecipitationDay(getPrecip(this.getDayNumber()));
			double precipitation = this.getPrecipitationDay();
		}
	}
	
	//MaxiF y MaxiC: Lista de temperaturas, empezando desde julio 2016
	private static double[]     Hums = dataSet.getClima_HumedadJulio2016aJunio2017()
	public  static 			 getHum(int dia) { return Hums[dia] }
	private static double       HumidityDay;    						// Precipitacion Diaria para trabajar dentro de la clase
	public  static double    getHumidityDay(){return HumidityDay}
	private 	   void      setHumidityDay(double newValue){ this.HumidityDay = newValue;}
	private 	   void   updateHumidityDay(){
		def currentTime = (int)GetTickCount();
		if( (int)currentTime %12 == 0){
			this.setHumidityDay(getHum(this.getDayNumber()));
		}
	}
	
	//MaxiF y MaxiC: Lista de temperaturas, empezando desde julio 2016
	private static double[]      vientos = dataSet.getClima_VientosJulio2016aJunio2017()
	public  static double     getViento(int dia){return this.vientos[dia];}
	private static double 	     vientoDiario
	public  static double     getVientoDiario(){return this.vientoDiario}
	private static void       setVientoDiario(double newValue){ this.vientoDiario=newValue; }
	private static void    updateVientoDiario(){
		def currentTime = (int)GetTickCount();
		if( (int)currentTime %12 == 0){
			this.setVientoDiario(this.getViento(this.getDayNumber()));
		}
	}
	
	public static double     getEvaporation(double totalWaterDay){
		double T             = this.getTemperatureDay() + 273;
		double horasDeSol    = dataSet.getClima_EvaporacionHorasDeSolMedia()
		double viento	     = this.getVientoDiario()*0.7;
		
		double P =  Math.pow(10, 8.07131 - 1730.63/(233.426 + (T - 273))) //presion del vapor. log(P) = 8.07131 - ((1730.63)/(233.426 + T))
		double H  = horasDeSol;			   			 		  	  // H  Exposicion Solar
		double U  = Math.pow(viento/1.61 ,0.78)          		  // U  Velocidad del viento [kmph/1.61],
		double MW = Math.pow(18.01528,2/3)        	   	 		  // MW Peso molecular del agua [g/mol]
		double A  = 1/0.00107  			          				  // Superficie expuesta del contenedor en centimetros cuadrados
		double k  = 26000                  	   			 		  // constante
		
		double evaporationInMl = (U*MW*A*H*P*H)/(k*T) 			  // evaporation rate [ml/dia]
		double evaporationInMmOfHigh = evaporationInMl*10;		  // siendo una superficie de 10[cm]*10[cm]*10[cm]
		return evaporationInMmOfHigh;
	}
}



//public 	static ArrayList    precipAcumByWeek = new ArrayList();
//public 	static double    getPrecipAcumByWeek(){
//	int weekNumber = this.getDayNumber()/7;
//	if((weekNumber>1)&&(!this.precipAcumByWeek.empty)){
//		double pAcumByWeek = this.precipAcumByWeek.getAt(weekNumber-1);
//		return pAcumByWeek;
//	}else{
//		return 0;
//	}
//}
//public 		   double 		precipAcum = 0;
//public 		   double[] setPrecipAcumByWeek(double precipitation){
//	this.precipAcum += precipitation
//	if((this.getDayNumber() +1)% 7 == 0){
//		double precipWeek = this.precipAcum/7;
//		this.precipAcumByWeek.add(precipWeek);
//		this.precipAcum = 0;
//	}
//}



