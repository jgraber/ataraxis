/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2016 Johnny Graber & Andreas Muedespacher
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

import static org.junit.Assert.assertNotNull;

import java.io.File;
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

import mockit.Mock;
import mockit.MockUp;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.crypt.ACEncryptOutputStream;
import ataraxis.crypt.AESKeyCreator;
import ataraxis.crypt.JurisdictionPolicyError;
import ataraxis.crypt.NotImplementedException;

public class ACEncryptOutputStreamTest 
{
	private static final Logger logger = LogManager.getLogger(ACEncryptOutputStreamTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	private static final String TEST_DIR_DATA = TEST_DIR + "/testrun/ACEncryptOutputStreamTest";
	private static SecretKey key;
	
	@BeforeClass 
	public static void initClass() throws KeyStoreException, IOException, JurisdictionPolicyError, Exception, NoSuchProviderException, NotImplementedException
	{
		(new File(TEST_DIR_DATA)).mkdirs();
		
		AESKeyCreator ac = new AESKeyCreator();
		key = ac.createSecretKey();
	
		logger.debug("start Tests for ACEncryptOutputStreamTest");
	}

	@Test
	public void testConstructor_Success() throws Exception
	{
		File outFile = new File(TEST_DIR_DATA+"/const_1");
		ACEncryptOutputStream acOut = new ACEncryptOutputStream(outFile, key);
		assertNotNull(acOut.getEncryptOutputStream());
	}
	
	@Test(expected=IOException.class)
	public void testConstructor_NoSuchAlgorithmException() throws Exception
	{
		File outFile = new File(TEST_DIR_DATA+"/const_2");
		
		new MockUp<javax.crypto.Cipher>(){
			   @Mock
			   public final Cipher getInstance(String transformation,
	                    Provider provider)
	             throws NoSuchAlgorithmException,
	                    NoSuchPaddingException
				{
					throw new NoSuchAlgorithmException();
				}
		};
		
		ACEncryptOutputStream acOut = new ACEncryptOutputStream(outFile, key);
		assertNotNull(acOut.getEncryptOutputStream());
	}
	
	@Test(expected=IOException.class)
	public void testConstructor_NoSuchPaddingException() throws Exception
	{
		File outFile = new File(TEST_DIR_DATA+"/const_2");
		
		new MockUp<javax.crypto.Cipher>(){
			   @Mock
			   public final Cipher getInstance(String transformation,
	                    Provider provider)
	             throws NoSuchAlgorithmException,
	                    NoSuchPaddingException
				{
					throw new NoSuchPaddingException();
				}
		};
		
		ACEncryptOutputStream acOut = new ACEncryptOutputStream(outFile, key);
		assertNotNull(acOut.getEncryptOutputStream());
	}
	
	
	@Test(expected=IOException.class)
	public void testConstructor_InvalidKeyException() throws Exception
	{
		File outFile = new File(TEST_DIR_DATA+"/const_2");
		
		new MockUp<javax.crypto.Cipher>(){
			   @Mock
			   public final void init(int opmode,
                       Key key,
                       AlgorithmParameterSpec params)
                throws InvalidKeyException,
                       InvalidAlgorithmParameterException

			{
				throw new InvalidKeyException();
			}
		};
		
		ACEncryptOutputStream acOut = new ACEncryptOutputStream(outFile, key);
		assertNotNull(acOut.getEncryptOutputStream());
	}
	
	@Test(expected=IOException.class)
	public void testConstructor_InvalidAlgorithmParameterException() throws Exception
	{
		File outFile = new File(TEST_DIR_DATA+"/const_2");
		
		new MockUp<javax.crypto.Cipher>(){
			   @Mock
			   public final void init(int opmode,
                       Key key,
                       AlgorithmParameterSpec params)
                throws InvalidKeyException,
                       InvalidAlgorithmParameterException

			{
				throw new InvalidAlgorithmParameterException();
			}
		};
		
		ACEncryptOutputStream acOut = new ACEncryptOutputStream(outFile, key);
		assertNotNull(acOut.getEncryptOutputStream());
	}
	
	@Test(expected=IOException.class)
	public void testConstructor_NoSuchProviderException() throws Exception
	{
		File outFile = new File(TEST_DIR_DATA+"/const_2");
		
		new MockUp<javax.crypto.Cipher>(){
			   @Mock
			   public final Cipher getInstance(String transformation,
                       String provider)
                throws NoSuchAlgorithmException,
                       NoSuchProviderException,
                       NoSuchPaddingException
                       {
				   			throw new NoSuchProviderException();
                       }
		};
		
		ACEncryptOutputStream acOut = new ACEncryptOutputStream(outFile, key);
		assertNotNull(acOut.getEncryptOutputStream());
	}

}
