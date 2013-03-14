package calendar;

//import javax.speech.*;
//import javax.speech.synthesis.*;
//import java.util.Locale;

public class HelloWorld {

        public static Process Festival;
        public static String  callFestival = "./talkToUser.sh";
        public static String  helloWorld = "Hello World!";

	public static void main(String args[]) {
		try {
                       StringBuffer test = new StringBuffer();
                       for(int i=0; i<args.length; i++)
                            test.append( args[i] + " " );
 			
			System.out.println("Args : " + test.toString() );

                        //Festival
                        //Call festival shell from here
                        Festival = Runtime.getRuntime().exec(callFestival + " " + helloWorld );
 

/*			// Create a synthesizer for English
			Synthesizer synth = Central.createSynthesizer(
				new SynthesizerModeDesc(Locale.ENGLISH));

			// Get it ready to speak
			synth.allocate();
			synth.resume();

			// Speak the "Hello world" string
			synth.speakPlainText("Hello, world!", null);

			// Wait till speaking is done
			synth.waitEngineState(Synthesizer.QUEUE_EMPTY);

			// Clean up
			synth.deallocate();
*/
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
