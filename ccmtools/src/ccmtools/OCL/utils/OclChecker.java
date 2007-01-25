/* CCM Tools : OCL helpers
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003, 2004 Salomon Automation
 *
 * $Id: OclChecker.java,v 1.1 2004/07/20 16:21:23 teiniker Exp $
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

package ccmtools.OCL.utils;

import oclmetamodel.*;
import ccmtools.OCL.parser.OclConstants;


/**
 * Functions for checking the semantic of OCL expressions.
 *
 * @author Robert Lechner
 */
public class OclChecker
{
    /**
     * The question cannot be answered.
     * Return value for 'isBoolean', 'isNumeric' and 'isSet'.
     */
    public static final short UNKNOWN = 0x00;

    /**
     * The answer is definitely 'yes'.
     * Return value for 'isBoolean', 'isNumeric' and 'isSet'.
     */
    public static final short YES     = 0x01;

    /**
     * The answer is definitely 'no'.
     * Return value for 'isBoolean', 'isNumeric' and 'isSet'.
     */
    public static final short NO      = 0x02;

    /**
     * The parameter is null.
     * Return value for 'isBoolean', 'isNumeric' and 'isSet'.
     */
    public static final short NULL    = 0x04;


    //////////////////////////////////////////////////////////////////////////


    /**
     * Checks if the given expression is of type 'Boolean'.
     *
     * @param expr  an OCL expression
     * @return YES, NO, NULL or UNKNOWN
     */
    public static short isBoolean( MExpression expr )
    {
        if( expr==null )
        {
            return NULL;
        }
        if( expr instanceof MLiteralExpression )
        {
            return isBoolean( (MLiteralExpression)expr );
        }
        else if( expr instanceof MOperationExpression )
        {
            return isBoolean( (MOperationExpression)expr );
        }
        else if( expr instanceof MIfExpression )
        {
            return isBoolean( (MIfExpression)expr );
        }
        else
        {
            return UNKNOWN;
        }
    }


    /**
     * Checks if the given expression is of type 'Integer' or 'Real'.
     *
     * @param expr  an OCL expression
     * @return YES, NO, NULL or UNKNOWN
     */
    public static short isNumeric( MExpression expr )
    {
        if( expr==null )
        {
            return NULL;
        }
        if( expr instanceof MLiteralExpression )
        {
            return isNumeric( (MLiteralExpression)expr );
        }
        else if( expr instanceof MOperationExpression )
        {
            return isNumeric( (MOperationExpression)expr );
        }
        else if( expr instanceof MIfExpression )
        {
            return isNumeric( (MIfExpression)expr );
        }
        else
        {
            return UNKNOWN;
        }
    }


    /**
     * Checks if the given expression is of type 'Set'.
     *
     * @param expr  an OCL expression
     * @return YES, NO, NULL or UNKNOWN
     */
    public static short isSet( MExpression expr )
    {
        if( expr==null )
        {
            return NULL;
        }
        if( expr instanceof MLiteralExpression )
        {
            return isSet( (MLiteralExpression)expr );
        }
        else if( expr instanceof MOperationExpression )
        {
            return isSet( (MOperationExpression)expr );
        }
        else if( expr instanceof MIfExpression )
        {
            return isSet( (MIfExpression)expr );
        }
        else
        {
            return UNKNOWN;
        }
    }


    //////////////////////////////////////////////////////////////////////////


    /**
     * Checks if the given expression is of type 'Boolean'.
     *
     * @param expr  an OCL expression
     * @return YES, NO, NULL or UNKNOWN
     */
    public static short isBoolean( MIfExpression expr )
    {
        if( expr==null )
        {
            return NULL;
        }
        short result1 = isBoolean( expr.getTrueExpression() );
        short result2 = isBoolean( expr.getFalseExpression() );
        if( result1==NO || result2==NO )
        {
            return NO;
        }
        if( result1==YES || result2==YES )
        {
            return YES;
        }
        return UNKNOWN;
    }


    /**
     * Checks if the given expression is of type 'Integer' or 'Real'.
     *
     * @param expr  an OCL expression
     * @return YES, NO, NULL or UNKNOWN
     */
    public static short isNumeric( MIfExpression expr )
    {
        if( expr==null )
        {
            return NULL;
        }
        short result1 = isNumeric( expr.getTrueExpression() );
        short result2 = isNumeric( expr.getFalseExpression() );
        if( result1==NO || result2==NO )
        {
            return NO;
        }
        if( result1==YES || result2==YES )
        {
            return YES;
        }
        return UNKNOWN;
    }


