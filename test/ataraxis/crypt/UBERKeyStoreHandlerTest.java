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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import javax.crypto.SecretKey;
import javax.security.auth.x500.X500Principal;

import mockit.Mockit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.crypt.AESKeyCreator;
import ataraxis.crypt.KeyPairCreator;
import ataraxis.crypt.RSAKeyCreator;
import ataraxis.crypt.SecretKeyCreator;
import ataraxis.crypt.UBERKeyStoreCreator;
import ataraxis.crypt.UBERKeyStoreHandler;
import ataraxis.misc.AtaraxisHashCreator;
import ataraxis.util.FileCopy;


/**
 * Unit Test for UBERKeyStoreHandler
 * @author Johnny Graber
 *
 */
@SuppressWarnings("deprecation") // until BC sorts out X509V3CertificateGenerator
public class UBERKeyStoreHandlerTest 
{

	private static final Logger logger = Logger.getLogger(UBERKeyStoreHandler.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + File.separator + "test";
	private static final String TEST_DIR_DATA = TEST_DIR + File.separator + "testrun" + File.separator + "ueberHandler";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";

	private static String ks_Password = "thisisA";
	private static String ks_Path = TEST_DIR_DATA + File.separator + "ueber.ubr";
	private static String ks_PathClean = TEST_DIR_DATA + "/ueber_Clean.ubr";
	private static UBERKeyStoreHandler s_ksh = null;

	@BeforeClass 
	public static void initClass() throws KeyStoreException, NoSuchProviderException, NoSuchAlgorithmException
	{
		PropertyConfigurator.configure(LOG_PROPS_FILE); 

		(new File(TEST_DIR_DATA)).mkdirs();

		UBERKeyStoreCreator ksCreator = new UBERKeyStoreCreator();
		File ks_File = new File(ks_Path);
		ksCreator.createKeyStore(ks_File, ks_Password.toCharArray());
		s_ksh = new UBERKeyStoreHandler(ks_File, ks_Password.toCharArray());


		ksCreator = new UBERKeyStoreCreator();
		ksCreator.createKeyStore(new File(ks_PathClean), ks_Password.toCharArray());

	}


	@Test
	public void testChangePassword() 
	{

		String currentPW = ks_Password;
		String newPW = "dies ist neu";
		try {
			s_ksh.changePassword(currentPW.toCharArray(), newPW.toCharArray());

			s_ksh.changePassword(newPW.toCharArray(), currentPW.toCharArray());
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


	@Test
	public void testContainsAlias() 
	{
		try 
		{
			boolean existsNot = s_ksh.containsAlias("Den wird es nicht geben");

			if (existsNot) 
			{
				fail("found not exisiting Alias");
			}
		} 
		catch (KeyStoreException e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}

	@Test
	public void testSetAndGetKey() 
	{
		try 
		{
			SecretKeyCreator createAESKey = new AESKeyCreator();
			SecretKey aesKey = createAESKey.createSecretKey();
			s_ksh.setEntry("MyAliasISThis",
					new KeyStore.SecretKeyEntry(aesKey),
					new KeyStore.PasswordProtection("myPassword".toCharArray()));

			SecretKey aesKey2 = (SecretKey) s_ksh.getKey("MyAliasISThis", "myPassword");
			s_ksh.store();
			assertEquals(aesKey, aesKey2);
		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}

	@Test
	public void testSetAndGetCertificate() 
	{
		try 
		{
			KeyPairCreator createRSAKey = new RSAKeyCreator();
			KeyPair RSAKeys = createRSAKey.createKeyPair();

			java.security.cert.Certificate[] myCerts = new java.security.cert.Certificate[1];
			myCerts[0] = generateX509V3Cert(RSAKeys); 
			s_ksh.setKeyEntry("RSA Key", RSAKeys.getPrivate(), "this", myCerts);
			s_ksh.setCertificate("MyCert", myCerts[0]);
			s_ksh.store();

			String myCertAlias = s_ksh.getCertificateAlias(myCerts[0]);
			assertEquals(myCertAlias, "MyCert");

			RSAPrivateKey ra = (RSAPrivateKey) s_ksh.getKey("RSA Key", "this");
			X509Certificate certd = (X509Certificate) s_ksh.getCertificate("RSA Key");


			assertEquals(certd.getPublicKey(),RSAKeys.getPublic());
			logger.debug("testSetAndGetCertificate Public-Keys are ok");

			assertEquals(AtaraxisHashCreator.prettyPrintHash(RSAKeys.getPrivate().getEncoded()),
					AtaraxisHashCreator.prettyPrintHash(ra.getEncoded()));
			logger.debug("testSetAndGetCertificate ok");
		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			System.out.println(e.getMessage());
			fail(e.getMessage());
		}
	}



	@Test
	public void testDeleteEntry() 
	{
		try 
		{
			SecretKeyCreator createAESKey = new AESKeyCreator();
			SecretKey aesKey = createAESKey.createSecretKey();
			s_ksh.setEntry("MyAliasToDelete",
					new KeyStore.SecretKeyEntry(aesKey),
					new KeyStore.PasswordProtection("myPassword".toCharArray()));

			KeyStore.Entry deleteEntry = s_ksh.getEntry("MyAliasToDelete",  
					new KeyStore.PasswordProtection("myPassword".toCharArray()));
			assertNotNull(deleteEntry);
			if(s_ksh.containsAlias("MyAliasToDelete"))
			{
				s_ksh.deleteEntry("MyAliasToDelete");
				if(s_ksh.containsAlias("MyAliasToDelete"))	
				{
					fail("Could not delete the Entry");
				}
			}
			else
			{
				fail("Could not create the Entry");
			}
			s_ksh.store();

		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetProvider() 
	{
		assertEquals(s_ksh.getProvider().getName(), "BC");
	}

	@Test
	public void testGetType() 
	{
		assertEquals(s_ksh.getType(), "UBER");
	}


	@Test
	public void testGetPassword() 
	{
		assertTrue(Arrays.equals(s_ksh.getPassword(),ks_Password.toCharArray()));
	}

	@Test
	public void testGetKeyStorePath() 
	{
		assertEquals(new File(ks_Path).getAbsolutePath(), s_ksh.getKeyStorePath());
	}

	@Test
	public void testSetKeyStorePath() 
	{
		String newPath = ks_Path+"_new";
		s_ksh.setKeyStorePath(newPath);
		assertEquals(newPath, s_ksh.getKeyStorePath());
		s_ksh.setKeyStorePath(ks_Path);
	}

	@Test
	public void testGetKeyStore() 
	{
		KeyStore javaKS;
		try {
			javaKS = KeyStore.getInstance("UBER", "BC");
			javaKS.load(new FileInputStream(ks_Path), ks_Password.toCharArray());
			String currentElement;
			List<String> ksContent = new ArrayList<String>();

			// aliasses1 and aliasses2 have to be equal
			Enumeration<String> aliasses1 = javaKS.aliases();
			while (aliasses1.hasMoreElements ()) 
			{
				ksContent.add(aliasses1.nextElement());
			} 
			Enumeration<String> aliasses2 = s_ksh.aliases();
			while (aliasses2.hasMoreElements ()) 
			{
				currentElement = aliasses2.nextElement();
				if(ksContent.contains(currentElement))
				{
					ksContent.remove(currentElement);
				}
				else
				{
					ksContent.add(currentElement);
				}
			} 

			assertEquals(ksContent.size(), 0);
		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		} 	
	}


	private static X509Certificate generateX509V3Cert(KeyPair pair)
	throws InvalidKeyException, NoSuchProviderException, SignatureException
	{
		// define Generator
		X509V3CertificateGenerator  certGen = new X509V3CertificateGenerator();

		// define the parameters
		certGen.setSerialNumber(BigInteger.valueOf(System.currentTimeMillis()));
		certGen.setIssuerDN(new X500Principal("CN=Test Certificate Issuer"));
		certGen.setNotBefore(new Date(System.currentTimeMillis() - 50000));
		Calendar cal = Calendar.getInstance();

		cal.add(Calendar.YEAR, +1);

		//Date then = cal.getTime();

		certGen.setNotAfter(cal.getTime());
		certGen.setSubjectDN(new X500Principal("CN=Test Certificate Subject"));
		certGen.setPublicKey(pair.getPublic());
		certGen.setSignatureAlgorithm("SHA256WithRSAEncryption");

		;
		// generate and return the certificate
		return certGen.generateX509Certificate(pair.getPrivate(), "BC");
	}

	@Test
	public void testShowKeyStore() 
	{
		s_ksh.showKeyStore();
	}


	@Test
	public void testKeyStoreHandler_CertificateException() throws Exception
	{
		File ksFile = new File(ks_PathClean);
		Mockit.redefineMethods(KeyStore.class, new Object() {
			@SuppressWarnings("unused")
			public void load(InputStream stream,
					char[] password)
			throws IOException,
			NoSuchAlgorithmException,
			CertificateException
			{
				throw new CertificateException();
			}
		});

		try
		{
			UBERKeyStoreHandler ksHandler = new UBERKeyStoreHandler(ksFile, ks_Password.toCharArray());
			assertNotNull(ksHandler);
		}
		catch (KeyStoreException e)
		{
			assertTrue(e.getMessage().contains("Error on Certificate"));
		}
	}

	@Test
	public void testKeyStoreHandler_IOException() throws Exception
	{
		File ksFile = new File(ks_PathClean);
		Mockit.redefineMethods(FileInputStream.class, new Object() {
			@SuppressWarnings("unused")
			public int read(byte[] b, int off, int len) throws IOException
			{
				throw new IOException();
			}
			@SuppressWarnings("unused")
			public void close() throws IOException
			{
				throw new IOException();
			}
		});

		try
		{
			UBERKeyStoreHandler ksHandler = new UBERKeyStoreHandler(ksFile, ks_Password.toCharArray());
			assertTrue("should be null",ksHandler == null);
		}
		catch (KeyStoreException e)
		{
			assertTrue(e.getMessage().contains("IO Error on loading the KeyStore"));
		}
	}

	@Test
	public void testKeyStoreHandler_FileNotFoundException() throws Exception
	{
		File ksFile = new File(ks_PathClean+"dontExist");
		try
		{
			UBERKeyStoreHandler ksHandler = new UBERKeyStoreHandler(ksFile, ks_Password.toCharArray());
			assertTrue("should be null",ksHandler == null);
		}
		catch (KeyStoreException e)
		{
			assertTrue(e.getMessage().contains("KeyStore File not found!"));
		}
	}

	@Test
	public void testKeyStoreHandler_NoSuchAlgorithmException() throws Exception
	{
		File ksFile = new File(ks_PathClean);
		Mockit.redefineMethods(KeyStore.class, new Object() {
			@SuppressWarnings("unused")
			public void load(InputStream stream,
					char[] password)
			throws IOException,
			NoSuchAlgorithmException,
			CertificateException
			{
				throw new NoSuchAlgorithmException();
			}
		});

		try
		{
			UBERKeyStoreHandler ksHandler = new UBERKeyStoreHandler(ksFile, ks_Password.toCharArray());
			assertTrue("should be null",ksHandler == null);
		}
		catch (KeyStoreException e)
		{
			assertTrue("should be NoSuchAlgorithmException",e.getMessage().contains("Algorithm of KeyStore does not exist!"));
		}
	}

	@Test
	public void testShowKeyStore_KeyStoreException() throws Exception
	{
		File ksFile = new File(ks_PathClean);
		Mockit.redefineMethods(KeyStore.class, new Object() {
			@SuppressWarnings("unused")
			public final Enumeration<String> aliases() throws KeyStoreException
			{
				throw new KeyStoreException();
			}
		});

		UBERKeyStoreHandler ksHandler = new UBERKeyStoreHandler(ksFile, ks_Password.toCharArray());
		ksHandler.showKeyStore();
	}
	
	@Test
	public void testShowKeyStore_OtherThanKeyOrCert() throws Exception
	{
		File ksFile = new File(ks_Path);
		Mockit.redefineMethods(KeyStore.class, new Object() {
			@SuppressWarnings("unused")
			public final boolean isCertificateEntry(String alias) throws KeyStoreException
			{
				return false;
			}
			@SuppressWarnings("unused")
			public final boolean isKeyEntry(String alias) throws KeyStoreException
			{
				return false;
			}
		});

		UBERKeyStoreHandler ksHandler = new UBERKeyStoreHandler(ksFile, ks_Password.toCharArray());
		ksHandler.showKeyStore();
	}
	
	@Test
	public void testStore_NoSuchAlgorithmException() throws Exception
	{
		File ksFile = new File(ks_PathClean+"2");
		FileCopy.copyFile(ks_PathClean, ksFile.getAbsolutePath());
		
		UBERKeyStoreHandler ksHandler = new UBERKeyStoreHandler(ksFile, ks_Password.toCharArray());
		
		Mockit.redefineMethods(KeyStore.class, new Object() {
			@SuppressWarnings("unused")
			public final void store(OutputStream stream, char[] password)
                 throws KeyStoreException,
                        IOException,
                        NoSuchAlgorithmException,
                        CertificateException
			{
				throw new NoSuchAlgorithmException();
			}
		});

		try
		{	ksHandler.store();
			fail("should throw a Exception");
		}
		catch (KeyStoreException e)
		{
			assertTrue("should be NoSuchAlgorithmException",e.getMessage().contains("No such Algorithm for KeyStore"));
		}
	}
	
	@Test
	public void testStore_CertificateException() throws Exception
	{
		File ksFile = new File(ks_PathClean+"1");
		FileCopy.copyFile(ks_PathClean, ksFile.getAbsolutePath());
		
		
		UBERKeyStoreHandler ksHandler = new UBERKeyStoreHandler(ksFile, ks_Password.toCharArray());
		
		Mockit.redefineMethods(KeyStore.class, new Object() {
			@SuppressWarnings("unused")
			public final void store(OutputStream stream, char[] password)
                 throws KeyStoreException,
                        IOException,
                        NoSuchAlgorithmException,
                        CertificateException
			{
				throw new CertificateException();
			}
		});

		try
		{	ksHandler.store();
			fail("should throw a Exception");
		}
		catch (KeyStoreException e)
		{
			assertTrue("should be CertificateException",e.getMessage().contains("Certificate Error on KeyStore"));
		}
	}
	@Test
	public void testStore_IOException() throws Exception
	{
		File ksFile = new File(ks_PathClean+"3");
		FileCopy.copyFile(ks_PathClean, ksFile.getAbsolutePath());
		
		
		UBERKeyStoreHandler ksHandler = new UBERKeyStoreHandler(ksFile, ks_Password.toCharArray());
		
		Mockit.redefineMethods(KeyStore.class, new Object() {
			@SuppressWarnings("unused")
			public final void store(OutputStream stream, char[] password)
                 throws KeyStoreException,
                        IOException,
                        NoSuchAlgorithmException,
                        CertificateException
			{
				throw new IOException();
			}
		});

		try
		{	ksHandler.store();
			fail("should throw a Exception");
		}
		catch (KeyStoreException e)
		{
			assertTrue("should be CertificateException",e.getMessage().contains("IO Error on writing the KeyStore"));
		}
	}
	

	@Test
	public void testStore_FileNotFoundException() throws Exception
	{
		File ksFile = new File(ks_PathClean+"NotFound");
		FileCopy.copyFile(ks_PathClean, ksFile.getAbsolutePath());
		
		
		UBERKeyStoreHandler ksHandler = new UBERKeyStoreHandler(ksFile, ks_Password.toCharArray());
		
		Mockit.redefineMethods(FileOutputStream.class, MockedFileOutputStream.class);

		try
		{	ksHandler.store();
			fail("should throw a Exception");
		}
		catch (KeyStoreException e)
		{
			assertTrue("should be FileNotFound",e.getMessage().contains("File not Found"));
		}
	}
	
	@Test
	public void testChangePassword_WithException() throws Exception
	{
		File ksFile = new File(ks_PathClean+"ChangePWException");
		FileCopy.copyFile(ks_PathClean, ksFile.getAbsolutePath());
		
		
		UBERKeyStoreHandler ksHandler = new UBERKeyStoreHandler(ksFile, ks_Password.toCharArray());
		
		Mockit.redefineMethods(FileOutputStream.class, MockedFileOutputStream.class);

		try
		{	ksHandler.changePassword(ks_Password.toCharArray(), "newPassword".toCharArray());
			fail("should throw a Exception");
		}
		catch (KeyStoreException e)
		{
			assertTrue("should be FileNotFound",e.getMessage().contains("File not Found"));
		}
	}

	
	
}
class MockedFileOutputStream
{ 
	public MockedFileOutputStream(String s) throws FileNotFoundException { 
		throw new FileNotFoundException("Mocked Exception");
	}
} 
