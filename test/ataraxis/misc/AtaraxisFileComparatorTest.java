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

package ataraxis.misc;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import mockit.Mockit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.crypt.AESKeyCreatorTest;
import ataraxis.misc.AtaraxisFileComparator;
import ataraxis.misc.AtaraxisHashCreator;
import ataraxis.misc.HashingDigest;
import ataraxis.util.FileCopy;


public class AtaraxisFileComparatorTest 
{

	private static final Logger logger = Logger.getLogger(AESKeyCreatorTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	private static final String TEST_DIR_DATA = TEST_DIR + "/fileCompareTest";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	
	private static final File FILE_SMALL = new File(TEST_DIR + "/performance.xls");
	private static final File FILE_BIG = new File(TEST_DIR + "/wallstreet.mp3");
	private static AtaraxisFileComparator s_hashCreator;
	private static File DIR_A, DIR_B;
	private static File DIR_C, DIR_D;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
		PropertyConfigurator.configure(LOG_PROPS_FILE); 
		logger.debug("AtaraxisFileComparatorTest startet");
	
		s_hashCreator = new AtaraxisFileComparator();
		
		DIR_A = new File(TEST_DIR_DATA+"/DirA");
		DIR_A.mkdirs();
		DIR_B = new File(TEST_DIR_DATA+"/DirB");
		DIR_B.mkdirs();
		DIR_C = new File(TEST_DIR_DATA+"/DirC");
		DIR_C.mkdirs();
		DIR_D = new File(TEST_DIR_DATA+"/DirD");
		DIR_D.mkdirs();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception 
	{
	
	}

	@Test
	public void testFileFileEquals() throws IOException 
	{
		assertTrue(s_hashCreator.areFilesEquals(FILE_SMALL, FILE_SMALL));
	}
	
	@Test
	public void testFileFileOK() throws IOException 
	{
		String newFilePath = TEST_DIR_DATA+"/newSmallFile";
		FileCopy.copyFile(FILE_SMALL.getAbsolutePath(), newFilePath);
		File bFile = new File(newFilePath);
		assertTrue(s_hashCreator.areFilesEquals(FILE_SMALL, bFile));
	}
	
	@Test
	public void testFileFileFalse() throws IOException 
	{
		assertFalse(s_hashCreator.areFilesEquals(FILE_SMALL, FILE_BIG));
	}
	
	@Test
	public void testFileDir() throws IOException 
	{
		assertFalse(s_hashCreator.areFilesEquals(FILE_SMALL, DIR_A));
		assertFalse(s_hashCreator.areFilesEquals(DIR_B, FILE_SMALL));
	}
	
	@Test
	public void testDirDirOK() throws IOException 
	{
		FileCopy.copyFile(FILE_SMALL.getAbsolutePath(), DIR_A.getAbsolutePath()+"/a.file");
		FileCopy.copyFile(FILE_SMALL.getAbsolutePath(), DIR_A.getAbsolutePath()+"/c.file");
		FileCopy.copyFile(FILE_SMALL.getAbsolutePath(), DIR_B.getAbsolutePath()+"/a.file");
		FileCopy.copyFile(FILE_SMALL.getAbsolutePath(), DIR_B.getAbsolutePath()+"/c.file");
		
		assertTrue(s_hashCreator.areFilesEquals(DIR_A, DIR_B));
		assertTrue(s_hashCreator.areFilesEquals(DIR_B, DIR_A));
	}
	
	@Test
	public void testDirDirFalse() throws IOException 
	{
		FileCopy.copyFile(FILE_SMALL.getAbsolutePath(), DIR_C.getAbsolutePath()+"/a.file");
		FileCopy.copyFile(FILE_SMALL.getAbsolutePath(), DIR_C.getAbsolutePath()+"/c.file");
		FileCopy.copyFile(FILE_SMALL.getAbsolutePath(), DIR_D.getAbsolutePath()+"/a.file");
		//FileCopy.copyFile(FILE_BIG.getAbsolutePath(), DIR_D.getAbsolutePath()+"/c.file");
		(new File(DIR_D.getAbsolutePath()+"/c.file")).mkdirs();
		
		assertFalse(s_hashCreator.areFilesEquals(DIR_C, DIR_D));
		assertFalse(s_hashCreator.areFilesEquals(DIR_D, DIR_C));
	}
	
	@Test
	public void testDirDirEquals() throws IOException 
	{
		assertTrue(s_hashCreator.areFilesEquals(DIR_A, DIR_A));
	}

	@Test(expected=NullPointerException.class)
	public void testAtaraxisFileComparator_NoSuchAlgorithmException() throws Exception 
	{
		Mockit.redefineMethods(AtaraxisHashCreator.class, MockAtaraxisHashCreator.class);

		AtaraxisFileComparator afc = new AtaraxisFileComparator();
		assertTrue(afc.areFilesEquals(DIR_A, DIR_B)); // No Comparator => Null Pointer Exception
				
	}
}

class MockAtaraxisHashCreator 
{
	public MockAtaraxisHashCreator(HashingDigest digestAlgorithm) throws NoSuchAlgorithmException
	{
		throw new NoSuchAlgorithmException("Mocked Exception");
	}
}
	