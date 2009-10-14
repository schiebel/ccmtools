/* CCM Tools : Code Generator Library
 * Leif Johnson <leif@ambient.2y.net>
 * Egon Teiniker <egon.teiniker@salomon.at>
 * Copyright (C) 2002 - 2005 Salomon Automation
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import ccmtools.Coda.CodaManager;
import ccmtools.Coda.XMLFileManager;
import ccmtools.Metamodel.BaseIDL.MAttributeDef;
import ccmtools.Metamodel.BaseIDL.MContained;
import ccmtools.Metamodel.BaseIDL.MContainer;
import ccmtools.Metamodel.BaseIDL.MExceptionDef;
import ccmtools.Metamodel.BaseIDL.MFixedDef;
import ccmtools.Metamodel.BaseIDL.MIDLType;
import ccmtools.Metamodel.BaseIDL.MInterfaceDef;
import ccmtools.Metamodel.BaseIDL.MModuleDef;
import ccmtools.Metamodel.BaseIDL.MPrimitiveDef;
import ccmtools.Metamodel.BaseIDL.MPrimitiveKind;
import ccmtools.Metamodel.BaseIDL.MStringDef;
import ccmtools.Metamodel.BaseIDL.MTyped;
import ccmtools.Metamodel.BaseIDL.MTypedefDef;
import ccmtools.Metamodel.BaseIDL.MWstringDef;
import ccmtools.Metamodel.ComponentIDL.MComponentDef;
import ccmtools.Metamodel.ComponentIDL.MConsumesDef;
import ccmtools.Metamodel.ComponentIDL.MEmitsDef;
import ccmtools.Metamodel.ComponentIDL.MEventPortDef;
import ccmtools.Metamodel.ComponentIDL.MFactoryDef;
import ccmtools.Metamodel.ComponentIDL.MFinderDef;
import ccmtools.Metamodel.ComponentIDL.MHomeDef;
import ccmtools.Metamodel.ComponentIDL.MProvidesDef;
import ccmtools.Metamodel.ComponentIDL.MPublishesDef;
import ccmtools.Metamodel.ComponentIDL.MSupportsDef;
import ccmtools.Metamodel.ComponentIDL.MUsesDef;

/**
 * This code generation base class performs a large part of code generation
 * tasks, but it does not perform any actual code output. Thus it needs to be
 * subclassed. Each subclass should handle a particular language.
 * 
 * Before this class performs any actions at all, three steps usually need to
 * take place:
 *  - A user interface or driver program creates a ParserManager class instance
 * and a GraphTraverser class instance.
 *  - The driver program instantiates a class derived from this CodeGenerator
 * class, and also a class derived from the Driver class. These objects will
 * handle traversal events and user interfacing, respectively.
 *  - The driver program uses the parser to reads an IDL3 file and create a CCM
 * MOF graph in memory. This graph gets passed to the graph traverser object for
 * code generation.
 * 
 * Then, when the graph traverser starts, the code generator object handles
 * traversal events and passes some events on to the driver object for user
 * feedback. Specifically, when the code generator receives certain graph
 * traversal events from the graph traverser class, these events are used to
 * generate code. Code is generally written to disk when certain node types are
 * encountered (usually some combination of MContainer, MComponentDef,
 * MInterfaceDef, and MHomeDef, although any node type is valid).
 * 
 * This is very similar in concept to the Python XML SAX parser: a generic graph
 * reader object sends out parse events to a document handler object, and the
 * document handler object contains all the custom code needed to perform
 * desired actions (for example, print out the nodes, count nodes of a certain
 * type, etc.). This base class performs the bulk of the code generation work;
 * the remainder of the work is handled by a few special node functions in
 * derived classes.
 * 
 * Code templates are usually looked for in a directory like
 * /usr/share/ccmtools-A.B/ <language>CodeGenerator/; see the TemplateManager
 * and PythonTemplateManagerImpl documentation for details. Templates form the
 * basis of the code generator customization: Variables declared within a
 * template determine which type of node information gets placed in which output
 * file.
 * 
 * Traversal events ================
 * 
 * There are three graph traversal events of interest : start node, receive node
 * data, and end node.
 * 
 * Start node ----------
 * 
 * When a node starts, the code generator class adds information about the node
 * to the current state of the generator. Specifically, the code generator keeps
 * the current node, node name, node type, and relevant node variables in
 * internal state variables.
 * 
 * It then attempts to call a node-specific handler function. These specific
 * handler functions, defined in the derived classes, interpret language
 * specific information and generally do things like update the output file or
 * directory name.
 * 
 * Node data ---------
 * 
 * Node data elements get added to a global (as opposed to node-specific)
 * variable hash. Data elements are indexed using their full scope identifier.
 * 
 * End node --------
 * 
 * Similar to starting a node, ending a node removes information about the
 * current node from the current state of the generator. This includes updating
 * the current node, node name, node type, and node variables.
 * 
 * If there is a parent node, the variables from the finished node are added to
 * the variables from the parent node, normally using the finished node's output
 * template. For more details, see the documentation for the endNode() and
 * updateVariables() functions.
 * 
 * If appropriate (specifically, if the node type is defined as "global" by
 * being in the global types list, as described above), an output file is
 * generated using the current output template and the values in the variable
 * hash. Derived classes are responsible for handling this event by defining a
 * "protected void writeOutput(Template)" function.
 * 
 * Identifiers ===========
 * 
 * The full scope identifier of a node is found by joining all ancestor node
 * names together with double colons. For example, a traversal history of
 * ModuleName -> ComponentName -> OperationName -> ParameterName would yield
 * ModuleName::ComponentName::OperationName::ParameterName for ParameterName's
 * full scope identifier. This helps distinguish nodes in the graph.
 * 
 * I think a better long-term solution will be to use each element's
 * AbsoluteName (currently not implemented) instead of this scope identifier, as
 * there are currently graph traversal issues for things like supported
 * interfaces and homes (i.e. it is currently possible, though not at all
 * common, to reach an interface or a home in the graph through different
 * traversal paths). This is not a large issue but might benefit from some
 * attention.
 * 
 * As previously mentioned, all data elements are stored in a hash table. Data
 * elements are indexed using this full scope identifier of the data object.
 * This full scope identifier hash key corresponds with the full scope
 * identifier found in the node's template.
 * 
 * Template variable substitution ==============================
 * 
 * See the documentation for the Template interface and TemplateImpl
 * implementing class.
 */
