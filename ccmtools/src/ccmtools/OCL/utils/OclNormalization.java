/* CCM Tools : OCL helpers
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003, 2004 Salomon Automation
 *
 * $Id: OclNormalization.java,v 1.2 2005/02/02 09:55:00 rlechner Exp $
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
import ccmtools.OCL.parser.OclParserException;
import ccmtools.OCL.parser.OclConstants;

import ccmtools.Metamodel.BaseIDL.*;

import java.util.Vector;
import java.util.Iterator;


/**
 * Normalizes OCL expressions.
 * <p> The normalisation includes:
 <ul>
 <li>  merging of MFile and MPackage             </li>
 <li>  removal of unnecessary operations         </li>
 <li>  try to calculate the type of expressions  </li>
 </ul>
 *
 * @author Robert Lechner
 */
public class OclNormalization
{
    protected OclElementCreator creator_;
    protected String contextSelfName_;


    public OclNormalization( OclElementCreator creator )
    {
        if( creator==null )
        {
            throw new NullPointerException();
        }
        creator_ = creator;
        contextSelfName_ = OclConstants.KEYWORD_SELF;
    }


    public MFile normalize( MFile oldFile ) throws OclParserException
    {
        Vector packages = new Vector();
        int size = 0;
        Iterator it = oldFile.getPackages().iterator();
        while( it.hasNext() )
        {
            MPackage pkg = (MPackage)it.next();
            String name = pkg.getName();
            boolean newPackage = true;
            for( int index=0; index<size; index++ )
            {
                MPackage p2 = (MPackage)packages.get(index);
                if( p2.getName().equals(name) )
                {
                    Iterator i2 = pkg.getContexts().iterator();
                    while( i2.hasNext() )
                    {
                        creator_.add( p2, (MContext)i2.next() );
                    }
                    newPackage = false;
                    break;
                }
            }
            if( newPackage )
            {
                packages.add(pkg);
                size++;
            }
        }
        MFile newFile = creator_.createFile();
        for( int index=0; index<size; index++ )
        {
	    MPackage new_pkg = normalize( (MPackage)packages.get(index) );
            creator_.add( newFile, new_pkg );
        }
        return newFile;
    }


    public MPackage normalize( MPackage oldPackage ) throws OclParserException
    {
        Vector classContext = new Vector();
        Vector operationContext = new Vector();
        int classCount=0, operationCount=0, index;
        Iterator it = oldPackage.getContexts().iterator();
        while( it.hasNext() )
        {
            Object obj = it.next();
            if( obj instanceof MClassifierContext )
            {
                MClassifierContext ctxt = normalize( (MClassifierContext)obj );
                String className = ctxt.getClassName();
                String selfName = ctxt.getSelfName();
                boolean newContext = true;
                for( index=0; index<classCount; index++ )
                {
                    MClassifierContext c2 = (MClassifierContext)classContext.get(index);
                    if( c2.getClassName().equals(className) && c2.getSelfName().equals(selfName) )
                    {
                        copy(ctxt,c2);
                        newContext = false;
                        break;
                    }
                }
                if( newContext )
                {
                    classContext.add(ctxt);
                    classCount++;
                }
            }
            else if( obj instanceof MOperationContext )
            {
                MOperationContext ctxt = normalize( (MOperationContext)obj );
                String name = calculateIdentifier(ctxt);
                boolean newContext = true;
                for( index=0; index<operationCount; index++ )
                {
                    MOperationContext c2 = (MOperationContext)operationContext.get(index);
                    if( calculateIdentifier(c2).equals(name) )
                    {
                        copy(ctxt,c2);
                        newContext = false;
                        break;
                    }
                }
                if( newContext )
                {
                    operationContext.add(ctxt);
                    operationCount++;
                }
            }
            else
            {
                throw new OclParserException("unknown context");
            }
        }
        MPackage newPackage = creator_.createPackage( oldPackage.getName() );
        for( index=0; index<classCount; index++ )
        {
            creator_.add( newPackage, (MClassifierContext)classContext.get(index) );
        }
        for( index=0; index<operationCount; index++ )
        {
            creator_.add( newPackage, (MOperationContext)operationContext.get(index) );
        }
        return newPackage;
    }


    /**
     * Calculates the full name of an operation context.
     */
    public static String calculateIdentifier( MOperationContext ctxt ) throws OclParserException
    {
        String name = ctxt.getClassName() + OclConstants.PATHNAME_SEPARATOR + ctxt.getOperationName() + "(";
        Iterator it = ctxt.getParameters().iterator();
        if( it.hasNext() )
        {
            MFormalParameter fp = (MFormalParameter)it.next();
            name += makeName(fp.getType());
            while( it.hasNext() )
            {
                fp = (MFormalParameter)it.next();
                name += ","+makeName(fp.getType());
            }
        }
        return name + ")";
    }


    /**
     * Calculates the name of a type specifier.
     */
    public static String makeName( MTypeSpecifier ts ) throws OclParserException
    {
        if( ts instanceof MSimpleTypeSpec )
        {
            return ((MSimpleTypeSpec)ts).getName();
        }
        if( ts instanceof MCollectionTypeSpec )
        {
            MCollectionTypeSpec cts = (MCollectionTypeSpec)ts;
            return cts.getKind()+"("+cts.getType().getName()+")";
        }
        throw new OclParserException("unknown type specifier");
    }


