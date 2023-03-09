package app.remitly;

import javax.swing.*;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
@SuppressWarnings("serial") //Because it's annoying
public class CurrencyExchanger extends JPanel implements ActionListener
{
	//Default exchange rate PLN/GBP and LocalDate for fun
	private static double ex_rate = 6.0;
	private LocalDate date = LocalDate.now();
	protected static final String textFieldString = "JTextField";
	//Two text fields required in our App
	JTextField textFieldFirst = new JTextField(10);
	JTextField textFieldSecond = new JTextField(10);
	
	//Constants for Layout
	final static boolean shouldFill = false;
    final static boolean shouldWeightX = true;
    final static boolean RIGHT_TO_LEFT = false;
    
	/*public static void addComponentsToPane(Container pane)
	{
		if (RIGHT_TO_LEFT) {
			pane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
		}
		
		JTextField textFieldFirst;
		JTextField textFieldSecond;
		pane.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		if (shouldFill) {
			c.fill = GridBagConstraints.HORIZONTAL;
		}
		
		textFieldFirst = new JTextField(10);
		if (shouldWeightX) {
			c.weightx = 0.5;
		}
		//c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 0;
		c.gridy = 0;
		pane.add(textFieldFirst, c);
		
		textFieldSecond = new JTextField(10);
		//c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 1;
		pane.add(textFieldSecond, c);
		
	}*/
	
	//Constructor to get current exchange rate from NBP API
	public CurrencyExchanger()
	{
		//Create text field
		textFieldFirst.setActionCommand("First");
		textFieldFirst.addActionListener(this);
		textFieldSecond.setActionCommand("Second");
		textFieldSecond.addActionListener(this);
		
		//Create labels for text fields
		JLabel textFieldLabel1 = new JLabel("GBP: ");
		textFieldLabel1.setLabelFor(textFieldFirst);
		JLabel textFieldLabel2 = new JLabel("PLN: ");
		textFieldLabel2.setLabelFor(textFieldSecond);
		
		//Lay out for text controls and labels.
		JPanel textControlsPane = new JPanel();
        GridBagLayout gridbag = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
		
        textControlsPane.setLayout(gridbag);
        JLabel[] labels = {textFieldLabel1,textFieldLabel2};
        JTextField[] textFields = {textFieldFirst, textFieldSecond};
        addLabelTextRows(labels, textFields, gridbag, textControlsPane);
		
        c.gridwidth = GridBagConstraints.REMAINDER; //last
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        textControlsPane.setBorder(
                BorderFactory.createCompoundBorder(
                                BorderFactory.createTitledBorder("Insert value to calculate"),
                                BorderFactory.createEmptyBorder(5,5,5,5)));
        
        JPanel leftPane = new JPanel(new BorderLayout());
        leftPane.add(textControlsPane,BorderLayout.PAGE_START);
        add(leftPane, BorderLayout.LINE_START);
        
        
        
        
        
		//Connect to NBP API for exchange rates
		try {
		//Make connection
		URL url = new URL("http://api.nbp.pl/api/exchangerates/rates/a/gbp/");
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.connect();
		
		//Get response code
		int responseCode = conn.getResponseCode();
		
		//if not 200, connection failed
		if (responseCode != 200) {
			throw new RuntimeException("HttpResponseCode :" + responseCode);
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
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
	
	
	//Method to draw simple text next to TextPanels (simplest solution)
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
	
	
	//Rounding method so we get nice values 
	public static double round(double value, int places) {
	    if (places < 0) throw new IllegalArgumentException();

	    BigDecimal result = BigDecimal.valueOf(value);
	    result = result.setScale(places, RoundingMode.HALF_UP);
	    return result.doubleValue();
	}
	
	
	@Override
	/* Action Listener method to monitor text fields and return modified value
	to another field
	*/
	public void actionPerformed(ActionEvent e) {
		double tmp = 0.0;
		String result;
		JTextField source = (JTextField)e.getSource();
		System.out.println(source.getText());
		if (e.getActionCommand().equals("First"))
		{
			tmp = round(Double.parseDouble(source.getText())*ex_rate,2);
			result = String.valueOf(tmp);
			setFieldSecond(result);
		}
		else if (e.getActionCommand().equals("Second"))
		{
			tmp = round(Double.parseDouble(source.getText())/ex_rate,2);
			result = String.valueOf(tmp);
			setFieldFirst(result);
		}
		
	}
	
	//Text field methods are duplicated but are simplest and work nicely
	//There is probably	a better way to implement this solution
	
	//textFieldFirst modify method
	public void setFieldFirst(String s)
	{
		textFieldFirst.setText(s);
	}
	//textFieldSecond modify method
	public void setFieldSecond(String s)
	{
		textFieldSecond.setText(s);
	}
	
	//Main method
	public static void main(String[] args) 
	   {
			//Build a frame
			JFrame frame = new JFrame("Currency Exchanger");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.getContentPane().add(new CurrencyExchanger());
			//addComponentsToPane(frame.getContentPane());
			
			//Set frame size
			frame.setSize(400,250);
			
			
			//Set starting position of the frame
			Toolkit kit = Toolkit.getDefaultToolkit();
			Dimension screenSize = kit.getScreenSize();
			int sW = screenSize.width;
			int sH = screenSize.height;
			
			//Position frame in the middle
			frame.setLocation(sW/2-150,sH/2-150);
			
			//textFieldSecond.setPreferredSize(new Dimension(100,20));
			//frame.add(textFieldSecond);
			
			//Display the frame
			frame.setVisible(true);
			
			//Can't change the size
			//frame.setResizable(true);
			
			
			
	   }



}



