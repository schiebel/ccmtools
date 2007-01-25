/* CCM Tools : Code Generator Library
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

package ccmtools.CodeGenerator;

/**
 * Node handler objects are used by the CCM Tools graph traverser to interpret
 * graph traversal events. Typically such events are emitted with respect to a
 * given node in the graph, hence the name for this interface.
 *
 * Node handlers must define exactly five handler functions, which will be
 * called when the graph traverser starts traversing a graph, ends traversing a
 * graph, starts investigating a particular node, ends looking at a particular
 * node, and encounters a node's data element. Generally the sequence of events
 * in a graph traversal goes something like:
 *
 * GRAPH_START, ( NODE_START, NODE_DATA *, NODE_END ) *, GRAPH_END
 *
 * where the * is used in the regular expression sense of "repeat zero or more
 * times." All graph traversals will have exactly one call to the start and end
 * graph functions.
 */
public interface NodeHandler
{
    /**
     * The graph traverser is starting a traversal of a new graph. The node
     * handler usually resets any per-graph state variables here.
     */
    void startGraph();

    /**
     * The graph traverser has finished traversing a graph. This is a good place
     * to accumulate any per-graph state variables and do whatever processing
     * needs to be done with all graph information.
     */
    void endGraph();

    /**
     * The graph traverser is starting to investigate a particular node in the
     * graph. Zero or more data elements will follow, and then exactly one call
     * to the node end function.
     *
     * @param node The graph node that the traverser is about to start visiting.
     * @param scope_id The full scope identifier of the node. This is a
     *        double-colon (::) delimited list of the nodes visited so far in
     *        the shortest path on the  graph traversal from the top node to the
     *        current node.
     */
    void startNode(Object node, String scope_id);

    /**
     * The graph traverser is starting to investigate a particular node in the
     * graph. Zero or more data elements will follow, and then exactly one call
     * to the node end function.
     *
     * @param node The graph node that the traverser has just finished visiting.
     * @param scope_id The full scope identifier of the node. This is a
     *        double-colon (::) delimited list of the nodes visited so far in
     *        the shortest path on the  graph traversal from the top node to the
     *        current node.
     */
    void endNode(Object node, String scope_id);


    /**
     * The graph traverser has found a constituent data element from the current
     * node in the graph. Calls to this function are preceded by zero or more
     * calls to handleNodeData, which are preceded by a single call to the
     * startNode function. Similarly, calls to this function are followed by
     * zero or more calls to handleNodeData, followed by exactly one call to
     * endNode.
     *
     * @param field_type The type of the data element being visited. This is
     *        provided as a string, usually obtained by calling the data
     *        element's toString method somehow.
     * @param field_id The variable name of the data element being visited, as
     *        it is defined in the owning class.
     * @param value The value of the given data element.
     */
    void handleNodeData(String field_type, String field_id, Object value);
}

