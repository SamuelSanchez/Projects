package calendar;

public final class mainApp {
   //Display Usage
   private static void displayUsage()
   {
       System.out.println("Usage: mainApp [option] \nOptions: \n" +
                          "\t-f Path_to_File_name \n"); 
   }

   public static void main(String[] args){
      try{
          DocumentValidatorFrame doc = new DocumentValidator();

          if( args.length < 1 ){
             displayUsage();
             return;
          }
          //Running from an executable Jar - If changes in the configuration, jar must be recompiled
          if( args[0].equals("-j") ){
             if( !doc.validateConfiguration( Constants.file_path ) )
                throw new InvalidInputException( "Check the Configuration File : " + Constants.file_path );
          }
	  //Using Configuration File - You can make any changes in the configuration - don't have to recompiled
	  else if( args[0].equals("-f") ){
             String fileName = args[1];
             if( !doc.validateConfiguration( fileName ) )
                throw new InvalidInputException( "Check the Configuration File : " + fileName );
	  }
          else{
             displayUsage();
             return;
          }

          //Let's check what we have
          if( Debugger.DEBUG ) doc.print();

          //Let's get the password for the Database
          String password = "";
//          PasswordGUI pt = new PasswordGUI();

//          while( !pt.isPassword() );
//          password = pt.getPassword();
//          pt.endThread();

          TaskManagerFrame taskManager = new TaskManager( doc.getConfigurationData(), password );
          taskManager.execute();

      } catch (Exception e){
           System.out.println( "Error : " + e.toString() );
      }
   }
}
