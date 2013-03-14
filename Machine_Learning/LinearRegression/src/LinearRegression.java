import java.io.IOException;
import Jama.Matrix;
import java.util.ArrayList;
import java.util.List;

public class LinearRegression {

	//Degree :[-1]=Invalid, [0]=Constant, [1]=linear,
	//[2]=quadratic ... [n]=Polynomial of degree 'n'
	private static final boolean DEBUG = false;
	private static final int DECIMAL_SPACES = 6;
	private int polynomialDegree; 
	private double lamda;
	private Matrix X_by_D_Training; 		//Matrix for Variables of D dimensions
	private Matrix Targets_Training; 		//Matrix holding the targets
	private Matrix X_by_D_Testing;
	private Matrix Targets_Testing;
	private Matrix W_Vector;
	private double trainingMeanSquaredError;
	private double testingMeanSquaredError;
	
	//Constructor assigning bad data
	public LinearRegression(){
		this( -1, 0.0 );
	}
	
	public LinearRegression( int polynomialDegree ){
		this( polynomialDegree, 0.0 );
	}
	
	public LinearRegression( int polynomialDegree, double lamda ){
		this.polynomialDegree = polynomialDegree;
		this.lamda = lamda;
		this.trainingMeanSquaredError = -1;
		this.testingMeanSquaredError = -1;
	}
	
	//Getters and Setters
	public double getTrainingError(){
		return trainingMeanSquaredError;
	}
	
	public double getTestingError(){
		return testingMeanSquaredError;
	}
	
	public int getPolynomialDegree(){
		return polynomialDegree;
	}
	
	public double getLamda(){
		return lamda;
	}
	
	public void setPolynomialDegree( int polynomialDegree ){
		this.polynomialDegree = polynomialDegree;
	}
	
	public void setLamda( double lamda ){
		this.lamda = lamda;
	}
	
	//Creates a training model given the list of data and target values
	//Heavily prevents errors from the User
	//Create the matrixes of the Training Data and get its squared error
	public void createTrainingModel( List<Double> data, List<Double> target ) throws LinearRegressionException, IOException{
		//Test Possible Failure cases - Prevent Errors from User
		if( polynomialDegree < 0 )
			throw new LinearRegressionException( "Error : Degree of [ " + polynomialDegree + " ] is invalid for a Polynomial!" );
		if( data == null || data.size() == 0 )
			throw new LinearRegressionException( "Error : Provide a valid list of Training Data points!" );
		if( target == null || target.size() == 0 )
			throw new LinearRegressionException( "Error : Provide a valid list of Training Target points!" );
		
		if( DEBUG ){
			System.out.println("Training Model : Degree[ " + polynomialDegree + " ] - Lamda[ " + lamda + " ]");
		}
		Matrix[] matrixes = createModel( data, target );
		this.X_by_D_Training = matrixes[0];
		this.Targets_Training = matrixes[1]; 
		generateModel( this.X_by_D_Training, this.Targets_Training ); //Get the W vector
		trainingMeanSquaredError = findMeanSquaredError( X_by_D_Training, W_Vector, Targets_Training );
	}
	
	//Creates a testing model given the list of data and target values
	//Heavily prevents errors from the User
	//Create the matrixes of the Testing Data and get its squared error
	public void createTestingModel( List<Double> data, List<Double> target ) throws LinearRegressionException, IOException{		
		//Test Possible Failure cases - Prevent Errors from User
		if( polynomialDegree < 0 )
			throw new LinearRegressionException( "Error : Degree of [ " + polynomialDegree + " ] is invalid for a Polynomial!" );
		if( data == null || data.size() == 0 )
			throw new LinearRegressionException( "Error : Provide a valid list of Testing Data points!" );
		if( target == null || target.size() == 0 )
			throw new LinearRegressionException( "Error : Provide a valid list of Testing Target points!" );
		
		if( DEBUG ){
			System.out.println("Testing Model : Degree[ " + polynomialDegree + " ] - Lamda[ " + lamda + " ]");
		}
		Matrix[] matrixes = createModel( data, target );
		this.X_by_D_Testing = matrixes[0];
		this.Targets_Testing = matrixes[1];
		testingMeanSquaredError = findMeanSquaredError( X_by_D_Testing, W_Vector, Targets_Testing );
	}
	
