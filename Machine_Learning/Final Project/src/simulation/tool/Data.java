package simulation.tool;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Data {

	private BufferedWriter bufferWriter;
	private static final String filename = "data.csv";
	private boolean canWrite;
	private List<Integer> demonstrationsList;
	private List<Double> confidenceList;
	private List<String> directionsList;
	private List<Integer> crashesList;
	
	public Data(){
		try {
			demonstrationsList = new LinkedList<Integer>();
			confidenceList = new LinkedList<Double>();
			directionsList = new LinkedList<String>();
			crashesList = new LinkedList<Integer>();
			bufferWriter = new BufferedWriter(new FileWriter(filename));
			canWrite = true;
		} catch (IOException e) {
			canWrite = false;
			e.printStackTrace();
		}
	}
	
	public void addData(int demonstration, double confidence, String direction){
		//Don't keep data and waste heap space if it is not possible to write data
		if(!canWrite) return;
		
		demonstrationsList.add(demonstration);
		confidenceList.add(confidence);
		directionsList.add(direction);
	}
	
	public void addCrash(int demonstration){
		//Don't keep anything if it's not possible to write data
		if(!canWrite) return;
		
		crashesList.add(demonstration);
	}
	
	public void writeData(){
		//Don't compute since there's not file to write neither data
		if(!canWrite) return;
		
		//If they don't have the same amount then don't compute
		if(demonstrationsList.size() != confidenceList.size() && demonstrationsList.size() != directionsList.size() && demonstrationsList.size() != crashesList.size()){
			return;
		}
		
		int size = demonstrationsList.size();
		//Write the Confidence and demonstration
		try {
			bufferWriter.write("\n");
			bufferWriter.write("Confidence, Demonstrations\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		for(int i = 0; i < size; i++){
			try {
				bufferWriter.write(confidenceList.get(i) + ", " + demonstrationsList.get(i) + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Write Direction and Demonstrations
		try {
			bufferWriter.write("\n\n\n");
			bufferWriter.write("Direction, Demonstrations\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		for(int i = 0; i < size; i++){
			try {
				bufferWriter.write(directionsList.get(i) + ", " + demonstrationsList.get(i) + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Write Crashes
		try {
			bufferWriter.write("\n\n\n");
			bufferWriter.write("Crashes on Demonstration\n");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		for(int i = 0; i < crashesList.size(); i++){
			try {
				bufferWriter.write(crashesList.get(i) + "\n");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//Close stream
		try {
			bufferWriter.flush();
			bufferWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
