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

package ch.ethz.origo.ataraxis.misc;

/**
 * Exception for all errors on UpdateCheck.
 * 
 * @author J. Graber
 * @version 1.0
 *
 */
public class UpdateException extends Exception {

	/**
	 *  Serial Version
	 */
	private static final long serialVersionUID = 130196160744775576L;
	
	/**
	 * Default Constructor
	 */
	public UpdateException() 
	{
		super();
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UpdateException(String message, Throwable cause) 
	{
		super(message, cause);
	}

	/**
	 * @param message
	 */
	public UpdateException(String message) 
	{
		super(message);
	}

	/**
	 * @param cause
	 */
	public UpdateException(Throwable cause) 
	{
		super(cause);
	}

}
