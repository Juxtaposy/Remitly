package app.remitly;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.GridBagConstraints;
import java.awt.event.*;
import javax.swing.ImageIcon;

@SuppressWarnings("serial")
public class MyDocumentEvent extends JPanel implements ActionListener
{

	JTextField textFieldFirst;
	JTextField textFieldSecond;
	
	public MyDocumentEvent()
	{


        textFieldFirst = new JTextField(20);
        textFieldFirst.getDocument().addDocumentListener(new MyDocumentListener());
        textFieldFirst.getDocument().putProperty("name", "Text Field First");
        
        textFieldSecond = new JTextField(20);
        //textFieldSecond.getDocument().addDocumentListener(new MyDocumentListener());
        textFieldSecond.getDocument().putProperty("name", "Text Field Second");
        
        //Create some labels for textfields
        JLabel label1 = new JLabel("British pound: ");
        JLabel label2 = new JLabel("Polish złoty: ");
        label1.setLabelFor(textFieldFirst);
        label2.setLabelFor(textFieldSecond);
        add(textFieldFirst);
        add(textFieldSecond);
        
        
        setPreferredSize(new Dimension(300, 200));
        setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
	}
	
	class MyDocumentListener implements DocumentListener {

        public void insertUpdate(DocumentEvent e) 
        {
            updateLog(e);
        }
        public void removeUpdate(DocumentEvent e) 
        {
            updateLog(e);
        }
        public void changedUpdate(DocumentEvent e) 
        {
            //Plain text components don't fire these events.
        }

        public void updateLog(DocumentEvent e) 
        {
            
        }

    }

	
	public void actionPerformed(ActionEvent e) {
		 
	}

}
