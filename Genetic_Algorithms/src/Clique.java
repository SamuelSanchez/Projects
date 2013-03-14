import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * This class trying to find a solutions for the Clique problem using Genetic Algorithms.
 * 
 * @author Samuel E. Sanchez
 *
 */
public class Clique {
	//Variables implemented by the User
	private int MAX_GENERATION;
	private int POPULATION_SIZE;
	private double CROSSOVER_RATE;
	private double MUTATION_RATE;
	private int CLIQUE_NODES;
	private boolean DEBUG;
	
	//Variables used by the program
	private Graph graph;
	private double totalFitness;
	private List<Node> items;
	private int generation;
	private boolean isSolutionFound;
	private List<Node> solution;
	private List<List<Node>> populationList;
	private List<Double> populationFitness;
	
	
	/**
	 * Creates a constructor for this program. It initializes the variables given by the user.
	 * If no variables are given by the User, then it assigns 0 to the missing ones.
	 */
	public Clique() {
		this.MAX_GENERATION = 0;
		this.POPULATION_SIZE = 0;
		this.CROSSOVER_RATE = 0.0;
		this.MUTATION_RATE = 0.0;
		this.CLIQUE_NODES = 0;
		this.DEBUG = false;
		this.graph = new Graph();
		
		this.totalFitness = 0.0;
		this.items = new LinkedList<Node>();
		this.generation = 0;		//Current population
		this.isSolutionFound = false;
		this.solution = new LinkedList<Node>();	//This is the solution for this G.A.
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
				else if(parts[0].equalsIgnoreCase("clique")){
					CLIQUE_NODES = Integer.parseInt(parts[1]);
				}
				else if(parts[0].equalsIgnoreCase("debug")){
					DEBUG = Boolean.valueOf(parts[1]);
				}
			}
			//Get Items: 
			else if(parts.length == 3){
				if(parts[1].trim().isEmpty()) continue;
				graph.addEdge(parts[1].trim(), parts[2].trim());
				items.add(new Node(parts[1].trim()));
			}
			
