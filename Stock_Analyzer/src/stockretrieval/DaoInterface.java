package stockretrieval;

import java.sql.SQLException;

public interface DaoInterface {
	
	public void Disconnect() throws SQLException;
	
	public void createUsePrimaryDatabase() throws SQLException;
	
	public void usePrimaryDatabase() throws SQLException;
	
	public void put(Object obj) throws SQLException;
	
	public void executeBatch() throws SQLException;
}
