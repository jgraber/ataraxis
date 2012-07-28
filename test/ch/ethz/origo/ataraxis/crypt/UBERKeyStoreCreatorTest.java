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

package ch.ethz.origo.ataraxis.crypt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import mockit.Mockit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class UBERKeyStoreCreatorTest 
{

	private static final Logger logger = Logger.getLogger(UBERKeyStoreCreatorTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	private static final String TEST_DIR_DATA = TEST_DIR + "/ueberKSCreator_data";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	
	private static String ks_Password = "thisisA";
	private static String ks_Path = TEST_DIR_DATA + "/ueber.ks";
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
		PropertyConfigurator.configure(LOG_PROPS_FILE);
		(new File(TEST_DIR_DATA)).mkdirs();
		File ksFile = new File(ks_Path);
		if(ksFile.exists())
		{
			ksFile.delete();
		}
		logger.info("ksCreator setUpBeforeClass ok");
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception 
	{
	
	}

	@Test
	public void testCreateKeyStore() throws Exception 
	{
		
		UBERKeyStoreCreator ksCreator = new UBERKeyStoreCreator();
		ksCreator.createKeyStore(new File(ks_Path), ks_Password.toCharArray());
		File ksFile = new File(ks_Path);
		if(!ksFile.exists())
		{
			fail("KS not created");
		}
		KeyStore javaKS = KeyStore.getInstance("UBER", "BC");
		javaKS.load(new FileInputStream(ks_Path), ks_Password.toCharArray());
		assertEquals(javaKS.getType(),"UBER");
			
		logger.info("ksCreator works");		
	}
	
	@Test
	public void testCreateKeyStore_FileIsDir() throws Exception 
	{ 
		File dir = new File(TEST_DIR_DATA+"/isADir");
		dir.mkdir();
		
		try
		{
			UBERKeyStoreCreator ksCreator = new UBERKeyStoreCreator();
			ksCreator.createKeyStore(dir, "aaaaaa".toCharArray());
			fail("KeyStore-File is a directory");
		}
		catch (KeyStoreException e)
		{
			assertTrue(e.getMessage().contains("Error on FileHandling"));
		}
	}
		
	
	@Test
	public void testCreateKeyStore_saveIOException() throws Exception
	{
		
		File ksFile = new File(ks_Path+"_io");
		Mockit.redefineMethods(KeyStore.class, new Object() {
			@SuppressWarnings("unused")
			public final void store(OutputStream stream, char[] password)
                 throws KeyStoreException,IOException,NoSuchAlgorithmException,
                        CertificateException
			{
				throw new IOException();
			}
		});
		
		try
		{
			UBERKeyStoreCreator ksCreator = new UBERKeyStoreCreator();
			ksCreator.createKeyStore(ksFile, "aaaaaa".toCharArray());
			fail("Exception missing!");
		}
		catch (KeyStoreException e)
		{
			assertTrue("IO Exception Message wrong",e.getMessage().contains("IO Error on writing the KeyStore"));
		}
	}
	
	@Test
	public void testCreateKeyStore_CertificateException() throws Exception
	{
		File ksFile = new File(ks_Path+"_io");
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
			UBERKeyStoreCreator ksCreator = new UBERKeyStoreCreator();
			ksCreator.createKeyStore(ksFile, "aaaaaa".toCharArray());
			fail("Exception missing!");
		}
		catch (KeyStoreException e)
		{
			assertTrue(e.getMessage().contains("Certificate Error on KeyStore"));
		}
	}
}
