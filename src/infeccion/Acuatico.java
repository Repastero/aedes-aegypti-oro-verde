package infeccion

import static java.lang.Math.*
import cern.jet.random.Normal
import static repast.simphony.essentials.RepastEssentials.*
import infeccion.aedes.LifeCicle.*
import infeccion.aedes.StateHuevo.*
import infeccion.aedes.StateLarva.*
import infeccion.aedes.StatePupa.*
import infeccion.*

import java.math.*
import java.util.ArrayList;
import java.util.Set;

import javax.measure.unit.*

import org.jscience.mathematics.number.*
import org.jscience.mathematics.vector.*
import org.jscience.physics.amount.*

import repast.simphony.adaptation.neural.*
import repast.simphony.adaptation.regression.*
import repast.simphony.context.*
import repast.simphony.context.space.continuous.*
import repast.simphony.context.space.gis.*
import repast.simphony.context.space.graph.*
import repast.simphony.context.space.grid.*
import repast.simphony.data2.engine.*
import repast.simphony.data2.engine.CountSourceDefinition.*
import repast.simphony.engine.environment.*
import repast.simphony.engine.schedule.*
import repast.simphony.engine.watcher.*
import repast.simphony.groovy.math.*
import repast.simphony.integration.*
import repast.simphony.matlab.link.*
import repast.simphony.parameter.*
import repast.simphony.query.*
import repast.simphony.query.space.continuous.*
import repast.simphony.query.space.gis.*
import repast.simphony.query.space.graph.*
import repast.simphony.query.space.grid.*
import repast.simphony.query.space.projection.*
import repast.simphony.random.*
import repast.simphony.space.continuous.*
import repast.simphony.space.gis.*
import repast.simphony.space.graph.*
import repast.simphony.space.grid.*
import repast.simphony.space.projection.*
import repast.simphony.ui.probe.*
import repast.simphony.util.*
import simphony.util.messages.*

public class Acuatico{
	Acuatico(){}
	private   static final long serialVersionUID = 1L
	public    static long                  agentIDCounter = -1
	public    static long               getAgentIDCounter(){ 
		return this.agentIDCounter
	} 
	protected        String                agentID = "aedesAcuatico " + (agentIDCounter += 1)
	public    		 Integer                    ID = (int)agentIDCounter; 
	private          Integer   getAgentID() {
		return this.ID;
	}
	
	public    static void       inicializarAgentIDCounter(){
		agentIDCounter = -1
	}
	public    static ArrayList	     AcuaticList = new ArrayList(); //Lista de AedesAcuaticosTotales
	public    static ArrayList    getAcuaticList(){
		return this.AcuaticList; 
	} 
	public    static void         addAcuaticList(Object myObject){
		this.AcuaticList.add(myObject); 
	}
	public    static void      removeAcuaticListObject(Object myObject){ 
		this.AcuaticList.remove(myObject); 
	}
	
	public static int getCantidadHuevos(){
		return StateHuevo.getCantidad();
	}
	public static int getCantidadLarvas(){
		return StateLarva.getCantidad();		
	}
	public static int getCantidadPupas() {
		return StatePupa.getCantidad();
	}
	public static int getCantidadLarvasYPupas() {
		def p = StatePupa.getCantidad(); 
		def l = StateLarva.getCantidad() 
		int cant = p + l 
		return cant;
	}
	
	public  static boolean oviposicionHabilitadaByCarryingCapacity(){
		def cantidadAedes = this.getAgentIDCounter();
		def ccNeta 		  = WaterSites.getCarryingCapacityNeta();
		if(cantidadAedes < ccNeta){
			return true;
		}else{
			return false
		}
	}
	
	private LifeCicle lifeCicle;
	
	public  void intialGeneratorAcuaticRandom(){//generador del "sembrado" de huevos,larvas,pupas acuaticos iniciales			//RAFA!!!!!!!!!
		double randomLife 		   = RandomHelper.nextDoubleFromTo(0   ,1);
		double rDistributionHuevos = RandomHelper.nextDoubleFromTo(0.25 ,0.35);
		double rDistributionLarvas = RandomHelper.nextDoubleFromTo(0.88,0.95);
		double          		 r = RandomHelper.nextDoubleFromTo(0   ,1);
		int    state;
		switch(true){
			case    r < rDistributionHuevos: state = 0; break;
			case    r < rDistributionLarvas: state = 1; break;
			default 	   				   : state = 2; break;
		}
		this.intialGeneratorAcuatic(randomLife, state)
	}
	public  void intialGeneratorAcuaticHuevos(){//generador del "sembrado" de huevos iniciales
		this.intialGeneratorAcuatic(0.0, 0)
	}
	public  void intialGeneratorAcuaticLarvas(){//generador del "sembrado" de larvas iniciales
		this.intialGeneratorAcuatic(0.0, 1)
	}
	public  void intialGeneratorAcuaticPupas() {//generador del "sembrado" de pupas iniciales
		this.intialGeneratorAcuatic(0.0, 2)
	}
	private void intialGeneratorAcuatic(double life, int state){
		def myagentID = this.getAgentID();
		switch (state){
			case 0:
				this.lifeCicle   = new StateHuevo(life, myagentID);
				break;
			case 1:
				this.lifeCicle   = new StateLarva(life, myagentID);
				break;
			case 2:
				this.lifeCicle   = new StatePupa(life, myagentID);
				break;
		}
		this.addAcuaticList(this);
	}

