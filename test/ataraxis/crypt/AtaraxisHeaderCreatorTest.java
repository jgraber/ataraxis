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

package ataraxis.crypt;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.security.KeyStoreException;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.crypt.AtaraxisHeaderCreator;
import ataraxis.crypt.JurisdictionPolicyError;


public class AtaraxisHeaderCreatorTest 
{
	private static final Logger logger = Logger.getLogger(AtaraxisHeaderCreatorTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	
	@BeforeClass 
	public static void initClass() throws KeyStoreException, IOException, JurisdictionPolicyError
	{
		PropertyConfigurator.configure(LOG_PROPS_FILE);
		logger.info("Start AtaraxisHeaderCreatorTest");
	}
	
	/**
	 * Test if the AtaraxisHeaderCreator has a Bug with the byte[] iv
	 */
	@Test
	public void testForBugCopyRefToByteArray() 
	{
		AtaraxisHeaderCreator ahc = new AtaraxisHeaderCreator();
		//String acHeaderString = ahc.getHeader();
		byte[] headerGet = ahc.getIV();
		byte[] initialHeaderCopy = Arrays.copyOf(ahc.getIV(),ahc.getIV().length);
		assertEquals(headerGet, headerGet);
		
		headerGet[0] = 0;
		headerGet[1] = 0;
		headerGet[2] = 0;
		headerGet[3] = 0;
		headerGet[4] = 0;
		headerGet[5] = 0;
		
		byte[] copyHeaderAfterLocalIvModification = Arrays.copyOf(ahc.getIV(),ahc.getIV().length);
		
		
		/*
		System.out.println(acHeaderString);
		System.out.println("headerGet: "+ AtaraxisHashCreator.prettyPrintHash(headerGet) +" " + new String(Base64.encode(headerGet)));
		System.out.println("initialHeaderCopy: "+ AtaraxisHashCreator.prettyPrintHash(initialHeaderCopy) +" " + new String(Base64.encode(initialHeaderCopy)));
		System.out.println("headerCopyAfter: "+ AtaraxisHashCreator.prettyPrintHash(copyHeaderAfterLocalIvModification) +" " + new String(Base64.encode(copyHeaderAfterLocalIvModification)));
		*/
		
		Assert.assertArrayEquals("A local Change should not modify the internal IV",initialHeaderCopy, copyHeaderAfterLocalIvModification);
	}
	
	@Test
	public void testGetHeaderVersion()
	{
		AtaraxisHeaderCreator ahc = new AtaraxisHeaderCreator();
		assertEquals("1.0.0", ahc.getHeaderVersion());
	}
	
}