    private void copy( MContext source, MContext destination ) throws OclParserException
    {
        Iterator it1 = source.getConstraints().iterator();
        while( it1.hasNext() )
        {
            creator_.add( destination, (MConstraint)it1.next() );
        }
        Iterator it2 = source.getDefinitions().iterator();
        while( it2.hasNext() )
        {
            creator_.add( destination, (MDefinition)it2.next() );
        }
    }


    public MClassifierContext normalize( MClassifierContext oldContext ) throws OclParserException
    {
        contextSelfName_ = oldContext.getSelfName();
        MClassifierContext newContext = creator_.createClassifierContext(
            OclConstants.KEYWORD_SELF, oldContext.getClassName() );
        Iterator it1 = oldContext.getConstraints().iterator();
        while( it1.hasNext() )
        {
	    MConstraint con = normalize( (MConstraint)it1.next() );
            creator_.add( newContext, con );
        }
        Iterator it2 = oldContext.getDefinitions().iterator();
        while( it2.hasNext() )
        {
	    MDefinition def = normalize( (MDefinition)it2.next() );
            creator_.add( newContext, def );
        }
        contextSelfName_ = OclConstants.KEYWORD_SELF;
        return newContext;
    }


    public MOperationContext normalize( MOperationContext oldContext ) throws OclParserException
    {
        contextSelfName_ = OclConstants.KEYWORD_SELF;
        MOperationContext newContext = creator_.createOperationContext(
            oldContext.getClassName(), oldContext.getOperationName() );
        //
        newContext.setReturnType( oldContext.getReturnType() );
        Iterator it3 = oldContext.getParameters().iterator();
        while( it3.hasNext() )
        {
            creator_.add( newContext, (MFormalParameter)it3.next() );
        }
        //
        Iterator it1 = oldContext.getConstraints().iterator();
        while( it1.hasNext() )
        {
	        MConstraint con = normalize( (MConstraint)it1.next() );
            creator_.add( newContext, con );
        }
        Iterator it2 = oldContext.getDefinitions().iterator();
        while( it2.hasNext() )
        {
	        MDefinition def = normalize( (MDefinition)it2.next() );
            creator_.add( newContext, def );
        }
        return newContext;
    }


    public MConstraint normalize( MConstraint oldCon ) throws OclParserException
    {
	    MConstraintExpression newExpr = normalize( oldCon.getExpression() );
        return creator_.createConstraint( oldCon.getStereotype(), oldCon.getName(), newExpr );
    }


    public MConstraintExpression normalize( MConstraintExpression oldExpr ) throws OclParserException
    {
        MConstraintExpression newExpr = creator_.createConstraintExpression();
        Iterator it = oldExpr.getStatements().iterator();
        while( it.hasNext() )
        {
	        MLetStatement newLet = normalize( (MLetStatement)it.next() );
            creator_.add( newExpr, newLet );
        }
        newExpr.setExpression( normalize( oldExpr.getExpression() ) );
        return newExpr;
    }


    public MDefinition normalize( MDefinition oldDef ) throws OclParserException
    {
        MDefinition newDef = creator_.createDefinition( oldDef.getName() );
        Iterator it = oldDef.getStatements().iterator();
        while( it.hasNext() )
        {
	        MLetStatement newLet = normalize( (MLetStatement)it.next() );
            creator_.add( newDef, newLet );
        }
        return newDef;
    }


    public MLetStatement normalize( MLetStatement oldLet ) throws OclParserException
    {
        MLetStatement newLet = creator_.createLetStatement( oldLet.getName() );
        Iterator it = oldLet.getParameters().iterator();
        while( it.hasNext() )
        {
            creator_.add( newLet, (MFormalParameter)it.next() );
        }
        newLet.setType( oldLet.getType() );
        newLet.setExpression( normalize( oldLet.getExpression() ) );
        return newLet;
    }


    public MExpression normalize( MExpression expr ) throws OclParserException
    {
        MExpression result;
        if( expr instanceof MIfExpression )
        {
            result = normalize_IfExpression( (MIfExpression)expr );
        }
        else if( expr instanceof MCollectionLiteral )
        {
            result = normalize_CollectionLiteral( (MCollectionLiteral)expr );
        }
        else if( expr instanceof MOperationExpression )
        {
            result = normalize_OperationExpression( (MOperationExpression)expr );
        }
        else if( expr instanceof MPostfixExpression )
        {
            result = normalize_PostfixExpression( (MPostfixExpression)expr );
        }
        else if( expr instanceof MPropertyCall )
        {
            result = normalize_PropertyCall( (MPropertyCall)expr );
        }
        else
        {
            result = expr;
        }
        setAndGetOclType(result);
        return result;
    }


