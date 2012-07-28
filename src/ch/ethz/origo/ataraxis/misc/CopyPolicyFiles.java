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

package ch.ethz.origo.ataraxis.misc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class CopyPolicyFiles 
{
	/**
	 * Logger for this class
	 */
	private static final Logger LOGGER = Logger.getLogger(CopyPolicyFiles.class);

	private static final String USER_DIR = System.getProperty("user.dir");
	private static final String APPL_DIR = USER_DIR + "/application_data";
	
	private static String s_JreSecurityPlace = System.getProperty("java.home")+"/lib/security/";
	private static String s_JreVersion = System.getProperty("java.version");
	
	private static String s_PatchedPolicyLoc = APPL_DIR+"/jce_policy/";
	
	private static String s_PatchedPolicyLoc_15 = APPL_DIR+"/jce_policy/";
	private static String s_PatchedPolicyLoc_16 = APPL_DIR+"/jce_policy/java6/";
	
	private static String s_local_policy = "local_policy.jar";
	private static String s_usexport_policy = "US_export_policy.jar";
	
	private static String text1 = "File created by AtaraxiS at: ";
	private static String text2 = "\n\nYour JRE has been patched with the unrestricted policy " +
			"files.\nNow you are able to use full strength encryption.\n\nBackup of your " +
			"original policy files:\n- bak_local_policy.jar\n- bak_US_export_policy.jar\n";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		BasicConfigurator.configure();
    	Logger.getRootLogger().setLevel(Level.ALL);
		CopyPolicyFiles cpPolicy = new CopyPolicyFiles();
		boolean wasOK = cpPolicy.copyFiles();
		LOGGER.info("Patch was ok? " + wasOK);
	}
	
	/**
	 * Only an empty constructor.
	 */
	public CopyPolicyFiles()
	{
		// empty constructor
	}
	
	/**
	 * Copy the Policy-Fils for Java 5 or Java 6 to the current JRE
	 * 
	 * @return true if all was ok, false otherwise
	 */
	public boolean copyFiles()
	{
		boolean patchWasOk = false;
		LOGGER.fatal("JRE path:    " + s_JreSecurityPlace);
		LOGGER.fatal("JRE version: " + s_JreVersion);
		if(s_JreVersion.startsWith("1.5") || s_JreVersion.startsWith("1.6"))
		{
			if(s_JreVersion.startsWith("1.5"))
			{
				s_PatchedPolicyLoc = s_PatchedPolicyLoc_15;
			}
			else if(s_JreVersion.startsWith("1.6"))
			{
				s_PatchedPolicyLoc = s_PatchedPolicyLoc_16;
			}
			else
			{
				LOGGER.fatal("Error on determing the Patch-File path.");
			}
			
			File jreSec = new File(s_JreSecurityPlace);
			File patchedAtaraxis = new File(jreSec.getAbsolutePath()+"/AtaraxiS_patch.txt").getAbsoluteFile();
			if(patchedAtaraxis.exists())
			{
				LOGGER.info("JRE is allready patched");
			}
			else
			{
				try 
				{
					// Files on the System
					File SysLocalPolicyFile = new File (s_JreSecurityPlace+s_local_policy);
					File SysExportPolicyFile = new File (s_JreSecurityPlace+s_usexport_policy);
					
					// Files for the patch
					File PatchLocalPolicyFile = new File (s_PatchedPolicyLoc+s_local_policy);
					File PatchExportPolicyFile = new File (s_PatchedPolicyLoc+s_usexport_policy);
					
					// Backup of System files
					File backupLocalPolicy = new File (s_JreSecurityPlace+"bak_"+s_local_policy);
					File backupUsExportPolicy = new File (s_JreSecurityPlace+"bak_"+s_usexport_policy);
					
					if(backupLocalPolicy.exists() || backupUsExportPolicy.exists())
					{
						LOGGER.error("Backup-Files allredy exist");
					}
					else
					{
						long sysLocalModified = SysLocalPolicyFile.lastModified();
						long sysExportModified = SysExportPolicyFile.lastModified();
						
						copy(SysLocalPolicyFile,backupLocalPolicy);
						copy(SysExportPolicyFile,backupUsExportPolicy);
						backupLocalPolicy.setLastModified(sysLocalModified);
						backupUsExportPolicy.setLastModified(sysExportModified);
						
						// reinitialise for the old paths
						SysLocalPolicyFile = new File (s_JreSecurityPlace+s_local_policy);
						SysExportPolicyFile = new File (s_JreSecurityPlace+s_usexport_policy);
						
						copy(PatchLocalPolicyFile, SysLocalPolicyFile);
						copy(PatchExportPolicyFile, SysExportPolicyFile);
						
						Date date = new Date();
						patchedAtaraxis.createNewFile();
						BufferedWriter out = new BufferedWriter(new FileWriter(patchedAtaraxis));
				        out.write(text1 + date.toString());
				        out.write(text2);
				        out.close();
				        patchedAtaraxis.setReadOnly();
				        patchWasOk = true;
					}
				} 
				catch (IOException ioe) 
				{
					LOGGER.error("IOException", ioe);
				}
			}
				
		}
		else
		{
			LOGGER.error("Can only patch Java JRE Version 1.5.x");		
		}
		
		return patchWasOk;
	}

    private static void copy(File src, File dst) throws IOException
    {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
      
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
}