			line = file.readLine();
		}
	}
	
	/**
	 * Runs the Clique Genetic Algorithm
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
			List<List<Node>> tempPopulation = new ArrayList<List<Node>>(POPULATION_SIZE);
			for(int i = 0; i < POPULATION_SIZE; i += 2){
				List<Node> parent1 = selection();
				List<Node> parent2 = selection();
				List<Node> child1 = new ArrayList<Node>(items.size());
				List<Node> child2 = new ArrayList<Node>(items.size());
				
				if(DEBUG){
					System.out.println("\nSelection");
					System.out.println("Child 1 : ");
					for(Node item : child1){
						item.print();
					}
					System.out.println("Child 2 : ");
					for(Node item : child2){
						item.print();
					}
					System.out.println("Parent 1 [" + parent1.size() + "]");
					for(Node item : parent1){
						item.print();
					}
					System.out.println("Parent 2 [" + parent2.size() + "]");
					for(Node item : parent2){
						item.print();
					}
				}
				
				//Perform Crossover
				List<List<Node>> children = recombination(parent1, parent2, child1, child2);
				child1 = children.get(0);
				child2 = children.get(1);
				
				if(DEBUG){
					System.out.println("\nCrossover");
					System.out.println("Child 1 : ");
					for(Node item : child1){
						item.print();
					}
					System.out.println("Child 2 : ");
					for(Node item : child2){
						item.print();
					}
					System.out.println("Parent 1 [" + parent1.size() + "]");
					for(Node item : parent1){
						item.print();
					}
					System.out.println("Parent 2 [" + parent2.size() + "]");
					for(Node item : parent2){
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
					for(Node item : child1){
						item.print();
					}
					System.out.println("Child 2 [" + child2.size() + "]");
					for(Node item : child2){
						item.print();
					}
					System.out.println("Parent 1 [" + parent1.size() + "]");
					for(Node item : parent1){
						item.print();
					}
					System.out.println("Parent 2 [" + parent2.size() + "]");
					for(Node item : parent2){
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
		solution = new LinkedList<Node>();
		populationList = new ArrayList<List<Node>>(POPULATION_SIZE);
		populationFitness = new ArrayList<Double>(POPULATION_SIZE);
		
		//Create a random population and children
		for(int c = 0; c < POPULATION_SIZE; c++){
			List<Node> itemList = new ArrayList<Node>(CLIQUE_NODES);
			List<Node> emptyData = new ArrayList<Node>(CLIQUE_NODES);
			Boolean[] isAvailable = new Boolean[items.size()];			//For every iteration, let's create a boolean array
			Arrays.fill(isAvailable, Boolean.TRUE);						//Let's say that they are all available
			for(int r = 0; r < CLIQUE_NODES; r++){
				//Get a random item
				int loc = new Random().nextInt(items.size());
				//Add to the list if it's available, otherwise add an empty one
				if(isAvailable[loc] == true){
					Node item = items.get(loc);
					itemList.add(item);
					isAvailable[loc] = false;    //Set the item an unavailable for this Genome
				}
				else{
					itemList.add(new Node()); //Add an empty one to fill in the list space. It has no name
				}
				emptyData.add(new Node());
			}
			populationList.add(itemList);
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
		populationFitness.clear();
		//Reset and retrieve the total fitness of the population
		totalFitness = 0.0;
		
		//For every genome in the population
		for(List<Node> genome : populationList){
			//For every node find the number of connection to other nodes
			//Count all the links in the genome. The higher the links the
			int counter = 0;
			Object[] genomeItems = genome.toArray();
			for(Node item : genome){
				//Let's see if we have any connections with other nodes in the same genome
				if(item.name == null) continue; //Don't add anything
				List<Edge> edges = graph.getNode(item.name).adjNodes;
				for(Edge e : edges){
					for(Object g : genomeItems){
						if(e.node.name.equals(((Node) g).name)){
							counter++;
						}
					}
				}
			}
			totalFitness += counter;			//Keep the total fitness of the population
			populationFitness.add((double) counter);
		}
		
		//Evaluate the weights and value according to sack capacity - Parents
		int sackPosition = -1;
		for(int pos = 0; pos < populationFitness.size(); pos++){
			//Get all the items that have the capacity or have lower
			if(populationFitness.get(pos) == CLIQUE_NODES){ //The fitness number == (nodes * nodes-1 links)
				sackPosition = pos;
			}
		}
		
		//The minimum criteria is met - One terminating condition
		if(sackPosition != -1){
			if(DEBUG){
				System.out.println("A solution was found at Generation [" + generation + "]");
			}

			//Iterate through the list and do not keep null ids
			for(Node node : populationList.get(sackPosition)){
				//If it is not a placeholder, then add it
				if(node.name != null){
					if(solution.contains(graph.getNode(node.name))){
						return false;
					}
					solution.add(graph.getNode(node.name));
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
	private List<Node> selection(){
		if(DEBUG){
			System.out.println("\nPerforming Selections [" + totalFitness + "]!\n");
		}
		
		//Get a percentage of the ones who are going to be selected
		double valueOfSelection = Math.random() * totalFitness;
		double fitnessOfSelection = 0.0;
		int position = 0;
		for(int loc = 0; loc < POPULATION_SIZE; loc++){
			fitnessOfSelection += populationFitness.get(loc);
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
	private List<List<Node>> recombination(List<Node> parent1, List<Node> parent2, List<Node> child1, List<Node> child2){
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
			
			List<List<Node>> children = new ArrayList<List<Node>>();
			children.add(child1);
			children.add(child2);
			return children;
		}
		
		int position = new Random().nextInt(CLIQUE_NODES); //Select a random point to start transfering DNA
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
		
		List<List<Node>> children = new ArrayList<List<Node>>();
		children.add(child1);
		children.add(child2);
		
		return children;
	}
	
	/**
	 * Given a genome, it will mutate it.
	 * @param genome To be changed
	 * @return The mutated genome
	 */
	private List<Node> mutation(List<Node> genome){
		if(DEBUG){
			System.out.println("\nPerforming Mutations!\n");
		}
		
		//Iterate through every object of this list and
		//mutate each one if they meet the probability
		List<Node> tempGenome = new ArrayList<Node>(items.size());
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
			Node toReplace = new Node();
			for(Node newNode : items){
				if(!genome.contains(newNode)){
					toReplace = newNode;
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
			for(Node node : solution){
				node.print();
			}
			System.out.println("Generation[ " + (generation) + " ]");
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
			
			Clique clique = new Clique();
			clique.init(args);
			clique.run();
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

//This is the Graph class
class Graph{
	private Map<String, Node> map;
	
	public Graph(){
		this.map = new HashMap<String, Node>();
	}
	
	public void addEdge(String from, String to){
		if(from == null || to == null) return;
		//Avoid cycles in the same node
		if(from.equals(to)) return;
		Node f = getNode(from);
		Node t = getNode(to);
		f.adjNodes.add(new Edge(t));
	}
	
	public Node getNode(String node){
		if(node == null) return null;
		Node n = map.get(node);
		if(n == null){
			n = new Node(node);
			map.put(node, n);
		}
		return n;
	}
}

//This is the node in a graph
class Node{
	public String name;
	public List<Edge> adjNodes;
	public Node back;
	
	public Node(){
		this(null);
	}
	
	public Node(String name){
		this.name = name;
		this.adjNodes = new LinkedList<Edge>();
		this.back = null;
	}
	
	public void print(){
		System.out.println(toString());
	}
	
	public String toString(){
		String edgesStr = "";
		for(Edge e :adjNodes){
			edgesStr += (e.node.name + " - ");
		}
		return "Node [" + name + "] : " + edgesStr;
	}
}

//This is the edge in a graph
class Edge{
	public Node node;
	
	public Edge(Node node){
		this.node = node;;
	}
	
	public String toString(){
		return node.name;
	}
}