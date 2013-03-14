//package parser;
import java.util.ArrayList;
import java.util.LinkedList;
import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.JScrollPane;
//TODO: Finish after Research
public class ExperimentTable{
   private static boolean TABLE_DEBUG = true;
   private LinkedList<LogNode> logs;
   private ArrayList<String> types;
   private ArrayList<String> properties;
   private JTable table;
   private TableColumn column;

   ExperimentTable( ArrayList<String> properties, ArrayList<String> types ){
      logs = new LinkedList<LogNode>();
      this.types = types;
      this.types.add( 0, "\t" );
      this.properties = properties;

      if( TABLE_DEBUG ){
         for( String type: types )
            System.out.println("TYPE [ " + type + " ]" );
         for( String prop: properties )
            System.out.println("PROPERTY [ " + prop + " ]" );
      }
   }
 
   public JScrollPane getTable(){
      return (new JScrollPane(table));
   }

   public void add( LogNode log ){
      logs.add( log );
   }
   
   public void update(){
      try{
         int prop = properties.size();
         int rows = logs.size() * prop;
         int cols = types.size();
         //Create Columns Names
         String[] columnNames = new String[ types.size() ];
         for( int i = 0; i < types.size(); i++ )
              columnNames[i] = (String) types.get(i);
         if( TABLE_DEBUG ){
            for( String cn: columnNames )
                System.out.println("Column [ " + cn + " ]" );
            System.out.println( "LOGS       [ " + logs.size() + " ]" ) ;
            System.out.println( "Properties [ " + prop + " ]" ) ;
            System.out.println( "COLUMNS    [ " + cols + " ]" ) ;
            System.out.println( "ROWS       [ " + rows  + " ]" );
         }

         //Create Row and populate Contents
         Object[][] contents = new Object[ rows ][ cols ];

         //Populate with space - to Not have exceptions - Also populate the row names
         for( int r = 0; r < rows; r++ ){
            for( int c = 0; c < cols; c++ ){
                contents[r][c] = "\t";
                contents[r][0] = properties.get(  r%prop );
            }
         }
         //Populate with Content
        /* for( int c = 0; c < cols; c++ ){
            for( int r = 1; r < rows; r++ ){
                contents[r][0] = logs.get( r%prop ).getPointsNumber();
                contents[r][1] = logs.get( r%prop ).getDurationMins() + " mins - " + logs.get( r%prop ).getDurationSecs() + " secs - "  + logs.get( r%prop ).getDurationMSecs() + " msecs ";
                contents[r][2] = logs.get( r%prop ).getPointsNumber();
                contents[r][3] = logs.get( r%prop ).getPointsNumber();
                contents[r][4] = logs.get( r%prop ).getPointsNumber();
                contents[r][5] = logs.get( r%prop ).getPointsNumber();
                contents[r][6] = logs.get( r%prop ).getPointsNumber();
            }
         }       
       */  

         table = new JTable( contents, columnNames );
         //table.setAutoResizeMode( table.AUTO_RESIZE_OFF );
         table.setDragEnabled( false );
         table.setRowHeight( 20 );
      }catch(Exception e){
          System.out.println( "Error : " + e.toString() );
      }
   }
}
