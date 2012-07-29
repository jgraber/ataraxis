/* ----------------------------------------------------------------------------
 * Copyright 2007 - 2010 Johnny Graber & Andreas Muedespacher
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

import javax.crypto.SecretKey;

import junit.framework.Assert;
import mockit.Mockit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.TestingHelper;
import ataraxis.crypt.AESKeyCreator;
import ataraxis.crypt.AESKeyCreatorTest;
import ataraxis.crypt.AtaraxisCrypter;
import ataraxis.crypt.CryptoMethodError;
import ataraxis.crypt.JurisdictionPolicyError;
import ataraxis.crypt.KeyStoreHandler;
import ataraxis.crypt.NotImplementedException;
import ataraxis.crypt.UBERKeyStoreCreator;
import ataraxis.misc.AtaraxisHashCreator;
import ataraxis.misc.HashingDigest;



/**
 * Unit Test for AtaraxisCrypter
 * @author Johnny Graber
 *
 */
public class AtaraxisCrypterTest
{

	private static AtaraxisCrypter s_ac = null;
	private static final Logger logger = Logger.getLogger(AESKeyCreatorTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	private static final String TEST_DIR_DATA = TEST_DIR + "/testrun/ac_test_data";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	private static final String FILE_SMALL = TEST_DIR + "/performance.xls";
	private static File inputTestFile;
	private static String s_ksPath;
	private static String s_ksPassword = "The starter Password";
	private static String ZIP_DIR = TEST_DIR_DATA+"/ZIP";

	/**
	 * Run once before this test class and configure the Logger
	 * @throws JurisdictionPolicyError 
	 * @throws IOException 
	 * @throws KeyStoreException 
	 */
	@BeforeClass 
	public static void initClass() throws KeyStoreException, IOException, JurisdictionPolicyError
	{

		PropertyConfigurator.configure(LOG_PROPS_FILE); 
		
		TestingHelper.deleteDir(new File(TEST_DIR_DATA));
				
		(new File(TEST_DIR_DATA)).mkdirs();
		s_ksPath = TEST_DIR_DATA+"/main_ok.ks";
		s_ac = new AtaraxisCrypter(new File(s_ksPath),s_ksPassword.toCharArray(),true);
		inputTestFile = new File(TEST_DIR_DATA+"/InputStreamTestFile");
		inputTestFile.createNewFile();


		// Create Data for Zip
		(new File(ZIP_DIR)).mkdirs();
		(new File(ZIP_DIR+"/1")).createNewFile();
		(new File(ZIP_DIR+"/2")).createNewFile();
		(new File(ZIP_DIR+"/3")).mkdirs();
		(new File(ZIP_DIR+"/3/4")).createNewFile();

	}

	/**
	 * Runs once after the test class and cleanup the files
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception 
	{
		TestingHelper.deleteDir(new File (TEST_DIR_DATA));
	}

	/**
	 * Test the AtaraxisCrypter when KS does not exist and Create KS is on true. 
	 *
	 * Target: create a new KeyStore.
	 */
	@Test
	public void acNewCreateTrue() {

		String ksPath = TEST_DIR_DATA+"/ACnewCreateTrue.ks";

		// Check that no KS with this name exist. When then try to delete. If this does not
		// work, then mark this test as failed
		File newKS = new File(ksPath);
		if(newKS.exists()) newKS.delete();

		if(newKS.exists())
		{
			fail("KS exist on" + ksPath);
		}
		else
		{
			try 
			{
				AtaraxisCrypter ac = new AtaraxisCrypter(new File(ksPath),"ThisIsMyPass".toCharArray(),true);
				assertNotNull(ac);
			} 
			catch (KeyStoreException e) 
			{
				logger.fatal(e.getMessage());
				fail(e.getMessage());
			} 
			catch (IOException e) 
			{
				logger.fatal(e.getMessage());
				fail(e.getMessage());
			}
		}
	}

	/**
	 * Test the AtaraxisCrypter when KS does not exist and Create KS is on false. 
	 *
	 * Target: throw an KS Exception, because it can't do anything
	 */
	@Test
	public void acNewCreateFalse() {

		String ksPath = TEST_DIR_DATA+"/ACnewCreateFalse.ks";
		if((new File(ksPath)).exists())
		{
			fail("KS exist on" + ksPath);
		}
		else
		{
			try 
			{
				AtaraxisCrypter ac = new AtaraxisCrypter(new File(ksPath),"ThisIsMyPass".toCharArray(),false);
				fail("Should by null and not create " + ac);
			} 
			catch (KeyStoreException e) 
			{
				assertEquals("KeyStore does not exist an creation is not allowed!",e.getMessage());
			} 
			catch (IOException e) 
			{
				fail("IOException");
			}				

		}
	}

	/**
	 * Test the AtaraxisCrypter when KS does exist and Create KS is on true. 
	 *
	 * Target: throw Exception that KS exist alredy
	 */
	@Test
	public void acOldCreateTrue() {

		String ksPath = TEST_DIR_DATA+"/ACnewCreateTrue.ks";

		// Check that no KS with this name exist. When then try to delete. If this does not
		// work, then mark this test as failed
		File newKS = new File(ksPath);


		if(!newKS.exists())
		{
			fail("KS does not exist on" + ksPath);
		}
		else
		{
			try 
			{
				AtaraxisCrypter ac = new AtaraxisCrypter(new File(ksPath),"ThisIsMyPass".toCharArray(),true);
				fail("KS should have thrown a KeyStoreException for " + ac);
			} 
			catch (KeyStoreException e) 
			{
				assertEquals(e.getMessage(), "KS already exists");
			} 
			catch (IOException e) 
			{
				fail("IOException");
			}
		}
	}

	/**
	 * Test the AtaraxisCrypter when KS does exist and Create KS is on false. 
	 *
	 * Target: throw an KS Exception, because it can't do anything
	 */
	@Test
	public void acOldCreateFalse() {

		String ksPath = TEST_DIR_DATA+"/ACnewCreateTrue.ks";
		if(!(new File(ksPath)).exists())
		{
			fail("KS does not exist on" + ksPath);
		}
		else
		{
			try 
			{
				AtaraxisCrypter ac = new AtaraxisCrypter(new File(ksPath),"ThisIsMyPass".toCharArray(),false);
				assertNotNull(ac);
			} 
			catch (KeyStoreException e) 
			{
				fail("KeyStoreException");
			} 
			catch (IOException e) 
			{
				fail("IOException");
			}				

		}
	}


	/**
	 * Test the AtaraxisCrypter when KS does exist and Create KS is on false. 
	 *
	 * Target: throw an KS Exception, because it can't do anything
	 */
	@Test
	public void acFromAJKS() 
	{

		String ksPath = TEST_DIR_DATA+"/ACfromAJKS.ks";
		String ks_Password = "ThisIsMyPass";
		try
		{
			KeyStore javaKS  = KeyStore.getInstance("UBER", "BC");
			javaKS.load(null, ks_Password.toCharArray());
			javaKS.store(new FileOutputStream(ksPath), ks_Password.toCharArray());
		}
		catch (Exception e)
		{
			fail("Fail on JKS creation");
		}

		if(!(new File(ksPath)).exists())
		{
			fail("KS does not exist on" + ksPath);
		}
		else
		{
			try 
			{
				AtaraxisCrypter ac = new AtaraxisCrypter(new File(ksPath), ks_Password.toCharArray(),false);
				assertNotNull(ac);
			} 
			catch (KeyStoreException e) 
			{
				fail("KeyStoreException");
			} 
			catch (IOException e) 
			{
				fail("IOException");
			}
		}
	}

	/**
	 * Test method for {
	 * @link ataraxis.crypt.AtaraxisCrypter#encryptFile(
	 * java.lang.String, java.lang.String, boolean)}.
	 * Encrypt a file and decrypt (do not zip it).
	 */
	@Test
	public void testCryptFileSMALL() 
	{
		try 
		{
			String fileEncrypted = TEST_DIR_DATA+"/smal_encryptedSMALL.ac";
			String fileDecrypted = TEST_DIR_DATA+"/smal_decryptedSMALL.dec";

			s_ac.encryptFile(new File(FILE_SMALL), new File(fileEncrypted),false);
			s_ac.decryptFile(new File(fileEncrypted), new File(fileDecrypted),false);

			AtaraxisHashCreator ahc = new AtaraxisHashCreator(HashingDigest.MD5);


			assertEquals(
					AtaraxisHashCreator.prettyPrintHash(ahc.createHashForFile(new File(FILE_SMALL))), 
					AtaraxisHashCreator.prettyPrintHash(ahc.createHashForFile(new File(fileDecrypted))));

		} 
		catch (Exception e) {
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}




	/**
	 * Test method for 
	 * {@link ataraxis.crypt.AtaraxisCrypter#encryptFile(
	 * File, File)} 
	 * and {@link ataraxis.crypt.AtaraxisCrypter#decryptFile(
	 * File, File)}.
	 * Encrypt a file, zip it and unzip it after decryption.
	 */
	@Test
	public void testCryptZipFile() 
	{
		try 
		{
			String fileEncrypted = TEST_DIR_DATA+"/smal_encryptedZip.acz";
			String fileDecrypted = TEST_DIR_DATA+"/B/";

			s_ac.encryptFile(new File(FILE_SMALL), new File(fileEncrypted), true);
			s_ac.decryptFile(new File(fileEncrypted), new File(fileDecrypted), true);

			AtaraxisHashCreator ahc = new AtaraxisHashCreator(HashingDigest.MD5);


			assertEquals(
					AtaraxisHashCreator.prettyPrintHash(ahc.createHashForFile(new File(FILE_SMALL))), 
					AtaraxisHashCreator.prettyPrintHash(ahc.createHashForFile(new File(fileDecrypted+(new File(FILE_SMALL).getName())))));

		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for 
	 * {@link ataraxis.crypt.AtaraxisCrypter#encryptFile(
	 * File, File)} 
	 * and {@link ataraxis.crypt.AtaraxisCrypter#decryptFile(
	 * File, File)}.
	 * Encrypt a directory, zip it and unzip it after decryption.
	 */
	@Test
	public void testCryptDirectory() 
	{
		try 
		{
			String fileEncrypted = TEST_DIR_DATA+"/b.acz";
			String fileDecrypted = TEST_DIR_DATA+"/C/";

			s_ac.encryptFile(new File(TEST_DIR_DATA+"/B/"), new File(fileEncrypted), true);
			s_ac.decryptFile(new File(fileEncrypted), new File(fileDecrypted), true);


			assertTrue(
					new File(TEST_DIR_DATA+"/C/B/"+(new File(FILE_SMALL).getName())).exists()
			);
		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}
	/**
	 * Test method for 
	 * {@link ataraxis.crypt.AtaraxisCrypter#encryptFile(
	 * File, File)} 
	 * and {@link ataraxis.crypt.AtaraxisCrypter#decryptFile(
	 * File, File)}.
	 * Encrypt a file and zip it, but do not unzip it after decryption.
	 */
	@Test
	public void testCryptNotUnZipFile() 
	{
		try 
		{
			s_ac.encryptFile(new File(TEST_DIR+"/performance.xls"), new File(TEST_DIR_DATA+"/performance2.xls.acz"),true);
			s_ac.decryptFile(new File(TEST_DIR_DATA+"/performance2.xls.acz"), new File(TEST_DIR_DATA+"/performance2.zip"),false);
		} 
		catch (Exception e) {
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}


	/**
	 * Test method for {@link 
	 * ataraxis.crypt.AtaraxisCrypter#changePassword(char[], 
	 * char[])}.
	 * Change password and try to open the KeyStore.
	 */
	@Test
	public void testChangePasswordOK() 
	{
		try 
		{
			String tmpPassword = "ein sehr langes passwort mit vielen Zeichen und 0808079699";
			s_ac.changePassword(s_ksPassword.toCharArray(), tmpPassword.toCharArray());

			AtaraxisCrypter tmpAC = new AtaraxisCrypter(new File(s_ksPath),(tmpPassword).toCharArray(),false);
			assertNotNull(tmpAC);

			s_ac.changePassword(tmpPassword.toCharArray(),s_ksPassword.toCharArray());
		} 
		catch (KeyStoreException e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		} 
		catch (IOException e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link 
	 * ataraxis.crypt.AtaraxisCrypter#changePassword(char[], 
	 * char[])}.
	 * Set a wrong password and check that the password is not changed.
	 */
	@Test
	public void testChangePasswordFail() 
	{
		try 
		{
			String tmpPassword = "another realy long password with 0808079699";
			s_ac.changePassword("not the right".toCharArray(), tmpPassword.toCharArray());

			AtaraxisCrypter tmpAC = new AtaraxisCrypter(new File(s_ksPath),(tmpPassword).toCharArray(),false);

			fail("Wrong passwort, so do not change the Password of " + tmpAC);
		} 
		catch (KeyStoreException e) 
		{
			logger.info("Fail as expectet with "+e.getMessage());
		} 
		catch (IOException e) 
		{
			logger.info("Fail as expectet with "+e.getMessage());
		}
	}


	/**
	 * Test method for {@link ataraxis.crypt.AtaraxisCrypter#getKeyStorePath()}.
	 * Is Path what he should be?
	 */
	@Test
	public void testGetKeyStorePath() 
	{
		assertEquals(s_ac.getKeyStorePath(),new File(s_ksPath).getAbsolutePath());
	}


	/**
	 * Test if a SystemProperty can change the Properties File
	 * @throws Exception 
	 * @throws IOException 
	 * @throws KeyStoreException 
	 */
	@Test
	public void testSystemPropertiesEmpty() throws KeyStoreException, IOException, Exception
	{
		String ksPath = TEST_DIR_DATA+"/ACsyspropEmpty.ks";

		System.setProperty("ATARAXIS_PROPERTIES_FILE",TEST_DIR_DATA+"/empty");
		AtaraxisCrypter tmpAC = new AtaraxisCrypter(new File(ksPath),"ThisIsMyPasds".toCharArray(),true);
		assertNotNull(tmpAC);
	}

	/**
	 * Test if a SystemProperty can change the Properties File
	 * @throws Exception 
	 * @throws IOException 
	 * @throws KeyStoreException 
	 */
	@Test
	public void testSystemPropertiesExist() throws KeyStoreException, IOException, Exception
	{
		String ksPath = TEST_DIR_DATA+"/ACsyspropExist.ks";
		String ksProperties = TEST_DIR_DATA+"/ACsyspropExist.properties";
		File props = new File(ksProperties);
		props.createNewFile();

		System.setProperty("ATARAXIS_PROPERTIES_FILE",ksProperties);
		AtaraxisCrypter tmpAC = new AtaraxisCrypter(new File(ksPath),"ThisIsMyPasds".toCharArray(),true);
		assertNotNull(tmpAC);
	}


	@Test
	public void checkJurisdictionPolicy_NoSuchAlgorithmException()
	{
		Mockit.redefineMethods(javax.crypto.Cipher.class, new Object() {
			@SuppressWarnings("unused")
			public final void init(int opmode,Key key)throws InvalidKeyException
			{
				throw new InvalidKeyException();
			}
		});

		try {
			AtaraxisCrypter.checkJurisdictionPolicy(new AESKeyCreator());
			fail("should have throw an Exception");
		} catch (JurisdictionPolicyError e) {
			Assert.assertEquals("Unrestricted Files needed","unrestricted PolicyFiles needet", e.getMessage());
		}		
	}

	@Test
	public void checkJurisdictionPolicy_OtherException()
	{
		Mockit.redefineMethods(AESKeyCreator.class, new Object() {
			@SuppressWarnings("unused")
			public SecretKey createSecretKey() throws NoSuchAlgorithmException,
			NoSuchProviderException, NotImplementedException
			{
				throw new NoSuchProviderException();
			}
		});

		try {
			AtaraxisCrypter.checkJurisdictionPolicy(new AESKeyCreator());
		} catch (Exception e) {
			fail("should not have throw an Exception");
		}		
	}

	@Test
	public void encryptOutputStream_UnrecoverableKeyException()
	{
		Mockit.redefineMethods(KeyStoreHandler.class, new Object() {
			@SuppressWarnings("unused")
			public final Key getKey(String alias, String password)
			throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
			{
				throw new UnrecoverableKeyException();
			}
		});

		try {
			s_ac.encryptOutputStream(null);
		} catch (IOException e) {
			assertEquals("UnrecoverableKeyException expected","UnrecoverableKeyException", e.getMessage());
		}		
	}

	@Test
	public void encryptOutputStream_KeyStoreException()
	{
		Mockit.redefineMethods(KeyStoreHandler.class, new Object() {
			@SuppressWarnings("unused")
			public final Key getKey(String alias, String password)
			throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
			{
				throw new KeyStoreException();
			}
		});

		try {
			s_ac.encryptOutputStream(null);
		} catch (IOException e) {
			assertEquals("KeyStoreException expected","KeyStoreException", e.getMessage());
		}		
	}

	@Test
	public void encryptOutputStream_NoSuchAlgorithmException()
	{
		Mockit.redefineMethods(KeyStoreHandler.class, new Object() {
			@SuppressWarnings("unused")
			public final Key getKey(String alias, String password)
			throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
			{
				throw new NoSuchAlgorithmException();
			}
		});

		try {
			s_ac.encryptOutputStream(null);
		} catch (IOException e) {
			assertEquals("NoSuchAlgorithmException expected","NoSuchAlgorithmException", e.getMessage());
		}		
	}


	@Test
	public void decryptInputStream_UnrecoverableKeyException()
	{
		Mockit.redefineMethods(KeyStoreHandler.class, new Object() {
			@SuppressWarnings("unused")
			public final Key getKey(String alias, String password)
			throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
			{
				throw new UnrecoverableKeyException();
			}
		});

		try {
			s_ac.decryptInputStream(inputTestFile);
		} catch (IOException e) {
			assertEquals("UnrecoverableKeyException expected","UnrecoverableKeyException", e.getMessage());
		}		
	}

	@Test
	public void decryptInputStream_KeyStoreException()
	{
		Mockit.redefineMethods(KeyStoreHandler.class, new Object() {
			@SuppressWarnings("unused")
			public final Key getKey(String alias, String password)
			throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
			{
				throw new KeyStoreException();
			}
		});

		try {
			s_ac.decryptInputStream(inputTestFile);
		} catch (IOException e) {
			assertEquals("KeyStoreException expected","KeyStoreException", e.getMessage());
		}		
	}

	@Test(expected=FileNotFoundException.class)
	public void decryptInputStream_FileNotFound() throws Exception
	{
		s_ac.decryptInputStream(new File(TEST_DIR_DATA+"/FileNotFound"));
	}

	@Test
	public void createKeyStore_Exception() throws Exception
	{
		Mockit.redefineMethods(UBERKeyStoreCreator.class, new Object() {
			@SuppressWarnings("unused")
			public final KeyStore createKeyStore(File keyStoreFile, char[] keyStorePassword)
			throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException
			{
				throw new KeyStoreException();
			}
		});

		try {
			AtaraxisCrypter ac = new AtaraxisCrypter(new File(s_ksPath+"_createKeyStore"),s_ksPassword.toCharArray(),true);
			assertNull(ac);
		} catch (KeyStoreException e) {
			assertEquals("KeyStoreException expected","Class for KeyStoreHandler not found!", e.getMessage());
		}
	}

	@Test
	public void createKeyStore_CantReadKeyStore() throws Exception
	{
		Mockit.redefineMethods(File.class, new Object() {
			@SuppressWarnings("unused")
			public boolean canRead()
			{
				return false;
			}
		});

		try {
			AtaraxisCrypter ac = new AtaraxisCrypter(new File(s_ksPath),s_ksPassword.toCharArray(),false);
			assertNull(ac);
		} catch (KeyStoreException e) {
			assertEquals("Access denied on KeyStoreFile","Access denied on KeyStore by Filesystem", e.getMessage());
		}
	}

	@Test
	public void decryptInputStream_NoSuchAlgorithmException()
	{
		Mockit.redefineMethods(KeyStoreHandler.class, new Object() {
			@SuppressWarnings("unused")
			public final Key getKey(String alias, String password)
			throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
			{
				throw new NoSuchAlgorithmException();
			}
		});

		try {
			s_ac.decryptInputStream(inputTestFile);
		} catch (IOException e) {
			assertEquals("NoSuchAlgorithmException expected","NoSuchAlgorithmException", e.getMessage());
		}		
	}


	@Test
	public void encrypt_RenameACWhenDirZip() throws Exception
	{
		String zippedDirFile = TEST_DIR_DATA+"/zipDir.ac";
		s_ac.encryptFile(new File(ZIP_DIR), new File(zippedDirFile), true);

		assertTrue("zip should end on .acz but is ac",!(new File(zippedDirFile)).exists());
		assertTrue("zip should end on .acz",new File(zippedDirFile+"z").exists());
	}

	@Test
	public void encrypt_RenameWrongNameWhenDirZip() throws Exception
	{
		String zippedDirFile = TEST_DIR_DATA+"/ziprenameWrongDir.zip";
		s_ac.encryptFile(new File(ZIP_DIR), new File(zippedDirFile), true);

		assertTrue("zip should end on a wrong name",!(new File(zippedDirFile)).exists());
		assertTrue("zip should end on .acz",new File(zippedDirFile+".acz").exists());
	}

	@Test
	public void encrypt_ZipDirAllways() throws Exception
	{
		String zippedDirFile = TEST_DIR_DATA+"/zipDirAllways.zip";
		s_ac.encryptFile(new File(ZIP_DIR), new File(zippedDirFile), false);

		assertTrue("zip should end on .acz",new File(zippedDirFile+".acz").exists());
	}

	@Test
	public void encrypt_ExistingOutFile() throws Exception
	{
		String zippedDirFile = TEST_DIR_DATA+"/zipexistDir.zip";
		File outFile = new File(zippedDirFile);
		outFile.createNewFile();
		s_ac.encryptFile(new File(ZIP_DIR), outFile, false);

		assertTrue("zip should end on .acz",new File(zippedDirFile+".acz").exists());
	}

	@Test(expected=CryptoMethodError.class)
	public void encrypt_ZipException() throws Exception
	{
		Mockit.redefineMethods(java.util.zip.ZipOutputStream.class, new Object() {
			@SuppressWarnings("unused")
			public void putNextEntry(ZipEntry e)
			throws IOException
			{
				throw new IOException();
			}
		});

		String zippedDirFile = TEST_DIR_DATA+"/zipExceptionDir.zip";
		File outFile = new File(zippedDirFile);
		s_ac.encryptFile(new File(ZIP_DIR), outFile);
	}

	@Test
	public void encrypt_SingleFileIOException() throws Exception
	{
		Mockit.redefineMethods(AtaraxisCrypter.class, new Object() {
			@SuppressWarnings("unused")
			public void copyStreams(InputStream fromStream, OutputStream toStream) throws IOException
			{
				throw new IOException();
			}
		});

		File outFile = new File(TEST_DIR_DATA+"/encrypt_SingleFileIOException.ac");

		assertTrue("OutFile does not exist bevore test",!outFile.exists());

		try {
			s_ac.encryptFile(new File(FILE_SMALL), outFile );
			fail("CryptoMethodError expected");
		} catch (CryptoMethodError e) {
			assertEquals("Wrong Exception", "IOException", e.getMessage());
			assertTrue("OutFile does not exist after test",!outFile.exists());
		}
	}
	@Test
	public void encrypt_SingleFileOutExistIOException() throws Exception
	{
		Mockit.redefineMethods(AtaraxisCrypter.class, new Object() {
			@SuppressWarnings("unused")
			public void copyStreams(InputStream fromStream, OutputStream toStream) throws IOException
			{
				throw new IOException();
			}
		});

		File outFile = new File(TEST_DIR_DATA+"/encrypt_SingleFileOutExistIOException.ac");
		assertTrue("OutFile does not exist bevore test",!outFile.exists());
		outFile.createNewFile();
		try {
			s_ac.encryptFile(new File(FILE_SMALL), outFile );
			fail("CryptoMethodError expected");
		} catch (CryptoMethodError e) {
			assertEquals("Wrong Exception", "IOException", e.getMessage());
			assertTrue("OutFile still exist after test",outFile.exists());
		}
	}
	@Test
	public void encrypt_SingleFileIOException_deleteFalse() throws Exception
	{
		Mockit.redefineMethods(AtaraxisCrypter.class, new Object() {
			@SuppressWarnings("unused")
			public void copyStreams(InputStream fromStream, OutputStream toStream) throws IOException
			{
				throw new IOException();
			}
		});
		Mockit.redefineMethods(File.class, new Object() {
			@SuppressWarnings("unused")
			public boolean delete()
			{
				return false;
			}
		});

		File outFile = new File(TEST_DIR_DATA+"/encrypt_SingleFileIOExceptionDelete.ac");

		assertTrue("OutFile does not exist bevore test",!outFile.exists());

		try {
			s_ac.encryptFile(new File(FILE_SMALL), outFile );
			fail("CryptoMethodError expected");
		} catch (CryptoMethodError e) {
			assertEquals("Wrong Exception", "IOException", e.getMessage());
			assertTrue("OutFile does not exist after test",outFile.exists());
		}
	}	


	@Test
	public void encrypt_ExceptionOnClose()
	{
		Mockit.redefineMethods(javax.crypto.CipherOutputStream.class, new Object() {
			@SuppressWarnings("unused")
			public void close() throws IOException
			{
				throw  new IOException();
			}
		});

		File outFile = new File(TEST_DIR_DATA+"/encrypt_ExceptionOnClose.ac");

		try {
			s_ac.encryptFile(new File(FILE_SMALL), outFile );
		} catch (Exception e) {
			fail("No Exception if final close of CryptoStream throws an exception");
		}
	}	


	@Test
	public void decrypt_SingleFile()
	{
		File outFile = new File(TEST_DIR_DATA+"/decrypt_SingleFile.ac");
		File outFileDecrypted = new File(TEST_DIR_DATA+"/decrypt_SingleFile.ac.txt");
		try {
			s_ac.encryptFile(new File(FILE_SMALL), outFile);
		} catch (Exception e) {
			fail("Test-Setup failed");
		} 

		try {
			s_ac.decryptFile(outFile, outFileDecrypted);
		} catch (Exception e) {
			fail("Decryption failed");
		} 
	}

	@Test(expected=FileNotFoundException.class)
	public void decrypt_SingleFileOutIsDir() throws Exception
	{
		File outFile = new File(TEST_DIR_DATA+"/decrypt_SingleFileOutIsDir.ac");
		File outFileDecrypted = new File(TEST_DIR_DATA+"/decrypt_SingleFileOutIsDir.ac.txt");
		outFileDecrypted.mkdir();
		try {
			s_ac.encryptFile(new File(FILE_SMALL), outFile);
		} catch (Exception e) {
			fail("Test-Setup failed");
		} 

		s_ac.decryptFile(outFile, outFileDecrypted);

	}

	@Test
	public void decrypt_SingleFileIOException() throws Exception
	{
		File outFile = new File(TEST_DIR_DATA+"/decrypt_SingleFileIOException.ac");
		File outFileDecrypted = new File(TEST_DIR_DATA+"/decrypt_SingleFileIOException.txt");

		try {
			s_ac.encryptFile(new File(FILE_SMALL), outFile);
		} catch (Exception e) {
			fail("Test-Setup failed");
		} 

		Mockit.redefineMethods(AtaraxisCrypter.class, new Object() {
			@SuppressWarnings("unused")
			public void copyStreams(InputStream fromStream, OutputStream toStream) throws IOException
			{
				throw new IOException();
			}
		});

		assertTrue("outFileDecrypted should not exist bevore test",!outFileDecrypted.exists());
		
		try {
			s_ac.decryptFile(outFile, outFileDecrypted);
			fail("CryptoMethodError missing");
		}
		catch (CryptoMethodError e) {
			assertTrue("outFileDecrypted should not exist after test",!outFileDecrypted.exists());
		}
	}
	
	@Test
	public void decrypt_SingleFileIOException_deleteFailed() throws Exception
	{
		File outFile = new File(TEST_DIR_DATA+"/decrypt_SingleFileIOException_deleteFailed.ac");
		File outFileDecrypted = new File(TEST_DIR_DATA+"/decrypt_SingleFileIOException_deleteFailed.txt");

		try {
			s_ac.encryptFile(new File(FILE_SMALL), outFile);
		} catch (Exception e) {
			fail("Test-Setup failed");
		} 

		Mockit.redefineMethods(AtaraxisCrypter.class, new Object() {
			@SuppressWarnings("unused")
			public void copyStreams(InputStream fromStream, OutputStream toStream) throws IOException
			{
				throw new IOException();
			}
		});
		Mockit.redefineMethods(File.class, new Object() {
			@SuppressWarnings("unused")
			public boolean delete()
			{
				return false;
			}
		});
				
		assertTrue("outFileDecrypted should not exist bevore test",!outFileDecrypted.exists());
				
		try {
			s_ac.decryptFile(outFile, outFileDecrypted);
			fail("CryptoMethodError missing");
		}
		catch (CryptoMethodError e) {
			assertTrue("outFileDecrypted should exist after test",outFileDecrypted.exists());
		}
	}

	
	@Test
	public void decrypt_SingleFileIOException_IOOnClose() throws Exception
	{
		File outFile = new File(TEST_DIR_DATA+"/decrypt_SingleFileIOException_IOOnClose.ac");
		File outFileDecrypted = new File(TEST_DIR_DATA+"/decrypt_SingleFileIOException_IOOnClose.txt");

		try {
			s_ac.encryptFile(new File(FILE_SMALL), outFile);
		} catch (Exception e) {
			fail("Test-Setup failed");
		} 

		Mockit.redefineMethods(FileOutputStream.class, new Object() {
			@SuppressWarnings("unused")
			public void close() throws IOException
			{
				throw  new IOException();
			}
		});
		assertTrue("outFileDecrypted should not exist bevore test",!outFileDecrypted.exists());
		
		try {
			s_ac.decryptFile(outFile, outFileDecrypted);
			assertTrue("outFileDecrypted should exist after test - error in final close",outFileDecrypted.exists());
		}
		catch (Exception e) {
			fail("No Exception if final Streams could not be closed");
		}
	}

	

	@Test(expected=FileNotFoundException.class)
	public void decrypt_SingleFileZipEndig_OutFileIsFileNotDir() throws Exception
	{
		File outFile = new File(TEST_DIR_DATA+"/decrypt_SingleFileZipEndig_OutFileIsFileNotDir.acz");
		File outFileDecrypted = new File(TEST_DIR_DATA+"/decrypt_SingleFileZipEndig_OutFileIsFileNotDir.txt");

		outFile.createNewFile();
		outFileDecrypted.createNewFile();
		s_ac.decryptFile(outFile, outFileDecrypted);
	}
	
	@Test
	public void decrypt_ZipFile() throws Exception
	{
		File outFile = new File(TEST_DIR_DATA+"/decrypt_ZipFile.acz");
		File outFileDecrypted = new File(TEST_DIR_DATA+"/decrypt_ZipFile.dir");

		
		try {
			s_ac.encryptFile(new File(ZIP_DIR), outFile, true);
		} catch (Exception e) {
			fail("Test-Setup failed");
		}
		
		s_ac.decryptFile(outFile, outFileDecrypted);
	}
	

	@Test(expected=ZipException.class)
	public void decrypt_ZipFile_NotZipped() throws Exception
	{
		File outFile = new File(TEST_DIR_DATA+"/decrypt_ZipFile_NotZipped.acz");
		File outFileDecrypted = new File(TEST_DIR_DATA+"/decrypt_ZipFile_NotZipped.dir");

		
		try {
			s_ac.encryptFile(new File(FILE_SMALL), outFile, false);
		} catch (Exception e) {
			fail("Test-Setup failed");
		}
		
		s_ac.decryptFile(outFile, outFileDecrypted);
	}


	@Test(expected=CryptoMethodError.class)
	public void decrypt_ZipFile_IOException() throws Exception
	{
		File outFile = new File(TEST_DIR_DATA+"/decrypt_ZipFile_IOException.acz");
		File outFileDecrypted = new File(TEST_DIR_DATA+"/decrypt_ZipFile_IOException.dir");
		
		try {
			s_ac.encryptFile(new File(FILE_SMALL), outFile, false);
		} catch (Exception e) {
			fail("Test-Setup failed");
		}
		
		Mockit.redefineMethods(ZipInputStream.class, new Object() {
			@SuppressWarnings("unused")
			public void close() throws IOException
			{
				throw  new IOException();
			}
		});

		
		s_ac.decryptFile(outFile, outFileDecrypted);
	}
}