package infeccion

import static java.lang.Math.*
import static repast.simphony.essentials.RepastEssentials.*
import repast.simphony.engine.schedule.*
import repast.simphony.parameter.*
import repast.simphony.ui.probe.*

public final class dataSet{
	private 	  static int      aedesAcuatico_capacidadAcarreoNeta 		  	   = 85000;	//maxima cantidad de Aedes Acuaticos, se actualiza cada 2 dias							RAFA!!!!!!!!!!!!												
	private 	  static int      aedesAcuatico_capacidadAcarreoNetaInicial    	   = 10000;  //desvio estandar de la maxima cantidad De Aedes Acuaticos								RAFA!!!!!!!!!!!!
	private 	  static boolean  aedesAcuatico_MortalidadHabilitada			   = true;	//sirve para testeo, desabhilita las mortalidades acuaticas
	private 	  static double   aedesAcuatico_Huevo_MortalidadDiaria		 	   = 0.01;	//de cada 100 en condiciones optimas
	
	private 	  static int      aedesAdulto_CantidadHuevosPorOvoposicion         = 35;																						//  RAFA!!!!!!!!!!!!
	private 	  static int	  aedesAdulto_CantidadDeOvoposiciones   		   = 3;   																						//  RAFA!!!!!!!!!!!!			
	 