    public MExpression normalize_IfExpression( MIfExpression expr ) throws OclParserException
    {
        MExpression condition = normalize( expr.getCondition() );
        MExpression trueExpr = normalize( expr.getTrueExpression() );
        MExpression falseExpr = normalize( expr.getFalseExpression() );
        if( condition instanceof MBooleanLiteral )
        {
            if( ((MBooleanLiteral)condition).isValue() )
            {
                return trueExpr;
            }
            return falseExpr;
        }
        if( equals(trueExpr, falseExpr) )
        {
            return trueExpr;
        }
        return creator_.createIfExpression(condition,trueExpr,falseExpr);
    }


    public MCollectionLiteral normalize_CollectionLiteral( MCollectionLiteral oldLiteral )
     throws OclParserException
    {
	    int count1=countElements(oldLiteral);
        String kind = oldLiteral.getKind();
        MCollectionLiteral newLiteral = creator_.createCollectionLiteral(kind);
        Iterator it = oldLiteral.getItems().iterator();
        while( it.hasNext() )
        {
            Object obj = it.next();
            if( obj instanceof MCollectionItem )
            {
		        MCollectionItem item = (MCollectionItem)obj;
		        MExpression expr1 = item.getExpression();
                MExpression expr2 = normalize( expr1 );
                if( expr2 instanceof MCollectionLiteral )
                {
                    Iterator it2 = ((MCollectionLiteral)expr2).getItems().iterator();
                    while( it2.hasNext() )
                    {
			            MCollectionPart part = (MCollectionPart)it2.next();
                        creator_.add( newLiteral, part );
                    }
                }
                else
                {
		            MCollectionItem newItem = creator_.createCollectionItem(expr2);
                    creator_.add( newLiteral, newItem );
                }
            }
            else if( obj instanceof MCollectionRange )
            {
                MCollectionRange range = (MCollectionRange)obj;
                MExpression expr1 = normalize( range.getLowerRange() );
                MExpression expr2 = normalize( range.getUpperRange() );
		        MCollectionRange newRange = creator_.createCollectionRange(expr1,expr2);
                creator_.add( newLiteral, newRange );
            }
            else
            {
                creator_.add( newLiteral, (MCollectionPart)obj );   // this should never happen
            }
        }
    	int count2 = countElements(newLiteral);
    	if( count2!=count1 )
    	{
    	    System.err.println("normalize_CollectionLiteral: size mismatch: count1="+
                                            count1+", count2="+count2);
            return oldLiteral;
    	}
    	return newLiteral;
    }

    private static int countElements( MCollectionLiteral literal )
    {
    	int result = 0;
    	Iterator it = literal.getItems().iterator();
    	while( it.hasNext() )
    	{
    	    Object obj = it.next();
    	    if( obj instanceof MCollectionItem )
    	    {
        		MExpression expr = ((MCollectionItem)obj).getExpression();
        		if( expr instanceof MCollectionLiteral )
        		{
        		    result += countElements((MCollectionLiteral)expr);
        		}
        		else
        		{
        		    result++;
        		}
    	    }
    	    else
    	    {
        		result++;
    	    }
    	}
    	return result;
    }


    public MPropertyCall normalize_PropertyCall( MPropertyCall oldCall ) throws OclParserException
    {
        MPropertyCall newCall;
        String name = oldCall.getName();
        if( name.equals(contextSelfName_) || name.equals(OclConstants.KEYWORD_SELF) )
        {
            newCall = creator_.createPropertyCall(OclConstants.KEYWORD_SELF,false);
            name = OclConstants.KEYWORD_SELF;
        }
        else if( name.equals(OclConstants.KEYWORD_RESULT) )
        {
            newCall = creator_.createPropertyCall(OclConstants.KEYWORD_RESULT,false);
            name = OclConstants.KEYWORD_RESULT;
        }
        else
        {
            newCall = creator_.createPropertyCall( name, oldCall.isPrevious() );
            MActualParameters qualifiers = oldCall.getQualifiers();
            if( qualifiers!=null )
            {
                newCall.setQualifiers( normalize(qualifiers) );
            }
            MPropertyCallParameters parameters = oldCall.getCallParameters();
            if( parameters!=null )
            {
                newCall.setCallParameters( normalize(parameters) );
            }
        }
        return newCall;
    }


    public MActualParameters normalize( MActualParameters expr ) throws OclParserException
    {
        MActualParameters ap = null;
        Iterator it = expr.getExpressions().iterator();
        while( it.hasNext() )
        {
            MExpression e = normalize( (MExpression)it.next() );
            if( ap==null )
            {
                ap = creator_.createActualParameters(e);
            }
            else
            {
                creator_.add(ap,e);
            }
        }
        return ap;
    }


    public MPropertyCallParameters normalize( MPropertyCallParameters expr ) throws OclParserException
    {
        MDeclarator decl1=expr.getDeclarator(), decl2=null;
        if( decl1!=null )
        {
            decl2 = normalize(decl1);
        }
        MActualParameters parameters1=expr.getParameters(), parameters2=null;
        if( parameters1!=null )
        {
            parameters2 = normalize(parameters1);
        }
        return creator_.createPropertyCallParameters(decl2,parameters2);
    }


