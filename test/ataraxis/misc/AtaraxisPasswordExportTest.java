package ataraxis.misc;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.TestingHelper;
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
		
		TestingHelper.deleteDir(new File(TEST_DIR_DATA));
		
		(new File(TEST_DIR_DATA)).mkdirs();
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
		
		List<String> rows = Files.readAllLines(new File(exportedFile).toPath(), Charset.defaultCharset());
				
		
		String expectedHeaderLine = "\"Group\",\"Account\",\"Login Name\",\"Password\",\"Web Site\",\"Comments\"";
		assertEquals("header line wrong", expectedHeaderLine, rows.get(0));
		
		String expectedLine1 = "\"\",\"Eintrag 2\",\"root\",\"1234\",\"\",\"\"";
		assertEquals("line 1 wrong", expectedLine1, rows.get(1));
		
		String expectedLine2 = "\"Gruppe B\",\"Eintrag 1\",\"demo\",\"testtest\",\"https://test.com\",\"Dies ist mein Kommentar\"";
		assertEquals("line 2 wrong", expectedLine2, rows.get(2));
		
		String expectedLine3 = "\"Gruppe C\",\"Eintrag 3\",\"user\",\"abc\",\"https://demo.com\",\"\"";
		assertEquals("line 3 wrong", expectedLine3, rows.get(3));
		
		String expectedLine4 = "\"Gruppe C\",\"Eintrag 4\",\"\",\"789\",\"\",\"Kommentar zu Eintrag 4\"";
		assertEquals("line 4 wrong", expectedLine4, rows.get(4));
		
		/* format as required by KeePass: 
"Group","Account","Login Name","Password","Web Site","Comments"
,"Sample Entry 1","User Name","Password","https://keepass.info/","Note"
"Group A\\Group B","Sample Entry 2","tom@example.com","Tom'sPass",,"This is a sample note"
"Group A","Sample Entry 3","Fred","Password\,with\\escaped\"characters",,
"Group C\\Group D.1","Sample Entry 4","Michael321","12345","https://keepass.info/",
		 */
	}

}
