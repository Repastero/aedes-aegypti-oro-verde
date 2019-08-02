package infeccion

import static java.lang.Math.*
import static repast.simphony.essentials.RepastEssentials.*

import infeccion.aedes.*

import java.math.*

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
import cern.jet.random.Normal

class WaterSites{
	//------------------------------------Properties--------------------------------
	//	private final static int     globalCarryingCapacity = 20000
	//	public  final static int  getGlobalCarryingCapacity(){return this.globalCarryingCapacity}
	
	private static ArrayList<Integer> huevoAmountHistory = new ArrayList<>()
	private static ArrayList<Integer> larvaAmountHistory = new ArrayList<>()
	private static ArrayList<Integer>  pupaAmountHistory = new ArrayList<>()
	
	private static ArrayList<List<Integer>> huevoListHistory = new ArrayList<>()
	private static ArrayList<List<Integer>> larvaListHistory = new ArrayList<>()
	private static ArrayList<List<Integer>>  pupaListHistory = new ArrayList<>()
	
	private static StateHuevo huevo = new StateHuevo();
	private static StateLarva larva = new StateLarva();
	private static StatePupa  pupa  = new StatePupa();

	private static double    globalMeanMmWater = 0
	public  static double getGlobalMeanMmWater(){return this.globalMeanMmWater}
	private static void   setGlobalMeanMmWater(double newValue){
		this.globalMeanMmWater = newValue
	}
	public  static void updateGlobalMeanMmWater(){											//priority = 0.84d,
		def GlobalMeanMmWater = 0;
		def historyWaterInConteiners = this.getMmWaterOutsideHistoryPerConteinerMap();
		if(!historyWaterInConteiners.isEmpty()){
			GlobalMeanMmWater = this.getGlobalWaterAcumulated()/historyWaterInConteiners.size();
		}
		this.setGlobalMeanMmWater(GlobalMeanMmWater)
	}

	private   static double            globalWaterAcumulated = 0
	public    static double         getGlobalWaterAcumulated(){return this.globalWaterAcumulated}
	protected static void           setGlobalWaterAcumulated(double newValue){ //se actualiza siendo llamado por cada contenedor
		this.globalWaterAcumulated = newValue
	}
	protected static void        updateGlobalWaterAcumulated(double newValue){
		def newGlobalWaterAcumulated = this.getGlobalWaterAcumulated() + newValue;
		this.setGlobalWaterAcumulated(newGlobalWaterAcumulated);
	}

	protected static HashMap<Integer, ArrayList<Double>>         mmWaterOutsideHistoryPerConteinerMap = new HashMap<> ();
	public 	  static HashMap<Integer, ArrayList<Double>>      getMmWaterOutsideHistoryPerConteinerMap() { 
		return this.mmWaterOutsideHistoryPerConteinerMap 
	}
	public 	  static void							    addValueMmWaterOutsideHistoryPerConteinerMap(Integer agentID, ArrayList<Double> waterList){
		this.mmWaterOutsideHistoryPerConteinerMap.putAt(agentID, waterList);
	}

	protected static Map<Integer, ArrayList<Integer>>     acuaticListByConteinerHistoryMap = new HashMap<> ();
	public 	  static Map<Integer, ArrayList<Integer>>  getAcuaticListByConteinerHistoryMap() {
		return this.acuaticListByConteinerHistoryMap 
	}
	public 	  static void						    addValueAcuaticListByConteinerHistoryMap(Integer agentID, Boolean inside, ArrayList<Integer> amountAcuaticsHistory){
		String id = agentID.toString();
		if (inside){
			id = "i  " + id
		}else{
			id = "o  " + id
		}
		this.acuaticListByConteinerHistoryMap.put(id, amountAcuaticsHistory);
	}
	
