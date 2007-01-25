/* CCM Tools : OCL generators
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003 Salomon Automation
 *
 * $Id: ClassIterator.java,v 1.1 2004/07/20 16:21:17 teiniker Exp $
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

import java.util.Vector;


/**
 * An iterator for all base classes and supported interfaces of a class.
 *
 * @author Robert Lechner
 * @version 0.1
 */
public class ClassIterator
{
    /**
     * Creates the iterator for a given class.
     *
     * @param obj  the class
     * @param recursive  false=only base classes and supported interfaces, true=all inherited classes
     */
    public static ClassIterator getIterator( MContainer obj, boolean recursive )
    {
        if( obj!=null )
        {
            if( obj instanceof MValueDef )
            {
                return new ValueIterator( (MValueDef)obj, recursive );
            }
            if( obj instanceof MHomeDef )
            {
                return new HomeIterator( (MHomeDef)obj, recursive );
            }
            if( obj instanceof MComponentDef )
            {
                return new ComponentIterator( (MComponentDef)obj, recursive );
            }
            if( obj instanceof MInterfaceDef )
            {
                return new InterfaceIterator( (MInterfaceDef)obj, recursive );
            }
        }
        return new ClassIterator();
    }

    private Vector data_ = new Vector();
    private int index_;

    protected ClassIterator()
    {
        index_ = 0;
    }

    protected void add( MContainer obj )
    {
        data_.add(obj);
    }

    protected void add( ClassIterator ci )
    {
        data_.addAll( ci.data_ );
    }

    public boolean hasNext()
    {
        return index_<data_.size();
    }

    public MContainer next()
    {
        MContainer result = (MContainer)data_.get(index_);
        index_++;
        return result;
    }
}
