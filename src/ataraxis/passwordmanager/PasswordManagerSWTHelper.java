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

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;



public class PasswordManagerSWTHelper 
{
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(PasswordManagerSWTHelper.class);

	
	private static final String USER_DIR = System.getProperty("user.dir");
	private static final String APPL_DIR = USER_DIR + "/application_data";
	private static final String ICON_DIR = APPL_DIR + "/icons";

	final static Display display = Display.getDefault();
	final Image XMLUser = new Image(display, ICON_DIR + "/XML_User.png");
	final Image XMLDir = new Image(display, ICON_DIR + "/XML_Folder.png");

	private ArrayList<String> expandetTrees = new ArrayList<String>();

	
	public void createTree(Tree swtTree, List<PasswordEntry> entryList)
	{
		// clean up tree
		swtTree.removeAll();
	
		Hashtable<String, TreeItem> groupItems = new Hashtable<String, TreeItem>();
		
		Iterator<PasswordEntry> listIterator = entryList.iterator();
		
		while(listIterator.hasNext())
		{
			
			PasswordEntry current = listIterator.next();
			
			if(current.getType().equals("group"))
			{
				TreeItem elementItem = new TreeItem(swtTree, SWT.NONE);
				elementItem.setImage(XMLDir);
				elementItem.setText(current.getId());
				groupItems.put(current.getId(),elementItem);
			}
			else if(current.getType().equals("account"))
			{
				AccountEntry currentAccount = (AccountEntry) current;
				
				TreeItem elementItem = null; 
				PasswordEntry parent = currentAccount.getParentEntry();
				
				if( parent != null)
				{
					if(groupItems.containsKey(parent.getId()))
					{
						elementItem = new TreeItem(groupItems.get(parent.getId()),SWT.NONE);
					}
					else
					{
						elementItem = new TreeItem(swtTree,SWT.NONE);
						logger.warn("Parent ("+ parent.getId() +") not found for Element ("+ current.getId() +")");
					}
				}
				else
				{
					elementItem = new TreeItem(swtTree,SWT.NONE);
				}
				
				elementItem.setImage(XMLUser);
				elementItem.setText(current.getId());
			}
		}
		
		
		// Expand Trees at End. Otherwise expand will not work.
		if(expandetTrees.size() > 0)
		{
			for(String expand : expandetTrees)
			{
				TreeItem itm =  groupItems.get(expand);
				if(itm != null)
				{
					itm.setExpanded(true);
				}
			}
		}

		
	}
	
	/** 
	 * Method to trace which Elements of the SWT-Tree are expanded
	 * 
	 * @param ElementID the ID of the Element (== the Text of the TreeItem)
	 */
	public void expandTree(String ElementID)
	{
		expandetTrees.add(ElementID);
		logger.debug("market TreeItem " + ElementID + " as expanded");
	}
	
	
	/** 
	 * Method to trace which Elements of the SWT-Tree are no longer expanded
	 * 
	 * @param ElementID the ID of the Element (== the Text of the TreeItem)
	 */
	public void collapseTree(String ElementID)
	{
		if(expandetTrees.contains(ElementID)){
			expandetTrees.remove(ElementID);
			logger.debug("market TreeItem " + ElementID + " as collapsed");
		}
		else
		{
			logger.debug("market TreeItem " + ElementID + " not found for collapse");
		}
	}

}
