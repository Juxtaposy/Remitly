package app.remitly;
import java.awt.*;
import javax.swing.*;
/**This is a class for main window app configuration using awt and swing.
 * 
 * @author MG_AGH
 *
 */


class SimpleFrame extends JFrame
{
	
	public SimpleFrame()
	{
		Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenWidth = screenSize.width;
        int screenHeight = screenSize.height;
		setSize(screenWidth/3,screenHeight/3);
		setLocation(screenWidth/3,screenHeight/3);
	}

}