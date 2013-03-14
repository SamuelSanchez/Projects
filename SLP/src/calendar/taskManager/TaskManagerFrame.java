package calendar;

public interface TaskManagerFrame {

    //This will be the heart of the program
    public void execute();

    //Will update the data into the database
    public void updateDatabase();

    //Will update the data into Google Calendar
    public void updateGoogleCalendar();
}
