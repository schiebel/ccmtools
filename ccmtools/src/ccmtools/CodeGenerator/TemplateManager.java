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

import java.util.Set;

/**
 * Template managers provide an interface between code generators and a set of
 * code generation templates. Template managers are responsible for retrieving
 * templates of a particular name from the template set, finding the variables
 * in the templates, and performing various substitution and string retrieval
 * tasks.
 */
public interface TemplateManager
{
    /**
     * Retrieve the variables from all templates starting with a given name. The
     * name of the CCM metamodel class is used as the type of the graph nodes,
     * and templates are named correspondingly. So, for example, if a node type
     * of "MInterfaceDef" is given, and there are templates called
     * "MInterfaceDefA" and "MInterfaceDefB" in the current template set, then
     * the template manager will return a list of all variables in both the A
     * and B templates.
     *
     * @param node_type the type of node to find templates for. Normally this is
     *        an interface type in the CCM metamodel.
     * @return a set of variables that occur in all templates whose names start
     *         with the given node type.
     */
    Set getVariables(String node_type);
    /**
     * Retrieve the template associated with a particular node type. If there
     * are multiple templates whose names start with the node type, retrieve the
     * template that seems most appropriate (normally the closest string match).
     * The template will have its variables augmented with the scope identifier
     * given.
     *
     * @param node_type the type of node to find a template for.
     * @param scope_id the full scope identifier to use when adjusting the
     *        variable names in the template.
     * @return a template object containing the desired template information.
     */
    Template getTemplate(String node_type, String scope_id);

    /**
     * Retrieve the template associated with the particular node type without
     * performing any variable substitution on the template variables. This is
     * normally used for internal generator tasks when the generator must do a
     * local (i.e. not based on the graph traversal) template substitution. See
     * the C++ generator family for examples.
     * @param node_type the type of node to find a template for.
     * @param scope TODO
     *
     * @return a template object containing the raw template information.
     */
    Template getRawTemplate(String node_type, String scope);
}

