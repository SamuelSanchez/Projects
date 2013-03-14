package calendar;

//This interface is the frame that we have to implement
public interface DocumentValidatorFrame {

     //Cannot be left as private
     //private boolean validateAudioFiles();

     //Retrieves the information from the document
     public boolean validateConfiguration( String document );

     //Returns the class Configuration_Data which holds all the information retrieved
     public Configuration_Data getConfigurationData() throws Exception;

     //To display the information
     public void print();
}
