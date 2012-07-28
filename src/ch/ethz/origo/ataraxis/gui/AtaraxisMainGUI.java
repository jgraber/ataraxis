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

package ch.ethz.origo.ataraxis.gui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipException;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.TreeEvent;
import org.eclipse.swt.events.TreeListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import ch.ethz.origo.ataraxis.crypt.AtaraxisCrypter;
import ch.ethz.origo.ataraxis.crypt.CryptoMethodError;
import ch.ethz.origo.ataraxis.misc.AtaraxisShredder;
import ch.ethz.origo.ataraxis.misc.AtaraxisUpdateInfo;
import ch.ethz.origo.ataraxis.passwordmanager.AccountEntry;
import ch.ethz.origo.ataraxis.passwordmanager.EntryAlreadyExistException;
import ch.ethz.origo.ataraxis.passwordmanager.EntryDoesNotExistException;
import ch.ethz.origo.ataraxis.passwordmanager.GroupEntry;
import ch.ethz.origo.ataraxis.passwordmanager.PasswordManager;
import ch.ethz.origo.ataraxis.passwordmanager.PasswordManagerSWTHelper;
import ch.ethz.origo.ataraxis.passwordmanager.StorageException;

/**
 * AtaraxisMainGUI is the main class of the presentation layer.
 * 
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 */
class AtaraxisMainGUI
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(AtaraxisMainGUI.class);

	private static final String VERSION = "1.2.0";
	private static final String AUTHOR = "Johnny Graber + Andreas Muedespacher";
	private static final String DATE = "16.10.2010";
	private static final String WEBSITE = "http://ataraxis.origo.ethz.ch/";

	// component constants:
	private static final int PASSWORDS = 1;
	private static final int ENCRYPTION = 2;
	private static final int DECRYPTION = 3;	
	private static final int SHRED = 4;
	private static final int CONFIGURATION = 5;
	private static final int INFROMATION = 6;
	private static final int PUBLIC_KEYS = 7;

	private static final int COMPOSITE_WIDTH = 456;
	private static final int COMPOSITE_HEIGHT= 438;
	private static final int GROUP_HEIGHT = 120;
	private static final int GROUP_WIDTH = 450;
	private static final int BUTTON_HEIGHT = 80;
	private static final int BUTTON_WIDTH = 120;
	private static final int TEXT_WIDTH = 425;

	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String USER_DATA_DIR = APPL_DIR + "/user_data";
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	private static final String ICON_DIR = APPL_DATA_DIR + "/icons";
	private static final String LOG_FILE = APPL_DATA_DIR + "/ataraxis.log";
	private static final String LOG_FILE_SHORT = ".../application_data/ataraxis.log";
	private static final String NET_FILE = APPL_DATA_DIR + "/config/network.properties";
	private static final String EMPTY_XML = APPL_DATA_DIR + "/user_data_template/template.xml";

	private static final String SUFFIX_FILE = ".ac";
	private static final String SUFFIX_FOLDER = ".acz";
	private static final String SUFFIX_ZIP = ".zip";

	private static final int ENCRYPT_SOURCE = 1;
	private static final int ENCRYPT_TARGET = 2;
	private static final int DECRYPT_SOURCE = 3;
	private static final int DECRYPT_TARGET = 4;
	private static final int SHRED_SOURCE = 5;

	private static final int FILE = 1;
	private static final int FOLDER = 2;

	private static final int GERMAN = 0;
	private static final int FRENCH = 1;
	private static final int ENGLISH = 2;

	private static final int SHRED_ALGO_ZEROES = 0;
	private static final int SHRED_ALGO_DOD = 1;
	private static final int SHRED_ALGO_DOD_EXT = 2;
	private static final int SHRED_ALGO_VSITR = 3;
	private static final int SHRED_ALGO_SCHNEIER = 4;
	private static final int SHRED_ALGO_GUTMANN_FLOPPY = 5;
	private static final int SHRED_ALGO_GUTMANN = 6;
	private static final int SHRED_ALGO_RANDOM = 7;
	private static final int SHRED_ALGO_DEFAULT = SHRED_ALGO_RANDOM;

	private static final int FATAL = 0;
	private static final int WARN = 1;
	private static final int INFO = 2;
	private static final int DEBUG = 3;
	private static final String LOG_LEVEL_KEY = "log4j.appender.dest1.Threshold";
	private static final String DEFAULT_DELETE_KEY = "default.delete.algo";

	private static String s_textWindowTitle;
	private static String s_textToggleEncrypt;
	private static String s_textToggleDecrypt;
	private static String s_textToggleShred;
	private static String s_textToggleAccount;
	private static String s_textToggleConfig;
	private static String s_textToggleInfo;
	private static String s_textToggleExit;
	private static String s_textBrowse;
	private static String s_textFolder;
	private static String s_textFile;
	private static String s_textSource;
	private static String s_textTarget;
	private static String  s_messageExitingTitle;
	private static String s_messageExiting;
	@SuppressWarnings("unused") // is used!
	private static String s_textYes;
	@SuppressWarnings("unused") // is used!
	private static String s_textNo;
	private static String s_textEncrypt;
	private static String s_textDecrypt;
	private static String s_textShred;
	private static String s_textShredOptions;
	private static String s_textTrayToolTip;
	private static String s_textGroupApplConfig;
	private static String s_textGroupUserConfig;
	private static String s_textshredZeroes;
	private static String s_textShredDoD;
	private static String s_textShredDoDext;
	private static String s_textShredVSITR;
	private static String s_textShredRandom;
	private static String s_textShredSchneier;
	private static String s_textShredGutmannFloppy;
	private static String s_textShredGutmann;
	private static String s_textShredLeaveDir;
	private static String s_textShredProcedure;
	private static String s_textShredRepetitions;

	private static String s_textLogLevelLabel;
	private static String s_textLogFile;
	private static String s_textLanguage;
	private static String s_textDeleteAlgorithm;
	private static String s_textPasswordOld;
	private static String s_textPasswordNew;
	private static String s_textPasswordNewRep;
	private static String s_messageNoTxtProgTitle;
	private static String s_messageNoTxtProg;
	private static String s_textGerman;
	private static String s_textFrench;
	private static String s_textEnglish;

	private static String s_textAuthor;
	private static String s_textLicense;
	private static String s_textVersion;
	private static String s_textDescription;
	private static String s_textJavaVendor;
	private static String s_textJavaHome;
	private static String s_textJavaVersion;
	// private static String s_textJavaMemory;
	private static String s_textGroupApplInfo;
	private static String s_textGroupSysInfo;
	private static String s_textWebsite;
	private static String s_textOsName;
	private static String s_messageEncryptTitle;
	private static String s_messageEncrypt;
	private static String s_messageDecryptTitle;
	private static String s_messageDecrypt;
	private static String s_messageOverwriteTitle;
	private static String s_messageOverwrite;
	private static String s_messageEmptyFieldTitle;
	private static String s_messageEmptyField;
	private static String s_messageNoSourceTitle;
	private static String s_messageNoSource;
	@SuppressWarnings("unused") // is used!
	private static String s_textCancel;
	private static String s_textFilesAtaraxis;
	private static String s_textFilesAll;
	private static String s_messageDurationTitle;
	private static String s_messageDurationFile;
	private static String s_messageDurationDir;
	private static String s_messageDeletionConflictTitle;
	private static String s_messageDeletionConflict;
	private static String s_messageFilePermission;
	private static String s_messageFileConflictTitle;
	private static String s_messageFileConflict;
	private static String s_messageCantUnzip;
	private static String s_messageEmptyOldPwd;
	private static String s_messageEmptyNewPwds;
	private static String s_messageNewPwdTitle;
	private static String s_messageNewPwd;
	private static String s_messageInvalidPasswordTitle;
	private static String s_messageInvalidPassword;
	private static String s_messagePasswordChangeFailed;
	private static String s_messageNotAllowedTitle;
	private static String s_messageNotCompressFolder;
	private static String s_messageCompressZip;
	private static String s_textButtonCompr;
	private static String s_textButtonDecompr;
	private static String s_textButtonDeleteSrc;
	private static String s_textButtonChangePwd;
	private static String s_messageCryptoErrorTitle;
	private static String s_messageCryptoError;	

	// i18n Text for PasswordManager
	private static String s_passwordManagerTitle;
	private static String s_groupLabelText;
	private static String s_accountIdLabelText;
	private static String s_usernameLabelText;
	private static String s_passwordLabelText;
	private static String s_linkLabelText;
	private static String s_commentLabelText;
	private static String s_copyUsernameButtonTip;
	private static String s_openLinkButtonTip;
	private static String s_createGroupTip;
	private static String s_deleteGroupTip;
	private static String s_createUserTip;
	private static String s_deleteUserTip;
	private static String s_editButtonText;
	private static String s_editcancelButtonText;
	private static String s_saveButtonText;
	private static String s_copyPasswordButtonTip;
	private static String s_generatePasswordTip;
	private static String s_pPwmError;
	private static String s_pFileMissing;

	private static PasswordManager s_pwManager = null;
	private static AccountEntry s_pLastSelectedAccount;

	private static String s_pAttention;
	private static String s_pFirstEndUserMod;
	private static String s_error;
	private static String s_pSelectGroupBeforeDelete;
	private static String s_pSelectAccount;
	private static String s_pGroupExists;
	private static String s_pGroupCreateError;
	private static String s_pRealyDeleteGroup;
	private static String s_pElementDeleteFailed;
	private static String s_pOnlyEmptyGroupsDelete;
	private static String s_pSelectAccountBeforeDelete;
	private static String s_pRealyDeleteAccount;
	private static String s_pOnlyAccountDelete;
	private static String s_pAIDnotEmpty;
	private static String s_pErrorElementChange;
	private static String s_pErrorAIDexist;

	// i18n Text for TextInputDialog
	@SuppressWarnings("unused") // is used!
	private static String s_TID_Title;
	@SuppressWarnings("unused") // is used!
	private static String s_TID_OkButton;
	@SuppressWarnings("unused") // is used!
	private static String s_TID_labelText;
	@SuppressWarnings("unused") // is used!
	private static String s_TID_warnText;		
	@SuppressWarnings("unused") // is used!
	private static String s_TID_warnMessage;
	@SuppressWarnings("unused") // is used!
	private static String s_warnMessage;

	private static boolean s_changedByEdit = false; 
	private static boolean s_allowAccountchange = true;


	private static int s_x, s_y;
	private static int s_encryptFiletype = 0;
	private static int s_shredFiletype = 0;
	private static int s_lang = 0;
	private static int s_deleteDefault = SHRED_ALGO_DEFAULT;
	private static int s_logLevel = 1;

	// swt widget objects
	private static TrayItem s_trayItem = null;
	private static Shell s_shell = null;
	private static Display s_display = null;
	private static int s_selectedComposite = 0;
	private static Composite naviComposite = null;
	private static Composite s_composite = null;
	private static Composite s_compositeEncrypt = null;
	private static Composite s_compositeDecrypt = null;
	private static Composite s_compositeShred = null;
	private static Composite s_compositePasswords = null;
	private static Composite s_compositePublicKeys = null;
	private static Composite s_compositeConfig = null;
	private static Composite s_compositeInfo = null;
	private static Image s_icon = null;
	private static Font s_fontBig = null;
	private static Font s_fontMiddle = null;
	@SuppressWarnings("unused") // is used!
	private static Font s_fontRed = null;
	private static Text s_textEncryptSource = null;
	private static Text s_textEncryptTarget = null;
	private static Text s_textDecryptSource = null;
	private static Text s_textDecryptTarget = null;
	private static Text s_textShredSource = null;
	private static Combo s_configLogCombo = null;
	private static Combo s_configLangCombo = null;
	private static String s_user = "";
	private static Properties s_userProps = null;
	private static Properties s_logProps = null;
	private static String s_accountFile = "";
	private static Text s_configPwd = null;
	private static Text s_configPwdNew = null;
	private static Text s_configPwdNewRep = null;
	private static Label s_labelDeletion = null;
	private static Button s_buttonEncryptDelete = null;
	private static Button s_buttonDecryptDelete = null;
	private static Button s_buttonEncryptCompr = null;
	private static Button s_buttonDecryptCompr = null;
	private static StackLayout s_pwCopyStackLayout = null;
	private static AccountEntry s_editingElement = null;
	private static Listener naviListener = null;
	private static StackLayout s_stackLayout = null;
	private Composite m_pwCopyStack = null;

	private PasswordManagerSWTHelper s_pwMSwtHelper;

	private static Button buttonProxyNo;

	private static Button buttonProxyYes;

	private static Text s_networkHost;

	private static Text s_networkPort;

	private static String s_textNetworkHost = "";

	private static String s_textNetworkPort = "";

	// Textfields and Combo for PasswordManager
	private static Combo comboGroup;
	private static Text textAccountID;
	private static Text textUsername;
	private static Text textPassword;
	private static Text textLink;
	private static Text textComment;

	// Buttons for PasswordManager
	private static Button copyUsernameButton;
	private static Button openLinkButton;
	private static Button createGroup;
	private static Button deleteGroup;
	private static Button createUser;
	private static Button deleteUser;
	private static Button editButton;
	private static Button editcancelButton;
	private static Button saveButton;
	private static Button copyPasswordButton;
	private static Button generatePassword;
	private static char s_EchoChar;

	// Labels for PasswordManager
	private static Label passwordManagerTitle;
	private static Label groupLabel;
	private static Label accountIdLabel;
	private static Label usernameLabel;
	private static Label passwordLabel;
	private static Label linkLabel;
	private static Label commentLabel;

	private static AtaraxisShredder s_shredder = null;
	protected static Properties s_langProps = null;
	private static AtaraxisCrypter s_ac = null;

	private static String s_NETWORK_PROXY_YES;
	private static String s_NETWORK_PROXY_NO;
	private static String s_NETWORK_GROUP_TITLE;
	private static String s_NETWORK_SAVE_BUTTON;
	private static Properties s_networkProps;
	private static String s_UPDATE_CHECK;
	private static String s_UPDATE_BOXTITLE;
	private static String s_UPDATE_LOAD_NEW_VERSION;
	private static String s_UPDATE_UPTODATE;
	private static String s_UPDATE_ERROR_DONTWORK;
	private static String s_entryDoesNotExist;
	private static String s_pwmRenameOkTitle;
	private static String s_pwmRenameOkMessage;
	private static String s_pwmRenameErrorTitle;
	private static String s_pwmRenameErrorNotFound;
	private static String s_pwmRenameErrorAlreadyExist;
	private static String s_pwmRenameContextMenu;

	private static String s_pwmRenameErrorSave;

	
	// images for GUI
	private final static Image ICON_ENCRYPT = new Image(s_display, ICON_DIR + "/Menu_Encrypt.png");
	private final static Image ICON_DECRYPT = new Image(s_display, ICON_DIR + "/Menu_Decrypt.png");
	private final static Image ICON_SHRED = new Image(s_display, ICON_DIR + "/Menu_Shredder.png");
	private final static Image ICON_PASSWORDS = new Image(s_display, ICON_DIR + "/Menu_Password.png");
	private final static Image ICON_CONFIG = new Image(s_display, ICON_DIR + "/Menu_Config.png");
	private final static Image ICON_INFO = new Image(s_display, ICON_DIR + "/Menu_Info.png");
	private final static Image ICON_EXIT = new Image(s_display, ICON_DIR + "/Menu_Exit.png");
	// private final static Image ICON_PGP = new Image(s_display, ICON_DIR + "/Menu_Passwords.png");

	private final static Image ICON_FOLDER = new Image(s_display, ICON_DIR + "/folder.png");
	private final static Image ICON_FILE = new Image(s_display, ICON_DIR + "/file.png");
	private final static Image ICON_BROWSE = new Image(s_display, ICON_DIR + "/browse.png");
	private final static Image ICON_ATARAXIS = new Image(s_display, ICON_DIR + "/Info_Ataraxis.png");
	private final static Image ICON_PWGen = new Image(s_display, ICON_DIR + "/generate.png");
	private final static Image ICON_ADDACCOUNT = new Image(s_display, ICON_DIR + "/XML_User_add.png");
	private final static Image ICON_DELACCOUNT = new Image(s_display, ICON_DIR + "/XML_User_del.png");
	private final static Image ICON_DELGROUP = new Image(s_display, ICON_DIR + "/XML_Folder_del.png");
	private final static Image ICON_ADDGROUP = new Image(s_display, ICON_DIR + "/XML_Folder_add.png");
	private final static Image ICON_COPY = new Image(s_display, ICON_DIR + "/copy.png");
	private final static Image ICON_WEB = new Image(s_display, ICON_DIR + "/web.png");


	// ####################################################################
	// ########################## Inner Classes ###########################
	// ####################################################################

	/**
	 * This class is used to handle the drop action for Source-Text fields
	 */
	private final class DropTargetAdapterSource extends DropTargetAdapter
	{
		private final Text source;

		private DropTargetAdapterSource(Text source)
		{
			this.source = source;
		}

		public void drop(DropTargetEvent event)
		{
			LOGGER.info("Data has been dropped in source");
			String fileList[] = null;
			final FileTransfer ft = FileTransfer.getInstance();
			if (ft.isSupportedType(event.currentDataType)) {
				LOGGER.info("Dropped Data is one or more files");
				fileList = (String[])event.data;
				if (fileList != null && fileList[0].length() > 0)
				{
					source.setText(fileList[0]);
					if (s_selectedComposite == ENCRYPTION && s_textEncryptTarget.getText().equals(""))
					{
						if((new File(fileList[0]).isDirectory()))
							s_textEncryptTarget.setText(fileList[0] + SUFFIX_FOLDER);
						else if (fileList[0].endsWith(SUFFIX_ZIP))
							s_textEncryptTarget.setText(fileList[0].substring(0, fileList[0].length() - SUFFIX_ZIP.length()) + SUFFIX_FOLDER);
						else
							s_textEncryptTarget.setText(fileList[0] + SUFFIX_FILE);
					}
					else if (s_selectedComposite == DECRYPTION && s_textDecryptTarget.getText().equals(""))
					{
						if (fileList[0].endsWith(SUFFIX_FILE))
							s_textDecryptTarget.setText(fileList[0].substring(0, fileList[0].length() - SUFFIX_FILE.length()));
						else if (fileList[0].endsWith(SUFFIX_FOLDER))
						{
							s_textDecryptTarget.setText((new File(fileList[0])).getParent());
							s_buttonDecryptCompr.setSelection(true);
						}
						// else: there's no reasonable suggestion to make if encrypted file has no .ac-ending...
					}
				}
			}
		}
	}

	/**
	 * This class is used to handle the drop action for Target-Text fields
	 */
	private final class DropTargetAdapterTarget extends DropTargetAdapter
	{
		private final Text target;

		private DropTargetAdapterTarget(Text source)
		{
			this.target = source;
		}

		public void drop(DropTargetEvent event)
		{
			LOGGER.info("Data has been dropped in target");
			String fileList[] = null;
			final FileTransfer ft = FileTransfer.getInstance();
			if (ft.isSupportedType(event.currentDataType))
			{
				LOGGER.info("Dropped Data is one or more files");
				fileList = (String[])event.data;
				if (fileList != null && fileList[0].length() > 0)
				{
					if (!fileList[0].endsWith(SUFFIX_FILE))
						target.setText(fileList[0] + SUFFIX_FILE);
					else
						target.setText(fileList[0]);

					// to autocomplete source entry:
					String text = "";
					if(fileList[0].endsWith(SUFFIX_FILE))
						text = fileList[0].substring(0, fileList[0].length() - SUFFIX_FILE.length());
					else if (s_selectedComposite == ENCRYPTION)
						text = fileList[0];
					if (s_selectedComposite == DECRYPTION)
					{
						text = fileList[0]+ SUFFIX_FILE;
						target.setText(fileList[0]);
					}


					if (s_selectedComposite == ENCRYPTION && s_textEncryptSource.getText().equals(""))
						s_textEncryptSource.setText(text);
					else if (s_selectedComposite == DECRYPTION && s_textDecryptSource.getText().equals(""))
						s_textDecryptSource.setText(text);
				}
			}
		}
	}

	/**
	 * This class is used to borwse for a Source and for a target file.
	 * The int parameter can be AtaraxisGUI.OPEN or AtaraxisGUI.SAVE
	 */
	private static final class Browse extends SelectionAdapter
	{
		int action;
		int filetype = FILE;
		private Browse(int action)
		{
			this.action = action;
		}

		public void widgetSelected(SelectionEvent e)
		{
			// if called from shred source, then set filetype (file/folder)
			if (action == SHRED_SOURCE)
			{
				this.filetype = s_shredFiletype;
				LOGGER.debug("Browse in SHRED_SOURCE");
			}
			// if called from encrypt source, then set filetype (file/folder)
			else if(action == ENCRYPT_SOURCE)
			{
				this.filetype = s_encryptFiletype;
				LOGGER.debug("Browse in ENCRYPT_SOURCE");
			}

			if (filetype == FOLDER)
			{
				browseOpenFolder(action);
				LOGGER.debug("FOLDER (open)");
			}
			else
			{
				LOGGER.debug("FILE (open or save)");
				if(action == ENCRYPT_TARGET || action == DECRYPT_TARGET)
					browseSave(action);
				else
					browseOpenFile(action);
			}

		}
	}

	/**
	 * This class is used to encrypt a file, if the encrypt button is selected.
	 */
	private static final class Encrypt extends SelectionAdapter
	{
		String source;
		String target;
		private Encrypt()
		{
			// empty constructor
		}

		public void widgetSelected(SelectionEvent e)
		{
			source = s_textEncryptSource.getText();
			target = s_textEncryptTarget.getText();

			if (source.equals("") || target.equals(""))
			{
				warningMessage(s_messageEmptyFieldTitle, s_messageEmptyField);
			}
			else if (fileExists(source) && createTargetFile(target))
			{
				try
				{
					s_shell.setCursor(new Cursor(s_display,SWT.CURSOR_WAIT));
					s_ac.encryptFile(new File(source), new File(target), s_buttonEncryptCompr.getSelection());

					if (s_buttonEncryptDelete.getSelection())
						shred(source, s_deleteDefault, false);
					infoMessage(s_messageEncryptTitle, s_messageEncrypt);
				}
				catch (FileNotFoundException fnfe)
				{
					// should never happen, because source file must exist,
					// maybe it can happen if directory is write protected
					errorMessage(s_messageFileConflictTitle, s_messageFilePermission);
					LOGGER.debug("Error during encryption!", fnfe);
				}
				catch (CryptoMethodError cme)
				{
					LOGGER.error("Error during encryption!", cme);
					errorMessage(s_messageCryptoErrorTitle, s_messageCryptoError);
				}
				s_shell.setCursor(null);
			}
		}
	}

	/**
	 * This class is used to decrypt a file, if the decrypt button is selected.
	 */
	private static final class Decrypt extends SelectionAdapter
	{
		String source;
		String target;
		private Decrypt()
		{
			// empty constructor
		}

		public void widgetSelected(SelectionEvent e)
		{
			source = s_textDecryptSource.getText();
			target = s_textDecryptTarget.getText();

			if (source.equals("") || target.equals(""))
			{
				warningMessage(s_messageEmptyFieldTitle, s_messageEmptyField);
			}
			else if (fileExists(source) && createTargetFile(target))
			{
				try
				{
					s_shell.setCursor(new Cursor(s_display,SWT.CURSOR_WAIT));
					s_ac.decryptFile(new File(source), new File(target), s_buttonDecryptCompr.getSelection());

					if (s_buttonDecryptDelete.getSelection())
						shred(source, s_deleteDefault, false);
					infoMessage(s_messageDecryptTitle, s_messageDecrypt);
				}
				catch (FileNotFoundException fnfe)
				{
					errorMessage(s_messageFileConflictTitle, s_messageFileConflict);
				}
				catch (CryptoMethodError cme)
				{
					LOGGER.error("Error during decryption!", cme);
					errorMessage(s_messageCryptoErrorTitle, s_messageCryptoError);
				}
				catch (ZipException ze)
				{
					//ze.printStackTrace();
					errorMessage(s_messageFileConflictTitle, s_messageCantUnzip);
				}
				s_shell.setCursor(null);
			}
		}
	}

	/**
	 * This class is used to change the login password
	 */
	private static final class ChangePwdButton extends SelectionAdapter
	{
		private ChangePwdButton()
		{
			// empty constructor
		}

		public void widgetSelected(SelectionEvent e)
		{
			// validate entries (length, characters and identical passwords)
			final String pwd = s_configPwd.getText();
			final String pwdNew = s_configPwdNew.getText();
			final String pwdNewRep = s_configPwdNewRep.getText();

			if (pwd.equals(""))
				warningMessage(s_messageEmptyFieldTitle, s_messageEmptyOldPwd);
			else if (pwdNew.equals("") || pwdNewRep.equals(""))
				warningMessage(s_messageEmptyFieldTitle, s_messageEmptyNewPwds);
			else
			{
				boolean validPwd = pwdNew.equals(pwdNewRep);
				validPwd = validPwd && pwdNew.length() > 3; // pwd must have at least 4 chars
				if (validPwd && !pwdNew.equalsIgnoreCase(s_user))
				{
					try 
					{
						if(s_ac.changePassword(pwd.toCharArray(), pwdNew.toCharArray()))
						{
							infoMessage(s_messageNewPwdTitle, s_messageNewPwd);
						}		
						else
						{
							warningMessage(s_messageInvalidPasswordTitle, s_messagePasswordChangeFailed);
						}
					} 
					catch (KeyStoreException kse) {
						LOGGER.warn(kse.getMessage());
					} 
					catch (IOException ioe) {
						LOGGER.warn(ioe.getMessage());
					}
				}
				else 
				{
					warningMessage(s_messageInvalidPasswordTitle, s_messageInvalidPassword);
				}
			}
		}
	}


	/**
	 * AtaraxisMainGUI constructor
	 * @param ac the AtaraxisCrypter to use for Encryption/Decryption
	 */
	AtaraxisMainGUI(AtaraxisCrypter ac)
	{
		s_ac = ac;	
	}


	/**
	 * Open the window
	 */
	public void open()
	{
		String pathOfKeyStore = s_ac.getKeyStorePath();
		File ksFile = new File(pathOfKeyStore);
		File directoryOfKs = ksFile.getParentFile();
		s_user = directoryOfKs.getName();
		
		s_accountFile = USER_DATA_DIR + "/" + s_user + "/accounts.data";

		loadProperties();
		loadLogProps();
		s_display = Display.getDefault();
		createContents();
		changeBackgroundColor(s_shell, AtaraxisStarter.COLOR);
		s_shell.addShellListener(new ShellAdapter()
		{
			public void shellIconified(ShellEvent shellEvent)
			{
				LOGGER.debug("Window is reduced to Icon");
				s_shell.setVisible(false);
			}
		});


		s_shell.open();
		s_shell.layout();
		makeTrayEntry();
		while (!s_shell.isDisposed())
		{
			if (!s_display.readAndDispatch())
				s_display.sleep();
		}
		s_trayItem.dispose();
	}

	/**
	 * Create contents of the window
	 */
	private void createContents()
	{
		s_shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN);
		s_shell.setLayout(new FormLayout());
		s_shell.setSize(578, 476);
		s_shell.setText(s_textWindowTitle + " - " + s_user);

		final FontData[] fontData = s_shell.getFont().getFontData();
		for (int i = 0; i < fontData.length; i++)
		{
			fontData[i].setHeight(32);
		}
		s_fontBig = new Font(s_display, fontData);

		for (int i = 0; i < fontData.length; i++)
		{
			fontData[i].setHeight(16);
		}
		s_fontMiddle = new Font(s_display, fontData);

		// Use only primary Monitor - important if 2 Displays
		Monitor primary = s_display.getPrimaryMonitor();
		final Rectangle displayRect = primary.getBounds();
		s_x = (displayRect.width - 578) / 2;
		s_y = (displayRect.height - 476) / 2;
		s_icon = new Image(s_display, ICON_DIR + "/Tray_Ataraxis.png");

		s_shell.setLocation(s_x, s_y);
		s_shell.setImage(s_icon);

		// ####################################################################
		// ###################### Navigation Composite ########################
		// ####################################################################

		naviComposite = new Composite(s_shell, SWT.NONE);
		naviComposite.setLayout(new FillLayout(SWT.VERTICAL));
		final FormData formData_1 = new FormData();
		formData_1.right = new FormAttachment(0, 80);
		formData_1.bottom = new FormAttachment(0, 450);
		formData_1.top = new FormAttachment(0, 0);
		formData_1.left = new FormAttachment(0, 0);
		naviComposite.setLayoutData(formData_1);

		// Listener to add radio button behavior to navigation buttons
		naviListener = new NavigationListener(naviComposite);

		// ####################################################################
		// ####################### Navigation Buttons #########################
		// ####################################################################

		final Button toggleAccount = new Button(naviComposite, SWT.TOGGLE);
		toggleAccount.setImage(ICON_PASSWORDS);
		toggleAccount.setToolTipText(s_textToggleAccount);
		toggleAccount.addListener(SWT.Selection, naviListener);


		final Button toggleEncrypt = new Button(naviComposite, SWT.TOGGLE);
		toggleEncrypt.setToolTipText(s_textToggleEncrypt);
		toggleEncrypt.setImage(ICON_ENCRYPT);
		toggleEncrypt.addListener(SWT.Selection, naviListener);

		final Button toggleDecrypt = new Button(naviComposite, SWT.TOGGLE);
		toggleDecrypt.setToolTipText(s_textToggleDecrypt);
		toggleDecrypt.setImage(ICON_DECRYPT);
		toggleDecrypt.addListener(SWT.Selection, naviListener);

		final Button toggleShred = new Button(naviComposite, SWT.TOGGLE);
		toggleShred.setImage(ICON_SHRED);
		toggleShred.addListener(SWT.Selection, naviListener);
		toggleShred.setToolTipText(s_textToggleShred);



		final Button toggleConfig = new Button(naviComposite, SWT.TOGGLE);
		toggleConfig.setImage(ICON_CONFIG);
		toggleConfig.setToolTipText(s_textToggleConfig);
		toggleConfig.addListener(SWT.Selection, naviListener);

		final Button toggleInfo = new Button(naviComposite, SWT.TOGGLE);
		toggleInfo.setImage(ICON_INFO);
		toggleInfo.setToolTipText(s_textToggleInfo);
		toggleInfo.addListener(SWT.Selection, naviListener);

		final Button toggleExit = new Button(naviComposite, SWT.TOGGLE); 
		toggleExit.setImage(ICON_EXIT);
		toggleExit.setToolTipText(s_textToggleExit);
		toggleExit.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (questMessage(s_messageExitingTitle, s_messageExiting))
					s_shell.dispose();
			}
			public void widgetDefaultSelected(SelectionEvent e)
			{ 
				// not implemented
			}
		});


		// ####################################################################
		// ########################## Stack Composite #########################
		// ####################################################################

		s_composite = new Composite(s_shell, SWT.NONE);
		final FormData formDataStack = new FormData();
		formDataStack.left = new FormAttachment(0, 95);
		formDataStack.bottom = new FormAttachment(0, 445);
		formDataStack.top = new FormAttachment(0, 0);
		formDataStack.right = new FormAttachment(0, 575);
		s_composite.setLayoutData(formDataStack);

		s_stackLayout = new StackLayout();
		s_composite.setLayout(s_stackLayout);


		// ####################################################################
		// ################# Create Contents of Stack Composit ################
		// ####################################################################

		createEncryptContent();
		createDecryptContent();
		createShrederContent();
		createPasswordsContent();
		createConfigContent();
		createInfoContent();

		// load Network Config
		loadNetworkProps();


		// if nothing is selected at startup, crypt composit is shown
		if (s_selectedComposite == 0)
		{
			toggleAccount.setSelection(true);
			s_selectedComposite = PASSWORDS;
			s_stackLayout.topControl = s_compositePasswords;
		}
	}

	private void createEncryptContent()
	{
		s_compositeEncrypt = new Composite(s_composite, SWT.NONE);
		final GridLayout gridLayoutEncrypt = new GridLayout();
		gridLayoutEncrypt.numColumns = 3;
		s_compositeEncrypt.setLayout(gridLayoutEncrypt);
		final GridData gridDataEncrypt = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gridDataEncrypt.widthHint = COMPOSITE_WIDTH;
		gridDataEncrypt.heightHint = COMPOSITE_HEIGHT;
		s_compositeEncrypt.setLayoutData(gridDataEncrypt);


		// ################## Composite Encrypt, Group Source ##################

		final Group groupEncryptSource = new Group(s_compositeEncrypt, SWT.NONE);
		groupEncryptSource.setText(s_textSource);
		final GridData gridDataEncryptSource = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gridDataEncryptSource.heightHint = GROUP_HEIGHT;
		gridDataEncryptSource.widthHint = GROUP_WIDTH;
		groupEncryptSource.setLayoutData(gridDataEncryptSource);
		final GridLayout gridLayoutEncryptSource = new GridLayout();
		gridLayoutEncryptSource.numColumns = 3;
		groupEncryptSource.setLayout(gridLayoutEncryptSource);

		s_textEncryptSource = new Text(groupEncryptSource, SWT.BORDER);
		final GridData gridDataEncryptSource_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gridDataEncryptSource_1.widthHint = TEXT_WIDTH;
		s_textEncryptSource.setLayoutData(gridDataEncryptSource_1);


		final DropTarget dtEncryptSource = new DropTarget(groupEncryptSource, DND.DROP_DEFAULT | DND.DROP_MOVE );
		dtEncryptSource.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dtEncryptSource.addDropListener(new DropTargetAdapterSource(s_textEncryptSource));



		final Button buttonEncryptSourceFile = new Button(groupEncryptSource, SWT.RADIO);
		final GridData gridDataEncryptSource_2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataEncryptSource_2.heightHint = BUTTON_HEIGHT;
		gridDataEncryptSource_2.widthHint = BUTTON_WIDTH;
		buttonEncryptSourceFile.setLayoutData(gridDataEncryptSource_2);
		buttonEncryptSourceFile.setImage(ICON_FILE);
		buttonEncryptSourceFile.setToolTipText(s_textFile);
		buttonEncryptSourceFile.setSelection(true);
		buttonEncryptSourceFile.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(buttonEncryptSourceFile.getSelection())
				{
					LOGGER.debug("buttonEncryptSourceFile is selected");	
					s_encryptFiletype = FILE;
				}
			}
			public void widgetDefaultSelected(SelectionEvent e)
			{ 
				// method not implemented
			}
		});

		final Button buttonEncryptSourceFolder = new Button(groupEncryptSource, SWT.RADIO);
		final GridData gridDataEncryptSource_3 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataEncryptSource_3.heightHint = BUTTON_HEIGHT;
		gridDataEncryptSource_3.widthHint = BUTTON_WIDTH;
		buttonEncryptSourceFolder.setLayoutData(gridDataEncryptSource_3);
		buttonEncryptSourceFolder.setImage(ICON_FOLDER);
		buttonEncryptSourceFolder.setToolTipText(s_textFolder);
		buttonEncryptSourceFolder.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(buttonEncryptSourceFolder.getSelection())
				{
					LOGGER.debug("buttonEncryptSourceFolder is selected");	
					s_encryptFiletype = FOLDER;
				}
			}
			public void widgetDefaultSelected(SelectionEvent e)
			{ 
				// method not implemented
			}
		});


		final Button buttonEncryptSource = new Button(groupEncryptSource, SWT.NONE);
		final GridData gridDataEncryptSource_4 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataEncryptSource_4.heightHint = BUTTON_HEIGHT;
		gridDataEncryptSource_4.widthHint = BUTTON_WIDTH;
		buttonEncryptSource.setLayoutData(gridDataEncryptSource_4);
		buttonEncryptSource.setImage(ICON_BROWSE);
		buttonEncryptSource.setToolTipText(s_textBrowse);
		buttonEncryptSource.addSelectionListener(new Browse(ENCRYPT_SOURCE));


		// ################### Composite Encrypt, Group Target ##################

		final Group groupEncryptTarget = new Group(s_compositeEncrypt, SWT.NONE);
		final GridData gridDataEncryptTarget = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gridDataEncryptTarget.heightHint = GROUP_HEIGHT;
		gridDataEncryptTarget.widthHint = GROUP_WIDTH;
		groupEncryptTarget.setLayoutData(gridDataEncryptTarget);
		final GridLayout gridLayoutEncryptTarget = new GridLayout();
		gridLayoutEncryptTarget.numColumns = 2;
		groupEncryptTarget.setLayout(gridLayoutEncryptTarget);
		groupEncryptTarget.setText(s_textTarget);

		s_textEncryptTarget = new Text(groupEncryptTarget, SWT.BORDER);
		final GridData gridDataEncryptTarget_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gridDataEncryptTarget_1.widthHint = TEXT_WIDTH;
		s_textEncryptTarget.setLayoutData(gridDataEncryptTarget_1);

		final DropTarget dtEncryptTarget = new DropTarget(groupEncryptTarget, DND.DROP_DEFAULT | DND.DROP_MOVE );
		dtEncryptTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dtEncryptTarget.addDropListener(new DropTargetAdapterTarget(s_textEncryptTarget));

		final SashForm sashFormEncryptTarget = new SashForm(groupEncryptTarget, SWT.NONE);
		sashFormEncryptTarget.setLayoutData(new GridData(314, SWT.DEFAULT));

		final Button buttonEncryptTargetFile = new Button(groupEncryptTarget, SWT.NONE);
		final GridData gridDataEncryptTarget_2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataEncryptTarget_2.heightHint = BUTTON_HEIGHT; // BUTTON_HEIGHT
		gridDataEncryptTarget_2.widthHint = BUTTON_WIDTH;
		buttonEncryptTargetFile.setLayoutData(gridDataEncryptTarget_2);
		buttonEncryptTargetFile.setImage(ICON_BROWSE);
		buttonEncryptTargetFile.setToolTipText(s_textBrowse);
		buttonEncryptTargetFile.addSelectionListener(new Browse(ENCRYPT_TARGET));
		new Label(s_compositeEncrypt, SWT.NONE);


		// ##################### Composite Encrypt, Buttons ####################

		final SashForm sashFormEncrypt = new SashForm(s_compositeEncrypt, SWT.NONE);
		final GridData gridData_2 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData_2.widthHint = 5;
		gridData_2.heightHint = 50;
		sashFormEncrypt.setLayoutData(gridData_2);
		new Label(s_compositeEncrypt, SWT.NONE);


		s_buttonEncryptCompr = new Button(s_compositeEncrypt, SWT.CHECK);
		s_buttonEncryptCompr.setLayoutData(new GridData(255, 35));
		s_buttonEncryptCompr.setText(s_textButtonCompr);
		s_buttonEncryptCompr.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(!s_buttonEncryptCompr.getSelection())
				{
					if((new File(s_textEncryptSource.getText()).isDirectory()))
					{
						s_buttonEncryptCompr.setSelection(true);
						infoMessage(s_messageNotAllowedTitle, s_messageNotCompressFolder);
					}	
				}
				else if (s_textEncryptSource.getText().endsWith(SUFFIX_ZIP))
				{
					s_buttonEncryptCompr.setSelection(false);
					infoMessage(s_messageNotAllowedTitle, s_messageCompressZip);
				}

			}
			public void widgetDefaultSelected(SelectionEvent e)
			{ 
				// method not implemented
			}
		});



		final Button buttonEncrypt = new Button(s_compositeEncrypt, SWT.NONE);
		final GridData gridDataEncryptButtons = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 2);
		gridDataEncryptButtons.heightHint = BUTTON_HEIGHT;
		gridDataEncryptButtons.widthHint = BUTTON_WIDTH;
		buttonEncrypt.setLayoutData(gridDataEncryptButtons);
		buttonEncrypt.setImage(ICON_ENCRYPT);
		buttonEncrypt.setToolTipText(s_textEncrypt);
		buttonEncrypt.addSelectionListener(new Encrypt());
		new Label(s_compositeEncrypt, SWT.NONE);

		s_buttonEncryptDelete = new Button(s_compositeEncrypt, SWT.CHECK);
		//final GridData gridData = new GridData(100, SWT.DEFAULT);
		//s_buttonEncryptDelete.setLayoutData(gridData);
		s_buttonEncryptDelete.setText(s_textButtonDeleteSrc);
		new Label(s_compositeEncrypt, SWT.NONE);
		new Label(s_compositeEncrypt, SWT.NONE);
		new Label(s_compositeEncrypt, SWT.NONE);

		final Label l_rLabel = new Label(s_compositeEncrypt, SWT.NONE);
		l_rLabel.setAlignment(SWT.RIGHT);
		final GridData gridData_1 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridData_1.heightHint = 15;
		gridData_1.widthHint = 20;
		l_rLabel.setLayoutData(gridData_1);

	}


	private void createDecryptContent()
	{
		s_compositeDecrypt = new Composite(s_composite, SWT.NONE);
		final GridLayout gridLayoutDecrypt = new GridLayout();
		gridLayoutDecrypt.numColumns = 3;
		s_compositeDecrypt.setLayout(gridLayoutDecrypt);
		final GridData gridDataDecrypt = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gridDataDecrypt.widthHint = COMPOSITE_WIDTH;
		gridDataDecrypt.heightHint = COMPOSITE_HEIGHT;
		s_compositeDecrypt.setLayoutData(gridDataDecrypt);


		// ################## Composite Decrypt, Group Source ##################

		final Group groupDecryptSource = new Group(s_compositeDecrypt, SWT.NONE);
		groupDecryptSource.setText(s_textSource);
		final GridData gridDataDecryptSource = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gridDataDecryptSource.heightHint = GROUP_HEIGHT;
		gridDataDecryptSource.widthHint = GROUP_WIDTH;
		groupDecryptSource.setLayoutData(gridDataDecryptSource);
		final GridLayout gridLayoutDecryptSource = new GridLayout();
		gridLayoutDecryptSource.numColumns = 3;
		groupDecryptSource.setLayout(gridLayoutDecryptSource);

		s_textDecryptSource = new Text(groupDecryptSource, SWT.BORDER);
		final GridData gridDataDecryptSource_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gridDataDecryptSource_1.widthHint = TEXT_WIDTH;
		s_textDecryptSource.setLayoutData(gridDataDecryptSource_1);

		final DropTarget dtDecryptSource = new DropTarget(groupDecryptSource, DND.DROP_DEFAULT | DND.DROP_MOVE );
		dtDecryptSource.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dtDecryptSource.addDropListener(new DropTargetAdapterSource(s_textDecryptSource));

		final SashForm sashFormDecryptSource = new SashForm(groupDecryptSource, SWT.NONE);
		sashFormDecryptSource.setLayoutData(new GridData(185, SWT.DEFAULT));

		new Label(groupDecryptSource, SWT.NONE);

		final Button buttonDecryptSourceFile = new Button(groupDecryptSource, SWT.NONE);
		final GridData gridDataDecryptSource_2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataDecryptSource_2.heightHint = BUTTON_HEIGHT;
		gridDataDecryptSource_2.widthHint = BUTTON_WIDTH;
		buttonDecryptSourceFile.setLayoutData(gridDataDecryptSource_2);
		buttonDecryptSourceFile.setImage(ICON_BROWSE);
		buttonDecryptSourceFile.setToolTipText(s_textBrowse);
		buttonDecryptSourceFile.addSelectionListener(new Browse(DECRYPT_SOURCE));


		// ################### Composite Decrypt, Group Target ##################

		final Group groupDecryptTarget = new Group(s_compositeDecrypt, SWT.NONE);
		final GridData gridDataDecryptTarget = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gridDataDecryptTarget.heightHint = GROUP_HEIGHT;
		gridDataDecryptTarget.widthHint = GROUP_WIDTH;
		groupDecryptTarget.setLayoutData(gridDataDecryptTarget);
		final GridLayout gridLayoutDecryptTarget = new GridLayout();
		gridLayoutDecryptTarget.numColumns = 2;
		groupDecryptTarget.setLayout(gridLayoutDecryptTarget);
		groupDecryptTarget.setText(s_textTarget);

		s_textDecryptTarget = new Text(groupDecryptTarget, SWT.BORDER);
		final GridData gridDataDecryptTarget_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gridDataDecryptTarget_1.widthHint = TEXT_WIDTH;
		s_textDecryptTarget.setLayoutData(gridDataDecryptTarget_1);

		final DropTarget dtDecryptTarget = new DropTarget(groupDecryptTarget, DND.DROP_DEFAULT | DND.DROP_MOVE );
		dtDecryptTarget.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dtDecryptTarget.addDropListener(new DropTargetAdapterTarget(s_textDecryptTarget));

		final SashForm sashFormDecryptTarget = new SashForm(groupDecryptTarget, SWT.NONE);
		sashFormDecryptTarget.setLayoutData(new GridData(314, SWT.DEFAULT));

		final Button buttonDecryptTargetFile = new Button(groupDecryptTarget, SWT.NONE);
		final GridData gridDataDecryptTarget_2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataDecryptTarget_2.heightHint = BUTTON_HEIGHT;
		gridDataDecryptTarget_2.widthHint = BUTTON_WIDTH;
		buttonDecryptTargetFile.setLayoutData(gridDataDecryptTarget_2);
		buttonDecryptTargetFile.setImage(ICON_BROWSE);
		buttonDecryptTargetFile.setToolTipText(s_textBrowse);
		buttonDecryptTargetFile.addSelectionListener(new Browse(DECRYPT_TARGET));
		new Label(s_compositeDecrypt, SWT.NONE);


		// ##################### Composite Decrypt, Buttons ####################

		final SashForm sashFormDecrypt = new SashForm(s_compositeDecrypt, SWT.NONE);
		final GridData gridDataDecryptButtonSash = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridDataDecryptButtonSash.widthHint = 5;
		gridDataDecryptButtonSash.heightHint = 50;
		sashFormDecrypt.setLayoutData(gridDataDecryptButtonSash);
		new Label(s_compositeEncrypt, SWT.NONE);
		new Label(s_compositeDecrypt, SWT.NONE);	

		s_buttonDecryptCompr = new Button(s_compositeDecrypt, SWT.CHECK);
		s_buttonDecryptCompr.setLayoutData(new GridData(255, 35));
		s_buttonDecryptCompr.setText(s_textButtonDecompr);
		s_buttonDecryptCompr.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(s_buttonDecryptCompr.getSelection())
					s_textDecryptTarget.setText((new File(s_textDecryptSource.getText())).getParent());
				else
				{
					final String fileName = s_textDecryptTarget.getText();
					if (!fileName.trim().equals("") && (new File(fileName).isDirectory()))
					{
						final String sourceName = s_textDecryptSource.getText();
						if(sourceName.endsWith(SUFFIX_FOLDER))
							s_textDecryptTarget.setText(sourceName.substring(0, sourceName.length() - SUFFIX_FOLDER.length()) + SUFFIX_ZIP);
					}
				}
			}
			public void widgetDefaultSelected(SelectionEvent e)
			{ 
				// method not implemented
			}
		});

		final Button buttonDecrypt = new Button(s_compositeDecrypt, SWT.NONE);
		final GridData gridDataDecryptButtons = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 2);
		gridDataDecryptButtons.heightHint = BUTTON_HEIGHT;
		gridDataDecryptButtons.widthHint = BUTTON_WIDTH;
		buttonDecrypt.setLayoutData(gridDataDecryptButtons);
		buttonDecrypt.setImage(ICON_DECRYPT);
		buttonDecrypt.setToolTipText(s_textDecrypt);
		buttonDecrypt.addSelectionListener(new Decrypt());
		new Label(s_compositeDecrypt, SWT.NONE);

		s_buttonDecryptDelete = new Button(s_compositeDecrypt, SWT.CHECK);
		//final GridData gridData = new GridData(100, SWT.DEFAULT);
		//s_buttonEncryptDelete.setLayoutData(gridData);
		s_buttonDecryptDelete.setText(s_textButtonDeleteSrc);

		new Label(s_compositeDecrypt, SWT.NONE);	
		new Label(s_compositeDecrypt, SWT.NONE);
		new Label(s_compositeDecrypt, SWT.NONE);

		final Label l_rLabel = new Label(s_compositeDecrypt, SWT.NONE);
		l_rLabel.setAlignment(SWT.RIGHT);
		final GridData gridData_1 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridData_1.heightHint = 15;
		gridData_1.widthHint = 20;
		l_rLabel.setLayoutData(gridData_1);
	}

	private void createShrederContent()
	{
		s_compositeShred = new Composite(s_composite, SWT.NONE);
		final GridLayout gridLayoutShred = new GridLayout();
		gridLayoutShred.numColumns = 3;
		s_compositeShred.setLayout(gridLayoutShred);
		final GridData gridDataShred = new GridData(SWT.CENTER, SWT.CENTER, false, false);
		gridDataShred.widthHint = COMPOSITE_WIDTH;
		gridDataShred.heightHint = COMPOSITE_HEIGHT;
		s_compositeShred.setLayoutData(gridDataShred);

		// ################### Composite Shred, Group Source ##################
		final Group groupShred = new Group(s_compositeShred, SWT.NONE);
		groupShred.setText(s_textSource);
		final GridData gridDataShredSource = new GridData(SWT.LEFT, SWT.CENTER, true, false, 3, 1);
		gridDataShredSource.heightHint = GROUP_HEIGHT;
		gridDataShredSource.widthHint = GROUP_WIDTH;
		groupShred.setLayoutData(gridDataShredSource);
		final GridLayout gridLayoutShredSource = new GridLayout();
		gridLayoutShredSource.numColumns = 3;
		groupShred.setLayout(gridLayoutShredSource);

		s_textShredSource = new Text(groupShred, SWT.BORDER);
		final GridData gridDataShred_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gridDataShred_1.widthHint = TEXT_WIDTH;
		s_textShredSource.setLayoutData(gridDataShred_1);

		final DropTarget dtShred = new DropTarget(s_compositeShred, DND.DROP_DEFAULT | DND.DROP_MOVE );
		dtShred.setTransfer(new Transfer[] { FileTransfer.getInstance() });
		dtShred.addDropListener(new DropTargetAdapterSource(s_textShredSource));

		final Button buttonShredFile = new Button(groupShred, SWT.RADIO);
		final GridData gridDataShred_2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataShred_2.heightHint = BUTTON_HEIGHT;
		gridDataShred_2.widthHint = BUTTON_WIDTH;
		buttonShredFile.setLayoutData(gridDataShred_2);
		buttonShredFile.setImage(ICON_FILE);
		buttonShredFile.setToolTipText(s_textFile);
		buttonShredFile.setSelection(true);
		buttonShredFile.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(buttonShredFile.getSelection())
				{
					LOGGER.debug("buttonShredFile is selected");	
					s_shredFiletype = FILE;
				}
			}
			public void widgetDefaultSelected(SelectionEvent e)
			{ 
				// method not implemented
			}
		});

		final Button buttonShredFolder = new Button(groupShred, SWT.RADIO);
		final GridData gridDataShred_3 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataShred_3.heightHint = BUTTON_HEIGHT;
		gridDataShred_3.widthHint = BUTTON_WIDTH;
		buttonShredFolder.setLayoutData(gridDataShred_3);
		buttonShredFolder.setImage(ICON_FOLDER);
		buttonShredFolder.setToolTipText(s_textFolder);
		buttonShredFolder.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if(buttonShredFolder.getSelection())
				{
					LOGGER.debug("buttonShredFolder is selected");	
					s_shredFiletype = FOLDER;
				}
			}
			public void widgetDefaultSelected(SelectionEvent e)
			{ 
				// method not implemented
			}
		});

		final Button buttonShredBrowse = new Button(groupShred, SWT.NONE);
		final GridData gridDataShred_4 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		gridDataShred_4.heightHint = BUTTON_HEIGHT;
		gridDataShred_4.widthHint = BUTTON_WIDTH;
		buttonShredBrowse.setLayoutData(gridDataShred_4);
		buttonShredBrowse.setImage(ICON_BROWSE);
		buttonShredBrowse.setToolTipText(s_textBrowse);
		buttonShredBrowse.addSelectionListener(new Browse(SHRED_SOURCE));
		new Label(s_compositeShred, SWT.NONE);
		new Label(s_compositeShred, SWT.NONE);


		//		 #################### Composite Shred, Group Options ###################

		final Group groupShredOptions = new Group(s_compositeShred, SWT.NONE);
		groupShredOptions.setText(s_textShredOptions);
		final GridData gridDataShredOptions = new GridData(SWT.LEFT, SWT.TOP, true, false, 3, 1);
		gridDataShredOptions.heightHint = GROUP_HEIGHT + 20;
		gridDataShredOptions.widthHint = GROUP_WIDTH;
		groupShredOptions.setLayoutData(gridDataShredOptions);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		groupShredOptions.setLayout(gridLayout);

		final Button shredZeroes = new Button(groupShredOptions, SWT.RADIO);
		//shredZeroes.setLayoutData(new GridData(143, SWT.DEFAULT)); 
		/* removed, because with the code it means width=146, height=SWT.DEFAULT 
			==> with the edit it fix the sizing-Problem in Linux
		 */
		shredZeroes.setText(s_textshredZeroes);
		shredZeroes.setToolTipText(s_textShredRepetitions + ": 1x");

		final Button shredSchneier = new Button(groupShredOptions, SWT.RADIO);
		//shredSchneier.setLayoutData(new GridData(143, SWT.DEFAULT));
		shredSchneier.setText(s_textShredSchneier);
		shredSchneier.setToolTipText(s_textShredRepetitions + ": 7x");

		final Button shredDoD = new Button(groupShredOptions, SWT.RADIO);
		//shredDoD.setLayoutData(new GridData(143, SWT.DEFAULT));
		shredDoD.setText(s_textShredDoD);
		shredDoD.setToolTipText(s_textShredRepetitions + ": 3x");

		final Button shredGutmannFloppy = new Button(groupShredOptions, SWT.RADIO);
		//shredGutmannFloppy.setLayoutData(new GridData(143, SWT.DEFAULT));
		shredGutmannFloppy.setText(s_textShredGutmannFloppy);
		shredGutmannFloppy.setToolTipText(s_textShredRepetitions + ": 18x");

		final Button shredDoDext = new Button(groupShredOptions, SWT.RADIO);
		//shredDoDext.setLayoutData(new GridData(SWT.DEFAULT));
		shredDoDext.setText(s_textShredDoDext);
		shredDoDext.setToolTipText(s_textShredRepetitions + ": 7x");

		final Button shredGutmann = new Button(groupShredOptions, SWT.RADIO);
		//shredGutmann.setLayoutData(new GridData(143, SWT.DEFAULT));
		shredGutmann.setText(s_textShredGutmann);
		shredGutmann.setToolTipText(s_textShredRepetitions + ": 35x");

		final Button shredVSITR = new Button(groupShredOptions, SWT.RADIO);
		//shredVSITR.setLayoutData(new GridData(143, SWT.DEFAULT));
		shredVSITR.setText(s_textShredVSITR);
		shredVSITR.setToolTipText(s_textShredRepetitions + ": 7x");

		final Button shredRandom = new Button(groupShredOptions, SWT.RADIO);
		//shredRandom.setLayoutData(new GridData(143, SWT.DEFAULT));
		shredRandom.setText(s_textShredRandom);
		shredRandom.setToolTipText(s_textShredRepetitions + ": 10x");


		switch(s_deleteDefault)
		{
		case SHRED_ALGO_ZEROES: shredZeroes.setSelection(true);
		break;
		case SHRED_ALGO_DOD: shredDoD.setSelection(true);
		break;
		case SHRED_ALGO_DOD_EXT: shredDoDext.setSelection(true);
		break;
		case SHRED_ALGO_VSITR: shredVSITR.setSelection(true);
		break;
		case SHRED_ALGO_SCHNEIER: shredSchneier.setSelection(true);
		break;
		case SHRED_ALGO_GUTMANN_FLOPPY: shredGutmannFloppy.setSelection(true);
		break;
		case SHRED_ALGO_GUTMANN: shredGutmann.setSelection(true);
		break;
		default: shredRandom.setSelection(true);
		}



		// ###################### Composite Shred, Buttons #####################

		final Button buttonShredDeleteRoot = new Button(s_compositeShred, SWT.LEFT | SWT.CHECK);
		buttonShredDeleteRoot.setText(s_textShredLeaveDir);
		buttonShredDeleteRoot.setLayoutData(new GridData(260, 55));

		final Button buttonShred = new Button(s_compositeShred, SWT.NONE);
		final GridData gridData = new GridData(SWT.RIGHT, SWT.BOTTOM, true, false, 1, 2);
		gridData.heightHint = BUTTON_HEIGHT;
		gridData.widthHint = BUTTON_WIDTH;
		buttonShred.setLayoutData(gridData);
		buttonShred.setImage(ICON_SHRED);
		buttonShred.setToolTipText(s_textShred);
		buttonShred.addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				int shredAlgo = SHRED_ALGO_DEFAULT;
				final String filePath = s_textShredSource.getText();
				boolean leaveRoot = false;

				if(buttonShredDeleteRoot.getSelection())
					leaveRoot = true;
				if (shredZeroes.getSelection())
					shredAlgo = 0;
				else if (shredDoD.getSelection())
					shredAlgo = 1;
				else if (shredDoDext.getSelection())
					shredAlgo = 2;
				else if (shredVSITR.getSelection())
					shredAlgo = 3;
				else if (shredSchneier.getSelection())
					shredAlgo = 4;
				else if (shredGutmannFloppy.getSelection())
					shredAlgo = 5;
				else if (shredGutmann.getSelection())
					shredAlgo = 6;
				else if (shredRandom.getSelection())
					shredAlgo = 7;

				s_shell.setCursor(new Cursor(s_display,SWT.CURSOR_WAIT));
				shred(filePath, shredAlgo, leaveRoot);
				s_shell.setCursor(null);
			}

			public void widgetDefaultSelected(SelectionEvent e)
			{
				// method not implemented
			}
		});
		new Label(s_compositeShred, SWT.NONE);

		s_labelDeletion = new Label(s_compositeShred, SWT.LEFT);
		final GridData gridDataShredDeletion = new GridData(SWT.FILL, SWT.CENTER, false, true);
		gridDataShredDeletion.heightHint = 34;
		gridDataShredDeletion.widthHint = 260;
		s_labelDeletion.setLayoutData(gridDataShredDeletion);
		new Label(s_compositeShred, SWT.NONE);

		final Label l_uLabel = new Label(s_compositeShred, SWT.NONE);
		final GridData gridData_2 = new GridData(SWT.LEFT, SWT.FILL, false, false);
		gridData_2.heightHint = 10;
		l_uLabel.setLayoutData(gridData_2);
		new Label(s_compositeShred, SWT.NONE);

		final Label l_rLabel = new Label(s_compositeShred, SWT.NONE);
		final GridData gridData_1 = new GridData(SWT.RIGHT, SWT.FILL, false, false);
		gridData_1.heightHint = 13;
		gridData_1.widthHint = 20;
		l_rLabel.setLayoutData(gridData_1);
	}


	private void createPasswordsContent()
	{
		final Clipboard cb = new Clipboard(s_display);
		s_compositePasswords = new Composite(s_composite, SWT.NONE);
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
		item.setText(s_pwmRenameContextMenu);
		item.addListener (SWT.Selection, new Listener(){
			public void handleEvent (Event event) {
				final TreeItem[] t = tree.getSelection();
				String idToRename = t[0].getText();

				Properties renameProps = new Properties(s_langProps);
				
				renameProps.put("PWM.TID_Title", s_langProps.get("PWM.RENAME_OK_TITLE"));
				renameProps.put("PWM.TID_OkButton", s_langProps.get("PWM.RENAME_CONTEXT_MENU"));
				renameProps.put("PWM.TID_labelText", s_langProps.get("PWM.RENAME_NEW_NAME"));
				
				
				final TextInputDialog tid = new TextInputDialog(s_shell, renameProps);
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
							
							infoMessage(s_pwmRenameOkTitle, s_pwmRenameOkMessage );	
						} 
						catch (EntryDoesNotExistException e) 
						{
							LOGGER.warn(e.getMessage());
							errorMessage(s_pwmRenameErrorTitle, s_pwmRenameErrorNotFound);
						} 
						catch (EntryAlreadyExistException e) 
						{
							LOGGER.warn(e.getMessage());
							errorMessage(s_pwmRenameErrorTitle, s_pwmRenameErrorAlreadyExist);
						} 
						catch (StorageException e) 
						{
							LOGGER.warn(e.getMessage());
							errorMessage(s_pwmRenameErrorTitle, s_pwmRenameErrorSave);
						}
					}
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

		passwordManagerTitle = new Label(composite, SWT.NONE);
		final GridData gridData_4 = new GridData(SWT.FILL, SWT.FILL, false, false, 3, 1);
		gridData_4.heightHint = 29;
		gridData_4.widthHint = 20;
		passwordManagerTitle.setLayoutData(gridData_4);
		passwordManagerTitle.setText(s_passwordManagerTitle);
		passwordManagerTitle.setFont(s_fontMiddle);

		groupLabel = new Label(composite, SWT.NONE);
		groupLabel.setText(s_groupLabelText + ":");

		comboGroup = new Combo(composite, SWT.NONE);
		comboGroup.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		new Label(composite, SWT.NONE);

		accountIdLabel = new Label(composite, SWT.NONE);
		accountIdLabel.setText(s_accountIdLabelText + ":");

		textAccountID = new Text(composite, SWT.BORDER);
		textAccountID.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		new Label(composite, SWT.NONE);

		usernameLabel = new Label(composite, SWT.NONE);
		usernameLabel.setText(s_usernameLabelText + ":");

		textUsername = new Text(composite, SWT.BORDER);
		textUsername.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));


		final Composite usernameCopy = new Composite(composite, SWT.NONE);
		usernameCopy.setLayout(new FillLayout());
		usernameCopy.setLayoutData(new GridData(25, 22));

		copyUsernameButton = new Button(usernameCopy, SWT.FLAT);
		copyUsernameButton.setImage(ICON_COPY);
		copyUsernameButton.setToolTipText(s_copyUsernameButtonTip );
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

		passwordLabel = new Label(composite, SWT.NONE);
		passwordLabel.setText(s_passwordLabelText + ":");


		textPassword = new Text(composite, SWT.BORDER);
		textPassword.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		s_EchoChar = textPassword.getEchoChar();
		//System.out.print((int)s_EchoChar);
		textPassword.setEchoChar('*');


		m_pwCopyStack = new Composite(composite, SWT.NONE);
		s_pwCopyStackLayout = new StackLayout();
		m_pwCopyStack.setLayout(s_pwCopyStackLayout);
		m_pwCopyStack.setLayoutData(new GridData(25, 22));

		copyPasswordButton = new Button(m_pwCopyStack, SWT.FLAT);
		copyPasswordButton.setImage(ICON_COPY);
		copyPasswordButton.setSize(18, 18);
		copyPasswordButton.setToolTipText(s_copyPasswordButtonTip );

		generatePassword = new Button(m_pwCopyStack, SWT.FLAT);
		generatePassword.setImage(ICON_PWGen);
		generatePassword.setToolTipText(s_generatePasswordTip );
		generatePassword.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent arg0) 
			{
				// open PWGeneratorGUI to generate passwords
				final PWGeneratorGUI window = new PWGeneratorGUI(s_shell, s_langProps);
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

		linkLabel = new Label(composite, SWT.NONE);
		linkLabel.setLayoutData(new GridData());
		linkLabel.setText(s_linkLabelText + ":");

		textLink = new Text(composite, SWT.BORDER);
		textLink.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));

		openLinkButton = new Button(composite, SWT.FLAT);
		openLinkButton.setLayoutData(new GridData(25, 22));
		openLinkButton.setImage(ICON_WEB);
		openLinkButton.setToolTipText(s_openLinkButtonTip );
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

		commentLabel = new Label(composite, SWT.NONE);
		commentLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		commentLabel.setText(s_commentLabelText + ":");

		textComment = new Text(composite, SWT.BORDER|SWT.WRAP);
		final GridData gridData_3 = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gridData_3.heightHint = 210;
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
		createGroup.setToolTipText(s_createGroupTip );
		deleteGroup = new Button(compositeTreeItemActions, SWT.FLAT);
		deleteGroup.setImage(ICON_DELGROUP);
		deleteGroup.setToolTipText(s_deleteGroupTip );
		createUser = new Button(compositeTreeItemActions, SWT.FLAT);
		createUser.setImage(ICON_ADDACCOUNT);
		createUser.setToolTipText(s_createUserTip );
		deleteUser = new Button(compositeTreeItemActions, SWT.FLAT);
		deleteUser.setImage(ICON_DELACCOUNT);
		deleteUser.setToolTipText(s_deleteUserTip );
		final Composite composite_1 = new Composite(s_compositePasswords, SWT.NONE);
		final GridLayout gridLayout_2 = new GridLayout();
		gridLayout_2.numColumns = 3;
		composite_1.setLayout(gridLayout_2);
		composite_1.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		editButton = new Button(composite_1, SWT.FLAT);
		editButton.setText(s_editButtonText );

		editcancelButton = new Button(composite_1, SWT.FLAT);
		editcancelButton.setText(s_editcancelButtonText );
		editcancelButton.setVisible(true);
		editcancelButton.setEnabled(false);

		saveButton = new Button(composite_1, SWT.FLAT);
		saveButton.setText(s_saveButtonText );
		saveButton.setVisible(true);
		saveButton.setEnabled(false);

		changePWManagerFields(false);
		editButton.setEnabled(false);

		/*
		 *  When editable and ESC pressed, change to none editable....
		 * 		composite_1.addListener(SWT.Traverse, new Listener() {
			public void handleEvent (Event event) 
			{
				if(event.detail == SWT.TRAVERSE_ESCAPE){
					if(s_allowAccountchange){
						LOGGER.debug("ESC pressed - fields are no more editable");
						changePWManagerFields(false);
					}
				}
			}
		});*/



		// check if accountFile exist. If not, ask if it should be created
		final File accountFile = new File(s_accountFile);

		if (! accountFile.exists())
		{
			if(questMessage(s_pPwmError, s_pFileMissing))
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
								errorMessage(s_pAttention, s_entryDoesNotExist);
								s_pwMSwtHelper.createTree(tree,s_pwManager.getAllEntries());;
							}


						}
					}
					else
						warningMessage(s_pAttention, s_pFirstEndUserMod );
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
						errorMessage(s_error, s_pSelectGroupBeforeDelete);
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
								errorMessage(s_pAttention, s_entryDoesNotExist);
								s_pwMSwtHelper.createTree(tree,s_pwManager.getAllEntries());;
							}



						}
						else
							infoMessage(s_pAttention, s_pSelectAccount);
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
					final TextInputDialog tid = new TextInputDialog(s_shell, s_langProps);
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
								warningMessage(s_pAttention, s_pGroupExists);
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
									errorMessage(s_error, s_pGroupCreateError
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
						errorMessage(s_error, s_pSelectGroupBeforeDelete);
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

								if(questMessage(s_pAttention, s_pRealyDeleteGroup
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
								errorMessage(s_error, s_pElementDeleteFailed 
										+" '"+ selectedElement+"'");
						}
						// not a group element or have childs
						else							
							errorMessage(s_error, s_pOnlyEmptyGroupsDelete);
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
						errorMessage(s_error, s_pSelectAccountBeforeDelete);
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

								if(questMessage(s_pAttention, s_pRealyDeleteAccount
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
								errorMessage(s_error, s_pElementDeleteFailed );
						}
						else
						{
							// not a group element or has childs
							errorMessage(s_error, s_pOnlyAccountDelete);
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
						warningMessage(s_error, s_pAIDnotEmpty);
						allSaveOK = false;
					}
					else if (textAccountID.getText().trim().equals(""))
					{
						warningMessage(s_error, s_pAIDnotEmpty);
						allSaveOK = false;
					}
					else 	
					{
						// Accound ID is set
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
									errorMessage(s_error, s_pErrorElementChange);
									allSaveOK = false;
								}
							}
							else
							{
								errorMessage(s_error, s_pErrorElementChange);
								allSaveOK = false;
							}
						}
						else
						{
							if (s_pwManager.existID(textAccountID.getText().trim()))
							{
								warningMessage(s_error, s_pErrorAIDexist);
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

	private void createConfigContent()
	{
		s_compositeConfig = new Composite(s_composite, SWT.NONE);
		final GridLayout gridLayoutConfig = new GridLayout();
		gridLayoutConfig.numColumns = 2;
		s_compositeConfig.setLayout(gridLayoutConfig);
		final GridData gridDataConfig = new GridData(SWT.CENTER, SWT.TOP, false, false);
		gridDataConfig.widthHint = COMPOSITE_WIDTH;
		gridDataConfig.heightHint = COMPOSITE_HEIGHT;
		s_compositeConfig.setLayoutData(gridDataConfig);

		final Group groupApplConfig = new Group(s_compositeConfig, SWT.NONE);
		groupApplConfig.setText(s_textGroupApplConfig);
		final GridData gridDataConfigAppl = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gridDataConfigAppl.heightHint = 65; //GROUP_HEIGHT;
		gridDataConfigAppl.widthHint = GROUP_WIDTH;
		groupApplConfig.setLayoutData(gridDataConfigAppl);
		final GridLayout gridLayoutConfigAppl = new GridLayout();
		gridLayoutConfigAppl.numColumns = 2;
		groupApplConfig.setLayout(gridLayoutConfigAppl);

		final Label label_3 = new Label(groupApplConfig, SWT.LEFT);
		label_3.setLayoutData(new GridData(163, 20));
		label_3.setText(s_textLogLevelLabel + ":");

		s_configLogCombo = new Combo(groupApplConfig, SWT.DROP_DOWN | SWT.READ_ONLY);
		final GridData configLogComboGridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		configLogComboGridData.widthHint = 163 - 16;
		s_configLogCombo.setLayoutData(configLogComboGridData);
		s_configLogCombo.setItems(new String [] {"FATAL", "WARN", "INFO", "DEBUG"});
		s_configLogCombo.select(s_logLevel);
		s_configLogCombo.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (s_logLevel != s_configLogCombo.getSelectionIndex())
				{
					s_logLevel = s_configLogCombo.getSelectionIndex();
					changeLogLevel();
				}
			}
		});

		final Label label_11 = new Label(groupApplConfig, SWT.LEFT);
		label_11.setLayoutData(new GridData(163, 20));
		label_11.setText(s_textLogFile + ":");

		final Link link = new Link(groupApplConfig, SWT.NONE);
		link.setLayoutData(new GridData(270, 20));
		link.setText("<a href=\"" + LOG_FILE + "\">" + LOG_FILE_SHORT + "</a>");
		link.setToolTipText(LOG_FILE);
		link.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {				
				final Program p = Program.findProgram (".txt");

				if (p != null) 
					Program.launch(event.text);
				else
					infoMessage(s_messageNoTxtProgTitle, s_messageNoTxtProg);
			}
		});



		final Group groupUserConfig = new Group(s_compositeConfig, SWT.NONE);
		groupUserConfig.setText(s_textGroupUserConfig + " '" + s_user + "'");
		final GridData gridDataConfigSystem = new GridData(SWT.LEFT, SWT.TOP, true, true, 2, 1);
		gridDataConfigSystem.heightHint = 220;
		gridDataConfigSystem.widthHint = GROUP_WIDTH;
		groupUserConfig.setLayoutData(gridDataConfigSystem);
		final GridLayout gridLayoutConfigSystem = new GridLayout();
		gridLayoutConfigSystem.numColumns = 2;
		groupUserConfig.setLayout(gridLayoutConfigSystem);

		final Label label_23 = new Label(groupUserConfig, SWT.LEFT);
		label_23.setLayoutData(new GridData(163, SWT.DEFAULT));
		label_23.setText(s_textLanguage + ":");

		s_configLangCombo = new Combo(groupUserConfig, SWT.DROP_DOWN | SWT.READ_ONLY);
		final GridData configLangComboGridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		configLangComboGridData.widthHint = 163 - 16;
		s_configLangCombo.setLayoutData(configLangComboGridData);
		s_configLangCombo.setItems(new String [] {s_textGerman, s_textFrench, s_textEnglish});
		s_configLangCombo.select(s_lang);
		s_configLangCombo.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (s_lang != s_configLangCombo.getSelectionIndex())
				{
					s_lang = s_configLangCombo.getSelectionIndex();
					LOGGER.debug("change user language");
					changeUserLang();
				}
			}
		});

		final Label labelConfigDelete = new Label(groupUserConfig, SWT.LEFT);
		labelConfigDelete.setLayoutData(new GridData(163, SWT.DEFAULT));
		labelConfigDelete.setText(s_textDeleteAlgorithm + ":");

		final Combo comboConfigDelete = new Combo(groupUserConfig, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboConfigDelete.setLayoutData(configLangComboGridData);
		comboConfigDelete.setItems(new String [] {s_textshredZeroes, s_textShredDoD, 
				s_textShredDoDext, s_textShredVSITR, s_textShredSchneier, 
				s_textShredGutmannFloppy, s_textShredGutmann, s_textShredRandom});
		comboConfigDelete.select(s_deleteDefault);
		comboConfigDelete.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (s_deleteDefault != comboConfigDelete.getSelectionIndex()){
					s_deleteDefault = comboConfigDelete.getSelectionIndex();
					LOGGER.debug("change deletion algorithm for encryption");
					s_userProps.setProperty(DEFAULT_DELETE_KEY, "" + s_deleteDefault);
					saveUserProps();
					//changeUserLang();
				}
			}
		});

		final Label label_21 = new Label(groupUserConfig, SWT.LEFT);
		label_21.setLayoutData(new GridData(163, SWT.DEFAULT));
		label_21.setText(s_textPasswordOld + ":");

		s_configPwd = new Text(groupUserConfig, SWT.LEFT | SWT.BORDER);
		s_configPwd.setLayoutData(new GridData(163, SWT.DEFAULT));
		s_configPwd.setEchoChar('*');

		final Label label_22 = new Label(groupUserConfig, SWT.LEFT);
		label_22.setLayoutData(new GridData(163, SWT.DEFAULT));
		label_22.setText(s_textPasswordNew + ":");

		s_configPwdNew = new Text(groupUserConfig, SWT.LEFT | SWT.BORDER);
		s_configPwdNew.setLayoutData(new GridData(163, SWT.DEFAULT));
		s_configPwdNew.setEchoChar('*');

		final Label label_27 = new Label(groupUserConfig, SWT.LEFT);
		label_27.setLayoutData(new GridData(163, SWT.DEFAULT));
		label_27.setText(s_textPasswordNewRep + ":");

		s_configPwdNewRep = new Text(groupUserConfig, SWT.LEFT | SWT.BORDER);
		s_configPwdNewRep.setLayoutData(new GridData(163, SWT.DEFAULT));
		s_configPwdNewRep.setEchoChar('*');

		final Label label_28 = new Label(groupUserConfig, SWT.LEFT);
		label_28.setLayoutData(new GridData(163, SWT.DEFAULT));

		final Button buttonSavePwd = new Button(groupUserConfig, SWT.CENTER);
		buttonSavePwd.setLayoutData(new GridData(156, SWT.DEFAULT));
		buttonSavePwd.setText(s_textButtonChangePwd);
		buttonSavePwd.addSelectionListener(new ChangePwdButton());

		// Network Group

		final Group groupNetworkConfig = new Group(s_compositeConfig, SWT.NONE);
		final GridData gridDataNetworkConfig = new GridData(SWT.LEFT, SWT.BOTTOM, true, false);
		gridDataNetworkConfig.heightHint = 100;
		gridDataNetworkConfig.widthHint = GROUP_WIDTH;
		groupNetworkConfig.setLayoutData(gridDataNetworkConfig);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		groupNetworkConfig.setLayout(gridLayout);
		groupNetworkConfig.setText(s_NETWORK_GROUP_TITLE);

		buttonProxyNo = new Button(groupNetworkConfig, SWT.RADIO);
		buttonProxyNo.setText(s_NETWORK_PROXY_NO);
		new Label(groupNetworkConfig, SWT.NONE);

		buttonProxyYes = new Button(groupNetworkConfig, SWT.RADIO);
		buttonProxyYes.setText(s_NETWORK_PROXY_YES+":");

		final Composite compositeNetworkAddress = new Composite(groupNetworkConfig, SWT.NONE);
		final GridLayout gridDataNetworkAddress = new GridLayout();
		gridDataNetworkAddress.numColumns = 3;
		compositeNetworkAddress.setLayout(gridDataNetworkAddress);
		compositeNetworkAddress.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));

		s_networkHost = new Text(compositeNetworkAddress, SWT.BORDER);
		final GridData gd_text_1 = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_text_1.widthHint = 82;
		s_networkHost.setLayoutData(gd_text_1);
		s_networkHost.setText(s_textNetworkHost);

		final Label label = new Label(compositeNetworkAddress, SWT.NONE);
		label.setText(":");

		s_networkPort = new Text(compositeNetworkAddress, SWT.BORDER);
		final GridData gd_text_2 = new GridData(SWT.FILL, SWT.CENTER, false, false);
		gd_text_2.widthHint = 25;
		s_networkPort.setLayoutData(gd_text_2);
		s_networkPort.setText(s_textNetworkPort);

		final Label label_21_1 = new Label(groupNetworkConfig, SWT.NONE);
		label_21_1.setLayoutData(new GridData(163, SWT.DEFAULT));

		final Button buttonSaveNetwork = new Button(groupNetworkConfig, SWT.NONE);
		buttonSaveNetwork.setLayoutData(new GridData(156, SWT.DEFAULT));
		buttonSaveNetwork.setText(s_NETWORK_SAVE_BUTTON);


		buttonSaveNetwork.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				String tmpHost = s_networkHost.getText();
				String tmpPort = s_networkPort.getText();

				if(tmpHost != null && tmpHost.trim().length() > 0)
				{
					tmpHost = tmpHost.trim();
				}
				else
				{
					tmpHost = "";
				}

				if(tmpPort != null && tmpPort.trim().length() > 0)
				{
					tmpPort = tmpPort.trim();
				}
				else
				{
					tmpPort = "";
				}

				if(buttonProxyNo.getSelection())
				{
					// no Proxy
					s_networkProps.setProperty("ATARAXIS.NETWORK.USE_PROXY","FALSE");

				}
				else
				{
					// Proxy
					s_networkProps.setProperty("ATARAXIS.NETWORK.USE_PROXY","TRUE");
				}
				s_networkProps.setProperty("ATARAXIS.NETWORK.HOST",tmpHost);
				s_networkProps.setProperty("ATARAXIS.NETWORK.PORT",tmpPort);


				saveNetworkProps();
			}
		});

	}

	private void createInfoContent() 
	{
		s_compositeInfo = new Composite(s_composite, SWT.NONE);
		final GridLayout gridLayoutInfo = new GridLayout();
		gridLayoutInfo.numColumns = 2;
		s_compositeInfo.setLayout(gridLayoutInfo);
		final GridData gridDataInfo = new GridData(SWT.CENTER, SWT.TOP, false, false);
		gridDataInfo.widthHint = COMPOSITE_WIDTH;
		gridDataInfo.heightHint = COMPOSITE_HEIGHT;
		s_compositeInfo.setLayoutData(gridDataInfo);

		final Label label_01 = new Label(s_compositeInfo, SWT.CENTER);
		final GridData gridData_2 = new GridData(SWT.LEFT, SWT.BOTTOM, false, true, 1, 2);
		gridData_2.heightHint = 90;
		gridData_2.widthHint = 143;
		label_01.setLayoutData(gridData_2);
		label_01.setImage(ICON_ATARAXIS);

		final Label label_0 = new Label(s_compositeInfo, SWT.LEFT);
		final GridData gridData_1 = new GridData(143, 10);
		label_0.setLayoutData(gridData_1);

		final Label label_1 = new Label(s_compositeInfo, SWT.NONE);
		label_1.setLayoutData(new GridData(298, 50));
		label_1.setText("AtaraxiS");
		label_1.setFont(s_fontBig);

		final Group groupAppl = new Group(s_compositeInfo, SWT.NONE);
		groupAppl.setText(s_textGroupApplInfo);
		final GridData gridDataInfoAppl = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gridDataInfoAppl.heightHint = GROUP_HEIGHT + 80;
		gridDataInfoAppl.widthHint = GROUP_WIDTH;
		groupAppl.setLayoutData(gridDataInfoAppl);
		final GridLayout gridLayoutInfoAppl = new GridLayout();
		gridLayoutInfoAppl.numColumns = 2;
		groupAppl.setLayout(gridLayoutInfoAppl);

		final Label label_3 = new Label(groupAppl, SWT.LEFT);
		label_3.setLayoutData(new GridData(143, 20));
		label_3.setText(s_textAuthor + ":");

		final Label label_4 = new Label(groupAppl, SWT.LEFT);
		label_4.setLayoutData(new GridData(290, 20));
		label_4.setText(AUTHOR);

		final Label label_5 = new Label(groupAppl, SWT.LEFT);
		label_5.setLayoutData(new GridData(143, 20));
		label_5.setText(s_textLicense + ":");

		final Button licenseButton = new Button(groupAppl,SWT.NONE);
		licenseButton.setLayoutData(new GridData(290, 25));
		licenseButton.setText("EUPL");

		licenseButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				s_shell.setCursor(new Cursor(s_display,SWT.CURSOR_WAIT));

				final Program progPDF = Program.findProgram (".pdf");
				final Program progTXT = Program.findProgram (".txt");
				
				if (progPDF != null)
				{
					Program.launch(APPL_DIR +"/LICENCE.pdf");
				}
				else if (progTXT != null)
				{
					Program.launch(APPL_DIR +"/LICENCE.txt");
				}
				
				s_shell.setCursor(null);
			}
		});

		
		final Label label_7 = new Label(groupAppl, SWT.LEFT);
		label_7.setLayoutData(new GridData(143, 20));
		label_7.setText(s_textVersion + ":");

		final Label label_8 = new Label(groupAppl, SWT.LEFT);
		label_8.setLayoutData(new GridData(290, 20));
		label_8.setText(VERSION + "    (" + DATE + ")");

		final Label label_11 = new Label(groupAppl, SWT.LEFT);
		label_11.setLayoutData(new GridData(143, 20));
		label_11.setText(s_textWebsite + ":");

		final Link link = new Link(groupAppl, SWT.NONE);
		link.setLayoutData(new GridData(290, 20));
		link.setText("<a>" + WEBSITE + "</a>");
		link.addListener(SWT.Selection, new Listener() 
		{
			public void handleEvent(Event event) 
			{				
				final Program p = Program.findProgram (".html");
				if (p != null)
					Program.launch(event.text);
			}
		});

		final Label label_9 = new Label(groupAppl, SWT.LEFT);
		final GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gridData.heightHint = 20;
		gridData.widthHint = 143;
		label_9.setLayoutData(gridData);
		label_9.setText(s_textDescription + ":");

		final Label label_10 = new Label(groupAppl, SWT.LEFT | SWT.WRAP);
		label_10.setLayoutData(new GridData(290, 47));

		String langStr = "";
		switch(s_lang)
		{
		case GERMAN: langStr = "de"; break;
		case FRENCH: langStr = "fr"; break;
		case ENGLISH: langStr = "en"; break;
		default: langStr = "en";
		}


		if(langStr.equals("de"))
		{
			label_10.setText("Dieses Programm ist eine Weiterentwicklung unserer Diplomarbeit an der HTI Biel.");
		}
		else if(langStr.equals("fr"))
		{
			label_10.setText("Ce programme  est un d\u00E9veloppement au base de notre travail de dipl\u00F4me \u00E0 la HESB-TI Bienne. ");
		}
		else
		{
			label_10.setText("This application is the result of the diploma work at the Bern university of applied sciences HTI Biel.");
		}

		final Label label_update = new Label(groupAppl, SWT.LEFT);
		final GridData gridDataUpdate = new GridData(SWT.LEFT, SWT.TOP, false, false);
		gridData.heightHint = 20;
		gridData.widthHint = 143;
		label_update.setLayoutData(gridDataUpdate);
		label_update.setText(" ");

		final Button updateButton = new Button(groupAppl,SWT.NONE);
		updateButton.setLayoutData(new GridData(290, 25));
		updateButton.setText(s_UPDATE_CHECK);

		updateButton.addSelectionListener(new SelectionAdapter()
		{

			public void widgetSelected(SelectionEvent e)
			{
				s_shell.setCursor(new Cursor(s_display,SWT.CURSOR_WAIT));

				AtaraxisUpdateInfo updateInfo = new AtaraxisUpdateInfo();
				try {
					boolean existNewer = updateInfo.existNewerVersion(VERSION);
					if(existNewer)
					{
						LOGGER.info("A newer version exist");

						final MessageBox messageBox = new MessageBox(s_shell, SWT.ICON_INFORMATION | SWT.YES | SWT.NO);
						messageBox.setMessage("AtaraxiS "+updateInfo.getCurrentVersion()+" "+s_UPDATE_LOAD_NEW_VERSION);
						messageBox.setText(s_UPDATE_BOXTITLE);
						int downloadIt = messageBox.open();

						if(downloadIt == SWT.YES)
						{
							LOGGER.debug("Try to open website in browser");
							final Program p = Program.findProgram ("html");
							if (p != null) {
								Program.launch(updateInfo.getCurrentURL());
							}
						}

					}
					else
					{
						LOGGER.info("Version is up to date");
						infoMessage(s_UPDATE_BOXTITLE, s_UPDATE_UPTODATE);

					}

				} 
				catch (IOException e1) 
				{
					LOGGER.warn(e1.getMessage());

					errorMessage(s_UPDATE_BOXTITLE, s_UPDATE_ERROR_DONTWORK);
				}
				s_shell.setCursor(null);
			}
		});

		// Start of System Info Box

		final Group groupSystem = new Group(s_compositeInfo, SWT.NONE);
		groupSystem.setText(s_textGroupSysInfo);
		final GridData gridDataInfoSystem = new GridData(SWT.LEFT, SWT.TOP, true, true, 2, 1);
		gridDataInfoSystem.heightHint = GROUP_HEIGHT;// + 20;
		gridDataInfoSystem.widthHint = GROUP_WIDTH;
		groupSystem.setLayoutData(gridDataInfoSystem);
		final GridLayout gridLayoutInfoSystem = new GridLayout();
		gridLayoutInfoSystem.numColumns = 2;
		groupSystem.setLayout(gridLayoutInfoSystem);

		final Label label_21 = new Label(groupSystem, SWT.LEFT);
		label_21.setLayoutData(new GridData(143, SWT.DEFAULT));
		label_21.setText(s_textOsName + ":");

		final Label label_22 = new Label(groupSystem, SWT.LEFT);
		//label_22.setLayoutData(new GridData(298, SWT.DEFAULT));
		label_22.setText(System.getProperty("os.name") + ", " + 
				System.getProperty("sun.os.patch.level")+ " (" + 
				System.getProperty("os.arch") + ")");

		final Label label_23 = new Label(groupSystem, SWT.LEFT);
		label_23.setLayoutData(new GridData(143, SWT.DEFAULT));
		label_23.setText(s_textJavaVendor + ":");

		final Label label_24 = new Label(groupSystem, SWT.LEFT);
		//label_24.setLayoutData(new GridData(298, SWT.DEFAULT));
		label_24.setText(System.getProperty("java.vendor"));

		final Label label_27 = new Label(groupSystem, SWT.LEFT);
		label_27.setLayoutData(new GridData(143, SWT.DEFAULT));
		label_27.setText(s_textJavaVersion + ":");

		final Label label_28 = new Label(groupSystem, SWT.LEFT);
		//label_28.setLayoutData(new GridData(298, SWT.DEFAULT));
		label_28.setText(System.getProperty("java.version"));

		final Label label_25 = new Label(groupSystem, SWT.LEFT);
		label_25.setLayoutData(new GridData(143, SWT.DEFAULT));
		label_25.setText(s_textJavaHome + ":");

		final Label label_26 = new Label(groupSystem, SWT.LEFT);
		//label_26.setLayoutData(new GridData(298, SWT.DEFAULT));
		label_26.setText(System.getProperty("java.home"));
	}

	/**
	 * Method to check if target file already exists.
	 * Ask user if existing file should be overwritten.
	 * @return true if file doesn't exist or user wants to overwrite it
	 */
	private static boolean createTargetFile(String target)
	{
		final File checkFile = new File(target);
		boolean createFile = true;
		if (checkFile.exists() && !checkFile.isDirectory()) 
		{
			if(!questMessage(s_messageOverwriteTitle, s_messageOverwrite))
				createFile = false;
		}
		return createFile;
	}

	/**
	 * Method to check if file exists, if not a info message opens
	 * @param source the file to check
	 * @return true if file exists
	 */
	private static boolean fileExists(String source)
	{
		boolean exists = false;
		if ((new File(source)).exists()) 
			exists = true;
		else
			infoMessage(s_messageNoSourceTitle, s_messageNoSource);
		return exists;
	}


	/**
	 * This method is used to browse for a source file (open)
	 * @param action 'COMPOSITE'_'FIELD' i.e. ENCRYPT_SOURCE
	 */
	private static void browseOpenFile(int action)
	{
		FileDialog fileDialog = null;
		fileDialog = new FileDialog (s_shell, SWT.OPEN);
		String path = "";
		switch (action)
		{
		case ENCRYPT_SOURCE: path = s_textEncryptSource.getText(); break;
		case DECRYPT_SOURCE: path = s_textDecryptSource.getText(); break;
		case SHRED_SOURCE: path = s_textShredSource.getText(); break;
		default: LOGGER.fatal("don't know where button is pressed (not: encrypt, decrypt or shred)!");
		}

		if (action == DECRYPT_SOURCE)
		{
			fileDialog.setFilterExtensions(new String [] {"*" + SUFFIX_FILE + "; *" + SUFFIX_FOLDER, "*.*"}); //Windows wild cards
			fileDialog.setFilterNames(new String [] {s_textFilesAtaraxis + " (*.ac; *.acz)", s_textFilesAll + " (*.*)"});
		}
		else  // for ENCRYPT and SHRED open file dialog
		{
			fileDialog.setFilterExtensions(new String [] {"*.*"}); //Windows wild cards
			fileDialog.setFilterNames(new String [] {s_textFilesAll + " (*.*)"});
		}
		if (!path.equalsIgnoreCase(""))
		{
			fileDialog.setFilterPath(path);
			int separator;
			separator = path.lastIndexOf("/") + 1;
			fileDialog.setFileName(path.substring(separator, path.length()));
		}
		else
			fileDialog.setFilterPath(new File(APPL_DIR).getParent());

		final String fileName = fileDialog.open();

		if (fileName != null)
		{
			switch (action)
			{
			case ENCRYPT_SOURCE:
				s_textEncryptSource.forceFocus();
				s_textEncryptSource.setText(fileName);
				if (s_textEncryptTarget.getText().equals(""))
				{
					if (fileName.endsWith(SUFFIX_ZIP))
						s_textEncryptTarget.setText(fileName + SUFFIX_FOLDER);
					else
						s_textEncryptTarget.setText(fileName + SUFFIX_FILE);
				}
				break;
			case DECRYPT_SOURCE:
				s_textDecryptSource.forceFocus();
				s_textDecryptSource.setText(fileName); 
				if (s_textDecryptTarget.getText().equals("") && fileName.endsWith(SUFFIX_FILE))
					s_textDecryptTarget.setText(fileName.substring(0, fileName.length() - SUFFIX_FILE.length()));
				if (s_textDecryptTarget.getText().equals("") && fileName.endsWith(SUFFIX_FOLDER))
				{
					s_textDecryptTarget.setText((new File(fileName)).getParent());
					s_buttonDecryptCompr.setSelection(true);
				}
				//System.err.println(fileName);
				//s_textDecryptTarget.setText(fileName.substring(0, fileName.lastIndexOf(File.pathSeparator)));
				break;
			case SHRED_SOURCE:
				s_textShredSource.forceFocus();
				s_textShredSource.setText(fileName); 
				break;
			default: 
				LOGGER.fatal("don't know where to fill the text field (not: encrypt, decrypt or shred)!");
			}
		}
	}

	/**
	 * This method is used to browse for a source folder (open).
	 * @param action 'COMPOSITE'_'FIELD' i.e. ENCRYPT_SOURCE
	 */
	private static void browseOpenFolder(int action)
	{
		DirectoryDialog dirDialog = null;
		dirDialog = new DirectoryDialog (s_shell, SWT.OPEN);

		// if file ist selected, open dialog at this location
		String path = "";
		switch (action)
		{
		case ENCRYPT_SOURCE: path = s_textEncryptSource.getText(); break;
		case DECRYPT_SOURCE: path = s_textDecryptSource.getText(); break;
		case SHRED_SOURCE: path = s_textShredSource.getText(); break;
		default: LOGGER.fatal("don't know where button is pressed (not: encrypt, decrypt or shred)!");
		}
		if (! path.equalsIgnoreCase(""))
		{
			dirDialog.setFilterPath(path);
			int separator;
			separator = path.lastIndexOf("/") + 1;
			dirDialog.setFilterPath(path.substring(separator, path.length()));
		}

		else
			dirDialog.setFilterPath(new File(APPL_DIR).getParent());

		final String fileName = dirDialog.open();

		if (fileName != null)
		{
			if (action == ENCRYPT_SOURCE)
			{
				s_textEncryptSource.forceFocus(); s_textEncryptSource.setText(fileName);
				s_buttonEncryptCompr.setSelection(true);
				if (s_textEncryptTarget.getText().equals(""))
					s_textEncryptTarget.setText(fileName + SUFFIX_FOLDER);
			}

			else{ // Shredder, cause only Enc Source and Shredder can work with folders
				s_textShredSource.forceFocus(); s_textShredSource.setText(fileName);
			}
		} // if fileName not null
	}

	/**
	 * This method is used to browse for a target file (save).
	 * @param action ENCRYPT_TARGET or DECRYPT_TARGET
	 */
	private static void browseSave(int action)
	{
		FileDialog fileDialog = null;
		String path = "";
		fileDialog = new FileDialog (s_shell, SWT.SAVE);
		if (action == ENCRYPT_TARGET){
			path = s_textEncryptTarget.getText();
			fileDialog.setFilterExtensions (new String [] {"*" + SUFFIX_FILE + "; *" + SUFFIX_FOLDER, "*.*"}); //Windows wild cards
			fileDialog.setFilterNames(new String [] {s_textFilesAtaraxis + " (*.ac; *.acz)", s_textFilesAll + " (*.*)"});
		}
		else // so it must be decrypt!
		{
			path = s_textDecryptTarget.getText();
			fileDialog.setFilterExtensions (new String [] {"*.*"}); //Windows wild cards
			fileDialog.setFilterNames(new String [] {s_textFilesAll + " (*.*)"});
		}
		if (! path.equalsIgnoreCase(""))
		{
			fileDialog.setFilterPath(path);
			int separator;
			separator = path.lastIndexOf("/") + 1;
			fileDialog.setFileName(path.substring(separator, path.length()));
		}
		else
		{
			fileDialog.setFilterPath(new File(APPL_DIR).getParent());
		}

		final String fileName = fileDialog.open();

		if (fileName != null)
		{
			if (action == ENCRYPT_TARGET)
			{
				s_textEncryptTarget.forceFocus();
				s_textEncryptTarget.setText(fileName);
			}
			else
			{
				s_textDecryptTarget.forceFocus();
				s_textDecryptTarget.setText(fileName);
			}
		} // if fileName not null
	}


	/**
	 * Method to shred a file or directory.
	 * @param filePath the file or directory to shred
	 * @param shredAlgo the shred algorithm to use
	 * @param leaveRoot true if the directory should not been deleted 
	 * i.e. on Linux if filePath is a disk/partition use true
	 */
	private static void shred(String filePath, int shredAlgo, boolean leaveRoot)
	{
		final int BIG_FILE = 1024 * 1024 * 50; // 50MB
		final File checkFile = new File(filePath);

		if(s_shredder == null)
			s_shredder = new AtaraxisShredder();

		if (checkFile.canWrite())
		{
			if((checkFile.length() / BIG_FILE) > 0)
				infoMessage(s_messageDurationTitle, s_messageDurationFile);
			else if(checkFile.isDirectory())
				infoMessage(s_messageDurationTitle, s_messageDurationDir);

			s_labelDeletion.setForeground(new Color(s_display, 220, 0, 0));
			s_labelDeletion.setText(s_textShredProcedure);
			LOGGER.debug("shred button pressed; leaveDirectory: " + leaveRoot);

			try
			{
				switch(shredAlgo)
				{
				case SHRED_ALGO_ZEROES: s_shredder.wipeWithByte(filePath, leaveRoot, (byte)0x00);
				break;
				case SHRED_ALGO_DOD: s_shredder.wipeDoD(filePath, leaveRoot, false);
				break;
				case SHRED_ALGO_DOD_EXT: s_shredder.wipeDoD(filePath, leaveRoot, true);
				break;
				case SHRED_ALGO_VSITR: s_shredder.wipeVSITR(filePath, leaveRoot);
				break;
				case SHRED_ALGO_SCHNEIER: s_shredder.wipeSchneier(filePath, leaveRoot);
				break;
				case SHRED_ALGO_GUTMANN_FLOPPY: s_shredder.wipeGutmann(filePath, leaveRoot, true);
				break;
				case SHRED_ALGO_GUTMANN: s_shredder.wipeGutmann(filePath, leaveRoot, false);
				break;
				case SHRED_ALGO_RANDOM: s_shredder.wipeRandom(filePath, leaveRoot);
				break;
				default: s_shredder.wipeRandom(filePath, leaveRoot);
				}
			}
			catch (IOException ioe)
			{
				errorMessage(s_messageDeletionConflictTitle, s_messageDeletionConflict);
			}
		}
		else if (!checkFile.exists())
			warningMessage(s_messageNoSourceTitle, s_messageNoSource);
		else
			errorMessage(s_messageDeletionConflictTitle, s_messageFilePermission);

		s_labelDeletion.setText("");
	}

	/**
	 * Method to change log level (write to log4j file).
	 */
	private static void changeLogLevel() 
	{
		//		 save user language

		String logLevel = "";

		switch(s_logLevel)
		{
		case FATAL: 
			LOGGER.debug("new logLevel selected - FATAL");
			logLevel = "FATAL";
			break;
		case WARN: 
			LOGGER.debug("new logLevel selected - WARN"); 
			logLevel = "WARN";
			break;
		case DEBUG: 
			LOGGER.debug("new logLevel selected - DEBUG"); 
			logLevel = "DEBUG";
			break;
		case INFO: 
			LOGGER.debug("new logLevel selected - INFO"); 
			logLevel = "INFO";
			break;
		default: 
			LOGGER.warn("couldn't retrieve new log level - don't save changes");
		}
		if (!logLevel.equals(""))
		{
			s_logProps.put(LOG_LEVEL_KEY, logLevel);
			saveLogProps();
		}
	}


	/**
	 * Method to change the user language and save it to user properties file.
	 */
	private static void changeUserLang() 
	{
		//		 save user language
		final String KEY = "user.lang";
		String langStr = "";

		switch(s_lang){
		case GERMAN: langStr = "de"; break;
		case FRENCH: langStr = "fr"; break;
		case ENGLISH: langStr = "en"; break;
		default: LOGGER.warn("could not get new user language - don't save changes");
		}
		if (!langStr.equals(""))
		{
			s_userProps.put(KEY, langStr);
			saveUserProps();
		}
	}

	/**
	 * Method to write user properties in user properties file.
	 */
	private static void saveUserProps() 
	{
		// save user props

		try
		{
			s_userProps.store(new FileOutputStream(USER_DATA_DIR + "/" + s_user + "/user.props"), null);
		}
		catch (FileNotFoundException e)
		{
			LOGGER.warn("Could not find user.props file");
		}
		catch (IOException e)
		{
			LOGGER.warn("Could not open user.props file");
		}
	}

	/**
	 * Method to save the log4j properties in config file.
	 */
	private static void saveLogProps()
	{
		// save log4j props
		try
		{
			s_logProps.store(new FileOutputStream(AtaraxisStarter.LOG_PROPS_FILE), null);
		} 
		catch (IOException e) {
			LOGGER.warn("Could not open/write log properties file: " + AtaraxisStarter.LOG_PROPS_FILE);
		}
	}

	/**
	 * Method to load the log4j properties.
	 */
	private static void loadLogProps() 
	{
		// load log4j props
		s_logProps = new Properties();
		try 
		{
			s_logProps.load(new FileInputStream(AtaraxisStarter.LOG_PROPS_FILE));
		}
		catch (FileNotFoundException e) 
		{
			LOGGER.warn("Could not find log properties file: " + AtaraxisStarter.LOG_PROPS_FILE);
		}
		catch (IOException e) 
		{
			LOGGER.warn("Could not load log properties file: " + AtaraxisStarter.LOG_PROPS_FILE);
		}
		String logLevel;
		logLevel = s_logProps.getProperty(LOG_LEVEL_KEY);
		if (logLevel != null)
		{
			if (logLevel.equals("FATAL"))
				s_logLevel = FATAL;
			else if (logLevel.equals("WARN"))
				s_logLevel = WARN;
			else if (logLevel.equals("INFO"))
				s_logLevel = INFO;
			else if (logLevel.equals("DEBUG"))
				s_logLevel = DEBUG;
			// else s_logLevel is initialised with WARN
		}
	}

	/**
	 * Method to load the network properties.
	 */
	private static void loadNetworkProps() 
	{
		// load log4j props
		s_networkProps = new Properties();
		try 
		{
			s_networkProps.load(new FileInputStream(NET_FILE));
			String useProxy = s_networkProps.getProperty("ATARAXIS.NETWORK.USE_PROXY");

			if (useProxy.equals("TRUE"))
			{
				buttonProxyYes.setSelection(true);
			}
			else
			{
				buttonProxyNo.setSelection(true);
			}

			s_textNetworkHost = s_networkProps.getProperty("ATARAXIS.NETWORK.HOST","");
			s_networkHost.setText(s_textNetworkHost);
			s_textNetworkPort  = s_networkProps.getProperty("ATARAXIS.NETWORK.PORT","");
			s_networkPort.setText(s_textNetworkPort);
		}
		catch (FileNotFoundException e) 
		{
			LOGGER.warn("Could not find Network properties file: " + NET_FILE);
		}
		catch (IOException e) 
		{
			LOGGER.warn("Could not load Network properties file: " + NET_FILE);
		}


	}

	/**
	 * Method to save the Network properties in config file.
	 */
	private static void saveNetworkProps()
	{
		// save log4j props
		try
		{
			s_networkProps.store(new FileOutputStream(NET_FILE), null);
		} 
		catch (IOException e) {
			LOGGER.warn("Could not open/write network properties file: " + NET_FILE);
		}
	}

	/**
	 * Method to load the language file dependent on configuration in user property file.
	 */
	private static void loadProperties() 
	{
		// get user language
		s_userProps = new Properties();
		try 
		{
			s_userProps.load(new FileInputStream(USER_DATA_DIR + "/" + s_user + "/user.props"));
		} 
		catch (FileNotFoundException e) 
		{
			LOGGER.warn("Could not find user.props file");
		}
		catch (IOException e) 
		{
			LOGGER.warn("Could not open user.props file");
		}

		s_deleteDefault = Integer.valueOf(s_userProps.getProperty(DEFAULT_DELETE_KEY, "" + SHRED_ALGO_DEFAULT));
		String userLang = s_userProps.getProperty("user.lang", "en");
		LOGGER.debug("user language is: " + userLang);

		if(userLang.equalsIgnoreCase("de"))
		{
			userLang = "langDE.props";
			s_lang = GERMAN;
		}
		else if(userLang.equalsIgnoreCase("fr"))
		{
			userLang = "langFR.props";
			s_lang = FRENCH;
		}
		else if (userLang.equalsIgnoreCase("en"))
		{
			userLang = "langEN.props";
			s_lang = ENGLISH;
		}
		else {
			userLang = "langEN.props";
			s_lang = ENGLISH;
			LOGGER.warn("Language in your user.props file is not supported by application: english is used");
		}


		//		 Language dependant fields
		s_langProps = new Properties();
		try
		{
			s_langProps.load(new FileInputStream(APPL_DATA_DIR + "/lang/" + userLang));
			LOGGER.debug("user language file '" + userLang + "' loaded");
		}
		catch (FileNotFoundException e) 
		{
			LOGGER.fatal(e);
		}
		catch (IOException e) 
		{
			LOGGER.fatal(e);
		}

		/* 
		 * text for buttons and labels:
		 * ############################
		 */
		s_textButtonCompr = s_langProps.getProperty("BUTTON.COMPRESS",  "Compress");
		s_textButtonDecompr = s_langProps.getProperty("BUTTON.DECOMPRESS",  "Decompress");
		s_textButtonDeleteSrc = s_langProps.getProperty("BUTTON.DELETE.SRC",  "Delete source");
		s_textButtonChangePwd = s_langProps.getProperty("BUTTON.CHANGE.PWD",  "Change password");
		s_textWindowTitle = s_langProps.getProperty("WINDOW.TITLE", "AtaraxiS");
		s_textToggleEncrypt = s_langProps.getProperty("TOGGLE.ENCRYPT", "Encryption");
		s_textToggleDecrypt = s_langProps.getProperty("TOGGLE.DECRYPT", "Decryption");
		s_textToggleShred = s_langProps.getProperty("TOGGLE.SHRED", "File Shredder");
		s_textToggleAccount = s_langProps.getProperty("TOGGLE.ACCOUNT", "Passwords");
		s_textToggleConfig = s_langProps.getProperty("TOGGLE.CONFIG", "Configuration");
		s_textToggleInfo = s_langProps.getProperty("TOGGLE.INFO", "Information");
		s_textToggleExit= s_langProps.getProperty("TOGGLE.EXIT", "Exit");
		s_textBrowse = s_langProps.getProperty("BROWSE", "Browse");
		s_textFolder = s_langProps.getProperty("FOLDER", "Folder");
		s_textFile = s_langProps.getProperty("FILE", "File");
		s_textSource = s_langProps.getProperty("SOURCE", "Source");
		s_textTarget = s_langProps.getProperty("TARGET", "Target");
		s_textYes = s_langProps.getProperty("YES", "Yes");
		s_textNo = s_langProps.getProperty("NO", "No");
		s_textEncrypt = s_langProps.getProperty("ENCRYPT", "Encrypt data");
		s_textDecrypt = s_langProps.getProperty("DECRYPT", "Decrypt data");
		s_textShred = s_langProps.getProperty("SHRED", "Shred data");
		s_textShredOptions = s_langProps.getProperty("SHRED.OPTIONS", "Shred algorithms");
		s_textTrayToolTip = s_langProps.getProperty("TRAY.TOOL.TIP", "AtaraxiS\nHide your secrets!");
		s_textGroupApplConfig = s_langProps.getProperty("APPL.CONFIG", "Application configurations");
		s_textGroupUserConfig = s_langProps.getProperty("USER.CONFIG", "Configurations for user");
		s_textLogLevelLabel = s_langProps.getProperty("LOG.LEVEL", "Log level");
		s_textLogFile = s_langProps.getProperty("LOG.FILE", "Log file");
		s_textLanguage = s_langProps.getProperty("LANGUAGE", "Language");
		s_textPasswordOld = s_langProps.getProperty("PASSWORD.OLD", "Old password");
		s_textPasswordNew = s_langProps.getProperty("PASSWORD.REP.1", "New password (1)");
		s_textPasswordNewRep = s_langProps.getProperty("PASSWORD.REP.2", "New password (2)");
		s_textGerman = s_langProps.getProperty("GERMAN", "German");
		s_textFrench = s_langProps.getProperty("FRENCH", "French");
		s_textEnglish = s_langProps.getProperty("ENGLISH", "English");
		s_textDeleteAlgorithm = s_langProps.getProperty("DELETE.ALGORITHMS", "Default deletion algorithm");
		s_textshredZeroes = s_langProps.getProperty("SHRED.ZEROES", "Zero bytes");
		s_textShredDoD = s_langProps.getProperty("SHRED.DOD", "DoD 5220.22-M");
		s_textShredDoDext = s_langProps.getProperty("SHRED.DODEXT", "DoD 5220.22-M ECE");
		s_textShredVSITR = s_langProps.getProperty("SHRED.VSITR", "BSI - VSITR");
		s_textShredRandom = s_langProps.getProperty("SHRED.RANDOM", "Random bytes");
		s_textShredSchneier = s_langProps.getProperty("SHRED.SCHNEIER", "Bruce Schneier");
		s_textShredGutmannFloppy = s_langProps.getProperty("SHRED.GUTMANN.FLOPPY", "Gutmann (floppy)");
		s_textShredGutmann = s_langProps.getProperty("SHRED.GUTMANN", "Gutmann");
		s_textShredLeaveDir = s_langProps.getProperty("SHRED.LEAVE.DIR", "Leave directory");
		s_textShredProcedure = s_langProps.getProperty("SHRED.PROCEDURE", "Erasing procedure is running...");
		s_textShredRepetitions = s_langProps.getProperty("SHRED.REPETITIONS", "overwrite");
		s_textGroupApplInfo = s_langProps.getProperty("APPLICATION.INFO", "Application infos");
		s_textGroupSysInfo = s_langProps.getProperty("SYSTEM.INFO", "System infos");
		s_textAuthor = s_langProps.getProperty("AUTHOR", "Authors");
		s_textLicense = s_langProps.getProperty("LICENSE", "License");
		s_textVersion = s_langProps.getProperty("VERSION", "Version");
		s_textWebsite = s_langProps.getProperty("WEBSITE", "Website");
		s_textDescription = s_langProps.getProperty("DESCRIPTION", "Description");
		s_textOsName = s_langProps.getProperty("OS.NAME", "Operating System");
		s_textJavaVendor = s_langProps.getProperty("JAVA.VENDOR", "Java vendor");
		s_textJavaHome = s_langProps.getProperty("JAVA.HOME", "Java home");
		s_textJavaVersion = s_langProps.getProperty("JAVA.VERSION", "Java version");
		// s_textJavaMemory = s_langProps.getProperty("JAVA.MEMORY", "Used memory");
		s_textCancel =  s_langProps.getProperty("CANCEL", "Cancel"); 
		s_textFilesAtaraxis = s_langProps.getProperty("FILEFILTER.ATARAXIS", "AtaraxiS files");
		s_textFilesAll = s_langProps.getProperty("FILEFILTER.ALL", "All files");


		/* 
		 * text and title for messages:
		 * ############################
		 */
		s_messageInvalidPasswordTitle = s_langProps.getProperty("LOGIN.MESSAGE.INVALID.PASSWROD.TITLE", "Invalid password");
		s_messageInvalidPassword = s_langProps.getProperty("LOGIN.MESSAGE.INVALID.PASSWORD", "Please, type two times the same password with a minimum length of 4 characters.\nFurthermore, the password must diverse from the user name.");
		s_messagePasswordChangeFailed = s_langProps.getProperty("LOGIN.MESSAGE.PASSWORD.CHANGE.FAILED", "Your password could not be changed. Is your current password correct?");
		s_messageDurationTitle = s_langProps.getProperty("MESSAGE.DURATION.FILE.TITLE",  "Duration");
		s_messageDurationFile = s_langProps.getProperty("MESSAGE.DURATION.FILE",  "Please, respect that the deletion of a file of this size needs some time.");
		s_messageDurationDir = s_langProps.getProperty("MESSAGE.DURATION.DIR",  "Please, respect that the deletion of a directory can take some time,\ndependent on how many files are in the directory and how big they are.");
		s_messageDeletionConflictTitle = s_langProps.getProperty("MESSAGE.DELETION.CONFLICT.TITLE",  "Deletion error");
		s_messageDeletionConflict = s_langProps.getProperty("MESSAGE.DELETION.CONFILCT",  "Please, verify that the file is not used by another program or person.");
		s_messageFilePermission = s_langProps.getProperty("MESSAGE.FILE.PERMISSION", "Please, verify that you have the necessary file permissions.");
		s_messageFileConflictTitle = s_langProps.getProperty("MESSAGE.FILE.CONFLICT.TITLE",  "Write error");
		s_messageFileConflict = s_langProps.getProperty("MESSAGE.FILE.CONFILCT",  "Please unzip to a directory and encrypt to a file!");
		s_messageCantUnzip = s_langProps.getProperty("MESSAGE.CANT.UNZIP",  "The file to unzip is not a zip file!");
		s_messageEmptyOldPwd = s_langProps.getProperty("MESSAGE.EMPTY.OLD.PWD",  "Please, enter your old password.");
		s_messageEmptyNewPwds = s_langProps.getProperty("MESSAGE.EMPTY.NEW.PWDS",  "Please, enter the new password two times.");
		s_messageNewPwdTitle = s_langProps.getProperty("MESSAGE.NEW.PWD.TITLE",  "New password");
		s_messageNewPwd = s_langProps.getProperty("MESSAGE.NEW.PWD",  "Your login password has been changed successfuly!");
		s_messageNotAllowedTitle = s_langProps.getProperty("MESSAGE.NOT.ALLOWED.TITLE",  "Not allowed");
		s_messageNotCompressFolder = s_langProps.getProperty("MESSAGE.NOT.COMPRESS.FOLDER",  "Folders will always get compressed before encryption.");
		s_messageCompressZip = s_langProps.getProperty("MESSAGE.COMPRESS.ZIP",  "Do not compress a ZIP file.");
		s_messageEncryptTitle = s_langProps.getProperty("MESSAGE.ENCRYPT.TITLE", "Successful encryption");
		s_messageEncrypt = s_langProps.getProperty("MESSAGE.ENCRYPT", "Your data has been encrypted using your AES-256 key.");
		s_messageDecryptTitle = s_langProps.getProperty("MESSAGE.DECRYPT.TITLE", "Successful decryption");
		s_messageDecrypt = s_langProps.getProperty("MESSAGE.DECRYPT", "Your data has been decrypted using your AES-256 key.");
		s_messageOverwriteTitle = s_langProps.getProperty("MESSAGE.OVERWRITE.TITLE", "Overwrite file");
		s_messageOverwrite = s_langProps.getProperty("MESSAGE.OVERWRITE", "The target file already exists. Do you really want to overwrite it?");
		s_messageEmptyFieldTitle = s_langProps.getProperty("MESSAGE.EMPTY.FIELD.TITLE", "Empty field");
		s_messageEmptyField = s_langProps.getProperty("MESSAGE.EMPTY.FIELDS", "Please, make sure, that all required fields are filled out.");
		s_messageNoSourceTitle = s_langProps.getProperty("MESSAGE.NO.SOURCE.TITLE", "No source file");
		s_messageNoSource = s_langProps.getProperty("MESSAGE.NO.SOURCE", "The source file doesn't exist!\nPlease, check your entry.");
		s_messageExitingTitle = s_langProps.getProperty("MESSAGE.EXITING.TITLE", "Exiting application");
		s_messageExiting = s_langProps.getProperty("MESSAGE.EXITING", "Do you really want " +
		"to quit this application?");
		s_messageNoTxtProgTitle = s_langProps.getProperty("MESSAGE.NOTXT.PROGRAM.TITLE", "Can't open the file");
		s_messageNoTxtProg = s_langProps.getProperty("MESSAGE.NOTXT.PROGRAM", "Could not find a text editor to open the log file.");
		s_messageCryptoErrorTitle = s_langProps.getProperty("MESSAGE.CRYPTO.ERROR.TITLE", "En/Decryption error");
		s_messageCryptoError = s_langProps.getProperty("MESSAGE.CRYPTO.ERROR", "An error occured during en/decryption.");


		/* 
		 * text and title for PasswordManager labels and tooltips:
		 * #######################################################
		 */
		s_passwordManagerTitle = s_langProps.getProperty("PWM.PASSWORD_MANAGER_TITLE", "Password Manager");
		s_groupLabelText = s_langProps.getProperty("PWM.GROUP_LABEL", "Group");
		s_accountIdLabelText = s_langProps.getProperty("PWM.ACCOUNT_ID", "Account ID");
		s_usernameLabelText = s_langProps.getProperty("PWM.USER_NAME", "Username");
		s_passwordLabelText = s_langProps.getProperty("PWM.PASSWORD", "Password");
		s_linkLabelText = s_langProps.getProperty("PWM.LINK", "Link");
		s_commentLabelText = s_langProps.getProperty("PWM.COMMENT", "Comment");
		s_copyUsernameButtonTip = s_langProps.getProperty("PWM.COPY_USERNAME", "Copy username to clipboard");
		s_openLinkButtonTip = s_langProps.getProperty("PWM.OPEN_LINK", "Open link in browser");
		s_createGroupTip = s_langProps.getProperty("PWM.CREATE_GROUP", "Create group");
		s_deleteGroupTip = s_langProps.getProperty("PWM.DELETE_GROUP", "Delete group");
		s_createUserTip = s_langProps.getProperty("PWM.CREATE_USER", "Create user");
		s_deleteUserTip = s_langProps.getProperty("PWM.DELETE_USER", "Delete user");
		s_editButtonText = s_langProps.getProperty("PWM.EDIT", "Edit user");
		s_editcancelButtonText = s_langProps.getProperty("PWM.EDIT_CANCEL", "Cancel");
		s_saveButtonText = s_langProps.getProperty("PWM.SAVE_USER", "Save");
		s_copyPasswordButtonTip = s_langProps.getProperty("PWM.COPY_PASSWORD", "Copy password to clipboard");
		s_generatePasswordTip = s_langProps.getProperty("PWM.GENERATE_PASSWORD", "Generate a password");
		s_entryDoesNotExist = s_langProps.getProperty("PWM.MISSING_ENTRY", "Entry does not exist.");

		s_pwmRenameContextMenu = s_langProps.getProperty("PWM.RENAME_CONTEXT_MENU", "rename");
		s_pwmRenameOkTitle = s_langProps.getProperty("PWM.RENAME_OK_TITLE", "Rename entry");
		s_pwmRenameOkMessage = s_langProps.getProperty("PWM.RENAME_OK_MESSAGE", "Rename was successful.");
		s_pwmRenameErrorTitle = s_langProps.getProperty("PWM.RENAME_ERROR_TITLE", "Error on rename");
		s_pwmRenameErrorNotFound = s_langProps.getProperty("PWM.RENAME_ERROR_ENTRY_NOT_FOUND", "Entry to rename does not exist.");
		s_pwmRenameErrorAlreadyExist = s_langProps.getProperty("PWM.RENAME_ERROR_ENTRY_EXIST_ALREADY", "New name already exist.");
		s_pwmRenameErrorSave = s_langProps.getProperty("PWM.RENAME_ERROR_SAVE", "Saving of accounts failed.");

		
		
		/* 
		 * text and title for PasswordManager messages:
		 * ############################################
		 */
		s_pAttention  = s_langProps.getProperty("PWM.ATTENTION",  "Attention");
		s_pFirstEndUserMod  = s_langProps.getProperty("PWM.FIRST_END_USER_MODIFICATION",  "End user modification first.");
		s_error  = s_langProps.getProperty("ERROR",  "Error");
		s_pSelectGroupBeforeDelete  = s_langProps.getProperty("PWM.SELECT_GROUP_BEFORE_DELETE",  "You have to select a Group before you can delete it.");
		s_pSelectAccount  = s_langProps.getProperty("PWM.SELECT_ACCOUNT",  "You have to select an account.");
		s_pGroupExists  = s_langProps.getProperty("PWM.AID_EXIST",  "An element with this ID already exists.");
		s_pGroupCreateError  = s_langProps.getProperty("PWM.GROUP_EXIST",  "Creation failed of group");
		s_pRealyDeleteGroup  = s_langProps.getProperty("PWM.REALY_DELETE_GROUP",  "Do you really want to delete this group:");
		s_pElementDeleteFailed  = s_langProps.getProperty("PWM.ELEMENT_DELETE_FAIL",  "Deletion failed of element");
		s_pOnlyEmptyGroupsDelete  = s_langProps.getProperty("PWM.ONLY_EMPTY_GROUPS",  "You can only delete groups with no children");
		s_pSelectAccountBeforeDelete  = s_langProps.getProperty("PWM.SELECT_ACCOUNT_BEFORE_DELETE",  "You have to select an account before you can delete it.");
		s_pRealyDeleteAccount  = s_langProps.getProperty("PWM.REALY_DELETE_ACCOUNT",  "Do you really want to delete this account:");
		s_pOnlyAccountDelete  = s_langProps.getProperty("PWM.ONLY_ACCOUNT_DELETE",  "You can only delete accounts.");
		s_pAIDnotEmpty  = s_langProps.getProperty("PWM.AID_NOT_EMPTY",  "Account ID cannot be empty.");
		s_pErrorElementChange  = s_langProps.getProperty("PWM.ERROR_ELEMENT_CHANGE",  "Element could not be changed.");
		s_pErrorAIDexist  = s_langProps.getProperty("PWM.AID_EXIST",  "Account ID already exists.");
		s_pPwmError = s_langProps.getProperty("PWM.PWM_ERROR",  "Password Manager error");
		s_pFileMissing = s_langProps.getProperty("PWM.MISSING_FILE",  "The account data file is missing. Create one?");

		/* 
		 * text and title for PasswordManager-TextInputDialog:
		 * ###################################################
		 */
		s_TID_Title = s_langProps.getProperty("PWM.TID_Title", "Create group");
		s_TID_OkButton = s_langProps.getProperty("PWM.TID_OkButton", "Create");
		s_TID_labelText = s_langProps.getProperty("PWM.TID_labelText", "Group name:");
		s_TID_warnText = s_langProps.getProperty("PWM.TID_warnText", "Warning");		
		s_TID_warnMessage = s_langProps.getProperty("PWM.TID_warnMessage", "Text may not be empty!");

		/* 
		 * text and title for Proxy-Settings:
		 * ###################################################
		 */
		s_NETWORK_PROXY_YES = s_langProps.getProperty("NETWORK.PROXY_YES", "use Proxy");
		s_NETWORK_PROXY_NO = s_langProps.getProperty("NETWORK.PROXY_NO", "direct");
		s_NETWORK_GROUP_TITLE = s_langProps.getProperty("NETWORK.GROUP_TITLE", "Network settings");
		s_NETWORK_SAVE_BUTTON = s_langProps.getProperty("NETWORK.SAVE_BUTTON", "Save Network");

		/* 
		 * text and title for Update-Dialogs:
		 * ###################################################
		 */

		s_UPDATE_CHECK = s_langProps.getProperty("UPDATE.CHECK_FOR_UPDATES", "check for update");
		s_UPDATE_BOXTITLE = s_langProps.getProperty("UPDATE.UPDATE_BOX_TITLE", "Ataraxis Update");
		s_UPDATE_LOAD_NEW_VERSION = s_langProps.getProperty("UPDATE.NEW_VERSION_DOWNLOAD", "is ready. Open website in browser?");
		s_UPDATE_UPTODATE = s_langProps.getProperty("UPDATE.VERSION_IS_UPTODATE", "Your Version is up to date.");
		s_UPDATE_ERROR_DONTWORK = s_langProps.getProperty("UPDATE.ERROR_NEED_A_PROXY", "Network error. Please check your network connection and try again later. If you have to use a proxy server, configure the network settings accordingly.");
	}

	/**
	 * Getter method to get the selected composite. 
	 * @return the number which indicates which composite is selected
	 */
	protected static int getSelectedComposite()
	{
		return s_selectedComposite;
	}

	/**
	 * Method to set the composite to use (change stackLayout)
	 * @param composite
	 */
	protected static void setSelectedComposite(int composite)
	{
		s_selectedComposite = composite;

		switch (s_selectedComposite)
		{
		case PASSWORDS: 
			s_stackLayout.topControl = s_compositePasswords;
			s_composite.layout();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("setSelectedComposite(int) - changed to Password Mgmt composite");
			break;

		case ENCRYPTION: 
			s_stackLayout.topControl = s_compositeEncrypt;
			s_composite.layout();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("setSelectedComposite(int) - changed to Encrypt composite");
			break;

		case DECRYPTION: 
			s_stackLayout.topControl = s_compositeDecrypt;
			s_composite.layout();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("setSelectedComposite(int) - changed to Decrypt composite");
			break;

		case SHRED: 
			s_stackLayout.topControl = s_compositeShred;
			s_composite.layout();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("setSelectedComposite(int) - changed to Shred composite");
			break;

		case PUBLIC_KEYS: 
			s_stackLayout.topControl = s_compositePublicKeys;
			s_composite.layout();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("setSelectedComposite(int) - changed to Public-Key Mgmt composite");
			break;
		case CONFIGURATION: 
			s_stackLayout.topControl = s_compositeConfig;
			s_composite.layout();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("setSelectedComposite(int) - changed to Config composite");
			break;
		case INFROMATION: 
			s_stackLayout.topControl = s_compositeInfo;
			s_composite.layout();
			if (LOGGER.isDebugEnabled())
				LOGGER.debug("setSelectedComposite(int) - changed to Info composite");
			break;
		default:
			s_stackLayout.topControl = s_compositeEncrypt;
		s_composite.layout();
		LOGGER.warn("setSelectedComposite(int) - changed to DEFAULT (encrypt) composite");
		}
	}

	/**
	 * Hellper-Method to show a warning message.
	 * @param title the message title
	 * @param message the message text
	 */
	private static void warningMessage(String title, String message)
	{
		final MessageBox messageBox = new MessageBox(s_shell, SWT.ICON_WARNING | SWT.YES);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	/**
	 * Hellper-Method to show a error message.
	 * @param title the message title
	 * @param message the message text
	 */
	private static void errorMessage(String title, String message)
	{
		final MessageBox messageBox = new MessageBox(s_shell, SWT.ICON_ERROR | SWT.OK);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	/**
	 * Hellper-Method to show a info message.
	 * @param title the message title
	 * @param message the message text
	 */
	private static void infoMessage(String title, String message)
	{
		final MessageBox messageBox = new MessageBox(s_shell, SWT.ICON_INFORMATION | SWT.YES);
		messageBox.setText(title);
		messageBox.setMessage(message);
		messageBox.open();
	}

	/**
	 * Hellper-Method to show a question message and return answer.
	 * @param title the message title
	 * @param message the message text (a question!)
	 * @return true if user answer is yes
	 */
	private static boolean questMessage(String title, String message)
	{
		final MessageBox messageBox = new MessageBox(s_shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
		messageBox.setText(title);
		messageBox.setMessage(message);
		return (messageBox.open() == SWT.YES);
	}

	/**
	 *  Change selection of navigation button (radio behavior).
	 */
	private static void updateNavi(){
		final Control [] children = naviComposite.getChildren();
		for (int i=0; i<children.length; i++)
		{
			((Button) children[i]).setSelection(false);
		}
		((Button) children[s_selectedComposite - 1]).setSelection(true);
	}


	/**
	 * Method to change the background color recursivly (all childs).
	 * @param parent the parent composite
	 * @param newColor the background color to set
	 */
	private void changeBackgroundColor(Composite parent, Color newColor)
	{
		parent.setBackground(newColor);
		final Control [] children = parent.getChildren();
		for (int i=0; i<children.length; i++)
		{
			//children[i].setBackground(newColor);
			if (children[i] instanceof Composite)
				changeBackgroundColor((Composite) children[i], newColor);
			else if (!(children[i] instanceof Text))
				children[i].setBackground(newColor);	
		}
	}	

	/**
	 *  Method to create the TrayEntry Menu.
	 */
	private void makeTrayEntry()
	{
		final Image encryptIcon = new Image(s_display, ICON_DIR + "/Tray_Encrypt.png");
		final Image decryptIcon = new Image(s_display, ICON_DIR + "/Tray_Decrypt.png");
		final Image shredIcon = new Image(s_display, ICON_DIR + "/Tray_Shredder.png");
		final Image passwordsIcon = new Image(s_display, ICON_DIR + "/Tray_Password.png");
		final Image configIcon = new Image(s_display, ICON_DIR + "/Tray_Config.png");
		final Image infoIcon = new Image(s_display, ICON_DIR + "/Tray_Info.png");
		final Image exitIcon = new Image(s_display, ICON_DIR + "/Tray_Exit.png");
		//final Image pgpIcon = new Image(s_display, ICON_DIR + "/Tray_Password.png");


		final Tray tray = s_display.getSystemTray ();
		if (tray == null)
			LOGGER.error("The system tray is not available");
		else
		{
			s_trayItem = new TrayItem (tray, SWT.NONE);
			s_trayItem.setToolTipText(s_textTrayToolTip);
			s_trayItem.addListener(SWT.Show, new Listener()
			{
				public void handleEvent (Event event)
				{
					LOGGER.info("show");
				}
			});
			s_trayItem.addListener(SWT.Hide, new Listener()
			{
				public void handleEvent (Event event)
				{
					LOGGER.info("hide");
				}
			});
			s_trayItem.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent (Event event)
				{
					LOGGER.info("selection");
					s_shell.setActive();
					s_shell.setMinimized(false);
					s_shell.setVisible(true);
				}
			});
			s_trayItem.addListener(SWT.DefaultSelection, new Listener()
			{
				public void handleEvent (Event event)
				{
					LOGGER.info("default selection");
				}
			});
			final Menu menu = new Menu (s_shell, SWT.POP_UP);

			final MenuItem menuTitle = new MenuItem(menu, SWT.Deactivate);
			menuTitle.setText("AtaraxiS");
			menuTitle.setImage(s_icon);
			menuTitle.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent (Event event)
				{
					LOGGER.info("selection " + event.widget);
					s_shell.setActive();
					s_shell.setMinimized(false);
					s_shell.setVisible(true);
				}
			});


			MenuItem menuLine = new MenuItem(menu, SWT.SEPARATOR);
			menuLine.setEnabled(true);

			final MenuItem menuPasswords = new MenuItem(menu, SWT.PUSH);
			menuPasswords.setText(s_textToggleAccount);
			menuPasswords.setImage(passwordsIcon);
			menuPasswords.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent (Event event)
				{
					LOGGER.info("selection " + event.widget);
					setSelectedComposite(PASSWORDS);
					updateNavi();
					s_shell.setActive();
					s_shell.setMinimized(false);
					s_shell.setVisible(true);
				}
			});


			final MenuItem menuEnc = new MenuItem(menu, SWT.PUSH);
			menuEnc.setText(s_textToggleEncrypt);
			menuEnc.setImage(encryptIcon);
			menuEnc.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent (Event event)
				{
					LOGGER.info("selection " + event.widget);
					setSelectedComposite(ENCRYPTION);
					updateNavi();
					s_shell.setActive();
					s_shell.setMinimized(false);
					s_shell.setVisible(true);
				}
			});

			final MenuItem menuDec = new MenuItem(menu, SWT.PUSH);
			menuDec.setText(s_textToggleDecrypt);
			menuDec.setImage(decryptIcon);
			menuDec.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent (Event event)
				{
					LOGGER.info("selection " + event.widget);
					setSelectedComposite(DECRYPTION);
					updateNavi();
					s_shell.setActive();
					s_shell.setMinimized(false);
					s_shell.setVisible(true);
				}
			});

			final MenuItem menuShred = new MenuItem(menu, SWT.PUSH);
			menuShred.setText(s_textToggleShred);
			menuShred.setImage(shredIcon);
			menuShred.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent (Event event)
				{
					LOGGER.info("selection " + event.widget);
					setSelectedComposite(SHRED);
					updateNavi();
					s_shell.setActive();
					s_shell.setMinimized(false);
					s_shell.setVisible(true);
				}
			});



			final MenuItem menuConfig = new MenuItem(menu, SWT.PUSH);
			menuConfig.setText(s_textToggleConfig);
			menuConfig.setImage(configIcon);
			menuConfig.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent (Event event)
				{
					LOGGER.info("selection " + event.widget);
					setSelectedComposite(CONFIGURATION);
					updateNavi();
					s_shell.setActive();
					s_shell.setMinimized(false);
					s_shell.setVisible(true);
				}
			});

			final MenuItem menuInfo = new MenuItem(menu, SWT.PUSH);
			menuInfo.setText(s_textToggleInfo);
			menuInfo.setImage(infoIcon);
			menuInfo.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent (Event event)
				{
					LOGGER.info("selection " + event.widget);
					setSelectedComposite(INFROMATION);
					updateNavi();
					s_shell.setActive();
					s_shell.setMinimized(false);
					s_shell.setVisible(true);
				}
			});

			menuLine = new MenuItem(menu, SWT.SEPARATOR);
			menuLine.setEnabled(true);

			final MenuItem menuExit = new MenuItem(menu, SWT.PUSH);
			menuExit.setText(s_textToggleExit);
			menuExit.setImage(exitIcon);
			menuExit.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent (Event event)
				{
					LOGGER.info("selection " + event.widget);
					if (questMessage(s_messageExitingTitle, s_messageExiting))
						s_shell.dispose();
				}
			});

			s_trayItem.addListener(SWT.MenuDetect, new Listener()
			{
				public void handleEvent (Event event)
				{
					menu.setVisible (true);
				}
			});
			s_trayItem.setImage (s_icon);
		}
	}
}
