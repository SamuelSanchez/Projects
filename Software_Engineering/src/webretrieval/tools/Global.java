package webretrieval.tools;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * This class have all Global variables used through out the class
 * @author Samuel E. Sanchez
 *
 */
public final class Global {

	public static final String SIGNATURE = "WEB_SIGNATURE_QC";
	public static final String PARAGRAPH_INIT = "\\b.*\\b";
	public static final String PARAGRAPH_END  = "\\b[^\\.]*.*[\\.\\n\\r?]?";
	public static final String SENTENCE_INIT = "\\b[^\\.\\n]*\\b";
	public static final String SENTENCE_END = "\\b[^\\.]*[\\.]?";
	public static final String SPLIT_SENTENCE = "[\\.]\\s?";
	public static final String SPLIT_PARAGRAPH = "[\\.].?\\n";
	public static final String IMAGE_NAME = "([^\\s/]+(?=\\.(jpg|gif|png))\\.\\2)";
	public static final String ALT_NAME = "alt\\s*=\\s*(\"[^\"]*\"|'[^']*')";
	public static final String CLEAN_NAME = "[\"';(&amp)+=:]";
	public static final String END_LINE = "\r|\n";
	public static final String LINE_CHAR = "\n";
	public static final String RETURN_CHAR = "\r";
	public static final String SPACE = "\\s+";
	public static final String NEWLINE = System.getProperty("line.separator");
	public static final String SLASH = System.getProperty("file.separator");
	public static final int TIME_OUT = 5000;
	public static final boolean DEBUG = false;
	public static final boolean WARNING = false;
	public static final String AMAZON_PAGE = "http://www.amazon.com/gp/product/";
	
	/**
	 * Get Todays date as a String 
	 * @return
	 */
	//Returns the string representation of the day
	public static String getDateString(){
		Calendar calendar = new GregorianCalendar();
		return (calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH)+1) + "-" + calendar.get(Calendar.DATE));
	}
	
	/**
	 * Gets the time representation of today's date as a time-stamp for the database
	 * @return Returns the stamp time representation for the database
	 */
	//Mysql date format - return the current date and time 
	public static Timestamp getTimeStamp(){
		return new Timestamp(new Date().getTime());
	}
}
