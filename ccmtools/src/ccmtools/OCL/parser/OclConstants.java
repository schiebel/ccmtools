/* CCM Tools : OCL metamodel
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003 Salomon Automation
 *
 * $Id: OclConstants.java,v 1.1 2004/07/20 16:21:23 teiniker Exp $
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

package ccmtools.OCL.parser;


/**
 * Constants for the OCL metamodel.
 *
 * @author Robert Lechner
 */
public interface OclConstants
{
    public static final String STEREOTYPE_PRECONDITION  = "precondition";
    public static final String STEREOTYPE_POSTCONDITION = "postcondition";
    public static final String STEREOTYPE_INVARIANT     = "invariant";

    public static final String OPERATOR_EQUAL   = "equal";
    public static final String OPERATOR_PLUS    = "plus";
    public static final String OPERATOR_MINUS   = "minus";
    public static final String OPERATOR_LT      = "less than";
    public static final String OPERATOR_LE      = "less or equal";
    public static final String OPERATOR_GE      = "greater or equal";
    public static final String OPERATOR_GT      = "greater than";
    public static final String OPERATOR_DIVIDE  = "divide";
    public static final String OPERATOR_MULT    = "multiply";
    public static final String OPERATOR_NEQUAL  = "not equal";
    public static final String OPERATOR_IMPLIES = "implies";
    public static final String OPERATOR_NOT     = "not";
    public static final String OPERATOR_OR      = "or";
    public static final String OPERATOR_XOR     = "xor";
    public static final String OPERATOR_AND     = "and";

    public static final String COLLECTIONKIND_COLLECTION = "Collection";
    public static final String COLLECTIONKIND_SEQUENCE   = "Sequence";
    public static final String COLLECTIONKIND_SET        = "Set";
    public static final String COLLECTIONKIND_BAG        = "Bag";

    public static final String PATHNAME_SEPARATOR = "::";

    public static final String KEYWORD_SELF   = "self";
    public static final String KEYWORD_RESULT = "result";

    public static final String TYPE_NAME_VOID        = "OclVoid";
    public static final String TYPE_NAME_BOOLEAN     = "Boolean";
    public static final String TYPE_NAME_REAL        = "Real";
    public static final String TYPE_NAME_INTEGER     = "Integer";
    public static final String TYPE_NAME_STRING      = "String";
    public static final String TYPE_NAME_ENUMERATION = "Enumeration";
}
