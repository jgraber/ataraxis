/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2010 Johnny Graber & Andreas Muedespacher
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

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import mockit.Mockit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.crypt.ACDecryptInputStream;
import ataraxis.crypt.AESKeyCreator;
import ataraxis.crypt.AtaraxisHeaderCreator;
import ataraxis.crypt.JurisdictionPolicyError;
import ataraxis.crypt.NotImplementedException;

public class ACDecryptInputStreamTest {

	private static final Logger logger = Logger.getLogger(ACDecryptInputStreamTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	private static final String TEST_DIR_DATA = TEST_DIR + "/testrun/ACDecryptInputStreamTest";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	private static final File testFile = new File(TEST_DIR_DATA+ "/testFile.txt");
	private static final File testEncryptHeaderFile = new File(TEST_DIR_DATA+ "/testFile.enc");
	

	
	
	private static SecretKey key;
	
	@BeforeClass 
	public static void initClass() throws KeyStoreException, IOException, JurisdictionPolicyError, Exception, NoSuchProviderException, NotImplementedException
	{
		
		PropertyConfigurator.configure(LOG_PROPS_FILE); 
		(new File(TEST_DIR_DATA)).mkdirs();
		
		AESKeyCreator ac = new AESKeyCreator();
		key = ac.createSecretKey();
	
		//testFile
		FileWriter writer = new FileWriter(testFile);
		writer.write("Dies ist eine Testdatei...");
		writer.flush();
		writer.close();
		
		AtaraxisHeaderCreator ahc = new AtaraxisHeaderCreator();
		FileWriter writer2 = new FileWriter(testEncryptHeaderFile);
		writer2.write(ahc.getHeader());
		writer2.flush();
		writer2.close();
		
		
		logger.debug("start Tests for ACDecryptInputStreamTest");
	}
	

	@Test
	public void ACDecryptInputStream_TryOldVersion() throws Exception
	{
		ACDecryptInputStream acI = new ACDecryptInputStream(testFile, key);
		assertNotNull("Should not be null",acI);
		assertNotNull("Stream should not be null", acI.getDecriptInputStream());
	}
	
	@Test
	public void ACDecryptInputStream_newVersion() throws Exception
	{
		ACDecryptInputStream acI = new ACDecryptInputStream(testEncryptHeaderFile, key);
		assertNotNull("Should not be null",acI);
		assertNotNull("Stream should not be null", acI.getDecriptInputStream());
	}
	
	@Test(expected=IOException.class)
	public void ACDecryptInputStream_NoSuchAlgorithmException() throws Exception
	{
		Mockit.redefineMethods(javax.crypto.Cipher.class, new Object() {
			@SuppressWarnings("unused")
			public final Cipher getInstance(String transformation,
                    Provider provider)
             throws NoSuchAlgorithmException,
                    NoSuchPaddingException
			{
				throw new NoSuchAlgorithmException();
			}
		});
		ACDecryptInputStream acI = new ACDecryptInputStream(testFile, key);
		fail("IOException missing for " + acI);
	}
	
	@Test(expected=IOException.class)
	public void ACDecryptInputStream_NoSuchPaddingException() throws Exception
	{
		Mockit.redefineMethods(javax.crypto.Cipher.class, new Object() {
			@SuppressWarnings("unused")
			public final Cipher getInstance(String transformation, Provider provider)
             throws NoSuchAlgorithmException, NoSuchPaddingException
			{
				throw new NoSuchPaddingException();
			}
		});		
		ACDecryptInputStream acI = new ACDecryptInputStream(testFile, key);
		assertNotNull("Should be null",acI);
	}
	
	@Test(expected=IOException.class)
	public void ACDecryptInputStream_InvalidKeyException() throws Exception
	{
		Mockit.redefineMethods(javax.crypto.Cipher.class, new Object() {
			@SuppressWarnings("unused")
			public final void init(int opmode, Key key, AlgorithmParameterSpec params)
                throws InvalidKeyException, InvalidAlgorithmParameterException
			{
				throw new InvalidKeyException();
			}
		});
		ACDecryptInputStream acI = new ACDecryptInputStream(testEncryptHeaderFile, key);
		assertNotNull("Should be null",acI);
	}
	
	@Test(expected=IOException.class)
	public void ACDecryptInputStream_InvalidAlgorithmParameterException() throws Exception
	{
		Mockit.redefineMethods(javax.crypto.Cipher.class, new Object() {
			@SuppressWarnings("unused")
			public final void init(int opmode, Key key, AlgorithmParameterSpec params)
                throws InvalidKeyException, InvalidAlgorithmParameterException
			{
				throw new InvalidAlgorithmParameterException();
			}
		});
		ACDecryptInputStream acI = new ACDecryptInputStream(testEncryptHeaderFile, key);
		assertNotNull("Should be null",acI);
	}
	
	@Test(expected=IOException.class)
	public void ACDecryptInputStream_NoSuchProviderException() throws Exception
	{
		Mockit.redefineMethods(javax.crypto.Cipher.class, new Object() {
			@SuppressWarnings("unused")
			public final Cipher getInstance(String transformation, String provider)
            	throws NoSuchAlgorithmException, NoSuchProviderException, NoSuchPaddingException
			{
				throw new NoSuchProviderException();
			}
		});
		ACDecryptInputStream acI = new ACDecryptInputStream(testEncryptHeaderFile, key);
		assertNotNull("Should be null",acI);
	}
}
