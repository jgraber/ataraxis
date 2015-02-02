/* ----------------------------------------------------------------------------
 * Copyright 2007 - 2015 Johnny Graber & Andreas Muedespacher
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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
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
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.crypto.SecretKey;

import mockit.Mock;
import mockit.MockUp;

import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.TestingHelper;
import ataraxis.crypt.AESKeyCreator;
import ataraxis.crypt.AtaraxisCrypter;
import ataraxis.crypt.CryptoMethodError;
import ataraxis.crypt.JurisdictionPolicyError;
import ataraxis.crypt.KeyStoreHandler;
import ataraxis.crypt.NotImplementedException;
import ataraxis.crypt.UBERKeyStoreCreator;



/**
 * Unit Test for AtaraxisCrypter
 * @author Johnny Graber
 *
 */
public class AtaraxisCrypter_JMockit_Test {

	private static AtaraxisCrypter s_ac = null;
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	private static final String TEST_DIR_DATA = TEST_DIR + "/testrun/ac_test_data";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	private static final String FILE_SMALL = TEST_DIR + "/fixtures/performance.xls";
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
	}
	
	@Test
	public void checkJurisdictionPolicy_NoSuchAlgorithmException()
	{
		new MockUp<javax.crypto.Cipher>(){
			   @Mock
			   public final void init(int opmode,Key key)throws InvalidKeyException
				{
					throw new InvalidKeyException();
				}
		};

		try {
			AtaraxisCrypter.checkJurisdictionPolicy(new AESKeyCreator());
			fail("should have throw an Exception");
		} catch (JurisdictionPolicyError e) {
			assertEquals("Unrestricted Files needed","unrestricted PolicyFiles needet", e.getMessage());
		}		
	}

	@Test
	public void checkJurisdictionPolicy_OtherException()
	{
		new MockUp<AESKeyCreator>(){
			   @Mock
			   public SecretKey createSecretKey() throws NoSuchAlgorithmException,
				NoSuchProviderException, NotImplementedException
				{
					throw new NoSuchProviderException();
				}
		};

		try {
			AtaraxisCrypter.checkJurisdictionPolicy(new AESKeyCreator());
		} catch (Exception e) {
			fail("should not have throw an Exception");
		}		
	}

	@Test
	public void encryptOutputStream_UnrecoverableKeyException()
	{
		new MockUp<KeyStoreHandler>(){
			   @Mock
			   public final Key getKey(String alias, String password)
						throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
						{
							throw new UnrecoverableKeyException();
						}
		};

		try {
			s_ac.encryptOutputStream(null);
		} catch (IOException e) {
			assertEquals("UnrecoverableKeyException expected","UnrecoverableKeyException", e.getMessage());
		}		
	}

	@Test
	public void encryptOutputStream_KeyStoreException()
	{
		new MockUp<KeyStoreHandler>(){
			   @Mock
			   public final Key getKey(String alias, String password)
						throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
						{
							throw new KeyStoreException();
						}
		};

		try {
			s_ac.encryptOutputStream(null);
		} catch (IOException e) {
			assertEquals("KeyStoreException expected","KeyStoreException", e.getMessage());
		}		
	}

	@Test
	public void encryptOutputStream_NoSuchAlgorithmException()
	{
		new MockUp<KeyStoreHandler>(){
			   @Mock
			   public final Key getKey(String alias, String password)
						throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
						{
							throw new NoSuchAlgorithmException();
						}
		};

		try {
			s_ac.encryptOutputStream(null);
		} catch (IOException e) {
			assertEquals("NoSuchAlgorithmException expected","NoSuchAlgorithmException", e.getMessage());
		}		
	}


	@Test
	public void decryptInputStream_UnrecoverableKeyException()
	{
		new MockUp<KeyStoreHandler>(){
			   @Mock
			   public final Key getKey(String alias, String password)
						throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
						{
							throw new UnrecoverableKeyException();
						}
		};
		
		try {
			s_ac.decryptInputStream(inputTestFile);
		} catch (IOException e) {
			assertEquals("UnrecoverableKeyException expected","UnrecoverableKeyException", e.getMessage());
		}		
	}

	@Test
	public void decryptInputStream_KeyStoreException()
	{
		new MockUp<KeyStoreHandler>(){
			   @Mock
			   public final Key getKey(String alias, String password)
						throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
						{
							throw new KeyStoreException();
						}
			    
			  };
			  

		try {
			s_ac.decryptInputStream(inputTestFile);
		} catch (IOException e) {
			assertEquals("KeyStoreException expected","KeyStoreException", e.getMessage());
		}		
	}

	@Test
	public void createKeyStore_Exception() throws Exception
	{
		new MockUp<UBERKeyStoreCreator>(){
			   @Mock
			   public final KeyStore createKeyStore(File keyStoreFile, char[] keyStorePassword)
						throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException
						{
							throw new KeyStoreException();
						}
		};

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
		new MockUp<File>(){
			   @Mock
			   public boolean canRead()
				{
					return false;
				}
		};

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
		new MockUp<KeyStoreHandler>(){
			   @Mock
			   public final Key getKey(String alias, String password)
						throws KeyStoreException, NoSuchAlgorithmException, UnrecoverableKeyException
						{
							throw new NoSuchAlgorithmException();
						}
		};

		try {
			s_ac.decryptInputStream(inputTestFile);
		} catch (IOException e) {
			assertEquals("NoSuchAlgorithmException expected","NoSuchAlgorithmException", e.getMessage());
		}		
	}
	
	@Test(expected=CryptoMethodError.class)
	public void encrypt_ZipException() throws Exception
	{
		new MockUp<ZipOutputStream>(){
			   @Mock
			   public void putNextEntry(ZipEntry e)
						throws IOException
						{
							throw new IOException();
						}
		};

		String zippedDirFile = TEST_DIR_DATA+"/zipExceptionDir.zip";
		File outFile = new File(zippedDirFile);
		s_ac.encryptFile(new File(ZIP_DIR), outFile);
	}
	
	@Test
	public void encrypt_SingleFileIOException() throws Exception
	{
		new MockUp<AtaraxisCrypter>(){
			   @Mock
			   public void copyStreams(InputStream fromStream, OutputStream toStream) throws IOException
				{
					throw new IOException();
				}
		};
		
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
		new MockUp<AtaraxisCrypter>(){
			   @Mock
			   public void copyStreams(InputStream fromStream, OutputStream toStream) throws IOException
				{
					throw new IOException();
				}
		};

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
		new MockUp<AtaraxisCrypter>(){
			   @Mock
			   public void copyStreams(InputStream fromStream, OutputStream toStream) throws IOException
				{
					throw new IOException();
				}
		};
		new MockUp<File>(){
			   @Mock
			   public boolean delete()
				{
					return false;
				}
		};

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
		new MockUp<javax.crypto.CipherOutputStream>(){
			   @Mock
			   public void close() throws IOException
				{
					throw  new IOException();
				}
		};
		
		File outFile = new File(TEST_DIR_DATA+"/encrypt_ExceptionOnClose.ac");

		try {
			s_ac.encryptFile(new File(FILE_SMALL), outFile );
		} catch (Exception e) {
			fail("No Exception if final close of CryptoStream throws an exception");
		}
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

		new MockUp<AtaraxisCrypter>(){
			   @Mock
			   public void copyStreams(InputStream fromStream, OutputStream toStream) throws IOException
				{
					throw new IOException();
				}
		};

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

		new MockUp<AtaraxisCrypter>(){
			   @Mock
			   public void copyStreams(InputStream fromStream, OutputStream toStream) throws IOException
				{
					throw new IOException();
				}
		};
		new MockUp<File>(){
			   @Mock
			   public boolean delete()
				{
					return false;
				}
		};
		
				
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

		new MockUp<FileOutputStream>(){
			   @Mock
			   public void close() throws IOException
				{
					throw  new IOException();
				}
		};
		
		assertTrue("outFileDecrypted should not exist bevore test",!outFileDecrypted.exists());
		
		try {
			s_ac.decryptFile(outFile, outFileDecrypted);
			assertTrue("outFileDecrypted should exist after test - error in final close",outFileDecrypted.exists());
		}
		catch (Exception e) {
			fail("No Exception if final Streams could not be closed");
		}
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
		
		new MockUp<ZipInputStream>(){
			   @Mock
			   public void close() throws IOException
				{
					throw  new IOException();
				}
		};

		s_ac.decryptFile(outFile, outFileDecrypted);
	}
}
