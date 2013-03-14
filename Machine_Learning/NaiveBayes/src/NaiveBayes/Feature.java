package NaiveBayes;

public class Feature {
	private String name;
	private int count;
	private double probability;
	private int location;
	
	public Feature(String name){
		this.name = name;
		this.count = 0;
		this.probability = 0.0;
		this.location = 0;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public void increaseCount(){
		count++;		
	}
	
	public void findProbability(int total){
		probability = (double) count/total;
	}
	
	public void setLocation(int location){
		this.location = location;
	}
	
	public String getName(){
		return name;
	}
	
	public int getCount(){
		return count;
	}
	
	public double getProbability(){
		return probability;
	}
	
	public int getLocation(){
		return location;
	}
	
	//Debug this class values
	public void print(){
		System.out.println("Feature [ " + name + " - " + count + " - " + probability + " - " + location +" ]");
	}
}