/* CCM Tools : CCM Metamodel Library
 * Egon Teiniker <egon.teiniker@tugraz.at>
 * Copyright (C) 2002, 2003 Salomon Automation
 *
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

package ccmtools.Metamodel.BaseIDL;

import org.omg.CORBA.TypeCode;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

public class MArrayDefImpl
    implements MArrayDef
{
    private final static MDefinitionKind definitionKind =
        MDefinitionKind.DK_ARRAY;

    private TypeCode typeCode_;
    private List bounds_;

    private MIDLType TypedBy_;

    public MArrayDefImpl()
    {
        TypedBy_ = null;
        typeCode_ = null;
	bounds_ = new ArrayList();
    }

    // override toString()
    public String toString()
    {
        String result = "MArrayDef: ";
        if (bounds_ != null) result += bounds_.toString();
        		return result;
    }
    // required by MIDLType
    public String toString(Set been_there)
    		{ return toString(); }


    //----------------------------------------------------------------
    // implementation of attribute access
    //----------------------------------------------------------------

    // read-only attribute definitionKind:ccmtools.Metamodel.BaseIDL.MDefinitionKind
    public MDefinitionKind getDefinitionKind()  { return definitionKind; }

    // attribute typeCode:TypeCode
    public TypeCode getTypeCode()               { return typeCode_; }
    public void setTypeCode(TypeCode __arg)     { typeCode_ = __arg; }

    // attribute bound:unsigned long (replaced with list of bounds)
    public List getBounds()                     { return bounds_; }
    public void setBounds(List __arg)           { bounds_ = new ArrayList(__arg); }
    public void addBound(Long __arg)            { bounds_.add(__arg); }
    public void removeBound(Long __arg)         { bounds_.remove(__arg); }

    //----------------------------------------------------------------
    // implementation of navigation
    //----------------------------------------------------------------

    // association: direct role: [*] --> opposite role: idlType[1]
    public MIDLType getIdlType()                { return TypedBy_; }
    public void setIdlType(MIDLType __arg)      { TypedBy_ = __arg; }
}
