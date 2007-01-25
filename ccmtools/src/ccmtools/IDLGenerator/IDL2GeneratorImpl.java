/*
 * CCM Tools : IDL Code Generator Library Leif Johnson <leif@ambient.2y.net>
 * Egon Teiniker <egon.teiniker@salomon.at> Copyright (C) 2002, 2003 Salomon
 * Automation
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

package ccmtools.IDLGenerator;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;

import ccmtools.CodeGenerator.Driver;
import ccmtools.CodeGenerator.Template;

import ccmtools.Metamodel.BaseIDL.MContained;
import ccmtools.Metamodel.BaseIDL.MInterfaceDef;
import ccmtools.Metamodel.ComponentIDL.MComponentDef;
import ccmtools.Metamodel.ComponentIDL.MHomeDef;
import ccmtools.Metamodel.ComponentIDL.MProvidesDef;
import ccmtools.Metamodel.ComponentIDL.MSupportsDef;
import ccmtools.Metamodel.ComponentIDL.MUsesDef;

public class IDL2GeneratorImpl extends IDLGenerator
{

    public IDL2GeneratorImpl(Driver d, File out_dir) throws IOException
    {
        super("2", d, out_dir);

        // Define the separator used to create file names.
        file_separator = "_";

        // Add a namespace to all IDL2 fragments.
        // base_namespace.add("CORBA_Stubs");
    }

    /***************************************************************************
     * Overwrites the IDLGenerator's method.
     * 
     * Substitute keywords from a template string with specific code. (E.g.
     * include names, etc.)
     */
    protected String getLocalValue(String variable)
    {
        String value = "";
        MInterfaceDef iface = null;

        // TODO: move this code in a super class (also used in IDL3Generator)
        if(variable.equals("ProvidesInclude")
                || variable.equals("SupportsInclude")
                || variable.equals("UsesInclude")) {
            if(current_node instanceof MProvidesDef)
                iface = ((MProvidesDef) current_node).getProvides();
            else if(current_node instanceof MSupportsDef)
                iface = ((MSupportsDef) current_node).getSupports();
            else if(current_node instanceof MUsesDef)
                iface = ((MUsesDef) current_node).getUses();
            if(iface != null)
                value = getScopedInclude(iface);
        }
        else if(variable.equals("HomeInclude")) {
            if(current_node instanceof MComponentDef) {
                Iterator homes = ((MComponentDef) current_node).getHomes()
                        .iterator();
                value = getScopedInclude((MHomeDef) homes.next());
            }
        }
        else if(variable.equals("ComponentInclude")) {
            if(current_node instanceof MHomeDef) {
                value = getScopedInclude(((MHomeDef) current_node)
                        .getComponent());
            }
        }
        // ----
        else if(variable.equals("BaseType")) {
            if(current_node instanceof MComponentDef
                    || current_node instanceof MHomeDef) {
                String base = joinBaseNames(", ");
                if(base.length() > 0)
                    return ", " + base;
            }
        }
        else {
            return super.getLocalValue(variable);
        }
        return value;
    }

    /***************************************************************************
     * Overwrites the IDLGenerator's method.
     * 
     * Calculates the include name of a given node, together with all namespaces
     * defined in base_namespace.
     */
    protected String getScopedInclude(MContained node)
    {
        List scope = getScope(node);

        Collections.reverse(base_namespace);
        for(Iterator i = base_namespace.iterator(); i.hasNext();)
            scope.add(0, i.next());
        Collections.reverse(base_namespace);

        scope.add(node.getIdentifier());
        return "#include<" + join(file_separator, scope) + ".idl>";
    }

    /***************************************************************************
     * The IDL2 files are used by the ORB's IDL compiler to create stub and
     * skeleton code (*.h and *.cc files). All files are written in a single
     * directory where namespaces are coded within filenames. Example:
     * idl3/module/name.idl => idl2/module_name.idl
     * 
     * Additionally, a Makefile and a Makefile.py is generated from templeates
     * to generate C++ stubs from these IDL2 files.
     */
    protected void writeOutput(Template template)
    {
        super.writeOutput(template);

        try {
            // Write a Makefile.py that forces Confix to compile generated
            // C++ stubs.
            template = template_manager.getRawTemplate("MakefilePy", null);
            if(template != null) {
                File confix_file = new File(output_dir, "");
                confix_file = new File(confix_file, "Makefile.py");
                if(!confix_file.isFile())
                    writeFinalizedFile("", "Makefile.py", template
                            .getTemplate());
            }

            // Write a Makefile that is used by Confix to generate C++ stubs
            // from IDL2.
            template = template_manager.getRawTemplate("MakefileTemplate", null);
            if(template != null) {
                File confix_file = new File(output_dir, "");
                confix_file = new File(confix_file, "Makefile");
                if(!confix_file.isFile())
                    writeFinalizedFile("", "Makefile", template.getTemplate());
            }

            // Write a build.xml file that is used by Ant to build generated
            // Java files.
            template = template_manager.getRawTemplate("AntTemplate", null);
            if(template != null) {
                File confix_file = new File(output_dir, "");
                confix_file = new File(confix_file, "build.xml");
                if(!confix_file.isFile())
                    writeFinalizedFile("", "build.xml", template.getTemplate());
            }
        }
        catch(Exception e) {
            System.out.println("!!!Error " + e.getMessage());
        }
    }
}

