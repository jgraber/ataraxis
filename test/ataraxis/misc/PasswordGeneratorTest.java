/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2016 Johnny Graber & Andreas Muedespacher
 * ----------------------------------------------------------------------------
 * 
 * This File is part of AtaraxiS (https://github.com/jgraber/ataraxis) and is
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.security.KeyStoreException;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.crypt.AtaraxisHeaderCreatorTest;
import ataraxis.crypt.JurisdictionPolicyError;
import ataraxis.misc.PasswordGenerator;


public class PasswordGeneratorTest
{

	private static final Logger logger = LogManager.getLogger(AtaraxisHeaderCreatorTest.class);
	
	@BeforeClass 
	public static void initClass() throws KeyStoreException, IOException, JurisdictionPolicyError
	{
		logger.info("Start PasswordGeneratorTest");
	}
	
	@Test
	public void testInitialConfig()
	{
		PasswordGenerator pwg = new PasswordGenerator();
		assertTrue(pwg.isIncluded_09());
		assertTrue(pwg.isIncluded_az());
		assertTrue(pwg.isIncluded_AZ());
		assertTrue(pwg.isIncluded_Special());
	}
	

	@Test
	public void testAlphabet()
	{
		PasswordGenerator pwg = new PasswordGenerator();
		String alphabet = pwg.getAlphabet();

		assertTrue(alphabet.matches(".*\\d*.*"));
		assertTrue(alphabet.matches(".*[a-z].*"));
		assertTrue(alphabet.matches(".*[A-Z].*"));
		assertTrue(alphabet.matches(".*[.:,*+/()=@].*"));
	}
	
	
	@Test
	public void testSomeRegex()
	{
	
		String nr = "t90z5";
		assertTrue(nr.matches(".*\\d*.*"));
		assertTrue(nr.matches(".*[a-zA-Z].*"));
		assertFalse(nr.matches(".*[A-Z].*"));
		
		String text = "onlyText";
		assertTrue(text.matches("[a-zA-Z]*"));
		
		String textSmall = "onlysmallletters";
		
		assertTrue(textSmall.matches("[a-z]*"));
		assertFalse(text.matches("[A-Z]*"));
		assertFalse(text.matches("\\d*"));
	}
	
	@Test
	public void testInclude_09()
	{
		PasswordGenerator pwg = new PasswordGenerator();
		pwg.setInclude_09(true);
		assertTrue(pwg.isIncluded_09());
		
		String password = pwg.generatePW(200);
		logger.info("testInclude_09 Password is: " + password);
		
		assertTrue(pwg.getAlphabet().matches(".*\\d+.*"));
		assertTrue(password.matches(".*\\d+.*"));
	}
	@Test
	public void testNotInclude_09()
	{
		PasswordGenerator pwg = new PasswordGenerator();
		
		assertTrue(pwg.isIncluded_09());
		pwg.setInclude_09(false);
		assertFalse(pwg.isIncluded_09());
		
		String password = pwg.generatePW(200);
		logger.info("testNotInclude_09 Password is: " + password);
		
		assertFalse(pwg.getAlphabet().matches(".*\\d+.*"));
		assertFalse(password.matches(".*\\d+.*"));
		
	}
	
	@Test
	public void testInclude_az()
	{
		PasswordGenerator pwg = new PasswordGenerator();
		
		pwg.setInclude_az(true);
		assertTrue(pwg.isIncluded_az());
		
		String password = pwg.generatePW(200);
		logger.info("testInclude_az Password is: " + password);
		
		assertTrue(pwg.getAlphabet().matches(".*[a-z].*"));
		assertTrue(password.matches(".*[a-z].*"));
	}
	@Test
	public void testNotInclude_az()
	{
		PasswordGenerator pwg = new PasswordGenerator();
		
		assertTrue(pwg.isIncluded_az());
		pwg.setInclude_az(false);
		assertFalse(pwg.isIncluded_az());
		
		String password = pwg.generatePW(200);
		logger.info("testNotInclude_az Password is: " + password);
		
		assertFalse(pwg.getAlphabet().matches(".*[a-z].*"));
		assertFalse(password.matches(".*[a-z].*"));
	}
	
	@Test
	public void testInclude_AZ()
	{
		PasswordGenerator pwg = new PasswordGenerator();
		
		pwg.setInclude_AZ(true);
		assertTrue(pwg.isIncluded_AZ());
		
		String password = pwg.generatePW(200);
		logger.info("testInclude_az Password is: " + password);
		
		assertTrue(pwg.getAlphabet().matches(".*[A-Z].*"));
		assertTrue(password.matches(".*[A-Z].*"));
	}
	@Test
	public void testNotInclude_AZ()
	{
		PasswordGenerator pwg = new PasswordGenerator();
		
		assertTrue(pwg.isIncluded_AZ());
		pwg.setInclude_AZ(false);
		assertFalse(pwg.isIncluded_AZ());
		
		String password = pwg.generatePW(200);
		logger.info("testNotInclude_az Password is: " + password);
		
		assertFalse(pwg.getAlphabet().matches(".*[A-Z].*"));
		assertFalse(password.matches(".*[A-Z].*"));
	}
	
	@Test
	public void testInclude_Special()
	{
		PasswordGenerator pwg = new PasswordGenerator();
		
		pwg.setInclude_Special(true);
		assertTrue(pwg.isIncluded_Special());
		
		String password = pwg.generatePW(200);
		logger.info("testInclude_az Password is: " + password);
		
		assertTrue(pwg.getAlphabet().matches(".*[.:,*+/()=@].*"));
		assertTrue(password.matches(".*[.:,*+/()=@].*"));
	}
	@Test
	public void testNotInclude_Special()
	{
		PasswordGenerator pwg = new PasswordGenerator();
		
		assertTrue(pwg.isIncluded_Special());
		pwg.setInclude_Special(false);
		assertFalse(pwg.isIncluded_Special());
		
		String password = pwg.generatePW(200);
		logger.info("testNotInclude_az Password is: " + password);
		
		assertFalse(pwg.getAlphabet().matches(".*[.:,*+/()=@].*"));
		assertFalse(password.matches(".*[.:,*+/()=@].*"));
	}
	
}
