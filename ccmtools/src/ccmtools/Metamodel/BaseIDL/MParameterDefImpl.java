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

public class MParameterDefImpl
    implements MParameterDef
{
    private final static MDefinitionKind definitionKind =
        MDefinitionKind.DK_PARAMETER;

    private MParameterMode direction_;
    private String identifier_;

    private MIDLType TypedBy_;
    private MOperationDef Operation_;

    // override toString()
    public String toString()
    {
	return "MParameterDef: "+ identifier_;
    }

    //----------------------------------------------------------------
    // implementation of attribute access
    //----------------------------------------------------------------

    // read-only attribute definitionKind:ccmtools.Metamodel.BaseIDL.MDefinitionKind
    public MDefinitionKind getDefinitionKind()  {return definitionKind;}

    // attribute direction:ParameterMode
    public MParameterMode getDirection()           {return direction_;}
    public void setDirection(MParameterMode __arg) {direction_ = __arg;}

    // attribute identifier:string
    public String getIdentifier()               {return identifier_;}
    public void setIdentifier(String __arg)     {identifier_ = __arg;}

    //----------------------------------------------------------------
    // implementation of navigation
    //----------------------------------------------------------------

    // association: direct role: [*] --> opposite role: idlType[1]
    public MIDLType getIdlType()                {return TypedBy_;}
    public void setIdlType(MIDLType __arg)      {TypedBy_ = __arg;}

    // association: direct role: parameter[*] <-> opposite role: operation[0..1]
    public MOperationDef getOperation()           {return Operation_;}
    public void setOperation(MOperationDef __arg) {Operation_ = __arg;}
}
