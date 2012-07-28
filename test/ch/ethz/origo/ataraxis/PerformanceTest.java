/*----------------------------------------------------------------------------
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

package ch.ethz.origo.ataraxis;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.ethz.origo.ataraxis.crypt.AtaraxisCrypter;
import ch.ethz.origo.ataraxis.util.FileCopy;

/**
 * This JUnit Test measure the performance. Needs approximately
 * 60 seconds, so don't use it in testsuite directly.
 * 
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 */
public class PerformanceTest
{
    /* ############################################
     * You may change the following fields.
     * ############################################
     */

    /**
     * This is the name of the medium sized file to test encryption / decryption.
     * You can change it to your needs.
     */
    private static final String FILE_M = "/wallstreet.mp3";
    
    /**
     * Number of loops the cryptPerformance() does.
     */
    private static final int LOOPS = new Integer(System.getProperty("ATARAXIS.LOOPS", "10"));
    
    
    /* ############################################
     * Don't change this fields, please.
     * ############################################
     */

    private static AtaraxisCrypter s_ac;
    private static InputStream s_inStream;
    
    private static final String DIR_DATA = System.getProperty("user.dir") + "/test";
    private static final String DIR_TEMP = DIR_DATA + "/tmp";
    private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
    protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
    private static final Logger logger = Logger.getLogger(PerformanceTest.class);
    
    /**
     * Original path of the medium sized file.
     */
    private static final String FILE_M_O = DIR_DATA + FILE_M;
    
    /**
     * Filename for the encrypted medium sized file.
     */
    private static final String FILE_M_E = DIR_TEMP + FILE_M + ".enc";
    
    /**
     * Filename for the decrypted medium sized file.
     */
    private static final String FILE_M_D = DIR_TEMP + FILE_M + ".dec";
    
    /**
     * Filename for the decrypted medium sized file.
     */
    private static final String FILE_M_C = DIR_TEMP + FILE_M + ".copy";
    
    
    /**
     * Test startup is run once before all tests.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void runOnceBeforeAllTests() throws Exception
    {        
    	PropertyConfigurator.configure(LOG_PROPS_FILE); 
    	
        (new File (DIR_TEMP)).mkdir();
        
        
        System.setProperty("ATARAXIS.KEYSTORE_PATH",
                DIR_TEMP +"/AtaraxiS_Test.ks");
        System.setProperty("ATARAXIS.KEYSTORE_CLASS",
                "ch.ethz.origo.ataraxis.crypt.UBERKeyStoreHandler");

        s_ac = new AtaraxisCrypter(new File(DIR_TEMP+"/PerformanceTest.ks"), "asdfjklo12".toCharArray(), true);
        
        
    }
    
    
    /**
     * Test closedown is run once after all tests.
     * 
     * @throws Exception
     */
    @AfterClass
    public static void runOnceAfterAllTests() throws Exception
    {
        s_ac = null;
        TestingHelper.deleteDir(new File (DIR_TEMP));
    }
    
    
    // ************************************************************************
    // *************************** File-Tests *********************************
    // ************************************************************************

    
    /**
     * Tests encryption and decryption of a medium sized file and as
     * reference value the time to copy the same file as well.
     * 
     * @throws Exception
     */
    @Test
    public void cryptPerformance() throws Exception
    {
        float encrSum, decrSum, copySum, totalSum;
        long timeEncr, timeEncrSum, timeDecr, timeDecrSum;
        long timeCopy, timeCopySum, fileLength;
        timeEncrSum = 0;
        timeDecrSum = 0;
        timeCopySum = 0;
        
        for (int i = 0; i < LOOPS; i++)
        {
            timeEncr = System.currentTimeMillis();
            s_ac.encryptFile(new File(FILE_M_O), new File(FILE_M_E));
            timeEncr = System.currentTimeMillis() - timeEncr;
            timeCopy = System.currentTimeMillis() - timeEncr;
            FileCopy.copyFile(FILE_M_O, FILE_M_C);
            timeCopy = System.currentTimeMillis() - timeCopy;
            timeDecr = System.currentTimeMillis();
            s_ac.decryptFile(new File(FILE_M_E), new File(FILE_M_D));
            timeDecr = System.currentTimeMillis() - timeDecr;
            
            timeEncrSum = timeEncrSum + timeEncr;
            timeDecrSum = timeDecrSum + timeDecr;
            timeCopySum = timeCopySum + timeCopy;
        }
        fileLength =  (new File(FILE_M_O).length())/1024;
        
        encrSum = ((float) timeEncrSum)/1000;
        decrSum = ((float) timeDecrSum)/1000;
        copySum = ((float) timeCopySum)/1000;
        totalSum = ((float) (timeEncrSum + timeDecrSum + timeCopySum))/1000;
        
        logger.info("Size of test file: \t" + fileLength + " KBytes");
        logger.info("Required time for \t" + LOOPS + " loops:");
        logger.info("-------------------------------------\n");
        logger.info("Encryption of file:    " + encrSum + " seconds");
        logger.info("Decryption of file:    " + decrSum + " seconds");
        logger.info("Ordinary copy of file: " + copySum + " seconds");
        logger.info("Overall test time:     " + totalSum+ " seconds");
        
        assertTrue(checkSum(FILE_M_O) == checkSum(FILE_M_D));
    }

    // ************************************************************************
    // *************************** Helper-Methods *****************************
    // ************************************************************************
    
    
    /**
     * Calculates the CR32 checksum of a given file
     * 
     * @param filename to check
     * @return checksum of the file
     * @throws IOException
     */
    private long checkSum(String filename) throws IOException
    {
        s_inStream = new FileInputStream(filename);
        CRC32 crc = new CRC32();
        CheckedInputStream cis = new CheckedInputStream(s_inStream, crc);
        while (cis.read() != -1);
        s_inStream.close();
        return crc.getValue();
    }
    

    

}