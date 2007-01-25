/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: Worker.java,v 1.2 2005/02/21 10:58:30 rlechner Exp $
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package uml2idl;


/**
Common interface for all XMI elements of this package.

@author Robert Lechner (rlechner@gmx.at)
@version January 2004
*/
interface Worker
{
    /**
     * Puts itself and all children of type 'Worker' into the map.
     * The key value is the unique identifier.
     */
	public void collectWorkers( java.util.HashMap map );

	/**
	 * Returns an unique identifier.
	 */
	public String getId();

	/**
	 * Returns the name.
	 */
	public String getName();

	/**
	 * Creates all connections to children, parent and other elements.
	 */
	public void makeConnections( Main main, Worker parent );

	/**
	 * Calculates the order in which the elements will be written.
	 *
	 * @param number the current number (>=1)
	 * @return the next number (>=current number)
	 */
	public int createDependencyOrder( int number, Main main );

	/**
	 * Returns the number which has been calculated by 'createDependencyOrder'.
	 * If the return value is less than zero, the method 'createDependencyOrder'
	 * has never been called.
	 */
	public int getDependencyNumber();

	/**
	 * Returns the IDL code (terminated by a newline).
	 */
	public String getIdlCode( Main main, String prefix, StringBuffer includeStatements );

	/**
	 * Returns the OCL code (terminated by a newline).
	 */
	public String getOclCode( Main main );
}
