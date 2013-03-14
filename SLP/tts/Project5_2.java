import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;
import java.net.URL;

public class Project5_2{

   public static String prompt2 = "./prompt-2.pl";
   public static Process festival;
   public static String[] sentence = {"How may I help You? You can say add, search, view or delete an appointment. You can also say help, cancel or exit at anytime",
				      "Correct!",
				      "Great!",
				      "Sure!",
				      "Please tell me if it's a doctor's appointment, dentist's appointment, school's appointment, or a reminder",
				      "When do you have your doctor's appoinment?",
				      "When do you have your dentist's appointment?",
				      "When do you have your school's appointment?",
				      "What kind of reminder is it? You can say Meet up with a friend, Call a friend, or pick up a package",
				      "Hello, how can I help you today? Please say add, search, view or delete an appointment. Or you can say help, cancel or exit at anytime",
				      "Would you like to add an appointment, correct? Say yes to confirm, cancel to return to the main menu",
				      "Would you like to search an appointment, correct? Say yes to confirm, cancel to return to the main menu",
				      "Would you like to view an appointment, correct? Say yes to confirm, cancel to return to the main menu",
				      "Would you like to delete an appointment, correct? Say yes to confirm, cancel to return to the main menu",
				      "When would you like to set your appointment?",
				      "What day would you like to search for your appointment?",
				      "Which appointment would you like to view?",
				      "Which day would view your appointment?",
				      "What appointment should I delete for you?",
				      "To confirm deleting this appointment say Yes or say No to cancel",
				      "Your appointment has been deleted",
                                      "Appointment successfully added",
                                      "Your reminder has been set up",
                                      "Would you like to add more appointments? Say yes to add, No to return to the main menu or exit to terminate the program",
                                      "You are exiting the application. Good bye",
                                      "Would you like me to remind you to Meed up with a friend, call a friend or pick up a package?",
                                      "Hello, how can I help you today?",
                                      "What is the appointment. Is it a doctor's appointment, dentist's appointment, school appointment, or a reminder?",
                                      "Please identify the type of your appointment by saying a doctor's appointment, dentist's appointment, school's appointment, or a reminder"};

   public static void main(String[] args){
      try{
        // StringBuffer arguments = new StringBuffer();
        // for( String text : args )
        //    arguments.append( text + " " );
        // System.out.println( "Args: " + arguments.toString() );
        // festival = Runtime.getRuntime().exec(prompt + " " + arguments.toString() );

       for( int i = 0; i < sentence.length; i++ ){
        try{
           festival = Runtime.getRuntime().exec( prompt2 + " testing" + i + ".wav:" + sentence[i] );
        /*    new Thread(new Runnable() { // Copied from online
                       public void run() {
                          try {
                              Clip clip = AudioSystem.getClip();
                              AudioInputStream inputStream = AudioSystem.getAudioInputStream(new File("testing.wav"));//Project5_2.class.getResourceAsStream("testing.wav"));
                              clip.open(inputStream);
                              clip.start(); 
                          } catch (Exception e) {
                            System.err.println(e.getMessage());
                          }
                       }
           }).start();
       */               
         //URL url = Project5_2.class.getClassLoader().getResource("testing.wav");
         //AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
         //Clip clip = AudioSystem.getClip();
         //clip.open(audioIn);
         //clip.start();

           //festival = Runtime.getRuntime().exec( "xterm -e " + prompt + " " + sentence[i] );
	   //Runtime sound = Runtime.getRuntime().exec( "pidof xterm" ); //Gets the process id of xterm to wait until it finishes
	   //Not going to get the pidof because then it will take too much code - it can easily be done with shell but it makes
 	   //no sence to make a script that will call to another script. So let's use java.
           Thread.sleep(5000);
	}catch(Exception e){
             System.out.println("Error: " + e.toString() );
  	}
       }
      }catch(Exception e){ }
   }
}
