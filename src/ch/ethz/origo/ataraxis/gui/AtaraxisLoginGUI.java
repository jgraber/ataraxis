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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.security.KeyStoreException;
import java.util.Properties;
import java.util.ResourceBundle;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ch.ethz.origo.ataraxis.crypt.AtaraxisCrypter;
import ch.ethz.origo.ataraxis.crypt.CryptoMethodError;
import ch.ethz.origo.ataraxis.misc.AtaraxisBackup;

/**
 * AtaraxisLoginGUI is used to instance the AtaraxisCrypter object.
 * The object is returned to AtaraxisStarter which passes it to AtaraxisMainGUI.
 * 
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.2
 */
class AtaraxisLoginGUI {

	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(AtaraxisLoginGUI.class);

	private static AtaraxisCrypter s_ac = null;
	
	private static int s_attempt= 0;
	private static int s_lang = 0;
	
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
	private Label s_attemptsL = null;
    private static Combo s_loginUserList;
	private static Text s_loginPwdText;
	private static Text s_userAddUserText;
	private static Text s_userAddPwdText;
	private static Text s_userAddPwdTextRep;

	private ResourceBundle s_translations;

	private GUIHelper guiHelper;

	public AtaraxisLoginGUI(ResourceBundle translations)
	{
		s_translations = translations;
	}
	
