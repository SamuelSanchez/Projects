package calendar;

import java.util.Scanner;
//import java.util.Locale;
//import javax.speech.*;
//import javax.speech.synthesis.*;
//import javax.speech.recognition.*;

//This class will listen to the User
//It's not using SPHINX
public class SpeechEngine implements ASR {

    private Configuration_Data configuration;
    private String bestResult;
    private boolean programQuit = false;
//    private Synthesizer synthesizer;
//    private Recognizer recognizer;
    private Scanner scan;
    private String input;
    private String output;

    public SpeechEngine( Configuration_Data input ) throws DialogException
    {
      try{
          configuration = input;
          scan = new Scanner(System.in);
      }catch(Exception e) { throw new DialogException( "SpeechEngine : " + e.toString() ); }
    }

/*    //Create the recognizer that will listen to the User
    private Recognizer createRecognizer( String locale ) throws EngineException
    {   //Recognizer from locale (en,es,fr...) - (we have a grammar)
        RecognizerModeDesc rec = new RecognizerModeDesc();
        rec.setDictationGrammarSupported( true );
        rec.setLocale( new Locale( locale, "" ) );
        return Central.createRecognizer( rec );
    }

    //Create the synthesizer who will talk to the User
    private Synthesizer createSynthesizer( String locale ) throws EngineException
    {   
        SynthesizerModeDesc required = new SynthesizerModeDesc( );
        //Set the language - en(English), es(Spanish), fr(French)
        required.setLocale( new Locale( locale, "" ) );        //Voice type, age, gender
        required.addVoice( new Voice ( null, Voice.GENDER_MALE, Voice.AGE_YOUNGER_ADULT, null ) );
        return Central.createSynthesizer( required );
    }
*/
    public void deallocate()
    {
      try{
      //   recognizer.deallocate();
          scan.close();
      }catch (Exception e){}
    }

    public void speechRecognition()
    {
      try{
          System.out.println ("Input : " );
          input = scan.nextLine();
          //PROCESS INPUT
          output = input;
          bestResult = input;
       }catch(Exception e){
	   bestResult = ""; //To handle errors, send nothing
       }
    }

    public void naturalLanguageUnderstanding()
    {
      try{
         //OUTPUT
         System.out.println ("Output : " + output );
      }catch (Exception e){
          bestResult=""; //To handle errors, send nothing
      }
    }

/*    public String getSpeech()
    {
       return bestResult;
    }
*/
    public String[][] getSpeech(){
       String[][] temp = { {""} };
       return temp;
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