    public MDeclarator normalize( MDeclarator oldDecl ) throws OclParserException
    {
        MExpression optExpr = oldDecl.getOptExpression();
        if( optExpr==null )
        {
            return oldDecl;
        }
        MDeclarator newDecl = null;
        Iterator it = oldDecl.getNames().iterator();
        while( it.hasNext() )
        {
            String name = ((MName)it.next()).getValue();
            if( newDecl==null )
            {
                newDecl = creator_.createDeclarator(name);
            }
            else
            {
                creator_.add(newDecl,name);
            }
        }
        newDecl.setOptExpression( normalize(optExpr) );
        newDecl.setOptName( oldDecl.getOptName() );
        newDecl.setOptType( oldDecl.getOptType() );
        newDecl.setSimpleType( oldDecl.getSimpleType() );
        return newDecl;
    }


    public MExpression normalize_PostfixExpression( MPostfixExpression pfe ) throws OclParserException
    {
        MExpression expr = normalize( pfe.getExpression() );
        MPropertyCall pc = normalize_PropertyCall( pfe.getPropertyCall() );
        if( expr instanceof MPropertyCall )
        {
            String name = ((MPropertyCall)expr).getName();
            if( name.equals(OclConstants.KEYWORD_SELF) )
            {
                return pc;
            }
        }
        if( pc.getName().equals(OclConstants.KEYWORD_SELF) )
        {
            return expr;
        }
        MPostfixExpression result = creator_.createPostfixExpression( expr, pc, pfe.isCollection() );
        result.setOclType( pc.getOclType() );
        return result;
    }


    public MExpression normalize_OperationExpression( MOperationExpression opExpr ) throws OclParserException
    {
        String operator = opExpr.getOperator();
        MExpression expr2 = normalize( opExpr.getRightParameter() );
        MExpression expr1org = opExpr.getLeftParameter();
        if( expr1org==null )
        {
            return normalizeUnaryOperation( operator, expr2 );
        }
        MExpression expr1 = normalize(expr1org);
        if( operator.equals(OclConstants.OPERATOR_IMPLIES) )
        {
            // (x => y) == (!x | y)
            MExpression not1 = creator_.createOperationExpression(null,OclConstants.OPERATOR_NOT,expr1);
            expr1 = normalize(not1);
            operator = OclConstants.OPERATOR_OR;
        }
        if( (expr1 instanceof MIntegerLiteral) && (expr2 instanceof MIntegerLiteral) )
        {
            MIntegerLiteral lit1 = (MIntegerLiteral)expr1;
            MIntegerLiteral lit2 = (MIntegerLiteral)expr2;
            int value1 = lit1.getValue();
            int value2 = lit2.getValue();
            if( operator.equals(OclConstants.OPERATOR_PLUS) )
            {
                String text = "(" + lit1.getText() + ")+(" + lit2.getText() + ")";
                return creator_.createIntegerLiteral(value1+value2,text);
            }
            if( operator.equals(OclConstants.OPERATOR_MINUS) )
            {
                String text = "(" + lit1.getText() + ")-(" + lit2.getText() + ")";
                return creator_.createIntegerLiteral(value1-value2,text);
            }
            if( operator.equals(OclConstants.OPERATOR_MULT) )
            {
                String text = "(" + lit1.getText() + ")*(" + lit2.getText() + ")";
                return creator_.createIntegerLiteral(value1*value2,text);
            }
            if( operator.equals(OclConstants.OPERATOR_EQUAL) )
            {
                return creator_.createBooleanLiteral( value1==value2 );
            }
            if( operator.equals(OclConstants.OPERATOR_NEQUAL) )
            {
                return creator_.createBooleanLiteral( value1!=value2 );
            }
            if( operator.equals(OclConstants.OPERATOR_LT) )
            {
                return creator_.createBooleanLiteral( value1<value2 );
            }
            if( operator.equals(OclConstants.OPERATOR_LE) )
            {
                return creator_.createBooleanLiteral( value1<=value2 );
            }
            if( operator.equals(OclConstants.OPERATOR_GT) )
            {
                return creator_.createBooleanLiteral( value1>value2 );
            }
            if( operator.equals(OclConstants.OPERATOR_GE) )
            {
                return creator_.createBooleanLiteral( value1>=value2 );
            }
        }
        else if( (expr1 instanceof MNumericLiteral) && (expr2 instanceof MNumericLiteral) )
        {
            String text1 = ((MNumericLiteral)expr1).getText();
            String text2 = ((MNumericLiteral)expr2).getText();
            double value1, value2;
            if( expr1 instanceof MIntegerLiteral )
            {
                value1 = ((MIntegerLiteral)expr1).getValue();
            }
            else
            {
                value1 = ((MRealLiteral)expr1).getValue();
            }
            if( expr2 instanceof MIntegerLiteral )
            {
                value2 = ((MIntegerLiteral)expr2).getValue();
            }
            else
            {
                value2 = ((MRealLiteral)expr2).getValue();
            }
            if( operator.equals(OclConstants.OPERATOR_PLUS) )
            {
                String text = "(" + text1 + ")+(" + text2 + ")";
                return creator_.createRealLiteral(value1+value2,text);
            }
            if( operator.equals(OclConstants.OPERATOR_MINUS) )
            {
                String text = "(" + text1 + ")-(" + text2 + ")";
                return creator_.createRealLiteral(value1-value2,text);
            }
            if( operator.equals(OclConstants.OPERATOR_MULT) )
            {
                String text = "(" + text1 + ")*(" + text2 + ")";
                return creator_.createRealLiteral(value1*value2,text);
            }
        }
        else if( (expr1 instanceof MBooleanLiteral) )
        {
            MExpression res = normalizeBooleanLiteral( (MBooleanLiteral)expr1, operator, expr2 );
            if( res!=null )
            {
                return res;
            }
        }
        else if( (expr2 instanceof MBooleanLiteral) )
        {
            MExpression res = normalizeBooleanLiteral( (MBooleanLiteral)expr2, operator, expr1 );
            if( res!=null )
            {
                return res;
            }
        }
        if( equals(expr1, expr2) )
        {
            if( operator.equals(OclConstants.OPERATOR_OR) ||
                operator.equals(OclConstants.OPERATOR_AND) )
            {
                return expr1;
            }
            if( operator.equals(OclConstants.OPERATOR_XOR) ||
                operator.equals(OclConstants.OPERATOR_NEQUAL) )
            {
                return creator_.createBooleanLiteral(false);
            }
            if( operator.equals(OclConstants.OPERATOR_EQUAL) )
            {
                return creator_.createBooleanLiteral(true);
            }
        }
        return creator_.createOperationExpression(expr1,operator,expr2);
    }


