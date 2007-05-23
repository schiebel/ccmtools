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

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import ccmtools.Constants;
import ccmtools.utils.Text;

public class PythonTemplateManagerImpl
    implements TemplateManager
{
    private List source;

    /**
     * Initialize the class instance by locating a likely directory for
     * templates.
     * @param language the language to use for templates. This is used to
     *                 locate a likely template source.
     *                 ("CppLocalTemplates", "CppRemoteTemplates", etc).
     * @param out_path TODO
     */
    public PythonTemplateManagerImpl(String language, String out_path)
        throws IOException
    {
    	
    		source = new ArrayList();
    		int load_count = 1;
    		
    		System.out.println("Templates are loaded in the following order: ");
    		
    		// All templates are organized in directories that reflect
    		// the name of generated languages:
    		//   IDL3Templates/
    		//   CppLocalTemplates/
    		//   CppRemoteTemplates/
    		//   etc.
    		String templatesDir = language + "Templates";

    		// Option 1:
    		// Load templates from output/templates directory.
    		// This directory is used so that templates can be recursively expanded. 
    		// Should it be automatically cleaned at the start (probably so)?
    		// String directory = out_path + File.separator + "templates";
    		File recursive_root = new File(out_path, "templates");
    		if (recursive_root.exists() && recursive_root.isDirectory()) {
    			File recursive_src = new File(recursive_root.getPath(),templatesDir);
    			if ( ! recursive_src.exists() )
    				recursive_src.mkdir();
    			System.out.println("  (" + load_count + ") " + recursive_src + "  (recursive)");
    			source.add(recursive_src);
    			load_count++;
    		}
    		
    		// Option 2:
    		// Load templates from src/templates directory of the source tree.
    		// This directory is used as default path for easy development. 
    		String directory = System.getProperty("user.dir") + File.separator + 
					"src" + File.separator + "templates";
    		File user_src = new File(directory, templatesDir);
    		if (user_src.exists() && user_src.isDirectory()) {
    			System.out.println("  (" + load_count + ") " + user_src + "  (user)");
    			source.add(user_src);
    			load_count++;
    		}

    		// Option 2:
    		// Load templates from CCMTOOLS_HOME system property.
    		// This property is set, when calling the ccmtools-generate script,
    		// with a operationg system environment variable value.
    		directory = System.getProperty("CCMTOOLS_HOME") + File.separator + "templates";
    		File env_src = new File(directory, templatesDir);
    		if (env_src.exists() && env_src.isDirectory()) {
    			System.out.println("  (" + load_count + ") " + env_src + "  (CCMTOOLS_HOME)");
    			source.add(env_src);
    			load_count++;
    		}

    		// Option 3:
    		// Load templates from TEMPLATE_ROOT directory specified in 
    		// the Constants.java file.
    		// In the build process, the Constants.java file is generated
    		// from a Constants.java.in template by replacing @name@ tags
    		// with current informations.
    		File root_src = new File(Constants.TEMPLATE_ROOT, templatesDir);
    		if (root_src.exists() && root_src.isDirectory()) {
    			System.out.println("  (" + load_count + ") " + root_src + "  (root)");
    			source.add(root_src);
    			load_count++;
    		}

    		if (source.isEmpty()) {
    			// Stop code generation because there are no valid templates found.
    			throw new IOException("Error: No template source found for " + language);
    		}
    }


    /**
     * Load all templates for the given node type and locate the variables in
     * each template. Return a list of unique variable names from the templates.
     *
     * @param node_type the type of node to find a template for.
     */
    public Set getVariables(String node_type)
    {
        Set ret = new HashSet();
        List templates = loadTemplates(node_type);
        for (Iterator i = templates.iterator(); i.hasNext(); ) {
            Set variables = ((Template) i.next()).findVariables();
            for (Iterator j = variables.iterator(); j.hasNext(); ) {
                ret.add(j.next());
            }
        }
        return ret;
    }

    /**
     * Load the template for the given node type, find and replace short
     * variables with full scope identifiers, and return the fully scoped
     * template.
     *
     * @param node_type a string providing the name of the desired template.
     *                  This is usually the name of an interface from the CCM
     *                  MOF library, i.e. a string starting with 'M' and ending
     *                  with 'Def'. Sometimes there are modifiers on the end of
     *                  the name, however, such as 'MInterfaceDefLocal'.
     * @param scope_id The full scope identifier to use as a base name for
     *                 variable replacement. See the documentation for
     *                 CodeGeneratorHandlerImpl for information on the full
     *                 scope identifier.
     * @return a Template object representing the template found for the given
     *         node type. Can be null, if no template was found.
     */
    public Template getTemplate(String node_type, String scope_id)
    {
        Template template = getRawTemplate(node_type, scopeIdToFileName(scope_id));
        if (template == null) {
            return null;
        } else {
            template.scopeVariables(scope_id);
            return template;
        }
    }

    /**
	 * @param scope_id
	 * @return
	 */
	private String scopeIdToFileName(String scope_id) {
		String[] ret = scope_id.split("::");
		if (ret.length > 1)
			ret[0] = "";
		return Text.join("_",ret);
	}


	/**
     * Load and the template for the given node type.
     * @param node_type a string indicating the type of the node to find a
     *                  template for. This is usually the name of an interface
     *                  from the CCM MOF library, i.e. a string starting with
     *                  'M' and ending with 'Def'.
     *        scope is the file name scope descritor which may be added for
     * 					recursive template expansions
     *
     * @return a Template object with unscoped variables.
     */
    public Template getRawTemplate(String node_type, String scope)
    {
        List templates = loadTemplates(node_type);
        for (Iterator i = templates.iterator(); i.hasNext(); ) {
            Template template = (PythonTemplateImpl) i.next();
            if (template.getName().equals(node_type) ||
            		template.getName().equals(node_type + "_" + scope)) {
                return template;
            }
        }

        return null;
    }

    /**************************************************************************/

    /**
     * Load all templates matching the node type.
     *
     * @param node_type The type of the node to find a template for. This is
     *                  usually the name of an interface from the CCM MOF
     *                  library, i.e. a string starting with 'M' and ending with
     *                  'Def'.
     * @return  a (possibly empty) set of Template objects.
     */
    private List loadTemplates(String node_type)
    {
        List ret = new ArrayList();        
        for(Iterator src = source.iterator(); src.hasNext();) {
        		File src_dir = (File) src.next();
        		String[] candidates = src_dir.list();

			if ( candidates == null ) {
			    System.err.println(">>>>>>>>>> " + node_type + "\n");
			}
        		for (int i = 0; i < candidates.length; i++) {
        			File file = new File(src_dir, candidates[i]);
        			if (file.getName().startsWith(node_type)) {
        				try {
        					ret.add(new PythonTemplateImpl(file));
       				} catch (IOException e) {	 }
        			}
        		}
        }
        return ret;
    }
}

