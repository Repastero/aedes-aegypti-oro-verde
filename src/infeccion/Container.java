package infeccion

import static java.lang.Math.*
import static repast.simphony.essentials.RepastEssentials.*

import infeccion.*
import java.math.*
import java.util.ArrayList
import java.util.HashMap;

import javax.measure.unit.*

import org.jscience.mathematics.number.*
import org.jscience.mathematics.vector.*

import infeccion.*
import repast.simphony.adaptation.neural.*
import repast.simphony.adaptation.regression.*
import repast.simphony.context.*
import repast.simphony.context.space.continuous.*
import repast.simphony.context.space.gis.*
import repast.simphony.context.space.graph.*
import repast.simphony.context.space.grid.*
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

class Conteiner{
	//------------------------------------Class Properties--------------------------------
	private static final long serialVersionUID = 1L
	public 	static int	   		   agentIDCounter = -1
	public 	static int	inicializarAgentIDCounter() {this.agentIDCounter = -1}

	//------------------------------------Properties--------------------------------
	//							Agents Identifiers
	protected String 	 agentID  	   = "Conteiner " + (this.agentIDCounter += 1);
	protected Integer  	 agentIDInteger = this.agentIDCounter;
	public	  Integer getAgentID(){return this.agentIDInteger;};
	//							Agents Identifiers

	private int        carryingCapacity
	public  int  	getCarryingCapacity(){
		return this.carryingCapacity
	}
	private void 	setCarryingCapacity(int newValue){
		this.carryingCapacity = newValue
	}
	private void updateCarryingCapacity(){
		double water = this.getMmWater();
		int cc = this.obtainCarryingCapacity(water);
		this.setCarryingCapacity(cc)
		//this.setCarryingCapacity(100)
	}
	private int obtainCarryingCapacity(double mmWater) {
		double capacidadAcarreoPorLitro = (double)dataSet.getConteiner_capacidadAcarreoPorLitro()
		int CCapacity = (int)(0.1*mmWater*(double)this.dimensiones[0]*0.001*capacidadAcarreoPorLitro); //Capacidad de acarreo = (0.1*Agua[mm] = Agua[cm])*(Area = [cm^2])*(70 individuos por litro)
		return CCapacity;
	}

	private int            carryingCapacityEggs
	public  int  	    getCarryingCapacityEggs(){
		return this.carryingCapacityEggs
	}
	private void 	    setCarryingCapacityEggs(int newValue){
		this.carryingCapacityEggs = newValue
	}
	private void updateCarryingCapacityEggs(){
		int cc;
		if(this.getInside()){
			cc = this.getCarryingCapacity();
		}
		else{
			def maxAmountOfWater = this.getDimensionesAlto()*10;
			cc = obtainCarryingCapacity(maxAmountOfWater)/2	//capacidad de acarreo media
		}
		int maxAmountAcuatic = cc*5;
		int minAmountAcuatic = cc*2;
		int maxAmount = RandomHelper.nextIntFromTo(maxAmountAcuatic, minAmountAcuatic);

		this.setCarryingCapacityEggs(maxAmount);
		//this.setCarryingCapacityEggs(cc*2);
	}

	private boolean    	   inside = false
	public  boolean 	getInside(){
		return this.inside
	}
	private void 		setInside(boolean newValue){
		this.inside = newValue
	}
	private void incializarInside() {
		boolean inside = false;
		int probabilityInside = dataSet.getConteiner_porcentajeContenedoresInside()
		int r = (int)RandomHelper.nextIntFromTo(1,100);
		if(r < probabilityInside){
			inside = true;
		}
		this.setInside(inside)
	}

