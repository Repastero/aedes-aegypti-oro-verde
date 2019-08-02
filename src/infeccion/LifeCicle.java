package infeccion;

public abstract class LifeCicle {
	/** Porcentaje de desarrollo de la fase actual. */
	private double life = 0f;
	
	/**
	 * Incrementa porcentaje de desarrollo, de acuerdo a fase actual.
	 */
	public    abstract void updateLife();
	
	/**
	 * Sumatoria de mortalidades.
	 * @return porcentaje de mortalidad
	 */
	public 	  abstract double updateMortalidadNeta();
	
	/**
	 * Mortalidad diaria, de acuerdo a fase actual.
	 * @return porcentaje de mortalidad
	 */
	protected abstract double mortalidadDiaria();
	
	/**
	 * Resta el Agente de la cantidad total.
	 * @param agentID ID del Agente
	 */
	public    abstract void eliminate(int agentID);
	//
	LifeCicle() { }
	
	LifeCicle(double value) {
		this.life = value;
	};
	
	/**
	 * @return Porcentaje de desarrollo de la fase actual.
	 */
	public double getLife() {
		return life;
	}
	
	/**
	 * Cambia el porcentaje de desarrollo de la fase actual.
	 * @param value nuevo porcentaje de desarrollo
	 */
	public void setLife(double value) {
		life = value;
	}
	
	/**
	 * Actualiza el porcentaje de desarrollo de la fase actual.
	 * @param value porcentaje de desarrollo a sumar
	 */
	public void updateLife(double value) {
		this.life += value;
	}
}
