package calendar;

//Here drop any enumerators or constants that any class might need
public interface Constants {
    //System Constants
    public static final String NEWLINE = System.getProperty( "line.separator" );

    public enum AUDIO { WAVE, MICROPHONE };

    public static String file_path = "resources/config_files/calendar.conf";
    public static String PROMPT = "./prompt.pl ";

    //Fake Database Name
    public static final String datebase_path = "resources/file_database/";
    public static final String FAKE_DATABASE = datebase_path + "Database_SLP.log";
    public static final String DATA_SEPARATOR = " | ";
}
