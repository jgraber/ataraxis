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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.input.sax.XMLReaders;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

/**
 * XMLHandler handles all the XML for the Account storage.
 * 
 * @author J. Graber
 * @version 1.0
 *
 */
public class XMLHandler implements PasswordStore
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(XMLHandler.class);


	private Element s_rootElement;

	private Document s_doc;

	//private static XPath xPath;

	private File accountFile;
	
	private DiskAccess diskAccess;

	/**
	 * 
	 * @param diskAccess to handle the (encrypted) Input/Output-Streams.
	 * @param accountFile XML-File who holds the Accounts
	 * @throws FileNotFoundException if accountFile is missing
	 * @throws StorageException if accountFile cannot be loaded.
	 */
	public XMLHandler(DiskAccess diskAccess, File accountFile) throws FileNotFoundException, StorageException
	{

		logger.debug("XMLHandler(File accountFile) - start");

		if(accountFile == null || !accountFile.exists())
		{
			throw new FileNotFoundException("accountFile missing");
		}

		// set fields
		this.accountFile = accountFile;
		this.diskAccess = diskAccess;


		loadPasswords();

		logger.debug("XMLHandler(File accountFile) - created");


	}

	
	/**
	 * Get a InputStream from the DiskAccess Implementation.
	 *
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private InputStream getAccountFileInputStream() throws FileNotFoundException, IOException
	{
		return diskAccess.getInputStream(accountFile);
	}

	
	/**
	 * Get a OutputStream from the DiskAccess Implementation.
	 *
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private OutputStream getAccountFileOutputStream() throws FileNotFoundException, IOException
	{
		return diskAccess.getOutputStream(accountFile);
	}

	
	/**
	 * Load the Passwords from a XML File.
	 *
	 * @throws StorageException if an error occurs
	 */
	public void loadPasswords() throws StorageException 
	{
		try
		{
			// Build document with on-the-fly decryption
			SAXBuilder xmlReader = new SAXBuilder(XMLReaders.NONVALIDATING);

			InputStream inStream = getAccountFileInputStream();
			s_doc = xmlReader.build(inStream);
			inStream.close();

			// set root Element of Document
			s_rootElement = s_doc.getRootElement();
		}
		catch (JDOMException e) 
		{
			logger.fatal(e);
			throw new StorageException(e);
		}
		catch (IOException e)
		{
			logger.fatal(e);
			throw new StorageException(e);
		}
	}

	
	/**
	 * Save the File encrypted to the disk.
	 *
	 * @throws StorageException 
	 */
	public void savePasswords() throws StorageException
	{
		try 
		{
			OutputStream out = getAccountFileOutputStream();

			XMLOutputter serializer = new XMLOutputter(Format.getPrettyFormat());
			serializer.output(s_doc,out);
			//serializer.output(s_doc,System.out);
			out.flush();
			out.close();
		} 
		catch (IOException e) 
		{
			logger.fatal(e);
			throw new StorageException(e);
		}
	}


	/**
	 * Add an PasswordEntry to the PasswordManager.
	 *
	 * @param entry the new PasswordEntry
	 * @throws EntryDoesNotExistException if Parent does not exist
	 * @throws EntryAlreadyExistException 
	 * @throws StorageException if save fails
	 */
	public void addEntry(PasswordEntry entry) throws EntryDoesNotExistException, EntryAlreadyExistException, StorageException 
	{
		
		if(entry == null)
		{
			throw new IllegalArgumentException("entry can not be null");
		}
		
		if(existID(entry.getId()))
		{
			throw new EntryAlreadyExistException("entry exist already");
		}

		if(entry.getType().equals("group"))
		{
			createGroupElement(entry.getId());
		}
		else if(entry.getType().equals("account"))
		{
			try {
				AccountEntry accountEntry = (AccountEntry) entry;
				createAccountElement(accountEntry);
			} 
			catch (JDOMException e) {
				logger.fatal(e);
				throw new EntryDoesNotExistException("No such parent");
			}
			
		}
		else
		{
			throw new IllegalArgumentException("entry type is not known");
		}
		
		
		savePasswords();
		
	}
	
	/**
	 * Create a new account-Element
	 * 
	 * @param entry the AccountEntry
	 * @throws JDOMException by Errors with JDOM
	 * @throws EntryDoesNotExistException 
	 */
	private void createAccountElement(AccountEntry entry) 
	throws JDOMException, EntryDoesNotExistException
	{
		// create Element sub-structure
		Element newAccountElement = new Element("account");
		newAccountElement.setAttribute("id", entry.getId());
		
		Element newName = new Element("name");
		newName.setText(entry.getName());
		
		Element newPass = new Element("password");
		newPass.setText(entry.getPassword());
		
		Element newLink = new Element("link");
		newLink.setText(entry.getLink());
		
		Element newComment = new Element("comment");
		newComment.setText(entry.getComment());
		
		// put newPWElement together
		newAccountElement.addContent(newName);
		newAccountElement.addContent(newPass);
		newAccountElement.addContent(newLink);
		newAccountElement.addContent(newComment);
		
		// add parent
		if (entry.getParentEntry() == null)
		{
			s_rootElement.addContent(newAccountElement);
		}
		else
		{
			try {
				Element parrent = getElement(entry.getParentEntry().getId());
				parrent.addContent(newAccountElement);
			} catch (JDOMException e) {
				logger.fatal(e);
				throw new EntryDoesNotExistException("No such parent");
			}
		}
	}
	
	/**
	 * Create a group-Element
	 * 
	 * @param GroupName the Name of the Group (== Element ID)
	 */
	private void createGroupElement(String GroupName){
		
		// Create Element
		Element newGroupElement = new Element("group");
		newGroupElement.setAttribute("id", GroupName);
		
		// add to root
		s_doc.getRootElement().addContent(newGroupElement);
	}
	
	
	/**
	 * Delete the entry.
	 * 
	 * @param entry
	 * @throws EntryDoesNotExistException if entry does not exist
	 * @throws StorageException if changed accounts cannot be saved
	 */
	public void deleteEntry(PasswordEntry entry) throws EntryDoesNotExistException, StorageException
	{
		if(entry == null || !existID(entry.getId()))
		{
			throw new EntryDoesNotExistException("No such entry");
		}
		
		try 
		{
			deleteElement(entry.getId());
		} 
		catch (JDOMException e) 
		{
			logger.error(e.getMessage());
			throw new EntryDoesNotExistException("No such entry " + entry.getId());
		}
		
		savePasswords();
	}
	
	
	/**
	 * Delete the Element with the ID ElementID and all sub-Elements
	 *
	 * @param ElementID ID of Element 
	 * @throws JDOMException if Element does not exist
	 */
	private void deleteElement(String ElementID) throws JDOMException
	{
		XPathExpression<Element> xpath = XPathFactory.instance().compile("//*[@id='"+ElementID+"']", 
				Filters.element());
		Element el = xpath.evaluateFirst(s_doc);
		
		if(el != null) 
		{
			el.detach();
		}
		else 
		{
			throw new JDOMException("Element not Found");
		}
	}
	
	
	/**
	 * Get a PasswordEntry with the given id.
	 *
	 * @param id
	 * @return requested PasswordEntry
	 * @throws EntryDoesNotExistException
	 */
	public PasswordEntry getEntry(String id) throws EntryDoesNotExistException 
	{
		PasswordEntry passwordEntry = null;
		if(existID(id))
		{
			if(isGroupElement(id))
			{
				passwordEntry = getGroupEntry(id);
			}
			else
			{
				passwordEntry = getAccountEntry(id);
			}
		}
		else
		{
			throw new EntryDoesNotExistException("entry not found " + id);
		}
		
		return passwordEntry;
	}
	
	
	/**
	 * Get a GroupEntry with the given id.
	 *
	 * @param id
	 * @return the requested GroupEntry
	 * @throws EntryDoesNotExistException
	 */
	public GroupEntry getGroupEntry(String id) throws EntryDoesNotExistException
	{
		GroupEntry groupEntry = null;
		
		try 
		{
			Element groupElement = getElement(id);
			if(groupElement != null)
			{
				if(isGroupElement(id))
				{
					String entryId = groupElement.getAttributeValue("id");
					groupEntry = new GroupEntry(entryId);
				}
			}
		} 
		catch (JDOMException e) 
		{
			throw new EntryDoesNotExistException("entry not found");
		}
		
		return groupEntry;
	}

	
	/**
	 * Get a AccountEntry with the given id.
	 * 
	 * @param id
	 * @return the requested AccountEntry
	 * @throws EntryDoesNotExistException
	 */
	public AccountEntry getAccountEntry(String id) throws EntryDoesNotExistException 
	{
		AccountEntry accountEntry = null;

		try 
		{
			Element accountElement = getElement(id);
			if(accountElement != null)
			{
				if(! isGroupElement(id))
				{
					String entryId = accountElement.getAttributeValue("id");
					accountEntry = new AccountEntry(entryId);
					accountEntry.setName(accountElement.getChildText("name"));
					accountEntry.setPassword(accountElement.getChildText("password"));
					accountEntry.setLink(accountElement.getChildText("link"));
					accountEntry.setComment(accountElement.getChildText("comment"));
					
					String parentId = accountElement.getParentElement().getAttributeValue("id");
					
					if(parentId != null && isGroupElement(parentId))
					{
						GroupEntry group = new GroupEntry(parentId);
						accountEntry.setParentEntry(group);
					}
				}
			}
			/*else
			{
				throw new EntryDoesNotExistException("entry not found");
			}*/
		} catch (JDOMException e) {
			logger.fatal(e);
			throw new EntryDoesNotExistException("entry not found");
		}
		
		
		return accountEntry;
	}

	
	/**
	 * Returns true if the Element exist, false otherwise
	 * 
	 * @param ElementID the ID of the Element
	 * @return true if exist, false otherwise
	 */
	public boolean existID(String EntryID)
	{
		boolean exist = false;

		try 
		{
			XPathExpression<Element> xpath = XPathFactory.instance().compile("//*[@id='"+EntryID+"']", 
					Filters.element());
			Element tempElement = xpath.evaluateFirst(s_doc);
			
			if(tempElement != null) 
				exist = true;
		}
		catch (Exception e) 
		{
			logger.debug("Element " + EntryID + " NOT found");
		}	

		return exist;
	}
	

	/**
	 * Check if ElementID is a Element of type group
	 * 
	 * @param ElementID the ID of the Element
	 * @return true if it is a group, false otherwise
	 */
	public boolean isGroupElement(String ElementID)
	{
		boolean isGroupElement = false;
		
		try 
		{
			XPathExpression<Element> xpath = XPathFactory.instance().compile("//group[@id='"+ElementID+"']", 
					Filters.element());
			Element tempElement = xpath.evaluateFirst(s_doc);
			
			if(tempElement != null) 
			{
				isGroupElement = true;
			}
		} 
		catch (Exception e) 
		{
			logger.debug("Element " + ElementID + " NOT found");
		}	
		
		return isGroupElement;
	}
	
	
	/**
	 * Return a List of all entries (account and groups).
	 *
	 * @return
	 */
	public List<PasswordEntry> getPasswordEntryList()
	{
		List<PasswordEntry> entryList = new ArrayList<PasswordEntry>();

		for(PasswordEntry entry : getGroupEntryList())
		{
			entryList.add(entry);
		}
		
		for(PasswordEntry entry : getAccountEntryList())
		{
			entryList.add(entry);
		}
		
		
		return entryList;
	}
	
	
	/**
	 * Return a List of all group entries.
	 *
	 * @return the GroupList
	 */
	@SuppressWarnings("rawtypes")
	public List<GroupEntry> getGroupEntryList()
	{
		List<GroupEntry> groupList = new ArrayList<GroupEntry>();
		
		try {
			List groups = getGroupList();
			
			for(int i = 0; i < groups.size(); i++)
			{
				groupList.add(new GroupEntry(((Element)groups.get(i)).getAttributeValue("id")));	
			}
			
		} 
		catch (JDOMException e) 
		{
			logger.fatal(e);
		}
		Collections.sort(groupList);
		return groupList;	
	}
	
	
	/**
	 * Return a List of all groups
	 * @return the GroupList
	 * @throws JDOMException by problems with JDOM 
	 */
	private List<Element> getGroupList() throws JDOMException
	{
		XPathExpression<Element> xpath = XPathFactory.instance().compile("//group", Filters.element());
		return xpath.evaluate(s_doc);
	}
	
	
	/**
	 * Return a List of all account entries.
	 *
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public List<AccountEntry> getAccountEntryList()
	{
		List<AccountEntry> accountList = new ArrayList<AccountEntry>();
		
		try 
		{
			List accounts = getAccountList();
			
			for(int i = 0; i < accounts.size(); i++)
			{
				Element current = (Element)accounts.get(i);
				accountList.add(getAccountEntry(current.getAttributeValue("id")));
			}
			
		} 
		catch (JDOMException e) 
		{
			logger.fatal(e);
		} catch (EntryDoesNotExistException e) 
		{
			logger.fatal(e);
		}
		
		return accountList;	
	}
	

	/**
	 * Return a List of all entries
	 * @return the GroupList
	 * @throws JDOMException by problems with JDOM 
	 */
	private List<Element> getAccountList() throws JDOMException
	{
		XPathExpression<Element> xpath = XPathFactory.instance().compile("//account", Filters.element());
		return xpath.evaluate(s_doc);
	}
	
	
	/**
	 * Check if the ElementId has Child-Elements.
	 * 
	 * @param ElementID
	 * @return true if Child's exist, false otherwise
	 */
	public boolean hasChilds(String ElementID) 
	{
		boolean hasChilds = false;

		if(ElementID == null || ElementID.equals(""))
		{
			hasChilds = false;
		}
		else
		{
			XPathExpression<Element> xpath = XPathFactory.instance().compile("//group[@id='"+ElementID+"']", 
					Filters.element());
			Element tempElement = xpath.evaluateFirst(s_doc);

			if (tempElement == null)
			{
				hasChilds = false;
			}
			else 
			{
				List<Element> childList = tempElement.getChildren();
				hasChilds = (childList.size() > 0);
			}
		}
		
		return hasChilds;
	}

	
	/**
	 * Get the Slot in which belongs to the ElementID.
	 * If the ElementID belongs to an account-Element, then it 
	 * returns the parent-group or null for the Root-Item. 
	 * If it is a group-Elment, it retrun the group-Element.
	 * 
	 * @param ElementID the ID of the Element
	 * @return the matching slot
	 */
	public String getGroupSlot(String ElementID) 
	{
		String parent = "";

		GroupEntry parentEntry = getGroupSlotElement(ElementID);

		if(parentEntry != null)
		{
			parent = parentEntry.getId();
		}
		else 
		{
			if (isGroupElement(ElementID))	
			{
				parent = ElementID;
			}
		}

		return parent;
	}

	
	/**
	 * Return the Slot Element. Same as getGroupSlot(), but isted of a String of 
	 * the ElementID it returns the Element itself.
	 *
	 * @param ElementID
	 * @return the slot Element
	 */
	public GroupEntry getGroupSlotElement(String ElementID) 
	{
		GroupEntry parentGroup = null;

		try {
			PasswordEntry entry = getEntry(ElementID);

			parentGroup = (GroupEntry) entry.getParentEntry();


		} 
		catch (EntryDoesNotExistException e) 
		{
			// normal for accounts with no parent
			logger.debug("No parent found for entry " + ElementID);
		}

		return parentGroup;
	}
	
	
	/**
	 * Rename a Entry.
	 *
	 * @param currentName Name of an existing entry
	 * @param newName new Name
	 * @throws EntryDoesNotExistException if currentName does not exist
	 * @throws EntryAlreadyExistException if new Name allready exist
	 * @throws StorageException when save fails
	 */
	public void renameElementId(String currentName, String newName)
			throws EntryDoesNotExistException, EntryAlreadyExistException,
			StorageException 
	{
		if(currentName == null || newName == null || 
				currentName.trim().equals("") || newName.trim().equals(""))
		{
			throw new IllegalArgumentException("entry can not be null");
		}
		
		if(!existID(currentName))
		{
			throw new EntryDoesNotExistException("entry not found " + currentName);
		}
		
		if(existID(newName))
		{
			throw new EntryAlreadyExistException("entry exist already " + newName);
		}
		
		 
		try 
		{
			Element element = getElement(currentName);
			element.setAttribute("id", newName); 
			savePasswords();
		} 
		catch (JDOMException e) 
		{
			logger.fatal(e.getMessage());
			throw new StorageException(e);
		}
	}

	
	/**
	 * Return the Element with the ID ElementID
	 *
	 * @param ElementID the ID of the Element (group or account)
	 * @throws JDOMException by problems with jdom
	 */
	private Element getElement(String ElementID) throws JDOMException
	{
		Element returnElement;

		XPathExpression<Element> xpath = XPathFactory.instance().compile("//*[@id='"+ElementID+"']", 
				Filters.element());
		Element tempElement = xpath.evaluateFirst(s_doc);
					
		if(tempElement != null) 
		{
			returnElement = tempElement;
		}
		else 
		{
			throw new JDOMException("Element not Found");
		}
		return returnElement;
	}
	
	
	/**
	 * Sort Child-Elements of the parent with the Comparator comp 
	 * @param parent the parent Element
	 * @param comp the Comparator
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })	// Compiler-Errors with Generics are suppressed
	private void sortElements(Element parent, Comparator comp) 
	{
		logger.debug("sortElements(Element, Comparator) - start");

		// Create a new static list of child elements
		// and sort it.
		List<Element> children = new ArrayList<Element>(parent.getChildren());

		Collections.sort(children, comp);

		// remove from every Element the Parent-Node-Link
		ListIterator<Element> childrenIter = children.listIterator();

		while(childrenIter.hasNext())
		{
			Object obj = childrenIter.next();
			if (obj instanceof Element)
				((Element) obj).detach();
		}

		// set the new Child structure to the parent
		parent.setContent(children);

		logger.debug("sortElements(Element, Comparator) - end");
	}
	


	/**
	 * Get all Entries of the PasswordStore
	 * 
	 * @return
	 */
	public List<PasswordEntry> getAllEntries() 
	{
		List<PasswordEntry> allEntries = null;
		
		
		AccountSorter as = new AccountSorter();
		sortElements(s_rootElement,as);
		
		List<PasswordEntry> allChilds = getElementSubTree(s_rootElement);
		if(allChilds != null && allChilds.size() > 0)
		{
			allEntries = allChilds;
		}
		
		Collections.sort(allEntries);
		return allEntries;
	}	
	
	
	private List<PasswordEntry> getElementSubTree(Element parent)
	{
		List<PasswordEntry> allChildEntries = new ArrayList<PasswordEntry>();
		
		
		Iterator<Element> topElements = parent.getChildren().iterator();
		
		while(topElements.hasNext())
		{
			Element currentElement = topElements.next();
			
			PasswordEntry entry;
			try {
				entry = getEntry(currentElement.getAttributeValue("id"));
				if(entry != null)
				{
					allChildEntries.add(entry);
				}
				
				String elementType = currentElement.getName();
				if(elementType.equals("group"))
				{
					
					List<PasswordEntry> childs = getElementSubTree(currentElement);
					if(childs != null && childs.size() > 0)
					{
						allChildEntries.addAll(childs);
					}
				}
			} 
			catch (EntryDoesNotExistException e) 
			{
				logger.fatal(e.getMessage());
			}			
		}
		
		return allChildEntries;	
	}
}
