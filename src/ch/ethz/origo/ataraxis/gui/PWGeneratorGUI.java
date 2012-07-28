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

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import ch.ethz.origo.ataraxis.misc.PasswordGenerator;

/**
 * PWGeneratorGUI is a simple and easy to use GUI to the PasswordGenerator-Class
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 *
 */
public class PWGeneratorGUI 
{

	// vars for the basic gui
	private static Properties langProps = null;
	
	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	private static final int SHELL_WIDTH = 242;
	private static final int SHELL_HEIGHT = 210;
	protected static final Color BACKGROUND_COLOR = new Color(Display.getDefault(), 220, 220, 255);
	
	
	private static Display s_display = Display.getDefault();
	private static Shell s_shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.ON_TOP);
	private static PasswordGenerator s_pg;

	private String generatedPassword;
	
	// i18n strings
	private static String s_PWGeneratorTitle;
	private static String s_PWSymbols;
	private static String s_generateButton;
	private static String s_PWLength;
	private static String s_Cancel;
	private static String s_OK;
	private static String s_PWGenErrorTitle;
	private static String s_PWGenErrorMessage;
	private static String s_PWtoShortErrorTitle;
	private static String s_PWtoShortErrorMessage;
	


	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(PWGeneratorGUI.class);

	/**
	 * Launch the application
	 *
	 * @param args
	 */
	public static void main(String[] args) 
	{
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.WARN);
		
		final PWGeneratorGUI window = new PWGeneratorGUI();
		System.out.println("Your Password is: "+window.open());
	}

	/**
	 * Constructor for stand-alone GUI
	 */
	public PWGeneratorGUI()
	{
		s_pg = new PasswordGenerator();
	}

	
	/**
	 * Constructor for stand-alone GUI that allow to set a specific 
	 * language-properties.
	 *
	 * @param languageProperties user defined language-properties
	 */
	public PWGeneratorGUI(Properties languageProperties)
	{
	
		s_pg = new PasswordGenerator();
		langProps = languageProperties;
	}
	
	/**
	 * Constructor for running the PWGenerator inside an application.
	 *
	 * @param shell the parent sthell
	 */
	public PWGeneratorGUI(Shell shell)
	{
		s_shell = new Shell(shell);
		s_pg = new PasswordGenerator();
	}

	
	/**
	 * Constructor for running the PWGenerator inside an application
	 * and allow to set a specific language-properties.
	 *
	 * @param shell the parent sthell
	 * @param languageProperties user defined language-properties
	 */
	public PWGeneratorGUI(Shell shell, Properties languageProperties)
	{
		s_shell = new Shell(shell);
		s_pg = new PasswordGenerator();
		langProps = languageProperties;
	}
	
	
	/**
	 * Open the window, return the generated password.
	 */
	public String open() 
	{
		loadProperties();
		generatedPassword = "";
		//final Display display = Display.getCurrent();//.getDefault();
		//final Shell s_shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.ON_TOP);
		final Image icon = new Image(s_display, APPL_DATA_DIR + "/icons/Tray_Ataraxis.png");
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		
		Rectangle displayRect = s_display.getActiveShell().getBounds();
		int posX = displayRect.x + (displayRect.width - SHELL_WIDTH) / 2;
		int posY = displayRect.y + (displayRect.height - SHELL_HEIGHT) / 2;

		s_shell.setLocation(posX, posY);
		s_shell.setImage(icon);
		s_shell.setLayout(gridLayout);
		s_shell.setSize(SHELL_WIDTH, SHELL_HEIGHT);
		s_shell.setText("AtaraxiS - " + s_PWGeneratorTitle);
		s_shell.setBackground(BACKGROUND_COLOR);
		s_shell.setBackgroundMode(SWT.INHERIT_FORCE);
		s_shell.addListener (SWT.Traverse, new Listener () {
			public void handleEvent (Event event) {
				if (event.detail == SWT.TRAVERSE_ESCAPE)
					s_shell.dispose();
			}
		});

		GridData gdGroup = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gdGroup.widthHint = 215;

		final Group passwordSymbolsGroup = new Group(s_shell, SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 2;
		passwordSymbolsGroup.setLayout(gridLayout_1);
		passwordSymbolsGroup.setLayoutData(gdGroup);
		passwordSymbolsGroup.setText(s_PWSymbols);

		final Button aZButton = new Button(passwordSymbolsGroup, SWT.CHECK);
		aZButton.setLayoutData(new GridData(94, SWT.DEFAULT));
		aZButton.setText("A - Z");
		aZButton.setSelection(true);

		final Button nrButton = new Button(passwordSymbolsGroup, SWT.CHECK);
		nrButton.setLayoutData(new GridData());
		nrButton.setText("0 - 9");
		nrButton.setSelection(true);

		final Button azButton = new Button(passwordSymbolsGroup, SWT.CHECK);
		azButton.setText("a - z");
		azButton.setSelection(true);

		final Button specialButton = new Button(passwordSymbolsGroup, SWT.CHECK);
		specialButton.setText(".:,*+/()=@");
		specialButton.setSelection(true);
		specialButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
			}
		});

		final Label lengthLabel = new Label(s_shell, SWT.NONE);
		lengthLabel.setLayoutData(new GridData());
		lengthLabel.setText(s_PWLength);

		final Spinner pwLengthSpinner = new Spinner(s_shell, SWT.BORDER);
		pwLengthSpinner.setLayoutData(new GridData());
		pwLengthSpinner.setSelection(8);
		pwLengthSpinner.setMaximum(2048);
		pwLengthSpinner.setMinimum(4);

		final Text passwordField = new Text(s_shell, SWT.BORDER);
		GridData gdText = new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1);

		gdText.widthHint = 130;
		passwordField.setLayoutData(gdText);

		final Composite composite = new Composite(s_shell, SWT.NONE);
		composite.setLayout(new FillLayout());
		final GridData gridData = new GridData(SWT.FILL, SWT.BOTTOM, false, true, 2, 1);
		composite.setLayoutData(gridData);

		final Button cancelButton = new Button(composite, SWT.FLAT);
		cancelButton.setText(s_Cancel);
		cancelButton.setBackground(BACKGROUND_COLOR);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				s_shell.dispose();
			}
		});

		final Button generateButton = new Button(composite, SWT.FLAT);
		generateButton.setText(s_generateButton);
		generateButton.setBackground(BACKGROUND_COLOR);
		generateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {

				if( (pwLengthSpinner.getSelection() == 0) 
						|| ( !azButton.getSelection() 
								&& !aZButton.getSelection()
								&& !specialButton.getSelection()
								&& !nrButton.getSelection()) )
				{
					LOGGER.error("Not enugh Data to generate Password");
					MessageBox messageBox = new MessageBox(s_shell,
							SWT.ICON_ERROR | SWT.OK);
					messageBox.setText(s_PWGenErrorTitle);
					messageBox.setMessage(s_PWGenErrorMessage);
					messageBox.open();
				}
				else 
				{
					boolean ignoreToShort = false;
					if(pwLengthSpinner.getSelection() < 8)
					{
						LOGGER.debug("PW size to short");
						MessageBox messageBox = new MessageBox(s_shell,
								SWT.ICON_WARNING | SWT.NO| SWT.YES);
						messageBox.setText(s_PWtoShortErrorTitle);
						messageBox.setMessage(s_PWtoShortErrorMessage);
						if(messageBox.open() == SWT.YES)
						{
							ignoreToShort = true;
						}
					}

					if((pwLengthSpinner.getSelection() >= 8) ||  ignoreToShort)
					{
						LOGGER.debug("generate Password");
						s_pg.setInclude_AZ(aZButton.getSelection());
						s_pg.setInclude_az(azButton.getSelection());
						s_pg.setInclude_09(nrButton.getSelection());
						s_pg.setInclude_Special(specialButton.getSelection());

						passwordField.setText(
							s_pg.generatePW(pwLengthSpinner.getSelection()));
					}
				}				
			}
		});

		final Button okButton = new Button(composite, SWT.FLAT);
		okButton.setText(s_OK);
		okButton.setBackground(BACKGROUND_COLOR);
		okButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				generatedPassword = passwordField.getText();
				s_shell.dispose();
			}
		});

		s_shell.setDefaultButton(generateButton);
		s_shell.addListener (SWT.Traverse, new Listener () {
			public void handleEvent (Event event) 
			{
				if(event.detail == SWT.TRAVERSE_ESCAPE)
					s_shell.dispose();
			}
		});

		s_shell.layout();
		s_shell.open();
		LOGGER.debug("PasswordGenerator shell is open");
		while (!s_shell.isDisposed()) 
		{
			if (!s_display.readAndDispatch())
				s_display.sleep();
		}

		LOGGER.debug("PasswordGenerator shell is closed");
		return generatedPassword;
	}

	/**
	 * method to load the content of the properties file (for i18n).
	 */
	private static void loadProperties() 
	{

		// Use default values if no Properties object received
		if(langProps == null){
			langProps = new Properties();
			LOGGER.debug("Language: default values are used");
		}

		// set the strings
		s_PWGeneratorTitle = langProps.getProperty("PWG.Title", "Password Generator");
		s_PWSymbols  = langProps.getProperty("PWG.Symbols", "Password symbols");
		s_generateButton  = langProps.getProperty("PWG.Generate", "Generate");
		s_PWLength  = langProps.getProperty("PWG.PWLength", "Password length:");
		s_Cancel  = langProps.getProperty("CANCEL", "Cancel");
		s_OK  = langProps.getProperty("OK", "OK");	
		s_PWGenErrorTitle = langProps.getProperty("PWG.PWGenErrorTitle", "Error");	
		s_PWGenErrorMessage = langProps.getProperty("PWG.PWGenErrorMessage", "Not enough symbols to generate a secure password.");
		s_PWtoShortErrorTitle = langProps.getProperty("PWG.PWtoShortErrorTitle", "Error");	
		s_PWtoShortErrorMessage = langProps.getProperty("PWG.PWtoShortErrorMessage", "Passwords with less than 8 chars are not secure. Procceed anyway?");		
	}
}
