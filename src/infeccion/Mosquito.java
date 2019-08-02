
package infeccion;

import static repast.simphony.essentials.RepastEssentials.*;

import java.util.ArrayList;
import java.util.List;

import cern.jet.random.Normal;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.*;
import repast.simphony.parameter.*;
import repast.simphony.query.space.grid.GridCell;
import repast.simphony.query.space.grid.GridCellNgh;
import repast.simphony.query.space.grid.MooreQuery;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.*;
import repast.simphony.ui.probe.*;
import repast.simphony.util.SimUtilities;

public class Mosquito extends LifeCicle {
	private static int agentIDCounter = 0;
	private static int agentCount = 0;
	//-private int agentID = agentIDCounter;
	private String agentIDString = "Mosquito " + agentIDCounter++;
	private Grid<Object> grid;

	public boolean	infected = false;
	public int[]	posicionFutura = new int[2];
	public boolean	cargaviral = false;
	private double	vida = 0;
	private double	vidaMedia = 0;
	private GridPoint posicionInicial = new GridPoint(0, 0);
	private Container container;

	public long contadorInactividad = 0; // MaxiC: contadorinactividad es una variable que registra la cantidad de veces que estuvo inactivo el aedes
	public int tinactivo = 0; // MaxiC: tinactivo es una variable que indica el tiempo que el aedes esta inactivo. Divir por 12 para obtener el tiempo en ticks
	public boolean inactivo = false;
	public double distancia = 0;
	public boolean buscar = true;
	public int picoExito = 0;
	public int picoFrustrado = 0;
	public int nacio = 1;
	public int contadorPicar;
	// MaxiC: cantidadPicar (setea la cantidad de veces que va a picar al comenzar su vida)
	private int cantidadPicar = 0;
	private static int cantidadPicarMedia;

	private int ovoposicionNumero = 0;
	private int diasBusquedaContenedor = 0;
	
	private boolean changeGTPhase = true;
	private boolean feedingPhase = true;	// Primera fase ciclo gonotrofico
	private boolean digestingPhase = false;	// Segunda fase ciclo gonotrofico
	private boolean breedingPhase = false;	// Tercera fase ciclo gonotrofico

	@Parameter (displayName = "Infected", usageName = "infected")
	public boolean	getInfected() { return infected; }

	@Parameter (displayName = "Posicion futura", usageName = "posicionfutura")
	public int[] getPosicionfutura() { return posicionFutura; }

	@Parameter (displayName = "Carga viral", usageName = "cargaviral")
	public boolean getCargaviral() { return cargaviral; }

	@Parameter (displayName = "Vida", usageName = "vida")
	public double getVida() { return vida; }

	@Parameter (displayName = "Posicion incial", usageName = "posicioninicial")
	private GridPoint getPosicioninicial() { return posicionInicial; }

	public long getContadorInactividad() { return contadorInactividad; }

	/** MaxiF: Distancia es una variable para determinar cuan lejos estan del lugar de nacimiento */
	public double getDistancia() { return distancia; }
	
	public static int getCount() {
		return agentCount;
	}

	public Mosquito(Grid<Object> grid, int posX, int posY) {
		++agentCount;
		this.grid = grid;
		this.posicionInicial = new GridPoint(posX, posY);
		inicializarVida();
		inicializarPicaduras();
	}
	
	public static void initAgentID() {
		agentIDCounter = 0;
		agentCount = 0;
	}
	
	/**
	 * Lee el parametro de Cantidad de Picaduras seteado en Repast Symphony.
	 */
	public static void initBitesMean() {
		Parameters params = RunEnvironment.getInstance().getParameters();
		cantidadPicarMedia = (Integer) params.getValue("cantPicaduras");
		if (cantidadPicarMedia < 1)
			cantidadPicarMedia = 1;
	}
	
	/**
	 * Se asignal un tiempo de vida de 13 dias, con un desvio de 2 dias.
	 */
	private void inicializarVida() {
		Normal norm = RandomHelper.createNormal(13d, 2d);
		vidaMedia = norm.nextInt();
		vida = vidaMedia;
	}
	
