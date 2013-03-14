package stockretrieval;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class CompanyInformationJdbc extends JdbcConnection implements DaoInterface {

    private static final String CREATE_TABLE = "create table if not exists " + DatabaseConstants.COMPANIES_TABLE + 
											       	  " (Symbol   varchar(20) not null,"+
											       	  "Name   varchar(150) not null,"+
											       	  "Exchange   varchar(10) not null,"+
													  "IPO_Year varchar(8)," +
													  "Sector varchar(100) not null," +
													  "Industry varchar(100) not null," +
													  "Summary_Quote varchar(100) not null, " +
													  "PRIMARY KEY (Symbol));";
	
	private static final String INSERT = "INSERT INTO " + DatabaseConstants.COMPANIES_TABLE +
									" (Symbol, Name, Exchange, IPO_YEAR, Sector, Industry, Summary_Quote)" +
									" VALUES (?, ?, ? , ?, ?, ?, ?);";
	private static final String WHERE = " WHERE 1 = 1 and ";
	
	public static final String SELECT = "SELECT Symbol, Name, Exchange, IPO_YEAR, Sector, Industry, Summary_Quote" +
										 " FROM " + DatabaseConstants.COMPANIES_TABLE + WHERE;
	
	public CompanyInformationJdbc() throws Exception {
		super();
		//Created predefined statements
		insertStatement = connection.prepareStatement(INSERT);
		//Populate this later on - Just avoid errors
	}

	public void createUsePrimaryDatabase() throws SQLException {
		//Select Database and table
	     statement.executeUpdate( DatabaseConstants.CREATE_DATABASE );
	     statement.executeUpdate( DatabaseConstants.USE_DATABASE );
		 statement.executeUpdate( CREATE_TABLE );
	}

	public void usePrimaryDatabase() throws SQLException {
		statement.executeUpdate( DatabaseConstants.USE_DATABASE );
	}

	public void put(Object obj) throws SQLException{
		if(obj == null || !(obj instanceof CompanyInformation)){
			throw new SQLException("Stock could not be added to database!");
		}
		
		CompanyInformation ci = (CompanyInformation)obj;
		List<String> query = new LinkedList<String>();
		List<Integer> position = new LinkedList<Integer>();
		
		//Create query based on the information given
		if(ci.getSymbol() != null){
			query.add(ci.getSymbol());
			position.add(1);
		}
		
		if(ci.getName() != null){
			query.add(ci.getName());
			position.add(2);
		}
		
		if(ci.getExchange() != null){
			query.add(ci.getExchange().toString());
			position.add(3);
		}
		
		if(ci.getIPOYear() != null){
			query.add(ci.getIPOYear());
			position.add(4);
		}
		
		if(ci.getSector() != null){
			query.add(ci.getSector());
			position.add(5);
		}
		
		if(ci.getIndustry() != null){
			query.add(ci.getIndustry());
			position.add(6);
		}
		
		if(ci.getSummaryQuote() != null){
			query.add(ci.getSummaryQuote());
			position.add(7);
		}
		
		//Create the statement
		for(int i=0; i<position.size(); i++){
			insertStatement.setString(position.get(i), query.get(i));
		}
		
		if(Global.DEBUG){
			System.out.println("Query : " +insertStatement.toString());
		}
		
		if(batchUpdatesSupported){
			insertStatement.addBatch();
		}else{
			insertStatement.executeQuery();
		}
	}
	
	public static void main(String[] args){
		try{
			if(args.length != 1){
				System.err.println("Usage: CompanyInfomation <file>\n\t-where <file> is the Excel file to parse.");
				return;
			}
			CompanyInformationJdbc companyInformationJdbc = new CompanyInformationJdbc();
			//String filename = "data/companylist_NASDAQ.xls";
			String filename = args[0];
			Stock.Exchange exchange = args[0].contains("NASDAQ") ? Stock.Exchange.NASDAQ :
									  args[0].contains("NYSE") ? Stock.Exchange.NYSE :
									  args[0].contains("AMEX") ? Stock.Exchange.AMEX : null;
			if(exchange == null){
				throw new Exception("Invalid Exchange [ " + args[0] + " ]");
			}
			List<List<String>> companyList = ExcelReader.retrieveExcelFile(filename, 0);
			List<CompanyInformation> companies = new LinkedList<CompanyInformation>();
			boolean skipFirst = true;
			
			//Create table
			companyInformationJdbc.createUsePrimaryDatabase();
			
			//Convert from Excel info into Company Objects
			for(List<String> list : companyList){
				if(skipFirst){
					skipFirst = false;
					continue;
				}
				CompanyInformation company = new CompanyInformation();
				company.setSymbol(list.get(0));
				company.setName(list.get(1));
				company.setExchange(exchange);
				company.setIPOYear(list.get(5));
				company.setSector(list.get(6));
				company.setIndustry(list.get(7));
				company.setSummaryQuote(list.get(8));
				
				//Add Company
				companies.add(company);
			}
			
			for(CompanyInformation company : companies){
				companyInformationJdbc.put(company);
			}
			//Update batch
			companyInformationJdbc.executeBatch();
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}