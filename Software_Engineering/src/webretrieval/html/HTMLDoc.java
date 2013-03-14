package webretrieval.html;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.List;
import java.util.Set;

/**
 * This class creates an Object that will contain all the html retrieved from the website
 * 
 * @author Samuel E. Sanchez
 *
 */
//TODO: Use generic more appropriately
public class HTMLDoc {
	
	private String downloadedTime;
	private String url;
	private String host;
	private Map<String, List<Object>> htmlTags;
	private List<String> wordTags;
	
	/**
	 * Creates a public contructor will empty data
	 */
	public HTMLDoc(){
		//Avoid errors, send an instance of the hash map to avoid null pointer exceptions
		this(null,null,null, new HashMap<String, List<Object>>());
	}
	
	/**
	 *  Creates a pulic constructor with the time, url name, host name, and a map of all tags
	 * @param time Time of the website retrieval
	 * @param url Url of the website
	 * @param host Host name of the website
	 * @param tags Map of tags retrieved from the website
	 */
	public HTMLDoc(String time, String url, String host, Map<String, List<Object>> tags){
		this.downloadedTime = time;
		this.url = url;
		this.host = host;
		this.htmlTags = tags;
		this.wordTags = new LinkedList<String>();	//Initialize it to avoid null pointer exceptions
	}

	/**
	 * Time of downloaded information
	 * @return The time when the website was downloaded
	 */
	public String getDownloadedTime() {
		return downloadedTime;
	}

	/**
	 * String URL
	 * @return The String representation of the URL site
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Host name
	 * @return The String representation of the host name
	 */
	public String getHost() {
		return host;
	}

	/**
	 * Gets all the tags retrieved from the website
	 * @return All the tags retrieved form the website
	 */
	public Set<String> getTags() {
		return htmlTags.keySet();
	}
	
	/**
	 * Gets all the tags matches given a tag
	 * @param tag Tag to be retrieved
	 * @return List of matches given the tags
	 */
	public List<Object> getTagList(String tag){
		return htmlTags.get(tag);
	}
	
	/**
	 * Counts the number of tags encounter given the tag
	 * @param t Tag to be searched
	 * @return Number of encounters
	 */
	public int getTagCount(String t){
		return htmlTags.get(t) != null ? htmlTags.get(t).size() : 0;
	}
	
	/**
	 * Return the list of words relating to the website
	 * @return List of words relating to the website
	 */
	//This are the tags set by the user - to associate website with tags
	public List<String> getWordsTags(){
		return wordTags;
	}

	/**
	 * Sets the downloaded time when the website was retrieved
	 * @param downloadedTime Sets the name of the downloaded time 
	 */
	public void setDownloadedTime(String downloadedTime) {
		this.downloadedTime = downloadedTime;
	}

	/**
	 * Sets the URL of the website
	 * @param url String representation of the URL
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Sets the String representation of the host
	 * @param host Host name
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * Save a list of tags given a tags
	 * @param t Tag to be saved
	 * @param list List of tags that are related to the Tag
	 */
	public void setTags(String t, List<Object> list) {
		htmlTags.remove(t);
		htmlTags.put(t, list);
	}
	
	/**
	 * Save the list of words to be related to the website
	 * @param words List of words relating to the website
	 */
	public void setWordTags(List<String> words){
		wordTags = words;
	}
	
	/**
	 * Displays the information of this website
	 */
	public void print(){
		System.out.println("Downloaded Time[ " + downloadedTime + " ]\n" +
							"URL           [ " + url + " ]\n" +
							"Host          [ " + host + " ]\n");
		for(String t1 : htmlTags.keySet()){
			System.out.println("Tag [ " + t1.toString() + " ]");
			for(Object t2 : htmlTags.get(t1)){
				System.out.println(t2.toString());
			}
		}
	}
}