    private MExpression normalizeBooleanLiteral( MBooleanLiteral lit1, String operator,
                                                 MExpression expr2 )  throws OclParserException
    {
        boolean value1 = lit1.isValue();
        if( operator.equals(OclConstants.OPERATOR_OR) )
        {
            if( value1 )
            {
                return creator_.createBooleanLiteral(true);
            }
            return expr2;
        }
        if( operator.equals(OclConstants.OPERATOR_AND) )
        {
            if( value1 )
            {
                return expr2;
            }
            return creator_.createBooleanLiteral(false);
        }
        if( operator.equals(OclConstants.OPERATOR_XOR) )
        {
            if( value1 )
            {
                return normalizeUnaryOperation( OclConstants.OPERATOR_NOT, expr2 );
            }
            return expr2;
        }
        return null;
    }


    private MExpression normalizeUnaryOperation( String operator, MExpression expression )  throws OclParserException
    {
        if( operator.equals(OclConstants.OPERATOR_NOT) )
        {
            if( expression instanceof MBooleanLiteral )
            {
                boolean value = ((MBooleanLiteral)expression).isValue();
                return creator_.createBooleanLiteral(!value);
            }
            if( expression instanceof MOperationExpression )
            {
                MOperationExpression op = (MOperationExpression)expression;
                String op2 = op.getOperator();
                MExpression e1 = op.getLeftParameter();
                MExpression e2 = op.getRightParameter();
                if( op2.equals(OclConstants.OPERATOR_NOT) )
                {
                    return e2;
                }
                if( op2.equals(OclConstants.OPERATOR_EQUAL) )
                {
                    MExpression result = creator_.createOperationExpression(e1, OclConstants.OPERATOR_NEQUAL, e2);
                    result.setOclType( creator_.createTypeBoolean() );
                    return result;
                }
                if( op2.equals(OclConstants.OPERATOR_NEQUAL) )
                {
                    MExpression result = creator_.createOperationExpression(e1, OclConstants.OPERATOR_EQUAL, e2);
                    result.setOclType( creator_.createTypeBoolean() );
                    return result;
                }
            }
        }
        else if( operator.equals(OclConstants.OPERATOR_MINUS) )
        {
            if( expression instanceof MIntegerLiteral )
            {
                MIntegerLiteral literal = (MIntegerLiteral)expression;
                int value = literal.getValue();
                String text = "-("+literal.getText()+")";
                return creator_.createIntegerLiteral(-value,text);
            }
            if( expression instanceof MRealLiteral )
            {
                MRealLiteral literal = (MRealLiteral)expression;
                double value = literal.getValue();
                String text = "-("+literal.getText()+")";
                return creator_.createRealLiteral(-value,text);
            }
        }
        return creator_.createOperationExpression(null,operator,expression);
    }


    //////////////////////////////////////////////////////////////////////////


