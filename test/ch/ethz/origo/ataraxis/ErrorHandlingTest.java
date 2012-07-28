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

package ch.ethz.origo.ataraxis;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileOutputStream;
import java.security.KeyStoreException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.ethz.origo.ataraxis.crypt.AtaraxisCrypter;


/**
 * The ErrorHandlingTest checks if the correct Exceptions are thrown.
 * 
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 */
public class ErrorHandlingTest
{
    /* #######################################
     * ## Don't change this fields, please. ##
     * #######################################
     */
	private static final Logger LOGGER = Logger.getLogger(ErrorHandlingTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	
	
    private static AtaraxisCrypter s_ac = null;
    private static AtaraxisCrypter s_acDefault;
    
    private static final String DIR_DATA    = System.getProperty("user.dir") + "/test";
    private static final String DIR_TEMP    = DIR_DATA + "/tmp";
    private static final char[] STD_PWD     = "asdf_j.k#lo12".toCharArray();
    private static final String LONG_STRING = "lkajfajdsiofojkdsajfohdsagoijdsfl" +
            "kjdsakjfaoids jflkjdsafjoids fdsaj fdafoo dajfkajoidsfj asdjfag" +
            "haiojgea dlkf jfdkhflka jfaif asgajflkdsj=)(/&%flkhdsa fadhsf " +
            "hakfjdaskfjka9898237423pj4324-.,.-%&*W542qrewjqr";
    
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

        String kspath = DIR_TEMP +"/AtaraxiS_Test.ks";
        
        File ksFile = new File(kspath);
        if(ksFile.exists())
        {
        	ksFile.delete();
        }
        s_ac = new AtaraxisCrypter(ksFile,"asdfjkl12".toCharArray(), true);

        LOGGER.debug("runOnceBeforeAllTests() done");
    }
    
    
    /**
     * Test closedown is run once after all tests.
     * 
     * @throws Exception
     */
    @AfterClass
    public static void runOnceAfterAllTests() throws Exception
    {
        s_acDefault = null;
        TestingHelper.deleteDir(new File (DIR_TEMP));
    }
    

    // ************************************************************************
    // *************************** File-Tests *********************************
    // ************************************************************************
    
    


    /**
     * checks if very long key alias are supported
     * 
     * @throws Exception
     */
    @Test
    public void keyAlias() throws Exception
    {
        s_ac = null;
        System.setProperty("ATARAXIS.KEY_ALIAS", LONG_STRING);  // test it with LONG_STRING
        System.setProperty("ATARAXIS.KEYSTORE_PATH",
                DIR_TEMP +"/AtaraxiS_TestAlias.ks");
        
        s_ac = new AtaraxisCrypter(new File(DIR_TEMP +"/AtaraxiS_TestAlias.ks"),STD_PWD, true);
     } 
    
    /**
     * checks if very long key passwords are supported
     * 
     * @throws Exception
     */
    @Test
    public void keyPassword() throws Exception
    {
        s_ac = null;
        System.setProperty("ATARAXIS.KEY_PW", LONG_STRING); // LONG_STRING
        System.setProperty("ATARAXIS.KEYSTORE_PATH",
                DIR_TEMP +"/AtaraxiS_TestKeyPwd.ks");
        
        s_ac = new AtaraxisCrypter(new File(DIR_TEMP +"/AtaraxiS_TestKeyPwd.ks"),STD_PWD, true);
        
     } 
    
    /**
     * checks if very long KS passwords are supported
     * 
     * @throws Exception
     */
    @Test
    public void keyStorePassword() throws Exception
    {
        s_ac = null;
        System.setProperty("ATARAXIS.KEYSTORE_PATH",
                DIR_TEMP +"/AtaraxiS_TestKS_Pwd.ks");
        
        s_ac = new AtaraxisCrypter(new File(DIR_TEMP +"/AtaraxiS_TestKS_Pwd.ks"),LONG_STRING.toCharArray(), true);
        assertNotNull("Long Passwords should work", s_ac);
     } 
    
    /**
     * checks if use of nonexistent KS throws the right exception 
     * 
     * @throws Exception
     */
    @Test
    public void dontCreateKS() throws Exception
    {
        s_ac = null;
        System.setProperty("ATARAXIS.KEYSTORE_PATH",
                DIR_TEMP +"/AtaraxiS_TestDontCreate.ks");
        
        try
        {
            s_ac = new AtaraxisCrypter(new File(DIR_TEMP +"/AtaraxiS_TestDontCreate.ks"),STD_PWD, false);
        }
        catch (KeyStoreException e)
        {
            assertTrue(e.getMessage().startsWith("KeyStore does not exist an " +
                    "creation is not allowed!"));
            return;
        }
        fail("Should have thrown a KeyStoreException");
     }
    