	private double[]  		    dimensiones  //[Area[cm^2], Alto[cm]]
	public  double[] 	     getDimensiones(){ return this.dimensiones}
	public  double 		     getDimensionesArea(){
		return this.dimensiones[0]
	}
	public  double	 	     getDimensionesAlto(){
		return this.dimensiones[1]
	}
	private void 		     setDimensiones(double[] newValue){
		this.dimensiones = newValue
	}
	private void     inicializarDimensiones() {
		double area   = this.inicializarDimensionesArea();
		double altura = this.inicializarDimensionesAltura();
		double[] dimensiones = [area, altura];
		this.setDimensiones(dimensiones)
	}
	private double   inicializarDimensionesAltura() {
		double valorMedioAlturaContenedores    = dataSet.getConteiner_valorMedioAlturaContenedores();
		double valorMedioAlturaContenedoresSTD = dataSet.getConteiner_valorMedioAlturaContenedoresSTD();
		RandomHelper.createNormal(valorMedioAlturaContenedores, valorMedioAlturaContenedoresSTD);
		double altura = RandomHelper.normal.nextDouble();
		while(altura<0){
			altura = RandomHelper.normal.nextDouble();
		}
		return altura
	}
	private double   inicializarDimensionesArea() {
		double valorMedioAreaContenedores 	 = dataSet.getConteiner_valorMedioAreaContenedores()
		double valorMedioAreaContenedoresSTD = dataSet.getConteiner_valorMedioAreaContenedoresSTD();
		RandomHelper.createNormal(valorMedioAreaContenedores, valorMedioAreaContenedoresSTD);
		double area = RandomHelper.normal.nextDouble();
		while(area<0){
			area = RandomHelper.normal.nextDouble();
		}
		return area
	}

	public  int[]     		 position;
	public  int[] 		  getPosition(){
		return this.position;
	}
	private void  		  setPosition(int[] newValue){
		this.position = newValue;
	}
	//Validacion David// private int[][]      newPosition = [[100, 100], [98, 97], [96, 101], [94, 98]];				//borrar
	//Validacion David//	private static int 	indexNewPosition = 0;										//borrar
	//Validacion David// public static int 	inicializarIndexNewPosition(){this.indexNewPosition = 0;}	//borrar

	private void  inicializarPosition() {
		Grid grid = FindGrid("infeccion/grid");
		GridPoint myPosition = grid.getLocation(this);
		int anchoGrilla = (int)grid.getDimensions().getHeight()-1;
		int altoGrilla  = (int)grid.getDimensions().getWidth()-1;
		int[] position
		if(this.getInside()){
			def  dimensions = grid.getDimensions();
			int valorMedioAncho = anchoGrilla/2;
			int valorMedioAnchoSTD = valorMedioAncho*0.7;//0.6
			int valorMedioAlto = anchoGrilla/2;
			int valorMedioAltoSTD = valorMedioAncho*0.7;//0.6
			int positionX = 0;
			int positionY = 0;
			//Validacion David// double[] dimensionesOutside = [212, 15];						//borrar
			//Validacion David// this.setDimensiones(dimensionesOutside);					//borrar
			//Validacion David// int positionX = this.newPosition[this.indexNewPosition][0];	//borrar		//=0
			//Validacion David// int positionY = this.newPosition[this.indexNewPosition][1];	//borrar		//=0
			//Validacion David// if (this.indexNewPosition == 2){							//borrar
			//Validacion David// 	this.setInside(false);									//borrar
			//Validacion David// }															//borrar
			//Validacion David// if (this.indexNewPosition == 3){							//borrar
			//Validacion David// 	this.setInside(false);									//borrar
			//Validacion David// 	dimensionesOutside = [100, 10];							//borrar
			//Validacion David// 	this.setDimensiones(dimensionesOutside);				//borrar
			//Validacion David// }															//borrar
			//Validacion David// this.indexNewPosition++;									//borrar
			while(positionX < 1 || positionX > anchoGrilla){
				def anchoNormal = RandomHelper.createNormal(valorMedioAncho, valorMedioAnchoSTD)
				positionX = anchoNormal.nextInt()
			}
			while(positionY < 1 || positionY > altoGrilla){
				def altoNormal = RandomHelper.createNormal(valorMedioAlto, valorMedioAltoSTD)
				positionY = altoNormal.nextInt()
			}
			position = [positionX, positionY]
		}
		else{
			int positionX = RandomHelper.nextIntFromTo(1,anchoGrilla);
			int positionY = RandomHelper.nextIntFromTo(1,altoGrilla);
			position = [positionX, positionY];
		}
		grid.moveTo(this,position[0], position[1]);
		this.setPosition(position)
	}

