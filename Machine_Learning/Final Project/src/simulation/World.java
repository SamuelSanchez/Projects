package simulation;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.imageio.ImageIO;

import simulation.learning.Obstacle;
import simulation.tool.Global;


//TODO: Fix the boolean isCrashed. Only create a global variable for it. Remove Local variables.
public class World {
	
	private volatile Agent agent;
	private volatile Vector<Agent> neighbors;
	private Vector<Long> agentsId;
	private int lanes;
	private int height;
	private int width;
	private int xDistance;
	private boolean isValid;
	private volatile boolean isCrashed;
	
	public World(){
		this.agentsId = new Vector<Long>();
		this.agent = new Agent(generateID());
		this.neighbors = new Vector<Agent>();
		this.lanes = 0;
		this.height = 0;
		this.width = 0;
		this.isValid = false;
		this.isCrashed = false;
	}
	
	//Total number of lanes, including the lanes for bushes and auxiliary ones
	public void setLanes(int lanes){
		this.lanes = lanes;
	}
	
	//Height and Width of the Window
	public void setWindowDimensions(int height, int width){
		this.height = height;
		this.width = width;
	}
	
	//All variables must be gotten before it is initialized
	public void init() throws Exception{
		if(lanes == 0 || height == 0 || width == 0){
			System.out.println("Lanes : " + lanes + " Height : " + height + " Widht : " + width);
			throw new Exception("Assign value to variables before initializing the world!");
		}
		
		xDistance = width/lanes;
		System.out.println("Width : " + width + "\tLanes : " + lanes + "\tXDist : " + xDistance);
		this.isValid = true;
		
		//Initialize agent
		agent.setImage(ImageIO.read(new File(Global.AGENT_IMG)));
		agent.setXPos(xDistance*(lanes/2) + Global.AGENT_OFFSET);
		agent.setYPos((int) ((height/2)+(height*.02)));
		
		//Generate neighbors
		neighbors = generateNeighbors();
	}
	
	public Agent getAgent(){
		return agent;
	}
	
	public Vector<Agent> getNeighbors(){
		return neighbors;
	}
	
	//Reset Agent's position and all neighbors position
	public void reset() throws IOException{
		this.isCrashed = false;
		this.agent.setXPos(xDistance*(lanes/2) + Global.AGENT_OFFSET);
		this.agent.setYPos((int) ((height/2)+(height*.02)));
		this.agentsId.clear();
		this.neighbors.clear();
		generateNeighbors();
	}
	
