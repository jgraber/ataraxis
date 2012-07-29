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

/**
 * GroupEntry is a value Object for storing Account-Groups.
 * 
 * @author J. Graber
 * @version 1.0
 *
 */
public class GroupEntry implements PasswordEntry 
{
	private String id;

	public GroupEntry(String groupId) 
	{
		setId(groupId);
	}

	public String getId() 
	{
		return id;
	}
	private void setId(String id) 
	{
		this.id = id;
	}

	public String getType() 
	{
		return "group";
	}

	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) 
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GroupEntry other = (GroupEntry) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public PasswordEntry getParentEntry() 
	{
		return null;
	}

	@Override
	public int compareTo(PasswordEntry o) 
	{
		if(o != null)
		{
			if(!o.getType().equals("group"))
			{
				return -1;
			}
			
			return id.toLowerCase().compareTo(o.getId().toLowerCase());
		}
		return -1;
	}

}
