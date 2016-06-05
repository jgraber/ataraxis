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

package ataraxis.misc;

import java.security.SecureRandom;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * PasswordGenerator can be used to create passwords  
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 *
 */
public class PasswordGenerator 
{

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(PasswordGenerator.class);


	// Basic Elements for the Alphabet
	private String chars_AZ = "ABCDEFGHIJKLMNOPQRTSUVWXYZ";
	private String chars_az = "abcdefghijklmnopqrstuvwxyz";
	private String chars_09 = "0123456789";
	private String chars_Special = ".:,*+/()=@";

	// Which one is allowed in Password
	private boolean include_AZ = true;
	private boolean include_az = true;
	private boolean include_09 = true;
	private boolean include_Special = true;

	// If the Alphabet needs to recreated
	private boolean selection_changed = true;

	// Helper vars for Prog
	private SecureRandom sr;
	private StringBuffer alphabetBuffer;
	private int alphabetLength = 0;


	/**
	 * Constructor for the PasswordGenerator() class
	 *
	 */
	public PasswordGenerator() 
	{
		sr = new SecureRandom();

		LOGGER.debug("SecureRandom info:");
		LOGGER.debug("==================");
		LOGGER.debug("Provider: "+ sr.getProvider());
		LOGGER.debug("Algorithm: "+ sr.getAlgorithm());
	}

	/**
	 * Method to create a passwort with the length of size
	 *
	 * @param size length of the password
	 * @return the generated password
	 */
	public String generatePW(int size)
	{
		final StringBuffer newPassword = new StringBuffer();
		char letter;

		// Init new Alphabet if needed
		if(selection_changed)
		{
			initAlphabet();
		}

		// Generate Password
		for (int i = 0; i < size; i++)
		{
			letter = alphabetBuffer.charAt(sr.nextInt(alphabetLength));
			newPassword.append(letter);
		}

		LOGGER.debug("generatePW(int) - password generated");

		return newPassword.toString();
	}

	/**
	 * Method to recreate the alphabet. Will be automaticaly called if needet.
	 *
	 */
	private void initAlphabet()
	{
		alphabetBuffer = new StringBuffer();

		if(include_AZ)
		{
			alphabetBuffer.append(chars_AZ);
			LOGGER.debug("initAlphabet() - add chars_AZ");
		}
		if(include_az)
		{
			alphabetBuffer.append(chars_az);
			LOGGER.debug("initAlphabet() - add chars_az");
		}
		if(include_09)
		{
			alphabetBuffer.append(chars_09);
			LOGGER.debug("initAlphabet() - add chars_09");
		}
		if(include_Special)
		{
			alphabetBuffer.append(chars_Special);
			LOGGER.debug("initAlphabet() - add chars_Special");
		}

		alphabetLength = alphabetBuffer.length();
		selection_changed = false;
		LOGGER.debug("initAlphabet() - Alphabeth recreated");
	}

	/**
	 * Are numbers (0-9) included?
	 * @return true for yes, false otherwise
	 */
	public boolean isIncluded_09()
	{
		return include_09;
	}

	/**
	 * Include numbers 0 - 9?
	 * @param include_09 set if should be included or not
	 */
	public void setInclude_09(boolean include_09)
	{
		if(this.include_09 != include_09)
		{
			this.include_09 = include_09;
			selection_changed = true;
			LOGGER.debug("setInclude_09 - changed to " + include_09);
		}
	}

	/**
	 * Are chars a - z included?
	 * @return true for yes, false otherwise
	 */
	public boolean isIncluded_az() 
	{
		return include_az;
	}

	/**
	 * Include chars a - z?
	 * @param include_az set if should be included or not
	 */
	public void setInclude_az(boolean include_az) {
		if(this.include_az != include_az)
		{
			this.include_az = include_az;
			selection_changed = true;
			LOGGER.debug("setInclude_az - changed to " + include_az);
		}
	}

	/**
	 * Are chars A - Z included?
	 * @return true for yes, false otherwise
	 */
	public boolean isIncluded_AZ() 
	{
		return include_AZ;
	}

	/**
	 * Include chars A - Z?
	 * @param include_AZ set if should be included or not
	 */
	public void setInclude_AZ(boolean include_AZ) 
	{
		if(this.include_AZ != include_AZ)
		{
			this.include_AZ = include_AZ;
			selection_changed = true;
			LOGGER.debug("setInclude_AZ - changed to " + include_AZ);
		}
	}

	/**
	 * Special chars ".:,*+/()=@" included?
	 * @return true for yes, false otherwise
	 */
	public boolean isIncluded_Special() 
	{
		return include_Special;
	}


	/**
	 * Include special chars ".:,*+/()=@"? 
	 * @param include_Special set if should be included or not
	 */
	public void setInclude_Special(boolean include_Special)
	{
		if(this.include_Special != include_Special)
		{
			this.include_Special = include_Special;
			selection_changed = true;
			LOGGER.debug("setInclude_Special - changed to " + include_Special);
		}
	}

	/**
	 * Get the current selected Alphabet. It will be recreated if it has been
	 * changed but not yet updated
	 * 
	 * @return the current alphabet
	 */
	public String getAlphabet()
	{
		if(selection_changed)
		{
			initAlphabet();
		}
		return alphabetBuffer.toString();
	}
}
