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

package ch.ethz.origo.ataraxis.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * The FileCopy copy one file to an other.
 * 
 * @author J. Graber & A. Muedespacher
 * @version 1.0
 *
 */
public class FileCopy 
{

	 /**   
     * copyFile is used to copy a file.
     * 
     * @throws IOException 
     */
    public static void copyFile(String orig, String copy) throws IOException
    {
        InputStream fis   = new FileInputStream(orig);
        OutputStream fout = new FileOutputStream(copy);
        
        byte[] buf = new byte[2048];
        int len;
        while ((len = fis.read(buf)) > 0)
        {
            fout.write(buf, 0, len);
        }
        fis.close();
        fout.close();
    }
}
