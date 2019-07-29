package infeccion;

import infeccion.*;
import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;

public class AgentStyle2D extends DefaultStyleOGL2D {

	@Override
	public Color getColor(Object agent) {
		// TODO Auto-generated method stub
		if(agent instanceof humanos){
			humanos hum= (humanos)agent;
			if(hum.infected){
				return Color.red;
		
			    }
			
			else return Color.blue;
		}
		else if(agent instanceof Adulto) {
			 	Adulto mos=(Adulto)agent;
			 	if(mos.infected){
			 		return Color.MAGENTA;
			 	}
			 	else {return Color.ORANGE;}
	
		      }
		      else if(agent instanceof Conteiner) {
					Conteiner conteiner=(Conteiner)agent;
				 	if(conteiner.getHaveMmWater()){
				 		return Color.blue;
				 	}
				 	else {return Color.black;}
			      } 
		    	  return Color.white;
	}

}
