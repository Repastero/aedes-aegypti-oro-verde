package infeccion;

import static repast.simphony.essentials.RepastEssentials.*;

import java.util.ArrayList;

import repast.simphony.engine.schedule.*;
import repast.simphony.random.*;
import repast.simphony.ui.probe.*;
import repast.simphony.space.grid.*;

public class Container {
	private static int agentIDCounter = 0;
	private int agentID = agentIDCounter;
	private String agentIDString = "Container " + agentIDCounter++;
	
	private ArrayList<Integer> adultProductivityHistory = new ArrayList<Integer>();
	private ArrayList<Integer> adultProductivityCleanHistory = new ArrayList<Integer>();
	
	private ArrayList<Double> mmWaterOutsideHistory = new ArrayList<Double>();
	private ArrayList<Integer> aquaticListHistory = new ArrayList<Integer>();
	
	private ArrayList<Acuatico> aquaticsList = new ArrayList<Acuatico>();

	private int adultProductivity = 0;
	private Grid<Object> grid;
	private int	carryingCapacity;
	private int	carryingCapacityEggs;
	private boolean inside = false;
	private double[] dimensiones = {0d,0d}; //[Area[cm^2], Alto[cm]]
	private int[] position = {0,0};
	private double mmWater = 0d;
	private int eggsAmount = 0;
	private int aquaticAmount = 0;

	public Container(Grid<Object> grid, boolean isInside, double area, double height, int posX, int posY) {
		this.grid = grid;
		this.inside = isInside;
		this.dimensiones[0] = area;
		this.dimensiones[1] = height;
		this.position[0] = posX;
		this.position[1] = posY;
		if (this.inside) {
			// Valor inicial de agua
			this.mmWater = dimensiones[1] * 10; //paso de cm de altura a mm
			updateCarryingCapacity();
		}
		updateCarryingCapacityEggs();
	}
	
	public static void initAgentID() {
		agentIDCounter = 0;
	}
	
	/**
	 * Actualiza la cantidad de agua de Containers al exterior y actualiza el estado de sus Acuaticos.
	 */
	@ScheduledMethod(start = 0d, interval = 12d, priority = 0.92d)
	public void updateDay() {
		if (!inside) {
			updateMmWater();
			updateCarryingCapacity();
			mmWaterOutsideHistory.add(mmWater);
		}
		if (DataSet.BATCH_LOGGING_ENABLED) {
			aquaticListHistory.add(aquaticsList.size());
			adultProductivityHistory.add(adultProductivity);
			if (adultProductivity > 0) {
				adultProductivityCleanHistory.add(adultProductivity);
			}
		}
		updateWaterSites();
		adultProductivity = 0; // Reinicia la cantidad diaria de nuevos Mosquitos 
		
		// Eliminar los acuaticos que mueran
		aquaticsList.removeIf(aqua -> !aqua.updateLife());
	}
	
	/**
	 * Actualiza valores historicos, para el archivo de reporte.
	 */
	private void updateWaterSites() {
		if (!inside) {
			WaterSites.updateGlobalWaterAcumulated(mmWater);
			WaterSites.addValueMmWaterOutsideHistoryPerContainerMap(agentID, mmWaterOutsideHistory);
		}
		if (DataSet.BATCH_LOGGING_ENABLED) {
			if (Clima.dayNumber % DataSet.WATERSITES_INTERVALO_GUARDAR_DATOS == 0) { //dia para almacenar corridas
				if (!checkIfIsEmpty(aquaticListHistory)) {
					WaterSites.addValueAquaticListByContainerHistoryMap(agentID, inside, aquaticListHistory);
				}
				if (!checkIfIsEmpty(adultProductivityHistory)) {
					WaterSites.addValueAdultProductivityByContainerHistoryMap(agentID, inside, adultProductivityHistory);
				}
				WaterSites.addValueAdultProductivityByContainerCleanHistoryMap(agentID, inside, adultProductivityCleanHistory);
			}
		}
	}

	private boolean checkIfIsEmpty(ArrayList<Integer> aquaticListHistory) {
		boolean empty = true;
		for (int aquatics : aquaticListHistory) {
			if (aquatics != 0) {
				empty = false;
				break;
			}
		}
		return empty;
	}
	
