/* CCM Tools : OCL generators
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003 Salomon Automation
 *
 * $Id: ComponentIterator.java,v 1.1 2004/07/20 16:21:17 teiniker Exp $
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

import ccmtools.Metamodel.BaseIDL.*;
import ccmtools.Metamodel.ComponentIDL.*;

import java.util.Iterator;


class ComponentIterator extends InterfaceIterator
{
    ComponentIterator( MComponentDef obj, boolean recursive )
    {
        super(obj,recursive);
        Iterator it = obj.getSupportss().iterator();
        while( it.hasNext() )
        {
            MInterfaceDef def = ((MSupportsDef)it.next()).getSupports();
            add(def);
            if( recursive )
            {
                add( getIterator(def,true) );
            }
        }
    }
}

