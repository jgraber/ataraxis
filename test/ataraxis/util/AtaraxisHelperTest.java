/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2015 Johnny Graber & Andreas Muedespacher
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

package ataraxis.util;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.TestingHelper;
import ataraxis.crypt.AtaraxisCrypter;
import ataraxis.util.AtaraxisHelper;


public class AtaraxisHelperTest {
	
	private static final Logger logger = LogManager.getLogger(AtaraxisHelperTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	private static final String TEST_DIR_DATA = TEST_DIR + "/testrun/AtaraxisHelperTest";
	private static final String TEST_DIR_USER_A = TEST_DIR_DATA + "/UserA";
	private static final String KSFILE_USER_A = TEST_DIR_USER_A + "/keystore.ks";
	private static final String PATH_PROPS_USER_A = TEST_DIR_USER_A + "/user.props";
	private static AtaraxisCrypter ac_UserA;
	
	@BeforeClass 
	public static void initClass() throws IOException, Exception
	{
		logger.info("Start FileCopyTest");
		
		File userAFiles = new File(TEST_DIR_USER_A);
		if(userAFiles.exists())
		{
			TestingHelper.deleteDir(userAFiles);
		}
		
		ac_UserA = new AtaraxisCrypter(new File(KSFILE_USER_A), "pass".toCharArray(), true);
	}

	private static void CreateUserProperty(String pathPropsUserA, String language) throws Exception 
	{
		final Properties userProps = new Properties();
    	userProps.setProperty("user.lang", language);
    	final FileOutputStream userFile = new FileOutputStream(pathPropsUserA);
    	userProps.store(userFile, null);		
	}
	
	private static void CreateUserProperty(String pathPropsUserA) throws Exception 
	{
		final Properties userProps = new Properties();
    	final FileOutputStream userFile = new FileOutputStream(pathPropsUserA);
    	userProps.store(userFile, null);		
	}

	@Test
	public void GetUserName_FromKSPath() throws IOException
	{
		AtaraxisHelper ah = new AtaraxisHelper();
				
		assertEquals("UserA", ah.GetUserName(ac_UserA.getKeyStorePath()));		
	}
	
	@Test
	public void GetUserName_FromAtaraxisCrypter() throws IOException
	{
		AtaraxisHelper ah = new AtaraxisHelper();
				
		assertEquals("UserA", ah.GetUserName(ac_UserA));		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void GetUserName_WithNullAsPath()
	{
		AtaraxisHelper ah = new AtaraxisHelper();
		String empty = "";
				
		assertEquals("UserA", ah.GetUserName(empty));		
	}
	
	@Test
	public void GetUserLanguage_FromKSPath_de() throws Exception
	{
		CreateUserProperty(PATH_PROPS_USER_A, "de");
		AtaraxisHelper ah = new AtaraxisHelper();
		assertEquals("de", ah.GetUserLanguage(ac_UserA.getKeyStorePath()));
	}
	
	@Test
	public void GetUserLanguage_FromKSPath_fr() throws Exception
	{
		CreateUserProperty(PATH_PROPS_USER_A, "fr");
		AtaraxisHelper ah = new AtaraxisHelper();
		assertEquals("fr", ah.GetUserLanguage(ac_UserA.getKeyStorePath()));
	}
	
	@Test
	public void GetUserLanguage_FromKSPath_Empty_Must_Return_En() throws Exception
	{
		CreateUserProperty(PATH_PROPS_USER_A);
		AtaraxisHelper ah = new AtaraxisHelper();
		assertEquals("en", ah.GetUserLanguage(ac_UserA.getKeyStorePath()));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void GetUserLanguage_FromKSPath_EmptyPath() throws Exception
	{
		AtaraxisHelper ah = new AtaraxisHelper();
		assertEquals("en", ah.GetUserLanguage(""));
	}
}
