
package infeccion

import static java.lang.Math.*
import static repast.simphony.essentials.RepastEssentials.*

import java.util.HashMap;

import cern.jet.random.Normal
import infeccion.*;
import repast.simphony.context.Context
import repast.simphony.engine.environment.RunEnvironment
import repast.simphony.engine.schedule.*
import repast.simphony.parameter.*
import repast.simphony.query.PropertyEquals
import repast.simphony.query.space.grid.GridCell
import repast.simphony.query.space.grid.GridCellNgh
import repast.simphony.query.space.grid.MooreQuery
import repast.simphony.random.RandomHelper
import repast.simphony.space.grid.Grid
import repast.simphony.space.grid.GridPoint
import repast.simphony.ui.probe.*
import repast.simphony.util.ContextUtils
import repast.simphony.util.SimUtilities

public class Adulto extends LifeCicle{
	
	private   static final long serialVersionUID = 1L
	protected static long agentIDCounter = 1
	protected String agentID = "aedesAdulto " + (agentIDCounter++)
	public static ArrayList	   AdultList = new ArrayList(); //Lista de AedesAcuaticosTotales
	public static ArrayList getAdultList(){ return this.AdultList; }
	public static void      addAdultList(Object myObject){ 	this.AdultList.add(myObject); }
	public static void   removeAdultList(Object myObject){ this.AdultList.remove(myObject); }	
	
//	public void intialGeneratorAdultoRandom(){
//		double randomLife  = (int)RandomHelper.nextDoubleFromTo(0,1);
//		this.intialGeneratorAdulto(randomLife);
//	}

	@Parameter (displayName = "extrinsic period", usageName = "ePeriod")
	public int  getEPeriod() { return this.ePeriod }
	public void setEPeriod(int newValue) { this.ePeriod = newValue }
	public int     ePeriod = 1536   //MaxiC: hay que dividir por 12 para obtener el valor en ticks
  	
	@Parameter (displayName = "Infected", usageName = "infected")
	public boolean getInfected() { return this.infected	}
	public void    setInfected(boolean newValue) {	this.infected = newValue	}
	public boolean    infected = 0;
	
	@Parameter (displayName = "Posicion futura", usageName = "posicionfutura")
	public int[] getPosicionfutura() { return this.posicionfutura }
	public void  setPosicionfutura(int[] newValue) {	this.posicionfutura = newValue }
	public int[]    posicionfutura = new int[2]
	
	@Parameter (displayName = "Carga viral", usageName = "cargaviral")
	public boolean getCargaviral() { return this.cargaviral;	}
	public void    setCargaviral(boolean newValue) { this.cargaviral = newValue;	}
	public boolean    cargaviral = 0;
	
	@Parameter (displayName = "Vida", usageName = "vida")
	public int  getVida() {	return this.vida;	}
	public void setVida(int newValue) {	this.vida = newValue;	}
	public int     vida = 0;

	@Parameter (displayName = "zona_con_humanos", usageName = "zona_con_humanos")
	public ArrayList  getzona_con_humanos() { return this.zona_con_humanos;	}
	public void 	  setzona_con_humanos(ArrayList newValue) {	this.zona_con_humanos = newValue;	}
	public ArrayList     zona_con_humanos = [];
	//	@Parameter (displayName = "zona_con_humanos", usageName = "zona_con_humanos")
	//	public def  getzona_con_humanos() { return this.zona_con_humanos;	}
	//	public void setzona_con_humanos(def newValue) {	this.zona_con_humanos = newValue;	}
	//	public def     zona_con_humanos = [];
	
	@Parameter (displayName = "zona_con_contenedores", usageName = "zona_con_humanos")
	public ArrayList  getZonaConContenedores() { return this.zonaConContenedores;	}
	public void 	  setZonaConContenedores(def newValue) {	this.zonaConContenedores = newValue;	}
	public ArrayList     zonaConContenedores = [];
	
	@Parameter (displayName = "Posicion incial", usageName = "posicioninicial")
	private def     	 getPosicioninicial() { return this.posicioninicial; }
	private void       	 setPosicioninicial(def newValue) {	this.posicioninicial = newValue ;}
	private def             posicioninicial = 0;
	
	//-------------Codigo generado sin flujograma ------------------------------
	//MaxiC: vidainicial es una variable para se utiliza para registriar la vida inicial del aedes
	public int vidainicial = 0;
	
	public def setvecesinactivo(def aux) {	vecesinactivo.add(aux)	}
	public def getvecesinactivo() {	return vecesinactivo }
	public def    vecesinactivo = []; //MaxiC: vecesinactivo es un vector que registra la cantidad de veces que estuvo inactivo el aedes, ademas de cuanto vivio.	
	public def getcontadorinactividad()	{ return contadorinactividad }
	public def    contadorinactividad=0 //MaxiC: contadorinactividad es una variable que registra la cantidad de veces que estuvo inactivo el aedes
			
	public def getInactivo(){ return inactivo }
	public def tinactivo = 0; //MaxiC: tinactivo es una variable que indica el tiempo que el aedes esta inactivo. Divir por 12 para obtener el tiempo en ticks
	public def inactivo = false;
	//-----------------------------------------
	// distancia es una variable para determinar cuan lejos estan del lugar de nacimiento
	public def getDistancia() {	return distancia }
	public def    distancia=0;
	