abstract public class CodeGenerator implements TemplateHandler
{

    protected Driver driver = null;

    protected TemplateManager template_manager;
    
    // supports all of the information that is
    // beyond the purview of IDL
    protected static CodaManager coda;
    private static int init_count = 0;

    // types for which we have a global template ; that is, a template that is
    // not contained inside another template.
    protected Set output_types;

    // reserved words. identifiers that contain these words will be mapped to
    // new identifiers.
    protected Set reserved_words;

    // language mappings, a map from idl types (actually just from primitive
    // kinds) to target language constructs.
    protected Map language_mappings;

    protected File output_dir;

    protected Map output_variables;

    protected Object current_node;

    protected String current_name;

    protected String current_type;

    protected Set current_variables;

    protected Stack namespace;
    
    protected int flags = 0x0;
    protected String coda_startup_file = null;

    public final static int FLAG_APPLICATION_FILES = 0x0001;
    public final static int FLAG_CODA_INFO = 0x0010;
    public final static int FLAG_OVERWRITE_FILES = 0x0100;

    private Stack node_stack;

    private Stack name_stack;

    private Stack type_stack;

    private Stack variables_stack;

    protected String scope_separator = "::";

    protected String file_separator = File.separator;

    
    /** *********************************************************************** */

    /**
     * Initialize the class instance.
     * 
     * @param language
     *            a string containing the language type to output. This is used
     *            to help find a template set, and it is case sensitive !
     * @param d
     *            a driver object to handle messages and (if any) user input for
     *            this node handler object.
     * @param out_dir
     *            the directory that will be the root of the output source tree.
     * @param _output_types
     *            an array of type names (for example, MContainer or
     *            MInterfaceDef) for which a code file should be generated.
     * @param _reserved_words
     *            an array of reserved words specific to the language being
     *            generated.
     * @param _language_map
     *            an array of language types to generate for each of the CORBA
     *            primitive types given in the MPrimitiveKind enumeration. This
     *            must be the same size as said enum, and its elements will be
     *            assigned in the same order as the elements of the enum.
     * @see ccmtools.CodeGenerator.GraphTraverser
     */
    public CodeGenerator(String language, Driver d, File out_dir,
            String[] _output_types, String[] _reserved_words,
            String[] _language_map) throws IOException
    {
    		// keep track of how many subclasses may be using our statics
    		++init_count;
    		
        template_manager = new PythonTemplateManagerImpl(language, out_dir.getPath());
        driver = d;

        // set up output types, node types for which we should output some sort
        // of code.

        String[] my_output_types = {};

        output_types = new HashSet();
        for(int i = 0; i < my_output_types.length; i++)
            output_types.add(my_output_types[i]);
        if(_output_types != null)
            for(int i = 0; i < _output_types.length; i++)
                output_types.add(_output_types[i]);

        // set up reserved word list.

        String[] my_reserved_words = {
                "Helper", "Holder", "Operations", "POA", "POATie", "Package"
        };

        reserved_words = new HashSet();
        for(int i = 0; i < my_reserved_words.length; i++)
            reserved_words.add(my_reserved_words[i]);
        if(_reserved_words != null)
            for(int i = 0; i < _reserved_words.length; i++)
                reserved_words.add(_reserved_words[i]);

        // set up a language map from primitive types to whatever the target
        // language types are.

        String[] labels = MPrimitiveKind.getLabels();
        if(_language_map != null) {
            if(_language_map.length != labels.length)
                throw new RuntimeException(
                                           "Language map is not the same length as the primitive "
                                                   + "types list.");

            language_mappings = new Hashtable();
            for(int i = 0; i < labels.length; i++)
                language_mappings.put(labels[i], _language_map[i]);
        }

        // set up an output directory ; create it if it doesn't exist.

        output_dir = out_dir;
        if(!output_dir.isDirectory())
            output_dir.mkdirs();
    }