    private boolean equals( MExpression expr1, MExpression expr2 )
    {
        if( expr1==null && expr2==null )
        {
            return true;
        }
        if( expr1==null || expr2==null )
        {
            return false;
        }
        if( (expr1 instanceof MBooleanLiteral)&&(expr2 instanceof MBooleanLiteral) )
        {
            return ((MBooleanLiteral)expr1).isValue() == ((MBooleanLiteral)expr2).isValue();
        }
        if( (expr1 instanceof MIntegerLiteral)&&(expr2 instanceof MIntegerLiteral) )
        {
            return ((MIntegerLiteral)expr1).getValue() == ((MIntegerLiteral)expr2).getValue();
        }
        if( (expr1 instanceof MRealLiteral)&&(expr2 instanceof MRealLiteral) )
        {
            return ((MRealLiteral)expr1).getValue() == ((MRealLiteral)expr2).getValue();
        }
        if( (expr1 instanceof MStringLiteral)&&(expr2 instanceof MStringLiteral) )
        {
            return ((MStringLiteral)expr1).getValue().equals( ((MStringLiteral)expr2).getValue() );
        }
        if( (expr1 instanceof MOperationExpression)&&(expr2 instanceof MOperationExpression) )
        {
            MOperationExpression oe1 = (MOperationExpression)expr1;
            MOperationExpression oe2 = (MOperationExpression)expr2;
            String operator = oe1.getOperator();
            if( oe2.getOperator().equals(operator) )
            {
                MExpression o1e1 = oe1.getLeftParameter();
                MExpression o1e2 = oe1.getRightParameter();
                MExpression o2e1 = oe2.getLeftParameter();
                MExpression o2e2 = oe2.getRightParameter();
                if( equals(o1e1,o2e1) && equals(o1e2,o2e2) )
                {
                    return true;
                }
                if( operator.equals(OclConstants.OPERATOR_EQUAL) ||
                    operator.equals(OclConstants.OPERATOR_PLUS) ||
                    operator.equals(OclConstants.OPERATOR_MULT) ||
                    operator.equals(OclConstants.OPERATOR_NEQUAL) ||
                    operator.equals(OclConstants.OPERATOR_OR) ||
                    operator.equals(OclConstants.OPERATOR_XOR) ||
                    operator.equals(OclConstants.OPERATOR_AND) )
                {
                    if( equals(o1e1,o2e2) && equals(o1e2,o2e1) )
                    {
                        return true;
                    }
                }
                // TODO: for example: (x and y) and z == x and (y and z)
            }
            return false;
        }
        // TODO: PropertyCall, PostfixExpression
        return false;
    }


    //////////////////////////////////////////////////////////////////////////

    /**
     * Calculates and sets the type of an expression.
     *
     * @param expr  an expression
     * @return the type of the expression (or null)
     */
    protected OclType setAndGetOclType( MExpression expr )
    {
        OclType type = expr.getOclType();
        if( type!=null )
        {
            return type;
        }
        if( expr instanceof MEnumLiteral )
        {
            type = creator_.createTypeEnumeration();
        }
        else if( expr instanceof MIntegerLiteral )
        {
            type = creator_.createTypeInteger();
        }
        else if( expr instanceof MRealLiteral )
        {
            type = creator_.createTypeReal();
        }
        else if( expr instanceof MBooleanLiteral )
        {
            type = creator_.createTypeBoolean();
        }
        else if( expr instanceof MStringLiteral )
        {
            type = creator_.createTypeString();
        }
        else if( expr instanceof MCollectionLiteral )
        {
            type = getOclType( (MCollectionLiteral)expr );
        }
        else if( expr instanceof MIfExpression )
        {
            type = getOclType( (MIfExpression)expr );
        }
        else if( expr instanceof MOperationExpression )
        {
            type = getOclType( (MOperationExpression)expr );
        }
        else if( expr instanceof MPropertyCall )
        {
            type = getOclType( (MPropertyCall)expr );
        }
        else if( expr instanceof MPostfixExpression )
        {
            type = getOclType( (MPostfixExpression)expr );
        }
        expr.setOclType(type);
        return type;
    }


    protected OclType getOclType( MCollectionLiteral collection )
    {
        OclType type = getItemType(collection);
        String kind = collection.getKind();
        if( kind.equals(OclConstants.COLLECTIONKIND_SET) )
        {
            return creator_.createTypeSet(type);
        }
        if( kind.equals(OclConstants.COLLECTIONKIND_BAG) )
        {
            return creator_.createTypeBag(type);
        }
        if( kind.equals(OclConstants.COLLECTIONKIND_SEQUENCE) )
        {
            return creator_.createTypeSequence(type);
        }
        return creator_.createTypeCollection(type);
    }

    protected OclType getItemType( MCollectionLiteral collection )
    {
        Iterator it = collection.getItems().iterator();
        while( it.hasNext() )
        {
            Object obj = it.next();
            if( obj instanceof MCollectionRange )
            {
                return creator_.createTypeInteger();
            }
            if( obj instanceof MCollectionItem )
            {
                OclType type = setAndGetOclType( ((MCollectionItem)obj).getExpression() );
                if( type!=null )
                {
                    return type;
                }
            }
        }
        return null;
    }


    protected OclType getOclType( MIfExpression expression )
    {
        OclType type1 = setAndGetOclType( expression.getTrueExpression() );
        OclType type2 = setAndGetOclType( expression.getFalseExpression() );
        return getCommonOclType(type1,type2);
    }


    protected OclType getOclType( MOperationExpression expression )
    {
        String operator = expression.getOperator();
        if( operator.equals(OclConstants.OPERATOR_DIVIDE) )
        {
            return creator_.createTypeReal();
        }
        if( OclChecker.isRelationalOperator(operator) || OclChecker.isLogicalOperator(operator) )
        {
            return creator_.createTypeBoolean();
        }
        MExpression expr1 = expression.getLeftParameter();
        MExpression expr2 = expression.getRightParameter();
        OclType type2 = setAndGetOclType(expr2);
        if( expr1==null )
        {
            return type2;
        }
        OclType type1 = setAndGetOclType(expr1);
        return getCommonOclType(type1,type2);
    }


