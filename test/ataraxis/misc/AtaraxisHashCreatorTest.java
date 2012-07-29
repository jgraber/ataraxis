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

package ataraxis.misc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.crypt.AESKeyCreatorTest;
import ataraxis.misc.AtaraxisHashCreator;
import ataraxis.misc.HashingDigest;


/**
 * The AtaraxisHashCreatorTest test AtaraxisHashCreator.
 * 
 * @author J. Graber & A. Muedespacher
 * @version 1.0
 *
 */
public class AtaraxisHashCreatorTest 
{
	/*
	 * Sums calculated with or used as help:
	 *  - http://www.slavasoft.com/hashcalc/ 
	 *  - http://en.wikipedia.org/wiki/SHA-1 
	 *  - http://kobesearch.cpan.org/htdocs/perl/Digest.pm.html
	 */

	private static final Logger logger = Logger.getLogger(AESKeyCreatorTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	
	private static final File FILE_SMALL = new File(TEST_DIR + "/performance.xls");
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
		PropertyConfigurator.configure(LOG_PROPS_FILE); 
		logger.debug("AtaraxisHashCreatorTest startet");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception 
	{
	
	}

	/**
	 * Test method for {@link ataraxis.misc.AtaraxisHashCreator#createHashForString(java.lang.String)}.
	 */
	@Test
	public void testCreateHashForString() 
	{
		try 
		{
			AtaraxisHashCreator ahc = new AtaraxisHashCreator(HashingDigest.SHA_512);
		
			
			assertEquals(("07e547d9586f6a73f73fbac0435ed76951218fb7d0c8d788a309d785436bbb64"+
			"2e93a252a954f23912547d1e8a3b5ed6e1bfd7097821233fa0538f3db854fee6").toUpperCase(), 
					AtaraxisHashCreator.prettyPrintHash(ahc.createHashForString(
							"The quick brown fox jumps over the lazy dog")).toUpperCase());
		
			assertEquals(ahc.getAlgorithm(), "SHA-512");
			
		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link ataraxis.misc.AtaraxisHashCreator#createHashForString(java.lang.String)}.
	 */
	@Test
	public void testCreateHashForEmptyString() 
	{
		try 
		{
			AtaraxisHashCreator ahc = new AtaraxisHashCreator(HashingDigest.SHA_512);
		
			
			assertEquals(("cf83e1357eefb8bdf1542850d66d8007d620e4050b5715dc83f4a921d36ce9ce"
   +"47d0d13c5d85f2b0ff8318d2877eec2f63b931bd47417a81a538327af927da3e").toUpperCase(), 
					AtaraxisHashCreator.prettyPrintHash(ahc.createHashForString("")).toUpperCase());
		
			assertEquals(ahc.getAlgorithm(), "SHA-512");
			
		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}
	
	
	/**
	 * Test method for {@link ataraxis.misc.AtaraxisHashCreator#createHashForFile(java.io.File)}.
	 */
	@Test
	public void testCreateMD5HashForFile() 
	{
		try 
		{
			AtaraxisHashCreator ahc = new AtaraxisHashCreator(HashingDigest.MD5);
		
			assertEquals("296A17AD5CD728A7280F233AB44AA578", 
					AtaraxisHashCreator.prettyPrintHash(ahc.createHashForFile(FILE_SMALL)).toUpperCase());
		
			assertEquals(ahc.getAlgorithm(), "MD5");
			
		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link ataraxis.misc.AtaraxisHashCreator#createHashForFile(java.io.File)}.
	 */
	@Test
	public void testCreateSHA1HashForFile() 
	{
		try 
		{
			AtaraxisHashCreator ahc = new AtaraxisHashCreator(HashingDigest.SHA_1);
			
			assertEquals("6FED865453985E523BCFF70E2F690D8EF1F214BE", 
					AtaraxisHashCreator.prettyPrintHash(ahc.createHashForFile(FILE_SMALL)).toUpperCase());
			
			assertEquals(ahc.getAlgorithm(), "SHA-1");
		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link ataraxis.misc.AtaraxisHashCreator#createHashForFile(java.io.File)}.
	 */
	@Test
	public void testCreateSHA256HashForFile() 
	{
		try 
		{
			AtaraxisHashCreator ahc = new AtaraxisHashCreator(HashingDigest.SHA_256);
			
			assertEquals("6E0A2FB0BB2A2EE17177992D8267228E863090CBF6122EFB5540B5786FEE473C", 
					AtaraxisHashCreator.prettyPrintHash(ahc.createHashForFile(FILE_SMALL)).toUpperCase());
		
			assertEquals(ahc.getAlgorithm(), "SHA-256");
		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link ataraxis.misc.AtaraxisHashCreator#createHashForFile(java.io.File)}.
	 */
	@Test
	public void testCreateSHA384HashForFile() 
	{
		try 
		{
			AtaraxisHashCreator ahc = new AtaraxisHashCreator(HashingDigest.SHA_384);
			
			assertEquals("e6963a6b2b74de4c03c903db1f84bed33f4cff5c1d580d7a1f62850ab89f3c678c2b52d0d73ec0fdab2b118eecf5a36b".toUpperCase(), 
					AtaraxisHashCreator.prettyPrintHash(ahc.createHashForFile(FILE_SMALL)).toUpperCase());
		
			assertEquals(ahc.getAlgorithm(), "SHA-384");
		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link ataraxis.misc.AtaraxisHashCreator#createHashForFile(java.io.File)}.
	 */
	@Test
	public void testCreateSHA512HashForFile() 
	{
		try 
		{
			AtaraxisHashCreator ahc = new AtaraxisHashCreator(HashingDigest.SHA_512);
			
			assertEquals("4f9efc360412afc594e196fc54b55519a8b20817694af7af167d0a669d19a372e2a3166a19aa39efd6c0f0f0bb13d50c259cc15da109ff353744d7700dfa35c0".toUpperCase(), 
					AtaraxisHashCreator.prettyPrintHash(ahc.createHashForFile(FILE_SMALL)).toUpperCase());
		
			assertEquals(ahc.getAlgorithm(), "SHA-512");
		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}
	
	/**
	 * Test method for {@link ataraxis.misc.AtaraxisHashCreator#createHashForInputStream(java.io.InputStream)}.
	 */
	@Test
	public void testCreateHashForInputStream() 
	{
		try 
		{
			AtaraxisHashCreator ahc = new AtaraxisHashCreator(HashingDigest.MD5);
		
			assertEquals("296A17AD5CD728A7280F233AB44AA578", 
					AtaraxisHashCreator.prettyPrintHash(ahc.createHashForInputStream(new FileInputStream(FILE_SMALL))).toUpperCase());
		
			assertEquals(ahc.getAlgorithm(), "MD5");
			
		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}

	/**
	 * Test method for {@link ataraxis.misc.AtaraxisHashCreator#getDigestLength()}.
	 */
	@Test
	public void testGetDigestLength() 
	{
		try 
		{
			AtaraxisHashCreator ahc = new AtaraxisHashCreator(HashingDigest.MD5);
			assertEquals(128/8, ahc.getDigestLength());
			
			ahc = new AtaraxisHashCreator(HashingDigest.SHA_1);
			assertEquals(160/8, ahc.getDigestLength());
			
			ahc = new AtaraxisHashCreator(HashingDigest.SHA_256);
			assertEquals(256/8, ahc.getDigestLength());
			
			ahc = new AtaraxisHashCreator(HashingDigest.SHA_384);
			assertEquals(384/8, ahc.getDigestLength());
			
			ahc = new AtaraxisHashCreator(HashingDigest.SHA_512);
			assertEquals(512/8, ahc.getDigestLength());
			
		} 
		catch (Exception e) 
		{
			logger.fatal(e.getMessage());
			fail(e.getMessage());
		}
	}
}
