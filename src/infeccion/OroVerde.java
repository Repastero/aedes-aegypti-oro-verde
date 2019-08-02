package infeccion;

import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.GridPoint;

/**
 * La clase {@code OroVerde} contiene el tamano de la grilla y los lugares de Ocio, Trabajo a los cuales pueden acceder los Humanos.<p>
 * Ademas implementa metodos para ubicar los Humanos en posiciones aleatorias como Casa y Otros lugares visitados con menor frecuencia.<p>
 * Contiene una tabla de probabilidades de TMMC (Timed mobility markov chains), para que el Humano pueda cambiar de posicion en la grilla segun los periodos del dia.
 */
public final class OroVerde {
	protected static final int[] TAMANIO = {200, 200};
	/**
	 * MaxiF: Configuracion del tamano de la ciudad - cada punto son 5 metros
	 */
	public static int[] getTamanio() { return TAMANIO;	}
	public static int getAncho() { return TAMANIO[0]; }
	public static int getAlto() { return TAMANIO[1]; }

	/**
	 * Maxi F:Lista de los lugares de ocio en Oro Verde
	 */
	private static final GridPoint[] lugaresOcio = {
			new GridPoint(181,10),
			new GridPoint( 10,59),
			new GridPoint( 56,18),
			new GridPoint( 89,62),
			new GridPoint( 16,76)
	};

	/**
	 * Maxi F:Lista de los lugares de trabajo/estudio en Oro Verde
	 */
	private static final GridPoint[] lugaresTrabajo = {
			new GridPoint( 52, 62),
			new GridPoint( 64, 93),
			new GridPoint(104,161),
			new GridPoint(186, 25),
			new GridPoint(190, 43),
			new GridPoint( 24,134),
			new GridPoint(137,153),
			new GridPoint( 80,179),
			new GridPoint(102,174),
			new GridPoint( 44,154)
	};

	/**
	 * Elije aleatoriamente una posicion de trabajo de la lista, y le suma una distribucion normal.
	 * @return posicion del trabajo
	 */
	public static GridPoint posTrabajo() {
		int randomID = RandomHelper.nextIntFromTo(0, lugaresTrabajo.length-1);
		GridPoint pos = lugaresTrabajo[randomID];
		pos = new GridPoint(pos.getX() + (int)RandomHelper.getNormal().nextDouble(0,2), pos.getY() + (int)RandomHelper.getNormal().nextDouble(0,2));
		return pos;
	}

	/**
	 * Elije aleatoriamente una posicion en la grilla.<p>
	 * <i>Actualmete la casa es un lugar aleatorio seleccionado uniformemete de todo el grid, pero queda implementado este metodo para cambiarlo a otras formas si es necesario.</i>
	 * @return posicion del hogar
	 */
	public static GridPoint posCasa() {
		return new GridPoint(RandomHelper.nextIntFromTo(0, TAMANIO[0]-1), RandomHelper.nextIntFromTo(0, TAMANIO[1]-1));
	}

	/**
	 * Elije aleatoriamente una posicion de ocio de la lista, y le suma una distribucion normal.
	 * @return posicion de lugar de ocio
	 */
	public static GridPoint posOcio() {
		GridPoint pos = lugaresOcio[RandomHelper.nextIntFromTo(0, lugaresOcio.length-1)];
		pos = new GridPoint(pos.getX() + (int)RandomHelper.getNormal().nextDouble(0,2), pos.getY() + (int)RandomHelper.getNormal().nextDouble(0,2));
		return pos;
	}

	/**
	 * Elije aleatoriamente una posicion de otros lugares visitables, como familiares o amigos, supermercados, farmacias, etc.<p>
	 * Lugares que son menos probables que se visiten, y se se hacen, es por poco tiempo.<p>
	 * <i>Actualmete los otros lugares son seleccionados aleatorio uniformemete de todo el grid, pero queda implementado este metodo para cambiarlo a otras formas si es necesario.</i>
	 * @return posicion de otros lugares
	 */
	public static GridPoint posOtros() {
		return new GridPoint(RandomHelper.nextIntFromTo(0, TAMANIO[0]-1), RandomHelper.nextIntFromTo(0, TAMANIO[1]-1));
	}

	/**
	 * MaxiF: Devuelve la velocidad de traslado segun un distribucion de probabilidad propia de oro verde.<p>
	 * <i>MaxiF: La velocidad de traslado te marca la velocidad en unidades/(1/32)ticks, donde cada unidad son 5 metros, y un tick es una hora. Ej: La marcha humana es de 6km/Hora, entonces son aprox 38 unidades/tick.</i>
	 * @return velocidad de traslado del Humano
	 * @deprecated  Dado que el Aedes no pica en lugares al exterior, se decidio remover la implementacion del traslado de Humanos.
	 */
	public static double velocTraslado() {
		return 38d;
	}

	/**
	 * Matriz de 4x4x4 - Probabilidades sobre 1000.<p>
	 * <i>MaxiF: La probabilidad de la cadena de markov de movimiento temporal es un arreglo que:
	 * probabilidadTMMC[P,i,j], donde P es el periodo del dia (8-11 11-14 14-17 17-20hs)
	 * i es el nodo de donde sale, y j es el nodo a donde va.<p>
	 * El nodo 0 es la casa, el 1 es el trabajo/estudio, el 2 es ocio, el 3 es otros (supermercados, farmacias, etc)
	 * Ej: probabilidadTMMC[1][1][2] es la probabilidad de salir del trabajo al lugar de ocio en el periodo 1 (siesta)</i>
	 */
	private static final int probabilidadTMMC[][][] = {
			{ {100,700,100,100},{ 25,925, 25, 25},{100,700,100,100},{100,700,100,100} },
			{ {925, 25, 25, 25},{800,  0,100,100},{800,  0,100,100},{800,  0,100,100} },
			{ { 25,925, 25, 25},{ 25,925, 25, 25},{ 25,925, 25, 25},{ 25,925, 25, 25} },
			{ {925, 25, 25, 25},{ 50, 50,450,450},{700,  0,300,  0},{700,  0,  0,300} }
	};
	
	/** Matriz modificada para los Humanos que trabajan afuera o viven afuera. */
	private static final int probabilidadTMMCSC[][][] = {
			{ {  0,1000,  0,  0},{  0,1000,  0,  0},{  0,1000,  0,  0},{  0,1000,  0,  0} },
			{ {950,   0, 25, 25},{800,   0,100,100},{800,   0,100,100},{800,   0,100,100} },
			{ {300,   0,350,350},{300,   0,350,350},{300,   0,350,350},{300,   0,350,350} },
			{ {950,   0, 25, 25},{100,   0,450,450},{700,   0,300,  0},{700,   0,  0,300} }
	};

	/**
	 * @see #probabilidadTMMC
	 * @return matriz de TTMC
	 */
	public static int[][][] matrizTMMC(){
		return probabilidadTMMC;
	}
	
	public static int[][][] matrizTMMCSC(){
		return probabilidadTMMCSC;
	}
}
