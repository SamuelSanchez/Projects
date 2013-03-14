//package parser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.LinkedList;
import java.util.ArrayList;

public class Parser {
         
   private String filename;
   private BufferedReader file;
   private boolean validation;
   private LinkedList<LogNode> log;
   private boolean PARSER_DEBUG;
   private ArrayList<String> experimentTypes;      //Store the different types of experiments
   private ArrayList<String> experimentProperties; //Store the different types of properties

   public Parser( String file ){
      super();
      filename = file;
      validation = false;
      PARSER_DEBUG = true;
   } 

   public boolean validate()
   {
      try{
          file = new BufferedReader( new InputStreamReader( new FileInputStream( filename ) ) );
          validation = true;
          return validation;
      } catch( Exception e ){
           System.out.println( "Error : " + e.toString() );
           return validation;
      }
   }

   //TODO: Fix the display for debugging
   public void execute()
   {
      try{
          if( !validation ) return;

          log = new LinkedList<LogNode>();
          String line = file.readLine();
          String[] log_data;
          int totalRuns = -1;
          ArrayList<Long> collisionAgents = new ArrayList<Long>();
          boolean firstAgentInCollision = true;

          //One collision counts when two robots get closer to each other
          //14:01:42:997:818 [ RECIEVED ] LOG_MESSAGE AUCTIONEER_STARTED MODE COMBINATORIAL_AUCTION_PROXIMITY POINTS 2 [ BY ] Auction_Test 1323111702  --> size 15
          //14:02:44:303:073 [ RECIEVED ] LOG_MESSAGE AUCTIONEER_FINISHED SUCCESSFUL AGENTS 3 [ BY ] Auction_Test 1323111702                           --> size 14
          //14:18:14:723:124 [ RECIEVED ] EXECUTE_ROBOTS PATH_ALTERATION_COST 1323112626 1323112632 16.4464 [ BY ] blackfin-3 1323112632               --> size 14   // Collision
          //Smallest message - size 11

          while( line != null ){
              log_data = line.split(" "); 
              //Auction Starts
              if( log_data.length == 15 && log_data[5].equals("AUCTIONEER_STARTED") ){
                 totalRuns++;
                 log.add( new LogNode( log_data[7], log_data[0], Integer.parseInt(log_data[9]) ) ); //Add last
                 log.getLast().setRunNumber( totalRuns );    //Let's do this for now, a static int can do the same job in LogNode
                 if( PARSER_DEBUG ){
                     System.out.println( "\n" + 
                                         "**************************************\n" + line );
                     log.getLast().print();
                 }
              }
              //Auction Ends
              else if( log_data.length == 14 && log_data[5].equals("AUCTIONEER_FINISHED") ){
                 log.getLast().setSuccessful();
                 log.getLast().setRobotsNumber( Integer.parseInt( log_data[8] ) );
                 log.getLast().setEndTime( log_data[0] );
                 log.getLast().updateDuration();
                 if( PARSER_DEBUG ){
                     System.out.println( "\n" + line );
                     log.getLast().print();
                 }
              }
              //Robot Collisions
              //First robot sends collision cost - cost for it to go to its target
              else if( log_data.length == 14 && log_data[5].equals("PATH_ALTERATION_COST") ){
                 if( PARSER_DEBUG ) System.out.println( line );
                 if( collisionAgents.size() == 0 ){
                     collisionAgents.add( Long.parseLong(log_data[6]) );
                     collisionAgents.add( Long.parseLong(log_data[7]) );
                     log.getLast().increseCollisionCount();
                 }
                 //Second robot might reply [ 1 collision ] or this might be a different collision [ 2 collisions - previous and current ]
                 else{
                     if( collisionAgents.get(0) == Long.parseLong(log_data[7]) && collisionAgents.get(1) == Long.parseLong(log_data[6]) ){
                        log.getLast().increseCollisionResolved();
                        if( PARSER_DEBUG ) System.out.println( "\nCollision Resolved!" );
                     }
                     else{
                        log.getLast().increseCollisionCount();
                        if( PARSER_DEBUG) System.out.println( "\nNew Collision!" );
                     }
                     if( PARSER_DEBUG ){
                        System.out.println( "First Collision  : id_1 [ " + collisionAgents.get(0) + " ] - id_2 [ " + collisionAgents.get(1) + " ]\n" +
                                            "Second Collision : id_1 [ " + log_data[6] + " ] - id_2 [ " + log_data[7] + " ]\n" );
                     }
                     collisionAgents.clear();
                 }
              }
              else{
                  /* Do Nothing */
              }
              //if( PARSER_DEBUG ) System.out.println( line );
              line = file.readLine();
          }
          evaluateRunNumber();
      }catch(Exception e){
          System.out.println( "Error : " + e.toString() );
      }
   }

   //Let's iterate through the list and let's find if we have a different pair of experiment and points
   //This is our run number. For the first round, every experiment runs with 'x' points, for the following
   //rounds, it is a repetition of the 'x' points and we should tell the program that we are repeating such experiment 
   //Also, let's keep the different types of experiments
   private void evaluateRunNumber(){
      try{
          ArrayList<RunValidator> list = new ArrayList<RunValidator>();
          experimentTypes = new ArrayList<String>(); 
          experimentProperties = new ArrayList<String>();
          int run_number = 1;
          int type_Count = 0;    //Keeps the count for the number of types - Let's just keep all the types from the run that has the most different types
          for( LogNode node : log ){
             RunValidator temp = new RunValidator(node.getName(), node.getPointsNumber());
             if( list.contains( temp ) ){
                //After one successful run has passed then by the second run we can get the different types of Experiment
                //Data is obtained after if there is more than one run
                if( list.size() > type_Count ){
                   type_Count = list.size();
                   for( RunValidator run : list ){
                       if( !experimentTypes.contains( run.getType() ) ) //Do not repeat the name
                          experimentTypes.add( run.getType() );
                   }
                }
                list.clear();
                list.add( temp );
                run_number++; //Increase the Run number
                node.setRunNumber( run_number );
             }
             else{
                list.add( temp );
                node.setRunNumber( run_number );
             }
             if( experimentProperties.isEmpty() )  
                experimentProperties = log.getFirst().getProperties();
          }
      }catch(Exception e){
          System.out.println( "Error : " + e.toString() );
      }
   }

   public ArrayList<String> getProperties(){
      return experimentProperties;
   }

   public ArrayList<String> getTypes(){
      return experimentTypes;
   }

   public LinkedList<LogNode> getLog(){
      return log;
   }

   public void print(){
       //Print out all the different types
       System.out.println( "TYPES - Count [ " + experimentTypes.size() + " ]" );
       for( String type : experimentTypes )
           System.out.println( "\t[ " + type + " ]" );
       //Print all out the runs
       for( LogNode node : log ){
          System.out.println( "-------------------------" );
          node.print();
       }
   }
}
