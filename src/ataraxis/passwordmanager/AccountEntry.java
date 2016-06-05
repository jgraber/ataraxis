/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2016 Johnny Graber & Andreas Muedespacher
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
 * AccountEntry is a value Object for storing Passwords.
 * 
 * @author J. Graber
 * @version 1.0
 *
 */
public class AccountEntry implements PasswordEntry 
{

	private String id = "";
	private String name = "";
	private String password = "";
	private String link = "";
	private String comment = "";
	private GroupEntry parentGroup;
	

	public AccountEntry(String id)
	{
		setId(id);
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}

	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public GroupEntry getParentEntry() {
		return parentGroup;
	}
	public void setParentEntry(GroupEntry parentGroup) {
		this.parentGroup = parentGroup;
	}

	public String getType() {
		return "account";
	}
	
	@Override
	public int hashCode() 
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((comment == null) ? 0 : comment.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((link == null) ? 0 : link.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((password == null) ? 0 : password.hashCode());
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
		AccountEntry other = (AccountEntry) obj;
		if (comment == null) {
			if (other.comment != null)
				return false;
		} else if (!comment.equals(other.comment))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (link == null) {
			if (other.link != null)
				return false;
		} else if (!link.equals(other.link))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		return true;
	}

	@Override
	public String toString() 
	{
		return id;
	}

	@Override
	public int compareTo(PasswordEntry o) 
	{
		if(o != null)
		{
			if(!o.getType().equals("account"))
			{
				return 1;
			}
			
			return id.toLowerCase().compareTo(o.getId().toLowerCase());
		}
		return -1;
	}
}
