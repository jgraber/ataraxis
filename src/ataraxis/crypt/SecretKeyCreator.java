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

package ataraxis.crypt;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.SecretKey;

/**
 * Interface for the Creation of Cryptography-Keys used
 * by the AtaraxiS Classes.
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 */
public interface SecretKeyCreator
{
    /**
     * Create a symmetric SecretKey based on the implemented Algorithm
     * or throws a NotImplementedException if this Algorithm
     * does not support a SecretKey.
     *
     * @return the created SecretKey
     * @throws NoSuchAlgorithmException if the Algorithm does not exist
     * @throws NoSuchProviderException if the Provider does not exist
     * @throws NotImplementedException if the method is not implemented
     * @see javax.crypto.SecretKey
     */
    SecretKey createSecretKey() throws NoSuchAlgorithmException,
        NoSuchProviderException, NotImplementedException;

}
