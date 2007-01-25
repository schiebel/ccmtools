/* CCM Tools : OCL generators
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003 Salomon Automation
 *
 * $Id: ValueIterator.java,v 1.2 2004/08/28 09:17:22 teiniker Exp $
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
import ccmtools.Metamodel.BaseIDL.MValueDef;


class ValueIterator extends ClassIterator
{
    ValueIterator( MValueDef obj, boolean recursive )
    {
        super();
        set(obj,recursive);
    }

    private void set( MValueDef obj, boolean recursive )
    {
        MValueDef base = obj.getBase();
        if( base!=null )
        {
            add(base);
            if( recursive )
            {
                set(base,true);
            }
        }
        Iterator it = obj.getAbstractBases().iterator();
        while( it.hasNext() )
        {
            base = (MValueDef)it.next();
            add(base);
            if( recursive )
            {
                set(base,true);
            }
        }
        MInterfaceDef def = obj.getInterfaceDef();
        if( def!=null )
        {
            add(def);
            if( recursive )
            {
                add( getIterator(def,true) );
            }
        }
    }
}
