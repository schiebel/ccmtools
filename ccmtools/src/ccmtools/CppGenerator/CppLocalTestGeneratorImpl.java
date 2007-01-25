/* CCM Tools : C++ Code Generator Library
 * Leif Johnson <leif@ambient.2y.net>
 * Egon Teiniker <egon.teiniker@salomon.at>
 * Copyright (C) 2002, 2003, 2004 Salomon Automation
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

package ccmtools.CppGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

import ccmtools.CodeGenerator.Driver;
import ccmtools.CodeGenerator.Template;
import ccmtools.Metamodel.BaseIDL.MContained;
import ccmtools.Metamodel.BaseIDL.MOperationDef;
import ccmtools.Metamodel.ComponentIDL.MComponentDef;

public class CppLocalTestGeneratorImpl
    extends CppGenerator
{
    // types for which we have a global template ; that is, a template that is
    // not contained inside another template.

    private final static String[] local_output_types =
    { "MComponentDef" };

    /**************************************************************************/

    public CppLocalTestGeneratorImpl(Driver d, File out_dir)
        throws IOException
    {
        super("CppLocalTest", d, out_dir, local_output_types);
        base_namespace.add("CCM_Local");
    }

    /**
     * Write generated code to an output file.
     *
     * @param template the template object to get the generated code structure
     *        from ; variable values should come from the node handler object.
     */
    public void writeOutput(Template template)
        throws IOException
    {
        String generated_code = 
	    prettifyCode(template.substituteVariables(output_variables));

        if(generated_code.trim().equals("")) 
	    return;

        String node_name = ((MContained) current_node).getIdentifier();
        String file_dir = "test";

        String file_name = handleNamespace("IncludeNamespace", node_name);
        file_name = file_name.replaceAll("[^\\w]", "_");
        file_name = "_check_" + file_name + ".cc";
	
	File outFile = new File(output_dir 
				+ File.separator
				+ file_dir, file_name);
	if(outFile.isFile()) {
	    if(!isCodeEqualWithFile(generated_code, outFile)) {
		System.out.println("WARNING: " 
				   + outFile
				   + " already exists!");
		file_name += ".new";
		outFile = new File(output_dir 
				   + File.separator
				   + file_dir, file_name);
	    }
	}
	
	if(isCodeEqualWithFile(generated_code, outFile)) {
	    System.out.println("skipping " + outFile);
	}
	else {
	    writeFinalizedFile(file_dir, file_name, generated_code);
	}

        File makefile = new File(file_dir, "Makefile.py");
        File check_file = new File(output_dir, makefile.toString());
        if (! check_file.isFile())
            writeFinalizedFile(file_dir, "Makefile.py", "");
    }

    /**************************************************************************/

    protected String data_MComponentDef(String data_type, String data_value)
    {
        if (data_type.equals("UsingNamespace")) {
            String id = ((MComponentDef) current_node).getIdentifier();
            String temp = handleNamespace(data_type, id);
            return temp+"using namespace CCM_Session_"+id+"_mirror;\n";
        }
        return super.data_MComponentDef(data_type, data_value);
    }

    /**************************************************************************/

    protected Map getTwoStepOperationVariables(MOperationDef operation,
                                               MContained container)
    { return new Hashtable(); }
}
