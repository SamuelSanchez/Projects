package stockretrieval;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class JdbcConnection {

	protected Connection connection = null;
	protected Statement statement = null;
	protected PreparedStatement insertStatement = null;
	protected boolean batchUpdatesSupported;
	
	public JdbcConnection() throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
	     //Connect to default Database
	     Class.forName( DatabaseConstants.MYSQL_DRIVER ).newInstance();
	     connection = DriverManager.getConnection( DatabaseConstants.MYSQL_URL, 
	    		 DatabaseConstants.USERNAME, DatabaseConstants.PASSWORD );
	     //Database Statements
	     statement = connection.createStatement();

	     //Check if it supports batch updates - prevent unnecessary exceptions
	     batchUpdatesSupported = false;
	     try{
	    	 if(connection.getMetaData().supportsBatchUpdates()){
	    		 batchUpdatesSupported = true;
	    	 }
	     }catch(Exception e){
	    	 System.out.println("Error : " + e.toString());
	     }
	}
	
	public void Disconnect() throws SQLException{
		statement.close();
		if(insertStatement != null) insertStatement.close();
		connection.close();
	}
	
	public void executeBatch() throws SQLException{
		if(batchUpdatesSupported){
			statement.executeBatch();
			if(insertStatement != null) insertStatement.executeBatch();
		}
	}
}
