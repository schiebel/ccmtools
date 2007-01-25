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

public class MFieldDefImpl
    implements MFieldDef
{
    private final static MDefinitionKind definitionKind =
        MDefinitionKind.DK_FIELD;

    private String identifier_;

    private MIDLType TypedBy_;
    private MStructDef Structure_;
    private MExceptionDef Exception_;

    public MFieldDefImpl()
    {
        TypedBy_ = null;
        Structure_ = null;
        Exception_ = null;
    }

    // override toString()
    public String toString()
    {
	return "MFieldDef: " + identifier_ + " (" + TypedBy_.toString() + ")";
    }

    //----------------------------------------------------------------
    // implementation of attribute access
    //----------------------------------------------------------------

    // read-only attribute definitionKind:ccmtools.Metamodel.BaseIDL.MDefinitionKind
    public MDefinitionKind getDefinitionKind()  {return definitionKind;}

    // attribute identifier:string
    public String getIdentifier()               {return identifier_;}
    public void setIdentifier(String __arg)     {identifier_ = __arg;}

    //----------------------------------------------------------------
    // implementation of navigation
    //----------------------------------------------------------------

    // association: direct role member[*] <-> opposite role: structure[0..1]
    public MStructDef getStructure()            {return Structure_;}
    public void setStructure(MStructDef __arg)  {Structure_ = __arg;}

    // association: direct role member[*] <-> opposite role: exception[0..1]
    public MExceptionDef getException()           {return Exception_;}
    public void setException(MExceptionDef __arg) {Exception_ = __arg;}

    // association: direct role: [*] --> opposite role: idlType[1]
    public MIDLType getIdlType()                {return TypedBy_;}
    public void setIdlType(MIDLType __arg)      {TypedBy_ = __arg;}
}
