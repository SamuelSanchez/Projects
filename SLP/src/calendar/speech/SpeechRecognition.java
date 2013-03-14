package calendar;

import edu.cmu.sphinx.jsgf.JSGFGrammar;
import edu.cmu.sphinx.jsgf.JSGFGrammarException;
import edu.cmu.sphinx.frontend.util.AudioFileDataSource;
import edu.cmu.sphinx.recognizer.Recognizer;
import com.sun.speech.engine.recognition.BaseRecognizer;
import javax.speech.recognition.RuleGrammar;
import javax.speech.recognition.RuleParse;
import edu.cmu.sphinx.result.Result;
import edu.cmu.sphinx.util.props.ConfigurationManager;
import edu.cmu.sphinx.frontend.util.Microphone;
import edu.cmu.sphinx.tools.tags.ActionTagsParser;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;
import java.util.ArrayList;

//This class will listen to the User
public class SpeechRecognition {

    private Configuration_Data configuration;
    private ConfigurationManager confMan;          //Keeps the configuration for Sphinx
    private Recognizer recognizer;                 //Keeps recognition & configuration for Sphinx
    private JSGFGrammar jsgfGrammar;               //To Debug our grammar
    private BaseRecognizer baseRecognizer;
    private RuleGrammar ruleGrammar;        //Helps to Create a Rule in order to get Data from Tags
//    private RuleParse ruleParse;                   //Rule that will be passed to the tags
    private AudioFileDataSource dataSource;        //For audio input
    private Microphone microphone;                 //For Microphone input
    private Result result;                         //Store the result
    private String bestResult;
    private boolean programQuit = false;
    private static ActionTagsParser tags;                 //Here's where we are going to get our important data from
    private ArrayList<String> data = new ArrayList<String>(5);
    

    static public ActionTagsParser getTagsParser() {
        if (tags == null) {
            tags = new ActionTagsParser();
        }
        return tags;
    }

    public SpeechRecognition( Configuration_Data input ) throws DialogException
    {
      try{
          configuration = input;
          //Set up the Speech Configuration
          confMan = new ConfigurationManager( configuration.Configuration );
          //confMan = new ConfigurationManager( SpeechRecognition.class.getResource( configuration.Configuration ) );
          recognizer = (Recognizer) confMan.lookup( "recognizer" );
          recognizer.allocate();
          if( configuration.audio == Constants.AUDIO.WAVE ){
              dataSource = (AudioFileDataSource) confMan.lookup("audioFileDataSource");
          }
          else{
              microphone = (Microphone) confMan.lookup( "microphone" );
              baseRecognizer = new BaseRecognizer();
              baseRecognizer.allocate();
              //Make sure that the Microphone works
              if( !microphone.startRecording() )
                 throw new DialogException( "Microphone was not able to start!!!" +
                                            "\nThe program will quit!" );

              if( Debugger.DEBUG ){ //Let's Check if our .gram file is correctly done
               //  jsgfGrammar = (JSGFGrammar) confMan.lookup( "jsgfGrammar" );
               //  jsgfGrammar.loadJSGF( configuration.grammar_file );
               //  jsgfGrammar.dumpRandomSentences(1000);  //Drop possible combination of sentences
              }
              //Tags
              ruleGrammar = baseRecognizer.loadJSGF( null, configuration.grammar_url );
              ruleGrammar.setEnabled( true );
              baseRecognizer.commitChanges();
//              tags = new ActionTagsParser();
          }

       for( int i = 0; i < 5 ; i++)
           data.add("");

      }catch(Exception e) { throw new DialogException( e.toString() ); }
    }

    public void deallocate()
    {
      try{
         recognizer.deallocate();
         if( configuration.audio == Constants.AUDIO.MICROPHONE )
             baseRecognizer.deallocate();
      }catch (Exception e){}
    }

