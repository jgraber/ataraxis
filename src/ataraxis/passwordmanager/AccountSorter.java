/* ----------------------------------------------------------------------------
 * Copyright 2006 - 2010 Johnny Graber & Andreas Muedespacher
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

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import java.util.Comparator;

import org.jdom2.Element;


/**
 * AccountSorter is needed to sort the XML-Elements for the PasswordManager
 * Sort-Order: 
 * <ul>
 * <li>Groups before Accounts</li>
 * <li>Group to Group: sort by Alphabet</li>
 * <li>Account to Account: sort by Alphabet</li>
 *</ul>
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 *
 */
public class AccountSorter implements Comparator<Element> {
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AccountSorter.class);

	/**
	 * Compare first object with second.
	 * The result my be:
	 * <ul>
	 * <li>-1 if first is group, second is not a group</li>
	 * <li>1 if first is not a group but second is</li>
	 * <li>any number of the String.compareTo()-Method if first and 
	 *   second are from the same type</li>
	 * </ul>
	 * 
	 * @param first the first object
	 * @param second the second object
	 * @return the result of the comparison
	 */
	public int compare(Element first, Element second) 
	{
		LOGGER.debug("compare(Object, Object) - start");

		int result = 0;
		Element elm0 = (Element) first;
		Element elm1 = (Element) second;
		String elm0Type = elm0.getName();
		String elm1Type = elm1.getName();
		LOGGER.debug("element 0 is: " + elm0Type);
		LOGGER.debug("element 1 is: " + elm1Type);

		if(elm0Type.equals("group") && !elm1Type.equals("group"))
		{
			result = -1;
		}
		else if(!elm0Type.equals("group") && elm1Type.equals("group"))
		{
			result = 1;
		}
		else
		{
			result = (elm0.getAttribute("id").toString().toLowerCase())
			.compareTo(elm1.getAttribute("id").toString().toLowerCase());
		}

		LOGGER.debug("result is: " + result);
		LOGGER.debug("compare(Object, Object) - end");
		return result;
	}
}
