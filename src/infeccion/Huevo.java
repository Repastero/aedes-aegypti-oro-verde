package infeccion;

import infeccion.LifeCicle;
import repast.simphony.ui.probe.*;

public class Huevo extends LifeCicle {
	private static int agentIDCounter = 0;

	private Container container; // para saber nivel del agua
	private double initialWater;
	private double getInitialWater() {
		return initialWater;
	}
	
	public  static void setCantidad(int val) { agentIDCounter = val; }
	public  static int getCantidad(){ return agentIDCounter; }

	Huevo() { }//constructor vacio para generar archivo con cantidades por dia
	Huevo(double life, Integer agentID, Container container) {
		super(life);
		Huevo.agentIDCounter++;
		this.container = container;
		this.initialWater = container.getMmWater();
	}

	@Override
	public void updateLife() {
		double desMeta = Clima.getDMDHuevo();
		super.updateLife(desMeta);
		if (super.getLife() >= 0.95d)
			habilitarEclocion();
	}

	/**
	 * No permite eclosionar al Huevo hasta que el nivel de agua no supere el nivel inicial.<p>
	 * Si el contenedor es intradomiciliario, el Huevo eclosiona al cumplir su desarrollo.
	 */
	private void habilitarEclocion() {
		if (!container.isInside() && (container.getMmWater() <= getInitialWater())) {
			super.setLife(0.94d);
		}
	 }

	@Override
	protected double mortalidadDiaria() {
		return DataSet.mortalidadDiariaHuevos;
	}

	@Override
	public double updateMortalidadNeta() {
		return mortalidadDiaria();
	}

	@Override
	public void eliminate(int agentID) {
		Huevo.agentIDCounter--;
	}

	@ProbeID
	public String toString() {
		return String.valueOf(Huevo.agentIDCounter);
	}
}
