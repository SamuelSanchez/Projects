//package simulation;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Vector;
//import javax.swing.JOptionPane;
//
//import simulation.learning.gmm.GaussianMixture;
//import simulation.tool.Global;
//
//public class ControllerBack extends Thread{
//
//	private volatile World world;
//	private volatile SimulationGraphics simulationGraphics;
//	private GaussianMixture gmm;
//	private Map<Policy, Global.ACTION> policyMap;
//	private boolean findGMMFirstTime;
//	private double[][] data_points;
//	private int actualTrainingSize;
//	
//	public ControllerBack(SimulationGraphics simulationGraphics){
//		this.simulationGraphics = simulationGraphics;
//		this.world = new World();
//		this.gmm = null;
//		this.policyMap = new HashMap<Policy, Global.ACTION>();
//		this.findGMMFirstTime = true;
//		this.data_points = new double[Global.TRAINING_SIZE][Global.DIMENSIONS];
//		this.actualTrainingSize = 0;
//	}
//
//	public void init() throws Exception{
//		world.setWindowDimensions(Global.HEIGHT, Global.WIDTH);
//		world.setLanes(simulationGraphics.getLanes());
//		simulationGraphics.setWorld(world);
//		world.init();
//	}
//	
//	public void run(){
//		while(!simulationGraphics.isQuit()){
//			try{
//				//Update the world components
//				world.Update();
//				
//				//Request demonstrations if agents are inside threshold
//				if(world.isObstacleInRange(Global.REQUEST_MAX_DISTANCE)){
//					performedAction();
//				}
//				
//				//Draw agents
//				simulationGraphics.repaint();
//				
//				//If the agent is crashed, display to the user and let him know that we crashed then reset
//				if(world.isCrashedAgainst_X_Walls() || world.isCrashedAgaintAgent()){
//					JOptionPane.showMessageDialog(null, "You have crashed!");
//					try{
//						world.reset();
//						policyMap.clear();
//						findGMMFirstTime = true;
//						gmm = null;
//					}catch(Exception e){
//						e.printStackTrace();
//						break;
//					}
//				}
//				
//				//Slow the simulation for 1 second
//				try{
//					Thread.sleep(Global.SLEEP_TIME);
//				}catch(Exception e){
//					if(Global.DEBUG){ e.printStackTrace(); }
//				}
//			}catch(Exception e){
//				if(Global.DEBUG) { e.printStackTrace(); }
//			}
//		}
//		
//		//Exit the program
//		System.exit(0);
//	}
//	
//	//If there's a close agent
//	private void performedAction() throws Exception{
//		Global.ACTION advise = Global.ACTION.KEEP_IN_LANE;
//		double[][] data = new double[1][Global.DIMENSIONS];
//		Vector<Double> position = world.getDistanceInThreshold(Global.REQUEST_MAX_DISTANCE);
//		if(position.size() != 3){
//			throw new Exception("Dimentions must match @performedAction()");
//		}
//		//Convert to array
//		for(int i = 0; i < position.size(); i++){
//			data[0][i] = position.get(i)/100;	//To avoid problems with the GMM - reduce the size of the data
//		}
//
//		//Policy
//		Policy policy = new Policy(data[0][0], data[0][1], data[0][2]);
//		
//		//Add points to the training data
//		if(actualTrainingSize < Global.TRAINING_SIZE){
//			data_points[actualTrainingSize] = data[0];
//			actualTrainingSize++;
//			
//			//Add advice into map
//			policyMap.put(policy, simulationGraphics.getAdvise());
//			System.out.println("Adding training data : " + simulationGraphics.getAdvise() + "\tPolicy : " + policy);
//			
//		}
//		else{//If there are enough data points, then initiate the program for the first time
//			if(findGMMFirstTime){
//				System.out.println("************************************************************************************************");
//				findGMMFirstTime = false;
//				this.gmm = new GaussianMixture(3, data_points, false, false); //Three clusters
//				gmm.init();
//				//Find the EM
//				gmm.find_EM();
//			}
//		
//			if(policyMap.containsKey(policy)){
//				System.out.println("#############################################################");
//				System.out.println("Getting from the map");
//				advise = policyMap.get(policy);
//			}
//			else{
//				System.out.println("Adding to the map");
//				advise = requestAdvise();
//				
//				policyMap.put(policy, advise);
//			}
//				
//			System.out.println("Advise : " + advise);
//			
//			//Make a turn;
//			switch(advise){
//				case GO_LEFT: //Turn Left
//					world.turnLeft();
//					break;
//				case KEEP_IN_LANE: //Move Forward
//					/* Do nothing */ 
//					break;
//				case GO_RIGHT: //Move Right
//					world.turnRight();
//					break;
//				default:
//					System.out.println("What is this case : " + advise);
//					break;
//			}
//		}
//	}
//	
//	//If there's a close agent
//	@SuppressWarnings("unused")
//	private void performedActionOld() throws Exception{
//		double likehood = 0;
//		Global.ACTION advise = Global.ACTION.KEEP_IN_LANE;
//		double[][] data = new double[1][Global.DIMENSIONS];
//		Vector<Double> position = world.getDistanceInThreshold(Global.REQUEST_MIN_DISTANCE);
//		if(position.size() != 3){
//			throw new Exception("Dimentions must match @performedAction()");
//		}
//		//Convert to array
//		for(int i = 0; i < position.size(); i++){
//			data[0][i] = position.get(i)/100;	//To avoid problems with the GMM - reduce the size of the data
//		}
//
//		//Policy
//		Policy policy = new Policy(data[0][0], data[0][1], data[0][2]);
//		
//		//Add points to the training data
//		if(actualTrainingSize < Global.TRAINING_SIZE){
//			data_points[actualTrainingSize] = data[0];
//			actualTrainingSize++;
//			
//			//Add advice into map
//			policyMap.put(policy, simulationGraphics.getAdvise());
//			System.out.println("Adding training data : " + simulationGraphics.getAdvise() + "\tPolicy : " + policy);
//			
//		}
//		else{//If there are enough data points, then initiate the program for the first time
//			if(findGMMFirstTime){
//				System.out.println("************************************************************************************************");
//				this.gmm = new GaussianMixture(3, data_points, false, false); //Three clusters
//				gmm.init();
//				//Find the EM
//				gmm.find_EM();
//			}
//			likehood = gmm.findlikehood(data);
//			System.out.println("Likehood : " + likehood + "\t < Confidence : " + Global.CONFIDENCE_THRESHOLD + "\t?" + (likehood < Global.CONFIDENCE_THRESHOLD));
//		
//			//If the likelihood is greater than my confidence threshold, then request demonstration
//			if(likehood < Global.CONFIDENCE_THRESHOLD){
//				System.out.println("We dont have a good confidence");
//				advise = requestAdvise();
//				
//				//Add advice into map
//				policyMap.put(policy, advise);
//				gmm.addDataPoints(data);
//				gmm.find_EM();
//			}
//			//Our confidence is good, then let's see if we learned this already
//			else{
//				System.out.println("We have a good confidence!");
//				if(!policyMap.containsKey(policy)){
//					System.out.println("Adding to the map");
//					advise = requestAdvise();
//					
//					policyMap.put(policy, advise);
//					
//					System.out.println("Print data from the map : -----------------");
//					for(Policy pol : policyMap.keySet()){
//						System.out.println("Policy : " + pol);
//					}
//					System.out.println("------------------------------------");
//				}
//				else{
//					System.out.println("Getting from the map");
//					advise = policyMap.get(policy);
//				}
//			}
//			
//			System.out.println("Advise : " + advise);
//			
//			//Make a turn;
//			switch(advise){
//				case GO_LEFT: //Turn Left
//					world.turnLeft();
//					break;
//				case KEEP_IN_LANE: //Move Forward
//					/* Do nothing */ 
//					break;
//				case GO_RIGHT: //Move Right
//					world.turnRight();
//					break;
//				default:
//					System.out.println("What is this case : " + advise);
//					break;
//			}
//		}
//	}
//	
//	private Global.ACTION requestAdvise(){
//		String[] choices = { "Left ", "Forward", "Right" };
//		int response = JOptionPane.showOptionDialog( null, "Where should I move?",  "Provide advise",  0,  -1,   null, choices,  null);
//		Global.ACTION action = null;
//		
//		switch(response){
//			case 0:
//				action = Global.ACTION.GO_LEFT;
//				break;
//			case 1:
//				action = Global.ACTION.KEEP_IN_LANE;
//				break;
//			case 2:
//				action = Global.ACTION.GO_RIGHT;
//				break;
//			default:
//				action = Global.ACTION.UNKNOWN;
//				break;
//		}
//		return action;
//	}
//	
//	private class Policy{
//		double d1 = 0;
//		double d2 = 0;
//		double d3 = 0;
//		public static final double THRESHOLD = 0.5;
//		
//		public Policy(double d1, double d2, double d3){
//			this.d1 = d1;
//			this.d2 = d2;
//			this.d3 = d3;
//		}
//		
//		public String toString(){
//			return ("d1 : " + d1 + "\td2 : " + d2 + "\td3 : " + d3);
//		}
//		
//		public boolean equals(Object obj){
//			if(obj == null || !(obj instanceof Policy)) return false;
//			boolean isCopy = false;
//			double temp1  = 0;
//			double temp2 = 0;
//			double temp3 = 0;
//			
//			if(d1 < 0 || ((Policy)obj).d1 < 0){
//				temp1 = 0;
//			}
//			else{
//				temp1 = Math.abs(d1-((Policy)obj).d1);
//			}
//			if(d2 < 0 || ((Policy)obj).d2 < 0){
//				temp2 = 0;
//			}
//			else{
//				temp2 = Math.abs(d2-((Policy)obj).d2);
//			}
//			
//			if(d3 < 0 || ((Policy)obj).d3 < 0){
//				temp3 = 0;
//			}
//			else{
//				temp3 = Math.abs(d3-((Policy)obj).d3);
//			}
//			
//			System.out.println("Mine - d1 : " + d1 + "\td2 : " + d2 + "\td3 : " + d3);
//			System.out.println("Neig - d1 : " + ((Policy)obj).d1 + "\td2 : " + ((Policy)obj).d2 + "\td3 : " + ((Policy)obj).d3);
//			System.out.println("Diff - t1 : " + temp1 + "\tt2 : " + temp2 + "\tt3 : " + temp3);
//			System.out.println("Threshold : " + THRESHOLD);
//			
//			if(temp1 < THRESHOLD && temp2 < THRESHOLD && temp3 < THRESHOLD){
//				isCopy = true;
//			}
//			System.out.println("Boolean : " + isCopy);
//			return isCopy;
//		}
//		
//		public int hashCode() {  
//		    int hash = (int) (d1 < 0 ? 0 : d1) * 10000;  
//		    hash += (((int) (d2 < 0 ? 0 : d2)) * 1000);  
//		    hash += (((int) (d3 < 0 ? 0 : d3)) * 100);  
//		    
//		    System.out.println("Hash : " + hash);
//		    return hash;  
//		  } 
//	}
//}