	private final static double   aedesAdulto_diasDeGestacion 				  	   = 2;  	//minimo de dias para poder comer, aparearse e incuvar y gestacion de los huevos
	private 	  static double   aedesAdulto_MortalidadDiaria				  	   = 0.07;	//de cada 100 en condiciones optimas
	private final static double   aedesAdulto_TiempoMaximoBusquedaContenedor       = 2;		// tiene un tiempo maximo de 1 dia para buscar contenedores con agua, sino oviposiciona en otro lugar muriendo todos sus huevos
		
	
	private final static double[] clima_TemperaturaJulio2016aJunio2017		  	   = 
										 [10.00, 7.40, 8.15, 12.90, 11.35, 12.60, 12.70, 14.95, 16.05, 17.50, 12.75, 8.80, 10.25, 11.80, 10.60, 13.60, 13.00, 9.75, 8.30, 10.50, 12.45, 8.65, 12.80, 14.55, 17.00, 24.50, 19.45, 20.85, 19.25, 15.15,
										  20.10, 16.90, 16.20, 18.85, 24.85, 20.20, 21.40, 15.50, 13.70, 15.15, 12.20, 11.75, 10.55, 11.35, 12.65, 12.55, 15.75, 14.25, 13.65, 14.55, 16.30, 11.90, 12.05, 15.00, 18.60, 16.10, 19.60, 23.85, 24.65,
										  22.45, 12.60, 15.50, 12.05, 13.70, 15.45, 15.70, 17.05, 16.90, 17.30, 9.95, 9.10, 12.45, 16.80, 18.90, 22.25, 16.00, 15.65, 16.30, 18.05, 17.85, 17.85, 16.25, 14.75, 14.20, 15.50, 14.80, 17.75, 18.65, 
										  16.10, 15.25, 19.45, 16.00, 10.60, 12.30, 19.70, 22.60, 17.35, 16.90, 15.90, 12.90, 11.60, 13.75, 18.00, 20.35, 16.95, 14.70, 11.95, 15.45, 18.60, 19.05, 20.55, 19.80, 19.90, 14.20, 20.00, 20.15, 22.90, 
										  24.10, 21.65, 18.05, 17.80, 16.00, 16.40, 18.55, 18.00, 18.00, 18.80, 20.75, 22.25, 25.25, 22.30, 24.10, 23.05, 20.30, 22.50, 23.65, 23.40, 24.35, 24.45, 18.15, 16.60, 17.80, 21.90, 22.05, 21.15, 22.70, 
										  22.65, 22.30, 20.20, 21.05, 22.95, 23.30, 23.55, 22.10, 24.40, 24.15, 21.85, 21.95, 24.70, 22.05, 24.50, 25.90, 26.25, 24.35, 22.85, 24.95, 25.60, 21.85, 21.45, 19.05, 21.45, 26.10, 25.75, 21.60, 24.00, 
										  26.15, 26.15, 27.05, 28.95, 25.45, 26.90, 18.05, 19.20, 22.20, 25.30, 26.75, 27.80, 23.40, 26.05, 28.75, 28.00, 25.15, 26.45, 20.75, 24.70, 25.85, 26.75, 28.10, 26.70, 25.55, 21.85, 22.80, 23.25, 25.20, 
										  26.85, 28.05, 29.30, 30.05, 23.10, 22.35, 21.50, 23.40, 24.70, 26.80, 23.10, 26.95, 28.25, 28.05, 26.25, 26.30, 25.25, 20.70, 23.50, 25.25, 22.60, 23.75, 23.95, 26.20, 24.70, 23.95, 20.30, 19.00, 20.50, 
										  23.45, 26.50, 27.30, 21.65, 23.40, 25.05, 22.10, 23.55, 24.10, 21.25, 23.10, 25.80, 26.25, 26.05, 25.55, 25.40, 27.10, 26.85, 25.20, 25.05, 24.15, 25.10, 26.30, 27.95, 26.20, 24.80, 24.90, 16.10, 17.55, 
										  18.95, 17.00, 16.75, 17.80, 18.40, 20.05, 21.55, 22.15, 21.05, 20.05, 21.90, 24.55, 24.50, 23.85, 18.75, 18.55, 20.15, 22.45, 25.35, 25.30, 24.35, 21.75, 24.20, 22.70, 22.75, 19.25, 19.10, 20.75, 21.85, 
										  18.30, 19.50, 19.95, 20.85, 21.50, 20.60, 23.75, 24.55, 19.50, 21.45, 21.50, 17.40, 12.35, 15.20, 15.70, 16.15, 11.20, 15.45, 18.15, 18.20, 14.85, 16.90, 17.85, 18.35, 19.95, 20.90, 19.40, 20.30, 23.70, 
										  22.25, 24.00, 20.60, 21.25, 16.50, 16.30, 16.05, 15.40, 14.70, 15.05, 17.30, 15.95, 16.05, 17.95, 19.80, 20.05, 24.15, 20.40, 20.15, 18.20, 16.80, 13.20, 12.05, 11.65, 12.50, 11.40, 10.95, 11.10, 13.80, 
										  6.75, 7.05, 10.80, 11.95, 11.55, 7.35, 9.35, 13.30, 10.00, 7.40, 8.15, 14.85, 16.35, 13.70, 13.80, 11.02]; 
	private final static double[] clima_PrecipitacionesJulio2016aJunio2017	  	   = 			  	//	private static double[] clima_PrecipitacionesJulio2016aJunio2017 = [0.0, 100.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 	
	   									   [0.0, 0.2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 24.3, 0.0,
										    0.0, 0.0, 0.0, 0.0, 0.2, 0.0, 4.8, 0.0, 33.6, 24.7, 0.0, 24.9, 2.4, 1.0, 0.0, 0.0, 3.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
											0.0, 0.0, 20.0, 0.0, 0.0, 35.0, 0.0, 0.0, 0.0, 7.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 40.0, 0.0, 0.0, 5.5, 0.0, 0.0, 0.0, 3.5, 5.4, 0.0,
											11.5, 14.0, 0.0, 0.0, 0.0, 28.0, 0.0, 0.0, 20.0, 0.0, 0.0, 0.0, 10.6, 3.9, 0.0, 0.0, 0.0, 0.0, 28.0, 0.0, 0.0, 0.0, 0.0, 0.0, 10.0, 47.0, 0.0, 0.0, 30.0, 0.3, 0.0,
											3.4, 0.0, 0.0, 15.0, 0.0, 0.0, 0.0, 0.0, 0.0, 40.5, 0.0, 6.5, 0.0, 0.0, 0.0, 0.0, 2.4, 6.2, 19.6, 0.0, 0.0, 15.0, 0.0, 0.0, 0.0, 21.0, 35.3, 0.0, 0.0, 0.0,
											0.0, 26.0, 0.0, 0.0, 25.0, 0.0, 0.0, 0.0, 12.8, 0.0, 0.0, 7.3, 4.5, 0.3, 0.0, 0.0, 52.6, 5.6, 0.0, 0.0, 0.0, 1.3, 3.6, 0.0, 0.0, 0.0, 0.0, 0.0, 36.5, 0.0, 0.0,
											0.0, 0.0, 10.9, 20.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 5.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 26.0, 0.0, 0.0, 0.0, 0.0, 0.0, 25.5, 7.1, 0.0, 0.0, 1.2, 6.0, 0.0,
											0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 70.7, 0.0, 0.0, 0.0, 0.0, 2.4, 0.0, 0.2, 0.0, 52.0, 0.0, 94.7, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 81.2, 0.0, 0.0, 0.0,
											21.8, 0.0, 0.0, 17.0, 33.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 48.7, 7.4, 12.5, 0.3, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
											45.1, 10.2, 0.5, 15.3, 25.5, 15.0, 14.4, 34.0, 0.7, 26.0, 2.8, 21.7, 0.0, 0.9, 0.2, 32.2, 1.5, 20.0, 20.6, 7.0, 0.0, 0.0, 0.2, 0.0, 6.3, 0.0, 0.0, 0.0, 0.0, 0.0,
											0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 4.3, 0.0, 0.0, 0.0, 1.3, 1.2, 0.2, 0.2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.8, 3.2, 4.9, 0.0,
											0.8, 0.0, 0.0, 5.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 10.7, 8.7, 0.0, 0.0, 0.0, 0.0];
										
