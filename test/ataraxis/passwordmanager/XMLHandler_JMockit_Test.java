/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2015 Johnny Graber & Andreas Muedespacher
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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import mockit.Mockit;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.junit.BeforeClass;
import org.junit.Test;

import ataraxis.TestingHelper;
import ataraxis.passwordmanager.AccountEntry;
import ataraxis.passwordmanager.DiskAccess;
import ataraxis.passwordmanager.EntryDoesNotExistException;
import ataraxis.passwordmanager.GroupEntry;
import ataraxis.passwordmanager.PasswordEntry;
import ataraxis.passwordmanager.XMLHandler;
import ataraxis.util.FileCopy;


public class XMLHandler_JMockit_Test {
	private static final Logger logger = Logger.getLogger(XMLHandlerTest.class);
	private static final String TEST_DIR = System.getProperty("user.dir") + "/test";
	private static final String TEST_DIR_DATA = TEST_DIR + "/testrun/xmlhandler";
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
		XMLHandler xml = createXMLHandler(null);
		AccountEntry accOld = new AccountEntry(nameOld);
		xml.addEntry(accOld);
		xml.renameElementId(nameOld, "renameElementId_JDOMException_new");
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
}
