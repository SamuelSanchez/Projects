package simulation.learning.naiveBayes;
import java.util.ArrayList;
import java.util.List;

import simulation.learning.Action;
import simulation.learning.Classifier;
import simulation.learning.Obstacle;
import simulation.tool.Global;


//This class will round the (x,y) positions to the lowest 'MOD' for the position of the robot
//The lower the Threshold, the more request that it will ask the user
public class NaiveBayes implements Classifier {
	private NaiveBayesClassifier naiveBayesClassifier;
	
	//The number of features does not include the target value
	//Features = { x(left), y(left), x(center), y(center), x(right), y(right) }
	//Target = Action To Perform
	private static final int numberOfFeatures = 9;
	private static final int numberOfAgentsForDemonstration = 3;
	private int currentNumberOfFeatures;
	private static final int MOD = 10;
	private boolean show = true;
	
	public NaiveBayes(){
		this.naiveBayesClassifier = new NaiveBayesClassifier(numberOfFeatures);
		this.currentNumberOfFeatures = 0;
	}

	public void reset(){
		/*Reset the values of the training points*/
	}
	
	//Place training data and target value
	public void addTrainingData(List<Obstacle> trainingPoints, Global.ACTION action) throws Exception{
		System.out.println("Adding training data");
		String[] features = transformDataPoints(trainingPoints);
		
		//Make sure that we have the right amount of features
		if(features.length != numberOfFeatures)
			throw new Exception("Error : number of features [ " + features + " ] does not match [ " + numberOfFeatures + " ]");
		
		//Iterate through all features except the target value
		for(int position = 0; position < numberOfFeatures; position++){
			naiveBayesClassifier.addFeature(features[position].trim(), position, (action.toString()).trim(), position+1 == numberOfFeatures);
		}
		
		//Increase the number of features
		currentNumberOfFeatures++;
		
		//If the training data exceed the number established for training data, recompute probability
		if(currentNumberOfFeatures > Global.TRAINING_SIZE){
			System.out.println("Evaluating Probability");
			evaluateProbability();
		}
	}

	public void evaluateProbability(){
		System.out.println("******************************** Update Probability *****************************************");
		naiveBayesClassifier.updateProbability();
	}
	
	public Action getAdvise(List<Obstacle> trainingPoints) throws Exception {
		show = true;
		String[] features = transformDataPoints(trainingPoints);	
		List<String> featuresList = new ArrayList<String>();
		show = false;
		//Iterate through all features except the target value, which is assumed to be at the last position
		for(int position = 0; position < numberOfFeatures; position++){
			featuresList.add(features[position].trim());
		}

		Global.ACTION action = Global.ACTION.valueOf(naiveBayesClassifier.getTargetValueForFeatures(featuresList));
		double confidence = naiveBayesClassifier.getProbability();
		Action answer = new Action(action, confidence);
		
		if(Global.DEBUG){
			answer.print();
		}
		
		return answer;
	}

	private String[] transformDataPoints(List<Obstacle> trainingPoints) throws Exception{
		if(trainingPoints == null){
			throw new Exception("Error : Training data should not be null!");
		}
		
		if(trainingPoints.size() != numberOfAgentsForDemonstration){
			throw new Exception("Error : There should be [" + numberOfAgentsForDemonstration + "] per demonstration!");
		}
		
		String[] tempFeatures = new String[numberOfFeatures]; 
		int index = 0;
		
		//Retrieve data from training points - This is should be more dynamic
		for(Obstacle obstacle : trainingPoints){
			tempFeatures[index++] = (obstacle.getXPos() - (obstacle.getXPos() % MOD)) + "";
			tempFeatures[index++] = (obstacle.getYPos() - (obstacle.getYPos() % MOD)) + "";
			tempFeatures[index++] = obstacle.getId() + "";
			if(show){
				System.out.println("Id [" + obstacle.getId() + " ] - PosX [" + (obstacle.getXPos() - (obstacle.getXPos() % MOD)) + "] - PosY [" + (obstacle.getYPos() - (obstacle.getYPos() % MOD)) + "]");
			}
		}
		
		return tempFeatures;
	}
}
