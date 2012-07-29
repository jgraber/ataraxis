/* ----------------------------------------------------------------------------
 * Copyright 2006 - 2010 Johnny Graber & Andreas Muedespacher
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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import junit.framework.Assert;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.TestingHelper;
import ataraxis.crypt.AESKeyCreatorTest;
import ataraxis.misc.AtaraxisBackup;
import ataraxis.util.FileCopy;

import java.io.ByteArrayOutputStream;


/**
 * AtaraxisBackupTest is the test class for AtaraxisBackup
 *
 * @author J. Graber & A. Muedespacher
 * @version 1.2
 *
 */
public class AtaraxisBackupTest 
{
	private static final Logger logger = Logger.getLogger(AESKeyCreatorTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	private static final String TEST_DIR_DATA = TEST_DIR + "/AtaraxisBackupTest";
	private static final String TEST_DIR_DIRECTORY = TEST_DIR_DATA + "/HomeDirectory";
	private static final String TEST_DIR_DIRECTORY_USER = TEST_DIR_DIRECTORY + "/UserA";
	private static final String TEST_DIR_DIRECTORY_USER_FILE = TEST_DIR_DIRECTORY_USER + "/comment.txt";
	private static final String USER_FILE_CONTENT = "This is a simple little text!";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		PropertyConfigurator.configure(LOG_PROPS_FILE); 
		logger.debug("AtaraxisBackupTest startet");
	
		
		prepareBackupContent();
		
		logger.debug("AtaraxisBackupTest ready to go");
	}

	/**
	 * Prepare all data for the zip file
	 * @throws IOException
	 */
	private static void prepareBackupContent() throws IOException {

		File directoryForZipTest = new File(TEST_DIR_DIRECTORY);
		TestingHelper.deleteDir(directoryForZipTest);
		
		directoryForZipTest.mkdirs();
		File userHome = new File(TEST_DIR_DIRECTORY_USER);
		userHome.mkdir();
		
		File textFile = new File(TEST_DIR_DIRECTORY_USER_FILE);
		FileWriter fw = new FileWriter(textFile);
		fw.write(USER_FILE_CONTENT);
		fw.flush();
		fw.close();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}



	/**
	 * Test Method CreateZipFile()
	 * @throws Exception
	 */
	@Test
	public void testCreateZipFile() throws Exception 
	{
		String zipName = "testCreateZipFile.zip";
		File zipFile = new File(TEST_DIR_DATA, zipName);
		TestingHelper.removeFileIfExist(zipFile.getAbsolutePath());
		
		AtaraxisBackup ab = new AtaraxisBackup(new Shell(new Display()));
		boolean zipOK = ab.createZipFile(TEST_DIR_DATA + "/" + zipName, TEST_DIR_DIRECTORY);
		
		Assert.assertTrue("Creation of Zip failed", zipOK);
		Assert.assertTrue("Zip file is missing", zipFile.exists());
		
		validateZipFile(zipFile);
	}
	
	private void validateZipFile(File zipFile) throws Exception
	{
		boolean foundHome = false;
		boolean foundUser = false;
		boolean foundTextFile = false;
		
		
		ZipFile zippedFile = new ZipFile(zipFile);
		Enumeration<?> entries = zippedFile.entries();
		while(entries.hasMoreElements()) {
	        ZipEntry entry = (ZipEntry)entries.nextElement();

	        if(entry.isDirectory()) {

	        	if(entry.getName().equals("HomeDirectory/"))
	        	{
	        		foundHome = true;
	        	}
	        	else if (entry.getName().equals(String.format("HomeDirectory%1$sUserA/",System.getProperty("file.separator"))))
	        	{
	        		foundUser = true;
	        	}
	        }
	        else
	        {
	        	if(entry.getName().equals(String.format("HomeDirectory%1$sUserA%1$scomment.txt",System.getProperty("file.separator"))))
	        	{
	        		foundTextFile = true;
	        		ByteArrayOutputStream bout = new ByteArrayOutputStream();
	        		FileCopy.copyFile(zippedFile.getInputStream(entry), bout);
	        		Assert.assertEquals("Content of file does not match", USER_FILE_CONTENT, bout.toString());
	        		
	        	}
	        }
		}		
		zippedFile.close();

		Assert.assertTrue("Did not find HomeDirectory", foundHome);
		Assert.assertTrue("Did not find UserA", foundUser);
		Assert.assertTrue("Did not find comment.txt", foundTextFile);
	}

}
