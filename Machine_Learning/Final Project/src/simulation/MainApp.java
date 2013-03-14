package simulation;

public class MainApp {

	private volatile SimulationGraphics simulationGraphics;
	private Controller controller;
	private SimulationFrame simulationFrame;
	
	public MainApp(String title) throws Exception{
		simulationGraphics = new SimulationGraphics();
		simulationFrame = new SimulationFrame(title, simulationGraphics);
		controller = new Controller(simulationGraphics);
	}
	
	public void init() throws Exception{
		simulationFrame.init();
		controller.init();
	}
	
	public void execute(){
		controller.start();
	}
	
	public static void main(String[] args){
		try{
			MainApp mainApp = new MainApp("Samuel E. Sanchez - Machine Learning");
			mainApp.init();
			mainApp.execute();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
