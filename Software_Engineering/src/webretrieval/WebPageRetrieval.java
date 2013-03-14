package webretrieval;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import webretrieval.html.HTMLDoc;
import webretrieval.html.HTMLImage;
import webretrieval.html.HTMLTags;
import webretrieval.tools.Global;
import webretrieval.tools.WebConnector;


/**
 * This is the main class of the Program. This class retrieve all the data from the HTML source code given an URL.
 * Methods:
 * 		- getHost() : Returns the host name of the website, if it's a valid website, null otherwise.
 * 
 * 		- getURLString() : Returns the string representation of the URL, if it's valid, null otherwise.
 * 
 * 		- getTags() : Returns the Set of Strings containing all the tags retrieved from the website, if it's valid URL, empty Set otherwise.
 * 
 * 		- getTagList(tag) : Returns the Set of Strings containing all the matches given an html tag, if it's a valid URL, null otherwise.
 * 
 * 		- getTagCount(tag) : Given a tag, it returns the total number of tags found on the site, if it's a valid URL, 0 otherwise.
 * 
 * 		- isConnectionSuccessful : Returns true if the connection was successuful given a valid URL, false otherwise.
 * 
 * 		- getHTMLDoc() : Returns a class containing all the html retrieved form the website, if it's valid URL, null otherwise.
 * 
 * 		- Update(String) : Retrieve all tags information set by the user on the website, given a valid URL.
 * @author Samuel E. Sanchez
 *
 */
public class WebPageRetrieval{
	private WebConnector webConnector;					//Useful for retrieve the html source code from the website
	private String host;								//String representation of the host name
	private HTMLTags htmlTags;							//Class containing a map of all the possible html tags to retrieve
	private Map<String, Set<Object>> tagsMap;			//Map saving all found html tags
	private Pattern[] tagPattern;						//Useful for creating patterns to retrieve data from the source code
	private Matcher[] tagMatcher;						//Useful for retrieving patterns given the source code and a pattern.
	private String webContent;							//String representation of the website containing all crude data
	private URL currentURL;								//Stores the URL just connected
	private boolean isConnectionSuccesful;				//Useful for letting the user know if it's a valid connection
	private HTMLDoc htmlDoc;							//Store html retrieved content
	
	/**
	 * Creates a constructor for WebPageRetrieval Class. 
	 * It initializes all variables.
	 */
	public WebPageRetrieval(){
		this.webConnector = WebConnector.MOZILLA;
		this.htmlTags = HTMLTags.INSTANCE;
		reset();
	}

	///////////////////////
	//   PUBLIC METHODS  //
	///////////////////////
	
	/**
	 * Gets the host name from the URL
	 * @return The String representation of the Host, if valid, null otherwise.
	 */
	public String getHost(){
		return host;
	}
	
	/**
	 * Gets the String URL
	 * @return The String representation of the URL, if valid, null otherwise.
	 */
	public String getURLString(){
		return currentURL != null ? currentURL.toExternalForm() : null;
	}
	
	/**
	 * Gets all the tags retrieved from the website
	 * @return Set of String containing all the tags retrieved from the website, if valid, empty otherwise.
	 */
	//Returns the list of all the tags that we have looked
	public Set<String> getTags(){
		return tagsMap.keySet();
	}
	
	/**
	 * Gets all the tags found Given a tag
	 * @param tag Tag to be retrieved
	 * @return All matches given the tag
	 */
	public Set<Object> getTagList(String tag) {
		return tagsMap.get(tag);
	}
	
	/**
	 * Gets the total number of tags matched, given a tag
	 * @param tag Tag to be found
	 * @return The total number of matches, if it's a valid tag, 0 otherwise.
	 */
	public int getTagCount(String tag){
		return tagsMap.get(tag) != null ? tagsMap.get(tag).size() : 0;
	}
	
	/**
	 * Gets wether the connection was successful or not
	 * @return true if the connection was successful, false otherwise
	 */
	public boolean isConnectionSuccessful(){
		return isConnectionSuccesful;
	}
	
	/**
	 * Returns an Object storing all the HTML information given an URL
	 * @return HTMLDoc containing all information given a valid URL, null otherwise
	 */
	//Return all information about this Website stored into an Object
	public HTMLDoc getHtmlDoc(){
		return htmlDoc;
	}
	
