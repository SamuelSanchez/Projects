package simulation.learning;

import simulation.tool.Global;

public class Obstacle {
	
	private Global.STAGE id;
	private double xPos;
	private double yPos;
	
	public Obstacle(){
		this(Global.STAGE.UNKNOWN, 0, 0);
	}
	
	public Obstacle(Global.STAGE id, double xPos, double yPos){
		this.id = id;
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public Global.STAGE getId() {
		return id;
	}

	public double getXPos() {
		return xPos;
	}

	public double getYPos() {
		return yPos;
	}

	public void setId(Global.STAGE id) {
		this.id = id;
	}

	public void setXPos(double xPos) {
		this.xPos = xPos;
	}

	public void setYPos(double yPos) {
		this.yPos = yPos;
	}

	public String toString(){
		return "Id [" + id + "] - xPos[" + xPos + "] - yPos[" + yPos + "]";
	}
}
