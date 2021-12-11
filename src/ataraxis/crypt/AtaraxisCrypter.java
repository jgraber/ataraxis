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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Constructor;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import ataraxis.util.FileList;



/**
 * AtaraxisCrypter is the main encrypt and decrypt class of the AtaraxiS project.
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.1
 *
 */
public final class AtaraxisCrypter {
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisCrypter.class);

	/**
	 * The field for the KeyStoreHandler.
	 */
	private KeyStoreHandler m_keyStoreHandler;

	/**
	 * The field for the Propterties functionality.
	 */
	private Properties m_props = null;

	/**
	 * The field for the configuration directory.
	 */
	private static final String CONFIG_DIR = System.getProperty("user.dir")
	+ "/application_data/config";

	/**
	 * The field for the properties file.
	 */
	private static final String PROPS_FILE = "ataraxis.properties";

	/**
	 * The field for the Buffersize for encryption and decryption.
	 */
	private static final int BUFFER_SIZE = 2048;

	/**
	 * The field for the Key Alias.
	 */
	private String m_keyAlias;

	/**
	 * The field for the Key Password.
	 */
	private String m_keyPassword;




	/**
	 * AtaraxisCrypter with the keyStorePath as KeyStore, a 
	 * KeyStore password and a boolean for creating a KeyStore.
	 * 
	 * Needed Directories for the KeyStore will be createt, if this is allowed.
	 *
	 * @param keyStoreFile Path and File for the KeyStore
	 * @param keyStorePassword Password for the KeyStore
	 * @param createKS true if KeyStore should be created, false otherwise
	 * @throws IOException by errors with the I/O or wrong Password
	 * @throws KeyStoreException by errors with the KeyStore
	 * @throws JurisdictionPolicyError if the PolicyFile ist not unrestricted enough
	 */
	public AtaraxisCrypter(File keyStoreFile, char[] keyStorePassword, boolean createKS)
	throws IOException, KeyStoreException
	{
		LOGGER.debug("AtaraxisCrypter(String, String, boolean) - start"); 
		LOGGER.debug("Create " + keyStoreFile.getAbsolutePath() + "? " + createKS);

		Security.addProvider(new BouncyCastleProvider());

		m_props = new Properties();
		//m_props.load(new FileInputStream(CONFIG_DIR + "/" + PROPS_FILE));

		File defaultProperties = new File(CONFIG_DIR + "/" + PROPS_FILE);
		String pathSystemProperties = System.getProperty("ATARAXIS_PROPERTIES_FILE","");

		if(pathSystemProperties.length() > 0)
		{
			File propertiesFromSystem = new File(pathSystemProperties);

			if(propertiesFromSystem.exists())
			{
				m_props.load(new FileInputStream(propertiesFromSystem));
			}
		}
		else if(defaultProperties.exists())
		{
			m_props.load(new FileInputStream(defaultProperties));
		}

		m_keyAlias = m_props.getProperty("ATARAXIS.KEY_ALIAS", "ataraxisMainKey");
		m_keyPassword = m_props.getProperty("ATARAXIS.KEY_PW","sqHo4m"); 
		final String kshClass = m_props.getProperty("ATARAXIS.KEYSTORE_CLASS", "ataraxis.crypt.UBERKeyStoreHandler");
		final String ksCreator = m_props.getProperty("ATARAXIS.KEYSTORE_CREATOR_CLASS", "ataraxis.crypt.UBERKeyStoreCreator");

		if(createKS)
		{
			if (!keyStoreFile.exists())
			{
				final File keyStoreDir = new File(keyStoreFile.getParent());
				LOGGER.debug("Create directory " + keyStoreDir.getAbsolutePath());
				keyStoreDir.mkdirs();
			}
			else
			{
				throw new KeyStoreException("KS already exists");
			}
		}

		// Always use AES to encrypt/decrypt
		final SecretKeyCreator kh = new AESKeyCreator();

		if (keyStoreFile.exists() && keyStoreFile.canRead())
		{
			LOGGER.debug("KeyStore exist an is readable");

			m_keyStoreHandler = getKeyStoreHandler(kshClass, keyStoreFile, keyStorePassword, kh);
		}
		else if (keyStoreFile.exists() && !keyStoreFile.canRead())
		{
			LOGGER.error("Access denied on KeyStore by Filesystem");
			throw new KeyStoreException("Access denied on KeyStore by Filesystem");
		}
		else if (!keyStoreFile.exists() && createKS)
		{
			LOGGER.debug("KeyStore does not exist and creation is allowed");

			createKeyStore(ksCreator, keyStoreFile, keyStorePassword);
			m_keyStoreHandler = getKeyStoreHandler(kshClass, keyStoreFile, keyStorePassword, kh);
		}
		else
		{
			LOGGER.error("KeyStore does not exist and creation is NOT allowed");
			throw new KeyStoreException("KeyStore does not exist an creation is not allowed!");
		}

		LOGGER.debug("AtaraxisCrypter(String, boolean) - end");
	}

	private KeyStoreHandler getKeyStoreHandler(String kshClass, File keyStoreFile, char[] keyStorePassword, SecretKeyCreator kh) throws KeyStoreException
	{
		KeyStoreHandler ksh = null;

		try
		{
			final Class<?> kshelper = Class.forName(kshClass);
			final Constructor<?> constructor = kshelper.getConstructor(new Class[] {File.class, char[].class});
			ksh = (KeyStoreHandler)
			constructor.newInstance(keyStoreFile, keyStorePassword);

			if (!ksh.containsAlias(m_keyAlias))
			{
				ksh.setEntry(m_keyAlias,
						new KeyStore.SecretKeyEntry(kh.createSecretKey()),
						new KeyStore.PasswordProtection(m_keyPassword.toCharArray()));
				ksh.store();
			}
		}
		catch (Exception e) 
		{
			throw new KeyStoreException("InvocationTargetException");
		}

		return ksh;
	}


	private void createKeyStore(String ksCreator, File keyStoreFile, char[] keyStorePassword) throws KeyStoreException
	{
		try
		{
			// Load dynamic class and execute Method createKeyStore
			final Class<?> ksCreatorHelp = Class.forName(ksCreator);
			final Object ksCratorObject = ksCreatorHelp.getDeclaredConstructor().newInstance();
			final KeyStoreCreator ksCrator = (KeyStoreCreator) ksCratorObject;
			ksCrator.createKeyStore(keyStoreFile, keyStorePassword);
		}
		catch (Exception e) 
		{
			throw new KeyStoreException("Class for KeyStoreHandler not found!");
		}
	}


	/**
	 * encrypts the inFile to the outFile.
	 *
	 * @param inFile  Path to the original File
	 * @param outFile  Path where the encrypted File should be saved
	 * @param zipIt should the inFile be zipped?
	 * @throws FileNotFoundException if the inFile does not exist
	 * @throws CryptoMethodError  by errors on crypto-functionalities
	 */
	public final void encryptFile(File inFile, File outFile, boolean zipIt)
	throws FileNotFoundException, CryptoMethodError
	{
		LOGGER.debug("encryptFile(String, String, boolean) - start"); 
		LOGGER.debug("inFile: "+ inFile);
		LOGGER.debug("outFile: "+ outFile);

		final boolean outFileCreated = !outFile.exists();
		boolean encryptFailed = false;

		if(inFile.isDirectory() || zipIt)
		{
			// make the zip File

			if(outFile.getName().endsWith(".ac"))
			{
				outFile = new File(outFile.getAbsolutePath().concat("z"));
				LOGGER.debug("Change Name of Outfile to " + outFile.getAbsolutePath());
			}
			else if( ! outFile.getName().endsWith(".acz"))
			{
				outFile = new File(outFile.getAbsolutePath().concat(".acz"));
				LOGGER.debug("*** Change Name of Outfile to " + outFile.getAbsolutePath());
			}
			LOGGER.debug("Zip output name is " + outFile);

			final File startFile = inFile;
			final FileList fileList = new FileList();
			final List<File> paths = fileList.getFileList(inFile.getAbsolutePath());
			final byte[] buf = new byte[BUFFER_SIZE];
			OutputStream cout;
			ZipOutputStream out;

			/* The File or Directory named by filePath is the top-Entry of the 
			 * Zip-File. For the ZipFile-Entry (the Name of the File/Directory
			 * in the Zip-File) will always the Path of the filePath-Parent
			 * removed from the path of the entry.
			 */        
			final String pathSubstract = startFile.getParentFile().getAbsolutePath()+File.separator;
			final int substractLength = pathSubstract.length();

			try 
			{
				cout = encryptOutputStream(outFile);
				out = new ZipOutputStream(cout);
				out.setLevel(9); // max Level of compression

				// Compress the files
				for (File f : paths) 
				{
					if(f.isFile())
					{
						final FileInputStream in = new FileInputStream(f.getAbsolutePath());
						final String nameInZip = f.getAbsolutePath().substring(substractLength);
						final ZipEntry entry = new ZipEntry(nameInZip);
						entry.setTime(f.lastModified());
						out.putNextEntry(entry);

						// Transfer bytes from the file to the ZIP file
						int len;
						while((len = in.read(buf)) > 0) {
							out.write(buf, 0, len);
						}

						// Complete the entry
						out.closeEntry();
						in.close();
					}
					else 
					{
						final String nameInZip = f.getAbsolutePath().substring(pathSubstract.length());

						// Directories in Zip-files are marked with a '/' at 
						// the End of the Filename
						final ZipEntry entry = new ZipEntry(nameInZip+"/");
						out.putNextEntry(entry);
					}
				}

				// Complete the ZIP file
				out.close();
				cout.close();

			} 
			catch (IOException e) 
			{
				encryptFailed = true;
				LOGGER.error("IOException", e);
				throw new CryptoMethodError("IOException");
			}

		}
		else 
		{
			FileInputStream fis = null;
			OutputStream fout = null;

			try
			{
				fis = new FileInputStream(inFile);
				fout = encryptOutputStream(outFile);

				copyStreams(fis, fout);

			}
			catch (IOException e)
			{
				encryptFailed = true;
				LOGGER.error("IOException", e);
				throw new CryptoMethodError("IOException");
			}
			finally
			{
				try 
				{
					if(fout != null) {
						fout.flush();
						fout.close();
					}
					if(fis != null) {
						fis.close();
					}
					outFile.setLastModified(inFile.lastModified());
				} 
				catch (IOException e) 
				{
					LOGGER.error("IOException on close(): " + e.getMessage());
				}
				if(encryptFailed && outFileCreated)
				{
					boolean deleteWasOk = false;
					LOGGER.info("Crypto error. Failed to create File " 
							+ outFile.getAbsolutePath());
					deleteWasOk =  outFile.delete();
					if(deleteWasOk)
					{
						LOGGER.info("File " + outFile.getAbsolutePath()
								+ " deleted");
					}
					else
					{
						LOGGER.error("clearing of file " 
								+ outFile.getAbsolutePath()+" failed");
					}
				}
			}
		}

		LOGGER.debug("encryptFile(String, String, boolean) - end");
	}

	/**
	 * encrypts the inFile to the outFile.
	 *
	 * @param inFile  Path to the original File
	 * @param outFile  Path where the encrypted File should be saved
	 * @throws FileNotFoundException if the inFile does not exist
	 * @throws CryptoMethodError  by errors on crypto-functionalities
	 */
	public final void encryptFile(File inFile, File outFile)
	throws FileNotFoundException, CryptoMethodError
	{
		encryptFile(inFile, outFile, false);
	}


	/**
	 * decrypts the inFile to the outFile.
	 * @param inFile   Path to the decrypted File
	 * @param outFile   Path where the plaintext File should be saved
	 * @throws FileNotFoundException  if the inFile does not exist
	 * @throws CryptoMethodError  by errors on crypto-functionalities
	 * @throws ZipException if the file to unzip is not a zip file
	 */
	public final void decryptFile(File inFile, File outFile)
	throws FileNotFoundException, CryptoMethodError, ZipException
	{
		decryptFile(inFile, outFile, true);
	}

	/**
	 * decrypts the inFile to the outFile. If the input file ends with *.acz and
	 * unzipIt is true, it try to unzip it. 
	 * 
	 * @param inFile   Path to the decrypted File
	 * @param outFile   Path where the plaintext File should be saved
	 * @param unzipIt should the File be unzipped, if it ends with acz
	 * @throws FileNotFoundException  if the inFile does not exist
	 * @throws CryptoMethodError by errors on crypto-functionalities
	 * @throws ZipException if the file to unzip is not a zip file
	 */
	public final void decryptFile(File inFile, File outFile, boolean unzipIt)
	throws FileNotFoundException, CryptoMethodError, ZipException
	{
		LOGGER.debug("decryptFile(String, String) - start");
		LOGGER.debug("inFile: "+ inFile);
		LOGGER.debug("outFile: "+ outFile);
		boolean createdOutputDir = false;
		final boolean outFileCreated = ! outFile.exists();
		boolean decryptFailed = false;


		if(inFile.getName().endsWith(".acz") && unzipIt)
		{

			if(!outFile.exists())
			{

				createdOutputDir = outFile.mkdirs();
				LOGGER.debug("output Folder createt: "+createdOutputDir);
			}
			else if(! (outFile.isDirectory()))
			{
				LOGGER.error("Output File is not a directory");
				throw new FileNotFoundException("Output File is not a directory");

			}


			InputStream in;
			FileOutputStream out;
			ZipEntry entry;

			final byte[] input_buffer = new byte[BUFFER_SIZE];
			int len = 0;
			int nbOfEntry = 0;

			try 
			{
				in = decryptInputStream(inFile);
				final ZipInputStream zin = new ZipInputStream(in);

				while((entry=zin.getNextEntry())!= null) 
				{
					nbOfEntry++;

					if (entry.isDirectory()) 
					{
						(new File(outFile,entry.getName())).mkdirs();
					}
					else
					{
						// Entry is a File
						final File output_file = new File (outFile, entry.getName());
						if(! output_file.getParentFile().exists())
						{
							(new File(output_file.getParent())).mkdirs();
						}

						out = new FileOutputStream(output_file);
						final BufferedOutputStream destination = 
							new BufferedOutputStream(out, BUFFER_SIZE);

						while ((len = zin.read(input_buffer, 0, BUFFER_SIZE)) != -1)
						{
							destination.write(input_buffer, 0, len);
						}

						destination.flush(); 
						out.close();
						output_file.setLastModified(entry.getTime());
					}
				}

				zin.close();
				in.close();

				if(nbOfEntry == 0)
				{
					LOGGER.error("Onput file is not a compressed file");
					if(createdOutputDir)
					{
						boolean deleteWasOk = outFile.delete();
						LOGGER.debug("output Folder deletet: " + deleteWasOk);
					}        				

					throw new ZipException("Output file is not a compressed file");                	
				}

			}  
			catch (ZipException e) 
			{
				decryptFailed = true;
				LOGGER.error("ZipException", e);
				throw new ZipException("ZipException\n" + e.getMessage());
			}
			catch (IOException e) 
			{
				decryptFailed = true;
				LOGGER.error("IOException", e);
				throw new CryptoMethodError("IOException",e);
			}
		}
		else
		{
			LOGGER.debug("START encrypt from "+inFile.getAbsolutePath() +" to "+outFile.getAbsolutePath());
			InputStream fis = null;
			FileOutputStream fout = null;
			try
			{
				fis = decryptInputStream(inFile);
				fout = new FileOutputStream(outFile);

				copyStreams(fis, fout);

			}
			catch (FileNotFoundException e)
			{
				decryptFailed = true;
				LOGGER.error("Could not write to file, maybe its a directory");
				throw new FileNotFoundException(e.getMessage());
			}
			catch (IOException e)
			{
				decryptFailed = true;
				LOGGER.error("IOException", e);
				throw new CryptoMethodError("IOException");
			}
			finally
			{
				try {
					if(fout != null)
					{
						fout.flush();
						fout.close();
					}
					fis.close();
					outFile.setLastModified(inFile.lastModified());
				} 
				catch (IOException e) 
				{
					LOGGER.error("IOException on close(): " +e.getMessage());
				}
				if(decryptFailed && outFileCreated)
				{
					boolean deleteWasOk = false;
					LOGGER.info("Crypto error. Failed to create File " 
							+ outFile.getAbsolutePath());
					deleteWasOk =  outFile.delete();
					if(deleteWasOk)
					{
						LOGGER.info("File " + outFile.getAbsolutePath()
								+ " deleted");
					}
					else
					{
						LOGGER.error("clearing of file " 
								+ outFile.getAbsolutePath()+" failed");
					}	
				}
			}
		}
		LOGGER.debug("decryptFile(String, String) - end");
	}




	/**
	 * Decrypt an InputStream on-the-fly
	 * 
	 * @param InputFile Path to the input file
	 * @return the InputStream to the input file
	 * @throws IOException by problems with IO (inkl. KeyStore)
	 * @throws FileNotFoundException if the OutputFile does not exist
	 */
	public final InputStream decryptInputStream(File InputFile)
	throws IOException, FileNotFoundException 
	{
		LOGGER.debug("decryptInputStream(String) - start");
		if(!InputFile.exists())
		{
			LOGGER.error("InputFile does not exist - missing " + InputFile.getAbsolutePath());
			throw new FileNotFoundException("InputFile does not exist");
		}

		ACDecryptInputStream acDecryiptInputStream = null;
		try {
			acDecryiptInputStream = new ACDecryptInputStream(InputFile, 
					(SecretKey)m_keyStoreHandler.getKey(m_keyAlias,	m_keyPassword));
		}		
		catch (UnrecoverableKeyException e)
		{
			LOGGER.error("UnrecoverableKeyException", e);
			throw new IOException("UnrecoverableKeyException");
		}
		catch (KeyStoreException e)
		{
			LOGGER.error("KeyStoreException", e);
			throw new IOException("KeyStoreException");
		} 
		catch (NoSuchAlgorithmException e)
		{
			LOGGER.error("NoSuchAlgorithmException", e);
			throw new IOException("NoSuchAlgorithmException");
		}

		LOGGER.debug("decryptInputStream(String) - end");
		return  acDecryiptInputStream.getDecriptInputStream();
	}


	/**
	 * Encrypt an OutputStream on-the-fly
	 *
	 * @param OutputFile Path to the output file
	 * @return the OutputStream to the output file
	 * @throws FileNotFoundException if the OutputFile does not exist
	 * @throws IOException by problems with IO (inkl. KeyStore)
	 */
	public final OutputStream encryptOutputStream(File OutputFile) 
	throws FileNotFoundException, IOException
	{
		LOGGER.debug("encryptOutputStream(String) - start");


		ACEncryptOutputStream acEncryptOS = null;
		try {
			acEncryptOS = new ACEncryptOutputStream(OutputFile, 
					(SecretKey)m_keyStoreHandler.getKey(m_keyAlias, m_keyPassword));
		} 
		catch (UnrecoverableKeyException e)
		{
			LOGGER.error("UnrecoverableKeyException", e);
			throw new IOException("UnrecoverableKeyException");
		} 
		catch (KeyStoreException e)
		{
			LOGGER.error("KeyStoreException", e);
			throw new IOException("KeyStoreException");
		}
		catch (NoSuchAlgorithmException e)
		{
			LOGGER.error("NoSuchAlgorithmException", e);
			throw new IOException("NoSuchAlgorithmException");
		}

		LOGGER.debug("encryptOutputStream(String) - end");
		return  acEncryptOS.getEncryptOutputStream();
	}



	/**
	 * Method to change the password of the KeyStore.
	 * 
	 * @param  oldPassword the current password
	 * @param  newPassword the new password
	 * @return true if successful
	 * @throws KeyStoreException by errors with the KeyStore
	 * @throws IOException by I/O errors
	 */
	public boolean changePassword(char[] oldPassword, char[] newPassword)
	throws KeyStoreException, IOException
	{
		return m_keyStoreHandler.changePassword(oldPassword, newPassword);
	}


	/**
	 * Return the FileSystemPath of the Keystore.
	 *
	 * @return path to the KeyStore
	 */
	public final String getKeyStorePath()
	{
		return m_keyStoreHandler.getKeyStorePath();
	}

	/**
	 * Check if the JurisdictionPolicy is unrestircted enough for the keys to 
	 * use. If not, it throws a JurisdictionPolicyError Exception.
	 *
	 * @param keyCreator The KeyCreator for the Key
	 * @throws JurisdictionPolicyError if the PolicyFile ist not unrestricted
	 */
	public static void checkJurisdictionPolicy(SecretKeyCreator keyCreator) 
	throws JurisdictionPolicyError
	{

		Cipher cipherTest;
		SecretKey testKey = null;
		try 
		{
			testKey = keyCreator.createSecretKey();

			cipherTest = Cipher.getInstance("AES", "BC");
			cipherTest.init(Cipher.ENCRYPT_MODE,testKey);
		}  
		catch (InvalidKeyException e) 
		{
			LOGGER.error("InvalidKeyException", e);
			throw new JurisdictionPolicyError("unrestricted PolicyFiles needet");
		}
		catch (Exception e) 
		{
			LOGGER.error(e.getMessage(), e);
		}
		LOGGER.debug("JuristicationTest was ok");
	}



	private void copyStreams(InputStream fromStream, OutputStream toStream) throws IOException
	{
		byte[] buf = new byte[BUFFER_SIZE];
		int len;
		while ((len = fromStream.read(buf)) > 0)
		{
			toStream.write(buf, 0, len);
		}
	}
}
