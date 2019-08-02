package infeccion;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.stream.IntStream;

import cern.jet.random.Normal;
import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.engine.schedule.ISchedule;
import repast.simphony.engine.schedule.ScheduleParameters;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.StrictBorders;

public class InfeccionBuilder implements ContextBuilder<Object> {

	private Integer cantHumanosInfectados;
	private Integer cantMosquitosInfectados;
	private Integer tiempoEntradaCaso;
	private Context<Object> context;
	private long simulationStartTime;
	
	@Override
	public Context<Object> build(Context<Object> context) {
		simulationStartTime = System.currentTimeMillis();
		context.setId("infeccion");
		
		//MaxiF: Configuro para que termine la simulacion en el tick 4300
		RunEnvironment.getInstance().endAt(4300);
		
		ISchedule schedule = RunEnvironment.getInstance().getCurrentSchedule();
		ScheduleParameters stopParams = ScheduleParameters.createAtEnd(ScheduleParameters.LAST_PRIORITY);
		schedule.schedule(stopParams, this, "printSimulationDuration");
		
		//MaxiF: Creo una proyeccion grid de largo y ancho segun esta en la ciudad.
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new StrictBorders(),
						new RandomGridAdder<Object>(), true, OroVerde.getAncho(), OroVerde.getAlto()));
		
		setBachParameters();
		
		// Schedule one shot para agregar infectados
		ScheduleParameters params = ScheduleParameters.createOneTime(tiempoEntradaCaso, ScheduleParameters.FIRST_PRIORITY);
		schedule.schedule(params , this , "infectRandos");
		
		inicializarClima(context);
		inicializarWaterSites(context);
		inicializarContainers(context, grid);
		generateAedesAcuaticos(context, grid);
		generateAedesAdultos(context, grid);
		inicializarHumanos(context, grid);
		
		this.context = context;
		return context;
	}
	
	public void printSimulationDuration() {
       final long simTime = System.currentTimeMillis() - simulationStartTime;
       System.out.println("Tiempo simulacion: " + (simTime / (double)(1000*60)) + " minutos");
	}
	
	/**
	 * Selecciona al azar la cantidad de Humanos y Mosquitos seteada en los parametros y los infecta.
	 */
	public void infectRandos() {
		Iterable<Object> collection = context.getRandomObjects(Humano.class, cantHumanosInfectados);
		for (Iterator<Object> iterator = collection.iterator(); iterator.hasNext();) {
			Humano humano = (Humano) iterator.next();
			humano.infectHost(false);
		}
		
		collection = context.getRandomObjects(Mosquito.class, cantMosquitosInfectados);
		for (Iterator<Object> iterator = collection.iterator(); iterator.hasNext();) {
			Mosquito mosquito = (Mosquito) iterator.next();
			mosquito.infectTransmitter();
		}
	}

	private void setBachParameters() {
		Parameters params = RunEnvironment.getInstance().getParameters();
		cantHumanosInfectados				= (Integer) params.getValue("cantidadHumanosInfectados");
		cantMosquitosInfectados				= (Integer) params.getValue("cantidadMosquitosInfectados");
		tiempoEntradaCaso					= (Integer) params.getValue("tiempoEntradaCaso");
		DataSet.acarreoNetaAcuaticos		= (Integer) params.getValue("capAcarreoNetaAcuaticos"); // maximo de mosquitos en estado acuatico
		DataSet.cantidadOvoposiciones		= (Integer) params.getValue("cantOvoposiciones");
		DataSet.cantidadAcuaticosIniciales	= (Integer) params.getValue("cantHuevosIniciales");
		DataSet.cantidadHumanosIniciales	= (Integer) params.getValue("cantHumanos");
		
		DataSet.cantHumanosExtranjeros		= (Integer) params.getValue("cantHumanosExtranjeros");
		DataSet.cantHumanosLocales			= (Integer) params.getValue("cantHumanosLocales");
		
		DataSet.capacidadAcarreoPorLitro	= (Integer) params.getValue("capHuevosPorLitro");
	}

	private void inicializarClima(Context<Object> context) {
		// No hace falta agregarlo al contexto (se actualiza en WaterSites)
		context.add(new Clima()); // Lo agrego para que salga la grafica en Repast Simphony
	}

	private void inicializarWaterSites(Context<Object> context) {
		context.add(new WaterSites());
	}
	
	/**
	 * Crea Containers en la grilla en posiciones aleatorias (mitad de grilla + desviacion estandar);
	 * y selecciona aleatoriamene si son intradomiciliario.
	 * @param context  Contexto de la simulacion
	 * @param grid  Grilla de la simulacion
	 * @see DataSet#WATERSITES_CANTIDAD_CONTENEDORES
	 * @see DataSet#CONTAINER_PORCENTAJE_INTERIORES
	 */
	private void inicializarContainers(Context<Object> context, Grid<Object> grid) {
		Container.initAgentID(); // Reiniciar ID de contenedores
		final int anchoGrilla = OroVerde.getAncho() - 1;
		final int altoGrilla  = OroVerde.getAlto() - 1;
		final double valorMedioAncho = anchoGrilla / 2;
		final double valorMedioAnchoSTD = valorMedioAncho * 0.7;//0.6
		final double valorMedioAlto = anchoGrilla / 2;
		final double valorMedioAltoSTD = valorMedioAncho * 0.7;//0.6
		Normal rndAncho = RandomHelper.createNormal(valorMedioAncho, valorMedioAnchoSTD);
		Normal rndAlto = RandomHelper.createNormal(valorMedioAlto, valorMedioAltoSTD);
		
		boolean inside;
		double area, height;
		int posX, posY;
		for (int i = 0; i < DataSet.WATERSITES_CANTIDAD_CONTENEDORES; i++) {
			inside = (RandomHelper.nextIntFromTo(1, 100) <= DataSet.CONTAINER_PORCENTAJE_INTERIORES);
			area = randomizarDimension(DataSet.CONTAINER_VALOR_MEDIO_ALTURA, DataSet.CONTAINER_VALOR_MEDIO_ALTURA_STANDARD);
			height = randomizarDimension(DataSet.CONTAINER_VALOR_MEDIO_AREA, DataSet.CONTAINER_VALOR_MEDIO_AREA_STANDARD);
			if (inside) {
				do {
					posX = rndAncho.nextInt();
				} while (posX < 1 || posX > anchoGrilla);
				do {
					posY = rndAlto.nextInt();
				} while (posY < 1 || posY > altoGrilla);
			}
			else {
				posX = RandomHelper.nextIntFromTo(1, anchoGrilla);
				posY = RandomHelper.nextIntFromTo(1, altoGrilla);
			}
			Container container = new Container(grid, inside, area, height, posX, posY);
			if (inside)
				WaterSites.addContainerInside(container);
			context.add(container);
			grid.moveTo(container, posX, posY);
		}
	}

	/**
	 * Crea Mosquitos adultos y posiciona uno en cada Container intradomiciliario y
	 * el resto se distribuye aleatoriamente en la grilla.  
	 * @param context  Contexto de la simulacion
	 * @param grid  Grilla de la simulacion
	 * @see DataSet#INFECCIONBUILDER_MOSQUITOS_INICIALES
	 */
	private void generateAedesAdultos(Context<Object> context, Grid<Object> grid) {
		Mosquito.initAgentID(); // Reiniciar ID de mosquitos
		Mosquito.initBitesMean(); // Leer el valor ingresado de cantidad picaduras
		context.add(new InfeccionReport()); // Unicamente para la grafica en Repast Simphony
		final int anchoGrilla = OroVerde.getAncho() - 1;
		final int altoGrilla  = OroVerde.getAlto() - 1;
		final ArrayList<Container> containersInside = WaterSites.getContainerInsideList();
		int[] ciIndexes = IntStream.range(0, containersInside.size()).toArray();
		int indexesCount = containersInside.size()-1;
		int randomIndex, posX, posY;
		Container container;
		Mosquito adult;
		for (int i = 0; i < DataSet.INFECCIONBUILDER_MOSQUITOS_INICIALES; i++) {
			if (indexesCount >= 0) { // Si quedan contenedores inside
				randomIndex = RandomHelper.nextIntFromTo(0, indexesCount);
				container = containersInside.get(ciIndexes[randomIndex]);
				ciIndexes[randomIndex] = ciIndexes[indexesCount--];
				posX = container.getPositionX();
				posY = container.getPositionY();
			}
			else { // Si no hay mas contenedores inside, busca una posicion random
				posX = RandomHelper.nextIntFromTo(1, anchoGrilla);
				posY = RandomHelper.nextIntFromTo(1, altoGrilla);
			}
			adult = new Mosquito(grid, posX, posY);
			context.add(adult);
			grid.moveTo(adult, posX, posY);
			//AddAgentToContext("infeccion", adult);
			//MoveAgent("grid", adult, posX, posY);
		}
	}

	/**
	 * Crea Acuaticos de los 3 tipos (Huevo, Larva y Pupa) y los distribuye en los Containers intradomiciliarios.
	 * @param context  Contexto de la simulacion
	 * @param grid  Grilla de la simulacion
	 * @see DataSet#cantidadAcuaticosIniciales
	 */
	private void generateAedesAcuaticos(Context<Object> context, Grid<Object> grid) {
		double distChance, aquaLife;
		int aquaState;
		Acuatico.initAgentID(); // Reiniciar ID de acuaticos
		context.add(new Acuatico()); // Unicamente para la grafica en Repast Simphony
		// Aleatoriamente cuenta la cantidad de huevos, larvas y pupas a crear
		final ArrayList<Container> containersInside = WaterSites.getContainerInsideList();
		if (containersInside.size() == 0) // Solo agrega acuaticos en los contenedores dentro del hogar
			return;
		final int aquaticPerContainer = DataSet.cantidadAcuaticosIniciales / containersInside.size();
		// Distribuir
		for (Container container : containersInside) {
			int aquaticsCreated = 0;
			int eggChance = DataSet.ACUATICO_DIST_INICIAL_HUEVO;
			int larvaChance = DataSet.ACUATICO_DIST_INICIAL_OTROS;
			do { // Agrega acuaticos hasta que se pasen del promedio, o no entren mas
				aquaLife = RandomHelper.nextDoubleFromTo(0, 1); // Toda la camada (ovoposicion) tiene la misma edad
				distChance = RandomHelper.nextIntFromTo(1, 100);
				if (distChance < eggChance) // 25-35 -> 30%
					aquaState = 0; // Huevo
				else if (distChance < larvaChance) // 63-70 - 53-60 -> 61.5%
					aquaState = 1; // Larva
				else
					aquaState = 2; // Pupa
				
				for (int i = 0; i < DataSet.ADULTO_HUEVOS_OVOPOSICION; i++) {
					++aquaticsCreated;
					if (!container.addAquatic(new Acuatico(container, aquaState, aquaLife))) {
						if (aquaState == 0)	// Si se llena de huevos - cambia la probabilidad a cero
							eggChance = 0;
						else				// Si se llena de larvas-pupas - cambia la probabilidad de huevos
							eggChance = 100;
						break;
					}
					if (aquaticsCreated >= aquaticPerContainer)
						break;
				}
			} while ((aquaticsCreated <= aquaticPerContainer) &&
					(container.getAvailableCarryingCapacity() >= DataSet.ADULTO_HUEVOS_OVOPOSICION));
		} 
	}

	/**
	 * Crea Humanos y asigna a cada uno un lugar aleatorio en la grilla, como posicion del hogar.
	 * @param context  Contexto de la simulacion
	 * @param grid  Grilla de la simulacion
	 * @see DataSet#cantidadHumanosIniciales
	 */
	private void inicializarHumanos(Context<Object> context, Grid<Object> grid) {
		// MaxiF: La cantidad de humanos la saco de la ciudad
		for (int i = 0; i < DataSet.cantidadHumanosIniciales; i++) {
			// MaxiF: Agrego los humanos
			// MaxiF: Defino cuestiones de comportamiento de los humanos
			Humano agent = new Humano(grid, OroVerde.posCasa(), OroVerde.posTrabajo(), OroVerde.matrizTMMC());
			// Posiciono los Humanos en el contexto y en el grid
			context.add(agent);
			// Ahora tengo que mover al Humano a su casa, que es donde arranca.
			grid.moveTo(agent, agent.getPosCasa().getX(), agent.getPosCasa().getY());
		}
		
		for (int i = 0; i < DataSet.cantHumanosExtranjeros; i++) {
			Humano agent = new Humano(grid, null, OroVerde.posTrabajo(), OroVerde.matrizTMMCSC());
			// El extranjero comienza fuera del Contexto
			agent.setForeignResident();
		}
		
		for (int i = 0; i < DataSet.cantHumanosLocales; i++) {
			Humano agent = new Humano(grid, OroVerde.posCasa(), null, OroVerde.matrizTMMCSC());
			context.add(agent);
			grid.moveTo(agent, agent.getPosCasa().getX(), agent.getPosCasa().getY());
			agent.setLocalResident();
		}
	}
	
	private double randomizarDimension(int min, int max) {
		return randomizarDimension((double) min, (double) max);
	}
	
	private double randomizarDimension(double min, double max) {
		Normal rndHelper = RandomHelper.createNormal(min, max);
		double dimension;
		do {
			dimension = rndHelper.nextDouble();
		} while (dimension <= 0d);
		return dimension;
	}
}
