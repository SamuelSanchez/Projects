package webretrieval;
import java.util.LinkedList;
import java.util.List;
//import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import webretrieval.tools.Global;
import webretrieval.tools.WebConnector;

/**
 * This class creates a Google query given the user input. It will find all pages in google relating to the user input.
 * The query can also be created restricted to a website, by adding "site:www.site.com" in the query.
 * 
 * The class only needs to be instantiated once and it can query as many times as 'Update' method is called. The Update
 * method takes a String as parameter, which is the query that will be send to Google in order to retrieve information.
 * Information is retrieve by three different methods:
 * 
 * 		- getLinksAtPage(number) will get all the links at the valid page number as a List of Strings. If there's not
 * 			such page, then it will return null. It captures all links.
 * 
 * 		- getAllLinks() will retrieve all the links, in the order that they are given by Google. This can be duplicate links or not duplicate links.
 * 			Set "ALLOW_DUPLICATE_LINKS" boolean as True, if duplicates are to be allowed, or false otherwise.
 * 
 * 		- getTotalNumberOfLinks will return the total number of links retrieved from Google. This can be duplicate links or not duplicate links.
 * 			Set "ALLOW_DUPLICATE_LINKS" boolean as True, if duplicates are to be allowed, or false otherwise.
 * 
 * @author Samuel E. Sanchez
 *
 */

//Google gives many duplicate pages
public class GoogleQuery {
	//Variables
	private static final String QUERY = "https://www.google.com/search?q="; //Query should come right after
	private static final String PAGE_NUMBER = "&hl=en&sa=N&start=";			//Page number should come right here
	private List<List<String>> links;										//Here store the links per page
	private List<String> allLinks;											//Store all the links
	private WebConnector webConnector;										//This is the class that retrieve the source code from the web-site
	private Pattern tagPattern;												//To create a pattern that will retrieve all Google links.
	private Matcher tagMatcher;												//Given a patter and a String, it will retrieve such pattern from the String if it exists
	private static final String A_LINK = "\\<a([^\\>]+)class=l([^\\>]+)\\>.*?\\</a\\>";	//Pattern of the Google link. All links are called class=l
	private static final int LINKS_PER_PAGE = 10;							//This is the number added to the query that will go to the next page
	private static boolean ALLOW_DUPLICATE_LINKS = false;					//This is a boolean that allows or disallows duplicates
	
	/**
	 * Creates a constructor for GoogleQuery class
	 * This initializes all variables.
	 */
	public GoogleQuery(){
		links = new LinkedList<List<String>>();
		allLinks = new LinkedList<String>();
		webConnector = WebConnector.MOZILLA;
	}
	
	/**
	 * Retrieve all the links at a give page number given a query to the Update method
	 * @param page Page number to retrieve all the links
	 * @return The list of pages from the given page number if it's a valid page, null otherwise
	 */
	public List<String> getLinksAtPage(int page){
		if(page >= links.size()) return null;
		return links.get(page);
	}
	
	/**
	 * Retrieves all the links from Google given a query to the Update method
	 * @return All the links queried from Google
	 */
	public List<String> getAllLinks(){
		return allLinks;
	}
	
	/**
	 * Retrieve the total number of links give a query to the Update method
	 * @return The total number of links provided to Google
	 */
	public int getTotalNumberOfLinks(){
		return allLinks.size();
	}
	
	/**
	 * This is the method that will retrieve all the links generated by Google given a query
	 * @param query Query to search in Google Engine in order to retrieve all the links relating to it
	 */
	//Get all links for every page in google
	public void Update(String query){
		try{
			reset();
			tagPattern = Pattern.compile(A_LINK);
			retrieveLinks(query);
			cleanLinks();
		}catch(Exception e){
			if(Global.DEBUG){
				e.printStackTrace();
				System.out.println("Error : " + e.getMessage());
			}
			//Reset values again to prevent unforeseen errors
			reset();
		}
	}
	
	/**
	 * This resets all the methods. This is called by the Update method everything that it is going to query a site
	 */
	private void reset(){
		//Initialize pattern/matcher for tags
		links = new LinkedList<List<String>>();
		allLinks = new LinkedList<String>();
	}
	
