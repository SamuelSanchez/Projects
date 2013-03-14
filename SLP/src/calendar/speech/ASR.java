package calendar;

//This is the frame for recognizing speech
public interface ASR {

    public void deallocate();

    public void speechRecognition();

    public void naturalLanguageUnderstanding();

//    public String[][] getSpeech();

    //Let's look for a keyword to exit the program
    public boolean isProgramQuit();

    //private void setProgramQuit();
}
