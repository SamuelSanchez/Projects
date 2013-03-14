package simulation;
import java.util.List;
import javax.swing.JOptionPane;

import simulation.learning.Action;
import simulation.learning.Classifier;
import simulation.learning.Obstacle;
import simulation.learning.naiveBayes.NaiveBayes;
import simulation.tool.Data;
import simulation.tool.Global;


public class Controller extends Thread{

	private volatile World world;
	private volatile SimulationGraphics simulationGraphics;
	private Classifier classifier;
	private int demonstrations;
	private int crashes;
	private Data data;
	
	public Controller(SimulationGraphics simulationGraphics){
		this.simulationGraphics = simulationGraphics;
		this.world = new World();
		this.classifier = new NaiveBayes();
		this.demonstrations = 0;
		this.crashes = 0;
		this.data = new Data();
	}

	public void init() throws Exception{
		world.setWindowDimensions(Global.HEIGHT, Global.WIDTH);
		world.setLanes(simulationGraphics.getLanes());
		simulationGraphics.setWorld(world);
		world.init();
	}
	
	public void run(){
		Global.ACTION goTowardsDirection = Global.ACTION.KEEP_IN_LANE;
		while(!simulationGraphics.isQuit()){
			try{
				if(Global.DEBUG){
					System.out.println("---------------------------[Update]---------------------------");
				}
				//Update the world components
				world.Update();
				//Paint Demonstrations obtained
				simulationGraphics.setDemonstration("Demonstr : " + demonstrations);
				simulationGraphics.setCrashes("Crashes : " + crashes);
				
				goTowardsDirection = Global.ACTION.KEEP_IN_LANE;
				
				//Request demonstrations if agents are inside threshold (safety zone)
				if(world.isObstacleInRange(Global.REQUEST_MIN_DISTANCE)){
					//Get all obstacles that are within a threshold
					List<Obstacle> obstacles = world.getObstacles(Global.REQUEST_MAX_DISTANCE);
					Action action = classifier.getAdvise(obstacles);

					if(!simulationGraphics.isSupervised()){
						if(demonstrations > Global.TRAINING_SIZE){
							//If it's in supervised mode then ask agent when needed
							if(action.getConfidence() == 0){
								goTowardsDirection = requestAdvise();
								classifier.addTrainingData(obstacles, goTowardsDirection);
								demonstrations++;
							}else{
								goTowardsDirection = action.getAction();
							}
						}else{
							goTowardsDirection = requestAdvise();
							classifier.addTrainingData(obstacles, goTowardsDirection);
							demonstrations++;
						}
					}
					else{
						//If the action advise by the algorithm is less than the threshold, then request a demonstration
						if(action.getConfidence() < Global.CONFIDENCE_THRESHOLD){
							goTowardsDirection = requestAdvise();
							classifier.addTrainingData(obstacles, goTowardsDirection);
							demonstrations++;
						}
						//If the advise has a good confidence value, then perform it
						else{
							System.out.println("Agent has a good confidence [ " + action.getConfidence() + " ]");
							goTowardsDirection = action.getAction();
							if(Global.DEBUG){
								System.out.println("Agent Move : " + goTowardsDirection);
							}
						}
					}
					
					//Write data down
					data.addData(demonstrations, action.getConfidence(), goTowardsDirection.toString());
				}
				
				//Move the agent
				moveAgent(goTowardsDirection);
				
				//Draw agents
				simulationGraphics.repaint();
				
				//If the agent is crashed, display to the user and let him know that we crashed then reset
				if(world.isCrashed()){
					JOptionPane.showMessageDialog(null, "You have crashed!");
					try{
						world.reset();
						//simulationGraphics.setTime(System.currentTimeMillis()/1000);
						crashes++;
						
						//Write crashes to file
						data.addCrash(demonstrations);
					}catch(Exception e){
						e.printStackTrace();
						break;
					}
				}
				
				//Slow the simulation
				try{
					Thread.sleep(Global.SLEEP_TIME);
				}catch(Exception e){
					if(Global.DEBUG){ e.printStackTrace(); }
				}
			}catch(Exception e){
				if(Global.DEBUG) { e.printStackTrace(); }
			}
		}
		
		//Write data to file
		data.writeData();
		
		//Exit the program
		System.exit(0);
	}
	
	private void moveAgent(Global.ACTION direction){
		switch(direction){
			case GO_LEFT:
				world.turnLeft();
				break;
			case GO_RIGHT:
				world.turnRight();
				break;
			case KEEP_IN_LANE:
				/*Do nothing*/
				break;
			default:
				/*Do nothing*/
				break;
		}
	}
	
	private Global.ACTION requestAdvise(){
		String[] choices = { "Left ", "Forward", "Right" };
		int response = JOptionPane.showOptionDialog( null, "Where should I move?",  "Provide advise",  0,  -1,   null, choices,  null);
		Global.ACTION action = null;
		
		switch(response){
			case 0:
				action = Global.ACTION.GO_LEFT;
				break;
			case 1:
				action = Global.ACTION.KEEP_IN_LANE;
				break;
			case 2:
				action = Global.ACTION.GO_RIGHT;
				break;
			default:
				action = Global.ACTION.UNKNOWN;
				break;
		}
		return action;
	}
}
