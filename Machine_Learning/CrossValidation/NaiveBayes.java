import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class NaiveBayes {
	private NaiveBayesClassifier naiveBayesClassifier;
	public static NaiveBayes naiveBayes_six_features = new NaiveBayes(6);
	private ArrayList<String> predictions;
	private static final String DELIMETER = ",";
	private int numberOfFeatures;
	public static final String NEWLINE = System.getProperty("line.separator");
	
	//Should be private when knowing the number of features in advance
	//But since it can be any number of features, let's leave it public
	//Prevent the compiler from creating the default constructor
	public NaiveBayes(){
		this(0);
	}
	
	public NaiveBayes(int numberOfFeatures){
		this.numberOfFeatures = numberOfFeatures;
		naiveBayesClassifier = new NaiveBayesClassifier(numberOfFeatures);
		predictions = new ArrayList<String>();
	}

	public void displayErrorMessage(){
		System.err.println("Error : training_data testing_data and predictions must be given as arguments");
		System.exit(1);
	}
	
	//Read training data file and populate the NaiveBayes Classifier
	public void readTrainingData(String trainingData) throws Exception{
		BufferedReader trainingDataFile = new BufferedReader( new InputStreamReader( new FileInputStream(trainingData) ) );
		String trainingDataLine = trainingDataFile.readLine();
		
		while(trainingDataLine != null){
			String[] features = trainingDataLine.split(DELIMETER);
			
			//Make sure that we have the right amount of features
			if(features.length != numberOfFeatures+1)
				throw new Exception("Error : number of features [ " + features.length + " ] does not match [ " + numberOfFeatures+1 + " ]");
			
			//Iterate through all features except the target value, which is assumed to be at the last position
			for(int position = 0; position < numberOfFeatures; position++){
				naiveBayesClassifier.addFeature(features[position].trim(), position, features[numberOfFeatures].trim(), position+1 == numberOfFeatures);
			}
			
			trainingDataLine = trainingDataFile.readLine();
		}
	}
	
	public void evaluateProbability(){
		naiveBayesClassifier.updateProbability();
	}
	
	public void readTestingDataAndWritePredictions(String testingData, String predictionsData) throws Exception{
		BufferedReader testingDataFile = new BufferedReader( new InputStreamReader( new FileInputStream(testingData) ) );
		PrintWriter predictionsFile = new PrintWriter(new FileWriter(predictionsData));
		String testingDataLine = testingDataFile.readLine();
		predictions = new ArrayList<String>();
		
		while(testingDataLine != null){
			String[] features = testingDataLine.split(DELIMETER);
			List<String> featuresList = new ArrayList<String>();
			
			//Iterate through all features except the target value, which is assumed to be at the last position
			for(int position = 0; position < numberOfFeatures; position++){
				featuresList.add(features[position].trim());
			}

			predictions.add(naiveBayesClassifier.getTargetValueForFeatures(featuresList));
			testingDataLine = testingDataFile.readLine();
		}
		
		for(String prediction : predictions){
			//Find probability for the Set of features
			predictionsFile.write(prediction + NEWLINE);
		}
		predictionsFile.flush();
	}
	
	public List<String> getPredictionsList(){
		return predictions;
	}
	
	//Only the number of features must be specified, and it is assumed
	//that the targets value are located at the last row.
	public static void main(String[] args){
		NaiveBayes mainApp = NaiveBayes.naiveBayes_six_features;
		
		if(args.length != 3)
			mainApp.displayErrorMessage();
		
		try{
			mainApp.readTrainingData(args[0]);
			mainApp.evaluateProbability();
			mainApp.readTestingDataAndWritePredictions(args[1], args[2]);
			
		}catch(Exception e){
			System.err.println("Error : " + e.toString());
			e.printStackTrace();
		}
	}
	
}
