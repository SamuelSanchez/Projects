package webretrieval.html;


import java.util.Map;
import java.util.HashMap;
import java.util.Set;

/**
 * This class creates a map of the HTML tags with their corresponding Regular Expressions
 * @author Samuel E. Sanchez
 *
 */
public class HTMLTags {

	//Regular Expressions to retrieve the sentence of the word prefix by SENTENCE_INIT and post-fix by SENTENCE_END
	public static final String BODY = "BODY";
	public static final String BODY_INIT = "\\<body([^\\>]*)\\>";
	public static final String BODY_END = "\\</body([^\\>]*)\\>";
	public static final String ALL_TAGS = "\\</?[^\\>]+\\>";		/*"\\</?([^\\>]+)(\\>)?"*/
	public static final String OPEN_TAGS = "\\<([^\\>]+)(\\>)";
	public static final String CLOSE_TAGS = "\\</([^\\>]+)(\\>)";
	
	//HTML Tags public for the Dictionary
	public static final String LINK = "LINK";
	public static final String SCRIPT = "SCRIPT";
	public static final String NOSCRIPT = "NOSCRIPT";
	public static final String STYLE = "STYLE";
	public static final String COMMENT = "COMMENT";
	public static final String TITLE = "TITLE";
	public static final String META = "META";
	public static final String IMG = "IMG";
	public static final String A = "A";

	//Regular Expressions Associated with the HTML Tags
	private static final String LINK_REGEX = "\\<link([^\\>]+)\\>";
	private static final String SCRIPT_REGEX = "\\<script[^(\\>)]*\\>.*?\\</script\\>";
	private static final String NOSCRIPT_REGEX = "\\<noscript[^(\\>)]*\\>.*?\\</noscript\\>";
	private static final String STYLE_REGEX = "\\<style[^(\\>)]*\\>.*?\\</style\\>";
	private static final String COMMENT_REGEX = "\\<!--.*?-->";
	private static final String TITLE_REGEX = "\\<title([^\\>]*)\\>.*\\</title\\>";
	private static final String META_REGEX = "\\<meta([^\\>]+)\\>";
	private static final String IMG_REGEX = "\\<img([^\\>]+)\\>";
	private static final String A_REGEX = "\\<a([^\\>]+)\\>";
	
	//Necessary for the dictionary	
	private Map<String,String> htmlDictionary;
	public static HTMLTags INSTANCE = new HTMLTags();
	
	/**
	 * This private constructor creates the dictionary of the regular expressions and the tags
	 */
	//Populate HTML tags dictionary
	private HTMLTags(){
		this.htmlDictionary = new HashMap<String,String>();
		populateTags();
	}
	
	/**
	 * Checks if the tag is valid
	 * @param tag Tag to be searched
	 * @return True if the tag is in the dictionary, false otherwise
	 */
	//Look in the Dictionary if the HTML tag 'tag' exists
	public boolean isTagValid(String tag){
		return htmlDictionary.get( tag ) != null ? true : false;
	}
	
	/**
	 * Gets the regular expression give the tags
	 * @param tag Tag to be looked in the dictionary
	 * @return The regular expression relating to the tag
	 */
    //Get the Regular Expression associated with the tag if any
	public String getRegex(String tag){
		return htmlDictionary.get(tag);
	}
	
	/**
	 * Populates the dictionary
	 */
	private void populateTags(){
		this.htmlDictionary.put( LINK, LINK_REGEX );
		this.htmlDictionary.put( SCRIPT, SCRIPT_REGEX );
		this.htmlDictionary.put( NOSCRIPT, NOSCRIPT_REGEX );
		this.htmlDictionary.put( STYLE, STYLE_REGEX );
		this.htmlDictionary.put( COMMENT, COMMENT_REGEX );
		this.htmlDictionary.put( TITLE, TITLE_REGEX );
		this.htmlDictionary.put( META, META_REGEX );
		this.htmlDictionary.put( IMG, IMG_REGEX );
		this.htmlDictionary.put( A, A_REGEX );
//		this.htmlDictionary.put( HN, HN_REGEX );
	}
	
	/**
	 * Gets the all the tags found in the dictionary
	 * @return Tags listed in the dictionary
	 */
	public Set<String> getTags(){
		return this.htmlDictionary.keySet();
	}
}