	public def buscar=true
	@Parameter (displayName = "movimiento", usageName = "moverse")
	public boolean getmoverse() { return moverse }
	public void    setmoverse(boolean newValue) { moverse = newValue }
	public boolean moverse = false

	@Parameter (displayName = "ultimavida", usageName = "ultimavida")
	public def  getultimavida() { return ultimavida }
	public void setultimavida(def newValue) { ultimavida = newValue	}
	public def 	   ultimavida = 1440.0; // MaxiC: ultimavida es una variable que indica el tiempo a partir del cual el aedes no revivira. 

	//MaxiF: PicoExito es una variable para poder visualizar la cantidad de picuaduras exitosas se dieron
	public def getPicoExito() {
		def aux = this.picoExito
		this.picoExito = 0
		return aux
	}
	public def 	  picoExito = 0;

	//MaxiF: picoFrustrado es una variable para poder visualizar la cantidad de picuaduras frustradas por no encontrar humanos se dieron
	public def getPicoFrustrado() {
		def aux = this.picoFrustrado
		this.picoFrustrado = 0
		return aux
	}
	public def 	  picoFrustrado = 0;
	
	//MaxiF: Picar es una variable para que el mosquito recuerde que tiene que picar y siga intent�ndolo hasta poder lograrlo.
	public def picar = 0;

	//MaxiF: nacio es una variable para poder visualizar la cantidad de nacimientos por d�a
	public def getNacio() {
		def aux = this.nacio
		this.nacio = 0
		return aux
	}
	public def nacio = 1;

	//MaxiC: aedes_lleno es una variable que indica cuando el aedes debes estar inactivo
	private boolean    aedesLleno = false;
	public  boolean getAedesLleno() {	 
		return this.aedesLleno	
	}
	private void    setAedesLleno(boolean valor) { 
		this.aedesLleno=valor	
	}
	
	//MaxiC:veces_ppicar (setea la cantidad de veces que va a picar al comenzar su vida)
	public  int            veces_xpicar = 4;	
	public  int         getveces_xpicar() {return this.veces_xpicar }
	public  void        setveces_xpicar(def valor) { this.veces_xpicar = valor }
	
		
	public def getDato(){ return dato; }
	public def    dato //MaxiC: dato es una variable para visualizar la distribucion de actividad
	
	public def controlpique = 0; // MaxiC: controlpique es una variable que indica cuando debe picar el aedes
	
	
	private double       diasGestacion = 0;
	private double    getDiasGestacion(){
		return this.diasGestacion
	}
	private void      setDiasGestacion(double newValue){
		this.diasGestacion = newValue
	}

	private boolean          gestacionHabilitada = false
	private boolean       getGestacionHabilitada(){
		return this.gestacionHabilitada 
	} 
	private void          setGestacionHabilitada(boolean newValue){
		this.gestacionHabilitada = newValue;
	}
	
	private boolean    ovoposicionHabilitada = false;
	private boolean getOvoposicionHabilitada(){
		return this.ovoposicionHabilitada
	}
	private void    setOvoposicionHabilitada(boolean newValue){
		this.ovoposicionHabilitada = newValue
	}

	private int     ovoposicionNumero = 0;
	private int  getOvoposicionNumero(){return this.ovoposicionNumero}
	private void setOvoposicionNumero(int newValue){ this.ovoposicionNumero = newValue }

	private boolean inicializado = false
	//--------------------------------------------------------------
	
	@ScheduledMethod(
		start = 0.0d,
		interval = 0.03125d,
		priority = 0.95d,
		shuffle = true
	)
	private void inicializarvariables() {
		if(!this.inicializado){
			this.nacio = 1;
			this.contadorinactividad = 0;
			this.setCargaviral(false);
			this.setInfected(false);
			this.setAedesLleno(false);
			if((int)GetTickCount() == 0){
				this.inicializarEnConteinerInside()
			}
			this.inicializarPosicionInicial();
			this.inicializarVida();
			this.inicializarEPeriod();
			this.inicializarVeces_xpicar();			
			this.inicializado = true;
		}
	}

	private void  inicializarEnConteinerInside(){
		Grid grid=FindGrid("infeccion/grid");
		GridPoint pt = grid.getLocation(this);
		HashMap insideContainersMap = WaterSites.getValuesInsideContainersMap()
		Iterator i = insideContainersMap.sort().iterator();
		ArrayList<Integer> conteinerID = new ArrayList();
		while (i.hasNext()) {
			Map.Entry entry = (Map.Entry) i.next();
			conteinerID.add(entry.getKey());
		}
		int conteinerAmount = insideContainersMap.size()
		
		int[] newPosition = [0,0]
		while (conteinerAmount > 0 && newPosition == [0, 0]){
			int randomConteinerInside = RandomHelper.nextIntFromTo(0,conteinerAmount-1)
			HashMap conteinerValuesMap = insideContainersMap.get(conteinerID[randomConteinerInside])
			
			newPosition = inicializarEnConteinerInsideGetPosition(grid, conteinerValuesMap)
			if(newPosition == [0, 0]){															//si la capacidad de acarreo es mayor y no me devuelve una nueva posicion, elimino este valor del map para elegir otro al azar
				insideContainersMap.remove(conteinerID[randomConteinerInside])
			}
		}
		if(newPosition[0] != 0 && newPosition[1] != 0){
			newPosition[0] = this.generateRandomPosition().getX();
			newPosition[1] = this.generateRandomPosition().getY();
		}
		GridPoint point = new GridPoint(newPosition);
		grid.moveTo(this, point.getX(), point.getY())
	}

