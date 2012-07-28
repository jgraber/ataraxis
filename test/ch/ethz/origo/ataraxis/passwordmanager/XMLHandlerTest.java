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

package ch.ethz.origo.ataraxis.passwordmanager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import mockit.Mockit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.ethz.origo.ataraxis.TestingHelper;
import ch.ethz.origo.ataraxis.util.FileCopy;

public class XMLHandlerTest 
{
	private static final Logger logger = Logger.getLogger(XMLHandlerTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	private static final String TEST_DIR_DATA = TEST_DIR + "/xmlhandler";
	protected static final String LOG_PROPS_FILE = TEST_DIR + "/config/log4j_test.properties";
	protected static final String PW_FILE = TEST_DIR_DATA + "/passwords.xml";
	private static String ATARAXIS_DEFAULT_ACCOUNTS = System.getProperty("user.dir") + "/application_data/user_data_template/template.xml";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception 
	{
		PropertyConfigurator.configure(LOG_PROPS_FILE); 
		logger.debug("XMLHandlerTest startet");
	
		File xmlTestDir = new File(TEST_DIR_DATA);
		
		if(xmlTestDir.exists())
		{
			//System.out.println("Exist: " + xmlTestDir.getAbsolutePath());
			TestingHelper.deleteDir(xmlTestDir);
			xmlTestDir.mkdirs();
		}
		else
		{
			xmlTestDir.mkdirs();
			//System.out.println("created");
		}
	
		FileCopy.copyFile(ATARAXIS_DEFAULT_ACCOUNTS, PW_FILE);
	}

	
	@Test
	public void testCreateHandler() throws FileNotFoundException, StorageException
	{
		File accountFile = new File(PW_FILE);
		XMLHandler xml = new XMLHandler(new ExceptionDiskAccess(0,0), accountFile);
		assertNotNull(xml);
	}
	
	@Test(expected=StorageException.class)
	public void testCreateHandler_FileNotFoundException_OnLoad() throws FileNotFoundException, StorageException
	{
		File accountFile = new File(PW_FILE);
		XMLHandler xml = new XMLHandler(new ExceptionDiskAccess(1,1), accountFile);
		assertNotNull(xml);
	}
	@Test(expected=StorageException.class)
	public void testCreateHandler_IOException_OnLoad() throws FileNotFoundException, StorageException
	{
		File accountFile = new File(PW_FILE);
		XMLHandler xml = new XMLHandler(new ExceptionDiskAccess(1,1), accountFile);
		assertNotNull(xml);
	}
	@Test(expected=StorageException.class)
	public void testCreateHandler_IOException_OnSave() throws FileNotFoundException, StorageException
	{
		File accountFile = new File(PW_FILE);
		XMLHandler xml = new XMLHandler(new ExceptionDiskAccess(0,1), accountFile);
		xml.savePasswords();
		assertNotNull(xml);
	}
	
	@Test(expected=FileNotFoundException.class)
	public void testNullFileConstructor() throws FileNotFoundException, StorageException
	{
		XMLHandler xml = new XMLHandler(new ExceptionDiskAccess(0,0), null);
		fail("Should not exist: "+ xml);
	}
	

	@Test
	public void testStoreInitialFile() throws IOException, StorageException, NoSuchAlgorithmException
	{
		
		(new File(PW_FILE)).delete();
		FileCopy.copyFile(ATARAXIS_DEFAULT_ACCOUNTS, PW_FILE);
		
		XMLHandler xml = createXMLHandler();
		xml.savePasswords();
		
		FileCopy.copyFile(PW_FILE, PW_FILE+"save.xml");
		
		XMLHandler xmlAfterStore = createXMLHandler();
		
		assertEquals(xml.getPasswordEntryList(), xmlAfterStore.getPasswordEntryList());
	}
	
	@Test
	public void testInitialGroupList() throws FileNotFoundException, StorageException
	{
		XMLHandler xml = createXMLHandler();
		
		List<GroupEntry> groups = xml.getGroupEntryList();
		
		assertEquals(1, groups.size());
		assertEquals("E-Mail", groups.get(0).getId());
	}
	
	@Test 
	public void testExistElement() throws FileNotFoundException, StorageException
	{
		XMLHandler xml = createXMLHandler();
		
		assertTrue(xml.existID("E-Mail"));
		assertFalse(xml.existID("this does not Exist"));
	}
	
	@Test
	public void testSetNewGroupEntry() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		
		String id = "new Group Entry";
		GroupEntry ge = new GroupEntry(id);
		xml.addEntry(ge);
		
		GroupEntry group = xml.getGroupEntry(id);
		
		assertEquals(ge, group);
	}
	
