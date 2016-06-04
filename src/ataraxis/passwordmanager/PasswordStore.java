/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2010 Johnny Graber & Andreas Muedespacher
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

package ataraxis.passwordmanager;

import java.util.List;

/**
 * Interface to the PasswordStore (like XMLHandler).
 * 
 * @author J. Graber
 * @version 1.0
 *
 */
public interface PasswordStore 
{

	/**
	 * Load the Passwords.
	 *
	 * @throws StorageException if an error occurs
	 */
	void loadPasswords() throws StorageException;
	
	
	/**
	 * Save the File encrypted to the disk.
	 *
	 * @throws StorageException 
	 */
	void savePasswords() throws StorageException;
	
	
	/**
	 * Add an PasswordEntry to the PasswordManager.
	 *
	 * @param entry the new PasswordEntry
	 * @throws EntryDoesNotExistException if Parent does not exist
	 * @throws EntryAlreadyExistException 
	 * @throws StorageException if save fails
	 */
	void addEntry(PasswordEntry entry) throws EntryDoesNotExistException, EntryAlreadyExistException, StorageException;
	
	
	/**
	 * Delete the entry.
	 * 
	 * @param entry
	 * @throws EntryDoesNotExistException if entry does not exist
	 * @throws StorageException if changed accounts cannot be saved
	 */
	void deleteEntry(PasswordEntry entry) throws EntryDoesNotExistException, StorageException;

	
	/**
	 * Get a PasswordEntry with the given id.
	 *
	 * @param id
	 * @return requested PasswordEntry
	 * @throws EntryDoesNotExistException
	 */
	PasswordEntry getEntry(String id) throws EntryDoesNotExistException;
	
	
	/**
	 * Get a GroupEntry with the given id.
	 *
	 * @param id
	 * @return the requested GroupEntry
	 * @throws EntryDoesNotExistException
	 */
	GroupEntry getGroupEntry(String id) throws EntryDoesNotExistException;
	
	
	/**
	 * Get a AccountEntry with the given id.
	 * 
	 * @param id
	 * @return the requested AccountEntry
	 * @throws EntryDoesNotExistException
	 */
	AccountEntry getAccountEntry(String id) throws EntryDoesNotExistException;
	
	
	/**
	 * Returns true if the Element exist, false otherwise
	 * 
	 * @param ElementID the ID of the Element
	 * @return true if exist, false otherwise
	 */
	boolean existID(String EntryID);
	
	
	/**
	 * Check if ElementID is a Element of type group
	 * 
	 * @param ElementID the ID of the Element
	 * @return true if it is a group, false otherwise
	 */
	boolean isGroupElement(String ElementID);
	 
	
	/**
	 * Return a List of all entries (account and groups).
	 *
	 * @return
	 */
	List<PasswordEntry> getPasswordEntryList();
	
	
	/**
	 * Return a List of all account entries.
	 *
	 * @return
	 */
	List<AccountEntry> getAccountEntryList();
	
	
	/**
	 * Return a List of all group entries.
	 *
	 * @return the GroupList
	 */
	List<GroupEntry> getGroupEntryList();
	
	
	/**
	 * Check if the ElementId has Child-Elements.
	 * 
	 * @param ElementID
	 * @return true if Childs exist, false otherwise
	 */
	boolean hasChilds(String ElementID);
	
	
	/**
	 * Get the Slot in which belongs to the ElementID.
	 * If the ElementID belongs to an account-Element, then it 
	 * returns the parent-group or null for the Root-Item. 
	 * If it is a group-Elment, it retrun the group-Element.
	 * 
	 * @param ElementID the ID of the Element
	 * @return the matching slot
	 */
	String getGroupSlot(String ElementID);
	
	
	/**
	 * Return the Slot Element. Same as getGroupSlot(), but isted of a String of 
	 * the ElementID it returns the Element itself.
	 *
	 * @param ElementID
	 * @return the slot Element
	 */
	GroupEntry getGroupSlotElement(String ElementID);
	
	
	/**
	 * Rename a Entry.
	 *
	 * @param currentName Name of an existing entry
	 * @param newName new Name
	 * @throws EntryDoesNotExistException if currentName does not exist
	 * @throws EntryAlreadyExistException if new Name allready exist
	 * @throws StorageException when save fails
	 */
	void renameElementId(String currentName, String newName) throws EntryDoesNotExistException, EntryAlreadyExistException, StorageException; 


	/**
	 * Get all Entries of the PasswordStore
	 * 
	 * @return
	 */
	List<PasswordEntry> getAllEntries();
	
}
