package calendar;

import java.util.LinkedList;
import java.util.ArrayList;

public class DialogManager extends Thread {//implements Runnable {

    private SpeechRecognition listenToUser;
    private LanguageGeneration talkToUser;
    private Configuration_Data resources;
    private Data dataDB;
    private Timer dialogTimer;
    private boolean programQuit = false;

    private boolean userWantsData = false;
    private boolean isDataComplete = false;
    private String[] dataToTalk;
    private String[] DB_Query;
    private String[] data={"","","","",""};
    private String prevOutput="";
	

    public DialogManager( Configuration_Data config, Data data ) throws DialogException
    {
       dataDB = data;
       listenToUser = new SpeechRecognition( config ); // new SpeechEngine( config );
       talkToUser   = new LanguageGeneration(); //new LanguageEngine();
       dialogTimer  = new Timer();
    }


    //This is the heart of the ThreadlistenToUser
    public void run()
    {
      try{ 
             Thread.sleep(3000);
      }catch(Exception e){}


      try{
          if( Debugger.USEFUL )
             System.out.format("%s: %s%n", Thread.currentThread().getName(), "Dialog - Manager");

         while(true){
             try{
			listenToUser.speechRecognition();
			ArrayList<String> tempData=listenToUser.extractData();

			//Command interpretation
			{
				String cData=tempData.get(0);
				if(data[0].equals("")&&cData.equals("")){
					talkToUser.textToSpeech(prevOutput="I need to know what to do");
					continue;
				}
				
				else if(cData.equals("no")){
						if(prevOutput.contains("Do you want")){
							promptBeforeExec();
							continue;
						}
				}
				else if(cData.equals("exit")){
					talkToUser.textToSpeech("Bye!");
					break;
				}
				
				else if(cData.equals("upload")){
					System.out.print(prevOutput="Okay I'm going to "+data[0]+" "+data[1]);
					if(!data[2].equals(""))
						System.out.print(" on "+data[2]);
					if(!data[3].equals(""))
						System.out.print(" at "+data[3]+" o'clock ");
					if(!data[4].equals(""))
						System.out.print(" in "+data[4]);
					System.out.println();
					/*
					 *Insert to database code here
					 */
					reset(data);
					talkToUser.textToSpeech(prevOutput="What else do you need?");
					continue;
				}
				
				else if(data[0].equals("")){
						data[0]=cData;
				}
			}
			
			//Type interpretation
			{
				String tData=tempData.get(1);
				if(data[1].equals("")){
					if(tData.equals("")){
						talkToUser.textToSpeech(prevOutput="I need to know what will be happening");
						continue;
					}
					else{
						data[1]=tData;
					}
				}
				if(!data[1].equals("")){
					if(!tData.equals("")){
					}
				}
			}
			
			//Remainder Interpreter
			{
				//date interpretation
				String dData=tempData.get(2);
				if(data[2].equals("")){
					if(!dData.equals("")){
						data[2]=dData;
					}
				}
				else{
					if(!dData.equals("")){
					}
				}
				
				//time interpretation
				String timeData=tempData.get(3);
				if(data[3].equals("")){
					if(!timeData.equals("")){
						data[3]=timeData;
					}
				}
				else{
					if(!timeData.equals("")){
					}
				}
				
				//location interpretation
				String lData=tempData.get(4);
				if(data[4].equals("")){
					if(!lData.equals("")){
						data[4]=lData;
					}
				}
				else{
					if(!lData.equals("")){
					}
				}
			}
			
			if(tempData.get(0)!="upload"){
				String promptForMore="";
				for(int i=0;i<data.length;i++){
					if(data[i].equals("")){
						if(i==2)
							promptForMore+="date"+" ";
						else if(i==3)
							promptForMore+="time"+" ";
						else if(i==4)
							promptForMore+="location"+" ";
					}
				}
				if(promptForMore.length()>0&&!prevOutput.equals("no"))
					talkToUser.textToSpeech(prevOutput="Do you want to also provide "+promptForMore+"?");
				else
					promptBeforeExec();
			}
                    }catch(Exception e) { e.printStackTrace(); }
		}
        if( Debugger.NECESSARY )
                System.out.println("TIME PASSED : " + dialogTimer.time() + "\n");
       }catch (Exception e){
           System.out.println( "Error : " + e.toString() +
                               "\nDialog Manager will end!" );
           setProgramQuit();
       }
       finally{
           listenToUser.deallocate();
           talkToUser.deallocate();
       }
    }

	private void reset(String[] data){
		for(int i=0;i<data.length;i++)
			data[i]="";
	}
	
	private void promptBeforeExec(){
			String output="";
			output+=data[0] + " a " + data[1];
			if(!data[2].equals(""))
				output+=" on "+data[2];
			if(!data[3].equals(""))
				output+=" at "+data[3]+" o'clock";
			if(!data[4].equals(""))
				output+=" in "+data[4]+"?";
			talkToUser.textToSpeech(output);
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
