package webretrieval.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This is a general class that connects to a database.
 * Other classes should be extended from this class
 * @author Samuel E. Sanchez
 *
 */
//TODO: Make this class Generic
public class JdbcConnection {

	protected Connection connection = null;
	protected Statement statement = null;
	protected PreparedStatement insertStatement = null;
	protected boolean batchUpdatesSupported;
	
	/**
	 * Connects to a database
	 * @param mySQL_Url	URL where MySQL is to be connected, local-host if connected locally
	 * @param username	User name use to connect to the database
	 * @param password	Password to be use when connecting to the database
	 * @throws SQLException	Exception raised, if any
	 * @throws InstantiationException	Exception raised, if any
	 * @throws IllegalAccessException	Exception raised, if any
	 * @throws ClassNotFoundException	Exception raised, if any
	 */
	public JdbcConnection(String mySQL_Url, String username, String password) throws SQLException, InstantiationException, IllegalAccessException, ClassNotFoundException{
	     //Connect to default Database
	     Class.forName( DatabaseConstants.MYSQL_DRIVER ).newInstance();
	     connection = DriverManager.getConnection(mySQL_Url, username, password);
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
	
	/**
	 * Disconnect all statements from the database
	 * @throws SQLException
	 */
	public void Disconnect() throws SQLException{
		statement.close();
		if(insertStatement != null) insertStatement.close();
		connection.close();
	}
	
	/**
	 * Executes all batches stored in the Prepared Statements
	 * @throws SQLException Raised an Exception, if any
	 */
	public void executeBatch() throws SQLException{
		if(batchUpdatesSupported){
			statement.executeBatch();
			if(insertStatement != null) insertStatement.executeBatch();
		}
	}
}
