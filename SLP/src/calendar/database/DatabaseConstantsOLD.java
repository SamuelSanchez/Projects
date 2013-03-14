package calendar;

public interface DatabaseConstantsOLD {

   public static String password = "password";

   public static String fakeDB = "fakeDB.txt";

    //Appointment Types
    public static final String TYPE_APPOINTMENT = "Appointment";
    public static final String TYPE_OFFICE      = "Office";
    public static final String TYPE_SCHOOL      = "School";
    public static final String TYPE_HOME        = "Home";

    //Repetition
    public static final String REPEAT_ONCE        = "Once";
    public static final String REPEAT_DAILY       = "Daily";
    public static final String REPEAT_WEEKLY      = "Weekly";
    public static final String REPEAT_MONTHLY     = "Monthly";

    //Debug
    public static final boolean DEBUG_EVENT = true;

    //Database Drivers
    public static final String ACCESS_DB = "sun.jdbc.odbc.JdbcOdbcDriver";
    public static final String ORACLE_DB = "oracle.jdbc.driver.OracleDriver";

    //MS_Access
    public static final String ACCESS_DS = "dataSource";
    public static final String ORACLE_DBSID = "oracleDBSID";

    //Database URL
    public static final String ACCESS_URL = "jdbc:odbc:" + ACCESS_DS;
    public static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:3306:" + ORACLE_DBSID;

    //Database Connection
    public static final String DB_DRIVER = "com.mysql.jdbc.Driver";
    public static final String DB_DEFAULT = "jdbc:mysql://localhost:3306/mysql";
    public static final String DB_SLP = "slp";
    public static final String DB_ROOT = "root";
    public static final String DB_CHECK = "CREATE DATABASE IF NOT EXISTS " + DB_SLP;
    public static final String DB_USE = "USE " + DB_SLP;

    public static final String DB_TABLE = "event";

    public static final String DB_CHECK_TABLE = "CREATE TABLE IF NOT EXISTS " + DB_TABLE + 
               	"(type varchar(20) not null default 'Home',"+
		"title varchar(50) not null," +
		"description varchar(200) not null," +
		"day int(2) not null check (day>0)," +
		"month int(2) not null check (month>0)," +
		"year int(4) not null check (year>0)," +
		"date DATE not null," +
		"start_hour int(2) not null check(start_hour>0)," +
		"start_min int(2) not null check(start_min>0)," +
		"start_time TIME not null," +
		"end_hour int(2) not null check(end_hour>start_hour)," +
		"end_min int(2) not null check(end_min>start_min)," +
		"end_time TIME not null," +
		"reminder_h int(2) default 0," +
		"reminder_min int(2) default 15," +
		"repetition varchar(10) default 'Once'," +
		"guest varchar(200) default '0'," +
		"location varchar(200) default '0' );";

    public static final String ADD_EVENT = "INSERT INTO " + DB_TABLE + " (type,title,description,day,month,year,date,start_hour,start_min,start_time,"+
					   "end_hour,end_min,end_time,reminder_h,reminder_min,repetition,guest,location) values ";

    //Google Calendar Data

    /* Example in how to insert into TABLE 
     insert into event (type,title,description, day, month, year, date, start_hour, start_min, start_time, end_hour, end_min, end_time, reminder_h, reminder_min, repetition, guest, location) values ("Office","TESTING TABLE","NOTHING SO FAR",1,1,1, CURDATE(), 0, 0, CURTIME(), 0, 0, CURTIME(), 0, 0, 'Weekly', 'ME', 'MANHATTAN');
     */

}