	protected static Map<String, ArrayList<Integer>>     adultProductivityByConteinerHistoryMap = new HashMap<> ();
	public 	  static Map<String, ArrayList<Integer>>  getAdultProductivityByConteinerHistoryMap() { 
		return this.adultProductivityByConteinerHistoryMap 
	}
	public 	  static void						    addValueAdultProductivityByConteinerHistoryMap(Integer agentID, Boolean inside, ArrayList<Integer> adultProductivityHistory){
		String id = agentID.toString();
		if (inside){
			id = "i  " + id
		}else{
			id = "o  " + id
		}
		this.adultProductivityByConteinerHistoryMap.put(id, adultProductivityHistory);
	}
	
	protected static Map<String, ArrayList<Integer>>     adultProductivityByConteinerCleanHistoryMap = new HashMap<> ();
	public 	  static Map<String, ArrayList<Integer>>  getAdultProductivityByConteinerCleanHistoryMap() {
		return this.adultProductivityByConteinerCleanHistoryMap
	}
	public 	  static void						    addValueAdultProductivityByConteinerCleanHistoryMap(Integer agentID, Boolean inside, ArrayList<Integer> adultProductivityHistory){
		String id = agentID.toString();
		if (inside){
			id = "i  " + id
		}else{
			id = "o  " + id
		}
		this.adultProductivityByConteinerCleanHistoryMap.put(id, adultProductivityHistory);
	}

	protected static HashMap<Integer, HashMap<String, Double>>    valuesInsideContainersMap = new HashMap<>(); //id: posicionX, posicionY, mmWater, alto, area, capacidadAcarre, cantidadAcuaticos
	protected static HashMap<Integer, HashMap<String, Double>> getValuesInsideContainersMap(){ return this.valuesInsideContainersMap };
	protected static void					 			       addValuesInsideContainersMap(Integer agentID, HashMap<String, Double> newValues){
		this.valuesInsideContainersMap.putAt(agentID, newValues)
	}

	private static int    	  				   carryingCapacityNeta = 8000
	public  static int     					getCarryingCapacityNeta() {
		return this.carryingCapacityNeta
	}
	private static void    					setCarryingCapacityNeta(int newValue) {
		this.carryingCapacityNeta = newValue;
	}
	
	//------------------------------------Methods--------------------------------
	private void crearContenedores(){
		int conteinerAmount = dataSet.getWaterSites_conteinerAmount();
		for (int i = 0; i < conteinerAmount; i++) {
			this.crearContenedor()
		}
	}

	private void crearContenedor(){
		Grid grid = FindGrid("infeccion/grid");
		def  dimensions = grid.getDimensions();
		int anchoGrilla = (int)grid.getDimensions().getHeight()-1;
		int altoGrilla  = (int)grid.getDimensions().getWidth()-1;
		int   positionX = (int)RandomHelper.nextIntFromTo(1,anchoGrilla);
		int   positionY = (int)RandomHelper.nextIntFromTo(1,altoGrilla);
		Object agent = CreateAgents("infeccion", "infeccion.Conteiner", 1);
		//grid.moveTo(agent,positionX, positionY);
		//this.addValueConteinerHistoryMap(agent)
	}

	@ScheduledMethod(
	start = 0d,
	interval = 12d,
	priority = 0.95d,
	shuffle = true
	)
	public void inicializadorDiario(){										//priority = 0.95d,
		if(GetTickCount() == 0){
			this.inicializarWaterSites()
		}
		this.setGlobalWaterAcumulated(0);
		this.setGlobalMeanMmWater(0)
	}

	private void inicializarWaterSites(){
		//Validacion David// Conteiner.inicializarIndexNewPosition(); 			//borrar
		this.mmWaterOutsideHistoryPerConteinerMap.clear()
		this.valuesInsideContainersMap.clear()
		Conteiner.inicializarAgentIDCounter()
		this.crearContenedores()
	}

