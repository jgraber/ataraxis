/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2010 Johnny Graber & Andreas Muedespacher
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

package ataraxis.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import ataraxis.crypt.AtaraxisCrypter;


/**
 * AtaraxisHelper helps to work with conventions in AtaraxiS.
 * 
 * @author J. Graber & A. Muedespacher
 * @version 1.2
 */
public class AtaraxisHelper 
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisHelper.class);

	/**
	 * Get the user name based on the AtaraxisCrypter.
	 * 
	 * @param ataraxisCrypter the AtaraxisCrypter
	 * @return name of the user
	 */
	public String GetUserName(AtaraxisCrypter ataraxisCrypter) 
	{
		return GetUserName(ataraxisCrypter.getKeyStorePath());
	}

	/**
	 * Get the user name based on the path of the KeyStore.
	 * 
	 * @param keyStorePath path of the KeyStore
	 * @return name of the user
	 */
	public String GetUserName(String keyStorePath)
	{
		if(keyStorePath == null || keyStorePath.equals(""))
		{
			throw new IllegalArgumentException("Path of KeyStore can't be empty");
		}
		
		File keyStoreFile = new File(keyStorePath);
		
		return keyStoreFile.getParentFile().getName();
	}

	/**
	 * Get language of user based on the path of the KeyStore.
	 * 
	 * @param keyStorePath path to the KeyStore
	 * @return 2 letter language code (de, fr, en)
	 * @throws FileNotFoundException if properties file not found
	 * @throws IOException on error with the properties file
	 */
	public String GetUserLanguage(String keyStorePath) throws FileNotFoundException, IOException 
	{
		if(keyStorePath == null || keyStorePath.equals(""))
		{
			throw new IllegalArgumentException("Path of KeyStore can't be empty");
		}
		
		File keyStoreFile = new File(keyStorePath);
		String propertiesFilePath = keyStoreFile.getParentFile().getPath() + "/user.props";
		
		LOGGER.debug("path to Properties: "+ propertiesFilePath);
		Properties userProperties = new Properties();
		userProperties.load(new FileInputStream(propertiesFilePath));		
		
		return userProperties.getProperty("user.lang", "en");
	}
}
