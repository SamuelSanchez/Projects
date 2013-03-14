package calendar;

public class LanguageGeneration {

    public static Process festival;
 
    public LanguageGeneration(){
      try{
         festival = Runtime.getRuntime().exec( "xterm -e " + Constants.PROMPT + "How may i help you?" );
      }catch(Exception e){
         System.out.println("Error : " + e.toString() );
      }
    }

    public void deallocate()
    {
       System.out.println("bye");
    }

    //TODO: FIX THIS - Make it have a distinction between txt and audio
    public void naturalLanguageGeneration( String[] output )
    {
     try{
	if(output[0].equals("")){
	    festival = Runtime.getRuntime().exec( "xterm -e " + Constants.PROMPT + "Please tell me what to do");
	}
	else if(output[1].equals("")){
		festival = Runtime.getRuntime().exec( "xterm -e " + Constants.PROMPT + "I need to know what the event will be");
	}
	else{
		String text = output[0] + " a " + output[1];
		if(!output[2].equals(""))
			text += " on "+output[2];
		if(!output[3].equals(""))
			text += " at "+output[3]+" o'clock";
		if(!output[4].equals(""))
			text += " in "+output[4]+"?";
		festival = Runtime.getRuntime().exec( "xterm -e " + Constants.PROMPT + text );
	}
     }catch(Exception e){
         System.out.println( "Error : " + e.toString() ); 
      }
    }

    public void textToSpeech( String text ){
      try{
          festival = Runtime.getRuntime().exec( "xterm -e " + Constants.PROMPT + text );
      }catch(Exception e){
         System.out.println( "Error : " + e.toString() );
      }
    }
}
