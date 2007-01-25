/**
 * CCM Tools : C++ Code Generator Library Leif Johnson <leif@ambient.2y.net>
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

package ccmtools.CppGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import ccmtools.Coda.CodaManager;
import ccmtools.Coda.XMLFileManager;
import ccmtools.CodeGenerator.CodeGenerator;
import ccmtools.CodeGenerator.Driver;
import ccmtools.CodeGenerator.Template;
import ccmtools.Metamodel.BaseIDL.MAliasDef;
import ccmtools.Metamodel.BaseIDL.MArrayDef;
import ccmtools.Metamodel.BaseIDL.MAttributeDef;
import ccmtools.Metamodel.BaseIDL.MContained;
import ccmtools.Metamodel.BaseIDL.MContainer;
import ccmtools.Metamodel.BaseIDL.MDefinitionKind;
import ccmtools.Metamodel.BaseIDL.MEnumDef;
import ccmtools.Metamodel.BaseIDL.MExceptionDef;
import ccmtools.Metamodel.BaseIDL.MFieldDef;
import ccmtools.Metamodel.BaseIDL.MFixedDef;
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
import ccmtools.Metamodel.BaseIDL.MTyped;
import ccmtools.Metamodel.BaseIDL.MTypedefDef;
import ccmtools.Metamodel.BaseIDL.MWstringDef;
import ccmtools.Metamodel.BaseIDL.MStructDef;
import ccmtools.Metamodel.ComponentIDL.MComponentDef;
import ccmtools.Metamodel.ComponentIDL.MFactoryDef;
import ccmtools.Metamodel.ComponentIDL.MFinderDef;
import ccmtools.Metamodel.ComponentIDL.MHomeDef;
import ccmtools.Metamodel.ComponentIDL.MProvidesDef;
import ccmtools.Metamodel.ComponentIDL.MSupportsDef;
import ccmtools.Metamodel.ComponentIDL.MUsesDef;

import ccmtools.utils.Text;

abstract public class CppGenerator extends CodeGenerator
{

	protected final static String long_type = "int";

    // reserved words in C++. identifiers that contain these words will be
    // mapped to new identifiers.

    private final static String[] _reserved = {
            "and", "and_eq", "asm", "auto", "bitand", "bitor", "bool", "break",
            "case", "catch", "char", "class", "compl", "const", "const_cast",
            "continue", "default", "delete", "do", "double", "dynamic_cast",
            "else", "enum", "explicit", "export", "extern", "false", "float",
            "for", "friend", "goto", "if", "inline", "int", "long", "mutable",
            "namespace", "new", "not", "not_eq", "operator", "or", "or_eq",
            "private", "protected", "public", "register", "reinterpret_cast",
            "return", "short", "signed", "sizeof", "static", "static_cast",
            "struct", "switch", "template", "this", "throw", "true", "try",
            "typedef", "typeid", "typename", "union", "unsigned", "using",
            "virtual", "void", "volatile", "wchar_t", "while", "xor", "xor_eq"
    };

    // c++ language types that get mapped from corba primitive kinds.

    private final static String[] _language = {
            "", "WX::Utils::Value",					// PK_ANY
            "bool",									// PK_BOOLEAN
            "char",									// PK_CHAR
            "double",									// PK_DOUBLE
            "(fixed data type not implemented",		// PK_FIXED
            "float",									// PK_FLOAT
            long_type,								// PK_LONG
            "double",									// PK_LONGDOUBLE
            "long", 									// PK_LONGLONG
            "NULL", 									// PK_NULL
            "LocalComponents::Object*", 				// PK_OBJREF
            "unsigned char", 							// PK_OCTET
            "(principal data type not implemented",	// PK_PRINCIPAL
            "short",									// PK_SHORT
            "std::string",							// PK_STRING
            "LocalComponents::TypeCode",				// PK_TYPECODE
            "unsigned long",							// PK_ULONG
            "unsigned long",							// PK_ULONGLONG
            "unsigned short",							// PK_USHORT
            "LocalComponents::Object*",				// PK_VALUEBASE
            "void",									// PK_VOID
            "wchar_t",								// PK_WCHAR
            "std::wstring"							// PK_WSTRING
    };

    protected final static String sequence_type = "std::vector";

    protected List base_namespace = null;
    
//  private enum DefaultTypes { implement, initialization, plain };
    protected static final int Default_impl = 1;
    protected static final int Default_init = 2;
    protected static final int Default_plain = 3;
    protected static final int Default_export = 4;
    protected static final int Default_error = 0;

    /** *********************************************************************** */

    public CppGenerator(String sublang, Driver d, File out_dir,
            String[] output_types) throws IOException
    {
        super(sublang, d, out_dir, output_types, _reserved, _language);
        base_namespace = new ArrayList();
    }

    /**
     * Acknowledge the start of the given node during graph traversal. If the
     * node is a MContainer type and is not defined in anything, assume it's the
     * global parse container, and push "CCM_Local" onto the namespace stack,
     * indicating that this code is for local CCM components.
     * 
     * @param node
     *            the node that the GraphTraverser object is about to
     *            investigate.
     * @param scope_id
     *            the full scope identifier of the node. This identifier is a
     *            string containing the names of parent nodes, joined together
     *            with double colons.
     */
    public void startNode(Object node, String scope_id)
    {
        if(node instanceof MContainer
                && (((MContainer) node).getDefinedIn() == null))
            for(Iterator i = base_namespace.iterator(); i.hasNext();)
                namespace.push(i.next());

        super.startNode(node, scope_id);
    }

    /**
     * Acknowledge and process a closing node during graph traversal. If the
     * node is an MContainer type, pop the namespace (this will remove our base
     * namespace that we pushed, in theory (tm)). If the node is of the correct
     * type and defined in the original parsed file, write code for this node.
     * 
     * @param node
     *            the node that the graph traverser object just finished
     *            investigating.
     * @param scope_id
     *            the full scope identifier of the node. This identifier is a
     *            string containing the names of ancestor nodes, joined together
     *            with double colons.
     */
    public void endNode(Object node, String scope_id)
    {
        super.endNode(node, scope_id);

        if(node instanceof MContainer
                && (((MContainer) node).getDefinedIn() == null)) {
            for(Iterator i = base_namespace.iterator(); i.hasNext();) {
                i.next();
                namespace.pop();
            }
        }

        writeOutputIfNeeded();
    }

    /** *********************************************************************** */

    /**
     * Join the bases of the current node using the given string as a separator.
     * The current node should be an instance of MInterfaceDef.
     * 
     * @param sep
     *            the separator to use between bases.
     * @return a string containing the names of base interfaces, separated by
     *         sep.
     */
    protected String joinBaseNames(String sep)
    {
        if(!(current_node instanceof MInterfaceDef))
            return "";
        MInterfaceDef node = (MInterfaceDef) current_node;
        ArrayList names = new ArrayList();
        for(Iterator i = node.getBases().iterator(); i.hasNext();)
            names.add("CCM_" + ((MInterfaceDef) i.next()).getIdentifier());
        return join(sep, names);
    }

    
    protected String getLocalName(MContained contained, String separator)
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
     * Build a string containing appropriately formatted namespace information
     * based on the given data type and local namespace component. This is aimed
     * at languages with C-like syntax (perl, C, C++, Java, IDL) and should be
     * overridden for others (Python, Prolog :-).
     * 
     * @param data_type
     *            a string referring to a desired type of namespace information.
     *            This is normally a variable name from a template.
     * @param local
     *            a string giving the name of the current namespace component.
     * @return a string containing the appropriately formatted namespace
     *         information.
     */
    protected String handleNamespace(String data_type, String local)
    {
    		List names = new ArrayList(namespace);
    		if(!local.equals(""))
    			names.add("CCM_Session_" + local);

    		if(data_type.equals("UsingNamespace")) {
    			List tmp = new ArrayList();
    			String prefix = "";
    			for(Iterator i = names.iterator(); i.hasNext();) {
    				String cur = (String) i.next();
    				tmp.add("using namespace " + prefix + cur + ";\n");
    				prefix = prefix + cur + "::";
    			} 
    			return join("", tmp);
    		}
    		else if(data_type.equals("OpenNamespace")) {
    			List tmp = new ArrayList();
    			for(Iterator i = names.iterator(); i.hasNext();)
    				tmp.add("namespace " + i.next() + " {\n");
    			return join("", tmp);
    		}
    		else if(data_type.equals("CloseNamespace")) {
    			Collections.reverse(names);
    			List tmp = new ArrayList();
    			for(Iterator i = names.iterator(); i.hasNext();)
    				tmp.add("} // /namespace " + i.next() + "\n");
    			return join("", tmp);
    		}
    		else if(data_type.equals("OpenLocalNamespace")) {
    			List tmp = new ArrayList();
    			for(Iterator i = names.iterator(); i.hasNext();)
    				tmp.add("namespace " + i.next() + " {\n");
    			return join("", tmp);
    		}
    		else if(data_type.equals("CloseLocalNamespace")) {
    			Collections.reverse(names);
    			List tmp = new ArrayList();
    			for(Iterator i = names.iterator(); i.hasNext();)
    				tmp.add("} // /namespace " + i.next() + "\n");
    			return join("", tmp);
    		}

    		return super.handleNamespace(data_type, local);
    }

	/**
     * Get the fully scoped identifier for the given node. If the current scope
     * contains some or all of the full scope for this node, then the identifier
     * will retain only those parts that are necessary to fully specify the
     * identifier in the current namespace.
     * 
     * @param node
     *            the node to use for retrieving the fully scoped identifier.
     * @return a string containing the full scope identifier of the node.
     */
    protected String getFullScopeIdentifier(MContained node)
    {
        String local = "CCM_Session_" + node.getIdentifier();
        List scope = getScope(node);

        if(node instanceof MComponentDef || node instanceof MHomeDef)
            scope.add(local);

        Collections.reverse(base_namespace);
        for(Iterator i = base_namespace.iterator(); i.hasNext();)
            scope.add(0, i.next());
        Collections.reverse(base_namespace);

        scope.add(node.getIdentifier());

        for(Iterator n = namespace.iterator(); n.hasNext();) {
            if(((String) n.next()).equals((String) scope.get(0)))
                scope.remove(0);
            else
                break;
        }

        if(node instanceof MComponentDef || node instanceof MHomeDef)
            if(((String) scope.get(0)).equals(local))
                scope.remove(0);

        return join(scope_separator, scope);
    }

    /**
     * Get a fully scoped filename for the given node.
     * 
     * @param node
     *            the node to use for retrieving the include filename info.
     * @return a string containing a fully scoped include file for the node.
     */
    protected String getFullScopeInclude(MContained node)
    {
        String local = "CCM_Session_" + node.getIdentifier();
        List scope = getScope(node);

        if(node instanceof MComponentDef || node instanceof MHomeDef)
            scope.add(local);

        Collections.reverse(base_namespace);
        for(Iterator i = base_namespace.iterator(); i.hasNext();)
            scope.add(0, i.next());
        Collections.reverse(base_namespace);

        scope.add(node.getIdentifier());

        return join(file_separator, scope);
    }

    /**
     * Create an #include statement sufficient for including the given node's
     * header file. This is normally only valid for C and C++ code generators
     * and should be overridden for other languages.
     * 
     * @param node
     *            the node to use for gathering include statement information.
     * @return a string containing an #include statement.
     */
    protected String getScopedInclude(MContained node)
    {
    		List scope = getScope(node);
    		Collections.reverse(base_namespace);
    		for(Iterator i = base_namespace.iterator(); i.hasNext();)
    			scope.add(0, i.next());
    		Collections.reverse(base_namespace);
    		scope.add(node.getIdentifier());
    		return "#include <" + join(file_separator, scope) + ".h>";
    }

	/**
	 * Get a variable hash table sutable for filling in the template from the
	 * fillTwoStepTemplates function. This version of the function handles
	 * operations from the given interface.
	 * 
	 * @param operation
	 *            the particular interface operation that we're filling in a
	 *            template for.
	 * @param container
	 *            the container in which the given interface is defined.
	 * @return a map containing the keys and values needed to fill in the
	 *         template for this interface.
	 */
	abstract protected Map getTwoStepOperationVariables(
	                                                    MOperationDef operation,
	                                                    MContained container);

    /**
     * Get a local value for the given variable name.
     * 
     * This function performs some common value parsing in the CCM MOF library.
     * More specific value parsing needs to be provided in the subclass for a
     * given language, in the subclass' getLocalValue function. Subclasses
     * should call this function first and then perform any subclass specific
     * value manipulation with the returned value.
     * 
     * @param variable
     *            The variable name to get a value for.
     * @return the value of the variable available from the current
     *         output_variables hash table. Could be an empty string.
     */
    protected String getLocalValue(String variable)
    {
        String value = super.getLocalValue(variable);

        if(current_node instanceof MHomeDef) {
            return data_MHomeDef(variable, value);
        }
        else if (current_node instanceof MStructDef) {
        		return data_MStructDef(variable, value);
        }
        else if(current_node instanceof MComponentDef) {
            return data_MComponentDef(variable, value);
        }
        else if(current_node instanceof MFactoryDef) {
            return data_MFactoryDef(variable, value);
        }
        else if(current_node instanceof MFinderDef) {
            return data_MFinderDef(variable, value);
        }
        else if(current_node instanceof MProvidesDef) {
            return data_MProvidesDef(variable, value);
        }
        else if(current_node instanceof MSupportsDef) {
            return data_MSupportsDef(variable, value);
        }
        else if(current_node instanceof MUsesDef) {
            return data_MUsesDef(variable, value);
        }
        else if(current_node instanceof MInterfaceDef) {
            return data_MInterfaceDef(variable, value);
        }
        else if(current_node instanceof MOperationDef) {
            return data_MOperationDef(variable, value);
        }
        else if(current_node instanceof MEnumDef) {
            return data_MEnumDef(variable, value);
        }
        else if(current_node instanceof MAliasDef) {
            return data_MAliasDef(variable, value);
        }

        return value;
    }

    /**
     * Return the C++ language type for the given object. This returns the value
     * given by getLanguageType if the node is neither an MParameterDef or
     * MOperationDef instance, otherwise it basically adds the C++ specific
     * parameters needed to correctly interpret the parameter or operation
     * direction (in, out, inout).
     * 
     * @param object
     *            the node object to use for type finding.
     */
    protected String getLanguageType(MTyped object)
    {
        MIDLType idl_type = object.getIdlType();
        String base_type = getBaseLanguageType(object);

        // Handle interfaces using smart pointers
        // (Any ia handled as interface
        if(idl_type instanceof MInterfaceDef
                || (idl_type instanceof MPrimitiveDef && ((MPrimitiveDef) idl_type)
                        .getKind() == MPrimitiveKind.PK_ANY)) {
        	
        		if (idl_type instanceof MInterfaceDef && (flags & FLAG_CODA_INFO) != 0) {	
        			initCoda( );
        			//Map method_info = coda.getTie("interface",op.getDefinedIn().getIdentifier(),op.getIdentifier());
        			//if (method_info != null && method_info.containsKey("params")) {
        			if (current_node instanceof MSupportsDef) {
        				String component = ((MSupportsDef)current_node).getComponent().getIdentifier();
        				String override = coda.getTie("interface",component,((MInterfaceDef)idl_type).getIdentifier());
        				if (override == null || override.length() == 0)
        					throw new RuntimeException("using Coda (for component \"" + ((MSupportsDef)current_node).getIdentifier() + 
        								"\") but found no interface override for \"" + component + "\"");
//        			base_type = base_type.replaceAll(((MInterfaceDef)idl_type).getIdentifier(),override);
       				base_type = "CCM_Session_" + override + "::" + override;
        			}
        			else {
        				String override = coda.getTie("mapping",((MInterfaceDef)idl_type).getIdentifier());
        				base_type = "CCM_Session_" + override + "::" + override;
        			}
        		}
        		base_type = "WX::Utils::SmartPtr<" + base_type + ">";
        }

        // This code defines the parameter passing rules for operations:
        //   in : simple types are passed as const values
        //           complex types are passed by const ref
        //   inout : always passed by ref
        //   out : always passed by ref
        if(object instanceof MParameterDef) {
            MParameterDef param = (MParameterDef) object;
            MParameterMode direction = param.getDirection();
            String prefix = "";
            String suffix = "";
            if(direction == MParameterMode.PARAM_IN) { // in
                prefix = "const ";
                if((idl_type instanceof MTypedefDef)
                        || (idl_type instanceof MPrimitiveDef && ((MPrimitiveDef) idl_type)
                                .getKind() == MPrimitiveKind.PK_ANY)
                        || (idl_type instanceof MStringDef)
                        || (idl_type instanceof MWstringDef)
                        || (idl_type instanceof MFixedDef)
                        || (idl_type instanceof MInterfaceDef)) {
                    suffix = "&";
                }
            }
            else { // inout, out
                prefix = "";
                suffix = "&";
            }
            return prefix + base_type + suffix;
        }

        if((object instanceof MAliasDef) && (idl_type instanceof MTyped))
            return getLanguageType((MTyped) idl_type);

        // FIXME : can we implement bounded sequences in C++ ?
        if(object instanceof MSequenceDef)
            return sequence_type + "<" + (base_type.startsWith(":") ? " " + base_type : base_type) + "> ";
        
        if(object instanceof MArrayDef) {
            /*
             * This code defines the IDL -> C++ mapping of arrays: long x[7] ->
             * std::vector <long> ... but no bounds checking.
             */

            String result = "std::vector<" + base_type + ">";
            int dimension = ((MArrayDef) object).getBounds().size();

            if(dimension > 1) {
                result = "std::vector<";
                for(int i = 1; i < dimension; i++)
                    result += "std::vector<";
                result += base_type + ">";
                for(int i = 1; i < dimension; i++)
                    result += " >";
            }

            return result + " ";
        }

        return base_type;
    }

    protected String getLanguageTypeStd(MTyped object)
    {
        MIDLType idl_type = object.getIdlType();
        String base_type;

        if(idl_type instanceof MTypedefDef) {
        		MTypedefDef mt = (MTypedefDef) idl_type;
        		if (mt instanceof MAliasDef) {
        			MAliasDef ct = (MAliasDef) mt;
        			MIDLType ctidl = ct.getIdlType();
        			if (ctidl instanceof MPrimitiveDef || ctidl instanceof MStringDef
        					|| ctidl instanceof MWstringDef)
        				base_type = getBaseLanguageType(ctidl);
        			else if (ctidl instanceof MSequenceDef) {
        				String element_type = getBaseLanguageType((MTyped) ctidl);
        				base_type = sequence_type + "<" + (element_type.startsWith(":") ? " " + element_type : element_type) + ">";
        			} else
        				throw new RuntimeException("unhandled typedef: " + mt.getIdentifier());
        		} else if (mt instanceof MStructDef){
        			// which namespace should this be in? We want to totally isolate
        			// the std (*_cmpt.h/cc) interface from ccmtools & corba, so for
        			// now we assume that for the std interface this will be within
        			// the current namespace... getBaseLanguageType() returns module::id
        			base_type = mt.getIdentifier();
        		} else {
        			base_type = getBaseLanguageType(object);
        		}
        }
        else
        		base_type = getBaseLanguageType(object);
        
        // Handle interfaces using smart pointers
        // (Any ia handled as interface
        if(idl_type instanceof MInterfaceDef
                || (idl_type instanceof MPrimitiveDef && ((MPrimitiveDef) idl_type)
                        .getKind() == MPrimitiveKind.PK_ANY)) {
        		
        		String override = null;
        		if (idl_type instanceof MInterfaceDef && (flags & FLAG_CODA_INFO) != 0) {	
        			initCoda( );
        			//Map method_info = coda.getTie("interface",op.getDefinedIn().getIdentifier(),op.getIdentifier());
        			//if (method_info != null && method_info.containsKey("params")) {
        			if (current_node instanceof MSupportsDef) {
        				String component = ((MSupportsDef)current_node).getComponent().getIdentifier();
        				override = coda.getTie("interface",component,((MInterfaceDef)idl_type).getIdentifier());
        				if (override == null || override.length() == 0)
        					throw new RuntimeException("using Coda but found no interface override");
        				override = "::" + namespace.lastElement() + "::" + override + " *";
        			}
        		}
        		base_type = (override != null && override.length() > 0) ? override : "WX::Utils::SmartPtr<" + base_type + ">";
        			
        }

        // This code defines the parameter passing rules for operations:
        //   in : simple types are passed as const values
        //           complex types are passed by const ref
        //   inout : always passed by ref
        //   out : always passed by ref
        if(object instanceof MParameterDef) {
            MParameterDef param = (MParameterDef) object;
            MParameterMode direction = param.getDirection();
            String prefix = "";
            String suffix = "";
            if(direction == MParameterMode.PARAM_IN) { // in
                prefix = "const ";
                		
                if((idl_type instanceof MTypedefDef)
                        || (idl_type instanceof MPrimitiveDef && ((MPrimitiveDef) idl_type)
                                .getKind() == MPrimitiveKind.PK_ANY)
                        || (idl_type instanceof MStringDef)
                        || (idl_type instanceof MWstringDef)
                        || (idl_type instanceof MFixedDef)
                        || (idl_type instanceof MInterfaceDef)) {
                    suffix = "&";
                }
            }
            else { // inout, out
                prefix = "";
                suffix = "&";
            }
            return prefix + base_type + suffix;
        }

        if((object instanceof MAliasDef) && (idl_type instanceof MTyped)) {
        		String alias = getLanguageType((MTyped) idl_type);
        		return alias;
        }   

        // FIXME : can we implement bounded sequences in C++ ?
        if(object instanceof MSequenceDef)
            return sequence_type + "<" + base_type + "> ";

        if(object instanceof MArrayDef) {
            /*
             * This code defines the IDL -> C++ mapping of arrays: long x[7] ->
             * std::vector <long> ... but no bounds checking.
             */

            String result = "std::vector<" + base_type + ">";
            int dimension = ((MArrayDef) object).getBounds().size();

            if(dimension > 1) {
                result = "std::vector<";
                for(int i = 1; i < dimension; i++)
                    result += "std::vector<";
                result += base_type + ">";
                for(int i = 1; i < dimension; i++)
                    result += " >";
            }

            return result + " ";
        }

        return base_type;
    }

    /** *********************************************************************** */

    protected String data_MAliasDef(String data_type, String data_value)
    {
        MIDLType idl_type = ((MAliasDef) current_node).getIdlType();

        if( data_type.equals("FirstBound") &&
        		current_node instanceof MArrayDef )
        			return "" + ((MArrayDef) idl_type).getBounds().get(0);
        else if(data_type.equals("AllBounds") &&
        			current_node instanceof MArrayDef) {
        		MArrayDef array = (MArrayDef) idl_type;
        		String result = "";
        		for(Iterator i = array.getBounds().iterator(); i.hasNext();)
        			result += "[" + (Long) i.next() + "]";
        		return result;
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

        if(data_type.endsWith("Namespace")) {
            return handleNamespace(data_type, ((MComponentDef) current_node)
                    .getIdentifier());
        }
        else if(data_type.equals("HomeInclude")) {
            String include = getFullScopeInclude(component);
            include = include.substring(0, include.lastIndexOf(file_separator));
            return include + file_separator + home.getIdentifier();
        }
        else if(data_type.endsWith("AbsoluteLocalHomeName")) {
            return getLocalName(home,"_");
        }
        
        else if(data_type.equals("Module")) {
        		return (String) namespace.lastElement();
        }
        
        return data_MInterfaceDef(data_type, data_value);
    }

    protected String data_MEnumDef(String data_type, String data_value)
    {
        if(data_type.equals("Members")) {
            List b = new ArrayList();
            MEnumDef enm = (MEnumDef) current_node;
            for(Iterator i = enm.getMembers().iterator(); i.hasNext();)
                b.add((String) i.next());
            return join(", ", b);
        }
        return data_value;
    }

    protected String data_MFactoryDef(String data_type, String data_value)
    {
        return data_MOperationDef(data_type, data_value);
    }

    protected String data_MFinderDef(String data_type, String data_value)
    {
        return data_MOperationDef(data_type, data_value);
    }

    protected String data_MHomeDef(String data_type, String data_value)
    {
        MHomeDef home = (MHomeDef) current_node;
        MComponentDef component = home.getComponent();

        String home_id = home.getIdentifier();
        String component_id = component.getIdentifier();

        if(data_type.endsWith("Namespace")) {
            return handleNamespace(data_type, component_id);
        }
        else if(data_type.equals("HomeInclude")) {
            String include = getFullScopeInclude(component);
            include = include.substring(0, include.lastIndexOf(file_separator));
            return include + file_separator + home_id;
        }
        else if(data_type.endsWith("AbsoluteLocalHomeName")) {
            return getLocalName(home,"_");
        }
        return data_MInterfaceDef(data_type, data_value);
    }

    protected String data_MInterfaceDef(String data_type, String data_value)
    {
        MInterfaceDef iface = (MInterfaceDef) current_node;

        if(data_type.equals("BaseType")) {
            String base = joinBaseNames(", virtual public ");
            if(base.length() > 0)
                return ", virtual public " + base;
        }
        
        else if (data_type.equals("Module")) {
        		return (String) namespace.lastElement();
        }
        
        return data_value;
    }

    protected String data_MOperationDef(String data_type, String data_value)
    {
        if(data_type.equals("MExceptionDefThrows") && data_value.endsWith(", ")) {
            return "throw ( LocalComponents::CCMException, "
                    + data_value.substring(0, data_value.length() - 2) + " )";
        }
        else if(data_type.startsWith("MParameterDef")
                && data_value.endsWith(", ")) {
            return data_value.substring(0, data_value.length() - 2);
        }
        else if(data_type.equals("UsesIdentifier")) {
            MOperationDef op = (MOperationDef) current_node;
            if(op.getDefinedIn() instanceof MUsesDef)
                return op.getDefinedIn().getIdentifier();
        }
        else if(data_type.equals("ProvidesIdentifier")) {
            MOperationDef op = (MOperationDef) current_node;
            if(op.getDefinedIn() instanceof MProvidesDef)
                return op.getDefinedIn().getIdentifier();
        }
        else if(data_type.startsWith("MOperationDefRetFwdDecl")) {
        		MOperationDef op = (MOperationDef) current_node;
        		MIDLType idl_type = ((MTyped)op).getIdlType();
    			if ((flags & FLAG_CODA_INFO) != 0) {
    				if (idl_type instanceof MInterfaceDef) {
    					String override = coda.getTie("mapping",((MInterfaceDef)idl_type).getIdentifier());
    					if (override != null && override.length() > 0) {
    						Template t = template_manager.getTemplate(data_type, current_name);
    						if (t == null)
    							throw new RuntimeException("couldn't find template for: " + data_type);
    						Map varmap = new Hashtable();
    						varmap.put(getScopeID("ResultType"), override);
    						return t.substituteVariables(varmap);
    					}
    				}
    			}
    			return "";
        }

        return data_value;
    }
    
    protected String data_MStructDef(String data_type, String data_value)
    {
        return data_value;
    }

    protected String data_MProvidesDef(String data_type, String data_value)
    {
        MInterfaceDef iface = ((MProvidesDef) current_node).getProvides();

        if(data_type.equals("CCMProvidesType")) {
            if(data_value.indexOf(scope_separator) < 0)
                return "CCM_" + data_value;
            int i = data_value.lastIndexOf(scope_separator)
                    + scope_separator.length();
            return data_value.substring(0, i) + "CCM_"
                    + data_value.substring(i);
        }
        else if(data_type.startsWith("MOperation")) {
            return fillTwoStepTemplates(iface, data_type, false);
        }
        else if(data_type.startsWith("MAttribute")) {
            return fillTwoStepTemplates(iface, data_type, true);
        }
        else if(data_type.startsWith("FullComponentType")) {
            // Return full scoped component type
            MComponentDef component = ((MProvidesDef) current_node)
                    .getComponent();
            List scope = getScope((MContained) component);
            if(scope.size() > 0)
                return "CCM_Local::" + join("::", scope) + "::CCM_Session_"
                        + component.getIdentifier() + "::CCM_"
                        + component.getIdentifier();
            else
                return "CCM_Local::CCM_Session_" + component.getIdentifier()
                        + "::CCM_" + component.getIdentifier();
        }
        else if(data_type.startsWith("OpenNamespace")
                || (data_type.startsWith("CloseNamespace"))) {
            // Add component Namespace to facet impl class files
            MComponentDef component = ((MProvidesDef) current_node)
                    .getComponent();
            return handleNamespace(data_type, component.getIdentifier());
        }
        return data_value;
    }

    protected String data_MSupportsDef(String data_type, String data_value)
    {
        MInterfaceDef iface = ((MSupportsDef) current_node).getSupports();

        if(data_type.equals("CCMSupportsType")) {
            if(data_value.indexOf(scope_separator) < 0)
                return "CCM_" + data_value;
            int i = data_value.lastIndexOf(scope_separator)
                    + scope_separator.length();
            return data_value.substring(0, i) + "CCM_"
                    + data_value.substring(i);
        }
        else if(data_type.startsWith("MOperation")) {
            return fillTwoStepTemplates(iface, data_type, false);
        }
        else if(data_type.startsWith("MAttribute")) {
            return fillTwoStepTemplates(iface, data_type, true);
        }
        
        return data_value;
    }

    protected String data_MUsesDef(String data_type, String data_value)
    {
        MInterfaceDef iface = ((MUsesDef) current_node).getUses();

        if(data_type.equals("CCMUsesType")) {
            if(data_value.indexOf(scope_separator) < 0)
                return "CCM_" + data_value;
            int i = data_value.lastIndexOf(scope_separator)
                    + scope_separator.length();
            return data_value.substring(0, i) + "CCM_"
                    + data_value.substring(i);
        }
        else if(data_type.startsWith("MOperation")) {
            return fillTwoStepTemplates(iface, data_type, false);
        }
        else if(data_type.startsWith("MAttribute")) {
            return fillTwoStepTemplates(iface, data_type, true);
        }

        return data_value;
    }

    /** *********************************************************************** */

    /**
     * Get type and name information about the parameters for the given
     * operation. This will return a comma-separated string, i.e. <type1>
     * <name1>, <type2><name2>, ... ,<typeN><nameN>for this operation's
     * parameters.
     * 
     * @param op
     *            the operation to investigate.
     * @return a comma separated string of the parameter information requested
     *         for this operation.
     */
    protected String getOperationParams(MOperationDef op)
    {
        List ret = new ArrayList();
        for(Iterator ps = op.getParameters().iterator(); ps.hasNext();) {
            MParameterDef p = (MParameterDef) ps.next();
            ret.add(getLanguageType(p) + " " + p.getIdentifier());
        }
        return join(", ", ret);
    }

    public void initCoda( ) {
    		super.initCoda( );
    		String source = null;
    		
    		if (current_node instanceof MContained)
    			source = ((MContained)current_node).getDefinedIn().getSourceFile();
    		else if (current_node instanceof MContainer)
    			source = ((MContainer)current_node).getSourceFile();
    		
    		if (source != null) {
    			File source_file = new File(source);
    			if (source_file.isFile()) {
    				File source_path = source_file.getParentFile();
    				if (source_path != null && source_path.isDirectory())
    					coda.setInputSource(source_path.getPath());
    			}
    		}
    }
    
    protected String findDefault( String id, MTyped p, Map info, int style, String prefix ) {
    		MIDLType pidl = p.getIdlType();
    		boolean no_default = false;
		Object ob = ((Map)info.get(id)).get("default");
		if (pidl instanceof MStructDef) {
			List args = new ArrayList();
			MStructDef sd = (MStructDef)pidl;
			if (! (ob instanceof Map))
				throw new RuntimeException("specified default for '" + id + "' is not a struct" );
			Map defaults = (Map) ob;
			Iterator fields = sd.getMembers().iterator();
			while (fields.hasNext()) {
				MFieldDef field = (MFieldDef) fields.next();
				if (defaults.containsKey(field.getIdentifier())) {
					String str = (String)defaults.get(field.getIdentifier());
					args.add(defaultify(null,field.getIdlType(),str, field.getIdentifier(), (style == Default_export ? Default_export : Default_plain), 1));
				}		
				else {
					no_default = true;
					break;
				}
			}
			if (no_default)
				return "";
			else {
				String assign = (style == Default_init ? " = " + sd.getIdentifier() : "");
				return (style == Default_export ? "<value type=\"struct\" idl=\"" + sd.getIdentifier() + "\" param=\"" + id + "\">" + Text.join("",args) + "</value>" : assign + "(" + Text.join(",",args) + ")");
			}
		}
		else if ((pidl instanceof MPrimitiveDef &&
				 ((MPrimitiveDef) pidl).getKind() != MPrimitiveKind.PK_ANY) ||
				 pidl instanceof MStringDef){
			String str = (String) ob;
			return defaultify(id, p.getIdlType(), str, null, style, 1);
		} else if ( pidl instanceof MPrimitiveDef &&
					((MPrimitiveDef) pidl).getKind() == MPrimitiveKind.PK_ANY ) {
			String str = (String) ob;
			return defaultify(id, p.getIdlType(), str, null, style, 1);
		} else if (pidl instanceof MSequenceDef) {

			String quote = "";
			MTyped element = (MTyped) pidl;
			MIDLType element_type = element.getIdlType();
			String basetype = getBaseLanguageType(element_type);
			if ((element_type instanceof MStringDef) ||
					(element_type instanceof MWstringDef))
    				quote = "\"";

			int length = 1;
			String str = null;
			if (ob instanceof List) {
				List arg_list = (List) ob;
				length = arg_list.size();
				if (length > 0) {
					if (arg_list.get(0) instanceof String) {
						if (style == Default_export) {
							str = Text.join("</value><value type=\"" + basetype + "\">",arg_list);
							if (arg_list.size() > 0)
								str = "<value type=\"" + basetype + "\">" + str + "</value>";
						} else
							str = Text.join( quote + ", (" + basetype + ") " + quote, arg_list );
					} else if ((element_type instanceof MStructDef) &&
								(arg_list.get(0) instanceof Map)) {
						MStructDef stdef = (MStructDef) element_type;
						List str_list = new ArrayList();
						String ctor = basetype;
						String ctor_start = "::" + ctor + "( ";
						String ctor_end = " )";
						String ctor_join = ", ";
						if ( style == Default_export ) {
							ctor_start = "<value type=\"struct\" idl=\"" + stdef.getIdentifier() + "\">";
							ctor_end = "</value>";
							ctor_join = "";
						}
						if (prefix != null && prefix.length() > 0) {
							ctor = prefix + stdef.getIdentifier();
						}
						for (Iterator i=arg_list.iterator(); i.hasNext(); ) {
							List dflt_args = new ArrayList();
							Map dflt = (Map) i.next();
							for (Iterator fields = stdef.getMembers().iterator(); fields.hasNext(); ) {
								MFieldDef field = (MFieldDef) fields.next();
								if ( ! dflt.containsKey(field.getIdentifier())) {
									//default not specified for a field
									return "";
								}
								dflt_args.add(style == Default_export ? "<value name=\"" + field.getIdentifier() + "\">" + dflt.get(field.getIdentifier()) + "</value>" 
													: dflt.get(field.getIdentifier()));
							}
							str_list.add(ctor_start + Text.join(ctor_join,dflt_args) + ctor_end);
						}
						str = Text.join(ctor_join,str_list);
					}
				}
			} else
				str = (String) ob;
			
			String ret = defaultify(id, p.getIdlType(), str, null, style, length);
			return ret;
		} else if (pidl instanceof MAliasDef ) {
    			MTyped containedType = (MTyped) pidl;
    			return findDefault(id, containedType, info, style, prefix);
		} else {
			return "";
		}

    }
    protected List findDefaults(MOperationDef op, int style, String target, String prefix) {
    		List params=op.getParameters();
    		ArrayList result = new ArrayList(params.size());
   
    		if ((flags & FLAG_CODA_INFO) != 0) {
    			
    			initCoda( );
    			Map method_info = coda.getMethodInfo(op.getDefinedIn().getIdentifier(),op.getIdentifier());
    			if (method_info != null && method_info.containsKey("params")) {
    				Map info = (Map) method_info.get("params");
    				if (info != null) {
    					boolean found_last_default = false;
    					ListIterator it=params.listIterator(params.size());
    					while (it.hasPrevious()) {
    						MParameterDef p = (MParameterDef) it.previous();
    						if (!info.containsKey(p.getIdentifier()) ||
    								!((Map)info.get(p.getIdentifier())).containsKey("default"))
    							found_last_default = true;
    					
    						if (found_last_default) {
    							result.add(0,"");
    							if ( current_node instanceof MSupportsDef &&
										coda.doExportDefaults() ) {
    								String opstr = op.getIdentifier();
								coda.exportDefault(((MSupportsDef)current_node).getComponent().getIdentifier(), opstr, p.getIdentifier(), 
										getUnsetDefault(((MSupportsDef)current_node).getSupports().getIdentifier(), opstr, p.getIdentifier(), p.getIdlType(), null, p.getIdentifier()));
							}
    						} else	 {
    							String dflt = findDefault(p.getIdentifier(),p,info,style, prefix);
    							if (dflt.equals(""))
    								found_last_default = true;
    							else {
    								if ( current_node instanceof MSupportsDef &&
    										coda.doExportDefaults() ) {
    									String xmldflt = findDefault(p.getIdentifier(),p,info,Default_export,prefix);
    									coda.exportDefault( ((MSupportsDef)current_node).getComponent().getIdentifier(),
    														op.getIdentifier(), p.getIdentifier(), xmldflt );
    								}
    								MIDLType pidl = p.getIdlType();
    								if ( pidl instanceof MPrimitiveDef &&
    										((MPrimitiveDef) pidl).getKind() == MPrimitiveKind.PK_ANY ) {
    									String param_type = coda.getAnyType(op.getDefinedIn().getIdentifier(),
    																		op.getIdentifier(),p.getIdentifier());
    									Map cinfo = coda.getConversionInfo(param_type,target);
    									if ( cinfo.containsKey("init") ) {
    										List what = (List) cinfo.get("init");
    										Map finfo = (Map) what.get(0);;
    										if ( finfo.containsKey("func")) {
    											dflt = (style == Default_init || style == Default_impl ? " = " : "") + (String) finfo.get("func") + "(" + dflt + ")";
   										}
    										else {
    											dflt = "";
   										}
    									} else {
    										dflt = "";
    									}
    								}
    							}
    							result.add(0,dflt);
    						}
    					}
    				}
    			}
		}

    		if (result.size() == 0) {
    			for (int i=0; i < params.size(); ++i)
    				result.add("");
    		}
    		
		return result;
    }
    
    private String getUnsetDefault(String component, String op, String p_id, MIDLType p_idl_type, String name, String param) {
		MIDLType idl_type = p_idl_type;
		if ((idl_type instanceof MStringDef) || (idl_type instanceof MWstringDef)) {
			return "<value" + (name != null && name.length() > 0 ? " name=\"" + name + "\"" : "") +
				(param != null && param.length() > 0 ? " param=\"" + param + "\"" : "") +
				" type=\"string\" unset=\"true\" />";
		} else if (idl_type instanceof MSequenceDef) {
			MTyped element = (MTyped) idl_type;
    			MIDLType element_type = element.getIdlType();
    			String elt = null;
    			if ((element_type instanceof MStringDef) ||
    				(element_type instanceof MWstringDef)) {
    				elt = "string";
    			} else {
    				elt = getBaseLanguageType(element_type);
    			}

			return "<value" + (name != null && name.length() > 0 ? " name=\"" + name + "\"" : "") +
				(param != null && param.length() > 0 ? " param=\"" + param + "\"" : "") +
				" type=\"vector\" unset=\"true\"><value unset=\"true\" type=\"" + elt + "\" /></value>";
		} else if (idl_type instanceof MStructDef) {
			MStructDef struct = (MStructDef) idl_type;
			String ret = "";
			for (Iterator i = struct.getMembers().iterator(); i.hasNext(); ) {
				MFieldDef ele = (MFieldDef) i.next();
				ret += getUnsetDefault(component, op, ele.getIdentifier(), ele.getIdlType(), ele.getIdentifier(), null);
			}
			return "<value" + (name != null && name.length() > 0 ? " name=\"" + name + "\"" : "") + 
				(param != null && param.length() > 0 ? " param=\"" + param + "\"" : "") +
				" idl=\"" + struct.getIdentifier() + "\"" +
				" type=\"struct\" unset=\"true\">" + ret + "</value>";
		} else if (idl_type instanceof MInterfaceDef){
			String override = "";
			if ((flags & FLAG_CODA_INFO) != 0) {
				override = coda.getTie("mapping",((MInterfaceDef)idl_type).getIdentifier());
				if (override == null) {
					override = "";
				}
			}
			return "<value" + (name != null && name.length() > 0 ? " name=\"" + name + "\"" : "") +
				(param != null && param.length() > 0 ? " param=\"" + param + "\"" : "") +
				" type=\"component:" + override + "\" unset=\"true\" />";
		} else if ((idl_type instanceof MPrimitiveDef) &&
				((MPrimitiveDef)idl_type).getKind() == MPrimitiveKind.PK_ANY) {
			String anytype = "";
			if ((flags & FLAG_CODA_INFO) != 0) {
				anytype = coda.getAnyType(component, op, p_id);
			}
			return "<value" + (name != null && name.length() > 0 ? " name=\"" + name + "\"" : "") +
					(param != null && param.length() > 0 ? " param=\"" + param + "\"" : "") +
					" type=\"any:" + anytype + "\" unset=\"true\" />";
		} else if (idl_type instanceof MAliasDef) {
			MTyped containedType = (MTyped) idl_type;
			return getUnsetDefault(component, op, p_id, containedType.getIdlType(), name, param);
		} else {
			return "<value" + (name != null && name.length() > 0 ? " name=\"" + name + "\"" : "") +
				(param != null && param.length() > 0 ? " param=\"" + param + "\"" : "") +
				" type=\"" + getBaseLanguageType(p_idl_type) + "\" unset=\"true\" />";
		}
	}

	private String defaultify(String id, MIDLType idl_type, String default_string, String name, int style, int num_defaults) {
        if((idl_type instanceof MStringDef)
        		|| (idl_type instanceof MWstringDef))
        		return (style == Default_impl ? "(\"" + default_string + "\")"
        				: style == Default_init ? " = \"" + default_string + "\""
        						: style == Default_plain ? "\"" + default_string + "\""
        							: style == Default_export ? "<value" +
        										(name != null && name.length() > 0 ? " name=\"" + name + "\"" : "") +
        										(id != null && id.length() > 0 ? " param=\"" + id + "\"" : "") +
        										" type=\"string\"" + ">" + default_string + "</value>"
        									: default_string );
        else if (idl_type instanceof MSequenceDef) {
        		String quote = "";
        		MTyped element = (MTyped) idl_type;
        		MIDLType element_type = element.getIdlType();
        		if ((element_type instanceof MStringDef) ||
        				(element_type instanceof MWstringDef)) {
        			quote = "\"";
        		}
        		if (num_defaults <= 1) {
        			MSequenceDef seq = (MSequenceDef) idl_type;
        			String seq_str = getLanguageTypeStd(seq);
        			if ( style == Default_export ) {
        				String vartagstart = "";
        				String vartagend = "";
        				if ( default_string != null && ! default_string.startsWith("<value") ) {
        					vartagstart = "<value type=\"" + getBaseLanguageType(element_type) + "\">";
        					vartagend = "</value>";
        				}
        				return "<value type=\"vector\"" + 
        							(name != null && name.length() > 0 ? " name=\"" + name + "\"" : "") + 
        							(id != null && id.length() > 0 ? " param=\"" + id + "\"" : "") + ">" +
        							vartagstart + default_string + vartagend + "</value>";
        			} else {
        				String arg = null;
        				int len = 1;
        				String comma = ", ";
        				if ( default_string == null ) {
        					len = 0;
        					arg = "";
        					comma = "";
        				} else {
        					arg = quote + default_string + quote;
        				}
        				return (style == Default_impl ? "(" + len + comma + arg + ")"
        						: style == Default_init ? " = " + seq_str + "(" + len + comma + arg + ")"
        								: style == Default_plain ? seq_str + "(" + len + comma + arg + ")"
        										: arg );
        			}
        		} else {
        			String module = (String) namespace.lastElement();
        			String cast = "";
        			if ((element_type instanceof MPrimitiveDef) ||
        					(element_type instanceof MStringDef) ||
        					(element_type instanceof MWstringDef)) {
        				String basetype = getBaseLanguageType(element_type);
        				cast = "(" + basetype + ") ";
        			}
        			if (style == Default_export)
        				return "<value type=\"vector\""+ (id != null && id.length() > 0 ? " param=\"" + id + "\"" : "") + ">" + default_string + "</value>";
        			else
        				return " = ::" + module + "::initialize_vector( " + num_defaults + ", " + cast + quote + default_string + quote + " )";
        		}
        }
        else if (idl_type instanceof MAliasDef ) {
        		MTyped containedType = (MTyped) idl_type;
        		return defaultify(id,containedType.getIdlType(), default_string, name, style, num_defaults);
        } else if (idl_type instanceof MPrimitiveDef &&
        				((MPrimitiveDef)idl_type).getKind() == MPrimitiveKind.PK_ANY) {
        		return "\"" + default_string.replaceAll("\"","\\\\\"") + "\"";
        } else
        		return style == Default_plain ? default_string 
        				: style == Default_export ? "<value" + (name != null && name.length() > 0 ? " name=\"" + name + "\"" : "") + 
        							(id != null && id.length() > 0 ? " param=\"" + id + "\"" : "") + 
        							" type=\"" + getBaseLanguageType(idl_type) + "\"" + ">" + default_string + "</value>"
        						: " = " + default_string;
	}

    private Set getOpIncludes(MOperationDef op, String coda_include_use, String target) {
		Map ret = new HashMap();
		for (Iterator params=op.getParameters().iterator(); params.hasNext(); ) {
			MParameterDef p = (MParameterDef) params.next();
			MIDLType idl_type = ((MTyped) p).getIdlType();

			if(idl_type instanceof MStructDef) {
				if (coda_include_use == null)
					ret.put("#include <impl" + file_separator +
							(String) namespace.lastElement() +
							file_separator + ((MStructDef)idl_type).getIdentifier() + ".h>","");
				else	 if ((flags & FLAG_CODA_INFO) != 0)
					ret.putAll(coda.getInclude(((MStructDef)idl_type).getIdentifier(),target,coda_include_use));
			}
			else if(idl_type instanceof MSequenceDef) {
				ret.put("#include <vector>","");
				String module = (String) namespace.lastElement();
				ret.put("#include <impl" + file_separator + module + file_separator + module + ".h>","");
			}
			else if(idl_type instanceof MAliasDef) {
				MTyped containedType = (MTyped) idl_type;
				MIDLType containedIdlType = containedType.getIdlType();
				if(containedIdlType instanceof MStructDef) {
					if (coda_include_use == null)
						ret.put("#include <impl" + file_separator +
								(String) namespace.lastElement() +
								file_separator + ((MStructDef)idl_type).getIdentifier() + ".h>","");
					else if ((flags & FLAG_CODA_INFO) != 0)
						ret.putAll(coda.getInclude(((MStructDef)idl_type).getIdentifier(),target,coda_include_use));
				}
    				else if(containedIdlType instanceof MSequenceDef) {
    					ret.put("#include <vector>","");
    					String module = (String) namespace.lastElement();
    					ret.put("#include <impl" + file_separator + module + file_separator + module + ".h>","");
    				}
			}
			else if ((flags & FLAG_CODA_INFO) != 0 && (idl_type instanceof MPrimitiveDef) &&
    				((MPrimitiveDef)idl_type).getKind() == MPrimitiveKind.PK_ANY) {
				String param_type = coda.getAnyType(op.getDefinedIn().getIdentifier(),
    											op.getIdentifier(),p.getIdentifier());
				ret.putAll(coda.getInclude(param_type,target,coda_include_use));
			}
		}
		
        MIDLType idl_op_type = op.getIdlType();
		if(idl_op_type instanceof MStructDef) {
			if (coda_include_use == null)
				ret.put("#include <impl" + file_separator +
						(String) namespace.lastElement() +
						file_separator + ((MStructDef)idl_op_type).getIdentifier() + ".h>","");
			else	 if ((flags & FLAG_CODA_INFO) != 0)
				ret.putAll(coda.getInclude(((MStructDef)idl_op_type).getIdentifier(),target,coda_include_use));
		}
		else if(idl_op_type instanceof MSequenceDef) {
			ret.put("#include <vector>","");
		}
		else if(idl_op_type instanceof MAliasDef) {
			MTyped containedType = (MTyped) idl_op_type;
			MIDLType containedIdlType = containedType.getIdlType();
			if(containedIdlType instanceof MStructDef) {
				if (coda_include_use == null)
					ret.put("#include <impl" + file_separator +
							(String) namespace.lastElement() +
							file_separator + ((MStructDef)idl_op_type).getIdentifier() + ".h>","");
				else if ((flags & FLAG_CODA_INFO) != 0)
					ret.putAll(coda.getInclude(((MStructDef)idl_op_type).getIdentifier(),target,coda_include_use));
			}
				else if(idl_op_type instanceof MSequenceDef) {
					ret.put("#include <vector>","");
				}
		}
		else if ((flags & FLAG_CODA_INFO) != 0 && (idl_op_type instanceof MPrimitiveDef) &&
				((MPrimitiveDef)idl_op_type).getKind() == MPrimitiveKind.PK_ANY) {
			String param_type = coda.getAnyType(op.getDefinedIn().getIdentifier(),
											op.getIdentifier());
			ret.putAll(coda.getInclude(param_type,target,coda_include_use));
		}

		return ret.keySet();
    }
    
    protected Set getOpIncludes(MContainer container, String coda_include_use, String target) {
	
    		Map result = new HashMap();
    		List ops  = container.getFilteredContents(MDefinitionKind.DK_OPERATION,true);
    		for ( Iterator i=ops.iterator(); i.hasNext(); ) {
    			Set ol = getOpIncludes((MOperationDef) i.next(), coda_include_use, target);
    			for ( Iterator j=ol.iterator(); j.hasNext(); ) {
    				result.put(j.next(),"");
    			}
    		}
	
    		if (container instanceof MComponentDef) {
    			MComponentDef component = (MComponentDef) container;
    			for (Iterator supports = component.getSupportss().iterator(); supports.hasNext(); ) {
    				Set sl = getOpIncludes(((MSupportsDef) supports.next()).getSupports(), coda_include_use, target);
    				for ( Iterator sli=sl.iterator(); sli.hasNext(); ) {
    					result.put(sli.next(),"");
    				}
    			}
		
    		}
    		return result.keySet();
    }

	protected String getOperationParamsStd(MOperationDef op, boolean decl, String target)
    {
		List ret = new ArrayList();
		Iterator defaults = findDefaults(op, Default_init, "coda", "").iterator();
		for(Iterator ps = op.getParameters().iterator(); ps.hasNext();) {
			MParameterDef p = (MParameterDef) ps.next();
    			String type = getLanguageTypeStd(p);
    			if (target != null && (flags & FLAG_CODA_INFO) != 0) {
    				MIDLType idl_type = ((MTyped) p).getIdlType();
            		if (! (idl_type instanceof MPrimitiveDef) ||
            				((MPrimitiveDef)idl_type).getKind() == MPrimitiveKind.PK_ANY) {
    					String param_type = (idl_type instanceof MPrimitiveDef) ?
								coda.getAnyType(op.getDefinedIn().getIdentifier(),
										op.getIdentifier(),p.getIdentifier()) : getBaseIdlType(p);
    					String new_type = coda.translateType(param_type,target);
    					if (new_type != null) {
    			            MParameterMode direction = p.getDirection();
    			            String prefix = "";
    			            if(direction == MParameterMode.PARAM_IN) {	// in
    			                prefix = "const ";
    			            }
    			            type = prefix + new_type + "&";
    					}
    				}
    			}
    			ret.add(type + " " + p.getIdentifier() + (decl ? (String) defaults.next() : ""));
		}
		return join(", ", ret);
    }

    /**
     * Get name information about the parameters for the given operation. This
     * will return a comma-separated string, i.e. <name1>, <name2>, ... ,
     * <nameN>for this operation's parameters.
     * 
     * @param op
     *            the operation to investigate.
     * @param name_mods TODO
     * @return a comma separated string of the parameter information requested
     *         for this operation.
     */
    protected String getOperationParamNames(MOperationDef op, Map name_mods)
    {
        List ret = new ArrayList();
        for(Iterator ps = op.getParameters().iterator(); ps.hasNext();) {
        		String name = ((MParameterDef) ps.next()).getIdentifier();
        		ret.add(name_mods != null && name_mods.containsKey(name) ? (String) name_mods.get(name) : name);
        }
        return join(", ", ret);
    }

    /**
     * Get the exceptions thrown by the given operation.
     * 
     * @param op
     *            the operation to investigate.
     * @return a string containing C++ code describing the exceptions that this
     *         operation throws. If there are no exceptions, this returns an
     *         empty string. If there are exceptions, it returns something like
     *         "throw ( exception, exception, ... )".
     */
    protected String getOperationExcepts(MOperationDef op)
    {
        List ret = new ArrayList();
        for(Iterator es = op.getExceptionDefs().iterator(); es.hasNext();)
            ret.add(((MExceptionDef) es.next()).getIdentifier());
        if(ret.size() > 0) {
            return "throw (LocalComponents::CCMException, " + join(", ", ret)
                    + " )";
        }
        else {
            return "throw (LocalComponents::CCMException)";
        }
    }

    /**
     * Load an appropriate template (based on the value in the template_name
     * argument) for the given child, and fill out its variable information.
     * 
     * @param child
     *            MInterfaceDef node to gather information from.
     * @param template_name
     *            the name of the template to load for variable substitution.
     * @param attribute
     *            true if we should fill in attribute information. If false,
     *            then we will fill in operation information.
     * @return a string containing the variable-substituted template requested.
     */
    protected String fillTwoStepTemplates(MInterfaceDef child,
                                          String template_name,
                                          boolean attribute)
    {
        MContained cont = (MContained) current_node;

        // if this is a supports node, we want to actually refer to the
        // home or component that owns this supports definition.

        if(current_node instanceof MSupportsDef) {
            MContained tmp = ((MSupportsDef) cont).getComponent();
            if(tmp == null)
                tmp = ((MSupportsDef) cont).getHome();
            cont = tmp;
        }

        // we need to recursively include all base interfaces here. we'll use a
        // stack and pop off the most recent element, add operation information
        // for that interface, then push on all the bases for that interface.
        // lather, rinse, repeat.

        Stack ifaces = new Stack();
        ifaces.push(child);

        StringBuffer result = new StringBuffer("");

        while(!ifaces.empty()) {
            MInterfaceDef iface = (MInterfaceDef) ifaces.pop();

            List contents = iface
                    .getFilteredContents(
                                         (attribute) ? MDefinitionKind.DK_ATTRIBUTE
                                                 : MDefinitionKind.DK_OPERATION,
                                         false);

            for(Iterator c = contents.iterator(); c.hasNext();) {
                Map vars = null;

                if(attribute)
                    vars = getTwoStepAttributeVariables((MAttributeDef) c
                            .next(), cont);
                else
                    vars = getTwoStepOperationVariables((MOperationDef) c
                            .next(), cont);

                Template template = template_manager
                        .getRawTemplate(template_name, null);

                result.append(template.substituteVariables(vars));
            }

            for(Iterator i = iface.getBases().iterator(); i.hasNext();)
                ifaces.push(i.next());
        }

        return result.toString();
    }

    /**
     * Get a variable hash table sutable for filling in the template from the
     * fillTwoStepTemplates function. This function handles attributes.
     * 
     * @param iface
     *            the interface from which we're starting the two step
     *            operation.
     * @param attr
     *            the particular interface attribute that we're filling in a
     *            template for.
     * @param container
     *            the container in which the given interface is defined.
     * @return a map containing the keys and values needed to fill in the
     *         template for this interface.
     */
    protected Map getTwoStepAttributeVariables(MAttributeDef attr,
                                               MContained container)
    {
        String lang_type = getLanguageType(attr);
        Map vars = new Hashtable();

        vars.put("Object", container.getIdentifier());
        vars.put("Identifier", attr.getIdentifier());
        vars.put("LanguageType", lang_type);

        return vars;
    }
}