    public void initCoda( ) {
    		if (coda == null)
    			coda = new XMLFileManager(coda_startup_file);
    }
    
    public void finalizeCoda( ) {
		if (coda != null && init_count == 0) {
			coda.close();
			coda = null;
		}
    }
    
    public void close( ) {
    		--init_count;
    		finalizeCoda( );
    }
    
    /** *********************************************************************** */

    /**
     * Start processing a new graph. Clear out existing class instance
     * variables.
     */
    public void startGraph()
    {
        current_node = null;
        current_name = null;
        current_type = null;
        current_variables = new HashSet();

        node_stack = new Stack();
        name_stack = new Stack();
        type_stack = new Stack();
        variables_stack = new Stack();

        namespace = new Stack();
        output_variables = new Hashtable();
    }

    /**
     * End processing a graph. This implementation does not do anything.
     */
    public void endGraph()
    {
    }

    /**
     * Start a new node in the graph. This function provides basic node tracking
     * functionality and should be called at the beginning of all derived
     * classes' startNode functions, if they exist.
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
        current_node = node;
        //current_name = new String(scope_id);
        current_name = scope_id;
        current_type = node.toString().split(":")[0];
        current_variables = template_manager.getVariables(current_type);

        driver.nodeStart(node, scope_id);
        driver.currentVariables(current_variables);

        node_stack.push(current_node);
        name_stack.push(current_name);
        type_stack.push(current_type);
        variables_stack.push(current_variables);

        // update the namespace if this is a module.

        if(node instanceof MModuleDef)
            namespace.push(((MModuleDef) node).getIdentifier());

        // initialize variables in output variables hash.

        for(Iterator i = current_variables.iterator(); i.hasNext();) {
            output_variables.put(getScopeID((String) i.next()), "");
        }
    }

    /**
     * End a node in the graph. This function provides basic node tracking
     * functionality and should be called at the beginning of all derived
     * classes' endNode functions, if they are defined.
     * 
     * This function updates variables in this node and in the parent node (if
     * there is a parent---see updateVariables for more details). Then, if the
     * node type is defined in the output_types list, the template for this node
     * is loaded, substituted, and written.
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
        current_node = node_stack.pop();
        current_name = (String) name_stack.pop();
        current_type = (String) type_stack.pop();
        current_variables = (Set) variables_stack.pop();
        
        if (coda == null)
        		initCoda( );

        driver.outputVariables(output_variables);
        updateVariables();
          
        // update the namespace if this is a module by removing the last item
        // from the namespace list.
        if(node instanceof MModuleDef)
            namespace.pop();

        driver.nodeEnd(node, scope_id);
    }

    /**
     * Add node data to our internal variable hash.
     * 
     * @param field_type
     *            a string indicating the type of field from whence the node
     *            data came.
     * @param field_name
     *            a string indicating the (capitalized) name of the variable
     *            being handled.
     * @param value
     *            the value of the given variable.
     */
    public void handleNodeData(String field_type, String field_id, Object value)
    {
        String key = getScopeID(field_id);

        driver.nodeData(current_node, field_id, value);

        if(field_type.endsWith("boolean")) {
            Boolean hack = new Boolean(value.toString());
            if(!hack.booleanValue())
                output_variables.put(key, "");
            else
                output_variables.put(key, field_id);

        }
        else if(field_id.equals("Identifier")) {
            String name = (String) value;
            if(field_type.endsWith("MModuleDef")
                    || field_type.endsWith("MInterfaceDef")
                    || field_type.endsWith("MHomeDef")
                    || field_type.endsWith("MComponentDef"))
                name = mapName(name);
            output_variables.put(key, name);

        }
        else {
            if(value == null)
                output_variables.put(key, "");
            else
                output_variables.put(key, value.toString());
        }
    }

    
    

    /** *********************************************************************** */

    /**
     * Return a boolean indicating the state of the given flag.
     * 
     * @return the value of the given flag.
     */
    public boolean getFlag(int flag)
    {
        return ((flags & flag) != 0);
    }

    /**
     * Set the given flag.
     * 
     * @param flag
     *            the flag to set.
     */
    public void setFlag(int flag)
    {
        flags |= flag;
    }

    /**
     * Clear the given flag.
     * 
     * @param flag
     *            the flag to clear.
     */
    public void clearFlag(int flag)
    {
        flags &= (int) ~flag;
    }
    