	private int[] inicializarEnConteinerInsideGetPosition(Grid grid, HashMap conteinerValuesMap) {
		int[] newPosition = [0,0]
		int newPositionX = conteinerValuesMap.get("x")
		int newPositionY = conteinerValuesMap.get("y")
		Iterable objects = grid.getObjectsAt(newPositionX, newPositionY);
		while (objects.hasNext() && newPosition == [0,0]) {
			Object o = objects.next()
			if (o instanceof Conteiner) {
					newPosition = [newPositionX, newPositionY];
			}
		}
		return newPosition
	}
	
	private void inicializarPosicionInicial() {
		GridPoint pt = FindGrid("infeccion/grid").getLocation(this);
		this.setPosicioninicial(pt) //MaxiC: fijo la posicion de nacimiento
	}

	private void inicializarVida(){
		def time = GetTickCountInTimeUnits()
		int esVal = (int)time.getEstimatedValue()
		int diaTrece = esVal/12+ 13; //se suma 13 d�as al dia de nacimiento, ya que la vida promedio es de 13 d�as
		double temp = Clima.getTemp(diaTrece);
	
		double hum  = Clima.getHum(diaTrece);
		// MaxiF: Genero una normal con medias y std seg�n el trabajo de Ivan P. basado en un paper.
		// MaxiF: Esta normal se usa para obtener la vida del mosquito.
		def lifeMean = 33.29 - 2.0307*temp -  0.03654*hum + 0.04054*temp*temp +  0.001703*temp*hum + 0.0004375*hum*hum;
		def lifeStd  = 12.51 - 0.9804*temp + 0.009028*hum + 0.01958*temp*temp - 0.0007759*temp*hum + 0.0002305*hum*hum;
		Normal norm = RandomHelper.createNormal(lifeMean, lifeStd);
		def auxvida = norm.nextInt();
		//MaxiC: bucle de control, para la vida
		while (auxvida <= 0){
			auxvida = norm.nextInt()
		}
		this.setVida(auxvida);
		this.vidainicial = auxvida;
		//		this.setVida(15);
		//		this.vidainicial = 15;
	}

	private void inicializarEPeriod() {
		double temp = Clima.getTemperatureDay()
		def ePeriod = ((4+(int)Math.exp(4-0.123*temp)))*384 //documentar formula!
		this.setEPeriod(ePeriod)
	}

	
	private void inicializarVeces_xpicar() {
		
		Parameters params = RunEnvironment.getInstance().getParameters();
		float Media_cantPicaduras	 = (Float) params.getValue("Media_cantPicaduras");
		
		def norm = RandomHelper.createNormal(Media_cantPicaduras, 1);
		def vecesxpicar_aux = norm.nextInt()
		while (vecesxpicar_aux <= 0){ 	//MaxiC: controlo que no sea negativo o cero
			vecesxpicar_aux = norm.nextInt()
		}
		this.setveces_xpicar(vecesxpicar_aux)
	}
	
	@ScheduledMethod(
		start = 0.0d,
		interval = 12d,
		priority = 0.91d,
		shuffle = true
	)
	public void updateLife(){											   //priority = 0.91			interval = 12d,
		if(!this.inicializado){
			this.inicializarvariables();
		}
		double random = RandomHelper.nextDoubleFromTo(0,1);
		double mortalidadDiaria = this.getMortalidadNeta()		
		if((this.vida < 1)||(random < mortalidadDiaria)) {
			this.eliminate();
		}
		if(this.getAedesLleno()){
			this.updateGonotroficMoving()
		}
		this.vida--;
	}
	
	//ciclo gonotrofico: Per�odo entre que chupa sangre, ovipone y vuelve a alimentarse.
	private void updateGonotroficMoving(){
		this.updateDiasGestacion();
		this.updateOvoposicionHabilitada();
		int cantidadOvoposiciones = dataSet.getAedesAdulto_CantidadDeOvoposiciones();
		if( this.getOvoposicionHabilitada() && this.getOvoposicionNumero() < cantidadOvoposiciones){
			if(this.getMyConteiner() != null){
				this.ovopositar();
			}
			this.setAedesLleno(false);
			this.setDiasGestacion(0)
			this.inicializarVeces_xpicar();
			this.setOvoposicionNumero(this.getOvoposicionNumero()+1);
		}
	}
	
	private void updateOvoposicionHabilitada() {
		def diasGestacion				    = dataSet.getAedesAdulto_DiasDeGestacion();
		def tiempoMaximoDeBusquedaConteiner = dataSet.getAedesAdulto_TiempoMaximoBusquedaContenedor();
		def tiempoMax = tiempoMaximoDeBusquedaConteiner + diasGestacion; 
		def gestacion = this.getDiasGestacion();
		if(this.getGestacionHabilitada() && (this.getMyConteiner() || (gestacion >= tiempoMax))){
			this.setOvoposicionHabilitada(true)
		}else{
			this.setOvoposicionHabilitada(false)
		}
	}

