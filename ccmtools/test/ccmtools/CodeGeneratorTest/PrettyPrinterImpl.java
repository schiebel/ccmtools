/* CCM Tools : Testsuite
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

import ccmtools.CodeGenerator.NodeHandler;
import ccmtools.Metamodel.BaseIDL.MContained;
import ccmtools.Metamodel.BaseIDL.MFieldDef;
import ccmtools.Metamodel.BaseIDL.MParameterDef;
import ccmtools.Metamodel.BaseIDL.MUnionFieldDef;

import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;

public class PrettyPrinterImpl
    implements NodeHandler
{
    private String indent;
    private PrintStream output;

    public PrettyPrinterImpl()
    {
        output = System.out;
        indent = "";
    }

    public PrettyPrinterImpl(String o)
        throws FileNotFoundException
    {
        output = new PrintStream(new FileOutputStream(o));
        indent = "";
    }

    public void startGraph()
    {
        output.println("Starting graph.");
    }

    public void endGraph()
    {
        output.println("Graph traversal finished.");
    }

    public void startNode(Object node, String scope_id)
    {
        indent += "  ";

        // this type check comb is silly.

        String id = "(unknown)";
        if (node instanceof MContained) {
            id = ((MContained) node).getIdentifier();
        } else if (node instanceof MFieldDef) {
            id = ((MFieldDef) node).getIdentifier();
        } else if (node instanceof MParameterDef) {
            id = ((MParameterDef) node).getIdentifier();
        } else if (node instanceof MUnionFieldDef) {
            id = ((MUnionFieldDef) node).getIdentifier();
        }

        output.println(indent+" > "+id+" ["+scope_id+"]");
    }

    public void endNode(Object node, String scope_id)
    {
        String id = "(unknown)";
        if (node instanceof MContained) {
            id = ((MContained) node).getIdentifier();
        } else if (node instanceof MFieldDef) {
            id = ((MFieldDef) node).getIdentifier();
        } else if (node instanceof MParameterDef) {
            id = ((MParameterDef) node).getIdentifier();
        } else if (node instanceof MUnionFieldDef) {
            id = ((MUnionFieldDef) node).getIdentifier();
        }

        output.println(indent+" < "+id+" ["+scope_id+"]");
        indent = indent.substring(2);
    }

    public void handleNodeData(String field_type, String field_id, Object value)
    {
        indent += "  ";
        output.println(indent + " @ " + field_id + " : " + value);
        indent = indent.substring(2);
    }
}

