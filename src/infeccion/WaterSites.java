package infeccion;

import static java.lang.Math.*;
import static repast.simphony.essentials.RepastEssentials.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import repast.simphony.engine.schedule.*;
import repast.simphony.random.*;
import cern.jet.random.Normal;

/**
 * La clase {@code WaterSites} actualiza el Clima diariamente y lleva un control del remanente de agua en contenedores al exterior.<p>
 * Ademas implementa un control global de la cantidad de Acuaticos y lleva registros de los mismos para almacenar la informacion en distintos logs.
 */
public final class WaterSites {
	/** Containers intradomiciliarios. */
	private static ArrayList<Container> containersInside = new ArrayList<Container>();
	/** Remanente de agua en contenedores al exterior. */
	private static double globalMeanMmWater = 0d;
	/** Suma diaria de agua en contenedores al exterior. */
	private static double globalWaterAcumulated = 0d;
	
	protected static HashMap<Integer, ArrayList<Double>> mmWaterOutsideHistoryPerContainerMap = new HashMap<> ();
	protected static Map<String, ArrayList<Integer>> acuaticListByContainerHistoryMap = new HashMap<> ();
	protected static Map<String, ArrayList<Integer>> adultProductivityByContainerHistoryMap = new HashMap<> ();
	protected static Map<String, ArrayList<Integer>> adultProductivityByContainerCleanHistoryMap = new HashMap<> ();
	
	private static int carryingCapacityNeta = 8000;
	
	public  static double	getGlobalMeanMmWater(){ return globalMeanMmWater; }

	protected static void updateGlobalWaterAcumulated(double value) {
		globalWaterAcumulated += value;
	}

	public	static void addValueMmWaterOutsideHistoryPerContainerMap(Integer agentID, ArrayList<Double> waterList) {
		mmWaterOutsideHistoryPerContainerMap.put(agentID, waterList);
	}

	public	static void addValueAquaticListByContainerHistoryMap(int agentID, boolean inside, ArrayList<Integer> amountAcuaticsHistory) {
		String id = ((inside) ? "i " : "o ");
		id += String.valueOf(agentID);
		acuaticListByContainerHistoryMap.put(id, amountAcuaticsHistory);
	}

	public 	  static void addValueAdultProductivityByContainerHistoryMap(int agentID, boolean inside, ArrayList<Integer> adultProductivityHistory) {
		String id = ((inside) ? "i " : "o ");
		id += String.valueOf(agentID);
		adultProductivityByContainerHistoryMap.put(id, adultProductivityHistory);
	}

	public 	  static void addValueAdultProductivityByContainerCleanHistoryMap(int agentID, boolean inside, ArrayList<Integer> adultProductivityCleanHistory) {
		String id = ((inside) ? "i " : "o ");
		id += String.valueOf(agentID);
		adultProductivityByContainerCleanHistoryMap.put(id, adultProductivityCleanHistory);
	}

	public  static int getCarryingCapacityNeta() {
		return carryingCapacityNeta;
	}

	public static void addContainerInside(Container container) {
		containersInside.add(container);
	}
	public static ArrayList<Container> getContainerInsideList() {
		return containersInside;
	}

	public WaterSites() {
		Clima.initDayNumber();
		containersInside.clear();
		WaterSites.mmWaterOutsideHistoryPerContainerMap.clear();
	}
	
	/**
	 * Diariamente actualiza Clima y reinicia los contadores de agua en contenedores al exterior.
	 * @see Clima#updateClima() 
	 */
	@ScheduledMethod(start = 0d, interval = 12d, priority = 0.95d)
	public void inicializadorDiario() {
		Clima.updateClima();
		WaterSites.globalWaterAcumulated = 0;
		WaterSites.globalMeanMmWater = 0;
	}

	/**
	 * Calcula el remanente de agua en contenedores al exterior. 
	 */
	@ScheduledMethod(start = 0d, interval = 12d, priority = 0.84d)
	public void updateValues() {
		double mmWater = 0d;
		HashMap<Integer, ArrayList<Double>> historyWaterInContainers = mmWaterOutsideHistoryPerContainerMap;
		if (!historyWaterInContainers.isEmpty()) {
			mmWater = globalWaterAcumulated / historyWaterInContainers.size();
		}
		WaterSites.globalMeanMmWater = mmWater;

		if (DataSet.BATCH_LOGGING_ENABLED) {
			if (Clima.dayNumber == 1) {
				saveValuesInsideFile();
			}
			else if (Clima.dayNumber % DataSet.WATERSITES_INTERVALO_GUARDAR_DATOS == 0) { // dia para almacenar corridas
				saveWaterOutsideFile();
				saveAcuaticListByContainerFile();
				saveAdultProductivityByContainerFile();
				saveAdultProductivityByContainerCleanFile();
			}
		}
	}

