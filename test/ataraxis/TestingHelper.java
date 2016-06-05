/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2016 Johnny Graber & Andreas Muedespacher
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

package ataraxis;

import java.io.File;

public class TestingHelper 
{
    /**   
     * Deletes all files and subdirectories under dir.
     * Returns true if all deletions were successful.
     * If a deletion fails, the method stops attempting to delete and returns false.
     * 
     * @param dir the directory (File) to delete
     */
    public static void deleteDir(File dir)
    {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++)
            {
                deleteDir(new File(dir, children[i]));
            }
        }
        // The directory is now empty so delete it
        dir.delete();
    }
    

    /**
     * Remove file if it exist
     * @param fileName
     */
    public static void removeFileIfExist(String fileName)
	{
		File origFile = new File(fileName);
		if(origFile.exists())
		{
			origFile.delete();
		}
	}
}
