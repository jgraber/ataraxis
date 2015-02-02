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

package ataraxis.passwordmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom2.JDOMException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.TestingHelper;
import ataraxis.crypt.AtaraxisCrypter;
import ataraxis.crypt.CryptoMethodError;
import ataraxis.passwordmanager.AccountEntry;
import ataraxis.passwordmanager.EntryAlreadyExistException;
import ataraxis.passwordmanager.EntryDoesNotExistException;
import ataraxis.passwordmanager.GroupEntry;
import ataraxis.passwordmanager.PasswordEntry;
import ataraxis.passwordmanager.PasswordManager;
import ataraxis.passwordmanager.StorageException;


public class PasswordManagerTest 
{

	private static final Logger logger = Logger.getLogger(PasswordManagerTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	private static final String TEST_DIR_DATA = TEST_DIR + "/testrun/passwordManager";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	protected static final String PW_FILE = TEST_DIR_DATA + "/passwords.xml";
	private static final String PASSWORD = "password";
	private static AtaraxisCrypter s_ac;
	private static String ATARAXIS_DEFAULT_ACCOUNTS = System.getProperty("user.dir") + "/application_data/user_data_template/template.xml";


	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
		PropertyConfigurator.configure(LOG_PROPS_FILE); 
		logger.debug("AtaraxisFileComparatorTest startet");

		File pwmTestDir = new File(TEST_DIR_DATA);

		if(pwmTestDir.exists())
		{
			//System.out.println("Exist: " + pwmTestDir.getAbsolutePath());
			TestingHelper.deleteDir(pwmTestDir);
		}
		else
		{
			pwmTestDir.mkdirs();
		}


		s_ac = new AtaraxisCrypter(new File(TEST_DIR_DATA+"/pwmTest.ks"),PASSWORD.toCharArray(), true);
		s_ac.encryptFile(new File(ATARAXIS_DEFAULT_ACCOUNTS), new File(PW_FILE));

	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception 
	{

	}

	@Test
	public void testCreatePWManager() throws FileNotFoundException, JDOMException, IOException, StorageException
	{
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		assertTrue(pwm != null);
	}

	
	@Test
	public void testAddGroup() throws Exception
	{
		String nameOfGroup = "Group A";
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		GroupEntry group = new GroupEntry(nameOfGroup);
		pwm.addEntry(group);
//		pwm.createGroupElement(nameOfGroup);
		pwm.savePasswords();
		s_ac.decryptFile(new File(PW_FILE), new File(PW_FILE+"_addGroup.txt"));
		assertTrue(pwm.existID(nameOfGroup));
		assertEquals(nameOfGroup, pwm.getGroupSlot(nameOfGroup));
	}

	@Test(expected=EntryAlreadyExistException.class)
	public void testAddGroupTwice() throws Exception
	{
		String nameOfGroup = "Group T";
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		GroupEntry group = new GroupEntry(nameOfGroup);
		pwm.addEntry(group);
		pwm.addEntry(group);
		fail();
//		pwm.createGroupElement(nameOfGroup);
//		pwm.savePasswords();
//		s_ac.decryptFile(PW_FILE, PW_FILE+"_addGroupTwice.txt");
//		assertTrue(pwm.existID(nameOfGroup));
	}

	@Test
	public void testAddElementToRoot() throws Exception
	{
		String nameOfElement = "Element A";
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		AccountEntry account = new AccountEntry(nameOfElement);
		account.setName("test");
		pwm.addEntry(account);
		
		
		//pwm.createAccountElement(null, nameOfElement, "a", "b", "l", "test");
		pwm.savePasswords();
		s_ac.decryptFile(new File(PW_FILE), new File(PW_FILE+"_addRootElement.txt"));
		assertTrue(pwm.existID(nameOfElement));
		assertEquals("", pwm.getGroupSlot(nameOfElement));
		assertEquals(null, pwm.getGroupSlotElement(nameOfElement));
	}

	@Test
	public void testAddElementToGroup() throws Exception
	{
		String nameOfGroup = "Group with Element";
		String nameOfElement = "Element to Group";
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		GroupEntry group = new GroupEntry(nameOfGroup);
		pwm.addEntry(group);
		GroupEntry fromPWM = pwm.getGroupEntry(nameOfGroup);
		AccountEntry account = new AccountEntry(nameOfElement);
		account.setParentEntry(group);
		pwm.addEntry(account);
		pwm.savePasswords();
		
		s_ac.decryptFile(new File(PW_FILE), new File(PW_FILE+"_addElementToGroup.txt"));
		assertTrue(pwm.existID(nameOfElement));
		assertTrue(pwm.hasChilds(nameOfGroup));
		assertEquals(nameOfGroup, pwm.getGroupSlot(nameOfElement));
		assertEquals(group, pwm.getGroupSlotElement(nameOfElement));
		assertEquals(group, fromPWM);

	}

	@Test
	public void testGetElement() throws Exception
	{
		String nameOfElement = "ElementToGet";
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		
		AccountEntry account = new AccountEntry(nameOfElement);
		account.setName("tttt");
		pwm.addEntry(account);
		
		AccountEntry fromPWM = pwm.getAccountEntry(nameOfElement);
		
		//System.out.println(account);;
		//System.out.println(fromPWM);
		
		pwm.savePasswords();
		s_ac.decryptFile(new File(PW_FILE), new File(PW_FILE+"_getElement.txt"));
		assertTrue(fromPWM != null);
		
		assertEquals(account.getId(), fromPWM.getId());
		assertEquals(account.getName(), fromPWM.getName());
		assertEquals(account.getComment(), fromPWM.getComment());
		assertEquals(account, fromPWM);
	}
	 
	@Test
	public void testGetNotExistingElement() throws FileNotFoundException, IOException, CryptoMethodError, Exception
	{
		String nameOfElement = "ElementDoesNotExist";
		PasswordManager pwm;
		try {
			pwm = new PasswordManager(s_ac, PW_FILE);
			pwm.savePasswords();
			s_ac.decryptFile(new File(PW_FILE), new File(PW_FILE+"_testGetNotExistingElement.txt"));
			PasswordEntry entry =  pwm.getEntry(nameOfElement);
			fail("Should not exist: " + entry);
		} catch (EntryDoesNotExistException e) {
			assertTrue(e.getMessage().startsWith("entry not found"));
		}
	}

	@Test
	public void testDeleteNotPreviousExistingElement() throws Exception
	{
		String nameOfElement = "ElementToDelete";
		PasswordManager pwm;

		pwm = new PasswordManager(s_ac, PW_FILE);
		AccountEntry account = new AccountEntry(nameOfElement);
		account.setLink("test");
		pwm.addEntry(account);
		pwm.deleteEntry(account);
		pwm.savePasswords();
		s_ac.decryptFile(new File(PW_FILE), new File(PW_FILE+"_deleteElement.txt"));
		assertFalse(pwm.existID(nameOfElement));
	}

	@Test
	public void testDeleteElement() throws FileNotFoundException, IOException, CryptoMethodError, Exception
	{
		String nameOfElement = "ElementDoesNotExist";
		AccountEntry account = new AccountEntry(nameOfElement);
		PasswordManager pwm;
		try {
			pwm = new PasswordManager(s_ac, PW_FILE);
			pwm.deleteEntry(account);
			fail();
		} catch (EntryDoesNotExistException e) {
			assertTrue(e.getMessage().startsWith("No such entry"));
		}
	}

	@Test
	public void testIfGroupIsGroup() throws Exception
	{
		String nameOfGroup = "Group is a Group";
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);

		GroupEntry group = new GroupEntry(nameOfGroup);
		pwm.addEntry(group);
		pwm.savePasswords();
		s_ac.decryptFile(new File(PW_FILE), new File(PW_FILE+"_GroupIsGroup.txt"));
		assertTrue(pwm.isGroupElement(nameOfGroup));
	}

