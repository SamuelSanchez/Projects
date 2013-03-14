package calendar;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileInputStream;

public class DocumentValidator implements DocumentValidatorFrame {

     private Configuration_Data configuration_Data;

     public DocumentValidator(){
        super();
        configuration_Data = null;
     }

     //If we are using the wave files, then let's get the audio file names
     private boolean validateAudioFiles()
     {
        try{
            BufferedReader conf = new BufferedReader( new InputStreamReader( new FileInputStream( configuration_Data.Audio_Files ) ) );
  
            //Values needed to parse the file
            String line = conf.readLine();
            String[] commands;

            while( line != null ){
               //Ignore empty lines or comments with //
               if( !( ( line.trim().equals("") ) || ( line.charAt(0) == '/' && line.charAt(1) == '/' ) ) ){
                  //Make sure that we are parsing the right files
                  if( Debugger.DEBUG )
                     System.out.println( line );

                  commands = line.split(" ");
                
                  if( commands[0].trim().toLowerCase().equals("file") ){
                      configuration_Data.audioData.addFirst( commands[1].trim() );
                  }
                  else{
                     System.out.println( "Unknown Command : " + commands[0] );
                  }
               }
	       //Read the next line
               line = conf.readLine();
            }
            return true;
        }catch (Exception e){
           return false;
        }
     }


     //Add here "any keyword" that you want to keep from the file
     public boolean validateConfiguration( String document )
     {
        try{    
            BufferedReader file = new BufferedReader( new InputStreamReader( new FileInputStream( document ) ) );
      
            //Values needed to parse the file
            configuration_Data = new Configuration_Data();;
            String[] commands;
            String Audio_Config = "";
            String Microphone_Config = "";
            String line = file.readLine();

            while( line != null ){
               //Ignore empty lines or comments
               if( !( ( line.trim().equals("") ) || ( line.charAt(0) == '/' && line.charAt(1) == '/' ) ) ){
                  //Make sure that we are parsing the right files
                  if( Debugger.DEBUG )
                     System.out.println( line );

                  commands = line.split(" ");
                
                  if( commands[0].trim().toLowerCase().equals("audio") ){
                     if( commands[1].trim().toLowerCase().equals("-w") )
                        configuration_Data.audio = Constants.AUDIO.WAVE;
                     else if( commands[1].trim().toLowerCase().equals("-m") )
                        configuration_Data.audio = Constants.AUDIO.MICROPHONE;
                     else{
                        System.out.println( "Unknown AUDIO : " + commands[1] +
                                          "\nSetting to Microphone input" );
                     }
                  }
                  else if( commands[0].trim().toLowerCase().equals("grammar") ){
                      configuration_Data.grammar_file = commands[1].trim();
                  }
                  else if( commands[0].trim().toLowerCase().equals("grammarurl") ){
                      configuration_Data.grammar_url = commands[1].trim();
                  }
                  else if( commands[0].trim().toLowerCase().equals("afile") ){
                      configuration_Data.Audio_Files = commands[1].trim();
                  }
                  else if( commands[0].trim().toLowerCase().equals("aconf") ){
                      Audio_Config = commands[1].trim();
                  }
                  else if( commands[0].trim().toLowerCase().equals("mconf") ){
                      Microphone_Config = commands[1].trim();
                  }
                  else{
                     System.out.println( "Unknown Command : " + commands[0] );
                  }
               }
	       //Read the next line
               line = file.readLine();
            }

            //Setting the Configuration File
            if( configuration_Data.audio == Constants.AUDIO.WAVE && !Audio_Config.equals("") ){
               configuration_Data.Configuration = Audio_Config;

               //Let's get the audio files
               if( configuration_Data.Audio_Files.equals("") || !validateAudioFiles() )
                    throw new InvalidInputException( "Check Audio File : " + configuration_Data.Audio_Files );
 
            }
            else if( configuration_Data.audio == Constants.AUDIO.MICROPHONE && !Microphone_Config.equals("") )
               configuration_Data.Configuration = Microphone_Config;
            else
                throw new InvalidInputException ( "Check Sphinx Configuration File" );

            return true; 
        }catch (Exception e){
            System.out.println( "Error : " + e.toString() );
            return false;
        }
     } 

     public Configuration_Data getConfigurationData() throws Exception
     {
         if( configuration_Data == null )
            throw new Exception( "Cannot get Data Configuration before parsing the Document!" );

         return configuration_Data;
     }

     //Debugging
     public void print()
     {
        try{
            System.out.println( "\nAUDIO         : " + configuration_Data.audio +
                                "\nGrammar       : " + configuration_Data.grammar_file +
                                "\nGrammar URL   : " + configuration_Data.grammar_url +
                                "\nAudio File    : " + configuration_Data.Audio_Files +
                                "\nConfiguration : " + configuration_Data.Configuration );
        }catch (Exception e){}
     }
}