	/**
	 * This is going to retrieve all the links Given the query. This method is called by the Update method
	 * @param query Query to be searched by the Google Engine
	 */
	//Retrieve all 'a' tags that contains the links in google
	private void retrieveLinks(String query){
		int pages = 0;
		String input = null;
		boolean hasLinks = false;
		//Clean query
		query = query.replaceAll("\"", "%22");
		query = query.replaceAll(Global.SPACE, "+");
		List<String> pageLinks = null;
		
		//Create a query and connect to it
		while(webConnector.Connect(QUERY + query + PAGE_NUMBER + pages)){
			input = webConnector.getInput();
			if(Global.DEBUG){
				System.out.println(QUERY + query + PAGE_NUMBER + pages);
			}
			hasLinks = false;
			pageLinks = new LinkedList<String>();
			//Retrieve all the Links that are inside <h3 class="r">...</h3>
			tagMatcher = tagPattern.matcher(input);
			while(tagMatcher.find()){
				hasLinks = true;		//Should change. There're 10 unnecessary computations
				String link = tagMatcher.group().trim();
				pageLinks.add(link);
			}
			//Exit if there are no links to retrieve
			if(!hasLinks) break;
			links.add(pageLinks);
			pages += LINKS_PER_PAGE;
			
//			if(pages > 9) return;
		}
	}

	/**
	 * Once all the html <a> links are retrieved, then they have to be cleaned in order to keep working links.
	 */
	private void cleanLinks(){
		int index = 0;
		String subReferenceLink = null;
		StringTokenizer tokens = null;
		List<String> oldList = null;
		List<String> newList = null;
		List<List<String>> newLinks = new LinkedList<List<String>>();
		int size = links.size();
		
		for(int pos = 0; pos < size; pos++){
			oldList = links.get(pos);
			newList = new LinkedList<String>();
			for(String url : oldList){
				//Make sure that there's a reference and if so, keep the index
				//Otherwise stop looking and go to the next word
				index = 0;
				subReferenceLink = null;
				tokens = null;
				if((index = url.toLowerCase().indexOf("href", index)) == -1)
					continue;
				//Keep the reference of the equal sign
				if((index = url.toLowerCase().indexOf("=", index)) == -1)
					continue;
				//Catch any errors that might appear due to re-naming
				try{
					//Cut the Substring and then convert it into tokens by cleaning up useless data
					subReferenceLink = (url.substring(index+1)).trim();
					tokens = new StringTokenizer(subReferenceLink, "\t\n\r\"<>#'");
					subReferenceLink = (tokens.hasMoreElements() ? tokens.nextToken() : null);
					//There's no need to reconnect and verify that they are legit links - we 'trust' Google
					newList.add(subReferenceLink);
					//Add to the list of all links
					//Takes linear time, but only few nanoseconds in total for hundreds of links
					if(ALLOW_DUPLICATE_LINKS && !allLinks.contains(subReferenceLink)){
						allLinks.add(subReferenceLink);	//Avoid Duplicate links
					}else{
						allLinks.add(subReferenceLink);	//Allow Duplicate links
					}
				}catch(Exception e){
					if(Global.DEBUG) System.out.println("ERROR : " + e.toString());
			    	continue;
				}
			}
			//Replace the previous list with the clean one
			if(!newList.isEmpty()){
				newLinks.add(newList);
			}
		}
		//Replace old list with new list
		links = newLinks;
	}
	
	/**
	 * Once Google have queried all the links, this method can be called in order to display the results.
	 */
	public void print(){
		System.out.println("Total Links " + (ALLOW_DUPLICATE_LINKS ? "Unique [ " : " [") + allLinks.size() + " ] - Pages [ " + links.size() + " ] ");
		for(int position = 0; position < links.size(); position++){
			System.out.println("Page [ " + position + " ] - Size [ " + links.get(position).size() + " ]");
			for(String link : links.get(position)){
				System.out.println("\t[ " + link + " ]");
			}
		}
	}
	
//	public static void main(String[] args){
//		try{
//			Scanner scanner = new Scanner(System.in);
//			GoogleQuery googleQuery = new GoogleQuery();
//			
//			while(true){
//				try{
//					System.out.println("-------------------------------");
//					System.out.print("\nQuery : " );
//					String line = scanner.nextLine();
//					//Exit if the user wants to quit
//					if(line.equalsIgnoreCase("exit") || line.equalsIgnoreCase("quit")) break;
//					//Ask the user again if he wants to query in google
//					if(line.matches(".+?\\.[a-zA-Z]{2,6}")){
//						System.out.println("Invalid input!");
//						continue;
//					}
//					//Get all the links in a web-site
//					googleQuery.Update(line);
//					googleQuery.print();
//				}catch(Exception e){
//					System.out.println("Error : " + e.getMessage());
//				}
//			}
//		}catch(Exception e){
//			System.err.println("Error : " + e.toString());
//			e.printStackTrace();
//		}
//	}
}