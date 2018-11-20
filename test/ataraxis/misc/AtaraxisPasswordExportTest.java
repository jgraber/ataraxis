package ataraxis.misc;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.security.KeyStoreException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.crypt.AtaraxisCrypter;


public class AtaraxisPasswordExportTest {

	private static String PASSWORD = "demodemo";
	private static final Logger logger = LogManager.getLogger(AtaraxisPasswordExportTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	
	private static final String TEST_SOURCE_DATA = TEST_DIR + "/fixtures/pwexport/";
	private static final String TEST_DIR_DATA = TEST_DIR + "/pwexport";
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
		logger.debug("AtaraxisPasswordExportTest startet");
		logger.debug("Test-Data read from " + TEST_SOURCE_DATA);
		
		
	}
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception 
	{
	
	}
	
	@Test
	public void test() throws Exception, IOException {
		AtaraxisCrypter s_ac = new AtaraxisCrypter(new File(TEST_SOURCE_DATA+"/keystore.ks"), PASSWORD.toCharArray(), false);
		
		String exportedFile = TEST_DIR_DATA + "/export.cvs";
		
		AtaraxisPasswordExport testee = new AtaraxisPasswordExport(new Shell(new Display()));
		
		testee.savePasswords(s_ac, exportedFile);
		
		
	}

}
