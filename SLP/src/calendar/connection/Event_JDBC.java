// THIS CLASS SHOULD BE DIVIDED INTO TWO SECTIONS
// SECTION 1: EVENT CLASS
//            It only holds the information for the event
// SECTION 2: EVENT MANAGER
//	      It places events into the db
// LIKE A LINKED-LIST AND NODE RELATIONSHIP


package calendar;

import java.util.LinkedList;
import java.sql.*;

class Event_JDBC{
/*
  //Database Connection
  private Connection dbConn;
  private boolean connection;
  private Statement dbInfo;
  //Default Time
  private static final double DEFAULT_ELAPSED_TIME = 0.50; //0.50 represents 30 mins
  private static final double DEFAULT_REMINDER_TIME = 1;
  //Variables 
  private String type;
  private String title;
  private String description;
  //Date -separate values
  private int day;
  private int month;
  private int year;
  //Time
  private double startTime;
  private double endTime;
  
  private int reminder_h;
  private int reminder_min;
  private String repetition;
  private LinkedList<String> guests;
  private String location;

  Event_JDBC(){
     this(null,null);
  }

  Event_JDBC(String type, String title){
     dbConn= null;
     connection = false;
     dbInfo = null;
     this.type = type;
     this.title = title;
     description = title;  //By default the title is the description
     day = -1;
     month = -1;
     year = -1;
     startTime = -1;
     endTime = -1;
     reminder_h = 0;
     reminder_min = 15;
     repetition = DatabaseConstants.REPEAT_ONCE; //in mins 
     guests = new LinkedList<String>(); //Default '0' meaning that there are no guests
     location = "0"; //Deafult '0' meaning that location was not inserted
  }

  boolean Connect(String pwd){
     try{
         //Connect to default Database
         Class.forName ( DatabaseConstants.DB_DRIVER ).newInstance();
         dbConn = DriverManager.getConnection ( DatabaseConstants.DB_DEFAULT , DatabaseConstants.DB_ROOT , pwd );

         if(DatabaseConstants.DEBUG_EVENT) 
	    System.out.println( "Successful connected to DB" );

         //Create method to modify database
         dbInfo = dbConn.createStatement();

         //Check if database for SpokenLanguages Exist, if not then create it
         dbInfo.executeUpdate( DatabaseConstants.DB_CHECK );

         dbInfo.executeUpdate( DatabaseConstants.DB_USE );

         //Check or create default table
	 dbInfo.executeUpdate( DatabaseConstants.DB_CHECK_TABLE );

	 connection = true;
         return true;
     }catch(Exception e){
         if(DatabaseConstants.DEBUG_EVENT){
            System.out.println( "Error : " + e.toString() );
            System.out.println( "Cannot connect to DB" );
         }
         return false; 
     }
  }

  boolean Disconnect(){
     try{
	 dbConn.close();
         return true;
     }catch(Exception e){
         if(DatabaseConstants.DEBUG_EVENT){
            System.out.println( "Error : " + e.toString() );
         }
         return false;
     } 
  }

  void setType(String type){
     this.type = type;
  }

  String getType(){
     return type;
  }

  boolean isTypeSet(){
     if(type == null)
	return false;
     return true;
  }

  void setTitle(String title){
     this.title = title;
  }

  String getTitle(){
     return title;
  }

  boolean isTitleSet(){
     if(title == null)
        return false;
     return true;
  }
 
  void setDescription(String description){
     this.description = description;
  }

  String getDescription(){
     return description;
  }

  boolean isDescriptionSet(){
     if(description == null)
        return false;
     return true;
  }

  void setDate(int d, int m, int y){
     //Make sure we have valid data
     if( (d > 31 || d < 0) || (m > 12 || m < 0) || (y < 2011) ) //Can not be a reminder before today
        return;

     day = d;
     month = m;
     year = y;
  }

  boolean isDateSet(){
     if( day < 0 || month < 0 || year < 0 )
        return false;
     return true;
  }

  void setTime(double st){
     if( st < 0 )
        return;
     startTime = st;
     endTime = st + DEFAULT_ELAPSED_TIME;
  }

  void setTime(double st, double et){
     if( st < 0 || et < 0 )
        return;
     startTime = st;
     endTime = et;
  }

  boolean isTimeSet(){
     if( startTime < 0 || endTime < 0 )
        return false;
     return true;
  }

  void setReminder(int h, int min){
     reminder_h = h;
     reminder_min = min;
  }

  void setRepetition(String repetition){
     this.repetition = repetition;
  } 

  void addGuest(String guest){
     guests.add(guest);
  }

  void clearGuest(){
     guests.clear();
  }

  void setLocation(String loc){
     location = loc;
  }

  //Query to add user - HAVE TO TEST!!!!!!!!!!!!!!!!  ''''d[-_0]b''''
  boolean addEvent(){
     try{
	 //Check the connection
         if( connection == false )
            return false;
         //Check if the main files are done
         if( !isTitleSet() || !isDateSet() || !isTimeSet() )
            return false;

         if( !isDescriptionSet() )
            setDescription( title );
	
	 //Temp string buffer for the guest
         StringBuffer guest_temp = new StringBuffer();
         
 	 //If we have no guests then insert '0'
         if( guests.size() == 0 )
	    guest_temp.append("0");
	
	 //Insert the list
	 else
	    for(int i = 0; i < guests.size(); i++)
		guest_temp.append( guests.get(i) + " , " );

         //Create Query to add user 
         dbInfo.executeUpdate( DatabaseConstants.ADD_EVENT + "(" + type + "," + title + "," + description + "," + day + "," + month + "," + year + "," +
			"CURDATE()" + "," + startTime + "," + endTime + "," + "CURTIME()" + startTime + "," + endTime + "," + "CURTIME()" + 
			"," + reminder_h + "," + reminder_min + "," + repetition + "," + guest_temp.toString() + "," + location + ");" );

         return true;
     }catch (Exception e){
        System.out.println ("Error: " + e.toString() ); 
	return false; 
     }
  }

  //DELETE - ONLY MAKE SURE THAT INSERTION WORKS
  void dummyInsert(){
     try{
          dbInfo.executeUpdate( DatabaseConstants.ADD_EVENT + "(\"Office\",\"TESTING TABLE\",\"NOTHING SO FAR\",1,1,1, CURDATE(), 0, 0, CURTIME(), 0, 0, CURTIME(), 0, 0, 'Weekly', 'ME', 'MANHATTAN');");
     } catch( Exception e){
	 if(DatabaseConstants.DEBUG_EVENT){
            System.out.println( "Error : " + e.toString() );
            System.out.println( "Cannot INSERT dummy data into DB" );
	 }
     }
  }

  int findMissingInformation(){
      if( connection == false )
	  return 0;//"Connect To Database!";

      if( !isTitleSet() )
          return 1;//"Set title!";

      if( !isDateSet() )
	  return 2;//"Set Date";

      if( !isTimeSet() )
	  return 3;//"Set Time";

      return -1; //"Unknow missing information" - Should never get here!      
  }

  void print(){
     System.out.println(toString());
  }

  public String toString(){
      String data = "\nType : " + type + "\nTitle : " + title + "\nDescription : " + description +
	            "\nDay : " + day + "\nMonth : " + month + "\nYear : " + year + "\nStart Time : " + startTime +
		    "\nEnd Line : " + endTime + "\nReminder Hour : " + reminder_h + "\nReminder Min : " + reminder_min +
		    "\nRepetition : " + repetition + "\nGuests Size : " + guests.size() + "\nLocation : " + location;
      return data;
  }
*/
}
