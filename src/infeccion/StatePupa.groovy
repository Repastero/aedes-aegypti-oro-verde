package infeccion

import static java.lang.Math.*
import static repast.simphony.essentials.RepastEssentials.*

import java.util.ArrayList;
import java.util.Set

import infeccion.LifeCicle;
import repast.simphony.engine.schedule.*
import repast.simphony.parameter.*
import repast.simphony.space.grid.Grid
import repast.simphony.space.grid.GridPoint;
import repast.simphony.ui.probe.*

public class StatePupa  extends LifeCicle{
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
	
	StatePupa(){}//constructor vacio para generar archivo con cantidades por dia 
	StatePupa(GridPoint myPosition, Integer agentID){     
		super(myPosition); 
		this.agentIDCounter += 1;
		this.addToAcuaticSet(agentID);
    }
	StatePupa(double life, Integer agentID){
		super(life);
		this.agentIDCounter += 1;
		this.addToAcuaticSet(agentID);
	}

	public void updateLife(){
		double newValue = this.getLife() + this.getDesarrolloMetabolicodiario(); 
		this.setLife(newValue);
		def myDebuggerSet = this.getAcuaticSet()
		def debugger = 2
	}
	
	public double getDesarrolloMetabolicodiario(){
		double R   = 0.98588;
		double Rdk = 0.384;
		double Ha  = 14931;
		double Hh  = -472379;
		double T12 = 148;
		double desarrolloDiario = this.getDesarrolloMetabolicodiario(R, Rdk, Ha, Hh, T12);
		return desarrolloDiario;
	}

	protected double mortalidadDiaria(){
		def T = Clima.getTemperatureDay() + 273; //temperatura en °K
		def mortalidad = 0.01 + 0.9725*Math.exp(-(T-278)/2.7035);
		return mortalidad;
	}
	public void mortalidadNeta(){
		double mortalidad = this.mortalidadDiaria()
		if(this.getMortalidadEmergencia()>0){
			mortalidad = this.getMortalidadEmergencia();
		}
		this.setMortalidadNeta(mortalidad)
		//this.setMortalidadNeta(0)
	}
	
	public double getMortalidadEmergencia(){
		def mortalidadEmergencia = 0;
		def life = this.getLife();
		if (life>=0.95){//si va emerger seteo la mortalidad por emergencia
			mortalidadEmergencia = 0.17;
		}
		return mortalidadEmergencia;
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