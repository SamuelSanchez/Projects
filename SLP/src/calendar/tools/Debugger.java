package calendar;

public interface Debugger {
    //Debugger for errors
    //Level 1 : Highest Priority
    //Level 4 : Lowest  Priority
    public static boolean IMPORTANT = false;  //Level 1 - Prints Critical Errors
    public static boolean NECESSARY = true;   //Level 2 - Prints Data that should be visual for the User
    public static boolean USEFUL    = true;   //Level 3 - Prints Data that can be useful when tracking the Logic Flow
    public static boolean DEBUG     = true;   //Level 4 - Prints All Data to check where we are getting stuck or the Running Flow of the program
}
