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
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.KeyStoreException;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.ethz.origo.ataraxis.crypt.AtaraxisCrypter;
import ch.ethz.origo.ataraxis.crypt.CryptoMethodError;
import ch.ethz.origo.ataraxis.crypt.JurisdictionPolicyError;
import ch.ethz.origo.ataraxis.misc.AtaraxisBackup;
import ch.ethz.origo.ataraxis.misc.CopyPolicyFiles;

/**
 * AtaraxisLoginGUI is used to instance the AtaraxisCrypter object.
 * The object is returned to AtaraxisStarter which passes it to AtaraxisMainGUI.
 * 
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 */
class AtaraxisLoginGUI {

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(AtaraxisLoginGUI.class);

	private static AtaraxisCrypter s_ac = null;
	protected static Properties s_langProps = null;
	
	private static int s_attempt= 0;
	private static int s_lang = 0;
	
	// language dependent Strings
	private static String s_windowTitle;
	private static String s_ok;
	private static String s_cancel;
	private static String s_newUser;
	private static String s_german;
	private static String s_french;
	private static String s_english;
	private static String s_user;
	private static String s_password;
	private static String s_passwordNew;
	private static String s_passwordNewRep;
	private static String s_language;
	private static String s_attemptText1;
	private static String s_attemptText2;
	
	private static String s_messageExitQuest;
	private static String s_messageCloseOnPwd1;
	private static String s_messageCloseOnPwd2;
	private static String s_messageCloseTitle;// used any time the application closes (title)
	private static String s_messageClose; // used any time the application closes (last sentence of message)
	private static String s_messagePolicy;
	private static String s_messageUnknownUserTitle;
	private static String s_messageUnknownUser;
	private static String s_messageIOETitle;
	private static String s_messageIOE;
	private static String s_messageNoUsernameTitle;
	private static String s_messageNoUsername;
	private static String s_messageNoPasswordTitle;
	private static String s_messageNoPasswordLogin;
	private static String s_messageNoPasswordAddUser;
	private static String s_messageExistentUsername;
	private static String s_messageInvalidUsernameTitle;
	private static String s_messageInvalidUsername;
	private static String s_messageInvalidPasswordTitle;
	private static String s_messageInvalidPassword;
	private static String s_messageAlreadyLoggedInTitle;
	private static String s_messageAlreadyLoggedIn;
	
	
	//	component constants:
	private static final int COMPOSITE_WIDTH = 240;
	private static final int COMPOSITE_HEIGHT= 230;
	private static final int LABEL_WIDTH = 94;
	private static final int BUTTON_WIDTH = 92;
	private static final int BUTTON_BIG_WIDTH = 189;
	
	// constants to compare with combobox value
	private static final int GERMAN = 0;
	private static final int FRENCH = 1;
	private static final int ENGLISH = 2;
	
	// max attempts for login
	private static final int MAX_ATTEMPTS = 3;
	
	// file path constants
	private static final String APPL_DIR = System.getProperty("user.dir");
	private static final String USER_DATA_DIR = APPL_DIR + "/user_data";
	private static final String APPL_DATA_DIR = APPL_DIR + "/application_data";
	private static final String ICON_DIR = APPL_DATA_DIR + "/icons";
	private static final String EMPTY_XML = APPL_DATA_DIR + "/user_data_template/template.xml";
	private static final String BACKUP_FILE = APPL_DATA_DIR + "/userBackup.zip";
	
	//keys for property file
	private static final String USER_LANG = "user.lang";

	// swt widgets
	private static Shell s_shell;
	private static Display s_display;
	private static Composite s_composite;  //stack-comp
	private static Composite s_loginComp;
	private static Composite s_useraddComp;
	private static Image s_icon;
	private static Combo s_useraddLangCombo = null;
	private static Button s_loginLoginButton = null;
	private static Button s_useraddLoginButton = null;
	private static StackLayout s_stackLayout;
	private static Date s_date;
	private static Label s_attemptsL = null;
    //private static Text s_loginUserText;
    private static Combo s_loginUserList;
	private static Text s_loginPwdText;
	private static Text s_userAddUserText;
	private static Text s_userAddPwdText;
	private static Text s_userAddPwdTextRep;

