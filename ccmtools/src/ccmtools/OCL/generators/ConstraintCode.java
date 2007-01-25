/* CCM Tools : OCL generators
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003 Salomon Automation
 *
 * $Id: ConstraintCode.java,v 1.2 2004/08/28 09:17:22 teiniker Exp $
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

package ccmtools.OCL.generators;

import java.util.HashMap;

import oclmetamodel.MExpression;
import oclmetamodel.MOperationContext;
import ccmtools.Metamodel.BaseIDL.MContainer;
import ccmtools.Metamodel.BaseIDL.MIDLType;
import ccmtools.Metamodel.BaseIDL.MTyped;
import ccmtools.OCL.parser.OclConstants;
import ccmtools.OCL.parser.OclParserException;
import ccmtools.OCL.parser.OclParsetreeCreator;


/**
 * The code of one constraint.
 * We also use this class to transport parameters through the code generation process.
 *
 * @author Robert Lechner
 * @version $Date: 2004/08/28 09:17:22 $
 */
public abstract class ConstraintCode
{
    /**
     * helper statements
     */
    public String helpers_;
    
    /**
     * extra statements for "@pre"
     */
    public String preStatements_;

    /**
     * maps the initial value of a helper variable (only for "@pre"!) to the variable name
     */
    HashMap preHelpers_ = new HashMap();

    /**
     * the expression (source code)
     */
    public String expression_;

    /**
     * all necessary statements
     */
    public String statements_;


    /**
     * the OCL-expression of {@link expression_}
     */
    public MExpression constraint_;


    /**
     * the class (interface, component, etc.) of this constraint
     */
    public MContainer theClass_;

    /**
     * the IDL type if {@link theClass_} is null
     */
    public MIDLType idlType_;

    /**
     * operation context or null
     */
    public MOperationContext opCtxt_;

    /**
     * return type of {@link #opCtxt_} (or null)
     */
    public MTyped returnType_;


    /// default constructor
    protected ConstraintCode()
    {
        helpers_ = preStatements_ = expression_ = statements_ = "";
    }


    /// copy constructor (only {@link theClass_}, {@link opCtxt_} and {@link returnType_})
    protected ConstraintCode( ConstraintCode ref )
    {
        helpers_ = preStatements_ = expression_ = statements_ = "";
        theClass_ = ref.theClass_;
        opCtxt_ = ref.opCtxt_;
        returnType_ = ref.returnType_;
    }


    /**
     * Adds a new expression; creates an AND-operation, if an expression already exists.
     *
     * @param expr  source code
     * @param oclExpr  an OCL expression
     * @param creator  only for {@link OclParsetreeCreator#createOperationExpression}
     *
     * @throws IllegalStateException if {@link ccmtools.OCL.parser.OclParsetreeCreator#createOperationExpression} fails
     */
    void addExpression_And( String expr, MExpression oclExpr, OclParsetreeCreator creator )
     throws IllegalStateException
    {
        if( expr.length()>0 )
        {
            if( expression_.length()>0 )
            {
                expression_ = makeAnd(expression_,expr);
            }
            else
            {
                expression_ = expr;
            }
        }
        if( oclExpr!=null )
        {
            if( constraint_==null )
            {
                constraint_ = oclExpr;
            }
            else
            {
                try
                {
                    constraint_ = creator.createOperationExpression(constraint_,
                                                                    OclConstants.OPERATOR_AND, oclExpr);
                }
                catch( OclParserException e )
                {
                    throw new IllegalStateException(e.getMessage());
                }
            }
        }
    }


    /**
     * Adds a new expression; creates an OR-operation, if an expression already exists.
     *
     * @param expr  source code
     * @param oclExpr  an OCL expression
     * @param creator  only for {@link OclParsetreeCreator#createOperationExpression}
     *
     * @throws IllegalStateException if {@link ccmtools.OCL.parser.OclParsetreeCreator#createOperationExpression} fails
     */
    void addExpression_Or( String expr, MExpression oclExpr, OclParsetreeCreator creator )
     throws IllegalStateException
    {
        if( expr.length()>0 )
        {
            if( expression_.length()>0 )
            {
                expression_ = makeOr(expression_,expr);
            }
            else
            {
                expression_ = expr;
            }
        }
        if( oclExpr!=null )
        {
            if( constraint_==null )
            {
                constraint_ = oclExpr;
            }
            else
            {
                try
                {
                    constraint_ = creator.createOperationExpression(constraint_,
                                                                    OclConstants.OPERATOR_OR, oclExpr);
                }
                catch( OclParserException e )
                {
                    throw new IllegalStateException(e.getMessage());
                }
            }
        }
    }


    /**
     * Creates an AND-operation.
     */
    protected abstract String makeAnd( String param1, String param2 );

    /**
     * Creates an OR-operation.
     */
    protected abstract String makeOr( String param1, String param2 );
}
