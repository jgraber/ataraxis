/* ----------------------------------------------------------------------------
 * Copyright 2008 - 2010 Johnny Graber & Andreas Muedespacher
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

package ataraxis.crypt;

import java.security.SecureRandom;
import java.util.Arrays;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bouncycastle.util.encoders.Base64;


/**
 * AtaraxisHeaderCreator creates an Header to use in the encryption to store 
 * the initial vector for the CBC mode.
 *
 * @author J. Graber
 * @version 1.0
 */
public class AtaraxisHeaderCreator 
{

	private static SecureRandom random;
	private byte[] ivBytes;
	private String headerVersion = "1.0.0";
	private String headerContent;

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisHeaderCreator.class);

	/**
	 * AtaraxisHeaderCreator create a new random initial Vector and makes all ready
	 * to store this new Header to a File. You could use this for mulitple files, but we
	 * strongly suggest that you create for every file a new Header.
	 */
	public AtaraxisHeaderCreator()
	{
		LOGGER.debug("Start with AtaraxisHeaderCreator()");
		random = new SecureRandom();
		ivBytes = new byte[16];
		random.nextBytes(ivBytes); 

		headerContent = "AtaraxiS Header Start\n" 
			+ "HeaderVersion: " + headerVersion + "\n"
			+ "IV: " + new String(Base64.encode(ivBytes ))  + "\n"	
			+ "AtaraxiS Header End\n\n";

		LOGGER.info("AtaraxisHeaderCreator() is ready to use");
	}


	/**
	 * Get the Header to insert into the File encrypted by AtaraxiS.
	 * 
	 * @return the Header as String
	 */
	public String getHeader()
	{		
		return headerContent;
	}


	/**
	 * Get the Version of the Header.
	 *
	 * @return the Version as String
	 */
	public String getHeaderVersion()
	{
		return headerVersion;
	}


	/**
	 * Get the InitialVector for the encryption stored in the Header.
	 *
	 * @return the InitialVector
	 */
	public byte[] getIV()
	{
		return Arrays.copyOf(ivBytes,ivBytes.length);
	}
}
