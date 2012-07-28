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

package ch.ethz.origo.ataraxis.crypt;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * UBERKeyStoreCreator creates a BouncyCastle UBER KeyStore.
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 */
public final class UBERKeyStoreCreator implements KeyStoreCreator
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(UBERKeyStoreCreator.class);
    
    /**
     * UBERKeyStoreCreator create a UBER KeyStore at the FileSystem-Path 
     * with the submitted password.
     *
     * @param  keyStoreFile FileSystemPath to the KeyStore
     * @param  keyStorePassword the password for the Keystore
     * @return the created and to disk saved KeyStore
     * @throws KeyStoreException by errors with the KeyStore creation
     * @throws NoSuchProviderException if the Provider is missing
     * @throws NoSuchAlgorithmException is the Algorithm is missing
     */
    public final KeyStore createKeyStore(File keyStoreFile, char[] keyStorePassword)
        throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException
    {
		LOGGER.debug("createKeyStore(String, String) - start"); 

        Security.addProvider(new BouncyCastleProvider());
        KeyStore ksUBER = KeyStore.getInstance("UBER", "BC");
        
        
        try
        {
        	// Load empty KeyStore
            ksUBER.load(null, keyStorePassword);

            // Save Keystore
            FileOutputStream fos;
            fos = new FileOutputStream(keyStoreFile);
            ksUBER.store(fos, keyStorePassword);
            fos.close();
        }
        catch (FileNotFoundException e)
        {
			LOGGER.error("Error on FileHandling", e);
            throw new KeyStoreException("Error on FileHandling");
        }
        catch (CertificateException e)
        {
			LOGGER.error("Certificate Error on KeyStore", e);
            throw new KeyStoreException("Certificate Error on KeyStore");
        }
        catch (IOException e)
        {
			LOGGER.error("IO Error on writing the KeyStore", e);
            throw new KeyStoreException("IO Error on writing the KeyStore");
        }

		LOGGER.debug("createKeyStore(String, String) - end");

        return ksUBER;
    }
}
