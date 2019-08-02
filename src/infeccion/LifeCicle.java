package infeccion

import static java.lang.Math.*
import static repast.simphony.essentials.RepastEssentials.*
import repast.simphony.engine.schedule.*
import repast.simphony.parameter.*
import repast.simphony.space.grid.Grid
import repast.simphony.space.grid.GridPoint
import repast.simphony.ui.probe.*

public abstract class LifeCicle{ 
	LifeCicle(){}
	LifeCicle(GridPoint myPosition){
		this.setLife(0);
		this.setMyPosition(myPosition);
	};
	LifeCicle(double life){
		this.setLife(life);
	};
	
	//--------------------------------------Propertys-----------------------------------------------
	private   double    life;
	protected double getLife() {
		return this.life;
	}
	protected void 	 setLife(double newValue) {
		this.life = newValue;
	}

	private	double    mortalidadNeta;
	public 	double getMortalidadNeta(){
		this.mortalidadNeta()
		return this.mortalidadNeta
	};
	public 	double setMortalidadNeta(double newValue){
		this.mortalidadNeta = newValue
	};
	
	private   boolean    eclocionHumana = false;											//comentar
	protected boolean getEclocionHumana(){
		return this.eclocionHumana;
	}						//comentar
	protected void 	  setEclocionHumana(boolean newValue){ 
		this.eclocionHumana = newValue; 
	}//comentar
		
	private GridPoint    myPosition;
	public  GridPoint getMyPosition() {
		return this.myPosition;
	}
	public  void 	  setMyPosition(GridPoint gridPoint) {
		this.myPosition = gridPoint;
	}
	
	private Conteiner    myConteiner;
	public  Conteiner getMyConteiner(){
		this.searchConteiner()
		return this.myConteiner;
	}
	public  void 	  setMyConteiner(Conteiner newValue) {
		this.myConteiner = newValue;
	}

	//--------------------------------------Methods-----------------------------------------------
	public    abstract void updateLife();
	
	public 	  abstract void mortalidadNeta() //[0..1]metodo que devuelve la sumatorias de probabilidad de muertes (diaria+capacidad de acarreo).
	
	protected abstract double mortalidadDiaria();

	public    abstract void eliminate(int agentID);
	
	protected double getDesarrolloMetabolicodiario(double R,double Rdk, double Ha, double Hh, double T12){
		def T = Clima.getTemperatureDay() + 273;
		def desarrolloMetabolicodiario = Rdk*(T/298)*Math.exp((Ha/R)*(1/298  - 1/T))   / (1 + Math.exp((Hh/R)*(1/T12  - 1/T)));
		return desarrolloMetabolicodiario;
	}

	private void searchConteiner(){
		//		if (this.myConteiner == null){
		GridPoint pt = this.getMyPosition();
		if(pt !=null){
			Grid 	  grid    = FindGrid("infeccion/grid");
			Iterable objects = grid.getObjectsAt(pt.getX(), pt.getY());
			Conteiner c = this.myConteiner
			while (objects.hasNext() && c == null) {
				Object o = objects.next()
				o instanceof Conteiner ? this.setMyConteiner(o) : {};
			}
		}
		//		}
	}
		
	public static void saveFileAmount(Class myType){
		String path = 'C:/RepastSimphony-2.4/eclipse/WorkSapaces/Tesis/infeccion/OutFiles/';
		String fileName= myType.getSimpleName() +'.txt';
		String pathFileName = path + fileName;
		File file = new File(pathFileName)
		(!file.isFile()) ? this.generateFile(file, myType): {};
		def dia = Clima.getDayNumber();
		def diaLimite = 10;  //dia para almacenar corridas
		if(dia == 1){
			def name = myType.getSimpleName();
			def runNumber = Clima.runNumber.toString();
			def headVector = name + runNumber +" <- c("
			file << headVector;
		}
		if(dia < diaLimite){
			def a = myType.newInstance();
			def cantidad = a.getCantidad();
			String newLine = cantidad + ", ";
			(dia == diaLimite - 1) ? newLine = cantidad : {} ;
			file << newLine;
		}
		else{
			dia == diaLimite ? file << ")\n\n" : {} ;
		}
	}
	
	private static void generateFile(File file, Class myType){
		def name = myType.getSimpleName();
		String newLine =  "\n";
		file << newLine;
	}
}

