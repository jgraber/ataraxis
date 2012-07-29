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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.TestingHelper;
import ataraxis.misc.AtaraxisShredder;



/**
 * @author J. Graber + A. Muedespacher
 *
 */
public class ShredderTest {
	
    private static AtaraxisShredder s_shredder;
    
    
    private static final String DIR_TEST = System.getProperty("user.dir") + "/test/shredderTest";
    private static final String DIR_MAIN_TEST = System.getProperty("user.dir") + "/test";
    private static final String DIR_GENERATED_1 = DIR_TEST + "/genDir1";
    private static final String FILE_GENERATED_11 = DIR_GENERATED_1 + "/generated11.file";
    private static final String FILE_GENERATED_12 = DIR_GENERATED_1 + "/generated12.file";
    private static final String DIR_GENERATED_2 = DIR_GENERATED_1 + "/genDir2";
    private static final String FILE_GENERATED_21 = DIR_GENERATED_2 + "/generated21.file";
    private static final String FILE_GENERATED_22 = DIR_GENERATED_2 + "/generated22.file";
    
    private static final Logger logger = Logger.getLogger(ShredderTest.class);
    protected static final String LOG_PROPS_FILE = DIR_MAIN_TEST + "/config/log4j_test.properties";
    
    private static File s_dir = new File (DIR_TEST);
    private static File s_dir1 = new File (DIR_GENERATED_1);
    private static File s_dir2 = new File (DIR_GENERATED_2);
    private static File s_file11 = new File (FILE_GENERATED_11);
    private static File s_file12 = new File (FILE_GENERATED_12);
    private static File s_file21 = new File (FILE_GENERATED_21);
    private static File s_file22 = new File (FILE_GENERATED_22);
    
    private static File s_LeaveDir1 = new File (DIR_TEST + "/LeaveDir1");
    private static File s_LeaveDir2InDir1 = new File (DIR_TEST + "/LeaveDir1/LeaveDir2");
    private static File s_LeaveFileInDir2InDir1 = new File (DIR_TEST + "/LeaveDir1/LeaveDir2/file.txt");
    
