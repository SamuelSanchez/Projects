import java.io.*;
import java.util.*;


public class MainApp {
	private LinearRegressionParser linearRegressionParser;
	private LinearRegression linearRegression;
	private static final String NEWLINE = System.getProperty("line.separator");
	
	public MainApp( String[] args ) throws Exception{
		if( args == null || args.length != 4 ){
			throw new Exception( "Error : Arguments " + ( args == null ? "is null!" : "is not of length 4" ));
		}
		
		this.linearRegressionParser = new LinearRegressionParser( args[0], args[1]);
		this.linearRegression = new LinearRegression( Integer.parseInt( args[2] ), Double.parseDouble( args[3] ) );
	}
	
	public void execute() throws LinearRegressionException, IOException{
		linearRegressionParser.init();
		linearRegression.createTrainingModel( linearRegressionParser.getTrainingData(), linearRegressionParser.getTrainingTargets() );
		linearRegression.createTestingModel( linearRegressionParser.getTestingData(), linearRegressionParser.getTestingTargets() );
		System.out.println( linearRegression.getTrainingError() + ", " + linearRegression.getTestingError() );
	}
	
	public void execute( int maxDegree, double maxLamda ) throws LinearRegressionException, IOException{
		this.execute( 0, maxDegree, 1, 0.0, maxLamda, 1.0 );
	}
	
	public void execute( int maxDegree, double maxLamda, double lamdaIncrement) throws LinearRegressionException, IOException{
		this.execute( 0, maxDegree, 1, 0.0, maxLamda, lamdaIncrement);
	}
	
	public void execute( int maxDegree, double minLamda, double maxLamda, double lamdaIncrement) throws LinearRegressionException, IOException{
		this.execute( 0, maxDegree, 1, minLamda, maxLamda, lamdaIncrement);
	}
	
