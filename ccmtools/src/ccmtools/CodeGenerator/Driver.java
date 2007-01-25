/* CCM Tools : Code Generator Library
 * Leif Johnson <leif@ambient.2y.net>
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

package ccmtools.CodeGenerator;

import java.util.Map;
import java.util.Set;

public interface Driver
{
    void graphStart();
    void graphEnd();

    void nodeStart(Object node, String scope_id);
    void nodeEnd(Object node, String scope_id);
    void nodeData(Object node, String name, Object value);

    void templateContents(String template);
    void outputVariables(Map variables);
    void currentVariables(Set variables);

    void message(Object value);

    void outputFile(String name);
}