	@Test
	public void testIfElementIsGroup() throws FileNotFoundException, JDOMException, IOException, CryptoMethodError, StorageException, EntryDoesNotExistException, EntryAlreadyExistException
	{
		String nameOfElement = "Element is Group";
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);

		AccountEntry account = new AccountEntry(nameOfElement);
		account.setComment("a");
		account.setName("b");

		pwm.addEntry(account);
		pwm.savePasswords();
		s_ac.decryptFile(new File(PW_FILE), new File(PW_FILE+"_ElementIsGroup.txt"));
		assertFalse(pwm.isGroupElement(nameOfElement));
	}

	@Test
	public void testGroupsAreAllGroups() throws FileNotFoundException, JDOMException, IOException, CryptoMethodError, StorageException
	{
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		List<GroupEntry> groups = pwm.getGroupEntryList();
		for(GroupEntry group : groups)
		{
			assertTrue(pwm.isGroupElement(group.getId()));
		}
	}

	@Test
	public void testNullHasChilds() throws FileNotFoundException, JDOMException, IOException, CryptoMethodError, StorageException
	{
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		assertEquals(false, pwm.hasChilds(""));
		assertEquals(false, pwm.hasChilds(null));
	}
	

	
	@Test
	public void testSetNewAccountEntry() throws Exception
	{
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		String id = "new Account Entry";
		String name = "my name";
		String link = "this is not realy a link";
		String comment = "A Test Entry for the new AccountEntry";
		String password = "testPassword09083";
		AccountEntry ae = new AccountEntry(id);
		ae.setName(name);
		ae.setLink(link);
		ae.setComment(comment);
		ae.setPassword(password);
		pwm.addEntry(ae);
		AccountEntry account = pwm.getAccountEntry(id);

		pwm.savePasswords();
		s_ac.decryptFile(new File(PW_FILE), new File(PW_FILE+"_SetNewAccount.txt"));

		assertEquals(ae.getName(), account.getName());
		assertEquals(ae.getId(), account.getId());
		assertEquals(ae.getPassword(), account.getPassword());
		assertEquals(ae.getLink(), account.getLink());
		assertEquals(ae.getComment(), account.getComment());

		assertEquals(ae, account);
	}

	@Test (expected=IllegalArgumentException.class)
	public void testAddNullEntry() throws Exception
	{
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		pwm.addEntry(null);
	}

	@Test
	public void testSetNewGroupEntry() throws Exception
	{
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		String id = "new Group Entry";
		GroupEntry ge = new GroupEntry(id);
		pwm.addEntry(ge);

		GroupEntry entry = pwm.getGroupEntry(id);


		pwm.savePasswords();
		s_ac.decryptFile(new File(PW_FILE), new File(PW_FILE+"_SetNewGroup.txt"));

		assertEquals(ge.getId(),entry.getId());
	}

	@Test
	public void testSetNewGroupAndAccountEntry() throws Exception
	{
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		String id = "new Group Entry with Child";
		GroupEntry ge = new GroupEntry(id);
		pwm.addEntry(ge);

		String idA = "new Account Entry with Parrent";
		String name = "my name";
		String link = "this is not realy a link";
		String comment = "A Test Entry for the new AccountEntry";
		String password = "testPassword09083";
		AccountEntry ae = new AccountEntry(idA);
		ae.setName(name);
		ae.setLink(link);
		ae.setComment(comment);
		ae.setPassword(password);
		ae.setParentEntry(ge);
		pwm.addEntry(ae);

		pwm.savePasswords();
		s_ac.decryptFile(new File(PW_FILE), new File(PW_FILE+"_SetNewGroupAndAccount.txt"));

		assertEquals(ge.getId(),ae.getParentEntry().getId());
	}


	@Test
	public void testGetGroupEntry() throws Exception
	{
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		String id = "new Group Entry with Child testGetGroup";
		GroupEntry ge = new GroupEntry(id);
		pwm.addEntry(ge);
		GroupEntry gePW = pwm.getGroupEntry(id);
		assertEquals(ge, gePW);
	}

	@Test
	public void testGetAccountEntry() throws Exception
	{
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);

		String groupID = "Group for GetAccountEntry()";
		GroupEntry groupEntry = new GroupEntry(groupID);
		pwm.addEntry(groupEntry);

		String idA = "new Account Entry to Test GetAccountEntry()";
		String name = "my name";
		String link = "this is not realy a link";
		String comment = "A Test Entry for the new AccountEntry";
		String password = "testPassword09083";
		AccountEntry ae = new AccountEntry(idA);
		ae.setName(name);
		ae.setLink(link);
		ae.setComment(comment);
		ae.setPassword(password);
		ae.setParentEntry(groupEntry);

		pwm.addEntry(ae);

		AccountEntry aePW = pwm.getAccountEntry(idA);

		assertEquals(ae, aePW);
		assertEquals(groupEntry, aePW.getParentEntry());
	}


	@Test
	public void testGetEntry() throws FileNotFoundException, IOException, EntryDoesNotExistException, StorageException
	{
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		List<GroupEntry> newList = pwm.getGroupEntryList();

		for(GroupEntry group : newList)
		{
			GroupEntry newGroup = (GroupEntry) pwm.getEntry(group.getId());
			assertEquals(group, newGroup);
		}

	}
	
	@Test
	public void testGetAllEntries() throws Exception
	{
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		List<PasswordEntry> allEntries = pwm.getAllEntries();
		assertNotNull(allEntries);
	}
	
	@Test
	public void testGetAllEntries_NotEmpty() throws Exception
	{
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		
		String groupID = "testGetAllEntries_NotEmpty";
		GroupEntry groupEntry = new GroupEntry(groupID);
		pwm.addEntry(groupEntry);
		
		
		String accountId = "testGetAllEntries_NotEmpty_acc";
		AccountEntry account = new AccountEntry(accountId);
		account.setParentEntry(groupEntry);
		pwm.addEntry(account);
		pwm.savePasswords();
		
		List<PasswordEntry> allEntries = pwm.getAllEntries();
		assertNotNull(allEntries);
		assertTrue(allEntries.size() > 0);
		
		assertTrue(allEntries.contains(groupEntry));
		assertTrue(allEntries.contains((PasswordEntry)account));
	}
	
	@Test
	public void testRenameGroup() throws Exception
	{
		String nameOfGroup = "GroupForRename";
		String nameOfRenamedGroup = "GroupRenamed";
		
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);
		GroupEntry group = new GroupEntry(nameOfGroup);
		pwm.addEntry(group);
//		pwm.createGroupElement(nameOfGroup);
		pwm.savePasswords();
		s_ac.decryptFile(new File(PW_FILE), new File(PW_FILE+"_testRenameGroup.txt"));
		
		assertTrue(pwm.existID(nameOfGroup));
		assertFalse(pwm.existID(nameOfRenamedGroup));
		
		pwm.renameElementId(nameOfGroup, nameOfRenamedGroup);
		
		assertFalse(pwm.existID(nameOfGroup));
		assertTrue(pwm.existID(nameOfRenamedGroup));	
	}
	
	@Test
	public void testRenameElement() throws Exception
	{
		String nameOfElement = "ElementForRename";
		String nameOfElementRenamed = "ElementRenamed";
		
		PasswordManager pwm = new PasswordManager(s_ac, PW_FILE);

		AccountEntry account = new AccountEntry(nameOfElement);
		pwm.addEntry(account);
		
		
		
		assertTrue(pwm.existID(nameOfElement));
		assertFalse(pwm.existID(nameOfElementRenamed));
		
		pwm.renameElementId(nameOfElement, nameOfElementRenamed);
		
		assertFalse(pwm.existID(nameOfElement));
		assertTrue(pwm.existID(nameOfElementRenamed));
	}
}