	@ScheduledMethod(
		start = 0d,
		interval = 12d,
		priority = 0.84d,//0.74d
		shuffle = true
	)
	public void updateValues(){											//priority = 0.84d,		// RAFAAA!!!!! COMENTAR METODO COMPLETO!
		this.updateGlobalMeanMmWater();
		// -----------------   comentado para correr en Batch  -----------------   
		//		if(GetTickCount() == 0){
		//			this.saveValuesInsideFile()
		//		}
		//		def diaAlamacenamientoEnArchivos = dataSet.getWaterSites_intervaloDiaAlamacenamientoEnArchivos();  //dia para almacenar corridas
		//		if (Clima.getDayNumber() % diaAlamacenamientoEnArchivos == 0){			
		//			this.saveWaterOutsideFile();
		//			this.saveAcuaticListByConteinerFile();
		//			this.saveAdultProductivityByConteinerFile();
		//			this.saveAdultProductivityByConteinerCleanFile();
		//		}
		//-----------------   comentado para correr en Batch  -----------------
	}
	
	@ScheduledMethod(													//priority = 0.66d,
	start = 0d,
	interval = 12d,
	priority = 0.66d,
	shuffle = true
	)
	public void updateAcuaticValues(){
		List<Integer> huevoList = new ArrayList<Integer>();
		huevoList.addAll(this.huevo.getAcuaticSet());
		this.huevoListHistory.add(huevoList)
		this.huevoAmountHistory.add(huevoList.size())
				
		List<Integer> larvaList = new ArrayList<Integer>();
		larvaList.addAll(this.larva.getAcuaticSet());
		this.larvaListHistory.add(larvaList)
		this.larvaAmountHistory.add(larvaList.size())
		
		List<Integer> pupaList = new ArrayList<Integer>();
		pupaList.addAll(this.pupa.getAcuaticSet());
		this.pupaListHistory.add(pupaList)
		this.pupaAmountHistory.add(pupaList.size())
		
		if(Clima.getDayNumber()%2 == 0){
			this.updateCarryingCapacityNeta();
		}
		
		this.updateAaedesM()
	}

	public static void saveValuesInsideFile(){
		String fileName= 'DataValuesInside.txt';
		File file = this.generateFile(fileName);
		def valuesInsideContainersMap = this.getValuesInsideContainersMap().sort()
		String fileText = 'ID:[posicionX,\t posicionY,\t mmWater,\t Alto,\t Area,\t CapacidadDeAcarreo]\t\t CANTIDAD:'
		if(!valuesInsideContainersMap.isEmpty()){
			fileText += (String)valuesInsideContainersMap.size() + '\n';
			Iterator i = valuesInsideContainersMap.sort().iterator();
			while (i.hasNext()) {
				Map.Entry entry = (Map.Entry) i.next();
				String key = entry.getKey();
				fileText += "contenedor_"+ key + ": [";
				HashMap conteinerMap = entry.getValue()
				def x 				   = conteinerMap.get("x").toString()
				def y 				   = conteinerMap.get("y").toString()
				def mmWater 		   = conteinerMap.get("mmWater").toString()
				def alto 		       = conteinerMap.get("alto").toString()
				def area 			   = conteinerMap.get("area").toString()
				def capacidadDeAcarreo = conteinerMap.get("capacidadDeAcarreo").toString()
				fileText += x + ",\t" + y + ",\t" + mmWater + ",\t" + alto + ",\t" + area + ",\t" + capacidadDeAcarreo + "]" + "\n";
			}
			file<<fileText
		}
	}
	
	public static void saveWaterOutsideFile(){
		String fileName= 'DataWaterSitesOutside.txt';
		File file = this.generateFile(fileName);
		def dia = Clima.getDayNumber();
		def mmWaterHistoryPerConteinerMap = this.getMmWaterOutsideHistoryPerConteinerMap().sort()
		this.generateVectorPythonFile(mmWaterHistoryPerConteinerMap, file)
	}
	
	public static void saveAdultProductivityByConteinerFile(){
		String fileName= 'AdultProductivityByConteiner.txt';
		File file = this.generateFile(fileName);
		def adultProductivityMap = this.getAdultProductivityByConteinerHistoryMap().sort()
		this.generateVectorPythonFile(adultProductivityMap, file)
	}
	
	public static void saveAdultProductivityByConteinerCleanFile(){
		String fileName= 'AdultProductivityCleanByConteiner.txt';
		File file = this.generateFile(fileName);
		def adultProductivityMap = this.getAdultProductivityByConteinerCleanHistoryMap().sort()
		this.generateVectorPythonFile(adultProductivityMap, file)
	}

