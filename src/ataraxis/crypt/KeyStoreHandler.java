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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.UnrecoverableEntryException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.Enumeration;

/**
 * Abstract Class for KeyStoreHandling.
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 *
 */
public abstract class KeyStoreHandler
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(KeyStoreHandler.class);

	/**
	 * Field for the KeyStore.
	 */
	private KeyStore m_keyStore = null;

	/**
	 * Field for the KeyStorePath.
	 */
	private String m_keyStorePath = null;

	/**
	 * Field for the KeyStorePassword.
	 */
	private char[] m_ksPassword;


	/**
	 * Method to change the password of the underlying KeyStore.
	 * 
	 * @param  oldPassword the current password
	 * @param  newPassword the new password
	 * @return true if successful
	 * @throws KeyStoreException by errors with the KeyStore
	 * @throws IOException by I/O errors
	 */
	public final boolean changePassword(char[] oldPassword, char[] newPassword)
	throws KeyStoreException, IOException
	{
		LOGGER.debug("changePassword(char[], char[]) - start");

		char[] localOldPassword = null;
		if(oldPassword != null)
		{
			localOldPassword = Arrays.copyOf(oldPassword,oldPassword.length);
		}

		char[] localNewPassword = null;
		if(newPassword != null)
		{
			localNewPassword = Arrays.copyOf(newPassword,newPassword.length);
		}

		boolean successChanged = false;
		if (Arrays.equals(localOldPassword, m_ksPassword))
		{
			char[] tempOldPassword = null;
			if(m_ksPassword != null)
			{
				tempOldPassword = Arrays.copyOf(m_ksPassword,m_ksPassword.length);
			}
			
			try
			{
				m_ksPassword = localNewPassword;
				store();
				successChanged = true;
				LOGGER.debug("Password changed");
			}
			finally
			{
				if (!successChanged)
				{
					m_ksPassword =  tempOldPassword;
					LOGGER.error("Password change failed");
				}
			}
		}
		else
		{
			LOGGER.debug("Passwords don't macht! No change.");
		}

		LOGGER.debug("changePassword(char[], char[]) - end");
		return successChanged;
	}

	/**
	 * Abstract Method to show the content of the KeyStore.
	 * Have to be implemented by the concrete KeyStoreHandler.
	 */
	public abstract void showKeyStore();

	/**
	 * Lists all the alias names of this keystore.
	 * 
	 * @return  enumeration of the alias names
	 * @throws  KeyStoreException if the keystore has not been initialized (loaded)
	 */
	public final Enumeration<String> aliases() throws KeyStoreException
	{
		LOGGER.debug("aliases() - start");

		Enumeration<String> returnEnumeration = m_keyStore.aliases();

		LOGGER.debug("aliases() - end");

		return returnEnumeration;
	}

	/**
	 * Checks if the given alias exists in this KeyStore.
	 * 
	 * @param   alias the alias name
	 * @return  true if the alias exists, false otherwise
	 * @throws  KeyStoreException if the keystore has not been initialized (loaded)
	 */
	public final boolean containsAlias(String alias) throws KeyStoreException
	{
		LOGGER.debug("containsAlias(String) - start");

		boolean returnboolean = m_keyStore.containsAlias(alias);

		LOGGER.debug("containsAlias(String) - end");

		return returnboolean;
	}

	/**
	 * Retrieves the number of entries in this KeyStore.
	 * 
	 * @return  the number of entries in this KeyStore
	 * @throws  KeyStoreException if the KeyStore has not been initialized (loaded)
	 */
	public final int size() throws KeyStoreException
	{
		LOGGER.debug("size() - start");

		int returnint = m_keyStore.size();

		LOGGER.debug("size() - end");

		return returnint;
	}

	/**
	 * Returns true if the entry identified by the given alias was
	 * created by a call to setKeyEntry, or created by a call to
	 * setEntry with a PrivateKeyEntry or a SecretKeyEntry.
	 *
	 * @param   alias the alias for the keystore entry to be checked
	 * @return  true if the entry identified by the given alias is a
	 *          key-related entry, false otherwise.
	 * @throws  KeyStoreException if the keystore has not been initialized (loaded)
	 */
	public final boolean isKeyEntry(String alias) throws KeyStoreException
	{
		LOGGER.debug("isKeyEntry(String) - start");

		boolean returnboolean = m_keyStore.isKeyEntry(alias);

		LOGGER.debug("isKeyEntry(String) - end");

		return returnboolean;
	}

	/**
	 * Returns true if the entry identified by the given alias was
	 * created by a call to setCertificateEntry, or created by a call
	 * to setEntry with a TrustedCertificateEntry.
	 *
	 * @param   alias the alias for the keystore entry to be checked
	 * @return  true if the entry identified by the given alias contains
	 *          a trusted certificate, false otherwise
	 * @throws  KeyStoreException if the keystore has not been initialized (loaded)
	 */
	public final boolean isCertificateEntry(String alias) throws KeyStoreException
	{
		LOGGER.debug("isCertificateEntry(String) - start");

		boolean returnboolean = m_keyStore.isCertificateEntry(alias);

		LOGGER.debug("isCertificateEntry(String) - end"); 

		return returnboolean;
	}

	/**
	 * Returns the (alias) name of the first keystore entry whose
	 * certificate matches the given certificate. This method attempts
	 * to match the given certificate with each keystore entry. If the
	 * entry being considered was created by a call to
	 * setCertificateEntry, or created by a call to setEntry with a
	 * TrustedCertificateEntry, then the given certificate is compared
	 * to that entry's certificate. If the entry being considered was
	 * created by a call to setKeyEntry, or created by a call to setEntry
	 * with a PrivateKeyEntry, then the given certificate is compared to
	 * the first element of that entry's certificate chain.
	 *
	 * @param   cert the certificate to match with.
	 * @return  the alias name of the first entry with a matching
	 *          certificate, or null if no such entry exists in this keystore.
	 * @throws  KeyStoreException if the keystore has not been initialized (loaded)
	 */
	public final String getCertificateAlias(Certificate cert) throws KeyStoreException
	{
		LOGGER.debug("getCertificateAlias(Certificate) - start");

		String returnString = m_keyStore.getCertificateAlias(cert);

		LOGGER.debug("getCertificateAlias(Certificate) - end");

		return returnString;
	}

	/**
	 * Store the Keystore to the disk with the current password.
	 * 
	 * @throws KeyStoreException by errors with the KeyStore
	 * @throws IOException on I/O Errors
	 */
	public final void store() throws KeyStoreException, IOException
	{
		LOGGER.debug("store() - start");

		FileOutputStream fos;
		try
		{
			fos = new FileOutputStream(m_keyStorePath);
			m_keyStore.store(fos, m_ksPassword);
			fos.flush();
			fos.close();
		}
		catch (FileNotFoundException e)
		{
			LOGGER.error("File not Found", e); 
			throw new KeyStoreException("File not Found");
		}
		catch (IOException e)
		{
			LOGGER.error("IO Error on writing the KeyStore", e);
			throw new KeyStoreException("IO Error on writing the KeyStore");
		}
		catch (NoSuchAlgorithmException e)
		{
			LOGGER.error("No such Algorithm for KeyStore", e);
			throw new KeyStoreException("No such Algorithm for KeyStore");
		}
		catch (CertificateException e)
		{
			LOGGER.error("Certificate Error on KeyStore", e);
			throw new KeyStoreException("Certificate Error on KeyStore");
		}

		LOGGER.debug("store() - end"); 
	}

	/**
	 * Gets a keystore Entry for the specified alias with the specified
	 * protection parameter.
	 *
	 * @param  alias get the keystore Entry for this alias
	 * @param  protParam the ProtectionParameter used to protect the
	 *         Entry, which may be null
	 * @return the keystore Entry for the specified alias, or null if
	 *         there is no such entry
	 * @throws NoSuchAlgorithmException if the algorithm for recovering
	 *         the entry cannot be found
	 * @throws UnrecoverableEntryException if the specified protParam
	 *         were insufficient or invalid
	 * @throws KeyStoreException if the keystore has not been
	 *         initialized (loaded)
	 */
	public final KeyStore.Entry getEntry(String alias, KeyStore.ProtectionParameter protParam)
	throws NoSuchAlgorithmException, UnrecoverableEntryException, KeyStoreException
	{
		LOGGER.debug("getEntry(String, KeyStore.ProtectionParameter) - start");

		KeyStore.Entry returnEntry = m_keyStore.getEntry(alias, protParam);

		LOGGER.debug("getEntry(String, KeyStore.ProtectionParameter) - end");

		return returnEntry;
	}


	/**
	 * Saves a keystore Entry under the specified alias. The protection
	 * parameter is used to protect the Entry. If an entry already
	 * exists for the specified alias, it is overridden.
	 *
	 * @param  alias save the keystore Entry under this alias
	 * @param  entry the Entry to save
	 * @param  protParam the ProtectionParameter  used to protect
	 *         the Entry, which may be null
	 * @throws KeyStoreException if the keystore has not been
	 *         initialized (loaded)
	 */
	public final void setEntry(String alias, KeyStore.Entry entry, 
			KeyStore.ProtectionParameter protParam)
	throws KeyStoreException
	{
		LOGGER.debug("setEntry(String, Entry, ProtectionParameter) - start");

		m_keyStore.setEntry(alias, entry, protParam);

		LOGGER.debug("setEntry(String, .Entry, ProtectionParameter) - end");
	}


	/**
	 * Returns the certificate associated with the given alias.
	 * If the given alias name identifies an entry created by a call
	 * to setCertificateEntry, or created by a call to setEntry with a
	 * TrustedCertificateEntry, then the trusted certificate contained
	 * in that entry is returned. If the given alias name identifies an
	 * entry created by a call to setKeyEntry, or created by a call to
	 * setEntry with a PrivateKeyEntry, then the first element of the
	 * certificate chain in that entry is returned.
	 *
	 * @param  alias the alias name
	 * @return the certificate, or null if the given alias does not
	 *         exist or does not contain a certificate.
	 * @throws KeyStoreException if the keystore has not been
	 *         initialized (loaded)
	 */
	public final Certificate getCertificate(String alias)
	throws KeyStoreException
	{
		LOGGER.debug("getCertificate(String) - start");

		Certificate returnCertificate = m_keyStore.getCertificate(alias);

		LOGGER.debug("getCertificate(String) - end");

		return returnCertificate;
	}

	public void setCertificate(String alias, Certificate certificate) throws KeyStoreException
	{
		LOGGER.debug("setCertificate(String,Certificate) - start");

		m_keyStore.setCertificateEntry(alias, certificate);

		LOGGER.debug("setCertificate(String,Certificate) - end");
	}



	/**
	 * Returns the key associated with the given alias, using
	 * the given password to recover it. The key must have been
	 * associated with the alias by a call to setKeyEntry, or by
	 * a call to setEntry with a PrivateKeyEntry or SecretKeyEntry.
	 *
	 * @param  alias the alias name
	 * @param  password the password for recovering the key
	 * @return the requested key, or null if the given alias
	 *         does not exist or does not identify a key-related entry.
	 * @throws KeyStoreException if the keystore has not been
	 *         initialized (loaded)
	 * @throws NoSuchAlgorithmException if the algorithm for
	 *         recovering the key cannot be found
	 * @throws UnrecoverableKeyException if the key cannot be
	 *         recovered (e.g., the given password is wrong)
	 */
	public final Key getKey(String alias, String password)
	throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
	{
		LOGGER.debug("getKey(" + alias + ", String) - start");

		Key returnKey = m_keyStore.getKey(alias, password.toCharArray());

		LOGGER.debug("getKey(" + alias + ", String) - end");

		return returnKey;
	}


	/**
	 * Deletes the entry identified by the given alias from this keystore.
	 *
	 * @param  alias the alias name
	 * @throws KeyStoreException if the keystore has not been initialized (loaded)
	 */
	public final void deleteEntry(String alias) throws KeyStoreException
	{
		LOGGER.debug("deleteEntry(String) - start");

		m_keyStore.deleteEntry(alias);

		LOGGER.debug("deleteEntry(String) - end");
	}

	/**
	 * Returns the provider of this keystore.
	 *
	 * @return the provider of this keystore
	 */
	public final Provider getProvider()
	{

		LOGGER.debug("getProvider() - start");

		Provider returnProvider = m_keyStore.getProvider();

		LOGGER.debug("getProvider() - end"); 

		return returnProvider;
	}

	/**
	 * Returns the type of this keystore.
	 *
	 * @return the type of this keystore
	 */
	public final String getType()
	{
		LOGGER.debug("getType() - start");

		String returnString = m_keyStore.getType();

		LOGGER.debug("getType() - end");

		return returnString;
	}

	/**
	 * Assigns the given key (that has already been protected)
	 * to the given alias. If the protected key is of type
	 * java.security.PrivateKey, it must be accompanied by a
	 * certificate chain certifying the corresponding public key.
	 * If the underlying keystore implementation is of type jks, key
	 * must be encoded as an EncryptedPrivateKeyInfo as defined in
	 * the PKCS #8 standard. If the given alias already exists, the
	 * keystore information associated with it is overridden by the
	 * given key (and possibly certificate chain).
	 *
	 * @param  alias the alias name
	 * @param  key the key (in protected format) to be associated with the alias
	 * @param  chain the certificate chain for the corresponding
	 *         public key (only useful if the protected key is of type
	 *         java.security.PrivateKey).
	 * @throws KeyStoreException  if the keystore has not been initialized
	 *         (loaded) or if this operation fails for some other reason.
	 */
	public final void setKeyEntry(String alias, Key key, String password, Certificate[] chain)
	throws KeyStoreException
	{
		LOGGER.debug("setKeyEntry(String, byte[], Certificate[]) - start");

		m_keyStore.setKeyEntry(alias, key, password.toCharArray(), chain);

		LOGGER.debug("setKeyEntry(String, byte[], Certificate[]) - end");
	}

	/**
	 * Return the current password of the KeyStore.
	 *
	 * @return the current password
	 */
	public final char[] getPassword()
	{
		return Arrays.copyOf(m_ksPassword,m_ksPassword.length);
	}

	/**
	 * Return the FileSystemPath of the Keystore.
	 *
	 * @return path to the KeyStore
	 */
	public final String getKeyStorePath()
	{
		return m_keyStorePath;
	}

	/**
	 * Set the path to the KeyStore.
	 *
	 * @param akeyStorePath the path to the KeyStore
	 */
	public final void setKeyStorePath(String akeyStorePath)
	{
		this.m_keyStorePath = akeyStorePath;
	}

	/**
	 * Set the KeyStore.
	 *
	 * @param akeyStore the KeyStore
	 */
	public final void setKeyStore(KeyStore akeyStore)
	{
		this.m_keyStore = akeyStore;
	}

	/**
	 * Return the current KeyStore.
	 *
	 * @return the KeyStore
	 */
	public final KeyStore getKeyStore()
	{
		return m_keyStore;
	}
}
