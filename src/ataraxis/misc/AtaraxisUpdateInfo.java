/* ----------------------------------------------------------------------------
 * Copyright 2008 - 2016 Johnny Graber & Andreas Muedespacher
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

package ataraxis.misc;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;


/**
 * AtaraxisUpdateInfo inform about new versions of AtaraxiS.
 *
 * @author J. Graber & A. Muedespacher
 * @version 1.0
 *
 */
public class AtaraxisUpdateInfo 
{
	
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisUpdateInfo.class);
	
	
	private static final String networkConfigFile =  System.getProperty("user.dir") + 
		"/application_data/config/network.properties";
	
	
	private Properties s_updateProps;
	

	
	private boolean usingCurrentVersion = false;
	private String lastReleasedVersion = "";
	private String urlOfCurrentVersion = "";


	private UpdateCheckable updateChecker;
	

	
	/**
	 * AtaraxisUpdateInfo can be used to check if a newer version of 
	 * AtaraxiS exist. It connects to the Website and sends the current
	 * version as Parameter to a PHP script.
	 * 
	 * To find out where to send and if it has to use a proxy server it parses
	 * the property-file in
	 * System.getProperty("user.dir")+"/application_data/config/network.properties
	 * 
	 */
	public AtaraxisUpdateInfo()
	{
		LOGGER.info("Create AtaraxisUpdateInfo" );
		Properties configuration = new Properties();
		
		try 
		{
			configuration.load(new FileInputStream(networkConfigFile));
		}
		catch (Exception e) {
			LOGGER.warn("Properties could not be loaded",e);
		}
		
	
		this.updateChecker = new NetworkUpdateCheck(configuration);

	}
	
	public AtaraxisUpdateInfo(UpdateCheckable updateChecker)
	{
		LOGGER.info("Create AtaraxisUpdateInfo(UpdateCheckable)" );
		
		this.updateChecker = updateChecker;
	}
	
	/**
	 * Performs the Check by sending the version to the Website and
	 * check what it returns. Possible return values of the PHP Script are:
	 * - old: your version is an old one
	 * - current: you have the latest version
	 * - newer: your version is newer than the current version (for the PHP Script)
	 * - error: your version is missing or not a valid version 
	 * 
	 * @param myCurrentVersion your version who should by checked
	 * @return true if a newer version exist, false otherwise
	 * @throws IOException if the connection could not be establish
	 */
	public boolean existNewerVersion(String myCurrentVersion) throws IOException
	{
		LOGGER.debug("Start existNewVersion() for "+myCurrentVersion );
		
		try 
		{
			s_updateProps = updateChecker.checkForUpdate(myCurrentVersion);
			
			LOGGER.debug("Responsed Property: " + s_updateProps);
			
			String s_isCurrent = s_updateProps.getProperty("submitted.version");
			
			if(s_isCurrent.equals("current") || s_isCurrent.equals("next"))
			{
				usingCurrentVersion = true;
			}
			else
			{
				usingCurrentVersion = false;
			}
			
			lastReleasedVersion = s_updateProps.getProperty("current.version");
			urlOfCurrentVersion =  s_updateProps.getProperty("download.url");
			 
		} 
		catch (UpdateException e) 
		{
			LOGGER.warn(e.getMessage());
			throw new IOException(e);
		}        
        
        
		return !usingCurrentVersion;
	}
	
	
	/**
	 * Returns the last released version. If you do 
	 * @return the last released version or an empty string
	 */
	public String getCurrentVersion()
	{
		return lastReleasedVersion;
	}
	
	/**
	 * Get the URL of the current version of AtaraxiS.
	 *
	 * @return the URL for download AtaraxiS
	 */
	public String getCurrentURL()
	{
		return urlOfCurrentVersion;
	}
}
