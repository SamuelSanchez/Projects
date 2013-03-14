package calendar;

import java.util.LinkedList;
import java.sql.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;

//This is for our local database
//TODO: Implement MySQL or Oracle Database interface but unfortunately there's not enough time
public class Database{
  private enum CONNECTION { MySQL, MAP }
  private Connection connection = null;
  private CONNECTION Connection_Type = CONNECTION.MySQL; 		//We're going to have a DB regardless; true (using mysql) : false (using Map)
  private Statement statement = null;
  private Map <Long, Event> fakeDatabase = null;
  private String invalidDB = "";					//Keeps the message if data cannot be stored in db
  private String password = "";
  private static SLPConstants slpc;
  private static DatabaseConstants dbc;

  public Database(){}

  public Database( String password ){
      this.password = password;
  }

  public void Connect(){
     try{
         //Connect to default Database
         Class.forName( dbc.MYSQL_DRIVER ).newInstance();
         connection = DriverManager.getConnection( dbc.MYSQL_URL, 
                                                   dbc.USERNAME, password );
         //Database Statements
         statement = connection.createStatement();

         //Select Database and table
         statement.executeUpdate( dbc.CREATE_DATABASE );
         statement.executeUpdate( dbc.USE_DATABASE );
	 statement.executeUpdate( dbc.SLP_CREATE_TABLE );

	 //Connection_Type = MySQL;
     }catch(Exception e){
         if(Debugger.IMPORTANT)
            System.out.println( "Not Connected to Database : " + e.toString() );

         Connection_Type = CONNECTION.MAP;
         fakeDatabase = new HashMap<Long, Event >();  
         //Retrieve data from file if it exists
         fakeConnect();
     }
  }

  //Retrieve data from file is we are not using MySQL
  private void fakeConnect(){
     try{
         BufferedReader file = new BufferedReader( new InputStreamReader( new FileInputStream( Constants.FAKE_DATABASE ) ) ); 
         String doc = file.readLine();
         Event event = new Event();
         while( doc != null ){
            event = Event.getEvent( doc );
            if( event != null )
               fakeDatabase.put( event.getKey(), event );
            doc = file.readLine();
         }
         //Let's see what data we retrieved
         if( Debugger.DEBUG ){
            Set<Long> keys = fakeDatabase.keySet();
            for( long key : keys ){
               Event temp = fakeDatabase.get( key );
               temp.print();
            }
         }
     }catch(Exception e){
         if( Debugger.IMPORTANT ) System.out.println( "Error : " + e.toString() );
     }
  }

  public boolean Disconnect(){
     try{
         switch( Connection_Type ){
            case MySQL:
               connection.close();
            break;
            case MAP:
               BufferedWriter file = new BufferedWriter( new FileWriter( Constants.FAKE_DATABASE ) );
               Set<Long> keys = fakeDatabase.keySet();  //Let's get all keys so that we can iterate and store values into txt file
               Event temp = null;
               String type = "";
               String date = "";
               String month = "";
               String time = "";
               String location = "";
               for( long key : keys ){
                   temp = fakeDatabase.get( key ); //Let's get the object associated with key
                   //Get values of the Object
                   if( temp.getType() != null ) type = temp.getType();
                   else type = "";
                   if( temp.getDate() != null ) date = temp.getDate();
                   else date = "";
                   if( temp.getMonth() != null ) month = temp.getMonth();
                   else month = "";
                   if( temp.getTime() != null ) time = temp.getTime(); 
                   else time = "";
                   if( temp.getLocation() != null ) location = temp.getLocation();
                   else location = "";
                   //Write date into File
                   file.write( key + Constants.DATA_SEPARATOR + type + Constants.DATA_SEPARATOR + date + 
                               Constants.DATA_SEPARATOR + month +  Constants.DATA_SEPARATOR + time +
                               Constants.DATA_SEPARATOR + location + Constants.NEWLINE );
               }
               file.close();
            break;
         }
         return true;
     }catch(Exception e){
         if(Debugger.IMPORTANT)
            System.out.println( "Error Closing Database : " + e.toString() );
         return false; 
     } 
  }

