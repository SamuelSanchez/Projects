package stockretrieval;

public interface DatabaseConstants {
    //Database Properties
    public static final String HOSTNAME         = "development.zapto.org";
    public static final String PORT             = ":3306";
    public static final String USERNAME         = "root"; //"research";
    public static final String PASSWORD			= "Edu237193"; //"Sam828097";
    public static final String MYSQL_DEFAULT_DB = "mysql";

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

    //Stock Database
    public static final String STOCK_DATABASE = "stock";
    public static final String COMPANIES_TABLE    = "Companies_Information";

    public static final String CREATE_DATABASE = "create database if not exists " + STOCK_DATABASE;
    public static final String USE_DATABASE    = "use " + STOCK_DATABASE;
    
    //QUERY Constants
    public static final String LIKE = " like ";
    public static final String EQUAL = " = ";
}
