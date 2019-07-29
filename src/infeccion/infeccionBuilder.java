package infeccion;


import java.util.ArrayList;

import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.parameter.Parameters;
import repast.simphony.random.RandomHelper;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.RandomGridAdder;
import repast.simphony.space.grid.StrictBorders;

public class infeccionBuilder implements ContextBuilder<Object> {
	
	private Integer infectados;
	private Integer tiempoEntreCadaCaso;

	@Override
	public Context<Object> build(Context<Object> context) {
		context.setId("infeccion");

		//MaxiF: Agrego el agente oro verde al contexto para tener informaci�n de la ciudad
        OroVerde ciudad = new OroVerde();
		context.add(ciudad);
		
		//MaxiF: Configuro para que termine la simulaci�n en el tick 4300
		RunEnvironment.getInstance().endAt(4300);
		
		//MaxiF: Creo una proyeccion grid de largo y ancho seg�n est� en la ciudad.
		GridFactory gridFactory = GridFactoryFinder.createGridFactory(null);
		Grid<Object> grid = gridFactory.createGrid("grid", context,
				new GridBuilderParameters<Object>(new StrictBorders(),
						new RandomGridAdder<Object>(), true, ciudad.getTamanio()[0], ciudad.getTamanio()[1]));
		
		this.setBachParameters();
		
		this.inicializarClima(context);
		this.inicializarWaterSites(context);
		this.inicializarHumanos(context, ciudad, grid);
    	this.inicializarMosquitos(context); // generar adultos y acuaticos al azar
		return context;
	}
	
	private void setBachParameters(){
		Parameters params = RunEnvironment.getInstance().getParameters();

		int infectados 					 = (Integer) params.getValue("cantidadinfectados");
		int tiempoEntreCadaCaso 		 = (Integer) params.getValue("tiempoentradacaso");
		
		
		//int capAcarreoIncialAcuaticos 	 = (Integer) params.getValue("capAcarreoIncialAcuaticos");
		int capAcarreoNetaAcuaticos 	 = (Integer) params.getValue("capAcarreoNetaAcuaticos"); // maximo de mosquitos en estado acuatico
		//int cantidadContenedores 		 = (Integer) params.getValue("cantidadContenedores");
		//int cantidadHuevosPorOvoposicion = (Integer) params.getValue("cantidadHuevosPorOvoposicion");
		int cantOvoposiciones 	 = (Integer) params.getValue("cantOvoposiciones");
		int cantHuevosIniciales  = (Integer) params.getValue("cantHuevosIniciales");
		int cantHumanos          = (Integer) params.getValue("cantHumanos");
		int capHuevosPorLitro    = (Integer) params.getValue("capHuevosPorLitro");
		
		this.infectados = infectados;
		this.tiempoEntreCadaCaso = tiempoEntreCadaCaso;
		//dataSet.setAedesAcuatico_capacidadAcarreoNetaInicial(capAcarreoIncialAcuaticos);
		dataSet.setAedesAcuatico_capacidadAcarreoNeta(capAcarreoNetaAcuaticos);// maximo de mosquitos en estado acuatico
		//dataSet.setWatersites_Conteineramount(cantidadContenedores);
		//dataSet.setAedesAdulto_CantidadHuevosPorOvoposicion(cantidadHuevosPorOvoposicion);
		dataSet.setAedesAdulto_CantidadDeOvoposiciones(cantOvoposiciones);
		dataSet.setInfeccionBuilder_amountInitialAedesAcuatic(cantHuevosIniciales);
		dataSet.setInfeccionBuilder_amountInitialHumanos(cantHumanos);
		dataSet.setConteiner_capacidadAcarreoPorLitros(capHuevosPorLitro);
	}

	private void inicializarClima(Context<Object> context) {
		Clima clima = new Clima();
		context.add(clima);
	}

	private void inicializarWaterSites(Context<Object> context) {
		WaterSites newWaterSites = new WaterSites();
		context.add(newWaterSites);
	}
	
