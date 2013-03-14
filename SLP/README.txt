To RUN:

     1) COPY AND PASTE THE FOLDER:
	     "Calendar-Dev-v0.7" into any directory.
    
     2) To compile the Program go inside the Directory : "Calendar-Dev-v0.7"
        and run:  
              ant
 
     3) To run the Program use a terminal with the directory pointing to : "Calendar-Dev-v0.7"
        and run:
             java -Xmx256M -jar bin/calendarJar.jar -f src/calendar/config_files/calendar.conf
             java -cp .:lib/sphinx4.jar calendar.mainApp -f resources/config_files/calendar.conf




To know the commands type: ant -projecthelp

ant : To Compile the Code
ant run : To Run the Code
ant jar : Creates a executable jar excluding the source file and build.xml file
ant runJ : Runs the executable jar, it creates the jar by default

By default leave it for use of the Microphone when you create a Jar for it



TODO: IMPLEMENT JAVADOCS

IF TTS FOLDER IS GOING TO BE MOVED. Then path variables must be updated as well.
Do not move tts folder. Only move the .java and .pl files into the directory where
tts is going to be used.
