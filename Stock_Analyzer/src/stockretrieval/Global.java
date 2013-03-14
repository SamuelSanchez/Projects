package stockretrieval;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public final class Global {
	public static final String SIGNATURE = "WEB_SIGNATURE_QC";
	public static final String SENTENCE_INIT = "\\b.*\\b";
	public static final String SENTENCE_END = "\\b[^\\.]*[\\.]?";
	public static final String PARAGRAPH_END = "\\b[^\\.]*.*[\\.\\n\\r?]?";
	public static final String IMAGE_NAME = "([^\\s/]+(?=\\.(jpg|gif|png))\\.\\2)";
	public static final String ALT_NAME = "alt\\s*=\\s*(\"[^\"]*\"|'[^']*')";
	public static final String CLEAN_NAME = "[\"';(&amp)+=:]";
	public static final String END_LINE = "\r|\n";
	public static final String LINE_CHAR = "\n";
	public static final String RETURN_CHAR = "\r";
	public static final String SPACE = "\\s+";
	public static final String NEWLINE = System.getProperty("line.separator");
	public static final String SLASH = System.getProperty("file.separator");
	public static final boolean DEBUG = false;
	
	//Prevent from calling an instance of this class
	private Global(){}
	
	//Creates an URL based on the quote - It assumes that it is the right quote symbol
	public static String createURLforQuote(String quote){
		return ("http://www.google.com/ig/api?stock=" + quote);
	}
	
	//Returns the string representation of the day
	public static String getDateString(){
		Calendar calendar = new GregorianCalendar();
		int month = calendar.get(Calendar.MONTH)+1;
		int day   = calendar.get(Calendar.DAY_OF_MONTH);
		return ((month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day) + "-" + calendar.get(Calendar.YEAR));
	}
	
	//Return the current time as a String
	public static String getTimeString(){
		Calendar calendar = new GregorianCalendar();
		int hour   = calendar.get(Calendar.HOUR_OF_DAY);
		int minute = calendar.get(Calendar.MINUTE);
		int second = calendar.get(Calendar.SECOND);
		return ((hour < 10 ? "0" + hour : hour) + ":" + (minute < 10 ? "0" + minute : minute) + ":" + (second < 10 ? "0" + second : second));
	}
	
	//Return the current time as a Calendar Object
	public static GregorianCalendar getTime(){
		return new GregorianCalendar();
	}
	
	//Creates a Calendar based on today's date in the set time - 24 hours
	public static GregorianCalendar getTime(int hours, int minutes, int seconds){
		GregorianCalendar temp = new GregorianCalendar(); 
		GregorianCalendar cal = new GregorianCalendar(temp.get(Calendar.YEAR), temp.get(Calendar.MONTH), temp.get(Calendar.DAY_OF_MONTH), hours, minutes, seconds);
		return cal;
	}
	
	//Returns true if the first clock is before the second clock
	public static boolean isBefore(GregorianCalendar calendar1, GregorianCalendar calendar2){
		return calendar1.before(calendar2);
	}
	
	//Mysql date format - return the current date and time 
	public static Timestamp getTimeStamp(){
		return new Timestamp(new Date().getTime());
	}
}
