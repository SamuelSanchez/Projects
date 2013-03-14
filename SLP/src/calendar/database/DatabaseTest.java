package calendar;

import java.util.Scanner;
import java.util.LinkedList;
import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;

//Hard Code
public class DatabaseTest{
   public static void displayUsage(){
       DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
       Date date = new Date();

       System.out.println( "Enter a text: " + Constants.NEWLINE +
                           " Command [Insert, delete, get, set, exit] type date time location " + Constants.NEWLINE +
                           "Ex: Insert doctor's appointment 07/15/11 15:00 Manhattan" + Constants.NEWLINE +
                           "NOTE : When inserting an appointment, do not insert any day " + Constants.NEWLINE + 
                           "before today's date [ " + dateFormat.format(date) + " ]" );
   }

   public static void main(String[] args){
      //TODO: Test it   
       Scanner scan = new Scanner( System.in );
       Database db = new Database ();
       db.Connect();
       Event event = null;
       String command = null;
       String type = null;
       String date = null;
       String[] dateTemp;
       int year = -1, month = -1, day = -1;
       String time = null;
       String[] timeTemp;
       int hour = -1, min = -1;
       String location = null;
       boolean go = true;
       long key = 0;

       displayUsage();

       while( go ){
          event = new Event();  //Reset the event;
          command = null;
          type = null;
          date = null;
          year = -1; month = -1; day = -1;
          time = null;
          hour = -1; min = -1;
          location = null;
          System.out.println( "Command : " );
          command = scan.nextLine();
          if( command.equals( "insert" ) || command.equals( "set" ) ){
             System.out.println( "Type : " );
             type = scan.nextLine();
             System.out.println( "Date : " );
             date = scan.nextLine();
             if( date != null && !date.isEmpty() ){
                dateTemp = date.split("/");
                month = Integer.parseInt( dateTemp[0] );
                day = Integer.parseInt( dateTemp[1] );
                year = Integer.parseInt( dateTemp[2] );
             }
             System.out.println( "Time : " );
             time = scan.nextLine();
             if( time != null && !time.isEmpty() ){
                timeTemp = time.split(":");
                hour = Integer.parseInt( timeTemp[0] );
                min = Integer.parseInt( timeTemp[1] );
             }
             System.out.println( "Location : " );
             location = scan.nextLine();
          
             //Populate events
             event.setType( type );
             event.setDate( year, month, day );
             event.setTime( hour, min );
             event.setLocation( location );
             event.setType( type );
          }

          if( command.trim().toLowerCase().equals( "insert" ) && !event.setKey() ){
             System.out.println( "Invalid Event" );
             event.print();
             displayUsage();
             continue;
          }

          if( command.trim().toLowerCase().equals( "get" ) || command.trim().toLowerCase().equals( "delete" ) ){
              key = 0;
              System.out.println( "Insert Key : " );
              key = Long.parseLong( scan.nextLine() );
          }

          //System.out.println( "-----------------------" );
          //System.out.println( "COMMAND [" + command + "]" );
          //event.print();
          //System.out.println( "-----------------------" );

          //Process Commands
          if( command.trim().toLowerCase().equals( "exit" ) ){
              System.out.println( "Good Bye!" );
              break;
          }
          else if( command.trim().toLowerCase().equals( "insert" ) ){
              if( db.insert( event ) )
                  System.out.println( "Event was succeessfully inserted!" );
              else
                  System.out.println( "Event  wasn't inserted - REASON [ " + db.getErrorMessage() + "  ]" );
          }
          else if( command.trim().toLowerCase().equals( "get" ) ){
              if( db.find( key ) ){
                  event = db.get( key );
                  event.print();
              }
              else
                  System.out.println( "Event  wasn't found - REASON [ " + db.getErrorMessage() + "  ]" );              
          }
          else if( command.trim().toLowerCase().equals( "delete" ) ){
              if( db.find( key ) ){
                  if ( db.delete( key ) )
                       System.out.println( "Even sucessfully deleted!" );
                  else
                    System.out.println( "Event wasn't deleted - REASON [ " + db.getErrorMessage() + " ]" );
              }
              else
                  System.out.println( "Event  wasn't found - REASON [ " + db.getErrorMessage() + " ]" );             
          }
          else if( command.trim().toLowerCase().equals( "set" ) ){
              LinkedList<Event> events = db.getEvents( event );
              for( Event temp : events ){
                  System.out.println( "************************" );
                  temp.print();
                  System.out.println( "************************" );
              }             
          }
          else {
            System.out.println( "Command [ " + command + " ]" );
            displayUsage();
          }
          System.out.println();
       }

       db.Disconnect();
   }
}
