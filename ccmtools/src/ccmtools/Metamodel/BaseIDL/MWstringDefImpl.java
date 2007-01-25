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

import java.util.Set;

import org.omg.CORBA.TypeCode;

public class MWstringDefImpl
    implements MWstringDef
{
    private final static MDefinitionKind definitionKind =
        MDefinitionKind.DK_STRING;

    private final static MPrimitiveKind kind =
        MPrimitiveKind.PK_WSTRING;

    private Long bound_;
    private TypeCode typeCode_;

    // override toString()
    public String toString()
    {
    		return "MWstringDef: ";
    }
    public String toString( Set been_there )
    		{ return toString( ); }

    //----------------------------------------------------------------
    // implementation of attribute access
    //----------------------------------------------------------------

    // read-only attribute definitionKind:ccmtools.Metamodel.BaseIDL.MDefinitionKind
    public MDefinitionKind getDefinitionKind()  {return definitionKind;}

    // read-only attribute kind:PrimitiveKind
    public MPrimitiveKind getKind()             {return kind;}

    // attribute bound:unsigned long
    public Long getBound()                      {return bound_;}
    public void setBound(Long __arg)            {bound_ = __arg;}

    // attribute typeCode:TypeCode
    public TypeCode getTypeCode()               {return typeCode_;}
    public void setTypeCode(TypeCode __arg)     {typeCode_ = __arg;}
}

