package infeccion

import static java.lang.Math.*
import static repast.simphony.essentials.RepastEssentials.*

import java.util.ArrayList;
import java.util.Set;

import infeccion.*
import repast.simphony.context.space.grid.*
import repast.simphony.engine.schedule.*
import repast.simphony.parameter.*
import repast.simphony.space.grid.*
import repast.simphony.ui.probe.*

public class StateLarva extends LifeCicle{
	private static int agentIDCounter = 0;
	public  static int setCantidad(int val){this.agentIDCounter = val;}
	public  static int getCantidad(){return this.agentIDCounter;}
	
	private static ArrayList<Integer> 	     acuaticSet = new ArrayList<Integer>();
	public  static ArrayList<Integer>      getAcuaticSet() {return this.acuaticSet}
	public  static int 	          getAcuaticSettSize(){ return this.acuaticSet.size()}	//crear un metodo en WaterSites--> WaterSites.getAcuaticAmount(agentID)   //agentId = id del contenedor
	public  static void 	        addToAcuaticSet(Integer newValue){this.acuaticSet.add(newValue)}
	public  static boolean      containsAcuaticSet(Integer value){return this.acuaticSet.contains(value);}
	public  static void 	   removeFromAcuaticSet(Integer value){
		int index = this.acuaticSet.indexOf(value)
		this.acuaticSet.remove(index);
	}

	StateLarva(){}//constructor vacio para generar archivo con cantidades por dia 
	StateLarva(GridPoint myPosition, Integer agentID){ 
		super(myPosition);
		this.setMyPosition(myPosition);
		this.agentIDCounter += 1;
		this.addToAcuaticSet(agentID);
    }
	StateLarva(double life, Integer agentID){
		super(life);
		this.agentIDCounter += 1;
		this.addToAcuaticSet(agentID);
	}

	public void updateLife(){
		double newValue = this.getLife() + this.getDesarrolloMetabolicodiario();
		this.setLife(newValue);
		def myDebuggerSet = this.getAcuaticSet()
		def debugger = 2;
	}
	
	public double getDesarrolloMetabolicodiario(){
		double R   = 0.98588;
		double Rdk = 0.2088;
		double Ha  = 26018;
		double Hh  = 55990;
		double T12 = 304.6
		double desarrolloDiario = this.getDesarrolloMetabolicodiario(R, Rdk, Ha, Hh, T12);
		return desarrolloDiario;
	}
	
	protected double mortalidadDiaria(){
		double T = Clima.getTemperatureDay() + 273; //temperatura en °K
		double mortalidad = 0.01 + 0.9725*Math.exp(-(T-278)/2.7035);
		return mortalidad
	}
	
	public void mortalidadNeta(){
		double mortalidad = this.mortalidadDiaria()
		def life = this.getLife();
		if (this.mortalidadCapacidadAcarreo()){// && (this.life == 0)){ 
			mortalidad = 1;
		}
		this.setMortalidadNeta(mortalidad);
	}
	
	private boolean mortalidadCapacidadAcarreo(){
		boolean muere = false
		Conteiner conteiner= this.getMyConteiner();
		if( conteiner == null ){									     //si no hay un contenedor
			muere = true;													    //muere
		}else{														     //si hay un contenedor, lo obtengo
			def acuaticAmountInContainer = conteiner.getAcuaticListSize() //obtengo cuantos Aedes acuaticos tiene ese contenedor
			def carryingCapacity 	     = conteiner.getCarryingCapacity()//obtengo la capacidad de acarreo que tiene ese contenedor
			if(acuaticAmountInContainer > carryingCapacity){	  		  //si hay menos de los permitido,
				muere = true									 				//muere
			}
		}
		return muere
	}
	
	public void eliminate(int agentID){
		this.removeFromAcuaticSet(agentID)
		this.agentIDCounter -= 1;
	}
	
	@ProbeID()
	public String toString() {
		def returnValue;
		def time = GetTickCountInTimeUnits();
		returnValue = this.agentID;
		return returnValue;
	}

}
