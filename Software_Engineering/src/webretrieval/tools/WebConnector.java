package webretrieval.tools;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class retrieve the website information as a String, given a valid URL
 * 
 * @author Samuel E. Sanchez
 *
 */

public class WebConnector {
	//Variables useful for the URL connection
	private String USER_AGENT;
	private URL url;
	private URLConnection urlConnection;
	private boolean isConnectionSuccessful;
	private String urlData;
	
	//List of the Browsers User-Agent
	private static final String SAFARI_USER = "Mozilla/5.0 (Windows NT6.1; WOW64) AppletWebKit/534.55.3 (KHTML, like Gecko) Version/5.1.5 Safari/534.55.3";
	private static final String CHROME_USER = "Mozilla/5.0 (Windows NT6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.79 Safari/535.11";
	private static final String IE_9_USER = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; InfoPath.2; .NET4.0E";
	private static final String MOZILLA_USER = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6";
	private static final String BING_USER = "Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)";
	
	//Possible Instances
	public static WebConnector SAFARI = new WebConnector( SAFARI_USER );
	public static WebConnector CHROME = new WebConnector( CHROME_USER );
	public static WebConnector IE_9 = new WebConnector( IE_9_USER );
	public static WebConnector MOZILLA = new WebConnector( MOZILLA_USER );
	public static WebConnector BING = new WebConnector( BING_USER );
	
	private WebConnector(String userAgent){
		this.USER_AGENT = userAgent;
	}

	/**
	 * Connects to the website
	 * @param address Website to be connected
	 * @return true if website we connected, false otherwise
	 */
	//If the connection is successful then set the connection flag to true
	//Reset Url's for every connection to a web-site
	public boolean Connect(String address){
		isConnectionSuccessful = false;
		url = null;
		urlConnection = null;
		urlData = null;
		try{
			url = new URL(address);
			URLConnection.setDefaultAllowUserInteraction(false);
			urlConnection = url.openConnection();
			urlConnection.setDoOutput(true);
			//Only wait a specified number of seconds
			urlConnection.setConnectTimeout(Global.TIME_OUT);
			urlConnection.setReadTimeout(Global.TIME_OUT);
			urlConnection.setRequestProperty("User-Agent", USER_AGENT);
			//Avoid unnecessary computation if it's a bad request
			if(((HttpURLConnection) urlConnection).getResponseCode() >= 400){
				int error = ((HttpURLConnection) urlConnection).getResponseCode();
				if(Global.DEBUG){
					String errorMsg = error >= 500 ? "5xx Server Error" : "4xx Client Error";
					System.err.println("Error : " + errorMsg + " [ " + error + " ]");
				}
				return false;
			}
			//Avoid subsequent errors if there's no data to retrieve
			if(parseStream(urlConnection)){
				isConnectionSuccessful = true;
			}
		}catch(MalformedURLException e){
			if(Global.DEBUG) System.err.println("Malformed URL Error : " + e.toString());
		}catch(IOException e){
			if(Global.DEBUG) System.err.println("IO Error : " + e.toString());
		}
		return isConnectionSuccessful;
	}
	
	/**
	 * Parses the stream and converts it to a String
	 * @param url URL to retrieve the string from
	 * @return The string representation of the URL website
	 */
	private boolean parseStream(URLConnection url){
		boolean isGoodData = false;
		try{
			InputStream inputStream = url.getInputStream();
			int bufferSize = 124000;
			byte[] buffer = new byte[bufferSize];
			ByteArrayOutputStream arrayStream = new ByteArrayOutputStream(bufferSize);
			int status = inputStream.read(buffer);
			while(status != -1){
				arrayStream.write(buffer, 0, status);
				status = inputStream.read(buffer);
			}
			if(inputStream != null) inputStream.close();
			urlData = (arrayStream.toString()).replaceAll(Global.END_LINE, Global.SIGNATURE);
			isGoodData = true;
		}catch(Exception e){
			if(Global.DEBUG) System.out.println("Connection Error : " + e.toString());
		}
		return isGoodData;
	}
	
	/**
	 * 
	 * @return true if the connection is valid
	 */
	//Check if the Connection is valid
	public boolean isConnectionValid(){
		return isConnectionSuccessful;
	}
	
	//Can return either null or the URL
	public URL getURL(){
		return url;
	}
	
	public String getHost(){
		return url != null ? url.getHost().toLowerCase() : null;
	}
	
	public String getInput(){
		if(!isConnectionSuccessful) return null;
		return org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(urlData);
//		return escapeHTMLChars(urlData);
	}
	
	public String getInput(boolean isUnescapeHTML){
		if(!isConnectionSuccessful) return null;
		return (isUnescapeHTML) ? org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(urlData) : urlData;
//		return (isUnescapeHTML) ? escapeHTMLChars(urlData) : urlData;
	}
	
	//Return the web-site information
	@Deprecated
	public InputStream getInputStream(){
		try{
			//If there's no connection, then do not proceed
			if(!isConnectionSuccessful) return null;
			return urlConnection.getInputStream();
		}catch(Exception e){
			if(Global.DEBUG) System.out.println("Connection Error : " + e.toString());
			return null;
		}
	}
	
	@SuppressWarnings("unused")
	private String escapeHTMLChars(String string){
		if(string == null || string.isEmpty()) return "";
		return string.replaceAll("&amp;", "&").replaceAll("&nbsp;"," ").
				replaceAll("&quot;", "\"").replaceAll("&euro;", "E").replace("&lt;", "<").
				replaceAll("&gt;", ">").replaceAll("&iexcl;","!").replaceAll("&cent;", "C").
				replaceAll("&pound;","P").replaceAll("&curren;", "C").replaceAll("&yen;", "Y").
				replaceAll("&brvbar;","|").replaceAll("&sect;", "S").replaceAll("&copy;", "CR");
	}
	
	//Display the contents of the web-site
	public void print(){
		try{
			//If there's no connection, then do not proceed
			if(!isConnectionSuccessful) return;
			
			System.out.println( url.toExternalForm() + " :" );
			System.out.println( "HOST : " + url.getHost() );
			System.out.println( "Protocol : " + url.getProtocol() );
			System.out.println( "User Info : " + url.getUserInfo() );
			System.out.println( "Content Type : " + urlConnection.getContentType() );
			System.out.println( "Last Modified : " + urlConnection.getLastModified() );
			System.out.println( "Expiration : " + urlConnection.getExpiration() );
			System.out.println( "Content Encoding : " + urlConnection.getContentEncoding() );
			
			//Display more info if HTTP
	        if (urlConnection instanceof HttpURLConnection) {
	            System.out.println( "Request Method : " + ( (HttpURLConnection) urlConnection ).getRequestMethod());
	            System.out.println( "Response Message : " + ( (HttpURLConnection) urlConnection ).getRequestMethod());
	            System.out.println( "Response Code : " + ( (HttpURLConnection) urlConnection ).getRequestMethod());
	        }
		}catch(Exception e){
			/*Do nothing*/
		}
	}
}