/* CCM Tools : C++ Code Generator Library
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

package ccmtools.CppGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import ccmtools.Coda.XMLFileManager;
import ccmtools.CodeGenerator.Driver;
import ccmtools.CodeGenerator.Template;
import ccmtools.Metamodel.BaseIDL.MAliasDef;
import ccmtools.Metamodel.BaseIDL.MArrayDef;
import ccmtools.Metamodel.BaseIDL.MAttributeDef;
import ccmtools.Metamodel.BaseIDL.MContained;
import ccmtools.Metamodel.BaseIDL.MDefinitionKind;
import ccmtools.Metamodel.BaseIDL.MEnumDef;
import ccmtools.Metamodel.BaseIDL.MExceptionDef;
import ccmtools.Metamodel.BaseIDL.MFieldDef;
import ccmtools.Metamodel.BaseIDL.MIDLType;
import ccmtools.Metamodel.BaseIDL.MIdTyped;
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
import ccmtools.Metamodel.BaseIDL.MWstringDef;
import ccmtools.Metamodel.BaseIDL.MModuleDef;
import ccmtools.Metamodel.BaseIDL.MContainer;
import ccmtools.Metamodel.ComponentIDL.MComponentDef;
import ccmtools.Metamodel.ComponentIDL.MHomeDef;
import ccmtools.Metamodel.ComponentIDL.MProvidesDef;
import ccmtools.Metamodel.ComponentIDL.MSupportsDef;
import ccmtools.utils.Code;
import ccmtools.utils.Text;

public class CppPythonGeneratorImpl
    extends CppGenerator
{
	// keeps a mapping of the module elements so that we can
	// include the previous expansion in the new expansion
	Map module_elements;
	
	public static final int CONVERT_TO_LOCAL = 0;
    public static final int CONVERT_FROM_LOCAL = 1;
    
    private static final int C_TYPE = 2;
    private static final int SYMBOL_TYPE = 3;
    private static final int DOCSIG_TYPE = 4;
    
    // types for which we have a global template ; that is, a template that is
    // not contained inside another template.
    private final static String[] local_output_types =
    {
        "MComponentDef", "MInterfaceDef", "MHomeDef",
        "MStructDef", "MUnionDef", "MAliasDef", "MEnumDef", "MExceptionDef",
		"MModuleDef"
    };

    /**
     * IDL -> python parameter flag
     */
    private Map flag_mappings;
    private final static String[] flag_set = { 
            "", 
            "O&",					// PK_ANY
            "i",					// PK_BOOLEAN
            "b",					// PK_CHAR
            "d",					// PK_DOUBLE
            "",						// PK_FIXED
            "f",					// PK_FLOAT
            long_type_python_flag,	// PK_LONG
            "d",					// PK_LONGDOUBLE
            "l",					// PK_LONGLONG
            "",						// PK_NULL
            "",						// PK_OBJREF
            "i",					// PK_OCTET
            "",						// PK_PRINCIPAL
            "h",					// PK_SHORT
            "s",					// PK_STRING
            "",						// PK_TYPECODE
            "k",					// PK_ULONG
            "K",					// PK_ULONGLONG
            "H",					// PK_USHORT
            "",						// PK_VALUEBASE
            "",						// PK_VOID
            "",						// PK_WCHAR
            ""						// PK_WSTRING
    };

    /**
     * IDL -> python parameter flag
     */
    private Map type_mappings;
    private final static String[] type_set = { 
            "", 
            "::WX::Utils::Value *",						// PK_ANY
            "bool",										// PK_BOOLEAN
            "char",										// PK_CHAR
            "double",									// PK_DOUBLE
            "(fixed data type not implemented)",		// PK_FIXED
            "float",									// PK_FLOAT
            long_type,									// PK_LONG
            "double",									// PK_LONGDOUBLE
            "long",										// PK_LONGLONG
            "NULL",										// PK_NULL
            "",											// PK_OBJREF
            "unsigned char",							// PK_OCTET
            "(principal data type not implemented)",	// PK_PRINCIPAL
            "short",									// PK_SHORT
            "std::string",								// PK_STRING
            "",											// PK_TYPECODE
            "unsigned long",							// PK_ULONG
            "unsigned long",							// PK_ULONGLONG
            "unsigned short",							// PK_USHORT
            "",											// PK_VALUEBASE
            "void",										// PK_VOID
            "wchar_t",									// PK_WCHAR
            "std:wstring"								// PK_WSTRING
    };

    private Map type_ident_mappings;
    private final static String[] type_ident_set = { 
            "", 
            "",							// PK_ANY
            "boolean",					// PK_BOOLEAN
            "char",						// PK_CHAR
            "double",					// PK_DOUBLE
            "",							// PK_FIXED
            "float",					// PK_FLOAT
            "long",						// PK_LONG
            "double",					// PK_LONGDOUBLE
            "long",						// PK_LONGLONG
            "NULL",						// PK_NULL
            "",							// PK_OBJREF
            "octet",					// PK_OCTET
            "",							// PK_PRINCIPAL
            "short",					// PK_SHORT
            "string",					// PK_STRING
            "",							// PK_TYPECODE
            "unsigned_long",			// PK_ULONG
            "unsigned_long",			// PK_ULONGLONG
            "unsigned_short",			// PK_USHORT
            "",							// PK_VALUEBASE
            "void",						// PK_VOID
            "",							// PK_WCHAR
            ""							// PK_WSTRING
    };

    private Map conversion_mappings;
    private final static String[] conversion_set = { 
            "", 
            "",							// PK_ANY
            "PyBool_Check",				// PK_BOOLEAN
            "PyInt_Check",				// PK_CHAR
            "PyFloat_Check",			// PK_DOUBLE
            "",							// PK_FIXED
            "PyFloat_Check",			// PK_FLOAT
            "PyLong_Check",				// PK_LONG
            "PyFloat_CHeck",			// PK_LONGDOUBLE
            "PyLong_Check",				// PK_LONGLONG
            "",							// PK_NULL
            "",							// PK_OBJREF
            "octet",					// PK_OCTET
            "",							// PK_PRINCIPAL
            "PyInt_Check",				// PK_SHORT
            "PyString_Check",			// PK_STRING
            "",							// PK_TYPECODE
            "PyLong_Check",				// PK_ULONG
            "PyLong_Check",				// PK_ULONGLONG
            "PyInt_Checkt",				// PK_USHORT
            "",							// PK_VALUEBASE
            "",							// PK_VOID
            "",							// PK_WCHAR
            ""							// PK_WSTRING
    };

    private Map type_docsig_mappings;
    private final static String[] type_docsig_set = { 
            "", 
            "any",						// PK_ANY
            "boolean",					// PK_BOOLEAN
            "integer",					// PK_CHAR
            "float",					// PK_DOUBLE
            "",							// PK_FIXED
            "float",					// PK_FLOAT
            "integer",					// PK_LONG
            "double",					// PK_LONGDOUBLE
            "integer",					// PK_LONGLONG
            "NULL",						// PK_NULL
            "",							// PK_OBJREF
            "",							// PK_OCTET
            "",							// PK_PRINCIPAL
            "integer",					// PK_SHORT
            "string",					// PK_STRING
            "",							// PK_TYPECODE
            "integer",					// PK_ULONG
            "integer",					// PK_ULONGLONG
            "integer",					// PK_USHORT
            "",							// PK_VALUEBASE
            "void",						// PK_VOID
            "",							// PK_WCHAR
            ""							// PK_WSTRING
    };
    
    protected List LocalNamespace = null;

	private List degenerate_aliases = new ArrayList();
	private List overridden_conversion = new ArrayList();
	
    /**************************************************************************/

    public CppPythonGeneratorImpl(Driver d, File out_dir)
        throws IOException
    {
        super("CppPython", d, out_dir, local_output_types);
        
        module_elements = new Hashtable();
        
        LocalNamespace = new ArrayList();
        LocalNamespace.add("CCM_Local");

        base_namespace.add("CCM_Python");
        base_namespace.add("CCM_Local");
        
        String[] labels = MPrimitiveKind.getLabels();
        flag_mappings = new Hashtable();
        for(int i = 0; i < labels.length; i++) {
            flag_mappings.put(labels[i], flag_set[i]);
        }
        type_mappings = new Hashtable();
        for(int i = 0; i < labels.length; i++) {
            type_mappings.put(labels[i], type_set[i]);
        }
        type_ident_mappings = new Hashtable();
        for(int i = 0; i < labels.length; i++) {
            type_ident_mappings.put(labels[i], type_ident_set[i]);
        }
        
        conversion_mappings = new Hashtable();
        for(int i = 0; i < labels.length; i++) {
        		conversion_mappings.put(labels[i], conversion_set[i]);
        }
        
        type_docsig_mappings = new Hashtable();
        for(int i = 0; i < labels.length; i++) {
    			type_docsig_mappings.put(labels[i], type_docsig_set[i]);
        }

    }


    //====================================================================
    // Code generator core functions
    //====================================================================

    protected String getLocalNamespace(MContained contained, String separator, String local, boolean trailing_seperator)
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
            if ( trailing_seperator ) buffer.append(separator);
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
        
        // Node specific actions
        if (current_node instanceof MAttributeDef) {
            return data_MAttributeDef(variable, value);
        }
        else if (current_node instanceof MFieldDef) {
            return data_MFieldDef(variable, value);
        }
        else if (current_node instanceof MModuleDef) {
        		return data_MModuleDef(variable, value);
        }
        else if (current_node instanceof MExceptionDef) {
        		return data_MExceptionDef(variable, value);
        }
        else if (current_node instanceof MComponentDef &&
        			variable.startsWith("MSupportsOperation")) {
        		List supports = ((MComponentDef)current_node).getSupportss();
        		String result = "";
        		String template = variable.replaceFirst("Supports","");
        		for ( Iterator it=supports.iterator(); it.hasNext();) {
        			MInterfaceDef iface = ((MSupportsDef)it.next()).getSupports();
        			result += fillTwoStepTemplates(iface, template, false);
        		}
        		return result;
        }
        else if (current_node instanceof MAliasDef) {
            // determine the contained type of MaliasDef
            MTyped type = (MTyped) current_node;
            MIDLType idlType = type.getIdlType();
 
            if (variable.equals("IncludeLocalHeader")) {
            		MContained contained = (MContained) current_node;
            		MAliasDef alias = (MAliasDef) current_node;
            		String result = "#include <" + getLocalNamespace(contained,"/","",false) + "/" +
            					alias.getIdentifier() + ".h>";
            		return result;
    			}

            if (isPrimitiveType(idlType)) {
            		degenerate_aliases.add(((MAliasDef)current_node).getIdentifier());
            		return value;
            }
            else {
            		MTyped singleType = (MTyped) idlType;
            		MIDLType singleIdlType = singleType.getIdlType();

            		if (variable.equals("IncludeElementConvert")) {
            			return isPrimitiveType(singleIdlType) ? "" :
            				"#include \"" + ((MContained) singleIdlType).getIdentifier() + "_python.h\"";
            		}
            		else if (variable.equals("IncludeLocalElement")) {
            			MContained contained = (MContained) current_node;
            			return isPrimitiveType(singleIdlType) ? "" :
            				"#include \"" + getLocalNamespace(contained,"_","",false) + "/" +
            					((MContained) singleIdlType).getIdentifier() + ".h\"";
            		}
            		else if (idlType instanceof MPrimitiveDef || idlType instanceof MStringDef
            				 || idlType instanceof MWstringDef) {
            			return value;
            		}
            		else if (idlType instanceof MSequenceDef) {
            			return data_MSequenceDef(variable, value);
            		}
            		else if (idlType instanceof MArrayDef) {
            			// Signal an implementation bug
            			throw new RuntimeException("Unhandled alias type:" + "CppPythonGenerator."
            					+ "getLocalValue(" + variable + ")");
            		}
            		else {
            			// Signal an implementation bug
            			throw new RuntimeException("Unhandled alias type:" + "CppPythonGenerator."
                        + "getLocalValue(" + variable + ")");
            		}
            }
        }
        else if (current_node instanceof MOperationDef)
    			return data_MOperationDef(variable,value);

        return value;
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
        		return getLocalNamespace((MContained)current_node, "::", local, true);
        }
        else if (dataType.equals("RemoteNamespace")) {
        		return getRemoteNamespace("::", local);
        }
        else if (dataType.equals("LocalIncludeNamespace")) {
        		return getLocalNamespace((MContained)current_node, "/", local, true);
        }
        
        else if ( dataType.equals("UsingLocalNamespace")) {
        		String prefix = "";
        		List scope = getScope((MContained)current_node);
        	    	List tmp = new ArrayList();

        	    for(Iterator i = LocalNamespace.iterator(); i.hasNext();) {
        	    		String cur = (String) i.next();
        	    		tmp.add("using namespace " + prefix + cur + ";");
        	    		prefix = prefix + cur + "::";
        	    }

        	    	for(Iterator i = scope.iterator(); i.hasNext();) {
        	    		String cur = (String) i.next();
        	    		tmp.add("using namespace " + prefix + cur + ";");
        	    		prefix = prefix + cur + "::";
        	    	}
        	    	
        	    	if (current_node instanceof MComponentDef)
        	    		tmp.add("using namespace " + prefix + "CCM_Session_" + ((MComponentDef)current_node).getIdentifier() + ";");
        	    	else if (current_node instanceof MHomeDef)
        	    		tmp.add("using namespace " + prefix + "CCM_Session_" + ((MHomeDef)current_node).getComponent().getIdentifier() + ";");
        	    			
        	    	return join("\n", tmp);
        }

        // here if we pass "local" on up to the super class, we get a 
        // CCM_Session_<local> local namespace added onto our set of 
        // namespace stack... why this is done or what it is for,
        // I don't know... but I find it inconvienient
        // -->> return super.handleNamespace(dataType, local);
        return super.handleNamespace(dataType, "");
    }

    /**
     * Implements the following tags found in the MOperation* templates:
     * 'OperationConvertInclude' include return type converter
     * 'ParameterConvertInclude' include parameter converters
     * 'ExceptionConvertInclude' include exception converter
     */
    protected String data_MOperationDef(String data_type, String dataValue)
    {
    		MTyped type = (MTyped) current_node;
    		MOperationDef operation = (MOperationDef) type;

    		if(data_type.equals("InterfaceType")) {
    			dataValue = operation.getDefinedIn().getIdentifier();
    		}

    		//TODO: Replace this by a MParameterDefLocal Tag in the MOperationDef* template
    		if(data_type.equals("LocalParameters")) {
    			dataValue = getLocalOperationParams(operation);
    		}

    		// TODO: Replace this by a MExceptionDefLocal Tag in the MOperationDef* template
    		else if(data_type.equals("LocalExceptions")) { 
    			dataValue = getOperationExcepts(operation);
    		}

    		else if (data_type.equals("InvocationSetup")) {
    			dataValue = pythonOperationSetup(operation, "interface");
    		}
    		else if (data_type.equals("Invocation")) {
    			dataValue = pythonOperationExecute(operation, "interface");
    		}
    		else if (data_type.equals("InvocationTeardown")) {
    			dataValue = pythonOperationTeardown(operation,"interface");
    		}

    		// Tags for Adapters to CORBA
    		else if (data_type.equals("ConvertReceptacleParameterToCorba")) {
    			dataValue = convertReceptacleParameterToCorba(operation);
    		}

    		else if (data_type.equals("ConvertReceptacleParameterToCpp")) {
    			dataValue = convertReceptacleParameterToCpp(operation);
    		}
        
    		else if(data_type.equals("IncludeLocalInterface")) {
    			return "#include \"" + getLocalNamespace(operation,"_","",false) + "/" +
    						operation.getIdentifier() + ".h\"";
    		}
        
    		else if (data_type.equals("ParameterConvertInclude")) {
    			return pythonOperationParamInclude(operation);
    		}
        
    		else if (data_type.equals("ExceptionInclude")) {
    			return pythonOperationExceptionInclude(operation);
    		}
       
    		else if (data_type.equals("ResultConvertInclude")) {
    			return pythonOperationResultInclude(operation);
    		}
    		
    		// MInterfaceDef expansion
    		else if (data_type.equals("ShortDescription")) {
    			return getDescription("short",operation);
    		}
    		
    		else if (data_type.equals("CmptResultInclude"))
    			return pythonOperationResultInclude2(operation);
    		
    		else if (data_type.equals("CmptResultUsing"))
    			return pythonOperationResultUsing(operation);

    		else {
    			dataValue = super.data_MOperationDef(data_type, dataValue);
    		}
    		// TODO: remove equal include lines (operation type, attribute types)
    		return dataValue;
    }
   


	protected String data_MModuleDef( String data_type, String data_value ) {
    		if (data_type.startsWith("MModuleDef")) {
    			MContainer container = (MContainer) current_node;
    			String module = container.getIdentifier();
    			String module_orig = (String) namespace.lastElement();
    			
    			// Map tree:
    			//	    root         ->    modules   -> template_types ->      expansions
    			// (module_elements) -> (module_map) ->   (ret_type)   -> (template substitution)
    			//
    			if (!module_elements.containsKey(module))
    				module_elements.put(module,new Hashtable());
    			Map module_map = (Map) module_elements.get(module);
    			if (!module_map.containsKey(data_type))
    				module_map.put(data_type,new Hashtable());
    			Map ret_map = (Map) module_map.get(data_type);

    			List contents = container.getContentss();
    			for(Iterator c = contents.iterator(); c.hasNext();) {
    				MContained contained = (MContained) c.next();
    				if (contained instanceof MHomeDef && data_type.startsWith("Home",10) ||
    						contained instanceof MStructDef && data_type.startsWith("Struct",10) ||
							contained instanceof MEnumDef && data_type.startsWith("Enum",10)) {
    					String identifier = contained.getIdentifier();
    					Map vars = new Hashtable();
    					vars.put("Identifier",identifier);
    					if (contained instanceof MHomeDef) {
    	   					MHomeDef home = (MHomeDef) contained;
    	   					String componentName = home.getComponent().getIdentifier();
//   	   					vars.put("IncludeNamespace",handleNamespace("FileNamespace", componentName) + "_CCM_Session_" + componentName);
    	   					vars.put("IncludeNamespace",getLocalNamespace(home,file_separator,componentName,false));
    					}
    					
    					vars.put("CodaExport","");
    					if (( flags & FLAG_CODA_INFO) != 0 ) {
    						initCoda( );
    						Map exports = coda.getExports();
    						if (exports.containsKey(identifier)) {
    							vars.put("CodaExport", "if (global_dict != NULL && PyDict_Check(global_dict )) {\n        Py_INCREF(" +
    										identifier + "_type_);\n        PyModule_AddObject( global_module, \"" +
    										(String) exports.get(identifier) + "\", (PyObject*) " +
    										identifier + "_type_ );\n    }");
    						}
    					}
    					
    					Template template = template_manager.getRawTemplate(data_type, null);
    					ret_map.put(identifier,template.substituteVariables(vars));
    				}
    			}
    			return Text.join("\n",ret_map);
    		}
    		else {
    			return data_value;
    		}
    }
    
    protected String data_MHomeDef(String data_type, String data_value)
    {
        MHomeDef home = (MHomeDef) current_node;
        MComponentDef component = home.getComponent();

        String home_id = home.getIdentifier();
        String component_id = component.getIdentifier();

        if (data_type.equals("Module")) {
        		return (String) namespace.lastElement();
        }
        else if(data_type.equals("IncludeLocalHome")) {
			String result = "#include <" + getLocalNamespace(home,"/",component.getIdentifier(),false) + "/" +
					home.getIdentifier() + "_gen.h>";
			return result;
        }
        else if(data_type.equals("IncludeLocalComponent")) {
        		String result = "#include <" + getLocalNamespace(home,"/",component.getIdentifier(),false) + "/" +
					component.getIdentifier() + "_gen.h>";
        		return result;
        }

        return super.data_MHomeDef(data_type, data_value);
    }

    protected String data_MInterfaceDef(String data_type, String data_value)
    {
        MInterfaceDef iface = (MInterfaceDef) current_node;

        if(data_type.equals("IncludeLocalHeader")) {
    			return "#include <" + getLocalNamespace(iface,"/","",false) + "/" +
				iface.getIdentifier() + ".h>";
        }
        else if (data_type.equals("GetSetMethods")) {
        		List attributes = iface.getFilteredContents(MDefinitionKind.DK_ATTRIBUTE,true);
        		return attributes.size() > 0 ? "Py" + iface.getIdentifier() + "_getset" : "0";
        }
        
		else if (data_type.equals("CodaConvertInclude")) {
			if ((flags & FLAG_CODA_INFO) == 0)
				return "";
			Set thestuff = getOpIncludes(iface,"all", "python");
			return Text.join("\n",thestuff);
		}

		else if (data_type.equals("CodaResultInclude"))
			return pythonCodaResultInclude( );
			

		else if (data_type.equals("CodaTypeInclude")) {
			if ((flags & FLAG_CODA_INFO) == 0)
				return "";
			Set thestuff = getOpIncludes(iface,"type", "python");
			return Text.join("\n",thestuff);
		}

        return super.data_MInterfaceDef(data_type, data_value);

    }

