/* CCM Tools : C++ Code Generator Library
 * Egon Teiniker <egon.teiniker@tugraz.at>
 * copyright (c) 2002, 2003, 2004 Salomon Automation
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

package ccmtools.CppGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ccmtools.CodeGenerator.Driver;
import ccmtools.CodeGenerator.Template;
import ccmtools.Metamodel.BaseIDL.MAliasDef;
import ccmtools.Metamodel.BaseIDL.MArrayDef;
import ccmtools.Metamodel.BaseIDL.MAttributeDef;
import ccmtools.Metamodel.BaseIDL.MContained;
import ccmtools.Metamodel.BaseIDL.MEnumDef;
import ccmtools.Metamodel.BaseIDL.MExceptionDef;
import ccmtools.Metamodel.BaseIDL.MFieldDef;
import ccmtools.Metamodel.BaseIDL.MIDLType;
import ccmtools.Metamodel.BaseIDL.MInterfaceDef;
import ccmtools.Metamodel.BaseIDL.MOperationDef;
import ccmtools.Metamodel.BaseIDL.MParameterDef;
import ccmtools.Metamodel.BaseIDL.MParameterMode;
import ccmtools.Metamodel.BaseIDL.MPrimitiveDef;
import ccmtools.Metamodel.BaseIDL.MPrimitiveKind;
import ccmtools.Metamodel.BaseIDL.MSequenceDef;
import ccmtools.Metamodel.BaseIDL.MStringDef;
import ccmtools.Metamodel.BaseIDL.MStructDef;
import ccmtools.Metamodel.BaseIDL.MTyped;
import ccmtools.Metamodel.BaseIDL.MTypedefDef;
import ccmtools.Metamodel.BaseIDL.MWstringDef;
import ccmtools.Metamodel.ComponentIDL.MComponentDef;
import ccmtools.Metamodel.ComponentIDL.MHomeDef;
import ccmtools.Metamodel.ComponentIDL.MProvidesDef;
import ccmtools.Metamodel.ComponentIDL.MSupportsDef;
import ccmtools.Metamodel.ComponentIDL.MUsesDef;
import ccmtools.utils.Code;
import ccmtools.utils.Text;

/*******************************************************************************
 * Remote C++ component generator
 * 
 * This generator creates CORBA objects that build the remote skin of a
 * component as well as a set of adapters that convert CORBA types into C++
 * native types and vice versa.
 ******************************************************************************/
public class CppRemoteGeneratorImpl extends CppGenerator {

    //====================================================================
    // Definition of arrays that determine the generator's behavior
    //====================================================================

    private Map CORBA_mappings;

    protected List CorbaStubsNamespace = null;

    protected List LocalNamespace = null;

    /**
     * Top level node types: Types for which we have a global template; that is,
     * a template that is not contained inside another template.
     */
    private final static String[] local_output_types = {
            "MHomeDef", "MComponentDef", "MInterfaceDef", "MStructDef", "MAliasDef", "MEnumDef",
            "MExceptionDef"
    };

    /**
     * Language type mapping: Defines the IDL to C++ Mappings for primitive
     * types.
     * 
     * Note: order and length of the array must be the same as used by the
     * MPrimitiveKind enumeration of the CCM metamodel.
     */
    private final static String[] remote_language_map = { 
            "", 
            "CORBA::Any", 			// PK_ANY
            "CORBA::Boolean", 		// PK_BOOLEAN
            "CORBA::Char",    		// PK_CHAR
            "CORBA::Double",  		// PK_DOUBLE
            "",               		// PK_FIXED
            "CORBA::Float",   		// PK_FLOAT
            "CORBA::Long",    		// PK_LONG
            "CORBA::LongDouble", 	// PK_LONGDOUBLE
            "CORBA::LongLong",   	// PK_LONGLONG
            "",                  	// PK_NULL
            "",                  	// PK_OBJREF
            "CORBA::Octet",      	// PK_OCTET
            "", 				 	// PK_PRINCIPAL
            "CORBA::Short", 	 	// PK_SHORT
            "char*", 	         	// PK_STRING
            "", 				 	// PK_TYPECODE
            "CORBA::ULong", 	 	// PK_ULONG
            "CORBA::ULongLong",  	// PK_ULONGLONG
            "CORBA::UShort", 	 	// PK_USHORT
            "", 				 	// PK_VALUEBASE
            "void", 				// PK_VOID
            "CORBA::WChar", 		// PK_WCHAR
            "CORBA::WChar*" 		// PK_WSTRING
    };

    
    /**
     * The generator constructor calls the constructor of the base class and
     * sets up the map for the CORBA to C++ mappings.
     * 
     * @param d
     * @param out_dir
     * 
     * @exception IOException
     */
    public CppRemoteGeneratorImpl(Driver d, File out_dir) throws IOException
    {
        super("CppRemote", d, out_dir, local_output_types);
        base_namespace.add("CCM_Remote");
        CorbaStubsNamespace = new ArrayList();
        // CorbaStubsNamespace.add("CORBA_Stubs");
        LocalNamespace = new ArrayList();
        LocalNamespace.add("CCM_Local");

        // Fill the CORBA_mappings with IDL to C++ Mapping types
        String[] labels = MPrimitiveKind.getLabels();
        CORBA_mappings = new Hashtable();
        for(int i = 0; i < labels.length; i++) {
            CORBA_mappings.put(labels[i], remote_language_map[i]);
        }
    }
    

    //====================================================================
    // Code generator core functions
    //====================================================================

    /**
     * Collect all defined CORBA Stub prefixes into a single string. All CORBA
     * Stub prefixes are stored in a class attribute list called
     * CorbaStubsNamespace which is filled in the constructor.
     * 
     * @param separator
     *            A separator string that is used between two list entries
     *            (example "::"). Example: {"CORBA_Stubs"} -> "CORBA_Stubs::"
     */
    protected String getCorbaStubsNamespace(MContained contained, String separator)
    {
        List scope = getScope(contained);
        StringBuffer buffer = new StringBuffer();
        if(CorbaStubsNamespace.size() > 0) {
            buffer.append(Text.join(separator, CorbaStubsNamespace));
            buffer.append(separator);
        }
        if (scope.size() > 0) {
            buffer.append(Text.join(separator, scope));
            buffer.append(separator);
        }
        return buffer.toString();
    }

    protected String getCorbaStubName(MContained contained, String separator)
    {
        List scope = getScope(contained);
        StringBuffer buffer = new StringBuffer();
        if(CorbaStubsNamespace.size() > 0) {
            buffer.append(Text.join(separator, CorbaStubsNamespace));
            buffer.append(separator);
        }
        if (scope.size() > 0) {
            buffer.append(Text.join(separator, scope));
            buffer.append(separator);
        }
        buffer.append(contained.getIdentifier());
        return buffer.toString();
    }

