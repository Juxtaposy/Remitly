package app.remitly;

import javax.swing.*;
import java.awt.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.Scanner;

@SuppressWarnings("serial") //Because it's annoying
public class CurrencyExchanger extends JPanel
{
	//Default exchange rate PLN/GBP and LocalDate for fun
	private static double ex_rate = 6.0;
	private LocalDate date = LocalDate.now();
	
	//Constructor to get current exchange rate from NBP API
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
			//Because I know exactly how the string looks like, I get what by
			//avoiding addition of custom libraries to handle JSON format 

			//Update exchange rate for the day
			ex_rate = Double.parseDouble(informationString.substring(121,127));
		}
		
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	//Method to draw simple text next to TextPanels (simplest solution)
	public void paint(Graphics g) {
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
	}
	
	//Main method
	public static void main(String[] args) 
	   {
			//Build a frame
			JFrame frame = new JFrame("Currency Exchanger");
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
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			//Can't change the size
			frame.setResizable(false);
			
			
			
	   }
	
	
	

}