	public void execute( int minDegree, int maxDegree, double minLamda, double maxLamda, double lamdaIncrement ) throws LinearRegressionException, IOException{
		this.execute( minDegree, maxDegree, 1, minLamda, maxLamda, lamdaIncrement);
	}
	
	
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//This function helps to find the best parametrization, such as Lamda, degree of polynomial and overfitting. //
	//This functions should be broken down into many parts for an easier understanding.                          //
	//Ignore the below function. It is not asked by the homework, but it was helpful for me to play with the data//
	///////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/* Find the best parametrization such as Lamda and the degree of polynomial, 
	 * the most over-fitting given the range of lamda and degree. 
	 * Also write the iterations and results into a CVS file
	 */
	public void execute( int minDegree, int maxDegree, int degreeIncrement, double minLamda, double maxLamda, double lamdaIncrement ) throws LinearRegressionException, IOException{
		linearRegressionParser.init();
		
		//////////////////////////////////////////////////////////////
		//Find the Polynomial that best fits the curve with Lamda 0 //
		//////////////////////////////////////////////////////////////
		
		/* Look at the minimum value among the Testing Data with Lamda 0 */
		/* Overfitting : Look at the difference when going from one degree to another in the training data */
		//BufferedWriter regularization = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( "NoRegularization.cvs" )));
		Map<Double, Integer> bestPolynomialStimate = new TreeMap<Double, Integer>();
		Map<Double, Integer> overFittingStimate = new TreeMap<Double, Integer>();
		int bestDegree = -1;  // -1 is an invalid Degree for a polynomial in my program
		int overFittingDegree = -1;
		double overFittingDiff = 0.0;
		double prevTrainingError = 0.0;
		for( int tempDegree = minDegree; tempDegree <= maxDegree; tempDegree += degreeIncrement ){
			try{
				//With the same Document only Modify the Polynomial and Lamda = 0
				linearRegression.setPolynomialDegree( tempDegree );
				linearRegression.setLamda( 0 );
				linearRegression.createTrainingModel( linearRegressionParser.getTrainingData(), linearRegressionParser.getTrainingTargets() );
				linearRegression.createTestingModel( linearRegressionParser.getTestingData(), linearRegressionParser.getTestingTargets() );
				
				bestPolynomialStimate.put( linearRegression.getTestingError(), linearRegression.getPolynomialDegree() );
				//regularization.write( linearRegression.getPolynomialDegree() + "," + linearRegression.getLamda() + "," + linearRegression.getTrainingError() + "," + linearRegression.getTestingError() );
				//regularization.write( "\n" );
				if( tempDegree != minDegree ){
					//System.out.println( "Prev Training Error : " + prevTrainingError + " - Now : " + linearRegression.getTrainingError() );
					//System.out.println( "Subs : " + Math.abs( prevTrainingError - linearRegression.getTrainingError() ) + " - Degree : " + linearRegression.getPolynomialDegree() );
					overFittingStimate.put( Math.abs(prevTrainingError - linearRegression.getTrainingError()), linearRegression.getPolynomialDegree() );
				}
				
				//Keep the previous error for the next iteration
				prevTrainingError = linearRegression.getTrainingError();
				
			}catch( Exception e ){
				System.out.println( "Error when testing the Degree : " + e.toString() );
			}
		}
		//regularization.close();

		//Only get the first value which is the minimum one
		LinkedList<Double> testing = new LinkedList<Double>( bestPolynomialStimate.keySet() );
		for( Double tempDouble : testing ){
			bestDegree = bestPolynomialStimate.get( tempDouble );
			//System.out.println("List for the best error [ " + tempDouble + " ] - Polynomial [ " + bestPolynomialStimate.get( tempDouble ) + " ]");
			break;  //Don't compute uneccesary data
		}
		//Free unnecessary Memory
		bestPolynomialStimate.clear(); 
		testing.clear();
		
		//Get the Overfitting
		testing = new LinkedList<Double>( overFittingStimate.keySet() );
		overFittingDegree = overFittingStimate.get( testing.get( testing.size()-1 ) );
		overFittingDiff = testing.get( testing.size()-1 );
		//System.out.println("Overfitting : Squared Error [ " + overFittingDiff + " ] - Polynomial [ " + overFittingDegree + " ]");
		//Free unnecessary Memory
		overFittingStimate.clear(); 
		testing.clear();
		
		//Make sure that the Degree is valid [ Unlikely that it will happen ]
		if( bestDegree < 0 ){
			throw new LinearRegressionException( "Error : A best Degree was not found!" );
		}
		
		//////////////////////////////////////////////
		// Find the Lamda that best fits this curve //
		//////////////////////////////////////////////
		
		/* Look at the minimum value among the Testing Data with Degree d */
		Map<Double, Double> bestLamdaStimate = new TreeMap<Double, Double>();
		double bestLamda = 0.0;
		double bestStimate = 0.0;
		for( double tempLamda = minLamda; tempLamda <= maxDegree; tempLamda += lamdaIncrement ){
			try{
				//With the same Document only Modify the Polynomial = d and Lamda
				linearRegression.setPolynomialDegree( bestDegree );
				linearRegression.setLamda( tempLamda );
				linearRegression.createTrainingModel( linearRegressionParser.getTrainingData(), linearRegressionParser.getTrainingTargets() );
				linearRegression.createTestingModel( linearRegressionParser.getTestingData(), linearRegressionParser.getTestingTargets() );
				
				bestLamdaStimate.put( linearRegression.getTestingError(), linearRegression.getLamda() );
			}catch( Exception e ){
				System.out.println( "Error when testing for Lamda : " + e.toString() );
			}
		}
		
		testing = new LinkedList<Double>( bestLamdaStimate.keySet() );
		for( Double tempDouble : testing ){
			bestLamda = bestLamdaStimate.get( tempDouble );
			bestStimate = tempDouble;
			//System.out.println("List for the best error [ " + tempDouble + " ] - Lamda [ " + bestLamdaStimate.get( tempDouble ) + " ]");
			break;  //Don't compute uneccesary data
		}
		//Free unnecessary Memory
		bestLamdaStimate.clear(); 
		testing.clear();
		
		//Display the Degree and Lamda that best fit this Regression
		System.out.println( "-------------------------------------------------------------------" );
		System.out.println( "Polynomial of Degree [ " + bestDegree + " ] - Lamda [ " + bestLamda + " ] - Estimate Squared Error [ " + bestStimate + " ]" + NEWLINE +
							"Overfitting at " + " degree [ " + overFittingDegree + " ] by [ " + overFittingDiff + " ]" );
		System.out.println( "-------------------------------------------------------------------" );
		
		//Create the CVS file that will hold the inputs
		BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( "resultsData.cvs" )));
		
		writer.write( "Best Degree, " + bestDegree + ", Best Lamda," + bestLamda + NEWLINE );
		writer.write( ",,," + NEWLINE + ",,," + NEWLINE );
		writer.write( "Degree of Polynomial, Lamda, Training Data, Testing Data" + NEWLINE );
		writer.write( ",,," + NEWLINE + ",,," + NEWLINE );
		
		//Iterate first by Degree and for each degree iterate lamda
		for( int tempDegree = minDegree; tempDegree <= maxDegree; tempDegree += degreeIncrement ){
			for( double tempLamda = minLamda; tempLamda <= maxLamda; tempLamda += lamdaIncrement ){
				try{
					//With the same Document only Modify the Polynomial and Lamda
					linearRegression.setPolynomialDegree( tempDegree );
					linearRegression.setLamda( tempLamda );
					linearRegression.createTrainingModel( linearRegressionParser.getTrainingData(), linearRegressionParser.getTrainingTargets() );
					linearRegression.createTestingModel( linearRegressionParser.getTestingData(), linearRegressionParser.getTestingTargets() );
					
					//Write the values into a CVS file
					writer.write( linearRegression.getPolynomialDegree() + ", " + linearRegression.getLamda() + ", " + linearRegression.getTrainingError() + ", " + linearRegression.getTestingError() );
					writer.write( NEWLINE );
				}catch( Exception e ){
					//Don't display errors : They were already displayed above
					//System.out.println( "Error while creating the file : " + e.toString() );
				}
			}
			//Skip two line
			writer.write( ",,," + NEWLINE + ",,," + NEWLINE );
		}
		writer.write( ",,," );
		
		//Close the file
		writer.close();
	}
	
	public static void main(String[] args){
		try{
			MainApp mainApp = new MainApp( args );
			mainApp.execute();
			
			//Construct multiple values from range of Degrees d and Lamdas into a CVS
			//TODO: Find out why decimals are a problem when giving lambda as a small decimal[0.1..]
			//For now only use with whole numbers
			//mainApp.execute( 10, -5, 10, 1 );
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