	/**
	 * Cada dos dias actualiza el limite global de Acuaticos.
	 */
	@ScheduledMethod(start = 0d, interval = 24d, priority = 0.66d)
	public void updateAcuaticValues() {
		WaterSites.updateCarryingCapacityNeta();
	}
	
	public static void saveValuesInsideFile() {
		String fileName = "DataValuesInside.txt";
		File file = generateFile(fileName);
		StringBuilder str = new StringBuilder("ID:[posicionX,\t posicionY,\t mmWater,\t Alto,\t Area,\t CapacidadDeAcarreo]\t\t CANTIDAD:");
		if (!containersInside.isEmpty()) {
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(file));
				str.append(containersInside.size());
				str.append("\n");
				for (Container container : containersInside) {
					str.append("contenedor_");
					str.append(container.getAgentID());
					str.append(": [");
					str.append(container.getPositionX());
					str.append(",\t");
					str.append(container.getPositionY());
					str.append(",\t");
					str.append(container.getMmWater());
					str.append(",\t");
					str.append(container.getHeight());
					str.append(",\t");
					str.append(container.getArea());
					str.append(",\t");
					str.append(container.getCarryingCapacity());
					str.append("]\n");
				}
				writer.write(str.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void saveWaterOutsideFile() {
		String fileName = "DataWaterSitesOutside.txt";
		File file = generateFile(fileName);
		generateVectorPythonFile(mmWaterOutsideHistoryPerContainerMap, file);
	}

	public static void saveAdultProductivityByContainerFile() {
		String fileName = "AdultProductivityByContainer.txt";
		File file = generateFile(fileName);
		generateVectorPythonFile(adultProductivityByContainerHistoryMap, file);
	}

	public static void saveAdultProductivityByContainerCleanFile() {
		String fileName = "AdultProductivityCleanByContainer.txt";
		File file = generateFile(fileName);
		generateVectorPythonFile(adultProductivityByContainerCleanHistoryMap, file);
	}

	public static void saveAcuaticListByContainerFile() {
		String fileName = "AcuaticListByContainer.txt";
		File file = generateFile(fileName);
		generateVectorPythonFile(acuaticListByContainerHistoryMap, file);
	}

	private static void generateVectorPythonFile(Map mapToVector, File file) {
		if (!mapToVector.isEmpty()) {
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(file));
				StringBuilder str = new StringBuilder();
				Iterator it = mapToVector.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry entry = (Map.Entry) it.next();
					Iterator valuesIterator = ((ArrayList<?>) entry.getValue()).iterator();
					if (valuesIterator.hasNext()) {
						str.append(entry.getKey());
						str.append(": [");
						while (valuesIterator.hasNext()) {
							str.append(valuesIterator.next().toString());
							if (valuesIterator.hasNext()) {
								str.append(", ");
							}
							else {
								str.append("]");
							}
						}
						str.append("\n");
					}
				}
	            writer.write(str.toString());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private static File generateFile(String fileName) {
		String path = "output/";
		String pathFileName = path + fileName;
		File file = new File(pathFileName);
		if (file.exists() && file.isFile()) {
			file.delete();
		}
		return file;
	}

	public  static void updateCarryingCapacityNeta() {
		final int maxValueLogistic = 3300;
		final int tick = (int)GetTickCount();
		double ccNeta;
		if (tick < maxValueLogistic) {
			ccNeta = logisticCarryingC(tick, DataSet.acarreoNetaAcuaticos, DataSet.ACUATICO_ACARREO_NETA_INICIAL);
		}
		else {
			ccNeta = expInversa(tick, DataSet.acarreoNetaAcuaticos, DataSet.ACUATICO_ACARREO_NETA_INICIAL, maxValueLogistic);
		}
		Normal norm = RandomHelper.createNormal(ccNeta, ccNeta*0.15d);
		carryingCapacityNeta = norm.nextInt();
	}

	private static double logisticCarryingC(int t, int capacidadNeta, int capacidadInicial) {
		final int midleGrowth = 1700;
		final int capacidadNetaParaEcuacion = capacidadNeta - capacidadInicial;
		final double result = capacidadInicial + capacidadNetaParaEcuacion / (1 + Math.exp(-0.004d*(t - midleGrowth)));
		return result;
	}

	private static double expInversa(int t, int capacidadNeta, int capacidadInicial, int maxValueLogistic) {
		final double coefDecadencia = 6;
		final double coefLetargo = coefDecadencia / (4300 - (double)maxValueLogistic);
		final double capacidadNetaParaEcuacion = capacidadNeta - capacidadInicial;
		final double coefMult = capacidadNetaParaEcuacion / Math.exp(6);
		final double result = capacidadInicial + coefMult*exp(-(t-maxValueLogistic)*coefLetargo + coefDecadencia);
		return result;
	}
}
