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

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * AESKeyCreator creates an AES SecretKey.
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 *
 */
public final class AESKeyCreator implements KeyCreator
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(AESKeyCreator.class);

    /**
     * Size of the AES-Key.
     */
    private final int KEY_SIZE = 256;

    /**
     * Create a symetric SecretKey based on the AES Algorithm.
     * @return  the created SecretKey
     * @throws NoSuchAlgorithmException  if the Algorithm does not exist
     * @throws NoSuchProviderException  if the Provider does not exist
     * @throws NotImplementedException  if the method is not implementet
     * @see ch.ethz.origo.ataraxis.crypt.KeyCreator
     */
    public final SecretKey createSecretKey() throws NoSuchAlgorithmException,
        NoSuchProviderException, NotImplementedException
    {

		LOGGER.debug("createSecretKey() - start"); 
		LOGGER.debug("Size of AES-Key: " + KEY_SIZE);
		
        Security.addProvider(new BouncyCastleProvider());
        final KeyGenerator generator = KeyGenerator.getInstance("AES", "BC");
        generator.init(KEY_SIZE);
		final SecretKey returnSecretKey = generator.generateKey();

		LOGGER.debug("createSecretKey() - end");
		
        return returnSecretKey;
    }

    /**
     * AES does not support a KeyPair, therefore it will throw a
     * NotImplementedException.
     *
     * @return  the created SecretKey
     * @throws NoSuchAlgorithmException  if the Algorithm does not exist
     * @throws NoSuchProviderException  if the Provider does not exist
     * @throws NotImplementedException  if the method is not implementet
     * @see ch.ethz.origo.ataraxis.crypt.KeyCreator
     */
    public final KeyPair createKeyPair() throws NoSuchAlgorithmException,
        NoSuchProviderException, NotImplementedException
    {
    	LOGGER.error("Methode not Implemented!");
        throw new NotImplementedException("Methode not Implemented!");
    }
}