	/**
	 * Asigna la cantidad de picaduras que debe realizar el Mosquito antes de comenzar la embrionacion.<p>
	 * <i>Nota: El valor asignado es {@link #cantidadPicarMedia} mas un desvio estandard de uno.</i>
	 * @see #initBitesMean()
	 */
	private void inicializarPicaduras() {
		int vecesPicar;
		Normal norm = RandomHelper.createNormal(cantidadPicarMedia, 1d);
		do {
			vecesPicar = norm.nextInt();
		} while (vecesPicar <= 0); //MaxiC: controlo que no sea negativo o cero
		cantidadPicar = vecesPicar;
		contadorPicar = cantidadPicar; // Iniciar cantidad de picaduras
	}
	
	/**
	 * Actualiza la probabilidad de muerte, resta 1 dia de vida y continua el ciclo Gonotrofico (si corresponde).<p>
	 * Three phases of one gonotrophic cycle:<p>
	 * <ul>
	 * <li> The searching for a host and the obtaining of the blood-meal. {@link #firstGTPhase()}
	 * <li> Digestion of the blood and egg formation. {@link #secondGTPhase()}
	 * <li> The search for breeding places and ovipositions. {@link #thirdGTPhase()}
	 * </ul>
	 */
	@ScheduledMethod(start = 0d, interval = 12d, priority = 0.91d)
	@Override
	public void updateLife() {
		final double random = RandomHelper.nextDoubleFromTo(0,1);
		vida -= (vidaMedia / Clima.getDDAdulto());
		if ((vida <= 0d) || (random < updateMortalidadNeta())) {
			eliminate(agentIDCounter);
		}
		
		// Si esta en la fase de ovoposicion y no ha finalizado
		if ((breedingPhase) && (!changeGTPhase)) {
			// Si pasa el tiempo maximo y no encuentra donde ovopositar
			if (++diasBusquedaContenedor >= DataSet.ADULTO_TIEMPO_BUSQUEDA_CONTENEDOR) {
				// Se fuerza a cambiar de fase
				changeGTPhase = true;
			}
		}
		
		// Si hace falta cambiar de etapa
		if (changeGTPhase) {
			if (feedingPhase) {
				feedingPhase = false;
				++contadorInactividad;
				ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
				// Tiempo de embrionacion 24 ticks = 2 dias (Otero 2006)
				ScheduleParameters params = ScheduleParameters.createOneTime(GetTickCount() + DataSet.ADULTO_DIAS_DE_GESTACION, ScheduleParameters.FIRST_PRIORITY);
				schedule.schedule(params, this, "secondGTPhase");
				digestingPhase = true;
			}
			else if (digestingPhase) {
				digestingPhase = false;
				breedingPhase = true;
			}
			else if (breedingPhase) {
				breedingPhase = false;
				// Vuelve a iniciar el ciclo
				if (ovoposicionNumero < DataSet.cantidadOvoposiciones) {
					contadorPicar = cantidadPicar; // Reiniciar cantidad de picaduras
					diasBusquedaContenedor = 0; // Reinicia el tiempo de busqueda para ovopositar
					feedingPhase = true;
				}
				// Si ya cumplio con la cantidad de ovoposiciones - no reinicia el ciclo
			}
			changeGTPhase = false;
		}
	}

	/**
	 * Cada 1 tick elije aleatoriamente una probabilidad y la compara<p>
	 * con el Ciclo Circardiano, para saber si le toca buscar Humanos para picar.<p>
	 * Una vez cumplida la cantidad de picaduras, pasa a la proxima fase.
	 */
	@ScheduledMethod(start = 0d, interval = 1d, priority = 0.80d)
	public void firstGTPhase() {
		if ((!feedingPhase) || (changeGTPhase))
			// Esta en otra etapa o esperando que termine el dia
			return;
		
		final int cicloCircadianoIndex = (int)GetTickCount() % 12; // 0 - 11
		final double udato = RandomHelper.nextDoubleFromTo(0, 1);
		// MaxiC: la funcion de probabilidad de actividad esta definida en un rango de 12 horas o ticks
		// MaxiC: distribucion de probabilidad de actividad calculda por IvanG
		// Ema: Ivan G se basa en el paper: Jones, M. D. R. (1981). The programming of circadian flight activity in relation to mating and the gonotrophic cycle in the mosquito
		if (udato < DataSet.CICLO_CIRCADIANO[cicloCircadianoIndex]) {
			findHumans();
			moveToNewLocation();
			biteHumans();
			
			if (contadorPicar <= 0)
				changeGTPhase = true;
		}
	}

