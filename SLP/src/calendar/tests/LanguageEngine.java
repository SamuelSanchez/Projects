package calendar;

import javax.speech.*;
import javax.speech.synthesis.*;
import java.util.Locale;
import java.io.*;

public class LanguageEngine implements NLG {

    private static String  callFestival = "./talkToUser.sh";
    private Synthesizer talkToUser;
    private StringBuffer dataToTalk; 
    private Process speak;

    public LanguageEngine() throws DialogException
    {
      try{
          dataToTalk = new StringBuffer();
          talkToUser = createSynthesizer( "en" );

           //if( talkToUser == null )
	      //throw new DialogException(" Could not load general domain Synthesizer");

          //talkToUser.allocate();
          //talkToUser.resume();
      }catch (Exception e){ throw new DialogException( "LanguageEngine : " + e.toString() ); }
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
   
    public void deallocate()
    {
      try{
          talkToUser.deallocate();
      }catch (Exception e){}
    }

    public void naturalLanguageGeneration( String[] output )
    {
       //dataToTalk.append( output );
    }

    public void textToSpeech()
    {
      try{
           speak = Runtime.getRuntime().exec(callFestival + " " + dataToTalk.toString() );
           dataToTalk.replace( 0, dataToTalk.length(), "" ); //Let's empty the data
      }catch (Exception e){
          if( Debugger.IMPORTANT )
             System.out.println("Language Engine - Error : " + e.toString() );
      }
    }
}
