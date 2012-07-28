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

import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import ch.ethz.origo.ataraxis.crypt.AtaraxisCrypter;

public class AtaraxisStarter
{
	private static AtaraxisCrypter s_ac = null;
	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	protected static final String LOG_PROPS_FILE = APPL_DATA_DIR + "/config/log4j.properties";
	protected static final Color COLOR = new Color(Display.getDefault(), 220, 220, 255);
	
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(AtaraxisStarter.class);

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		PropertyConfigurator.configure(LOG_PROPS_FILE); 
		Date date = new Date();

		LOGGER.info("##### Entering login GUI at: " + date.toString() + " #####");
		final AtaraxisLoginGUI login = new AtaraxisLoginGUI();
		s_ac = login.open();
		date = new Date();
		LOGGER.debug("##### Exiting login GUI at: " + date.toString() + " #####");
		
		if (s_ac == null)
			LOGGER.warn("AtaraxisCrypter could not be initalised!");
		// message window is opened by AtaraxisLoginGUI
		
		else
		{
			date = new Date();
			LOGGER.debug("##### Entering main GUI at: " + date.toString() + " #####");
			final AtaraxisMainGUI main = new AtaraxisMainGUI(s_ac);
			main.open();
			date = new Date();
			LOGGER.info("##### Exiting application at: " + date.toString() + " #####");
		}
	}
}