	private final static double[] clima_VientosJulio2016aJunio2017			       = 
								[24.666666, 16.666666, 6.0, 12.5, 16.333334, 19.166666, 13.666667, 11.333333, 14.833333, 17.5, 16.333334, 19.0, 11.333333, 12.5, 15.166667, 19.0, 16.0, 13.666667, 23.5, 22.0, 13.666667, 22.333334, 24.833334, 17.5, 15.166667, 18.166666, 16.666666, 17.5, 9.0, 13.666667, 15.166667, 
								 16.833334, 11.333333, 19.0, 29.0, 26.166666, 16.333334, 19.0, 8.333333, 21.666666, 12.833333, 11.333333, 11.333333, 9.5, 11.333333, 26.333334, 24.0, 10.666667, 22.0, 13.666667, 8.333333, 16.666666, 16.0, 27.666666, 12.0, 13.666667, 11.333333, 9.833333, 11.333333, 8.333333, 17.5, 9.833333, 
								 38.333332, 7.5, 24.666666, 17.5, 13.666667, 15.166667, 13.666667, 8.333333, 12.5, 17.833334, 23.666666, 11.333333, 16.0, 10.166667, 16.666666, 14.0, 21.833334, 22.333334, 13.666667, 11.333333, 13.666667, 11.333333, 20.333334, 22.0, 28.0, 12.166667, 20.5, 20.5, 18.666666, 15.166667, 15.166667, 
								 21.833334, 19.0, 19.0, 13.666667, 23.333334, 18.166666, 33.5, 15.166667, 15.0, 24.833334, 26.0, 10.666667, 6.0, 11.333333, 16.666666, 18.166666, 11.333333, 13.666667, 17.5, 23.666666, 13.666667, 15.166667, 23.5, 13.833333, 11.333333, 27.666666,  20.333334, 13.666667, 19.333334, 17.5, 19.0, 
								 15.166667, 13.666667, 16.0, 19.0, 20.5, 12.5, 15.666667, 9.666666, 19.666666, 11.333333, 11.333333, 21.833334, 8.333333, 23.333334, 17.5, 17.5, 13.666667, 13.666667, 16.333334, 27.5, 19.0, 24.666666, 17.833334, 9.0, 9.0, 18.166666, 11.333333, 16.666666, 19.666666, 12.0, 11.333333, 
								 16.0, 19.0, 15.0, 11.333334, 19.333334, 26.333334, 20.333334, 16.666666, 9.0, 25.0, 26.666666, 18.0, 13.666667, 19.166666, 16.0, 16.666666, 5.666666, 16.666666, 12.833333, 20.5, 26.333334, 19.333334, 9.0, 14.333333, 13.666667, 21.0, 17.5, 14.833333, 13.666667, 14.833333, 14.833333, 
								 15.166667, 9.0, 8.333333, 13.666667, 12.5, 22.333334, 20.5, 12.5, 15.166667, 25.0, 13.666667, 20.5, 17.5, 14.833333, 19.833334, 31.0, 13.666667, 19.666666, 9.0, 19.0, 15.166667, 22.0, 20.833334, 20.333334, 13.666667, 11.333333, 13.666667, 17.5, 20.5, 11.333333, 16.666666, 
								 12.333334, 14.833333, 18.166666, 14.666666, 11.333333, 11.333333, 20.333334, 18.0, 13.666667, 9.0, 10.166667, 30.833334, 19.333334, 22.0, 11.333333, 19.0, 19.666666, 20.333334, 12.5, 11.333333, 11.333333, 15.166667, 7.1666665, 11.333333, 12.5, 15.166667, 9.0, 17.5, 16.333334, 15.166667, 11.333333, 19.0, 
								 13.666667, 11.333333, 11.333333, 13.666667, 13.666667, 11.333333, 21.333334, 11.333333, 12.5, 13.666667, 15.166667, 16.0, 9.0, 12.5, 12.5, 15.166667, 16.666666, 6.0, 34.333332, 7.1666665, 6.0, 15.5, 13.666667, 12.5, 15.166667, 19.166666, 11.333333, 12.5, 13.666667, 10.666667, 13.666667, 
								 12.5, 23.333334, 13.666667, 12.166667, 12.5, 15.166667, 19.0, 11.333333, 4.5, 8.666667, 15.166667, 11.333333, 19.0, 24.833334, 13.666667, 12.166667, 16.666666, 9.0, 5.6666665, 10.166667, 15.5, 7.1666665, 7.1666665, 12.5, 12.166667, 11.0, 12.5, 7.5, 14.166667, 6.0, 15.166667, 
								 8.333333, 16.333334, 18.0, 11.333333, 14.833333, 14.333333, 15.166667, 15.166667, 11.333333, 22.0, 19.0, 12.5, 16.333334, 6.0, 9.0, 13.666667, 9.0, 17.5, 16.333334, 11.333333,  9.0, 12.5, 14.333333, 19.0, 18.0, 16.0, 12.5, 18.166666, 11.333333, 16.666666, 16.0, 
								18.166666, 21.0, 19.0, 17.833334, 19.0, 9.0, 19.666666, 23.333334, 15.5, 19.5, 16.333334, 19.666666, 11.333333, 17.5, 19.0, 7.5, 18.166666, 27.666666, 29.0, 15.666667, 19.166666, 8.333333, 6.0]
	private final static double[] clima_HumedadJulio2016aJunio2017				   = 
							[67, 71, 73, 75, 95, 71, 67, 64, 87, 92, 84, 83, 77, 59, 47, 48, 77, 79, 73, 70, 56, 54, 65, 66, 83, 82, 72, 70, 84, 71, 93, 85, 85, 59, 59, 84, 69, 86, 89, 97, 89,
							 85, 98, 98, 97, 77, 82, 99, 75, 79, 85, 75, 69, 77, 63, 62, 79, 49, 55, 63, 67, 68, 53, 67, 70, 59, 41, 46, 56, 58, 70, 61, 48, 49, 41, 37, 47, 56, 43, 43, 65, 77, 
							 84, 87, 96, 95, 76, 70, 56, 59, 82, 51, 60, 80, 68, 55, 67, 68, 75, 63, 56, 64, 74, 61, 58, 61, 67, 61, 50, 47, 70, 58, 53, 51, 54, 57, 67, 86, 69, 57, 86, 95, 64, 
							 74, 70, 77, 76, 63, 56, 51, 54, 61, 74, 66, 84, 73, 54, 48, 67, 76, 71, 83, 73, 55, 52, 46, 50, 53, 72, 87, 54, 43, 42, 45, 60, 71, 55, 64, 54, 43, 55, 71, 54, 55, 
							 72, 69, 82, 57, 50, 92, 54, 43, 51, 74, 85, 85, 72, 72, 66, 60, 60, 82, 70, 76, 64, 65, 77, 81, 78, 68, 54, 66, 58, 62, 61, 78, 73, 54, 52, 49, 62, 57, 51, 53, 53, 
							 53, 50, 56, 74, 62, 60, 59, 69, 86, 68, 57, 59, 64, 58, 70, 58, 77, 95, 78, 64, 71, 76, 88, 74, 75, 83, 95, 77, 86, 90, 73, 70, 66, 66, 73, 95, 53, 54, 62, 91, 79, 
							 68, 64, 83, 62, 63, 67, 78, 80, 75, 61, 65, 63, 61, 61, 67, 95, 83, 94, 90, 76, 61, 63, 75, 81, 82, 65, 64, 65, 64, 88, 88, 86, 95, 98, 87, 91, 96, 97, 94, 96, 96, 
							 83, 90, 79, 78, 94, 93, 89, 83, 59, 65, 78, 77, 89, 64, 70, 64, 66, 67, 71, 77, 74, 77, 72, 76, 76, 83, 80, 93, 84, 73, 67, 80, 81, 83, 77, 72, 66, 77, 76, 69, 71, 
							 74, 78, 88, 82, 96, 99, 96, 83, 90, 83, 93, 93, 93, 86, 64, 64, 70, 75, 68, 71, 70, 56, 73, 81, 79, 74, 75, 85, 73, 80, 76, 86, 94, 98, 87, 86, 82, 89];
	private final static double   clima_EvaporacionHorasDeSolMedia				   = 3
						 
