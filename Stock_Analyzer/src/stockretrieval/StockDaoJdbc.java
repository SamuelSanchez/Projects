package stockretrieval;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringEscapeUtils;

public final class StockDaoJdbc extends JdbcConnection implements StockDao{
	
	 private static final String CREATE_TABLE = "create table if not exists " + "?" + 
					       	  " (Date DATE not null," +
					       	  "Time varchar(8) not null," +
					       	  "Open FLOAT not null," +
					       	  "High FLOAT not null," +
							  "Low  FLOAT not null," +
							  "Close FLOAT not null," +
							  "Volume INT not null," +
							  "Amount_Change  FLOAT not null, " +
							  "Percent_Change FLOAT not null," +
							  "Last_Close FLOAT not null," +
							  "Average_Volume INT not null," +
							  "Market_Capitalization FLOAT not null, " +
							  "PRIMARY KEY (Date, Time));";
	
	private static final String INSERT = "INSERT INTO " + "?" +
			" (Date, Time, Open, High, Low, Close, Volume, Amount_Change, Percent_Change, Last_Close, Average_Volume, Market_Capitalization)" +
			" VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
	
	private static final String WHERE = " WHERE 1 = 1 and ";
	
	public static final String SELECT = "SELECT Symbol, Name, Exchange, IPO_YEAR, Sector, Industry, Summary_Quote" +
					 " FROM " + DatabaseConstants.COMPANIES_TABLE + WHERE;
		
	public StockDaoJdbc() throws Exception{
		super();
	}
	
	public void createUsePrimaryDatabase() throws SQLException{
		//Select Database and table
	     statement.executeUpdate( DatabaseConstants.CREATE_DATABASE );
	     statement.executeUpdate( DatabaseConstants.USE_DATABASE );
	}
	
	public void usePrimaryDatabase() throws SQLException{
		statement.executeUpdate( DatabaseConstants.USE_DATABASE );
	}
	
	//Check if the symbol is valid - must be the right symbol
	public boolean isSymbolValid(String symbol){
		return searchWord("Symbol", symbol, DatabaseConstants.EQUAL);
	}
	
	//Check if the word is valid - could be partial start name
	public boolean isNameValid(String name){
		return searchWord("Name", name+"%", DatabaseConstants.LIKE);
	}
	
	//Search only one field
	private boolean searchWord(String columnName, String fieldName, String operator){
		boolean isValid = false;
		try{
			//Prevent SQL Injection
			String Query = CompanyInformationJdbc.SELECT + columnName + operator + "'" + StringEscapeUtils.escapeJava(fieldName) + "';";
			ResultSet result = statement.executeQuery(Query);
			//Matches were found!
			if(result.next()){
				isValid = true;
				//Display first Match to console
				if(Global.DEBUG){
					ResultSetMetaData meta = result.getMetaData();
					//Print Columns Name
					System.out.println("Query : " + Query);
					for(int c = 1; c <= meta.getColumnCount(); c++){
						System.out.print(meta.getColumnName(c) + "\t");
					}
					System.out.println();
					//Print result
					for(int c = 1; c <= meta.getColumnCount(); c++){
						System.out.print(result.getObject(c) + "\t");
					}
					System.out.println();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return isValid;
	}
	
	//The name given to the query could be part of it
	public String getSymbolByName(String name){
		return getWord("Symbol", "Name", name+"%", DatabaseConstants.LIKE);
	}
	
	//The Symbol given to the query should be the right one
	public String getNameBySymbol(String symbol){
		return getWord("Name", "Symbol", symbol, DatabaseConstants.LIKE);
	}
	
	private String getWord(String columnToGet, String columnName, String fieldName, String operator){
		String word = null;
		try{
			//Prevent SQL injection
			String Query = CompanyInformationJdbc.SELECT + columnName + operator + "'" + StringEscapeUtils.escapeJava(fieldName) + "';";
			ResultSet result = statement.executeQuery(Query);
			//Matches were found!
			if(result.next()){
				ResultSetMetaData meta = result.getMetaData();
				//Iterate through the Column names
				for(int c = 1; c <= meta.getColumnCount(); c++){
					if(meta.getColumnName(c).equalsIgnoreCase(columnToGet)){
						word = result.getObject(c).toString();
						break;
					}
				}
				//Display first Match to console
				if(Global.DEBUG){
					//Print result
					for(int c = 1; c <= meta.getColumnCount(); c++){
						System.out.print(result.getObject(c) + "\t");
					}
					System.out.println();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return word;
	}
	
	//Adding data into database can be done using batch mode
	public void put(Object obj) throws SQLException{
		if(obj == null || !(obj instanceof Stock) || ((Stock) obj).getSymbol() == null){
			throw new SQLException("Stock could not be added to database!");
		}

		Stock stock = (Stock)obj;
		List<Object> query = new LinkedList<Object>();
		String QUERY = INSERT;

		//NOT REALLY A GOOD WAY OF ADDING DATA BECAUSE IT ASSUMES THAT IT WILL GET ALL VALUES
		//UNFORTUNATELLY A PERPARED STATEMENT CANNOT BE CONSTRUCTED SINCE THE TABLE NAME IS OBTAINED ON THE FLY 
		//Create table if it does not exist
		useTable(stock.getSymbol());
		
		//Use Symbol table
		query.add(stock.getSymbol());
		
		//Store current date and time
		query.add(Global.getTimeStamp()); //Date
		query.add(Global.getTimeString());//Time

		//Create query based on the information given
		if(stock.getOpen() != null){
			query.add(stock.getOpen());
		}
		
		if(stock.getHigh() != null){
			query.add(stock.getHigh());
		}
		
		if(stock.getLow() != null){
			query.add(stock.getLow());
		}

		if(stock.getClose() != null){
			query.add(stock.getClose());
		}
		
		if(stock.getVolume() != null){
			query.add(stock.getVolume());
		}
		
		if(stock.getChange() != null){
			query.add(stock.getChange());
		}
		
		if(stock.getPercentChange() != null){
			query.add(stock.getPercentChange());
		}
		
		if(stock.getLastClose() != null){
			query.add(stock.getLastClose());
		}
		
		if(stock.getAverageVolume() != null){
			query.add(stock.getAverageVolume());
		}
		
		if(stock.getMarketCapitalization() != null){
			query.add(stock.getMarketCapitalization());
		}
		
		//Create the statement
		for(int i=0; i < query.size(); i++){
			if(i==0){
				QUERY = QUERY.replaceFirst("\\?", query.get(i)+"");
				continue;
			}
			//The first one is the table's name
			if(query.get(i) instanceof String){
				QUERY = QUERY.replaceFirst("\\?", "'" + query.get(i) + "'");
			}
			else if(query.get(i) instanceof Timestamp){
				QUERY = QUERY.replaceFirst("\\?", "'" + query.get(i).toString().substring(0, 19) + "'");
			}
			else{
				QUERY = QUERY.replaceFirst("\\?", query.get(i)+"");
			}
		}
		
		if(Global.DEBUG){
			System.out.println("Query : " + QUERY);
		}
		
		if(batchUpdatesSupported){
			statement.addBatch(QUERY);
		}else{
			statement.executeUpdate(QUERY);
		}
	}
	
	private void useTable(String tableName) throws SQLException{
		if(batchUpdatesSupported){
			statement.addBatch(CREATE_TABLE.replace("?", tableName));
		}
		else{
			statement.executeUpdate(CREATE_TABLE.replace("?", tableName));
		}
	}
}