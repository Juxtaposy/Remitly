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

        final DocumentListener docListener = new DocumentListener() {
        
        	private Document doc;
        	
	        public void insertUpdate(DocumentEvent e) {
	            updateLog(e);
	        }
	        public void removeUpdate(DocumentEvent e) {
	            updateLog(e);
	        }
	        public void changedUpdate(DocumentEvent e) {
	            //updateLog(e);
	        }
	
	        private void updateLog(DocumentEvent e) {
	        	if (null == doc) {
	        		doc = e.getDocument();
	        		String text = "";
	        	
	        		try {
	        			text = doc.getText(0, doc.getLength());
	        		}		
	        		catch (final Exception ex) {
	        		ex.printStackTrace();
	        		}
	        	
	        	if (!text.isEmpty()) {
	        		final double p = Double.parseDouble(text);
	        		
	        		if (doc.equals(textFieldFirst.getDocument())) {
	        			final double d = round(p*ex_rate,2);
	        			final String s = String.valueOf(d);
	        			textFieldSecond.setText(s);
	        		}
	        		
	        		else if (doc.equals(textFieldSecond.getDocument()))
	        		{
	        			final double d = round(p/ex_rate,2);
	        			final String s = String.valueOf(d);
	        			textFieldFirst.setText(s);
	        		}
	        	}
	        	else
	        	{
	        		textFieldFirst.setText(text);
	        		textFieldSecond.setText(text);
	        	}
	        	doc = null;
	        	}
        	
	        }
        
        };
	
	
	textFieldFirst.getDocument().addDocumentListener(docListener);
	textFieldSecond.getDocument().addDocumentListener(docListener);

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
    ((AbstractDocument) textFieldFirst.getDocument()).setDocumentFilter(docFilter);
    ((AbstractDocument) textFieldSecond.getDocument()).setDocumentFilter(docFilter);

}	
	//Method to draw simple text next to TextPanels (old)
	/*public void paint(Graphics g) {
		//Get images of flags
		Image image_brit = Toolkit.getDefaultToolkit().getImage("british.jpg");
		Image image_pol = Toolkit.getDefaultToolkit().getImage("polish.jpg");
		
		//Draw images with positions
		g.drawImage(image_brit,30,30,this);
		g.drawImage(image_pol,30,80,this);
		//Draw some basic Strings
		g.drawString("Exchange rate PLN/GBP: ", 30, 180);
		g.drawString("as of " + date.toString(),30,200);
		//Bold font for exchange rate
		g.setFont(new Font("default", Font.BOLD, 16));
		g.drawString(String.valueOf(ex_rate), 200, 180);
		
	}*/	
	
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