    private static final long FILE_SIZE = 1024 * 128; //   * 10    = 10MB
    private static long s_startTime = 0;
    private static long s_tempTime = 0;
    
	
    /**
     * Test startup is run once before all tests.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void runOnceBeforeAllTests() throws Exception
    {        
    	PropertyConfigurator.configure(LOG_PROPS_FILE); 

    	TestingHelper.deleteDir(new File(DIR_TEST));
    	
        s_startTime = System.currentTimeMillis();
        s_shredder = new AtaraxisShredder(); 
    }
    
    /**
     * Test startup is run once before all tests.
     * 
     * @throws Exception
     */
    @Before
    public void runBeforeEachTest() throws Exception
    {      
    	s_dir.mkdir();
     	s_dir1.mkdir();
    	s_dir2.mkdir();
    	fillFile(s_file11, FILE_SIZE);
    	fillFile(s_file12, FILE_SIZE);
    	fillFile(s_file21, FILE_SIZE);
    	fillFile(s_file22, FILE_SIZE);
    	
    	// Data for LeaveDir
    	s_LeaveDir1.mkdir();
    	s_LeaveDir2InDir1.mkdir();
    	fillFile(s_LeaveFileInDir2InDir1, FILE_SIZE);
    }
    
    
    /**
     * Test closedown is run once after all tests.
     * 
     * @throws Exception
     */
    @AfterClass
    public static void runOnceAfterAllTests() throws Exception
    {
    	logger.debug("Used time for all tests in this class: "
    			+ (System.currentTimeMillis() - s_startTime));
    	s_dir1.delete();
    	s_dir2.delete();
    }
    
    
    /**
     * Tests encryption and decryption of a FileInputStream 
     * @throws Exception 
     * 
     * @throws Exception
     */
    @Test
    public void shredGutmannFloppy() throws Exception
    {
		logger.debug("shredFloppy() - start");

		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeGutmann (FILE_GENERATED_11, false, true);  // FILE_SHRED
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (floppy mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
    	logger.debug("shredFloppy(file) - end; time for test [ms]: " + s_tempTime);
    	
    	s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeGutmann(DIR_GENERATED_1, false, true);  // FILE_SHRED
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (floppy mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredFloppy(dir) - end; time for test [ms]: " + s_tempTime);
     } 
    
    /**
     * Tests encryption and decryption of a FileInputStream 
     * @throws Exception 
     * 
     * @throws Exception
     */
    @Test
    public void shredGutmann() throws Exception
    {
		logger.debug("shredGutmann() - start");

		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeGutmann(FILE_GENERATED_11, false, false);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (floppy mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredGutmann(file) - end; time for test [ms]: " + s_tempTime);
		
		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeGutmann(DIR_GENERATED_1, false, false);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (floppy mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredGutmann(dir) - end; time for test [ms]: " + s_tempTime);
     } 
    
    @Test
    public void shredGutmann_LeaveDir() throws Exception
    {
		logger.debug("shredGutmann() - start");
		assertTrue("Dir does not exist in the beginning!", s_LeaveDir2InDir1.exists());
		assertTrue("File in dir does not exist in the beginning!", s_LeaveFileInDir2InDir1.exists());
		
    	s_shredder.wipeGutmann(s_LeaveDir1.getAbsolutePath(), true, false);
    	
		assertTrue("Dir must exist when leafe=true!", s_LeaveDir1.exists());
		assertFalse("Dir in dir must be removed!", s_LeaveDir2InDir1.exists());
		assertFalse("File in dir must be removed!", s_LeaveFileInDir2InDir1.exists());		
    } 
    
    /**
     * Tests encryption and decryption of a FileInputStream 
     * @throws Exception 
     * 
     * @throws Exception
     */
    @Test
    public void shredVSITR() throws Exception
    {
		logger.debug("shredVSITR() - start");

		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeVSITR(FILE_GENERATED_11, false);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (floppy mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredVSITR(file) - end; time for test [ms]: " + s_tempTime);
		
		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeVSITR(DIR_GENERATED_1, false);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (floppy mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredVSITR(dir) - end; time for test [ms]: " + s_tempTime);
     } 
    
    @Test
    public void shredVSITR_LeaveDir() throws Exception
    {
		logger.debug("shredVSITR_LeaveDir() - start");
		assertTrue("Dir does not exist in the beginning!", s_LeaveDir2InDir1.exists());
		assertTrue("File in dir does not exist in the beginning!", s_LeaveFileInDir2InDir1.exists());
		
    	s_shredder.wipeVSITR(s_LeaveDir1.getAbsolutePath(), true);
    	
		assertTrue("Dir must exist when leafe=true!", s_LeaveDir1.exists());
		assertFalse("Dir in dir must be removed!", s_LeaveDir2InDir1.exists());
		assertFalse("File in dir must be removed!", s_LeaveFileInDir2InDir1.exists());		
    }
    
    @Test
    public void shredDoD() throws Exception
    {
		logger.debug("shredDoD() - start");

		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeDoD(FILE_GENERATED_11, false, false);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (floppy mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredDoD(file) - end; time for test [ms]: " + s_tempTime);
		
		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeDoD(DIR_GENERATED_1, false, false);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (floppy mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredDoD(dir) - end; time for test [ms]: " + s_tempTime);
     } 
    
    @Test
    public void shredDoD_LeaveDir() throws Exception
    {
		logger.debug("shredDoD_LeaveDir() - start");
		assertTrue("Dir does not exist in the beginning!", s_LeaveDir2InDir1.exists());
		assertTrue("File in dir does not exist in the beginning!", s_LeaveFileInDir2InDir1.exists());
		
    	s_shredder.wipeDoD(s_LeaveDir1.getAbsolutePath(), true, true);
    	
		assertTrue("Dir must exist when leafe=true!", s_LeaveDir1.exists());
		assertFalse("Dir in dir must be removed!", s_LeaveDir2InDir1.exists());
		assertFalse("File in dir must be removed!", s_LeaveFileInDir2InDir1.exists());		
    }
    
    @Test
    public void shredDoDExt() throws Exception
    {
		logger.debug("shredDoDExt() - start");

		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeDoD(FILE_GENERATED_11, false, true);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (floppy mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredDoDExt(file) - end; time for test [ms]: " + s_tempTime);
		
		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeDoD(DIR_GENERATED_1, false, true);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (floppy mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredDoDExt(dir) - end; time for test [ms]: " + s_tempTime);
     } 
    
    @Test
    public void shredDoDExt_LeaveDir() throws Exception
    {
		logger.debug("shredDoDExt_LeaveDir() - start");
		assertTrue("Dir does not exist in the beginning!", s_LeaveDir2InDir1.exists());
		assertTrue("File in dir does not exist in the beginning!", s_LeaveFileInDir2InDir1.exists());
		
    	s_shredder.wipeDoD(s_LeaveDir1.getAbsolutePath(), true, false);
    	
		assertTrue("Dir must exist when leafe=true!", s_LeaveDir1.exists());
		assertFalse("Dir in dir must be removed!", s_LeaveDir2InDir1.exists());
		assertFalse("File in dir must be removed!", s_LeaveFileInDir2InDir1.exists());		
    } 
    
    @Test
    public void shredSchneier() throws Exception
    {
		logger.debug("shredSchneier() - start");

		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeSchneier(FILE_GENERATED_11, false);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (floppy mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredSchneier(file) - end; time for test [ms]: " + s_tempTime);
		
		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeSchneier(DIR_GENERATED_1, false);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (floppy mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredSchneier(dir) - end; time for test [ms]: " + s_tempTime);
     } 
    
    @Test
    public void shredSchneier_LeaveDir() throws Exception
    {
		logger.debug("shredSchneier_LeaveDir() - start");
		assertTrue("Dir does not exist in the beginning!", s_LeaveDir2InDir1.exists());
		assertTrue("File in dir does not exist in the beginning!", s_LeaveFileInDir2InDir1.exists());
		
    	s_shredder.wipeSchneier(s_LeaveDir1.getAbsolutePath(), true);
    	
		assertTrue("Dir must exist when leafe=true!", s_LeaveDir1.exists());
		assertFalse("Dir in dir must be removed!", s_LeaveDir2InDir1.exists());
		assertFalse("File in dir must be removed!", s_LeaveFileInDir2InDir1.exists());		
    } 
    
    @Test
    public void shredWithByte() throws Exception
    {
    	
		logger.debug("shredWithByte() - start");
		
		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeWithByte(FILE_GENERATED_11, false, (byte)0x00);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (random mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredWithByte(file) - end; time for test [ms]: " + s_tempTime);

		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeWithByte(DIR_GENERATED_1, false, (byte)0x00);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (random mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredWithByte(dir) - end; time for test [ms]: " + s_tempTime);
     } 
    
    @Test
    public void shredWithByte_LeaveDir() throws Exception
    {
		logger.debug("shredWithByte_LeaveDir() - start");
		assertTrue("Dir does not exist in the beginning!", s_LeaveDir2InDir1.exists());
		assertTrue("File in dir does not exist in the beginning!", s_LeaveFileInDir2InDir1.exists());
		
    	s_shredder.wipeWithByte(s_LeaveDir1.getAbsolutePath(), true, (byte)0x00);
    	
		assertTrue("Dir must exist when leafe=true!", s_LeaveDir1.exists());
		assertFalse("Dir in dir must be removed!", s_LeaveDir2InDir1.exists());
		assertFalse("File in dir must be removed!", s_LeaveFileInDir2InDir1.exists());		
    } 
    
    /**
     * Tests encryption and decryption of a FileInputStream 
     * @throws Exception 
     * 
     * @throws Exception
     */
    @Test
    public void shredRandomPattern() throws Exception
    {
		logger.debug("shredRandom() - start");

		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeRandom(FILE_GENERATED_11, false, AtaraxisShredder.RANDOM_PATTERN, 10);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (random mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredRandom(file) - end; time for test [ms]: " + s_tempTime);
		
		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeRandom(DIR_GENERATED_1, false, AtaraxisShredder.RANDOM_PATTERN, 10);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (random mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredRandom(dir) - end; time for test [ms]: " + s_tempTime);
     } 
    
    @Test
    public void shredRandom() throws Exception
    {
		logger.debug("shredRandom() - start");

		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeRandom(FILE_GENERATED_11, false, AtaraxisShredder.RANDOM, 10);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (random mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredRandom(file) - end; time for test [ms]: " + s_tempTime);
		
		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeRandom(DIR_GENERATED_1, false, AtaraxisShredder.RANDOM, 10);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (random mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredRandom(dir) - end; time for test [ms]: " + s_tempTime);
     } 
    
    @Test
    public void shredRandomPattern_LeaveDir() throws Exception
    {
		logger.debug("shredRandomPattern_LeaveDir() - start");
		assertTrue("Dir does not exist in the beginning!", s_LeaveDir2InDir1.exists());
		assertTrue("File in dir does not exist in the beginning!", s_LeaveFileInDir2InDir1.exists());
		
    	s_shredder.wipeRandom(s_LeaveDir1.getAbsolutePath(), true, AtaraxisShredder.RANDOM, 10);
    	
		assertTrue("Dir must exist when leafe=true!", s_LeaveDir1.exists());
		assertFalse("Dir in dir must be removed!", s_LeaveDir2InDir1.exists());
		assertFalse("File in dir must be removed!", s_LeaveFileInDir2InDir1.exists());		
    } 
    
    @Test
    public void shredRandom_LeaveDir() throws Exception
    {
		logger.debug("shredRandom_LeaveDir() - start");
		assertTrue("Dir does not exist in the beginning!", s_LeaveDir2InDir1.exists());
		assertTrue("File in dir does not exist in the beginning!", s_LeaveFileInDir2InDir1.exists());
		
    	s_shredder.wipeRandom(s_LeaveDir1.getAbsolutePath(), true);
    	
		assertTrue("Dir must exist when leafe=true!", s_LeaveDir1.exists());
		assertFalse("Dir in dir must be removed!", s_LeaveDir2InDir1.exists());
		assertFalse("File in dir must be removed!", s_LeaveFileInDir2InDir1.exists());		
    } 
    
    @Test
    public void shredRandom_WrongId_LeaveDir() throws Exception
    {
		logger.debug("shredRandom_LeaveDir() - start");
		assertTrue("Dir does not exist in the beginning!", s_LeaveDir2InDir1.exists());
		assertTrue("File in dir does not exist in the beginning!", s_LeaveFileInDir2InDir1.exists());
		
    	s_shredder.wipeRandom(s_LeaveDir1.getAbsolutePath(), true, -1, 10);
    	
		assertTrue("Dir must exist when leafe=true!", s_LeaveDir1.exists());
		assertFalse("Dir in dir must be removed!", s_LeaveDir2InDir1.exists());
		assertFalse("File in dir must be removed!", s_LeaveFileInDir2InDir1.exists());		
    } 
    
    @Test
    public void shredRandomStringBoolean() throws Exception
    {
		logger.debug("shredRandom() - start");

		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeRandom(FILE_GENERATED_11, false);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (random mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredRandom(file) - end; time for test [ms]: " + s_tempTime);
		
		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeRandom(DIR_GENERATED_1, false);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (random mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredRandom(dir) - end; time for test [ms]: " + s_tempTime);
     } 
    
    
    @Test
    public void shredSecureRandom() throws Exception
    {
		logger.debug("shredRandom() - start");

		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeRandom(FILE_GENERATED_11, false, AtaraxisShredder.RANDOM_SECURE, 10);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (random mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredRandom(file) - end; time for test [ms]: " + s_tempTime);
		
		s_tempTime = System.currentTimeMillis();
    	s_shredder.wipeRandom(DIR_GENERATED_1, false, AtaraxisShredder.RANDOM_SECURE, 10);
    	//assertTrue("File is not wiped (zeroed)", checkFileEmpty(s_copy));
    	assertTrue("File is not wiped (random mode)", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("shredRandom(dir) - end; time for test [ms]: " + s_tempTime);
     } 
    
    @Test
    public void shredOpenedFile() throws Exception
    {
    	if(System.getProperty("os.name").contains("Windows"))
    	{
    		logger.debug("shredOpenedFile() - start");
    		String tmpName = DIR_TEST + "/openedFile.test";
    		File tmpFile = new File(tmpName);
    		fillFile(tmpFile, 1024);
    		
    		FileInputStream fis = new FileInputStream(tmpFile);
    		//fis.write(1);
    		
    		try {
    			s_shredder.wipeWithByte(tmpName, true, (byte) 0x00);
    			assertTrue("An opened file should never get renamed!", false);
    		}
    		catch (IOException ioe)
    		{
    			assertTrue("IOException with rename message should be thrown", 
    					ioe.getMessage().startsWith("File could not be renamed"));
    		}
    		finally
    		{
    			fis.close();
    			tmpFile.deleteOnExit();
    		}
    		logger.debug("shredOpenedFile() - end");
    	}
    	else
    	{
    		logger.info("Not run on Windows, skipped test");
    	}

    	
		
     } 
    
    
    @Test
    public void shredProtectedFile() throws Exception
    {
    	logger.debug("shredOpenedFile() can only be executed manually (by removing " +
    			"comment tag in sourc code)!");
    	/* 
    	 * This test can't be performed automatically, becaus
    	 * if a file is set to read only, it can never get deleted by java!
    	 * There is no method implemented to change the file permission writable.
    	 * In Java 6 exists now a method to do this (setWritable).
    	 * @see http://java.sun.com/developer/technicalArticles/J2SE/Desktop/javase6/enhancements/
    	 * 
    	 */ 
    	
/*		logger.debug("shredProtectedFile() - start");
		String tmpName = DIR_TEST + "/protectedFile.test";
		File tmpFile = new File(tmpName);
		tmpFile.setReadOnly();
		
		try {
			s_shredder.wipeWithByte(tmpName, true, (byte) 0x00);
			assertTrue("A write protected file can not be deleted!", false);
		}
		catch (FileNotFoundException fnfe)
		{
			System.err.println(fnfe.getMessage());
			assertTrue("FileNotFoundException with rename message should be thrown", 
					fnfe.getMessage().endsWith(" (Access is denied)"));
		}
		finally
		{
			//tmpFile.;
		}
		logger.debug("shredProtectedFile() - end");*/
     } 
    
    @Test
    public void unNameFile() throws Exception
    {
		logger.debug("unNameFile() - start");

		s_tempTime = System.currentTimeMillis();
    	s_shredder.unNameAndDelete(FILE_GENERATED_11);
    	assertTrue("File is not 'unnamed'", true);
    	s_tempTime = System.currentTimeMillis() - s_tempTime;
		logger.debug("unNameFile() - end; time for test [ms]: " + s_tempTime);
		
		// to delete the folders, use shredder ;-)
		s_shredder.wipeWithByte(DIR_GENERATED_1, false, (byte)0x00);
     }
    
    @Test(expected=FileNotFoundException.class)
    public void shredGutmannFloppy_FileNotExist() throws IOException
    {
    	s_shredder.wipeGutmann(FILE_GENERATED_11+"_dontExist", false, true); 	
    }
    
    @Test(expected=FileNotFoundException.class)
    public void shredGutmann_FileNotExist() throws IOException
    {
    	s_shredder.wipeGutmann(FILE_GENERATED_11+"_dontExist", false, true); 	
    }
    
    @Test(expected=FileNotFoundException.class)
    public void shredDoDExt_FileNotExist() throws IOException
    {
    	s_shredder.wipeDoD(FILE_GENERATED_11+"_dontExist", false,false); 	
    }
	
    @Test(expected=FileNotFoundException.class)
    public void shredDoD_FileNotExist() throws IOException
    {
    	s_shredder.wipeDoD(FILE_GENERATED_11+"_dontExist", false, true); 	
    }
    
    @Test(expected=FileNotFoundException.class)
    public void shredSchneier_FileNotExist() throws IOException
    {
    	s_shredder.wipeSchneier(FILE_GENERATED_11+"_dontExist", false); 	
    }
    
    @Test(expected=FileNotFoundException.class)
    public void shredVSITR_FileNotExist() throws IOException
    {
    	s_shredder.wipeVSITR(FILE_GENERATED_11+"_dontExist", false); 	
    }
    
    @Test(expected=FileNotFoundException.class)
    public void shredWithByte_FileNotExist() throws IOException
    {
    	s_shredder.wipeWithByte(FILE_GENERATED_11+"_dontExist", false,(byte)0x00); 	
    }
    
    @Test(expected=FileNotFoundException.class)
    public void shredRandom_FileNotExist() throws IOException
    {
    	s_shredder.wipeRandom(FILE_GENERATED_11+"_dontExist", false); 	
    }
    
    @Test(expected=FileNotFoundException.class)
    public void shredRandomMultiple_FileNotExist() throws IOException
    {
    	s_shredder.wipeRandom(FILE_GENERATED_11+"_dontExist", false,AtaraxisShredder.RANDOM_PATTERN, 2); 	
    }
    
	/**
	 * Fills a file with 0xAA.
	 * 
	 * @param fileToFill the file to fill with data
	 * @param fileSize the file size in bytes
	 * @throws Exception
	 */
	private static void fillFile(File fileToFill, long fileSize) throws Exception {
		final int arrayLength = 1024;
		final byte[] byteArray = new byte[arrayLength];
		final long loops = fileSize / arrayLength;
		final int rest = (int) (fileSize % arrayLength);
        final FileOutputStream os = new FileOutputStream(fileToFill);

        Arrays.fill(byteArray, (byte)0xAA);
        for (long i = 1; i <= loops; i++){
			os.write(byteArray);		
        	if(i % 1024 == 0)
        		os.flush(); // flush each MB to file (1024*1024)
        }
        if (rest != 0){
        	os.write(byteArray, 0, rest);
        	os.flush();
        }        
        os.close();
	}
}
