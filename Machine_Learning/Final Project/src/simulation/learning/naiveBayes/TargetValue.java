package simulation.learning.naiveBayes;

import java.util.HashMap;
import java.util.Vector;
import java.util.Map;
import java.util.Set;

public class TargetValue {
    //Variables
	private String name;
	private Vector<Map<String,Feature>> featureSet;
	private Vector<Set<String>> dictionaryOfFeatures;
	private int totalNumber_this_targetValue;
	private int totalNumber_all_targetValues;
	private double probability;
	
	public TargetValue(String name, int totalNumberOfFeatures){
		this.name = name;
		totalNumber_all_targetValues = 0;
		totalNumber_this_targetValue = 0;
		probability = 0.0;
		this.dictionaryOfFeatures = null;
		
		//Instantiate containers for features
		featureSet = new Vector<Map<String,Feature>>();
		for(int i = 0; i < totalNumberOfFeatures; i++){
			featureSet.add(new HashMap<String,Feature>());
		}
	}
	
	//Features are case sensitive
	//Increase the number of Features for this Target Value with training data
	public void addFeature(String feature, int position){
		//Create a feature if it doesn't exist
		if(!featureSet.get(position).containsKey(feature)){
			featureSet.get(position).put(feature, new Feature(feature));
			featureSet.get(position).get(feature).setLocation(position);
		}
		
		//Increase the number of features for this Target value
		featureSet.get(position).get(feature).increaseCount();
	}
	
	public void setDictionaryOfFeatures(Vector<Set<String>> dictionary){
		this.dictionaryOfFeatures = dictionary;
	}
	
	//Get the probability of each feature for this Target value
	//after training the data to be used with the testing data
	public void updateProbability(){
		for(Map<String,Feature> set : featureSet){
			Set<String> features = set.keySet();
			
			for(String featureName : features){
				set.get(featureName).findProbability(totalNumber_this_targetValue);
			}
		}
		
		probability = (double) totalNumber_this_targetValue/totalNumber_all_targetValues;
	}
	
	public void increaseTargetValueCount(){
		totalNumber_this_targetValue++;
	}
	
	public void setTotalTargetValuesCount(int total){
		totalNumber_all_targetValues = total;
	}
	
	//Get the probability of a feature for the testing data
	//Throws an exception if there's a new feature that was not introduced during training data
	public double getProbability(String feature, int position) throws NaiveBayesException{
		Feature tempFeature = featureSet.get(position).get(feature);
		
		//Let's check if the feature that we are looking for has a probability,
		//has no probability or doesn't belong to the Set of features
		if(tempFeature == null){
			//If we didn't start the dictionary
			if(dictionaryOfFeatures == null){
				return 0;
			}
			
			Set<String> tempDictionary = dictionaryOfFeatures.get(position);
			
			//If we have no probability for a feature but it exists in a dictionary then add it to the set
			if(tempDictionary.contains(feature)){
				featureSet.get(position).put(feature, new Feature(feature));
				tempFeature = featureSet.get(position).get(feature);
			}
			else
				//This should not send an exception but rather add it to the map or sent probability back of 0
				//throw new NaiveBayesException("Feature [ " + feature + " ] not Found at Position [ " + position + " ]");
				return 0.0;
		}
		
		return tempFeature.getProbability();
	}
	
	//Gets the probability of this Target value among the Target value set
	public double getProbability(){
		return probability;
	}
	
	public String getName(){
		return name;
	}
	
	//Debug this class' values
	public void print(){
		System.out.println("Target Value Name [ " + name + " ] - Probability [ " + probability +
				           " ] - Count [ " + totalNumber_this_targetValue + " ] - Total [ " + totalNumber_all_targetValues + " ]");
//		for(Map<String,Feature> set : featureSet){
//			Set<String> features = set.keySet();
//			
//			for(String featureName : features){
//				set.get(featureName).print();
//			}
//		}
	}
}
