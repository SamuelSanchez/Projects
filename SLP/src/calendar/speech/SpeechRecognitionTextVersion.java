package calendar;

import java.util.Scanner;

public class SpeechRecognitionTextVersion implements ASR {
    private boolean programQuit;
    private String[][] bestResult;  //Store results
    private String data;            //Store user input
    private Scanner in;

    public SpeechRecognitionTextVersion( Configuration_Data input ) throws DialogException
    {
      try{
           programQuit = false;
           data = "";
	   in = new Scanner(System.in);
      }catch(Exception e) { throw new DialogException( e.toString() ); }
    }

    public void deallocate(){}

    public void speechRecognition()
    {
      try{
	   data = in.nextLine();
       }catch(Exception e){
           System.out.println( "Error : " + e.toString() );
	   data = ""; //To handle errors, send nothing
       }
    }

    //Let's parse what the user said - Also let's check if the User said (Quit Program or Exit Program)
    public void naturalLanguageUnderstanding()
    {
      try{
        String[][] tempSR = {{"command",""},{"types",""},{"date",""},{"time",""},{"location",""}};
	bestResult = tempSR;
	
	int dataPointer;
	
	if(data.indexOf("yeah")>=0||data.indexOf("yes")>=0 ||data.indexOf("finish")>=0)
		bestResult[0][1]="upload";
	
	if(data.indexOf("quit")>=0||data.indexOf("exit")>=0)
			bestResult[0][1]="exit";
	
	if(data.indexOf("add ")>=0||data.indexOf("make ")>=0||data.indexOf("put ")>=0)
		bestResult[0][1]="create";
	
	if(data.contains("in ")){
		dataPointer=data.indexOf("in ");
		String temp = data.substring(dataPointer+3);
		int endPoint=temp.indexOf(' ');
		if(endPoint==-1)
			bestResult[4][1]=temp.substring(0);
		else
			bestResult[4][1]=temp.substring(0,endPoint);
	}
	if(data.contains("at ")){
		dataPointer=data.indexOf("at ");
		String temp = data.substring(dataPointer+3);	
		int endPoint=temp.indexOf(' ');
		if(endPoint==-1)
			bestResult[3][1]=temp.substring(0);
		else
			bestResult[3][1]=temp.substring(0,endPoint);
	}
	if(data.indexOf("on ")>=0||data.indexOf("for ")>=0){
		int beginPoint=data.indexOf(' ',Math.max(data.indexOf("on "),data.indexOf("for ")));
		int endPoint=data.indexOf(' ',data.indexOf(' ',beginPoint+1)+1);
		if(endPoint==-1)
			bestResult[2][1]=data.substring(beginPoint);
		else
			bestResult[2][1]=data.substring(beginPoint,endPoint);
	}
	if(data.contains(" a ")){
		dataPointer=data.indexOf(" a ");
		String temp=data.substring(dataPointer+3);
		int[] endPointers={temp.indexOf("on "),temp.indexOf("at "),temp.indexOf("for "),temp.indexOf("in ")};
		int smallest=1000;
		for(int i=0;i<endPointers.length;i++){
			if(endPointers[i]>0 && endPointers[i]<smallest)
				smallest=endPointers[i];
		}
		if(smallest==1000)
			bestResult[1][1]=temp.substring(0);
		else
			bestResult[1][1]=temp.substring(0,smallest);
	}
      }catch (Exception e){
          System.out.println( "Error : " + e.toString() );
          String[][] tempSR = {{"command",""},{"types",""},{"date",""},{"time",""},{"location",""}};
	  bestResult = tempSR;  //To handle errors, send empty 
      }
    }

    public String[][] getSpeech()
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