    public void setCodaStartupFile(String file) {
    		coda_startup_file = file;
    }

    /**
     * Get the template manager responsible for handling this node handler's
     * templates.
     * 
     * @return the current object's template manager.
     */
    public TemplateManager getTemplateManager()
    {
        return template_manager;
    }

    /** *********************************************************************** */

    // abstract base class functionality. concrete derived classes must
    // implement these functions.
    /**
     * Write generated code to an output file.
     * 
     * @param template
     *            the template object to get the generated code structure from ;
     *            variable values should come from the node handler object.
     */
    abstract protected void writeOutput(Template template) throws IOException;

    /**
     * Return the language type corresponding to the given object's IdlType.
     * 
     * @param object
     *            the node object to use for type finding.
     */
    abstract protected String getLanguageType(MTyped object);

    /** *********************************************************************** */

    // some helper functions ... trying to emulate python here :)
    /**
     * Join a collection of strings (a, b, c, ..., z) by combining each element
     * with the given separator A. The resulting string will be of the form
     * aAbAcA...Az.
     * 
     * @param sep
     *            the string to use as a separator.
     * @param parts
     *            a collection of strings to join.
     * @return a string containing the joined parts separated by the given
     *         separator.
     */
    protected String join(String sep, Collection parts)
    {
        if(parts != null) {
            if(parts.size() > 1) {
                StringBuffer ret = new StringBuffer("");
                for(Iterator i = parts.iterator(); i.hasNext();) {
                    String part = (String) i.next();
                    ret.append(part + sep);
                }
                ret = ret.reverse();
                ret = new StringBuffer(ret.substring(sep.length()));
                return ret.reverse().toString();
            }

            if(parts.size() == 1)
                return "" + parts.iterator().next();
        }

        return new String("");
    }

    /**
     * Slice a part of the given list. If start is negative, the function will
     * return the part of the collection that includes all but the last "start"
     * elements. Otherwise slice will return the subcollection that includes all
     * but the first "start" elements.
     * 
     * @param parts
     *            the source list to slice.
     * @param start
     *            the portion of the list to remove.
     * @return a new sublist that includes only the desired sublist from the
     *         original parts.
     */
    protected List slice(List parts, int start)
    {
        if(start == 0)
            return parts;
        if(parts == null)
            return new ArrayList();
        int size = parts.size();
        if(size == 0)
            return new ArrayList();
        if((start >= size) || (start <= -size))
            return new ArrayList();
        if(start < 0)
            return parts.subList(0, size + start);
        return parts.subList(start, size);
    }

    /** *********************************************************************** */

    // miscellaneous helper functions.
    /**
     * Helper function for writing finalized files.
     * 
     * @param directory
     *            the directory, relative to the package root, where the file
     *            should be written.
     * @param file
     *            the name of the file to write.
     * @param output
     *            a string holding the destination file's contents.
     */
    protected void writeFinalizedFile(String directory, String file,
                                      String output) throws IOException
    {
        File local_dir = new File(output_dir, directory);
        if(!local_dir.isDirectory())
            local_dir.mkdirs();

        File out_file = new File(local_dir, file);
        FileWriter writer = new FileWriter(out_file);
        writer.write(output, 0, output.length());
        writer.close();

        driver.outputFile(out_file.toString());
    }

    /**
     * Join the base names of the current node using the given string as a
     * separator. The current node should be an instance of MInterfaceDef.
     * 
     * @param sep
     *            the separator to use between base names.
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
            names.add(((MInterfaceDef) i.next()).getIdentifier());
        return join(sep, names);
    }

    /**
     * Find a list of the modules in which the given node is contained.
     * 
     * @param node
     *            a graph node to investigate.
     * @return a list of the namespaces that fully scope this node.
     */
    protected List getScope(MContained node)
    {
        List scope = new ArrayList();
        MContainer c = node.getDefinedIn();
        while(c.getDefinedIn() != null) {
            if(c instanceof MModuleDef)
                scope.add(0, c.getIdentifier());
            c = c.getDefinedIn();
        }
        return scope;
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

        if(data_type.equals("Namespace"))
            return join(scope_separator, names);

        if(data_type.equals("IncludeNamespace"))
            return join(file_separator, names);

        return "";
    }

    /**
     * Return a string version of the IDL type corresponding to the given
     * object's CCM IdlType.
     * 
     * @param object
     *            the node object to use for type finding.
     * @return a string containing the base IDL type of the given object. An
     *         exception is thrown if the object has no IDL type. The resulting
     *         string will be (for primitive types and strings) the lookup from
     *         the language_mappings hash, or (for MContained graph nodes) the
     *         fully scoped identifier of the object.
     */
    protected String getBaseLanguageType(MTyped object)
    {
        MIDLType idl_type = object.getIdlType();

        if(idl_type == null)
            throw new RuntimeException("object '" + object
                    + "' has no IDL type");

        return getBaseLanguageType(idl_type);
    }

