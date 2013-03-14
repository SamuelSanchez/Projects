package simulation.tool;

public class Global {
	//Global specifics
	public static final boolean DEBUG = true;
	
	//Window specifics
	public static final int WIDTH = 400;
	public static final int HEIGHT = 700;

	//World specifics
	public static final int TOTAL_LANES = 3;
	public static final int CARS_PER_LANE = 2;
	public static final int SLEEP_TIME = 350;
	public static final int LINE_WIDTH = (WIDTH/(TOTAL_LANES+4)); //4 : 2 for the trees and 2 for auxiliary roads
	
	//Agent specific data
	public static final int AGENT_HEIGHT = (HEIGHT/17);
	public static final int AGENT_WIDTH = (WIDTH/17);
	public static final int AGENT_OFFSET = (LINE_WIDTH - AGENT_WIDTH)/2;   //This is to place the agent in the center of a lane int the GUI
	public static final String AGENT_IMG = "img/car_03.jpg";
	public static final String[] NEIGHBOR_IMG = { "img/car_01.png", "img/car_02.jpg", "img/car_04.jpg", "img/car_02.jpg", "img/truck_01.png" };
	
	//Data information for the learning algorithm
	public static final int TRAINING_SIZE = 25;
	public static final int DIMENSIONS = 3;
	public static final double CONFIDENCE_THRESHOLD = 0.005;
	
	//Distance information
	public static final double COLLITION_THRESHOLD = AGENT_HEIGHT;	//This is a circumference with the car height (longer position) from the center of this car to the center of another agent
	public static final double REQUEST_MIN_DISTANCE = AGENT_HEIGHT*1.3;
	public static final double REQUEST_MAX_DISTANCE = AGENT_HEIGHT*1.8;
	
	//Agent Actions
	public static enum ACTION { UNKNOWN, GO_LEFT, KEEP_IN_LANE, GO_RIGHT };
	
	//Obstacles
	public static enum STAGE { UNKNOWN, OPEN_SPACE, NEIGHBOR, WALL };
	
	public static String getTimeDifference(long time){
		long timeDiff = System.currentTimeMillis()/1000;
		timeDiff -= time;
		int hour = (int) timeDiff/3600;
		timeDiff = timeDiff % 3600;
		int minutes = (int) timeDiff/60;
		timeDiff = timeDiff % 60;
		int seconds = (int) timeDiff;
		return (hour < 10 ? "0" : "") + hour + ":" + (minutes < 10 ? "0" : "") + minutes + ":" + (seconds < 10 ? "0" : "") + seconds;
	}
}