    /**
     * Returns the type of a property call or null.
     * <p><b>Note:</b> This implementation returns always null!
     * Only a subclass with access to the declaration of the properties
     * can return the correct type.
     */
    protected OclType getOclType( MPropertyCall expression )
    {
        return null;    // we don't know the type
    }


    /**
     * Returns the type of a postfix expression or null.
     */
    protected OclType getOclType( MPostfixExpression pfe )
    {
        return setAndGetOclType(pfe.getPropertyCall());  // an approximation
    }


    /**
     * Calculates the common type of two OCL types.
     *
     * @return the common type or null
     */
    protected OclType getCommonOclType( OclType type1, OclType type2 )
    {
        if( type1==null || type2==null )
        {
            return null;
        }
        String name1 = type1.getName();
        String name2 = type2.getName();
        if( name1.equals(name2) )
        {
            return type1;
        }
        if( (type1 instanceof OclReal) && (type2 instanceof OclReal) )
        {
            // type1 or type2 is of type 'OclInteger'
            return creator_.createTypeReal();
        }
        if( (type1 instanceof OclCollection) && (type2 instanceof OclCollection) )
        {
            OclType item = getCommonOclType( ((OclCollection)type1).getType(), ((OclCollection)type2).getType() );
            if( (type1 instanceof OclSet) && (type2 instanceof OclSet) )
            {
                return creator_.createTypeSet(item);
            }
            if( (type1 instanceof OclBag) && (type2 instanceof OclBag) )
            {
                return creator_.createTypeBag(item);
            }
            if( (type1 instanceof OclSequence) && (type2 instanceof OclSequence) )
            {
                return creator_.createTypeSequence(item);
            }
            return creator_.createTypeCollection(item);
        }
        return null;
    }


    /**
     * Converts a type specifier to an instance of {@link oclmetamodel.OclType}.
     */
    public OclType convertOclType( MTypeSpecifier ts )
    {
        return convertOclType(ts, creator_);
    }


    /**
     * Converts a type specifier to an instance of {@link oclmetamodel.OclType}.
     */
    public static OclType convertOclType( MTypeSpecifier ts, OclElementCreator creator )
    {
        if( ts==null )
        {
            return null;
        }
        if( ts instanceof MSimpleTypeSpec )
        {
            return getOclTypeByName( ((MSimpleTypeSpec)ts).getName(), creator );
        }
        if( ts instanceof MCollectionTypeSpec )
        {
            MCollectionTypeSpec cts = (MCollectionTypeSpec)ts;
            OclType item = getOclTypeByName( cts.getType().getName(), creator );
            String kind = cts.getKind();
            if( kind.equals(OclConstants.COLLECTIONKIND_SEQUENCE) )
            {
                return creator.createTypeSequence(item);
            }
            if( kind.equals(OclConstants.COLLECTIONKIND_SET) )
            {
                return creator.createTypeSet(item);
            }
            if( kind.equals(OclConstants.COLLECTIONKIND_BAG) )
            {
                return creator.createTypeBag(item);
            }
            return creator.createTypeCollection(item);
        }
        return null;
    }


    /**
     * Converts the name of an OCL type to an instance of {@link oclmetamodel.OclType}.
     */
    public OclType getOclTypeByName( String name )
    {
        return getOclTypeByName(name, creator_);
    }


    /**
     * Converts the name of an OCL type to an instance of {@link oclmetamodel.OclType}.
     */
    public static OclType getOclTypeByName( String name, OclElementCreator creator )
    {
        if( name.equals(OclConstants.TYPE_NAME_INTEGER) )
        {
            return creator.createTypeInteger();
        }
        if( name.equals(OclConstants.TYPE_NAME_REAL) )
        {
            return creator.createTypeReal();
        }
        if( name.equals(OclConstants.TYPE_NAME_BOOLEAN) )
        {
            return creator.createTypeBoolean();
        }
        if( name.equals(OclConstants.TYPE_NAME_STRING) )
        {
            return creator.createTypeString();
        }
        if( name.equals(OclConstants.TYPE_NAME_VOID) )
        {
            return creator.createTypeVoid();
        }
        if( name.equals(OclConstants.TYPE_NAME_ENUMERATION) )
        {
            return creator.createTypeEnumeration();
        }
        return creator.createTypeUser(name);
    }


