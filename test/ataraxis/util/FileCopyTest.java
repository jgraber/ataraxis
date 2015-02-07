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

package ataraxis.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.TestingHelper;
import ataraxis.misc.AtaraxisFileComparator;
import ataraxis.util.FileCopy;


public class FileCopyTest 
{

	private static final Logger logger = LogManager.getLogger(FileCopyTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	private static final String TEST_DIR_DATA = TEST_DIR + "/testrun/FileCopy";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	private static String originalFile = TEST_DIR_DATA + "/originalFile.txt";
	private static String copyFile = TEST_DIR_DATA + "/copyFile.txt";
	
	@BeforeClass 
	public static void initClass() throws IOException
	{
		PropertyConfigurator.configure(LOG_PROPS_FILE);
		logger.info("Start FileCopyTest");
		(new File(TEST_DIR_DATA)).mkdirs();
		
		File oFile = new File(originalFile);
		if(oFile.exists())
		{
			oFile.delete();
		}
		
		File cFile = new File(copyFile);
		if(cFile.exists())
		{
			cFile.delete();
		}
	}
	
	@Test
	public void testCopyFiles_Strings() throws IOException
	{
		TestingHelper.removeFileIfExist(originalFile);
		TestingHelper.removeFileIfExist(copyFile);
		createFile();
		
		assertTrue("should not be empty", new File(originalFile).length() > 0);
		
		
		FileCopy.copyFile(originalFile, copyFile);	
		
		AtaraxisFileComparator afc = new AtaraxisFileComparator();
		
		assertTrue("should not be empty", new File(originalFile).length() > 0);
		
		assertTrue("should be same", afc.areFilesEquals(new File(originalFile), new File(copyFile)));
	}
	
	@Test
	public void testCopyFiles_Files() throws IOException
	{
		TestingHelper.removeFileIfExist(originalFile);
		TestingHelper.removeFileIfExist(copyFile);
		createFile();
		
		assertTrue("should not be empty", new File(originalFile).length() > 0);
		
		
		FileCopy.copyFile(new File(originalFile), new File(copyFile));	
		
		AtaraxisFileComparator afc = new AtaraxisFileComparator();
		
		assertTrue("should not be empty", new File(originalFile).length() > 0);
		
		assertTrue("should be same", afc.areFilesEquals(new File(originalFile), new File(copyFile)));
	}
	
	@Test
	public void testConstructor() throws IOException
	{
		FileCopy fc = new FileCopy();
		assertTrue(fc != null);
	}
	
	private static void createFile() throws IOException
	{		
		FileWriter fileWriter = new FileWriter(originalFile);
		fileWriter.write("This is a simple file with some text inside");
		fileWriter.flush();
		fileWriter.close();
	}	
}