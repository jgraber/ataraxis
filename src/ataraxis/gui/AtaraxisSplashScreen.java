/* ----------------------------------------------------------------------------
 * Copyright 2006 - 2011 Johnny Graber & Andreas Muedespacher
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

package ataraxis.gui;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;

import ataraxis.crypt.AESKeyCreator;
import ataraxis.crypt.AtaraxisCrypter;
import ataraxis.crypt.JurisdictionPolicyError;
import ataraxis.misc.CopyPolicyFiles;


/**
 * AtaraxisSplashScreen is used to display a small icon on startup. In the background
 * it checks if the necessary policy files are installed.
 * 
 * @author J. Graber & A. Muedespacher
 * @version 1.3
 */
public class AtaraxisSplashScreen 
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisSplashScreen.class);

	private ResourceBundle s_translations;
	private GUIHelper guiHelper;
	
	// file path constants
	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	private static final String ICON_DIR = APPL_DATA_DIR + "/icons";
	private static final String LOG_PROPS_FILE = APPL_DATA_DIR + "/config/log4j.properties";
	
	private boolean patchWasOk = true;

	/**
	 * Constructor for AtaraxisSplashScreen.
	 * @param translations
	 */
	public AtaraxisSplashScreen(ResourceBundle translations)
	{
		s_translations = translations;
	}
	
	/**
	 * Open the window
	 */
	public boolean open() 
	{
		final Display display = Display.getDefault();
		final Image image = new Image(display, ICON_DIR + "/Info_Ataraxis.png");
		final Shell splash = new Shell(SWT.ON_TOP);
		guiHelper = new GUIHelper(splash);
		
		// add Icon
		Label label = new Label(splash, SWT.NONE);
		label.setImage(image);
		GridData gridData_1 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridData_1.horizontalAlignment = SWT.CENTER;
		label.setLayoutData(gridData_1);
		
		// add ProgressBar
		Label infoText = new Label(splash, SWT.NONE);
		infoText.setText(s_translations.getString("SPLASH.INFOTEXT"));
		
		// put it together
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		splash.setLayout(layout);
		splash.pack();
		
		// center Splash
		Rectangle splashRect = splash.getBounds();
		Monitor primary = display.getPrimaryMonitor();
		final Rectangle displayRect = primary.getBounds();
		int x = (displayRect.width - splashRect.width) / 2;
		int y = (displayRect.height - splashRect.height) / 2;
		splash.setLocation(x, y);
		
		// set color
		guiHelper.changeBackgroundColor(splash, AtaraxisStarter.COLOR);
		
		// open
		splash.open();
		
		
		// do work
		display.asyncExec(new Runnable() {
			public void run() 
			{	
				try 
				{
					// check jurisdiction policies
					AtaraxisCrypter.checkJurisdictionPolicy(new AESKeyCreator());
				} 
				catch (JurisdictionPolicyError e) 
				{
					LOGGER.debug("policy files are restricted (default JRE files)");
		        	if(guiHelper.displayQuestionMessage(s_translations.getString("MESSAGE.EXITING.TITLE"), s_translations.getString("LOGIN.MESSAGE.POLICY")))
		        	{
		        		LOGGER.debug("copy unrestricted policy files now");
		        		boolean patchWorked = (new CopyPolicyFiles()).copyFiles();
		        		
		        		if(!patchWorked)
		        		{
		        			guiHelper.displayErrorMessage(s_translations.getString("ERROR"), s_translations.getString("LOGIN.MESSAGE.POLICY.ADMIN"));
		        		}
		        		
		        		LOGGER.info("EXIT for Restart after copy policy files. Patch worked? " + patchWorked);
		        		System.exit(0); // zero indicates a normal termination
		        	}
		        	else
		        	{
		        		LOGGER.info("EXIT after user denied to copy polcy files");
		        		System.exit(0); // zero indicates a normal termination
		        	}
				}
				splash.close();
				image.dispose();
			}
		});
		
		while (!splash.isDisposed())
		{
			if (!display.readAndDispatch())
				display.sleep();
		}
		display.dispose();

		return patchWasOk;
	}
	
	public static void main(String[] args) {
		PropertyConfigurator.configure(LOG_PROPS_FILE); 
		
		AtaraxisSplashScreen splash = new AtaraxisSplashScreen(ResourceBundle.getBundle("AtaraxisTranslation", new Locale("de")));
		splash.open();
	}
}
