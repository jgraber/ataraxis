/* ----------------------------------------------------------------------------
 * Copyright 2006 - 2010 Johnny Graber & Andreas Muedespacher
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

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * AESKeyCreator creates an AES SecretKey.
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 *
 */
public final class AESKeyCreator implements SecretKeyCreator
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AESKeyCreator.class);

    /**
     * Size of the AES-Key.
     */
    private final int KEY_SIZE = 256;

    /**
     * Create a symetric SecretKey based on the AES Algorithm.
     * @return  the created SecretKey
     * @throws NoSuchAlgorithmException  if the Algorithm does not exist
     * @throws NoSuchProviderException  if the Provider does not exist
     * @throws NotImplementedException  if the method is not implemented
     * @see ataraxis.crypt.SecretKeyCreator
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
}
