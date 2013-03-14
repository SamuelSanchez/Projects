package webretrieval.tools;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import webretrieval.html.HTMLImage;

/**
 * Creates a class that will find sentences and paragraphs given a document
 * 
 * @author Samuel E. Sanchez
 *
 */
public class WordFinder {

	//Prevent from making Object of this class all methods must be static
	private WordFinder(){
		/*Do nothing*/
	}
	
	/**
	 * Returns a maps of all the words and matches for that word
	 * @param words Words to be searched
	 * @param document Document to search the words from
	 * @return List of sentences if there is any matched, null otherwise
	 */
	public static Map<String, List<String>> getSentences(Set<String> words, String document){
		if(words == null || words.size() == 0 || document == null){
			return null;
		}
		return findMatches(words, document, false);
	}
	
	/**
	 * Returns a maps of all the words and matches for that word
	 * @param words Words to be searched
	 * @param document Document to search the words from
	 * @return List of paragraph if there is any matched, null otherwise
	 */
	public static Map<String, List<String>> getParagraphs(Set<String> words, String document){
		if(words == null || words.size() == 0 || document == null){
			return null;
		}
		return findMatches(words, document, true);
	}
	
	/**
	 * Returns a maps of all the words and matches for that word
	 * @param words Words to be searched
	 * @param document Document to search the words from
	 * @param keepParagraph true if paragraph is to be retrieve, otherwise sentence
	 * @return List of paragraph if there is any matched, null otherwise
	 */
	//Avoid word duplicates - use a Set
	//Document should be a String containing all the words that we are looking for 
	private static Map<String, List<String>> findMatches(Set<String> words, String document, boolean keepParagraph){
		if(words == null || words.size() == 0 || document == null){
			return null;
		}
		//Variables to be used
		Map<String, List<String>> wordsMatches = new HashMap<String, List<String>>();
		Pattern[] keyWordPattern = new Pattern[words.size()];
		Matcher[] keyWordMatcher = new Matcher[words.size()];
		int index = 0;
		//Compile regular expressions for key words case insensitive
		for(String word : words){
			keyWordPattern[index] = Pattern.compile((keepParagraph ? Global.PARAGRAPH_INIT : Global.SENTENCE_INIT) + word 
							+ (keepParagraph ? Global.PARAGRAPH_END : Global.SENTENCE_END), Pattern.CASE_INSENSITIVE);
			index++;
		}
		
		index = 0;
		Set<String> wordsNoRepetitions = null;
		//Look for matches in the document for every word
		for(String word : words){
			wordsNoRepetitions = new HashSet<String>();
			keyWordMatcher[index] = keyWordPattern[index].matcher(document);
			//Get all the sentences for this tag
			while(keyWordMatcher[index].find()){
				wordsNoRepetitions.add(keyWordMatcher[index].group());
			}
			//Change from Set to List because it's much easier to iterate
			wordsMatches.put(word, new LinkedList<String>(wordsNoRepetitions));
			index++;
		}
		return wordsMatches;
	}
	
	/**
	 * Returns the best matches sentences given the document and the words
	 * @param word
	 * @param document
	 * @return
	 */
	//Give a document as a string
	//word can be also a sentence as well
	public static String getBestMatchSentence(String word, String document){
		if(document == null || word == null){
			return null;
		}
		//Return the best match sentence
		return getBestMatch(word, document, false);
	}
	
	/**
	 * Returns the best matches sentences given the document and the words
	 * @param word
	 * @param document
	 * @return
	 */
	//Give a document as a string
	//word can be also a sentence as well
	public static String getBestMatchParagraph(String word, String document){
		if(document == null || word == null){
			return null;
		}
		//Return the best match paragraph
		return getBestMatch(word, document, true);
	}
	
	//Find the best match given the document as a String and 
	//the word (word can be a sentence, which is a list of words)
	//the boolean defines if you want to keep the sentence of the paragraph - false keeps sentences
	//--  For the same word(s) to look in the document, the best matched sentence might be different from the best matched paragraph
	//--  The best matched is measured by weight of word in document.
	private static String getBestMatch(String word, String document, boolean keepParagraph){
		if(document == null || word == null){
			return null;
		}
		//Split by sentences or paragraphs - Split by the end of the paragraph which is the beginning of another
		String[] sentences = document.split((keepParagraph ? Global.SPLIT_PARAGRAPH : Global.SPLIT_SENTENCE));
		String[] keywords = word.split(Global.SPACE); //The word might be a sentence
		int counter;
		int position = 0;
		int max = 0;
		
		for(int i = 0; i < sentences.length; i++){
			counter = 0;
			//Iterate through every part of the word given to search
			for(String partialWord : keywords){
				//Count the number of times that this sentence/paragraph
				//contains a word that we are looking for
				if(sentences[i].contains(partialWord)){
					counter++;
				}
			}
			//Let's keep this sentence/paragraph if it ha
			//the highest number of encounters with the word given to search
			if(counter > max){
				max = counter;
				position = i;
			}
		}
		//Avoid Index out of bounds exception
		if(sentences.length == 0)
			return null;
		//return the best match
		return sentences[position];
	}
	
	/**
	 * Returns the best HMLT Image given the string
	 * @param images
	 * @param word
	 * @return
	 */
	public static HTMLImage getBestMatchImage(List<Object> images, String word){
		if(images == null || images.size() == 0 || !(images.get(0) instanceof HTMLImage) ||  word == null){
			return null;
		}
		
		String[] keywords = word.split(Global.SPACE); //The word might be a sentence
		int counter;
		int position = 0;
		int max = 0;
		
		//Iterate through all images
		for(int i = 0; i < images.size(); i++){
			counter = 0;
			//Iterate through every part of the word given to search
			for(String partialWord : keywords){
				//Count the number of times that this sentence/paragraph
				//contains a word that we are looking for
				if(((HTMLImage)images.get(i)).alt != null && ((HTMLImage)images.get(i)).alt.contains(partialWord)){
					counter++;
				}
			}
			//Let's keep this sentence/paragraph if it ha
			//the highest number of encounters with the word given to search
			if(counter > max){
				max = counter;
				position = i;
			}
		}
		//Return the best match
		return (HTMLImage) images.get(position);
	}
}