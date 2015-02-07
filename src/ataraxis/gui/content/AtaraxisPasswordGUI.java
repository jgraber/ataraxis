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

package ataraxis.gui.content;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ataraxis.crypt.AtaraxisCrypter;
import ataraxis.crypt.CryptoMethodError;
import ataraxis.gui.AtaraxisStarter;
import ataraxis.gui.GUIHelper;
import ataraxis.gui.utils.PWGeneratorGUI;
import ataraxis.gui.utils.TextInputDialog;
import ataraxis.passwordmanager.AccountEntry;
import ataraxis.passwordmanager.EntryAlreadyExistException;
import ataraxis.passwordmanager.EntryDoesNotExistException;
import ataraxis.passwordmanager.GroupEntry;
import ataraxis.passwordmanager.PasswordManager;
import ataraxis.passwordmanager.PasswordManagerSWTHelper;
import ataraxis.passwordmanager.StorageException;


/**
 * AtaraxisPasswordGUI creates the password composite.
 * 
 * @author J. Graber & A. Muedespacher
 * @version 1.2
 */
public class AtaraxisPasswordGUI {

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisPasswordGUI.class);

	// used AtaraxiS classes
	private PasswordManager s_pwManager;
	private ResourceBundle s_translations;
	private PasswordManagerSWTHelper s_pwMSwtHelper;
	private GUIHelper guiHelper;
	private AtaraxisCrypter s_ac = null;	
	
	// general GUI elements
	private Display s_display;
	private char s_EchoChar;
	private StackLayout s_pwCopyStackLayout;
	
	// Account-Entry form
	private Combo comboGroup;
	private Text textAccountID;
	private Text textUsername;
	private Text textPassword;
	private Text textLink;
	private Text textComment;
	private Button copyUsernameButton;
	private Composite m_pwCopyStack;
	private Button copyPasswordButton;
	private Button generatePassword;
	private Button openLinkButton;

	// Buttons left side down
	private Button createGroup;
	private Button deleteGroup;
	private Button createUser;
	private Button deleteUser;

	// Buttons right side down
	private Button editButton;
	private Button editcancelButton;
	private Button saveButton;

	// vars to control program flow
	protected boolean s_changedByEdit;
	protected boolean s_allowAccountchange = true;
	protected AccountEntry s_pLastSelectedAccount;
	private AccountEntry s_editingElement = null;
	
	// Images
	private Image ICON_PWGen;
	private Image ICON_ADDACCOUNT;
	private Image ICON_DELACCOUNT;
	private Image ICON_DELGROUP;
	private Image ICON_ADDGROUP;
	private Image ICON_COPY;
	private Image ICON_WEB;

	// Paths and files
	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String USER_DATA_DIR = APPL_DIR + "/user_data";
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	private static final String ICON_DIR = APPL_DATA_DIR + "/icons";
	private static final String EMPTY_XML = APPL_DATA_DIR + "/user_data_template/template.xml";
	private String s_user;
	private String s_accountFile = "";
	
	/**
	 * AtaraxisPasswordGUI manages all to create a composite
	 * for the password manager GUI.
	 * 
	 * @param pwManager who manage the passwords
	 * @param ac for the encryption
	 * @param translations with the translations
	 */
	public AtaraxisPasswordGUI(PasswordManager pwManager, AtaraxisCrypter ac, ResourceBundle translations) 
	{
		s_pwManager = pwManager;
		s_translations = translations;
		s_ac = ac;
		
		String pathOfKeyStore = s_ac.getKeyStorePath();
		File ksFile = new File(pathOfKeyStore);
		File directoryOfKs = ksFile.getParentFile();
		s_user = directoryOfKs.getName();
		s_accountFile = USER_DATA_DIR + "/" + s_user + "/accounts.data";

	}
	
	/**
	 * Crate the Composite for the PW-Manager GUI.
	 * 
	 * @param s_shell shell of outside SWT application
	 * @param display display of the outside SWT application
	 * @param parentComposite where the PasswordContent should be placed
	 * @return the composite
	 */
	public Composite createPasswordsContent(final Shell s_shell, Display display, Composite parentComposite)
	{
		s_display = display;
		guiHelper = new GUIHelper(s_shell);
		setIcons();
		
		final Clipboard cb = new Clipboard(s_display);
		Composite s_compositePasswords = new Composite(parentComposite, SWT.NONE);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		s_compositePasswords.setLayout(gridLayout);

		final Tree tree = new Tree(s_compositePasswords, SWT.BORDER);
		final GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
		gridData.widthHint = 159;
		tree.setLayoutData(gridData);

		// Create Tree Popup Menu
		Menu treeMenu = new Menu(s_shell, SWT.POP_UP);
		tree.setMenu(treeMenu);

		MenuItem item = new MenuItem(treeMenu, SWT.PUSH);
		item.setText(s_translations.getString("PWM.RENAME_CONTEXT_MENU"));
		item.addListener (SWT.Selection, new Listener(){
			public void handleEvent (Event event) {
				final TreeItem[] t = tree.getSelection();
				String idToRename = t[0].getText();

				Properties renameProps = new Properties();
				
				renameProps.put("PWM.TID_Title", s_translations.getString("PWM.RENAME_OK_TITLE"));
				renameProps.put("PWM.TID_OkButton", s_translations.getString("PWM.RENAME_CONTEXT_MENU"));
				renameProps.put("PWM.TID_labelText", s_translations.getString("PWM.RENAME_NEW_NAME"));
				renameProps.put("CANCEL", s_translations.getString("CANCEL"));
				renameProps.put("PWM.TID_labelText", s_translations.getString("PWM.TID_labelText"));
				renameProps.put("PWM.TID_warnText", s_translations.getString("PWM.TID_warnText"));
				renameProps.put("PWM.TID_warnMessage", s_translations.getString("PWM.TID_warnMessage"));
				
				ByteArrayOutputStream outRenamePropsStream = new ByteArrayOutputStream();
				try {
					renameProps.store(outRenamePropsStream, "Properties for rename dialog");
					ResourceBundle renameBundle = new PropertyResourceBundle(
							(InputStream)new ByteArrayInputStream(outRenamePropsStream.toByteArray()));
					final TextInputDialog tid = new TextInputDialog(s_shell, renameBundle);
					tid.setWidth(450);
					tid.setDisplayText(idToRename);
					String newName = tid.open();
					if(newName != null)
					{
						newName = newName.trim();
						if(!newName.equals("")) 
						{
							try 
							{
								s_pwManager.renameElementId(idToRename, newName);
								setGroupListToComboGroup();
								s_pwManager.savePasswords();
								s_pwMSwtHelper.createTree(tree,s_pwManager.getAllEntries());
								
								guiHelper.displayInfoMessage(s_translations.getString("PWM.RENAME_OK_TITLE"), s_translations.getString("PWM.RENAME_OK_MESSAGE") );	
							} 
							catch (EntryDoesNotExistException e) 
							{
								LOGGER.warn(e.getMessage());
								guiHelper.displayErrorMessage(s_translations.getString("PWM.RENAME_ERROR_TITLE"), s_translations.getString("PWM.RENAME_ERROR_ENTRY_NOT_FOUND"));
							} 
							catch (EntryAlreadyExistException e) 
							{
								LOGGER.warn(e.getMessage());
								guiHelper.displayErrorMessage(s_translations.getString("PWM.RENAME_ERROR_TITLE"), s_translations.getString("PWM.RENAME_ERROR_ENTRY_EXIST_ALREADY"));
							} 
							catch (StorageException e) 
							{
								LOGGER.warn(e.getMessage());
								guiHelper.displayErrorMessage(s_translations.getString("PWM.RENAME_ERROR_TITLE"), s_translations.getString("PWM.RENAME_ERROR_SAVE"));
							}
						}
					}
				} catch (IOException e1) {
					LOGGER.warn(e1.getMessage());
					guiHelper.displayErrorMessage(s_translations.getString("PWM.RENAME_ERROR_TITLE"), e1.getMessage());
				}
			}
		});


		final Composite composite = new Composite(s_compositePasswords, SWT.NONE);
		final GridLayout gridLayout_1 = new GridLayout();
		gridLayout_1.numColumns = 3;
		composite.setLayout(gridLayout_1);
		final GridData gridData_1 = new GridData(SWT.LEFT, SWT.FILL, false, false);
		gridData_1.widthHint = 295;
		composite.setLayoutData(gridData_1);

		Label passwordManagerTitle = new Label(composite, SWT.NONE);
		final GridData gridData_4 = new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1);
		gridData_4.heightHint = 29;
		gridData_4.widthHint = 20;
		passwordManagerTitle.setLayoutData(gridData_4);
		passwordManagerTitle.setText(s_translations.getString("PWM.PASSWORD_MANAGER_TITLE"));
		passwordManagerTitle.setFont(guiHelper.getMiddleFont(s_display));

		Label groupLabel = new Label(composite, SWT.NONE);
		groupLabel.setText(s_translations.getString("PWM.GROUP_LABEL") + ":");

		comboGroup = new Combo(composite, SWT.NONE);
		comboGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		new Label(composite, SWT.NONE);

		Label accountIdLabel = new Label(composite, SWT.NONE);
		accountIdLabel.setText(s_translations.getString("PWM.ACCOUNT_ID") + ":");

		textAccountID = new Text(composite, SWT.BORDER);
		textAccountID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		new Label(composite, SWT.NONE);

		Label usernameLabel = new Label(composite, SWT.NONE);
		usernameLabel.setText(s_translations.getString("PWM.USER_NAME") + ":");

		textUsername = new Text(composite, SWT.BORDER);
		textUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		final Composite usernameCopy = new Composite(composite, SWT.NONE);
		usernameCopy.setLayout(new FillLayout());
		usernameCopy.setLayoutData(new GridData(25, 22));

		copyUsernameButton = new Button(usernameCopy, SWT.FLAT);
		copyUsernameButton.setImage(ICON_COPY);
		copyUsernameButton.setToolTipText(s_translations.getString("PWM.COPY_USERNAME") );
		copyUsernameButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e) 
			{
				final String username = textUsername.getText();
				if (!username.equals(""))
				{
					final TextTransfer textTransfer = TextTransfer.getInstance();
					final Transfer[] types = new Transfer[] {textTransfer};
					cb.setContents(new Object[] {username}, types);	
				}		
			}
		});

		Label passwordLabel = new Label(composite, SWT.NONE);
		passwordLabel.setText(s_translations.getString("PWM.PASSWORD") + ":");

		textPassword = new Text(composite, SWT.BORDER);
		textPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		s_EchoChar = textPassword.getEchoChar();
		textPassword.setEchoChar('*');

		m_pwCopyStack = new Composite(composite, SWT.NONE);
		s_pwCopyStackLayout = new StackLayout();
		m_pwCopyStack.setLayout(s_pwCopyStackLayout);
		m_pwCopyStack.setLayoutData(new GridData(25, 22));

		copyPasswordButton = new Button(m_pwCopyStack, SWT.FLAT);
		copyPasswordButton.setImage(ICON_COPY);
		copyPasswordButton.setSize(18, 18);
		copyPasswordButton.setToolTipText(s_translations.getString("PWM.COPY_PASSWORD") );

		generatePassword = new Button(m_pwCopyStack, SWT.FLAT);
		generatePassword.setImage(ICON_PWGen);
		generatePassword.setToolTipText(s_translations.getString("PWM.GENERATE_PASSWORD") );
		generatePassword.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0) 
			{
				// open PWGeneratorGUI to generate passwords
				final PWGeneratorGUI window = new PWGeneratorGUI(s_shell, s_translations);
				final String newPW = window.open();
				if (!newPW.equals(""))
					textPassword.setText(newPW);
			}
		});

		copyPasswordButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e) 
			{
				final String password = textPassword.getText();

				if (!password.equals(""))
				{
					final TextTransfer textTransfer = TextTransfer.getInstance();
					final Transfer[] types = new Transfer[] {textTransfer};
					cb.setContents(new Object[] {password}, types);
				}
			}
		});

		s_pwCopyStackLayout.topControl = copyPasswordButton;

		Label linkLabel = new Label(composite, SWT.NONE);
		linkLabel.setLayoutData(new GridData());
		linkLabel.setText(s_translations.getString("PWM.LINK") + ":");

		textLink = new Text(composite, SWT.BORDER);
		textLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		openLinkButton = new Button(composite, SWT.FLAT);
		openLinkButton.setLayoutData(new GridData(25, 22));
		openLinkButton.setImage(ICON_WEB);
		openLinkButton.setToolTipText(s_translations.getString("PWM.OPEN_LINK") );
		openLinkButton.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e) 
			{
				final String link = textLink.getText();
				if (! link.equals(""))
				{

					final Program p = Program.findProgram ("html");
					if (p != null) {
						Program.launch(link);
					}
				}
			}
		});

		Label commentLabel = new Label(composite, SWT.NONE);
		commentLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		commentLabel.setText(s_translations.getString("PWM.COMMENT") + ":");

		textComment = new Text(composite, SWT.BORDER|SWT.WRAP);
		final GridData gridData_3 = new GridData(SWT.FILL, SWT.FILL, true, true);
		//gridData_3.heightHint = 210;
		textComment.setLayoutData(gridData_3);
		textComment.setEditable(false);
		new Label(composite, SWT.NONE);

		final Composite compositeTreeItemActions = new Composite(s_compositePasswords, SWT.NONE);
		compositeTreeItemActions.setLayout(new FillLayout());
		final GridData gridData_2 = new GridData(SWT.FILL, SWT.FILL, false, false);
		gridData_2.widthHint = 143;
		compositeTreeItemActions.setLayoutData(gridData_2);

		comboGroup.setEnabled(false);
		textAccountID.setEditable(false);
		textUsername.setEditable(false);
		textPassword.setEditable(false);
		textLink.setEditable(false);
		textComment.setEditable(false);

		createGroup = new Button(compositeTreeItemActions, SWT.FLAT);

		createGroup.setImage(ICON_ADDGROUP);
		createGroup.setToolTipText(s_translations.getString("PWM.CREATE_GROUP") );
		deleteGroup = new Button(compositeTreeItemActions, SWT.FLAT);
		deleteGroup.setImage(ICON_DELGROUP);
		deleteGroup.setToolTipText(s_translations.getString("PWM.DELETE_GROUP") );
		createUser = new Button(compositeTreeItemActions, SWT.FLAT);
		createUser.setImage(ICON_ADDACCOUNT);
		createUser.setToolTipText(s_translations.getString("PWM.CREATE_USER") );
		deleteUser = new Button(compositeTreeItemActions, SWT.FLAT);
		deleteUser.setImage(ICON_DELACCOUNT);
		deleteUser.setToolTipText(s_translations.getString("PWM.DELETE_USER") );
		final Composite composite_1 = new Composite(s_compositePasswords, SWT.NONE);
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 3;
		composite_1.setLayout(gridLayout_2);
		composite_1.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false));

		editButton = new Button(composite_1, SWT.FLAT);
		editButton.setText(s_translations.getString("PWM.EDIT") );

		editcancelButton = new Button(composite_1, SWT.FLAT);
		editcancelButton.setText(s_translations.getString("PWM.EDIT_CANCEL") );
		editcancelButton.setVisible(true);
		editcancelButton.setEnabled(false);

		saveButton = new Button(composite_1, SWT.FLAT);
		saveButton.setText(s_translations.getString("PWM.SAVE_USER") );
		saveButton.setVisible(true);
		saveButton.setEnabled(false);

		changePWManagerFields(false);
		editButton.setEnabled(false);


		// check if accountFile exist. If not, ask if it should be created
		final File accountFile = new File(s_accountFile);

		if (! accountFile.exists())
		{
			if(guiHelper.displayQuestionMessage(s_translations.getString("PWM.PWM_ERROR"), s_translations.getString("PWM.MISSING_FILE")))
			{
				try {
					s_ac.encryptFile(new File(EMPTY_XML), new File(s_accountFile));
				} 
				catch (FileNotFoundException e1) 
				{
					LOGGER.error(e1.getMessage());
				} 
				catch (CryptoMethodError e1) 
				{
					LOGGER.error(e1.getMessage());
				}
			}
		}

		boolean pwManagerOpenOK = true;
		LOGGER.debug("path of xml with account data: " + s_accountFile);

		try
		{
			s_pwManager = new PasswordManager(s_ac, s_accountFile);
			s_pwMSwtHelper = new PasswordManagerSWTHelper();
			s_pwMSwtHelper.createTree(tree,s_pwManager.getAllEntries());

			setGroupListToComboGroup();


			tree.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent e)
				{

					if(s_allowAccountchange)
					{
						final TreeItem[] t = tree.getSelection();

						// get selected Element
						if(t.length != 0)
						{

							//final Element askedElement = s_pwManager.getElement(t[0].getText());

							try 
							{
								AccountEntry askedEntry = s_pwManager.getAccountEntry(t[0].getText());
								s_pLastSelectedAccount = askedEntry;
								fillPWManagerFields(askedEntry);	
							} 
							catch (EntryDoesNotExistException e1) 
							{
								guiHelper.displayErrorMessage(s_translations.getString("PWM.ATTENTION"), s_translations.getString("PWM.MISSING_ENTRY"));
								s_pwMSwtHelper.createTree(tree,s_pwManager.getAllEntries());;
							}


						}
					}
					else
						guiHelper.displayWarningMessage(s_translations.getString("PWM.ATTENTION"), s_translations.getString("PWM.FIRST_END_USER_MODIFICATION") );
				}

			});

			tree.addTreeListener(new TreeListener()
			{
				// mark which group is expanded and which not
				public void treeExpanded(TreeEvent e)
				{
					final TreeItem ti = (TreeItem) e.item;
					s_pwMSwtHelper.expandTree(ti.getText());				
				}

				public void treeCollapsed(TreeEvent e) 
				{
					final TreeItem ti = (TreeItem) e.item;
					s_pwMSwtHelper.collapseTree(ti.getText());	
				}
			});


			editButton.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent arg0) 
				{
					final TreeItem[] t = tree.getSelection();

					if (t.length == 0){   // warn nothing is selected
						guiHelper.displayErrorMessage(s_translations.getString("ERROR"), s_translations.getString("PWM.SELECT_GROUP_BEFORE_DELETE"));
					}
					else 
					{
						final String selectedElement = t[0].getText();
						LOGGER.debug("Try to edit element '" + selectedElement+"'");
						if(s_pwManager.existID(selectedElement) && 
								! s_pwManager.isGroupElement(selectedElement) )
						{

							try 
							{
								s_editingElement = s_pwManager.getAccountEntry(selectedElement);
								s_changedByEdit = true;
								s_allowAccountchange = false;
								changePWManagerFields(true);
							} 
							catch (EntryDoesNotExistException e) 
							{
								guiHelper.displayErrorMessage(s_translations.getString("PWM.ATTENTION"), s_translations.getString("PWM.MISSING_ENTRY"));
								s_pwMSwtHelper.createTree(tree,s_pwManager.getAllEntries());;
							}
						}
						else
							guiHelper.displayInfoMessage(s_translations.getString("PWM.ATTENTION"), s_translations.getString("PWM.SELECT_ACCOUNT"));
					}
				}
			});

			editcancelButton.addSelectionListener(new SelectionAdapter()
			{
				public void widgetSelected(SelectionEvent arg0)
				{
					s_allowAccountchange = true;
					changePWManagerFields(false);
					fillPWManagerFields(s_pLastSelectedAccount);
				}
			});

			createGroup.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent arg0) 
				{
					final TextInputDialog tid = new TextInputDialog(s_shell, s_translations);
					String newGroup = tid.open();
					if(newGroup != null)
						newGroup = newGroup.trim();
					if( newGroup == null) 
					{
						// do nothing
					}
					else 
					{
						if (newGroup.equals("") )
						{
							// do nothing
						}
						else 
						{
							// text for new Group is set
							if(s_pwManager.existID(newGroup))
							{
								// warn user that ID allredy exist
								guiHelper.displayWarningMessage(s_translations.getString("PWM.ATTENTION"), s_translations.getString("PWM.AID_EXIST"));
							}
							else 
							{
								// create new Group
								boolean createWasOK = true;

								try 
								{
									GroupEntry newGroupEntry = new GroupEntry(newGroup);
									s_pwManager.addEntry(newGroupEntry);


									setGroupListToComboGroup();

									s_pwManager.savePasswords();
									s_pwMSwtHelper.createTree(tree,s_pwManager.getAllEntries());
								} 
								catch (EntryDoesNotExistException e) 
								{
									LOGGER.error("EntryDoesNotExistException", e);
									createWasOK = false;
								} 
								catch (EntryAlreadyExistException e) 
								{
									LOGGER.error("EntryAlreadyExistException", e);
									createWasOK = false;
								} catch (StorageException e) {
									LOGGER.error("StorageException", e);
									createWasOK = false;
								}
								if(!createWasOK)
									guiHelper.displayErrorMessage(s_translations.getString("ERROR"), s_translations.getString("PWM.GROUP_EXIST")
											+ "'" + newGroup + "'");
							}
						}
					}
				}
			});

			deleteGroup.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent arg0) 
				{
					final TreeItem[] t = tree.getSelection();

					// warn nothing is selected
					if (t.length == 0)
						guiHelper.displayErrorMessage(s_translations.getString("ERROR"), s_translations.getString("PWM.SELECT_GROUP_BEFORE_DELETE"));
					else 
					{
						final String selectedElement = t[0].getText();
						LOGGER.debug("Try to delete element '" + selectedElement+"'");
						if(s_pwManager.isGroupElement(selectedElement) &&
								! s_pwManager.hasChilds(selectedElement))
						{
							boolean deleteWasOK = true;
							// Group element has no childs
							try
							{

								if(guiHelper.displayQuestionMessage(s_translations.getString("PWM.ATTENTION"), s_translations.getString("PWM.REALY_DELETE_GROUP")
										+ " '" + selectedElement + "' ?"))
								{
									GroupEntry pwEntryToDelete = new GroupEntry(selectedElement);
									s_pwManager.deleteEntry(pwEntryToDelete);
									s_pwManager.savePasswords();
									s_pwMSwtHelper.createTree(tree,s_pwManager.getAllEntries());

									setGroupListToComboGroup();
								}
							} 
							catch (EntryDoesNotExistException e) 
							{
								LOGGER.error("EntryDoesNotExistException", e);
								deleteWasOK = false;
							} catch (StorageException e) {
								LOGGER.error("StorageException", e);
								deleteWasOK = false;
							}

							if(!deleteWasOK)
								guiHelper.displayErrorMessage(s_translations.getString("ERROR"), s_translations.getString("PWM.ELEMENT_DELETE_FAIL") 
										+" '"+ selectedElement+"'");
						}
						// not a group element or have child's
						else							
							guiHelper.displayErrorMessage(s_translations.getString("ERROR"), s_translations.getString("PWM.ONLY_EMPTY_GROUPS"));
					}
				}
			});

			createUser.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent arg0) 
				{
					s_changedByEdit = false;
					s_allowAccountchange = false;
					final TreeItem[] t = tree.getSelection();

					// get selected Element

					int selectedNR = 0;
					if(t.length != 0) 
					{
						selectedNR = comboGroup.indexOf(s_pwManager.getGroupSlot(t[0].getText()));	
					}					
					comboGroup.select(selectedNR);

					// empty fields
					textAccountID.setText("");
					textUsername.setText("");
					textPassword.setText("");
					textLink.setText("");
					textComment.setText("");

					changePWManagerFields(true);

				}
			});

			deleteUser.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent arg0) 
				{

					final TreeItem[] t = tree.getSelection();

					// warn nothing is selected
					if (t.length == 0)
						guiHelper.displayErrorMessage(s_translations.getString("ERROR"), s_translations.getString("PWM.SELECT_ACCOUNT_BEFORE_DELETE"));
					else 
					{
						final String selectedElement = t[0].getText();
						LOGGER.debug("Try to delete element '" + selectedElement+"'");
						if(s_pwManager.existID(selectedElement)  && 
								! s_pwManager.isGroupElement(selectedElement))
						{
							boolean deleteWasOK = true;
							// Group element has no childs
							try {

								if(guiHelper.displayQuestionMessage(s_translations.getString("PWM.ATTENTION"), s_translations.getString("PWM.REALY_DELETE_ACCOUNT")
										+" '"+selectedElement+"' ?"))
								{
									//s_pwManager.deleteElement(selectedElement);
									AccountEntry accountToDelete = new AccountEntry(selectedElement);
									s_pwManager.deleteEntry(accountToDelete);
									s_pwManager.savePasswords();
									s_pwMSwtHelper.createTree(tree,s_pwManager.getAllEntries());

									// empty fields
									textAccountID.setText("");
									textUsername.setText("");
									textPassword.setText("");
									textLink.setText("");
									textComment.setText("");

								}
							} 
							catch (EntryDoesNotExistException e) 
							{
								LOGGER.error("EntryDoesNotExistException", e);
								deleteWasOK = false;
							} catch (StorageException e) {
								LOGGER.error("StorageException", e);
								deleteWasOK = false;
							}

							if(!deleteWasOK)
								guiHelper.displayErrorMessage(s_translations.getString("ERROR"), s_translations.getString("PWM.ELEMENT_DELETE_FAIL") );
						}
						else
						{
							// not a group element or has child's
							guiHelper.displayErrorMessage(s_translations.getString("ERROR"), s_translations.getString("PWM.ONLY_ACCOUNT_DELETE"));
						}
					}	
				}
			});

			saveButton.addSelectionListener(new SelectionAdapter() 
			{
				public void widgetSelected(SelectionEvent arg0) 
				{
					boolean allSaveOK = true;
					if(textAccountID.getText() == null)
					{
						guiHelper.displayWarningMessage(s_translations.getString("ERROR"), s_translations.getString("PWM.AID_NOT_EMPTY"));
						allSaveOK = false;
					}
					else if (textAccountID.getText().trim().equals(""))
					{
						guiHelper.displayWarningMessage(s_translations.getString("ERROR"), s_translations.getString("PWM.AID_NOT_EMPTY"));
						allSaveOK = false;
					}
					else 	
					{
						// Account ID is set
						if(s_changedByEdit)
						{
							if(s_editingElement != null)
							{
								String newId = textAccountID.getText().trim();

								if( (s_pwManager.existID(newId) && newId.equals(s_editingElement.getId()))
										|| ! s_pwManager.existID(newId)
								)
								{

									try 
									{
										s_pwManager.deleteEntry(s_editingElement);

										AccountEntry editedAccount = createAccountFromForm();

										s_pwManager.addEntry(editedAccount);

										s_pwManager.savePasswords();
									} 
									catch (EntryDoesNotExistException e) 
									{
										LOGGER.error("EntryDoesNotExistException", e);
										allSaveOK = false;
									} 
									catch (EntryAlreadyExistException e) 
									{
										LOGGER.error("EntryAlreadyExistException", e);
										allSaveOK = false;
									} catch (StorageException e) {
										LOGGER.error("StorageException", e);
										allSaveOK = false;
									}
									s_pwMSwtHelper.createTree(tree,s_pwManager.getAllEntries());
								}
								else 
								{
									guiHelper.displayErrorMessage(s_translations.getString("ERROR"), s_translations.getString("PWM.ERROR_ELEMENT_CHANGE"));
									allSaveOK = false;
								}
							}
							else
							{
								guiHelper.displayErrorMessage(s_translations.getString("ERROR"), s_translations.getString("PWM.ERROR_ELEMENT_CHANGE"));
								allSaveOK = false;
							}
						}
						else
						{
							if (s_pwManager.existID(textAccountID.getText().trim()))
							{
								guiHelper.displayWarningMessage(s_translations.getString("ERROR"), s_translations.getString("PWM.AID_EXIST"));
								allSaveOK = false;
							}
							else
							{
								try 
								{
									AccountEntry editedAccount = createAccountFromForm();

									s_pwManager.addEntry(editedAccount);


									s_pwManager.savePasswords();
									s_pwMSwtHelper.createTree(tree,s_pwManager.getAllEntries());
								} 
								catch (EntryDoesNotExistException e) 
								{
									LOGGER.error("EntryDoesNotExistException", e);
									allSaveOK = false;
								} 
								catch (EntryAlreadyExistException e) 
								{
									LOGGER.error("EntryAlreadyExistException", e);
									allSaveOK = false;
								} catch (StorageException e) {
									LOGGER.error("StorageException", e);
									allSaveOK = false;
								}
							}
						}

						if(allSaveOK)
						{
							s_allowAccountchange = true;
							changePWManagerFields(false);
						}
					}
				}
			});

		} 
		catch (FileNotFoundException e) 
		{
			pwManagerOpenOK = false;
			LOGGER.error("FileNotFoundException", e);
		}
		catch (IOException e) 
		{
			pwManagerOpenOK = false;
			LOGGER.error("IOException", e);
		} catch (StorageException e) {
			pwManagerOpenOK = false;
			LOGGER.error("StorageException", e);
		}

		if (! pwManagerOpenOK)
		{
			// no enabled buttons if PW-Manager does not work
			editButton.setEnabled(false);
			editcancelButton.setEnabled(false);
			saveButton.setEnabled(false);
			createGroup.setEnabled(false);
			createUser.setEnabled(false);
			deleteGroup.setEnabled(false);
			deleteUser.setEnabled(false);
			copyPasswordButton.setEnabled(false);
			copyUsernameButton.setEnabled(false);
			openLinkButton.setEnabled(false);
		}
		
		return s_compositePasswords;
	}

	protected void setGroupListToComboGroup() 
	{

		final List<GroupEntry> groupEntries = s_pwManager.getGroupEntryList();

		// All Groups + empty Element
		final String[] listEntry = new String[groupEntries.size()+1];
		listEntry[0] = "";
		for(int i = 0; i < groupEntries.size(); i++)
		{
			listEntry[i+1] = groupEntries.get(i).getId();	
		}	

		comboGroup.setItems(listEntry);

	}


	/**
	 * Build AccountEntry form Password Manager Form
	 * @return AccountEntry
	 * @throws EntryDoesNotExistException 
	 */
	private AccountEntry createAccountFromForm() throws EntryDoesNotExistException 
	{

		AccountEntry editedAccount = new AccountEntry(textAccountID.getText().trim());
		editedAccount.setName(textUsername.getText());
		editedAccount.setPassword(textPassword.getText());
		editedAccount.setLink(textLink.getText());
		editedAccount.setComment(textComment.getText());

		String groupId = comboGroup.getText().trim();
		if(!groupId.equals(""))
		{
			GroupEntry groupToSet = s_pwManager.getGroupEntry(comboGroup.getText());
			editedAccount.setParentEntry(groupToSet);
		}

		return editedAccount;
	}


	/**
	 * Fill the Fields of the PW-Manager with the informations of the Account.
	 *
	 * @param accountEntry the asked Account
	 */
	private void fillPWManagerFields(AccountEntry accountEntry)
	{
		if(accountEntry != null)
		{
			final int selectedNR = comboGroup.indexOf(
					s_pwManager.getGroupSlot(accountEntry.getId()));

			comboGroup.select(selectedNR);


			String tmpTxt = "";
			tmpTxt = accountEntry.getId();
			if( tmpTxt == null)
				tmpTxt = "";
			textAccountID.setText(tmpTxt);

			tmpTxt = accountEntry.getName();
			if( tmpTxt == null)
				tmpTxt = "";
			textUsername.setText(tmpTxt);

			tmpTxt = accountEntry.getPassword();
			if( tmpTxt == null)
				tmpTxt = "";
			textPassword.setText(tmpTxt);

			tmpTxt = accountEntry.getLink();
			if( tmpTxt == null)
				tmpTxt = "";
			textLink.setText(tmpTxt);

			tmpTxt = accountEntry.getComment();
			if( tmpTxt == null)
				tmpTxt = "";
			textComment.setText(tmpTxt);
			editButton.setEnabled(true);

		}
		else
		{
			textAccountID.setText("");
			textUsername.setText("");
			textPassword.setText("");
			textLink.setText("");
			textComment.setText("");
			editButton.setEnabled(false);
		}

	}

	/**
	 * Change the buttons and Labels depending if they
	 * are editable.
	 *  
	 * @param allowEdit if edit of account entry is allowed
	 */
	private void changePWManagerFields(boolean allowEdit)
	{
		comboGroup.setEnabled(allowEdit);
		textAccountID.setEnabled(true);
		textUsername.setEnabled(true);
		textPassword.setEnabled(true);
		textLink.setEnabled(true);
		textComment.setEnabled(true);

		textAccountID.setEditable(allowEdit);
		textUsername.setEditable(allowEdit);
		textPassword.setEditable(allowEdit);
		textLink.setEditable(allowEdit);
		textComment.setEditable(allowEdit);

		saveButton.setEnabled(allowEdit);
		editcancelButton.setEnabled(allowEdit);
		editButton.setEnabled(!allowEdit);
		copyPasswordButton.setVisible(!allowEdit);
		copyUsernameButton.setVisible(!allowEdit);
		openLinkButton.setVisible(!allowEdit);

		if(allowEdit)
		{
			s_pwCopyStackLayout.topControl = generatePassword;
			generatePassword.setVisible(true);
			openLinkButton.setVisible(false);

			final Color textBackground = new Color(s_display, 255,255,255);
			comboGroup.setBackground(textBackground);
			textAccountID.setBackground(textBackground);
			textUsername.setBackground(textBackground);
			textPassword.setBackground(textBackground);
			textLink.setBackground(textBackground);
			textComment.setBackground(textBackground);
			textPassword.setEchoChar(s_EchoChar);

			textAccountID.setFocus();
		}
		else
		{
			s_pwCopyStackLayout.topControl = copyPasswordButton;
			comboGroup.setBackground(AtaraxisStarter.COLOR);
			textAccountID.setBackground(AtaraxisStarter.COLOR);
			textUsername.setBackground(AtaraxisStarter.COLOR);
			textPassword.setBackground(AtaraxisStarter.COLOR);
			textLink.setBackground(AtaraxisStarter.COLOR);
			textComment.setBackground(AtaraxisStarter.COLOR);
			textPassword.setEchoChar('*');
		}
		m_pwCopyStack.layout();
	}
	
	/**
	 * Method to set the icons.
	 */
	private void setIcons()
	{
		ICON_PWGen = new Image(s_display, ICON_DIR + "/generate.png");
		ICON_ADDACCOUNT = new Image(s_display, ICON_DIR + "/XML_User_add.png");
		ICON_DELACCOUNT = new Image(s_display, ICON_DIR + "/XML_User_del.png");
		ICON_DELGROUP = new Image(s_display, ICON_DIR + "/XML_Folder_del.png");
		ICON_ADDGROUP = new Image(s_display, ICON_DIR + "/XML_Folder_add.png");
		ICON_COPY = new Image(s_display, ICON_DIR + "/copy.png");
		ICON_WEB = new Image(s_display, ICON_DIR + "/web.png");
	}
}
