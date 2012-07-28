/* ----------------------------------------------------------------------------
 * Copyright 2009 - 2010 Johnny Graber & Andreas Muedespacher
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

package ch.ethz.origo.ataraxis.passwordmanager;

/**
 * The EntryDoesNotExistException will be thrown if the requested Entry does 
 * not exist.
 *
 * @author J. Graber & A. Muedespacher
 * @version 1.0
 */
public class EntryDoesNotExistException extends Exception {

	/**
     * Serial ID needed for Serialisation.
     */
    private static final long serialVersionUID = 3826455183296032686L;

    /**
     * Empty Version of Exception constructor.
     *
     */
    public EntryDoesNotExistException()
    {
    	// empty constructor
    }

    /**
     * Version with String for Exception constructor.
     *
     * @param message the message for the exception
     */
    public EntryDoesNotExistException(String message)
    {
        super(message);
    }

    /**
     * Version with throwable cause for Exception constructor.
     *
     * @param cause the throwable cause for the exception
     */
    public EntryDoesNotExistException(Throwable cause)
    {
        super(cause);
    }

    /**
     * Version with String and throwable cause for Exception constructor.
     *
     * @param message the message for the exception
     * @param cause the throwable cause for the exception
     */
    public EntryDoesNotExistException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