	private void updateDiasGestacion() {
		double gestacion   = dataSet.getAedesAdulto_DiasDeGestacion() ; // el valor 2 es para remplazar que le gestacion este dada por picuduras a humanos, y sea siempre = 3
		double gActual = this.getDiasGestacion() + 1
		this.setDiasGestacion(gActual)
		if(gActual >= gestacion){
			this.setGestacionHabilitada(true);
		}
		else{
			this.setGestacionHabilitada(false);
		}
	}
	
	private void updateVeces_xpicar() {
		def vecesXPicar = this.getveces_xpicar() - 1
		this.setveces_xpicar(vecesXPicar);
		if(this.getveces_xpicar() <= 0){
			this.setAedesLleno(true);
		}
	}
	
	private void ovopositar(){
		Grid grid=FindGrid("infeccion/grid");
		GridPoint pt = grid.getLocation(this);
		int cantidadMosquitos = dataSet.getAedesAdulto_CantidadHuevosPorOvoposicion();
		boolean ovoposicionHabilitada = Acuatico.oviposicionHabilitadaByCarryingCapacity()
		Conteiner myConteiner = this.getMyConteiner();
		if(myConteiner != null){
			if(ovoposicionHabilitada){
				for(int i = 0; i < cantidadMosquitos; i++){
					if( pt!=null && !this.restrainAmountEggs()){
						Object agent = CreateAgents("infeccion", "infeccion.Acuatico", 1);
						grid.moveTo(agent,pt.getX(),pt.getY());
						myConteiner.addOneToEggsAmount();
					}
				}
			}
		}
	}
	
	private boolean restrainAmountEggs(){
		boolean restrain = false;
		Conteiner myConteiner = this.getMyConteiner();
		if(myConteiner != null){
			int ccEggs = this.getMyConteiner().getCarryingCapacityEggs();
			int eggsAmount = this.getMyConteiner().getEggsAmount();
			if(eggsAmount > ccEggs){
				restrain = true;
			}
		}
		return restrain;
	}

	

	
	@ScheduledMethod(
		start = 0d,
		interval = 0.03125d,
		priority = 0.9d,
		shuffle = true
	)
	public  void updateInfection() { 										   //priority = 0.9d			interval = 0.03125d,
		if (this.cargaviral) {
			if (!this.infected) {
				if (this.ePeriod > 0) {
					this.ePeriod-- ;
				} else  {
					this.setInfected(true);
				}
			}
		}
	}

	//MaxiC: comportamiento, primera aproximaci�n al ciclo circadiano
	@ScheduledMethod(
		start = 0.0d,
		interval = 0.03125d,
		priority = 0.80d,
		shuffle = true)
	public def ciclocircadiano(){										   //priority = 0.8d			interval = 0.03125d,
		//MaxiC: la funcion de probabilidad de actividad esta definida en un rango de 12 horas o ticks
		def time = GetTickCountInTimeUnits()
		def t=0
		def decimal = time.getEstimatedValue()-Math.floor(time.getEstimatedValue())
		def resto= Math.floor(time.getEstimatedValue())%12
		//MaxiC: t varia en el rango de 0 a 12
		t=resto+decimal
		//--------------------- codigo---------------------------------------------
		//dato=exp(-(((t-11)/3)*((t-11)/3)))+0.8*exp(-(((t-1)/3)*((t-1)/3)))
		//MaxiC: distribucion de probabilidad de actividad calculda por IvanG
		this.dato=-0.00259843498592661 * Math.pow(t,4) +   0.0612607953396546  * Math.pow(t,3) - 0.444711088037163 *  Math.pow(t,2) + 0.999856190885920  * t + 0.176467821119740
		// udato es una variable que toma el valor en 0 y 1 con una probabilidad uniforme
		def udato=RandomHelper.nextDoubleFromTo(0,1)
		if(udato<this.dato){
			//this.buscarConteiner();
			this.busquedaAgentes(this.zona_con_humanos, humanos.class);
		}
		else{
			this.controlpique=false
		}
		//-------------------------------------------------------------------------
	}
	
	private void buscarConteiner(){
		Grid grid = FindGrid("infeccion/grid")
		GridPoint pt = grid.getLocation(this)
		GridCellNgh nghCreator = new GridCellNgh(grid, pt, Conteiner.class, 1, 1)	//creo un "Creador de vecindad" en el el punto "pt" buscando los objetos agentes Conteiner en una extencion de x +- 1, y +- 1                    
		List gridCells = nghCreator.getNeighborhood(true)							//tomo una lista del barrio de los objetos agentes con el centro incluido
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform())					//las mesclo! 
		int maxCount = 0 // probar con menos -1
		GridCell cellRandom 
		this.setMyConteiner(null);
		for (GridCell cell in gridCells) {											
			if (cell.size() > maxCount) {
				GridPoint p = cell.getPoint()
				Iterable objects = grid.getObjectsAt(p.getX(), p.getY());
				while (objects.hasNext() && this.getMyConteiner() == null) {
					Object o = objects.next()
					if (o instanceof Conteiner){
						if (o.getHaveMmWater()){
							if (o.getAcuaticAmount() < o.getCarryingCapacityEggs()){  
								this.setMyConteiner(o);
							}
						}	
					}
				}
				maxCount = cell.size()
			}
			if((this.getMyConteiner() == null) && (cellRandom == null)){
				GridPoint randomPosition = cell.getPoint();
				if(this.posicioninicial == 0){this.inicializarPosicionInicial()}
				int diferenceX = Math.abs(this.posicioninicial.getX() - randomPosition.getX()); 
				int diferenceY = Math.abs(this.posicioninicial.getY() - randomPosition.getY()); 
				int radio = Math.round(Math.sqrt(Math.pow(diferenceX,2)+Math.pow(diferenceY,2)));
				if(radio < 7){ 
					cellRandom = cell
				}
			}
		}
		int[] newPosition;
		if (this.getMyConteiner() == null){
			int r = RandomHelper.nextIntFromTo(0,2)
			int x, y;
			if ((r == 0)&&(cellRandom != null)){
				GridPoint randomPosition = cellRandom.getPoint()
				x = randomPosition.getX()
				y = randomPosition.getY()
			}else{
				x = pt.getX()
				y = pt.getY()
			}
			newPosition = [x,y];
		}else{
			int x = this.getMyConteiner().getPosition()[0];
			int y = this.getMyConteiner().getPosition()[1];
			newPosition = [x,y];
		}
		
