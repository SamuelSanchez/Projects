package stockretrieval;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class WebConnector {
	//Variables useful for the URL connection
	private String USER_AGENT;
	private URL url;
	private URLConnection urlConnection;
	private boolean isConnectionSuccessful;
	private String urlData;
	
	//List of the Browsers User-Agent
	private static final String CHROME_USER = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.11 (KHTML, like Gecko) Chrome/17.0.963.79 Safari/535.11";
	private static final String IE_9_USER = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; WOW64; Trident/5.0; SLCC2; .NET CLR 2.0.50727; .NET CLR 3.5.30729; .NET CLR 3.0.30729; Media Center PC 6.0; .NET4.0C; InfoPath.2; .NET4.0E";
	private static final String MOZILLA_USER = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:11.0) Gecko/20100101 Firefox/11.0";//"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)";
	private static final String BING_USER = "Mozilla/5.0 (compatible; bingbot/2.0; +http://www.bing.com/bingbot.htm)";
	
	//Possible Instances
	public static WebConnector CHROME = new WebConnector( CHROME_USER );
	public static WebConnector IE_9 = new WebConnector( IE_9_USER );
	public static WebConnector MOZILLA = new WebConnector( MOZILLA_USER );
	public static WebConnector BING = new WebConnector( BING_USER );
	
	private WebConnector(String userAgent){
		this.USER_AGENT = userAgent;
	}

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
			urlConnection.setRequestProperty(USER_AGENT, "USER_AGENT");
			parseStream(urlConnection);
			isConnectionSuccessful = true;
		}catch(MalformedURLException e){
			if(Global.DEBUG) System.err.println("Malformed URL Error : " + e.toString());
		}catch(IOException e){
			if(Global.DEBUG) System.err.println("IO Error : " + e.toString());
		}
		return isConnectionSuccessful;
	}
	
	private void parseStream(URLConnection url){
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
			//Store one long string - replace all new lines with a unique string that we know
			urlData = (arrayStream.toString()).replaceAll(Global.END_LINE, Global.SIGNATURE);
		}catch(Exception e){
			if(Global.DEBUG) System.out.println("Connection Error : " + e.toString());
		}
	}
	
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
	}
	
	public String getInput(boolean isUnescapeHTML){
		if(!isConnectionSuccessful) return null;
		return (isUnescapeHTML) ? org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(urlData) : urlData;
	}
	
	//Return the web-site information as an input stream
	//It will not return anything because the stream was previously opened
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
