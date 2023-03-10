package app.remitly;
/**
 * Currency Exchanger App for calculating British Pound and Polish Złoty
 * exchange rates. User can input integer values into either text field
 * to instantly obtain recalculated value in other currency.
 * 
 * Rates are directly imported from NBP API (api.nbp.pl), if it is not 
 * possible, constant rate of 6.0 is set. 
 * 
 * @author Mateusz Gawroński
 */

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.math.BigDecimal;
import java.math.RoundingMode;

@SuppressWarnings("serial") //Because it's annoying
public class CurrencyExchanger extends JPanel
{
	//Exchange rate for GBP/PLN from NBP API
	private static double ex_rate = 6.0;

	//Constants for Layout
	final static boolean shouldFill = false;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;
    
	
	//Constructor
	public CurrencyExchanger()
	{
		//Connect to NBP API for exchange rates
		try {
			//Make connection
			URL url = new URL("http://api.nbp.pl/api/exchangerates/rates/a/gbp/");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();
				
			//Get response code
			int responseCode = conn.getResponseCode();
				
			//if not 200, connection failed - set default value
				if (responseCode != 200) {
					ex_rate = 6.0;
				}
				//if 200, lets do a very cumbersome analysis of the data
				else
				{
					//Build a new StringBuilder to save entire response
					StringBuilder informationString = new StringBuilder();
					Scanner scanner = new Scanner(url.openStream());
					
					//Add information to our String
					while (scanner.hasNext()) {
						informationString.append(scanner.nextLine());
					}
					//Close the file
					scanner.close();
					//Because I know exactly how the string looks like, I get what I want
					//avoiding addition of custom libraries to handle JSON format 

					//Update exchange rate for the day
					ex_rate = Double.parseDouble(informationString.substring(121,127));
				}
		}
		
		// Set default value for exchange rate if something goes wrong
		catch (Exception e) {
			ex_rate = 6.0;
		}
		
		//Add two text fields for user to input values in
		final JTextField textFieldFirst = new JTextField(10);
		final JTextField textFieldSecond = new JTextField(10);
				
		//Create labels for text fields
		JLabel textFieldLabel1 = new JLabel("GBP: ");
		textFieldLabel1.setLabelFor(textFieldFirst);
		JLabel textFieldLabel2 = new JLabel("PLN: ");
		textFieldLabel2.setLabelFor(textFieldSecond);
		
		//Lay out for text controls and labels.
		JPanel textControlsPane = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
		
        //Set-up for layout construction - from oracle tutorial
        textControlsPane.setLayout(gridbag);
        JLabel[] labels = {textFieldLabel1,textFieldLabel2};
        JTextField[] textFields = {textFieldFirst, textFieldSecond};
        addLabelTextRows(labels, textFields, gridbag, textControlsPane);
		
        //GridBagLayout handling
        c.gridwidth = GridBagConstraints.REMAINDER; 
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        textControlsPane.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("GBP/PLN:       " + ex_rate),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
        //Add to panel
        JPanel Pane = new JPanel(new BorderLayout());
        Pane.add(textControlsPane,BorderLayout.PAGE_START);
        add(Pane, BorderLayout.LINE_START);	
        
        //Document listener declaration
        final DocumentListener docListener = new DocumentListener() {
        	//Field for later
        	private Document doc;
        	
        	//Required methods, updateLog is most critical
	        public void insertUpdate(DocumentEvent e) {
	            updateLog(e);
	        }
	        public void removeUpdate(DocumentEvent e) {
	            updateLog(e);
	        }

	        public void changedUpdate(DocumentEvent e) {
	            updateLog(e);
	        }
	        
	        //Update function
	        private void updateLog(DocumentEvent e) {
	        	//Set up Document doc variable
	        	if (null == doc) {
	        		doc = e.getDocument();
	        		String text = "";
	        		//If we can get text
	        		try {
	        			text = doc.getText(0, doc.getLength());
	        		}//If we can't
	        		catch (final Exception ex) {
	        		ex.printStackTrace();
	        		}
	        	//If we have some text typed then
	        	if (!text.isEmpty()) {
	        		//Check for extra digits
	        		String[] strArr;
	                if (text.indexOf(".") != -1)
	            	{//Has a decimal point somewhere. If its first in string, remove it
	            		if (text.indexOf(".") == 0) { 
	            			text = text.replaceFirst("[.]", "");
	            		}//Lets continue if text still has some characters in it      	
	            		if (text.length() > 0) {
	            			//Split the string with '.' delimiter
	            			strArr = text.split("[.]",-1);
	            			//If there were multiple delimiters then build string
	            			if (strArr.length >= 2)
	            			{
	            				//Get first part and add delimiter
	            				text = strArr[0] + ".";
	            			//Add the rest of the string array
	            			for (int i = 1;i<strArr.length;i++)
	            				{
	            				text = text + strArr[i];
	            				}
	            			}
	            			//If only one String in array, just use it as final variable
	            			else
	            			{
	            				text = strArr[0];
	            			}
	            		}
	            		//If text is empty, then set it to "0" to avoid errors
	            		else
	            		{
	            			text = "0";
	            		}
	            		
	            	 }
	        		//Convert String to double
	        		final double p = Double.parseDouble(text);
	        		//Check if field First triggered event
	        		if (doc.equals(textFieldFirst.getDocument())) {
	        			//Calculate exchange rate and round it to two digits after delimiter
	        			final double d = round(p*ex_rate,2);
	        			final String s = String.valueOf(d);
	        			//Update other text field with string converted value
	        			textFieldSecond.setText(s);
	        		}
	        		//Check if field Second triggered event
	        		else if (doc.equals(textFieldSecond.getDocument()))
	        		{
	        			//Calculate exchange rate and round it to two digits after delimiter
	        			final double d = round(p/ex_rate,2);
	        			final String s = String.valueOf(d);
	        			//Update other text field with string converted value
	        			textFieldFirst.setText(s);
	        		}
	        	}
	        	else //If there is no text, then just update everything with blank text
	        	{
	        		textFieldFirst.setText(text);
	        		textFieldSecond.setText(text);
	        	}
	        	//Nullify our doc for proper event handling
	        	doc = null;
	        	}
        	
	        }
        
        };
	
	//Add listeners to fields so we can actually monitor changes
	textFieldFirst.getDocument().addDocumentListener(docListener);
	textFieldSecond.getDocument().addDocumentListener(docListener);
	
	//New Document filter to allow only digits and '.' - not working 100%
	final DocumentFilter docFilter = new DocumentFilter(){
        @Override
        public void insertString(FilterBypass fb, int off, String str, AttributeSet attr)
                throws BadLocationException {
        	//Keeps digits and dots only and calls insertion
        		str = str.replaceAll("[^\\d.]", "");
    			fb.insertString(off, str, attr); 
        }

        @Override
        public void replace(FilterBypass fb, int off, int len, String str, AttributeSet attr)
                throws BadLocationException {
        	//Keeps digits and dots only and calls replacement
        		str = str.replaceAll("[^\\d.]", "");
                fb.replace(off, len,  str, attr); 

        }
    };
    //Setting document filter for both fields
    ((AbstractDocument) textFieldFirst.getDocument()).setDocumentFilter(docFilter);
    ((AbstractDocument) textFieldSecond.getDocument()).setDocumentFilter(docFilter);

}	
	
