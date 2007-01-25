/* CCM Tools : OCL generators
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003 Salomon Automation
 *
 * $Id: InterfaceIterator.java,v 1.2 2004/08/28 09:17:22 teiniker Exp $
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

package ccmtools.OCL.generators;

import java.util.Iterator;

import ccmtools.Metamodel.BaseIDL.MInterfaceDef;


class InterfaceIterator extends ClassIterator
{
    InterfaceIterator( MInterfaceDef obj, boolean recursive )
    {
        super();
        set(obj,recursive);
    }

    private void set( MInterfaceDef obj, boolean recursive )
    {
        Iterator it = obj.getBases().iterator();
        while( it.hasNext() )
        {
            MInterfaceDef base = (MInterfaceDef)it.next();
            add(base);
            if( recursive )
            {
                set(base,true);
            }
        }
    }
}

