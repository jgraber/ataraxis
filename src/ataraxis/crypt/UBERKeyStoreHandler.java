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

package ataraxis.crypt;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.cert.CertificateException;
import java.util.Enumeration;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * The UBERKeyStoreHandler implement the KeyStoreHandler for the
 * UBER KeyStore of BouncyCastle.
 *
 * @author  J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 *
 */
public final class UBERKeyStoreHandler extends KeyStoreHandler
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(UBERKeyStoreHandler.class);

    /**
     * Constructor for the UBERKeyStoreHandler with a KeyStorePath and
     * a KeyStorePassword.
     *
     * @param  keyStorePath FileSystemPath to the existing KeyStore
     * @param  keyStorePassword the Password for the KeyStore
     * @throws KeyStoreException by errors with the KeyStore
     * @throws NoSuchProviderException if the Provider is missing
     */
    public UBERKeyStoreHandler(File keyStoreFile, char[] keyStorePassword)
        throws KeyStoreException, NoSuchProviderException
    {
    	LOGGER.debug("UBERKeyStoreHandler(String, String) - start");
    	
        Security.addProvider(new BouncyCastleProvider());
        KeyStore uberkeyStore = KeyStore.getInstance("UBER", "BC");

        LOGGER.debug("KeyStore Path: " + keyStoreFile.getAbsolutePath());

        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(keyStoreFile);
            uberkeyStore.load(fis, keyStorePassword);
            
            super.setKeyStore(uberkeyStore);
            super.setKeyStorePath(keyStoreFile.getAbsolutePath());
            super.changePassword(null, keyStorePassword);
            
            store();
        }
        catch (FileNotFoundException e)
        {
			LOGGER.error("KeyStore File not found!", e);
            throw new KeyStoreException("KeyStore File not found!");
        }
        catch (CertificateException e)
        {
			LOGGER.error("Error on Certificate", e);
            throw new KeyStoreException("Error on Certificate");
        }
        catch (IOException e)
        {
			LOGGER.error("IO Error on loading the KeyStore " + e.getMessage());
            throw new KeyStoreException("IO Error on loading the KeyStore");
        }
        catch (NoSuchAlgorithmException e)
        {
			LOGGER.error("Algorithm of KeyStore does not exist!", e);
            throw new KeyStoreException(
                    "Algorithm of KeyStore does not exist!");
        }
        finally
        {
            try
            {
            	if(fis != null)
            	{
            		fis.close();
            	}
            }
            catch (IOException e)
            {
				LOGGER.error("IOException", e); 
            }
        }
        
    	LOGGER.debug("UBERKeyStoreHandler(String, String) - end");
    }

    /**
     * Override the showKeyStore method of the KeyStoreHandler.
     * 
     */
    public final void showKeyStore()
    {
		LOGGER.debug("showKeyStore() - start");

        KeyStore keyStore = super.getKeyStore();
        
        try
        {
			LOGGER.info("KeyStore Type: " + keyStore.getType());
			
            Enumeration<String> contents = keyStore.aliases();
            while (contents.hasMoreElements())
            {
                String alias = (String) contents.nextElement();
                
                if (keyStore.isKeyEntry(alias))
                {
                	LOGGER.info(alias + " -  is a key");
                }
                else if (keyStore.isCertificateEntry(alias))
                {
					LOGGER.info(alias + " -  is a certificate");
                }
                else 
                {
                	LOGGER.info(alias + " -  is NOT a Key and NOT a certificate");
                }
            }
        }
        catch (KeyStoreException e)
        {
			LOGGER.warn("Error on proccessing the KeyStore content!", e);
        }

		LOGGER.debug("showKeyStore() - end");
    }
}
