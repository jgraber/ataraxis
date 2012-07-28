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
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tray;
import org.eclipse.swt.widgets.TrayItem;

import ch.ethz.origo.ataraxis.crypt.AtaraxisCrypter;
import ch.ethz.origo.ataraxis.gui.content.AtaraxisConfigGUI;
import ch.ethz.origo.ataraxis.gui.content.AtaraxisDecryptGUI;
import ch.ethz.origo.ataraxis.gui.content.AtaraxisEncryptGUI;
import ch.ethz.origo.ataraxis.gui.content.AtaraxisInfoGUI;
import ch.ethz.origo.ataraxis.gui.content.AtaraxisPasswordGUI;
import ch.ethz.origo.ataraxis.gui.content.AtaraxisShrederGUI;
import ch.ethz.origo.ataraxis.passwordmanager.PasswordManager;

/**
 * AtaraxisMainGUI is the main class of the presentation layer.
 * 
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 */
public class AtaraxisMainGUI
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(AtaraxisMainGUI.class);

	
	// component constants:
	private static final int PASSWORDS = 1;
	private static final int ENCRYPTION = 2;
	private static final int DECRYPTION = 3;	
	private static final int SHRED = 4;
	private static final int CONFIGURATION = 5;
	private static final int INFROMATION = 6;
	private static final int PUBLIC_KEYS = 7;

	// Paths
	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String USER_DATA_DIR = APPL_DIR + "/user_data";
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	private static final String ICON_DIR = APPL_DATA_DIR + "/icons";
	//public static final String LOG_PROPS_FILE = APPL_DATA_DIR + "/config/log4j.properties";
	//public static final Color COLOR = new Color(Display.getDefault(), 220, 220, 255);

	
	private static final int GERMAN = 0;
	private static final int FRENCH = 1;
	private static final int ENGLISH = 2;
	
	private static final int SHRED_ALGO_RANDOM = 7;
	private static final int SHRED_ALGO_DEFAULT = SHRED_ALGO_RANDOM;

	private static final String DEFAULT_DELETE_KEY = "default.delete.algo";
	
	private static PasswordManager s_pwManager = null;

	private static int s_x, s_y;
	//private static int s_encryptFiletype = 0;
	@SuppressWarnings("unused")
	private static int s_shredFiletype = 0;
	private static int s_lang = 0;
	private static int s_deleteDefault = SHRED_ALGO_DEFAULT;

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
	private static String s_user = "";
	private static Properties s_userProps = null;
	private static Listener naviListener = null;
	private static StackLayout s_stackLayout = null;
	
	private ResourceBundle s_translations;

	private GUIHelper guiHelper;

	protected static Properties s_langProps = null;
	private static AtaraxisCrypter s_ac = null;

	// images for GUI
	private final static Image ICON_ENCRYPT = new Image(s_display, ICON_DIR + "/Menu_Encrypt.png");
	private final static Image ICON_DECRYPT = new Image(s_display, ICON_DIR + "/Menu_Decrypt.png");
	private final static Image ICON_SHRED = new Image(s_display, ICON_DIR + "/Menu_Shredder.png");
	private final static Image ICON_PASSWORDS = new Image(s_display, ICON_DIR + "/Menu_Password.png");
	private final static Image ICON_CONFIG = new Image(s_display, ICON_DIR + "/Menu_Config.png");
	private final static Image ICON_INFO = new Image(s_display, ICON_DIR + "/Menu_Info.png");
	private final static Image ICON_EXIT = new Image(s_display, ICON_DIR + "/Menu_Exit.png");
		

	/**
	 * AtaraxisMainGUI constructor
	 * @param ac the AtaraxisCrypter to use for Encryption/Decryption
	 */
	AtaraxisMainGUI(AtaraxisCrypter ac, ResourceBundle translations)
	{
		s_ac = ac;	
		s_translations = translations;
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
		loadProperties();
		
		s_display = Display.getDefault();
		
		
		createContents();
		guiHelper.changeBackgroundColor(s_shell, AtaraxisStarter.COLOR);
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
		s_shell.setText(s_translations.getString("WINDOW.TITLE") + " - " + s_user);

		guiHelper = new GUIHelper(s_shell);
		
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
		toggleAccount.setToolTipText(s_translations.getString("TOGGLE.ACCOUNT"));
		toggleAccount.addListener(SWT.Selection, naviListener);


		final Button toggleEncrypt = new Button(naviComposite, SWT.TOGGLE);
		toggleEncrypt.setToolTipText(s_translations.getString("TOGGLE.ENCRYPT"));
		toggleEncrypt.setImage(ICON_ENCRYPT);
		toggleEncrypt.addListener(SWT.Selection, naviListener);

		final Button toggleDecrypt = new Button(naviComposite, SWT.TOGGLE);
		toggleDecrypt.setToolTipText(s_translations.getString("TOGGLE.DECRYPT"));
		toggleDecrypt.setImage(ICON_DECRYPT);
		toggleDecrypt.addListener(SWT.Selection, naviListener);

		final Button toggleShred = new Button(naviComposite, SWT.TOGGLE);
		toggleShred.setImage(ICON_SHRED);
		toggleShred.addListener(SWT.Selection, naviListener);
		toggleShred.setToolTipText(s_translations.getString("TOGGLE.SHRED"));



		final Button toggleConfig = new Button(naviComposite, SWT.TOGGLE);
		toggleConfig.setImage(ICON_CONFIG);
		toggleConfig.setToolTipText(s_translations.getString("TOGGLE.CONFIG"));
		toggleConfig.addListener(SWT.Selection, naviListener);

		final Button toggleInfo = new Button(naviComposite, SWT.TOGGLE);
		toggleInfo.setImage(ICON_INFO);
		toggleInfo.setToolTipText(s_translations.getString("TOGGLE.INFO"));
		toggleInfo.addListener(SWT.Selection, naviListener);

		final Button toggleExit = new Button(naviComposite, SWT.TOGGLE); 
		toggleExit.setImage(ICON_EXIT);
		toggleExit.setToolTipText(s_translations.getString("TOGGLE.EXIT"));
		toggleExit.addSelectionListener(new SelectionAdapter()
		{
			public void widgetSelected(SelectionEvent e)
			{
				if (guiHelper.displayQuestionMessage(s_translations.getString("MESSAGE.EXITING.TITLE"), s_translations.getString("MESSAGE.EXITING")))
					s_shell.dispose();
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

		AtaraxisEncryptGUI encryptGui = new AtaraxisEncryptGUI(s_translations, s_ac, s_deleteDefault);
		s_compositeEncrypt = encryptGui.createEncryptContent(s_shell, s_display, s_composite);
		
		AtaraxisDecryptGUI decryptGui = new AtaraxisDecryptGUI(s_translations, s_ac, s_deleteDefault);
		s_compositeDecrypt = decryptGui.createDecryptContent(s_shell, s_display, s_composite);
		
		AtaraxisShrederGUI shrederGui = new AtaraxisShrederGUI(s_translations, s_deleteDefault);
		s_compositeShred = shrederGui.createShredContent(s_shell, s_display, s_composite);
		
		AtaraxisPasswordGUI passwordGui = new AtaraxisPasswordGUI(s_pwManager, s_ac, s_translations);
		s_compositePasswords = passwordGui.createPasswordsContent(s_shell, s_display, s_composite);
		
		AtaraxisConfigGUI configGui = new AtaraxisConfigGUI(s_translations, s_ac, s_user);
		s_compositeConfig = configGui.createConfigContent(s_shell, s_display, s_composite);
				
		AtaraxisInfoGUI infoGUI = new AtaraxisInfoGUI(s_translations, s_lang);
		s_compositeInfo = infoGUI.createInfoContent(s_shell, s_display, s_composite);

		// if nothing is selected at startup, crypt composit is shown
		if (s_selectedComposite == 0)
		{
			toggleAccount.setSelection(true);
			s_selectedComposite = PASSWORDS;
			s_stackLayout.topControl = s_compositePasswords;
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
	 * Getter method to get the selected composite. 
	 * @return the number which indicates which composite is selected
	 */
	protected int getSelectedComposite()
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
	 *  Change selection of navigation button (radio behavior).
	 */
	private void updateNavi(){
		final Control [] children = naviComposite.getChildren();
		for (int i=0; i<children.length; i++)
		{
			((Button) children[i]).setSelection(false);
		}
		((Button) children[s_selectedComposite - 1]).setSelection(true);
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
			s_trayItem.setToolTipText(s_translations.getString("TRAY.TOOL.TIP"));
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
			menuPasswords.setText(s_translations.getString("TOGGLE.ACCOUNT"));
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
			menuEnc.setText(s_translations.getString("TOGGLE.ENCRYPT"));
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
			menuDec.setText(s_translations.getString("TOGGLE.DECRYPT"));
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
			menuShred.setText(s_translations.getString("TOGGLE.SHRED"));
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
			menuConfig.setText(s_translations.getString("TOGGLE.CONFIG"));
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
			menuInfo.setText(s_translations.getString("TOGGLE.INFO"));
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
			menuExit.setText(s_translations.getString("TOGGLE.EXIT"));
			menuExit.setImage(exitIcon);
			menuExit.addListener(SWT.Selection, new Listener()
			{
				public void handleEvent (Event event)
				{
					LOGGER.info("selection " + event.widget);
					if (guiHelper.displayQuestionMessage(s_translations.getString("MESSAGE.EXITING.TITLE"),s_translations.getString("MESSAGE.EXITING")))
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