	//Round method for nicer values
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();
	    
	    //BigDecimal is safer
	    BigDecimal bd = BigDecimal.valueOf(value);
	    bd = bd.setScale(places, RoundingMode.HALF_UP);
	    //Return double
	    return bd.doubleValue();
	}
	
	//Method for adding labels to text fields
	private void addLabelTextRows(JLabel[] labels, JTextField[] textFields, 
			GridBagLayout gridbag, Container container) 
	{
			//Some layout magic
			GridBagConstraints c = new GridBagConstraints();
			c.anchor = GridBagConstraints.EAST;
			int numLabels = labels.length;

			for (int i = 0; i < numLabels; i++) {
				c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
				c.fill = GridBagConstraints.NONE;      //reset to default
				c.weightx = 0.0;                       //reset to default
				container.add(labels[i], c);

				c.gridwidth = GridBagConstraints.REMAINDER;     //end row
				c.fill = GridBagConstraints.HORIZONTAL;
				c.weightx = 1.0;
				container.add(textFields[i], c);
				}
	}
	
	//Main method
	public static void main(String[] args) 
	   {
			//Build a frame
			JFrame frame = new JFrame("Currency Exchanger");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(new CurrencyExchanger());
			
			//Set frame size
			frame.setSize(250,150);
			
			//Set starting position of the frame
			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension screenSize = kit.getScreenSize();
			int sW = screenSize.width;
			int sH = screenSize.height;
			
			//Position frame in the middle
			frame.setLocation(sW/2-150,sH/2-150);

			//Display the frame
			frame.setVisible(true);	
			
	   }

}



