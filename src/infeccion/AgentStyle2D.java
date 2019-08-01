package infeccion;

import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;

public class AgentStyle2D extends DefaultStyleOGL2D {
	@Override
	public Color getColor(Object agent) {
		if (agent instanceof Humano) {
			Humano human = (Humano)agent;
			if (human.infected)
				return Color.red;
			else
				return Color.black;
		}
		else if (agent instanceof Mosquito) {
			Mosquito mosquito = (Mosquito)agent;
			if (mosquito.infected)
				return Color.pink;
			else
				return Color.yellow;
		}
		else if (agent instanceof Container) {
			Container conteiner = (Container)agent;
			if (conteiner.containsWater())
				return Color.blue;
			else
				return Color.gray;
		}
		return Color.DARK_GRAY;
	}
}
