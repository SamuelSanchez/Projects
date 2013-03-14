package calendar;

//import calendar.tools.Constants;
import java.util.LinkedList;

public class Configuration_Data {
    //Either Microphone or Wave
    public Constants.AUDIO  audio;

    //Store the grammar file, used for debbuging purposes with the microphone
    public String grammar_file;

    //Store the grammar URL, used to get the values for the Tags
    public String grammar_url;

    //To store the path to the audio files
    public String Audio_Files;

    //To store the configuration for mic or wave (xml)
    public String Configuration;

    //To store any wave files names
    public LinkedList<String> audioData;

    //Setting default values
    public Configuration_Data()
    {
        super();
        audio = Constants.AUDIO.MICROPHONE;
        grammar_file = "";
        grammar_url = "";
        Audio_Files = "";
        Configuration = "";
        audioData = new LinkedList<String>();
    }
}
