package app.remitly;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.math.BigDecimal;
import java.math.RoundingMode;

@SuppressWarnings("serial") //Because it's annoying
public class CurrencyExchanger extends JPanel
{
	//Exchange rate for GBP/PLN from NBP API
	private static double ex_rate;

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
				
		//if not 200, connection failed - set defautl value
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
	        		//Convert it to double
	        		final double p = Double.parseDouble(text);
	        		//Check if field First triggered
	        		if (doc.equals(textFieldFirst.getDocument())) {
	        			//Calculate exchange rate and round it to two digits
	        			final double d = round(p*ex_rate,2);
	        			final String s = String.valueOf(d);
	        			//Update other text field with string converted value
	        			textFieldSecond.setText(s);
	        		}
	        		//Check if field Second triggered
	        		else if (doc.equals(textFieldSecond.getDocument()))
	        		{
	        			//Calculate exchange rate and round it to two digits
	        			final double d = round(p/ex_rate,2);
	        			final String s = String.valueOf(d);
	        			//Update other text field with string converted value
	        			textFieldFirst.setText(s);
	        		}
	        	}
	        	else //If not text, then just update everything with blank text
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
        	//Keeps digits and only single dot - almost works
        	
            String[] strArr;
            
            if (str.indexOf(".") != -1)
        	{//Has a decimal point
        		if (str.indexOf(".") == 0) { //If its first, remove it
        			str = str.replaceFirst("[.]", "");
        		}       	
        		if (str.length() > 0) {
        			//Split the string
        			strArr = str.split("[.]:",-1);
        			//Get the value before dot
        			if (strArr.length > 1)
        			{
        			str = strArr[0] + ".";
        			//Add the rest
        			for (int i = 1;i<=strArr.length;i++)
        				{
        					str = str + strArr[i];
        				}
        			}
        			else
        			{
        				str = strArr[0];
        			}
        		}
        		
        	 }
        		//Final result of str
        		str = str.replaceAll("[^\\d.]", "");
    			fb.insertString(off, str, attr); 
        }

        @Override
        public void replace(FilterBypass fb, int off, int len, String str, AttributeSet attr)
                throws BadLocationException {
        	//Keeps digits and only single dot - almost works
        	
            String[] strArr;
            
            if (str.indexOf(".") != -1)
        	{//Has a decimal point
        		if (str.indexOf(".") == 0) { //If its first, remove it
        			str = str.replaceFirst("[.]", "");

        		}       	
        		if (str.length() > 0) {
        			//Split the string
        			strArr = str.split("[.]:",-1);
        			//Get the value before dot
        			if (strArr.length > 1)
        			{
        			str = strArr[0] + ".";
        			//Add the rest
        			for (int i = 1;i<=strArr.length;i++)
        				{
        					str = str + strArr[i];
        				}
        			}
        			else
        			{
        				str = strArr[0];
        			}
        		}
        		
        	 }
        		//Final result of str
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
	    return bd.doubleValue();
	}
	
	//Method for adding labels to text fields
	private void addLabelTextRows(JLabel[] labels, JTextField[] textFields, 
			GridBagLayout gridbag, Container container) 
	{
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
			frame.setSize(400,250);
			
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



