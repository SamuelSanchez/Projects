package simulation.learning;

import java.util.List;

import simulation.tool.Global;

//TODO: There should be a method to save the training data and actions for every class that implements Classifier
public interface Classifier {
	
	public Action getAdvise(List<Obstacle> obj) throws Exception;
	
	public void addTrainingData(List<Obstacle> trainingPoints, Global.ACTION action) throws Exception;
	
	public void reset();
}
