/* ----------------------------------------------------------------------------
 * Copyright 2006 - 2016 Johnny Graber & Andreas Muedespacher
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
 * The JurisdictionPolicyError will be thrown if the JurisdictionPolicy files 
 * are restricted (you have to replace them with the unrestricted version).
 *
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 */
public class JurisdictionPolicyError extends Exception
{
    /**
     * Serial ID needed for Serialisation.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Empty Version of Exception constructor.
     *
     */
    public JurisdictionPolicyError()
    {
    	// empty constructor
    }

    /**
     * Version with String for Exception constructor.
     *
     * @param message the message for the exception
     */
    public JurisdictionPolicyError(String message)
    {
        super(message);
    }

    /**
     * Version with throwable cause for Exception constructor.
     *
     * @param cause the throwable cause for the exception
     */
    public JurisdictionPolicyError(Throwable cause)
    {
        super(cause);
    }

    /**
     * Version with String and throwable cause for Exception constructor.
     *
     * @param message the message for the exception
     * @param cause the throwable cause for the exception
     */
    public JurisdictionPolicyError(String message, Throwable cause)
    {
        super(message, cause);
    }
}
