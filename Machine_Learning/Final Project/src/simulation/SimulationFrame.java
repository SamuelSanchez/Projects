package simulation;
import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;

import simulation.tool.Global;



public class SimulationFrame extends JFrame{ 

	private static final long serialVersionUID = -219229270024286427L;
	private volatile SimulationGraphics simulation;
	private Container container;
	private String title;
	
	public SimulationFrame(String title, SimulationGraphics simulationGraphics) throws Exception{
		this.title = title;
		this.simulation = simulationGraphics;
	}
	
	public void init(){
		initFrame();
		displaySimulation();
	}
	
	private void initFrame(){
		this.container = this.getContentPane();
		this.setTitle(title);
		container.setLayout(new BorderLayout());
		this.setSize(Global.WIDTH, Global.HEIGHT);
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setContentPane(container);
	}
	
	private void displaySimulation(){
		container.add(simulation, BorderLayout.CENTER);
		this.simulation.setFocusable(true);
	}
}
