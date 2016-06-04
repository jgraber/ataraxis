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

/**
 * Interface for all entries in the password manager
 *
 * @author J. Graber
 * @version 1.0
 */
public interface PasswordEntry extends Comparable<PasswordEntry>
{

	/**
	 * Every Entry has a Id to identify the Object.
	 *
	 * @return the id of the entry
	 */
	String getId();
	
	/**
	 * Type can be 'group' or 'account'.
	 *
	 * @return type of Entry
	 */
	String getType();
	
	/**
	 * Get parent Entry
	 *
	 * @return Entry
	 */
	PasswordEntry getParentEntry();
}
