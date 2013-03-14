import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

public class LinearRegressionParser {

	private static final String DELIMETER = ",";
	private static final int DIMENSIONS = 2;
	private static final boolean DEBUG = false;
	private String trainingFile;
	private String testingFile;
	private List<Double> trainingData;
	private List<Double> trainingTargets;
	private List<Double> testingData;
	private List<Double> testingTargets;

	//Constructor assinging bad data
	public LinearRegressionParser(){
		this( null, null);
	}

	public LinearRegressionParser( String trainingFile, String testingFile ){
		this.trainingFile = trainingFile;
		this.testingFile = testingFile;
	}
	
	//Setters and Getters
	public void setTrainingFile( String trainingFile ){
		this.trainingFile = trainingFile;
	}
	
	public void setTestingFile( String testingFile ){
		this.testingFile = testingFile;
	}
	
	public List<Double> getTrainingData() {
		return trainingData;
	}

	public List<Double> getTrainingTargets() {
		return trainingTargets;
	}

	public List<Double> getTestingData() {
		return testingData;
	}

	public List<Double> getTestingTargets() {
		return testingTargets;
	}

	//Main execution of this class
	public void init() throws LinearRegressionException, IOException{
		retrieveTrainingData();
		retrieveTestingData();
	}
	
	//Parse the Training Document and keep the lists of Variables and Targets 
	private void retrieveTrainingData() throws LinearRegressionException, IOException{
		if( trainingFile == null )
			throw new LinearRegressionException( "Error : Training Data must be defined!" );
		
		if( DEBUG ) System.out.println("Training Model : ");
		List<Double>[] models = createModel( trainingFile );
		trainingData = models[0];
		trainingTargets = models[1];
	}
	
	//Parse the Testing Document and keep the lists of Variables and Targets
	private void retrieveTestingData() throws LinearRegressionException, IOException{
		if( testingFile == null )
			throw new LinearRegressionException( "Error : Testing Data must be defined!" );
		
		if( DEBUG ) System.out.println("Testing Model : ");
		List<Double>[] models = createModel( testingFile );
		testingData = models[0];
		testingTargets = models[1];
	}
	
	//Parse a documents assuming that it has two items per line
	//and return an array of the list of the items parsed
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private List<Double>[] createModel( String file ) throws LinearRegressionException, IOException{
		//Get the Variables and Target Values from file
		BufferedReader traningValues = new BufferedReader( new InputStreamReader( new FileInputStream( file )));
		String line = traningValues.readLine();
		List<Double> data = new LinkedList<Double>();
		List<Double> target = new LinkedList<Double>();
		
		while( line != null ){
			String[] x_t = line.split( DELIMETER );
			
			//The number of Variables must be the same to the number of Targets
			if( x_t.length != DIMENSIONS ){
				throw new LinearRegressionException( "Error : File should have (x,t) values - values length [ " + x_t.length + " ]" );
			}
			
			data.add( Double.parseDouble( x_t[0] ) );
			target.add( Double.parseDouble( x_t[1] ));
			
			line = traningValues.readLine();
		}
		
		List[] modelValues = { data, target };
		return modelValues;
	}
	
}