    public void speechRecognition()
    {
      try{
             if( configuration.audio == Constants.AUDIO.WAVE ) 
                 audioRecognizer();
             else 
                 microphoneRecognizer();
       }catch(Exception e){
           if( Debugger.DEBUG ) System.out.println( "Error : " + e.toString() );
	   bestResult = ""; //To handle errors, send nothing
       }
    }

    private void audioRecognizer() throws Exception
    {
       //If there are no more wave files, quit!
       if( configuration.audioData.size() == 0 ){
          setProgramQuit();
          return;
       }
       String test = configuration.audioData.removeLast();  
       dataSource.setAudioFile( new File( test ).toURI().toURL() , null );		   //This is with reference to the 'Dev folder'
       //dataSource.setAudioFile( SpeechRecognition.class.getResource( test ) , null );   //This is with reference to the 'Jar file'

       if( Debugger.DEBUG ) System.out.println( "Using file : " + test );
       //Get Audio
       result = null;
       bestResult = null;
       while( result == null )
         result = recognizer.recognize();
       bestResult = result.getBestResultNoFiller();
       if( Debugger.DEBUG ) System.out.println( "RAW DATA : " + bestResult );
    }

    private void microphoneRecognizer() throws Exception
    {
      if( Debugger.NECESSARY ) System.out.println( "TALK : " );

      //Clear previous data
      result = null;
      bestResult = null;

      //Getting data from User
      while( result == null )
	   result = recognizer.recognize();
      bestResult = result.getBestResultNoFiller();//getBestFinalResultNoFiller();//
      if( Debugger.NECESSARY ) System.out.println( "RAW DATA : " + bestResult );

      RuleParse ruleParse = ruleGrammar.parse( bestResult, null ); //Create a rule with the data obtained from the user

      if( ruleParse != null ){
         try{
         System.out.println("TAGS");
         getTagsParser().parseTags( ruleParse );//From what the user said, let's get the tags
         getWords ( getTagsParser() );
         }catch(Exception e){ System.out.println( "ERR_TAG : " + e.toString() ); }
      }
    }

    private void getWords( ActionTagsParser parser ) 
    {
       System.out.println("LENTH - " + data.size() );
       String command = (String) parser.get("command");
       if( command != null ) { 
          System.out.println( "Command [ " + command + " ]" );
          data.set( 0, command );
       }
       else { System.out.println( "Command is null" );
          data.set( 0, "" );
       }
System.out.println("0");
       String type = (String) parser.get("appointment");
       if( type != null ) { 
          System.out.println( "Type [ " + type + " ]" );
          data.set( 1, type );
       }
       else{ System.out.println( "Type is null" );
          data.set( 1, "" );
       }
System.out.println("1");
       String date = (String) parser.get("date");
       if( date != null ) { 
          System.out.println( "date [ " + date + " ]" );
          data.set( 2, date );
       }
       else{ System.out.println( "data is null" );
          data.set( 2, "" );
       }
System.out.println("2");
       String time = (String) parser.get("time");
       if( time != null ) { 
          System.out.println( "time [ " + time + " ]" );
          data.set( 3, time );
       }
       else{ System.out.println( "time is null" );
          data.set( 3, "" );
       }
System.out.println("3");
       String location = (String) parser.get("location");
       if( location != null ) { 
          System.out.println( "location [ " + location + " ]" );
          data.set( 4, location );
       }
       else{ System.out.println( "location is null" );
          data.set( 4, "" );
       }

       System.out.println("DONE GETTING DATA" );
       System.out.println("Length [ " + data.size() + " ]" );
    }

    public ArrayList<String> extractData(){
       System.out.println("Retrieve Data" );
        return data;
    }


    //Let's parse what the user said - Also let's check if the User said (Quit Program or Exit Program)
    public void naturalLanguageUnderstanding()
    {
      try{

      }catch (Exception e){
          System.out.println( "Error : " + e.toString() );
          bestResult=""; //To handle errors, send nothing
      }
    }

    public String getSpeech()
    {
       return bestResult;
    }

    public boolean isProgramQuit()
    { 
       return programQuit;
    }

    private void setProgramQuit()
    {
       programQuit = true;
    }
}
