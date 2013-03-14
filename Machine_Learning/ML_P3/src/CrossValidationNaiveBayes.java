import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class CrossValidationNaiveBayes {
	
	private Vector<Double> foldMean;				//Keeps the mean for every fold
	private List<String> dataList;					//Keeps the training data into a list
	private List<String> targetValues;				//Keeps the actual target value for our testing data in order to find its accuracy
	private double meanTotal;						//Total mean
	private double standardDeviationTotal;			//Total SD
	private int foldSize;							//Keeps the size for each fold
	private int totalFolds;							//The total number of folds
	private int totalData;							//The total data in the training file
	private File trainingFile;						//Training data file name
	private boolean programCanExecute;				//Test whether the program has validated its data
	private boolean programCanWriteData;			//Test whether the program has run successfully
	private static final String TRAINING_TEMP_NAME = "trainingTemp";
	private static final String TESTING_TEMP_NAME = "testingTemp";
	private static final String PREDICTION_TEMP_NAME = "predictionTemp";
	private static final String DELIMITER = ",";	//Let's assume that the delimiter is comma
	private static final boolean DEBUG = false;
	private static final String NEWLINE = System.getProperty("line.separator");
	
	//Prevent 0 folds to happen
	public CrossValidationNaiveBayes() throws Exception{
		this(0);
	}
	
	public CrossValidationNaiveBayes(int folds) throws Exception{
		//Prevent invalid number of folds
		if(folds<0){
			throw new Exception("Error : Negative folds are not allowed!");
		}
		
		this.totalFolds = folds;
		this.foldMean = new Vector<Double>(folds);
		this.dataList = new ArrayList<String>();
		this.programCanExecute = false;
		this.programCanWriteData = false;
		this.meanTotal = 0;
		this.standardDeviationTotal = 0;
	}
	
	//Validate the training file and
	//copy its data into an array list
	public void init(String file) throws Exception{
		//Display State
		if(DEBUG){
			System.out.println("--------------------------");
			System.out.println("INIT STATE");
			System.out.println("--------------------------" + NEWLINE);
		}
		
		trainingFile = new File(file);
		
		//Check if the file exists and it can be read
		if(!trainingFile.canRead()){  
			throw new Exception("Error : File [ " + file + " ] cannot be read");
		}
		
		//Let's copy the file in a List
		BufferedReader reader = new BufferedReader( new InputStreamReader( new FileInputStream(trainingFile)));
		String line = reader.readLine();
		
		//Iterate through the end of the file
		while(line != null){
			//Avoid empty lines
			if(!line.isEmpty()){
				dataList.add(line);
				
				//Print out the file that we are reading
				if(DEBUG) System.out.println(line);
			}
			line = reader.readLine();
		}
		
		totalData = dataList.size();
		foldSize = totalData / totalFolds;
		reader.close();
		
		//Print out the data that we obtained
		if(DEBUG){
			System.out.println("Total Data  : " + totalData);
			System.out.println("Fold Size   : " + foldSize);
			System.out.println("Fold Number : " + totalFolds);
		}
		
		//If we are reading an empty file, then let's notify the user
		if(totalData == 0){
			throw new Exception("Error : The file does not contain any data to test!");
		}
		
		//Let the user be able to execute the program
		this.programCanExecute = true;
	}
	
	//Traverse through all folds and let's cross validate the algorithm
	public void execute() throws Exception{
		//Display State
		if(DEBUG){
			System.out.println("--------------------------");
			System.out.println("EXECUTE STATE");
			System.out.println("--------------------------" + NEWLINE);
		}
		
		//Do not execute if data wasn't validated by init()
		if(!programCanExecute) return;
		
		//Files that will be created in order to test BayesClassification
		//Store the names so that they can be deleted later
		List<String> filesToBeDeleted = new ArrayList<String>();
		
		int fold_index = 0;
		
		//Traverse all the folds
		for(int i = 0; i < totalFolds; i++){
			//Create a temporary a training temp file and a testing temp file
			PrintWriter trainingTemp = new PrintWriter(new FileWriter(TRAINING_TEMP_NAME+i+".txt"));
			PrintWriter testingTemp = new PrintWriter(new FileWriter(TESTING_TEMP_NAME+i+".txt"));
			
			List<String> testingDataList = new ArrayList<String>(foldSize);
			
			//Let's validate the NaiveBayes Program for every fold
			for(int f = 0; f < totalData; f++){
				//If we are in range of the fold, then let's keep in our testing data
				if(f >= fold_index && f < (foldSize + fold_index)){
					testingTemp.write(dataList.get(f) + NEWLINE);
					testingDataList.add(dataList.get(f));
					
					//Print out the testing data
					if(DEBUG){
						System.out.println("Fold [ " + i + " ] - Line [ " + f + " ] - Fold Index [ " + fold_index + " ]" +
								           " - Fold End [ " + (foldSize + fold_index) + " ] - Fold Size [ " + foldSize + " ]");
					}
				}
				//Any other data than our fold, let's keep it in our training data
				else{
					trainingTemp.write(dataList.get(f) + NEWLINE);
					
					if(DEBUG){
						System.out.println("Fold [ " + i + " ] - Line [ " + f + " ]");
					}
				}
			}
			//Flush the buffer and close the file so that another program can use it
			trainingTemp.flush();
			testingTemp.flush();
			trainingTemp.close();
			testingTemp.close();
			
			//After we have written our data to files, let's cross-validate NaiveBayes classifier
			checkCrossValidation(testingDataList, TRAINING_TEMP_NAME+i+".txt", TESTING_TEMP_NAME+i+".txt", PREDICTION_TEMP_NAME+i+".txt");
			
			//Increase the fold index to iterate to the next fold
			fold_index += foldSize;
			
			//Add files names to the List to be deleted later
			filesToBeDeleted.add(TRAINING_TEMP_NAME+i+".txt");
			filesToBeDeleted.add(TESTING_TEMP_NAME+i+".txt");
			filesToBeDeleted.add(PREDICTION_TEMP_NAME+i+".txt");
		}
		
		//Find the mean and Standard-Deviation
		findMeanAndStandardDeviation();
		
		//Let the user write the data
		this.programCanWriteData = true;
		
		//Once all files have been used delete them
		deleteFilesCreated(filesToBeDeleted);
	}
	
	//Testing NaiveBayes classifier
	private void checkCrossValidation(List<String> testingDataList, String trainingFile, String testingFile, String predictionFile) throws Exception{
		//Display State
		if(DEBUG){
			System.out.println("--------------------------");
			System.out.println("CROSS VALIDATION STATE");
			System.out.println("--------------------------" + NEWLINE);
		}
		
		//Make sure that the testing data list is the same size as the fold size
		//If this exception occurs, then something is really wrong!   d[o_O]b"
		if(testingDataList.size() != foldSize){
			throw new Exception("Error : Testing data list size does not equal to the fold size!");
		}
		
		//Let's check the number of features of our training data
		String featureLine = dataList.get(0);
		String[] features = featureLine.split(DELIMITER);
		int numberOfFeatures = (features.length)-1;
		targetValues = new ArrayList<String>(foldSize);
		
		//Print out data
		if(DEBUG){
			System.out.println("Number of Features (less Target Value) : " + numberOfFeatures);
		}
		
		//Also let's keep the target vector of our testing data
		//in order to compare it to the target values produce by the NaiveBayes classifiers
		for(String feature : testingDataList){
			features = feature.split(DELIMITER);
			
			//Make sure that all Testing data have the same number of features
			if(features.length != (numberOfFeatures+1)){
				throw new Exception("Error : Inconsistency number of features!");
			}
			
			targetValues.add(features[numberOfFeatures]);
			
			//Print out data
			if(DEBUG){
				System.out.println("Features : " + feature);
				System.out.println("Actual Target Value : " + features[numberOfFeatures]);
			}
		}
		
		//Run the NaiveBayes classifier
		NaiveBayes naiveBayes = new NaiveBayes(numberOfFeatures);
		naiveBayes.readTrainingData(trainingFile);
		naiveBayes.evaluateProbability();
		naiveBayes.readTestingDataAndWritePredictions(testingFile, predictionFile);
		
		//Let's get the mean value of every fold
		List<String> naiveBayesPrediction = naiveBayes.getPredictionsList();
		
		double meanForFold = 0;
		
		//Let's make sure that both list have the same size
		//This might through an error because NaiveBayes List size is created dynamically
		//we didn't specify a size for it when it was created
		if(naiveBayesPrediction.size() != targetValues.size()){
			throw new Exception("Error : Actual target value list size [ " + targetValues.size() + " ] " +
					            "and predictions [" + naiveBayesPrediction.size() + " ] do not match!");
		}
		
		//Check accuracy
		for(int pos = 0; pos < foldSize; pos++){
			if(targetValues.get(pos).equals(naiveBayesPrediction.get(pos))){
				meanForFold++;
				
				//Matched Target Value
				if(DEBUG){
					System.out.println("MATCHED   : Target Value [ " + targetValues.get(pos) + " ] - NaiveBayes [ " + naiveBayesPrediction.get(pos) + " ]");
				}
			}
			
			//Not match Target Value
			if(DEBUG){
				System.out.println("unmatched : Target Value [ " + targetValues.get(pos) + " ] - NaiveBayes [ " + naiveBayesPrediction.get(pos) + " ]");
			}
		}
		
		if(DEBUG){
			System.out.println("Accuracy [ " + meanForFold + " / " + foldSize + " = " + (meanForFold/foldSize) + " ]");
		}
		
		meanForFold /= foldSize;
		foldMean.add(meanForFold);
		
		targetValues.clear();
	}
	
	private void findMeanAndStandardDeviation(){
		//Display State
		if(DEBUG){
			System.out.println("--------------------------");
			System.out.println("FIND U & SD STATE");
			System.out.println("--------------------------" + NEWLINE);
		}
		
		for(Double mean : foldMean){
			meanTotal += mean.doubleValue();
		}
		meanTotal /= totalFolds;
		
		double tempSD = 0;
		for(Double mean : foldMean){
			tempSD += ((mean - meanTotal) * (mean - meanTotal));
		}
		
		standardDeviationTotal = tempSD / totalFolds;
		standardDeviationTotal = Math.sqrt(standardDeviationTotal);
		
		if(DEBUG){
			System.out.println("Mean : " + meanTotal);
			System.out.println("SD   : " + standardDeviationTotal);
		}
	}
	
	public void printResults(){
		//Write only if the user has executed the program successfully
		if(!programCanWriteData) return;
		
		for(int pos = 0; pos < totalFolds; pos++){
			System.out.println("Fold [ " + pos + " ] - Accuracy [ " + foldMean.get(pos) + " ]");
		}
		
		System.out.println("Total Mean [ " + meanTotal + " ]");
		System.out.println("Standard Deviation [ " + standardDeviationTotal + " ]");
		
	}
	
	//Delete the files we just created
	//This might slow the program down for few milliseconds
	private void deleteFilesCreated(List<String> filesToDelete){
		//Unfortunately, this does not delete all files.
		for(String file : filesToDelete){
			File trainingTempFile = new File(file);
			if(trainingTempFile.exists()){
				if(trainingTempFile.isFile()) trainingTempFile.delete();
			}
		}
	}
	
	public Vector<Double> getMeanForFolds(){
		return foldMean;
	}
	
	public double getMean(){
		return meanTotal;
	}
	
	public double getStandardDeviation(){
		return standardDeviationTotal;
	}
	
	public static void displayErrorMessage(){
		System.err.println("Usage : java CrossValidationNaiveBayes <training file> <folds>" + NEWLINE +
							"\tLower number of folds will throw an Exception because the training" + NEWLINE +
							"\tdata must have all variables declared for all the features that it will test.");
		System.exit(1);
	}
	
	public static void main(String[] args){
		try{
			if(args.length != 2){
				displayErrorMessage();
			}

			CrossValidationNaiveBayes crossValidationNaiveBayes = new CrossValidationNaiveBayes(Integer.parseInt(args[1]));
			crossValidationNaiveBayes.init(args[0]);
			crossValidationNaiveBayes.execute();
			crossValidationNaiveBayes.printResults();
			
		}catch(Exception e){
			System.out.println(e.toString());
			e.printStackTrace();
		}
	}
}
