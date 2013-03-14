import java.util.Random;

import javax.swing.JFrame;

import Jama.Matrix;

//TODO: Create a function that will update the number of clusters without changing data points
//TODO: Create a function that will add more data points
//TODO: Create a function that will remove data points
//TODO: Create a function that will check if there's a singular Matrix - if so, re-estimate EM
public class GaussianMixture{

	//Variables given by the user
	private int dimensions;
	private int clusters;
	private double[][] data_points;		//first [] carries the number of points, the second[] carries the number of dimension
										//0variables[200][3] means 3 dimension, each carrying 200 points
	//Variables used internally by the GM
	private double[][] mean;
	private Matrix[] covariance;
	private double[] weight;
	private double[][] responsability;
	private double likehood;
	private boolean DEBUG;
	private boolean GRAPH;
	private GaussianMixtureGraph2D graph;
	
	public GaussianMixture(int clusters, double[][] data_points) throws Exception{
		this(clusters, data_points, false, false);
	}
	
	public GaussianMixture(int clusters, double[][] data_points, boolean debug, boolean graph) throws Exception{
		this.DEBUG = debug;
		this.GRAPH = graph;
		refactor(clusters, data_points);
	}
	
	//Change the values of this GMM
	public void refactor(int clusters, double[][] data_points) throws Exception{
		//Check that the data is valid
		if(data_points == null || clusters < 1){
			throw new Exception("Error : " + (data_points == null ? "Data points are not defined!" : "provide a positive number of clusters!"));
		}
		
		this.dimensions = data_points[0].length;			//x,y,...
		this.clusters = clusters;							//number of normal distributions
		this.data_points = data_points;						//All the data Points
		this.responsability = new double[data_points.length][clusters];
		this.likehood = 0;
		
		//Graph 2d
		if(GRAPH && dimensions == 2){
			graph = new GaussianMixtureGraph2D(clusters, data_points);
			JFrame frame = new JFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.add(graph);
			frame.setSize(500,500);
			frame.setLocation(200,200);
			frame.setVisible(true);
			frame.setTitle("Gaussian Mixture Model - Cluster : " + clusters);
		}
		
		if(DEBUG){
			System.out.println("Dimensions = " + dimensions);
			System.out.println("Clusters   = " + clusters);
		}
	}
	
	//Initialize variables
	public void init(){
		double[][] u = new double[clusters][dimensions];			//mean[k][x,y,..]
		//Iterate through each cluster
		for(int k = 0; k < clusters; k++){
			//Iterate through each dimension
			for(int d = 0; d < dimensions; d++){
				//Assign random means from the range of variables from each dimension
				u[k][d] = data_points[new Random().nextInt(data_points.length)][d];
				
				if(DEBUG){
					System.out.println("mean[" + k + "][" + d + "] : " + u[k][d]);
				}
			}
		}
		//Create Mean matrix
		mean = u;
		
		//Create responsibility
		weight = new double[clusters];
		double weight_normilizer = 0;
		for(int k = 0; k < clusters; k++){
			weight[k] = new Random().nextInt(100) + 1;  //Avoid having a zero weight
			weight_normilizer += weight[k];
		}
		
		//Normalize the responsibility
		for(int k = 0; k < clusters; k++){
			weight[k] = weight[k]/weight_normilizer;
			
			if(DEBUG){
				System.out.println("weight[" + k + "] : " + weight[k]);
			}
		}
		
		//Create an Identity Matrix
		covariance = new Matrix[clusters];
		
		for(int k = 0; k < clusters; k++){
			covariance[k] = Matrix.identity(dimensions, dimensions);
			
			if(DEBUG){
				System.out.println("covariance[" + k + "] : ");
				covariance[k].print(dimensions, 5);
			}
		}
	}
	
	//Find the EM with threshold 0.01
	public void find_EM(){
		find_EM(0.01);
	}
	
	public void find_EM(int iterations){
		for(int i = 0; i < iterations; i++){
			E_Step();
			M_Step();
			likehood = findLikelihood();
			if(GRAPH && dimensions == 2){
				graph.update(responsability, mean);
			}
			
			if(DEBUG){
				System.out.println("---------------------------------------");
				System.out.println("Likelihood : " + likehood);
				System.out.println("---------------------------------------");
			}
		}
	}
	
	public void find_EM(double threshold){
		likehood = findLikelihood();
		double oldLikehood = 0;
		
		while(Math.abs(likehood - oldLikehood) > threshold){
			oldLikehood = likehood;
			E_Step();
			M_Step();
			likehood = findLikelihood();
			if(GRAPH && dimensions == 2){
				graph.update(responsability, mean);
			}
			
			if(DEBUG){
				System.out.println("---------------------------------------");
				System.out.println("Likelihood : " + Math.abs(likehood - oldLikehood));
				System.out.println("---------------------------------------");
			}
		}
	}
	
	public double getLikelihood(){
		return likehood;
	}
	