    protected String getBaseLanguageType(MIDLType idl_type)
    {
        String type = "";

        if(idl_type instanceof MPrimitiveDef) {
            type = ((MPrimitiveDef) idl_type).getKind().toString();
        }
        else if(idl_type instanceof MStringDef) {
            type = ((MStringDef) idl_type).getKind().toString();
        }
        else if(idl_type instanceof MWstringDef) {
            type = ((MWstringDef) idl_type).getKind().toString();
        }
        else if(idl_type instanceof MFixedDef) {
            type = ((MFixedDef) idl_type).getKind().toString();
        }

        if(!type.equals("") && language_mappings.containsKey(type)) {
            return (String) language_mappings.get(type);
        }

        if(idl_type instanceof MContained) {
            MContained cont = (MContained) idl_type;
            List scope = getScope(cont);
            scope.add(cont.getIdentifier());
            return join(scope_separator, scope);
        }
        return type;
    }

    /**
     * Return a string version of the IDL type corresponding to the given
     * object's CCM IdlType.
     * 
     * @param object
     *            the node object to use for type finding.
     * @return a string containing the base IDL type of the given object. The
     *         result will be null if the base type is not a recognized type
     *         object from the IDL metamodel, but otherwise will return
     *         something like 'PK_FOO' if object is of primitive type FOO, or
     *         the object's identifier if object is derived from a typedef
     *         class.
     */
    protected String getBaseIdlType(MTyped object)
    {
        MIDLType idl_type = object.getIdlType();

        if(idl_type == null)
            throw new RuntimeException(object + " has no IDL type");

        return getBaseIdlType(idl_type);
    }

    protected String getBaseIdlType(MIDLType idl_type)
    {
        // first check for aliases and structs and such ... try to get the
        // identifier, if that doesn't work get the underlying type.

        if(idl_type instanceof MTypedefDef) {
            MTypedefDef typedef = (MTypedefDef) idl_type;
            return typedef.getIdentifier();
        }

        // type is some other class derived from mtyped ... get its derivative
        // type.

        if(idl_type instanceof MTyped)
            return getBaseIdlType((MTyped) idl_type);

        // type must be one of the primitive kinds ... try to get the primitive
        // kind for it.
        String type = null;
        if(idl_type instanceof MPrimitiveDef) {
            type = ((MPrimitiveDef) idl_type).getKind().toString();
        }
        else if(idl_type instanceof MStringDef) {
            type = ((MStringDef) idl_type).getKind().toString();
        }
        else if(idl_type instanceof MWstringDef) {
            type = ((MWstringDef) idl_type).getKind().toString();
        }
        else if(idl_type instanceof MFixedDef) {
            type = ((MFixedDef) idl_type).getKind().toString();
        }
        else if(idl_type instanceof MTypedefDef) {
            type = ((MTypedefDef) idl_type).getIdentifier();
        }
        else if(idl_type instanceof MInterfaceDef) {
        		type = ((MInterfaceDef) idl_type).getIdentifier();
        }
        else {
            throw new RuntimeException("unknown IDL type :" + idl_type);
        }

        return type;
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
    abstract protected String getScopedInclude(MContained node);

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
        List scope = getScope(node);
        scope.add(node.getIdentifier());
        for(Iterator n = namespace.iterator(); n.hasNext();) {
            String name = (String) n.next();
            try {
                String mine = (String) scope.get(0);
                if(mine.equals(name))
                    scope.remove(0);
            }
            catch(Exception e) {
                break;
            }
        }
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
        List scope = getScope(node);
        scope.add(node.getIdentifier());
        String result = join(file_separator, scope);
        return result;
    }

