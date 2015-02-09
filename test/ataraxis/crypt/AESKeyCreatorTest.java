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
import static org.junit.Assert.fail;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.SecretKey;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.crypt.AESKeyCreator;
import ataraxis.crypt.NotImplementedException;

/**
 * Unit Test for AESKeyCreator
 * @author Johnny Graber
 *
 */
public class AESKeyCreatorTest 
{

	private static final Logger logger = LogManager.getLogger(AESKeyCreatorTest.class);
	
	
	@BeforeClass 
	public static void initClass()
	{
		logger.debug("AESKeyCreatorTest startet");
	}
	/**
	 * Test method for {@link ataraxis.crypt.AESKeyCreator#createSecretKey()}.
	 */
	@Test
	public void testCreateSecretKey() 
	{
		try 
		{
			SecretKey aesKey = new AESKeyCreator().createSecretKey();
			assertEquals(aesKey.getAlgorithm(),"AES");
			assertEquals(aesKey.getEncoded().length*8,256);
		} 
		catch (NoSuchAlgorithmException e) 
		{
			fail("NoSuchAlgorithmException");
		} 
		catch (NoSuchProviderException e) 
		{
			fail("NoSuchProviderException");
		} 
		catch (NotImplementedException e) 
		{
			fail("NotImplementedException");
		}
	}

}
