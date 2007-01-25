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

import java.util.List;

public interface MValueDef
    extends MContainer, MIDLType
{
    // attribute isAbstract:boolean
    boolean isAbstract();
    void setAbstract(boolean __arg);

    // attribute isCustom:boolean
    boolean isCustom();
    void setCustom(boolean __arg);

    // attribute isTruncatable:boolean
    boolean isTruncatable();
    void setTruncatable(boolean __arg);

    // association: direct role: [*] --> opposite role: abstractBase[*]
    List getAbstractBases();
    void setAbstractBases(List __arg);
    void addAbstractBase(MValueDef __arg);
    void removeAbstractBase(MValueDef __arg);

    // association: direct role: [*] --> opposite role: base[0..1]
    MValueDef getBase();
    void setBase(MValueDef __arg);

    // association: direct role: [*] --> opposite role: interfaceDef[0..1]
    MInterfaceDef getInterfaceDef();
    void setInterfaceDef(MInterfaceDef __arg);
}

