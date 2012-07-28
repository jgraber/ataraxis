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

package ch.ethz.origo.ataraxis.gui.utils;

import java.util.ResourceBundle;

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

/**
 * TextInputDialog prompt the user to insert a string.
 * 
 * @author J. Graber
 * @version 1.2
 */
public class TextInputDialog {
	
	private static final Logger LOGGER = Logger.getLogger(TextInputDialog.class);

	// GUI Basic components
	private static Display display = Display.getDefault();
	private static Shell s_shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.ON_TOP);
	private int s_width = 374;
	private int s_height = 73;
	
	private static ResourceBundle s_translations;	
	private String returnString;	
	private String displayText = "";
	
	
	/**
	 * Constructor with ResourceBundle to open TextInputDialog in a new Shell
	 * 
	 * @param translations the translation for the GUI
	 */
	public TextInputDialog(ResourceBundle translations)
	{
		// empty constructor
		this(new Shell(SWT.TITLE | SWT.CLOSE | SWT.ON_TOP), translations);
	}

	/**
	 * Open TextInputDialog as child of parentShell with a
	 * ResourceBundle for the translations.
	 * 
	 * @param parentShell the parent Shell
	 * @param translations the translations for the GUI
	 */
	public TextInputDialog(Shell parentShell, ResourceBundle translations)
	{
		s_shell = new Shell(parentShell);
		s_translations = translations;
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
		returnString = null;
		
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 4;

		Rectangle displayRect = display.getActiveShell().getBounds();
        
		int s_x = displayRect.x + (displayRect.width - s_width) / 2;
        int s_y = displayRect.y + (displayRect.height - s_height) / 2;

        s_shell.setLocation(s_x, s_y);
        
		// Layout the Shell
		s_shell.setLayout(gridLayout);
		s_shell.setSize(s_width, s_height);
		s_shell.setText(s_translations.getString("PWM.TID_Title"));
		s_shell.open();

		// make all the components
		final Label groupLabel = new Label(s_shell, SWT.NONE);
		groupLabel.setLayoutData(new GridData(SWT.DEFAULT, 19));
		groupLabel.setText(s_translations.getString("PWM.TID_labelText")+":");

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
		cancelButton.setText(s_translations.getString("CANCEL"));

		final Button createButton = new Button(s_shell, SWT.NONE);
		createButton.addSelectionListener(new SelectionAdapter() 
		{
			public void widgetSelected(SelectionEvent arg0) 
			{
				// if user had no text set, warn him
				if(textField.getText().equals(""))
				{
					MessageBox mb = new MessageBox(s_shell, SWT.OK | SWT.ICON_WARNING);
					mb.setText(s_translations.getString("PWM.TID_warnText"));
					mb.setMessage(s_translations.getString("PWM.TID_warnMessage"));
					mb.open();
				}
				else 
				{
					returnString = textField.getText();
					s_shell.dispose();
				}
			}
		});
		createButton.setText(s_translations.getString("PWM.TID_OkButton"));
		s_shell.addListener (SWT.Traverse, new Listener () 
		{
			public void handleEvent (Event event) 
			{
					if(event.detail == SWT.TRAVERSE_ESCAPE)
						s_shell.dispose();
			}
		});
		s_shell.setDefaultButton(createButton);
		s_shell.layout();

		// wait until the user quit or accept 
		while (!s_shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		LOGGER.debug("Display of TextInputDialog closed");
		return returnString;
	}
}