    protected String getLocalNamespace(MContained contained, String separator, String local)
    {
        List scope = getScope(contained);
        if (local.length() > 0) {
            scope.add("CCM_Session_" + local);
        }
        
        StringBuffer buffer = new StringBuffer();
        buffer.append(Text.join(separator, LocalNamespace));
        buffer.append(separator);
        if (scope.size() > 0) {
            buffer.append(Text.join(separator, scope));
            buffer.append(separator);
        }
        return buffer.toString();
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

    protected String getRemoteNamespace(String separator, String local)
    {
        List names = new ArrayList(namespace);
        if (local.length() > 0) {
            names.add("CCM_Session_" + local);
        }

        StringBuffer buffer = new StringBuffer();
        buffer.append(separator);
        if (names.size() > 1) {
            buffer.append(Text.join(separator, slice(names, 0)));
            buffer.append(separator);
        }
        else {
            // no additional namespace
        }
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
     * component logic will be generated "LocalNamespace": "RemoteNamespace":
     * "LocalIncludeNamespace": "StubsNamespace": "StubsIncludeNamespace":
     */
    protected String handleNamespace(String dataType, String local)
    {
        List names = new ArrayList(namespace);

        if (dataType.equals("FileNamespace")) {
            return Text.join("_", slice(names, 0));
        }
        else if (dataType.equals("LocalNamespace")) {
            return getLocalNamespace((MContained)current_node, "::", local);
        }
        else if (dataType.equals("RemoteNamespace")) {
            return getRemoteNamespace("::", local);
        }
        else if (dataType.equals("LocalIncludeNamespace")) {
            return getLocalNamespace((MContained)current_node, "/", local);
        }
        else if (dataType.equals("StubsNamespace")) {
            return getCorbaStubsNamespace((MContained)current_node, "::");
        }
        else if (dataType.equals("StubsIncludeNamespace")) {
            return getCorbaStubsNamespace((MContained)current_node, "_");
        }
        return super.handleNamespace(dataType, local);
    }

    /***************************************************************************
     * Overrides method from CppGenerator to handle the following tags within an
     * interface attribute template: %(CORBAType)s
     * %(MAttributeDefConvertResultType)s %(MAttributeDefConvertParameter)s
     */
    protected Map getTwoStepAttributeVariables(MAttributeDef attr,
            MContained container)
    {
        String lang_type = getLanguageType(attr);
        Map vars = super.getTwoStepAttributeVariables(attr, container);

        MTyped object = (MTyped) attr;
        String base_type = getBaseIdlType(object);
        return vars;
    }

    /**
     * Get a variable hash table sutable for filling in the template from the
     * fillTwoStepTemplates function. This version of the function fills in
     * operation information from the given interface.
     * 
     * @param operation
     *            the particular interface operation that we're filling in a
     *            template for.
     * @param container
     *            the container in which the given interface is defined.
     * @return a map containing the keys and values needed to fill in the
     *         template for this interface.
     */
    protected Map getTwoStepOperationVariables(MOperationDef operation, MContained container)
    {
        String lang_type = getLanguageType(operation);
        Map vars = new Hashtable();

        vars.put("Object", container.getIdentifier());
        vars.put("Identifier", operation.getIdentifier());
        vars.put("LanguageType", lang_type);
        vars.put("CORBAType", getCORBALanguageType(operation));
        
        vars.put("LocalExceptions", getOperationExcepts(operation));
        vars.put("MExceptionDefCORBA", getCORBAExcepts(operation));

        vars.put("ParameterDefLocal", getLocalOperationParams(operation));
        vars.put("MParameterDefCORBA", getCORBAOperationParams(operation));
        vars.put("MParameterDefName", getOperationParamNames(operation, null));

        // Used for supports adapter generation
        vars.put("ConvertFacetParameterToCpp", convertParameterToCpp(operation));
        vars.put("DeclareFacetCppResult", declareCppResult(operation));
        vars.put("ConvertFacetMethodToCpp", convertMethodToCpp(operation));
        vars.put("ConvertFacetExceptionsToCorba", convertExceptionsToCorba(operation));
        vars.put("ConvertFacetParameterToCorba", convertParameterToCorba(operation));
        vars.put("ConvertFacetResultToCorba", convertResultToCorba(operation));

        vars.put("Return", (lang_type.equals("void")) ? "" : "return ");

        return vars;
    }

    /**
     * Handles the different template names found for a particular model node
     * and returns the generated code. The current model node is represented in
     * the current_node variable. The string parameter contains the name of the
     * found template
     * 
     * Note that the method calls the super class method.
     * 
     * @param variable
     *            The variable name (tag name) to get a value (generated code)
     *            for.
     * 
     * @return The value of the variable available from the current
     *         output_variables hash table. Could be an empty string.
     */
    protected String getLocalValue(String variable)
    {
        // Get local value of CppGenerator 
        String value = super.getLocalValue(variable);

        // Handle simple tags from templates which are related to 
        // the remote generator 
        if (variable.equals("CcmToolsVersion")) {
            return "CCM Tools version " + ccmtools.Constants.VERSION;
        }
        else if (variable.equals("LocalName")) {
            return getLocalName((MContained)current_node, "::");	
        } 
        else if (variable.equals("CorbaStubName")) {
            return getCorbaStubName((MContained)current_node, "::");
        }
        else if (variable.equals("CorbaRemoteName")) {
            return getRemoteName((MContained)current_node,"::","");
        }
        
        // Node specific actions
        if (current_node instanceof MAttributeDef) {
            return data_MAttributeDef(variable, value);
        }
        else if (current_node instanceof MFieldDef) {
            return data_MFieldDef(variable, value);
        }
        else if (current_node instanceof MAliasDef) {
            // determine the contained type of MaliasDef
            MTyped type = (MTyped) current_node;
            MIDLType idlType = type.getIdlType();
            if (idlType instanceof MPrimitiveDef || idlType instanceof MStringDef
                    || idlType instanceof MWstringDef) {
                return value;
            }
            else if (idlType instanceof MSequenceDef) {
                return data_MSequenceDef(variable, value);
            }
            else if (idlType instanceof MArrayDef) {
                return data_MArrayDef(variable, value);
            }
            else {
                // Signal an implementation bug
                throw new RuntimeException("Unhandled alias type:" + "CppRemoteGenerator."
                        + "getLocalValue(" + variable + ")");
            }
        }
        return value;
    }

    protected String data_MArrayDef(String dataType, String dataValue)
    {
        // TODO: Implement array converter
        throw new RuntimeException("CppRemoteGenerator.data_MArrayDef(" + dataType + ", "
                + dataValue + " - Not implemented!");
    }

    /**
     * This method handles an alias to a MSequenceDef. All convert* methods are
     * defined in Java because an alias type can also be a primitive type that
     * already has a converter method. In that case, the converter file must be
     * empty.
     */
    protected String data_MSequenceDef(String dataType, String dataValue)
    {
        MTyped type = (MTyped) current_node;
        MIDLType idlType = type.getIdlType();
        MContained contained = (MContained) type;
        MTyped singleType = (MTyped) idlType;
        MIDLType singleIdlType = singleType.getIdlType();

        if (dataType.equals("ConvertFromCorbaDeclaration")) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("void convertFromCorba(const ");
            buffer.append(getCorbaStubName((MContained)current_node, "::"));
            buffer.append("& in, ");
            buffer.append(getLocalName((MContained)current_node, "::"));
            buffer.append("& out);");
            dataValue = buffer.toString();
        }
        else if (dataType.equals("ConvertToCorbaDeclaration")) {
            StringBuffer buffer = new StringBuffer();
            buffer.append("void convertToCorba(const ");
            buffer.append(getLocalName((MContained)current_node, "::"));
            buffer.append("& in, ");
            buffer.append(getCorbaStubName((MContained)current_node, "::"));
            buffer.append("& out);");
            dataValue = buffer.toString();
        }
        else if (dataType.equals("ConvertFromCorbaImplementation")) {
            List code = new ArrayList();
            code.add("void");
            
            code.add("convertFromCorba(const " 
                    + getCorbaStubName((MContained)current_node, "::")
                    + "& in, " 
                    + getLocalName((MContained)current_node, "::")
                    + "& out)");
            code.add("{");
            code.add("    LDEBUGNL(CCM_REMOTE,\" convertFromCorba("
                    + getCorbaStubName((MContained)current_node, "::")
                    + ")\");");
            code.add("    LDEBUGNL(CCM_REMOTE, in);");
            code.add(data_MSequenceDef("ConvertAliasFromCORBA", ""));
            code.add("}");
            dataValue = Text.join("\n", code);
        }
        else if (dataType.equals("ConvertToCorbaImplementation")) {
            List code = new ArrayList();
            code.add("void");
            code.add("convertToCorba(const " 
                    + getLocalName((MContained)current_node, "::") 
                    + "& in, " 
                    + getCorbaStubName((MContained)current_node, "::")
                    + "& out)");
            code.add("{");
            code.add("    LDEBUGNL(CCM_REMOTE,\" convertToCorba("
                    + getCorbaStubName((MContained)current_node, "::")
                    + ")\");");
            code.add(data_MSequenceDef("ConvertAliasToCORBA", ""));
            code.add("    LDEBUGNL(CCM_REMOTE, out);");
            code.add("}");
            dataValue = Text.join("\n", code);
        }
        else if (dataType.equals("OutputCorbaTypeDeclaration")) {
            dataValue = "std::ostream& operator<<(std::ostream& o, const "
                	+ getCorbaStubName((MContained)current_node, "::")
                    + "& value);";
        }
        else if (dataType.equals("OutputCorbaTypeImplementation")) {
            List code = new ArrayList();
            code.add("std::ostream&");
            code.add("operator<<(std::ostream& o, const " 
                     + getCorbaStubName((MContained)current_node, "::")	
                     + "& value)");
            code.add("{");
            code.add(data_MSequenceDef("OutputCORBAType", ""));
            code.add("    return o;");
            code.add("}");
            dataValue = Text.join("\n", code);
        }
        else if (dataType.equals("SingleValue")) {
            if (singleIdlType instanceof MPrimitiveDef || singleIdlType instanceof MStringDef) {
                dataValue = getBaseLanguageType(singleType);
            }
            else {
                dataValue = "CCM_Local::"
                // TODO: Handle local Namespace
                        + getBaseLanguageType(singleType);
            }
        }
        else if (dataType.equals("InOutValue")) {
            if (singleIdlType instanceof MStringDef) {
                dataValue = "out[i].inout()";
            }
            else {
                dataValue = "out[i]";
            }
        }
        else if (dataType.equals("CORBASequenceConverterInclude")) {
            if (singleIdlType instanceof MPrimitiveDef || singleIdlType instanceof MStringDef) {
                dataValue = "";
            }
            else if (idlType instanceof MStructDef || idlType instanceof MAliasDef
                    || idlType instanceof MSequenceDef) {
                MContained singleContained = (MContained) singleIdlType;
                StringBuffer buffer = new StringBuffer();
                buffer.append("#include \"");
                buffer.append(singleContained.getIdentifier());
                buffer.append("_remote.h\"");
                dataValue = buffer.toString();
            }
            else {
                throw new RuntimeException("data_MSequenceDef(" + dataType + dataValue
                        + ") Unhandled idlType: " + idlType);
            }
        }
        else if (dataType.equals("ConvertAliasFromCORBA")) {
            String singleValue = data_MSequenceDef("SingleValue", "");
            List code = new ArrayList();
            code.add("    out.clear();");
            code.add("    out.reserve(in.length());");
            code.add("    for(unsigned long i=0; i < in.length();i++) {");
            code.add("        " + singleValue + " singleValue;");
            code.add("        convertFromCorba(in[i], singleValue);");
            code.add("        out.push_back(singleValue);");
            code.add("    }");
            dataValue = Text.join("\n", code);
        }
        else if (dataType.equals("ConvertAliasToCORBA")) {
            String singleValue = data_MSequenceDef("SingleValue", "");
            String inOutValue = data_MSequenceDef("InOutValue", "");
            List code = new ArrayList();
            code.add("    out.length(in.size());");
            code.add("    for(unsigned long i=0; i < in.size(); i++) {");
            code.add("        " + singleValue + " singleValue = in[i];");
            code.add("        convertToCorba(singleValue, " + inOutValue + ");");
            code.add("    }");
            dataValue = Text.join("\n", code);
        }
        else if (dataType.equals("OutputCORBAType")) {
            List code = new ArrayList();
            code.add("    o << endl;");
            code.add("    o << \"sequence " + handleNamespace("StubsNamespace", "")
                    + contained.getIdentifier() + " [ \" << endl;");
            code.add("    for(unsigned long i=0; i < value.length();i++) {");
            code.add("        o << value[i] << endl;");
            code.add("    }");
            code.add("    o << \"]\";");
            dataValue = Text.join("\n", code);
        }
        else if (dataType.equals("OutputCppType")) {
            List code = new ArrayList();
            code.add("    o << \"[ \";");
            code.add("    for(unsigned long i=0; i < value.size(); i++) {");
            code.add("        if(i) o << \",\";");
            code.add("        o << value[i];");
            code.add("    }");
            code.add("    o << \" ]\";");
            dataValue = Text.join("\n", code);
        }
        return dataValue;
    }

    /**
     * Handle tags defined in %(MFieldDef*)s templates which are not substituted
     * by other templates.
     * 
     * @param data_type
     * @param data_value
     * @return The string that replaces the given tag in the template.
     */
    protected String data_MFieldDef(String dataType, String dataValue)
    {
        // current_node is MFieldDef
        MTyped type = (MTyped) current_node;
        MIDLType idlType = type.getIdlType();
        String fieldName = ((MFieldDef) current_node).getIdentifier();

        // Handle %(CORBATypeIn)s tag in %(MFieldDef*)s templates
        if (dataType.equals("CORBAType")) {
            dataValue = fieldName;
        }
        // Handle %(CORBATypeIn)s tag in %(MFieldDef*)s templates
        else if (dataType.equals("CORBATypeIn")) {
            if (idlType instanceof MStringDef) {
                dataValue = fieldName + ".in()";
            }
            else {
                dataValue = fieldName;
            }
        }
        // Handle %(CORBATypeInOut)s tag in %(MFieldDef*)s templates
        else if (dataType.equals("CORBATypeInOut")) {
            if (idlType instanceof MStringDef) {
                dataValue = fieldName + ".inout()";
            }
            else {
                dataValue = fieldName;
            }
        }
        // Handle %(CORBATypeOut)s tag in %(MFieldDef*)s templates
        else if (dataType.equals("CORBATypeOut")) {
            if (idlType instanceof MStringDef) {
                dataValue = fieldName + ".out()";
            }
            else {
                dataValue = fieldName;
            }
        }
        else if (dataType.equals("CORBAFieldConverterInclude")) {
            if (idlType instanceof MPrimitiveDef || idlType instanceof MStringDef) {
                dataValue = "";
            }
            else if (idlType instanceof MStructDef || idlType instanceof MAliasDef
                    || idlType instanceof MEnumDef) {
                MContained contained = (MContained) idlType;
                StringBuffer ret = new StringBuffer();
                ret.append("#include \"");
                ret.append(contained.getIdentifier());
                ret.append("_remote.h\"");
                dataValue = ret.toString();
            }
            else {
                throw new RuntimeException("data_MFieldDef(" + dataType + dataValue
                        + ") Unhandled idlType: " + idlType);
            }
        }
        return dataValue;
    }

    /**
     * Implements the following tags found in the MAttribute* templates:
     * 'CORBAType' 'AttributeConvertInclude'
     * Note that this method relates to component attributes only!
     */
    protected String data_MAttributeDef(String dataType, String dataValue)
    {
        // current_node is MAttributeDef
        MTyped type = (MTyped) current_node;
        MIDLType idlType = type.getIdlType();
        String baseType = getBaseIdlType(type);
        MAttributeDef attribute = (MAttributeDef)current_node;
        
        // Handle %(CORBAType)s tag in %(MAttributeDef*)s templates
       
        if(dataType.equals("InterfaceType")) {
            dataValue = attribute.getDefinedIn().getIdentifier();
        }
        else if(dataType.equals("CORBAType")) {
            dataValue = getCORBALanguageType((MTyped) current_node);
        }

        else if(dataType.equals("CORBAAttributeResult")) {
            dataValue = getCorbaAttributeResult((MTyped) current_node);
        }
        else if(dataType.equals("CORBAAttributeParameter")) {
            dataValue = getCorbaAttributeParameter((MTyped) current_node);
        }
        else if(dataType.equals("LocalAttributeType")) {
            dataValue = getLocalAttributeType((MTyped) current_node);
        }
        
        else if(dataType.equals("ConvertComponentGetAttributeFromCorba")) {
            dataValue = 
                convertGetAttributeFromCorba((MAttributeDef)current_node,"local_adapter"); 
        }
        else if(dataType.equals("ConvertComponentSetAttributeFromCorba")) {
            dataValue = 
                convertSetAttributeFromCorba((MAttributeDef)current_node,"local_adapter");
        }
        else if(dataType.equals("ConvertInterfaceGetAttributeFromCorba")) {
            dataValue = 
                convertGetAttributeFromCorba((MAttributeDef)current_node,"localInterface"); 
        }
        else if(dataType.equals("ConvertInterfaceSetAttributeFromCorba")) {
            dataValue = 
                convertSetAttributeFromCorba((MAttributeDef)current_node,"localInterface");
        }
        
        else if(dataType.equals("ConvertInterfaceGetAttributeToCorba")) {
            dataValue = 
                convertGetAttributeToCorba((MAttributeDef)current_node); 
        }
        else if(dataType.equals("ConvertInterfaceSetAttributeToCorba")) {
            dataValue = 
                convertSetAttributeToCorba((MAttributeDef)current_node);
        }
        
        else if(dataType.equals("AttributeConvertInclude")) {
            Set code = new HashSet();
            StringBuffer buffer = new StringBuffer();
            if(idlType instanceof MPrimitiveDef || idlType instanceof MStringDef 
                    || idlType instanceof MWstringDef) {
                // no include statement needed for these primitive types
            }
             else {   
                buffer.append("#include \"").append(baseType).append("_remote.h\"\n");
            }
            dataValue = buffer.toString(); 
        }
        return dataValue;
    }

    /**
     * Implements the following tags found in the MOperation* templates:
     * 'OperationConvertInclude' include return type converter
     * 'ParameterConvertInclude' include parameter converters
     * 'ExceptionConvertInclude' include exception converter
     */
    protected String data_MOperationDef(String dataType, String dataValue)
    {
        MTyped type = (MTyped) current_node;
        MIDLType idlType = type.getIdlType();
        String baseType = getBaseIdlType(type);
        MOperationDef operation = (MOperationDef) type;

        if(dataType.equals("InterfaceType")) {
            dataValue = operation.getDefinedIn().getIdentifier();
        }
        
        else if(dataType.equals("CORBAType")) {
            dataValue = getCORBALanguageType((MTyped) current_node);
        }
        //TODO: Replace this by a MParameterDefCORBA Tag in the MOperationDef* template
        else if(dataType.equals("CORBAParameters")) {
            dataValue = getCORBAOperationParams(operation);
        }
        // TODO: Replace this by a MExceptionDefCORBA Tag in the MOperationDef* template
        else if(dataType.equals("CORBAExceptions")) { 
            dataValue = getCORBAExcepts(operation);
        }
        //TODO: Replace this by a MParameterDefLocal Tag in the MOperationDef* template
        if(dataType.equals("LocalParameters")) {
            dataValue = getLocalOperationParams(operation);
        }
        // TODO: Replace this by a MExceptionDefLocal Tag in the MOperationDef* template
        else if(dataType.equals("LocalExceptions")) { 
            dataValue = getOperationExcepts(operation);
        }
        
        else if (dataType.equals("OperationConvertInclude")) {
            if (idlType instanceof MPrimitiveDef || idlType instanceof MStringDef) {
                dataValue = "";
            }
            else {
                StringBuffer ret = new StringBuffer();
                ret.append("#include\"");
                ret.append(baseType);
                ret.append("_remote.h\"");
                ret.append("\n");
                dataValue = ret.toString();
            }
        }
        else if (dataType.equals("ParameterConvertInclude")) {
            for (Iterator i = operation.getParameters().iterator(); i.hasNext();) {
                MParameterDef parameter = (MParameterDef) i.next();
                MTyped parameterType = (MTyped) parameter;
                MIDLType parameterIdlType = parameterType.getIdlType();
                if (parameterIdlType instanceof MPrimitiveDef
                        || parameterIdlType instanceof MStringDef) {
                    dataValue += "";
                }
                else {
                    StringBuffer ret = new StringBuffer();
                    ret.append("#include\"");
                    ret.append(getBaseIdlType(parameter));
                    ret.append("_remote.h\"");
                    ret.append("\n");
                    dataValue += ret.toString();
                }
            }
        }
        else if (dataType.equals("ExceptionConvertInclude")) {
            StringBuffer ret = new StringBuffer();
            for (Iterator i = operation.getExceptionDefs().iterator(); i.hasNext();) {
                MExceptionDef exception = (MExceptionDef) i.next();
                ret.append("#include\"");
                ret.append(exception.getIdentifier());
                ret.append("_remote.h\"");
                ret.append("\n");
                dataValue = ret.toString();
            }
        }

        // Tags for Adapters from CORBA
        else if (dataType.equals("ConvertFacetParameterToCpp")) {
            dataValue = convertParameterToCpp(operation);
        }
        else if (dataType.equals("DeclareFacetCppResult")) {
            dataValue = declareCppResult(operation);
        }
        else if (dataType.equals("ConvertInterfaceMethodToCpp")) {
            dataValue = convertInterfaceMethodToCpp(operation);
        }
        else if (dataType.equals("ConvertFacetExceptionsToCorba")) {
            dataValue = convertExceptionsToCorba(operation);
        }
        else if (dataType.equals("ConvertFacetParameterToCorba")) {
            dataValue = convertParameterToCorba(operation);
        }
        else if (dataType.equals("ConvertFacetResultToCorba")) {
            dataValue = convertResultToCorba(operation);
        }
        
        // Tags for Adapters to CORBA
        else if (dataType.equals("ConvertReceptacleParameterToCorba")) {
            dataValue = convertReceptacleParameterToCorba(operation);
        }
        else if (dataType.equals("DeclareReceptacleCorbaResult")) {
            dataValue = declareReceptacleCorbaResult(operation);
        }
        else if (dataType.equals("ConvertReceptacleMethodToCorba")) {
            dataValue = convertInterfaceMethodToCorba(operation);
        }
        else if (dataType.equals("ConvertReceptacleExceptionsToCpp")) {
            dataValue = convertReceptacleExceptionsToCpp(operation);
        }
        else if (dataType.equals("ConvertReceptacleParameterToCpp")) {
            dataValue = convertReceptacleParameterToCpp(operation);
        }
        else if (dataType.equals("ConvertReceptacleResultToCpp")) {
            dataValue = convertReceptacleResultToCpp(operation);
        }

        else {
            dataValue = super.data_MOperationDef(dataType, dataValue);
        }
        // TODO: remove equal include lines (operation type, attribute types)
        return dataValue;
    }

    
    protected String data_MEnumDef(String data_type, String data_value)
    {
        List ret = new ArrayList();
        MEnumDef enm = (MEnumDef) current_node;
        // Convert C++ enum members from and to CORBA enum members
        if (data_type.equals("MembersFromCorba")) {
            for (Iterator i = enm.getMembers().iterator(); i.hasNext();) {
                String member = (String) i.next();
                ret.add(Text.insertTab(1) + "case " 
                        + getCorbaStubsNamespace((MContained)current_node, "::") 
                        + member + ":");
                ret.add(Text.insertTab(2) + "out = " 
                        + getLocalNamespace((MContained)current_node, "::", "") 
                        + member + ";");
                ret.add(Text.insertTab(2) + "break;");
            }
            return Text.join("\n", ret);
        }
        else if (data_type.equals("MembersToCorba")) {
            for (Iterator i = enm.getMembers().iterator(); i.hasNext();) {
                String member = (String) i.next();
                ret.add(Text.insertTab(1) + "case " 
                        + getLocalNamespace((MContained)current_node, "::", "") 
                        + member + ":");
                ret.add(Text.insertTab(2) + "out = " 
                        + getCorbaStubsNamespace((MContained)current_node, "::") 
                        + member + ";");
                ret.add(Text.insertTab(2) + "break;");
            }
            return Text.join("\n", ret);
        }
        else if (data_type.equals("EnumCorbaOutput")) {
            // generate output string for a CORBA enum output operator
            for (Iterator i = enm.getMembers().iterator(); i.hasNext();) {
                String member = (String) i.next();
                ret.add(Text.insertTab(1) + "case " 
                        + getLocalNamespace((MContained)current_node, "::", "") 
                        + member + ":");
                ret.add(Text.insertTab(2) + "o << \"" 
                        + getCorbaStubsNamespace((MContained)current_node,"::") 
                        + member 
                        + "\" << endl;");
                ret.add(Text.insertTab(2) + "break;");
            }
            return Text.join("\n", ret);
        }
        return super.data_MEnumDef(data_type, data_value);
    }

    
    /**
     * Overwrites the superclass method to support standard IDL2C++ mapping of
     * parameters. MFactoryDef is a MOperationDef so we can use the
     * getCORBAOperationParams() method to convert the parameter list. Note that
     * the MFactoryDef templates contains the %(MParameterCORBA)s placeholder to
     * indicate the CORBA parameter list.
     */
    protected String data_MFactoryDef(String data_type, String data_value)
    {
        if (data_type.startsWith("MParameterCORBA")) {
            return getCORBAOperationParams((MOperationDef) current_node);
        }
        return data_value; // super.data_MFactoryDef() wegen , am Ende
    }

    protected String data_MHomeDef(String data_type, String data_value)
    {
        MHomeDef home = (MHomeDef) current_node;
        MComponentDef component = home.getComponent();

        String home_id = home.getIdentifier();
        String component_id = component.getIdentifier();

        if (data_type.endsWith("ComponentType")) {
            return component_id;
        }
        else if(data_type.endsWith("AbsoluteRemoteHomeName")) {
            return getRemoteName(home,"_","");
        }
        else {
            return super.data_MHomeDef(data_type, data_value);
        }
    }

    
    protected String data_MInterfaceDef(String dataType, String dataValue)
    {
        MInterfaceDef iface = (MInterfaceDef) current_node;
        StringBuffer buffer = new StringBuffer();
        
        if(dataType.equals("StubIdentifier")) {
            buffer.append(getCorbaStubsNamespace(iface,"::"));
            buffer.append(iface.getIdentifier());
            return buffer.toString();
        }
        else if(dataType.equals("CCM_LocalType")) {
            buffer.append(getLocalNamespace(iface,"::",""));
            buffer.append("CCM_");
            buffer.append(iface.getIdentifier());
            return buffer.toString();
        }

        else {
            return super.data_MInterfaceDef(dataType,dataValue);
        }
    }
    
    
    protected String data_MSupportsDef(String data_type, String data_value)
    {
        MSupportsDef supports = (MSupportsDef) current_node;

        if (data_type.equals("SupportsInclude")) {
            List scope = getScope((MContained) supports);
            if (scope.size() > 0) {
                return "CCM_Local/" + Text.join("/", scope) + "/"
                        + supports.getSupports().getIdentifier();
            }
            else {
                return "CCM_Local/" + supports.getSupports().getIdentifier();
            }
        }
        return super.data_MSupportsDef(data_type, data_value);
    }

    /**
     * Implements the following tags found in the MProvidesDef* templates:
     * 'ProvidesInclude' 'ProvidesConvertInclude' includes facet converters
     * 'IdlProvidesType' 'ProvidesType' 'ComponentType'
     */
    protected String data_MProvidesDef(String dataType, String dataValue)
    {
        MProvidesDef provides = (MProvidesDef) current_node;
        MInterfaceDef iface = ((MProvidesDef) current_node).getProvides();
        MComponentDef component = provides.getComponent();
        List scope = getScope((MContained) iface);
        StringBuffer ret = new StringBuffer();

        if (dataType.equals("ProvidesInclude")) {
            // TODO: Refactoring namespace method
            ret.append("#include <CCM_Local/");
            if (scope.size() > 0) {
                ret.append(Text.join("/", scope));
                ret.append("/");
            }
            ret.append(provides.getProvides().getIdentifier());
            ret.append(".h>");
            dataValue = ret.toString();
        }
        else if (dataType.equals("ProvidesConvertInclude")) {
            ret.append("#include <CCM_Remote/");
            ret.append(provides.getProvides().getIdentifier());
            ret.append("_remote.h>");
            ret.append("\n");
            dataValue = ret.toString();
        }
        else if (dataType.equals("IdlProvidesType")) {
            ret.append(getCorbaStubsNamespace(iface,"::"));
            ret.append(iface.getIdentifier());
            dataValue = ret.toString();
        }
        else if (dataType.equals("ProvidesType")) {
            // TODO: Refactoring namespace method
            if (scope.size() > 0) {
                ret.append(Text.join("::", scope));
                ret.append("::");
            }
            ret.append(provides.getProvides().getIdentifier());
            dataValue = ret.toString();
        }
        else if (dataType.equals("InterfaceType")) {
            dataValue = provides.getProvides().getIdentifier();
        }
        else if (dataType.equals("ComponentType")) {
            dataValue = component.getIdentifier();
        }
        else {
            dataValue = super.data_MProvidesDef(dataType, dataValue);
        }
        return dataValue;
    }

    
    protected String data_MUsesDef(String dataType, String dataValue)
    {
        MUsesDef usesDef = (MUsesDef) current_node;
        MInterfaceDef iface = ((MUsesDef) current_node).getUses();
        List scope = getScope((MContained) iface);
        StringBuffer buffer = new StringBuffer();
        
        if (dataType.equals("UsesInclude")) {
            buffer.append("#include <");
            buffer.append(getLocalNamespace(iface, "/", ""));
            buffer.append(usesDef.getUses().getIdentifier());
            buffer.append(".h>");
            dataValue = buffer.toString();
        }
        else if(dataType.equals("UsesConvertInclude")) {
            buffer.append("#include <CCM_Remote/");
            buffer.append(usesDef.getUses().getIdentifier());
            buffer.append("_remote.h>");
            buffer.append("\n");
            dataValue = buffer.toString();
        }
        else if(dataType.equals("CCM_UsesType")) {
            // TODO: Refactoring namespace method
            if (scope.size() > 0) {
                buffer.append(Text.join("::", scope));
                buffer.append("::CCM_");
            }
            else {
                buffer.append("CCM_");
            }
            buffer.append(usesDef.getUses().getIdentifier());
            dataValue = buffer.toString();
        }
        else if(dataType.equals("IdlUsesType")) {
            buffer.append(getCorbaStubsNamespace(iface,"::"));
            buffer.append(usesDef.getUses().getIdentifier());
            dataValue = buffer.toString();
        }
        else if(dataType.equals("UsesType")) {
            // TODO: Refactoring namespace method
            if (scope.size() > 0) {
                buffer.append(Text.join("::", scope));
                buffer.append("::");
            }
            buffer.append(usesDef.getUses().getIdentifier());
            dataValue = buffer.toString();
        }
        else if(dataType.equals("InterfaceType")) {
            dataValue = usesDef.getUses().getIdentifier();
        }
        return super.data_MUsesDef(dataType, dataValue);
    }

    
 
    
    /**
     * Write generated code to an output file. Each global template consists of
     * two sections separated by " < < < < < < <SPLIT>>>>>>>" that are written
     * in two different files node_name + "_remote.h" and node_name +
     * "_remote.cc"
     * 
     * @param template
     *            the template object to get the generated code structure from;
     *            variable values should come from the node handler object.
     */
    public void writeOutput(Template template) throws IOException
    {
        String sourceCode = template.substituteVariables(output_variables);
        String[] sourceFiles = sourceCode.split("<<<<<<<SPLIT>>>>>>>");
        String[] remoteSuffix = {
                "_remote.h", "_remote.cc"
        };

        for(int i = 0; i < sourceFiles.length; i++) {
            if(sourceFiles[i].trim().equals("")) {
                // skip the file creation
                continue;
            }

            try {
                if(current_node instanceof MComponentDef) {
                    // write the component files
                    String componentName = ((MContained) current_node).getIdentifier();
                    String fileDir = handleNamespace("FileNamespace", componentName)
                            + "_CCM_Session_" + componentName;

                    Code.writeFile(driver, output_dir, fileDir, componentName + remoteSuffix[i],
                                   sourceFiles[i]);
                }
                else if(current_node instanceof MHomeDef) {
                    // write the home files
                    MHomeDef home = (MHomeDef) current_node;
                    String componentName = ((MContained) home.getComponent()).getIdentifier();
                    String homeName = home.getIdentifier();
                    String fileDir = handleNamespace("FileNamespace", componentName)
                            + "_CCM_Session_" + componentName;

                    Code.writeFile(driver, output_dir, fileDir, homeName + remoteSuffix[i],
                                   sourceFiles[i]);
                    Code.writeMakefile(driver, output_dir, fileDir, "py", "");
                }
                else if(current_node instanceof MInterfaceDef || current_node instanceof MAliasDef
                        || current_node instanceof MStructDef
                        || current_node instanceof MExceptionDef
                        || current_node instanceof MEnumDef) {
                    // write converter files
                    String nodeName = ((MContained) current_node).getIdentifier();
                    String fileDir = "CORBA_Converter";

                    Code.writeFile(driver, output_dir, fileDir, nodeName + remoteSuffix[i],
                                   sourceFiles[i]);
                    Code.writeMakefile(driver, output_dir, fileDir, "py", "");
                }
                else {
                    throw new RuntimeException("CppRemoteGeneratorImpl.writeOutput()"
                            + ": unhandled node!");
                }
            }
            catch(Exception e) {
                System.out.println("!!!Error " + e.getMessage());
            }
        }
    }

    //====================================================================
    // Handle the C++ data types
    //====================================================================

    /**
     * Override a super class method to add a local namespace to operation
     * parameters (if they are not primitive types). Refactoring: move it to the
     * super class...
     */
    protected String getBaseLanguageType(MTyped object)
    {
        StringBuffer buffer = new StringBuffer();
        if (object instanceof MParameterDef || object instanceof MOperationDef) {
            MIDLType idlType = object.getIdlType();
            if (idlType instanceof MPrimitiveDef || idlType instanceof MStringDef
                    || idlType instanceof MWstringDef) {
                buffer.append(super.getBaseLanguageType(object));
            }
            else {
                buffer.append("CCM_Local::");
                buffer.append(super.getBaseLanguageType(object));
            }
        }
        else {
            // Only add namespace to operation parameters
            buffer.append(super.getBaseLanguageType(object));
        }
        return buffer.toString();
    }

    /**
     * Converts the CCM model type information (MTyped) to the corresponding
     * local C++ types.
     * 
     * @param object
     *            Reference to an element of the CCM model.
     * @return Generated code for the local C++ type as string.
     */
    protected String getLanguageType(MTyped object)
    {
        String base_type = getBaseIdlType(object);

        // override IDL array mapping from parent function.
        if (object instanceof MArrayDef) {
            Iterator i = ((MArrayDef) object).getBounds().iterator();
            Long bound = (Long) i.next();
            String result = base_type + "[" + bound;
            while (i.hasNext())
                result += "][" + (Long) i.next();
            return result + "]";
        }
        return super.getLanguageType(object);
    }

    protected String getLocalOperationParams(MOperationDef op)
    {
        List list = new ArrayList();
        for (Iterator params = op.getParameters().iterator(); params.hasNext();) {
            MParameterDef p = (MParameterDef) params.next();
            list.add(getLanguageType(p) + " " + p.getIdentifier());
        }
        return Text.join(", ", list);
    }

    /**
     * overwrite CppGenerator.getOperationExcepts() 
     * Refactoring: move this method up to CppGenerator
     */
    protected String getOperationExcepts(MOperationDef op)
    {
        List code = new ArrayList();
        for (Iterator es = op.getExceptionDefs().iterator(); es.hasNext();) {
            MExceptionDef idlException = (MExceptionDef) es.next();
            code.add(getLocalName(idlException, "::"));
        }
        if (code.size() > 0) {
            return ", " + Text.join(", ", code) + ")";
        }
        else {
            return ")";
        }
    }

    
    /**
     * Generates the scoped (with namespace) local type of an attribute. 
     * - primitive types must not have namespaces.
     * - attributes are always passed by value.
     * 
     * @param object
     * @return
     */
    protected String getLocalAttributeType(MTyped object)
    {
        MIDLType idlType = object.getIdlType();
        String dataValue;
        
        if(idlType instanceof MPrimitiveDef || idlType instanceof MStringDef 
                || idlType instanceof MWstringDef ) {
            dataValue = getLanguageType(object); 
        }
        else if(idlType instanceof MStructDef || idlType instanceof MEnumDef) {
            dataValue = getLocalName((MContained)idlType, "::");	
        }
        else if(idlType instanceof MAliasDef){
            MTyped containedType = (MTyped) idlType;
            MIDLType containedIdlType = containedType.getIdlType();
            if(containedIdlType instanceof MPrimitiveDef) {
                dataValue = getLanguageType(object);
            }
            else if(containedIdlType instanceof MSequenceDef) {
                dataValue = getLocalName((MContained)idlType, "::");
            }
            else {
                String message = "Unhandled alias type in getLocalAttributeResult(): ";	
                throw new RuntimeException(message + containedIdlType);
            }
        }
        else {
            String message = "Unhandled idl type in getLocalAttributeResult(): ";	
            throw new RuntimeException(message + idlType);
        }
        return dataValue;
    }
    

    
    
    
    //====================================================================
    // Handle the CORBA data types
    //====================================================================

    /**
     * Extract the scoped CORBA type from a MTyped entity. TODO: Refactoring,
     * this method is a subset of getCORBALanguageType()
     */
    protected String getCorbaType(MTyped type)
    {
        MIDLType idlType = type.getIdlType();
        String baseType = getBaseIdlType(type);
        String corbaType = "";

        if (CORBA_mappings.containsKey(baseType)) {
            // Primitive data types are mapped via map.
            corbaType = (String) CORBA_mappings.get(baseType);
        }
        else if (idlType instanceof MTypedefDef) {
            corbaType = getCorbaStubsNamespace((MContained)current_node,"::") 
            + baseType;
        }
        else {
            throw new RuntimeException("CppRemoteGeneratorImpl.getCorbaType(" + type
                    + "): unhandled MTyped!");
        }
        return corbaType;
    }

    /**
     * Converts the CCM model type information (MTyped) to the corresponding
     * CORBA types. If the MTyped type is used as an operation parameter, the
     * CORBA to C++ mapping parameter passing rules take place.
     * 
     * @param object
     *            Reference to a MTyped element of the CCM model.
     * @return Generated code for the CORBA type as string.
     * 
     * TODO: - Refactoring, move parameter handling out of this method, thus,
     * getCorbaType() can be eliminated! - Refactoring, use
     * getCorbaStubsNamespace("::") instead of scope...
     */
    protected String getCORBALanguageType(MTyped object)
    {
        MIDLType idl_type = object.getIdlType();
        String base_type = getBaseIdlType(object);
        String corba_type = "";

        if (CORBA_mappings.containsKey(base_type)) {
            // Primitive data types are mapped via map.
            corba_type = (String) CORBA_mappings.get(base_type);
        }
        else if (object instanceof MContained) {
            // Contained types are mapped with CORBA namespace
            List scope = getScope((MContained) object);
            if (scope.size() > 0) {
                corba_type = /*"::CORBA_Stubs::" +*/ Text.join("::", scope) + "::" + base_type;
            }
            else {
                corba_type = /*"::CORBA_Stubs::" + */ base_type;
            }
        }
        else if (idl_type instanceof MTypedefDef) {
            List scope = getScope((MContained) idl_type);
            if (scope.size() > 0) {
                corba_type = /*"::CORBA_Stubs::" +*/ Text.join("::", scope) + "::" + base_type;
            }
            else {
                corba_type = /*"::CORBA_Stubs::" + */ base_type;
            }
        }

        // Reduce MAliasDef to the original type
        if (idl_type instanceof MAliasDef) {
            idl_type = ((MTyped) idl_type).getIdlType();
        }

        // Handle operation parameter types and passing rules
        if (object instanceof MParameterDef) {
            MParameterDef param = (MParameterDef) object;
            MParameterMode direction = param.getDirection();
            String prefix = "const ";
            String suffix = "&";

            // IN Parameter
            if (direction == MParameterMode.PARAM_IN) {
                if (idl_type instanceof MPrimitiveDef || idl_type instanceof MEnumDef
                        || idl_type instanceof MArrayDef) {
                    prefix = "";
                    suffix = "";
                }
                else if (idl_type instanceof MStringDef) {
                    suffix = "";
                }

            }
            // OUT Parameter
            else if (direction == MParameterMode.PARAM_OUT) {
                if (idl_type instanceof MStringDef) {
                    // OUT string is a special case
                    return "CORBA::String_out";
                }
                else {
                    return corba_type + "_out";
                }
            }
            // INOUT Parameter
            else if (direction == MParameterMode.PARAM_INOUT) {
                prefix = "";
                if (idl_type instanceof MArrayDef) {
                    suffix = "";
                }
            }
            return prefix + corba_type + suffix;
        }

        // Handle operation return types
        else if (object instanceof MOperationDef) {
            // ToDo separate fixed and variable struct
            if (idl_type instanceof MPrimitiveDef || idl_type instanceof MEnumDef
                    || idl_type instanceof MStringDef) {

                return corba_type;
            }
            else if (idl_type instanceof MArrayDef) {
                return corba_type + "_slice*";
            }
            else
                return corba_type + "*";
        }
        return corba_type;
    }
    
    
    protected String getCorbaAttributeParameter(MTyped object)
    {
        MIDLType idlType = object.getIdlType();
        String dataValue = getCORBALanguageType(object);
       
        if(idlType instanceof MPrimitiveDef || idlType instanceof MStringDef 
                || idlType instanceof MWstringDef || idlType instanceof MEnumDef) {
            dataValue += ""; 
        }
        else if(idlType instanceof MStructDef) {
            dataValue += "&"; 
        }
        else if(idlType instanceof MAliasDef){
            MTyped containedType = (MTyped) idlType;
            MIDLType containedIdlType = containedType.getIdlType();
            if(containedIdlType instanceof MPrimitiveDef) {
                dataValue += "";
            }
            else if(containedIdlType instanceof MSequenceDef) {
                dataValue += "&";
            }
            // TODO: MArrayDef
        }
        return dataValue;			
    }
    
    protected String getCorbaAttributeResult(MTyped object)
    {
        MIDLType idlType = object.getIdlType();
        String dataValue = getCORBALanguageType(object);
        if(idlType instanceof MPrimitiveDef || idlType instanceof MStringDef 
                || idlType instanceof MWstringDef || idlType instanceof MEnumDef) {
            dataValue += ""; 
        }
        else if(idlType instanceof MStructDef) {
            dataValue += "*";
        }
        else if(idlType instanceof MAliasDef){
            MTyped containedType = (MTyped) idlType;
            MIDLType containedIdlType = containedType.getIdlType();
            
            if(containedIdlType instanceof MPrimitiveDef) {
                dataValue += "";
            }
            else if(containedIdlType instanceof MSequenceDef) {
                dataValue += "*";
            }
            // TODO: MArrayDef
        }
        return dataValue;
    }
    
    
    //====================================================================
    // Handle the CORBA to C++ adption on operation level
    //====================================================================

    /**
     * Creates the code that describes the parameter list of the operation using
     * CORBA data types.
     * 
     * The %(MParameterDefCORBA)s tag forces a call to this method via
     * getTwoStepVariables().
     * 
     * @param op
     *            Reference to an OperationDef element in the CCM model.
     * @return Generated code as a string.
     */
    protected String getCORBAOperationParams(MOperationDef op)
    {
        List ret = new ArrayList();
        for (Iterator params = op.getParameters().iterator(); params.hasNext();) {
            MParameterDef p = (MParameterDef) params.next();
            ret.add(getCORBALanguageType(p) + " " + p.getIdentifier());
        }
        return Text.join(", ", ret);
    }

    /**
     * Creates the code that declares the exceptions to the given operation in
     * CORBA style. Note that every CORBA operation can throw the
     * CORBA::SystemException.
     * 
     * The %(MExceptionDef)s tag foreces a call to this method via via
     * getTwoStepVariables().
     * 
     * @param op
     *            Reference to an OperationDef element in the CCM model.
     * @return Generated code as a string.
     */
    protected String getCORBAExcepts(MOperationDef op)
    {
        List code = new ArrayList();
        for (Iterator es = op.getExceptionDefs().iterator(); es.hasNext();) {
            MExceptionDef IdlException = (MExceptionDef) es.next();
            code.add(getCorbaStubName(IdlException, "::"));
        }

        if (code.size() > 0) {
            return "throw(CORBA::SystemException, " + Text.join(", ", code) + ")";
        }
        else {
            return "throw(CORBA::SystemException)";
        }
    }

    
    //====================================================================
    // Facet Adapter Stuff
    //====================================================================

    protected boolean isPrimitiveType(MIDLType type)
    {
        if (type instanceof MPrimitiveDef || type instanceof MStringDef
                || type instanceof MWstringDef || type instanceof MEnumDef) {
            return true;
        }
        else {
            return false;
        }
    }

    protected boolean isComplexType(MIDLType type)
    {
        if (type instanceof MStructDef || type instanceof MSequenceDef || type instanceof MAliasDef) {
            return true;
        }
        else {
            return false;
        }
    }

    
    /**
     * Creates the code that converts CORBA attribute setter methods to local C++ calls.
     * @param attr An AttributeDef item of a CCM model.
     * @return A string containing the generated code.
     */
    protected String convertSetAttributeFromCorba(MAttributeDef attr, String delegate)
    {
        MIDLType idlType = ((MTyped) attr).getIdlType();
        StringBuffer buffer = new StringBuffer();
       
        if(idlType instanceof MPrimitiveDef || idlType instanceof MStringDef 
                || idlType instanceof MWstringDef) {
            buffer.append(Text.insertTab(1))
        		.append(getBaseLanguageType((MTyped) attr)).append(" local_value;\n");   
        }
        else {
            buffer.append(Text.insertTab(1)).append("CCM_Local::")
    		.append(getBaseLanguageType((MTyped) attr)).append(" local_value;\n");  
        }
        buffer.append(Text.insertTab(1))
			.append("CCM_Remote::convertFromCorba(value, local_value);\n");
        buffer.append(Text.insertTab(1))
            .append(delegate).append("->").append(attr.getIdentifier()).append("(local_value);\n");
        return buffer.toString();    
    }
    

    /**
     * Creates the code that converts CORBA attribute getter methods to local C++ calls.
     * @param attr An AttributeDef item of a CCM model.
     * @return A string containing the generated code.
     */
    protected String convertGetAttributeFromCorba(MAttributeDef attr, String delegate)
    {
        MIDLType idlType = ((MTyped) attr).getIdlType();
        String code = "";
        
        if(idlType instanceof MPrimitiveDef || idlType instanceof MStringDef 
                || idlType instanceof MWstringDef || idlType instanceof MEnumDef) {	
            code = convertPrimitiveGetAttributeFromCorba(attr, delegate);
        }
        else if(idlType instanceof MStructDef) {  
            code = convertUserGetAttributeFromCorba(attr, delegate);
        }
        else if(idlType instanceof MAliasDef){     
            MTyped containedType = (MTyped) idlType;
            MIDLType containedIdlType = containedType.getIdlType();
            if(containedIdlType instanceof MPrimitiveDef) {
                code = convertPrimitiveGetAttributeFromCorba(attr, delegate);
            }
            else if(containedIdlType instanceof MSequenceDef) {
                code = convertUserGetAttributeFromCorba(attr,delegate);
            }
            // TODO: MArrayDef   
        }
        return code;
    }
    
    protected String convertPrimitiveGetAttributeFromCorba(MAttributeDef attr, String delegate)
    {
        MIDLType idlType = ((MTyped) attr).getIdlType();	
        StringBuffer buffer = new StringBuffer();
        if(idlType instanceof MEnumDef) {
            buffer.append(Text.insertTab(1)).append("CCM_Local::")
                .append(getBaseLanguageType((MTyped) attr))
                .append(" result;\n");
        }
        else {
            buffer.append(Text.insertTab(1))
            	.append(getBaseLanguageType((MTyped) attr))
            	.append(" result;\n");
        }
        buffer.append(Text.insertTab(1)).append("result = ").append(delegate)
        		.append("->")
                .append(attr.getIdentifier()).append("();\n");
        buffer.append(Text.insertTab(1))
                .append(getCORBALanguageType((MTyped) attr))
                .append(" return_value;\n");
        buffer.append(Text.insertTab(1))
                .append("CCM_Remote::convertToCorba(result, return_value);\n");
        buffer.append(Text.insertTab(1)).append("return return_value;\n");
        return buffer.toString();
    }
        
    protected String convertUserGetAttributeFromCorba(MAttributeDef attr, String delegate)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Text.insertTab(1)).append("CCM_Local::")
                .append(getBaseLanguageType((MTyped) attr))
                .append(" result;\n");
        buffer.append(Text.insertTab(1)).append("result = ").append(delegate)
                .append("->")
                .append(attr.getIdentifier()).append("();\n");
        buffer.append(Text.insertTab(1))
                .append(getCORBALanguageType((MTyped) attr))
                .append("_var return_value = new ")
                .append(getCORBALanguageType((MTyped) attr)).append(";\n");
        buffer.append(Text.insertTab(1))
                .append("CCM_Remote::convertToCorba(result, return_value);\n");
        buffer.append(Text.insertTab(1))
                .append("return return_value._retn();\n");
        return buffer.toString();
    }
        
    
    /**
     * Creates the code that converts the CORBA parameters to local C++ types.
     * Note that only the in and inout parameters are converted.
     * 
     * The %(MParameterDefConvertParameter)s tag forces a call to this method
     * via getTwoStepVariables().
     * 
     * @param op
     *            Reference to an OperationDef element in the CCM model.
     * @return Generated code as a string.
     */
    protected String convertParameterToCpp(MOperationDef op)
    {
        List ret = new ArrayList();
        for(Iterator params = op.getParameters().iterator(); params.hasNext();) {
            MParameterDef p = (MParameterDef) params.next();
            MIDLType idl_type = ((MTyped) p).getIdlType();

            if(isPrimitiveType(idl_type)) {
                ret.add(convertPrimitiveParameterFromCorbaToCpp(p));
            }
            else if(isComplexType(idl_type)) {
                ret.add(convertUserParameterFromCorbaToCpp(p));
            }
        }
        return Text.join("\n", ret) + "\n";
    }

    protected String convertPrimitiveParameterFromCorbaToCpp(MParameterDef p)
    {
        MParameterMode direction = p.getDirection();
        String cppType = getBaseLanguageType(p);
        List ret = new ArrayList();
        ret.add(Text.insertTab(1) + cppType + " parameter_" + p.getIdentifier() + ";");
        if(direction != MParameterMode.PARAM_OUT) {
            ret.add(Text.insertTab(1) + "CCM_Remote::convertFromCorba(" + p.getIdentifier()
                    + ", parameter_" + p.getIdentifier() + ");");
        }
        return Text.join("\n", ret);
    }

    protected String convertUserParameterFromCorbaToCpp(MParameterDef p)
    {
        List ret = new ArrayList();
        MParameterMode direction = p.getDirection();
        MIDLType idlType = ((MTyped) p).getIdlType();
        MTypedefDef typedef = (MTypedefDef) idlType;
        MContained contained = (MContained) typedef;
        List scope = getScope(contained);
        String localScope;

        // TODO: use LocalNamespace!!!
        if(scope.size() > 0)
            localScope = Text.join("::", scope) + "::";
        else
            localScope = "";

        ret.add("    CCM_Local::" + localScope + contained.getIdentifier() + " parameter_"
                + p.getIdentifier() + ";");
        if(direction != MParameterMode.PARAM_OUT) {
            ret.add("    CCM_Remote::convertFromCorba(" + p.getIdentifier() + ", parameter_"
                    + p.getIdentifier() + ");");
        }
        return Text.join("\n", ret);
    }

    
    /**
     * Create the code that declares the variable (C++ type and name) in which
     * the result value will be stored.
     * 
     * The %(MParameterDefDeclareResult)s tag forces a call to this method via
     * getTwoStepVariables().
     * 
     * @param op
     *            Reference to an OperationDef element in the CCM model.
     * @return Generated code as a string.
     */
    protected String declareCppResult(MOperationDef op)
    {
        String ret = "";
        MIDLType idlType = op.getIdlType();
        List scope = getScope((MContained) op);
        String localScope;

        if(scope.size() > 0)
            localScope = Text.join("::", scope) + "::";
        else
            localScope = "";

        if(idlType instanceof MPrimitiveDef
                && ((MPrimitiveDef) idlType).getKind() == MPrimitiveKind.PK_VOID) {
            return "";
        }

        if(isPrimitiveType(idlType)) {
            String cppType = getBaseLanguageType(op);
            ret = Text.insertTab(1) + cppType + " result;";
        }
        else if(isComplexType(idlType)) {
            MTypedefDef typedef = (MTypedefDef) idlType;
            MContained contained = (MContained) typedef;
            ret = "    CCM_Local::" + localScope + contained.getIdentifier() + " result;";
        }
        return ret;
    }

    
    /**
     * Create the code that makes the local method call, with all of the local
     * parameters. Note that the local method must be part of the object
     * local_adapter points to.
     * 
     * The %(MParameterDefConvertMethod)s tag forces a call to this method via
     * getTwoStepVariables().
     * 
     * @param op
     *            Reference to an OperationDef element in the CCM model.
     * @return Generated code as a string.
     */
    protected String convertMethodToCpp(MOperationDef op)
    {
        List ret = new ArrayList();
        String resultPrefix;
        MIDLType idlType = op.getIdlType();

        if(idlType instanceof MPrimitiveDef
                && ((MPrimitiveDef) idlType).getKind() == MPrimitiveKind.PK_VOID) {
            resultPrefix = ""; // void foo()
        }
        else {
            resultPrefix = Text.insertTab(2) + "result = ";
        }

        if(isPrimitiveType(idlType) || isComplexType(idlType)) {
            resultPrefix += "local_adapter->" + op.getIdentifier() + "(";
            for(Iterator params = op.getParameters().iterator(); params.hasNext();) {
                MParameterDef p = (MParameterDef) params.next();
                String base_type = (String) language_mappings.get((String) getBaseIdlType(p));
                ret.add(" parameter_" + p.getIdentifier());
            }
            return resultPrefix + Text.join(", ", ret) + ");";
        }
        else {
            throw new RuntimeException("CppRemoteGeneratorImpl.convertMethodToCpp():"
                    + "unhandled idl type " + idlType);
        }
    }

    
    protected String convertInterfaceMethodToCpp(MOperationDef op)
    {
        List ret = new ArrayList();
        String resultPrefix = Text.insertTab(2);
        MIDLType idlType = op.getIdlType();

        if(idlType instanceof MPrimitiveDef
                && ((MPrimitiveDef) idlType).getKind() == MPrimitiveKind.PK_VOID) {
            // void foo()
        }
        else {
            resultPrefix += "result = ";
        }

        if(isPrimitiveType(idlType) || isComplexType(idlType)) {
            resultPrefix += "localInterface->" + op.getIdentifier() + "(";
            for(Iterator params = op.getParameters().iterator(); params.hasNext();) {
                MParameterDef p = (MParameterDef) params.next();
                String base_type = (String) language_mappings.get((String) getBaseIdlType(p));
                ret.add(" parameter_" + p.getIdentifier());
            }
            return resultPrefix + Text.join(", ", ret) + ");";
        }
        else {
            throw new RuntimeException("CppRemoteGeneratorImpl.convertMethodToCpp():"
                    + "unhandled idl type " + idlType);
        }
    }
    
    
    /**
     * Create the code for the remote facet and supports adapter.
     * 
     * The %(MParameterDefConvertParameterFromCppToCorba)s in the template
     * %(MOperstaionFacetAdapterImpl)s tag forces a call to this method via
     * getTwoStepVariables().
     * 
     * @param op
     *            Reference to an OperationDef element in the CCM model.
     * @return Generated code as a string.
     */
    protected String convertParameterToCorba(MOperationDef op)
    {
        List ret = new ArrayList();
        for(Iterator params = op.getParameters().iterator(); params.hasNext();) {
            MParameterDef p = (MParameterDef) params.next();
            MIDLType idl_type = ((MTyped) p).getIdlType();

            if(isPrimitiveType(idl_type)) {
                ret.add(convertPrimitiveParameterFromCppToCorba(p));
            }
            else if(idl_type instanceof MStructDef || idl_type instanceof MEnumDef) {
                ret.add(convertUserParameterFromCppToCorba(p));
            }
            else if(idl_type instanceof MAliasDef) {
                MTyped containedType = (MTyped) idl_type;
                MIDLType containedIdlType = containedType.getIdlType();
                if(isPrimitiveType(containedIdlType)) {
                    ret.add(convertPrimitiveParameterFromCppToCorba(p));
                }
                else {
                    ret.add(convertUserParameterFromCppToCorba(p));
                }
            }
        }
        return Text.join("\n", ret) + "\n";
    }

    protected String convertPrimitiveParameterFromCppToCorba(MParameterDef p)
    {
        List ret = new ArrayList();
        MParameterMode direction = p.getDirection();
        if(direction != MParameterMode.PARAM_IN) {
            ret.add("    CCM_Remote::convertToCorba(parameter_" + p.getIdentifier() + ", "
                    + p.getIdentifier() + ");");
        }
        return Text.join("\n", ret);
    }

    protected String convertUserParameterFromCppToCorba(MParameterDef p)
    {
        MParameterMode direction = p.getDirection();
        List ret = new ArrayList();

        if(direction == MParameterMode.PARAM_IN) {
            return "";
        }
        else if(direction == MParameterMode.PARAM_INOUT) {
            ret.add("    CCM_Remote::convertToCorba(parameter_" + p.getIdentifier() + ", "
                    + p.getIdentifier() + ");");
        }
        else {
            MIDLType idlType = ((MTyped) p).getIdlType();
            MTypedefDef typedef = (MTypedefDef) idlType;
            MContained contained = (MContained) typedef;
            List scope = getScope(contained);
            String remoteScope = "::"; //"::CORBA_Stubs::";
            if(scope.size() > 0)
                remoteScope += Text.join("::", scope) + "::";

            ret.add("    " + p.getIdentifier() + " = new " + remoteScope
                    + contained.getIdentifier() + ";");
            ret.add("    CCM_Remote::convertToCorba(parameter_" + p.getIdentifier() + ", "
                    + p.getIdentifier() + ");");
        }
        return Text.join("\n", ret);
    }

    /**
     * Create the code that converts the result value as well as the inout and
     * out parameters from local C++ to CORBA types.
     * 
     * The %(MParameterDefConvertResult)s tag forces a call to this method via
     * getTwoStepVariables().
     * 
     * @param op
     *            Reference to an OperationDef element in the CCM model.
     * @return Generated code as a string.
     */
    protected String convertResultToCorba(MOperationDef op)
    {
        List ret = new ArrayList();
        MIDLType idl_type = op.getIdlType();

        if(idl_type instanceof MPrimitiveDef
                && ((MPrimitiveDef) idl_type).getKind() == MPrimitiveKind.PK_VOID) {
            return "";
        }

        if(isPrimitiveType(idl_type)) {
            ret.add(convertPrimitiveResultFromCppToCorba(op));
        }
        else if(idl_type instanceof MStructDef || idl_type instanceof MSequenceDef
                || idl_type instanceof MEnumDef) {
            ret.add(convertUserResultFromCppToCorba(op));
        }
        else if(idl_type instanceof MAliasDef) {
            MTyped containedType = (MTyped) idl_type;
            MIDLType containedIdlType = containedType.getIdlType();
   
            if(isPrimitiveType(containedIdlType)) {
                ret.add(convertPrimitiveResultFromCppToCorba(op));
            }
            else if(isComplexType(containedIdlType)) {
                ret.add(convertUserResultFromCppToCorba(op));
            }
            else {
                throw new RuntimeException("CppRemoteGeneratorImpl.convertResultToCorba()"
                        + ": Not supported alias type " + containedIdlType);
            }
        }
        else {
            throw new RuntimeException("CppRemoteGeneratorImpl.convertResultToCorba()"
                    + ": Not supported type" + idl_type);
        }
        return Text.join("\n", ret);
    }

    protected String convertPrimitiveResultFromCppToCorba(MOperationDef op)
    {
        String base_type = getBaseIdlType(op);
        List ret = new ArrayList();
        // Convert the result iff the result type is not void
        if(!base_type.equals("void")) {
            ret.add("    " + getCORBALanguageType(op) + " return_value;");
            ret.add("    CCM_Remote::convertToCorba(result, return_value);");
            ret.add("    return return_value;");
        }
        return Text.join("\n", ret);
    }

    protected String convertUserResultFromCppToCorba(MOperationDef op)
    {
        List ret = new ArrayList();
        MIDLType idlType = op.getIdlType();
        MTypedefDef typedef = (MTypedefDef) idlType;
        MContained contained = (MContained) typedef;
        List scope = getScope(contained);
        String remoteScope = "::"; //"::CORBA_Stubs::";

        if(scope.size() > 0)
            remoteScope += Text.join("::", scope) + "::";

        ret.add("    " + remoteScope + contained.getIdentifier() + "_var " + "return_value = new "
                + remoteScope + contained.getIdentifier() + ";");
        ret.add("    CCM_Remote::convertToCorba(result, return_value);");
        ret.add("    return return_value._retn();");
        return Text.join("\n", ret);
    }

    
    /**
     * Creates the code that converts the exception list of an operation into
     * catch statements for local exceptions that throw corresponding remote
     * exceptions.
     * 
     * The template contains the following structure: try { //... }
     * %(MParameterDefConvertExceptions)s catch(...) { throw
     * CORBA::SystemException(); } The %(MParameterDefConvertExceptions)s tag
     * forces a call to this method via getTwoStepVariables().
     * 
     * @param op
     *            Reference to an OperationDef element in the CCM model.
     * @return Generated code as a string.
     */
    protected String convertExceptionsToCorba(MOperationDef op)
    {
        List code = new ArrayList();
        for(Iterator es = op.getExceptionDefs().iterator(); es.hasNext();) {
            MExceptionDef exception = (MExceptionDef) es.next();
            code.add(Text.insertTab(1) + "catch(const " + getLocalName(exception, "::")
                    + "& ce) { ");
            code.add(Text.insertTab(2) + getCorbaStubName(exception, "::") + " te;");
            code.add(Text.insertTab(2) + "CCM_Remote::convertToCorba(ce, te);");
            code.add(Text.insertTab(2) + "throw te;");
            code.add(Text.insertTab(1) + "}");
        }
        return Text.join("\n", code);
    }

    
    
    
    
    //====================================================================
    // Receptacle Adapter Stuff (Converters from local C++ to CORBA) 
    //====================================================================

    /**
     * Generate attribute converter logic for setter methods from C++ to CORBA.
     * 
     * @param attr
     * @return Generated source code in a String.
     * 
     * TODO: Try to handle this in a template
     */
    protected String convertSetAttributeToCorba(MAttributeDef attr)
    {
        MIDLType idlType = ((MTyped)attr).getIdlType();
        String code;
       
        if(idlType instanceof MPrimitiveDef 
                || idlType instanceof MStringDef 
                || idlType instanceof MWstringDef
                || idlType instanceof MEnumDef) {
            code = convertPrimitiveSetAttributeToCorba(attr);
        }
        else if(idlType instanceof MStructDef) {
            code = convertUserSetAttributeToCorba(attr);
        }
        else if(idlType instanceof MAliasDef) {
            MTyped containedType = (MTyped) idlType;
            MIDLType containedIdlType = containedType.getIdlType();
            if(containedIdlType instanceof MPrimitiveDef) {
                code = convertPrimitiveSetAttributeToCorba(attr);	
            }
            else if(containedIdlType instanceof MSequenceDef) {
                code = convertUserSetAttributeToCorba(attr);
            }
            else {
                String message = "Unhandled alias type in convertSetAttributeToCorba(): ";
                throw new RuntimeException(message + attr); 
            }
        }
        else {
            String message = "Unhandled idl type in convertSetAttributeToCorba(): ";
            throw new RuntimeException(message + attr);
        }
        return code;    
    }
    
    protected String convertPrimitiveSetAttributeToCorba(MAttributeDef attr)
    {
        MIDLType idlType = ((MTyped)attr).getIdlType();	
        StringBuffer buffer = new StringBuffer();
    
        buffer.append(Text.insertTab(1))
			.append(getCORBALanguageType((MTyped) attr)).append(" remote_value;\n");   
        buffer.append(Text.insertTab(1))	
    		.append("CCM_Remote::convertToCorba(value, remote_value);\n");
        buffer.append(Text.insertTab(1))	
    		.append("remoteInterface->").append(attr.getIdentifier())
    		.append("(remote_value);\n");
        return buffer.toString();
    }
 
    protected String convertUserSetAttributeToCorba(MAttributeDef attr)
    {
        MIDLType idlType = ((MTyped)attr).getIdlType();	
        StringBuffer buffer = new StringBuffer();

        buffer.append(Text.insertTab(1))
			.append(getCORBALanguageType((MTyped) attr)).append("_var remote_value = new ")
			.append(getCORBALanguageType((MTyped) attr)).append(";\n");   
        buffer.append(Text.insertTab(1))	
			.append("CCM_Remote::convertToCorba(value, remote_value);\n");
        buffer.append(Text.insertTab(1))	
			.append("remoteInterface->").append(attr.getIdentifier())
			.append("(remote_value);\n");
        return buffer.toString();
    }
    
    
    /**
     * Generate attribute converter logic for getter methods from C++ to CORBA.
     *  
     * @param attr    
     * @return Generated source code in a String.
     *     
     * TODO: Try to handle this in a template
     */
    protected String convertGetAttributeToCorba(MAttributeDef attr)
    {
        MIDLType idlType = ((MTyped) attr).getIdlType();
        String code;
        
        if(idlType instanceof MPrimitiveDef 
                || idlType instanceof MStringDef 
                || idlType instanceof MWstringDef
                || idlType instanceof MEnumDef) {	
            code = convertPrimitiveGetAttributeToCorba(attr);
        }
        else if(idlType instanceof MStructDef) {  
            code = convertUserGetAttributeToCorba(attr);
        }
        else if(idlType instanceof MAliasDef){     
            MTyped containedType = (MTyped) idlType;
            MIDLType containedIdlType = containedType.getIdlType();
            if(containedIdlType instanceof MPrimitiveDef) {
                code = convertPrimitiveGetAttributeToCorba(attr);
            }
            else if(containedIdlType instanceof MSequenceDef) {
                code = convertUserGetAttributeToCorba(attr);
            }
            else {
                String message = "Unhandled alias type in convertGetAttributeToCorba(): ";
                throw new RuntimeException(message + attr);
            }           
        }
        else {
            String message = "Unhandled idl type in convertGetAttributeToCorba(): ";
            throw new RuntimeException(message + attr);
        }
        return code;
    }
    
    protected String convertPrimitiveGetAttributeToCorba(MAttributeDef attr)
    {
        MIDLType idlType = ((MTyped) attr).getIdlType();	
        StringBuffer buffer = new StringBuffer();
        buffer.append(Text.insertTab(1)).append(getCORBALanguageType((MTyped) attr))
            .append(" result;\n");
        buffer.append(Text.insertTab(1)).append("result = remoteInterface->")
        	.append(attr.getIdentifier()).append("();\n");
        buffer.append(Text.insertTab(1))//.append(getBaseLanguageType((MTyped) attr))
           	.append(getLocalAttributeType(attr))
        	.append(" return_value;\n");
        buffer.append(Text.insertTab(1))
            .append("CCM_Remote::convertFromCorba(result, return_value);\n");
        buffer.append(Text.insertTab(1)).append("return return_value;\n");
        return buffer.toString();
    }
        
    protected String convertUserGetAttributeToCorba(MAttributeDef attr)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Text.insertTab(1)).append(getCORBALanguageType((MTyped) attr))
        	.append("_var result;\n");
        buffer.append(Text.insertTab(1)).append("result = remoteInterface->")
            .append(attr.getIdentifier()).append("();\n");
        buffer.append(Text.insertTab(1)).append(getLocalAttributeType(attr))	
            .append(" return_value;\n");
        buffer.append(Text.insertTab(1))
        	.append("CCM_Remote::convertFromCorba(result, return_value);\n");
        buffer.append(Text.insertTab(1)).append("return return_value;\n");
        return buffer.toString();
    }
    
    
    
    
    /**
     * Creates code that converts the local C++ parameters to CORBA types. Note
     * that only the in and inout parameters are converted.
     * 
     * The %(MParameterDefConvertReceptacleParameterToCorba)s tag forces a call
     * to this method via getTwoStepVariables().
     * 
     * @param op
     *            Reference to an OperationDef element in the CCM model.
     * @return Generated code as a string.
     */
    protected String convertReceptacleParameterToCorba(MOperationDef op)
    {
        List list = new ArrayList();
        for(Iterator params = op.getParameters().iterator(); params.hasNext();) {
            MParameterDef p = (MParameterDef) params.next();
            MIDLType idlType = ((MTyped) p).getIdlType();

            if(isPrimitiveType(idlType)) {
                list.add(convertPrimitiveParameterToCorba(p));
            }
            else if(idlType instanceof MStructDef || idlType instanceof MEnumDef) {
                list.add(convertUserParameterToCorba(p));
            }
            else if(idlType instanceof MAliasDef) {
                MTyped containedType = (MTyped) idlType;
                MIDLType containedIdlType = containedType.getIdlType();

                if(isPrimitiveType(containedIdlType)) {
                    list.add(convertPrimitiveParameterToCorba(p));
                }
                else if(isComplexType(containedIdlType)) {
                    list.add(convertUserParameterToCorba(p));
                }
                else {
                    throw new RuntimeException(
                            "CppRemoteGenerator.convertReceptacleParameterToCorba()"
                                    + ": Not supported alias type " + containedIdlType);
                }
            }
        }
        return Text.join("\n", list) + "\n";
    }

    protected String convertPrimitiveParameterToCorba(MParameterDef p)
    {
        MParameterMode direction = p.getDirection();
        MIDLType idlType = ((MTyped) p).getIdlType();
        String baseType = getBaseIdlType(p);
        String corbaType = getCorbaType(p);
        List list = new ArrayList();

        list.add(Text.insertTab(1) + corbaType + " parameter_" + p.getIdentifier() + ";");
        if(direction != MParameterMode.PARAM_OUT) {
            list.add(Text.insertTab(1) + "CCM_Remote::convertToCorba(" + p.getIdentifier()
                    + ", parameter_" + p.getIdentifier() + ");");
        }
        return Text.join("\n", list);
    }

    protected String convertUserParameterToCorba(MParameterDef p)
    {
        MParameterMode direction = p.getDirection();
        MIDLType idlType = ((MTyped) p).getIdlType();
        MTypedefDef typedef = (MTypedefDef) idlType;
        MContained contained = (MContained) typedef;
        List list = new ArrayList();

        if(direction == MParameterMode.PARAM_IN || direction == MParameterMode.PARAM_INOUT) {
            list.add(Text.insertTab(1) + getCorbaStubName(contained, "::") + "_var parameter_"
                    + p.getIdentifier()
                    + "= new " + getCorbaStubName(contained, "::") + ";");
            list.add(Text.insertTab(1) + "CCM_Remote::convertToCorba(" + p.getIdentifier()
                    + ", parameter_" + p.getIdentifier() + ");");
        }
        else { // MParameterMode.PARAM_OUT
            list.add(Text.insertTab(1) + getCorbaStubName(contained, "::") + "_var parameter_"
                    + p.getIdentifier() + ";");
        }
        return Text.join("\n", list);
    }

    
    /**
     * Create the code that declases the variable (CORBA type and name) in which
     * the result value will be stored.
     * 
     * The %(MParameterDefDeclareReceptacleCorbaResult)s tag forces a call to
     * this method via getTwoStepVariables().
     * 
     * @param op
     *            Reference to an OperationDef element in the CCM model.
     * @return Generated code as a string.
     */
    protected String declareReceptacleCorbaResult(MOperationDef op)
    {
        MIDLType idlType = op.getIdlType();
        String result = "";

        // void foo() does not need a result declaration
        if(idlType instanceof MPrimitiveDef
                && ((MPrimitiveDef) idlType).getKind() == MPrimitiveKind.PK_VOID) {
            return "";
        }

        if(isPrimitiveType(idlType)) {
            result = declareReceptacleCorbaPrimitiveResult(op);
        }
        else if(idlType instanceof MStructDef || idlType instanceof MEnumDef) {
            result = declareReceptacleCorbaUserResult(op);
        }
        else if(idlType instanceof MAliasDef) {
            MTyped containedType = (MTyped) idlType;
            MIDLType containedIdlType = containedType.getIdlType();
            if(isPrimitiveType(containedIdlType)) {
                result = declareReceptacleCorbaPrimitiveResult(op);
            }
            else if(isComplexType(containedIdlType)) {
                result = declareReceptacleCorbaUserResult(op);
            }
            else {
                throw new RuntimeException("CppRemoteGenerator.declareReceptacleCorbaResult()"
                        + ": unhandled MAilasDef " + containedIdlType);
            }
        }
        else {
            throw new RuntimeException("CppRemoteGenerator.declareReceptacleCorbaResult()"
                    + ": unhandled idlType " + idlType);
        }
        return result;
    }

    protected String declareReceptacleCorbaPrimitiveResult(MOperationDef op)
    {
        StringBuffer buffer = new StringBuffer();
        buffer.append(Text.insertTab(1));
        buffer.append(getCorbaType(op));
        buffer.append(" result;");
        return buffer.toString();
    }

    protected String declareReceptacleCorbaUserResult(MOperationDef op)
    {
        MIDLType idlType = op.getIdlType();
        MTypedefDef typedef = (MTypedefDef) idlType;
        MContained contained = (MContained) typedef;
        StringBuffer buffer = new StringBuffer();
        buffer.append(Text.insertTab(1));
        buffer.append(getCorbaStubsNamespace((MContained)current_node,"::"));
        buffer.append(contained.getIdentifier());
        buffer.append("_var result;");
        return buffer.toString();
    }

    
    /**
     * Create the code that makes to remote method call, with all of the CORBA
     * parameters.
     * 
     * The %(MParameterDefConvertReceptacleMethodToCorba)s tag forces a call to
     * this method via getTwoStepVariables().
     * 
     * @param op
     *            Reference to an OperationDef element in the CCM model.
     * @return Generated code as a string.
     */
    protected String convertReceptacleMethodToCorba(MOperationDef op, String receptacleName)
    {
        StringBuffer buffer = new StringBuffer(Text.insertTab(2));
        List list = new ArrayList();
        MIDLType idlType = op.getIdlType();

        // void method, no result declaration
        if(idlType instanceof MPrimitiveDef
                && ((MPrimitiveDef) idlType).getKind() == MPrimitiveKind.PK_VOID) {
        }
        else {
            buffer.append("result = ");
        }
        buffer.append("component_adapter->get_connection_");
        buffer.append(receptacleName);
        buffer.append("()->");
        buffer.append(op.getIdentifier());
        buffer.append("(");

        for(Iterator params = op.getParameters().iterator(); params.hasNext();) {
            MParameterDef p = (MParameterDef) params.next();
            list.add("parameter_" + p.getIdentifier());
        }
        buffer.append(Text.join(", ", list));
        buffer.append(");");
        return buffer.toString();
    }

    
    protected String convertInterfaceMethodToCorba(MOperationDef op)
    {
        StringBuffer buffer = new StringBuffer(Text.insertTab(2));
        List list = new ArrayList();
        MIDLType idlType = op.getIdlType();

        buffer.append(Text.insertTab(1));
        // void method, no result declaration
        if(idlType instanceof MPrimitiveDef
                && ((MPrimitiveDef) idlType).getKind() == MPrimitiveKind.PK_VOID) {
        }
        else {
            buffer.append("result = ");
        }
        buffer.append("remoteInterface->");
        buffer.append(op.getIdentifier());
        buffer.append("(");

        for(Iterator params = op.getParameters().iterator(); params.hasNext();) {
            MParameterDef p = (MParameterDef) params.next();
            list.add("parameter_" + p.getIdentifier());
        }
        buffer.append(Text.join(", ", list));
        buffer.append(");");
        return buffer.toString();
    }
    
    
    protected String convertReceptacleParameterToCpp(MOperationDef op)
    {
        List ret = new ArrayList();
        for(Iterator params = op.getParameters().iterator(); params.hasNext();) {
            MParameterDef p = (MParameterDef) params.next();
            MIDLType idl_type = ((MTyped) p).getIdlType();

            if(isPrimitiveType(idl_type)) {
                ret.add(convertReceptaclePrimitiveParameterToCpp(p));
            }
            else if(idl_type instanceof MStructDef || idl_type instanceof MEnumDef) {
                ret.add(convertReceptacleUserParameterToCpp(p));
            }
            else if(idl_type instanceof MAliasDef) {
                MTyped containedType = (MTyped) idl_type;
                MIDLType containedIdlType = containedType.getIdlType();
                if(isPrimitiveType(containedIdlType)) {
                    ret.add(convertReceptaclePrimitiveParameterToCpp(p));
                }
                else {
                    ret.add(convertReceptacleUserParameterToCpp(p));
                }
            }
        }
        return Text.join("\n", ret) + "\n";
    }

    protected String convertReceptaclePrimitiveParameterToCpp(MParameterDef p)
    {
        List list = new ArrayList();
        MParameterMode direction = p.getDirection();
        if(direction != MParameterMode.PARAM_IN) {
            list.add("    CCM_Remote::convertFromCorba(parameter_" + p.getIdentifier() + ", "
                    + p.getIdentifier() + ");");
        }
        return Text.join("\n", list);
    }

    // TODO: If there are no additional features, remove this method
    protected String convertReceptacleUserParameterToCpp(MParameterDef p)
    {
        return convertReceptaclePrimitiveParameterToCpp(p);
    }

    
    /**
     * Create the code that converts the result value as well as the inout and
     * out parameters from CORBA to local C++ types.
     * 
     * The %(MParameterDefConvertReceptacleResultToCpp)s tag forces a call to
     * this method via getTwoStepVariables().
     * 
     * @param op
     *            Reference to an OperationDef element in the CCM model.
     * @return Generated code as a string.
     */
    protected String convertReceptacleResultToCpp(MOperationDef op)
    {
        List list = new ArrayList();
        MIDLType idlType = op.getIdlType();

        if(idlType instanceof MPrimitiveDef
                && ((MPrimitiveDef) idlType).getKind() == MPrimitiveKind.PK_VOID) {
            return ""; // convertion
        }

        if(isPrimitiveType(idlType)) {
            list.add(convertReceptaclePrimitiveResultToCpp(op));
        }
        else if(idlType instanceof MStructDef || idlType instanceof MSequenceDef
                || idlType instanceof MEnumDef) {
            list.add(convertReceptacleUserResultToCpp(op));
        }
        else if(idlType instanceof MAliasDef) {
            MTyped containedType = (MTyped) idlType;
            MIDLType containedIdlType = containedType.getIdlType();
            if(isPrimitiveType(containedIdlType)) {
                list.add(convertReceptaclePrimitiveResultToCpp(op));
            }
            else if(isComplexType(containedIdlType)) {
                list.add(convertReceptacleUserResultToCpp(op));
            }
            else {
                throw new RuntimeException("CppRemoteGeneratorImpl.convertReceptacleResultToCpp()"
                        + ": Not supported alias type " + containedIdlType);
            }
        }
        else {
            throw new RuntimeException("CppRemoteGeneratorImpl.convertReceptacleResultToCpp()"
                    + ": unhandled idl type " + idlType);
        }
        return Text.join("\n", list);
    }

    protected String convertReceptaclePrimitiveResultToCpp(MOperationDef op)
    {
        String baseType = getBaseIdlType(op);
        List list = new ArrayList();

        list.add(Text.insertTab(1) + getLanguageType(op) + " return_value;");
        list.add(Text.insertTab(1) + "CCM_Remote::convertFromCorba(result, return_value);");
        list.add(Text.insertTab(1) + "return return_value;");
        return Text.join("\n", list);
    }

    protected String convertReceptacleUserResultToCpp(MOperationDef op)
    {
        List list = new ArrayList();
        MIDLType idlType = op.getIdlType();
        MTypedefDef typedef = (MTypedefDef) idlType;
        MContained contained = (MContained) typedef;

        list.add(Text.insertTab(1) + getLocalNamespace((MContained)current_node, "::", "") 
                + contained.getIdentifier()
                + " return_value;");
        list.add(Text.insertTab(1) + "CCM_Remote::convertFromCorba(result, return_value);");
        list.add(Text.insertTab(1) + "return return_value;");
        return Text.join("\n", list);
    }

    
    /**
     * Creates the code that converts the exception list of an operation into
     * catch statements for CORBA exceptions that throw corresponding remote
     * exceptions.
     * 
     * The template contains the following structure: try { //... }
     * %(MParameterDefConvertReceptacleExceptionsToCpp)s catch(...) { throw; }
     * The %(MParameterDefConvertExceptionsToCpp)s tag forces a call to this
     * method via getTwoStepVariables().
     * 
     * @param op
     *            Reference to an OperationDef element in the CCM model.
     * @return Generated code as a string.
     */
    protected String convertReceptacleExceptionsToCpp(MOperationDef op)
    {
        List code = new ArrayList();
        for(Iterator es = op.getExceptionDefs().iterator(); es.hasNext();) {
            MExceptionDef exception = (MExceptionDef) es.next();
            code.add(Text.insertTab(1) + "catch(const " + getCorbaStubName(exception, "::")
                    + "&) {");
            code.add(Text.insertTab(2) + "throw " + getLocalName(exception, "::") + "();");
            code.add(Text.insertTab(1) + "}");
        }
        return Text.join("\n", code);
    }
}

