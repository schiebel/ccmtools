/* CCM Tools : IDL3 Parser
 * Edin Arnautovic <edin.arnautovic@salomon.at>
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

package ccmtools.IDL3Parser;

import ccmtools.Metamodel.BaseIDL.MArrayDef;

class DeclaratorHelper{
    private MArrayDef arrayDef = null;
    private String declarator = null;

    public boolean isArray()
    {
	return (arrayDef != null);
    }

    public MArrayDef getArray()
    {
	return arrayDef;
    }

    public void setArray(MArrayDef arrayDef)
    {
	this.arrayDef = arrayDef;
    }

    public String getDeclarator()
    {
	return declarator;
    }

    public void setDeclarator(String declarator)
    {
	this.declarator = declarator;
    }
}
