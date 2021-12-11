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

import mockit.Mock;
import mockit.MockUp;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.crypt.JurisdictionPolicyError;
import ataraxis.misc.AtaraxisUpdateInfo;
import ataraxis.misc.NetworkUpdateCheck;


public class AtaraxisUpdateInfoTest 
{
	private static final Logger logger = LogManager.getLogger(AtaraxisUpdateInfoTest.class);
	private static final String networkConfigFile =  System.getProperty("user.dir") + 
	"/test/fixtures/networkTEST.properties";
	private static final String currentVersion = "1.9.0";
	private static final String oldVersion = "1.1.0";
	private static final String nextMajorVersion = "2.0.0";
	
	@BeforeClass 
	public static void initClass() throws KeyStoreException, IOException, JurisdictionPolicyError
	{
		logger.info("Start PasswordGeneratorTest");
	}
	
	
	@Test
	public void olderVersion_existNewer() throws IOException
	{
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo();
		boolean existNewer = updateInfo.existNewerVersion(oldVersion);
		assertTrue(existNewer);
	}
	
	@Test
	public void newerVersion_noNewerVersion() throws IOException
	{
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo();
		boolean existNewer = updateInfo.existNewerVersion(nextMajorVersion);
		assertFalse(existNewer);
	}
	
	@Test
	public void wrongVersion_existNewer() throws IOException
	{
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo();
		boolean existNewer = updateInfo.existNewerVersion("a.b.c");
		assertTrue(existNewer);
	}
	
	@Test
	public void wrongVersion_testFields() throws IOException
	{
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo();
		updateInfo.existNewerVersion(nextMajorVersion);
		assertFalse(nextMajorVersion.equals(updateInfo.getCurrentVersion()));
		assertEquals("https://github.com/jgraber/ataraxis/",updateInfo.getCurrentURL());
	}
	
	@Test
	public void constructorNetworkUpdate_testOriginalProperties() throws IOException
	{
		Properties configuration = new Properties();
		configuration.load(new FileInputStream(networkConfigFile));
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo(new NetworkUpdateCheck(configuration));
		
		updateInfo.existNewerVersion(nextMajorVersion);
		assertEquals(currentVersion, updateInfo.getCurrentVersion());
		assertEquals("https://github.com/jgraber/ataraxis/",updateInfo.getCurrentURL());
	}
	
	@Test(expected=IOException.class)
	public void constructorNetworkUpdate_testWrongURL() throws IOException
	{
		Properties configuration = new Properties();
		configuration.load(new FileInputStream(networkConfigFile));
		configuration.setProperty("ATARAXIS.NETWORK.UPDATEURL", "https://jgraber.ch/AtaraxiS/updateinfo.php_dontExist");
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo(new NetworkUpdateCheck(configuration));
		
		updateInfo.existNewerVersion(nextMajorVersion);
	}
	
	@Test
	public void constructorNetworkUpdate_useProxy() throws IOException
	{
		Properties configuration = new Properties();
		configuration.load(new FileInputStream(networkConfigFile));
		configuration.setProperty("ATARAXIS.NETWORK.USE_PROXY", "TRUE");
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo(new NetworkUpdateCheck(configuration));
		
		updateInfo.existNewerVersion(nextMajorVersion);
		assertEquals(currentVersion, updateInfo.getCurrentVersion());
	}

	@Test
	public void AtaraxisUpdateInfo_ErrorOnProperties_LoadInsted() throws Exception
	{
		new MockUp<java.util.Properties>(){
			   @Mock
			   public void load(InputStream inStream) throws IOException
				{
					throw new IOException();
				}
		};
		
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo();
		assertNotNull(updateInfo);
	}
	
	
	@Test(expected=IOException.class)
	public void AtaraxisUpdateInfo_TimeOut_LoadInsted() throws Exception
	{
		new MockUp<java.util.Properties>(){
			   @Mock
			   public void load(InputStream inStream) throws IOException
				{
					throw new SocketTimeoutException();
				}
		};
		
		AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo();
		boolean newerVersion = updateInfo.existNewerVersion(oldVersion);
		assertTrue(newerVersion);
		assertNotNull(updateInfo);
		
		System.out.println("->"+updateInfo.getCurrentURL());
		System.out.println(updateInfo.getCurrentVersion());
	}

}