	private void  Inicializar(){
		this.nextLifeCicle();
		this.addAcuaticList(this);
		this.addToConteinerAcuaticList();
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
		if(newPosition[0] == 0 && newPosition[1] == 0){
			newPosition = this.generateRandomPosition()
		}
		GridPoint point = new GridPoint(newPosition);
		grid.moveTo(this, point.getX(), point.getY())
		if(this.lifeCicle.getMyPosition() == null) {
			this.lifeCicle.setMyPosition(point)
		}
	}

	private int[] inicializarEnConteinerInsideGetPosition(Grid grid, HashMap conteinerValuesMap) {
		int[] newPosition = [0,0]
		int newPositionX = conteinerValuesMap.get("x")
		int newPositionY = conteinerValuesMap.get("y")
		Iterable objects = grid.getObjectsAt(newPositionX, newPositionY);
		while (objects.hasNext() && newPosition == [0,0]) {
			Object o = objects.next()
			if (o instanceof Conteiner) {
				def amountAcuaticInConteiner 	= o.getAcuaticListSize();
				def carryingCapacityInConteiner = o.getCarryingCapacity();
				if(amountAcuaticInConteiner < carryingCapacityInConteiner){
					newPosition = [newPositionX, newPositionY];
				}
			}
		}
		return newPosition
	}
	
	private void addToConteinerAcuaticList() {
		Conteiner c = this.lifeCicle.getMyConteiner()
		if (c != null && !(this.lifeCicle instanceof StateHuevo)){
			c.addToAcuaticList(this.getAgentID())
		}
	}
	
	@ScheduledMethod(
		start = 0d,
		interval = 12d,
		priority = 0.77d,
		shuffle = true
	)
	public void updateLife(){
		if((int)GetTickCount() == 0){
			this.inicializarEnConteinerInside();			// a los primeros agentes los posiciones en contenedores intradomiciliarios
			this.addToConteinerAcuaticList();
		}
		if(this.lifeCicle == null){
			this.Inicializar();
		}
		double  random = RandomHelper.nextDoubleFromTo(0,1);
		double  probabilidadDiariaDeMuerte = this.lifeCicle.getMortalidadNeta();
		boolean mortalidadHabilitada = dataSet.getAedesAcuatico_MortalidadHabilitada();
		if( random < probabilidadDiariaDeMuerte && mortalidadHabilitada ){
			this.eliminate();
		}
		else{
			if(this.lifeCicle.getLife() >= 0.95){					//cambia de estados , huevo(0)=>larva(1)=>pupa(2)
				this.nextLifeCicle();
			}
			this.lifeCicle.updateLife();
		}
	}

	private void nextLifeCicle(){
		switch (true){
			case 	  this.lifeCicle instanceof StateHuevo : this.eclocion();	 break;
			case 	  this.lifeCicle instanceof StateLarva : this.pupacion();	 break;
			case 	  this.lifeCicle instanceof StatePupa  : this.emercion();	 break;
			default 								       : this.oviposition(); break; 
		}
	}

	private void oviposition(){
		GridPoint myPosition	= FindGrid("infeccion/grid").getLocation(this);
		this.lifeCicle = new StateHuevo(myPosition, this.getAgentID()); //posicion necesaria para chequear puntos de Irrigacion
	}
	
	private void eclocion(){
		GridPoint myPosition	= FindGrid("infeccion/grid").getLocation(this);
		this.lifeCicle.eliminate(this.getAgentID());
		this.lifeCicle = new StateLarva(myPosition, this.getAgentID()); //posicion necesaria para chequear puntos de Irrigacion
		this.addToConteinerAcuaticList();
	}
	
	private void pupacion(){
		GridPoint myPosition	= FindGrid("infeccion/grid").getLocation(this);
		this.lifeCicle.eliminate(this.getAgentID());
		this.lifeCicle = new StatePupa(myPosition, this.getAgentID());	//posicion necesaria para chequear puntos de Irrigacion
	}
	
	private void emercion(){
		this.lifeCicle.getMyConteiner().addToAdultProductivity();
		Grid grid=FindGrid("infeccion/grid");
		GridPoint pt=grid.getLocation(this);	
		Object agent = CreateAgents("infeccion", "infeccion.Adulto", 1);
		grid.moveTo(agent,pt.getX(),pt.getY());
		this.eliminate();
	}
	
	public void eliminate(){
		Conteiner myConteiner = this.lifeCicle.getMyConteiner()
		if (myConteiner != null && !(this.lifeCicle instanceof StateHuevo)){
			ArrayList<Integer> acuaticListInConteiner = myConteiner.getAcuaticList();
			if(acuaticListInConteiner.contains(this.getAgentID())){
				myConteiner.removeFromAcuaticList(this.getAgentID())
			}
		}
		this.removeAcuaticListObject(this);
		this.lifeCicle.eliminate(this.getAgentID());
		RemoveAgentFromContext("infeccion", this);
		this.agentIDCounter -= 1;
	}
	
	public GridPoint[] generateRandomPosition(){
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

	@ProbeID()
	public String toString() {
		def returnValue;
		def time = GetTickCountInTimeUnits();
		returnValue = this.agentID;
		return returnValue;
	}
	
}


//en el generador si posicion dad = 00 y no hay contenedores, eliminar huevos










