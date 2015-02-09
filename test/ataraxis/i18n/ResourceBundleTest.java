/* ----------------------------------------------------------------------------
 * Copyright 2006 - 2015 Johnny Graber & Andreas Muedespacher
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
package ataraxis.i18n;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.Test;


/**
 * Tests to try {@link java.util.ResourceBundle#getString(java.lang.String)}
 * for translations in AtaraxiS.
 * 
 * @author Johnny Graber
 */
public class ResourceBundleTest {

	ResourceBundle messages;
	
	@Test
	public void testGetString_EN_StringExist()
	{
		Locale gb = new Locale("en", "gb");
		messages = ResourceBundle.getBundle("i18ntest", gb);
		
		assertEquals("EN", messages.getString("TRANSLATION.TEXT"));		
	}

	@Test
	public void testGetString_De_StringExist()
	{
		messages = ResourceBundle.getBundle("i18ntest",	new Locale("de"));
		
		assertEquals("DE", messages.getString("TRANSLATION.TEXT"));		
	}
	
	@Test
	public void testGetString_Fr_StringExist()
	{
		messages = ResourceBundle.getBundle("i18ntest",	new Locale("fr"));
		
		assertEquals("FR", messages.getString("TRANSLATION.TEXT"));		
	}
	
	@Test
	public void testGetString_Fr_StringDoesNotExistInFrButInDefault()
	{
		messages = ResourceBundle.getBundle("i18ntest",	new Locale("fr"));
		
		assertEquals("From Default", messages.getString("TRANSLATION.NOT.EXISTING"));		
	}
	
	@Test
	public void testGetString_Fr_StringDoesNotExistInFrButInDe()
	{
		messages = ResourceBundle.getBundle("i18ntest",	new Locale("fr"));
		assertFalse(messages.containsKey("ONLY.IN.DE"));
	}
}
