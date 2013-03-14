package calendar;

public interface DatabaseConstants {
    //Database Properties
    public static final String HOSTNAME         = "localhost";
    public static final String PORT             = ":3306";
    public static final String USERNAME         = "root";
    public static final String MYSQL_DEFAULT_DB = "mysql";

    //Database Drivers
    public static final String ACCESS_DRIVER = "sun.jdbc.odbc.JdbcOdbcDriver";
    public static final String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    public static final String MYSQL_DRIVER  = "com.mysql.jdbc.Driver";
 
    //Dumby variables
    public static final String ACCESS_DATASOUCE = "";
    public static final String ORACLE_DBSID     = "";

    //Database URL
    public static final String ACCESS_URL = "jdbc:odbc:" + ACCESS_DATASOUCE;
    public static final String ORACLE_URL = "jdbc:oracle:thin:@" + HOSTNAME + PORT + ORACLE_DBSID;
    public static final String MYSQL_URL  = "jdbc:mysql://" + HOSTNAME + PORT + "/" + MYSQL_DEFAULT_DB;

    //Spoken Languages Database
    public static final String SLP_DATABASE = "slp";
    public static final String SLP_TABLE    = "calendar";

    public static final String CREATE_DATABASE = "create database if not exists " + SLP_DATABASE;
    public static final String USE_DATABASE    = "use " + SLP_DATABASE;


    public static final String SLP_CREATE_TABLE = "create table if not exists " + SLP_TABLE + 
					       	  "(type   varchar(20) not null,"+
						  "command varchar(20) not null," +
						  "date DATE not null," +
						  "time TIME not null," +
						  "location varchar(200) not null );";

    public static final String SLP_INSERT = "insert into " + SLP_TABLE +
                                            " (type, command, date, time, location)" +
                                            " values (?, ?, ? , ?, ?);";

}