	/**
	 * Busca Humanos en una vecinda de Moore de 1, desde la posicion del Mosquito.<p>
	 * Se crean 2 listas con las direcciones X e Y en la que se encuentran los Humanos.<p>
	 * De las listas se selecciona una direccion al azar. Si no se encuentran, toma una direccion aleatoria.<p> 
	 * El radio maximo de vuelo es < {@link DataSet#ADULTO_RADIO_VUELO} de la posicion de nacimiento.
	 */
	 /*  4    3     2
	  *  5    x(9)  1
	  *  6    7     8
	  *  Corresponde a ->
	  *  -X+Y   +Y   +X+Y
	  *  -X     =    +X
	  *  -X-Y   -Y   +X-Y
	  */
	public void findHumans() {
		GridPoint pt = grid.getLocation(this);
		GridCellNgh<Humano> nghCreator = new GridCellNgh<Humano>(grid, pt, Humano.class, 1, 1);
		List<GridCell<Humano>> gridCells = nghCreator.getNeighborhood(true);
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform());

		ArrayList<Integer> newPosX = new ArrayList<Integer>();
		ArrayList<Integer> newPosY = new ArrayList<Integer>();

		// MaxiC: separo en 9 zonas de interes para el movimiento.
		for (GridCell<Humano> cell : gridCells) {
			if (cell.size() != 0) {
				if (cell.getPoint().getX() == pt.getX()) {
					newPosX.add(0);
					if (cell.getPoint().getY() == pt.getY()) // 9
						newPosY.add(0);
					else if (cell.getPoint().getY() > pt.getY()) // 3
						newPosY.add(+1);
					else // cell y < inicial y // 7
						newPosY.add(-1);
				}
				else if (cell.getPoint().getX() > pt.getX()) {
					newPosX.add(+1);
					if (cell.getPoint().getY() == pt.getY()) // 1
						newPosY.add(0);
					else if (cell.getPoint().getY() > pt.getY()) // 2
						newPosY.add(+1);
					else // cell y < inicial y // 8
						newPosY.add(-1);
				}
				else { // cell x < inicial x
					newPosX.add(-1);
					if (cell.getPoint().getY() == pt.getY()) // 5
						newPosY.add(0);
					else if (cell.getPoint().getY() > pt.getY()) // 4
						newPosY.add(+1);
					else // cell y < inicial y // 6
						newPosY.add(-1);
				}
			}
		}
		if (newPosX.size() != 0) { // se encontraron humanos - seleccionar random
			int index = RandomHelper.nextIntFromTo(0, newPosX.size() - 1);
			posicionFutura[0] = pt.getX() + newPosX.get(index);
			posicionFutura[1] = pt.getY() + newPosY.get(index);
		}
		else { // no se encontraron humanos - mover aleatoriamente
			posicionFutura[0] = pt.getX() + RandomHelper.nextIntFromTo(-1, 1);
			posicionFutura[1] = pt.getY() + RandomHelper.nextIntFromTo(-1, 1);
		}
		
