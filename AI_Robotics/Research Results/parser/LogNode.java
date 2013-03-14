//package parser;
import java.util.ArrayList;

public class LogNode {
  
   private String Experiment_Name;        //Auction Type
   private int Robots_Number;	          //Number of robots
   private int Experiment_Points;         //Number of points
   private int Collision_Count;           //Number of collisions
   private int Collision_Resolved;        //Number of collisions resolved
   private int Experiment_Duration_min;   //Total duration in mins
   private int Experiment_Duration;       //Total duration in sec
   private int Experiment_Duration_ms;    //Total of ms
   private long All_Secs_USecs_s;         //Total of Secs in the experiment
   private long All_USecs;                //Total of Usecs in the experiment
   private String Start_Time;      
   private String End_Time;
   private boolean isExperimentSuccessful;
   private int Experiment_Run;            //Run number
   private ArrayList<String> properties; //Stores the different properties
   private boolean LOG_DEBUG;

   LogNode( String name, String startTime, int points )
   {
       Experiment_Name = name;
       Start_Time = startTime;
       Experiment_Points = points;
       isExperimentSuccessful = false;
       End_Time = null;
       Experiment_Duration_min = 0;
       Experiment_Duration = 0;
       Experiment_Duration_ms = 0;
       Collision_Count = 0;
       Collision_Resolved = 0;
       Robots_Number = 0;
       All_Secs_USecs_s = 0;
       All_USecs = 0;
       LOG_DEBUG = true;
       properties = new ArrayList<String>();
       properties.add( "RUN NUMBER            : " );//0
       properties.add( "AUCTION TYPE          : " );//1
       properties.add( "NUMBER OF ROBOTS      : " );//2
       properties.add( "NUMBER OF POINTS      : " );//3
       properties.add( "DURATION STARDART     : " );//4
       properties.add( "DURATION - Secs-USecs : " );//5
       properties.add( "DURATION - ALL USECS  : " );//6
       properties.add( "END TIME              : " );//7
       properties.add( "START TIME            : " );//8
       properties.add( "NUMBER OF COLLISIONS  : " );//9
       properties.add( "COLLISIONS RESOLVED   : " );//10
       properties.add( "EXPERIMENT SUCCESSFUL : " );//11
   }
   
   //Setters
   public void setSuccessful(){
      isExperimentSuccessful = true;
   }
         
   public void setRobotsNumber( int i ){
      Robots_Number = i;
   }

   public void setEndTime( String endTime ){
      End_Time = endTime;
   }

   public void increseCollisionCount(){
      Collision_Count++;
   }

   public void increseCollisionResolved(){
      Collision_Resolved++;
   }

   public void setRunNumber( int r ){
      Experiment_Run = r;
   }

   //Getters
   public ArrayList<String> getProperties(){
      return properties;
   }

   public boolean isSuccessful(){
      return isExperimentSuccessful;
   }
 
   public String getName(){
      return Experiment_Name;
   }

   public int getRobotsNumber(){
      return Robots_Number;
   }

   public int getPointsNumber(){
      return Experiment_Points;
   }

   public int getCollisionCount(){
      return Collision_Count;
   }

   public int getCollisionResolved(){
      return Collision_Resolved;
   }

   public int getExperimentRun(){
      return Experiment_Run;
   }

   public int getDurationMins(){
      return Experiment_Duration_min;
   }

   public int getDurationSecs(){
      return Experiment_Duration;
   }

   public int getDurationUSecs(){
      return Experiment_Duration_ms;
   }

   public long getAllSecs(){
      return All_Secs_USecs_s;
   }

   public long getAllUsecs(){
      return All_USecs;
   }

   public String getStartTime(){
      return Start_Time;
   }

   public String getEndTime(){
      return End_Time;
   }