	@Test
	public void testSetNewAccountEntry() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		
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
		xml.addEntry(ae);
		
		AccountEntry account = xml.getAccountEntry(id);
		
		assertEquals(ae, account);
	}
	
	
	@Test
	public void testSetNewGroupAndAccountEntry() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		
		String id = "new Group Entry with Child";
		GroupEntry ge = new GroupEntry(id);
		xml.addEntry(ge);
		
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
		xml.addEntry(ae);
		
		AccountEntry account = xml.getAccountEntry(idA);
		GroupEntry group = xml.getGroupEntry(id);
		
		assertEquals(ae, account);
		assertEquals(ge, group);
	}
	
	@Test
	public void testDeleteElement() throws Exception
	{
		
		XMLHandler xml = createXMLHandler();
		
		String idA = "AccountToDelete";
		AccountEntry ae = new AccountEntry(idA);
		
		assertFalse(xml.existID(idA));
		
		xml.addEntry(ae);
		
		assertTrue(xml.existID(idA));
		
		xml.deleteEntry(ae);
		
		assertFalse(xml.existID(idA));
	}
	
	
	@Test
	public void testGetAllElements() throws FileNotFoundException, StorageException
	{
		XMLHandler xml = createXMLHandler();
		xml.savePasswords();
		List<PasswordEntry> all = xml.getPasswordEntryList();
		assertNotNull(all);
	}
	
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testRenameEmptyOld() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		xml.renameElementId(null, "a");
	}
	@Test(expected=IllegalArgumentException.class)
	public void testRenameEmptyNew() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		xml.renameElementId("b",null);
	}
	@Test(expected=EntryDoesNotExistException.class)
	public void testRenameNotExistOld() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		xml.renameElementId("b","c");
	}	
	@Test(expected=EntryAlreadyExistException.class)
	public void testRenameExistNew() throws Exception
	{
		String name = "This should exist";
		XMLHandler xml = createXMLHandler();
		AccountEntry account = new AccountEntry(name);
		xml.addEntry(account);
		xml.renameElementId(name,name);
	}	
	@Test
	public void testRenameNormalAccount() throws Exception
	{
		String name = "testRenameNormalAccount";
		String newName = "new "+name;
		XMLHandler xml = createXMLHandler();
		AccountEntry account = new AccountEntry(name);
		account.setComment("Kommentar");
		account.setLink("ein Link");
		account.setName("rename Test");
		account.setPassword("myPassword");
		xml.addEntry(account);
		xml.renameElementId(name,newName);
		
		FileCopy.copyFile(PW_FILE, PW_FILE+"_testRenameNormalAccount");
		
		AccountEntry renamedAccount = xml.getAccountEntry(newName);
		assertEquals(newName, renamedAccount.getId());
		assertEquals(account.getName(), renamedAccount.getName());
		assertEquals(account.getPassword(), renamedAccount.getPassword());
		assertEquals(account.getLink(), renamedAccount.getLink());
		assertEquals(account.getComment(), renamedAccount.getComment());
	}
	
	@Test
	public void testRenameNormalGroup() throws Exception
	{
		String name = "testRenameNormalGroup";
		String newName = "new "+name;
		XMLHandler xml = createXMLHandler();
		GroupEntry group = new GroupEntry(name);
		xml.addEntry(group);
		xml.renameElementId(name,newName);
		
		FileCopy.copyFile(PW_FILE, PW_FILE+"_testRenameNormalGroup");
		
		GroupEntry renamedGroup = xml.getGroupEntry(newName);
		assertEquals(newName, renamedGroup.getId());
	}
	
	@Test
	public void testRenameGroupWithAccountRename() throws Exception
	{
		String name = "testRenameGroupWithAccountRename";
		String newName = "new "+name;
		XMLHandler xml = createXMLHandler();
		
		GroupEntry group = new GroupEntry("Group with Account to rename");
		xml.addEntry(group);
		
		AccountEntry account = new AccountEntry(name);
		account.setComment("Kommentar");
		account.setLink("ein Link");
		account.setName("rename Test");
		account.setPassword("myPassword");
		account.setParentEntry(group);
		xml.addEntry(account);
		xml.renameElementId(name,newName);
		
		FileCopy.copyFile(PW_FILE, PW_FILE+"_testRenameGroupWithAccountRename");
		
		AccountEntry renamedAccount = xml.getAccountEntry(newName);
		assertEquals(newName, renamedAccount.getId());
		assertEquals(account.getName(), renamedAccount.getName());
		assertEquals(account.getPassword(), renamedAccount.getPassword());
		assertEquals(account.getLink(), renamedAccount.getLink());
		assertEquals(account.getComment(), renamedAccount.getComment());
		assertEquals(account.getParentEntry(), renamedAccount.getParentEntry());
		
		assertTrue(xml.hasChilds(group.getId()));
	}
	
	@Test
	public void testRenameGroupWithAccount() throws Exception
	{
		String name = "testRenameGroupWithAccount";
		String newName = "new "+name;
		XMLHandler xml = createXMLHandler();
		
		GroupEntry group = new GroupEntry(name);
		xml.addEntry(group);
		
		AccountEntry account = new AccountEntry("Element with renamed Group");
		account.setComment("Kommentar");
		account.setLink("ein Link");
		account.setName("rename Test");
		account.setPassword("myPassword");
		account.setParentEntry(group);
		xml.addEntry(account);
		xml.renameElementId(name,newName);
		
		FileCopy.copyFile(PW_FILE, PW_FILE+"_testRenameGroupWithAccount");
		
		GroupEntry groupRenamed = xml.getGroupEntry(newName);
		
		AccountEntry renamedAccount = xml.getAccountEntry(account.getId());
		assertEquals(account.getId(), renamedAccount.getId());
		assertEquals(account.getName(), renamedAccount.getName());
		assertEquals(account.getPassword(), renamedAccount.getPassword());
		assertEquals(account.getLink(), renamedAccount.getLink());
		assertEquals(account.getComment(), renamedAccount.getComment());
		assertEquals(groupRenamed, renamedAccount.getParentEntry());
		
		assertTrue(xml.hasChilds(groupRenamed.getId()));
		assertFalse(xml.existID(group.getId()));
	}
	
	
	@Test
	public void testGetAllEntries() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		List<PasswordEntry> entries = xml.getAllEntries();
		assertNotNull(entries);
		assertTrue(entries.size() > 0);
	}
	
	
	@Test(expected=IllegalArgumentException.class)
	public void testStoreWrongType() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		xml.addEntry(new DummyPWEntry());
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testEntryIsNull() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		xml.addEntry(null);
	}
	
	@Test(expected=EntryAlreadyExistException.class)
	public void testEntryDouble() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		AccountEntry entry = new AccountEntry("exist");
		xml.addEntry(entry);
		xml.addEntry(entry);
	}
	
	@Test(expected=EntryDoesNotExistException.class)
	public void testDelete_Null() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		xml.deleteEntry(null);
	}
	
	@Test(expected=EntryDoesNotExistException.class)
	public void testDelete_NotExist() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		AccountEntry entry = new AccountEntry("DoesNotRealyExist");
		xml.deleteEntry(entry);
	}
	
	
	private XMLHandler createXMLHandler() throws FileNotFoundException, StorageException
	{
		return createXMLHandler(null);
	}
	
	private XMLHandler createXMLHandler(File inputFile) throws FileNotFoundException, StorageException
	{
		File accountFile = null;
		
		if(inputFile != null)
		{
			accountFile = inputFile;
		}
		else
		{
			accountFile = new File(PW_FILE);	
		}
		
		//System.out.println(accountFile);
		XMLHandler xml = new XMLHandler(new ExceptionDiskAccess(0,0), accountFile);
	
		return xml;
	}
	
	
	@Test(expected=EntryDoesNotExistException.class)
	public void deleteElement_JDOMException() throws Exception
	{
		File accountFile = new File(PW_FILE);
		accountFile.createNewFile();
		Mockit.redefineMethods(XMLHandler.class, new Object() {
			@SuppressWarnings("unused")
			public void deleteElement(String ElementID) throws JDOMException
			{
				throw new JDOMException();
			}
		});
		
		XMLHandler xml = new XMLHandler(new ExceptionDiskAccess(0,0), accountFile);
		PasswordEntry entry = new AccountEntry("deleteElement_JDOMException");
		xml.addEntry(entry);
		xml.deleteEntry(entry);
		assertNotNull(xml);
	}
	
	@Test(expected=EntryDoesNotExistException.class)
	public void addEntry_Parent_JDOMException() throws Exception
	{
		File accountFile = new File(PW_FILE);
		accountFile.createNewFile();
		Mockit.redefineMethods(XMLHandler.class, new Object() {
			@SuppressWarnings("unused")
			public Element getElement(String ElementID) throws JDOMException
			{
				throw new JDOMException();
			}
		});
		
		XMLHandler xml = new XMLHandler(new ExceptionDiskAccess(0,0), accountFile);
		GroupEntry group = new GroupEntry("Group_addEntry_Parent_JDOMException");
		AccountEntry entry = new AccountEntry("Account_addEntry_Parent_JDOMException");
		entry.setParentEntry(group);
		xml.addEntry(entry);
		
		fail("JDOM Exception expected");
	}

	@Test(expected=EntryDoesNotExistException.class)
	public void addEntry_Create_JDOMException() throws Exception
	{
		File accountFile = new File(PW_FILE);
		accountFile.createNewFile();
		Mockit.redefineMethods(XMLHandler.class, new Object() {
			@SuppressWarnings("unused")
			public void createAccountElement(AccountEntry entry) 
			throws JDOMException, EntryDoesNotExistException
			{
				throw new JDOMException();
			}
		});
		
		XMLHandler xml = new XMLHandler(new ExceptionDiskAccess(0,0), accountFile);
		AccountEntry entry = new AccountEntry("Account_addEntry_Parent_JDOMException");
		xml.addEntry(entry);
		
		fail("JDOM Exception expected");
	}
	
	@Test(expected=EntryDoesNotExistException.class)
	public void getEntry_DontExist() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		xml.getEntry("getEntry_DontExist");
	}
	
	@Test(expected=EntryDoesNotExistException.class)
	public void getGroupEntry_DontExist() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		xml.getGroupEntry("getGroupEntry_DontExist");
	}
	
	@Test(expected=EntryDoesNotExistException.class)
	public void getAccountEntry_DontExist() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		xml.getAccountEntry("getAccountEntry_DontExist");
	}
	
	
	@Test
	public void getGroupEntryList_JDOMException() throws Exception
	{
		File accountFile = new File(PW_FILE);
		accountFile.createNewFile();
		Mockit.redefineMethods(XMLHandler.class, new Object() {
			@SuppressWarnings({ "unused", "rawtypes" })
			public List getGroupList() throws JDOMException
			{
				throw new JDOMException();
			}
		});
		
		XMLHandler xml = new XMLHandler(new ExceptionDiskAccess(0,0), accountFile);
		assertNotNull(xml.getGroupEntryList());
	}
	
	@Test
	public void getAccountEntryList_JDOMException() throws Exception
	{
		File accountFile = new File(PW_FILE);
		accountFile.createNewFile();
		Mockit.redefineMethods(XMLHandler.class, new Object() {
			@SuppressWarnings({ "unused", "rawtypes" })
			public List getAccountList() throws JDOMException
			{
				throw new JDOMException();
			}
		});
		
		XMLHandler xml = new XMLHandler(new ExceptionDiskAccess(0,0), accountFile);
		assertNotNull(xml.getAccountEntryList());
	}
	
	@Test
	public void getAccountEntryList_EntryDoesNotExistException() throws Exception
	{
		File accountFile = new File(PW_FILE);
		accountFile.createNewFile();
		Mockit.redefineMethods(XMLHandler.class, new Object() {
			@SuppressWarnings({ "unused" })
			public AccountEntry getAccountEntry(String id) throws EntryDoesNotExistException
			{
				throw new EntryDoesNotExistException();
			}
		});
		
		XMLHandler xml = new XMLHandler(new ExceptionDiskAccess(0,0), accountFile);
		assertNotNull(xml.getAccountEntryList());
	}
	
	@Test
	public void hasChilds_Null() throws Exception
	{
		XMLHandler xml = createXMLHandler();
		assertFalse("should have no children",xml.hasChilds(null));
	}
	
	@Test(expected=StorageException.class)
	public void renameElementId_JDOMException() throws Exception
	{
		File accountFile = new File(PW_FILE);
		accountFile.createNewFile();
		Mockit.redefineMethods(XMLHandler.class, new Object() {
			@SuppressWarnings({ "unused" })
			public Element getElement(String ElementID) throws JDOMException
			{
				throw new JDOMException();
			}
		});
		String nameOld = "renameElementId_JDOMException_old";
		XMLHandler xml = createXMLHandler();
		AccountEntry accOld = new AccountEntry(nameOld);
		xml.addEntry(accOld);
		xml.renameElementId(nameOld, "renameElementId_JDOMException_new");
	}
	
	class ExceptionDiskAccess implements DiskAccess
	{
		private int exceptionInput;
		private int exceptionOutput;
		public ExceptionDiskAccess(int exceptionInput, int exceptionOutput)
		{
			this.exceptionInput = exceptionInput;
			this.exceptionOutput = exceptionOutput;
		}
		public InputStream getInputStream(File inputFile)
				throws FileNotFoundException, IOException 
		{
			if(exceptionInput == 1)
				throw new FileNotFoundException();
			if(exceptionInput == 2)
				throw new IOException();
			return new FileInputStream(inputFile);
		}
		public OutputStream getOutputStream(File outputFile)
				throws FileNotFoundException, IOException 
		{
			if(exceptionOutput == 1)
				throw new FileNotFoundException();
			if(exceptionOutput == 2)
				throw new IOException();
			return new FileOutputStream(outputFile);
		}
	}
	
	class DummyPWEntry implements PasswordEntry
	{
		private String id ="1";
		public DummyPWEntry(){}
		public DummyPWEntry(String id){ this.id = id;}
		@Override
		public String getId() { return id; }
		@Override
		public PasswordEntry getParentEntry() { return null; }
		@Override
		public String getType() { return "dummy"; }
		@Override
		public int compareTo(PasswordEntry o) {
			return 0;
		}
	}
	
	public class SaxMock extends org.jdom.input.SAXBuilder
	{
		
		public org.jdom.Document build(InputStream in)
        throws JDOMException, java.io.IOException
		{
			throw new JDOMException();
		}

		public Document build(java.io.File file)
        throws JDOMException,
               java.io.IOException
       		{
       			throw new JDOMException();
       		}
	}
}
