/*
 * CCM Tools : IDL Code Generator Library Leif Johnson <leif@ambient.2y.net>
 * Egon Teiniker <egon.teiniker@salomon.at> Copyright (C) 2002, 2003, 2004
 * Salomon Automation
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ccmtools.CodeGenerator.Driver;
import ccmtools.CodeGenerator.Template;
import ccmtools.Metamodel.BaseIDL.MContained;
import ccmtools.Metamodel.BaseIDL.MInterfaceDef;
import ccmtools.Metamodel.ComponentIDL.MComponentDef;
import ccmtools.Metamodel.ComponentIDL.MHomeDef;
import ccmtools.Metamodel.ComponentIDL.MProvidesDef;
import ccmtools.Metamodel.ComponentIDL.MSupportsDef;
import ccmtools.Metamodel.ComponentIDL.MUsesDef;

public class IDL3GeneratorImpl extends IDLGenerator
{

    public IDL3GeneratorImpl(Driver driver, File out_dir) throws IOException
    {
        super("3", driver, out_dir);
    }

    protected String getLocalValue(String variable)
    {
        String value = "";
        MInterfaceDef iface = null;

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
        else {
            value = super.getLocalValue(variable);
            if(current_node instanceof MHomeDef) {
                return data_MHomeDef(variable, value);
            }
        }
        return value;
    }

    protected String data_MHomeDef(String data_type, String data_value)
    {
        if(data_type.equals("MHomeDefPublicKeyExceptions")) {
            return ", Components::DuplicateKeyValue, Components::InvalidKey";
        }
        else if(data_type.equals("MHomeDefPublicKeyParameters")) {
            return "in " + ((MHomeDef) current_node).getPrimary_Key() + " key";
        }
        else if(data_type.equals("MHomeDefPublicKeyFunctions")) {
            MHomeDef home = (MHomeDef) current_node;

            String[] keys = {
                    "KeyType", "ComponentType"
            };
            String[] values = {
                    home.getPrimary_Key().getIdentifier(),
                    home.getComponent().getIdentifier()
            };

            Map local_map = new Hashtable();
            for(int i = 0; i < keys.length; i++)
                local_map.put(keys[i], values[i]);

            Template template = template_manager.getRawTemplate(data_type, null);
            return template.substituteVariables(local_map);
        }
        return super.data_MHomeDef(data_type, data_value);
    }

    /**
     * Override IDLGenerator method to generate idl3 #include <>statements in
     * the right way.
     *  
     */
    protected String getScopedInclude(MContained node)
    {
        List scope = getScope(node);
        scope.add(node.getIdentifier());
        return "#include <" + join(File.separator, scope) + ".idl>";
    }

    /**
     * Write generated IDL3 code to output files in the following structure:
     * 
     * component/ <namespace>/ <component_name>/ <component_name>.idl /
     * <component_home_name>.idl interface/ <namespace>/ <name>.idl
     * 
     * @param template
     *            the template object to get the generated code structure from ;
     *            variable values should come from the node handler object.
     */
    protected void writeOutput(Template template)
    {
        String[] pieces = template.substituteVariables(output_variables)
                .split("\n");

        List code_pieces = new ArrayList();
        for(int i = 0; i < pieces.length; i++) {
            code_pieces.add(pieces[i]);
        }
        String code = join("\n", code_pieces) + "\n";

        // Separate IDL3 code into files hosted in different directories
        String dir;
        String name;
        if(current_node instanceof MComponentDef
                || current_node instanceof MHomeDef) {
            dir = "component";
            if(namespace.size() > 0) {
                dir += File.separator + join(File.separator, namespace)
                        + File.separator;
            }
            name = ((MContained) current_node).getIdentifier() + ".idl";
        }
        else {
            dir = "interface";
            if(namespace.size() > 0) {
                dir += File.separator + join(File.separator, namespace);
            }
            name = ((MContained) current_node).getIdentifier() + ".idl";
        }

        String prettyCode = prettifyCode(code);

        try {
            File outFile = new File(output_dir + File.separator + dir, name);
            if(isCodeEqualWithFile(prettyCode, outFile)) {
                System.out.println("skipping " + outFile);
            }
            else {
                writeFinalizedFile(dir, name, prettyCode);
            }
        }
        catch(Exception e) {
            System.out.println("!!!Error " + e.getMessage());
        }
    }
}