		// Se fija si el Mosquito no va a salir del radio de 7 espacios en grilla
		final int difX = Math.abs(posicionInicial.getX() - posicionFutura[0]);
		final int difY = Math.abs(posicionInicial.getY() - posicionFutura[1]);
		final double radio = Math.sqrt((difX * difX) + (difY * difY));
		if (radio >= DataSet.ADULTO_RADIO_VUELO) {
			// Si se pasa del radio, se mueve en la direccion contraria
			if (difX > difY) {
				if (posicionFutura[0] < posicionInicial.getX())
					posicionFutura[0] += 1;
				else
					posicionFutura[0] -= 1;
			}
			else {
				if (posicionFutura[1] < posicionInicial.getY())
					posicionFutura[1] += 1;
				else
					posicionFutura[1] -= 1;
			}
		}
	}

	/**
	 * Comprueba que la nueva posicion no se salga de los margenes y cambia la posicion del agente a posicionFutura.
	 */
	public void moveToNewLocation() {
		int x = posicionFutura[0];
		int y = posicionFutura[1];
		//
		if (x >= OroVerde.TAMANIO[0])
			x = OroVerde.TAMANIO[0]-1;
		else if (x < 1)
			x = 1;
		if (y >= OroVerde.TAMANIO[1])
			y = OroVerde.TAMANIO[1]-1;
		else if (y < 1)
			y = 1;
		//
		posicionFutura[0] = x;
		posicionFutura[1] = y;
		grid.moveTo(this, posicionFutura[0], posicionFutura[1]);
		distancia = grid.getDistance(posicionInicial, new GridPoint(posicionFutura));
	}

	/**
	 * Busca en su cercania los agentes Humanos, arma un listado y selecciona uno al azar para alimentarse.<p>
	 * Si el Mosquito esta infectado, inicia el periodo de incubacion en Humano.<p> 
	 * Si el Humano esta infectado, inicia el periodo de incubacion en Mosqito.<p>
	 * Resta la cantidad de picaduras necesarias para iniciar la siguiente fase.
	 */
	public void biteHumans() {
		List<Humano> humanAgents = new ArrayList<Humano>();
		MooreQuery<Humano> query = new MooreQuery(grid, this, 1, 1); //MaxiC: busca agentes con un radio de Moore de 1
		// MaxiC: cargo todos los humanos en el vecindario de Moore, excepto el centro
		for (Object obj : query.query()) {
			if (obj instanceof Humano) {
				humanAgents.add((Humano) obj);
			}
		}

		//MaxiC: cargo el centro
		GridPoint pt = grid.getLocation(this);
		Object obj = grid.getObjectAt(pt.getX(), pt.getY());
		if (obj instanceof Humano) {
			humanAgents.add((Humano) obj);
		}

		if (humanAgents.size() != 0) {
			++InfeccionReport.picadurasExitosas; //MaxiF: PicoExito es una variable para poder visualizar la cantidad de picuaduras exitosas que se dieron
			//MaxiF: Picar es una variable para que el mosquito recuerde que tiene que picar y siga intentandolo hasta poder lograrlo.
			// 		Como lo logra, se setea a 0.
			int indexhumano = RandomHelper.nextIntFromTo(0, humanAgents.size()-1);

			if (infected) {
				humanAgents.get(indexhumano).startCargaViral();
			}
			else if (humanAgents.get(indexhumano).getInfected()) {
				startEIPeriod();
			}
			// Resta la cantidad de picaduras disponibles
			--contadorPicar;
		}
		else {
			++InfeccionReport.picadurasFrustradas; //MaxiF: picoFrustrado es una variable para poder visualizar la cantidad de picuaduras frustradas por no encontrar humanos se dieron
			//MaxiF: Picar es una variable para que el mosquito recuerde que tiene que picar y siga intentandolo hasta poder lograrlo.
			// 		Como no pudo picar a nadie por no encontrarlo, sigue buscando.
		}
	}
	
	/**
	 * Programa infectar el Mosquito una vez que termine el tiempo de incubacion (extrinsic incubation period).<p>
	 * The extrinsic incubation period is the time taken by an organism to complete its development in the intermediate host.
	 */
	private void startEIPeriod() {
		cargaviral = true;
		// Ema: modificada de Ecuacion estudiada por Helmersson, 2012.Mathematical Modeling of Dengue-Temperature Effect on Vectorial Capacity. UniversitetUMEA.
		final double eIPeriod = (int)((4 + (int)Math.exp(4-0.123 * Clima.getCurrentTemperature()))) * 12;	// dias * 12 ticks
		// Schedule one shot para terminar el periodo de incubacion y setear mosquito como infectado
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		ScheduleParameters params = ScheduleParameters.createOneTime(GetTickCount() + eIPeriod, ScheduleParameters.FIRST_PRIORITY);
		schedule.schedule(params , this , "setInfected");
	}
	
	/**
	 * Metodo invocado por SchedulableAction creada en: {@link #startEIPeriod()}
	 */
	public void setInfected() {
		if (cargaviral) { // Chequeo por las dudas 
			infected = true;
		}
	}
	
	/**
	 * Infectar sin periodo de incubacion.
	 */
	public void infectTransmitter() {
		cargaviral = true;
		infected = true;
	}

	/**
	 * Indica el final del tiempo de digestion (gestacion o embrionacion).<p>
	 * Metodo invocado por SchedulableAction creada en: {@link #updateLife()}
	 */
	public void secondGTPhase() {
		// Queda esperando que termine el dia
		changeGTPhase = true;
	}
	
	/**
	 * Busca contenedor con agua y capacidad de acarreo.<p>
	 * Una vez encontrado, deposita los Huevos, incrementa el numero de ovoposiciones y cambia de fase.
	 */
	@ScheduledMethod(start = 0d, interval = 0.03125d, priority = 0.67d)
	public void thirdGTPhase() {
		if ((!breedingPhase) || (changeGTPhase))
			// Esta en otra etapa o esperando que termine el dia
			return;
		findWetContainer();
		if (container != null) {
			ovopositar();
			// Suma cantidad de ovoposiciones solo si ovoposiciona
			++ovoposicionNumero;
			changeGTPhase = true;
		}
	}

	/**
	 * Busca contenedores en la vecindad (radio 1 celda), comprueba que tengan agua y capacidad de acarreo, y selecciona uno.<p>
	 * De no ser posible, selecciona alguno en un radio < {@link DataSet#ADULTO_RADIO_VUELO} de la posicion de nacimiento.<p>
	 * Se mueve a la nueva posicion del container encontrado, o al que selecciono al azar.
	 */
	private void findWetContainer() {
		GridPoint pt = grid.getLocation(this);
		GridCellNgh<Container> nghCreator = new GridCellNgh<Container>(grid, pt, Container.class, 1, 1); //creo un "Creador de vecindad" en el el punto "pt" buscando los objetos agentes Container en una extencion de x +- 1, y +- 1
		List<GridCell<Container>> gridCells = nghCreator.getNeighborhood(true); //tomo una lista del barrio de los objetos agentes con el centro incluido (9 grid cells)
		SimUtilities.shuffle(gridCells, RandomHelper.getUniform()); //las mesclo!
		int maxCount = 0; // probar con menos -1
		GridCell<Container> cellRandom = null;
		boolean containerFound = false;
		container = null;
		for (GridCell<Container> cell : gridCells) {
			if (cell.size() > maxCount && container == null) {
				GridPoint p = cell.getPoint();
				for (Object obj : grid.getObjectsAt(p.getX(), p.getY())) {
					if (obj instanceof Container) {
						Container containerF = (Container) obj;
						if (containerF.containsWater()) {
							if (containerF.getAquaticAmount() < containerF.getCarryingCapacityEggs()) {
								container = containerF;
								break;
							}
						}
					}
				}
				maxCount = cell.size();
			}
			if (container == null) {
				// Si no encuentra un Container (y que tenga agua/capacidad) selecciona alguno en un rango de 7.
				final double radio = grid.getDistance(posicionInicial, cell.getPoint());
				if (radio < DataSet.ADULTO_RADIO_VUELO) {
					cellRandom = cell;
					break;
				}
			}
		}
		
		final int[] newPosition = {0, 0};
		if (cellRandom != null) {
			GridPoint randomPos = cellRandom.getPoint();
			newPosition[0] = randomPos.getX();
			newPosition[1] = randomPos.getY();
			containerFound = true;
		}
		else if (container != null) {
			newPosition[0] = container.getPositionX();
			newPosition[1] = container.getPositionY();
			containerFound = true;
		}
		if (containerFound) {
			posicionFutura = newPosition;
			grid.moveTo(this, newPosition[0], newPosition[1]);
		}
	}
	
	/**
	 * Agrega la cantidad posible de huevos en el contenedor, sin pasarse del maximo.
	 * @see DataSet.ADULTO_HUEVOS_OVOPOSICION
	 */
	private void ovopositar() {
		// El limite de acuaticos esta puesto para que controlar el crecimiento de mosquitos
		final boolean ovoposicionHabilitada = Acuatico.oviposicionHabilitadaByCarryingCapacity();
		if (ovoposicionHabilitada) {
			if (container != null) {
				for (int i = 0; i < DataSet.ADULTO_HUEVOS_OVOPOSICION; i++) {
					if (!container.addAquatic(new Acuatico(container, 0, 0d))) {
						break;
					}
				}
			}
		}
	}
	
	@Override
	protected double mortalidadDiaria() {
		return DataSet.mortalidadDiariaAdultos;
	}

	@Override
	public double updateMortalidadNeta() {
		return mortalidadDiaria();
	}

	@Override
	public void eliminate(int agentID) {
		RemoveAgentFromContext("infeccion", this);
		--agentCount;
	}

	@ProbeID
	public String toString() {
		return agentIDString;
	}
}
