import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * This class trying to find a solutions for the Knapsack problem using Genetic Algorithms.
 * 
 * @author Samuel E. Sanchez
 *
 */
public class Knapsack {
	//Variables implemented by the User
	private int MAX_GENERATION;
	private int POPULATION_SIZE;
	private double CROSSOVER_RATE;
	private double MUTATION_RATE;
	private double SACK_CAPACITY;
	private boolean DEBUG;
	
	//Variables used by the program
	private double totalFitness;
	private List<Item> items;
	private int generation;
	private boolean isSolutionFound;
	private List<Item> solution;
	private List<List<Item>> populationList;
	private List<Double> parentWeightFitness;
	private List<Double> parentValueFitness;
	private List<List<Item>> childrenList;
	
	/**
	 * Creates a constructor for this program. It initializes the variables given by the user.
	 * If no variables are given by the User, then it assigns 0 to the missing ones.
	 */
	public Knapsack() {
		this.MAX_GENERATION = 0;
		this.POPULATION_SIZE = 0;
		this.CROSSOVER_RATE = 0.0;
		this.MUTATION_RATE = 0.0;
		this.SACK_CAPACITY = 0.0;
		this.totalFitness = 0.0;
		this.items = new LinkedList<Item>();
		this.generation = 0;		//Current population
		this.isSolutionFound = false;
		this.solution = new LinkedList<Item>();	//This is the solution for this G.A.
		this.DEBUG = false;
	}
	
	/**
	 * Initializes all variables. It reads argument from file to retrieve data.
	 * @param args Argument to be read from file
	 * @throws IOException Throws an Exception if no file is found
	 */
	public void init(String[] args) throws IOException{
		parseItems(args[0]);	//parse the text file
		initialization();		//Initialize population
	}
	