	private 	  static int      infeccionBuilder_amountInitialAedesAcuatic 	   = 4000;	//cantidad de Aedes acuaticos iniciales														RAFA!!!!!!!!!!!!
	private 	  static int      infeccionBuilder_amountInitialAedesAdultos 	   = 40;    //cantidad de Aedes adultos iniciales														RAFA!!!!!!!!!!!!
	
	private 	  static int      infeccionBuilder_amountInitialHumanos 		   = 7000; 	//cantidad de humanos														
			
	private 	 static int 	  waterSites_conteinerAmount 				   	   = 3000//cantidad de contenedores netos (expuestos, intradomiciliarios)						RAFA!!!!!!!!!!!!
	
	private final static int 	  waterSites_intervaloDiaAlamacenamientoEnArchivos = 15;		//dia en el que los archivos generados por water sites seran alamacenados	

	private final static int 	  conteiner_porcentajeContenedoresInside		   = 17;    //el 17% de los contenedores son interiores y de agua constante
	private  static int 	  conteiner_capacidadAcarreoPorLitro 			   = 70;    // 70 larvas por litro
	private final static int 	  conteiner_valorMedioAlturaContenedores		   = 15;    //distribucion de desvio estandar que varia entre 5cm y 35cm
	private final static int 	  conteiner_valorMedioAlturaContenedoresSTD		   = 10;    //distribucion de desvio estandar que varia entre 5cm y 35cm
	private final static int 	  conteiner_valorMedioAreaContenedores			   = 212.5; //distribucion de desvio estandar que varia entre 25cm2 y 400cm2
	private final static int 	  conteiner_valorMedioAreaContenedoresSTD		   = 187.5; //distribucion de desvio estandar que varia entre 25cm2 y 400cm2
	
