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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A template implementation based strongly on Python's printing operators.
 *
 * This class is used by code generator class family as an abstraction layer so
 * the template manager for each code generator does not have to worry about
 * exactly how to read template files from disk, locate variables in templates,
 * or substitute values for template variables.
 *
 * When templates are used (either written to disk or added to parent node
 * variables), a variation of Python's "%(key)s" % { 'key' : value } map-based
 * printing operator is used to substitute variable values from a map into the
 * template variable spots.
 *
 * Usually all template variables are replaced with full scope identifiers (but
 * see the two step functions in the CppGenerator family for a counterexample).
 * When these fully scoped template variables are combined with the full scope
 * identifier system of keys in the variable map, we are guaranteed to get
 * unique matches for each template.
 */
public class PythonTemplateImpl
    implements Template
{
    private final static Pattern key_regex =
        Pattern.compile("%\\((\\w+)\\)s");
    private final static Pattern scoped_key_regex =
        Pattern.compile("%\\((\\w[\\w:]+)\\)s");

    private Set<String> variables;
    private String name;
    private String template;

    /**
     * Create a new template object using the given file. Read the template in,
     * parse it for substitution variables, and set class instance variables
     * appropriately.
     *
     * @param file a file to read from.
     */
    public PythonTemplateImpl(File file)
        throws IOException
    {
        name = new String(file.getName());

        StringBuffer template_buffer = new StringBuffer();
        FileInputStream stream = new FileInputStream(file);
        InputStreamReader input = new InputStreamReader(stream);
        BufferedReader reader = new BufferedReader(input);
        String line = null;
        while ((line = reader.readLine()) != null) {
            template_buffer.append(line + "\n");
        }
        template = template_buffer.toString();

        // chop off the last newline. don't want to artificially make this
        // template longer, e.g. if it was only one line (without terminating
        // newline) to begin with.

        if (template.length() > 0)
            template = template.substring(0, template.length() - 1);

        variables = null;
        variables = findVariables();
    }

    /**
     * Copy constructor
     * 
     * @param other object to be copied
     */
    public PythonTemplateImpl( PythonTemplateImpl other )
    {
    	name = new String(other.name);
    	template= new String(other.template);
    	variables = new HashSet<String>(other.variables);
    }

	/**
     * Simple access function for name.
     *
     * @return the name of the file that the current template was loaded from.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Simple access function for template.
     *
     * @return the contents of the template as a string.
     */
    public String getTemplate()
    {
        return template;
    }

    /**
     * Locate variables (matching "%([A-Z][A-Za-z]*)s") in the template.
     *
     * @return a collection of variables found in this template.
     */
    public Set<String> findVariables()
    {
        if (variables != null) {
            return variables;
        } else {
            Set<String> ret = new HashSet<String>();
            Matcher m = key_regex.matcher(template);
            while (m.find()) {
                ret.add(m.group(1));
            }
            return ret;
        }
    }

    /**
     * Replace short variables in the current template with full scope
     * identifier variables.
     *
     * @param scope_id the full scope identifier to use as a base name for
     *                 variable replacement. See the documentation for
     *                 CodeGeneratorHandlerImpl for information on the full
     *                 scope identifier.
     */
    public void scopeVariables(String scope_id)
    {
        for (Iterator<String> i = variables.iterator(); i.hasNext(); ) {
            String var  = i.next();
            String from = "%\\(" + var + "\\)s";
            String to   = "%(" + scope_id + "::" + var + ")s";
            template = template.replaceAll(from, to);
        }
    }

    /**
     * Replace keys in the current template with variables from the given map.
     *
     * @param var_map a map filled with variables. The keys should be full scope
     *        identifiers for the corresponding values.
     * @return the fully substituted template.
     */
    public String substituteVariables(Map var_map)
    {
        String ret = new String(template);

        if (var_map == null) return ret;

        Matcher m = scoped_key_regex.matcher(ret);

        while (m.find()) {
            String key = m.group(1);
            if (var_map.containsKey(key)) {
                Object value = var_map.get(key);
                String replacement = value.toString().replaceAll("\\\\","\\\\\\\\");
                replacement = replacement.replaceAll("\\$","&#36;");
                ret = ret.replaceAll("%\\(" + key + "\\)s", replacement);
                ret = ret.replaceAll("&#36;","\\$");
            } else {
                throw new RuntimeException(
                    "Key " + key + " in template " + name + " has no value");
            }
        }

        return ret;
    }
}

