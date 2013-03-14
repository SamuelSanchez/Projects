package simulation;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

import simulation.tool.Global;



public class SimulationGraphics extends JPanel implements KeyListener{

	private static final long serialVersionUID = 592944910106369817L;
	private volatile World world;
	private Graphics2D graphics;
	private int lanes;				//This are the lanes that hold the other cars
	private int bushesDistance;
	private volatile boolean quit;
	private volatile Global.ACTION advise;
	private long startTime;
	private String demonstrations;
	private String crashes;
	private boolean isSupervisedMode;

	public SimulationGraphics() throws Exception{
		this.addKeyListener(this);
		this.setFocusable(true);
		this.lanes = Global.TOTAL_LANES;
		if(lanes < 1){
			throw new Exception("Error : Invalid number of lanes [ " + lanes + " ]");
		}
		this.world = null;
		this.lanes = lanes + 4; //2 for auxiliary roads and 2 for the trees
		this.bushesDistance = 20;
		this.quit = false;
		this.advise = Global.ACTION.KEEP_IN_LANE;
		this.startTime = System.currentTimeMillis()/1000;
		this.demonstrations = null;
		this.crashes = null;
		this.isSupervisedMode = true;
	}
	
	public boolean isSupervised(){
		return isSupervisedMode;
	}
	
	public void setTime(long time){
		startTime = time;
	}
	
	public void setDemonstration(String demonstration){
		this.demonstrations = demonstration;
	}
	
	public void setCrashes(String crashes){
		this.crashes = crashes;
	}
	
	public void setWorld(World world){
		this.world = world;
	}
	
	public World getWorld(){
		return world;
	}
	
	public synchronized boolean isQuit(){
		return quit;
	}
	
	public int getLanes(){
		return lanes;
	}
	
	//Reset advise
	public Global.ACTION getAdvise(){
		Global.ACTION temp = advise;
		advise = Global.ACTION.KEEP_IN_LANE;
		return temp;
	}
	
	protected void paintComponent(Graphics g) {
		this.graphics = (Graphics2D)g;
		paintBackground();
		paintBushes();
		paintWorld();
		paintStrings();
	}
	
	//Paint all lanes including the ones where the bushes will be located at
	private void paintBackground() {
		graphics.setColor(Color.black);
		int height = getHeight();
		int width = getWidth()/lanes;
		for (int pos = 0; pos < lanes; pos++) {
			int dist = pos*width;
			//Draw the lanes for the tree and add a vertical line to it
			if( (pos == 0) || (pos == lanes-1)){
				graphics.setColor(Color.green.darker());	
				graphics.fillRect(dist, 0, dist+width, height);
				graphics.setColor(Color.black);
				graphics.drawLine(dist, 0, dist, height);
			}
			//Draw the auxiliary lanes and add a vertical line to it
			else if((pos == 1) || (pos == lanes-2)) {
				graphics.setColor(Color.gray);
				graphics.fillRect(dist, 0, dist, height);
				graphics.setColor(Color.black);
				graphics.drawLine(dist, 0, dist, height);
			}
			//Leave all middle lanes, but draw the lines
			else{
				graphics.setColor(Color.white);
				graphics.fillRect(dist, 0, dist, height);
				graphics.setColor(Color.black);
				graphics.drawLine(dist, 0, dist, height);
			}
		}
	}
	
	//Paint the static bushes - should be dynamics images
	private void paintBushes(){
		float percent = 0.4f;
		graphics.setColor(Color.green.brighter());
		int height = getHeight()/bushesDistance;
		int width = (getWidth()/lanes);  //This is the width of a bush
		int rightBush = width;
		float diff = (float) (width*percent);
		width -= diff;
		Shape circle = null;
		int dist = 0;
		for(int b = 0; b < height; b++){
			dist += height*2;
			//Left Lane
			circle = new Ellipse2D.Float((width/2), dist, width/2, width/2);
			graphics.draw(circle);
			graphics.fill(circle);
			//Right Lane
			circle = new Ellipse2D.Float((width/2) + (rightBush*(lanes-1)), dist, width/2, width/2);
			graphics.draw(circle);
			graphics.fill(circle);
		}
	}
	
	//Paint all objects from the world
	private void paintWorld(){
		//Avoid errors
		if(world == null) return;
		
		//Paint the agent
		graphics.drawImage(world.getAgent().getImage(), world.getAgent().getXPos().intValue(), world.getAgent().getYPos().intValue(), Global.AGENT_WIDTH, Global.AGENT_HEIGHT, this);
		for(Agent neighbor : world.getNeighbors()){
			graphics.drawImage(neighbor.getImage(),neighbor.getXPos().intValue(),neighbor.getYPos().intValue(), Global.AGENT_WIDTH, Global.AGENT_HEIGHT, this);
		}
		graphics.setColor(Color.black);
	}
	
	private void paintStrings(){
		//paint the time
		Font font = new Font("SansSerif", Font.BOLD, 12);
		graphics.setFont(font);
		graphics.setColor(Color.white);
		graphics.drawString(Global.getTimeDifference(startTime), 5, 20);
		if(demonstrations != null){
			graphics.drawString(demonstrations, 5, 40);
		}
		if(crashes != null){
			graphics.drawString(crashes, 5, 60);
		}
	}
	
	public void keyTyped(KeyEvent paramKeyEvent) {
		/*Do nothing*/
	}

	//TODO: Give more keyboards actions such as {Debug, Reset, On, Off}
	public void keyPressed(KeyEvent event) {
		if(world == null) return;
		int value = event.getKeyCode();
		
		switch(value){
			case 37: // <- is pressed
				advise = Global.ACTION.GO_LEFT;
				this.world.turnLeft();
				break;
			case 38: //Speed Up
				advise = Global.ACTION.KEEP_IN_LANE;
				this.world.speedUp();
				break;
			case 39: // -> is pressed
				advise = Global.ACTION.GO_RIGHT;
				this.world.turnRight();
				break;
			case 40: //Slow Down
				advise = Global.ACTION.KEEP_IN_LANE;
				this.world.slowDown();
				break;
			case 81: //'q' is pressed
				this.quit = true;
				break;
			case 76:
				isSupervisedMode = !isSupervisedMode;
				System.out.println("Supervised mode : 	" + (isSupervisedMode ? "On" : "Off"));
				break;
			default:
				advise = Global.ACTION.KEEP_IN_LANE;
				break;
		}
		
		if(Global.DEBUG){
			System.out.println("User pressed key [" + event.getKeyChar() + "] - key value [" + value + "]" );
		}
	}

	public void keyReleased(KeyEvent event) {
		/*Do nothing*/
	}
}