	public static int	getAedesAcuatico_capacidadAcarreoNeta(){
		return this.aedesAcuatico_capacidadAcarreoNeta;
	}
	public static void  setAedesAcuatico_capacidadAcarreoNeta(Integer aedesAcuatico_capacidadAcarreoNeta) {
		this.aedesAcuatico_capacidadAcarreoNeta = aedesAcuatico_capacidadAcarreoNeta;
	}
	
	public static int	   getAedesAcuatico_capacidadAcarreoNetaInicial(){
		return this.aedesAcuatico_capacidadAcarreoNetaInicial;
	}
	public static void     setAedesAcuatico_capacidadAcarreoNetaInicial(Integer aedesAcuatico_capacidadAcarreoNetaInicial) {
		this.aedesAcuatico_capacidadAcarreoNetaInicial = aedesAcuatico_capacidadAcarreoNetaInicial;
	}
	
	public static boolean  getAedesAcuatico_MortalidadHabilitada() {
		return this.aedesAcuatico_MortalidadHabilitada;
	}
	public static double   getAedesAcuatico_Huevo_distanciaAguaVsHuevo() {
		return this.aedesAcuatico_Huevo_distanciaAguaVsHuevo;
	}
	public static double   getAedesacuatico_Huevo_Mortalidaddiaria() {
		return this.aedesAcuatico_Huevo_MortalidadDiaria;
	}
	public static double   setAedesacuatico_Huevo_Mortalidaddiaria(double newValue) {
		this.aedesAcuatico_Huevo_MortalidadDiaria = newValue;
	}

	
	public static int	   getAedesAdulto_CantidadHuevosPorOvoposicion() {  
		return this.aedesAdulto_CantidadHuevosPorOvoposicion;
	}
	public static void     	   setAedesAdulto_CantidadHuevosPorOvoposicion(Integer aedesAdulto_CantidadMosquitosPorOvoposicion) {
		this.aedesAdulto_CantidadHuevosPorOvoposicion = aedesAdulto_CantidadMosquitosPorOvoposicion;
	}
	
