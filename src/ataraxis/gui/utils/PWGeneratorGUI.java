/* ----------------------------------------------------------------------------
 * Copyright 2006 - 2016 Johnny Graber & Andreas Muedespacher
 * ----------------------------------------------------------------------------
 * 
 * This File is part of AtaraxiS (https://github.com/jgraber/ataraxis) and is
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

package ataraxis.gui.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
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

import ataraxis.misc.PasswordGenerator;


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
	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	private static final int SHELL_WIDTH = 242;
	private static final int SHELL_HEIGHT = 230;
	protected static final Color BACKGROUND_COLOR = new Color(Display.getDefault(), 220, 220, 255);
	
	private static Display s_display = Display.getDefault();
	private static Shell s_shell = new Shell(SWT.TITLE | SWT.CLOSE | SWT.ON_TOP);
	private static PasswordGenerator s_pg;

	private String generatedPassword;
	
	private static ResourceBundle s_translations;


	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(PWGeneratorGUI.class);

	/**
	 * Launch the application
	 *
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception 
	{
		BasicConfigurator.configure();
		Logger.getRootLogger().setLevel(Level.WARN);
		
		final PWGeneratorGUI window = new PWGeneratorGUI();
		System.out.println("Your Password is: "+window.open());
	}

	/**
	 * Constructor for stand-alone GUI
	 * @throws Exception 
	 */
	public PWGeneratorGUI() throws Exception
	{
		// create default ResourceBundle
		Properties translationProps = new Properties();
		translationProps.setProperty("PWG.Title", "Password Generator");
		translationProps.setProperty("PWG.Symbols", "Password symbols");
		translationProps.setProperty("PWG.Generate", "Generate");
		translationProps.setProperty("PWG.PWLength", "Password length:");
		translationProps.setProperty("CANCEL", "Cancel");
		translationProps.setProperty("OK", "OK");	
		translationProps.setProperty("PWG.PWGenErrorTitle", "Error");	
		translationProps.setProperty("PWG.PWGenErrorMessage", "Not enough symbols to generate a secure password.");
		translationProps.setProperty("PWG.PWtoShortErrorTitle", "Error");	
		translationProps.setProperty("PWG.PWtoShortErrorMessage", "Passwords with less than 8 chars are not secure. Procceed anyway?");		
		
		ByteArrayOutputStream outPropsStream = new ByteArrayOutputStream();
		translationProps.store(outPropsStream, "Properties for rename dialog");
		ResourceBundle renameBundle = new PropertyResourceBundle(
				(InputStream)new ByteArrayInputStream(outPropsStream.toByteArray()));
		
		
		s_translations = renameBundle;
		s_pg = new PasswordGenerator();
	}
	
	/**
	 * Constructor for stand-alone GUI that allow to set a specific 
	 * ResourceBundle.
	 *
	 * @param translations user defined language bundle
	 */
	public PWGeneratorGUI(ResourceBundle translations)
	{
	
		s_pg = new PasswordGenerator();
		s_translations = translations;
	}
		
	/**
	 * Constructor for running the PWGenerator inside an application
	 * and allow to set a specific ResourceBundle.
	 *
	 * @param shell the parent shell
	 * @param translations user defined ResourceBundle
	 */
	public PWGeneratorGUI(Shell shell, ResourceBundle translations)
	{
		s_shell = new Shell(shell);
		s_pg = new PasswordGenerator();
		s_translations = translations;
	}
	
	
	/**
	 * Open the window, return the generated password.
	 */
	public String open() 
	{
		generatedPassword = "";
		
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
		s_shell.setText("AtaraxiS - " + s_translations.getString("PWG.Title"));
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
		passwordSymbolsGroup.setText(s_translations.getString("PWG.Symbols"));

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
		lengthLabel.setText(s_translations.getString("PWG.PWLength"));

		final Spinner pwLengthSpinner = new Spinner(s_shell, SWT.BORDER);
		pwLengthSpinner.setLayoutData(new GridData());
		pwLengthSpinner.setSelection(16);
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
		cancelButton.setText(s_translations.getString("CANCEL"));
		cancelButton.setBackground(BACKGROUND_COLOR);
		cancelButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				s_shell.dispose();
			}
		});

		final Button generateButton = new Button(composite, SWT.FLAT);
		generateButton.setText(s_translations.getString("PWG.Generate"));
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
					messageBox.setText(s_translations.getString("PWG.PWGenErrorTitle"));
					messageBox.setMessage(s_translations.getString("PWG.PWGenErrorMessage"));
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
						messageBox.setText(s_translations.getString("PWG.PWtoShortErrorTitle"));
						messageBox.setMessage(s_translations.getString("PWG.PWtoShortErrorMessage"));
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
		okButton.setText(s_translations.getString("OK"));
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
}
