package calendar;

import java.text.DateFormat;
import java.util.Date;
import java.text.SimpleDateFormat;

public class KeyCreator{

   public KeyCreator(){
      super();
   }

   //TODO: We should check if it's a valid month, day, year, hour and min
   //This is left to do for the Dialog Manager
   public static final long getKey( int year, int month, int day, int hour, int min ){
      String y, m, d, h, mi;

      if( month < 10 ) m = "0" + month;
      else m = "" + month;

      if( day < 10 ) d = "0" + day;
      else d = "" + day;

      if( hour < 10 ) h = "0" + hour;
      else h = "" + hour;

      if( min < 10 ) mi = "0" + min;
      else mi = "" + min;

      //TODO: Check for year
      y = "" + year;

      if( month > 12 || day > 31 || hour > 24 || min > 59 ) 
          y = "00";

      return Long.parseLong ( y + m + d + h + mi );
   }

   //Let's only mark as invalid any search before today's date
   public static final boolean isKeyValid( long key ){
      long tempKey = createKey();
      if( key < tempKey || ("" + key).length() != 10 )
         return false;
      return true;
   }

   //Create key using today's date
   //Creates a key as year[last 2 digits] month day hour min
   public static final long createKey(){
      DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
      Date date = new Date();
      //Creates a key as year[last 2 digits] month day hour min
      return Long.parseLong( dateFormat.format(date).substring(2) + "0000" );
   }
}
