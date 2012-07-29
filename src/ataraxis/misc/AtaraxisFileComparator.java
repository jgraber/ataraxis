/* ----------------------------------------------------------------------------
 * Copyright 2007 - 2010 Johnny Graber & Andreas Muedespacher
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

package ataraxis.misc;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import ataraxis.util.FileList;


/**
 * The AtaraxisFileComparator compare 2 files or directories on their content.
 * 
 * @author J. Graber & A. Muedespacher
 * @version 1.0
 *
 */
public class AtaraxisFileComparator 
{
	
	private AtaraxisHashCreator hashCreator;
	private static final Logger LOGGER = Logger.getLogger(AtaraxisFileComparator.class);
	
	/**
	 * Empty Constructor for the AtaraxisFileComparator. It prepare the AtaraxisHashCreator
	 * which will be used if the file have the same size and the files could be the same.
	 */
	public AtaraxisFileComparator()
	{
		try 
		{
			LOGGER.debug("Start initialisation of the AtaraxisFileComparator()");
			hashCreator = new AtaraxisHashCreator(HashingDigest.SHA_256);
			LOGGER.debug("AtaraxisFileComparator is ready, AtaraxisHashCreator works with "
					+ hashCreator.getAlgorithm());
		} 
		catch (NoSuchAlgorithmException e) 
		{
			LOGGER.fatal(e.getMessage());
		}
	}
	
	/**
	 * Compare 2 Files or Directories if their content is equal.
	 *
	 * @param firstFile the first file or directory
	 * @param secondFile the second file or directory
	 * @return true if first contains the same data as the second
	 * @throws IOException if one of the files/directories can not be opened
	 */
	public boolean areFilesEquals(File firstFile, File secondFile) throws IOException
	{
		boolean areEquals = false;
		
		if(firstFile.isFile() && secondFile.isFile())
		{
			areEquals = compareFiles(firstFile, secondFile);
		}
		else if (firstFile.isDirectory() && secondFile.isDirectory())
		{
			areEquals = compareDirectories(firstFile, secondFile);
		}
		
		return areEquals;	
	}
	
	/**
	 * Check if 2 files are equals.
	 *
	 * @param firstFile the first file
	 * @param secondFile the second file
	 * @return true if first contains the same data as the second
	 * @throws IOException if one of the files can not be opened
	 */
	private boolean compareFiles(File firstFile, File secondFile) throws IOException
	{
		LOGGER.debug("compareFiles(File,File) startet");
		
		boolean areFilesEquals = false;
		if (firstFile.equals(secondFile))
		{
			areFilesEquals = true;
		}
		else if(firstFile.length() == secondFile.length())
		{
			String checksumA = AtaraxisHashCreator.prettyPrintHash(
					hashCreator.createHashForFile(firstFile));
			String checksumB = AtaraxisHashCreator.prettyPrintHash(
					hashCreator.createHashForFile(secondFile));
			
			if(checksumA.equals(checksumB))
			{
				areFilesEquals = true;
			}
		}
		
		LOGGER.debug("compareFiles(File,File) returned " + areFilesEquals);
		return areFilesEquals;
	}
	
	/**
	 * Check if 2 directories are equals.
	 *
	 * @param firstDirectory first Directory
	 * @param secondDirectory second Directory
	 * @return true if first and second contains the same data
	 * @throws IOException if one of the files/directories can not be opened
	 */
	private boolean compareDirectories(File firstDirectory, File secondDirectory) throws IOException
	{
		LOGGER.debug("compareDirectories(Dir, Dir) startet");
		boolean areDirectoriesEquals = false;
		
		if(firstDirectory.equals(secondDirectory))
		{
			areDirectoriesEquals = true;
		}
		else 
		{
			FileList fileLister = new FileList();
			List<File> fileListA = fileLister.getFileList(firstDirectory.getAbsolutePath());
			List<File> fileListB = fileLister.getFileList(secondDirectory.getAbsolutePath());
			List<String> entriesA = new ArrayList<String>();
			List<String> entriesB = new ArrayList<String>();;
			List<String> ListAwithoutB, ListBwithoutA;
			LOGGER.debug("A: "+fileListA.size()+" -- B: "+ fileListB.size());
			

			
			if(fileListA.size() == fileListB.size())
			{
				for (File f : fileListA)
				{
					String a = f.getAbsolutePath().replace(firstDirectory.getAbsolutePath(), "");
					entriesA.add(a);
				}
				
				for (File f : fileListB)
				{
					String a = f.getAbsolutePath().replace(secondDirectory.getAbsolutePath(), "");
					entriesB.add(a);
				}
				
				
				ListAwithoutB = new ArrayList<String>(entriesA);
				ListAwithoutB.removeAll(entriesB);
				ListBwithoutA = new ArrayList<String>(entriesB);
				ListBwithoutA.removeAll(entriesA);
				LOGGER.debug(ListBwithoutA.size()+" - "+ ListAwithoutB.size());
				if((ListBwithoutA.size() == 0) && (ListAwithoutB.size() == 0))
				{
					boolean fileListsEquals = true;
					File testA, testB;
					for (String s : entriesA)
					{
						testA = new File(firstDirectory+s);
						testB = new File(secondDirectory+s);
						if (testA.isFile())
						{
							fileListsEquals &= compareFiles(testA,testB);
						}
						else
						{
							fileListsEquals &= testA.isDirectory() && testB.isDirectory();
						}
						LOGGER.debug(fileListsEquals + " after " + testA.getAbsolutePath());
					}
					areDirectoriesEquals = fileListsEquals;					
				}
			}
		}
		LOGGER.debug("compareDirectories(Dir, Dir) endet with " + areDirectoriesEquals);
		return areDirectoriesEquals;
	}
}
