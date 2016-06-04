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

package ataraxis.gui.content;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStoreException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ataraxis.crypt.AtaraxisCrypter;
import ataraxis.gui.GUIHelper;



/**
 * AtaraxisConfigGUI creates the configuration composite.
 * 
 * @author J. Graber & A. Muedespacher
 * @version 1.2
 */
public class AtaraxisConfigGUI 
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = LogManager.getLogger(AtaraxisConfigGUI.class);

	// used AtaraxiS classes	
	private AtaraxisCrypter s_ac;
	private GUIHelper guiHelper;
	private ResourceBundle s_translations;

	// Basic settings
	private String s_user;
	private int s_lang;
	
	// SWT components
	private Combo s_configLangCombo;
	public Text s_configPwd;
	public Text s_configPwdNew;
	public Text s_configPwdNewRep;

	// Paths and files
	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String USER_DATA_DIR = APPL_DIR + "/user_data";
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	private static final String LOG_CONFIG = APPL_DATA_DIR + "/config/log4j2.xml";
	private static final String LOG_CONFIG_SHORT = ".../config/log4j2.xml";
	private static final String LOG_FILE = APPL_DATA_DIR + "/ataraxis.log";
	private static final String LOG_FILE_SHORT = ".../application_data/ataraxis.log";
	private static final String NET_FILE = APPL_DATA_DIR + "/config/network.properties";
	private static final String DEFAULT_DELETE_KEY = "default.delete.algo";

	// Properties to store the settings
	private static Properties s_userProps = null;
	private static Properties s_networkProps;
	
	// Shred settings
	private static final int SHRED_ALGO_RANDOM = 7;
	private static final int SHRED_ALGO_DEFAULT = SHRED_ALGO_RANDOM;
	private static int s_deleteDefault = SHRED_ALGO_DEFAULT;

	// Network settings
	private Text s_networkHost;
	private Text s_networkPort;
	private String s_textNetworkPort = "";
	private static String s_textNetworkHost =  "";

	private static Button buttonProxyYes;
	private static Button buttonProxyNo;
	
	// Language settings
	private static final int GERMAN = 0;
	private static final int FRENCH = 1;
	private static final int ENGLISH = 2;

	/**
	 * AtaraxisConfigGUI manages all to create a composite
	 * for the configuration GUI.
	 * 
	 * @param translations with the translations
	 */
	public AtaraxisConfigGUI(ResourceBundle translations, AtaraxisCrypter ac, String user)
	{
		s_translations = translations;
		s_ac = ac;
		s_user = user;
		loadProperties();
	}

	/**
	 * Crate the Composite for the configuration GUI.
	 * 
	 * @param s_shell shell of outside SWT application
	 * @param display display of the outside SWT application
	 * @param parentComposite where the PasswordContent should be placed
	 * @return the composite
	 */
	public Composite createConfigContent(final Shell s_shell, final Display display, Composite parentComposite) 
	{
		guiHelper = new GUIHelper(s_shell);
		
		
		Composite s_compositeConfig = new Composite(parentComposite, SWT.NONE);
		final GridLayout gridLayoutConfig = new GridLayout();
		gridLayoutConfig.numColumns = 2;
		s_compositeConfig.setLayout(gridLayoutConfig);
		final GridData gridDataConfig = new GridData(SWT.CENTER, SWT.TOP, false, false);
		gridDataConfig.widthHint = GUIHelper.COMPOSITE_WIDTH;
		gridDataConfig.heightHint = GUIHelper.COMPOSITE_HEIGHT;
		s_compositeConfig.setLayoutData(gridDataConfig);

		final Group groupApplConfig = new Group(s_compositeConfig, SWT.NONE);
		groupApplConfig.setText(s_translations.getString("APPL.CONFIG"));
		final GridData gridDataConfigAppl = new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1);
		gridDataConfigAppl.heightHint = 50; //GROUP_HEIGHT;
		gridDataConfigAppl.widthHint = GUIHelper.GROUP_WIDTH;
		groupApplConfig.setLayoutData(gridDataConfigAppl);
		final GridLayout gridLayoutConfigAppl = new GridLayout();
		gridLayoutConfigAppl.numColumns = 2;
		groupApplConfig.setLayout(gridLayoutConfigAppl);

		final Label label_3 = new Label(groupApplConfig, SWT.LEFT);
		label_3.setLayoutData(new GridData(200, 20));
		label_3.setText(s_translations.getString("LOG.CONFIG") + ":");

		final Link linkLogConfig = new Link(groupApplConfig, SWT.NONE);
		linkLogConfig.setLayoutData(new GridData(230, 18));
		linkLogConfig.setText("<a href=\"" + LOG_CONFIG + "\">" + LOG_CONFIG_SHORT + "</a>");
		linkLogConfig.setToolTipText(LOG_CONFIG);
		linkLogConfig.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {				
				final Program p = Program.findProgram (".xml");

				if (p != null) 
					Program.launch(event.text);
				else
					guiHelper.displayInfoMessage(s_translations.getString("MESSAGE.NOTXT.PROGRAM.TITLE"), s_translations.getString("MESSAGE.NOTXT.PROGRAM"));
			}
		});
		
		final Label label_11 = new Label(groupApplConfig, SWT.LEFT);
		label_11.setLayoutData(new GridData(200, 18));
		label_11.setText(s_translations.getString("LOG.FILE") + ":");

		final Link link = new Link(groupApplConfig, SWT.NONE);
		link.setLayoutData(new GridData(230, 20));
		link.setText("<a href=\"" + LOG_FILE + "\">" + LOG_FILE_SHORT + "</a>");
		link.setToolTipText(LOG_FILE);
		link.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event event) {				
				final Program p = Program.findProgram (".txt");

				if (p != null) 
					Program.launch(event.text);
				else
					guiHelper.displayInfoMessage(s_translations.getString("MESSAGE.NOTXT.PROGRAM.TITLE"), s_translations.getString("MESSAGE.NOTXT.PROGRAM"));
			}
		});



		final Group groupUserConfig = new Group(s_compositeConfig, SWT.NONE);
		groupUserConfig.setText(s_translations.getString("USER.CONFIG") + " '" + s_user + "'");
		final GridData gridDataConfigSystem = new GridData(SWT.LEFT, SWT.TOP, true, true, 2, 1);
		gridDataConfigSystem.heightHint = 220;
		gridDataConfigSystem.widthHint = GUIHelper.GROUP_WIDTH;
		groupUserConfig.setLayoutData(gridDataConfigSystem);
		final GridLayout gridLayoutConfigSystem = new GridLayout();
		gridLayoutConfigSystem.numColumns = 2;
		groupUserConfig.setLayout(gridLayoutConfigSystem);

		final Label label_23 = new Label(groupUserConfig, SWT.LEFT);
		label_23.setLayoutData(new GridData(200, SWT.DEFAULT));
		label_23.setText(s_translations.getString("LANGUAGE") + ":");

		s_configLangCombo = new Combo(groupUserConfig, SWT.DROP_DOWN | SWT.READ_ONLY);
		final GridData configLangComboGridData = new GridData(SWT.LEFT, SWT.CENTER, true, false);
		configLangComboGridData.widthHint = 200;
		s_configLangCombo.setLayoutData(configLangComboGridData);
		s_configLangCombo.setItems(new String [] {s_translations.getString("GERMAN"), s_translations.getString("FRENCH"), s_translations.getString("ENGLISH")});
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
		labelConfigDelete.setLayoutData(new GridData(183, SWT.DEFAULT));
		labelConfigDelete.setText(s_translations.getString("DELETE.ALGORITHMS") + ":");

		final Combo comboConfigDelete = new Combo(groupUserConfig, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboConfigDelete.setLayoutData(configLangComboGridData);
		comboConfigDelete.setItems(new String [] {s_translations.getString("SHRED.ZEROES"), s_translations.getString("SHRED.DOD"), 
				s_translations.getString("SHRED.DODEXT"), s_translations.getString("SHRED.VSITR"), s_translations.getString("SHRED.SCHNEIER"), 
				s_translations.getString("SHRED.GUTMANN.FLOPPY"), s_translations.getString("SHRED.GUTMANN"), s_translations.getString("SHRED.RANDOM")});
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
		label_21.setLayoutData(new GridData(200, SWT.DEFAULT));
		label_21.setText(s_translations.getString("PASSWORD.OLD") + ":");

		s_configPwd = new Text(groupUserConfig, SWT.LEFT | SWT.BORDER);
		s_configPwd.setLayoutData(new GridData(200, SWT.DEFAULT));
		s_configPwd.setEchoChar('*');

		final Label label_22 = new Label(groupUserConfig, SWT.LEFT);
		label_22.setLayoutData(new GridData(200, SWT.DEFAULT));
		label_22.setText(s_translations.getString("PASSWORD.REP.1") + ":");

		s_configPwdNew = new Text(groupUserConfig, SWT.LEFT | SWT.BORDER);
		s_configPwdNew.setLayoutData(new GridData(200, SWT.DEFAULT));
		s_configPwdNew.setEchoChar('*');

		final Label label_27 = new Label(groupUserConfig, SWT.LEFT);
		label_27.setLayoutData(new GridData(200, SWT.DEFAULT));
		label_27.setText(s_translations.getString("PASSWORD.REP.2") + ":");

		s_configPwdNewRep = new Text(groupUserConfig, SWT.LEFT | SWT.BORDER);
		s_configPwdNewRep.setLayoutData(new GridData(200, SWT.DEFAULT));
		s_configPwdNewRep.setEchoChar('*');

		final Label label_28 = new Label(groupUserConfig, SWT.LEFT);
		label_28.setLayoutData(new GridData(200, SWT.DEFAULT));

		final Button buttonSavePwd = new Button(groupUserConfig, SWT.CENTER);
		buttonSavePwd.setLayoutData(new GridData(200, SWT.DEFAULT));
		buttonSavePwd.setText(s_translations.getString("BUTTON.CHANGE.PWD"));
		buttonSavePwd.addSelectionListener(new ChangePwdButton());

		// Network Group

		final Group groupNetworkConfig = new Group(s_compositeConfig, SWT.NONE);
		final GridData gridDataNetworkConfig = new GridData(SWT.LEFT, SWT.BOTTOM, true, false);
		gridDataNetworkConfig.heightHint = 110;
		gridDataNetworkConfig.widthHint = GUIHelper.GROUP_WIDTH;
		groupNetworkConfig.setLayoutData(gridDataNetworkConfig);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		groupNetworkConfig.setLayout(gridLayout);
		groupNetworkConfig.setText(s_translations.getString("NETWORK.GROUP_TITLE"));

		buttonProxyNo = new Button(groupNetworkConfig, SWT.RADIO);
		buttonProxyNo.setText(s_translations.getString("NETWORK.PROXY_NO"));
		new Label(groupNetworkConfig, SWT.NONE);

		buttonProxyYes = new Button(groupNetworkConfig, SWT.RADIO);
		buttonProxyYes.setText(s_translations.getString("NETWORK.PROXY_YES")+":");

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
		label_21_1.setLayoutData(new GridData(200, SWT.DEFAULT));

		final Button buttonSaveNetwork = new Button(groupNetworkConfig, SWT.NONE);
		buttonSaveNetwork.setLayoutData(new GridData(156, SWT.DEFAULT));
		buttonSaveNetwork.setText(s_translations.getString("NETWORK.SAVE_BUTTON"));


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

		loadNetworkProps();
		
		return s_compositeConfig;

	}
	
	/**
	 * Method to change the user language and save it to user properties file.
	 */
	private void changeUserLang() 
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
	private void saveUserProps() 
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
	
	private void loadProperties()
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
		
		String userLang = s_userProps.getProperty("user.lang", "en");
		if(userLang.equalsIgnoreCase("de"))
		{
			s_lang = GERMAN;
		}
		else if(userLang.equalsIgnoreCase("fr"))
		{
			s_lang = FRENCH;
		}
		else
		{
			s_lang = ENGLISH;
		}
		
		String defDelete = s_userProps.getProperty(DEFAULT_DELETE_KEY,Integer.toString(SHRED_ALGO_RANDOM));
		Integer defaultDel = Integer.valueOf(defDelete);
		if(defaultDel != null )
		{
			s_deleteDefault = defaultDel.intValue();
		}		
	}

	
	/**
	 * Method to load the network properties.
	 */
	private void loadNetworkProps() 
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
	private void saveNetworkProps()
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
	 * This class is used to change the login password
	 */
	private final class ChangePwdButton extends SelectionAdapter
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
				guiHelper.displayWarningMessage(s_translations.getString("MESSAGE.EMPTY.FIELD.TITLE"), s_translations.getString("MESSAGE.EMPTY.OLD.PWD"));
			else if (pwdNew.equals("") || pwdNewRep.equals(""))
				guiHelper.displayWarningMessage(s_translations.getString("MESSAGE.EMPTY.FIELD.TITLE"), s_translations.getString("MESSAGE.EMPTY.NEW.PWDS"));
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
							guiHelper.displayInfoMessage(s_translations.getString("MESSAGE.NEW.PWD.TITLE"), s_translations.getString("MESSAGE.NEW.PWD"));
						}		
						else
						{
							guiHelper.displayWarningMessage(s_translations.getString("LOGIN.MESSAGE.INVALID.PASSWROD.TITLE"), s_translations.getString("LOGIN.MESSAGE.PASSWORD.CHANGE.FAILED"));
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
					guiHelper.displayWarningMessage(s_translations.getString("LOGIN.MESSAGE.INVALID.PASSWROD.TITLE"), s_translations.getString("LOGIN.MESSAGE.INVALID.PASSWORD"));
				}
			}
		}
	}

}
