/* ----------------------------------------------------------------------------
 * Copyright 2006 - 2010 Johnny Graber & Andreas Muedespacher
 * ----------------------------------------------------------------------------
 * 
 * This File is part of AtaraxiS (http://ataraxis.origo.ethz.ch/) and is
 * licensed under the European Public License, Version 1.1 only (the "Licence").
 * You may not use this work except in compliance with the Licence. 
 * 
 * You may obtain a copy of the Licence at: 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the Licence is distributed on an "AS IS" basis, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * 
 * See the Licence for the specific language governing permissions and 
 * limitations under the Licence. 
 */

package ch.ethz.origo.ataraxis.gui;

import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TextInputDialog {
	
	private static final Logger LOGGER = Logger.getLogger(TextInputDialog.class);

	// GUI Basic components
	private static Display display = Display.getDefault();
	private static Shell s_shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.ON_TOP);
	
	private static Properties s_langProps;

	// all Strings needed by the dialog
	private String returnString;
	private static String s_shellTitle;
	private static String s_OkButton;
	private static String s_CancelButton;
	private static String s_labelText;
	private static String s_warnText;
	private static String s_warnMessage;
	
	private String displayText = "";
	
	private int s_width = 374;
	private int s_height = 73;
	
	/**
	 * Empty Constructor, open TextInputDialog in a new Shell
	 *
	 */
	public TextInputDialog ()
	{
		// empty constructor
		this(new Shell(SWT.TITLE | SWT.CLOSE | SWT.ON_TOP));
	}

	/**
	 * Open TextInputDialog as child of parentShell
	 * 
	 * @param parentShell the parent Shell
	 */
	public TextInputDialog(Shell parentShell)
	{
		s_shell = new Shell(parentShell);
	}

	/**
	 * Open TextInputDialog as child of parentShell with language Properties
	 * 
	 * @param parentShell the parent Shell
	 * @param languageProperties the language porperties
	 */
	public TextInputDialog(Shell parentShell, Properties languageProperties)
	{
		s_shell = new Shell(parentShell);
		s_langProps = languageProperties;
	}

	
	
	/**
	 * Get current width of Dialog
	 * @return width
	 */
	public int getWidth() 
	{
		return s_width;
	}

	/**
	 * Set width of Dialog
	 * @param width
	 */
	public void setWidth(int width) 
	{
		this.s_width = width;
	}

	/**
	 * Get current height of Dialog
	 * @return height
	 */
	public int getHeight() 
	{
		return s_height;
	}

	/**
	 * Set height of Dialog
	 * @param height
	 */
	public void setHeight(int height) 
	{
		this.s_height = height;
	}
	
	
	/**
	 * Set a default Text to display
	 * 
	 * @param displayText
	 */
	public void setDisplayText(String displayText) 
	{
		this.displayText = displayText;
	}
	

	/**
	 * Open the window and return the text (or null)
	 *
	 * @return the text of the user OR null
	 */
	public String open() 
	{
		LOGGER.debug("Open Display of TextInputDialog");
		loadProperties();
		returnString = null;
		//final Display display = Display.getDefault();
		//final Shell shell = new Shell();
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;

		Rectangle displayRect = display.getActiveShell().getBounds();
        //s_width = displayRect.width / 3 * 2;
        //s_height = displayRect.height / 3 * 2;
        int s_x = displayRect.x + (displayRect.width - s_width) / 2;
        int s_y = displayRect.y + (displayRect.height - s_height) / 2;

         s_shell.setLocation(s_x, s_y);
        
		// Layout the Shell
		s_shell.setLayout(gridLayout);
		s_shell.setSize(s_width, s_height);
		s_shell.setText(s_shellTitle);
		s_shell.open();

		// make all the components
		final Label groupLabel = new Label(s_shell, SWT.NONE);
		groupLabel.setLayoutData(new GridData(SWT.DEFAULT, 19));
		groupLabel.setText(s_labelText+":");

		final Text textField = new Text(s_shell, SWT.BORDER);
		textField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		if(displayText != null && displayText.length() > 0)
		{
			textField.setText(displayText);
		}
		textField.setFocus();
		textField.setSelection(textField.getText().length());
		
		final Button cancelButton = new Button(s_shell, SWT.NONE);
		cancelButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent arg0) 
			{
				s_shell.dispose();
			}
		});
		cancelButton.setText(s_CancelButton);

		final Button createButton = new Button(s_shell, SWT.NONE);
		createButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent arg0) 
			{
				// if user had no text set, warn him
				if(textField.getText().equals(""))
				{
					MessageBox mb = new MessageBox(s_shell, SWT.OK | SWT.ICON_WARNING);
					mb.setText(s_warnText);
					mb.setMessage(s_warnMessage);
					mb.open();
				}
				else 
				{
					returnString = textField.getText();
					s_shell.dispose();
				}
			}
		});
		createButton.setText(s_OkButton);
		s_shell.addListener (SWT.Traverse, new Listener () 
		{
			public void handleEvent (Event event) 
			{
					if(event.detail == SWT.TRAVERSE_ESCAPE)
						s_shell.dispose();
			}
		});
		s_shell.setDefaultButton(createButton);
		// wait until the user quit or accept 
		s_shell.layout();
		while (!s_shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		LOGGER.debug("Display of TextInputDialog closed");
		return returnString;
	}
	
	/**
	 * Load the language properties.
	 */
	private void loadProperties() 
	{

		// Load the language file
		
		if(s_langProps == null)
		{
			s_langProps = new Properties();
			LOGGER.debug("Language: default values are used");
		}

		// set the strings
		s_shellTitle = s_langProps.getProperty("PWM.TID_Title", "TextInput Dialog");
		s_OkButton  = s_langProps.getProperty("PWM.TID_OkButton", "OK");
		s_CancelButton  = s_langProps.getProperty("CANCEL", "CANCEL");
		s_labelText  = s_langProps.getProperty("PWM.TID_labelText", "Text:");
		s_warnText  = s_langProps.getProperty("PWM.TID_warnText", "Warning");
		s_warnMessage  = s_langProps.getProperty("PWM.TID_warnMessage", "Text can not be empty!");	
	} 
}
