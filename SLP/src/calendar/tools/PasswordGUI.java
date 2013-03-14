package calendar;

import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

public class PasswordGUI implements Runnable, ActionListener, WindowListener {
     private JDialog dialog = null;
     private JPasswordField pField = null;
     private volatile boolean isDone = false;
     private volatile boolean isPassword = false;
     private static String CONNECT = "Connect";
     private StringBuffer password = null;
     private Thread passwordThread = null;
 
     public PasswordGUI()
     {
         password = new StringBuffer();
         //Create Dialog
         createWindow();
         passwordThread = new Thread( this, "Password GUI" );
         passwordThread.start();
     }

     private void createWindow()
     {
         dialog = new JDialog();
	 Container cp = dialog.getContentPane();
	 dialog.setTitle( "MySQL Password");
	 dialog.setSize( 400, 60 );
         dialog.setLocation( 50, 50 );
	 dialog.setResizable( false );
	 dialog.setDefaultCloseOperation( javax.swing.WindowConstants.DISPOSE_ON_CLOSE );
	 dialog.setVisible( true );
         //dialog.pack();

	 pField = new JPasswordField(20);
	 pField.setActionCommand(CONNECT);
	 pField.addActionListener(this);
	 
	 JLabel pLabel = new JLabel( "Enter Password: ");
	 pLabel.setLabelFor( pField );

	 JButton connectButton = new JButton(CONNECT);
	 connectButton.setActionCommand(CONNECT);
	 connectButton.addActionListener(this);

         cp.add( pLabel );
	 cp.add( pField );
	 cp.add( connectButton );
         cp.setLayout( new GridLayout( 0, 3 ) ); 

         //Let's check if the window is closed
         dialog.addWindowListener(this);
	 dialog.setContentPane( cp );
     }

     public void run()
     {
         while( !isDone ){
            try{
                 Thread.sleep(500);
             }catch (Exception e){}  
         }
     }

     public boolean isPassword()
     {
         return isPassword;
     }

     //Used when windows frame is disposed/closing it
     //First we close the Window and say that we have a password
     public void windowClosed( WindowEvent e )
     {
          isPassword = true;
     }

     //Second we get the password
     public String getPassword() 
     {
        return password.toString();
     }

     //Third we kill the Thread
     public void endThread()
     {
         isDone = true;
     }

     public void actionPerformed( ActionEvent e )
     {
	 String command = e.getActionCommand();
	 
	 if( CONNECT.equals(command) ){
	     password.append( pField.getPassword() );
	 }
         //dispose of this window
         dialog.dispose();
     }

     //Using when closing the frame
     public void windowClosing( WindowEvent e ){}

     public void windowDeactivated( WindowEvent e ){}

     public void windowActivated( WindowEvent e ){}

     public void windowDeiconified( WindowEvent e ){}

     public void windowIconified( WindowEvent e ){}

     public void windowOpened( WindowEvent e ){}
}
