package infeccion;

import repast.simphony.random.*;
import repast.simphony.ui.probe.*;

public class Acuatico {
	private static int agentIDCounter = 0;
	private static int agentCount = 0;
	private int agentID = agentIDCounter;
	private String agentIDString = "Acuatico " + agentIDCounter++;
	private LifeCicle lifeCicle;
	private Container container;
	
	public Acuatico() {	}
	
	public Acuatico(Container container, int lifeStage, double life) {
		++agentCount;
		switch (lifeStage) {
			case 0:
				this.lifeCicle = new Huevo(life, agentID, container);
				break;
			case 1:
				this.lifeCicle = new Larva(life, agentID, container);
				break;
			default: // 2
				this.lifeCicle = new Pupa(life, agentID);
				break;
		}
		this.container = container;
	}
	
	public static void initAgentID() {
		agentIDCounter = 0;
		agentCount = 0;
	}
	
	public Container getContainer() {
		return container;
	}
	public void setContainer(Container container) {
		this.container = container;
	}
	
	public static int getCount() {
		return agentCount;
	}
	
	public static int getCantidadHuevos() {
		return Huevo.getCantidad();
	}
	public static int getCantidadLarvas() {
		return Larva.getCantidad();
	}
	public static int getCantidadPupas() {
		return Pupa.getCantidad();
	}
	public static int getCantidadLarvasYPupas() {
		return Pupa.getCantidad() + Larva.getCantidad();
	}
	
	/**
	 * Compara la cantidad de Acuaticos actuales, con un numero X; para limitar la cantidad de acuaticos.
	 * @return <code>true</code> si hay capacidad de oviposicion
	 */
	public static boolean oviposicionHabilitadaByCarryingCapacity() {
		return (agentCount < WaterSites.getCarryingCapacityNeta());
	}
	
	/**
	 * Actualiza el tiempo de desarrollo, la probabilidad de muerte y cambia de estado si corresponde.
	 * @return <code>true</code> si sigue con vida el actuatico
	 */
	public boolean updateLife() {
		double random = RandomHelper.nextDoubleFromTo(0, 1);
		double probabilidadDiariaDeMuerte = lifeCicle.updateMortalidadNeta();
		if ((random < probabilidadDiariaDeMuerte) && DataSet.ACUATICO_MORTALIDAD_HABILITADA) {
			// Acuatico muere
			if (getLifeCicle() == 0) // Huevo
				container.decreaseEggsAmount();
			else // Larva o Pupa
				container.decreaseAquaticAmount();
			lifeCicle.eliminate(agentID);
			--agentCount;
			return false;
		}
		else {
			// Acuatico sobrevive
			if (lifeCicle.getLife() >= 0.95) {
				nextLifeCicle();
				if (lifeCicle == null) // Si emergio el mosquito adulto - muere el Acuatico
					return false;
			}
			lifeCicle.updateLife();
		}
		return true;
	}

	public int getLifeCicle() {
		if (lifeCicle instanceof Huevo)
			return 0;
		else if (lifeCicle instanceof Larva)
			return 1;
		else
			return 2;
	}
	
	/**
	 * Cambia de estado Acuatico: Huevo(0) => Larva(1) => Pupa(2)
	 */
	private void nextLifeCicle() {
		if (lifeCicle instanceof Huevo)
			eclocion();
		else if (lifeCicle instanceof Larva)
			pupacion();
		else // (lifeCicle instanceof Pupa)
			emercion();
	}

	private void eclocion() {
		// Descuenta un Huevo y aumenta un Acuatico
		container.decreaseEggsAmount();
		container.increaseAquaticAmount();
		//
		lifeCicle.eliminate(agentID);
		lifeCicle = new Larva(0f, agentID, container);
	}

	private void pupacion() {
		// Se considera como Acuatico - no hace falta incrementar cantidad
		lifeCicle.eliminate(agentID);
		lifeCicle = new Pupa(0f, agentID);
	}

	private void emercion() {
		// Descuenta un Acuatico y crea un Mosquito
		container.decreaseAquaticAmount();
		container.emergeMosquito();
		//
		lifeCicle.eliminate(agentID);
		lifeCicle = null;
		--agentCount;
	}
	
	@ProbeID
	public String toString() {
		return agentIDString;
	}
}