    /**
     * Calculates the OCL type of a given IDL object.
     */
    public OclType makeOclType( MTyped object )
    {
        if( object==null )
        {
            System.err.println("OclNormalization.makeOclType: object==null");
            return null;
        }
        MIDLType idlType = object.getIdlType();
        if( idlType==null )
        {
            if( object instanceof MOperationDef )
            {
                // an operation with no return type
                return null;
            }
            System.err.println("OclNormalization.makeOclType: idlType==null  object=="+
                object.getClass().getName() );
            return null;
        }
        if( idlType instanceof MPrimitiveDef )
        {
            return getOclTypeByName(makeOclTypeName( ((MPrimitiveDef)idlType).getKind() ));
        }
        if( (idlType instanceof MStringDef)||(idlType instanceof MWstringDef) )
        {
            return creator_.createTypeString();
        }
        if( (idlType instanceof MArrayDef)||(idlType instanceof MSequenceDef) )
        {
            OclType item = makeOclType( (MTyped)idlType );
            return creator_.createTypeSequence(item);
        }
        if( idlType instanceof MContained )
        {
            String id = ((MContained)idlType).getIdentifier();
            if( idlType instanceof MAliasDef )
            {
                OclType result = makeOclType( (MTyped)idlType );
                if( result!=null && (result instanceof OclCollection) )
                {
                    ((OclCollection)result).setAlias(id);
                }
                return result;
            }
            // TODO?
            return creator_.createTypeUser(id);
        }
        System.err.println("OclNormalization.makeOclType: unknown IDL type '"+
            idlType.getClass().getName()+"'");
        return null;
    }


    /**
     * Calculates the name of the OCL type, which is the best representation
     * of the given object of the IDL metamodel.
     *
     * @param object  an object of the IDL metamodel
     * @return the name of an OCL type or an user defined name
     */
    public static String makeOclTypeName( MTyped object )
    {
        return makeOclTypeNameFromIdlType(object.getIdlType());
    }

    private static String makeOclTypeNameFromIdlType( MIDLType idlType )
    {
        if (idlType instanceof MAliasDef)
        {
            return makeOclTypeNameFromIdlType(((MAliasDef) idlType).getIdlType());
        }
        if (idlType instanceof MPrimitiveDef)
        {
            return makeOclTypeName(((MPrimitiveDef) idlType).getKind());
        }
        if ((idlType instanceof MStringDef) || (idlType instanceof MWstringDef))
        {
            return OclConstants.TYPE_NAME_STRING;
        }
        if ((idlType instanceof MArrayDef) || (idlType instanceof MSequenceDef))
        {
            String item = makeOclTypeName((MTyped) idlType);
            return OclConstants.COLLECTIONKIND_SEQUENCE + "(" + item + ")";
        }
        if (idlType instanceof MTypedefDef)
        {
            return ((MTypedefDef) idlType).getIdentifier();
        }
        return "unknown IDL type";
    }


    /**
     * Calculates the name of the OCL type, which is the best representation
     * of the given simple data type.
     *
     * @param obj  a simple IDL data type
     * @return the name of an OCL type or an user defined name
     */
    public static String makeOclTypeName( MPrimitiveKind obj )
    {
        if( obj.equals(MPrimitiveKind.PK_BOOLEAN) )
        {
            return OclConstants.TYPE_NAME_BOOLEAN;
        }
        if( obj.equals(MPrimitiveKind.PK_CHAR) ||
            obj.equals(MPrimitiveKind.PK_LONG) ||
            obj.equals(MPrimitiveKind.PK_LONGLONG) ||
            obj.equals(MPrimitiveKind.PK_OCTET) ||
            obj.equals(MPrimitiveKind.PK_SHORT) ||
            obj.equals(MPrimitiveKind.PK_ULONG) ||
            obj.equals(MPrimitiveKind.PK_ULONGLONG) ||
            obj.equals(MPrimitiveKind.PK_USHORT) ||
            obj.equals(MPrimitiveKind.PK_WCHAR) )
        {
            return OclConstants.TYPE_NAME_INTEGER;
        }
        if( obj.equals(MPrimitiveKind.PK_STRING) ||
            obj.equals(MPrimitiveKind.PK_WSTRING) )
        {
            return OclConstants.TYPE_NAME_STRING;
        }
        if( obj.equals(MPrimitiveKind.PK_DOUBLE) ||
            obj.equals(MPrimitiveKind.PK_LONGDOUBLE) ||
            obj.equals(MPrimitiveKind.PK_FLOAT) )
        {
            return OclConstants.TYPE_NAME_REAL;
        }
        if( obj.equals(MPrimitiveKind.PK_ANY) )
        {
            return "OclAny";  // I don't know, if this type is useful [rlechner]
        }
        if( obj.equals(MPrimitiveKind.PK_VOID) )
        {
            return OclConstants.TYPE_NAME_VOID;
        }
        return obj.toString();  // an user type
    }


    /**
     * Returns the type of the items of a collection.
     */
    public static MIDLType makeItemTypeOfCollection( MIDLType collectionType )
    {
        if( collectionType==null )
        {
            return null;
        }
        if( collectionType instanceof MAliasDef )
        {
            return makeItemTypeOfCollection( ((MTyped)collectionType).getIdlType() );
        }
        if( (collectionType instanceof MArrayDef)||(collectionType instanceof MSequenceDef) )
        {
            return ((MTyped)collectionType).getIdlType();
        }
        System.err.println("error: OclNormalization.makeItemTypeOfCollection : collectionType="+
            collectionType.getClass().getName());
        return null;
    }

}
