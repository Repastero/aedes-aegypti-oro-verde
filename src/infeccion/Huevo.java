package infeccion

import static java.lang.Math.*
import static repast.simphony.essentials.RepastEssentials.*

import java.util.ArrayList;
import java.util.Set

import bsh.This
import infeccion.LifeCicle
import repast.simphony.engine.schedule.*
import repast.simphony.parameter.*
import repast.simphony.space.grid.Grid
import repast.simphony.space.grid.GridPoint
import repast.simphony.ui.probe.*

public class StateHuevo extends LifeCicle{
	private static int agentIDCounter = 0;
	public  static int setCantidad(int val){this.agentIDCounter = val;}
	public  static int getCantidad(){return this.agentIDCounter;}
	
	private static ArrayList<Integer> 	     acuaticSet = new ArrayList<Integer>();
	public  static ArrayList<Integer>      getAcuaticSet() {return this.acuaticSet}
	public  static int 	          getAcuaticSettSize(){ return this.acuaticSet.size()}	//crear un metodo en WaterSites--> WaterSites.getAcuaticAmount(agentID)   //agentId = id del contenedor
	public  static void 	    addToAcuaticSet(Integer newValue){this.acuaticSet.add(newValue)}
	public  static boolean      containsAcuaticSet(Integer value){return this.acuaticSet.contains(value);}
	public  static void 	   removeFromAcuaticSet(Integer value){
		int index = this.acuaticSet.indexOf(value)
			this.acuaticSet.remove(index);
	}
	
	private double    initialWater;
	private double getInitialWater() {
		return initialWater;
	}
	private void   setInitialWater(double initialWater) {
		this.initialWater = initialWater;
	}
	
	
	StateHuevo(){}//constructor vacio para generar archivo con cantidades por dia 
	StateHuevo(GridPoint myPosition, Integer agentID){     
		super(myPosition);
		this.agentIDCounter += 1;
		this.addToAcuaticSet(agentID);
		this.inicializarInitialWater();
		this.checkIfAddToEggsAmount()
    }

	StateHuevo(double life, Integer agentID){
		super(life);
		this.agentIDCounter += 1;
		this.addToAcuaticSet(agentID);
		this.inicializarInitialWater();
		this.checkIfAddToEggsAmount()
	}
	
	private checkIfAddToEggsAmount() {
		int dia = Clima.getDayNumber();
		if(dia == 0){
			Conteiner myConteiner = this.getMyConteiner()
			if(myConteiner != null){
				myConteiner.addOneToEggsAmount();
			}
		}
	}
	
	private inicializarInitialWater() {
		Conteiner myConteiner = this.getMyConteiner() 
		if(myConteiner != null){
			def mmWaterIniciales = myConteiner.getMmWater();
			this.setInitialWater(mmWaterIniciales)
		}
	}
	
	public  void updateLife(){
		double newLife = this.getLife() + this.getDesarrolloMetabolicodiario();
		if(newLife >= 0.95){			
			this.habilitarEclocion(newLife);
		}
		else{
			this.setLife(newLife);
		}
	}
	
	public double getDesarrolloMetabolicodiario(){
		double R   = 0.98588;
		double Rdk = 0.24;
		double Ha  = 10798;
		double Hh  = 100000;
		double T12 = 14184;
		double desarrolloDiario = this.getDesarrolloMetabolicodiario(R, Rdk, Ha, Hh, T12);
		return desarrolloDiario;
	}

	private void habilitarEclocion(double newLife){
		double distanciaHuevoVsAgua = 5 // 5 [mm]//dataSet.getAedesAcuatico_Huevo_distanciaAguaVsHuevo();
		Conteiner c = this.getMyConteiner(); 
		if(c != null){
			if(!c.getInside()){
				if(c.getMmWater() > this.getInitialWater()){
					this.setLife(newLife);
				}
			}else{
				this.setLife(newLife);
			}
		}else{
			this.setLife(newLife);
		}
	 }
	
	public double mortalidadDiaria(){
		double mortalidad = dataSet.getAedesacuatico_Huevo_Mortalidaddiaria();
		return mortalidad;
	}
	
	public void mortalidadNeta(){
		double mortalidad = this.mortalidadDiaria();
		def life = this.getLife();
		if(this.restrainAmountEggs() && (life == 0)){ 
			mortalidad = 1;
		}
		this.setMortalidadNeta(mortalidad);
	}
	
	public void eliminate(int agentID){
		this.getMyConteiner().restOneToEggsAmount();
		this.removeFromAcuaticSet(agentID)
		this.agentIDCounter -= 1;
	}
	
	private boolean restrainAmountEggs(){
		boolean restrain = false;
		Conteiner myConteiner = this.getMyConteiner();
		if(myConteiner != null){
			int acuaticAmountInConteiner = myConteiner.getAcuaticAmount();
			int ccEggs = this.getMyConteiner().getCarryingCapacityEggs();
			if(acuaticAmountInConteiner > ccEggs){
				restrain = true;
			}
		}
		return restrain;
	}
	
	@ProbeID()
	public String toString() {
		def returnValue;
		def time = GetTickCountInTimeUnits();
		returnValue = this.agentID;
		return returnValue;
	}
}
















