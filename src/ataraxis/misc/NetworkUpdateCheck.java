/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2016 Johnny Graber & Andreas Muedespacher
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

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class NetworkUpdateCheck implements UpdateCheckable 
{
	private Properties s_networkProps;
	private boolean useProxy;
	private String proxyHost;
	private String proxyPort;
	private String updateWebsite;

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisUpdateInfo.class);
	

	/**
	 * Constructor to build a network connection to the update site.
	 *
	 * @param configuration
	 */
	public NetworkUpdateCheck(Properties configuration)
	{
		s_networkProps = configuration;
		loadNetworkProps();
	}


	/**
	 * Method to load the network properties.
	 */
	private void loadNetworkProps() 
	{
		String proxyProperty = s_networkProps.getProperty("ATARAXIS.NETWORK.USE_PROXY","");

		if (proxyProperty.equals("TRUE"))
		{
			useProxy = true;
		}
		else
		{
			useProxy = false;
		}

		proxyHost = s_networkProps.getProperty("ATARAXIS.NETWORK.HOST","");
		proxyPort  = s_networkProps.getProperty("ATARAXIS.NETWORK.PORT","");
		updateWebsite = s_networkProps.getProperty("ATARAXIS.NETWORK.UPDATEURL","");	
	}

	
	/**
	 * Check for Update over a network connection.
	 *
	 * @param versionNumber the version of the program to check
	 * @return properties with update information 
	 */
	@Override
	public Properties checkForUpdate(String versionNumber) throws UpdateException
	{
		LOGGER.debug("Start existNewVersion() for " + versionNumber);

		Properties updateProps = null;
		
		try 
		{
			if(useProxy)
			{
				System.setProperty("http.useProxy", "true"); 
				System.setProperty("http.proxyHost", proxyHost);
				System.setProperty("http.proxyPort", proxyPort);
			}
			else
			{
				System.setProperty("http.useProxy", "false"); 
				System.setProperty("http.proxyHost", "");
				System.setProperty("http.proxyPort", "");
			}
			
			String tempURL = updateWebsite + "?version="+URLEncoder.encode(versionNumber.trim(), "UTF-8");
			LOGGER.debug("Connecting to this url: " + tempURL);
			
			URL url = new URL(tempURL);
			URLConnection conn = url.openConnection();
			conn.setRequestProperty("Accept", "text/html");
			conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.154 Safari/537.36");
			InputStream webInStream = conn.getInputStream();
		 		 
			updateProps = new Properties();
			updateProps.load(webInStream);
			
			LOGGER.debug("Responsed Property: " + updateProps); 
		} 
		catch (Exception e) 
		{
			LOGGER.warn(e.getMessage());
			throw new UpdateException(e);
		} 
		
		return updateProps;
	}

}
