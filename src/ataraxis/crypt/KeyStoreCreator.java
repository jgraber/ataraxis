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

import java.io.File;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * Interface for KeyStore creation.
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 */
public interface KeyStoreCreator
{
    
    /**
     * createKeyStore create a KeyStore at the FileSystem-Path 
     * with the submitted password.
     *
     * @param keyStoreFile FileSystemPath to the KeyStore
     * @param keyStorePassword the password for the Keystore
     * @return the created and to disk saved KeyStore
     * @throws KeyStoreException by errors with the KeyStore creation
     * @throws NoSuchProviderException if the Provider is missing
     * @throws NoSuchAlgorithmException is the Algorithm is missing
     */
    KeyStore createKeyStore(File keyStoreFile, char[] keyStorePassword)
        throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException;
}
