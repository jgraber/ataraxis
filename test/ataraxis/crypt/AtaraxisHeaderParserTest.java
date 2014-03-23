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

package ataraxis.crypt;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.security.KeyStoreException;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.crypt.AtaraxisHeaderCreator;
import ataraxis.crypt.AtaraxisHeaderParser;
import ataraxis.crypt.JurisdictionPolicyError;

public class AtaraxisHeaderParserTest 
{
	private static final Logger logger = Logger.getLogger(AtaraxisHeaderParserTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	private static final String TEST_DIR_DATA = TEST_DIR + "/testrun/AtaraxisHeaderParser";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	private static String headerPath = TEST_DIR_DATA + "/headerTestFile.ac";
	
	@BeforeClass 
	public static void initClass() throws KeyStoreException, IOException, JurisdictionPolicyError
	{
		PropertyConfigurator.configure(LOG_PROPS_FILE);
		logger.info("Start AtaraxisHeaderParserTest");
		(new File(TEST_DIR_DATA)).mkdirs();
		File headerFile = new File(headerPath);
		if(headerFile.exists())
		{
			headerFile.delete();
		}
	}
	
	@Test
	public void testGetHeaderVersion() throws Exception
	{
		AtaraxisHeaderCreator ahc = new AtaraxisHeaderCreator();
		
		OutputStream os = new FileOutputStream(headerPath);
		os.write(ahc.getHeader().getBytes());
		os.flush();
		os.close();
		
		AtaraxisHeaderParser ahp = new AtaraxisHeaderParser(new File(headerPath));
		assertEquals("1.0.0", ahp.getHeaderVersion());
	}
}
