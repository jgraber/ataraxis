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

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;

import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 * RSAKeyCreator creates an RSA KeyPair.
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 *
 */
public final class RSAKeyCreator implements KeyPairCreator
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(RSAKeyCreator.class);

    /**
     * Size of the AES-Key.
     */
    private final int KEY_SIZE = 1024;


    /**
     * Create a asymmetric KeyPair based on the RSA Algorithm.
     * 
     * @return  the created SecretKey
     * @throws  NoSuchAlgorithmException  if the Algorithm does not exist
     * @throws  NoSuchProviderException  if the Provider does not exist
     * @see     ataraxis.crypt.SecretKeyCreator
     */
    public final KeyPair createKeyPair() throws NoSuchAlgorithmException,
        NoSuchProviderException
    {
		LOGGER.debug("createKeyPair() - start");
		LOGGER.debug("Size of RSA-Key: " + KEY_SIZE);
		
		Security.addProvider(new BouncyCastleProvider());
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", "BC");
        generator.initialize(KEY_SIZE);
		KeyPair returnKeyPair = generator.generateKeyPair();
		
		LOGGER.debug("createKeyPair() - end");
        return returnKeyPair;
    }
}