	/**
	 * Retrieve all information given a URL
	 * @param url Page to retrieve information from
	 */
	public void Update( String url ){
		try{
			//If the connection was not successful then return
			if(!Connect( url )){
				isConnectionSuccesful = false;
				return;
			}
			if(Global.DEBUG){
				webConnector.print();
			}
			isConnectionSuccesful = true;
			//Compile all the HTML regular expressions given by the user
			compileRegex();
			//Retrieve crude data from the web-site
			//Clean the title and body
			findTag();
			//Clear URLs and IMGs
			if(tagsMap.containsKey(HTMLTags.A))
				clearTags(HTMLTags.A, "href");
			if(tagsMap.containsKey(HTMLTags.IMG))
				clearTags(HTMLTags.IMG, "src");
			//If everything is successful - store the data into an Object
			//Convert from a map of sets to a map of lists
			Set<String> keys = tagsMap.keySet();
			Map<String,List<Object>> tagsList = new HashMap<String, List<Object>>();
			for(String key : keys){
				tagsList.put(key, new LinkedList<Object>(tagsMap.get(key)));
			}
			htmlDoc = new HTMLDoc(Global.getDateString(), currentURL.toExternalForm(), host, tagsList);
		}catch(Exception e){
			System.out.println("Error : " + e.toString());
			e.printStackTrace();
		}
	}
	
	////////////////////////
	//   PRIVATE METHODS  //
	////////////////////////
	
	/**
	 * Connects to the URL
	 * @param url Connects to the URL
	 * @return true if connection is successful, false otherwise
	 */
	//Connect to the URL
	private boolean Connect( String url ) {
		this.webContent = null;
		this.currentURL = null;
		this.host = null;
		this.htmlDoc = null;
		boolean isConnected = false;
		if(webConnector.Connect(url)){
			this.host = webConnector.getHost();
			this.webContent = webConnector.getInput();
			this.currentURL = webConnector.getURL();
			isConnected = true;
		}
		else{
			System.err.println("Error : Page could not be reached");
		}
		return isConnected;
	}
	
	/**
	 * Compiles all the Regular Expressions in order to retrieve the Tags information from the website, if it's a valid.
	 * @throws WebPageRetrievalException If it's an invalid regular expression to be formed.
	 */
	//Compile all the regular expressions requested by the user
	private void compileRegex() throws WebPageRetrievalException{
		try{
			//Initialize pattern/matcher for tags
			tagsMap = new HashMap<String, Set<Object>>();
			tagPattern = new Pattern[htmlTags.getTags().size()];
			tagMatcher = new Matcher[htmlTags.getTags().size()];
			int index = 0;
			//Compile regular expressions for all given HTML tags
			for(String tag : htmlTags.getTags()){
				tagsMap.put(tag, new HashSet<Object>());
				tagPattern[index] = Pattern.compile(htmlTags.getRegex(tag));
				index++;
			}

		}catch(Exception e){
			throw new WebPageRetrievalException(e.toString(), e.getStackTrace());
		}
	}	

	/**
	 * Find all the tags specified in compileRegex() from the html source code.
	 */
	//Find all the tags requested by the user
	private void findTag(){
		//Let's narrow all the blank spaces
		webContent = webContent.replaceAll(Global.SPACE, " ");
		
		//--------------------  Tag Spotter  --------------------//
		int index = 0;
		//Find the HTML tags from the Stream
		for(String tag : htmlTags.getTags()){
			tagMatcher[index] = tagPattern[index].matcher(webContent);
			
			//Get all Matches for this tag
			while(tagMatcher[index].find()){
				tagsMap.get(tag).add(tagMatcher[index].group().trim().replaceAll(Global.SIGNATURE, " "));
			}
			index++;
			
			//Take them out to avoid unnecessary computation later
			webContent = webContent.replaceAll(htmlTags.getRegex(tag), " ");
		}
		
		//------------------  Clearing HTML tags  ------------------//
		webContent = webContent.replaceAll(HTMLTags.ALL_TAGS, " ").trim();
		
		//------------------ Clean unnecessary spaces -------------//
		LinkedList<String> list = new LinkedList<String>();
		
		//Get rid of too many New Lines
		for(String temp : webContent.split(Global.SIGNATURE)){
			temp = temp.replaceAll(Global.SPACE, " ");
			if(temp.trim().isEmpty()) continue;
			list.add(temp);
		}
		
		StringBuffer buffer = new StringBuffer();
		for(String temp : list){
			buffer.append(temp + Global.NEWLINE);
		}
		
		//Add the body tag to the Map
		tagsMap.put(HTMLTags.BODY, new HashSet<Object>());
		tagsMap.get(HTMLTags.BODY).add(buffer.toString());
		
		//Clear the title
		String pageTitle = tagsMap.get(HTMLTags.TITLE) != null ? tagsMap.get(HTMLTags.TITLE).toString().replaceAll(HTMLTags.ALL_TAGS, "") : "";
		if(pageTitle != null) pageTitle = pageTitle.substring(1, pageTitle.length()-1 );
		tagsMap.remove(HTMLTags.TITLE);
		tagsMap.put(HTMLTags.TITLE, new HashSet<Object>());
		tagsMap.get(HTMLTags.TITLE).add(pageTitle);
	}
	