	public void print(){
		System.out.println("Clusters : " + clusters + "\nLikehood : " + likehood + "\n");
		for(int k = 0; k < clusters; k++){
			System.out.println("Cluster[" + k + "] : ");
			//print weight
			System.out.println("weight : " + weight[k]);
			//print mean
			for(int d = 0; d < dimensions; d++){
				System.out.println("mean[" + k + "][" + d + "] : " + mean[k][d]);
			}
			//print covariance
			System.out.println("Covariance :");
			covariance[k].print(dimensions, 6);
		}
	}
	
	private double findLikelihood(){
		double likelihood = 0;
		for(int n = 0; n < data_points.length; n++){
			double Sum = 0;
			for(int k = 0; k < clusters; k++){
				Sum += weight[k] * gaussianProbability(data_points[n], mean[k], covariance[k]);
			}
			likelihood += Sum;
		}
		return Math.log(likelihood);
	}
	
	//Find the likelihood -> g(Xi|Uk,Ek)
	public double gaussianProbability(double[] Xi, double[] U_k, Matrix E_k){
		//Create matrix from Xi and Uk
		double[][] x_u = new double[Xi.length][2];   //x_u = [x -u]
		for(int pos = 0; pos < Xi.length; pos++ ){
			//Get the variables for the dimension
			x_u[pos][0] = Xi[pos];
			//Get the mean for k
			x_u[pos][1] = -U_k[pos];
		}
		Matrix X_U = new Matrix(x_u);
		double first_part = 1/( Math.pow((2*Math.PI),(dimensions/2)) * Math.sqrt(E_k.det()) );
		double[][] second_part = (((X_U.transpose()).times(E_k.inverse())).times(X_U)).times(-0.5).getArray();
		double exponent = 0;
		//Add the values of the Matrix
		for(int r = 0; r < second_part.length; r++){
			for(int c = 0; c < second_part[0].length; c++){
				exponent += second_part[r][c];
			}
		}
		return (first_part * Math.pow( Math.E, exponent) );
	}
	
	//Find the responsabilities
	private void E_Step(){
		if(DEBUG){
			System.out.println("E_Step");
		}
		
		double clusterSum = 0.0;
		for(int n = 0; n < data_points.length; n++){
			//Reset the Sum of the clusters at point n, where n = { xi, yi, .. } coordinates
			clusterSum = 0.0;
			for(int k = 0; k < clusters; k++){
				responsability[n][k] = weight[k] * gaussianProbability(data_points[n], mean[k], covariance[k]);
				clusterSum += responsability[n][k];
			}
			//Normalize the points for each cluster
			for(int k = 0; k < clusters; k++){
				responsability[n][k] = responsability[n][k]/clusterSum;
			}
		}
	}
	
	//Update variables
	private void M_Step(){
		if(DEBUG){
			System.out.println("M_Step");
		}

		for(int k = 0; k < clusters; k++){			
			//Nk - Normalizer
			double nk = getResponsabilitySum(k);			
			//Find Mean
			updateMean(k, nk);
			//Find Covariance - Here Singular Matrixes can be form : Avoid!
			updateCovariance(k, nk);
			//Find Weights
			updateWeight(k, nk);
		}
	}//M_Step
	
	private void updateMean(int k, double nk){
		//Iterate though every dimension
		for(int d = 0; d < dimensions; d++){
			//Find the mean - new U_k
			double u = 0;
			for(int n = 0; n < data_points.length; n++){
				u += (responsability[n][k] * data_points[n][d]);
			}
			mean[k][d] = u/nk;
			
			if(DEBUG){
				System.out.println("Mean k[" + k + "][" + d + "] : " + mean[k][d]);
			}
		}
	}
	
	private void updateCovariance(int k, double nk){
		//Iterate though every point
		Matrix E = new Matrix(dimensions, dimensions);
		for(int n = 0; n < data_points.length; n++){
			//Find the covariance - new E_k
			double[][] xi_uk = new double[dimensions][2];
			for(int d = 0; d < dimensions; d++){
				xi_uk[d][0] = data_points[n][d];
				xi_uk[d][1] = -mean[k][d];
			}
			Matrix X_U = new Matrix(xi_uk);
			E = E.plus((X_U.times(X_U.transpose())).times(responsability[n][k]));
		}
		covariance[k] = E.times(1/nk);
		
		if(DEBUG){
			System.out.println("Covariance k[ " + k + " ] : ");
			covariance[k].print(dimensions, 8);
		}
	}
	
	private void updateWeight(int k, double nk){
		//Find the new weight
		weight[k] = nk / data_points.length;
		
		if(DEBUG){
			System.out.println("Weight k[ " + k + " ] : " + weight[k]);
		}
	}
	
	private double getResponsabilitySum(int k){
		double respSum = 0;
		for(int n = 0; n < data_points.length; n++){
			respSum  += responsability[n][k];
		}

		if(DEBUG){
			System.out.println("Resposibility k[ " + k + " ] : " + respSum);
		}
		return respSum;
	}
}