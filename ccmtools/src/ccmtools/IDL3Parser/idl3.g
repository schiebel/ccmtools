header {
/* CCM Tools : IDL3 Parser
 * Edin Arnautovic <edin.arnautovic@salomon.at>
 * Leif Johnson <leif@ambient.2y.net>
 * Copyright (C) 2002, 2003 Salomon Automation
 *
 * Generated using antlr <http://antlr.org/> from source grammar file :
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

package ccmtools.IDL3Parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ccmtools.Metamodel.BaseIDL.*;
import ccmtools.Metamodel.ComponentIDL.*;

}

/*
 * This is a (semi-)complete parser for the IDL language as defined by the CORBA
 * 3.0 specification. It will allow those who need an IDL parser to get
 * up-and-running very quickly. Though IDL's syntax is very similar to C++, it
 * is also much simpler, due in large part to the fact that it is a
 * declarative-only language.
 *
 * Some things that are not included are: Symbol table construction (it is not
 * necessary for parsing, btw) and preprocessing (for IDL compiler #pragma
 * directives). You can use just about any C or C++ preprocessor, but there is
 * an interesting semantic issue if you are going to generate code: In C,
 * #include is a literal include, in IDL, #include is more like Java's import:
 * It adds definitions to the scope of the parse, but included definitions are
 * not generated.
 *
 * Jim Coker, jcoker@magelang.com
 * adapted for CORBA 3.0 by Edin Arnautovic <edin.arnautovic@salomon.at>
 */

/* TODO :
 * o unions :
 *   - if 'default' label exists, verify there is a default available in the
 *     discriminator
 * o operations:
 *   - verify that "oneway" operations have no out or inout params and have a
 *     void return type
 * o make name lookup more rigorous (see FIXMEs)
 * o add enum support to const attribute definitions (already there ?)
 * o implement better scope handling (especially lookups in symbol table)
 */

/* ROAD MAP :
 *
 * This file is very long. As such this little note is here to help locating a
 * particular identifier or some such. The symbols are arranged in more or less
 * decreasing scope. The file starts with the Parser definition and then
 * contains the grammar rules, arranged in increasing numeric order, as they are
 * numbered in the OMG IDL Grammar (section 3.4, CORBA version 3.0
 * specification). The rules are grouped roughly as follows (B stands for Base
 * IDL, C stands for Component IDL) :
 *
 * B specification (top level symbol), definitions, modules
 * B interfaces, values, constants, literals
 * B types, structs, unions, enums
 * B operations, parameters, attributes, exceptions
 *
 * C components, emits, publishes, consumes, provides, uses
 * C homes, factories, finders
 * C events
 *
 * Rules are specified in comments above each rule's implementation. Rules have
 * cross-referenced rule numbers to hopefully make it easier to find different
 * parts in a rule. Rules in comments generally have the form :
 *
 * // na. <A> ::= <B,nb> | <C,nc>
 *
 * The bottom of this file contains the lexer definition and rules.
 */

class IDL3Parser extends Parser;
options { exportVocab = IDL3; k = 2; }
{
    // debug levels for the parser are allowed to use the lower 24 bits of a
    // long int.
    private final static long DEBUG_FORWARD_DECL = 0x000001;
    private final static long DEBUG_INTERFACE    = 0x000002;
    private final static long DEBUG_COMPONENT    = 0x000004;
    private final static long DEBUG_EVENT        = 0x000008;
    private final static long DEBUG_EXCEPTION    = 0x000010;
    private final static long DEBUG_OPERATION    = 0x000020;
    private final static long DEBUG_PARAMETER    = 0x000040;
    private final static long DEBUG_IDL_TYPE     = 0x000080;
    private final static long DEBUG_TYPEDEF      = 0x000100;
    private final static long DEBUG_NAMESPACE    = 0x000200;
    private final static long DEBUG_PARSER_TOKEN = 0x000400;
    private final static long DEBUG_SYMBOL_TABLE = 0x000800;
    private final static long DEBUG_FILE         = 0x001000;
    private final static long DEBUG_VALUE        = 0x002000;
    private final static long DEBUG_CONSTANT     = 0x004000;
    private final static long DEBUG_INHERITANCE  = 0x008000;
    private final static long DEBUG_PRIMARY_KEY  = 0x010000;
    private final static long DEBUG_CONTAINER    = 0x020000;

    // padding for future debug level manipulation ...
    private final static long DEBUG_UNUSED_F     = 0x040000;
    private final static long DEBUG_UNUSED_G     = 0x080000;
    private final static long DEBUG_UNUSED_H     = 0x100000;
    private final static long DEBUG_UNUSED_I     = 0x200000;
    private final static long DEBUG_UNUSED_J     = 0x400000;
    private final static long DEBUG_UNUSED_K     = 0x800000;

    private final static String[] keywords =
    {
        "abstract", "any", "attribute", "boolean", "case", "char", "component",
        "const", "consumes", "context", "custom", "default", "double",
        "exception", "emits", "enum", "eventtype", "factory", "FALSE", "finder",
        "fixed", "float", "getraises", "home", "import", "in", "inout",
        "interface", "local", "long", "module", "multiple", "native", "Object",
        "octet", "oneway", "out", "primarykey", "private", "provides", "public",
        "publishes", "raises", "readonly", "setraises", "sequence", "short",
        "string", "struct", "supports", "switch", "TRUE", "truncatable",
        "typedef", "typeid", "typeprefix", "unsigned", "union", "uses",
        "ValueBase", "valuetype", "void", "wchar", "wstring",
    };

    private long debug = 0;

    private IDL3SymbolTable symbolTable;
    private Map scopeTable;
    private MContainer specification = null;
    private ParserManager manager;

    /*
     *  Set up the current class instance for a parse, by specifying the parser
     *  manager object that this parser belongs to. This loads the parser
     *  manager's symbol table into this parser instance.
     */
    public void setManager(ParserManager m)
    {
        manager = m;
        symbolTable = manager.getSymbolTable();
        scopeTable = new Hashtable();
    }

    /*
     * Set the debug level for this parser class instance.
     */
    public void setDebug(long d) { debug = d; }

    /*
     * Translate the given object scope id into a CORBA compatible repository
     * identifier.
     */
    private String createRepositoryId(String id)
    {
        String scope = symbolTable.currentScopeAsString();
        String replaced = scope.replaceAll("::", "/");

        if(scope.equals("")) {
            return "IDL:"+ replaced + id + ":1.0";
        } else {
            return "IDL:"+ replaced + "/" + id + ":1.0";
        }
    }

    /*
     * Find out if a given string is an IDL keyword.
     */
    private boolean checkKeyword(String id)
    {
        for (int i = 0; i < keywords.length; i++)
            if (keywords[i].equalsIgnoreCase(id)) return false;
        return true;
    }

    /*
     * Find out if the given string could be a Long object. If so, return the
     * Long object, and if not throw a semantic exception.
     */
    private Long checkLong(String expr)
        throws TokenStreamException
    {
        Long result = null;
        try {
            result = new Long(expr);
            return result;
        } catch (Exception e) {
            throw new TokenStreamException(
                "can't evaluate '"+expr+"' as an integer");
        }
    }

    /*
     * Check to make sure the given string is a positive (1 or greater) valid
     * long integer. Throw an exception if not.
     */
    private void checkPositive(String bound)
        throws TokenStreamException
    {
        try {
            Long limit = new Long(bound);
            if (limit.longValue() < 1) { throw new RuntimeException(); }
        } catch (RuntimeException e) {
            throw new TokenStreamException("invalid positive value "+bound);
        }
    }

    /*
     * Find out if the given string could be a Float object. If so, return the *
     * Float object, and if not throw a semantic exception.
     */
    private Float checkFloat(String expr)
        throws TokenStreamException
    {
        Float result = null;
        try {
            result = new Float(expr);
            return result;
        } catch (Exception e) {
            throw new TokenStreamException(
                "can't evaluate '"+expr+"' as a float");
        }
    }

    /*
     * Get the currently active source file. Returns an empty string if the
     * current source file is the same as the original source file.
     */
    private String getSourceFile()
    {
        String source = manager.getSourceFile();
        if (source.equals(manager.getOriginalFile())) source = "";
        if ((debug & DEBUG_FILE) != 0)
            System.out.println("[f] setting source file to '"+source+"'");
        return source;
    }

    /*
     * See if the given identifier points to an object that's already in the
     * symbol table. If the identifier is found, and points to an object of the
     * same type as the given query object, then check to see if the looked up
     * object is a forward declaration (which means it has a null definition
     * body in some sense, usually meaning that there are no members associated
     * with the definition). If the looked up object is not a forward
     * declaration, throw a semantic error.
     */
    private MContained verifyNameEmpty(String id, MContained query)
        throws TokenStreamException
    {
        MContained lookup =
            lookupName(id, DEBUG_TYPEDEF | DEBUG_INTERFACE);

        if (lookup == null) return query;

        Class qtype = query.getClass().getInterfaces()[0];

        if (! qtype.isInstance(lookup)) return query;

        if (((lookup instanceof MStructDef) &&
                (((MStructDef) lookup).getMembers().size() == 0)) ||
            ((lookup instanceof MUnionDef) &&
                (((MUnionDef) lookup).getUnionMembers().size() == 0)) ||
            ((lookup instanceof MEnumDef) &&
                (((MEnumDef) lookup).getMembers().size() == 0)) ||
            ((lookup instanceof MExceptionDef) &&
                (((MExceptionDef) lookup).getMembers().size() == 0)) ||
            ((lookup instanceof MComponentDef) &&
                ((((MComponentDef) lookup).getContentss().size() == 0) &&
                 (((MComponentDef) lookup).getEmitss().size() == 0) &&
                 (((MComponentDef) lookup).getPublishess().size() == 0) &&
                 (((MComponentDef) lookup).getConsumess().size() == 0) &&
                 (((MComponentDef) lookup).getFacets().size() == 0) &&
                 (((MComponentDef) lookup).getReceptacles().size() == 0))) ||
            ((lookup instanceof MHomeDef) &&
                ((((MHomeDef) lookup).getContentss().size() == 0) &&
                 (((MHomeDef) lookup).getFactories().size() == 0) &&
                 (((MHomeDef) lookup).getFinders().size() == 0))) ||
            ((lookup instanceof MInterfaceDef) &&
                (((MInterfaceDef) lookup).getContentss().size() == 0)) ||
            (lookup instanceof MModuleDef)) {
            return lookup;
        }

        throw new TokenStreamException(
            "identifier '"+id+"' was used more than once");
    }

    /*
     * Verify that the given name exists and is an instance of the correct
     * class. Throw an exception if not.
     */
    private MContained verifyNameExists(String id, MContained query)
        throws TokenStreamException
    {
        MContained lookup =
            lookupName(id, DEBUG_IDL_TYPE | DEBUG_TYPEDEF);

        try {
            Class qtype = query.getClass().getInterfaces()[0];
            if (qtype.isInstance(lookup)) return lookup;
            throw new RuntimeException();
        } catch (RuntimeException e) {
            throw new TokenStreamException("identifier '"+id+"' is undefined or of the wrong type");
        }
    }

    /*
     * Get the IDL type for a given identifier.
     */
    private MIDLType getIDLType(String id)
        throws TokenStreamException
    {
        MContained contained = new MContainedImpl();
        contained = verifyNameExists(id, contained);

        if (contained instanceof MIDLType)
            return (MIDLType) contained;

        if (contained instanceof MTyped)
            return ((MTyped) contained).getIdlType();

        throw new TokenStreamException("cannot find IDL type for '"+
            contained.getIdentifier()+"'");
    }

    /*
     * Add the contents to the given container. Check to ensure each item is
     * defined in the current file being parsed.
     */
    private void checkAddContents(MContainer container, List contents)
        throws TokenStreamException
    {
        if (container == null)
            throw new TokenStreamException(
                "can't add contents ("+contents+") to a null container");

        Iterator it = contents.iterator();
        while (it.hasNext()) {
            MContained item = (MContained) it.next();

            if (item == null)
                throw new TokenStreamException(
                    "can't add a null item from '"+contents+
                    "' to container '"+container+"'");

            item.setDefinedIn(container);
            container.addContents(item);

            if ((debug & DEBUG_CONTAINER) != 0)
                System.out.println(
                    "[C] adding "+item.getIdentifier()+" to container "+
                    container.getIdentifier());
        }
    }

    /*
     * Set the base list for the given interface, after checking to make sure
     * the given bases actually exist and have the correct abstract / local
     * attributes.
     */
    private void checkSetBases(MInterfaceDef iface, List bases)
        throws TokenStreamException
    {
        String id = iface.getIdentifier();

        ArrayList verified = new ArrayList();
        for (Iterator it = bases.iterator(); it.hasNext(); ) {
            String inherit = (String) it.next();
            MContained lookup = lookupName(inherit, DEBUG_INTERFACE | DEBUG_INHERITANCE);

            if ((lookup == null) || (! (lookup instanceof MInterfaceDef)))
                throw new TokenStreamException("interface '"+id+"' can't inherit from undefined interface '"+inherit+"'");

            MInterfaceDef base = (MInterfaceDef) lookup;

            if (iface.isAbstract() != base.isAbstract())
                throw new TokenStreamException("interface '"+id+"' can't inherit from '"+inherit+"' because one interface is abstract");

            if ((! iface.isLocal()) && base.isLocal())
                throw new TokenStreamException("interface '"+id+"' can't inherit from '"+inherit+"' because '"+id+"' is not local");

            if ((debug & DEBUG_INTERFACE) != 0)
                System.out.println("[f] adding base '"+inherit+"' to interface '"+id+"'");

            verified.add(base);
        }

        iface.setBases(verified);
    }

    /*
     * Set the supported interface list for the given interface (actually has to
     * be a component or home, but they're both of type MInterfaceDef), after
     * checking to make sure the given supported interfaces actually exist.
     */
    private void checkSetSupports(MInterfaceDef iface, List supports)
        throws TokenStreamException
    {
        List slist = new ArrayList();
        for (Iterator it = supports.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            MContained lookup = lookupName(name, DEBUG_INTERFACE | DEBUG_INHERITANCE);
            if ((lookup == null) || (! (lookup instanceof MInterfaceDef))) {
                throw new TokenStreamException("interface '"+iface.getIdentifier()+"' can't support undefined interface '"+name+"'");
            } else {
                MSupportsDef support = new MSupportsDefImpl();
                support.setIdentifier("support_"+name);
                support.setSupports((MInterfaceDef) lookup);
                support.setDefinedIn(iface);
                if (iface instanceof MComponentDef) {
                    support.setComponent((MComponentDef) iface);
                } else if (iface instanceof MHomeDef) {
                    support.setHome((MHomeDef) iface);
                }
                slist.add(support);
            }
        }

        if (iface instanceof MComponentDef)
            ((MComponentDef) iface).setSupportss(slist);
        else if (iface instanceof MHomeDef)
            ((MHomeDef) iface).setSupportss(slist);
        else throw new RuntimeException(iface.getIdentifier()+" must be a component or home instance");
    }

    /*
     * Examine the given list of objects for repeated identifiers, and set them
     * to the member list appropriate for the type of the given box.
     */
    private void checkSetMembers(MContained box, List members)
        throws TokenStreamException
    {
        MExceptionDef exception = null;
        MStructDef struct = null;
        MUnionDef union = null;

        if (box instanceof MExceptionDef) exception = (MExceptionDef) box;
        else if (box instanceof MStructDef) struct = (MStructDef) box;
        else if (box instanceof MUnionDef) union = (MUnionDef) box;

        if ((struct == null) && (exception == null) && (union == null))
            throw new RuntimeException(box+" must be an exception, union, or struct");

        if ((members.size() == 0) && (exception == null))
            throw new TokenStreamException("container '"+box.getIdentifier()+"' has no members");

        // check if there are fields with same identifier.

        String outID = "";
        String inID = "";
        Iterator o = members.iterator();
        Iterator i = members.iterator();
        try {
            if (union != null) {
                MUnionFieldDef out, in;
                while (o.hasNext()) {
                    out = (MUnionFieldDef) o.next(); outID = out.getIdentifier();
                    while (i.hasNext()) {
                        in = (MUnionFieldDef) i.next(); inID = in.getIdentifier();
                        if (outID.equals(inID) && ! out.equals(in))
                            throw new RuntimeException();
                    }
                }
            } else {
                MFieldDef out, in;
                while (o.hasNext()) {
                    out = (MFieldDef) o.next(); outID = out.getIdentifier();
                    while (i.hasNext()) {
                        in = (MFieldDef) i.next(); inID = in.getIdentifier();
                        if (outID.equals(inID) && ! out.equals(in))
                            throw new RuntimeException();
                    }
                }
            }
        } catch (RuntimeException e) {
            throw new TokenStreamException("repeated identifier '"+outID+"' in '"+box.getIdentifier()+"'");
        }

        // add the members to the given box.
        Iterator it = members.iterator();
        if (union != null) {
            while (it.hasNext()) ((MUnionFieldDef) it.next()).setUnion(union);
            union.setUnionMembers(members);
        } else {
            while (it.hasNext()) {
                MFieldDef f = (MFieldDef) it.next();
                if (struct != null) f.setStructure(struct);
                else f.setException(exception);
            }
            if (struct != null) struct.setMembers(members);
            else exception.setMembers(members);
        }
    }

    /*
     * Set the parameter list for the given operation.
     */
    private void checkSetParameters(MOperationDef op, List params)
    {
        for (Iterator p = params.iterator(); p.hasNext(); )
            ((MParameterDef) p.next()).setOperation(op);
        op.setParameters(params);
    }

    /*
     * Set the exception list for the given operation. Check that the given
     * exceptions have been defined somewhere.
     */
    private void checkSetExceptions(MOperationDef op, List excepts)
        throws TokenStreamException
    {
        op.setExceptionDefs(new HashSet(excepts));
    }

    /*
     * Set the base(s) of this value def according to the name given.
     */
    private void addValueBase(MValueDef val, String name)
    {
        MValueDef inherited = (MValueDef)
            lookupName(name, DEBUG_VALUE | DEBUG_INHERITANCE);

        if ((inherited != null) && (inherited.getContentss() != null)) {
            if (inherited.isAbstract()) val.addAbstractBase(inherited);
            else val.setBase(inherited);
        }
    }

    /*
     * Set the supports information for the given value using the given name.
     */
    private void addValueSupports(MValueDef val, String name)
        throws TokenStreamException
    {
        String id = val.getIdentifier();
        MContained support =
            lookupName(name, DEBUG_VALUE | DEBUG_INTERFACE);

        if (support != null) {
            val.setInterfaceDef((MInterfaceDef) support);
            if ((debug & (DEBUG_INTERFACE | DEBUG_INHERITANCE)) != 0)
                System.out.println("[v] added support '"+name+"' to value '"+id+"'");
        } else {
            throw new TokenStreamException("value '"+id+"' can't inherit from undefined value '"+name+"'");
        }
    }

    /*
     * This function is a debug wrapper for the symbol table function of the
     * same name. It essentially just calls the symbolTable function, but if
     * needed first it prints stuff.
     */
    private MContained lookupName(String name, long level)
    {
        MContained result = null;

        if ((debug & level) != 0) {
            if ((debug & DEBUG_SYMBOL_TABLE) != 0)
                System.out.println(symbolTable.toString());
            System.out.println("[L] looking up '" + name + "'");
        }

        if (name.indexOf("::") > 0) {
            result = this.symbolTable.lookupScopedName(name);
        } else {
            String look = new String(name);
            if (look.startsWith("::"))
                look = look.substring(2, look.length());
            result = this.symbolTable.lookupNameInCurrentScope(look);
        }

        if ((debug & level) != 0) {
            if (result == null) System.out.println("[L] '"+name+"' not found");
            else System.out.println("[L] '"+name+"' ("+result+") found");
        }

        return result;
    }
}

/******************************************************************************/

// 1. <specification> ::= <import_dcl,100>* <definition,2>+
specification returns [MContainer container = null]
{
    container = new MContainerImpl();
    String imported = null;
    List decls = null;
    List definitions = new ArrayList();
    specification = container;
}
    :   ( import_dcl )*
        ( decls = definition { definitions.addAll(decls); } )+
        {
            checkAddContents(container, definitions);

            if ((debug & DEBUG_FILE) != 0)
                System.out.println("[f] input file "+
                    manager.getOriginalFile()+" parsed");

            if ((debug & DEBUG_SYMBOL_TABLE) != 0)
                System.out.println(symbolTable.toString());
        } ;

// 2. <definition> ::= <type_dcl,42> ";" <-- this rule returns a list
//    | <const_dcl,27> ";"
//    | <except_dcl,> ";"
//    | <iface,4> ";"
//    | <module,3> ";"
//    | <value,13> ";"
//    | <type_id_dcl,102> ";"     <-- this isn't implemented
//    | <type_prefix_dcl,103> ";" <-- this isn't implemented
//    | <event,134> ";"
//    | <component,112> ";"
//    | <home_dcl,126> ";"
//
definition returns [List definitions = null]
{ definitions = new ArrayList(); MContained holder = null; }
    :   definitions = type_dcl    SEMI
    |   holder = const_dcl        SEMI { definitions.add(holder); }
    |   holder = except_dcl       SEMI { definitions.add(holder); }
    |   (iface) => holder = iface SEMI { definitions.add(holder); }
    |   holder = module           SEMI { definitions.add(holder); }
    |   (value) => holder = value SEMI { definitions.add(holder); }
    |   type_id_dcl               SEMI
    |   type_prefix_dcl           SEMI
    |   holder = event            SEMI { definitions.add(holder); }
    |   holder = component        SEMI { definitions.add(holder); }
    |   holder = home_dcl         SEMI { definitions.add(holder); }
    ;

// 3. <module> ::= "module" <identifier> "{" <definition,2>+ "}"
module returns [MModuleDef mod = null]
{
    mod = new MModuleDefImpl();
    String id;
    List decls = null;
    List definitions = new ArrayList();
}
    :   "module" id = identifier
        {
            mod = (MModuleDef) verifyNameEmpty(id, mod);
            mod.setSourceFile(getSourceFile());
            mod.setIdentifier(id);
            symbolTable.add(id, mod);
        }
        LCURLY { symbolTable.pushScope(id); }
        ( decls = definition { definitions.addAll(decls); } )*
        { checkAddContents(mod, definitions); }
        RCURLY { symbolTable.popScope(); } ;

// (lmj) this was renamed to iface to prevent collisions with Java keywords
//
// 4. <iface> ::= <interface_dcl,5> | <forward_dcl,6>
//
// (lmj) had to rewrite this rule because the specification grammar isn't
// written for an ll parser. changed the rule for <interface_header,7>
// correspondingly.
//
// 4. <iface> ::= <forward_dcl,6> [ <interface_dcl,5> ]
iface returns [MInterfaceDef iface = null]
    :   iface = forward_dcl ( interface_dcl[iface] )? ;

// 5. <interface_dcl> ::= <interface_header,7> "{" <interface_body,8> "}"
interface_dcl[MInterfaceDef iface]
{ List exports = new ArrayList(); }
    :   interface_header[iface]
        LCURLY { symbolTable.pushScope(iface.getIdentifier()); }
        exports = interface_body { checkAddContents(iface, exports); }
        RCURLY { symbolTable.popScope(); } ;

// 6. <forward_dcl> ::= [ "abstract" | "local" ] "interface" <identifier>
forward_dcl returns [MInterfaceDef iface = null]
{ iface = new MInterfaceDefImpl(); String id = null; }
    :   (   "abstract" { iface.setAbstract(true); }
        |   "local"    { iface.setLocal(true); } )?
        "interface" id = identifier
        {
            iface = (MInterfaceDef) verifyNameEmpty(id, iface);
            iface.setSourceFile(getSourceFile());
            iface.setIdentifier(id);
            iface.setRepositoryId(createRepositoryId(id));
            symbolTable.add(id, iface);
        } ;

// 7. <interface_header> ::= [ "abstract" | "local" ] "interface" <identifier>
//      [ <interface_inheritance_spec,10> ]
//
// (lmj) had to rewrite this rule because the specification grammar isn't
// written for an ll parser. see also rule 4.
//
// 7. <interface_header> ::= [ <interface_inheritance_spec,10> ]
interface_header[MInterfaceDef iface]
    :   ( interface_inheritance_spec[iface] )? ;

// 8. <interface_body> ::= <export,9>*
interface_body returns [List exports = null]
{ exports = new ArrayList(); List decls = null; }
    :   ( decls = export { exports.addAll(decls); } )* ;

// 9. <export> ::=
//      <type_dcl,42> ";"   <-- this rule can return a list of declarations
//    | <const_dcl,27> ";"
//    | <except_dcl,86> ";"
//    | <attr_dcl,85> ";"   <-- this rule can return a list too
//    | <op_dcl,87> ";"
//    | <type_id_dcl,102> ";"
//    | <type_prefix_dcl,103> ";"
export returns [List exports = null]
{ exports = new ArrayList(); MContained holder = null; }
    :   exports = type_dcl   SEMI
    |   holder  = const_dcl  SEMI { exports.add(holder); }
    |   holder  = except_dcl SEMI { exports.add(holder); }
    |   exports = attr_dcl   SEMI
    |   holder  = op_dcl     SEMI { exports.add(holder); }
    |   type_id_dcl          SEMI
    |   type_prefix_dcl      SEMI
    ;

// 10. <interface_inheritance_spec> ::= ":" <interface_name,11>
//       { "," <interface_name,11> }*
interface_inheritance_spec[MInterfaceDef iface]
{ List inherits = new ArrayList(); String name = null; }
    :   COLON name = interface_name { inherits.add(name); }
        ( COMMA name = interface_name { inherits.add(name); } )*
        { checkSetBases(iface, inherits); } ;

// 11. <interface_name> ::= <scoped_name,12>
interface_name returns [String name = null] : name = scoped_name ;

// 12. <scoped_name> ::= <identifier>
//     | "::" <identifier>
//     | <scoped_name,12> "::" <identifier>
//
// (lmj) had to rewrite this rule because antlr is an ll parser tool.
//
// 12. <scoped_name> ::= ( "::" )? <identifier> ( "::" <identifier> )*
scoped_name returns [String name = null]
{ String id = null; String global = new String(""); }
    :   ( s:SCOPEOP { global = s.getText(); } )?
        id = identifier { name = global + id; }
        ( c:SCOPEOP id = identifier { name += c.getText() + id; } )* ;

// 13. <value> ::=
//       <value_dcl,17>
//     | <value_abs_dcl,16>
//     | <value_box_dcl,15>
//     | <value_forward_dcl,14>
value returns [MContained val = null]
    :   ( value_abs_dcl ) =>val = value_abs_dcl
    |   ( value_dcl     ) =>val = value_dcl
    |   ( value_box_dcl ) =>val = value_box_dcl
    |   val = value_forward_dcl
    ;

// 14. <value_forward_dcl> ::= [ "abstract" ] "valuetype" <identifier>
value_forward_dcl returns [MValueDef val = null]
{ val = new MValueDefImpl(); String id = null; }
    :   ( "abstract" { val.setAbstract(true); } )? "valuetype" id = identifier
        {
            val = (MValueDef) verifyNameEmpty(id, val);
            val.setSourceFile(getSourceFile());
            val.setIdentifier(id);
            symbolTable.add(id, val);
        } ;

// 15. <value_box_dcl> ::= "valuetype"  <identifier> <type_spec,44>
value_box_dcl returns [MValueBoxDef valbox = null]
{
    valbox = new MValueBoxDefImpl();
    String id = null;
    MIDLType type = null;
}
    :   "valuetype" id = identifier type = type_spec
        {
            valbox = (MValueBoxDef) verifyNameEmpty(id, valbox);
            valbox.setSourceFile(getSourceFile());
            valbox.setIdentifier(id);
            valbox.setIdlType(type);
        } ;

// 16. <value_abs_dcl> ::= "abstract" "valuetype" <identifier>
//       [ <value_inheritance_spec,19> ] "{" <export,9>* "}"
//
// (lmj) had to change original rule because all of <value_inheritance_spec,19>
// is optional.
//
// 16. <value_abs_dcl> ::= "abstract" "valuetype" <identifier>
//       <value_inheritance_spec,19> "{" <export,9>* "}"
value_abs_dcl returns [MValueDef val = null]
{
    val = new MValueDefImpl();
    String id = null;
    List decls = null;
    List exports = new ArrayList();
}
    :   "abstract" { val.setAbstract(true); } "valuetype" id = identifier
        {
            val = (MValueDef) verifyNameEmpty(id, val);
            val.setSourceFile(getSourceFile());
            val.setIdentifier(id);
            symbolTable.add(id, val);
        }
        value_inheritance_spec[val]
        LCURLY { symbolTable.pushScope(id); }
        ( decls = export { exports.addAll(decls); } )*
        { checkAddContents(val, exports); }
        RCURLY { symbolTable.popScope(); } ;

// 17. <value_dcl> ::= <value_header,18> "{"  < value_element,21>* "}"
value_dcl returns [MValueDef val = null]
{ List elements = new ArrayList(); List decls = null; }
    :   val = value_header
        LCURLY { symbolTable.pushScope(val.getIdentifier()); }
        ( decls = value_element { elements.addAll(decls); } )*
        { checkAddContents(val, elements); }
        RCURLY { symbolTable.popScope(); } ;

// 18. <value_header> ::= ["custom" ] "valuetype" <identifier>
//       [ <value_inheritance_spec,19> ]
//
// (lmj) had to change rule because all of <value_inheritance_spec,19> is
// optional.
//
// 18. <value_header> ::= ["custom" ] "valuetype" <identifier>
//       <value_inheritance_spec,19>
value_header returns [MValueDef val = null]
{ val = new MValueDefImpl(); String id = null; }
    :   ( "custom" { val.setCustom(true); } )? "valuetype" id = identifier
        {
            val = (MValueDef) verifyNameEmpty(id, val);
            val.setSourceFile(getSourceFile());
            val.setIdentifier(id);
            symbolTable.add(id, val);
        }
        value_inheritance_spec[val] ;

// 19. <value_inheritance_spec> ::=
//       [ ":" [ "truncatable" ] <value_name,20> { "," <value_name,20> }* ]
//       [ "supports" <interface_name,11> { "," <interface_name,11> }* ]
value_inheritance_spec[MValueDef val]
{ String name = null; MValueDef inherited = null; }
    :   (   COLON ( "truncatable" { val.setTruncatable(true); } )?
            name = value_name { addValueBase(val, name); }
            ( COMMA name = value_name { addValueBase(val, name); } )*
        )?
        (   "supports" name = interface_name { addValueSupports(val, name); }
            ( COMMA    name = interface_name { addValueSupports(val, name); } )*
        )? ;

// 20. <value_name> ::= <scoped_name,12>
value_name returns [String name = null] : name = scoped_name ;

// 21. <value_element> ::= <export,9> | < state_member,22> | <init_dcl,23>
value_element returns [List elements = null]
{ elements = new ArrayList(); MContained holder = null; }
    :   elements = export
    |   elements = state_member
    |   holder = init_dcl { elements.add(holder); }
    ;

// 22. <state_member> ::= ( "public" | "private" ) <type_spec,44>
//       <declarators,49> ";"
state_member returns [List members = null]
{
    members = new ArrayList();
    List decls = null;
    MIDLType type = null;
    boolean isPublic = false;
}
    :   ( "public" { isPublic = true; } | "private" )
        type = type_spec decls = declarators
        {
            for (Iterator it = decls.iterator(); it.hasNext(); ) {
                DeclaratorHelper declarator = (DeclaratorHelper) it.next();
                String id = declarator.getDeclarator();

                if(declarator.isArray()) {
                    MArrayDef array = declarator.getArray();
                    array.setIdlType(type);
                    type = (MIDLType) array;
                }

                MValueMemberDef member = new MValueMemberDefImpl();
                member.setIdlType(type);
                member.setIdentifier(id);
                member.setPublicMember(isPublic);
                members.add(member);
            }
        }
        SEMI ;

// 23. <init_dcl> ::= "factory" <identifier>
//       "("  [ <init_param_decls,24> ]  ")" [ <raises_expr,93> ] ";"
init_dcl returns [MFactoryDef factory = null]
{
    factory = new MFactoryDefImpl();
    String id;
    List params = null;
    List excepts = null;
}
    :   "factory" id = identifier
        { factory.setIdentifier(id); symbolTable.add(id, factory); }
        LPAREN ( params = init_param_decls { checkSetParameters((MOperationDef) factory, params); } )?
        RPAREN ( excepts = raises_expr { checkSetExceptions((MOperationDef) factory, excepts); } )?
        SEMI ;

// 24. <init_param_decls> ::= <init_param_decl,25> { "," <init_param_decl,24> }*
init_param_decls returns [List params = null]
{ params = new ArrayList(); MParameterDef parameter; }
    :   parameter = init_param_decl { params.add(parameter); }
        ( COMMA parameter = init_param_decl { params.add(parameter); } )* ;

// 25. <init_param_decl> ::= <init_param_attribute,26> <param_type_spec,95>
//       <simple_declarator,51>
init_param_decl returns [MParameterDef param = null]
{
    param = new MParameterDefImpl();
    MIDLType type = null;
    String name = null;
}
    :   init_param_attribute { param.setDirection(MParameterMode.PARAM_IN); }
        type = param_type_spec { param.setIdlType(type); }
        name = simple_declarator { param.setIdentifier(name); } ;

// 26. <init_param_attribute> ::= "in"
init_param_attribute : "in" ;

// 27. <const_dcl> ::= "const" <const_type,28> <identifier> "=" <const_exp,29>
const_dcl returns [MConstantDef constant = null]
{
    constant = new MConstantDefImpl();
    String id = null;
    MIDLType type = null;
    String expr = null;
}
    :   "const" type = const_type { constant.setIdlType(type); } id = identifier
        {
            constant = (MConstantDef) verifyNameEmpty(id, constant);
            constant.setSourceFile(getSourceFile());
            constant.setIdentifier(id);
            symbolTable.add(id, constant);
        }
        ASSIGN expr = const_exp
        {
            if (type instanceof MPrimitiveDef) {
                MPrimitiveDef ptype = (MPrimitiveDef) type;
                if (ptype.getKind() == MPrimitiveKind.PK_LONG) {
                    constant.setConstValue(new Integer(expr));
                } else if (ptype.getKind() == MPrimitiveKind.PK_SHORT) {
                    constant.setConstValue(new Integer(expr));
                } else if (ptype.getKind() == MPrimitiveKind.PK_ULONG) {
                    constant.setConstValue(new Integer(expr));
                } else if(ptype.getKind() == MPrimitiveKind.PK_USHORT) {
                    constant.setConstValue(new Integer(expr));
                } else if (ptype.getKind() == MPrimitiveKind.PK_BOOLEAN) {
                    constant.setConstValue(new Boolean(expr));
                } else if (ptype.getKind() == MPrimitiveKind.PK_LONG) {
                    constant.setConstValue(new Long(expr));
                } else if (ptype.getKind() == MPrimitiveKind.PK_LONGLONG) {
                    constant.setConstValue(new Long(expr));
                } else if (ptype.getKind() == MPrimitiveKind.PK_ULONGLONG) {
                    constant.setConstValue(new Long(expr));
                } else if (ptype.getKind() == MPrimitiveKind.PK_CHAR) {
                    constant.setConstValue(expr);
                }
            } else if (type instanceof MStringDef) {
                constant.setConstValue(expr);
            } else if (type instanceof MWstringDef) {
                constant.setConstValue(expr);
            }
        } ;

// 28. <const_type> ::= <integer_type,54>
//     | <char_type,63>
//     | <wide_char_type,64>
//     | <boolean_type,65>
//     | <floating_pt_type,53>
//     | <string_type,81>
//     | <wide_string_type,82>
//     | <fixed_pt_const_type,97>
//     | <scoped_name,12>
//     | <octet_type,66>
const_type returns [MIDLType type = null]
{ String name = null; }
    :   ( integer_type ) => type = integer_type
    |   type = char_type
    |   type = wide_char_type
    |   type = boolean_type
    |   ( floating_pt_type ) => type = floating_pt_type
    |   type = string_type
    |   type = wide_string_type
    |   ( fixed_pt_const_type ) => type = fixed_pt_const_type
    |   name = scoped_name { type = getIDLType(name); }
    |   type = octet_type
    ;

// 29. <const_exp> ::= <or_expr,30>
const_exp returns [String expr = null] : expr = or_expr ;

// 30. <or_expr> ::= <xor_expr,31> | <or_expr,30> "|" <xor_expr,31>
//
// (lmj) have to rewrite this rule because antlr is an ll parser generator.
//
// 30. <or_expr> ::= <xor_expr,31> [ "|" <or_expr,30> ]
or_expr returns [String expr = null]
{ String right = null; }
    :   expr = xor_expr
        (
            OR right = or_expr
            {
                Long a = checkLong(expr); Long b = checkLong(right);
                long result = a.longValue() | b.longValue();
                expr = "" + result;
            }
        )? ;

// 31. <xor_expr> ::= <and_expr,32> | <xor_expr,31> "^" <and_expr,32>
//
// (lmj) have to rewrite this rule because antlr is an ll parser generator.
//
// 31. <xor_expr> ::= <and_expr,32> [ "^" <xor_expr,31> ]
xor_expr returns [String expr = null]
{ String right = null; }
    :   expr = and_expr
        (
            XOR right = xor_expr
            {
                Long a = checkLong(expr); Long b = checkLong(right);
                long result = a.longValue() ^ b.longValue();
                expr = "" + result;
            }
        )? ;

// 32. <and_expr> ::= <shift_expr,33> | <and_expr,32> "&" <shift_expr,33>
//
// (lmj) have to rewrite this rule because antlr is an ll parser generator.
//
// 32. <and_expr> ::= <shift_expr,33> [ "&" <and_expr,32> ]
and_expr returns [String expr = null]
{ String right = null; }
    :   expr = shift_expr
        (
            AND right = and_expr
            {
                Long a = checkLong(expr); Long b = checkLong(right);
                long result = a.longValue() & b.longValue();
                expr = "" + result;
            }
        )? ;

// 33. <shift_expr> ::= <add_expr,34>
//     | <shift_expr,33> ">>" <add_expr,34>
//     | <shift_expr,33> "<<" <add_expr,34>
//
// (lmj) have to rewrite this rule because antlr is an ll parser generator.
//
// 33. <shift_expr> ::= <add_expr,34> [ ( ">>" | "<<" ) <shift_expr,33> ]
shift_expr returns [String expr = null]
{ String right = null; boolean goLeft = false; }
    :   expr = add_expr
        (
            ( LSHIFT { goLeft = true; } | RSHIFT ) right = shift_expr
            {
                Long a = checkLong(expr); Long b = checkLong(right);
                long result = a.longValue() >> b.longValue();
                if (goLeft) { result = a.longValue() << b.longValue(); }
                expr = "" + result;
            }
        )? ;

// 34. <add_expr> ::= <mult_expr,35>
//     | <add_expr,34> "+" <mult_expr,35>
//     | <add_expr,34> "-" <mult_expr,35>
//
// (lmj) have to rewrite this rule because antlr is an ll parser generator.
//
// 34. <add_expr> ::= <mult_expr,35> [ ( "+" | "-" ) <add_expr,34> ]
add_expr returns [String expr = null]
{ String right = null; boolean add = false; }
    :   expr = mult_expr
        (
            ( PLUS { add = true; } | MINUS ) right = add_expr
            {
                Number a, b, result;
                // try to evaluate the expression as an integer first, if that
                // fails (because the numbers are floats) then try using float
                // math instead.
                try {
                    a = new Long(expr); b = new Long(right);
                    result = new Long(a.longValue() - b.longValue());
                    if (add) { result = new Long(a.longValue() + b.longValue()); }
                } catch (NumberFormatException e) {
                    a = checkFloat(expr); b = checkFloat(right);
                    result = new Float(a.floatValue() - b.floatValue());
                    if (add) { result = new Float(a.floatValue() + b.floatValue()); }
                }
                expr = result.toString();
            }
        )? ;

// 35. <mult_expr> ::= <unary_expr,36>
//     | <mult_expr,35> "*" <unary_expr,36>
//     | <mult_expr,35> "/" <unary_expr,36>
//     | <mult_expr,35> "%" <unary_expr,36>
//
// (lmj) have to rewrite this rule because antlr is an ll parser generator.
//
// 35. <mult_expr> ::= <unary_expr,36> [ ( "*" | "/" | "%" ) <mult_expr,35> ]
mult_expr returns [String expr = null]
{ String right = null; int op = 0; }
    :   expr = unary_expr
        (
            ( MOD | STAR { op = 1; } | DIV { op = 2; } ) right = mult_expr
            {
                if (op == 0) {
                    Long a = checkLong(expr); Long b = checkLong(right);
                    long result = a.longValue() % b.longValue();
                    expr = "" + result;
                } else {
                    // try to evaluate the expression as an integer first. if
                    // that fails, try using float math instead.
                    try {
                        Long a = new Long(expr); Long b = new Long(right);
                        long result = a.longValue() * b.longValue();
                        if (op == 2) { result = a.longValue() / b.longValue(); }
                        expr = "" + result;
                    } catch (NumberFormatException e) {
                        Float a = checkFloat(expr); Float b = checkFloat(right);
                        float result = a.floatValue() * b.floatValue();
                        if (op == 2) { result = a.floatValue() / b.floatValue(); }
                        expr = "" + result;
                    }
                }
            }
        )? ;

// 36. <unary_expr> ::= <unary_operator,37> <primary_expr,38>
//     | <primary_expr,38>
unary_expr returns [String expr = null]
{ String op = null; }
    :   op = unary_operator expr = primary_expr
        {
            if (op.equals("~")) {
                Long a = checkLong(expr);
                long result = ~ a.longValue();
                expr = "" + result;
            } else {
                expr = op + expr;
            }
        }
    |   expr = primary_expr
    ;

// 37. <unary_operator> ::= "-" | "+" | "~"
unary_operator returns [String expr = null]
    :   m:MINUS { expr = m.getText(); }
    |   p:PLUS  { expr = p.getText(); }
    |   t:TILDE { expr = t.getText(); }
    ;

// 38. <primary_expr> ::= <scoped_name,12>
//     | <literal,39>
//     | "(" <const_exp,29> ")"
primary_expr returns [String expr = null]
    :   expr = scoped_name
        {
            MConstantDef constant = new MConstantDefImpl();
            constant = (MConstantDef) verifyNameExists(expr, constant);
            expr = constant.getConstValue().toString();
        }
    |   expr = literal
    |   LPAREN expr = const_exp RPAREN
    ;

// 39. <literal> ::= <integer_literal>
//     | <string_literal>
//     | <wide_string_literal>
//     | <character_literal>
//     | <wide_character_literal>
//     | <fixed_pt_literal>
//     | <floating_pt_literal>
//     | <boolean_literal,40>
literal returns [String literal = null]
    :   ( integer_literal ) => literal = integer_literal
    |   literal = string_literal
    |   literal = wide_string_literal
    |   literal = character_literal
    |   literal = wide_character_literal
    |   ( fixed_pt_literal ) => literal = fixed_pt_literal
    |   ( floating_pt_literal ) => literal = floating_pt_literal
    |   literal = boolean_literal
    ;

// 40. <boolean_literal> ::= "TRUE" | "FALSE"
boolean_literal returns [String bool = null]
    :   "TRUE"  { bool = new String("TRUE");  }
    |   "FALSE" { bool = new String("FALSE"); }
    ;

// 41. <positive_int_const> ::= <const_exp,29>
positive_int_const returns [String expr = null]
    :   expr = const_exp { checkPositive(expr); } ;

// 42. <type_dcl> ::= "typedef" <type_declarator,43>    <-- this returns a list
//     | <struct_type,69>
//     | <union_type,72>
//     | <enum_type,78>
//     | "native" <simple_declarator,51>  <-- this is part of IDL ?
//     | <constr_forward_decl,99>
type_dcl returns [List decls = null]
{
    decls = new ArrayList();
    MIDLType type = null;
    String id = null;
}
    :   "typedef" decls = type_declarator
    |   ( struct_type ) => type = struct_type { decls.add(type); }
    |   ( union_type  ) => type = union_type  { decls.add(type); }
    |   type = enum_type                      { decls.add(type); }
    |   "native" id = simple_declarator       // FIXME : what is this ?
    |   ( constr_forward_decl SEMI ) => type = constr_forward_decl
        { decls.add(type); }
    ;

// 43. <type_declarator> ::= <type_spec,44> <declarators,49>
type_declarator returns [List declarations = null]
{
    declarations = new ArrayList();
    MIDLType type = null;
    List decls = null;
}
    :   type = type_spec decls = declarators
        {
            for (Iterator it = decls.iterator(); it.hasNext(); ) {
                DeclaratorHelper declarator = (DeclaratorHelper) it.next();
                String id = declarator.getDeclarator();

                if (declarator.isArray()) {
                    MArrayDef array = declarator.getArray();
                    array.setIdlType(type);
                    type = (MIDLType) array;
                }

                MAliasDef alias = new MAliasDefImpl();
                alias.setIdlType(type);
                alias.setIdentifier(id);
                alias.setSourceFile(getSourceFile());
                declarations.add(alias);

                symbolTable.add(id, alias);
            }
        } ;

// 44. <type_spec> ::= <simple_type_spec,45> | <constr_type_spec,48>
type_spec returns [MIDLType type = null]
    :   type = simple_type_spec | type = constr_type_spec ;

// 45. <simple_type_spec> ::= <base_type_spec,46>
//     | <template_type_spec,47>
//     | <scoped_name,12>
simple_type_spec returns [MIDLType type = null]
{ String name = null; }
    :   type = base_type_spec
    |   type = template_type_spec
    |   name = scoped_name { type = getIDLType(name); }
    ;

// 46. <base_type_spec> ::= <floating_pt_type,53>
//     | <integer_type,54>
//     | <char_type,63>
//     | <wide_char_type,64>
//     | <boolean_type,65>
//     | <octet_type,66>
//     | <any_type,67>
//     | <object_type,68>
//     | <value_base_type,98>
base_type_spec returns [MIDLType type = null]
    :   ( floating_pt_type ) => type = floating_pt_type
    |   ( integer_type ) => type = integer_type
    |   type = char_type
    |   type = wide_char_type
    |   type = boolean_type
    |   type = octet_type
    |   type = any_type
    |   type = object_type
    |   type = value_base_type
    ;

// 47. <template_type_spec> ::= <sequence_type,80>
//     | <string_type,81>
//     | <wide_string_type,82>
//     | <fixed_pt_type,96>
template_type_spec returns [MIDLType type = null]
    :   type = sequence_type
    |   type = string_type
    |   type = wide_string_type
    |   type = fixed_pt_type
    ;

// 48. <constr_type_spec> ::= <struct_type,69>
//     | <union_type,72>
//     | <enum_type,78>
constr_type_spec returns [MIDLType type = null]
    : type = struct_type | type = union_type | type = enum_type ;

// 49. <declarators> ::= <declarator,50> { "," <declarator,50> }*
declarators returns [List declarations = null]
{ declarations = new ArrayList(); DeclaratorHelper helper; }
    :   helper = declarator { declarations.add(helper); }
        ( COMMA helper = declarator { declarations.add(helper); } )* ;

// 50. <declarator> ::= <simple_declarator,51> | <complex_declarator,52>
declarator returns [DeclaratorHelper helper = null]
{ helper = new DeclaratorHelper(); String name; }
    :   ( simple_declarator ) => name = simple_declarator
        { helper.setDeclarator(name); }
    |   ( complex_declarator ) => helper = complex_declarator
    ;

// 51. <simple_declarator> ::= <identifier>
simple_declarator returns [String name = null] : name = identifier ;

// 52. <complex_declarator> ::= <array_declarator,83>
complex_declarator returns [DeclaratorHelper helper = null]
    :   helper = array_declarator ;

// 53. <floating_pt_type> ::= "float" | "double" | "long double"
floating_pt_type returns [MIDLType number = null]
{ number = new MPrimitiveDefImpl(); }
    :   "float"  { ((MPrimitiveDef) number).setKind(MPrimitiveKind.PK_FLOAT);  }
    |   "double" { ((MPrimitiveDef) number).setKind(MPrimitiveKind.PK_DOUBLE); }
    |   ( "long" "double" ) => "long" "double"
        { ((MPrimitiveDef) number).setKind(MPrimitiveKind.PK_LONGDOUBLE); }
    ;

// 54. <integer_type> ::= <signed_int,55> | <unsigned_int,59>
integer_type returns [MIDLType number = null]
{ number = new MPrimitiveDefImpl(); }
    :   number = signed_int | number = unsigned_int ;

// 55. <signed_int> ::= <signed_short_int,56>
//     | <signed_long_int,57>
//     | <signed_longlong_int,58>
signed_int returns [MPrimitiveDef number = null]
    :   ( signed_short_int    ) => number = signed_short_int
    |   ( signed_long_int     ) => number = signed_long_int
    |   ( signed_longlong_int ) => number = signed_longlong_int
    ;

// 56. <signed_short_int> ::= "short"
signed_short_int returns [MPrimitiveDef number = null]
{ number = new MPrimitiveDefImpl(); }
    :   "short" { ((MPrimitiveDef) number).setKind(MPrimitiveKind.PK_SHORT); } ;

// 57. <signed_long_int> ::= "long"
signed_long_int returns [MPrimitiveDef number = null]
{ number = new MPrimitiveDefImpl(); }
    :   "long" { ((MPrimitiveDef) number).setKind(MPrimitiveKind.PK_LONG); } ;

// 58. <signed_longlong_int> ::= "long" "long"
signed_longlong_int returns [MPrimitiveDef number = null]
{ number = new MPrimitiveDefImpl(); }
    :   "long" "long"
        { ((MPrimitiveDef) number).setKind(MPrimitiveKind.PK_LONGLONG); } ;

// 59. <unsigned_int> ::= <unsigned_short_int,60>
//     | <signed_long_int,61>
//     | <signed_longlong_int,62>
unsigned_int returns [MPrimitiveDef number = null]
    :   ( unsigned_short_int    ) => number = unsigned_short_int
    |   ( unsigned_long_int     ) => number = unsigned_long_int
    |   ( unsigned_longlong_int ) => number = unsigned_longlong_int
    ;

// 60. <unsigned_short_int> ::= "unsigned" "short"
unsigned_short_int returns [MPrimitiveDef number = null]
{ number = new MPrimitiveDefImpl(); }
    :   "unsigned" "short"
        { ((MPrimitiveDef) number).setKind(MPrimitiveKind.PK_USHORT); } ;

// 61. <unsigned_long_int> ::= "unsigned" "long"
unsigned_long_int returns [MPrimitiveDef number = null]
{ number = new MPrimitiveDefImpl(); }
    :   "unsigned" "long"
        { ((MPrimitiveDef) number).setKind(MPrimitiveKind.PK_ULONG); } ;

// 62. <unsigned_longlong_int> ::= "unsigned" "long" "long"
unsigned_longlong_int returns [MPrimitiveDef number = null]
{ number = new MPrimitiveDefImpl(); }
    :   "unsigned" "long" "long"
        { ((MPrimitiveDef) number).setKind(MPrimitiveKind.PK_ULONGLONG); } ;

// 63. <char_type> ::= "char"
char_type returns [MIDLType ch = null]
{ ch = new MPrimitiveDefImpl(); }
    :   "char" { ((MPrimitiveDef) ch).setKind(MPrimitiveKind.PK_CHAR); } ;

// 64. <wide_char_type> ::= "wchar"
wide_char_type returns [MIDLType wch = null]
{ wch = new MPrimitiveDefImpl(); }
    :   "wchar" { ((MPrimitiveDef) wch).setKind(MPrimitiveKind.PK_WCHAR); } ;

// 65. <boolean_type> ::= "boolean"
boolean_type returns [MIDLType bool = null]
{ bool = new MPrimitiveDefImpl(); }
    :   "boolean" { ((MPrimitiveDef) bool).setKind(MPrimitiveKind.PK_BOOLEAN); } ;

// 66. <octet_type> ::= "octet"
octet_type returns [MIDLType octet = null]
{ octet = new MPrimitiveDefImpl(); }
    :   "octet" { ((MPrimitiveDef) octet).setKind(MPrimitiveKind.PK_OCTET); } ;

// 67. <any_type> ::= "any"
any_type returns [MIDLType any = null]
{ any = new MPrimitiveDefImpl(); }
    :   "any" { ((MPrimitiveDef) any).setKind(MPrimitiveKind.PK_ANY); } ;

// 68. <object_type> ::= "Object"
object_type returns [MIDLType object = null]
{ object = new MPrimitiveDefImpl(); }
    :   "object" { ((MPrimitiveDef) object).setKind(MPrimitiveKind.PK_OBJREF); } ;

// 69. <struct_type> ::= "struct" <identifier> "{" <member_list,70> "}"
struct_type returns [MIDLType struct = null]
{
    struct = new MStructDefImpl();
    String id = null;
    List members = null;
}
    :   "struct" id = identifier
        {
            struct = (MStructDef) verifyNameEmpty(id, (MStructDef) struct);
            ((MStructDef) struct).setSourceFile(getSourceFile());
            ((MStructDef) struct).setIdentifier(id);
            symbolTable.add(id, (MStructDef) struct);
        }
        LCURLY members = member_list RCURLY
        { checkSetMembers((MStructDef) struct, members); } ;

// 70. <member_list> ::= <member,71>+
member_list returns [List members = null]
{ members = new ArrayList(); List decls = null; }
    :   ( decls = member { members.addAll(decls); } )+ ;

// 71. <member> ::= <type_spec,44> <declarators,49> ";"
member returns [List members = null]
{
    members = new ArrayList();
    List decls = null;
    MIDLType type = null;
}
    :   type = type_spec decls = declarators
        {
            for (Iterator it = decls.iterator(); it.hasNext(); ) {
                DeclaratorHelper declarator = (DeclaratorHelper) it.next();
                String id = declarator.getDeclarator();

                if (declarator.isArray()) {
                    MArrayDef array = declarator.getArray();
                    array.setIdlType(type);
                    type = (MIDLType) array;
                }

                MFieldDef field = new MFieldDefImpl();
                field.setIdentifier(id);
                field.setIdlType(type);
                members.add(field);
            }
        }
        SEMI ;

// 72. <union_type> ::= "union" <identifier> "switch"
//       "(" <switch_type_spec,73> ")" "{" <switch_body,74> "}"
union_type returns [MIDLType union = null]
{
    union = new MUnionDefImpl();
    String id = null;
    MIDLType type = null;
    List members = null;
}
    :   "union" id = identifier
        {
            union = (MUnionDef) verifyNameEmpty(id, (MUnionDef) union);
            ((MUnionDef) union).setSourceFile(getSourceFile());
            ((MUnionDef) union).setIdentifier(id);
            symbolTable.add(id, (MUnionDef) union);
        }
        "switch" LPAREN type = switch_type_spec RPAREN
        { ((MUnionDef) union).setDiscriminatorType(type); }
        LCURLY members = switch_body[type] RCURLY
        { checkSetMembers((MUnionDef) union, members); } ;

// 73. <switch_type_spec> ::= <integer_type,54>
//     | <char_type,63>
//     | <wide_char_type,64> <-- (lmj) i added this rule, seemed to belong
//     | <boolean_type,65>
//     | <enum_type,78>
//     | <scoped_name,12>
switch_type_spec returns [MIDLType type = null]
{ String name = null; }
    :   type = integer_type
    |   type = char_type
    |   type = wide_char_type
    |   type = boolean_type
    |   type = enum_type
    |   name = scoped_name { type = getIDLType(name); }
    ;

// 74. <switch_body> ::= <case_dcl,75>+
switch_body[MIDLType switchType] returns [List members = null]
{ members = new ArrayList(); List decls = null; }
    :   ( decls = case_dcl[switchType] { members.addAll(decls); } )+ ;

// (lmj) renamed this to case_dcl to avoid colliding with the java keyword
//
// 75. <case_dcl> ::= <case_label,76>+ <element_spec,77> ";"
case_dcl[MIDLType switchType] returns [List members = null]
{ String label = null; List labels = null; }
    :   (   label = case_label
            {
                if (label.equals("")) {
                    // a zero length string indicates the 'default' label
                    labels.add(label);
                } else if (switchType instanceof MPrimitiveDef) {
                    MPrimitiveDef ptype = (MPrimitiveDef) switchType;
                    if (ptype.getKind() == MPrimitiveKind.PK_BOOLEAN) {
                        labels.add(new Boolean(label));
                    } else {
                        if (label.charAt(0) == '-') {
                            if ((ptype.getKind() == MPrimitiveKind.PK_SHORT) ||
                                (ptype.getKind() == MPrimitiveKind.PK_LONG)  ||
                                (ptype.getKind() == MPrimitiveKind.PK_LONGLONG)) {
                                labels.add(new Long(label));
                            } else if (ptype.getKind() == MPrimitiveKind.PK_USHORT) {
                                labels.add(new Long(Long.parseLong(label) + (1<<16)));
                            } else if (ptype.getKind() == MPrimitiveKind.PK_ULONG) {
                                labels.add(new Long(Long.parseLong(label) + (1<<32)));
                            } else if (ptype.getKind() == MPrimitiveKind.PK_ULONGLONG) {
                                labels.add(new Long(Long.parseLong(label) + (1<<64)));
                            } else {
                                throw new TokenStreamException("invalid primitive type for union label '" + label + "'");
                            }
                        } else {
                            labels.add(new Long(label));
                        }
                        labels.add(new Short(label));
                    }
                } else if ((switchType instanceof MStringDef) ||
                           (switchType instanceof MWstringDef)) {
                    if (label.length() == 1) {
                        labels.add(new String(label));
                    } else {
                        throw new TokenStreamException("union label '" + label + "' has more than one character");
                    }
                } else if (switchType instanceof MEnumDef) {
                    MEnumDef enm = (MEnumDef) switchType;
                    if (enm.getMembers().contains(label)) {
                        labels.add(label);
                    } else {
                        throw new TokenStreamException("union label '" + label + "' is not a valid member of enum " + enm.getIdentifier());
                    }
                }
            }
        )+ members = element_spec[labels] SEMI ;

// 76. <case_label> ::= "case" <const_exp,29> ":" | "default" ":"
case_label returns [String label = null]
    :   "case" label = const_exp COLON
    |   "default" { label = new String(""); } COLON
    ;

// 77. <element_spec> ::= <type_spec,44> <declarator,50>
element_spec[List labels] returns [List fields = null]
{
    fields = new ArrayList();
    DeclaratorHelper helper = null;
    MIDLType type = null;
}
    :   type = type_spec helper = declarator
        {
            String id = helper.getDeclarator();

            if (helper.isArray()) {
                MArrayDef array = helper.getArray();
                array.setIdlType(type);
                type = array;
            }

            for (Iterator l = labels.iterator(); l.hasNext(); ) {
                Object case_label = l.next();

                for (Iterator f = fields.iterator(); f.hasNext(); ) {
                    MUnionFieldDef field = (MUnionFieldDef) f.next();
                    Object field_label = field.getLabel();
                    if (case_label.equals(field_label)) {
                        if (case_label.toString().equals("")) {
                            throw new TokenStreamException("unions cannot have multiple 'default' labels");
                        } else {
                            throw new TokenStreamException("case label '"+case_label+ "' was used more than once in one union");
                        }
                    }
                }

                MUnionFieldDef field = new MUnionFieldDefImpl();
                field.setIdentifier(id);
                field.setIdlType(type);
                field.setLabel(case_label);
                fields.add(field);
            }
        } ;

// 78. <enum_type> ::= "enum" <identifier>
//       "{" <enumerator,79> { "," <enumerator,79> }* "}"
enum_type returns [MIDLType enm = null]
{
    enm = new MEnumDefImpl();
    List members = new ArrayList();
    String name = null;
    String id = null;
}
    :   "enum" id = identifier
        {
            enm = (MEnumDef) verifyNameEmpty(id, (MEnumDef) enm);
            ((MEnumDef) enm).setSourceFile(getSourceFile());
            ((MEnumDef) enm).setIdentifier(id);
            symbolTable.add(id, (MEnumDef) enm);
        }
        LCURLY name = enumerator
        { if (name != null) members.add(name); name = null; }
        (
            COMMA name = enumerator
            { if (name != null) members.add(name); name = null; }
        )*
        RCURLY
        {
            if (members.size() == 0)
                throw new TokenStreamException("enum '"+id+"' is empty");

            Object[] membs = members.toArray();
            for (int i = 0; i < membs.length; i++) {
                String m = membs[i].toString();
                for (int j = i+1; j < membs.length; j++)
                    if (m.equals(membs[j].toString()))
                        throw new TokenStreamException("repeated member '"+m+"' in enum '"+id+"'");
            }

            ((MEnumDef) enm).setMembers(members);
        } ;

// 79. <enumerator> ::= <identifier>
enumerator returns [String name = null] : name = identifier ;

// 80. <sequence_type> ::= "sequence" "<" <simple_type_spec,45> ","
//       <positive_int_const,41> ">"
//     | "sequence" "<" <simple_type_spec,45> ">"
//
// (lmj) rewriting this rule because antlr is an ll parser tool.
//
// 80. <sequence_type> ::= "sequence" "<" <simple_type_spec,45>
//       [ "," <positive_int_const,41> ] ">"
sequence_type returns [MIDLType sequence = null]
{
    sequence = new MSequenceDefImpl();
    String bound = null;
    MIDLType type = null;
}
    :   "sequence" LT type = simple_type_spec
        { ((MSequenceDef) sequence).setIdlType(type); }
        ( COMMA bound = positive_int_const
            { ((MSequenceDef) sequence).setBound(new Long(bound)); }
        )? GT ;

// 81. <string_type> ::= "string" "<" <positive_int_const,41> ">" | "string"
//
// (lmj) rewriting because antlr is an ll parser tool.
//
// 81. <string_type> ::= "string" [ "<" <positive_int_const,41> ">" ]
string_type returns [MIDLType str = null]
{ str = new MStringDefImpl(); String bound = null; }
    :   "string"
        (   LT bound = positive_int_const GT
            { ((MStringDef) str).setBound(new Long(bound)); } )? ;

// 82. <wide_string_type> ::= "wstring" "<" <positive_int_const,41> ">"
//     | "wstring"
//
// (lmj) rewriting because antlr is an ll parser tool.
//
// 82. <wide_string_type> ::= "wstring" [ "<" <positive_int_const,41> ">" ]
wide_string_type returns [MIDLType wstr = null]
{ wstr = new MWstringDefImpl(); String bound = null; }
    :   "wstring"
        (   LT bound = positive_int_const GT
            { ((MWstringDef) wstr).setBound(new Long(bound)); } )? ;

// 83. <array_declarator> ::= <identifier> <fixed_array_size,84>+
array_declarator returns [DeclaratorHelper helper = null]
{
    helper = new DeclaratorHelper();
    String name;
    String size;
    MArrayDef array = new MArrayDefImpl();
}
    :   name = identifier { helper.setDeclarator(name); }
        ( size = fixed_array_size { array.addBound(new Long(size)); } )+
        { helper.setArray(array); } ;

// 84. <fixed_array_size> ::= "[" <positive_int_const,41> "]"
fixed_array_size returns [String size = null]
    :   LBRACK size = positive_int_const RBRACK ;

// 85. <attr_dcl> ::= <readonly_attr_spec,104> | <attr_spec,106>
attr_dcl returns [List attributes = null]
    :   attributes = readonly_attr_spec
    |   attributes = attr_spec
    ;

// 86. <except_dcl> ::= "exception" <identifier> "{" <member,71>* "}"
except_dcl returns [MExceptionDef except = null]
{
    except = new MExceptionDefImpl();
    String id = null;
    List members = new ArrayList();
    List decls = null;
    boolean repeatedID = false;
}
    :   "exception" id = identifier
        {
            except = (MExceptionDef) verifyNameEmpty(id, except);
            except.setSourceFile(getSourceFile());
            except.setIdentifier(id);
            symbolTable.add(id, except);
        }
        LCURLY { symbolTable.pushScope(id); }
        ( decls = member { members.addAll(decls); } )*
        { checkSetMembers(except, members); }
        RCURLY { symbolTable.popScope(); } ;

// 87. <op_dcl> ::= [ <op_attribute,88> ] <op_type_spec,89> <identifier>
//       <parameter_dcls,90> [ <raises_expr,93> ] [ <context_expr,94> ]
op_dcl returns [MOperationDef operation = null]
{
    operation = new MOperationDefImpl();
    boolean oneway = false;
    String context = null;
    List params = null;
    List exceptions = null;
    String id = null;
    MIDLType type = null;
}
    :   ( oneway = op_attribute )? type = op_type_spec id = identifier
        {
            operation = (MOperationDef) verifyNameEmpty(id, operation);
            operation.setSourceFile(getSourceFile());
            operation.setIdentifier(id);
            operation.setIdlType(type);
            operation.setOneway(oneway);
            symbolTable.add(id, operation);
        }
        params = parameter_dcls[id] { checkSetParameters(operation, params); }
        (
            exceptions = raises_expr
            { operation.setExceptionDefs(new HashSet(exceptions)); }
        )?
        ( context = context_expr { operation.setContexts(context); } )? ;

// 88. <op_attribute> ::= "oneway"
op_attribute returns [boolean oneway = false] : "oneway" { oneway = true; } ;

// 89. <op_type_spec> ::= <param_type_spec,95> | "void"
op_type_spec returns [MIDLType type = null]
{ type = new MPrimitiveDefImpl(); }
    :   type = param_type_spec
    |   "void" { ((MPrimitiveDef) type).setKind(MPrimitiveKind.PK_VOID); }
    ;

// 90. <parameter_dcls> ::= "(" <param_dcl,91> { "," <param_dcl,91> }* ")"
//     | "(" ")"
//
// (lmj) rewriting rule because antlr is an ll parser tool.
//
// 90. <parameter_dcls> ::= "(" [ <param_dcl,91> { "," <param_dcl,91> }* ] ")"
parameter_dcls[String scope] returns [List params = null]
{
    params = new ArrayList();
    MParameterDef param = null;
}
    :   LPAREN { symbolTable.pushScope(scope); }
        (
            param = param_dcl { params.add(param); }
            ( COMMA param = param_dcl { params.add(param); } )*
        )?
        RPAREN { symbolTable.popScope(); }
    ;

// 91. <param_dcl> ::= <param_attribute,92> <param_type_spec,95>
//       <simple_declarator,51>
param_dcl returns [MParameterDef param = null]
{
    param = new MParameterDefImpl();
    MIDLType type = null;
    String id;
}
    :   param_attribute[param]
        type = param_type_spec { param.setIdlType(type); }
        id = simple_declarator { param.setIdentifier(id); } ;

// 92. <param_attribute> ::= "in" | "out" | "inout"
param_attribute[MParameterDef param]
    :   "in"    { param.setDirection(MParameterMode.PARAM_IN);    }
    |   "out"   { param.setDirection(MParameterMode.PARAM_OUT);   }
    |   "inout" { param.setDirection(MParameterMode.PARAM_INOUT); }
    ;

// 93. <raises_expr> ::= "raises" "(" <scoped_name,12>
//       { ","  <scoped_name,12> }* ")"
raises_expr returns [List exceptions = null]
{ exceptions = new ArrayList(); String name = null; }
    :   "raises" LPAREN name = scoped_name
        {
            MExceptionDef exception = new MExceptionDefImpl();
            exception = (MExceptionDef) verifyNameExists(name, exception);
            exceptions.add(exception);
        }
        (   COMMA name = scoped_name
            {
                MExceptionDef exception = new MExceptionDefImpl();
                exception = (MExceptionDef) verifyNameExists(name, exception);
                exceptions.add(exception);
            }
        )* RPAREN ;

// 94. <context_expr> ::= "context" "(" <string_literal>
//       { "," <string_literal> }* ")"
context_expr returns [String context = null]
{ String id = null; }
    :   "context" LPAREN id = string_literal { context = id; }
        ( COMMA id = string_literal { context += "," + id; } )* RPAREN ;

// 95. <param_type_spec> ::= <base_type_spec,46>
//     | <string_type,81>
//     | <wide_string_type,82>
//     | <scoped_name,12>
param_type_spec returns [MIDLType type = null]
{ String name = null; }
    :   type = base_type_spec
    |   type = string_type
    |   type = wide_string_type
    |   name = scoped_name { type = getIDLType(name); }
    ;

// 96. <fixed_pt_type> ::= "fixed" "<" <positive_int_const,41> ","
//       <positive_int_const,41> ">"
fixed_pt_type returns [MIDLType type = null]
{
    type = new MFixedDefImpl();
    String digits = null;
    String scale = null;
}
    :   "fixed" LT scale = positive_int_const
        { ((MFixedDef) type).setScale(Short.parseShort(scale)); }
        COMMA digits = positive_int_const
        { ((MFixedDef) type).setDigits(Integer.parseInt(digits)); }
        GT ;

// 97. <fixed_pt_const_type> ::= "fixed"
fixed_pt_const_type returns [MIDLType type = null]
{ type = new MFixedDefImpl(); }
    :   "fixed" ;

// 98. <value_base_type> ::= "ValueBase"
value_base_type returns [MIDLType type = null]
{ type = new MPrimitiveDefImpl(); }
    :   "ValueBase"
        { ((MPrimitiveDef) type).setKind(MPrimitiveKind.PK_VALUEBASE); } ;

// 99. <constr_forward_decl> ::= "struct" <identifier> | "union" <identifier>
constr_forward_decl returns [MIDLType type = null]
{ String id = null; }
    :   "struct" id = identifier
        {
            MStructDef struct = new MStructDefImpl();
            struct = (MStructDef) verifyNameEmpty(id, struct);
            struct.setSourceFile(getSourceFile());
            symbolTable.add(id, struct);
            type = (MIDLType) struct;
        }
    |   "union" id = identifier
        {
            MUnionDef union = new MUnionDefImpl();
            union = (MUnionDef) verifyNameEmpty(id, union);
            union.setSourceFile(getSourceFile());
            symbolTable.add(id, union);
            type = (MIDLType) union;
        }
    ;

// (lmj) this was renamed to import_dcl to prevent conflicts with Java keywords
//
// 100. <import_dcl> ::= "import" <imported_scope,101> ";"
import_dcl
{ String scope = null; }
    :   "import" scope = imported_scope SEMI { scopeTable.put(scope, scope); } ;

// 101. <imported_scope> ::= <scoped_name,12> | <string_literal>
imported_scope returns [String scope = null]
    :   scope = scoped_name | scope = string_literal ;

// 102. <type_id_dcl> ::= "typeid" <scoped_name,12> <string_literal>
type_id_dcl
{ String scope = null; String id = null; }
    :   "typeid" scope = scoped_name id = string_literal
        { scopeTable.put(id, scope); } ;

// FIXME : figure out the difference between typeid and typeprefix

// 103. <type_id_dcl> ::= "typeprefix" <scoped_name,12> <string_literal>
type_prefix_dcl
{ String scope = null; String id = null; }
    :   "typeprefix" scope = scoped_name id = string_literal
        { scopeTable.put(id, scope); } ;

// 104. <readonly_attr_spec> ::= "readonly" "attribute" <param_type_spec,95>
//        <readonly_attr_declarator,105>
readonly_attr_spec returns [List attributes = null]
{
    attributes = new ArrayList();
    String id = null;
    MIDLType type = null;
    List decls = null;
}
    :   "readonly" "attribute" type = param_type_spec
        decls = readonly_attr_declarator
        {
            for (Iterator it = decls.iterator(); it.hasNext(); ) {
                MAttributeDef attr = (MAttributeDef) it.next();
                attr.setIdlType(type);
                attributes.add(attr);
                symbolTable.add(id, attr);
            }
        } ;

// 105. <readonly_attr_declarator> ::= <simple_declarator,51> <raises_expr,93>
//      | <simple_declarator,51> { "," <simple_declarator,51> }*
//
// (lmj) rewriting this rule because antlr is an ll parser tool.
//
// 105. <readonly_attr_declarator> ::= <simple_declarator,51>
//        ( <raises_expr,93> | { "," <simple_declarator,51> }* )
readonly_attr_declarator returns [List attributes = null]
{
    attributes = new ArrayList();
    String id = null;
    List excepts = null;
    MAttributeDef attr = new MAttributeDefImpl();
}
    :   id = simple_declarator
        {
            attr = (MAttributeDef) verifyNameEmpty(id, attr);
            attr.setSourceFile(getSourceFile());
            attr.setIdentifier(id);
            attr.setReadonly(true);
            attributes.add(attr);
        }
        (   excepts = raises_expr
            { attr.setGetExceptions(new HashSet(excepts)); }
        |   (   COMMA id = simple_declarator
                {
                    attr = (MAttributeDef) verifyNameEmpty(id, attr);
                    attr.setSourceFile(getSourceFile());
                    attr.setIdentifier(id);
                    attr.setReadonly(true);
                    attributes.add(attr);
                }
            )* ) ;

// 106. <attr_spec> ::= "attribute" <param_type_spec,95> <attr_declarator,107>
attr_spec returns [List attributes = null]
{
    attributes = new ArrayList();
    MIDLType type = null;
    List decls = null;
}
    :   "attribute" type = param_type_spec decls = attr_declarator
        {
            for (Iterator it = decls.iterator(); it.hasNext(); ) {
                MAttributeDef attr = (MAttributeDef) it.next();
                attr.setIdlType(type);
                attr.setReadonly(false);
                attributes.add(attr);
                symbolTable.add(attr.getIdentifier(), attr);
            }
        } ;

// 107. <attr_declarator> ::= <simple_declarator,51> <attr_raises_expr,108>
//      |  <simple_declarator,51> { "," <simple_declarator,51> }*
//
// (lmj) rewriting this rule because antlr is an ll parser tool.
//
// 107. <attr_declarator> ::= <simple_declarator,51>
//        ( <attr_raises_expr,108> | { "," <simple_declarator,51> }* )
attr_declarator returns [List attributes = null]
{
    attributes = new ArrayList();
    String id = null;
    MAttributeDef attr = new MAttributeDefImpl();
}
    :   id = simple_declarator
        {
            attr = (MAttributeDef) verifyNameEmpty(id, attr);
            attr.setSourceFile(getSourceFile());
            attr.setIdentifier(id);
            attr.setReadonly(false);
            attributes.add(attr);
        }
        (   attr_raises_expr[attr]
        |   (   COMMA id = simple_declarator
                {
                    attr = (MAttributeDef) verifyNameEmpty(id, attr);
                    attr.setSourceFile(getSourceFile());
                    attr.setIdentifier(id);
                    attr.setReadonly(false);
                    attributes.add(attr);
                }
            )* ) ;

// 108. <attr_raises_expr> ::= <get_excep_expr,109> [ <set_excep_expr,110> ]
//      | <set_excep_expr,110>
attr_raises_expr[MAttributeDef attr]
{ List gets = null; List sets = null; }
    :   gets = get_excep_expr   { attr.setGetExceptions(new HashSet(gets)); }
        ( sets = set_excep_expr { attr.setSetExceptions(new HashSet(sets)); } )?
    |   sets = set_excep_expr   { attr.setSetExceptions(new HashSet(sets)); }
    ;

// 109. <get_excep_expr> ::= "getraises" <exception_list,111>
get_excep_expr returns [List exceptions = null]
    :   "getraises" exceptions = exception_list ;

// 110. <set_excep_expr> ::= "setraises" <exception_list,111>
set_excep_expr returns [List exceptions = null]
    :   "setraises" exceptions = exception_list ;

// 111. <exception_list> ::= "(" <scoped_name,12> { "," <scoped_name,12> }* ")"
exception_list returns [List exceptions = null]
{ exceptions = new ArrayList(); String name = null; }
    :   LPAREN name = scoped_name
        {
            MExceptionDef except = new MExceptionDefImpl();
            except = (MExceptionDef) verifyNameExists(name, except);
            exceptions.add(except);
        }
        (
            COMMA name = scoped_name
            {
                MExceptionDef except = new MExceptionDefImpl();
                except = (MExceptionDef) verifyNameExists(name, except);
                exceptions.add(except);
            }
        )*
        RPAREN ;

// 112. <component> ::= <component_dcl,114> | <component_forward_dcl,113>
component returns [MComponentDef component = null]
    :   ( component_dcl ) => component = component_dcl
    |   component = component_forward_dcl
    ;

// 113. <component_forward_dcl> ::= "component" <identifier>
component_forward_dcl returns [MComponentDef component = null]
{ component = new MComponentDefImpl(); String id = null; }
    :   "component" id = identifier
        {
            component = (MComponentDef) verifyNameEmpty(id, component);
            component.setSourceFile(getSourceFile());
            component.setRepositoryId(createRepositoryId(id));
            component.setIdentifier(id);
            symbolTable.add(id, component);
        } ;

// 114. <component_dcl> ::=  <component_header,115> "{" <component_body,118> "}"
component_dcl returns [MComponentDef component = null]
{ component = new MComponentDefImpl(); List decls =  null; }
    :   component = component_header
        LCURLY { symbolTable.pushScope(component.getIdentifier()); }
        decls = component_body
        {
            for (Iterator it = decls.iterator(); it.hasNext(); ) {
                MContained element = (MContained) it.next();
                element.setDefinedIn(component);
                element.setSourceFile(getSourceFile());

                if ((debug & DEBUG_COMPONENT) != 0)
                    System.out.print(
                        "[c] adding '"+element.getIdentifier()+
                        "' to component '"+component.getIdentifier()+"'");

                if (element instanceof MEmitsDef) {
                    ((MEmitsDef) element).setComponent(component);
                    component.addEmits((MEmitsDef) element);
                    if ((debug & (DEBUG_COMPONENT + DEBUG_EVENT)) != 0)
                        System.out.println(" as emits");
                } else if (element instanceof MPublishesDef) {
                    ((MPublishesDef) element).setComponent(component);
                    component.addPublishes((MPublishesDef) element);
                    if ((debug & (DEBUG_COMPONENT + DEBUG_EVENT)) != 0)
                        System.out.println(" as publishes");
                } else if (element instanceof MConsumesDef) {
                    ((MConsumesDef) element).setComponent(component);
                    component.addConsumes((MConsumesDef) element);
                    if ((debug & (DEBUG_COMPONENT + DEBUG_EVENT)) != 0)
                        System.out.println(" as consumes");
                } else if (element instanceof MProvidesDef) {
                    ((MProvidesDef) element).setComponent(component);
                    component.addFacet((MProvidesDef) element);
                    if ((debug & (DEBUG_COMPONENT + DEBUG_INTERFACE)) != 0)
                        System.out.println(" as facet");
                } else if (element instanceof MUsesDef) {
                    ((MUsesDef) element).setComponent(component);
                    component.addReceptacle((MUsesDef) element);
                    if ((debug & (DEBUG_COMPONENT + DEBUG_INTERFACE)) != 0)
                        System.out.println(" as receptacle");
                } else if (element instanceof MAttributeDef) {
                    ((MAttributeDef) element).setDefinedIn(component);
                    component.addContents((MAttributeDef) element);
                    if ((debug & DEBUG_COMPONENT) != 0)
                        System.out.println(" as attribute");
                } else {
                    throw new TokenStreamException(
                        "can't add element '"+element.getIdentifier()+
                        "' to component '"+component.getIdentifier()+"'");
                }
            }
        }
        RCURLY { symbolTable.popScope(); } ;

// 115. <component_header> ::= "component" <identifier>
//        [ <component_inheritance_spec,116> ]
//        [ <supported_interface_spec,117> ]
component_header returns [MComponentDef component = null]
{ component = new MComponentDefImpl(); String id; List base; List sups; }
    :   "component" id = identifier
        {
            component = (MComponentDef) verifyNameEmpty(id, component);
            component.setSourceFile(getSourceFile());
            component.setRepositoryId(createRepositoryId(id));
            component.setIdentifier(id);
            symbolTable.add(id, component);
        }
        ( base = component_inheritance_spec { checkSetBases(component, base); } )?
        ( sups = supported_interface_spec { checkSetSupports(component, sups); } )? ;

// 116. <supported_interface_spec> ::= "supports" <scoped_name,12>
//        { "," <scoped_name,12> }*
supported_interface_spec returns [List supports = null]
{ supports = new ArrayList(); String name = null; }
    :   "supports" name = scoped_name { supports.add(name); }
        ( COMMA name = scoped_name { supports.add(name); } )* ;

// 117. <component_inheritance_spec> ::= ":" <scoped_name,12>
component_inheritance_spec returns [List names = null]
{ names = new ArrayList(); String name = null; }
    :   COLON name = scoped_name { names.add(name); } ;

// 118. <component_body> ::= <component_export,119>*
component_body returns [List exports = null]
{ exports = new ArrayList(); List decls = null; }
    :   ( decls = component_export { exports.addAll(decls); } )* ;

// 119. <component_export> ::= <provides_dcl,120> ";"
//      | <uses_dcl,122> ";"
//      | <emits_dcl,123> ";"
//      | <publishes_dcl,124> ";"
//      | <consumes_dcl,125> ";"
//      | <attr_dcl,85> ";"       <-- this returns a list
component_export returns [List exports = null]
{ exports = new ArrayList(); MContained holder = null; }
    :   holder = provides_dcl SEMI  { exports.add(holder); }
    |   holder = uses_dcl SEMI      { exports.add(holder); }
    |   holder = emits_dcl SEMI     { exports.add(holder); }
    |   holder = publishes_dcl SEMI { exports.add(holder); }
    |   holder = consumes_dcl SEMI  { exports.add(holder); }
    |   exports = attr_dcl SEMI
    ;

// 120. <provides_dcl> ::= "provides" <interface_type,121> <identifier>
provides_dcl returns [MProvidesDef provides = null]
{
    provides = new MProvidesDefImpl();
    String id = null;
    MInterfaceDef iface = null;
}
    :   "provides" iface = interface_type id = identifier
        {
            provides = (MProvidesDef) verifyNameEmpty(id, provides);
            provides.setSourceFile(getSourceFile());
            provides.setIdentifier(id);
            provides.setProvides(iface);
            symbolTable.add(id, provides);
        } ;

// 121. <interface_type> ::= <scoped_name,12> | "Object"
interface_type returns [MInterfaceDef iface = null]
{ iface = new MInterfaceDefImpl(); String name = null; }
    :   name = scoped_name
        { iface = (MInterfaceDef) verifyNameExists(name, iface); }
    |   "Object" // FIXME : what do we do with this ? { ((MPrimitiveDef) type).setKind(MPrimitiveKind.PK_OBJREF); }
    ;

// 122. <uses_dcl> ::= "uses" [ "multiple" ] < interface_type,121> <identifier>
uses_dcl returns [MUsesDef uses = null]
{
    uses = new MUsesDefImpl();
    String id = null;
    MInterfaceDef iface = null;
}
    :   "uses" ( "multiple" { uses.setMultiple(true); } )?
        iface = interface_type id = identifier
        {
            uses = (MUsesDef) verifyNameEmpty(id, uses);
            uses.setSourceFile(getSourceFile());
            uses.setIdentifier(id);
            uses.setUses(iface);
            symbolTable.add(id, uses);
        } ;

// 123. <emits_dcl> ::= "emits" <scoped_name,12> <identifier>
emits_dcl returns [MEmitsDef emits = null]
{
    emits = new MEmitsDefImpl();
    String id = null;
    String name = null;
    MEventDef event = new MEventDefImpl();
}
    :   "emits" name = scoped_name id = identifier
        {
            emits = (MEmitsDef) verifyNameEmpty(id, emits);
            event = (MEventDef) verifyNameExists(name, event);
            emits.setType(event);
            emits.setSourceFile(getSourceFile());
            emits.setIdentifier(id);
            symbolTable.add(id, emits);
        }
    ;

// 124. <publishes_dcl> ::= "publishes" <scoped_name,12> <identifier>
publishes_dcl returns [MPublishesDef publishes = null]
{
    publishes = new MPublishesDefImpl();
    String id = null;
    String name = null;
    MEventDef event = null;
}
    :   "publishes" name = scoped_name id = identifier
        {
            publishes = (MPublishesDef) verifyNameEmpty(id, publishes);
            event = (MEventDef) verifyNameExists(name, event);
            publishes.setType(event);
            publishes.setSourceFile(getSourceFile());
            publishes.setIdentifier(id);
            symbolTable.add(id, publishes);
        } ;

// 125. <consumes_dcl> ::= "consumes" <scoped_name> <identifier>
consumes_dcl returns [MConsumesDef consumes = null]
{
    consumes = new MConsumesDefImpl();
    String id = null;
    String name = null;
    MEventDef event = null;
}
    :   "consumes" name = scoped_name id = identifier
        {
            consumes = (MConsumesDef) verifyNameEmpty(id, consumes);
            event = (MEventDef) verifyNameExists(name, event);
            consumes.setType(event);
            consumes.setSourceFile(getSourceFile());
            consumes.setIdentifier(id);
            symbolTable.add(id, consumes);
        } ;

// 126. <home_dcl> ::= <home_header,127> <home_body,130>
home_dcl returns [MHomeDef home = null]
    :   home = home_header home_body[home] ;

// 127. <home_header> ::= "home" <identifier>
//        [ <home_inheritance_spec,128> ] [ <supported_interface_spec,117> ]
//        "manages" <scoped_name,12> [ <primary_key_spec,129> ]
home_header returns [MHomeDef home = null]
{
    home = new MHomeDefImpl();
    String id = null;
    String name;
    MComponentDef component = new MComponentDefImpl();
    MHomeDef b = null;
    List sups = null;
    MValueDef key = null;
}
    :   "home" id = identifier
        {
            home = (MHomeDef) verifyNameEmpty(id, home);
            home.setRepositoryId(createRepositoryId(id));
            home.setSourceFile(getSourceFile());
            home.setIdentifier(id);
            symbolTable.add(id, home);
        }
        ( b = home_inheritance_spec { if (b != null) { home.addBase(b); } } )?
        ( sups = supported_interface_spec { checkSetSupports(home, sups); } )?
        "manages" name = scoped_name
        {
            component = (MComponentDef) verifyNameExists(name, component);
            home.setComponent(component);
            component.addHome(home);
        }
        ( key = primary_key_spec { home.setPrimary_Key(key); } )? ;

// 128. <home_inheritance_spec> ::= ":" <scoped_name,12>
home_inheritance_spec returns [MHomeDef base = null]
{ String name = null; }
    :   COLON name = scoped_name
        {
            MContained lookup = lookupName(name, DEBUG_COMPONENT);
            if ((lookup != null) && (lookup instanceof MHomeDef))
            { base = (MHomeDef) lookup; }
        } ;

// 129. <primary_key_spec> ::= "primarykey" <scoped_name,12>
primary_key_spec returns [MValueDef key = null]
{ key = new MValueDefImpl(); String name = null; }
    :   "primarykey" name = scoped_name
        { key = (MValueDef) verifyNameExists(name, key); } ;

// 130. <home_body> ::= "{" <home_export,131>* "}"
home_body[MHomeDef home]
    :   LCURLY { symbolTable.pushScope(home.getIdentifier()); }
        ( home_export[home] )*
        RCURLY { symbolTable.popScope(); } ;

// 131. <home_export ::= <export,9>
//      | <factory_dcl,132> ";" | <finder_dcl,133> ";"
home_export[MHomeDef home]
{
    List exports = null;
    MFactoryDef factory = null;
    MFinderDef finder = null;
}
    :   exports = export { checkAddContents(home, exports); }
    |   factory = factory_dcl SEMI
        {
            factory.setHome(home);
            home.addFactory(factory);
        }
    |   finder = finder_dcl SEMI
        {
            finder.setHome(home);
            home.addFinder(finder);
        }
    ;

// 132. <factory_dcl> ::= "factory" <identifier>
//        "(" [ <init_param_decls,24> ] ")" [ <raises_expr,93> ]
factory_dcl returns [MFactoryDef factory = null]
{
    factory = new MFactoryDefImpl();
    String id = null;
    List params = null;
    List excepts = null;
}
    :   "factory" id = identifier
        {
            factory = (MFactoryDef) verifyNameEmpty(id, factory);
            factory.setSourceFile(getSourceFile());
            factory.setIdentifier(id);
            symbolTable.add(id, factory);
        }
        LPAREN ( params = init_param_decls
            { checkSetParameters((MOperationDef) factory, params); } )?
        RPAREN ( excepts = raises_expr
            { checkSetExceptions((MOperationDef) factory, excepts); } )? ;

// 133. <finder_dcl> ::= "finder" <identifier>
//        "(" [ <init_param_decls,24> ] ")" [ <raises_expr,93> ]
finder_dcl returns [MFinderDef finder = null]
{
    finder = new MFinderDefImpl();
    String id = null;
    List params = null;
    List excepts = null;
}
    :   "finder" id = identifier
        {
            finder = (MFinderDef) verifyNameEmpty(id, finder);
            finder.setSourceFile(getSourceFile());
            finder.setIdentifier(id);
            symbolTable.add(id, finder);
        }
        LPAREN ( params = init_param_decls
            { checkSetParameters((MOperationDef) finder, params); } )?
        RPAREN ( excepts = raises_expr
            { checkSetExceptions((MOperationDef) finder, excepts); } )? ;

// 134. <event> ::= <event_dcl,137> | <event_abs_dcl,136>
//      | <event_forward_dcl,135>
event returns [MEventDef event = null]
    :   ( event_dcl ) => event = event_dcl
    |   ( event_abs_dcl ) => event = event_abs_dcl
    |   event = event_forward_dcl
    ;

// 135. <event_forward_dcl> ::= [ "abstract" ] "eventtype" <identifier>
event_forward_dcl returns [MEventDef event = null]
{ event = new MEventDefImpl(); String id = null; }
    :   ( "abstract" { event.setAbstract(true); } )? "eventtype" id = identifier
        {
            event = (MEventDef) verifyNameEmpty(id, event);
            event.setSourceFile(getSourceFile());
            event.setIdentifier(id);
            symbolTable.add(id, event);
            event.setContentss(null);

            if ((debug & (DEBUG_EVENT | DEBUG_FORWARD_DECL)) != 0) {
                System.out.println("[d] event forward declaration for " + id);
            }
        }
    ;

// 136. <event_abs_dcl> ::= "abstract" "eventtype" <identifier>
//        [ <value_inheritance_spec,19> ] "{" <export,9>* "}"
//
// (lmj) had to change original rule because all of <value_inheritance_spec,19>
// is optional.
//
// 136. <event_abs_dcl> ::= "abstract" "eventtype" <identifier>
//        <value_inheritance_spec,19> "{" <export,9>* "}"
event_abs_dcl returns [MEventDef event = null]
{
    event = new MEventDefImpl();
    String id = null;
    List exports = new ArrayList();
    List decls = null;
}
    :   "abstract" { event.setAbstract(true); } "eventtype" id = identifier
        {
            event = (MEventDef) verifyNameEmpty(id, event);
            event.setSourceFile(getSourceFile());
            event.setIdentifier(id);
            symbolTable.add(id, event);
        }
        value_inheritance_spec[(MValueDef) event]
        LCURLY { symbolTable.pushScope(id); }
        ( decls = export { exports.addAll(decls); } )*
        RCURLY
        {
            for (Iterator i = exports.iterator(); i.hasNext(); ) {
                ((MContained) i.next()).setDefinedIn(event);
            }
            symbolTable.popScope();
        } ;

// 137. <event_dcl> ::= <event_header,138> "{" <value_element,21>* "}"
event_dcl returns [MEventDef event = null]
{
    event = new MEventDefImpl();
    List elements = new ArrayList();
    List decls = null;
}
    :   event = event_header
        LCURLY { symbolTable.pushScope(event.getIdentifier()); }
        (
            decls = value_element
            {
                for (Iterator i = decls.iterator(); i.hasNext(); ) {
                    ((MContained) i.next()).setDefinedIn(event);
                }
                elements.addAll(decls);
            }
        )*
        RCURLY
        {
            event.setContentss(elements);
            symbolTable.popScope();
        } ;

// 138. <event_header> ::= [ "custom"  ] "eventtype" <identifier>
//        [ <value_inheritance_spec,19> ]
//
// (lmj) had to change original rule because all of <value_inheritance_spec,19>
// is optional.
//
// 138. <event_header> ::= [ "custom"  ] "eventtype" <identifier>
//        <value_inheritance_spec,19>
event_header returns [MEventDef event = null]
{ event = new MEventDefImpl(); String id = null; }
    :   ( "custom" { event.setCustom(true); } )? "eventtype" id = identifier
        {
            event = (MEventDef) verifyNameEmpty(id, event);
            event.setSourceFile(getSourceFile());
            event.setIdentifier(id);
            symbolTable.add(id, event);
        }
        value_inheritance_spec[(MValueDef) event] ;

// **** LITERALS and IDENTIFIERS ****

integer_literal returns [String literal = null]
    :   i:INT { literal = i.getText(); }
    |   o:OCTAL
        {
            Integer value = new Integer(Integer.parseInt(o.getText(), 8));
            literal = value.toString();
        }
    |   h:HEX
        {
            Integer value = new Integer(
                Integer.parseInt(o.getText().substring(2), 16));
            literal = value.toString();
        }
    ;

string_literal returns [String literal = null]
{ literal = new String(); }
    :   ( s:STRING_LITERAL { literal += s.getText(); } )+ ;

wide_string_literal returns [String literal = null]
{ literal = new String(); }
    :   ( ws:WIDE_STRING_LITERAL { literal += ws.getText(); } )+ ;

character_literal returns [String literal = null]
    :   c:CHAR_LITERAL { literal = c.getText(); } ;

wide_character_literal returns [String literal = null]
    :   wc:WIDE_CHAR_LITERAL { literal = wc.getText(); } ;

floating_pt_literal returns [String literal = null]
    :   f:FLOAT { literal = f.getText(); } ;

fixed_pt_literal returns [String literal = null]
    :   ( FLOAT "d" ) => f:FLOAT { literal = f.getText(); } "d"
    |   ( FLOAT "D" ) => F:FLOAT { literal = F.getText(); } "D"
    ;

identifier returns [String identifier = null]
    :   i:IDENT { identifier = i.getText(); } { checkKeyword(identifier) }? ;

/******************************************************************************/
/* IDL LEXICAL RULES  */

class IDL3Lexer extends Lexer;
options { exportVocab = IDL3; k = 4; charVocabulary = '\u0000'..'\u0377'; }
{
    // debug levels for the lexer. i assume the lexer will have fewer semantic
    // issues (== those that require debugging) so the lexer only gets the top
    // eight bits for flags.
    private final static long DEBUG_FILE     = 0x01000000;
    private final static long DEBUG_INCLUDE  = 0x02000000;

    // unused debug flags, implement as needed.
    private final static long DEBUG_UNUSED_C = 0x04000000;
    private final static long DEBUG_UNUSED_D = 0x08000000;
    private final static long DEBUG_UNUSED_E = 0x10000000;
    private final static long DEBUG_UNUSED_F = 0x20000000;
    private final static long DEBUG_UNUSED_G = 0x40000000;
    private final static long DEBUG_UNUSED_H = 0x80000000;

    private long debug = 0;

    private ParserManager manager;

    /*
     *  Set up the current class instance for a parse, by specifying the parser
     *  manager object that this parser belongs to. This loads the parser
     *  manager's symbol table into this parser instance.
     */
    public void setManager(ParserManager m) { manager = m; }

    /*
     * Set the debug level for this parser class instance.
     */
    public void setDebug(long d) { debug = d; }
}

SEMI     options { paraphrase = ";" ; } : ';'  ;
QUESTION options { paraphrase = "?" ; } : '?'  ;
LPAREN   options { paraphrase = "(" ; } : '('  ;
RPAREN   options { paraphrase = ")" ; } : ')'  ;
LBRACK   options { paraphrase = "[" ; } : '['  ;
RBRACK   options { paraphrase = "]" ; } : ']'  ;
LCURLY   options { paraphrase = "{" ; } : '{'  ;
RCURLY   options { paraphrase = "}" ; } : '}'  ;
OR       options { paraphrase = "|" ; } : '|'  ;
XOR      options { paraphrase = "^" ; } : '^'  ;
AND      options { paraphrase = "&" ; } : '&'  ;
COLON    options { paraphrase = ":" ; } : ':'  ;
COMMA    options { paraphrase = "," ; } : ','  ;
DOT      options { paraphrase = "." ; } : '.'  ;
ASSIGN   options { paraphrase = "=" ; } : '='  ;
NOT      options { paraphrase = "!" ; } : '!'  ;
LT       options { paraphrase = "<" ; } : '<'  ;
GT       options { paraphrase = ">" ; } : '>'  ;
DIV      options { paraphrase = "/" ; } : '/'  ;
PLUS     options { paraphrase = "+" ; } : '+'  ;
MINUS    options { paraphrase = "-" ; } : '-'  ;
TILDE    options { paraphrase = "~" ; } : '~'  ;
STAR     options { paraphrase = "*" ; } : '*'  ;
MOD      options { paraphrase = "%" ; } : '%'  ;
LSHIFT   options { paraphrase = "<<"; } : "<<" ;
RSHIFT   options { paraphrase = ">>"; } : ">>" ;
SCOPEOP  options { paraphrase = "::"; } : "::" ;

WS options { paraphrase = "white space"; }
    :   ( ' ' | '\t' | '\f' | '\n' { newline(); } | '\r' )
        { $setType(Token.SKIP); } ;

PREPROC_DIRECTIVE options { paraphrase = "a preprocessor directive"; }
    :   ( "# " ( DIGIT )+  | "#pragma GCC " IDENT ) ' ' s:STRING_LITERAL
        {
            String include = s.getText();
            if (include.length() > 0 && include.charAt(0) != '<') {
                manager.setSourceFile(s.getText());
                if ((debug & DEBUG_FILE) != 0) {
                    String label = "[f] source from file : ";
                    System.out.println(label + s.getText());
                }
            }
        }
        ( ' ' ( DIGIT )+ )? ( ' ' | '\t' | '\f' )* ( '\n' | '\r' ( '\n' )? )
        { $setType(Token.SKIP); newline(); } ;

// this is cracked up, but for some reason antlr can't handle dollars at the end
// of a single-line comment.

SL_COMMENT options { paraphrase = "a comment"; }
    :   "//" ( options { warnWhenFollowAmbig = false; } : '$' )?
        ( ~ ( '\n' | '\r' ) )* ( '\n' | '\r' ( '\n' )? )
        { $setType(Token.SKIP); newline(); } ;

ML_COMMENT options { paraphrase = "a comment"; }
    :   "/*"
        ( options { generateAmbigWarnings = false; }
        :   { LA(2) != '/' }? '*'
        |   '\r' '\n' { newline(); }
        |   '\r'      { newline(); }
        |   '\n'      { newline(); }
        |   ~ ( '*' | '\n' | '\r' )
        )*
        "*/" { $setType(Token.SKIP); } ;

protected DIGIT options { paraphrase = "a digit"; } : '0'..'9' ;
protected OCTDIGIT options { paraphrase = "an octal digit"; } : '0'..'7' ;

protected HEX_LETTER options { paraphrase = "a hexadecimal letter"; }
    :    ( 'a'..'f' | 'A'..'F' ) ;

protected EXPONENT options { paraphrase = "an exponent"; }
    :   ( 'e' | 'E' ) ( '+' | '-' )? ( DIGIT )+ ;

protected IDENT_LETTER options { paraphrase = "an identifier character"; }
    :   ( 'g'..'z' | 'G'..'Z' | '_' ) ;

protected
ESC options { paraphrase = "an escape sequence"; }
    :   '\\'
        (   'n' | 't' | 'v' | 'b' | 'r' | 'f' | 'a' | '\\' | '?' | '\'' | '"'
        |   ( '0'..'3' )
            ( options { warnWhenFollowAmbig = false; }
            : OCTDIGIT ( options { warnWhenFollowAmbig = false; } : OCTDIGIT )?
            )?
        |   'x' ( DIGIT | HEX_LETTER )
            ( options { warnWhenFollowAmbig = false; } :
                ( DIGIT | HEX_LETTER ) )?
        ) ;

CHAR_LITERAL        options { paraphrase = "a character literal"; }
    :   '\'' ( ESC | ~ '\'' ) '\'' ;
WIDE_CHAR_LITERAL   options { paraphrase = "a character literal"; }
    :   'L' '\'' ( ESC | ~ '\'' ) '\'' ;
STRING_LITERAL      options { paraphrase = "a string literal"; }
    :   '"'! ( ESC | ~ ( '"' | '\\' ) )* '"'! ;
WIDE_STRING_LITERAL options { paraphrase = "a string literal"; }
    :   'L'! '"'! ( ESC | ~ '"' )* '"'! ;

OCT options { paraphrase = "an octal value"; } : "0" ( OCTDIGIT )+ ;

INT options { paraphrase = "an integer value"; }
    :   '1'..'9' ( DIGIT )*
        ( '.' ( DIGIT )* { $setType(FLOAT); } )?
        ( EXPONENT { $setType(FLOAT); } )? ;

HEX options { paraphrase = "a hexadecimal value"; }
    :   ( "0x" | "0X" ) ( DIGIT | HEX_LETTER )+ ;

FLOAT options { paraphrase = "a floating point value"; }
    :   '.' ( DIGIT )+ ( EXPONENT )? ;

IDENT options { paraphrase = "an identifier"; }
    :   ( HEX_LETTER | IDENT_LETTER )
        ( 'a'..'z' | 'A'..'Z' | '_' | '0'..'9' )* ;

