package stockretrieval;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.LinkedList;

public class StockDownloader {

	private WebConnector webConnector;			//Interfaces with the Internet to download xml data
	private StockDao stockDao;					//Interfaces with the database
	private List<String> stockList;				//Stock quotes to be retrieved - does not reset
	private List<String> stockXMLList;			//XML data retrieved from the web for each stock in stockList - resets every update
	private List<Stock> stocks;					//Stock objects create for each stock in stockDataList - resets every update
	private StringBuffer errors;
	private static final String ERROR_FOLDER = "errors";
	
	public StockDownloader() throws Exception{
		this.webConnector = WebConnector.MOZILLA;
		this.stockDao = new StockDaoJdbc();
		this.stockDao.usePrimaryDatabase(); //User primary database - throw an exception if invalid
		this.stockList = new LinkedList<String>();
		this.stockXMLList = new LinkedList<String>();
		this.stocks = new LinkedList<Stock>();
		this.errors = new StringBuffer();
	}
	
	//If the stock symbol is valid then add it to the list
	public void addStockBySymbol(String symbol){
		if(!stockDao.isSymbolValid(symbol)){
			errors.append("[ " + Global.getTimeString() + " ] Error : Could not retrieve Symbol[ " + symbol + " ]" + Global.NEWLINE);
			return;
		}
		stockList.add(symbol);
	}
	
	//If the stock name is valid then add its symbol to the list
	public void addStockByName(String name){
		if(!stockDao.isNameValid(name)){
			errors.append("[ " + Global.getTimeString() + " ] Error : Could not retrieve Name[ " + name + " ]" + Global.NEWLINE);
			return;
		}
		stockList.add(stockDao.getSymbolByName(name));
	}
	
	//Retrieve stock every minute from the stock list between regular trading time
	public void update(){
		if(Global.DEBUG){
			System.out.println("Current time : " + Global.getTimeString());
		}
		//Only run if the current time is between 9:30 am and 4:00 pm
		while(!Global.isBefore(Global.getTime(), Global.getTime(9,30,00)) && Global.isBefore(Global.getTime(), Global.getTime(16,00,00))){
			try{
				//Retrieve and store stock data into database
				retrieveStockData();
				generateStockfromXML();
				storeStockData();
				//Sleep one minute
				try{
					Thread.sleep(60000);
				}catch(Exception e){
					/* Do nothing */
				}
			}catch(Exception e){
				//Catch all the errors that might appear
				errors.append("[ " + Global.getTimeString() + " ] Error : " + e.toString() + Global.NEWLINE);
				//Sleep one minute
				try{ Thread.sleep(60000); }catch(Exception t){/* Do nothing */}
			}
		}
		
		//Write all errors to a file and disconnect from database
		//Print the error to the console if there's an error when writing the data to the file
		try{
			//Only write the file, if there are errors
			if(!errors.toString().isEmpty()){
				File errorsFolder = new File(ERROR_FOLDER);
				if(!errorsFolder.exists()) errorsFolder.mkdir();
				BufferedWriter buffer = new BufferedWriter(new FileWriter(ERROR_FOLDER + "/" + Global.getDateString().trim() + ".txt"));
				buffer.write(errors.toString());
				buffer.close();
			}
			//Close Database Connection
			stockDao.Disconnect();
		}catch(Exception e){
			System.out.println("Error : " + e.toString());
		}
	}
	
	//Connect to the web in order to retrieve stock data
	private void retrieveStockData(){
		if(Global.DEBUG){
			System.out.println("RetrieveStockData");
		}
		//Clear the previous stock data list
		stockXMLList.clear();
		
		for(String quote : stockList){
			try{
				if(Global.DEBUG){
					System.out.println("Quote : " + quote);
				}
				//Retrieve data from the web and add it into the stock data list
				if(webConnector.Connect(Global.createURLforQuote(quote))){
					stockXMLList.add(webConnector.getInput());
				}else{ //Write as an error if couldn't connect to the web-site
					errors.append("[ " + Global.getTimeString() + " ] Error : Could not connect to the website [ " + Global.createURLforQuote(quote) + " ]" + Global.NEWLINE);
				}
			}catch(Exception e){
				//Catch all the errors that might appear when retrieve data from the web
				errors.append("[ " + Global.getTimeString() + " ] Error : " + e.toString() + Global.NEWLINE);
			}
		}
	}
	
	//Generate stock objects from the xml obtained from the web
	private void generateStockfromXML(){
		if(Global.DEBUG){
			System.out.println("generateStockfromXML");
		}
		//Clear the previous stocks
		stocks.clear();
		
		for(String xml : stockXMLList){
			try{
				if(Global.DEBUG){
					System.out.println("XML : " + xml);
				}
				Stock stock = StockParser.generateStockFromXML(xml);
				if(stock != null){
					stocks.add(stock);
				}else{ //Write as an error if couldn't generate the stock from xml
					errors.append("[ " + Global.getTimeString() + " ] Error : Could not generate stock from xlm [ " + xml + " ]" + Global.NEWLINE);
				}
			}catch(Exception e){
				//Catch all the errors that might appear when generating stock objects from xml
				errors.append("[ " + Global.getTimeString() + " ] Error : " + e.toString() + Global.NEWLINE);
			}
		}
	}
	
	//Stores the stocks into the database
	private void storeStockData() throws Exception{
		if(Global.DEBUG){
			System.out.println("storeStockData");
		}
		for(Stock stock : stocks){
			try{
				if(Global.DEBUG){
					System.out.println("Stock : " + stock.toString());
				}
				stockDao.put(stock);
			}catch(Exception e){
				//Catch all the errors that might appear with the database
				errors.append("[ " + Global.getTimeString() + " ] Error : " + e.toString() + Global.NEWLINE);
			}
		}
		stockDao.executeBatch();
	}
}