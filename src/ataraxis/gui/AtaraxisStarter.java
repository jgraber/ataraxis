/* ----------------------------------------------------------------------------
 * Copyright 2006 - 2015 Johnny Graber & Andreas Muedespacher
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

package ataraxis.gui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import ataraxis.crypt.AtaraxisCrypter;
import ataraxis.util.AtaraxisHelper;


public class AtaraxisStarter
{
	private static AtaraxisCrypter s_ac = null;
	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	public static final String LOG_CONFIG_FILE = APPL_DATA_DIR + "/config/log4j2.xml";
	public static final Color COLOR = new Color(Display.getDefault(), 220, 220, 255);
	
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisStarter.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		//PropertyConfigurator.configure(LOG_PROPS_FILE); 
		Date date = new Date();

		AtaraxisHelper ataraxisHelper = new AtaraxisHelper();
		
		String runtimeLanguage = System.getProperty("user.language");
		Locale systemLanguage = new Locale(runtimeLanguage);
		ResourceBundle translations = ResourceBundle.getBundle("AtaraxisTranslation", systemLanguage); 
		
		LOGGER.info("##### Entering splash screen at: " + date.toString() + " #####");
		AtaraxisSplashScreen splashScreen = new AtaraxisSplashScreen(translations);
		boolean policyIsOk = splashScreen.open();
		
		LOGGER.info("Splashscreen: policyIsOK => " + policyIsOk);
		LOGGER.debug("##### Exiting splash screen at: " + date.toString() + " #####");
		
		
		LOGGER.info("##### Entering login GUI at: " + date.toString() + " #####");
		final AtaraxisLoginGUI login = new AtaraxisLoginGUI(translations);
		s_ac = login.open();
		date = new Date();
		LOGGER.debug("##### Exiting login GUI at: " + date.toString() + " #####");
		
		if (s_ac == null)
		{
			LOGGER.warn("AtaraxisCrypter could not be initalised!");
		}
		else // message window is opened by AtaraxisLoginGUI
		{
			date = new Date();
			String userLanguageString = "en";
			
			try {
				userLanguageString = ataraxisHelper.GetUserLanguage(s_ac.getKeyStorePath());
			} catch (FileNotFoundException e) {
				LOGGER.warn("Properties File not found", e);
			} catch (IOException e) {
				LOGGER.warn("Error with properties File", e);
			}
			
			Locale userLanguage = new Locale(userLanguageString);
			ResourceBundle.clearCache();
			translations = ResourceBundle.getBundle("AtaraxisTranslation", userLanguage); 
			
			LOGGER.debug("##### Entering main GUI at: " + date.toString() + " #####");
			final AtaraxisMainGUI main = new AtaraxisMainGUI(s_ac, translations);
			main.open();
			date = new Date();
			LOGGER.info("##### Exiting application at: " + date.toString() + " #####");
		}
	}
}
