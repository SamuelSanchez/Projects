package webretrieval;

/**
 * This is the main class of the program that will run the GUI.
 * The GUI interacts with all the other programs.
 * 
 * @author Samuel E. Sanchez
 *
 */
public class MainApp {
	public static void main(String[] args){
		try{
			WebPageRetrievalGUI gui = new WebPageRetrievalGUI();
			gui.startGUI();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
