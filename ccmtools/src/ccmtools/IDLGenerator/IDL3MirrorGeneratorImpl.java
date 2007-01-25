/*
 * CCM Tools : IDL Code Generator Library Leif Johnson <leif@ambient.2y.net>
 * Egon Teiniker <egon.teiniker@salomon.at> Copyright (C) 2002, 2003, 2004
 * Salomon Automation
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

import ccmtools.CodeGenerator.Driver;
import ccmtools.CodeGenerator.Template;
import ccmtools.Metamodel.BaseIDL.MContained;
import ccmtools.Metamodel.BaseIDL.MIDLType;
import ccmtools.Metamodel.BaseIDL.MTyped;
import ccmtools.Metamodel.BaseIDL.MInterfaceDef;
import ccmtools.Metamodel.ComponentIDL.MComponentDef;
import ccmtools.Metamodel.ComponentIDL.MHomeDef;
import ccmtools.Metamodel.ComponentIDL.MProvidesDef;
import ccmtools.Metamodel.ComponentIDL.MSupportsDef;
import ccmtools.Metamodel.ComponentIDL.MUsesDef;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

public class IDL3MirrorGeneratorImpl extends IDLGenerator
{

    public IDL3MirrorGeneratorImpl(Driver d, File out_dir) throws IOException
    {
        super("3Mirror", d, out_dir);
    }

    /**
     * Return the language type for the given object.
     * 
     * @param object
     *            the node object to use for type finding.
     * @return a string describing the IDL language type.
     */
    protected String getLanguageType(MTyped object)
    {
        MIDLType idl_type = object.getIdlType();
        String super_type = super.getLanguageType(object);
        if(idl_type instanceof MComponentDef || idl_type instanceof MHomeDef)
            return super_type + "_mirror";
        return super_type;
    }

    /**
     * Override IDLGenerator method...
     * 
     * FIXME: the same method is used in IDL3GeneratorImpl.java
     */
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
                value = "#include <" + getScopedInclude(iface) + ".idl>";
        }
        else if(variable.equals("HomeInclude")) {
            if(current_node instanceof MComponentDef) {
                Iterator homes = ((MComponentDef) current_node).getHomes()
                        .iterator();
                value = "#include <"
                        + getScopedInclude((MHomeDef) homes.next())
                        + "_mirror.idl>";
            }
        }
        else if(variable.equals("ComponentInclude")) {
            if(current_node instanceof MHomeDef) {
                value = "#include <"
                        + getScopedInclude(((MHomeDef) current_node)
                                .getComponent()) + "_mirror.idl>";
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

    /**
     * Override IDLGenerator method to generate idl3mirror component #include <>
     * statements in the right way.
     *  
     */
    protected String getScopedInclude(MContained node)
    {
        List scope = getScope(node);
        scope.add(node.getIdentifier());
        return join(File.separator, scope);
    }

    /**
     * Override IDLGenerator method to generate the mirror component files into
     * a 'component' directory.
     *  
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

        try {
            String dir;
            String name;
            if(current_node instanceof MComponentDef
                    || current_node instanceof MHomeDef) {
                dir = "component";
                if(namespace.size() > 0) {
                    dir += File.separator + join(File.separator, namespace)
                            + File.separator;
                }
                name = ((MContained) current_node).getIdentifier()
                        + "_mirror.idl";

                String prettyCode = prettifyCode(code);
                File outFile = new File(output_dir + File.separator + dir, name);
                if(isCodeEqualWithFile(prettyCode, outFile)) {
                    System.out.println("skipping " + outFile);
                }
                else {
                    writeFinalizedFile(dir, name, prettyCode);
                }
            }
            else {
                // Don't generate code for other nodes than MComponentDef and
                // MHomeDef.
                // TODO: cancel all other templates from IDL3MirrorTemplates
            }
        }
        catch(Exception e) {
            System.out.println("!!!Error " + e.getMessage());
        }
    }
}

