package app.remitly;

import javax.swing.*;


public class CurrencyExchanger
{

	public static void main(String[] args)
	   {
	      SwingUtilities.invokeLater(() ->
	         {
	        	//Create new window
	            JFrame frame = new JFrame("Currency Exchanger for Remitly");
	            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	            
	            JComponent newContentPane = new MyDocumentEvent();
	            newContentPane.setOpaque(true);
	            frame.setContentPane(newContentPane);
	            
	            //Display the window
	            frame.pack();
	            frame.setVisible(true);
	         });
	   }



}
