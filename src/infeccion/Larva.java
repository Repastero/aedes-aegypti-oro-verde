package infeccion;

import repast.simphony.ui.probe.*;

public class Larva extends LifeCicle {
	private static int agentIDCounter = 0;
	public  static void setCantidad(int val) { agentIDCounter = val; }
	public  static int getCantidad() { return agentIDCounter; }

	private Container container; // para saber cantidad y capacidad de acuaticos
	
	Larva() { } //constructor vacio para generar archivo con cantidades por dia
	Larva(double life, Integer agentID, Container container) {
		super(life);
		Larva.agentIDCounter++;
		this.container = container;
	}

	@Override
	public void updateLife() {
		double desMeta = Clima.getDMDLarva();
		super.updateLife(desMeta);
	}

	@Override
	protected double mortalidadDiaria() {
		return Clima.getMDAcuatico();
	}

	@Override
	public double updateMortalidadNeta() {
		double mortalidad;
		if (!mortalidadCapacidadAcarreo())
			mortalidad = mortalidadDiaria();
		else
			mortalidad = 1d;
		return mortalidad;
	}

	/**
	 * Chequea si hay capacidad en el contenedor para que la Larva se alimente y desarrolle.
	 * @return <code>true</code> si se debe aplicar la mortalidad por limite de capacidad
	 */
	private boolean mortalidadCapacidadAcarreo() {
		final int acuaticAmountInContainer = container.getAquaticAmount(); // obtengo cuantos Aedes acuaticos tiene ese contenedor
		final int carryingCapacity = container.getCarryingCapacity(); // obtengo la capacidad de acarreo que tiene ese contenedor
		return (acuaticAmountInContainer > carryingCapacity); // si hay menos de los permitido,
	}

	@Override
	public void eliminate(int agentID) {
		Larva.agentIDCounter--;
	}

	@ProbeID
	public String toString() {
		return String.valueOf(Larva.agentIDCounter);
	}
}