   //Update Methods
   public void updateDuration(){
     try{
       int hi = 0, hf = 0;
       int mini = 0, minf = 0;
       int seci = 0, secf = 0;
       int msi = 0, msf = 0;
       int usi = 0, usf = 0;
       int totalseci = 0, totalsecf = 0;
       int totalusi = 0, totalusf = 0;

       //StartTime
       String[] timeInitial = Start_Time.split(":");
       if( timeInitial.length != 5 ) throw new IllegalArgumentException( "Invalid Initial TimeStamp [ " + Start_Time + " ]" );

       //Everything To The Smallest Unit - microSeconds
       hi = Integer.parseInt( timeInitial[0] ) * 3600;
       mini = Integer.parseInt( timeInitial[1] ) * 60;
       seci = Integer.parseInt( timeInitial[2] );
       totalseci = hi + mini + seci;
       msi = Integer.parseInt( timeInitial[3] ) * 1000;
       usi = Integer.parseInt( timeInitial[4] );
       totalusi = msi + usi;

       if( LOG_DEBUG ){
          System.out.println( "Hour : " + timeInitial[0] + "\t- [ " + hi + " secs ]\n" +
                              "Min  : " + timeInitial[1] + "\t- [ " + mini + " secs ]\n" +
                              "Sec  : " + timeInitial[2] + "\t- [ " + seci + " secs ]\n" +
                              "mSec : " + timeInitial[3] + "\t- [ " + msi + " usecs ]\n" +
                              "uSec : " + timeInitial[4] + "\t- [ " + usi + " usecs ]\n" +
                              "Total : [ " + totalseci + " secs ]\n" +
                              "Total : [ " + totalusi + " usecs ]\n");
       }

       //EndTime
       String[] timeFinal = End_Time.split(":");
       if( timeFinal.length != 5 ) throw new IllegalArgumentException( "Invalid Final TimeStamp [ " + End_Time + " ]" );

       hf = Integer.parseInt( timeFinal[0] ) * 3600;
       minf = Integer.parseInt( timeFinal[1] ) * 60;
       secf = Integer.parseInt( timeFinal[2] );
       totalsecf = hf + minf + secf;
       msf = Integer.parseInt( timeFinal[3] ) * 1000;
       usf = Integer.parseInt( timeFinal[4] );
       totalusf = msf + usf;

       if( LOG_DEBUG ){
          System.out.println( "Hour : " + timeFinal[0] + "\t- [ " + hf + " secs ]\n" +
                              "Min  : " + timeFinal[1] + "\t- [ " + minf + " secs ]\n" +
                              "Sec  : " + timeFinal[2] + "\t- [ " + secf + " secs ]\n" +
                              "mSec : " + timeFinal[3] + "\t- [ " + msf + " usecs ]\n" +
                              "uSec : " + timeFinal[4] + "\t- [ " + usf + " usecs ]\n" +
                              "Total : [ " + totalsecf + " secs ]\n" +
                              "Total : [ " + totalusf + " usecs ]\n");
       }

       //Set to 24h. Error can only happen if it goes from one day to the other, say 23h to 0h.
       if( hi > hf ) throw new Exception( "Initial hour [ " + timeInitial[0] + " ] cannot be greater than final hour [ " + timeFinal[0] + " ]" );

       int totalSeconds = totalsecf - totalseci;
       int totalUSeconds = totalusf - totalusi;
       //If negative usecs then get another second
       if( totalUSeconds < 0 ){
          totalSeconds--;  //Decrease 1 sec
          totalUSeconds = 1000000 + totalUSeconds; //Add negative
       }

       if( totalSeconds > 59 ){
           Experiment_Duration_min = totalSeconds/60;
           Experiment_Duration = totalSeconds%60;
       }
       else{
           Experiment_Duration = totalSeconds;
       }
       Experiment_Duration_ms = totalUSeconds;
       All_Secs_USecs_s = totalSeconds;
       All_USecs = totalUSeconds + ( totalSeconds * 1000000 );
     }catch(Exception e){
        System.out.println( "Error : " + e.toString() );
        Experiment_Duration_min = 0;
        Experiment_Duration = 0;
        Experiment_Duration_ms = 0;
        All_Secs_USecs_s = 0;
        All_USecs = 0;
     }
   }

   //Debugger
   public void print(){
      System.out.println( "Auction Type       [ " + Experiment_Name + " ] \n" +
                          "Number of Robots   [ " + Robots_Number + " ] \n" +
                          "Number of Points   [ " + Experiment_Points + " ] \n" +
                          "Collision Counts   [ " + Collision_Count + " ] \n" +
                          "Collision Resolved [ " + Collision_Resolved + " ] \n" +
                          "Duration in mins   [ " + Experiment_Duration_min + " mins ] \n" +
                          "Duration in secs   [ " + Experiment_Duration + " secs ] \n" +
                          "Duration in usecs  [ " + Experiment_Duration_ms + " usecs ] \n" +
                          "Duration all secs  [ " + All_Secs_USecs_s + " secs ] \n" +
                          "Duration all usecs [ " + All_USecs + " usecs ] \n" +
                          "End Time           [ " + End_Time + " ] \n" +
                          "Start Time         [ " + Start_Time + " ] \n" +
                          "isSucessful?       [ " + isExperimentSuccessful + " ] \n" +
                          "Experiment Run     [ " + Experiment_Run + " ] \n" );
   }
}
