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

public class MFixedDefImpl
    implements MFixedDef
{
    private final static MDefinitionKind definitionKind =
        MDefinitionKind.DK_FIXED;

    private final static MPrimitiveKind kind =
        MPrimitiveKind.PK_FIXED;

    private int digits_;
    private short scale_;
    private TypeCode typeCode_;

    public MFixedDefImpl()
    {
        digits_ = 0;
        scale_ = 0;
        typeCode_ = null;
    }

    // override toString()
    public String toString()
    {
    		return "MFixedDefImpl: ";
    }
    // required by MIDLType
    public String toString(Set been_there)
    		{ return toString(); }



    //----------------------------------------------------------------
    // implementation of attribute access
    //----------------------------------------------------------------

    // read-only attribute definitionKind:ccmtools.Metamodel.BaseIDL.MDefinitionKind
    public MDefinitionKind getDefinitionKind()  {return definitionKind;}

    // read-only attribute kind:PrimitiveKind
    public MPrimitiveKind getKind()             {return kind;}

    // attribute digits:unsigned short
    public int getDigits()                      {return digits_;}
    public void setDigits(int __arg)            {digits_ = __arg;}

    // attribute scale:short
    public short getScale()                     {return scale_;}
    public void setScale(short __arg)           {scale_ = __arg;}

    // attribute typeCode:TypeCode
    public TypeCode getTypeCode()               {return typeCode_;}
    public void setTypeCode(TypeCode __arg)     {typeCode_ = __arg;}
}

