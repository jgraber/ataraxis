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

package ataraxis.passwordmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import ataraxis.crypt.AtaraxisCrypter;


public class AtaraxisDiskAccessAdapter implements DiskAccess {

	private AtaraxisCrypter s_ac;
	
	public AtaraxisDiskAccessAdapter(AtaraxisCrypter ac)
	{
		s_ac = ac;
	}
	
	public InputStream getInputStream(File inputFile)
			throws FileNotFoundException, IOException 
	{
		if(inputFile == null || !inputFile.exists())
		{
			throw new FileNotFoundException("accountFile missing");
		}
		
		return s_ac.decryptInputStream(inputFile);
	}

	public OutputStream getOutputStream(File outputFile)
			throws FileNotFoundException, IOException
	{

		if(outputFile == null)
		{
			throw new FileNotFoundException("accountFile missing");
		}
		
		return s_ac.encryptOutputStream(outputFile);
	}

}