	//Create and return the Matrixes of the Data [x's : the number of points] with D-Dimensions and Target values
	private Matrix[] createModel( List<Double> data, List<Double> target ) throws LinearRegressionException, IOException{
		//Check that both list have the same amount of points
		if( data.size() != target.size() )
			throw new LinearRegressionException( "Error : Variable Points and Target Points should be of the same size!" );
		
		//Create 2-d Arrays Matrixes 
		double[][] tempMatrix_X = new double[data.size()][polynomialDegree+1];
		double[][] tempMatrix_T = new double[data.size()][1];
		
		//Fill the Matrixes
		for( int m = 0; m < data.size(); m++ ){
			tempMatrix_T[m][0] = target.get( m ); //Fill the T Matrix
			
			for( int n = 0; n < polynomialDegree+1; n++ ){
				tempMatrix_X[m][n] = Math.pow( data.get(m), n); //File the MxN Matrix
			}
		}
		
		//Create Matrixes from 2-d Arrays
		Matrix X = new Matrix( tempMatrix_X );
		Matrix T = new Matrix( tempMatrix_T );
		
		if( DEBUG ){
			X.print( polynomialDegree+1, DECIMAL_SPACES );
			T.print( 1, DECIMAL_SPACES );
		}
		
		Matrix[] matrixes = { X, T };
		return matrixes;
	}
	
	//Obtain the Weight vector of D-Dimension constructed by Lamda, Polynomial of Degree d and the training data given by the user
	private void generateModel( Matrix X, Matrix T ) throws LinearRegressionException{
		try{
			//Check for possible errors
			if( X == null || T == null ){
				throw new LinearRegressionException( "Matrix [ " + ( X == null ? "X" : "T" ) + " ] is null!" );
			}
			
			// Formula   : W = ( (X^t * X) + (I*lamda) )^(-1) * (X^t * T)
			// Dimension : [D_by_1] = ( [D_by_X]*[X_by_D] + [D_by_D] ) * [D_by_X]*[X_by_1]
			// Dimension : [D_by_1] = ( [D_by_D] ) * [D_by_1]
			Matrix I = Matrix.identity( polynomialDegree+1, polynomialDegree+1 );
			W_Vector = ( ( ( X.transpose().times( X ) ).plus( I.times( lamda ) ) ).inverse() ).times( X.transpose().times( T ) );
			
			if( DEBUG ){
				System.out.println("W vector : ");
				W_Vector.print( 1, DECIMAL_SPACES );
			}
		}catch( Exception e){
			System.out.println( "Error : Degree[ " + polynomialDegree + " ] - Lamda[ " + lamda + " ]" );
			System.out.println( "Error : " + e.toString() );
			throw new LinearRegressionException(e.toString());
		}
	}

	//Find the squared error given the matrixes X, W, T
	private double findMeanSquaredError( Matrix X, Matrix W, Matrix T ) throws LinearRegressionException{
		//Check for possible errors
		if( X == null || W == null || T == null ){
			throw new LinearRegressionException( "Matrix [ " + ( X == null ? "X" : ( W == null ? "W" : "T" ) ) + " ] is null!" );
		}
		
		//Get a 1x1 matrix
		//Error = ( T[i] - ( W^t * X[i] ) )^2
		//Error = ( T[i] - ( X[i] * W )^2  [Depends on how the X[i] is gotten]
		//Dimensions : ( [1x1] - ( [1xD]*[Dx1] ) )^2
		//Dimensions : ( [1x1] - [1x1] )^2
		List<Double> meanSquaredError = new ArrayList<Double>();
		double tempError = 0.0;
		double rows = X.getRowDimension();
		Matrix X_i = null;
		
		for( int i = 0; i < rows; i++ ){
			X_i = X.getMatrix( i, i, 0, polynomialDegree );
			tempError = Math.pow( ( T.get( i, 0 ) - ( X_i.times( W ).get( 0, 0 ) ) ), 2);
			meanSquaredError.add( tempError );
			
			if( DEBUG ){
				System.out.println( "Row [ " + i + " ] - Error [ " + meanSquaredError + " ]" );
				X_i.print( polynomialDegree+1, DECIMAL_SPACES );
			}
		}
		
		double temp = 0.0;
		for( Double error_i : meanSquaredError ){
			temp += error_i.doubleValue();
		}
		
		return ( temp / meanSquaredError.size() );
	}
	
}