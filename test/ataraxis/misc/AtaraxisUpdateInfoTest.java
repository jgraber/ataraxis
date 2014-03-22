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

package ataraxis.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.security.KeyStoreException;
import java.util.Properties;

import mockit.Mockit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.crypt.JurisdictionPolicyError;
import ataraxis.misc.AtaraxisUpdateInfo;
import ataraxis.misc.NetworkUpdateCheck;


public class AtaraxisUpdateInfoTest 
{
	private static final Logger logger = Logger.getLogger(AtaraxisUpdateInfoTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	private static final String networkConfigFile =  System.getProperty("user.dir") + 
	"/application_data/config/network.properties";
	
	@BeforeClass 
	public static void initClass() throws KeyStoreException, IOException, JurisdictionPolicyError
	{
		PropertyConfigurator.configure(LOG_PROPS_FILE); 
		logger.info("Start PasswordGeneratorTest");
	}
	
	
	@Test
	public void olderVersion_existNewer() throws IOException
	{
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo();
		boolean existNewer = updateInfo.existNewerVersion("1.1.0");
		assertTrue(existNewer);
	}
	
	@Test
	public void newerVersion_noNewerVersion() throws IOException
	{
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo();
		boolean existNewer = updateInfo.existNewerVersion("2.0.0");
		assertFalse(existNewer);
	}
	
	@Test
	public void wrongVersion_existNewer() throws IOException
	{
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo();
		boolean existNewer = updateInfo.existNewerVersion("3.0.0");
		assertTrue(existNewer);
	}
	
	@Test
	public void wrongVersion_testFields() throws IOException
	{
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo();
		updateInfo.existNewerVersion("3.0.0");
		assertFalse("3.0.0".equals(updateInfo.getCurrentVersion()));
		assertEquals("http://github.com/jgraber/ataraxis/",updateInfo.getCurrentURL());
	}
	
	@Test
	public void constructorNetworkUpdate_testOriginalProperties() throws IOException
	{
		Properties configuration = new Properties();
		configuration.load(new FileInputStream(networkConfigFile));
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo(new NetworkUpdateCheck(configuration));
		
		updateInfo.existNewerVersion("3.0.0");
		assertEquals("1.4.0",updateInfo.getCurrentVersion());
		assertEquals("http://github.com/jgraber/ataraxis/",updateInfo.getCurrentURL());
	}
	
	@Test(expected=IOException.class)
	public void constructorNetworkUpdate_testWrongURL() throws IOException
	{
		Properties configuration = new Properties();
		configuration.load(new FileInputStream(networkConfigFile));
		configuration.setProperty("ATARAXIS.NETWORK.UPDATEURL", "http://jgraber.ch/AtaraxiS/updateinfo.php_dontExist");
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo(new NetworkUpdateCheck(configuration));
		
		updateInfo.existNewerVersion("3.0.0");
	}
	
	@Test
	public void constructorNetworkUpdate_useProxy() throws IOException
	{
		Properties configuration = new Properties();
		configuration.load(new FileInputStream(networkConfigFile));
		configuration.setProperty("ATARAXIS.NETWORK.USE_PROXY", "TRUE");
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo(new NetworkUpdateCheck(configuration));
		
		updateInfo.existNewerVersion("3.0.0");
		assertEquals("1.4.0",updateInfo.getCurrentVersion());
	}

	@Test
	public void AtaraxisUpdateInfo_ErrorOnProperties_LoadInsted() throws Exception
	{
		Mockit.redefineMethods(java.util.Properties.class, new Object() {
			@SuppressWarnings("unused")
			public void load(InputStream inStream) throws IOException
			{
				throw new IOException();
			}
		});
		
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo();
		assertNotNull(updateInfo);
	}
	
	
	@Test(expected=IOException.class)
	public void AtaraxisUpdateInfo_TimeOut_LoadInsted() throws Exception
	{
		Mockit.redefineMethods(java.util.Properties.class, new Object() {
			@SuppressWarnings("unused")
			public void load(InputStream inStream) throws IOException
			{
				throw new SocketTimeoutException();
			}
		});
		
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo();
		boolean newerVersion = updateInfo.existNewerVersion("1.0.0");
		assertTrue(newerVersion);
		assertNotNull(updateInfo);
		
		System.out.println("->"+updateInfo.getCurrentURL());
		System.out.println(updateInfo.getCurrentVersion());
	}
}
