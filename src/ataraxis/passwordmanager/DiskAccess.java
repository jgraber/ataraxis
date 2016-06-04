/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2010 Johnny Graber & Andreas Muedespacher
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

package ataraxis.passwordmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Interface to the handle the Disk Access
 * 
 * @author J. Graber
 * @version 1.0
 *
 */
public interface DiskAccess 
{

	/**
	 * Get a InputStream from the inputFile.
	 *
	 * @param inputFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	InputStream getInputStream(File inputFile) throws FileNotFoundException, IOException;
	
	
	/**
	 * Get a OutputStream from the outputFile.
	 *
	 * @param outputFile
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	OutputStream getOutputStream(File outputFile) throws FileNotFoundException, IOException;
}
