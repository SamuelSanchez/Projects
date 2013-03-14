package webretrieval.html;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import webretrieval.tools.Global;

/**
 * This class is the representation of an image tags from the HTML doc
 * It stores, the Image Source, alt, stream and the length of the string. 
 * The stream and length of the stream are used to be display in the gui or stored in the database.
 * @author Eduardo
 *
 */
public class HTMLImage {
	public String src;
	public String alt;
	public InputStream image;
	public int imageLength;
	
	/**
	 * Creates a public constructor with empty data
	 */
	public HTMLImage(){
		this.src = null;
		this.alt = null;
		this.image = null;
		this.imageLength = 0;
	}
	
	/**
	 * Creates a constructor for this website
	 * @param src Source URL of the image
	 * @param alt Alt of the image
	 * @param imgURL URL of the image
	 * @throws Exception Exception is raised, if any
	 */
	public HTMLImage(String src, String alt, URL imgURL) throws Exception{
		this.src = src;
		this.alt = (alt != null ? alt.trim() : null);
		URLConnection urlConn = imgURL.openConnection();
		//Set a time out for 1 second
		urlConn.setConnectTimeout(Global.TIME_OUT);
		urlConn.setReadTimeout(Global.TIME_OUT);
		this.image = urlConn.getInputStream();
		this.imageLength = imgURL.getFile().length();
	}
	
	/**
	 * Returns the Source URL of this website.
	 */
	public String toString(){
		return src;
	}
}