		this.setPosicionfutura(newPosition);
		grid.moveTo(this,newPosition[0],newPosition[1])
	}

	 /**   MaxiC: Este comportamiento busca en una vecinda de Moore de 1, agentes humanos. Se divide la la vencindad
	  * de Moore en 9 zonas, se cuenta el centro. 
	  *  4    3     2 
	  *  5    x(9)  1
	  *  6    7     8	 */
	public def busquedaAgentes(ArrayList agentesBusqueda, def myClass) {
		def time = GetTickCountInTimeUnits()
		double currentTime= time.getEstimatedValue()
		Grid grid=FindGrid("infeccion/grid")

	    if((currentTime%1)==0 && this.buscar) {
		    this.controlpique=true
			agentesBusqueda.clear()
			this.setmoverse(true) //MaxiC: habilita el movimiento
			if(this.vida == 0){
				this.inicializarvariables();
			}
			GridCellNgh nghCreator = new GridCellNgh(grid, this.posicioninicial, humanos.class,1, 1) //MaxiC: busca humanos dentro de su radio de movimiento
			List gridCells = nghCreator.getNeighborhood(true) 
			SimUtilities.shuffle(gridCells , RandomHelper.getUniform())
			//MaxiC: separo en 9 zonas de interes para el movimiento. 
			for (GridCell cell in gridCells) {
				if (cell.size() != 0) {
					if (cell.getPoint().getX()>posicioninicial.getX() && cell.getPoint().getY()==posicioninicial.getY() ) {
						agentesBusqueda.add(1)
					} else  {
						if (cell.getPoint().getX()>posicioninicial.getX() && cell.getPoint().getY()>posicioninicial.getY() ) {
							agentesBusqueda.add(2)
						} else  {
							if (cell.getPoint().getX()==posicioninicial.getX() && cell.getPoint().getY()>posicioninicial.getY() ) {
								agentesBusqueda.add(3)
							}else  {
								if (cell.getPoint().getX()<posicioninicial.getX() && cell.getPoint().getY()>posicioninicial.getY() ) {
									agentesBusqueda.add(4)
								} else  {
									if (cell.getPoint().getX()<posicioninicial.getX() && cell.getPoint().getY()==posicioninicial.getY() ) {
										agentesBusqueda.add(5)
									} else  {
										if (cell.getPoint().getX()<posicioninicial.getX() && cell.getPoint().getY()<posicioninicial.getY() ) {
											agentesBusqueda.add(6)
									} else  {
										if (cell.getPoint().getX()==posicioninicial.getX() && cell.getPoint().getY()<posicioninicial.getY() ) {
											agentesBusqueda.add(7)
									 } else  {
									 	if (cell.getPoint().getX()>posicioninicial.getX() && cell.getPoint().getY()<posicioninicial.getY() ) {
											 agentesBusqueda.add(8)
							     		} else  {
								        		if (cell.getPoint().getX()==posicioninicial.getX() && cell.getPoint().getY()==posicioninicial.getY() ) {
										        	agentesBusqueda.add(9)
									             } 
								               } 
								           } 
								       } 
								   }
								}
							}
						}
					}
				}
			}
			this.direccion(agentesBusqueda)
		} else {
             this.controlpique=false
			 this.moverse=false
		}
	}
	
	public def direccion(ArrayList agentesBusqueda) {
		Collections.sort(agentesBusqueda)
		Grid grid=FindGrid("infeccion/grid")
		
		RandomHelper.createUniform()
		int index = RandomHelper.nextIntFromTo(0, agentesBusqueda.size() - 1)
		switch(agentesBusqueda[index]){
			case 1: posicionfutura[0]=posicioninicial.getX()+1
					posicionfutura[1]=posicioninicial.getY()
					//println("1" + posicioninicial + " " +posicionfutura)
					break
			case 2: posicionfutura[0]=posicioninicial.getX()+1
					posicionfutura[1]=posicioninicial.getY()+1
					//println("2"+ posicioninicial + " " +posicionfutura)
					break
			case 3: posicionfutura[0]=posicioninicial.getX()
					posicionfutura[1]=posicioninicial.getY()+1
					//println("3"+ posicioninicial + " " +posicionfutura)
					break
			case 4: posicionfutura[0]=posicioninicial.getX()-1
					posicionfutura[1]=posicioninicial.getY()+1
					//println("4"+ posicioninicial + " " +posicionfutura)
					break
			case 5: posicionfutura[0]=posicioninicial.getX()-1
					posicionfutura[1]=posicioninicial.getY()
					//println("5"+ posicioninicial + " " +posicionfutura)
					break
			case 6: posicionfutura[0]=posicioninicial.getX()-1
					posicionfutura[1]=posicioninicial.getY()-1
					//println("6"+ posicioninicial + " " +posicionfutura)
			case 7:  posicionfutura[0]=posicioninicial.getX()
					 posicionfutura[1]=posicioninicial.getY()-1
					//println("7"+ posicioninicial + " " +posicionfutura)
					 break
			case 8: posicionfutura[0]=posicioninicial.getX()+1
					posicionfutura[1]=posicioninicial.getY()-1
					//println("8"+ posicioninicial + " " +posicionfutura)
					break
			case 9: posicionfutura[0]=posicioninicial.getX()
					posicionfutura[1]=posicioninicial.getY()
					//println("9"+ posicioninicial + " " +posicionfutura)
					break
			default:def x=RandomHelper.nextIntFromTo(-1,1)
					def y=RandomHelper.nextIntFromTo(-1,1)
					while(-2>x && x<2 && -2>y &&y<2){
						x=RandomHelper.nextIntFromTo(-1,1)
						y=RandomHelper.nextIntFromTo(-1,1)
					}
					posicionfutura[0]=(posicioninicial.getX()-x)
					posicionfutura[1]=(posicioninicial.getY()-y)
					//-------------------------David M agregado--------------------------------
					//					int probabilidadSalirDeCasa = 5;
					//					int r = RandomHelper.nextIntFromTo(0,100)
					//					if(r < probabilidadSalirDeCasa){
					//						int[] newPoint = [posicionfutura[0], posicionfutura[1]]
					//						GridPoint pt = new GridPoint(newPoint) 
					//						this.setPosicioninicial(pt)
					//					}	
					//					
					//-------------------------David M agregado--------------------------------
					//println("azar"+ posicioninicial + " " +posicionfutura)
					break
		}
	}
	
	@ScheduledMethod(
		start = 0.0d,
		interval = 0.03125d,
		priority = 0.68d,
		shuffle = true
	)
	public def movimiento() {											   //priority = 0.68d			interval = 0.03125d,
		if (this.moverse){
			Grid grid=FindGrid("infeccion/grid")
			int x = this.posicionfutura[0]
			int y = this.posicionfutura[1]
			while(x < 1){
				x = 1
			}
			while(x > 199){
				x = 199
			}
			while(y < 1){
				y = 1
			}
			while(y > 199){
				y = 199
			}
			while(x < 1){
				x = 1
			}
			this.posicionfutura[0] = x
			this.posicionfutura[1] = y
			grid.moveTo(this,this.posicionfutura[0],this.posicionfutura[1])
			//			catch(repast.simphony.space.SpatialException e)
			//			{ //e.println("error al moverse, vuelve a su posicion inicial")
			//				  posicionfutura[0]=this.posicioninicial.getX()
			//				  posicionfutura[1]=this.posicioninicial.getY()
			//				  grid.moveTo(this,posicionfutura[0],posicionfutura[1])
			//			}
			this.setmoverse(false)
			distancia = grid.getDistance(posicioninicial, new GridPoint(posicionfutura))
			//println("distancia:"+ distancia + "time:"+time.getEstimatedValue())
			}
	}

	@ScheduledMethod(
		start = 0.0d,
		interval = 0.03125d,
		priority = 0.67d,
		shuffle = true
	)
	public def infect() { 												   //priority = 0.67d,			interval = 0.03125d,
		def almacenador_de_agentes = [];
		if (this.controlpique && this.getveces_xpicar()>0 ){
			Grid grid=FindGrid("infeccion/grid");
			GridPoint pt=grid.getLocation(this);
			Iterator list=new MooreQuery(grid,this,1,1).query().iterator(); //MaxiC: busca agentes con un radio de Moore de 1
			
			//MaxiC: cargo todos los humanos en el vecindario de Moore, excepto el centro
			for (o in list) {
				if (o instanceof humanos) {
					almacenador_de_agentes.add(o);
				}
			}
			//MaxiC: cargo el centro
			Object objetos = grid.getObjectAt(pt.getX(), pt.getY())
			for(o in objetos){
				if (o instanceof humanos) {
					almacenador_de_agentes.add(o)
				}
			}
			if (almacenador_de_agentes.size()!=0) {
				this.picoExito += 1	//MaxiF: PicoExito es una variable para poder visualizar la cantidad de picuaduras exitosas que se dieron
				//MaxiF: Picar es una variable para que el mosquito recuerde que tiene que picar y siga intent�ndolo hasta poder lograrlo.
				// 		Como lo logra, se setea a 0.
				this.picar = 0;
				RandomHelper.createUniform()
				int indexhumano = RandomHelper.nextDoubleFromTo(0, almacenador_de_agentes.size()-1);

				if (this.infected){
					if (!almacenador_de_agentes[indexhumano].infected){
						almacenador_de_agentes[indexhumano].cargaviralhumano = true;
					}
				}else{
					if (almacenador_de_agentes[indexhumano].infected){
						this.setCargaviral(true);
					}
				}
				this.updateVeces_xpicar()																					// AAAAAACTUALIZARRRRRRRRRRRRRRRRRRRRRRRRRRRR Sacar el comenterio
			}
			else{//MaxiF: picoFrustrado es una variable para poder visualizar la cantidad de picuaduras frustradas por no encontrar humanos se dieron
				this.picoFrustrado += 1
				//MaxiF: Picar es una variable para que el mosquito recuerde que tiene que picar y siga intent�ndolo hasta poder lograrlo.
				// 		Como no pudo picar a nadie por no encontrarlo, sigue buscando.
			}
			
		}
		//BORRRAAAAAAAAARRRRRRR-------------------------------------------------------------------------------------------------------------------------------------------------
		//BORRRAAAAAAAAARRRRRRR-------------------------------------------------------------------------------------------------------------------------------------------------
		//	this.setAedesLleno(true); //seteo los aedes llenos para que ovopositen siempre sin necesidad de picar humanos!--------------------------------------------------------------------------------
		//BORRRAAAAAAAAARRRRRRR-------------------------------------------------------------------------------------------------------------------------------------------------
		//BORRRAAAAAAAAARRRRRRR-------------------------------------------------------------------------------------------------------------------------------------------------
		//this.updateVeces_xpicar() 																					// AAAAAACTUALIZARRRRRRRRRRRRRRRRRRRRRRRRRRRR borrar linea
		if(this.getAedesLleno()){ //no puede pasar mas de dos dias buscando un lugar para ovipositar
			this.buscarConteiner();
		}else{
			//this.buscarConteiner();
			this.busquedaAgentes(this.zona_con_humanos, humanos.class);
		}	
	}

	@ScheduledMethod(
		start = 0.0d,
		interval = 0.03125d,
		priority = 0.66d,
		shuffle = true
	)
	public def Digestion() {											   //priority = 0.66			interval = 0.03125d,
		if(this.inactivo==false){
			if(this.getveces_xpicar() <= 0){
				//println("digestion" + " "+ this.agentID + " "+ time.getEstimatedValue())
				//no busca, no se mueve y no pica
				this.buscar=false
				this.picar=false;
				this.tinactivo=768 //equivalente a 2 dias o 24 ticks. 
				this.inactivo=true
				this.contadorinactividad+=1
			}
		}else{ 
			if(this.tinactivo<=0){
				this.buscar=true
				this.inactivo=false
				//this.inicializarVeces_xpicar();
				this.tinactivo=768
			}
			else{
				this.tinactivo-=1
			}
		}
	}
	
	public double mortalidadDiaria(){//mortalidad minima
		return dataSet.getAedesAdulto_MortalidadDiaria();
	}
	
	public void mortalidadNeta(){//sumatoria de mortalidades
		this.setMortalidadNeta(this.mortalidadDiaria());
	}
	
	public GridPoint generateRandomPosition(){
		Grid grid = FindGrid("infeccion/grid");
		def dimensions = grid.getDimensions();
		int anchoGrilla = (int)dimensions.getWidth();
		int altoGrilla  = (int)dimensions.getHeight();
		int x = (int)RandomHelper.nextDoubleFromTo(1,(anchoGrilla-1));
		int y = (int)RandomHelper.nextDoubleFromTo(1,(altoGrilla -1));
		int[] newPoint = [x,y];
		GridPoint point = new GridPoint(newPoint);
		return point;
	}
	
	public void eliminate(int agentID){}
	public void eliminate(){
		this.removeAdultList(this);
		RemoveAgentFromContext("infeccion", this);
		this.agentIDCounter -= 1;
	}
	
	
	
	@ProbeID()
	public String toString() {
		def returnValue
		def time = GetTickCountInTimeUnits()
		returnValue = this.agentID
		return returnValue
	}

}