	private	boolean 	haveMmWater = false
	public	boolean  getHaveMmWater(){
		return this.haveMmWater
	}
	private	void	 setHaveMmWater(boolean newValue){
		this.haveMmWater = newValue
	}
	private void updateHaveMmWater() {
		if(this.getMmWater()>0){
			this.setHaveMmWater(true)
		}
		else{
			this.setHaveMmWater(false)
		}
	}

	private double          mmWater = 0;
	public  double       getMmWater(){
		return this.mmWater
	}
	private void 	     setMmWater(double newValue){
		this.mmWater = newValue
	}
	private void      updateMmWater(){
		def totalWater = this.getMmWater()
		if(totalWater>0 && (int)GetTickCount() != 0){
			totalWater = totalWater - Clima.getEvaporation(totalWater);
			if(totalWater<0){// mm de agua
				totalWater = 0
			}
		}
		totalWater += Clima.getPrecipitationDay();
		def altoConteiner = this.getDimensionesAlto()
		if(totalWater > altoConteiner*10){	//contenedor lleno
			totalWater = altoConteiner*10
		}
		this.setMmWater(totalWater)
		this.updateHaveMmWater()
	}
	private void inicializarMmWater(){
		def altura = this.getDimensionesAlto()*10 //paso de cm de altura a mm
		this.setMmWater(altura)
		this.updateHaveMmWater()
	}


	private int 	     eggsAmount= 0
	public  int       getEggsAmount(){
		return this.eggsAmount;
	}
	private void      setEggsAmount(int newValue){
		this.eggsAmount = newValue;
	}
	private void addOneToEggsAmount(){
		this.setEggsAmount(this.getEggsAmount() + 1);
	}
	private void restOneToEggsAmount(){
		this.setEggsAmount(this.getEggsAmount() - 1);
	}



	private int 	   acuaticAmount= 0
	public  int     getAcuaticAmount(){
		return this.acuaticAmount;
	}
	private void    setAcuaticAmount(int newValue){
		this.acuaticAmount = newValue;
	}

	private void updateAmountAcuatics(){
		Grid grid = FindGrid("infeccion/grid");
		//GridPoint pt = grid.getLocation(this)
		GridPoint pt = new GridPoint(this.getPosition())//debuggear ver si anda!!
		Iterable objects = grid.getObjectsAt(pt.getX(), pt.getY());
		int acuaticAmount = 0
		while (objects.hasNext()) {
			Object o = objects.next()
			if (o instanceof Acuatico) {
				acuaticAmount++;
			}
		}
		this.setAcuaticAmount(acuaticAmount);
	}

	private ArrayList<Double> mmWaterOutsideHistory    = new ArrayList<Double>()

	private ArrayList<Double>  	 acuaticListHistory  = new ArrayList<Double>()
	public  ArrayList<Double> getAcuaticListHistory() {
		return this.acuaticListHistory
	}

	private ArrayList<Integer>    acuaticList = new HashSet<Integer>();
	public  ArrayList<Integer> getAcuaticList() {
		return this.acuaticList
	}
	public  int 	           getAcuaticListSize(){
		return this.acuaticList.size()
	}
	public  void 	         addToAcuaticList(Integer newValue){
		this.acuaticList.add(newValue)
	}
	public  boolean       containsAcuaticList(Integer value){
		return this.acuaticList.contains(value);
	}
	public  void 	    removeFromAcuaticList(Integer value){
		int index = this.acuaticList.indexOf(value)
		this.acuaticList.remove(index);
	}



	private ArrayList<Integer>  	 adultProductivityHistory  = new ArrayList<Integer>()
	public  ArrayList<Integer> getAdultProductivityHistory() {
		return this.adultProductivityHistory
	}