    /**
     * Get a local value for the given variable name. This function performs
     * some common value parsing in the CCM MOF library. More specific value
     * parsing needs to be provided in the subclass for a given language, in the
     * overridden getLocalValue function. Subclasses should call this function
     * first and then perform any subclass-specific value manipulation with the
     * returned value.
     * 
     * @param variable
     *            the variable name to get a value for.
     * @return the value of the variable available from the current
     *         output_variables hash table. Could be an empty string.
     */
    protected String getLocalValue(String variable)
    {
        String scope_id = getScopeID(variable);
        String value = "";

        if(output_variables.containsKey(scope_id))
            value = (String) output_variables.get(scope_id);

        // this whole variable/type comb is really horrible. yuk.

        if(variable.equals("LanguageType")) {
            if(current_node instanceof MTyped)
                value = (String) getLanguageType((MTyped) current_node);
        }
        else if(variable.endsWith("SupportsType")) {
            MSupportsDef supports = (MSupportsDef) current_node;
            value = getFullScopeIdentifier(supports.getSupports());
        }
        else if(variable.endsWith("ProvidesType")) {
            MProvidesDef provides = (MProvidesDef) current_node;
            value = getFullScopeIdentifier(provides.getProvides());
        }
        else if(variable.endsWith("UsesType")) {
            MUsesDef uses = (MUsesDef) current_node;
            value = getFullScopeIdentifier(uses.getUses());
        }
        else if(variable.endsWith("EmitsType")
                || variable.endsWith("PublishesType")
                || variable.endsWith("ConsumesType")) {
            MEventPortDef port = (MEventPortDef) current_node;
            value = getFullScopeIdentifier(port.getType());
        }
        else if(variable.endsWith("ComponentType")) {
            MComponentDef component = null;

            if(current_node instanceof MHomeDef)
                component = ((MHomeDef) current_node).getComponent();
            else if(current_node instanceof MProvidesDef)
                component = ((MProvidesDef) current_node).getComponent();
            else if(current_node instanceof MUsesDef)
                component = ((MUsesDef) current_node).getComponent();
            else if(current_node instanceof MEmitsDef)
                component = ((MEmitsDef) current_node).getComponent();
            else if(current_node instanceof MPublishesDef)
                component = ((MPublishesDef) current_node).getComponent();
            else if(current_node instanceof MConsumesDef)
                component = ((MConsumesDef) current_node).getComponent();
            else if(current_node instanceof MSupportsDef)
                component = ((MSupportsDef) current_node).getComponent();
            else if(current_node instanceof MFactoryDef)
                component = ((MFactoryDef) current_node).getHome()
                        .getComponent();
            else if(current_node instanceof MFinderDef)
                component = ((MFinderDef) current_node).getHome()
                        .getComponent();

            if(current_node instanceof MAttributeDef) {
                MContained cont = ((MAttributeDef) current_node).getDefinedIn();
                if(cont instanceof MComponentDef)
                    component = (MComponentDef) cont;
            }

            if(component != null)
                value = getFullScopeIdentifier(component);
        }
        else if(variable.endsWith("HomeType")) {
            Iterator homes = null;
            MHomeDef home = null;

            if(current_node instanceof MFactoryDef)
                home = ((MFactoryDef) current_node).getHome();
            else if(current_node instanceof MFinderDef)
                home = ((MFinderDef) current_node).getHome();
            else if(current_node instanceof MComponentDef)
                homes = ((MComponentDef) current_node).getHomes().iterator();
            else if(current_node instanceof MProvidesDef)
                homes = ((MProvidesDef) current_node).getComponent().getHomes()
                        .iterator();
            else if(current_node instanceof MUsesDef)
                homes = ((MUsesDef) current_node).getComponent().getHomes()
                        .iterator();
            else if(current_node instanceof MEmitsDef)
                homes = ((MEmitsDef) current_node).getComponent().getHomes()
                        .iterator();
            else if(current_node instanceof MPublishesDef)
                homes = ((MPublishesDef) current_node).getComponent()
                        .getHomes().iterator();
            else if(current_node instanceof MConsumesDef)
                homes = ((MConsumesDef) current_node).getComponent().getHomes()
                        .iterator();
            else if(current_node instanceof MSupportsDef)
                homes = ((MSupportsDef) current_node).getComponent().getHomes()
                        .iterator();

            if(homes != null) {
                String id = ((MContained) current_node).getIdentifier();
                try {
                    home = (MHomeDef) homes.next();
                }
                catch(Exception e) {
                    throw new RuntimeException("Node '" + id + "' has no home");
                }
            }

            if(home != null)
                value = getFullScopeIdentifier(home);
        }
        else if(variable.equals("LanguageTypeInclude")) {
            if(current_node instanceof MTyped) {
                MIDLType idl_type = ((MTyped) current_node).getIdlType();
                if(idl_type instanceof MContained) {
                    value = getScopedInclude((MContained) idl_type);
                }
                else if(idl_type instanceof MTyped) {
                    MIDLType sub_type = ((MTyped) idl_type).getIdlType();
                    if(sub_type instanceof MContained)
                        value = getScopedInclude((MContained) sub_type);
                }
            }
        }
        else if(variable.equals("BaseInclude")) {
            if(current_node instanceof MInterfaceDef) {
                MInterfaceDef iface = (MInterfaceDef) current_node;
                List bases = new ArrayList();
                for(Iterator i = iface.getBases().iterator(); i.hasNext();)
                    bases.add(getScopedInclude((MInterfaceDef) i.next()));
                value = join("\n", bases);
            }
        }
        else if( variable.equals("ExceptionInclude") &&
        			current_node instanceof MExceptionDef ) {
            value = getScopedInclude((MExceptionDef) current_node);
        }
        else if(variable.equals("HomeInclude")) {
            if(current_node instanceof MComponentDef) {
                Iterator homes = ((MComponentDef) current_node).getHomes()
                        .iterator();
                value = getFullScopeInclude((MHomeDef) homes.next());
            }
        }
        else if(variable.equals("ProvidesInclude")
                || variable.equals("SupportsInclude")
                || variable.equals("UsesInclude")) {
            MInterfaceDef iface = null;

            if(current_node instanceof MProvidesDef)
                iface = ((MProvidesDef) current_node).getProvides();
            else if(current_node instanceof MSupportsDef)
                iface = ((MSupportsDef) current_node).getSupports();
            else if(current_node instanceof MUsesDef)
                iface = ((MUsesDef) current_node).getUses();

            if(iface != null)
                value = getFullScopeInclude(iface);
        }
        else if(variable.equals("ConsumesInclude")
                || variable.equals("EmitsInclude")
                || variable.equals("PublishesInclude")) {
            value = getFullScopeInclude(((MEventPortDef) current_node)
                    .getType());
        }
        else if(variable.equals("SelfInclude")) {
            if(current_node instanceof MContained)
                value = getFullScopeInclude((MContained) current_node);
        }
        else if(variable.equals("PreprocIdentifier")) {
            if(current_node instanceof MContained)
                value = getFullScopeInclude((MContained) current_node);
            value = value.replaceAll("[^\\w]", "_");
        }
        else if(variable.endsWith("Namespace")) {

            // This is just a useful default ; subclasses should override this
            // case if they need to pass a particular second parameter.

            value = handleNamespace(variable, "");
        } else if(variable.equals("Module")) {
        		value = (String) namespace.lastElement();
        }

        return value;
    }

