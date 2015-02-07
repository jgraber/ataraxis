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

package ataraxis.special;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.TestingHelper;
import ataraxis.crypt.AtaraxisCrypter;



/**
 * The CryptTest is used to test encryption / decryption of files and streams.
 * 
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 */
public class CryptTest
{
    /* ############################################
     * ## You may change the following fields.   ##
     * ############################################
     */
    
    /**
     * This is the name of the small file to test encryption / decryption.
     * You can change it to your needs.
     */
    private static final String FILE_S = "/performance.xls";
    
    /**
     * This is the name of the medium sized file to test encryption / decryption.
     * You can change it to your needs.
     */
    private static final String FILE_M = "/ataraxis-hitech.pdf";
    
    
    /* ############################################
     * ## Don't change this fields, please.      ##
     * ############################################
     */
    
    private static AtaraxisCrypter s_ac;
    private static InputStream s_inStream;
    
    private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
    private static final String DIR_DATA = TEST_DIR + "/fixtures";
    private static final String DIR_TEMP = TEST_DIR + "/testrun/tmp";
    
    /**
     * Original path of the small file.
     */
    private static final String FILE_S_O = DIR_DATA + FILE_S;
    
    /**
     * Original path of the medium sized file.
     */
    private static final String FILE_M_O = DIR_DATA + FILE_M;
    
    /**
     * Filename for the encrypted small file.
     */
    private static final String FILE_S_E = DIR_TEMP + FILE_S + ".enc";
    
    /**
     * Filename for the encrypted medium sized file.
     */
    private static final String FILE_M_E = DIR_TEMP + FILE_M + ".enc";
    
    /**
     * Filename for the decrypted small file.
     */
    private static final String FILE_S_D = DIR_TEMP + FILE_S + ".dec";
    
    /**
     * Filename for the decrypted medium sized file.
     */
    private static final String FILE_M_D = DIR_TEMP + FILE_M + ".dec";
    
    private static final Logger logger = LogManager.getLogger(CryptTest.class);
    
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
    
    
    /**
     * Test startup is run once before all tests.
     * 
     * @throws Exception
     */
    @BeforeClass
    public static void runOnceBeforeAllTests() throws Exception
    {        
    	PropertyConfigurator.configure(LOG_PROPS_FILE); 
		logger.debug("AtaraxisFileComparatorTest startet");
    	
        (new File (DIR_TEMP)).mkdir();
        
        
        System.setProperty("ATARAXIS.KEYSTORE_PATH",
                DIR_TEMP +"/AtaraxiS_Test.ks");
        System.setProperty("ATARAXIS.KEYSTORE_CLASS",
                "ataraxis.crypt.UBERKeyStoreHandler");

        
        s_ac = new AtaraxisCrypter(new File(DIR_TEMP+"/CryptTest.ks"),"asdfjklo12".toCharArray(), true);
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
    // *************************** Stream-Tests *******************************
    // ************************************************************************    

    /**
     * Tests encryption and decryption of a FileInputStream 
     * 
     * @throws Exception
     */
    /*
    @Test
    public void encryptByteStream() throws Exception
    {
        FileInputStream fis = new FileInputStream(FILE_S_O);
        FileOutputStream fosEnc = new FileOutputStream(FILE_S_E);
        FileOutputStream fosDec = new FileOutputStream(FILE_S_D);
        s_ac.encryptStream(fis, fosEnc);
        
        fis.close();
        fosEnc.close();
        
        InputStream fisc = new FileInputStream(FILE_S_E);
        s_ac.decryptStream(fisc, fosDec);
        
        fisc.close();
        fosDec.close();
        
        assertTrue(checkSum(FILE_S_O) == checkSum(FILE_S_D));
     } 

    */
    
    
    // ************************************************************************
    // *************************** File-Tests *********************************
    // ************************************************************************

    /**
     * Tests encryption and decryption of a small file 
     * 
     * @throws Exception
     */
    @Test
    public void encryptFileSmall() throws Exception
    {
    	
    	File a = new File(FILE_S_O);
    	File b = new File(FILE_S_E);
    	File c = new File(FILE_S_D);
        s_ac.encryptFile(a, b);
        
        assertTrue(b.exists());
        
        s_ac.decryptFile(b, c);
        
        assertTrue(checkSum(FILE_S_O) == checkSum(FILE_S_D));
    }
    
    /**
     * Tests encryption and decryption of a medium sized file 
     * 
     * @throws Exception
     */
    @Test
    public void encryptFileMedium() throws Exception
    {
        s_ac.encryptFile(new File(FILE_M_O), new File(FILE_M_E));
        s_ac.decryptFile(new File(FILE_M_E), new File(FILE_M_D));
        
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
        s_inStream = new FileInputStream( filename );
        CRC32 crc = new CRC32();
        CheckedInputStream cis = new CheckedInputStream( s_inStream, crc );
        while ( cis.read() != -1 );
        s_inStream.close();
        return crc.getValue();
    }
}