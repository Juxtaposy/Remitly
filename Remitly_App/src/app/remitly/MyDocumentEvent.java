package app.remitly;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.event.*;

import java.awt.Dimension;

import java.awt.event.*;

@SuppressWarnings("serial")
public class MyDocumentEvent extends JPanel implements ActionListener
{

	JTextField textFieldFirst;
	JTextField textFieldSecond;
	JTextArea displayArea;
	
	public MyDocumentEvent()
	{
        
        textFieldFirst = new JTextField(20);
        textFieldFirst.getDocument().addDocumentListener(new MyDocumentListener());
        textFieldFirst.getDocument().putProperty("name", "Text Field First");
        
        textFieldSecond = new JTextField(20);
        //textFieldSecond.getDocument().addDocumentListener(new MyDocumentListener());
        textFieldSecond.getDocument().putProperty("name", "Text Field Second");
        
        
        add(textFieldFirst);
        add(textFieldSecond);

        setPreferredSize(new Dimension(450, 250));
        setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
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
        	textFieldSecond.setText("Test");
            
        }

    }

	
	public void actionPerformed(ActionEvent e) {
	     textFieldFirst.requestFocus();
	     textFieldSecond.requestFocus();
		 System.out.println("Action oerformed funcion:");
	}

}
