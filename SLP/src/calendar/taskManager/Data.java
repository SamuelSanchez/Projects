package calendar;

import java.util.LinkedList;

public class Data{
   private static Data dataObj;               //Only one instance to be shared among classes
   private LinkedList<Event> QueryList;    //Requests to go into DB
   private LinkedList<Event> DataNeeded;   //Requests obtained from DB

   //Let's return the instance to everyone
   public static synchronized Data getInstance()
   {
       if( dataObj == null )
          dataObj = new Data();
       return dataObj;
   }

   private Data(){
       QueryList   = new LinkedList<Event>();
       DataNeeded  = new LinkedList<Event>();
   }

   //-- FUNCTIONS FOR A DIALOGMANAGER TO CHECK AND MANAGE --
   public synchronized void setQuery( Event Query )
   {   
       QueryList.addFirst( Query );
   }

   public synchronized boolean isDataReady()
   {
       return ( DataNeeded.size() > 0 );
   }

   public synchronized Event getDataNeeded()
   {
       return DataNeeded.removeLast();
   }

   //-- FUNCTIONS FOR DB TO CHECK AND MANAGE --
   public synchronized boolean isQuery()
   {
      return ( QueryList.size() > 0 );
   }

   public synchronized Event getQuery()
   {
       return QueryList.removeLast();
   }

   public synchronized void setDataNeeded( Event Info )
   { 
       DataNeeded.addFirst( Info );
   }

   //Let's not have too many instances - Only one shared among DB and DialogManager
   public Object clone() throws CloneNotSupportedException
   {
       throw new CloneNotSupportedException();
   }
}
