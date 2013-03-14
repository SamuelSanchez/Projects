package webretrieval.database;

/**
 * This class holds a constants of database information needed globally.
 * 
 * @author Samuel E. Sanchez
 *
 */
public interface DatabaseConstants {
    //Database Properties
    public static final String HOSTNAME         = "development.zapto.org";
    public static final String PORT             = ":3306";
    public static final String USERNAME         = "qcuser";
    public static final String PASSWORD			= "qcuser";
    public static final String MYSQL_DEFAULT_DB = "web";

    //Database Drivers
    public static final String ACCESS_DRIVER = "sun.jdbc.odbc.JdbcOdbcDriver";
    public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    public static final String MYSQL_DRIVER  = "com.mysql.jdbc.Driver";
    public static final String ACCESS_DATASOUCE = "";
    public static final String ORACLE_DBSID     = "";

    //Database URL
    public static final String ACCESS_URL = "jdbc:odbc:" + ACCESS_DATASOUCE;
    public static final String ORACLE_URL = "jdbc:oracle:thin:@" + HOSTNAME + PORT + ORACLE_DBSID;
    public static final String MYSQL_URL  = "jdbc:mysql://" + HOSTNAME + PORT + "/" + MYSQL_DEFAULT_DB;

    //Web Database
    public static final String WEB_DATABASE 			= "web";
    public static final String PAGE_INFORMATION_TABLE  	= "Pages_Information";
    public static final String IMAGES_TABLE	 			= "Images";
    public static final String LINKS_TABLE				= "Links";
    public static final String TAGS_TABLE				= "Tags";

    public static final String CREATE_DATABASE = "create database if not exists " ;
    public static final String USE_DATABASE    = "use ";
    
    //QUERY Constants
    public static final String LIKE = " like ";
    public static final String EQUAL = " = ";
}
