/* ----------------------------------------------------------------------------
 * Copyright 2007 - 2015 Johnny Graber & Andreas Muedespacher
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

import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.crypt.RSAKeyCreator;

/**
 * @author Johnny Graber
 *
 */
public class RSAKeyCreatorTest 
{
	private static final Logger logger = LogManager.getLogger(RSAKeyCreatorTest.class);
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{ 
		logger.debug("RSAKeyCreatorTest startet");
	}

	/**
	 * Test method for 
	 * {@link ataraxis.crypt.RSAKeyCreator#createKeyPair()}.
	 * @throws Exception 
	 * @throws NoSuchProviderException 
	 * @throws NoSuchAlgorithmException 
	 */
	@Test
	public void testCreateKeyPair() throws NoSuchAlgorithmException, NoSuchProviderException, Exception 
	{
		KeyPair rsaPair = new RSAKeyCreator().createKeyPair();
		assertEquals(rsaPair.getPrivate().getAlgorithm(),"RSA");
	}

}