  public boolean insert( Event event ){
     try{
         //Reset the error message
         invalidDB = "";
         if( Debugger.DEBUG ) System.out.println( "Event Key [ " + event.getKey() + " ]" );

         //Before touching our storage - check if the key is valid
         if( !KeyCreator.isKeyValid( event.getKey() ) ){
            invalidDB = slpc.INVALID_KEY;
            if( Debugger.DEBUG ) System.out.println( "Key [ " + event.getKey() + " ] - is invalid!" );
            return false;
         }
      
         //Let's check if we already have it
         if( find( event.getKey() ) ){
             invalidDB = slpc.DOUBLE_KEY;
             if( Debugger.DEBUG ) System.out.println( "Key [ " + event.getKey() + " ] - Already exists" );
             return false;
         }
      
         //Let's check if we have errors
         if( invalidDB.length() != 0 ){
             if( Debugger.DEBUG ) System.out.println( "Key [ " + event.getKey() + " ] - Error Unknown" );
             return false;
         }

         switch( Connection_Type ){
            case MySQL:
               //TODO: Populate this once mysql is installed
            break;
            case MAP:
               fakeDatabase.put( event.getKey(), event );
            break;
         }
         return true;
     }catch(Exception e){
         if( Debugger.IMPORTANT )
            System.out.println( "Error : " + e.toString() );
         invalidDB = slpc.UNKNOW_ERROR;
         return false;
     }
  }

  //Use find method before using this method
  public Event get( long key ){
     try{
        Event temp = null;
        //Reset the error message
        invalidDB = "";
        if( Debugger.DEBUG ) System.out.println( "Event Key [ " + key + " ]" );

        switch( Connection_Type ){
           case MySQL:
              //TODO: Pupulate this
              break;
           case MAP:
              temp = fakeDatabase.get( key );
              break;
        }
       return temp;
    }catch(Exception e){
        if( Debugger.IMPORTANT ) System.out.println( "Error : " + e.toString() );
        invalidDB = slpc.UNKNOW_ERROR;
        return null;
    }
  }

  //Use find method before using this method
  public boolean delete( long key ){
     try{
        //Reset the error message
        invalidDB = "";
        if( Debugger.DEBUG ) System.out.println( "Event Key [ " + key + " ]" );

        switch( Connection_Type ){
           case MySQL:
              //TODO: Pupulate this
              break;
           case MAP:
              fakeDatabase.remove( key );
              break;
        }
       return true;
    }catch(Exception e){
        if( Debugger.IMPORTANT ) System.out.println( "Error : " + e.toString() );
        invalidDB = slpc.UNKNOW_ERROR;
        return false;
    }
  }

  //Use this function before retrieving data from DB - get or delete
  public boolean find( long key ){
     try{
         switch( Connection_Type ){
            case MySQL:
               //TODO: Pupulate this
               return true;
            case MAP:
               if( fakeDatabase.containsKey( key ) ) return true;
         }
         return false;
     }catch(Exception e){
         if( Debugger.IMPORTANT )
            System.out.println( "Error : " + e.toString() );
         invalidDB = slpc.UNKNOW_ERROR;
         return false;
     }
  }

  public String getErrorMessage(){
     return invalidDB;
  }

  //This is if the user forgets when is his appointment - therefore we get all 
  //queries [or queries the user wants] after todays or the day the user specifies
  //The key can be invalid, since we don't know yet what we are looking for
  public LinkedList<Event> getEvents( Event event ){
     try{
         if( Debugger.DEBUG ) System.out.println( "GET EVENTS [ " +  event.getKey() + " ]" );

         LinkedList<Event> events = null;
         switch( Connection_Type ){
            case MySQL:
               //TODO: Pupulate this
               return events;
            case MAP:
               Set<Long> keys = fakeDatabase.keySet();
               events = getSet( keys , event );
         }
         return events;
     }catch(Exception e){
         if( Debugger.IMPORTANT )
            System.out.println( "Error : " + e.toString() );
         invalidDB = slpc.UNKNOW_ERROR;
         return null;
     }
  }

  private LinkedList<Event> getSet( Set<Long> keys, Event event ){
     long key = -1;
     //If they don't know when they have the event, then let's start looking from today on
     if( !KeyCreator.isKeyValid( event.getKey() ) )
         key = KeyCreator.createKey();
     else
         key = event.getKey();

     LinkedList<Event> tempEvents = new LinkedList<Event>();
     Event tempEvent = null;

     //Let's save typing space
     String type = event.getType();
     String date = event.getDate();
     String time = event.getTime();
     String location = event.getLocation();

     //Let's get all events after the specific day
     //We don't need to sort the days, since a Set is a sort Collection
     for( long tempKey : keys ){
         if( tempKey >= key ){
             tempEvent = fakeDatabase.get( tempKey );
             //Let's use process of elimination [much easier] to filter for date - if data is irrelevant, then we don't need to check for anything else
             if( type != null && tempEvent.getType() != null && !type.equals( tempEvent.getType() ) ) continue;
             if( date != null && tempEvent.getDate() != null && !date.equals( tempEvent.getDate() ) ) continue;
             if( time != null && tempEvent.getTime() != null && !time.equals( tempEvent.getTime() ) ) continue;
             if( location != null && tempEvent.getLocation() != null && !location.equals( tempEvent.getLocation() ) ) continue;
             tempEvents.addLast( tempEvent );
         }
     }
     return tempEvents;
  }
}
