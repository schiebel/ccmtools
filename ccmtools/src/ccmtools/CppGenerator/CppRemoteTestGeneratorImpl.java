/*
 * CCM Tools : C++ Code Generator Library Egon Teiniker
 * <egon.teiniker@tugraz.at> copyright (c) 2002, 2003 Salomon Automation
 * 
 * $Id: CppRemoteTestGeneratorImpl.java,v 1.5 2005/03/08 19:33:49 teiniker Exp $
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
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import ccmtools.CodeGenerator.Driver;
import ccmtools.CodeGenerator.Template;
import ccmtools.Metamodel.BaseIDL.MContained;
import ccmtools.Metamodel.BaseIDL.MInterfaceDef;
import ccmtools.Metamodel.BaseIDL.MOperationDef;
import ccmtools.Metamodel.ComponentIDL.MComponentDef;
import ccmtools.Metamodel.ComponentIDL.MHomeDef;
import ccmtools.Metamodel.ComponentIDL.MProvidesDef;
import ccmtools.utils.Debug;
import ccmtools.utils.Text;

public class CppRemoteTestGeneratorImpl extends CppGenerator
{

    protected List CorbaStubsNamespace = null;
    protected List LocalNamespace = null;
    
    // types for which we have a global template ; that is, a template that is
    // not contained inside another template.
    private final static String[] local_output_types = {
        "MComponentDef"
    };

    public CppRemoteTestGeneratorImpl(Driver d, File out_dir)
        throws IOException
    {
        super("CppRemoteTest", d, out_dir, local_output_types);

        base_namespace.add("CCM_Remote");
        CorbaStubsNamespace = new ArrayList();
        // CorbaStubsNamespace.add("CORBA_Stubs");
        LocalNamespace = new ArrayList();
        LocalNamespace.add("CCM_Local");
    }

    /**
     * Collect all defined CORBA Stub prefixes into a single string. All CORBA
     * Stub prefixes are stored in a class attribute list called
     * CorbaStubsNamespace which is filled in the constructor.
     * 
     * @param separator
     *            A separator string that is used between two list entries
     *            (example "::"). Example: {"CORBA_Stubs"} -> "CORBA_Stubs::"
     */
    protected String getCorbaStubsNamespace(String separator)
    {
        if(CorbaStubsNamespace.size() > 0) {
            return join(separator, CorbaStubsNamespace) + separator;
        }
        else {
            return "";
        }
    }

    
    protected String getLocalName(MContained contained, String separator)
    {
        List scope = getScope(contained);
        StringBuffer buffer = new StringBuffer();

        buffer.append(Text.join(separator, LocalNamespace));
        buffer.append(separator);
        if (scope.size() > 0) {
            buffer.append(Text.join(separator, scope));
            buffer.append(separator);
        }
        buffer.append(contained.getIdentifier());
        return buffer.toString();
    }
    

    protected String getRemoteName(MContained contained, String separator,
                                   String local)
    {
        List scope = getScope(contained);
        StringBuffer buffer = new StringBuffer();
        buffer.append(Text.join(separator, base_namespace));
        buffer.append(separator);
        if (scope.size() > 0) {
            buffer.append(Text.join(separator, scope));
            buffer.append(separator); 
        }
        buffer.append(contained.getIdentifier());
        return buffer.toString();
    }

    
    /**
     * Overwrites the CppGenerator's method to handle namespaces in different
     * ways. There are local (CCM_Local) namespaces, remote (CCM_Remote)
     * namespaces and the namespaces of the generated stubs and skeletons.
     * 
     * "FileNamespace": is used to create the directory in which the remote
     * component logic will be generated
     * 
     * "IncludeNamespace": is used to create the include path depending on the
     * component's namespace. The CCM_Remote namespace is cut off because there
     * can be local and remote include paths.
     * 
     * "Namespace": is used to create the scope for classes. The CCM_Remote
     * namespace is cut off because there can be local and remote include paths.
     * If a namespace is defined, it has to start with "::"
     * 
     * "ShortNamespace": corresponds with the module hierarchy in the IDL file,
     * with a IDL-prefix.
     * 
     * "IdlFileNamespace":
     * 
     * "IdlNamespace":
     */
    protected String handleNamespace(String data_type, String local)
    {
        List names = new ArrayList(namespace);

        if(!local.equals(""))
            names.add("CCM_Session_" + local);

        if(data_type.equals("FileNamespace")) {
            return join("_", slice(names, 0));
        }
        else if(data_type.equals("IncludeNamespace")) {
            return join("/", slice(names, 1));
        }
        else if(data_type.equals("Namespace")) {
            List NamespaceList = slice(names, 1);
            if(NamespaceList.size() > 0) {
                return "::" + join("::", NamespaceList);
            }
            return "";
        }
        else if(data_type.equals("ShortNamespace")) {
            if(names.size() > 1) {
                List shortList = new ArrayList(names.subList(1,
                                                             names.size() - 1));
                if(shortList.size() > 0)
                    return getCorbaStubsNamespace("::") + join("::", shortList)
                            + "::";
            }
            return getCorbaStubsNamespace("::");
        }
        else if(data_type.equals("IdlFileNamespace")) {
            if(names.size() > 1) {
                List IdlFileList = new ArrayList(names
                        .subList(1, names.size() - 1));
                if(IdlFileList.size() > 0)
                    return getCorbaStubsNamespace("_") + join("_", IdlFileList)
                            + "_";
            }
            return getCorbaStubsNamespace("_");
        }
        else if(data_type.equals("IdlNamespace")) {
            if(names.size() > 2) {
                List IdlFileList = new ArrayList(names
                        .subList(2, names.size() - 1));
                return join("::", IdlFileList) + "::";
            }
            return "";
        }
        return super.handleNamespace(data_type, local);
    }

    /**
     * Write generated code to an output file.
     * 
     * @param template
     *            the template object to get the generated code structure from ;
     *            variable values should come from the node handler object.
     */
    public void writeOutput(Template template) throws IOException
    {
        Debug.println(Debug.METHODS, "writeOutput(" + template + ")");

        String generated_code = template.substituteVariables(output_variables);

        if(generated_code.trim().equals(""))
            return;

        String node_name = ((MContained) current_node).getIdentifier();
        String file_dir = "test";
        String file_name = "_check_"
                + handleNamespace("FileNamespace", node_name) + "_remote.cc";

        writeFinalizedFile(file_dir, file_name, generated_code);

        // generate an empty Makefile.py in the CCM_Test
        // directory - needed by Confix
        writeFinalizedFile(file_dir, "Makefile.py", "");
    }

    /** *********************************************************************** */

    protected Map getTwoStepOperationVariables(MOperationDef operation,
                                               MContained container)
    {
        String lang_type = getLanguageType(operation);
        Map vars = new Hashtable();

        vars.put("Object", container.getIdentifier());
        vars.put("Identifier", operation.getIdentifier());
        vars.put("LanguageType", lang_type);
        vars.put("MExceptionDefThrows", getOperationExcepts(operation));
        vars.put("MParameterDefAll", getOperationParams(operation));
        vars.put("MParameterDefName", getOperationParamNames(operation, null));

        if(!lang_type.equals("void"))
            vars.put("Return", "return ");
        else
            vars.put("Return", "");

        return vars;
    }

    protected String data_MComponentDef(String data_type, String data_value)
    {
        MComponentDef component = (MComponentDef) current_node;
        MHomeDef home = null;

        try {
            home = (MHomeDef) component.getHomes().iterator().next();
            if(home == null)
                throw new RuntimeException();
        }
        catch(Exception e) {
            throw new RuntimeException("Component '"
                    + component.getIdentifier()
                    + "' does not have exactly one home.");
        }
        List HomeScope = getScope((MContained) home);
        List ComponentScope = getScope((MContained) component);

        if(data_type.equals("HomeType")) {
            return home.getIdentifier();
        }
        else if(data_type.equals("IdlHomeType")) {
            if(HomeScope.size() > 0)
                return getCorbaStubsNamespace("::") + join("::", HomeScope)
                        + "::" + home.getIdentifier();
            else
                return getCorbaStubsNamespace("::") + home.getIdentifier();
        }
        else if(data_type.equals("Identifier")) {
            return component.getIdentifier();
        }
        else if(data_type.equals("IdlIdentifier")) {
            if(ComponentScope.size() > 0)
                return getCorbaStubsNamespace("::")
                        + join("::", ComponentScope) + "::"
                        + component.getIdentifier();
            else
                return getCorbaStubsNamespace("::") + component.getIdentifier();
        }
        else if(data_type.endsWith("AbsoluteRemoteHomeName")) {
            return getRemoteName(home,"_","");
        }
        else if(data_type.endsWith("AbsoluteLocalHomeName")) {
            return getLocalName(home,"_");
        }
        return super.data_MComponentDef(data_type, data_value);
    }

    protected String data_MProvidesDef(String data_type, String data_value)
    {
        MInterfaceDef iface = ((MProvidesDef) current_node).getProvides();
        MComponentDef component = ((MProvidesDef) current_node).getComponent();
        List scope = getScope((MContained) iface);

        if(data_type.equals("IdlProvidesType")) {
            if(scope.size() > 0)
                return getCorbaStubsNamespace("::") + join("::", scope) + "::"
                        + iface.getIdentifier();
            else
                return getCorbaStubsNamespace("::") + iface.getIdentifier();
        }
        else if(data_type.equals("ProvidesType")) {
            return iface.getIdentifier();
        }
        else if(data_type.equals("ComponentType")) {
            return component.getIdentifier();
        }
        return super.data_MProvidesDef(data_type, data_value);
    }
}