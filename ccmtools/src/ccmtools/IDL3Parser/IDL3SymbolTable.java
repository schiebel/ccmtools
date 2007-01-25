/* CCM Tools : IDL3 Parser
 * Edin Arnautovic <edin.arnautovic@salomon.at>
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

/*
 * This code is taken mostly from the gnuc parser made by John Mitchell and
 * Monty Zukowski, found on www.antlr.org (Original file: CSymbolTable). The
 * TNode has been exchanged with MContained.
 */

package ccmtools.IDL3Parser;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.SortedSet;
import java.util.Stack;
import java.util.TreeSet;

import ccmtools.Metamodel.BaseIDL.MContained;

public class IDL3SymbolTable {
    /* holds list of scopes */
    private Stack scopeStack;

    /* table where all defined names are mapped to parse tree nodes */
    private Hashtable symTable;

    public IDL3SymbolTable()
    {
        scopeStack = new Stack();
        symTable = new Hashtable(533);
    }

    /* clear the state of the symbol table. */
    public void clear()
    {
        scopeStack = new Stack();
        symTable = new Hashtable(533);
    }

    /* push a new scope onto the scope stack. */
    public void pushScope(String s) { scopeStack.push(s); }

    /* pop the last scope off the scope stack. */
    public void popScope() { scopeStack.pop(); }

    /* push a new input file onto the scope stack. new files always start out
     * with an empty scope, represented as a "::" on the stack. */
    public void pushFile() { scopeStack.push("::"); }

    /* retrieve the last level of the meta-stack ; that is, pop until we find
     * the preceding empty scope operator. */
    public void popFile()
    {
        String toPop = (String) scopeStack.peek();
        while (! toPop.equals("::")) {
            scopeStack.pop();
            toPop = (String) scopeStack.peek();
        }
        if (toPop != null && toPop.equals("::")) scopeStack.pop();
    }

    /* return the current scope as a string */
    public String currentScopeAsString()
    {
        StringBuffer buf = new StringBuffer("");
        boolean first = true;
        ListIterator i = scopeStack.listIterator(scopeStack.size());
        while (i.hasPrevious()) {
            String elem = (String) i.previous();
            if (elem.equals("::")) break;
            if (first) { first = false; buf.insert(0, elem); }
            else buf.insert(0, elem + "::");
        }

        return buf.toString();
    }

    /* given a name for a type, append it with the current scope. */
    public String addCurrentScopeToName(String name)
    { return addScopeToName(currentScopeAsString(), name); }

    /* given a name for a type, append it with the given scope. MBZ */
    public String addScopeToName(String scope, String name)
    {
        if (scope != null && scope.length() < 2) return name;
        if (scope.substring(scope.length()-2).equals("::"))
            return scope + name;
        return scope + "::" + name;
    }

    /* remove one level of scope from name MBZ */
    public String removeOneLevelScope(String scopeName)
    {
        int index = scopeName.lastIndexOf("::");
        if (index > 0) return scopeName.substring(0, index);
        if (scopeName.length() > 0) return "";
        return null;
    }

    /* add a contained to the table with it key as the current scoped name */
    public MContained add(String name, MContained cont)
    { return (MContained) symTable.put(addCurrentScopeToName(name), cont); }

    /* lookup a fully scoped name in the symbol table */
    public MContained lookupScopedName(String scopedName)
    { return (MContained) symTable.get(scopedName); }

    /* lookup an unscoped name in the table by prepending the current scope.
       MBZ */
    public MContained lookupNameInCurrentScope(String name)
    {
        if (name == null) return null;

        String scope = currentScopeAsString();
        String scopedName;
        MContained mContained = null;

        while (mContained == null && scope != null) {
            scopedName = addScopeToName(scope, name);
            mContained = (MContained)symTable.get(scopedName);
            scope = removeOneLevelScope(scope);
        }

        return mContained;
    }

    /* convert this table to a string */
    public String toString()
    {
        StringBuffer buf = new StringBuffer("");
        SortedSet keys = new TreeSet(symTable.keySet());
        for (Iterator i = keys.iterator(); i.hasNext(); )
            buf.append("[s] " + i.next() + "\n");
        String str = buf.toString();
        return (str.length() > 0) ? str.substring(0, str.length() - 1) : "";
    }
};
