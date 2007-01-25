/* CCM Tools : Testsuite
 * Edin Arnautovic <edin.arnautovic@salomon.at>
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

package ccmtools.CodeGeneratorTest;

import ccmtools.CodeGenerator.CCMMOFGraphTraverserImpl;
import ccmtools.CodeGenerator.GraphTraverser;
import ccmtools.CodeGenerator.NodeHandler;
import ccmtools.IDL3Parser.IDL3SymbolTable;
import ccmtools.IDL3Parser.ParserManager;
import ccmtools.Metamodel.BaseIDL.MContainer;

public class Main {
    public static IDL3SymbolTable symbolTable = new IDL3SymbolTable();

    public static void main(String[] args)
        throws Exception
    {
        ParserManager manager = new ParserManager(-1);

        MContainer container = null;
        try {
            container = manager.parseFile(args[0]);
        } catch (Exception e) {
            System.err.println("Error parsing file "+args[0]);
            System.err.println(e);
            throw e;
        }

        container.setIdentifier("test-"+args[0]);
        NodeHandler handler = new PrettyPrinterImpl();
        GraphTraverser traverser = new CCMMOFGraphTraverserImpl();
        traverser.addHandler(handler);
        try {
            traverser.traverseGraph(container);
        } catch (Exception e) {
            System.err.println("Error traversing graph in "+args[0]);
            System.err.println(e);
            throw e;
        }
    }
}

