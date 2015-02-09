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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import ataraxis.passwordmanager.AccountEntry;
import ataraxis.passwordmanager.GroupEntry;

public class GroupEntryTest 
{


	@Test
	public void hashCode_SameCodeForSameInput() throws Exception
	{
		GroupEntry first = new GroupEntry("myName");
		GroupEntry second = new GroupEntry("myName");
		GroupEntry third = new GroupEntry("myNameIsOther");
		
		assertEquals(first.hashCode(),second.hashCode());
		assertFalse(first.hashCode() == third.hashCode());
	}
	
	@Test
	public void hashCode_SameCodeForNullInput() throws Exception
	{
		GroupEntry first = new GroupEntry(null);
		GroupEntry second = new GroupEntry(null);
		GroupEntry third = new GroupEntry("myNameIsOther");
		
		assertEquals(first.hashCode(),second.hashCode());
		assertFalse(first.hashCode() == third.hashCode());
	}
	
	@Test
	public void equals_SameObject() throws Exception
	{
		GroupEntry first = new GroupEntry(null);
		assertTrue(first.equals(first));
	}
	
	@Test
	public void equals_NullObject() throws Exception
	{
		GroupEntry first = new GroupEntry(null);
		assertFalse(first.equals(null));
	}
	
	@Test
	public void equals_differentObject() throws Exception
	{
		GroupEntry first = new GroupEntry("a");
		GroupEntry second = new GroupEntry("b");
		assertFalse(first.equals(second));
	}
	
	@Test
	public void equals_otherNullName() throws Exception
	{
		GroupEntry first = new GroupEntry("a");
		GroupEntry second = new GroupEntry(null);
		assertFalse(first.equals(second));
	}
	
	@Test
	public void equals_SameID() throws Exception
	{
		GroupEntry first = new GroupEntry("a");
		GroupEntry second = new GroupEntry("a");
		assertTrue(first.equals(second));
	}
	
	@Test
	public void equals_otherType() throws Exception
	{
		GroupEntry first = new GroupEntry("a");
		AccountEntry second = new AccountEntry("a");
		assertFalse(first.equals(second));
	}
	
	@Test
	public void equals_ownIdNullOtherA() throws Exception
	{
		GroupEntry first = new GroupEntry(null);
		GroupEntry second = new GroupEntry("a");
		assertFalse(first.equals(second));
	}
	
	@Test
	public void Comparable_toNullIsSmaller()
	{
		GroupEntry entry = new GroupEntry("A");		
		assertEquals(-1, entry.compareTo(null));
	}
	
	@Test
	public void Comparable_AisSmallerThanZ()
	{
		GroupEntry entryA = new GroupEntry("A");
		GroupEntry entryZ = new GroupEntry("Z");
		
		assertTrue(entryA.compareTo(entryZ) < 0);
	}
	
	@Test
	public void Comparable_mustBeSortable()
	{
		List<GroupEntry> sortedList = new ArrayList<GroupEntry>();
		sortedList.add(new GroupEntry("Z"));
		sortedList.add(new GroupEntry("A"));
		
		Collections.sort(sortedList);

		assertEquals("A", sortedList.get(0).getId());
	}
}
