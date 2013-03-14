//package parser;
import java.util.LinkedList;
import java.util.ArrayList;
import java.io.BufferedWriter;
import java.io.FileWriter;

//TODO: Use Apache POI after Research is done for future experiments
public class mainApp {
   private static boolean MAIN_DEBUG = true;
   private static String NEWLINE = System.getProperty("line.separator");
   private static int write_Options = 0; //0 Important - 1 All

   //Display Usage
   private static void displayUsage()
   {
       System.out.println("Usage: mainApp [option] \nOptions: \n" +
                          "\t-f Path_to_File_name \n" +
						   "\t-l Log_file \n"); 
   }

   public static void main(String[] args){
     try{
       Parser parser = null;
	   String log_name = null;

       //Exit Parser if not arguments are provided
       if( args.length < 4 ){
           displayUsage();
           return;
       }       

       if( args[0].equals("-f") ){
          parser = new Parser( args[1] );
          if( !parser.validate() )
             throw new IllegalArgumentException( "Invalid Configuration File : " + args[1] );
          parser.execute();
          if( MAIN_DEBUG ) parser.print();
		  if( !args[2].equals("-l") ){
		     displayUsage();
			 return;
		  }
		  log_name = args[3];
		  if( args.length >4 && args[4].equals( ("-Verbose").toLowerCase()) )
		     write_Options = 1;
       }
       else{
          displayUsage();
          return;
       }
       
       BufferedWriter file = new BufferedWriter( new FileWriter( log_name ) );
       LinkedList<LogNode> logs = parser.getLog();
       ArrayList<String> properties = parser.getProperties();
       for( LogNode log : logs ){
            file.write( properties.get(0) + log.getExperimentRun() + NEWLINE );
            file.write( properties.get(1) + log.getName() + NEWLINE );
            if( write_Options == 1 ) 
			   file.write( properties.get(2) + log.getRobotsNumber() + NEWLINE );
            file.write( properties.get(3) + log.getPointsNumber() + NEWLINE );
            if( write_Options == 1 ){
			   file.write( properties.get(4) + "Mins [ " + log.getDurationMins() + " ] - Secs [ " + log.getDurationSecs() + " ] - Usecs [ "+ log.getDurationUSecs() + " ]" + NEWLINE );            
			   file.write( properties.get(5) + "Secs [ " + log.getAllSecs() + " ] - Usecs [ " + log.getDurationUSecs() + " ]" + NEWLINE );
            }
			file.write( properties.get(6) + "USecs [ " + log.getAllUsecs() + " ]" + NEWLINE );
            if( write_Options == 1 ){
			   file.write( properties.get(7) + log.getEndTime() + NEWLINE );
               file.write( properties.get(8) + log.getStartTime() + NEWLINE );
               file.write( properties.get(9) + log.getCollisionCount() + NEWLINE );
               file.write( properties.get(10) + log.getCollisionResolved() + NEWLINE );
			}
            file.write( properties.get(11) + log.isSuccessful() + NEWLINE );
            file.write( NEWLINE );
       }
       file.close();
     }catch(Exception e){
        System.out.println( "Error : " + e.toString() );
        e.printStackTrace();
     }
   }
}
