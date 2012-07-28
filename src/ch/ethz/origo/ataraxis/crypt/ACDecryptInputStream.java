/* ----------------------------------------------------------------------------
 * Copyright 2008 - 2010 Johnny Graber & Andreas Muedespacher
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

package ch.ethz.origo.ataraxis.crypt;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.log4j.Logger;

/**
 * ACDecryptInputStream creates an CipherInputStream which will decrypt all the data 
 * read from it. It uses the AtaraxisHeader to detect if there was an InitialVector 
 * used. If so it will use the CBC mode, otherwise it will simply decrypt block by block.
 *
 * @author J. Graber
 * @version 1.1
 */
public class ACDecryptInputStream 
{
	private static final Logger LOGGER = Logger.getLogger(ACDecryptInputStream.class);
	private FileInputStream fis;
	private Cipher cipher;
	private CipherInputStream decryptedInputStream;


	/**
	 * ACEncryptOutputStream create an CipherInputStream and use CBC when the Header 
	 * contains an InitialVector.
	 *
	 * @param inFile the File to read the Data from
	 * @param aesKey the Key to decrypt the Data
	 * @throws IOException if any error occurs
	 */
	public ACDecryptInputStream (File inFile, SecretKey aesKey) throws IOException
	{

		try 
		{
			// Parse the Header of the File
			AtaraxisHeaderParser ahParser = new AtaraxisHeaderParser(inFile);

			// When it contains a Header, then prepare all for CBC-Mode
			if(ahParser.containsFileHeader())
			{
				IvParameterSpec ivSpec = new IvParameterSpec(ahParser.getIV());

				fis = new FileInputStream(inFile);
				fis.skip(ahParser.bytesToSkip());

				cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
				cipher.init(Cipher.DECRYPT_MODE, aesKey, ivSpec);
			}
			else
			{
				// no Header means no IV, so use the old method without CBC
				fis = new FileInputStream(inFile);
				cipher = Cipher.getInstance("AES", "BC");
				cipher.init(Cipher.DECRYPT_MODE, aesKey);
			}

			// Create the CipherInputStream
			decryptedInputStream = new CipherInputStream(fis, cipher);
		} 
		catch (NoSuchAlgorithmException e)
		{
			LOGGER.error("NoSuchAlgorithmException", e);
			throw new IOException("NoSuchAlgorithmException");
		} 
		catch (NoSuchProviderException e)
		{
			LOGGER.error("NoSuchProviderException", e);
			throw new IOException("NoSuchProviderException");
		}
		catch (NoSuchPaddingException e)
		{
			LOGGER.error("NoSuchPaddingException", e);
			throw new IOException("NoSuchPaddingException");
		}
		catch (InvalidKeyException e)
		{
			LOGGER.error("InvalidKeyException", e);
			throw new IOException("InvalidKeyException");
		} 
		catch (InvalidAlgorithmParameterException e) 
		{
			LOGGER.error("InvalidAlgorithmParameterException", e);
			throw new IOException("InvalidAlgorithmParameterException");
		}
		
		
		LOGGER.debug("ACDecriptInputStream is ready");
	}

	/**
	 * Returns an CipherInputStream who decrypt the Data read from it.
	 * You can use it as a normal input Stream and do not to care about the 
	 * decryption itself. This will be made transparent to your application.
	 * 
	 * Please close the Stream if you do not longer need it.
	 *
	 * @return the InputStream
	 */
	public InputStream getDecriptInputStream()
	{
		return decryptedInputStream;
	}
}
