package webretrieval.database;


import java.sql.SQLException;
import java.util.List;

/**
 * This class contains the structure of what methods should be implemented on classes using this interface.
 * This are generic methods for all database retrieval.
 * @author Samuel E. Sanchez
 *
 */
public interface DaoInterface {
	
	/**
	 * Disconnects all statements from the database.
	 * @throws SQLException
	 */
	public void Disconnect() throws SQLException;

	/**
	 * It uses a database if it exists, otherwise it will create it and use it.
	 * @param database Database to be created
	 * @throws SQLException	Exception raised
	 */
	public void createUsePrimaryDatabase(String database) throws SQLException;
	
	/**
	 * It uses a database if it exists.
	 * @param database	Database to be used.
	 * @throws SQLException	Exception raised
	 */
	public void usePrimaryDatabase(String database) throws SQLException;
	
	/**
	 * Put an Object data into the Databases
	 * @param obj Object to retrieve information from and its data will be stored in the database
	 * @throws SQLException Exception raised
	 */
	public void put(Object obj) throws SQLException;
	
	/**
	 * Function that gets all the list of Objects that will be retrieved given an filter
	 * @param obj	Object used as a filter with some information to be matched against the database
	 * @return	The list of matched objects given the filters, if any matched is found, Empty list otherwise
	 * @throws SQLException Exception is raised if there are any errors
	 */
	public List<?> get(Object obj) throws SQLException;
	
	/**
	 * Executes batches from the database
	 * @throws SQLException Exception is raised if there are any errors
	 */
	public void executeBatch() throws SQLException;
}
