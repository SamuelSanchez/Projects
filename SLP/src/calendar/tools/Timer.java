package calendar;

//Class cannot be further extended or subclassed
public final class Timer {
   private double startTime;

   //should be private
   public Timer()
   {
      startTime = 0;
   }

   //Save the current time
   public void init(){
      startTime = System.currentTimeMillis();
   }

   //Return the time in seconds
   public double time(){
      return (System.currentTimeMillis()-startTime)/1000;
   }
}
