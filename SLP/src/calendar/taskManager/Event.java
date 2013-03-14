package calendar;

import java.util.StringTokenizer;

//Class containts the Calendar event
public class Event{
    private String Type = null;
    private int Year = -1;          // Let's keep the last two digits
    private int Month = -1;
    private String MonthStr = null;
    private int Day = -1;
    private int Hour = -1;          //Should be military time [ 24h ]
    private int Min = -1;
    private String Location = null;
    private long Key = -1;                     //Key is in the format of year month day hour min
    private boolean AUTO_DATE = true;   //Only for testing
    private static SLPConstants slpc;
    
    public Event(){}

    public void setType( String type ){
       Type = type;
    }

    public void setDate( int year, int month, int day ){
       Year = year;
       Month = month;
       Day = day;

       //Setting the date
       if( AUTO_DATE ){
          switch( Month ){
             case 1:
                MonthStr = slpc.JAN;
             break;
             case 2:
                MonthStr = slpc.FEB;
             break;
             case 3:
                MonthStr = slpc.MAR;
             break;
             case 4:
                MonthStr = slpc.APR;
             break;
             case 5:
                MonthStr = slpc.MAY;
             break;
             case 6:
                MonthStr = slpc.JUN;
             break;
             case 7:
                MonthStr = slpc.JUL;
             break;
             case 8:
                MonthStr = slpc.AUG;
             break;
             case 9:
                MonthStr = slpc.SEP;
             break;
             case 10:
                MonthStr = slpc.OCT;
             break;
             case 11:
                MonthStr = slpc.NOV;
             break;
             case 12:
                MonthStr = slpc.DEC;
             break;
          }
       }
    }

    public void setMonth( String month ){
        MonthStr = month;
    }

    public void setTime( int hour, int min ){
        Hour = hour;
        Min = min;
    }

    public void setLocation( String location ){
        Location = location;
    }
 
    //After the key has been set, then the Event is valid to be input into the DB
    //Also in order to give a key, at least the type must be set
    public boolean setKey(){
        if( Year > 0 && Month > 0 && Day > 0 && Hour > 0 && Min > -1 && Type != null){
            Key = KeyCreator.getKey( Year, Month, Day, Hour, Min );
            return true;
        }
        return false;
    }

    //Only useful for retrieving data from text file
    private void setKey( long key ){
        Key = key;
    }

    public String getType(){
        if( Type.isEmpty() ) return null;
        return Type;
    }

    //year-month-day  -> use String.split("-");
    public String getDate(){
        if( Year > 0 && Month > 0 && Day > 0 )
           return Year + "-" + Month + "-" + Day;
        return null;
    }

    public String getMonth(){
       if( MonthStr.isEmpty() ) return null;
       return MonthStr;
    }

    //hour-min
    public String getTime(){
        if( Hour > 0 && Min > -1 )
           return Hour + "-" + Min;
        return null;
    }

    public String getLocation(){
        if( Location.isEmpty() ) return null;
        return Location;
    }

    public long getKey(){
        return Key;
    }

    //Get a string and return an Event
    public static Event getEvent( String data ){
       try{
           StringTokenizer info = new StringTokenizer( data, Constants.DATA_SEPARATOR );
           
           Event temp = new Event();
           temp.setKey( Long.parseLong( info.nextToken() ) );
           temp.setType( info.nextToken() );
           String date[] = info.nextToken().split( "-" );
           temp.setDate( Integer.parseInt( date[0] ), Integer.parseInt( date[1] ), Integer.parseInt( date[2] ) );
           temp.setMonth( info.nextToken() );
           String time[] = info.nextToken().split( "-" );
           temp.setTime( Integer.parseInt( time[0] ), Integer.parseInt( time[1] ) );
           temp.setLocation( info.nextToken() );
           return temp;
        }catch(Exception e){
            if( Debugger.IMPORTANT ) System.out.println( "Error : " + e.toString() );
            return null;
        }
    }

    public void print(){
        System.out.println( "EVENT - DEBUGGER : \n" + 
                            "Type     [ " + Type + " ]\n" +
                            "Date     [ " + Month + " - " + Day + " - " + Year + " ]\n" + 
                            "Time     [ " + Hour + ":" + Min + " ]\n" + 
                            "Location [ " + Location + " ]\n" +
                            "Key      [ " + Key + " ]" );
    }
}
