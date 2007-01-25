/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: IdlContainer.java,v 1.1 2005/01/24 14:47:20 teiniker Exp $
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
Common interface for all IDL containers.

@author Robert Lechner (rlechner@gmx.at)
@version January 2004
*/
interface IdlContainer extends Worker
{
	/**
	 * Like {@link Worker#createDependencyOrder}, but changes also
	 * the order of all parents.
	 *
	 * @param number the current number (>=1)
	 * @return the next number (>=current number)
	 */
	public int updateDependencyOrder( int number, Main main );

    /**
     * Returns the full path name.
     */
	public String getPathName();
}