    /**
     * Call the writeOutput function if this node is appropriate for generated
     * code output. This node checks to make sure the current node is given in
     * the output_types list, and it makes sure the node is defined in an
     * original source IDL file (we don't want to generate code from included
     * IDL files).
     */
    protected void writeOutputIfNeeded()
    {
        // don't generate code for model elements that are not defined as
        // output_types (e.g. MStructDef, MExceptionDef, etc.)
        if(!output_types.contains(current_type))
            return; 

        // don't generate code for model elements that are included from
        // the original IDL file.
        if((current_node instanceof MContained)
                && !((MContained) current_node).getSourceFile().equals(""))
            return;

        // don't generate code for an MExceptionDef that has an MOperationDef 
        // as parent in the type tree.
        if((current_node instanceof MExceptionDef)) {
            String parent = (String)type_stack.peek();
            if(parent.equals("MOperationDef")) {
                return;
            }
        }
        
        try {
            Template template = template_manager.getTemplate(current_type,current_name);
            if(template == null)
                throw new RuntimeException();
            writeOutput(template);
        }
        catch(RuntimeException error) {
            throw new RuntimeException("Cannot find a template for "
                    + current_name + " (node type " + current_type + ")");
        }
        catch(IOException error) {
            throw new RuntimeException("Error writing output for "
                    + current_name + " (node type " + current_type + ")");
        }
    }

    /** *********************************************************************** */

    /**
     * Get a local scope id for the given node variable name.
     */
    protected String getScopeID(String var)
    {
        return current_name + scope_separator + var;
    }

    /**
     * Map an identifier to a language-safe identifier.
     * 
     * @param identifier
     *            an identifier to check for name collisions. If the identifier
     *            collides with one of the reserved words for the target
     *            language, the identifier will be mapped to a language-safe
     *            identifier by adding a prefix underscore.
     * @return the string that results after mapping.
     */
    private String mapName(String identifier)
    {
        if(reserved_words.contains(identifier))
            return new String("_" + identifier);
        else
            return new String(identifier);
    }

    /**
     * Build up a string from current node info by adding boolean parts relevant
     * to the current MContained instance. A boolean part is relevant if it
     * applies to the node instance (for example, "Abstract" is not a property
     * of MOperationDefs, but is relevant for MInterfaceDefs), and if its
     * current value is true.
     * 
     * If there are multiple boolean variables available for a node type, they
     * are added in alphabetical order. Appended boolean variable names are all
     * lowercase letters except the first letter, which is uppercase.
     * 
     * @return a string containing all true boolean attribute variables for the
     *         given node.
     */
    private String getBooleans()
    {
        String attrs = "";
        SortedSet bool_attrs = new TreeSet();

        Method[] node_methods = current_node.getClass().getMethods();
        for(int i = 0; i < node_methods.length; i++) {
            String field_name = node_methods[i].getName();
            if(field_name.startsWith("is"))
                bool_attrs.add(field_name);
        }

        for(Iterator i = bool_attrs.iterator(); i.hasNext();) {
            try {
                Class klass = current_node.getClass();
                Method method = klass.getMethod((String) i.next(), (Class[]) null);
                Object result = method.invoke(current_node, (Object[]) null);
                Boolean hack = new Boolean(result.toString());
                if(hack.booleanValue())
                    attrs += method.getName().substring(2);
            }
            catch(NoSuchMethodException e) {
                continue;
            }
            catch(IllegalAccessException e) {
                continue;
            }
            catch(InvocationTargetException e) {
                continue;
            }
        }

        return attrs;
    }