	/**
	 * Clears the information retrieve from the regular expressions
	 * @param searchTag	HTML tag to be clean, Ex: a, link, title, ...
	 * @param source The source to be saved from the tag, Ex: href, src, ...
	 */
	private void clearTags(String searchTag, String source){
		Set<Object> cleanTags = new HashSet<Object>();
		Set<Object> currentTags = tagsMap.get(searchTag);	//Double check that it exists
		int index = 0;
		String subReferenceLink = null;
		StringTokenizer tokens = null;
		
		if(currentTags != null){
			for(Object obj : currentTags){
				//So far all the Objects are Strings, but now img tag will get converted into a ImageHTML Object
				String tag = (String)obj;
				index = 0;
				subReferenceLink = null;
				tokens = null;
				//Make sure that there's a reference and if so, keep the index
				//Otherwise stop looking and go to the next word
				if((index = tag.toLowerCase().indexOf(source, index)) == -1)
					continue;
				//Keep the reference of the equal sign
				if((index = tag.toLowerCase().indexOf("=", index)) == -1)
					continue;
				//Catch any errors that might appear due to re-naming
				try{
					//Cut the Substring and then convert it into tokens by cleaning up useless data
					subReferenceLink = (tag.substring(index+1)).trim();
					//subReferenceLink = subReferenceLink.replace("amp;", ""); //Replace the HTML escape character for '&' used at links 
					tokens = new StringTokenizer(subReferenceLink, "\t\n\r\"<>#'");
					subReferenceLink = (tokens.hasMoreElements() ? tokens.nextToken() : null);
					//For an IMG also get the alt - reuse tag string
					if(searchTag.equalsIgnoreCase(HTMLTags.IMG)){
						try{
							int loc = tag.toLowerCase().indexOf(" alt");
							if(loc != -1){
								tag = (tag.substring(loc+1)).trim();
								loc = tag.toLowerCase().indexOf("=");
								//It should be within max 4 steps away
								if(loc >= 0 && loc < 4){
									tag = (tag.substring(loc+1)).trim();
									StringTokenizer st = new StringTokenizer(tag, "\"'\t\n\r");
									tag = (st.hasMoreElements() ? st.nextToken() : null);
								}else{
									tag = null;
								}
							}else{
								tag = null;
							}
						}catch(Exception e){
							tag = null;
						}
					}
				}catch(Exception e){
					if(Global.DEBUG) System.out.println("ERROR : " + e.toString());
			    	continue;
				}
				
				//Assuming that we filtered enough, let's make sure that we can connect to the links
				if(subReferenceLink != null && subReferenceLink.length() > 1){
					URL link = null;
					try {
						link = new URL(currentURL, subReferenceLink);
						subReferenceLink = link.toString();
				    } catch (MalformedURLException e) {
				    	if(Global.DEBUG) System.out.println("ERROR: bad URL " + subReferenceLink);
				    	continue;
				    }

					//Let's only keep all references that has http[s] as protocol
					if (link.getProtocol().contains("http")){
						if(searchTag.equalsIgnoreCase(HTMLTags.IMG)){
							try{
								HTMLImage htmlImage = new HTMLImage(subReferenceLink, tag, link);
								cleanTags.add(htmlImage);
							}catch(Exception e){
								System.out.println("Error : " + e.toString());
							}
						}else{
							cleanTags.add(subReferenceLink);
						}
					}
				}
			}

			//Remove the previous links and put the clean one
			if(tagsMap.remove(searchTag) != null){
				tagsMap.put(searchTag, cleanTags);
				if(Global.DEBUG) System.out.println("Removing crude data [ " + searchTag + " ] and adding a cleaner one!");
			}
		}//Tags
	}
	
	/**
	 * Resets all the values every time a new website is to be search in the Update method
	 */
	public void reset(){
		this.tagsMap = new HashMap<String, Set<Object>>();
		this.tagPattern = new Pattern[0];
		this.tagMatcher = new Matcher[0];
		this.isConnectionSuccesful = false;
		this.htmlDoc = null;
	}
}