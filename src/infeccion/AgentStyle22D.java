package infeccion;

import infeccion.aedes.*;
import java.awt.Color;
import repast.simphony.gis.styleEditor.SimpleMarkFactory;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.ShapeFactory2D;
import saf.v3d.scene.VSpatial;


public class AgentStyle22D extends DefaultStyleOGL2D {

	
	  private static SimpleMarkFactory markFac = new SimpleMarkFactory();

	    @Override
	    public void init(ShapeFactory2D factory) {
	        super.init(factory);
	    }

	    @Override
	    public VSpatial getVSpatial(Object agent, VSpatial spatial) {
	        if (spatial==null || agent instanceof Adulto) {
	            spatial = shapeFactory.createShape(markFac.getMark("Triangle"), true);
	        }
	        else if (spatial==null || agent instanceof humanos) {
	            spatial = shapeFactory.createShape(markFac.getMark("X"), true);
	        }
	        return spatial;
	    }

	    @Override
	    public Color getColor(Object object) {
	    	if(object instanceof humanos){
				humanos hum= (humanos)object;
				if(hum.infected){
					return Color.red;
			
				    }
				
				else return Color.blue;
			}
			else if(object instanceof Adulto) {
				 	Adulto mos=(Adulto)object;
				 	if(mos.infected){
				 		return Color.orange;
				 	}
				 	else {return Color.BLACK;}
		
			      }
			      else return Color.white;
	    }

	    @Override
	    public float getRotation(Object object) {
	        return 0;
	    }

	    @Override
	    public float getScale(Object object) {
	    	float a=0;
	    	if(object instanceof humanos)
	    		{a=4;}
	    	else{
	    			if(object instanceof Adulto){a=4;}
	    	 }	
	    	return a;
	    	
	    }

	
	
	
}
