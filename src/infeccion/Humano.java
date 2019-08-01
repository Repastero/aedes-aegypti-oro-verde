package infeccion;

import static repast.simphony.essentials.RepastEssentials.*;

import java.util.ArrayList;

import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.*;
import repast.simphony.parameter.*;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.*;
import repast.simphony.ui.probe.*;

public class Humano  {
	public int IPeriod = 60; // 60 ticks = 5 dias
	public boolean infected = false;	
	public GridPoint posfutura;
	public int viremia = 0;
	public boolean incubating = false;
	public boolean recuperado = false;
	private GridPoint posTrabajo = new GridPoint(0,0); // Posicion del trabajo
	private GridPoint posCasa = new GridPoint(0,0); // Posicion de la casa
	private int localizActual = 0; // Localizacion actual es el estado de markov donde esta. El nodo 0 es la casa, el 1 es el trabajo/estudio, el 2 es ocio, el 3 es otros (supermercados, farmacias, etc)
	private GridPoint posLocalizActual; // Posicion localizacion actual es la coordenada donde esta la localizacion actual.
	private static double velTraslado = 0; // La velocidad de traslado es la velocidad a la que se mueve de un nodo a otro.
    private int[][][] matrizTMMC; // La matriz TMMC es la matriz que define la probabilidad de traspaso entre los nodos de la maquina de markov.
    private ArrayList<String> contactados = new ArrayList<String>();
	private Grid<Object> grid;
	private boolean foreignResident = false;
    protected static int agentIDCounter = 0;
    protected String agentID = "Humano " + agentIDCounter++;
	
    @Parameter (displayName = "Periodo Intrinseco", usageName = "IPeriod")
    public int getIPeriod() { return IPeriod; }
    public void setIPeriod(int IPeriod) { this.IPeriod = IPeriod; }

    @Parameter (displayName = "Infectado", usageName = "infected")
    public boolean getInfected() { return infected; }
    public void setInfected(boolean infected) { this.infected = infected; }

    @Parameter (displayName = "Posicion futura", usageName = "posfutura")
    public GridPoint getPosfutura() { return posfutura; }
    public void setPosfutura(GridPoint posfutura) { this.posfutura = posfutura; }

    @Parameter (displayName = "Viremia", usageName = "viremia")
    public int getViremia() { return viremia; }
    public void setViremia(int viremia) { this.viremia = viremia; }

    @Parameter (displayName = "Recuperado", usageName = "recuperado")
    public boolean getRecuperado() { return recuperado; }
    public void setRecuperado(boolean recuperado) { this.recuperado = recuperado; }

    /**
	* MaxiF: Lista de agentes con los que tuvo contacto.
	* 			La idea de esto es para medir la red de contactos, no afecta el comportamiento.
	* 			Se agrega al arreglo por cada 1/32 tick todos los que tenga alrededor que no estan ya en el arreglo.
	*/
    public ArrayList<String> getContactados() { return contactados; }
    public void setContactados(ArrayList<String> contactados) { this.contactados = contactados; }


    public GridPoint getPosTrabajo() { return posTrabajo; }
    public void setPosTrabajo(GridPoint posTrabajo) { this.posTrabajo = posTrabajo; }

    public GridPoint getPosCasa() { return posCasa; }
    public void setPosCasa(GridPoint posCasa) { this.posCasa = posCasa; }

    public int getLocalizActual() { return localizActual; }
    public void setLocalizActual(int localizActual) { this.localizActual = localizActual; }

    public GridPoint getPosLocalizActual() { return posLocalizActual; }
    public void setPosLocalizActual(GridPoint posLocalizActual) { this.posLocalizActual = posLocalizActual; }

    public static double getVelTraslado() { return velTraslado; }
    public static void setVelTraslado(double velocidad) { velTraslado = velocidad; }

	public Humano(Grid<Object> grid, GridPoint posCasa, GridPoint posTrabajo, int[][][] matrizMarkov) {
		this.grid = grid;
		this.posCasa = posCasa;
		this.posTrabajo = posTrabajo;
		this.matrizTMMC = matrizMarkov;
		this.posLocalizActual = posCasa; // Inician en su casa
	}
	
	public void setLocalResident() {
		//Locales: X 3-5 6-8 9-11
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		ScheduleParameters params = ScheduleParameters.createRepeating(0d, 12d, ScheduleParameters.FIRST_PRIORITY);
		schedule.schedule(params , this , "removeFromContext");
		params = ScheduleParameters.createRepeating(3d, 12d, ScheduleParameters.FIRST_PRIORITY);
		schedule.schedule(params , this , "addToContext");
	}
	
	public void setForeignResident() {
		//Extranjeros: 0-2 X X X
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		ScheduleParameters params = ScheduleParameters.createRepeating(3d, 12d, ScheduleParameters.FIRST_PRIORITY);
		schedule.schedule(params , this , "removeFromContext");
		params = ScheduleParameters.createRepeating(0d, 12d, ScheduleParameters.FIRST_PRIORITY);
		schedule.schedule(params , this , "addToContext");
		foreignResident = true;
	}
	
	public void removeFromContext() {
		RemoveAgentFromContext("infeccion", this);
	}
	
	public void addToContext() {
		AddAgentToContext("infeccion", this);
		if (!foreignResident) {
			// Como el Humano residente vuelve de trabajar, su posicion anterior es trabajo
			// para el Humano extranjero no tiene importancia ya que la unica posicion que conoce es la de trabajo
			localizActual = 1; // Trabajo
			posLocalizActual = posCasa;
		}
		else {
			posLocalizActual = posTrabajo;
		}
		grid.moveTo(this, posLocalizActual.getX(), posLocalizActual.getY());
	}
	
