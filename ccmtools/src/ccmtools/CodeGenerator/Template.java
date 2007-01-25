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

/**
 * Templates form the information base of code generation. This interface
 * provides a thin layer of abstraction over the filesystem to allow a template
 * manager object to quickly load a given template for a given language.
 * Templates also provide functionality for finding and substituting template
 * variables.
 */
public interface Template
{
    /**
     * Retrieve the name associated with a template. This is the name of the
     * template file from which the template was loaded.
     *
     * @return the name of the file from which the template was loaded.
     */
    String getName();

    /**
     * Retrieve the template information as it currently exists in this template
     * object. Template information can be changed through calls to the
     * scopeVariables and substituteVariables functions.
     *
     * @return a string containing the template information in this template
     *         object.
     */
    String getTemplate();

    /**
     * Get a collection of all variables that occur in this template. (Variables
     * are sometimes also referred to as keys in the CCM Tools documentation.)
     * Variables in a template are delimited with %( )s (this comes from the
     * original CCM Tools implementation in Python). So, for example, if %(a)s
     * and %(foo)s occur in a template, this function will return ["a", "foo"].
     *
     * @return a collection of variable names that occur in this template. This
     *         will be a list of strings.
     */
    Set findVariables();

    /**
     * Replace all variables in the current template with a "full scope
     * identifier". This is basically the variable name prefixed with a double
     * colon (::) and the scope identifier given as an argument. This allows
     * code generator implementations to replace template variables based on the
     * full scope identifier of the nodes in a CCM metamodel graph.
     *
     * @param scope_id the scope identifier to use as a prefix for variables in
     *        the template.
     */
    void scopeVariables(String scope_id);

    /**
     * Substitute variables in the template with their associated values, based
     * on the given map. If a variable in the template is not found as a key in
     * the map, an exception is thrown.
     *
     * @param variables a map of variable names to variable values. Values in
     *        this map can contain the (variable-substituted) contents of other
     *        templates.
     * @return the template contents as a string after performing variable
     *         substitution.
     */
    String substituteVariables(Map variables);
}