	public  static int	   getAedesAdulto_CantidadDeOvoposiciones() { 
		return this.aedesAdulto_CantidadDeOvoposiciones;
	}
	public static void     setAedesAdulto_CantidadDeOvoposiciones(Integer aedesAdulto_CantidadDeOvoposiciones){ //SET AGREGADO 15/2
		this.aedesAdulto_CantidadDeOvoposiciones=aedesAdulto_CantidadDeOvoposiciones;
	}
	
	public static double   getAedesAdulto_DiasDeGestacion() {
		return this.aedesAdulto_diasDeGestacion;
	}
	public static double   getAedesAdulto_MortalidadDiaria() {
		return this.aedesAdulto_MortalidadDiaria;
	}
	public static void     setAedesAdulto_MortalidadDiaria(double aedesAdulto_MortalidadDiaria) {
		this.aedesAdulto_MortalidadDiaria = aedesAdulto_MortalidadDiaria;
	}
	public static double   getAedesAdulto_TiempoMaximoBusquedaContenedor(){
		return this.aedesAdulto_TiempoMaximoBusquedaContenedor
	}
	
	public static double[] getClima_TemperaturaJulio2016aJunio2017(){
			return this.clima_TemperaturaJulio2016aJunio2017;
		}
	public static double[] getClima_PrecipitacionesJulio2016aJunio2017(){
		return this.clima_PrecipitacionesJulio2016aJunio2017;
	}
	public static double[] getClima_HumedadJulio2016aJunio2017(){
		return this.clima_HumedadJulio2016aJunio2017;
	}
	public static double[] getClima_VientosJulio2016aJunio2017	(){
		return this.clima_VientosJulio2016aJunio2017;
	}
	public static double   getClima_EvaporacionHorasDeSolMedia(){
		return this.clima_EvaporacionHorasDeSolMedia
	}
	
	
	public static int      getInfeccionBuilder_amountInitialAedesAcuatic() {
		return this.infeccionBuilder_amountInitialAedesAcuatic;
	}
	public static void     setInfeccionBuilder_amountInitialAedesAcuatic(int infeccionBuilder_amountInitialAedesAcuatic) {
		this.infeccionBuilder_amountInitialAedesAcuatic = infeccionBuilder_amountInitialAedesAcuatic;
	}
	
	public static int      getInfeccionBuilder_amountInitialAedesAdultos() {
		return infeccionBuilder_amountInitialAedesAdultos;
	}
	public static void     setInfeccionBuilder_amountInitialAedesAdultos(int infeccionBuilder_amountInitialAedesAdultos) {
		this.infeccionBuilder_amountInitialAedesAdultos = infeccionBuilder_amountInitialAedesAdultos;
	}
		
	public static int     getInfeccionBuilder_amountInitialHumanos(){
		return this.infeccionBuilder_amountInitialHumanos;
	}
	
	public static void     setInfeccionBuilder_amountInitialHumanos(Integer CantDeHumanos){ //SET AGREGADO 15/2
		this.infeccionBuilder_amountInitialHumanos=CantDeHumanos;
	}
	
	
	private static int     getWaterSites_conteinerAmount(){  
		return this.waterSites_conteinerAmount;
	}
	public static int      setWatersites_Conteineramount(Integer newValue) {
		this.waterSites_conteinerAmount = newValue;
	}
	
	private static int     getWaterSites_intervaloDiaAlamacenamientoEnArchivos(){
		return this.waterSites_intervaloDiaAlamacenamientoEnArchivos;
	}

	public static int      getConteiner_porcentajeContenedoresInside(){
		return this.conteiner_porcentajeContenedoresInside;
	}
	public static int      getConteiner_capacidadAcarreoPorLitro(){
		return this.conteiner_capacidadAcarreoPorLitro
	}
	
	public static void     setConteiner_capacidadAcarreoPorLitros(Integer HuevosPorLitro){ //SET AGREGADO 15/2
		this.conteiner_capacidadAcarreoPorLitro=HuevosPorLitro;
	}
	