	private ArrayList<Integer>  	 adultProductivityCleanHistory  = new ArrayList<Integer>()
	public  ArrayList<Integer> getAdultProductivityCleanHistory() {
		return this.adultProductivityCleanHistory
	}

	private int        adultProductivity = 0;
	public  int     getAdultProductivity() {
		return this.adultProductivity
	}
	private void    setAdultProductivity(int newValue) {
		this.adultProductivity = newValue;
	}
	public  void  addToAdultProductivity(){
		this.adultProductivity += 1;
	}

	//------------------------------------Methods--------------------------------

	private void inicializarConteiner(){
		this.incializarInside();
		this.inicializarDimensiones();
		this.inicializarPosition();
		if(this.getInside()){
			this.inicializarMmWater()
			this.updateCarryingCapacity()
			this.updateGlobalValuesInsideContainers()
		}
	}

	private void updateGlobalValuesInsideContainers() {
		HashMap<String, Double> insideMap = new HashMap<String, Double>();
		insideMap.putAt("x",this.getPosition()[0]);
		insideMap.putAt("y",this.getPosition()[1]);
		insideMap.putAt("mmWater",this.getMmWater());
		insideMap.putAt("alto",this.getDimensiones()[1]);
		insideMap.putAt("area",this.getDimensiones()[0]);
		insideMap.putAt("capacidadDeAcarreo",this.getCarryingCapacity());
		insideMap.putAt("cantidadAcuaticos",this.acuaticList.size());///////ACTUALIZAR EN EL TIEMPO para los contenedores inside/outside
		WaterSites.addValuesInsideContainersMap(this.getAgentID(),insideMap)
	}

	@ScheduledMethod(
	start = 0d,
	interval = 12d,
	priority = 0.92d,
	shuffle = true
	)
	public  void updateDay(){															//priority = 0.92d,
		if((int)GetTickCount() == 0){
			this.inicializarConteiner()
		}
		if(!this.getInside()){
			this.updateMmWater();
			this.updateCarryingCapacity()
			this.mmWaterOutsideHistory.add(this.getMmWater())
		}
		this.updateCarryingCapacityEggs()
		this.updateAmountAcuatics();
		this.acuaticListHistory.add(this.getAcuaticList().size());
		this.adultProductivityHistory.add(this.adultProductivity);
		if(this.adultProductivity > 0){
			this.adultProductivityCleanHistory.add(this.adultProductivity);
		}
		this.updateWaterSites()
		this.setAdultProductivity(0);
	}

	private void updateWaterSites(){
		if(!this.getInside()){
			WaterSites.updateGlobalWaterAcumulated(this.getMmWater());
			WaterSites.addValueMmWaterOutsideHistoryPerConteinerMap(this.getAgentID(),this.mmWaterOutsideHistory)
		}
		def diaAlamacenamientoEnArchivos = dataSet.getWaterSites_intervaloDiaAlamacenamientoEnArchivos();  //dia para almacenar corridas
		if (Clima.getDayNumber() % diaAlamacenamientoEnArchivos == 0){
			if(!this.checkIfIsEmpty(this.getAcuaticListHistory())){
				WaterSites.addValueAcuaticListByConteinerHistoryMap(this.getAgentID(), this.getInside(), this.getAcuaticListHistory())
			}
			if(!this.checkIfIsEmpty(this.getAdultProductivityHistory())){
				WaterSites.addValueAdultProductivityByConteinerHistoryMap(this.getAgentID(), this.getInside(), this.getAdultProductivityHistory())
			}
			WaterSites.addValueAdultProductivityByConteinerCleanHistoryMap(this.getAgentID(),this.getInside(),this.getAdultProductivityCleanHistory())
		}
	}

	private checkIfIsEmpty(ArrayList acuaticListHistory) {
		boolean empty =  true;
		for(acuatics in acuaticListHistory ){
			if (acuatics != 0){
				empty = false;
				break;
			}
		}
		return empty
	}

	@ProbeID()
	public String toString() {
		def returnValue;
		def time = GetTickCountInTimeUnits();
		// Set the default agent identifier.
		returnValue = this.agentID;
		return returnValue;
	}
}

