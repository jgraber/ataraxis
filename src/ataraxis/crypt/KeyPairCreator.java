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

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Interface for the Creation of Cryptography-Keys used
 * by the AtaraxiS Classes.
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 */
public interface KeyPairCreator
{
    /**
     * Create a asymmetric KeyPair (Public and Private Key) based on the
     * implemented Algorithm or throws a NotImplementedException
     * if this Algorithm does not support a KeyPair.
     *
     * @return the created KeyPair
     * @throws NoSuchAlgorithmException if the Algorithm does not exist
     * @throws NoSuchProviderException if the Provider does not exist
     * @see java.security.KeyPair
     */
    KeyPair createKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException;
}
