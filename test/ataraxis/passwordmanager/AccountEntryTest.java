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
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import ataraxis.passwordmanager.AccountEntry;
import ataraxis.passwordmanager.GroupEntry;

public class AccountEntryTest 
{

	@Test
	public void hashCode_AllFieldsSet() throws Exception
	{
		GroupEntry group = new GroupEntry("group");
		AccountEntry first = getFullAccountEntry();
		first.setParentEntry(group);
		
		AccountEntry second = getFullAccountEntry();
		second.setParentEntry(group);
		
		assertEquals(first.hashCode(),second.hashCode());
		assertTrue(first.equals(second));
	}
	@Test
	public void hashCode_SameCodeForSameInput() throws Exception
	{
		AccountEntry first = new AccountEntry("myName");
		AccountEntry second = new AccountEntry("myName");
		AccountEntry third = new AccountEntry("myNameIsOther");
		
		assertEquals(first.hashCode(),second.hashCode());
		assertTrue(first.hashCode() != third.hashCode());
	}
	
	@Test
	public void hashCode_SameCodeForNullInput() throws Exception
	{
		AccountEntry first = new AccountEntry(null);
		AccountEntry second = new AccountEntry(null);
		AccountEntry third = new AccountEntry("myNameIsOther");
		
		assertEquals(first.hashCode(),second.hashCode());
		assertFalse(first.hashCode() == third.hashCode());
	}
	
	@Test
	public void equals_SameObject() throws Exception
	{
		AccountEntry first = new AccountEntry(null);
		assertTrue(first.equals(first));
	}
	
	@Test
	public void equals_NullObject() throws Exception
	{
		AccountEntry first = new AccountEntry(null);
		assertFalse(first.equals(null));
	}
	
	@Test
	public void equals_differentObject() throws Exception
	{
		AccountEntry first = new AccountEntry("a");
		AccountEntry second = new AccountEntry("b");
		assertFalse(first.equals(second));
	}
	
	@Test
	public void equals_otherNullName() throws Exception
	{
		AccountEntry first = new AccountEntry("a");
		AccountEntry second = new AccountEntry(null);
		assertFalse(first.equals(second));
	}
	
	@Test
	public void equals_SameID() throws Exception
	{
		AccountEntry first = new AccountEntry("a");
		AccountEntry second = new AccountEntry("a");
		assertTrue(first.equals(second));
	}
	
	@Test
	public void equals_otherType() throws Exception
	{
		AccountEntry first = new AccountEntry("a");
		GroupEntry second = new GroupEntry("a");
		assertFalse(first.equals(second));
	}
	
	@Test
	public void equals_ownIdNullOtherA() throws Exception
	{
		AccountEntry first = new AccountEntry(null);
		AccountEntry second = new AccountEntry("a");
		assertFalse(first.equals(second));
	}

	@Test
	public void equals_otherFieldsNullOwnNot() throws Exception
	{

		AccountEntry first = getFullAccountEntry();
		AccountEntry second = new AccountEntry(null);
		assertFalse(first.equals(second));
	}
	
	@Test
	public void equals_ownFieldsNullOtherNot() throws Exception
	{
		AccountEntry first = new AccountEntry(null);
		AccountEntry second = getFullAccountEntry();
		assertFalse(first.equals(second));
	}
	
	@Test
	public void equals_ownCommentFieldsNullOtherNot() throws Exception
	{
		AccountEntry first = getFullAccountEntry();
		first.setComment(null);
		AccountEntry second = getFullAccountEntry();
		
		assertFalse(first.equals(second));
		assertFalse(first.hashCode() == second.hashCode());
	}
	
	@Test
	public void equals_ownLinkFieldsNullOtherNot() throws Exception
	{
		AccountEntry first = getFullAccountEntry();
		first.setLink(null);
		AccountEntry second = getFullAccountEntry();

		assertFalse(first.equals(second));
		assertFalse(first.hashCode() == second.hashCode());
	}
	
	@Test
	public void equals_LinksDiffer() throws Exception
	{
		AccountEntry first = getFullAccountEntry();
		first.setLink("otherLink");
		AccountEntry second = getFullAccountEntry();
		
		assertFalse(first.equals(second));
	}
	
	@Test
	public void equals_NameDiffer() throws Exception
	{
		AccountEntry first = getFullAccountEntry();
		first.setName("otherName");
		AccountEntry second = getFullAccountEntry();
	
		assertFalse(first.equals(second));
		assertFalse(first.hashCode() == second.hashCode());
	}
	
	@Test
	public void equals_ownNameFieldsNullOtherNot() throws Exception
	{
		AccountEntry first = getFullAccountEntry();
		first.setName(null);
		AccountEntry second = getFullAccountEntry();

		assertFalse(first.equals(second));
		assertFalse(first.hashCode() == second.hashCode());
	}
	
	@Test
	public void equals_PasswordDiffer() throws Exception
	{
		AccountEntry first = getFullAccountEntry();
		first.setPassword("otherPW");
		AccountEntry second = getFullAccountEntry();

		assertFalse(first.equals(second));
	}
	
	@Test
	public void equals_ownPasswordFieldsNullOtherNot() throws Exception
	{
		AccountEntry first = getFullAccountEntry();
		first.setPassword(null);
		AccountEntry second = getFullAccountEntry();
		
		assertFalse(first.equals(second));
		assertFalse(first.hashCode() == second.hashCode());
	}
	
	@Test
	public void toString_SameAsId() throws Exception
	{
		AccountEntry first = getFullAccountEntry();
		first.setId("AAA");
		
		assertEquals(first.getId(), first.toString());
	}
	
	@Test
	public void Comparable_toNullIsSmaller()
	{
		AccountEntry entry = new AccountEntry("A");		
		assertEquals(-1, entry.compareTo(null));
	}
	
	@Test
	public void Comparable_AisSmallerThanZ()
	{
		AccountEntry entryA = new AccountEntry("A");
		AccountEntry entryZ = new AccountEntry("Z");
		
		assertTrue(entryA.compareTo(entryZ) < 0);
	}
	
	@Test
	public void Comparable_mustBeSortable()
	{
		List<AccountEntry> sortedList = new ArrayList<AccountEntry>();
		sortedList.add(new AccountEntry("Z"));
		sortedList.add(new AccountEntry("A"));
		
		Collections.sort(sortedList);

		assertEquals("A", sortedList.get(0).getId());
	}
	
	private AccountEntry getFullAccountEntry()
	{
		AccountEntry second = new AccountEntry("myName");
		second.setComment("AA");
		second.setLink("hh");
		second.setName("ggg");
		second.setPassword("$$$$");
		
		return second;
	}	
}