    /**
     * Checks if the given expression is of type 'Set'.
     *
     * @param expr  an OCL expression
     * @return YES, NO, NULL or UNKNOWN
     */
    public static short isSet( MIfExpression expr )
    {
        if( expr==null )
        {
            return NULL;
        }
        short result1 = isSet( expr.getTrueExpression() );
        short result2 = isSet( expr.getFalseExpression() );
        if( result1==NO || result2==NO )
        {
            return NO;
        }
        if( result1==YES || result2==YES )
        {
            return YES;
        }
        return UNKNOWN;
    }


    //////////////////////////////////////////////////////////////////////////


    /**
     * Checks if the given expression is of type 'Boolean'.
     *
     * @param expr  an OCL expression
     * @return YES, NO or NULL
     */
    public static short isBoolean( MLiteralExpression expr )
    {
        if( expr==null )
        {
            return NULL;
        }
        if( expr instanceof MBooleanLiteral )
        {
            return YES;
        }
        return NO;
    }


    /**
     * Checks if the given expression is of type 'Integer' or 'Real'.
     *
     * @param expr  an OCL expression
     * @return YES, NO or NULL
     */
    public static short isNumeric( MLiteralExpression expr )
    {
        if( expr==null )
        {
            return NULL;
        }
        if( expr instanceof MNumericLiteral )
        {
            return YES;
        }
        return NO;
    }


    /**
     * Checks if the given expression is of type 'Set'.
     *
     * @param expr  an OCL expression
     * @return YES, NO or NULL
     */
    public static short isSet( MLiteralExpression expr )
    {
        if( expr==null )
        {
            return NULL;
        }
        if( expr instanceof MCollectionLiteral )
        {
            MCollectionLiteral literal = (MCollectionLiteral)expr;
            if( literal.getKind().equals(OclConstants.COLLECTIONKIND_SET) )
            {
                return YES;
            }
        }
        return NO;
    }


    //////////////////////////////////////////////////////////////////////////


    /**
     * Checks if the given expression is of type 'Boolean'.
     *
     * @param expr  an OCL expression
     * @return YES, NO or NULL
     */
    public static short isBoolean( MOperationExpression expr )
    {
        if( expr==null )
        {
            return NULL;
        }
        String op = expr.getOperator();
        if( isRelationalOperator(op) || isLogicalOperator(op) )
        {
            return YES;
        }
        return NO;
    }


    /**
     * Checks if the given expression is of type 'Integer' or 'Real'.
     *
     * @param expr  an OCL expression
     * @return YES, NO or NULL
     */
    public static short isNumeric( MOperationExpression expr )
    {
        if( expr==null )
        {
            return NULL;
        }
        String op = expr.getOperator();
        if( isNumericOperator(op) )
        {
            return YES;
        }
        return NO;
    }


    /**
     * Checks if the given expression is of type 'Set'.
     *
     * @param expr  an OCL expression
     * @return YES, NO or NULL
     */
    public static short isSet( MOperationExpression expr )
    {
        if( expr==null )
        {
            return NULL;
        }
        String op = expr.getOperator();
        if( op.equals(OclConstants.OPERATOR_MINUS) )
        {
            if( isSet(expr.getLeftParameter())==YES ||
                isSet(expr.getRightParameter())==YES )
            {
                return YES;
            }
        }
        return NO;
    }


    //////////////////////////////////////////////////////////////////////////


    /**
     * Checks if the given operator is a relational operator.
     *
     * @param op  an OCL operator
     */
    public static boolean isRelationalOperator( String op )
    {
        return op.equals(OclConstants.OPERATOR_EQUAL) ||
            op.equals(OclConstants.OPERATOR_NEQUAL) ||
            op.equals(OclConstants.OPERATOR_LT) ||
            op.equals(OclConstants.OPERATOR_LE) ||
            op.equals(OclConstants.OPERATOR_GE) ||
            op.equals(OclConstants.OPERATOR_GT);
    }


    /**
     * Checks if the given operator is a logical operator.
     *
     * @param op  an OCL operator
     */
    public static boolean isLogicalOperator( String op )
    {
        return op.equals(OclConstants.OPERATOR_IMPLIES) ||
            op.equals(OclConstants.OPERATOR_NOT) ||
            op.equals(OclConstants.OPERATOR_OR) ||
            op.equals(OclConstants.OPERATOR_XOR) ||
            op.equals(OclConstants.OPERATOR_AND);
    }


    /**
     * Checks if the given operator is a numerical operator.
     *
     * @param op  an OCL operator
     */
    public static boolean isNumericOperator( String op )
    {
        return op.equals(OclConstants.OPERATOR_PLUS) ||
            op.equals(OclConstants.OPERATOR_MINUS) ||
            op.equals(OclConstants.OPERATOR_MULT) ||
            op.equals(OclConstants.OPERATOR_DIVIDE);
    }
}
