package stockretrieval;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class mainApp {
	
	public static void displayUsage(){
		System.err.println("Usage : mainApp <file>\n\t-<file> is where the stocks/companies names are stored");
	}
	
	public static void main(String[] args){
		try{
			StockDownloader stockDownloader = new StockDownloader();
			
			if(args.length < 1){
				displayUsage();
				return;
			}
			
			//Read file
			BufferedReader file = new BufferedReader(new FileReader(new File(args[0])));
			String line = file.readLine();
			
			//Parse and retrieve information
			while(line != null){
				if(line.trim().isEmpty() || line.startsWith("//")){
					line = file.readLine();
					continue;
				}
				//Split by white space
				String[] parts = line.split("\\s+?");
				if(parts.length < 2){
					line = file.readLine();
					continue;
				}
				//Store data to retrieve
				if(parts[0].equalsIgnoreCase("Symbol")){
					stockDownloader.addStockBySymbol(parts[1]);
				}
				else if(parts[0].equalsIgnoreCase("Name")){
					stockDownloader.addStockByName(parts[1]);
				}
				
				line = file.readLine();
			}
			
			//Run program during trading time and retrieve/store stock data
			stockDownloader.update();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
