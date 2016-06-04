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

package ataraxis.crypt;

/**
 * The CryptoMethodError will be throw whenever a Error in a CryptoMethod occurs.
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 */
public class CryptoMethodError extends Exception
{
    /**
     * Serial ID needed for Serialisation.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Empty Version of Exception constructor.
     *
     */
    public CryptoMethodError()
    {
    	// empty constructor
    }

    /**
     * Version with String for Exception constructor.
     *
     * @param message the message for the exception
     */
    public CryptoMethodError(String message)
    {
        super(message);
    }

    /**
     * Version with throwable cause for Exception constructor.
     *
     * @param cause the throwable cause for the exception
     */
    public CryptoMethodError(Throwable cause)
    {
        super(cause);
    }

    /**
     * Version with String and throwable cause for Exception constructor.
     *
     * @param message the message for the exception
     * @param cause the throwable cause for the exception
     */
    public CryptoMethodError(String message, Throwable cause)
    {
        super(message, cause);
    }
}