	public static void saveAcuaticListByConteinerFile(){
		String fileName= 'AcuaticListByConteiner.txt';
		File file = this.generateFile(fileName);
		Map valuesInsideContainersMap = this.getAcuaticListByConteinerHistoryMap().sort()
		this.generateVectorPythonFile(valuesInsideContainersMap, file)
	}

	private static void generateVectorPythonFile(Map mapToVector, File file) {
		if(!mapToVector.isEmpty()){
			String fileText = ''
			String newLine = ''
			Iterator i = mapToVector.sort().iterator();
			while (i.hasNext()) {
				Map.Entry entry = (Map.Entry) i.next();
				String key = entry.getKey();
				Iterator valuesIterator = entry.getValue().iterator();
				if(valuesIterator.hasNext()){
					newLine = key + ": [";
					while(valuesIterator.hasNext()){
						newLine += valuesIterator.next().toString()
						if(valuesIterator.hasNext()){
							newLine += ", ";
						}else{
							newLine += "]";
						}
					}
					fileText += newLine + "\n";
				}
			}
			file<<fileText
		}
	}
	
	private static File generateFile(String fileName){
		String path = 'C:/Users/usuario/workspaceUltimaV/infeccion/freezedried_data/waterSites/';
		String pathFileName = path + fileName;
		File file = new File(pathFileName)
		if (file.exists() && file.isFile()){
			file.delete();
		}
		return file
	}
	
	public  static void 			        updateCarryingCapacityNeta() {
		int capacidadNeta = dataSet.getAedesAcuatico_capacidadAcarreoNeta();
		int capacidadInicial = dataSet.getAedesAcuatico_capacidadAcarreoNetaInicial();
		int maxValueLogistic = 3300;
		int t = (int)GetTickCount();
		def ccNeta
		if(t<maxValueLogistic){
			ccNeta = logisticCarryingC(capacidadNeta, capacidadInicial)
		}else{
			ccNeta = expInversa(capacidadNeta, capacidadInicial, maxValueLogistic)
		}
		Normal norm = RandomHelper.createNormal(ccNeta, ccNeta*0.15 )
		int carryingCapacity = norm.nextInt();
		this.setCarryingCapacityNeta(carryingCapacity)
	}
	private static int logisticCarryingC(int capacidadNeta,int capacidadInicial){
		int t = (int)GetTickCount();
		int midleGrowth = 1700;
		int capacidadNetaParaEcuacion = capacidadNeta - capacidadInicial
		int result = capacidadInicial + capacidadNetaParaEcuacion/(1+Math.exp(-0.004*(t - midleGrowth)))
		return result;
	}
	
	private static int expInversa(int capacidadNeta,int capacidadInicial,int maxValueLogistic){
		double coefDecadencia = 6;
		double coefLetargo = coefDecadencia/(4300 - (double)maxValueLogistic)
		double capacidadNetaParaEcuacion = capacidadNeta - capacidadInicial
		double coefMult = capacidadNetaParaEcuacion/Math.exp(6);
		int t = (int)GetTickCount();
		int result =  capacidadInicial + (int)(coefMult*exp(-(t-maxValueLogistic)*coefLetargo + coefDecadencia))
		return result;
	}
	
	private updateAaedesM() {
		if(Clima.getDayNumber() == 1){
			dataSet.setAedesAdulto_MortalidadDiaria(0.07)
			dataSet.setAedesacuatico_Huevo_Mortalidaddiaria(0.01)			
		}
		if(Clima.getDayNumber()>250){
			def mA =  dataSet.getAedesAdulto_MortalidadDiaria() + 0.0015;
			def mH =  dataSet.getAedesacuatico_Huevo_Mortalidaddiaria() + 0.0015;
			dataSet.setAedesAdulto_MortalidadDiaria(mA)
			dataSet.setAedesacuatico_Huevo_Mortalidaddiaria(mH)
		}
	}
}