	/**
	 * Open the window
	 */
	public AtaraxisCrypter open() {
		loadProperties();
		s_display = Display.getDefault();
		createContents();
		changeBackgroundColor(s_shell, AtaraxisStarter.COLOR);
		s_shell.open();
		s_shell.layout();
		while (!s_shell.isDisposed())
		{
			if (!s_display.readAndDispatch())
				s_display.sleep();
		}
		return s_ac;
	}

	/**
	 * Create contents of the window
	 */
	protected void createContents()
	{
		s_shell = new Shell(SWT.CLOSE | SWT.TITLE | SWT.MIN);

		// Use only primary Monitor - important if 2 Displays
		Monitor primary = s_display.getPrimaryMonitor();
		final Rectangle displayRect = primary.getBounds();

        final int x = (displayRect.width - COMPOSITE_WIDTH) / 2;
        final int y = (displayRect.height - COMPOSITE_HEIGHT) / 2;
        s_shell.setSize(COMPOSITE_WIDTH, COMPOSITE_HEIGHT);
        s_shell.setLocation(x, y);
		s_shell.setText(s_windowTitle + " - Login");
		s_icon = new Image(s_display, ICON_DIR + "/Ataraxis_Title.png");
		s_shell.setImage(s_icon);
		s_shell.setLayout(new FillLayout());
		
		// Composit for Stack functionality
		s_composite = new Composite(s_shell, SWT.NONE);
		s_composite.setLocation(0, 0);
		//s_composite.setSize(234, 198);
		s_composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		s_stackLayout = new StackLayout();
		s_stackLayout.marginWidth = 20;
		s_stackLayout.marginHeight = 10;
		s_composite.setLayout(s_stackLayout);
		
		
 		// #####################################################################
		// ################# StackLayout - Composite Login (1) #################
		// #####################################################################
		
		s_loginComp = new Composite(s_composite, SWT.NONE);
		final GridLayout loginGridLayout = new GridLayout();
		loginGridLayout.numColumns = 2;
		s_loginComp.setLayout(loginGridLayout);
		s_loginComp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
		
		final Label loginUserLabel = new Label(s_loginComp, SWT.BOTTOM);
		loginUserLabel.setLayoutData(new GridData(LABEL_WIDTH, SWT.DEFAULT));
        loginUserLabel.setText(s_user + ":");
        s_loginUserList = new Combo(s_loginComp, SWT.BORDER);
        File userDir = new File(USER_DATA_DIR);
        FilenameFilter userFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) 
            {
            	
                return !name.startsWith(".")&&!name.equals("template");
            }
        };
        if (userDir.exists())
        {
        	s_loginUserList.setItems(userDir.list(userFilter));
        }
        //s_loginUserText = new Text(s_loginComp, SWT.BORDER);
        //s_loginUserText.setText("DDDD");
        final GridData gridData_4 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        s_loginUserList.setLayoutData(gridData_4);
		
        final Label loginPwdLabel = new Label(s_loginComp, SWT.BOTTOM);
        final GridData gridData_3 = new GridData(LABEL_WIDTH, SWT.DEFAULT);
        loginPwdLabel.setLayoutData(gridData_3);
        loginPwdLabel.setText(s_password + ":");
        s_loginPwdText = new Text(s_loginComp, SWT.BORDER);
        final GridData gridData_5 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        s_loginPwdText.setLayoutData(gridData_5);
        s_loginPwdText.setEchoChar('*');

		final Label loginSpaceLabel = new Label(s_loginComp, SWT.NONE);
		final GridData loginSpaceLabelGridData = new GridData(LABEL_WIDTH, 10);
		loginSpaceLabel.setLayoutData(loginSpaceLabelGridData);		
		
		s_attemptsL = new Label(s_loginComp, SWT.BOTTOM);  
		final GridData gridData_2 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gridData_2.widthHint = 174;
		gridData_2.heightHint = 21;
		s_attemptsL.setLayoutData(gridData_2);
		s_attemptsL.setSize(170, s_loginPwdText.getSize().x + 20);
		s_attemptsL.setForeground(new Color(s_display, 220, 0, 0));

		final Composite loginButtonComposite = new Composite(s_loginComp, SWT.NONE);
		final GridLayout loginGridLayout_1 = new GridLayout();
		loginGridLayout_1.marginWidth = 0;
		loginGridLayout_1.numColumns = 3;
		loginButtonComposite.setLayout(loginGridLayout_1);
		final GridData loginGridData_1 = new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1);
		loginGridData_1.widthHint = 188;
		loginGridData_1.heightHint = 73;
		loginButtonComposite.setLayoutData(loginGridData_1);

		final Button loginUseraddButton = new Button(loginButtonComposite, SWT.PUSH);
		final GridData gridData = new GridData(SWT.LEFT, SWT.CENTER, false, false, 3, 1);
		gridData.widthHint = BUTTON_BIG_WIDTH;
		loginUseraddButton.setLayoutData(gridData);
		loginUseraddButton.setText(s_newUser);
		loginUseraddButton.addSelectionListener(new SelectionAdapter()
		{
		    public void widgetSelected(SelectionEvent e)
		    {
		        LOGGER.debug("change to 'add User' Composite");
		        s_stackLayout.topControl = s_useraddComp;
		        s_useraddLangCombo.select(s_lang);
		        s_shell.setDefaultButton(s_useraddLoginButton);
		        s_composite.layout();
		    }
		});
		
		final Button loginExitButton = new Button(loginButtonComposite, SWT.NONE);
		loginExitButton.setLayoutData(new GridData(BUTTON_WIDTH, SWT.DEFAULT));
		loginExitButton.setText(s_cancel);
		loginExitButton.addSelectionListener(new SelectionAdapter()
		{
		    public void widgetSelected(SelectionEvent e)
		    {
		        LOGGER.debug("cancel button pressed (exit application)");
		        if(questMessage(s_messageCloseTitle, s_messageExitQuest))
		        	s_shell.dispose();
		    }
		});

		s_loginLoginButton = new Button(loginButtonComposite, SWT.PUSH);
		final GridData loginGridData_2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		loginGridData_2.widthHint = BUTTON_WIDTH;
		s_loginLoginButton.setLayoutData(loginGridData_2);
		s_loginLoginButton.setText(s_ok);
		s_loginLoginButton.addSelectionListener(new PasswordLoginButton());
		
        
        //System.out.println(s_pwdText.getText());
		s_shell.setDefaultButton(s_loginLoginButton);

		
 		// #####################################################################
		// ################ StackLayout - Composite Add User (2) ###############
		// #####################################################################
		
		s_useraddComp = new Composite(s_composite, SWT.NONE);
		final GridLayout useraddGridLayout = new GridLayout();
		useraddGridLayout.numColumns = 2;
		s_useraddComp.setLayout(useraddGridLayout);
		s_useraddComp.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
		
		final Label useraddUserLabel = new Label(s_useraddComp, SWT.BOTTOM);
		useraddUserLabel.setLayoutData(new GridData(LABEL_WIDTH, SWT.DEFAULT));
        useraddUserLabel.setText(s_user + ":");
        s_userAddUserText = new Text(s_useraddComp, SWT.BORDER);
        final GridData gridData_7 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        s_userAddUserText.setLayoutData(gridData_7);
		
        final Label useraddPwdLabel = new Label(s_useraddComp, SWT.BOTTOM);
        useraddPwdLabel.setLayoutData(new GridData(LABEL_WIDTH, SWT.DEFAULT));
        useraddPwdLabel.setText(s_passwordNew + ":");
        s_userAddPwdText = new Text(s_useraddComp, SWT.BORDER);
        final GridData gridData_9 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        s_userAddPwdText.setLayoutData(gridData_9);
        s_userAddPwdText.setEchoChar('*');
        
        final Label useraddPwdLabelRep = new Label(s_useraddComp, SWT.BOTTOM);
        useraddPwdLabelRep.setLayoutData(new GridData(LABEL_WIDTH, SWT.DEFAULT));
        useraddPwdLabelRep.setText(s_passwordNewRep + ":");
        s_userAddPwdTextRep = new Text(s_useraddComp, SWT.BORDER);
        final GridData gridData_8 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        s_userAddPwdTextRep.setLayoutData(gridData_8);
        s_userAddPwdTextRep.setEchoChar('*');

		final Label useraddLangLabel = new Label(s_useraddComp, SWT.NONE);
		useraddLangLabel.setLayoutData(new GridData(LABEL_WIDTH, SWT.DEFAULT));
		useraddLangLabel.setText(s_language + ":");

		s_useraddLangCombo = new Combo(s_useraddComp, SWT.NONE);
		final GridData useraddGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		s_useraddLangCombo.setLayoutData(useraddGridData);
		s_useraddLangCombo.setItems(new String [] {s_german, s_french, s_english});
		s_useraddLangCombo.select(s_lang);
		s_useraddLangCombo.addSelectionListener(new SelectionAdapter()
		{
		    public void widgetSelected(SelectionEvent e)
		    {
		    	s_lang = s_useraddLangCombo.getSelectionIndex();
		        if(s_lang == GERMAN)
		        	LOGGER.debug("language selected - german");
		        else if(s_lang == FRENCH)
		        	LOGGER.debug("language selected - french");
		        else if(s_lang == ENGLISH)
	        	LOGGER.debug("language selected - english");
		    }
		});
        
		final Label useraddSpace = new Label(s_useraddComp, SWT.BOTTOM);  
		final GridData gridData_6 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		gridData_6.heightHint = 14;
		useraddSpace.setLayoutData(gridData_6);

		final Composite useraddButtonComposite = new Composite(s_useraddComp, SWT.NONE);
		final GridLayout useraddGridLayout_1 = new GridLayout();
		useraddGridLayout_1.marginWidth = 0;
		useraddGridLayout_1.numColumns = 2;
		useraddButtonComposite.setLayout(useraddGridLayout_1);
		final GridData useraddGridData_1 = new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1);
		useraddGridData_1.widthHint = BUTTON_BIG_WIDTH;
		useraddGridData_1.heightHint = 41;
		useraddButtonComposite.setLayoutData(useraddGridData_1);

		final Button useraddExitButton = new Button(useraddButtonComposite, SWT.NONE);
		useraddExitButton.setLayoutData(new GridData(BUTTON_WIDTH, SWT.DEFAULT));
		useraddExitButton.addSelectionListener(new SelectionAdapter()
		{
		    public void widgetSelected(SelectionEvent e)
		    {
		        LOGGER.debug("Cancel 'user add' - return to login");
		        s_stackLayout.topControl = s_loginComp;
		        s_shell.setDefaultButton(s_loginLoginButton);
		        //s_loginLangCombo.select(s_lang);
		        s_composite.layout();
		    }
		});
		useraddExitButton.setText(s_cancel);
		
		s_useraddLoginButton = new Button(useraddButtonComposite, SWT.PUSH);
		final GridData gridData_1 = new GridData(BUTTON_WIDTH, SWT.DEFAULT);
		s_useraddLoginButton.setLayoutData(gridData_1);
		s_useraddLoginButton.setText(s_ok);
		s_useraddLoginButton.addSelectionListener(new AddUserButton());
		
		// if in 'add user mode' change to 'login mode' else ask to exit application
        s_composite.addListener(SWT.Traverse, new Listener()
        {
			public void handleEvent (Event event) 
			{
				if(event.detail == SWT.TRAVERSE_ESCAPE){
					if(s_stackLayout.topControl == s_loginComp)
					{
						LOGGER.debug("ESC pressed in 'user login' - exit application?");
				        if(questMessage(s_messageCloseTitle, s_messageExitQuest))
				        	s_shell.dispose();
					}
					else
					{
						LOGGER.debug("ESC pressed in 'user add' - change to 'user login'");
				        s_stackLayout.topControl = s_loginComp;
				        s_shell.setDefaultButton(s_loginLoginButton);
				        s_composite.layout();
					}
				}
			}
		});
		
		// important:  (gets changed by WindowBuilder !)
        s_stackLayout.topControl = s_loginComp;
        s_composite.layout();
    }
	
    /**
     * method which opens a window to ask if user really wants to exit 
     * the application.
     * @return true if user really wants to exit
     */
    private static boolean questMessage(String title, String message)
    {
        final MessageBox messageBox = new MessageBox(s_shell, SWT.ICON_QUESTION | SWT.YES | SWT.NO);
        messageBox.setMessage(message);
        messageBox.setText(title);
        return (messageBox.open() == SWT.YES);
    }
	
	/**
	 * method to load the content of the properties file and save the properties.
	 */
	private static void loadProperties()
	{
		// Get System Language of User:
		String systemLang = System.getProperty("user.language");
		if(systemLang == null)
		{
			LOGGER.warn("System Language could not be retrieved");
			systemLang = "en";
		}
			
		if(systemLang.equalsIgnoreCase("de"))
			systemLang = "langDE.props";
		else if(systemLang.equalsIgnoreCase("fr"))
			systemLang = "langFR.props";
		else if (systemLang.equalsIgnoreCase("en"))
			systemLang = "langEN.props";
		else
		{
			systemLang = "langEn.props";
			LOGGER.warn("System Language is not supported by application: use english");
		}

		s_langProps = new Properties();
		try
		{
			LOGGER.debug("trying to load language file: " + systemLang);
			s_langProps.load(new FileInputStream(APPL_DATA_DIR + "/lang/" + systemLang));
		}
		catch (FileNotFoundException fnfe)
		{
			LOGGER.error("File not found: " + systemLang);
		}
		catch (IOException ioe)
		{
			LOGGER.error("IOException during loading of: " + systemLang);
		}
		
		s_windowTitle = s_langProps.getProperty("WINDOW_TITLE", "AtaraxiS");
		s_ok = s_langProps.getProperty("OK", "OK");
		s_cancel = s_langProps.getProperty("CANCEL", "Cancel");
		s_newUser = s_langProps.getProperty("NEW.USER", "New user");
		s_german = s_langProps.getProperty("GERMAN", "German");
		s_french = s_langProps.getProperty("FRENCH", "French");
		s_english = s_langProps.getProperty("ENGLISH", "English");
		s_password = s_langProps.getProperty("PASSWORD", "Password") ;
		s_user = s_langProps.getProperty("USER", "User");
		s_language = s_langProps.getProperty("LANGUAGE", "Language");
		s_attemptText1 = s_langProps.getProperty("LOGIN.ATTEMPT.TEXT.1", "Number of attempts:");
		s_attemptText2 = s_langProps.getProperty("LOGIN.ATTEMPT.TEXT.2", "of");
		
		s_messageExitQuest = s_langProps.getProperty("MESSAGE.EXITING", "Do you really want " +
		"to quit this application?");
		s_messageCloseTitle = s_langProps.getProperty("MESSAGE.EXITING.TITLE", "Exiting application");
		s_messageClose = s_langProps.getProperty("LOGIN.MESSAGE.CLOSE", "The application will close down now.");
		s_messageCloseOnPwd1 = s_langProps.getProperty("LOGIN.MESSAGE.CLOSE.ON.PWD.1", "You have typed ");
		s_messageCloseOnPwd2 = s_langProps.getProperty("LOGIN.MESSAGE.CLOSE.ON.PWD.2", " times a wrong password.");
		s_messagePolicy = s_langProps.getProperty("LOGIN.MESSAGE.POLICY", "You have to change your java policy files to use this application.\nCopy the unrerstricted files now?\nRestart is required.");
		s_messageUnknownUserTitle = s_langProps.getProperty("LOGIN.MESSAGE.UNKNOWN.USER.TITLE", "Unknown user name");
		s_messageUnknownUser = s_langProps.getProperty("LOGIN.MESSAGE.UNKNOWN.USER", "No user with this name is registered.\nCheck if user name is correct or create a new user account.");
		s_messageIOETitle = s_langProps.getProperty("LOGIN.MESSAGE.IOE.TITLE", "I/O-Problem");
		s_messageIOE = s_langProps.getProperty("LOGIN.MESSAGE.IOE", "An i/o problem has occured. Please, try again.");
		s_messageNoUsernameTitle = s_langProps.getProperty("LOGIN.MESSAGE.NO.USERNAME.TITLE", "Enter a username");
		s_messageNoUsername = s_langProps.getProperty("LOGIN.MESSAGE.NO.USERNAME", "Please, enter a username.");
		s_messageNoPasswordTitle = s_langProps.getProperty("LOGIN.MESSAGE.NO.PASSWORD.TITLE", "Enter a password");
		s_messageNoPasswordLogin = s_langProps.getProperty("LOGIN.MESSAGE.NO.PASSWORD.LOGIN", "Please, enter the password.");
		s_messageNoPasswordAddUser = s_langProps.getProperty("LOGIN.MESSAGE.NO.PASSWORD.ADD.USER", "Please, enter the password two times.");
		s_messageExistentUsername = s_langProps.getProperty("LOGIN.MESSAGE.EXISTENT.USERNAME", "There already exists a user with this name.\nPlease, chose another name.");
		s_messageInvalidUsernameTitle = s_langProps.getProperty("LOGIN.MESSAGE.INVALID.USERNAME.TITLE", "Invalid user name");
		s_messageInvalidUsername = s_langProps.getProperty("LOGIN.MESSAGE.INVALID.USERNAME", "Please, use a name with a minimum length of 2 characters or digits.");
		s_messageInvalidPasswordTitle = s_langProps.getProperty("LOGIN.MESSAGE.INVALID.PASSWROD.TITLE", "Invalid password");
		s_messageInvalidPassword = s_langProps.getProperty("LOGIN.MESSAGE.INVALID.PASSWORD", "Please, type two times the same password with a minimum length of 4 characters.\nFurthermore, the password must diverse from the user name.");
		s_messageAlreadyLoggedInTitle = s_langProps.getProperty("LOGIN.MESSAGE.ALREADY.LOGGEDIN.TITLE", "Attention!");
		s_messageAlreadyLoggedIn = s_langProps.getProperty("LOGIN.MESSAGE.ALREADY.LOGGEDIN", "You are already logged in. Do you realy want to proceed?");
				
		if (systemLang.equals("langFR.props"))
		{
			// On Linux, the LoginGUI is too small to show string 'mot de passe (1)'
			s_passwordNew = s_password;
			s_passwordNewRep = s_password;
		}
		else
		{
			s_passwordNew = s_password + " (1)";
			s_passwordNewRep = s_password + " (2)";
		}

	}


    /**
     * @param name the name of the user
     * @param pwd the password of the user
     * @return true if password is correct
     * @throws IllegalPasswordException when MAX_ATTEMPTS reached
     */
    private static boolean checkPwd(String name, String pwd)
        throws IllegalPasswordException
    {
    	boolean ok = false;
    	final String path = USER_DATA_DIR + "/" + name + "/keystore.ks";
    	
        try
        {
        	LOGGER.debug("login in (checkPwd) as user "+ name + " with KS " + path);
            s_ac = new AtaraxisCrypter(new File(path), pwd.toCharArray(), false);
            ok = true;
            
            File loginFile = new File(USER_DATA_DIR + "/" + name + "/currentlyLoggedIn");
            if(loginFile.exists())
            {
      
            	LOGGER.warn("User allready logged in!");
            	
            	boolean procedeAllreadyLoggedIn = questMessage(s_messageAlreadyLoggedInTitle, s_messageAlreadyLoggedIn);
            	
            	if(procedeAllreadyLoggedIn)
            	{
            		// File exist, remove when stop (for the case that the file should not be there)
            		loginFile.deleteOnExit();
            		LOGGER.debug("proceeded with the login");
            	}
            	else
            	{
            		LOGGER.debug("stopped the login");
            		ok = false;
			s_ac = null;
            	}
            	
            }
            else
            {
            	// Normal flow: create File and let it remove on exit
            	boolean loginFileCreated = loginFile.createNewFile();
            	loginFile.deleteOnExit();
            	LOGGER.debug("LoginFile createt? "+ loginFileCreated);
            	
            }
        }
        catch (KeyStoreException kse)
        {
            if (s_attempt == MAX_ATTEMPTS -1)
                throw new IllegalPasswordException("password has been wrongly typed "
                        + MAX_ATTEMPTS + " times.");
            else if (kse.getMessage().startsWith("KeyStore does not exist"))
            	warningMessage(s_messageUnknownUserTitle, s_messageUnknownUser);

            s_attempt++;
            LOGGER.error("login failed - " + s_attempt  + " attempts of " + MAX_ATTEMPTS);
        }
        catch (IOException ioe)
        {
        	warningMessage(s_messageIOETitle, s_messageIOE);
        }
        catch (JurisdictionPolicyError jpe)
        {
        	LOGGER.debug("policy files are restricted (default JRE files)");
        	if(questMessage(s_messageCloseTitle, s_messagePolicy + " " + s_messageClose))
        	{
        		LOGGER.debug("copy unrestricted policy files now");
        		(new CopyPolicyFiles()).copyFiles();
        	}
        	s_date = new Date();
    		LOGGER.debug("##### Application close down at: " + s_date.toString() + " #####");
        	System.exit(0); // zero indicates a normal termination
		}
        return ok;
    }


	/**
     * @param name the name of the new user
     * @param pwd the password of the new user
     * @return true if keystore could be created
     */
    private static boolean addUser(String name, String pwd)
        throws IllegalPasswordException
    {
    	boolean worked = false;
    	final String accountPath = USER_DATA_DIR + "/" + name + "/accounts.data";
    	final String ksPath =      USER_DATA_DIR + "/" + name + "/keystore.ks";
    	
        try
        {
        	LOGGER.debug("adding new user (create new KS) at " + ksPath);
            s_ac = new AtaraxisCrypter(new File(ksPath), pwd.toCharArray(), true);
            s_ac.encryptFile(new File(EMPTY_XML), new File(accountPath));
            worked = true;
        }
        catch (KeyStoreException kse)
        {
        	LOGGER.error(kse.getMessage());
        }
        catch (IOException ioe)
        {
            LOGGER.error(ioe.getMessage());
        }
        catch (CryptoMethodError cme)
        {
        	LOGGER.error(cme.getMessage());
		}
        catch (JurisdictionPolicyError jpe)
        {
        	LOGGER.debug("policy files are restricted (default JRE files)");
        	if(questMessage(s_messageCloseTitle, s_messagePolicy + " " + s_messageClose))
        	{
        		LOGGER.debug("copy unrestricted policy files now");
        		(new CopyPolicyFiles()).copyFiles();
        	}
        	s_date = new Date();
    		LOGGER.debug("##### Application close down at: " + s_date.toString() + " #####");
        	System.exit(0); // zero indicates a normal termination
		}
        return worked;
    }
    
    /**
     * Method which shows an exit message, after MAX_ATTEMPTS of incorrect
     * password entries is reached.
     */
    private static void errorMessage(String title, String message)
    {
        final MessageBox messageBox = new MessageBox(s_shell, SWT.ICON_ERROR | SWT.OK);
        messageBox.setMessage(message);
        messageBox.setText(title);
        messageBox.open();
    }
    
    /**
     * Method which shows an exit message, after MAX_ATTEMPTS of incorrect
     * password entries is reached.
     */
    private static void warningMessage(String title, String message)
    {
        final MessageBox messageBox = new MessageBox(s_shell, SWT.ICON_WARNING | SWT.OK);
        messageBox.setMessage(message);
        messageBox.setText(title);
        messageBox.open();
    }
	
	/**
	 *  method to change the background color recursivly
	 */
	private void changeBackgroundColor(Composite parent, Color newColor){
		parent.setBackground(newColor);
		final Control [] children = parent.getChildren ();
		for (int i=0; i<children.length; i++)
		{
			//children[i].setBackground(newColor);
			if (children[i] instanceof Composite)
				changeBackgroundColor((Composite) children[i], newColor);
			else if (!(children[i] instanceof Text) && !(children[i] instanceof Combo))
				children[i].setBackground(newColor);	
		}
	}
	
    /**
     * This class is used to check if the password ist correct,
     * when the user selects the OK button.
     */
    private static final class PasswordLoginButton extends SelectionAdapter
    {
        private PasswordLoginButton()
        { }

        public void widgetSelected(SelectionEvent e)
        {
            try
            {
            	if (s_loginUserList.getText().equals(""))
            	{
            		warningMessage(s_messageNoUsernameTitle, s_messageNoUsername);
            	}
            	else if(s_loginPwdText.getText().equals(""))
            	{
            		warningMessage(s_messageNoPasswordTitle, s_messageNoPasswordLogin);
            	}
            	else
            	{
            		if(checkPwd(s_loginUserList.getText().toLowerCase(), s_loginPwdText.getText()))
                    {
                        s_shell.dispose();
                    }
                    else
                    {
                        s_attemptsL.setFocus();
                        s_attemptsL.setText(s_attemptText1 + " " + s_attempt +
                                " " + s_attemptText2 + " " + MAX_ATTEMPTS);
                        s_attemptsL.redraw();
                        s_loginPwdText.setFocus();
                        s_loginPwdText.selectAll();
                        s_composite.layout();
                    }
            	}
                
            }
            catch (IllegalPasswordException ipe)
            {
            	LOGGER.warn(MAX_ATTEMPTS + " failed logins - application shut down for security reasons");
                errorMessage(s_messageCloseTitle, s_messageCloseOnPwd1 + MAX_ATTEMPTS 
                		+ s_messageCloseOnPwd2 + "\n" + s_messageClose);
                System.exit(0); // zero indicates a normal termination
            }
        }
    }
    
    /**
     * This class is used to check if the password ist correct,
     * when the user selects the OK button.
     */
    private static final class AddUserButton extends SelectionAdapter
    {
        private AddUserButton()
        { }

        public void widgetSelected(SelectionEvent e)
        {
        	// validate entries (length, characters and identical passwords)
            boolean notNull = true;            
        	final String pwd = s_userAddPwdText.getText();
        	final String pwdRep = s_userAddPwdTextRep.getText();
    		final String user = s_userAddUserText.getText();
    		
        	if (user.equals(""))
        	{
        		notNull = false;
        		warningMessage(s_messageNoUsernameTitle, s_messageNoUsername);
        	}
        	else if (pwd.equals("") || pwdRep.equals(""))
        	{
        		notNull = false;
        		warningMessage(s_messageNoPasswordTitle, s_messageNoPasswordAddUser);
        	}
        	
        	if(notNull)
        	{
	        	boolean validPwd = pwd.equals(pwdRep);
	        	validPwd = validPwd && pwd.length() > 3; // pwd must have at least 4 chars
	        	validPwd = validPwd && !pwd.equalsIgnoreCase(user); // pwd must differ from username
	        	boolean validUser = user.length() > 1;  // pwd must have at least 2 chars
	        	validUser = validUser && user.matches("[a-z0-9]+"); // [a-zA-Z0-9]+ is incompatible with Linux
	        	
	        	if (validPwd && validUser)
	        	{
	                try
	                {
	                    if(addUser(user, pwd))
	                    {
	                    	final Properties userProps = new Properties();
	                    	if (s_lang == GERMAN)
	                    		userProps.setProperty(USER_LANG, "de");
	                    	else if (s_lang == FRENCH)
	                    		userProps.setProperty(USER_LANG, "fr");
	                    	else
	                    		userProps.setProperty(USER_LANG, "en");

	                    	final FileOutputStream userFile = new FileOutputStream(USER_DATA_DIR + "/" + user + "/user.props");
	                    	userProps.store(userFile, null);
	                    	
	                    	final AtaraxisBackup ataraxisBackup = new AtaraxisBackup(s_shell);
	                    	if (!ataraxisBackup.makeBackup(BACKUP_FILE))
	                    		LOGGER.warn("Could not make a backup of user_data!");
	                    	
	                    	LOGGER.debug("properties stored - closing login shell");
	                        s_shell.dispose();
	                    }
	                    else
	                    {
	                        warningMessage(s_messageInvalidUsernameTitle, s_messageExistentUsername);
	                    }
	                }
	                catch (FileNotFoundException fnfe)
	                {
	                	LOGGER.debug(fnfe.getMessage());
					}
	                catch (IOException ioe)
	                {
	                	LOGGER.debug(ioe.getMessage());
					}
	                catch (IllegalPasswordException ipe)
	                {
	                	// never happens for a new (not existing) user!
	                	LOGGER.warn(ipe.getMessage());
					}
	        	} // if - all entries are valid
	        	else if(!validUser)
	        	{
	        		warningMessage(s_messageInvalidUsernameTitle, s_messageInvalidUsername);
	        		s_userAddPwdText.setText("");
	        		s_userAddPwdTextRep.setText("");
	        		s_userAddPwdText.setFocus();
	        	}
	        	else
	        	{
	        		warningMessage(s_messageInvalidPasswordTitle, s_messageInvalidPassword);
	        		s_userAddPwdText.setText("");
	        		s_userAddPwdTextRep.setText("");
	        		s_userAddPwdText.setFocus();
	        	} // else - different passwords
        	} // if - no entry is null
        } // method widgetSelected()
    } // class AddUserButton
}