///**   MaxiC: Este comportamiento busca en una vecinda de Moore de 1, agentes humanos. Se divide la la vencindad
// * de Moore en 9 zonas, se cuenta el centro.
// *  4    3     2
// *  5    x(9)  1
// *  6    7     8	 */
//public def busquedahumanos() {
//   def time = GetTickCountInTimeUnits()
//   double currentTime= time.getEstimatedValue()
//   Grid grid=FindGrid("infeccion/grid")
//
//   if((currentTime%1)==0 && this.buscar) {
//	   this.controlpique=true
//	   this.zona_con_humanos.clear()
//	   this.setmoverse(true) //MaxiC: habilita el movimiento
//	   if(this.vida == 0){
//		   this.inicializarvariables();
//	   }
//	   GridCellNgh nghCreator = new GridCellNgh(grid, this.posicioninicial, humanos.class,1, 1) //MaxiC: busca humanos dentro de su radio de movimiento
//	   List gridCells = nghCreator.getNeighborhood(true)
//	   SimUtilities.shuffle(gridCells , RandomHelper.getUniform())
//	   //MaxiC: separo en 9 zonas de interes para el movimiento.
//	   for (GridCell cell in gridCells) {
//		   if (cell.size() != 0) {
//			   if (cell.getPoint().getX()>posicioninicial.getX() && cell.getPoint().getY()==posicioninicial.getY() ) {
//				   this.zona_con_humanos.add(1)
//			   } else  {
//				   if (cell.getPoint().getX()>posicioninicial.getX() && cell.getPoint().getY()>posicioninicial.getY() ) {
//					   this.zona_con_humanos.add(2)
//				   } else  {
//					   if (cell.getPoint().getX()==posicioninicial.getX() && cell.getPoint().getY()>posicioninicial.getY() ) {
//						   this.zona_con_humanos.add(3)
//					   }else  {
//						   if (cell.getPoint().getX()<posicioninicial.getX() && cell.getPoint().getY()>posicioninicial.getY() ) {
//							   this.zona_con_humanos.add(4)
//						   } else  {
//							   if (cell.getPoint().getX()<posicioninicial.getX() && cell.getPoint().getY()==posicioninicial.getY() ) {
//								   this.zona_con_humanos.add(5)
//							   } else  {
//								   if (cell.getPoint().getX()<posicioninicial.getX() && cell.getPoint().getY()<posicioninicial.getY() ) {
//									   this.zona_con_humanos.add(6)
//							   } else  {
//								   if (cell.getPoint().getX()==posicioninicial.getX() && cell.getPoint().getY()<posicioninicial.getY() ) {
//									   this.zona_con_humanos.add(7)
//								} else  {
//									if (cell.getPoint().getX()>posicioninicial.getX() && cell.getPoint().getY()<posicioninicial.getY() ) {
//										this.zona_con_humanos.add(8)
//									} else  {
//										   if (cell.getPoint().getX()==posicioninicial.getX() && cell.getPoint().getY()==posicioninicial.getY() ) {
//											   this.zona_con_humanos.add(9)
//											}
//										  }
//									  }
//								  }
//							  }
//						   }
//					   }
//				   }
//			   }
//		   }
//	   }
//	   this.direccion()
//   } else {
//		this.controlpique=false
//		this.moverse=false
//   }
//}