	/**
	 * It retrieve the variables from the text file.
	 * @param fileName Name of the file to retrieve variables
	 * @throws IOException Throws an Exception if no file is found
	 */
	private void parseItems(String fileName) throws IOException{
		//Read file
		BufferedReader file = new BufferedReader(new FileReader(new File(fileName)));
		String line = file.readLine();
		
		//Parse and retrieve information
		while(line != null){
			//Ignore comments or blank lines
			if(line.trim().isEmpty() || line.startsWith("//")){
				line = file.readLine();
				continue;
			}
			
			//Split by white space
			String[] parts = line.trim().split("\\s+");
			if(parts.length != 2 && parts.length != 3){
				line = file.readLine();
				continue;
			}
			
			if(DEBUG){
				System.out.println(line);
			}
			
			//Get variables values
			if(parts.length == 2){
				if(parts[0].equalsIgnoreCase("max_generations")){
					MAX_GENERATION = Integer.parseInt(parts[1]);
				}
				else if(parts[0].equalsIgnoreCase("population_size")){
					POPULATION_SIZE = Integer.parseInt(parts[1]);
					//It must be pair in order to perform crossover
					if(POPULATION_SIZE % 2 != 0){
						POPULATION_SIZE += 1;
					}
				}
				else if(parts[0].equalsIgnoreCase("crossover_rate")){
					CROSSOVER_RATE = Double.parseDouble(parts[1]);
				}
				else if(parts[0].equalsIgnoreCase("mutation_rate")){
					MUTATION_RATE = Double.parseDouble(parts[1]);
				}
				else if(parts[0].equalsIgnoreCase("capacity")){
					SACK_CAPACITY = Double.parseDouble(parts[1]);
				}
				else if(parts[0].equalsIgnoreCase("debug")){
					DEBUG = Boolean.valueOf(parts[1]);
				}
			}
			//Get Items: 
			else if(parts.length == 3){
				Item item = new Item(parts[0], Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
				items.add(item);
			}
			
			line = file.readLine();
		}
	}
	
	/**
	 * Runs the Knapsack Genetic Algorithm
	 */
	public void run(){
		//Iterate until a solution is found or we have reached the limit generation
		while(generation <= MAX_GENERATION){
			if(DEBUG){
				System.out.println("\n----------------------------[Generation : " + generation + "]----------------------------");
			}
			
			//If the evaluation found a solution, then break from the loop
			isSolutionFound = evaluation();
			
			if(isSolutionFound){
				break;
			}
			
			//Create a new population
			List<List<Item>> tempPopulation = new ArrayList<List<Item>>(POPULATION_SIZE);
			for(int i = 0; i < POPULATION_SIZE; i += 2){
				List<Item> parent1 = selection();
				List<Item> parent2 = selection();
				List<Item> child1 = new ArrayList<Item>(items.size());
				List<Item> child2 = new ArrayList<Item>(items.size());
				
				if(DEBUG){
					System.out.println("\nSelection");
					System.out.println("Child 1 : ");
					for(Item item : child1){
						item.print();
					}
					System.out.println("Child 2 : ");
					for(Item item : child2){
						item.print();
					}
					System.out.println("Parent 1 [" + parent1.size() + "]");
					for(Item item : parent1){
						item.print();
					}
					System.out.println("Parent 2 [" + parent2.size() + "]");
					for(Item item : parent2){
						item.print();
					}
				}
				
				//Perform Crossover
				List<List<Item>> children = recombination(parent1, parent2, child1, child2);
				child1 = children.get(0);
				child2 = children.get(1);
				
				if(DEBUG){
					System.out.println("\nCrossover");
					System.out.println("Child 1 : ");
					for(Item item : child1){
						item.print();
					}
					System.out.println("Child 2 : ");
					for(Item item : child2){
						item.print();
					}
					System.out.println("Parent 1 [" + parent1.size() + "]");
					for(Item item : parent1){
						item.print();
					}
					System.out.println("Parent 2 [" + parent2.size() + "]");
					for(Item item : parent2){
						item.print();
					}
				}
				
				//Perform mutation for every child
				child1 = mutation(child1);
				child2 = mutation(child2);
				
				//Add children to the population
				tempPopulation.add(child1);
				tempPopulation.add(child2);
				
				if(DEBUG){
					System.out.println("Mutation");
					System.out.println("Child 1 [" + child1.size() + "]");
					for(Item item : child1){
						item.print();
					}
					System.out.println("Child 2 [" + child2.size() + "]");
					for(Item item : child2){
						item.print();
					}
					System.out.println("Parent 1 [" + parent1.size() + "]");
					for(Item item : parent1){
						item.print();
					}
					System.out.println("Parent 2 [" + parent2.size() + "]");
					for(Item item : parent2){
						item.print();
					}
				}
			}
			
			//Delete All Replacement
			populationList = tempPopulation;	//Replace old population with new one
			generation++; 						//Increment the population
		}
		print();	//Printing the results to the user
	}
	
	/**
	 * Initializes the population for this genetic algorithm
	 */
	private void initialization(){
		solution = new LinkedList<Item>();
		populationList = new ArrayList<List<Item>>(POPULATION_SIZE);
		parentWeightFitness = new ArrayList<Double>(POPULATION_SIZE);
		parentValueFitness = new ArrayList<Double>(POPULATION_SIZE);
		childrenList = new ArrayList<List<Item>>(POPULATION_SIZE);
		
		//Create a random population and children
		for(int c = 0; c < POPULATION_SIZE; c++){
			List<Item> itemList = new ArrayList<Item>(items.size());
			List<Item> emptyData = new ArrayList<Item>(items.size());
			Boolean[] isAvailable = new Boolean[items.size()];			//For every iteration, let's create a boolean array
			Arrays.fill(isAvailable, Boolean.TRUE);						//Let's say that they are all available
			for(int r = 0; r < items.size(); r++){
				//Get a random item
				int loc = new Random().nextInt(items.size());
				//Add to the list if it's available, otherwise add an empty one
				if(isAvailable[loc] == true){
					Item item = items.get(loc);
					itemList.add(item);
					isAvailable[loc] = false;    //Set the item an unavailable for this Genome
				}
				else{
					itemList.add(new Item()); //Add an empty one to fill in the list space. It has no id/value/weight
				}
				emptyData.add(new Item());
			}
			populationList.add(itemList);
			childrenList.add(emptyData);
		}
	}
	
	/**
	 * It evaluates the current population to make sure that they meet the constraint.
	 * @return true there's a solution in the current population, false otherwise
	 */
	private boolean evaluation(){
		if(DEBUG){
			System.out.println("\nPerformation Evaluations!\n");
		}
		
		//Update the weights and values of the genome
		solution.clear();
		parentValueFitness.clear();
		parentWeightFitness.clear();
		//Reset and retrieve the total fitness of the population
		totalFitness = 0.0;
		
		//For every genome in the population
		for(List<Item> genome : populationList){
			double weight = 0;
			double value = 0;
			//Add all the items in it
			for(Item item : genome){
				weight += item.getWeight();
				value += item.getValue();
			}
			totalFitness += value;			//Keep the total fitness of the population
			parentValueFitness.add(value);
			parentWeightFitness.add(weight);
		}
		
		//Evaluate the weights and value according to sack capacity - Parents
		double maxValue = 0;
		int sackPosition = -1;
		for(int pos = 0; pos < parentValueFitness.size(); pos++){
			//Get all the items that have the capacity or have lower
			if(parentWeightFitness.get(pos) == SACK_CAPACITY){
				if(parentValueFitness.get(pos) > maxValue){
					maxValue = parentValueFitness.get(pos);
					sackPosition = pos;
				}
			}
		}
		
		//The minimum criteria is met - One terminating condition
		if(sackPosition != -1){
			if(DEBUG){
				System.out.println("A solution was found at Generation [" + generation + "]");
			}

			//Iterate through the list and do not keep null ids
			for(Item item : populationList.get(sackPosition)){
				//If it is not a placeholder, then add it
				if(item.getItem() != null){
					//Check for repetitions
					if(solution.contains(item)){
						return false;
					}
					solution.add(item);
				}
			}
			return true;
		}
		
		//If there's not solution, then we don't keep anything
		return false;
	}
	
	/**
	 * Returns a genome from the current population by using Roulette Wheel Selection
	 * The probability of being selected is proportional to higher fitness.
	 * @return The genome selected
	 */
	//Using Roulette Wheel Selection
	private List<Item> selection(){
		if(DEBUG){
			System.out.println("\nPerforming Selections [" + totalFitness + "]!\n");
		}
		
		//Get a percentage of the ones who are going to be selected
		double valueOfSelection = Math.random() * totalFitness;
		double fitnessOfSelection = 0.0;
		int position = 0;
		for(int loc = 0; loc < POPULATION_SIZE; loc++){
			fitnessOfSelection += parentValueFitness.get(loc);
			if(DEBUG){
				System.out.println("Fitness[" + fitnessOfSelection + "] - value [" + valueOfSelection + "]");
			}
			//Once we've reached the selection threshold, keep the individual
			if(fitnessOfSelection > valueOfSelection){
				if(DEBUG){
					System.out.println("Value[" + valueOfSelection + "] - Fitness[" + fitnessOfSelection + "]");
				}
				
				position = loc;
				break;
			}
		}
		
		return populationList.get(position);
	}
	
	/**
	 * Given two parents, it will generate two children. It depends on a probability in order to see if the parents
	 * can perform crossover. Otherwise they children will be identical copies of their parents.
	 * @param parent1 Parent 1
	 * @param parent2 Parent 2
	 * @param child1 Child 1
	 * @param child2 Child 2
	 * @return The list of children 1 and 2 generated by the crossover
	 */
	private List<List<Item>> recombination(List<Item> parent1, List<Item> parent2, List<Item> child1, List<Item> child2){
		if(DEBUG){
			System.out.println("\nPerforming Crossover!");
		}
		
		//If the crossover rate are not met, then ignore them
		double threshold = Math.random();
		
		if(DEBUG){
			System.out.println("Crossover random number [" + threshold + "] - Crossover rate[" + CROSSOVER_RATE + "]");
		}
		
		if(threshold > CROSSOVER_RATE){
			if(DEBUG){
				System.out.println("No crossover is performed!\n");
			}
			
			child1 = parent1;
			child2 = parent2;
			
			List<List<Item>> children = new ArrayList<List<Item>>();
			children.add(child1);
			children.add(child2);
			return children;
		}
		
		int position = new Random().nextInt(items.size()); //Select a random point to start transfering DNA
		if(DEBUG){
			System.out.println("Position of Crossover [" + position + "] - Size [" + items.size() + "]\n");
		}
		
		//Copy the first part of this genome
		for(int i = 0; i < position; i++){
			child1.add(parent1.get(i));
			child2.add(parent2.get(i));
		}
		//Copy the second part of this genome
		for(int i = position; i < parent1.size(); i++){
			child1.add(parent2.get(i));
			child2.add(parent1.get(i));
		}
		
		List<List<Item>> children = new ArrayList<List<Item>>();
		children.add(child1);
		children.add(child2);
		
		return children;
	}
	
	/**
	 * Given a genome, it will mutate it.
	 * @param genome To be changed
	 * @return The mutated genome
	 */
	private List<Item> mutation(List<Item> genome){
		if(DEBUG){
			System.out.println("\nPerforming Mutations!\n");
		}
		
		//Iterate through every object of this list and
		//mutate each one if they meet the probability
		List<Item> tempGenome = new ArrayList<Item>(items.size());
		for(int i = 0; i < genome.size(); i++){
			//If the mutation rate is not met, then ignore it
			if(Math.random() > MUTATION_RATE){
				tempGenome.add(genome.get(i));
				continue;
			}
			
			if(DEBUG){
				System.out.println("Mutation at [" + i + "]");
			}
			
			//Select an item that is not in the list
			//and if this genome does not contains it, then add it otherwise add an empty one
			Item toReplace = new Item();
			for(Item newItem : items){
				if(!genome.contains(newItem)){
					toReplace = newItem;
					break;
				}
			}
			tempGenome.add(toReplace);
		}
		//Replace old gene with the mutated one
		return tempGenome;
	}
	
	/**
	 * Displays the solution for this problem if there's any
	 */
	private void print(){
		if(isSolutionFound){
			System.out.println("Solution was found!");
			double weight = 0;
			double value = 0;
			for(Item item : solution){
				weight += item.getWeight();
				value += item.getValue();
				item.print();
			}
			System.out.println("Generation[ " + (generation) + " ] - Total Weight[" + weight + "] - Value [" + value + "]");
		}
		else{
			System.out.println("Solution was not found [" + (generation-1) + "]!");
		}
	}

	/**
	 * Runs the main program of this class
	 * @param args Filename where the data is going to be retrieved
	 */
	public static void main(String[] args) {
		try{
			//If there are not arguments, then display usage and exit the program
			if(args.length < 1){
				displayUsage();
				return;
			}
			
			Knapsack knapsack = new Knapsack();
			knapsack.init(args);
			knapsack.run();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * This will display the usage of this program
	 */
	public static void displayUsage(){
		System.out.println("Usage :	program <file>\n+" +
							"\t-<file> is the file where the parameters are stored");
	}
}

/**
 * Auxiliary class used to store the items' name, weights and values.
 * @author Samuel E. Sanchez
 *
 */
class Item{
	private String item;
	private double weight;
	private double value;
	
	public Item(){
		this(null, 0, 0);
	}
	
	public Item(String item, double weight, double value){
		this.setItem(item);
		this.setWeight(weight);
		this.setValue(value);
	}

	public String getItem() {
		return item;
	}

	public void setItem(String item) {
		this.item = item;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}
	
	public void print(){
		System.out.println(toString());
	}
	
	public String toString(){
		return "Item [" + item + "] - Weight [" + weight + "] - Value [" + value + "]";
	}
}
