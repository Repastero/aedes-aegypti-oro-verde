package infeccion;

import infeccion.LifeCicle;
import repast.simphony.ui.probe.*;

public class Pupa extends LifeCicle {
	private static int agentIDCounter = 0;
	public  static void setCantidad(int val){ agentIDCounter = val; }
	public  static int getCantidad(){ return agentIDCounter; }

	Pupa() { } //constructor vacio para generar archivo con cantidades por dia
	Pupa(double life, Integer agentID) {
		super(life);
		Pupa.agentIDCounter++;
	}

	@Override
	public void updateLife() {
		double desMeta = Clima.getDMDPupa();
		super.updateLife(desMeta);
	}
	
	@Override
	protected double mortalidadDiaria() {
		return Clima.getMDAcuatico();
	}
	
	@Override
	public double updateMortalidadNeta() {
		double mortalidad;
		if (super.getLife() < 0.95d)
			mortalidad = mortalidadDiaria();
		else // si va emerger seteo la mortalidad por emergencia
			mortalidad = 0.17d;
		return mortalidad;
	}
	
	@Override
	public void eliminate(int agentID) {
		Pupa.agentIDCounter -= 1;
	}

	@ProbeID
	public String toString() {
		return String.valueOf(agentIDCounter);
	}
}
