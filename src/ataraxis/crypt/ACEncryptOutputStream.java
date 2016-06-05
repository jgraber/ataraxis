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

package ataraxis.crypt;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * ACEncryptOutputStream creates an CipherOutputStream which will encrypt all the data 
 * send to it. It uses the AtaraxisHeader to store an InitialVector and encrypt the 
 * data in CBC mode. This means more security if you re-encrypt the Data you get a different 
 * output.
 *
 * @author J. Graber
 * @version 1.0
 */
public class ACEncryptOutputStream 
{
	private static final Logger LOGGER = LogManager.getLogger(ACEncryptOutputStream.class);
	private CipherOutputStream encryptedOutputStream;


	/**
	 * ACEncryptOutputStream create an CipherOutputStream with CBC based on an InitialVector
	 * stored on the beginning of the outFile.
	 *
	 * @param outFile the File to store the Data in
	 * @param aesKey the Key to encrypt the Data
	 * @throws IOException if any error occurs
	 */
	public ACEncryptOutputStream (File outFile, SecretKey aesKey) throws IOException
	{

		try 
		{
			// Create a AtaraxisHeaderCreator-Object
			AtaraxisHeaderCreator headerCreator = new AtaraxisHeaderCreator();

			// write the Header to the File			
			FileOutputStream headerOutStream = new FileOutputStream(outFile);
			headerOutStream.write(headerCreator.getHeader().getBytes());
			headerOutStream.flush();
			headerOutStream.close();

			// use the IV from the Header and create the CipherOutputStream with CBC			
			IvParameterSpec ivSpec = new IvParameterSpec(headerCreator.getIV());
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
			cipher.init(Cipher.ENCRYPT_MODE, aesKey, ivSpec);
			encryptedOutputStream = new  CipherOutputStream(new FileOutputStream(outFile,true), cipher);

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

		LOGGER.debug("ACEncryptOutputStream is ready");
	}


	/**
	 * Returns an CipherOutputStream who encrypt the Data written to it.
	 * You can use it as a normal output Stream and do not to care about the 
	 * encryption itself. This will be made transparent to your application.
	 * 
	 * Please close the Stream if you do not longer need it.
	 *
	 * @return the OutputStream
	 */
	public OutputStream getEncryptOutputStream()
	{
		return encryptedOutputStream;
	}
}
