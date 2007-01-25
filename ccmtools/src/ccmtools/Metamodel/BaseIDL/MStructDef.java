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

public interface MStructDef
    extends MTypedefDef
{
    // aggregation: direct role: [0..1] <-> opposite role: member[*]
    List getMembers();
    void setMembers(List __arg);
    void addMember(MFieldDef __arg);
    void removeMember(MFieldDef __arg);

    void addMember(int __pos, MFieldDef __arg);
    void removeMember(int __pos);
    void setMember(int __pos, MFieldDef __arg);
    MFieldDef getMember(int __pos);
}