	private static int     getConteiner_valorMedioAlturaContenedores(){
		return this.conteiner_valorMedioAlturaContenedores
	}
	private static int     getConteiner_valorMedioAlturaContenedoresSTD(){
		return this.conteiner_valorMedioAlturaContenedoresSTD
	}
	private static int     getConteiner_valorMedioAreaContenedores(){
		return this.conteiner_valorMedioAreaContenedores
	}
	private static int     getConteiner_valorMedioAreaContenedoresSTD(){
		return this.conteiner_valorMedioAreaContenedoresSTD
	}
	
}

//private final static double[] clima_PrecipitacionesJulio2016aJunio2017	  	=
//[0.0, 2.2, 0.0, 0.0, 22.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 24.3, 1.0, 0.0,
//	0.0, 0.0, 0.0, 0.2, 4.8, 0.0, 0.0, 65.3, 3.0, 4.7, 22.3, 1.3, 0.0, 0.0, 0.0, 3.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
//	0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 7.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.2, 5.3, 0.0, 0.0, 0.0, 8.9, 0.5, 0.0, 25.5, 0.0, 0.0, 0.0,
//	0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 14.5, 0.0, 0.0, 0.0, 0.0, 2.8, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 5.5, 0.0, 0.0, 30.3, 0.0, 0.0, 3.4, 0.0, 0.0, 1.0, 0.0,
//	0.0, 0.0, 0.0, 40.5, 0.0, 0.0, 6.5, 0.0, 0.0, 0.0, 2.4, 0.0, 22.3, 3.5, 0.0, 0.0, 0.0, 0.0, 0.0, 1.8, 35.4, 2.6, 0.0, 0.0, 0.0, 0.0, 30.2, 0.0, 0.0, 0.0, 0.0, 0.0,
//	12.8, 0.0, 0.0, 0.0, 7.3, 4.5, 0.3, 0.0, 45.2, 13.0, 0.0, 0.0, 0.0, 0.0, 1.3, 3.6, 0.0, 0.0, 0.0, 0.0, 0.0, 36.5, 0.0, 0.0, 0.0, 0.0, 10.9, 14.0, 0.0, 0.0, 0.0,
//	0.0, 0.0, 0.0, 0.0, 5.5, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 25.5, 7.1, 0.0, 0.0, 0.0, 1.2, 6.0, 30.4, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 69.2, 2.0,
//	0.0, 0.0, 33.3, 0.3, 2.1, 0.2, 0.0, 48.5, 3.5, 78.5, 16.2, 0.0, 0.0, 0.0, 0.0, 0.0, 18.5, 62.7, 0.0, 0.0, 16.2, 5.3, 0.0, 0.0, 17.4, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
//	0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 30.5, 18.2, 19.6, 0.3, 0.3, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 55.3, 0.0, 0.7, 34.2, 21.4, 0.0, 43.2, 5.4, 0.5, 19.2,
//	0.5, 21.7, 0.9, 0.2, 0.0, 33.5, 0.2, 50.7, 0.1, 7.0, 0.0, 0.2, 0.0, 2.5, 3.8, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.8, 3.5, 0.0, 0.0,
//	0.0, 2.5, 0.0, 0.2, 0.2, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5, 3.0, 2.0, 3.4, 0.0];


