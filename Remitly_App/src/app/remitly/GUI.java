package app.remitly;
import java.awt.*;
import javax.swing.*;

/** This is a class for main GUI window set-up and probably
 * will work as main class for starting the App for currency exchanger
 * @author MG_AGH
 *
 */


public class GUI {

	public static void main(String[] args)
	   {
	      EventQueue.invokeLater(() ->
	         {
	            var frame = new SimpleFrame();
	            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	            frame.setVisible(true);
	         });
	   }


}