	private void inicializarMosquitos(Context<Object> context){
		//Acuatico.eliminarOutFiles();
		StateHuevo.setCantidad(0);
		StateLarva.setCantidad(0);
		StatePupa.setCantidad(0);
		Acuatico.inicializarAgentIDCounter();
		this.generateAedesAdultos(context);
		this.generateAedesAcuaticos(context);
	}
	
	private void generateAedesAdultos(Context<Object> context) {
		int amountInitialAedes = dataSet.getInfeccionBuilder_amountInitialAedesAdultos();
		for (int i = 0; i < amountInitialAedes; i++) {
			Adulto newAedes = new Adulto();
			// newAedes.intialGeneratorAdultoRandom();
			// newAedes.intialGeneratorAdultoNews();
			context.add(newAedes);
			// newAedes.inicializarvariables();
		}
	}
	
	private void generateAedesAcuaticos(Context<Object> context) {
		int amountInitialAcuatic = dataSet.getInfeccionBuilder_amountInitialAedesAcuatic();
		for (int i = 0; i < amountInitialAcuatic; i++) {
			Acuatico newAedes = new Acuatico();
			newAedes.intialGeneratorAcuaticRandom();
			context.add(newAedes);
		}
		// for (int i = 0; i < amountInitialAcuatic ; i++) {
		// Acuatico huevo = new Acuatico();
		// huevo.intialGeneratorAcuaticHuevos();
		// context.add(huevo);
		//
		// Acuatico larva = new Acuatico();
		// larva.intialGeneratorAcuaticLarvas();
		// context.add(larva);
		//
		// Acuatico pupa = new Acuatico();
		// pupa.intialGeneratorAcuaticPupas();
		// context.add(pupa);
		// }
	}

	private void inicializarHumanos(Context<Object> context, OroVerde ciudad, Grid<Object> grid) {
		
		// MaxiF: La cantidad de humanos la saco de la ciudad
		// int humanCount = ciudad.getPoblacion();
		int humanCount = dataSet.getInfeccionBuilder_amountInitialHumanos();
		// DavidM---------------------------------------------------------------------------------------------
		//Validacion David int[][] newPosition = { { 102, 102 }, { 101, 101 }, { 99, 100 },{ 97, 97 } ,{ 93, 98 } };
		// DavidM---------------------------------------------------------------------------------------------
		for (int i = 0; i < humanCount; i++) {
			// MaxiF: Agrego los humanos
			humanos agent = new humanos();
			// MaxiF: Defino cuestiones de epidemiolog�a
			RandomHelper.createNormal(2500, 288);
			agent.viremia = (int) RandomHelper.getNormal().nextDouble();
			agent.IPeriod = 1920;
			// MaxiF: Defino cuestiones de comportamiento de los humanos
			agent.setPosCasa(ciudad.posCasa());
			agent.setIDTrabajo(ciudad.IDTrabajo());
			agent.setPosTrabajo(ciudad.posTrabajo(agent.getIDTrabajo()));
			agent.setVelTraslado(ciudad.velocTraslado());
			agent.setMatrizTMMC(ciudad.matrizTMMC());
			agent.setPosLocalizActual(agent.getPosCasa());
			agent.setPosfutura(agent.getPosCasa());

			if (this.infectados > 0) {
				// mientras el contador de infectados de, marco a esos humanos
				// como
				// casos iniciales.
				agent.sospechoso = true;
				agent.tiempoentradacaso = this.tiempoEntreCadaCaso;
				this.infectados--;
			}

			// Posiciono los humanos en el contexto y en el grid
			context.add(agent);

			// Ahora tengo que mover al humano a su casa, que es donde arranca.
			grid.moveTo(agent, agent.getPosCasa().toIntArray(null));
			// DavidM---------------------------------------------------------------------------------------------
			//Validacion David // grid.moveTo(agent, newPosition[i]);
			// DavidM---------------------------------------------------------------------------------------------
		}
	}
}