//public def direccion() {
//	Collections.sort(zona_con_humanos)
//	Grid grid=FindGrid("infeccion/grid")
//	
//	RandomHelper.createUniform()
//	int index = RandomHelper.nextIntFromTo(0, zona_con_humanos.size() - 1)
//	switch(zona_con_humanos[index]){
//		case 1: posicionfutura[0]=posicioninicial.getX()+1
//				posicionfutura[1]=posicioninicial.getY()
//				//println("1" + posicioninicial + " " +posicionfutura)
//				break
//		case 2: posicionfutura[0]=posicioninicial.getX()+1
//				posicionfutura[1]=posicioninicial.getY()+1
//				//println("2"+ posicioninicial + " " +posicionfutura)
//				break
//		case 3: posicionfutura[0]=posicioninicial.getX()
//				posicionfutura[1]=posicioninicial.getY()+1
//				//println("3"+ posicioninicial + " " +posicionfutura)
//				break
//		case 4: posicionfutura[0]=posicioninicial.getX()-1
//				posicionfutura[1]=posicioninicial.getY()+1
//				//println("4"+ posicioninicial + " " +posicionfutura)
//				break
//		case 5: posicionfutura[0]=posicioninicial.getX()-1
//				posicionfutura[1]=posicioninicial.getY()
//				//println("5"+ posicioninicial + " " +posicionfutura)
//				break
//		case 6: posicionfutura[0]=posicioninicial.getX()-1
//				posicionfutura[1]=posicioninicial.getY()-1
//				//println("6"+ posicioninicial + " " +posicionfutura)
//		case 7:  posicionfutura[0]=posicioninicial.getX()
//				 posicionfutura[1]=posicioninicial.getY()-1
//				//println("7"+ posicioninicial + " " +posicionfutura)
//				 break
//		case 8: posicionfutura[0]=posicioninicial.getX()+1
//				posicionfutura[1]=posicioninicial.getY()-1
//				//println("8"+ posicioninicial + " " +posicionfutura)
//				break
//		case 9: posicionfutura[0]=posicioninicial.getX()
//				posicionfutura[1]=posicioninicial.getY()
//				//println("9"+ posicioninicial + " " +posicionfutura)
//				break
//		default:def x=RandomHelper.nextIntFromTo(-1,1)
//				def y=RandomHelper.nextIntFromTo(-1,1)
//				while(-2>x && x<2 && -2>y &&y<2){
//					x=RandomHelper.nextIntFromTo(-1,1)
//					y=RandomHelper.nextIntFromTo(-1,1)
//				}
//				posicionfutura[0]=(posicioninicial.getX()-x)
//				posicionfutura[1]=(posicioninicial.getY()-y)
//				//-------------------------David M agregado--------------------------------
//				int probabilidadSalirDeCasa = 5;
//				int r = RandomHelper.nextIntFromTo(0,100)
//				if(r < probabilidadSalirDeCasa){
//					int[] newPoint = [posicionfutura[0], posicionfutura[1]]
//					GridPoint pt = new GridPoint(newPoint)
//					this.setPosicioninicial(pt)
//				}
//				
//				//-------------------------David M agregado--------------------------------
//				//println("azar"+ posicioninicial + " " +posicionfutura)
//				break
//	}
//}