	//Update the world variables
	public void Update(){
		//Don't do anything if it's not valid
		if(!isValid){
			return;
		}
		try{
			//Update coordinates
			updateNeighborsPosition();
			//If there are neighbors outside of frame, place them back
			removeOutOfRangeNeighbors();
			//Add more neighbors if there are coming above the frame
			placeBackNeighbors();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	//Update neighbors coordinates
	private void updateNeighborsPosition(){
		//Make the Other agents go down
		for(Agent neighbor : neighbors){
			double velocity = neighbor.getSpeed();
			double yPos = neighbor.getYPos();
			neighbor.setYPos((int) (yPos += velocity));
		}
	}
	
	//Updates the position of the neighbors that below the frame and cannot be seen by the user
	//Place these neighbors back into the track
	private void removeOutOfRangeNeighbors(){
		Vector<Agent> tempV = new Vector<Agent>();
		for(Agent neighbor : neighbors){
			double yPos = neighbor.getYPos();
			//Only keep neighbors that are in the frame or above it
			if(yPos <= Global.HEIGHT){
				tempV.add(neighbor);
			}
		}
		neighbors = tempV;
	}
	
	private void placeBackNeighbors() throws IOException{
		boolean generateMore = true;
		for(Agent neighbor : neighbors){
			double yPos = neighbor.getYPos();
			//Only generate new cars if there are none above the frame and the rest are below have window
			if(yPos < (Global.HEIGHT/2)){
				generateMore = false;
			}
		}
		
		//Add more neighbors
		if(generateMore){
			Vector<Agent> tempV = generateNeighbors();
			neighbors.addAll(tempV);
		}
	}
	
	public void speedUp(){
		for(Agent neighbor : neighbors){
			neighbor.setSpeed(neighbor.getSpeed()+1);
		}
	}
	
	public void slowDown(){
		for(Agent neighbor : neighbors){
			neighbor.setSpeed(neighbor.getSpeed()-1);
		}
	}
	
	public void turnRight(){
		double tempX = agent.getXPos();
		tempX += xDistance;
		//If I hit the wall, then stop and display that it is crashed
		if(tempX >= (xDistance*(lanes-1))){
			isCrashed = true;
		}
		agent.setXPos((int) tempX);
	}
	
	public void turnLeft(){
		double tempX = agent.getXPos();
		tempX -= xDistance;
		//If I hit the wall, then stop and display that it is crashed
		if(tempX < xDistance){
			isCrashed = true;
		}
		agent.setXPos((int) tempX);
	}
	
	//Check if it's crashed against a wall or agents
	public boolean isCrashed() throws Exception{
		return (isCrashed || isCrashedAgainst_X_Walls() || isCrashedAgaintAgent()) ? true : false;
	}
	
	public boolean isCrashedAgainst_X_Walls(){
		//If we already hit the wall, then don not compute anything
		if(isCrashed) return true;
		
		double tempX = agent.getXPos();
		boolean isCrashed = false;
		if (tempX < xDistance || tempX >= (xDistance*(lanes-1))){
			System.out.println("TempX : " + tempX + "\txDistance : " + xDistance);
			isCrashed = true;
		}
		return isCrashed;
	}
	
	//Return false because the Agent doesn't move up nor down
	public boolean isCrashedAgainst_Y_Walls(){
		return false;
	}
	
	//Compare that the distance of this agent to its neighbors is greater than collision Threshold
	public boolean isCrashedAgaintAgent() throws Exception{
		//Check for neighbors in range
		for(Agent neighbor : neighbors){
			//Only get the distances for neighbors above agent - ignore neighbors behind agent
			if((neighbor.getYPos()-Global.AGENT_HEIGHT) > agent.getYPos()) continue;
			//Get the Euclidean distance towards the neighbor
			if(getEuclideanDistance(agent.getXPos(), agent.getYPos(), neighbor.getXPos(), neighbor.getYPos()) < Global.COLLITION_THRESHOLD){
				return true;
			}
		}
		
		return false;
	}
	
	//Compare that the distance of this agent to its neighbors is greater than the threshold
	public boolean isObstacleInRange(double distance) throws Exception{
		if(distance < 0){
			throw new Exception("Error : Provide a positive distance!");
		}
		
		//Check for neighbors in range
		for(Agent neighbor : neighbors){
			//Only get the distances for neighbors above agent - ignore neighbors behind agent
			if((neighbor.getYPos()-Global.AGENT_HEIGHT) > agent.getYPos()) continue;
			//Get the Euclidean distance towards the neighbor
			if(getEuclideanDistance(agent.getXPos(), agent.getYPos(), neighbor.getXPos(), neighbor.getYPos()) < distance){
				return true;
			}
		}
		
		//Check for walls in range
		double agentX = agent.getXPos();
		//Add distance to the left wall, subtract distance from the right wall
		//Coordinates go from left to right, and agent is in the middle. Threshold like this works.
		if (agentX < (xDistance + distance) || agentX >= ((xDistance*(lanes-1)) - distance)){
			return true;
		}
		
		//If nothing is in range, then return false
		return false;
	}
	
	//Return a list of obstacles or open space around the agents within a distance
	public List<Obstacle> getObstacles(double distance) throws Exception{
		if(distance < 0){
			throw new Exception("Error : Provide a positive distance!");
		}
		
		//Create obstacles in a clock wise order starting at the agent's left
		//There should be 3 obstacles overall
		List<Obstacle> left = new LinkedList<Obstacle>();
		List<Obstacle> right = new LinkedList<Obstacle>();
		Obstacle center = null;
		
		//Get agents position
		for(Agent neighbor : neighbors){
			//Only get the distances for neighbors above agent - ignore neighbors behind agent
			if((neighbor.getYPos()-Global.AGENT_HEIGHT) > agent.getYPos()) continue;
			if(getEuclideanDistance(agent.getXPos(), agent.getYPos(), neighbor.getXPos(), neighbor.getYPos()) < distance){
				//Check if neighbor is at my right, left or front
				if(neighbor.getXPos() < agent.getXPos()){	//Left
					if(left.isEmpty()){
						System.out.println("Left : " + neighbor.toString());
						left.add(new Obstacle(Global.STAGE.NEIGHBOR, neighbor.getXPos(), neighbor.getYPos()));	//Get only one
					}
				}
				else if(neighbor.getXPos() > agent.getXPos()){	//Right
					if(right.isEmpty()){
						System.out.println("Right : " + neighbor.toString());
						right.add(new Obstacle(Global.STAGE.NEIGHBOR, neighbor.getXPos(), neighbor.getYPos()));	//Get only one
					}
				}
				else{	//Above Agent
					if(center == null){
						System.out.println("Center : " + neighbor.toString());
						center = new Obstacle(Global.STAGE.NEIGHBOR, neighbor.getXPos(), neighbor.getYPos());		//Only need the closest one on top
					}
					else{
						//If there'a another neighbor closer, then get it's Y position - otherwise ignore it
						if(neighbor.getYPos() < center.getYPos()){
							System.out.println("Center : " + neighbor.toString());
							center = new Obstacle(Global.STAGE.NEIGHBOR, neighbor.getXPos(), neighbor.getYPos());
						}
					}
				}
			}
		}
		
		//Add all obstacles into one vector
		List<Obstacle> obstacles = new LinkedList<Obstacle>();
			
		//If there's no agent at my left side, then most likely it could be a wall or nothing at all
		if(left.isEmpty()){
			if (agent.getXPos() < (xDistance + distance)){
				//For the wall, at the at the left
				obstacles.add(new Obstacle(Global.STAGE.WALL, xDistance, agent.getYPos()));
			}
			//There's free space
			else{
				//Subtract the agents x position to the distance since it is in the left side
				obstacles.add(new Obstacle(Global.STAGE.OPEN_SPACE, (agent.getXPos() - distance), agent.getYPos()));
			}
		}
		else{
			obstacles.add(left.get(0));
		}
		
		//If there's not agent on top, then declare it free space
		if(center == null){
			//Place at the same X position of the agent, and Y distance
			obstacles.add(new Obstacle(Global.STAGE.OPEN_SPACE, agent.getXPos(), (distance + agent.getYPos())));
		}
		else{
			obstacles.add(center);
		}
		
		//If there's no agent at my left side, then most likely it could be a wall
		if(right.isEmpty()){
			if (agent.getXPos() >= ((xDistance*(lanes-1)) - distance)){
				//For the wall, at the at the right
				obstacles.add(new Obstacle(Global.STAGE.WALL, xDistance*(lanes-1), agent.getYPos()));
			}
			//There's free space
			else{
				//add the agents x position to the distance since it is in the right side
				obstacles.add(new Obstacle(Global.STAGE.OPEN_SPACE, (agent.getXPos() + distance), agent.getYPos()));
			}
		}
		else{
			obstacles.add(right.get(0));
		}
		
		//Get the distance relative to the robot
		getRelativePositionToAgent(obstacles);
		
		return obstacles;
	}
	
	//Convert the position of all agents relative to the position of the agent
	private void getRelativePositionToAgent(List<Obstacle> obstacles){
		if(obstacles == null || obstacles.isEmpty()) return;
		
		double tempX = agent.getXPos();
		double tempY = agent.getYPos();
		
		for(Obstacle obstacle : obstacles){
			obstacle.setXPos(obstacle.getXPos() - tempX);
			obstacle.setYPos(tempY - obstacle.getYPos());
		}
	}
	
	//OLD_METHOD : NOT IN USE
	//This will return a vector with all the distances inside threshold
	//To avoid empty vectors use isInContanceWithAgent(true) before using this method
	public Vector<Double> getDistanceInThreshold(double threshold){
		Vector<Double> distances = new Vector<Double>();
		double[] distanceTemp = new double[Global.DIMENSIONS];
		for(Agent neighbor : neighbors){
			//Only get the distances for neighbors above agent - ignore neighbors behind agent
			if((neighbor.getYPos()-Global.AGENT_HEIGHT) > agent.getYPos()) continue;
			double distance = getEuclideanDistance(agent.getXPos(), agent.getYPos(), neighbor.getXPos(), neighbor.getYPos());
			if(distance < threshold){
				//If it's at my left, then place if first
				if(neighbor.getXPos() < agent.getXPos()){
					distanceTemp[0] = distance;
				}
				//Right lane, place it last
				else if(neighbor.getXPos() > agent.getXPos()){
					distanceTemp[2] = distance;
				}
				//Same lane, place it in the middle
				else{
					distanceTemp[1] = distance;
				}
			}
		}
		
		for(double d : distanceTemp){
			if(d == 0){
				distances.add(-25 - (Math.random() * ((-50 + 25) - 1))); //Add a negative random number
			}
			else{
				distances.add(d);
			}
		}
		return distances;
	}
	
	//OLD_METHOD : NOT IN USE
	//This will return a vector with all the distances inside threshold
	//To avoid empty vectors use isInContanceWithAgent(true) before using this method
	public Vector<Vector<Integer>> getPositionInThreshold(double threshold){
		Vector<Vector<Integer>> position = new Vector<Vector<Integer>>();
		for(Agent neighbor : neighbors){
			//Only get the distances for neighbors above agent - ignore neighbors behind agent
			if((neighbor.getYPos()-Global.AGENT_HEIGHT) > agent.getYPos()) continue;
			double distance = getEuclideanDistance(agent.getXPos(), agent.getYPos(), neighbor.getXPos(), neighbor.getYPos());
			if(distance < threshold){
				Vector<Integer> pos = new Vector<Integer>();
				//This must match the DIMENTIONS parameter in Globals
				pos.add(neighbor.getXPos());
				pos.add(neighbor.getYPos());
				position.add(pos);
			}
		}
		return position;
	}
	
	//There should be x amount of neighbors created for each lane
	//Every neighbor should have the same speed if in the same lane
	//Also, they should have a distance threshold
	private Vector<Agent> generateNeighbors() throws IOException{
		//Create a random number of neighbors
		Vector<Agent> neighborsTemp = new Vector<Agent>();
		Vector<Double> velocityVector = new Vector<Double>();
		//Iterate through every lane
		for(int i = 0; i < Global.TOTAL_LANES; i++){
			int xPos = (xDistance * (i+2))  + Global.AGENT_OFFSET;	//Ignore the first 2 lanes
			int yPos = -(Global.AGENT_HEIGHT + new Random().nextInt(100));
			//Make sure that every lane has a different velocity
			double speed = (new Random().nextDouble()*10)+5;
			while(velocityVector.contains(speed)){
				speed = (new Random().nextDouble()*10)+5;
			}
			velocityVector.add(speed);
			//Initialize agents for every lane
			for(int j = 0; j < Global.CARS_PER_LANE; j++){
				Agent neighbor = new Agent(generateID());
				neighbor.setImage(ImageIO.read(new File(Global.NEIGHBOR_IMG[new Random().nextInt(Global.NEIGHBOR_IMG.length)])));
				neighbor.setXPos(xPos);
				neighbor.setYPos(yPos);
				neighbor.setSpeed(speed);
				neighborsTemp.add(neighbor);
				double distNeighbor = Global.AGENT_HEIGHT + new Random().nextInt(700)+System.currentTimeMillis()%100;
				yPos -= distNeighbor;
			}
		}
		return neighborsTemp;
	}
	
	private double getEuclideanDistance(double x1, double y1, double x2, double y2){
		return Math.sqrt(Math.pow((x1-x2), 2) + Math.pow((y1-y2), 2));
	}
	
	private long generateID(){
		Long id;
		do{  //Generate Id's of range [90,000 - 50,000]
			id = 50000 + (long)(Math.random() * ((90000 - 50000) + 1));
		}while(agentsId.contains(id));
		agentsId.add(id);
		return id.longValue();
	}
}