	/**
	 * Open the window
	 */
	public AtaraxisCrypter open() {
		s_display = Display.getDefault();
		createContents();
		guiHelper.changeBackgroundColor(s_shell, AtaraxisStarter.COLOR);
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

		guiHelper = new GUIHelper(s_shell);
		// Use only primary Monitor - important if 2 Displays
		Monitor primary = s_display.getPrimaryMonitor();
		final Rectangle displayRect = primary.getBounds();

        final int x = (displayRect.width - COMPOSITE_WIDTH) / 2;
        final int y = (displayRect.height - COMPOSITE_HEIGHT) / 2;
        s_shell.setSize(COMPOSITE_WIDTH, COMPOSITE_HEIGHT);
        s_shell.setLocation(x, y);
		s_shell.setText(s_translations.getString("WINDOW.TITLE") + " - Login");
		s_icon = new Image(s_display, ICON_DIR + "/Ataraxis_Title.png");
		s_shell.setImage(s_icon);
		s_shell.setLayout(new FillLayout());
		
		// Composite for Stack functionality
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
        loginUserLabel.setText(s_translations.getString("USER") + ":");
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
        loginPwdLabel.setText(s_translations.getString("PASSWORD") + ":");
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
		loginUseraddButton.setText(s_translations.getString("NEW.USER"));
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
		loginExitButton.setText(s_translations.getString("CANCEL"));
		loginExitButton.addSelectionListener(new SelectionAdapter()
		{
		    public void widgetSelected(SelectionEvent e)
		    {
		        LOGGER.debug("cancel button pressed (exit application)");
		        if(guiHelper.displayQuestionMessage(s_translations.getString("MESSAGE.EXITING.TITLE"), s_translations.getString("MESSAGE.EXITING")))
		        	s_shell.dispose();
		    }
		});

		s_loginLoginButton = new Button(loginButtonComposite, SWT.PUSH);
		final GridData loginGridData_2 = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		loginGridData_2.widthHint = BUTTON_WIDTH;
		s_loginLoginButton.setLayoutData(loginGridData_2);
		s_loginLoginButton.setText(s_translations.getString("OK"));
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
        useraddUserLabel.setText(s_translations.getString("USER") + ":");
        s_userAddUserText = new Text(s_useraddComp, SWT.BORDER);
        final GridData gridData_7 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        s_userAddUserText.setLayoutData(gridData_7);
		
        final Label useraddPwdLabel = new Label(s_useraddComp, SWT.BOTTOM);
        useraddPwdLabel.setLayoutData(new GridData(LABEL_WIDTH, SWT.DEFAULT));
        useraddPwdLabel.setText(s_translations.getString("PASSWORD") + " (1)" + ":");
        s_userAddPwdText = new Text(s_useraddComp, SWT.BORDER);
        final GridData gridData_9 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        s_userAddPwdText.setLayoutData(gridData_9);
        s_userAddPwdText.setEchoChar('*');
        
        final Label useraddPwdLabelRep = new Label(s_useraddComp, SWT.BOTTOM);
        useraddPwdLabelRep.setLayoutData(new GridData(LABEL_WIDTH, SWT.DEFAULT));
        useraddPwdLabelRep.setText(s_translations.getString("PASSWORD") + " (2)" + ":");
        s_userAddPwdTextRep = new Text(s_useraddComp, SWT.BORDER);
        final GridData gridData_8 = new GridData(SWT.FILL, SWT.CENTER, true, false);
        s_userAddPwdTextRep.setLayoutData(gridData_8);
        s_userAddPwdTextRep.setEchoChar('*');

		final Label useraddLangLabel = new Label(s_useraddComp, SWT.NONE);
		useraddLangLabel.setLayoutData(new GridData(LABEL_WIDTH, SWT.DEFAULT));
		useraddLangLabel.setText(s_translations.getString("LANGUAGE") + ":");

		s_useraddLangCombo = new Combo(s_useraddComp, SWT.NONE);
		final GridData useraddGridData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		s_useraddLangCombo.setLayoutData(useraddGridData);
		s_useraddLangCombo.setItems(new String [] {s_translations.getString("GERMAN"), s_translations.getString("FRENCH"), s_translations.getString("ENGLISH")});
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
		useraddExitButton.setText(s_translations.getString("CANCEL"));
		
		s_useraddLoginButton = new Button(useraddButtonComposite, SWT.PUSH);
		final GridData gridData_1 = new GridData(BUTTON_WIDTH, SWT.DEFAULT);
		s_useraddLoginButton.setLayoutData(gridData_1);
		s_useraddLoginButton.setText(s_translations.getString("OK"));
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
				        if(guiHelper.displayQuestionMessage(s_translations.getString("MESSAGE.EXITING.TITLE"), s_translations.getString("MESSAGE.EXITING")))
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
     * @param name the name of the user
     * @param pwd the password of the user
     * @return true if password is correct
     * @throws IllegalPasswordException when MAX_ATTEMPTS reached
     */
    private boolean checkPwd(String name, String pwd)
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
            	
            	boolean procedeAllreadyLoggedIn = guiHelper.displayQuestionMessage(s_translations.getString("LOGIN.MESSAGE.ALREADY.LOGGEDIN.TITLE"), s_translations.getString("LOGIN.MESSAGE.ALREADY.LOGGEDIN"));
            	
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
            	guiHelper.displayWarningMessage(s_translations.getString("LOGIN.MESSAGE.UNKNOWN.USER.TITLE"), s_translations.getString("LOGIN.MESSAGE.UNKNOWN.USER"));

            s_attempt++;
            LOGGER.error("login failed - " + s_attempt  + " attempts of " + MAX_ATTEMPTS);
        }
        catch (IOException ioe)
        {
        	guiHelper.displayWarningMessage(s_translations.getString("LOGIN.MESSAGE.IOE.TITLE"), s_translations.getString("LOGIN.MESSAGE.IOE"));
        }
        return ok;
    }


	/**
     * @param name the name of the new user
     * @param pwd the password of the new user
     * @return true if keystore could be created
     */
    private boolean addUser(String name, String pwd)
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
        
        return worked;
    }
   
	
    /**
     * This class is used to check if the password ist correct,
     * when the user selects the OK button.
     */
    private final class PasswordLoginButton extends SelectionAdapter
    {
        private PasswordLoginButton()
        { }

        public void widgetSelected(SelectionEvent e)
        {
            try
            {
            	if (s_loginUserList.getText().equals(""))
            	{
            		guiHelper.displayWarningMessage(s_translations.getString("LOGIN.MESSAGE.NO.USERNAME.TITLE"), s_translations.getString("LOGIN.MESSAGE.NO.USERNAME"));
            	}
            	else if(s_loginPwdText.getText().equals(""))
            	{
            		guiHelper.displayWarningMessage(s_translations.getString("LOGIN.MESSAGE.NO.PASSWORD.TITLE"), s_translations.getString("LOGIN.MESSAGE.NO.PASSWORD.LOGIN"));
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
                        s_attemptsL.setText(s_translations.getString("LOGIN.ATTEMPT.TEXT.1") + " " + s_attempt +
                                " " + s_translations.getString("LOGIN.ATTEMPT.TEXT.2") + " " + MAX_ATTEMPTS);
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
            	guiHelper.displayErrorMessage(s_translations.getString("MESSAGE.EXITING.TITLE"), s_translations.getString("LOGIN.MESSAGE.CLOSE.ON.PWD.1") + " " + MAX_ATTEMPTS 
                		+ " " + s_translations.getString("LOGIN.MESSAGE.CLOSE.ON.PWD.2") + "\n" + s_translations.getString("LOGIN.MESSAGE.CLOSE"));
                System.exit(0); // zero indicates a normal termination
            }
        }
    }
    
    /**
     * This class is used to check if the password ist correct,
     * when the user selects the OK button.
     */
    private final class AddUserButton extends SelectionAdapter
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
        		guiHelper.displayWarningMessage(s_translations.getString("LOGIN.MESSAGE.NO.USERNAME.TITLE"), s_translations.getString("LOGIN.MESSAGE.NO.USERNAME"));
        	}
        	else if (pwd.equals("") || pwdRep.equals(""))
        	{
        		notNull = false;
        		guiHelper.displayWarningMessage(s_translations.getString("LOGIN.MESSAGE.NO.PASSWORD.TITLE"), s_translations.getString("LOGIN.MESSAGE.NO.PASSWORD.ADD.USER"));
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
	                    	guiHelper.displayWarningMessage(s_translations.getString("LOGIN.MESSAGE.INVALID.USERNAME.TITLE"), s_translations.getString("LOGIN.MESSAGE.EXISTENT.USERNAME"));
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
	        		guiHelper.displayWarningMessage(s_translations.getString("LOGIN.MESSAGE.INVALID.USERNAME.TITLE"), s_translations.getString("LOGIN.MESSAGE.INVALID.USERNAME"));
	        		s_userAddPwdText.setText("");
	        		s_userAddPwdTextRep.setText("");
	        		s_userAddPwdText.setFocus();
	        	}
	        	else
	        	{
	        		guiHelper.displayWarningMessage(s_translations.getString("LOGIN.MESSAGE.INVALID.PASSWROD.TITLE"), s_translations.getString("LOGIN.MESSAGE.INVALID.PASSWORD"));
	        		s_userAddPwdText.setText("");
	        		s_userAddPwdTextRep.setText("");
	        		s_userAddPwdText.setFocus();
	        	} // else - different passwords
        	} // if - no entry is null
        } // method widgetSelected()
    } // class AddUserButton
}
