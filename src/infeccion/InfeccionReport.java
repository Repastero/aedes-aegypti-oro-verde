package infeccion;

import repast.simphony.engine.schedule.ScheduledMethod;

/**
 * La clase {@code InfecconReport} lleva la cuenta de picaduras de mosquitos e infecciones en Humanos.<p>
 * El objetivo de esta clase es puramente informativo y mas que nada para llevar la cuenta de los Humanos infectados que no siempre estan en el Contexto.
 */
public class InfeccionReport {
	/** Cantidad actual de Humanos infectados */
	public static int humanosInfectados = 0;
	/** Cantidad total de Humanos recuperados de infeccion */
	public static int humanosRecuperados = 0;
	/** Cantidad de nuevos casos de infeccion */
	public static int nuevosCasosInfectados = 0;
	
	/** Cantidad diaria de picaduras realizadas */
	public static int picadurasExitosas = 0;
	/** Cantidad diaria de picaduras no realizadas (por no encontrar Humanos) */
	public static int picadurasFrustradas = 0;
	
	/**
	 * Reinicio diariamente la cantidad de nuevos casos de Humanos infectados y las picaduras de Mosquitos.
	 */
	@ScheduledMethod(start = 0d, interval = 12d, priority = 0.99d) //ScheduleParameters.FIRST_PRIORITY
	public void inicializadorDiario() {
		InfeccionReport.nuevosCasosInfectados = 0;		
		InfeccionReport.picadurasExitosas = 0;
		InfeccionReport.picadurasFrustradas = 0;
	}
	
	public static int getPicadurasExitosas() { return picadurasExitosas; }
	public static int getPicadurasFrustradas() { return picadurasFrustradas; }
	
	public static int getNuevosCasos() { return nuevosCasosInfectados; }
	public static int getHumanosInfectados() { return humanosInfectados; }
	public static int getHumanosRecuperados() { return humanosRecuperados; }
}
