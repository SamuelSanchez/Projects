package calendar;

import java.util.LinkedList;

public class TaskManager implements TaskManagerFrame {
   private DialogManager dialog;
   private Data dataManagement;
   private Database database;

   public TaskManager( Configuration_Data data, String dbPassword ) throws DialogException
   {
      dataManagement = Data.getInstance();
      dialog = new DialogManager( data, dataManagement );
      database = new Database( dbPassword );
   }

   public void execute()
   {
      try{
          database.Connect();

          //Start Dialog Thread
          dialog.start();

          //Let's continue until the user Quits
          while( !dialog.isProgramQuit() ){
              updateDatabase();
              //updateGoogleCalendar();
          }
       }catch (Exception e){
           System.out.println( "Error : " + e.toString() +
                               "\nEvent Manager will end!" );
       }
       finally{
           //Disconnect from DataBase
       }
   }

   public void updateDatabase()
   {


   }


   public void updateGoogleCalendar()
   {

   }
}
