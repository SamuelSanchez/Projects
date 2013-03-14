package simulation.learning.naiveBayes;

import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.List;
import java.util.Vector;

public class NaiveBayesClassifier {
	
	//Variables
	private int numberOfFeatures;
	private Map<String,TargetValue> targetValueSet;
	private Vector<Set<String>> dictionaryOfFeatures;
	private static final boolean DEBUG = false;
	private int totalNumberOfSets;
	private double probabilityOfTargetValue;
	
	public NaiveBayesClassifier(int numberOfFeatures){
		this.numberOfFeatures = numberOfFeatures;
		this.totalNumberOfSets = 0;
		this.probabilityOfTargetValue = 0;
		this.targetValueSet = new HashMap<String,TargetValue>();
		
		//Instantiate dictionary of features for each feature set
		this.dictionaryOfFeatures = new Vector<Set<String>>();
		for(int i = 0; i < numberOfFeatures; i++)
			dictionaryOfFeatures.add(new HashSet<String>());
	}
	
	//Add features during training data
	public void addFeature(String feature, int position, String targetValue, boolean isNextSet){
		//Create a TargetValue if it doesn't exist
		if(!targetValueSet.containsKey(targetValue))
			targetValueSet.put(targetValue, new TargetValue(targetValue, numberOfFeatures));
		
		//Increase the number of features for this Target value
		targetValueSet.get(targetValue).addFeature(feature, position);
		
		//Add into dictionary a feature for the feature set at position 'position'
		dictionaryOfFeatures.get(position).add(feature);
		
		//Only when testing a different set of features(in another line), update the number of sets
		if(isNextSet){
			targetValueSet.get(targetValue).increaseTargetValueCount();
			totalNumberOfSets++;
			
			if(DEBUG){
				targetValueSet.get(targetValue).print();
			}
		}
	}
	
	//Update probability after reading training data to use with testing data
	public void updateProbability(){
		Set<String> set = targetValueSet.keySet();
			
		for(String featureName : set){
			targetValueSet.get(featureName).setDictionaryOfFeatures(dictionaryOfFeatures);
			targetValueSet.get(featureName).setTotalTargetValuesCount(totalNumberOfSets);
			targetValueSet.get(featureName).updateProbability();
			
			if(DEBUG){
				targetValueSet.get(featureName).print();
			}
		}
	}
	
	//Find the Target Value with the highest probability for the set of features
	//Throws an exception if a new feature is found that was not introduced during training data
	public String getTargetValueForFeatures(List<String> features) throws NaiveBayesException{
		String nameOfTargetValue = null;
		Set<String> targets = targetValueSet.keySet();
		TargetValue tempTargetValue = null;
		double tempProbability = 0;
		
		//Iterate through all targets to find the highest probability for the list of features
		for(String target: targets){
			tempTargetValue = targetValueSet.get(target);
			double localProbability = 1;// -> (identity * anything) = anything
			
			//Get the probability of the features for a target value
			for(int position = 0; position < features.size(); position++){
//				System.out.println("p( " + features.get(position) + " | " + target + " ) [ " + position + " ] = " + tempTargetValue.getProbability(features.get(position), position));
				localProbability *= tempTargetValue.getProbability(features.get(position), position);
			}
			//Get the probability of the feature itself
			localProbability *= tempTargetValue.getProbability();
//			System.out.println("p( " + target + " ) = " + localProbability);
			
			//Only care if there's a higher probability.
			//If it's the same probability, then ignore
			if(tempProbability < localProbability){
				tempProbability = localProbability;
				nameOfTargetValue = target;
				
				//Store the probability
				probabilityOfTargetValue = localProbability;
			}
		}
		
		if(DEBUG){
			System.out.println("TargetValue : " + nameOfTargetValue);
		}
		
		if(nameOfTargetValue == null){
			System.out.println("Target Value is Null!");
			nameOfTargetValue = "UNKNOWN";
		}
		
		return nameOfTargetValue;
	}
	
	public double getProbability(){
		//Reset probability after asking for it
		double tempProb = probabilityOfTargetValue;
		probabilityOfTargetValue = 0;
		return tempProb;
	}
	
}