	public  void increaseAdultProductivity() { ++adultProductivity; }
	
	public  int	getCarryingCapacity() { return carryingCapacity; }
	/**
	 * Actualiza la capacidad de acarreo de Larvas y Pupas, segun la cantidad de agua.
	 */
	private void updateCarryingCapacity() {
		carryingCapacity = obtainCarryingCapacity(mmWater);
	}
	
	public  int getAgentID() { return agentID; }
	
	public  int getPositionX() { return position[0]; }
	public  int getPositionY() { return position[1]; }
	
	public double getArea() { return dimensiones[0]; }
	public double getHeight() { return dimensiones[1]; }
	
	public  double getMmWater() { return mmWater; }
	public	boolean containsWater() { return (mmWater > 0d); }
	
	public  int getEggsAmount() { return eggsAmount; }
	public void increaseEggsAmount() { ++eggsAmount; }
	public void decreaseEggsAmount() { --eggsAmount; }

	public  int getAquaticAmount() { return aquaticAmount; }
	public void increaseAquaticAmount() { ++aquaticAmount; }
	public void decreaseAquaticAmount() { --aquaticAmount; }
	
	private int obtainCarryingCapacity(double mmWater) {
		//Capacidad de acarreo = (0.1*Agua[mm] = Agua[cm])*(Area = [cm^2])*(70 individuos por litro)
		return (int)(0.1 * mmWater * (double)dimensiones[0] * 0.001 * DataSet.capacidadAcarreoPorLitro);
	}
	public  int	getCarryingCapacityEggs() { return carryingCapacityEggs; }
	private void updateCarryingCapacityEggs() {
		int cc = carryingCapacity;
		if (!inside) {
			cc = obtainCarryingCapacity(dimensiones[1] * 10) / 2; // capacidad de acarreo media
		}
		carryingCapacityEggs = RandomHelper.nextIntFromTo(cc * 2, cc * 5);
	}

	public  boolean isInside() { return inside; }

	/**
	 * Resta el agua evaporada y suma la de la precipitacion del dia.
	 */
	private void updateMmWater() {
		double totalWater = mmWater;
		if (totalWater > 0d && GetTickCount() != 0d) {
			totalWater -= Clima.getEvaporation();
			if (totalWater < 0d) // mm de agua
				totalWater = 0d;
		}
		totalWater += Clima.getCurrentPrecipitation();
		if (totalWater > dimensiones[1] * 10) { //contenedor lleno
			totalWater = dimensiones[1] * 10;
		}
		mmWater = totalWater;
	}
	
	public int getAvailableCarryingCapacity() {
		return (carryingCapacityEggs - eggsAmount) + (carryingCapacity - aquaticAmount);
	}
	
	/**
	 * Agrega nuevo Acuatico al contenedor e incrementa su cantidad.
	 * @param aquatic  instancia de objecto Acuatico a agregar
	 * @return <code>true</code> si sobra espacio para ese tipo de Actuatico
	 */
	public boolean addAquatic(Acuatico aquatic) {
		aquaticsList.add(aquatic);
		if (aquatic.getLifeCicle() == 0) { // Huevo
			if (++eggsAmount >= carryingCapacityEggs)
				return false;
		}
		else { // Larva o pupa
			if (++aquaticAmount >= carryingCapacity)
				return false;
		}
		return true;
	}
	
	/**
	 * Crea un nuevo agente Mosquito y lo agrega al contexto, en la posicion de la grilla del Container.<p>
	 * El Acuatico al completar todas las fases, emerge como Mosquito adulto;
	 * se eliminar la referencia del Acuatico de la lista en {@link #updateDay()}.
	 */
	public void emergeMosquito() {
		increaseAdultProductivity();
		//
		Mosquito adult = new Mosquito(grid, position[0], position[1]);
		AddAgentToContext("infeccion", adult);
		MoveAgent("grid", adult, position[0], position[1]);
		//Context context = ContextUtils.getContext(this);
	}

	@ProbeID
	public String toString() {
		return agentIDString;
	}
}