	/**
	 * Si no ha sido infectado anteriormente, programa infectar el Humano una vez que termine el tiempo de incubacion (intrinsic incubation period).<p>
	 * The intrinsic incubation period is the time taken by an organism to complete its development in the definitive host.
	 */
	public void startCargaViral() {
		// Chequea si no esta incubando, infectado o recuperado
		if ((!recuperado) && (!incubating) && (!infected)) {
			incubating = true;
			// Schedule one shot para terminar el periodo de incubacion y setear humano como infectado
			ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
			ScheduleParameters scheduleParams = ScheduleParameters.createOneTime(GetTickCount() + IPeriod, ScheduleParameters.FIRST_PRIORITY);
			schedule.schedule(scheduleParams, this, "infectHost", true);
		}
	}
	
	/**
	 * Si no ha sido infectado anteriormente, termina de infectar el Humano y programa recuperarlo cuando termine el tiempo de contagio.
	 */
	public void infectHost(boolean reportar) {
		if (!recuperado) {
			incubating = false;
			infected = true;
			++InfeccionReport.humanosInfectados;
			if (reportar)
				++InfeccionReport.nuevosCasosInfectados;
			
			// Schedule one shot para terminar el periodo de contagio y setear humano como recuperado
			ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
			ScheduleParameters params = ScheduleParameters.createNormalProbabilityOneTime(GetTickCount() + 78.125d, 9d, ScheduleParameters.FIRST_PRIORITY);
			schedule.schedule(params, this, "setRecovered");
			//2500 * 0.03125 = 78.125 ticks = 6.5 dias
			//288 * 0.03125 = 9 ticks
		}
	}
	
	/**
	 * Finaliza el periodo de contagio del virus en el Humano.
	 */
	public void setRecovered() {
		if (infected) {
			infected = false;
			--InfeccionReport.humanosInfectados;
			recuperado = true;
			++InfeccionReport.humanosRecuperados;
		}
	}

    //Descomentar esto si se quiere calcular la red de contactos.
    //@ScheduledMethod(start = 0d, interval = 1d, priority = 0.99d, shuffle = true)
    public void calcularRed() {
        // MaxiF: En este comportamiento se actualiza la red de contactos. Se agregan
        //			todos los vecinos nuevos que encuentre.

        //MaxiF: Para obtener los contactos, primero arranque con una proyeccion tipo network, pero
        //		es tan grande la red que termina siendo re ineficiente.
        //		Entonces lo solucione con que cada agente tenga una lista de a quienes contacto
        MooreQuery<Object> query = new MooreQuery<Object>(grid, this, 1, 1);
        for (Object v : query.query()) {
            if (!(contactados.contains(v.toString())) && v.getClass() == this.getClass()) {
                contactados.add(v.toString());
            }
        }
    }

    public String obtenerContactos() {
        //MaxiF: Para obtener los contactos, primero arranque con una proyeccion tipo network, pero
        //		es tan grande la red que termina siendo re ineficiente.
        //		Entonces lo solucion con que cada agente tenga una lista de a quienes contacta

        //Este metodo, devuelve un string con un formato similar al que usa networkx. La idea es
        //		llamarlo al final y grabar la respuesta en un archivo de texto con un data sink.
        StringBuilder salida = new StringBuilder();
        for (String c : contactados) {
        	salida.append(c+";");
            //-salida += "${this} ${c};"
        }
        return salida.toString();
    }

    /**
    * Cambia la posicion en la grilla segun TMMC (Timed mobility markov chains).
    */
    @ScheduledMethod(start = 0d, interval = 1.0d, priority = 0.6d)
    public void cambiarPosicionMarkov() {
        // MaxiF: Este metodo cambia el lugar donde va a estar el humano en cada hora.
        //			Esto se hace con cadenas de mobilidad de markov temporales.

        // MaxiF: Para elegir si cambiar o no uso u algoritmo de seleccion ponderado.
        // MaxiF: La matriz tiene la forma 	probabilidadTMMC[P,i,j], donde P es el periodo del dia (manana, siesta, tarde, anochecer)
        //		i es el nodo de donde sale, y j es el nodo a donde va.
        // MaxiF: Averiguo el periodo en el que estoy
        final int p = ((int)GetTickCount() % 12) / 3;	// 0 1 2 3
        // MaxiF: Ahora selecciono el siguiente lugar, segun probabilidad
        int r = RandomHelper.nextIntFromTo(1, 1000);
        int i = 0;
        while (r > matrizTMMC[p][localizActual][i]) {
        	// La suma de las pobabilidades no debe dar mas de 1000
        	r -= matrizTMMC[p][localizActual][i];
        	++i;
        }
        // MaxiF: Ahora cambio la localizacion actual a la calculada recien si es que cambio
        if (localizActual != i) {
            localizActual = i;
            switch (localizActual) {
	        	case 0: // 0 Casa
	        		posLocalizActual = posCasa;
	        		break;
	        	case 1: // 1 Trabajo / Estudio
	        		posLocalizActual = posTrabajo;
	        		break;
	        	case 2: // 2 Ocio
	        		posLocalizActual = OroVerde.posOcio();
	        		break;
	        	default: // 3 Otros (supermercados, farmacias, etc)
	        		posLocalizActual = OroVerde.posOtros();
	        		break;
            }
            // Si esta funcion tira error, es porque alguna posicion de las tablas esta mal
            grid.moveTo(this, posLocalizActual.getX(), posLocalizActual.getY());
        }
    }
  
	@ProbeID
	public String toString() {
		return agentID;
	}
}