//[10.00, 7.40, 8.15, 12.90, 11.35, 12.60, 12.70, 14.95, 16.05, 17.50, 12.75, 8.80, 10.25, 11.80, 10.60, 13.60, 13.00, 9.75, 8.30, 10.50,
//	12.45, 8.65, 12.80, 14.55, 17.00, 24.50, 19.45, 20.85, 19.25, 15.15, 20.10, 16.90, 16.20, 18.85, 24.85, 20.20, 21.40, 15.50, 13.70,
//	  15.15, 12.20, 11.75, 10.55, 11.35, 12.65, 12.55, 15.75, 14.25, 13.65, 14.55, 16.30, 11.90, 12.05, 15.00, 18.60, 16.10, 19.60, 23.85,
//	   24.65, 22.45, 12.60, 15.50, 12.05, 13.70, 15.45, 15.70, 17.05, 16.90, 17.30, 9.95, 9.10, 12.45, 16.80, 18.90, 22.25, 16.00, 15.65,
//	  16.30, 18.05, 17.85, 17.85, 16.25, 14.75, 14.20, 15.50, 14.80, 17.75, 18.65, 16.10, 15.25, 19.45, 16.00, 10.60, 12.30, 19.70, 22.60,
//	  17.35, 16.90, 15.90, 12.90, 11.60, 13.75, 18.00, 20.35, 16.95, 14.70, 11.95, 15.45, 18.60, 19.05, 20.55, 19.80, 19.90, 14.20, 20.00,
//	  20.15, 22.90, 24.10, 21.65, 18.05, 17.80, 16.00, 16.40, 18.55, 18.00, 18.00, 18.80, 20.75, 22.25, 25.25, 22.30, 24.10, 23.05, 20.30,
//	  22.50, 23.65, 23.40, 24.35, 24.45, 18.15, 16.60, 17.80, 21.90, 22.05, 21.15, 22.70, 22.65, 22.30, 20.20, 21.05, 22.95, 23.30, 23.55,
//	  22.10, 24.40, 24.15, 21.85, 21.95, 24.70, 22.05, 24.50, 25.90, 26.25, 24.35, 22.85, 24.95, 25.60, 21.85, 21.45, 19.05, 21.45, 26.10,
//	  25.75, 21.60, 24.00, 26.15, 26.15, 27.05, 28.95, 25.45, 26.90, 18.05, 19.20, 22.20, 25.30, 26.75, 27.80, 23.40, 26.05, 28.75, 28.00,
//	  25.15, 26.45, 20.75, 24.70, 25.85, 26.75, 28.10, 26.70, 25.55, 21.85, 22.80, 23.25, 25.20, 26.85, 28.05, 29.30, 30.05, 23.10, 22.35,
//	  21.50, 23.40, 24.70, 26.80, 23.10, 26.95, 28.25, 28.05, 26.25, 26.30, 25.25, 20.70, 23.50, 25.25, 22.60, 23.75, 23.95, 26.20, 24.70,
//	  23.95, 20.30, 19.00, 20.50, 23.45, 26.50, 27.30, 21.65, 23.40, 25.05, 22.10, 23.55, 24.10, 21.25, 23.10, 25.80, 26.25, 26.05, 25.55,
//	  25.40, 27.10, 26.85, 25.20, 25.05, 24.15, 25.10, 26.30, 27.95, 26.20, 24.80, 24.90, 16.10, 17.55, 18.95, 17.00, 16.75, 17.80, 18.40,
//	  20.05, 21.55, 22.15, 21.05, 20.05, 21.90, 24.55, 24.50, 23.85, 18.75, 18.55, 20.15, 22.45, 25.35, 25.30, 24.35, 21.75, 24.20, 22.70,
//	  22.75, 19.25, 19.10, 20.75, 21.85, 18.30, 19.50, 19.95, 20.85, 21.50, 20.60, 23.75, 24.55, 19.50, 21.45, 21.50, 17.40, 12.35, 15.20,
//	  15.70, 16.15, 11.20, 15.45, 18.15, 18.20, 14.85, 16.90, 17.85, 18.35, 19.95, 20.90, 19.40, 24.30, 23.70, 22.25, 24.00, 24.60, 21.25,
//	  16.50, 16.30, 16.05, 15.40, 14.70, 15.05, 17.30, 15.95, 16.05, 17.95, 19.80, 20.05, 24.15, 25.40, 20.15, 18.20, 16.80, 13.20, 12.05,
//	   11.65, 12.50, 11.40, 10.95, 11.10, 13.80, 6.75, 7.05, 10.80, 11.95, 11.55, 7.35, 9.35, 13.30, 17.35, 19.65, 19.55, 14.85, 16.35, 13.70, 13.80];


//private final static double[] clima_TemperaturaJulio2016aJunio2017		  	=
//	[25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00,
//	25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00,
//	25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00,
//	25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00,
//	25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00,
//	25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00,
//	25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00,
//	25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00,
//	25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00,
//	25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00,
//	25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00, 25.00 ];

//private final static double[] clima_TemperaturaJulio2016aJunio2017		  	=
//	[31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00,
//	 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00,
//	 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00,
//	 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00,
//	 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00,
//	 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00,
//	 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00,
//	 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00,
//	 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00,
//	 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00,
//	 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00, 31.00];

