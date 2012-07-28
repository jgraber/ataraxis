/* ----------------------------------------------------------------------------
 * Copyright 2006 - 2010 Johnny Graber & Andreas Muedespacher
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


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;

import ch.ethz.origo.ataraxis.crypt.AtaraxisCrypter;

/**
 * PasswordManager is needed to for the Account and Password management in 
 * AtaraxiS. It controll the access to the encrypted password-File, create, edit,
 * or delete the Entries. 
 * 
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 *
 */
public class PasswordManager 
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(PasswordManager.class);

	private PasswordStore s_passwordStore;


	/**
	 * Constructor for PasswordManager
	 * 
	 * @param ac the AtaraxisCrypter open and running
	 * @param PathToAccountFile Path to the Account File (encrypted)
	 * @throws FileNotFoundException if file does not Exist
	 * @throws JDOMException by problems with jdom
	 * @throws IOException by IO problems
	 * @throws StorageException 
	 */
	public PasswordManager(AtaraxisCrypter ac, String PathToAccountFile) 
	throws FileNotFoundException, IOException, StorageException
	{
		this(ac, 
				new XMLHandler(
						new AtaraxisDiskAccessAdapter(ac)
						,new File(PathToAccountFile)));
	}


	/**
	 * Constructor for PasswordManager
	 * 
	 * @param ac the AtaraxisCrypter open and running
	 * @param PathToAccountFile Path to the Account File (encrypted)
	 * @throws FileNotFoundException if file does not Exist
	 * @throws JDOMException by problems with jdom
	 * @throws IOException by IO problems
	 */
	public PasswordManager(AtaraxisCrypter ac, PasswordStore passwordStore) 
	throws FileNotFoundException, IOException
	{
		logger.debug("PasswordManager(AtaraxisCrypter, String) - start");

		// set fields
		s_passwordStore = passwordStore;

		logger.debug("PasswordManager(AtaraxisCrypter, String) - created");
	}


	/**
	 * Save the File encrypted to the disk.
	 *
	 * @throws StorageException 
	 */
	public void savePasswords() throws StorageException
	{
		s_passwordStore.savePasswords();
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
		s_passwordStore.deleteEntry(entry);
	}


	/**
	 * Returns true if the Element exist, false otherwise
	 * 
	 * @param ElementID the ID of the Element
	 * @return true if exist, false otherwise
	 */
	public boolean existID(String EntryID)
	{
		return s_passwordStore.existID(EntryID);
	}

	
	/**
	 * Check if ElementID is a Element of type group
	 * 
	 * @param ElementID the ID of the Element
	 * @return true if it is a group, false otherwise
	 */
	public boolean isGroupElement(String ElementID)
	{
		return s_passwordStore.isGroupElement(ElementID);
	}

	
	/**
	 * Return a List of all group entries
	 * @return the GroupList
	 */
	public List<GroupEntry> getGroupEntryList()
	{
		return s_passwordStore.getGroupEntryList();
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
		return s_passwordStore.getGroupSlot(ElementID);
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
		return s_passwordStore.getGroupSlotElement(ElementID);
	}
	

	/**
	 * Check if the ElementId has Child-Elements.
	 * 
	 * @param ElementID
	 * @return true if Childs exist, false otherwise
	 */
	public boolean hasChilds(String ElementID)
	{
		return s_passwordStore.hasChilds(ElementID);
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
		s_passwordStore.addEntry(entry);
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
		return s_passwordStore.getEntry(id);
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
		return s_passwordStore.getGroupEntry(id);
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
		return s_passwordStore.getAccountEntry(id);
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
	public void renameElementId(String currentName, String newName) throws EntryDoesNotExistException, EntryAlreadyExistException, StorageException 
	{
		s_passwordStore.renameElementId(currentName, newName);
	}
	

	/**
	 * Get all Entries of the PasswordManager
	 * 
	 * @return
	 */
	public List<PasswordEntry> getAllEntries() 
	{		
		return s_passwordStore.getAllEntries();
	}

	
}