    /**
     * Update the current node's output variables information by getting local
     * values for each variable in the given list.
     * 
     * @param variables
     *            a list of variable names to find values for and add to the
     *            output_variables hash.
     */
    private void updateSubvariables(Set variables)
    {
    		for(Iterator i = variables.iterator(); i.hasNext();) {
            String var = (String) i.next();
            Object key = getScopeID(var);
            Object value = getLocalValue(var);
            output_variables.put(key, value);

            driver.message("subvariable " + key + " => " + value);
        }
    }

    /**
     * Update variables that depend on this node.
     * 
     * This function first updates subvariables in the current node using the
     * updateSubvariables function. Then, if the node has a parent, the function
     * updates all parent variables matching the current node's type. Parent
     * variables are updated by performing the following tasks :
     * 
     * 1. Updating the global variable hash with values for the parent
     * variable's template,
     * 
     * 2. Loading the template for the parent variable (with all appropriate
     * boolean variable names appended), and
     * 
     * 3. Adding the resulting substituted template to the parent variable's
     * existing value.
     * 
     * The last step is quite important, as it allows for arbitrary numbers of
     * child nodes to successively add their information to a parent. For
     * example, this is what allows an MOperationDef node to have zero or more
     * MParameterDef child nodes. See the template section of the user manual
     * for more information on this process.
     */
    private void updateVariables()
    {
        updateSubvariables(current_variables);

        if(variables_stack.size() <= 0)
            return;

        String bool_attrs = getBooleans();
        Set parent_vars = (Set) variables_stack.peek();
        for(Iterator i = parent_vars.iterator(); i.hasNext();) {
            String var = (String) i.next();

            // here we're only interested in nodes that are appropriate for the
            // parent node.
            if(!var.startsWith(current_type))
                continue;

            updateSubvariables(template_manager.getVariables(var));

            // This adds boolean variable attributes to the end of the template
            // name we're trying to build. The resulting template name might be
            // something like, for example, 'Multiple' (for an MUsesDef object)
            // or 'AbstractLocal' (for an MComponetDef object).

            String full_var = var + bool_attrs;
            driver.message("loading template for " + full_var);
            Template t = template_manager.getTemplate(full_var, current_name);
            if(t == null)
                throw new RuntimeException("Cannot find a template for "
                        + current_name + " (" + full_var + ")");

            driver.templateContents(t.substituteVariables(output_variables));

            // add the template contents to the appropriate parent variable's
            // current value.

            Object scope_id = name_stack.peek() + scope_separator + var;
            String prev_value = (String) output_variables.get(scope_id);
            String result = t.substituteVariables(output_variables);

            output_variables.put(scope_id, prev_value + result);

            driver.message("variable " + scope_id + " => " + prev_value
                    + result);
        }
    }

    /**
     * This method reads a file, specified by a File object, and compares the
     * file's content with a given code string.
     * 
     * @param code
     *            A string containing source code.
     * @param file
     *            A File object that points to a file which should be compare.
     * @return true if the file's content is equal with the given code string
     *         false in all other cases
     */
    protected boolean isCodeEqualWithFile(String code, File file)
    {
        try {
            if(file.isFile()) {
                StringBuffer buffer = new StringBuffer();
                FileInputStream stream = new FileInputStream(file);
                InputStreamReader input = new InputStreamReader(stream);
                BufferedReader reader = new BufferedReader(input);
                String line = null;
                while((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
                return code.equals(buffer.toString());
            }
        }
        catch(IOException e) {
            System.err.println("ERROR: Can't read " + file);
        }
        return false;
    }

    /**
     * This method removes empty lines (if more than one) and similar #include
     * statements from the generated code.
     * 
     * @param code
     *            A string containing generated code that should be prettified.
     * @return A string containing a prittified version of a given source code.
     */
    protected String prettifyCode(String code)
    {
        StringBuffer pretty_code = new StringBuffer();
        Set include_set = new HashSet();
        int from_index = 0;
        int newline_index = 0;
        boolean isEmptyLineSuccessor = false;
        do {
            newline_index = code.indexOf('\n', from_index);
            String code_line = code.substring(from_index, newline_index);
            from_index = newline_index + 1;
            if(code_line.length() != 0) {
                isEmptyLineSuccessor = false;

                if(code_line.startsWith("#include")) {
                    if(include_set.contains(code_line)) {
                        // Ignore similar #include statements
                    }
                    else {
                        include_set.add(code_line);
                        pretty_code.append(code_line);
                        pretty_code.append('\n');
                    }
                }
                else {
                    pretty_code.append(code_line);
                    pretty_code.append('\n');
                }
            }
            else {
                if(isEmptyLineSuccessor) {
                    // Ignore second empty line
                }
                else {
                    isEmptyLineSuccessor = true;
                    pretty_code.append('\n');
                }
            }
        } while(from_index < code.length());
        return pretty_code.toString();
    }
}

