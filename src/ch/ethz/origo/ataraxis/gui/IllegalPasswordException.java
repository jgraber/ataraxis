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

/**
 * The IllegalPasswordException will be throw whenever the maximum allowed 
 * attempts are overstepped.
 * 
 * @author J. Graber & A. Muedespacher, HTI Biel
 * @version 1.0
 */
public class IllegalPasswordException extends Exception
{
    /**
     * 
     */
    private static final long serialVersionUID = 1136760546097879971L;

    public IllegalPasswordException()
    {
    	// empty constructor
    }

    public IllegalPasswordException(String message)
    {
        super(message);
    }

    public IllegalPasswordException(Throwable cause)
    {
        super(cause);
    }

    public IllegalPasswordException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