//    protected String data_MInterfaceDef(String data_type, String data_value)
//   {
//        MInterfaceDef iface = (MInterfaceDef) current_node;
//
//        if (data_type.equals("DamnStupid")) {
//			List inits = new ArrayList();
//			List contents = iface.getFilteredContents(MDefinitionKind.DK_ALL,true);
//			for(Iterator c = contents.iterator(); c.hasNext();) {
//				inits.add(((MContained) (c.next())).getIdentifier());
//			}
//        		return Text.join(" ",inits);
//        }
//
//        else {
//            return super.data_MInterfaceDef(data_type, data_value);
//        }
//    }

    /**
     * Implements the following tags found in the MProvidesDef* templates:
     * 'ProvidesInclude' 'ProvidesConvertInclude' includes facet converters
     * 'IdlProvidesType' 'ProvidesType' 'ComponentType'
     */
    protected String data_MProvidesDef(String data_type, String data_value)
    {
        MProvidesDef provides = (MProvidesDef) current_node;
        MInterfaceDef iface = ((MProvidesDef) current_node).getProvides();
        MComponentDef component = provides.getComponent();
        List scope = getScope((MContained) iface);
        StringBuffer ret = new StringBuffer();

        if (data_type.equals("InterfaceType")) {
            data_value = provides.getProvides().getIdentifier();
        }
        else if (data_type.equals("ComponentType")) {
        		data_value = component.getIdentifier();
        }
        else if(data_type.equals("IncludeLocalHeader")) {
            data_value = "#include <" + getLocalNamespace(provides,"/","",false) + "/" +
				provides.getProvides().getIdentifier() + ".h>";
        }

        else {
            data_value = super.data_MProvidesDef(data_type, data_value);
        }
        return data_value;
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
        
        if(data_type.equals("IncludeLocalComponent")) {
    			return "#include <" + getLocalNamespace(component,"/",component.getIdentifier(),false) + "/" +
				component.getIdentifier() + "_gen.h>";
        }
        else if(data_type.equals("IncludeHome")) {
//        		return "#include \"" + handleNamespace("FileNamespace", component.getIdentifier()) + "_CCM_Session_" +
//					component.getIdentifier() + "/" + home.getIdentifier() + "_python.h\"";
    		return "#include <" + getOutputDirectory(component.getIdentifier()) + "/" + home.getIdentifier() + "_python.h>";
        }
        
		else if (data_type.equals("CodaConvertInclude")) {
			if ((flags & FLAG_CODA_INFO) == 0)
				return "";
			Set thestuff = getOpIncludes(component,"all", "python");
			return Text.join("\n",thestuff);
		}
        
		else if (data_type.equals("CodaTypeInclude")) {
			if ((flags & FLAG_CODA_INFO) == 0)
				return "";
			Set thestuff = getOpIncludes(component,"type", "python");
			return Text.join("\n",thestuff);
		}
       
        return super.data_MComponentDef(data_type, data_value);
    }

    protected String data_MEnumDef(String data_type, String data_value)
    {
        List ret = new ArrayList();
        MEnumDef enm = (MEnumDef) current_node;
        // Convert C++ enum members from and to CORBA enum members
        if (data_type.startsWith("MEnumDef")) {
        		List enums = new ArrayList();
        		Map vars = new Hashtable(output_variables);
        		vars.put(getScopeID("Module"),(String) namespace.lastElement());
        		vars.put(getScopeID("EnumType"),enm.getIdentifier());
        		vars.put(getScopeID("LocalNamespace"),handleNamespace("LocalNamespace",""));
        		Template t = template_manager.getTemplate(data_type, current_name);
        		for (Iterator i = enm.getMembers().iterator(); i.hasNext();) {
        			vars.put(getScopeID("Identifier"),(String) i.next());
        			enums.add(t.substituteVariables(vars));
        		}
        		return Text.join("\n",enums);
        }
        else if (data_type.equals("Module")) {
        		return (String) namespace.lastElement();
        }
        else if (data_type.equals("IncludeLocalHeader")) {
        		return "#include <" + getLocalNamespace(enm,"/","",false) + "/" +
					enm.getIdentifier() + ".h>";
        }
        else if (data_type.equals("DefaultEnum")) {
        		return "Py" + (String) enm.getMembers().get(0) + "_enum";
        }
        return super.data_MEnumDef(data_type, data_value);
    }

    /**
	 * @param variable
	 * @param value
	 * @return
	 */
	private String data_MExceptionDef(String data_type, String data_value) {
		MExceptionDef excp = (MExceptionDef) current_node;
		if (data_type.equals("Module"))
			return excp.getDefinedIn().getIdentifier();
		return data_value;
	}
    
    /**
     * Handle tags defined in %(MFieldDef*)s templates which are not substituted
     * by other templates.
     * 
     * @param data_type
     * @param data_value
     * @return The string that replaces the given tag in the template.
     */
    protected String data_MFieldDef(String dataType, String dataValue) {
    		MFieldDef field = (MFieldDef) current_node;
    		MTyped type = (MTyped) field;
    		MIDLType idl_type = type.getIdlType();
    		String fieldName = ((MFieldDef) current_node).getIdentifier();
    		String baseType = getBaseIdlType(type);

    		// Handle %(CORBATypeIn)s tag in %(MFieldDef*)s templates
    		if (dataType.equals("ValueToPython")) {  		
    			if (isPrimitiveType(idl_type)) {
    				return "Py_BuildValue( \"" + 
						flag_mappings.get(baseType) + "\", arg->" +
						fieldName + " )";
                }
                else if(idl_type instanceof MStructDef) {
                    return convertLocalToPythonConversion(type,fieldName);
                }
    		}
    		else if ( dataType.equals("StructIdentifier")) {
    			MStructDef struct = ((MFieldDef)current_node).getStructure();
    			return struct.getIdentifier();
    		}
    		else if ( dataType.equals("IdentifierTypeSymbol")) {
    			return getType(type, SYMBOL_TYPE);
    		}
    		else if ( dataType.equals("IdentifierType")) {
    			return getType(type, C_TYPE);
    		}
    		else if ( dataType.equals("FieldFree")) {
//    		if ( idl_type instanceof MStringDef || idl_type instanceof MWstringDef )
//    				return "free(self->" + field.getIdentifier() + ");";
//    			// || type instanceof MEnumDef || type instance of MStructDef
//    		else
    				return "";
    		}
    		
    		else if ( dataType.equals("ScopeId")) {
        		MStructDef struct = field.getStructure( );
        		return getLocalNamespace(struct,"::","", true) + struct.getIdentifier() + "." + field.getIdentifier( );
    		}
    		else if ( dataType.equals("FieldConvertString")) {
        	    return "fubar";
    		}

    		return dataValue;
    }


	/**
	 * Implements the following tags found in the MAttribute* templates:
	 * 'CORBAType' 'AttributeConvertInclude'
	 * Note that this method relates to component attributes only!
	 */
	protected String data_MAttributeDef(String data_type, String data_value)
	{
	    // current_node is MAttributeDef
	    MTyped type = (MTyped) current_node;
	    MIDLType idl_type = type.getIdlType();
	    String baseType = getBaseIdlType(type);
	    MAttributeDef attribute = (MAttributeDef)current_node;
	    
	    // Handle %(CORBAType)s tag in %(MAttributeDef*)s templates
	    if(data_type.equals("InterfaceType")) {
	        data_value = attribute.getDefinedIn().getIdentifier();
	    }	    
	    else if(data_type.equals("IdentifierTypeSymbol")) {
			if (isPrimitiveType(idl_type))
				return (String) type_ident_mappings.get(baseType);
			else if(idl_type instanceof MStructDef)
				return getBaseIdlType(type);
	    }	    
	    else if(data_type.equals("IdentifierType")) {
			if (isPrimitiveType(idl_type))
				return (String) type_mappings.get(baseType);
			else if(idl_type instanceof MStructDef)
				return getBaseIdlType(type);
	    }
	    else if(data_type.equals("AttributeConvertInclude")) {
	        Set code = new HashSet();
	        StringBuffer buffer = new StringBuffer();
	        if(idl_type instanceof MPrimitiveDef || idl_type instanceof MStringDef 
	                || idl_type instanceof MWstringDef) {
	            // no include statement needed for these primitive types
	        }
	         else {   
	            buffer.append("#include \"").append(baseType).append("_remote.h\"\n");
	        }
	        data_value = buffer.toString(); 
	    }
	    return data_value;
	}
    protected String data_MSequenceDef(String data_type, String data_value)
    {
        MTyped type = (MTyped) current_node;
        MIDLType idlType = type.getIdlType();
        MContained contained = (MContained) type;
        MTyped singleType = (MTyped) idlType;
        MIDLType singleIdlType = singleType.getIdlType();
        
        String coda_check = null;
        String coda_convert_from = null;
        String coda_convert_to = null;
        String base_type = getBaseIdlType(singleType);
        
        if (! isPrimitiveType(singleIdlType) && (flags & FLAG_CODA_INFO) != 0 ) {
			initCoda();
			Map conversion = coda.getConversionInfo(base_type,"python");
			
			if (conversion.containsKey("to") && conversion.containsKey("from")) {
				Map to = (Map) (((List)conversion.get("to")).get(0));
				Map from = (Map) (((List)conversion.get("from")).get(0));
				Map include_set = (Map) conversion.get("include");
				Map include = new HashMap();
			
				coda_check = (String) to.get("check");
				coda_convert_from = (String) to.get("func");
				coda_convert_to = (String) from.get("func");
				
//				for (Iterator i=include_set.values().iterator(); i.hasNext(); )
//					include.putAll((Map)i.next());

//				if (data_type.equals("CodaConversionToFunc")) {
//					return (String) to.get("func");
//				} else if (data_type.equals("CodaConversionToCheck")) {
//					return (String) to.get("check");
//				} else if (data_type.equals("CodaConversionFromFunc")) {
//					return (String) from.get("func");
//				} else if (data_type.equals("CodaConversionFromCheck")) {
//					return (String) from.get("check");
//				} else if (data_type.equals("CodaConvertInclude") && include != null) {
//					return Text.join("\n",include.keySet());
//				} else
//					return "";
			}
        }
        
        // ?convert_%(ElementTypeSymbol)s_from_python

		Map vars = new Hashtable(output_variables);
		String singletype_str = getType(singleType, C_TYPE);
		vars.put(getScopeID("ElementType"),(isPrimitiveType(singleIdlType) ? singletype_str :
										  	"::" + handleNamespace("LocalNamespace","") + singletype_str));
		vars.put(getScopeID("ElementTypeSymbol"),getType(singleType, SYMBOL_TYPE));
		
		if (coda_check != null) 
			vars.put(getScopeID("ElementCheck"),coda_check);
		else {
			String conv_mapping = (String) conversion_mappings.get(base_type);
			vars.put(getScopeID("ElementCheck"),conv_mapping == null ? "is_python_" + base_type : conv_mapping);
		}

		vars.put(getScopeID("ElementConvertFrom"), coda_convert_from != null ? coda_convert_from : ("convert_" + getType(singleType, SYMBOL_TYPE) + "_from_python"));
		vars.put(getScopeID("ElementConvertTo"), coda_convert_to != null ? coda_convert_to : ("convert_" + getType(singleType, SYMBOL_TYPE) + "_to_python"));
		
		vars.put(getScopeID("UsingElementType"), (isPrimitiveType(singleIdlType) ? "" :
				("using ::" + handleNamespace("LocalNamespace","") + getType(singleType, C_TYPE) + ";")));
		vars.put(getScopeID("LocalNamespace"),handleNamespace("LocalNamespace",""));
		
		vars.put(getScopeID("CodaPolymorphTo"),"");
		vars.put(getScopeID("CodaPolymorphFrom"),"");
		vars.put(getScopeID("CodaPolymorphInclude"),"");

		if (( flags & FLAG_CODA_INFO) != 0 ) {
			
			String polytype = "";
			
			if (current_node instanceof MAliasDef)
				polytype = ((MAliasDef)current_node).getIdentifier();
			else
				throw new RuntimeException("can't find identifier");
			
			boolean found_polymorph = false;
			Map info = coda.getPolymorphInfo(polytype,"python");
			List mods = new ArrayList();
			mods.add("to");
			mods.add("from");
			for (Iterator i=mods.iterator(); i.hasNext(); ) {
				String s = (String) i.next();
				if (info.containsKey(s)) {
					List detail_list = (List) info.get(s);
					List polymorph = new ArrayList();
					for (Iterator details = detail_list.iterator(); details.hasNext(); ) {
						Map detail = (Map) details.next();
						if (detail.containsKey("check") && detail.containsKey("func")) {
							String check = (String) detail.get("check");
							String func = (String) detail.get("func");
							polymorph.add("if ( " + check + "( obj ) )\n        return " + func + (s.equals("to") ? "( obj, s );" : "(obj);"));
							found_polymorph = true;
						}
					}
					if (found_polymorph) {
						vars.put(getScopeID("CodaPolymorph" + Text.capitalize(s)), Text.join("\n",polymorph));
					}
				}
			}
			if (found_polymorph && info.containsKey("include")) {
				// I am not sure why we go through this entire song and dance just to fetch
				// CodaPolymorphInclude... there seems to be much room for optimization.
				Map includes = (Map)info.get("include");
				if (includes.containsKey("all"))
					vars.put(getScopeID("CodaPolymorphInclude"),
							Text.join("\n",((Map)includes.get("all")).keySet( )));
			}
		}
        if (data_type.equals("ConvertToPythonDecl") ||
        		data_type.equals("ConvertToPythonImpl")) {
        	
        		Template t = template_manager.getTemplate("MSequenceDef" + data_type, current_name);
        		data_value = t.substituteVariables(vars);

        } else if (data_type.equals("LoopConvertTo")) {
        } else if (data_type.equals("LoopConvertFrom")) {
        } else if (data_type.equals("CodaPolymorphInclude")) {
        		return (String) vars.get(getScopeID("CodaPolymorphInclude"));
        }
        return data_value;
    }

    /**
	 * @param out_type TODO
     * @param singleType
     * @return
	 */
	private String getType(MTyped type, int out_type) {
		MIDLType idl_type = type.getIdlType();
		if ( isPrimitiveType(idl_type))
			return (out_type == SYMBOL_TYPE ? (String) type_ident_mappings.get(getBaseIdlType(type)) :
					out_type == DOCSIG_TYPE ? (String) type_docsig_mappings.get(getBaseIdlType(type)) :
					(String) type_mappings.get(getBaseIdlType(type)));
		else if ( idl_type instanceof MStructDef )
			return ((MStructDef)idl_type).getIdentifier();
		else if ( idl_type instanceof MAliasDef )
			return ((MAliasDef)idl_type).getIdentifier();
		else if ( idl_type instanceof MEnumDef )
			return ((MEnumDef)idl_type).getIdentifier();
		else
			return ((MContained)type).getIdentifier();
	}


	protected String data_MStructDef(String data_type, String dataValue) {
        MContained contained = (MContained) current_node;

//        MTyped type = (MTyped) current_node;
//        MIDLType idlType = type.getIdlType();
//        String baseType = getBaseIdlType(type);
//        MOperationDef operation = (MOperationDef) type;
        MStructDef struct = (MStructDef) current_node;

        if (data_type.equals("IncludeLocalHeader")) {
        		return "#include <" + getLocalNamespace(struct,"/","",false) + "/" +
					struct.getIdentifier() + ".h>";
        }
        if (data_type.equals("FieldConvertString")) {
        		List flags = new ArrayList();
            for (Iterator i = struct.getMembers().iterator(); i.hasNext();) {
           		// current_node is MFieldDef
            		MTyped type = (MTyped) i.next();
            		flags.add(convertTypeToPythonFlag(type));
            }
            return "|" + Text.join("",flags);
        }
        else if ( data_type.equals("FieldFree")) {
        		List free = new ArrayList();
        		for (Iterator i = struct.getMembers().iterator(); i.hasNext();) {
        			// current_node is MFieldDef
        			MIdTyped type = (MIdTyped) i.next();
        			MIDLType idl_type = type.getIdlType();
        			if (idl_type instanceof MPrimitiveDef )
        				continue;
        			else if (idl_type instanceof MStringDef || idl_type instanceof MWstringDef)
        				continue;
        			//	free.add("    free(self->" + type.getIdentifier() + ");");
        			// || type instanceof MEnumDef || type instance of MStructDef
        		}
        		return Text.join("\n",free);
        }
        else if (data_type.equals("FieldInitPre")) {
        		List init = new ArrayList();
        		for (Iterator i = struct.getMembers().iterator(); i.hasNext();) {
        			// current_node is MFieldDef
        			MIdTyped type = (MIdTyped) i.next();
        			MIDLType idl_type = type.getIdlType();
        			String baseType = getBaseIdlType(type);
        			if (idl_type instanceof MPrimitiveDef )
        				init.add("    default_" + (String) type_ident_mappings.get(baseType) + "( self->structure->" + type.getIdentifier() + ", \"" +
        							getLocalNamespace(struct,"::","", true) + struct.getIdentifier() + "." + type.getIdentifier() + "\" );");
        			else if (idl_type instanceof MStringDef || idl_type instanceof MWstringDef) {
        				init.add("    const char *" + type.getIdentifier() + "_param_char_ = 0;");
        				// || type instanceof MEnumDef || type instance of MStructDef
        			}
        		}
        		return Text.join("\n",init);
        }
        else if (data_type.equals("FieldInitPost")) {
        		List init = new ArrayList();
        		for (Iterator i = struct.getMembers().iterator(); i.hasNext();) {
        			// current_node is MFieldDef
        			MIdTyped type = (MIdTyped) i.next();
        			MIDLType idl_type = type.getIdlType();
            		String baseType = getBaseIdlType(type);
        			if (idl_type instanceof MPrimitiveDef )
        				continue;
        			else if (idl_type instanceof MStringDef || idl_type instanceof MWstringDef) {
        				init.add("    if ( " + type.getIdentifier() + "_param_char_ )");
        				init.add("        self->structure->" + type.getIdentifier() + " = " + type.getIdentifier() + "_param_char_;");
        				init.add("    else");
        				init.add("        default_" + (String) type_ident_mappings.get(baseType) + "( self->structure->" + type.getIdentifier() + ", \"" +
        						getLocalNamespace(struct,"::","", true) + struct.getIdentifier() + "." + type.getIdentifier() + " \");\n");
        			// || type instanceof MEnumDef || type instance of MStructDef
        			}
        		}
        		return Text.join("\n",init);
        }
        else if (data_type.equals("FieldConvertVariables")) {
        		List convert = new ArrayList();
            for (Iterator i = struct.getMembers().iterator(); i.hasNext();) {
                MFieldDef mem = (MFieldDef) i.next();
                // current_node is MFieldDef
                MIdTyped type = (MIdTyped) mem;
                convert.add(Text.join(", ",pyLocalArgConvert(CONVERT_TO_LOCAL,true, type, "structure")));
            }
            return Text.join(", ",convert);
        }
        
        else if (data_type.equals("CopyFields")) {
        		List copy = new ArrayList();
        		for (Iterator i = struct.getMembers().iterator(); i.hasNext();) {
        			MFieldDef mem = (MFieldDef) i.next();
        			// current_node is MFieldDef
        			MIdTyped type = (MIdTyped) mem;
        			String field_name = mem.getIdentifier();
        			copy.add("    to->" + field_name + " = from->" + field_name + ";");
        		}
        		return Text.join("\n",copy);
        	}
        
		else if ( data_type.equals("Module")) {
			return (String) namespace.lastElement();
		}
		else if ( data_type.startsWith("Coda")) {
			if (( flags & FLAG_CODA_INFO) != 0 ) {
				Map conversion = coda.getConversionInfo(struct.getIdentifier(),"python");
			
				if (conversion.containsKey("to") && conversion.containsKey("from")) {
					Map to = (Map) (((List)conversion.get("to")).get(0));
					Map from = (Map) (((List)conversion.get("from")).get(0));
					Map include_set = (Map) conversion.get("include");
					Map include = new HashMap();
				
					for (Iterator i=include_set.values().iterator(); i.hasNext(); )
						include.putAll((Map)i.next());

					if (data_type.equals("CodaConversionToFunc")) {
						return (String) to.get("func");
					} else if (data_type.equals("CodaConversionToCheck")) {
						return (String) to.get("check");
					} else if (data_type.equals("CodaConversionFromFunc")) {
						return (String) from.get("func");
					} else if (data_type.equals("CodaConversionFromCheck")) {
						return (String) from.get("check");
					} else if (data_type.equals("CodaConvertInclude") && include != null) {
						return Text.join("\n",include.keySet());
					} else
						return "";
				} else return "";
			}
			else return "";
		}
    
       return dataValue;
    }

    //====================================================================
    // Facet Adapter Stuff
    //====================================================================

    protected boolean isPrimitiveType(MIDLType type)
    {
        if (type instanceof MPrimitiveDef || type instanceof MStringDef
                || type instanceof MWstringDef) {
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

    private String getOutputDirectory(String local)
    {
        List names = new ArrayList(namespace);
        if(!local.equals("")) {
            names.add("CCM_Session_" + local);
        }
        String result = join("/", names);
        if (result.startsWith("CCM_Python/"))
        		result = result.replaceFirst("CCM_Python","Python_Converter");
        return result;
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
        String sourceCode = template.substituteVariables(output_variables);
        String[] sourceFiles = sourceCode.split("<<<<<<<SPLIT>>>>>>>");
        String[] remoteSuffix = {
                "_python.h", "_python.cc"
        };

        for(int i = 0; i < remoteSuffix.length; i++) {
            if(sourceFiles[i].trim().equals("")) {
                // skip the file creation
                continue;
            }

            try {
                if(current_node instanceof MComponentDef) {
                    // write the component files
                    String componentName = ((MContained) current_node).getIdentifier();
                    String fileDir = getOutputDirectory(componentName);

                    Code.writeFile(driver, output_dir, fileDir, componentName + remoteSuffix[i],
                                   sourceFiles[i]);
                }
                else if(current_node instanceof MHomeDef) {
                    // write the home files
                    MHomeDef home = (MHomeDef) current_node;
                    String componentName = ((MContained) home.getComponent()).getIdentifier();
                    String homeName = home.getIdentifier();
                    String fileDir = getOutputDirectory(componentName);

                    Code.writeFile(driver, output_dir, fileDir, homeName + remoteSuffix[i],
                                   sourceFiles[i]);
                    Code.writeMakefile(driver, output_dir, fileDir, "py", "");
                }
                else if (current_node instanceof MStructDef) {
                	  // write converter files
                	  String nodeName = ((MContained) current_node).getIdentifier();
                	  String fileDir = "Python_Converter";
                	  int source_index = i;
                	  
                	  Map conversion = coda.getConversionInfo(nodeName,"python");

                	  if (conversion.containsKey("to") && conversion.containsKey("from")) {
                		  if (remoteSuffix[i].endsWith(".cc"))
                			  source_index += 1;
                	  }
                	  if (! degenerate_aliases.contains(nodeName)) {
                		  Code.writeFile(driver, output_dir, fileDir, nodeName + remoteSuffix[i],
                				  sourceFiles[source_index]);
                		  Code.writeMakefile(driver, output_dir, fileDir, "py", "");
                	  }
                }
                else if(current_node instanceof MInterfaceDef
                		   || current_node instanceof MAliasDef
                        || current_node instanceof MExceptionDef
                        || current_node instanceof MEnumDef
					   || current_node instanceof MModuleDef ) {
                    // write converter files
                    String nodeName = ((MContained) current_node).getIdentifier();
                    String fileDir = "Python_Converter";

                    if (! degenerate_aliases.contains(nodeName)) {
                    		Code.writeFile(driver, output_dir, fileDir, nodeName + remoteSuffix[i],
                    						sourceFiles[i]);
                    		Code.writeMakefile(driver, output_dir, fileDir, "py", "");
                    }
                }

                else {
                    throw new RuntimeException("CppPythonGeneratorImpl.writeOutput()"
                            + ": unhandled node!");
                }
            }
            catch(Exception e) {
                System.out.println("!!!Error " + e.getMessage());
            }
        }
 
//        if( current_node instanceof MModuleDef ) {
//        	    // write converter files
//            String nodeName = ((MContained) current_node).getIdentifier();
//            String fileDir = "Python_Converter";
//
//            Code.writeFile(driver, output_dir, "templates/CppPythonTemplates", "MModuleDef_" + nodeName, sourceCode );
//        }

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
    
    protected String convertPrimitiveParameterToCorba(MParameterDef p)
    {
        MParameterMode direction = p.getDirection();
        MIDLType idlType = ((MTyped) p).getIdlType();
        String baseType = getBaseIdlType(p);
        String corbaType = "getCorbaType(p)";
        List list = new ArrayList();

        list.add(Text.insertTab(1) + corbaType + " parameter_" + p.getIdentifier() + ";");
        if(direction != MParameterMode.PARAM_OUT) {
            list.add(Text.insertTab(1) + "CCM_Remote::convertToCorba(" + p.getIdentifier()
                    + ", parameter_" + p.getIdentifier() + ");");
        }
        return Text.join("\n", list);
    }

    // TODO: If there are no additional features, remove this method
    protected String convertReceptacleUserParameterToCpp(MParameterDef p)
    {
        return convertReceptaclePrimitiveParameterToCpp(p);
    }

    protected String convertUserParameterToCorba(MParameterDef p)
    {
        MParameterMode direction = p.getDirection();
        List list = new ArrayList();

        if(direction == MParameterMode.PARAM_IN || direction == MParameterMode.PARAM_INOUT) {
            list.add(Text.insertTab(1) + "getCorbaStubName(contained, \"::\")" + "_var parameter_"
                    + p.getIdentifier()
                    + "= new " + "getCorbaStubName(contained, \"::\")" + ";");
            list.add(Text.insertTab(1) + "CCM_Remote::convertToCorba(" + p.getIdentifier()
                    + ", parameter_" + p.getIdentifier() + ");");
        }
        else { // MParameterMode.PARAM_OUT
            list.add(Text.insertTab(1) + "getCorbaStubName(contained, \"::\")" + "_var parameter_"
                    + p.getIdentifier() + ";");
        }
        return Text.join("\n", list);
    }

    protected String pythonOperationSetup(MOperationDef op, String container)
    {
    		List decl = new ArrayList();
    		List flags = new ArrayList();
    		List args = new ArrayList();
    		List post = new ArrayList();
    		List keyword_list = new ArrayList();

    		List parameters = op.getParameters();
    		List default_values = findDefaults(op,Default_impl, "python", "::" + getLocalNamespace((MContained)current_node, "::", "", true));
    		Iterator defaults = default_values.iterator();
    		for (Iterator params = parameters.iterator(); params.hasNext();) {
    			MParameterDef p = (MParameterDef) params.next();
    			MParameterMode mode = p.getDirection();
    			if (mode != MParameterMode.PARAM_OUT)
    				keyword_list.add("\"" + p.getIdentifier() + "\"");
    			String dcl = convertIdlParamToPythonDecl(p,(String)defaults.next());
    			if (dcl.length() > 0) {
    				decl.add(dcl);
    				String ps = convertIdlParamToPythonSetupPost(p);
    				if (ps.length() > 0)
    					post.add(ps);
    			}
    				
    			if (mode != MParameterMode.PARAM_OUT) {
    				flags.add(convertTypeToPythonFlag((MTyped)p));
    				args.add(Text.join(", ",pyLocalArgConvertCoda(CONVERT_TO_LOCAL,true,op, p, container)));
    			}

 /*          else if(isComplexType(idl_type)) {
             	decl.add(convertUserParameterFromCorbaToCpp(p));
             }
*/
    		}
    		
    		String default_objs = "  PyObject *objs[" + parameters.size() + "] = { ";
    		String default_flag = "    unsigned char dflt[" + parameters.size() + "] = { ";
    		String default_setobjs = "    PyArg_ParseTuple(args, \"|";
    		String default_setobj_ptrs = "";
    		String xml_defaults = "    ";
    		String supplied_parameters = "";
    		String param_flags = "    ";
    		defaults = default_values.iterator();
    		for (int i=0; i < parameters.size(); ++i) {
    			if (i == parameters.size()-1) {
        			default_flag += "0";
        			default_objs += "0x0";
        			default_setobj_ptrs += "&objs[" + i + "]";	
    			}
    			else {
    				default_flag += "0, ";
    				default_objs += "0x0, ";
    				default_setobj_ptrs += "&objs[" + i + "], ";
    			}
    			supplied_parameters += "    if (objs[" + i + "]) dflt[" + i + "] |= 0x001;\n";
    			default_setobjs += "O";
    			if (((String)defaults.next()).length() > 0)
    				xml_defaults += "dflt[" + i + "] |= 0x010; ";
    		}
    		default_flag += " };\n";
    		default_objs += " };\n";
    		
    		String keywords = "    static char *kwlist[] = { " + Text.join(",","strdup",keyword_list) + ", NULL };\n";
    		String result = Text.join("\n",decl) + "\n/*" + default_objs + "\n" + default_flag + "\n" + xml_defaults + "\n" +
    					default_setobjs + "\", " + default_setobj_ptrs + " );\n" +
    					supplied_parameters + "*/";
				
    		if ( flags.size() > 0 && args.size() > 0 )
    			result = result + keywords + "\n    clear_python_conversion_error( );\n"  +  
							"    if ( ! PyArg_ParseTupleAndKeywords(args, keywds, \"|" + 
							Text.join("",flags) + "\", kwlist, " + 
							Text.join(", ",args) + ") ) return NULL;\n" +
							"    if ( get_python_conversion_error() ) return NULL;\n"  +
							Text.join("\n",post);
    		return result;
    }
    
    protected String pythonOperationExecute(MOperationDef op, String container ) {
    	
    	List pre = new ArrayList();
    	List post = new ArrayList();
    	List args = new ArrayList();
    	List exceptions = new ArrayList();
    	
    	String Space = "";
    	String catchall_try = "    try {";
    	String catchall_catch = "\n    } catch(const LocalComponents::CCMException &ccme) {";
    	catchall_catch += "\n        PyErr_SetString( PyExc_StandardError, ccme.what() );";
    	catchall_catch += "\n        return NULL;\n    }";
    	Space += "    ";
		
    	// setup exceptions
    	for (Iterator excps = op.getExceptionDefs().iterator(); excps.hasNext();) {
    		MExceptionDef e = (MExceptionDef) excps.next();
    		exceptions.add("    catch( ::" + getLocalNamespace((MContained)current_node, "::", "", true) + e.getIdentifier() + " &exc ) {\n" +
							"        PyErr_SetString( get_" + e.getIdentifier() + "_python_exception( ), \"error: " + e.getIdentifier() + "\" );\n" +
							"        return NULL;\n" +
							"    }" );
    	}
		
    	String Try = "";
    	String Catch = "";
		
    	if (exceptions.size() > 0) {
    		Try = "    try {\n";
    		Catch = "\n    }\n" + Text.join("\n",exceptions) + "\n";
    		Space += "    ";
    	}
    	
		if ((flags & FLAG_CODA_INFO) != 0) {
			Map pyinfo = coda.getPython( );
			if (pyinfo.containsKey("prereturn")) {
				List pypre = (List) pyinfo.get("prereturn");
				pre.add(Text.join("\n",pypre));
			}
			if (pyinfo.containsKey("postreturn")) {
				List pypost = (List) pyinfo.get("postreturn");
				post.add(Text.join("\n",pypost));
			}
		}
		
    	// setup arguments
    	for (Iterator params = op.getParameters().iterator(); params.hasNext();) {
    		MParameterDef p = (MParameterDef) params.next();
    		MIDLType idl_type = ((MTyped)p).getIdlType();
    		if ((flags & FLAG_CODA_INFO) != 0 &&
    				idl_type instanceof MPrimitiveDef &&
    				((MPrimitiveDef)idl_type).getKind() == MPrimitiveKind.PK_ANY ) {
    			args.add(convertPythonToLocalArg2((MIdTyped)p,"_coda_"));
    			initCoda( );
    			String param_type = coda.getAnyType(op.getDefinedIn().getIdentifier(),
    					op.getIdentifier(), p.getIdentifier());
    			Map conversion = coda.getConversionInfo(param_type,"python");
    			if (! conversion.isEmpty() && conversion.containsKey("to") &&
    					conversion.containsKey("from") && conversion.containsKey("init")) {
    				Map init = (Map) (((List)conversion.get("init")).get(0));
    				pre.add("if ( " + p.getIdentifier() + "_param_ == 0 )\n        " +
    						p.getIdentifier() + "_param_ = " + init.get("func") + "( );");
    			}
    			pre.add("::WX::Utils::SmartPtr<WX::Utils::Value> " + p.getIdentifier() + "_coda_("
    					+ p.getIdentifier() + "_param_);");
    		} else
    			args.add(convertPythonToLocalArg2((MIdTyped)p, null));
    	}
		
    	String ret_type = "";
    	String deref = "*";
    	MIDLType ret_idltype = op.getIdlType();
    	boolean is_complex = false;

    	if(isPrimitiveType(ret_idltype) ||
    			ret_idltype instanceof MInterfaceDef)
    		ret_type = getBaseLanguageType(op);
    	else if(isComplexType(ret_idltype)) {
    		is_complex = true;
    		ret_type = getLocalNamespace((MContained)current_node, "::", "", true) +
				getBaseIdlType((MTyped)op);
    	}

    	// structures are not kept in smart pointers (because
    	// the local functions takes them smart pointerless)
    	if (current_node instanceof MStructDef)
    		deref = "";
        
    	if (ret_type.equals("void"))
    		return (pre.size() > 0 ? "\n   " + Text.join("\n",pre) + "\n" : "") + 
        			catchall_try + Try + Space + "    (" + deref + "self->" + container + ")->" +
					op.getIdentifier() + "( " +
					Text.join( ", ",args) + ");" +
					Catch + catchall_catch + (post.size() > 0 ? "    " + Text.join("\n",post) + "\n" : "");
        	else if (is_complex) {
        		return (pre.size() > 0 ? "    " + Text.join("\n",pre) + "\n" : "") +
        				"    ::" + ret_type + " result_;\n" + catchall_try + Try + Space + 
        				"    result_ = (" + deref + "self->" + container + ")->" + 
        				op.getIdentifier() + "( " +
        				Text.join( ", ",args) + " );" +
        				Catch + catchall_catch + (post.size() > 0 ? "\n    " + Text.join("\n",post) + "\n" : "");
        	} else {
        		MIDLType idl_type = ((MTyped)op).getIdlType();
        		if ((flags & FLAG_CODA_INFO) != 0) {
        			if (idl_type instanceof MPrimitiveDef &&
        					((MPrimitiveDef)idl_type).getKind() == MPrimitiveKind.PK_ANY ) {
        				ret_type = "::WX::Utils::SmartPtr<" + ret_type + ">";
        			}
        			else if (idl_type instanceof MInterfaceDef) {
        				if (current_node instanceof MSupportsDef || current_node instanceof MComponentDef) {	
        					initCoda( );
        					//Map method_info = coda.getTie("interface",op.getDefinedIn().getIdentifier(),op.getIdentifier());
        					//if (method_info != null && method_info.containsKey("params")) {
        					String component = (current_node instanceof MSupportsDef ? ((MSupportsDef)current_node).getComponent().getIdentifier() : 
        											((MComponentDef)current_node).getIdentifier());
        					String override = coda.getTie("interface",component,((MInterfaceDef)idl_type).getIdentifier());
        					if (override != null && override.length() > 0)
        						ret_type = "WX::Utils::SmartPtr<CCM_Session_" + override + "::" + override + ">";
        				}
        				else {
        					String override = coda.getTie("mapping",((MInterfaceDef)idl_type).getIdentifier());
        					if (override != null && override.length() > 0)
        						ret_type = "WX::Utils::SmartPtr<CCM_Session_" + override + "::" + override + ">";
        					else
        						ret_type = "WX::Utils::SmartPtr<" + ret_type + ">";
        				}
        			}
        		}

        		return (pre.size() > 0 ? "    " + Text.join("\n",pre) + "\n" : "") +
        				"    " + ret_type + " result_;\n" + catchall_try + Try + Space + 
        				"    result_ = (" + deref + "self->" + container + ")->" +
        				op.getIdentifier() + "( " +
        				Text.join( ", ",args) + " );" +
        				Catch + catchall_catch + (post.size() > 0 ? "    " + Text.join("\n",post) + "\n" : "");
        	}
    }
 
    protected String getParameterConversionFrom(MOperationDef op, MParameterDef p) {
    		MIDLType idltype = p.getIdlType();
    		
    		if (idltype instanceof MPrimitiveDef && ((MPrimitiveDef)idltype).getKind() == MPrimitiveKind.PK_BOOLEAN) {
    			return "PyBool_FromLong( (long) " + p.getIdentifier() + "_param_ );";
    		}
    		else if ((flags & FLAG_CODA_INFO) != 0 && (idltype instanceof MPrimitiveDef) &&
				((MPrimitiveDef)idltype).getKind() == MPrimitiveKind.PK_ANY) {
			String type = coda.getAnyType(op.getDefinedIn().getIdentifier(),
											op.getIdentifier(),p.getIdentifier());
			Map conversion = coda.getConversionInfo(type,"python");
			if (! conversion.isEmpty() && conversion.containsKey("to") && conversion.containsKey("from")) {
				Map to = (Map) (((List)conversion.get("to")).get(0));
				Map from = (Map) (((List)conversion.get("from")).get(0));
				return to.get("func") + "( " + p.getIdentifier() + "_param_ )";
			} else
				return "Py_BuildValue( \"" + getPythonFlag((MTyped)p) + "\", " + p.getIdentifier() + "_param_ );";
    		}
    		
    		return "";
    }
    
    protected String pythonOperationTeardown(MOperationDef op, String container) {
    		MIDLType ret_idltype = op.getIdlType();
    		
		String ret = "";
		
		// handle out & in/out parameters
		List tags = new ArrayList();
		List conversions = new ArrayList();
		List parameters = op.getParameters();
		for (Iterator params = parameters.iterator(); params.hasNext();) {
			MParameterDef p = (MParameterDef) params.next();
			MParameterMode mode = p.getDirection();
			if (mode != MParameterMode.PARAM_IN) {
				tags.add(p.getIdentifier());
				String conversion = Text.join("",pyLocalArgConvertCoda(CONVERT_FROM_LOCAL,false,op, p, op.getDefinedIn().getIdentifier()));
				conversions.add(conversion);
			}
		}
		
		// handle out & in/out parameters in result
		List retconv = pyLocalArgConvertCoda(CONVERT_FROM_LOCAL,false,op,op,op.getDefinedIn().getIdentifier());
		List retlist = new ArrayList();
		String return_string = null;
		if (! tags.isEmpty() && ! conversions.isEmpty()) {
			retlist.add("    PyObject *result = PyDict_New();");
			Iterator tagsi = tags.iterator();
			Iterator conversionsi = conversions.iterator();
			while (tagsi.hasNext() && conversionsi.hasNext()) {
				String tag = (String) tagsi.next( );
				retlist.add("    PyObject *" + tag + "_retval_ = " + conversionsi.next() + ";");
				retlist.add("    PyDict_SetItemString(result,\"" + tag + "\"," + tag + "_retval_);");
				retlist.add("    Py_DECREF( " + tag + "_retval_ );");
			}
		}
		
		// handle return value
		if ((ret_idltype instanceof MPrimitiveDef) &&
				((MPrimitiveDef)ret_idltype).getKind() == MPrimitiveKind.PK_VOID) {
			if (!tags.isEmpty() && ! conversions.isEmpty())
				retlist.add("    PyDict_SetItemString(result,\"return\",Py_None);");
			else {
				retlist.add("    Py_INCREF( Py_None );");
				retlist.add("    PyObject *result = Py_None;");
			}
		}
		else if (ret_idltype instanceof MInterfaceDef && container.equals("component")) {
			String cmpt = null;
			if (current_node instanceof MSupportsDef)
				cmpt = ((MSupportsDef) current_node).getComponent().getIdentifier();
			else if ((flags & FLAG_CODA_INFO) != 0) {
				String override = coda.getTie("mapping",((MInterfaceDef)ret_idltype).getIdentifier());
				if (override != null && override.length() > 0)
					cmpt = override;
				else
					cmpt = "";
			}

			if (cmpt != null) {
				retlist.add("    PyObject *result_if_ = " + cmpt + "_component_to_pyobj( result_ );");
				if (!tags.isEmpty() && ! conversions.isEmpty()) {
					retlist.add("    PyDict_SetItemString(result,\"result\",(PyObject*)result_if_);");
					retlist.add("    Py_DECREF(result_if_);");
				} else
					retlist.add("    PyObject *result = (PyObject*) result_if_;");
			}
		}
		else 
			if (! retconv.isEmpty()) {
				if (! tags.isEmpty() && ! conversions.isEmpty()) {
					retlist.add("    PyObject *result_retval_ = " + retconv.get(0) + "(result_);");
					retlist.add("    PyDict_SetItemString(result,\"return\",result_retval_);");
					retlist.add("    Py_DECREF(result_retval_);");
				} else
					retlist.add("    PyObject *result = " + retconv.get(0) + "(result_);");
			}
			else {
				if (! tags.isEmpty() && ! conversions.isEmpty())
					retlist.add("    PyDict_SetItemString(result,\"return\",Py_None);");
				else {
					retlist.add("    Py_INCREF( Py_None );");
					retlist.add("    PyObject *result = Py_None;");
				}
			}
		
//          ****** CODE FROM BEFORE I SWITCHED THE RESULT TO USE THE ARG HANDLING ROUTINES ******		
//      		else if (ret_type.equals("bool"))
//      			ret = "    PyObject *result = PyBool_FromLong( (long) result_ );";
//      		else if ((flags & FLAG_CODA_INFO) != 0 && (ret_idltype instanceof MPrimitiveDef) &&
//      				((MPrimitiveDef)ret_idltype).getKind() == MPrimitiveKind.PK_ANY) {
//      			String type = coda.getAnyType(op.getDefinedIn().getIdentifier(),
//      											op.getIdentifier());
//      			Map conversion = coda.getConversionInfo(type,"python");
//      			if (! conversion.isEmpty() && conversion.containsKey("to") && conversion.containsKey("from")) {
//      				Map to = (Map) (((List)conversion.get("to")).get(0));
//      				Map from = (Map) (((List)conversion.get("from")).get(0));
//      				ret = "    PyObject *result = " + from.get("func") + "( result_ );";
//      			} else
//      				ret = "    PyObject *result = Py_BuildValue( \"" + 
//      				getPythonFlag((MTyped)op) + "\", result_ );";
//      		} else
//      			ret = "    PyObject *result = Py_BuildValue( \"" + 
//					getPythonFlag((MTyped)op) + "\", result_ );";
//      }      
//      else
//      		ret = "    PyObject *result = convert_" + getBaseIdlType((MTyped)op) + "_to_python( result_ );";
		
        return Text.join("\n",retlist) + "\n    return result;";
    }

    /**
	 * @param operation
	 * @return
	 */
	private String pythonOperationResultInclude(MOperationDef operation) {
		MTyped type = (MTyped) operation;
		MIDLType optype = operation.getIdlType();
		
		if ( ! isPrimitiveType(optype) ) {
			if (optype instanceof MInterfaceDef) {
				if ((flags & FLAG_CODA_INFO) != 0) {
					String override = coda.getTie("mapping",((MInterfaceDef)optype).getIdentifier());
					if (override != null && override.length() > 0)
						return "#include <Python_Converter/CCM_Local/" + namespace.lastElement() + "/CCM_Session_" + override + "/" + override + "_python.h>";
				}
				return "";
			}
			
			return "#include \"Python_Converter/" + getBaseIdlType(type) + "_python.h\"";
		}
		
		return "";
	}
	private String pythonOperationResultInclude2(MOperationDef operation) {
		MTyped type = (MTyped) operation;
		MIDLType optype = operation.getIdlType();
		
		if (optype instanceof MInterfaceDef) {
			String override = coda.getTie("mapping",((MInterfaceDef)optype).getIdentifier());
			if (override != null && override.length() > 0)
				return "#include <CCM_Local/" + namespace.lastElement() + "/CCM_Session_" + override + "/" + override + "_gen.h>";
		}
		return "";
	}
	private String pythonCodaResultInclude( ) {
		if ((flags & FLAG_CODA_INFO) != 0) {
			Map pyinfo = coda.getPython();
			if (pyinfo.containsKey("include")) {
				Map includes = (Map) pyinfo.get("include");
				return Text.join("\n",includes.keySet());
			}
		}	
		return "";
	}

	private String pythonOperationResultUsing(MOperationDef operation) {
		MTyped type = (MTyped) operation;
		MIDLType optype = operation.getIdlType();
		
		if (optype instanceof MInterfaceDef) {
			String override = coda.getTie("mapping",((MInterfaceDef)optype).getIdentifier());
			if (override != null && override.length() > 0)
				return "using namespace CCM_Local::" + namespace.lastElement() + "::CCM_Session_" + override + ";";
		}
		return "";
	}



	protected String pythonOperationExceptionInclude(MOperationDef operation) {
		Map includes = new Hashtable();
		for (Iterator i = operation.getExceptionDefs().iterator(); i.hasNext();) {
			MExceptionDef exception = (MExceptionDef) i.next();
			includes.put(exception.getIdentifier(), "#include \"Python_Converter/" +
							exception.getIdentifier() + "_python.h\"");
		}
		return Text.join("\n",includes);
	}
	
	/**
	 * @return
	 */
	protected String pythonOperationParamInclude(MOperationDef operation) {
		// TODO Auto-generated method stub
		Map includes = new Hashtable();
		for (Iterator i = operation.getParameters().iterator(); i.hasNext();) {
			MParameterDef parameter = (MParameterDef) i.next();
			MTyped parameterType = (MTyped) parameter;
			MIDLType parameterIdlType = parameterType.getIdlType();
			
			if (parameterIdlType instanceof MAliasDef) {
				MIDLType ctidl = ((MAliasDef)parameterIdlType).getIdlType();
				if (isPrimitiveType(ctidl))
					parameterIdlType = ctidl;
			}

			if (! (parameterIdlType instanceof MPrimitiveDef)
					&& ! (parameterIdlType instanceof MStringDef)) {
				includes.put(getBaseIdlType(parameter),
							"#include \"Python_Converter/" + getBaseIdlType(parameter) +
        					"_python.h\"");
			}
		}
		return Text.join("\n",includes);
	}

    /**
     * Extract the scoped CORBA type from a MTyped entity. TODO: Refactoring,
     * this method is a subset of getCORBALanguageType()
     */
    protected String getPythonType(MTyped p) {
    	
		MIDLType idl_type = ((MTyped) p).getIdlType();
		if (idl_type instanceof MAliasDef) {
			MIDLType ctidl = ((MAliasDef)idl_type).getIdlType();
			if (isPrimitiveType(ctidl))
				idl_type = ctidl;
		}

        String baseType = getBaseIdlType(idl_type);
        String corbaType = "";

        if (type_mappings.containsKey(baseType)) {
            // Primitive data types are mapped via map.
            corbaType = (String) type_mappings.get(baseType);
        } else if(idl_type instanceof MStructDef) {
        		corbaType = getLocalNamespace((MContained)current_node, "::", "", true) + baseType;
        } else if(idl_type instanceof MAliasDef) {
        		corbaType = getLocalNamespace((MContained)current_node, "::", "", true) + baseType;
        }
        /*************************************************
        else if (idlType instanceof MTypedefDef) {
            corbaType = getCorbaStubsNamespace((MContained)current_node,"::") 
            + baseType;
        }
        **************************************************/
        else {
            throw new RuntimeException("CppPythonGeneratorImpl.getPythonType(" + baseType
                    + "): unhandled MTyped!");
        }
        return (isPrimitiveType(idl_type) ? corbaType : "::" + corbaType);
    }

    protected String getPythonFlag(MTyped type)
    {
        String baseType = getBaseIdlType(type);
        String corbaType = "";

        if (flag_mappings.containsKey(baseType)) {
            // Primitive data types are mapped via map.
            corbaType = (String) flag_mappings.get(baseType);
        }
        /*************************************************
        else if (idlType instanceof MTypedefDef) {
            corbaType = getCorbaStubsNamespace((MContained)current_node,"::") 
            + baseType;
        }
        **************************************************/
        else {
            throw new RuntimeException("CppPythonGeneratorImpl.getPythonFlag(" + baseType
                    + "): unhandled MTyped!");
        }
        return corbaType;
    }

    /**
	 * @param p
     * @param default_value TODO
     * @param typed
     * @return
	 */
	
    private String convertIdlParamToPythonDecl(MParameterDef p, String default_value) {
    	
        String cppType = getPythonType(p);
        
        if (cppType.endsWith("*") && (default_value == null || default_value.equals("")))
        		default_value = " = 0";
        
        if (cppType.equals("bool"))
        		return "    bool " + p.getIdentifier() + "_param_;\n" +
        			   "    int " + p.getIdentifier() + "_int_" + default_value + ";";
        else if (cppType.equals("std::string"))
        		return "    " + cppType + " " + p.getIdentifier() + "_param_" + default_value + ";\n" +
        			   "    const char *" + p.getIdentifier() + "_param_char_ = 0;";
        else
        		return "    " + cppType + " " + p.getIdentifier() + "_param_" + default_value + ";";
	}
    
    private String convertIdlParamToPythonSetupPost(MParameterDef p) {
    		String cppType = getPythonType(p);
    		
    		if (cppType.equals("bool"))
    			return "    " + p.getIdentifier() + "_param_ = (" + p.getIdentifier() + "_int_  ? true : false );";
    		else if (cppType.equals("std::string")) {
    			String id = p.getIdentifier();
    			String string = id + "_param_";
    			String chars = id + "_param_char_";
    			return "    if (" + chars + ")\n" +
    				   "        " + string + " = " + chars + ";";
    		} else 
    			return "";
    }

	/**
	 * @param direction TODO
	 * @param pyargparse TODO
	 * @param p
	 * @param container
	 * @return
	 */
	
    private List pyLocalArgConvertCoda(int direction, boolean pyargparse, MOperationDef op, MIdTyped p, String container) {
    		MIDLType idl_type = ((MTyped)p).getIdlType();
    		if ((flags & FLAG_CODA_INFO) != 0 &&
    				idl_type instanceof MPrimitiveDef &&
    				((MPrimitiveDef)idl_type).getKind() == MPrimitiveKind.PK_ANY ) {
    			List ret = new ArrayList();
    			initCoda();
    			String param_type = (p instanceof MOperationDef ? 
    						coda.getAnyType(op.getDefinedIn().getIdentifier(), op.getIdentifier()) : 
    						coda.getAnyType(op.getDefinedIn().getIdentifier(), op.getIdentifier(), p.getIdentifier()));
    			Map conversion = coda.getConversionInfo(param_type,"python");
    			if (! conversion.isEmpty() &&
    					conversion.containsKey("to") && conversion.containsKey("from")) {
    				Map to = (Map) (((List)conversion.get(direction == CONVERT_TO_LOCAL ? "to" : "from")).get(0));
    				ret.add(to.get("func"));
    				if (pyargparse)
    					ret.add("&" + p.getIdentifier( ) + "_param_");
    				else
    					ret.add("(" + p.getIdentifier() + "_coda_)");
    			} else {
    				throw new RuntimeException(
                            "CppPythonGenerator.convertPythonToLocalArgCoda()"
                                    + ": Incomplete conversion info for any parameter '"
                                    + p.getIdentifier() + "' of operation '" + op.getIdentifier() + "'");
    			}
    			return ret;
    		}
    		return pyLocalArgConvert(direction, pyargparse, (MIdTyped)p, container);
    }
    
	private List pyLocalArgConvert(int direction, boolean pyargparse, MIdTyped p, String container) {
		// TODO Auto-generated method stub
		MIDLType idl_type = ((MTyped) p).getIdlType();
//		String type = "";
//		String user_type = "";
//		if(idl_type instanceof MStructDef) {
//			type = "&Py" + getBaseIdlType((MTyped)p) + "_Type, ";
//			user_type = "ob_";h
//		}
		List ret = new ArrayList();
		String direction_str = (direction == CONVERT_TO_LOCAL ? "_from_python" : "_to_python");
		
		if (idl_type instanceof MAliasDef) {
			MIDLType ctidl = ((MAliasDef)idl_type).getIdlType();
			if (isPrimitiveType(ctidl))
				idl_type = ctidl;
		}
		if (idl_type instanceof MPrimitiveDef )
			if (((MPrimitiveDef)idl_type).getKind() == MPrimitiveKind.PK_ANY) {
				throw new RuntimeException(
                        "CppPythonGenerator.convertPythonToLocalArg()"
                                + ": Unhandled any for parameter '" + p.getIdentifier() + "'");
			} else if (container.equals("structure"))
				ret.add("&self->" + container + "->" + p.getIdentifier());
			else {
				if (pyargparse)
					ret.add("&" + p.getIdentifier() + (((MPrimitiveDef)idl_type).getKind() == MPrimitiveKind.PK_BOOLEAN ? "_int_" : "_param_"));
				else {
					String type_string = getType((MTyped) p, SYMBOL_TYPE);
					if (! type_string.equals("void")) {
						ret.add("convert_" + type_string + direction_str);
						ret.add("(" + p.getIdentifier() + "_param_)");
					}
				}
			}
		
		else if (idl_type instanceof MStringDef || idl_type instanceof MWstringDef) {
			if (pyargparse)
				ret.add("&" + p.getIdentifier() + "_param_char_");
			else {
				ret.add("convert_string" + direction_str);
				ret.add("(" + p.getIdentifier() + "_param_)");
			}

		} else if (idl_type instanceof MStructDef ||
				idl_type instanceof MEnumDef ||
				idl_type instanceof MAliasDef ) {
			ret.add("convert_" + getBaseIdlType(p) + direction_str);
			if (pyargparse) {
				if (container.equals("structure"))
					ret.add("&self->" + container + "->" + p.getIdentifier());
				else
					ret.add("&" + p.getIdentifier() + "_param_");
			} else 
				ret.add("(" + p.getIdentifier() + "_param_)");
		}
		
		return ret;
	}

	private String convertPythonToLocalArg2(MIdTyped p, String extension) {
		return p.getIdentifier() + (extension == null ? "_param_" : extension);
	}

	/**
	 * @param idl_type
	 * @return
	 */
	private String convertTypeToPythonFlag( MTyped p) {
		// TODO Auto-generated method stub
		MIDLType idl_type = p.getIdlType();
		if (isPrimitiveType(idl_type) /* &&
				(! (idl_type instanceof MPrimitiveDef) ||
						((MPrimitiveDef)idl_type).getKind() != MPrimitiveKind.PK_ANY)*/ )
			return getPythonFlag(p);
		else if (idl_type instanceof MAliasDef) {
			MAliasDef ct = (MAliasDef) idl_type;
    			MIDLType ctidl = ct.getIdlType();
    			if (isPrimitiveType(ctidl))
    				return getPythonFlag((MTyped)idl_type);
		}
		return "O&";
	}

	private String convertLocalToPythonConversion(MTyped mt, String fieldName) {
		// TODO Auto-generated method stub
		MIDLType idl_type = mt.getIdlType();
		if(idl_type instanceof MStructDef)
			return "convert_" + getBaseIdlType(mt) + "_to_python( arg->" + 
					fieldName + " )";
		else
			return "";
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
                            "CppPythonGenerator.convertReceptacleParameterToCorba()"
                                    + ": Not supported alias type " + containedIdlType);
                }
            }
        }
        return Text.join("\n", list) + "\n";
    }
    protected String getDescription( String type, MOperationDef operation ) {
    		Map desc = coda.getMethodDoc(operation.getDefinedIn().getIdentifier(),operation.getIdentifier());
    		if (desc != null) {
    			if (desc.containsKey(type)) {
    				return (String) desc.get(type);
    			}
    		}
    		return operation.getDefinedIn().getIdentifier() + "." + operation.getIdentifier();
    }
    
    protected String getSignature( MOperationDef op ) {
		List params=op.getParameters();
		ArrayList defaults = new ArrayList(params.size());
		ArrayList tags = new ArrayList(params.size());
		ArrayList types = new ArrayList(params.size());
		ArrayList results = new ArrayList();
		ArrayList results_types = new ArrayList();

		if ((flags & FLAG_CODA_INFO) != 0) {
			
			initCoda( );
			Map info = null;
			Map method_info = coda.getMethodInfo(op.getDefinedIn().getIdentifier(),op.getIdentifier());
			if (method_info != null && method_info.containsKey("params"))
				info = (Map) method_info.get("params");
				
			boolean found_last_default = (info != null ? false : true);
			ListIterator it=params.listIterator(params.size());
				
			while (it.hasPrevious()) {
				MParameterDef p = (MParameterDef) it.previous();
						
				if (p.getDirection() == MParameterMode.PARAM_OUT) {
					results.add( 0, p.getIdentifier());
					results_types.add( 0, getType((MTyped) p,DOCSIG_TYPE));
				} else {
							
					if (p.getDirection() == MParameterMode.PARAM_INOUT) {
						results.add( 0, p.getIdentifier() );
						results_types.add(getType((MTyped) p,DOCSIG_TYPE));
					}
					
					if (info != null && (! info.containsKey(p.getIdentifier()) ||
							!((Map)info.get(p.getIdentifier())).containsKey("default")))
						found_last_default = true;
					
					if (! found_last_default) {
						
						Object ob = ((Map)info.get(p.getIdentifier())).get("default");
						String addition = getSignatureValue(p, ob);
						if (addition == null) {
							found_last_default = true;
							defaults.add(0,"");
						} else
							defaults.add(0,addition);
					}
					else {
						defaults.add(0,"");
					}
							
					//tags.add( 0, (found_last_default ? "" : "@") + p.getIdentifier() + " <" + getType((MTyped) p,false, DOCSIG_TYPE) + ">");
					tags.add( 0, p.getIdentifier() );
					types.add( 0, getType((MTyped) p,DOCSIG_TYPE));
				}
			}
		}

		ArrayList result = new ArrayList();
		Iterator tagit = tags.iterator();
		Iterator resit = results.iterator();
		Iterator defit = defaults.iterator();
		Iterator rtit = results_types.iterator();
		String str = "\\n";
		result.add("\\n");
		while (tagit.hasNext() && defit.hasNext()) {
			String df = (String) defit.next();
			result.add("      " + tagit.next() + (df.equals("") ? "" : "\t= ") + df + "\\n");
		}
		result.add("    ----------------------------------------\\n");
		while (resit.hasNext() && rtit.hasNext()) {
			result.add("      " + resit.next() + ":\t " + rtit.next() + "\\n");
		}
		
		return Text.join( "", result);
    }


	private String getSignatureValue(MTyped p, Object ob ) {
		MIDLType pidl = p.getIdlType();
		if (pidl instanceof MStructDef) {
			boolean no_default = false;
			ArrayList dflts = new ArrayList();
			MStructDef sd = (MStructDef)pidl;
			Map struct_defaults = (Map) ob;
			Iterator fields = sd.getMembers().iterator();
			while (fields.hasNext()) {
				MFieldDef field = (MFieldDef) fields.next();
				if (struct_defaults.containsKey(field.getIdentifier())) {
					String str = (String)struct_defaults.get(field.getIdentifier());
					dflts.add(field.getIdentifier() + "=" + str);
				}		
				else {
					no_default = true;
					break;
				}
			}
					
			if (no_default)
				return null;
			else
				return "{ " + Text.join(", ",dflts) + " }";
		} else if ((pidl instanceof MPrimitiveDef &&
				((MPrimitiveDef) pidl).getKind() != MPrimitiveKind.PK_ANY) ||
				pidl instanceof MStringDef) {
			return (String) ob;
		} else if (pidl instanceof MSequenceDef) {
			String str = null;
			if (ob instanceof List) {
				List param_list = (List) ob;
				if (param_list.size() > 0 && (param_list.get(0) instanceof String))
					str = "[ " + Text.join(", ", param_list) + " ]";
				else {
					MTyped element = (MTyped) pidl;
					MIDLType element_type = element.getIdlType();
					if ((element_type instanceof MStructDef) &&
							(param_list.get(0) instanceof Map)) {
						MStructDef stdef = (MStructDef) element_type;
						List str_list = new ArrayList();
						boolean missing_field = false;
						for (Iterator i=param_list.iterator(); i.hasNext(); ) {
							List ele_fields = new ArrayList();
							Map dflt = (Map) i.next();
							for (Iterator fields = stdef.getMembers().iterator(); fields.hasNext(); ) {
								MFieldDef field = (MFieldDef) fields.next();
								if ( ! dflt.containsKey(field.getIdentifier())) {
									//default not specified for a field
									missing_field = true;
									break;
								}
								ele_fields.add(field.getIdentifier() + "=" + dflt.get(field.getIdentifier()));
							}
							if (missing_field) break;
							str_list.add("{ " + Text.join(", ", ele_fields) + " }");
						}
						if (missing_field)
							str = "";
						else
							str = "[ " + Text.join(", ", str_list) + " ]";
					}
				}
			} else 
				str = "[ " + ob + " ]";
			return str;
		} else if (pidl instanceof MAliasDef ) {
			MTyped containedType = (MTyped) pidl;
			return getSignatureValue(containedType, ob);
		}
		
		return "";
	}
    
    /**************************************************************************/

    /**
     * Return the language type for the given object. This returns the value
     * given by getLanguageType, but it replaces non-word characters with
     * underscores and such.
     *
     * @param object the node object to use for type finding.
     */
    protected String getLanguageType(MTyped object)
    {
        String lang_type = super.getLanguageType(object);
        lang_type = lang_type.replaceAll("[*]", "_ptr");
        lang_type = lang_type.replaceAll("&", "_ref");
        lang_type = lang_type.replaceAll("const ", "const_");
        lang_type = lang_type.replaceAll("std::", "");
        lang_type = lang_type.replaceAll(sequence_type, "");
        return      lang_type.replaceAll("[ ><]", "");
    }

    protected String getScopedInclude(MContained node)
    {
    		return "#include <Python_Converter/" + node.getIdentifier() + "_python.h>";
    }

    /**************************************************************************/

    /**
     * Get C++ information about the parameters for the given operation. This is
     * essentially a way to circumvent the getLanguageType function to get
     * access to the parent class getLanguageType implementation.
     *
     * @param op the operation to investigate.
     * @return a comma separated string of the parameter information requested
     *         for this operation.
     */
    private String getOperationCppParams(MOperationDef op)
    {
        List ret = new ArrayList();
        for (Iterator ps = op.getParameters().iterator(); ps.hasNext(); ) {
            MParameterDef p = (MParameterDef) ps.next();
            ret.add(super.getLanguageType(p) + " " + p.getIdentifier());
        }
        return join(", ", ret);
    }

    /**
     * Get Python parameter conversion code for the given operation.
     *
     * @param op the operation to investigate.
     * @return a string containing code for converting the given operation's parameters
     *         to Python values and adding them to the pyArgs tuple.
     */
    private String getOperationConvertTo(MOperationDef op)
    {
        StringBuffer ret = new StringBuffer("");
        int pos = 0;
        for (Iterator params = op.getParameters().iterator(); params.hasNext(); ) {
            MParameterDef p = (MParameterDef) params.next();
            String lang_type = getLanguageType(p);
            String id = p.getIdentifier();

            ret.append("  PyObject *python_" + id + " = convert_" + lang_type);
            ret.append("_to_python ( " + id + " );\n");

            ret.append("  if ( PyTuple_SetItem ( args, " + pos++ + ", python_");
            ret.append(id + " ) ) return;\n");
        }
        return ret.toString();
    }
    
    /**
	 * Get a variable hash table sutable for filling in the template from the
	 * fillTwoStepTemplates function.
	 *
	 * @param operation the particular interface operation that we're filling in
	 *        a template for.
	 * @param container the container in which the given interface is defined.
	 * @return a map containing the keys and values needed to fill in the
	 *         template for this interface.
	 */
	protected Map getTwoStepOperationVariables(MOperationDef operation,
	                                           MContained container)
	{
	    String lang_type = super.getLanguageType(operation);
	    Map vars = new Hashtable();
	
	    vars.put("Object",                   container.getIdentifier());
	    vars.put("Identifier",               operation.getIdentifier());
	    vars.put("LanguageType",             lang_type);
	    vars.put("MExceptionDef",            getOperationExcepts(operation));
	    vars.put("MParameterDefAll",         getOperationCppParams(operation));
	    vars.put("MParameterDefName",        getOperationParamNames(operation, null));
	    vars.put("MParameterDefConvertTo",   getOperationConvertTo(operation));
	    vars.put("MParameterDefConvertFrom", getOperationConvertFrom(operation));
	
	    vars.put("NumParams", new Integer(operation.getParameters().size()));
	    
	    // this is somewhat messed up... we use fillTwoStepTemplates() to fill "supports" functions
	    // using the same template as the interface functions (?MOperationDefWrapper)... probably we
	    // need to revisit this...
	    vars.put("InterfaceType",			  container.getIdentifier());
	    vars.put("InvocationSetup",		  pythonOperationSetup(operation,"component"));
	    vars.put("Invocation",			  pythonOperationExecute(operation, "component"));
	    vars.put("InvocationTeardown",		  pythonOperationTeardown(operation, "component"));
	    vars.put("ParameterConvertInclude", pythonOperationParamInclude(operation));
	    vars.put("ResultConvertInclude",	  pythonOperationResultInclude(operation));
	    vars.put("ExceptionInclude",		  pythonOperationExceptionInclude(operation));
	    
	    // MComponentDef expansion
	    vars.put("ShortDescription",		  getDescription("short", operation));
	    vars.put("Signature",				  getSignature(operation));
	
	    if (! lang_type.equals("void")) {
	        vars.put("Return",
	                 "  result = convert_" + getLanguageType(operation) +
	                 "_from_python ( python_result );\n" +
	                 "  Py_DECREF ( python_result );\n" +
	                 "  return result;\n");
	        vars.put("ReturnVar", "  " + lang_type + " result;");
	    } else {
	        vars.put("Return", "  Py_DECREF ( python_result );\n");
	        vars.put("ReturnVar", "");
	    }
	
	    return vars;
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
     * Get Python parameter conversion code for the given operation.
     *
     * @param op the operation to investigate.
     * @return a string containing code for converting the given operation's parameters
     *         to Python values and adding them to the pyArgs tuple.
     */
    private String getOperationConvertFrom(MOperationDef op)
    {
        StringBuffer ret = new StringBuffer("");
        for (Iterator params = op.getParameters().iterator(); params.hasNext(); ) {
            MParameterDef p = (MParameterDef) params.next();
            ret.append("  Py_DECREF ( python_" + p.getIdentifier() + " );\n");
        }
        return ret.toString();
    }
}