    /**
     * checks exception for wrong KSHandler class
     * 
     * @throws Exception
     */
   //System-Property not supportet
    @Test
    public void unsupportedKSType() throws Exception
    {
        s_ac = null;
        
        File propertiesFile = new File(TEST_DIR+"/unsupportedKSType.property");
        
        Properties specialProps = new Properties();
        specialProps.setProperty("ATARAXIS.KEYSTORE_CLASS",
            "ch.ethz.origo.ataraxis.crypt.MyKeyStoreHandler");
        specialProps.store(new FileOutputStream(propertiesFile), "Created by Test unsupportedKSType()");
        
        System.setProperty("ATARAXIS_PROPERTIES_FILE",
        		propertiesFile.getAbsolutePath());
        
        try
        {
            s_ac = new AtaraxisCrypter(new File(DIR_TEMP +"/AtaraxiS_Test_unsupportedKS_Type.ks"),STD_PWD, true);
            fail("Should have thrown a KeyStoreException");
        }
        catch (KeyStoreException e)
        {
        	assertTrue("Exception should be InvocationTargetException",e.getMessage().startsWith("InvocationTargetException"));
        } 
        finally
        {
        	System.setProperty("ATARAXIS_PROPERTIES_FILE","");
        }
     }

    /**
     * checks exception for wrong KS password
     * 
     * @throws Exception
     */
    @Test
    public void keyStoreWrongPassword() throws Exception
    {
        s_ac = null;
        
        try
        {
            s_ac = new AtaraxisCrypter(new File(DIR_TEMP +"/AtaraxiS_Test.ks"),"incorrect_Password".toCharArray(), false);
        }
        catch (Exception e)
        {
            // Exception kann leider nicht genauer geworfen werden...
            assertTrue(e.getMessage().startsWith("InvocationTargetException"));
            return;
        }
        fail("Should have thrown a KeyStoreException");
     } 
    
    /**
     * checks exception for wrong key password
     * 
     * @throws Exception
     */
    @Test
    public void keyWrongPassword() throws Exception
    {
        s_ac = null;
        System.setProperty("ATARAXIS.KEY_PW", "incorrect_Password");
        System.setProperty("ATARAXIS.KEYSTORE_PATH",
                DIR_TEMP +"/AtaraxiS_Test.ks"); // use existing default KS
        
        try
        {
            s_ac = new AtaraxisCrypter(new File(DIR_TEMP +"/AtaraxiS_Test.ks"),STD_PWD, false);
            fail("Should have thrown a KeyStoreException");
        }
        catch (Exception e)
        {
            // Exception kann leider nicht genauer geworfen werden...
            assertTrue(e.getMessage().startsWith("InvocationTargetException"));
            return;
        }
        
     }    
 
    /**
     * checks exception for wrong key alias
     * 
     * @throws Exception
     */
    @Test
    public void wrongKeyAlias() throws Exception
    {
        s_ac = null;
        System.setProperty("ATARAXIS.KEY_ALIAS", "incorrect_Alias");
        System.setProperty("ATARAXIS.KEYSTORE_PATH",
                DIR_TEMP +"/AtaraxiS_Test.ks"); // use existing default KS
        
        try
        {
            s_ac = new AtaraxisCrypter(new File(DIR_TEMP +"/AtaraxiS_Test.ks"),STD_PWD, false);
        }
        catch (Exception e)
        {
            // Exception kann leider nicht genauer geworfen werden...
            assertTrue(e.getMessage().startsWith("InvocationTargetException"));
            return;
        }
        fail("Should have thrown a KeyStoreException");
     } 
    
    /**
     * checks exception for encrypting an inexistent file
     * 
     * @throws Exception
     */
    @Test
    public void wrongSourceFile() //throws Exception
    {
        try
        {
            s_acDefault.encryptFile(new File(DIR_TEMP + "/inexistantFile.ief"),
            		new File(DIR_TEMP + "/inexistantFile.ief.ac"));
        }
        catch (Exception e)
        {
            assertTrue(e instanceof NullPointerException);
            return;
        }
        fail("Should have thrown a KeyStoreException");
     } 

   
}