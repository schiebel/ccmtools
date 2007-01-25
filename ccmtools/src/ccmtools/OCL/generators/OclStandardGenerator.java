/* CCM Tools : OCL generators
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003, 2004 Salomon Automation
 *
 * $Id: OclStandardGenerator.java,v 1.2 2004/08/28 09:17:22 teiniker Exp $
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

import java.util.Iterator;

import oclmetamodel.MActualParameters;
import oclmetamodel.MBooleanLiteral;
import oclmetamodel.MCollectionItem;
import oclmetamodel.MCollectionLiteral;
import oclmetamodel.MCollectionRange;
import oclmetamodel.MDeclarator;
import oclmetamodel.MEnumLiteral;
import oclmetamodel.MExpression;
import oclmetamodel.MFile;
import oclmetamodel.MFormalParameter;
import oclmetamodel.MIfExpression;
import oclmetamodel.MIntegerLiteral;
import oclmetamodel.MLiteralExpression;
import oclmetamodel.MName;
import oclmetamodel.MOperationExpression;
import oclmetamodel.MPostfixExpression;
import oclmetamodel.MPropertyCall;
import oclmetamodel.MPropertyCallParameters;
import oclmetamodel.MRealLiteral;
import oclmetamodel.MStringLiteral;
import oclmetamodel.OclBag;
import oclmetamodel.OclBoolean;
import oclmetamodel.OclCollection;
import oclmetamodel.OclEnumeration;
import oclmetamodel.OclInteger;
import oclmetamodel.OclReal;
import oclmetamodel.OclSequence;
import oclmetamodel.OclSet;
import oclmetamodel.OclString;
import oclmetamodel.OclType;
import oclmetamodel.OclUser;
import oclmetamodel.OclVoid;
import ccmtools.Metamodel.BaseIDL.MIDLType;
import ccmtools.Metamodel.BaseIDL.MSequenceDef;
import ccmtools.Metamodel.BaseIDL.MSequenceDefImpl;
import ccmtools.OCL.parser.OclConstants;
import ccmtools.OCL.utils.OclElementCreator;
import ccmtools.OCL.utils.OclNormalization;


/**
 * Code generator for languages like C++ or Java.
 *
 * @author Robert Lechner
 * @version $Date: 2004/08/28 09:17:22 $
 */
public abstract class OclStandardGenerator extends OclCodeGenerator
{
    /**
     * Creates an OCL code generator.
     *
     * @param creator  The parse tree creator.
     * @param parseTree  The normalized parse tree.
     * @param checker  Calculates the type of OCL expressions.
     */
    protected OclStandardGenerator( OclElementCreator creator, MFile parseTree, OclTypeChecker checker )
    {
        super(creator, parseTree, checker);
    }


    //////////////////////////////////////////////////////////////////////////


    abstract protected String getStatement_CollectionInit( String cppClass, String cppType, String result );
    abstract protected String getStatement_CollectionAdd( String collection, String code );
    abstract protected String getStatements_CollectionRange( MCollectionRange range, String result,
                                                             ConstraintCode conCode );

    abstract protected String getExpr_If( String exprCond, String exprTrue, String exprFalse );
    abstract protected String getExpr_Divide( String z, String n, ConstraintCode code );
    abstract protected String getExpr_Xor( String p1, String p2, ConstraintCode code );
    abstract protected String getExpr_Implies( String p1, String p2, ConstraintCode code );
    abstract protected String getLanguagePathName( String prefix, String suffix );
    abstract protected String getExpr_String_size( String exprCode, ConstraintCode conCode );
    abstract protected String getExpr_String_concat( String exprCode, String parameter, ConstraintCode conCode );
    abstract protected String getExpr_String_toUpper( String exprCode, ConstraintCode conCode );
    abstract protected String getExpr_String_toLower( String exprCode, ConstraintCode conCode );
    abstract protected String getExpr_String_substring( String exprCode, String p1, String p2,
                                                        ConstraintCode conCode );
    abstract protected String getExpr_Integer_abs( String exprCode, ConstraintCode conCode );
    abstract protected String getExpr_Integer_div( String exprCode, String param, ConstraintCode conCode );
    abstract protected String getExpr_Integer_mod( String exprCode, String param, ConstraintCode conCode );
    abstract protected String getExpr_Integer_max( String exprCode, String param, ConstraintCode conCode );
    abstract protected String getExpr_Integer_min( String exprCode, String param, ConstraintCode conCode );
    abstract protected String getExpr_Real_abs( String exprCode, ConstraintCode conCode );
    abstract protected String getExpr_Real_floor( String exprCode, ConstraintCode conCode );
    abstract protected String getExpr_Real_max( String exprCode, String param, ConstraintCode conCode );
    abstract protected String getExpr_Real_min( String exprCode, String param, ConstraintCode conCode );
    abstract protected String getExpr_Sequence_union( String exprCode, String param, ConstraintCode conCode );
    abstract protected String getExpr_Set_union( String exprCode, String param, ConstraintCode conCode );
    abstract protected String getExpr_Collection_union( String exprCode, String param, ConstraintCode conCode );
    abstract protected String getExpr_Collection_size( String exprCode, ConstraintCode conCode );
    abstract protected String getExpr_Collection_includes( String exprCode, String param,
                                                           ConstraintCode conCode );
    abstract protected String getExpr_Collection_count( String exprCode, String param, ConstraintCode conCode );
    abstract protected String getExpr_Collection_includesAll( String exprCode, String param,
                                                              ConstraintCode conCode );
    abstract protected String getExpr_Collection_excludesAll( String exprCode, String param,
                                                              ConstraintCode conCode );
    abstract protected String getExpr_Collection_isEmpty( String exprCode, ConstraintCode conCode );
    abstract protected String getExpr_Bag_intersection( String exprCode, String param, ConstraintCode conCode );
    abstract protected String getExpr_Collection_intersection( String exprCode, String param,
                                                               ConstraintCode conCode );
    abstract protected String getExpr_Collection_including( String collClass, String collItem,
                                                            String exprCode, String param,
                                                            ConstraintCode conCode );
    abstract protected String getExpr_Collection_excluding( String collClass, String collItem,
                                                            String exprCode, String param,
                                                            ConstraintCode conCode );
    abstract protected String getExpr_symmetricDifference( String collItem, String exprCode,
                                                           String param, ConstraintCode conCode );
    abstract protected String getExpr_Collection_asSet( String collItem, String exprCode,
                                                        ConstraintCode conCode );
    abstract protected String getExpr_Collection_asBag( String collItem, String exprCode,
                                                        ConstraintCode conCode );
    abstract protected String getExpr_Collection_asSequence( String collItem, String exprCode,
                                                             ConstraintCode conCode );
    abstract protected String getExpr_Sequence_append( String exprCode, String param, ConstraintCode conCode );
    abstract protected String getExpr_Sequence_prepend( String exprCode, String param, ConstraintCode conCode );
    abstract protected String getExpr_subSequence( String exprCode, String lower, String upper,
                                                   ConstraintCode conCode );
    abstract protected String getExpr_Sequence_at( String exprCode, String param, ConstraintCode conCode );
    abstract protected String getExpr_Sequence_first( String exprCode, ConstraintCode conCode );
    abstract protected String getExpr_Sequence_last( String exprCode, ConstraintCode conCode );
    abstract protected String getExpr_Collection_sum( String exprCode, String cppCollection,
                                                      String cppItem, ConstraintCode conCode );
    abstract protected String getExpr_Real_equals( String real1, String real2, ConstraintCode conCode );

    abstract protected String copyCollection( String collClass, String collItem,
                                              String exprCode, ConstraintCode conCode );

    abstract protected String getName_ClassCollection();
    abstract protected String getName_ClassSequence();
    abstract protected String getName_ClassSet();
    abstract protected String getName_ClassBag();
    abstract protected String getName_ClassRange();

    abstract protected String getThis();


    /**
     * Returns the constants 'true' and 'false'.
     */
    protected String getLiteral_Boolean( boolean value, ConstraintCode code )
    {
        return value ? "true" : "false";
    }

    /**
     * Creates a new string variable with an constant text.
     * @param value  the text (not the source code!)
     * @return the name of the variable
     */
    abstract protected String getLiteral_String( String value, ConstraintCode code );

    /**
     * Creates a new real variable.
     * @param value  the source code (!) of the initial value
     * @return the name of the variable
     */
    abstract protected String getLiteral_Real( String value, ConstraintCode code );

    /**
     * Returns an integer constant.
     */
    abstract protected String getLiteral_Integer( int value, ConstraintCode code );


    /**
     * Calculates the helper statements and the source code of an OCL expression.
     *
     * @param expr  the OCL expression
     * @param code  only helpers_ and preStatements_ will be changed
     * @return the source code
     */
    protected String makeSourceCode( MExpression expr, ConstraintCode code )
    {
        if( expr instanceof MIfExpression )
        {
            MIfExpression ife = (MIfExpression)expr;
            String exprCond = makeCode(ife.getCondition(), code);
            String exprTrue = makeCode(ife.getTrueExpression(), code);
            String exprFalse = makeCode(ife.getFalseExpression(), code);
            return getExpr_If(exprCond, exprTrue, exprFalse);
        }
        if( expr instanceof MOperationExpression )
        {
            MOperationExpression oe = (MOperationExpression)expr;
            MExpression expr1 = oe.getLeftParameter();
            MExpression expr2 = oe.getRightParameter();
            String oclOp = oe.getOperator();
            String operator = getLanguageOperator(oclOp);
            if( expr1==null )
            {
                return operator + "(" + makeCode(expr2,code) + ")";
            }
            String code1 = makeCode(expr1,code);
            String code2 = makeCode(expr2,code);
            if( oclOp.equals(OclConstants.OPERATOR_DIVIDE) )
            {
                return getExpr_Divide(code1, code2, code);
            }
            if( oclOp.equals(OclConstants.OPERATOR_XOR) )
            {
                return getExpr_Xor(code1, code2, code);
            }
            if( oclOp.equals(OclConstants.OPERATOR_IMPLIES) )
            {
                return getExpr_Implies(code1, code2, code);
            }
    	    if( oclOp.equals(OclConstants.OPERATOR_EQUAL) )
    	    {
        		String result = getCode_Equal(code1,code2,expr1,expr2,true,code);
        		if( result!=null )
        		{
        		    return result;
        		}
    	    }
     	    else if( oclOp.equals(OclConstants.OPERATOR_NEQUAL) )
    	    {
        		String result = getCode_Equal(code1,code2,expr1,expr2,false,code);
        		if( result!=null )
        		{
        		    return result;
        		}
    	    }
            return "(" + code1 + ")" + operator + "(" + code2 + ")";
        }
        if( expr instanceof MLiteralExpression )
        {
            return getCode( (MLiteralExpression)expr, code );
        }
        if( expr instanceof MPropertyCall )
        {
            return getCode( (MPropertyCall)expr, true, code, null );
        }
        if( expr instanceof MPostfixExpression )
        {
            return getCode( (MPostfixExpression)expr, code );
        }
        return error( "unknown expression: "+expr.getClass().getName() );
    }
    
    
    private String getCode_Equal( String code1, String code2, MExpression expr1, MExpression expr2,
                                  boolean isEqual, ConstraintCode conCode )
    {
        OclType type1 = expr1.getOclType();
        if( type1==null )
        {
            type1 = typeChecker_.makeType(expr1,conCode).oclType_;
        }
        OclType type2 = expr2.getOclType();
        if( type2==null )
        {
            type2 = typeChecker_.makeType(expr2,conCode).oclType_;
        }
        if( type1==null || type2==null )
        {
            return null;
        }
        if( (type1 instanceof OclInteger)&&(type2 instanceof OclInteger) )
        {
            return null;
        }
        if( (type1 instanceof OclReal)&&(type2 instanceof OclReal) )
        {
            if( isEqual )
            {
                return getExpr_Real_equals(code1, code2, conCode);
            }
            else
            {
                return "!("+getExpr_Real_equals(code1, code2, conCode)+")";
            }
        }
        return null;
    }


    /**
     * Converts an OCL operator to an operator of the destination language.
     */
    abstract protected String getLanguageOperator( String op );

    /**
     * Converts an OCL type to a type of the destination language.
     *
     * @param type  the OCL type
     * @param collAlias  true=return the alias name if the type is a collection
     * @param itemAlias  true=use the alias name of the item type if the item is also a collection
     */
    abstract protected String getLanguageType( OclType type, boolean collAlias, boolean itemAlias );


    /**
     * Converts an OCL type to a type of the destination language.
     */
    abstract protected String getLanguageType( ElementType et );


    /**
     * Creates the source code, which reads the attribute of a class.
     *
     * @param parentCode  the access code of the class
     * @param parentType  the type of the class
     * @param childCode   the name of the attribute
     */
    abstract protected String getAccess( String parentCode, ElementType parentType, String childCode );


    private String getCode( MLiteralExpression expr, ConstraintCode code )
    {
        if( expr instanceof MBooleanLiteral )
        {
            return getLiteral_Boolean( ((MBooleanLiteral)expr).isValue(), code );
        }
        if( expr instanceof MStringLiteral )
        {
            return getLiteral_String( ((MStringLiteral)expr).getValue(), code );
        }
        if( expr instanceof MIntegerLiteral )
        {
            return getLiteral_Integer( ((MIntegerLiteral)expr).getValue(), code );
        }
        if( expr instanceof MRealLiteral )
        {
            return getLiteral_Real( ((MRealLiteral)expr).getText(), code );
        }
        if( expr instanceof MCollectionLiteral )
        {
            return getCode_CollectionLiteral( (MCollectionLiteral)expr, code );
        }
        if( expr instanceof MEnumLiteral )
        {
            // TODO
            return error("enum literal not implemented");
        }
        return error( "unknown literal: "+expr.getClass().getName() );
    }


    private String getCode_CollectionLiteral( MCollectionLiteral literal, ConstraintCode conCode )
    {
        String result = getNextHelperName();
        OclType type = null;
        String buffer = "";
        Iterator it = literal.getItems().iterator();
        while( it.hasNext() )
        {
            Object item = it.next();
            if( item instanceof MCollectionItem )
            {
                MExpression expr = ((MCollectionItem)item).getExpression();
                String code = makeCode(expr,conCode);
                if( type==null )
                {
                    type = expr.getOclType();
                }
                buffer += getStatement_CollectionAdd(result, code);
            }
            else if( item instanceof MCollectionRange )
            {
                if( type==null )
                {
                    type = creator_.createTypeInteger();
                }
                buffer += getStatements_CollectionRange((MCollectionRange)item, result, conCode);
            }
            else
            {
                buffer += error("unknown collection item")+"\n";
            }
        }
        String cppCollection, kind=literal.getKind();
        if( kind.equals(OclConstants.COLLECTIONKIND_SEQUENCE) )
        {
            cppCollection = getName_ClassSequence();
            literal.setOclType(creator_.createTypeSequence(type));
        }
        else if( kind.equals(OclConstants.COLLECTIONKIND_SET) )
        {
            cppCollection = getName_ClassSet();
            literal.setOclType(creator_.createTypeSet(type));
        }
        else if( kind.equals(OclConstants.COLLECTIONKIND_BAG) )
        {
            cppCollection = getName_ClassBag();
            literal.setOclType(creator_.createTypeBag(type));
        }
        else
        {
            cppCollection = getName_ClassCollection();
            literal.setOclType(creator_.createTypeCollection(type));
        }
        String cppType = getLanguageType(type,true,true);
        conCode.helpers_ += getStatement_CollectionInit(cppCollection, cppType, result);
        conCode.helpers_ += buffer;
        return result;
    }


    // ------------------------------------------------------------------------


    private MIDLType lastIdlType_;

    /**
     *  property call
     */
    private String getCode( MPropertyCall pc, boolean setBaseModule, ConstraintCode conCode,
                            ElementType etParent )
    {
        String code = pc.getName();
        if( setBaseModule )
        {
            String var = getLocalVariableName(code);
            if( var!=null )
            {
                // local variable
                ElementType t = getLocalVariableType(code);
                if( t!=null )
                {
                    if( t.oclType_!=null )
                    {
                        pc.setOclType(t.oclType_);
                    }
                    if( lastIdlType_==null )
                    {
                        lastIdlType_ = t.idlType_;
                    }
                }
                return var;
            }
        }
        ElementType etPC = typeChecker_.makeType(pc,conCode);
        OclType type = pc.getOclType();
        if( type==null )
        {
            type = etPC.oclType_;
        }
        else
        {
            etPC.oclType_ = type;
        }
        if( lastIdlType_==null )
        {
            lastIdlType_ = etPC.idlType_;
        }
        /*if( type==null && OCL_DEBUG_OUTPUT )
        {
            System.err.println("warning: cannot get type for '"+code+"' [PC]");
        }*/
        MPropertyCallParameters pcp = pc.getCallParameters();
        if( pcp==null )
        {
            /*  an attribute, the return value or a parameter
            */
            if( code.equals(OclConstants.KEYWORD_RESULT) )
            {
                return code;  // the return value
            }
            if( conCode.opCtxt_!=null )
            {
                Iterator it = conCode.opCtxt_.getParameters().iterator();
                while( it.hasNext() )
                {
                    MFormalParameter fp = (MFormalParameter)it.next();
                    if( fp.getName().equals(code) )
                    {
                        return code;  // a parameter
                    }
                }
            }
            //
            /*  an attribute
            */
            code = typeChecker_.getAttributeName(code, etParent);
            if( setBaseModule )
            {
                String name = typeChecker_.getLocalAdapterName(baseModuleType_);
                if( name!=null )
                {
                    code = getLanguagePathName(name,code);
                }
                else
                {
                    code = getThis()+code;
                }
            }
        }
        else
        {
            /*  an operation
            */
            if( setBaseModule )
            {
                code = getThis()+code;
            }
            code += "(";
            MActualParameters ap = pcp.getParameters();
            if( ap!=null )
            {
                Iterator it = ap.getExpressions().iterator();
                code += makeCode( (MExpression)it.next(), conCode );
                while( it.hasNext() )
                {
                    code += ","+makeCode( (MExpression)it.next(), conCode );
                }
            }
            code += ")";
        }
        if( setBaseModule && pc.isPrevious() )
        {
            if( type!=null )
            {
                String helper = (String)conCode.preHelpers_.get(code);
                if( helper==null )
                {
                    String langType = getLanguageType(etPC);
                    helper = getNextHelperName();
                    conCode.preStatements_ += "  /*PC*/"+langType+" "+helper+" = "+code+";\n";
                    conCode.preHelpers_.put(code, helper);
                }
                return helper;
            }
            else
            {
                return error("cannot resolve type of @pre-expression [PC]");
            }
        }
        return code;
    }


    // ------------------------------------------------------------------------


    /**
     *  postfix expression
     */
    private String getCode( MPostfixExpression pfe, ConstraintCode conCode )
    {
        MExpression expr = pfe.getExpression();
        MPropertyCall pc = pfe.getPropertyCall();
        ElementType etExpr = typeChecker_.makeType(expr,conCode);
        lastIdlType_ = null;
        String exprCode = makeCode(expr,conCode);
        OclType typeExpr = expr.getOclType();
        if( typeExpr==null )
        {
            typeExpr = etExpr.oclType_;
        }
        else
        {
            etExpr.oclType_ = typeExpr;
        }
        if( lastIdlType_==null )
        {
            lastIdlType_ = etExpr.idlType_;
        }
        else
        {
            etExpr.idlType_ = lastIdlType_;
        }
        if( pfe.isCollection() )
        {
            lastIdlType_ = null;
            return getCollectionOperationCode(expr,pc,conCode,pfe,exprCode,etExpr);
        }
        exprCode = "(" + exprCode + ")";
        ConstraintCode fake = getConstraintCode(conCode, lastIdlType_);
        if( pc.getCallParameters()==null && pc.isPrevious() )
        {
            ElementType etPC = typeChecker_.makeType(pc,fake);
            OclType typePC = etPC.oclType_;
            lastIdlType_ = etPC.idlType_;
            pfe.setOclType(typePC);
            if( typePC==null )
            {
                return error("cannot resolve type of @pre-expression [PFE]");
            }
            String h = getNextHelperName();
            String t = getLanguageType(etPC);
            String v = getCode(pc,false,conCode,etExpr);
            String c = "  /*PFE*/ "+t+" "+h+" = "+getAccess(exprCode,etExpr,v)+";\n";
            conCode.preStatements_ += c;
            lastIdlType_ = etPC.idlType_;
            return h;
        }
        if( typeExpr!=null )
        {
            lastIdlType_ = null;
            if( typeExpr instanceof OclCollection )
            {
                try
                {
                    String dummyIteratorName = "DUMMY_ITERATOR_NAME_" + dummyIteratorCounter_;
                    dummyIteratorCounter_++;
                    MDeclarator decl2 = creator_.createDeclarator(dummyIteratorName);
                    MPropertyCall pcL = creator_.createPropertyCall(dummyIteratorName,false);
                    MPostfixExpression pfe2 = creator_.createPostfixExpression(pcL,pc,false);
                    MActualParameters ap2 = creator_.createActualParameters(pfe2);
                    MPropertyCallParameters pcp2 = creator_.createPropertyCallParameters(decl2, ap2);
                    MPropertyCall pc2 = creator_.createPropertyCall("collect", false);
                    pc2.setCallParameters(pcp2);
                    return getCollectionOperationCode(expr,pc2,conCode,pfe,exprCode,etExpr);
                }
                catch(Exception e)
                {
                }
                return error("shorthand for 'collect' is not implemented");
            }
            if( typeExpr instanceof OclString )
            {
                return getCode_String(exprCode,pc,conCode,pfe);
            }
            if( typeExpr instanceof OclInteger )
            {
                return getCode_Integer(exprCode,pc,conCode,pfe);
            }
            if( typeExpr instanceof OclReal )
            {
                return getCode_Real(exprCode,pc,conCode,pfe);
            }
            if( typeExpr instanceof OclBoolean )
            {
                return error("unknown Boolean operation '" + pc.getName() + "'");
            }
            if( typeExpr instanceof OclEnumeration )
            {
                return error("unknown Enumeration operation '" + pc.getName() + "'");
            }
            if( typeExpr instanceof OclVoid )
            {
                return error("wrong use of type OclVoid");
            }
            if( !(typeExpr instanceof OclUser) )
            {
                return error( "unknown OCL type: "+typeExpr.getName() );
            }
        }
        // we assume that the type is OclUser
        ElementType etPC = typeChecker_.makeType(pc,fake);
        OclType typePC = etPC.oclType_;
        lastIdlType_ = etPC.idlType_;
        pfe.setOclType(typePC);
        String sc = getAccess(exprCode, etExpr, getCode(pc,false,conCode,etExpr));
        lastIdlType_ = etPC.idlType_;
        return sc;
    }


    //////////////////////////////////////////////////////////////////////////


    private String getCode_String( String exprCode, MPropertyCall pc,
                                   ConstraintCode conCode, MExpression parent )
    {
        String name = pc.getName();
        if( name.equals("size") )
        {
            parent.setOclType(creator_.createTypeInteger());
            return getExpr_String_size(exprCode, conCode);
        }
        if( name.equals("concat") )
        {
            parent.setOclType(creator_.createTypeString());
            String parameter = getParameterCode(pc,0,conCode);
            return getExpr_String_concat(exprCode, parameter, conCode);
        }
        if( name.equals("toUpper") )
        {
            parent.setOclType(creator_.createTypeString());
            return getExpr_String_toUpper(exprCode, conCode);
        }
        if( name.equals("toLower") )
        {
            parent.setOclType(creator_.createTypeString());
            return getExpr_String_toLower(exprCode, conCode);
        }
        if( name.equals("substring") )
        {
            parent.setOclType(creator_.createTypeString());
            String p1 = getParameterCode(pc,0,conCode);
            String p2 = getParameterCode(pc,1,conCode);
            return getExpr_String_substring(exprCode, p1, p2, conCode);
        }
        return error("unknown String operation '" + name + "'");
    }


    private String getCode_Integer( String exprCode, MPropertyCall pc,
                                    ConstraintCode conCode, MExpression parent )
    {
        String name = pc.getName();
        if( name.equals("abs") )
        {
            parent.setOclType(creator_.createTypeInteger());
            return getExpr_Integer_abs(exprCode, conCode);
        }
        if( name.equals("div") )
        {
            parent.setOclType(creator_.createTypeInteger());
            String param = getParameterCode(pc,0,conCode);
            return getExpr_Integer_div(exprCode, param, conCode);
        }
        if( name.equals("mod") )
        {
            parent.setOclType(creator_.createTypeInteger());
            String param = getParameterCode(pc,0,conCode);
            return getExpr_Integer_mod(exprCode, param, conCode);
        }
        OclType type = getParameterType(pc,0,conCode);
        if( type!=null && (type instanceof OclInteger) )
        {
            if( name.equals("max") )
            {
                parent.setOclType(creator_.createTypeInteger());
                String param = getParameterCode(pc,0,conCode);
                return getExpr_Integer_max(exprCode, param, conCode);
            }
            if( name.equals("min") )
            {
                parent.setOclType(creator_.createTypeInteger());
                String param = getParameterCode(pc,0,conCode);
                return getExpr_Integer_min(exprCode, param, conCode);
            }
        }
        // convert Integer to Real
        return getCode_Real( getLiteral_Real(exprCode, conCode), pc, conCode, parent );
    }


    private String getCode_Real( String exprCode, MPropertyCall pc,
                                 ConstraintCode conCode, MExpression parent )
    {
        String name = pc.getName();
        if( name.equals("abs") )
        {
            parent.setOclType(creator_.createTypeReal());
            return getExpr_Real_abs(exprCode, conCode);
        }
        if( name.equals("floor") )
        {
            parent.setOclType(creator_.createTypeInteger());
            return getExpr_Real_floor(exprCode, conCode);
        }
        if( name.equals("round") )
        {
            parent.setOclType(creator_.createTypeInteger());
            return getExpr_Real_floor(exprCode+"+0.5", conCode);
        }
        if( name.equals("max") )
        {
            parent.setOclType(creator_.createTypeReal());
            String param = getParameterCode(pc,0,conCode);
            return getExpr_Real_max(exprCode, param, conCode);
        }
        if( name.equals("min") )
        {
            parent.setOclType(creator_.createTypeReal());
            String param = getParameterCode(pc,0,conCode);
            return getExpr_Real_min(exprCode, param, conCode);
        }
        return error("unknown Real operation '" + name + "'");
    }


    //////////////////////////////////////////////////////////////////////////


    private String getCollectionOperationCode( MExpression expr, MPropertyCall pc,
                                               ConstraintCode conCode, MExpression parent,
                                               String exprCode, ElementType etExpr )
    {
        String cppCollection, cppItem;
        String name = pc.getName();
        if( !isHelper(exprCode) )
        {
            exprCode = "("+exprCode+")";
        }
        OclType collType = etExpr.oclType_;
        if( collType==null )
        {
            collType = typeChecker_.makeType(expr,conCode).oclType_;
        }
        OclType itemType;
        if( collType==null || !(collType instanceof OclCollection) )
        {
            // this should never happen
            if( collType==null )
            {
                cppItem = error("OclStandardGenerator.getCollectionOperationCode:  collType==null");
            }
            else
            {
                cppItem = error("OclStandardGenerator.getCollectionOperationCode:  wrong type '"
                                +collType.getName()+"'");
            }
            collType = creator_.createTypeCollection(null);
            itemType = null;
        }
        else
        {
            itemType = ((OclCollection)collType).getType();
            cppItem = getLanguageType(itemType,true,true);
        }
        OclType collectionType;
        if( collType instanceof OclSequence )
        {
            collectionType = creator_.createTypeSequence(itemType);
            cppCollection = getName_ClassSequence();
            if( name.equals("union") )
            {
                OclType t = getParameterType(pc,0,conCode);
                if( t!=null && (t instanceof OclSequence) )
                {
                    parent.setOclType(collectionType);
                    String param = getParameterCode(pc,0,conCode);
                    return getExpr_Sequence_union(exprCode, param, conCode);
                }
            }
            else if( name.equals("intersection") )
            {
                OclType t = getParameterType(pc,0,conCode);
                if( t!=null && ((t instanceof OclSequence)||(t instanceof OclBag)) )
                {
                    parent.setOclType(creator_.createTypeBag(itemType));
                    String param = getParameterCode(pc,0,conCode);
                    return getExpr_Bag_intersection(exprCode, param, conCode);
                }
            }
            else if( name.equals("append") )
            {
                parent.setOclType(collectionType);
                String param = getParameterCode(pc,0,conCode);
                return getExpr_Sequence_append(exprCode, param, conCode);
            }
            else if( name.equals("prepend") )
            {
                parent.setOclType(collectionType);
                String param = getParameterCode(pc,0,conCode);
                return getExpr_Sequence_prepend(exprCode, param, conCode);
            }
            else if( name.equals("subSequence") )
            {
                parent.setOclType(collectionType);
                String lower = getParameterCode(pc,0,conCode);
                String upper = getParameterCode(pc,1,conCode);
                return getExpr_subSequence(exprCode, lower, upper, conCode);
            }
            else if( name.equals("at") )
            {
                parent.setOclType(itemType);
                String param = getParameterCode(pc,0,conCode);
                return getExpr_Sequence_at(exprCode, param, conCode);
            }
            else if( name.equals("first") )
            {
                parent.setOclType(itemType);
                return getExpr_Sequence_first(exprCode, conCode);
            }
            else if( name.equals("last") )
            {
                parent.setOclType(itemType);
                return getExpr_Sequence_last(exprCode, conCode);
            }
        }
        else if( collType instanceof OclSet )
        {
            collectionType = creator_.createTypeSet(itemType);
            cppCollection = getName_ClassSet();
            if( name.equals("union") )
            {
                OclType t = getParameterType(pc,0,conCode);
                if( t!=null && (t instanceof OclSet) )
                {
                    parent.setOclType(collectionType);
                    String param = getParameterCode(pc,0,conCode);
                    return getExpr_Set_union(exprCode, param, conCode);
                }
            }
        }
        else if( collType instanceof OclBag )
        {
            collectionType = creator_.createTypeBag(itemType);
            cppCollection = getName_ClassBag();
            if( name.equals("intersection") )
            {
                OclType t = getParameterType(pc,0,conCode);
                if( t!=null && ((t instanceof OclSequence)||(t instanceof OclBag)) )
                {
                    parent.setOclType(collectionType);
                    String param = getParameterCode(pc,0,conCode);
                    return getExpr_Bag_intersection(exprCode, param, conCode);
                }
            }
        }
        else
        {
            collectionType = creator_.createTypeCollection(itemType);
            cppCollection = getName_ClassCollection();
        }
        if( name.equals("sum") )
        {
            parent.setOclType(itemType);
            return getExpr_Collection_sum(exprCode, cppCollection, cppItem, conCode);
        }
        if( name.equals("union") )
        {
            parent.setOclType(creator_.createTypeBag(itemType));
            String param = getParameterCode(pc,0,conCode);
            return getExpr_Collection_union(exprCode, param, conCode);
        }
        if( name.equals("intersection") )
        {
            parent.setOclType(creator_.createTypeSet(itemType));
            String param = getParameterCode(pc,0,conCode);
            return getExpr_Collection_intersection(exprCode, param, conCode);
        }
        if( name.equals("size") )
        {
            parent.setOclType(creator_.createTypeInteger());
            return getExpr_Collection_size(exprCode, conCode);
        }
        if( name.equals("includes") )
        {
            parent.setOclType(creator_.createTypeBoolean());
            String param = getParameterCode(pc,0,conCode);
            return getExpr_Collection_includes(exprCode, param, conCode);
        }
        if( name.equals("excludes") )
        {
            parent.setOclType(creator_.createTypeBoolean());
            String param = getParameterCode(pc,0,conCode);
            return "!("+getExpr_Collection_includes(exprCode, param, conCode)+")";
        }
        if( name.equals("count") )
        {
            parent.setOclType(creator_.createTypeInteger());
            String param = getParameterCode(pc,0,conCode);
            return getExpr_Collection_count(exprCode, param, conCode);
        }
        if( name.equals("includesAll") )
        {
            parent.setOclType(creator_.createTypeBoolean());
            String param = getParameterCode(pc,0,conCode);
            return getExpr_Collection_includesAll(exprCode, param, conCode);
        }
        if( name.equals("excludesAll") )
        {
            parent.setOclType(creator_.createTypeBoolean());
            String param = getParameterCode(pc,0,conCode);
            return getExpr_Collection_excludesAll(exprCode, param, conCode);
        }
        if( name.equals("isEmpty") )
        {
            parent.setOclType(creator_.createTypeBoolean());
            return getExpr_Collection_isEmpty(exprCode, conCode);
        }
        if( name.equals("notEmpty") )
        {
            parent.setOclType(creator_.createTypeBoolean());
            return "!("+getExpr_Collection_isEmpty(exprCode, conCode)+")";
        }
        if( name.equals("including") )
        {
            parent.setOclType(collectionType);
            String param = getParameterCode(pc,0,conCode);
            return getExpr_Collection_including(cppCollection, cppItem, exprCode, param, conCode);
        }
        if( name.equals("excluding") )
        {
            parent.setOclType(collectionType);
            String param = getParameterCode(pc,0,conCode);
            return getExpr_Collection_excluding(cppCollection, cppItem, exprCode, param, conCode);
        }
        if( name.equals("symmetricDifference") )
        {
            parent.setOclType(creator_.createTypeSet(itemType));
            String param = getParameterCode(pc,0,conCode);
            return getExpr_symmetricDifference(cppItem, exprCode, param, conCode);
        }
        if( name.equals("asSet") )
        {
            parent.setOclType(creator_.createTypeSet(itemType));
            return getExpr_Collection_asSet(cppItem, exprCode, conCode);
        }
        if( name.equals("asBag") )
        {
            parent.setOclType(creator_.createTypeBag(itemType));
            return getExpr_Collection_asBag(cppItem, exprCode, conCode);
        }
        if( name.equals("asSequence") )
        {
            parent.setOclType(creator_.createTypeSequence(itemType));
            return getExpr_Collection_asSequence(cppItem, exprCode, conCode);
        }
        //
        if( !isHelper(exprCode) )
        {
            exprCode = copyCollection(cppCollection, cppItem, exprCode, conCode);
        }
        if( name.equals("exists") )
        {
            parent.setOclType(creator_.createTypeBoolean());
            return make_Collection_exists(pc, cppItem, exprCode, conCode, itemType);
        }
        if( name.equals("forAll") )
        {
            parent.setOclType(creator_.createTypeBoolean());
            return make_Collection_forAll(pc, cppItem, exprCode, conCode, itemType);
        }
        if( name.equals("select") )
        {
            parent.setOclType(collectionType);
            return make_Collection_select(pc, cppCollection, cppItem, exprCode,
                                          conCode, false, itemType);
        }
        if( name.equals("reject") )
        {
            parent.setOclType(collectionType);
            return make_Collection_select(pc, cppCollection, cppItem, exprCode,
                                          conCode, true, itemType);
        }
        if( name.equals("one") )
        {
            parent.setOclType(creator_.createTypeBoolean());
            String c = make_Collection_select(pc, cppCollection, cppItem, exprCode,
                                              conCode, false, itemType);
            String s = getExpr_Collection_size(c, conCode);
            String op = getLanguageOperator(OclConstants.OPERATOR_EQUAL);
            String v = getLiteral_Integer(1, conCode);
            return s+op+v;
        }
        if( name.equals("any") )
        {
            parent.setOclType(itemType);
            String c = make_Collection_select(pc, cppCollection, cppItem, exprCode,
                                              conCode, false, itemType);
            return getExpr_Collection_any(c, cppItem, conCode);
        }
        if( name.equals("collect") )
        {
            return make_collect(pc, conCode, itemType, collType, parent,
                                exprCode, cppItem, "collect", etExpr);
        }
        if( name.equals("isUnique") )
        {
            String c = make_collect(pc, conCode, itemType, collType, parent,
                                    exprCode, cppItem, "isUnique", etExpr);
            parent.setOclType(creator_.createTypeBoolean());
            return getExpr_Collection_isUnique(c, conCode);
        }
        if( name.equals("sortedBy") )
        {
            String c = make_collect(pc, conCode, itemType, collType, parent,
                                    exprCode, cppItem, "sortedBy", etExpr);
            parent.setOclType(creator_.createTypeSequence(itemType));
            return getExpr_Collection_sortedBy(exprCode, c, cppItem, conCode);
        }
        if( name.equals("iterate") )
        {
            return make_iterate(pc, conCode, itemType, collType, parent, exprCode, cppItem);
        }
        return error("unknown collection operation: "+name);
    }


    abstract protected String getExpr_Collection_sortedBy( String collVarName, String refVarName,
                                                           String itemType, ConstraintCode conCode );

    abstract protected String getExpr_Collection_isUnique( String collVarName,
                                                           ConstraintCode conCode );

    abstract protected String getExpr_Collection_any( String collVarName, String cppItem,
                                                      ConstraintCode conCode );

    abstract protected String setCollectionIterator( String collVarName, String cppItem,
                                                     String iteratorVarName, ConstraintCode conCode );

    abstract protected String setBooleanHelper( String initValue, ConstraintCode conCode );

    abstract protected void setOrStatement( String result, String param1, String param2,
                                            ConstraintCode conCode );

    abstract protected void setAndStatement( String result, String param1, String param2,
                                             ConstraintCode conCode );


    private String make_Collection_exists( MPropertyCall pc, String cppItem, String collVarName,
                                           ConstraintCode conCode, OclType itemType )
    {
        MPropertyCallParameters pcp = pc.getCallParameters();
        if( pcp==null )
        {
            return error("[exists]: no parameters");
        }
        MDeclarator decl = pcp.getDeclarator();
        if( decl==null )
        {
            return error("[exists]: no declarator");
        }
        pushLocalVariables();
        String endCode = "";
        String helper = setBooleanHelper(getLiteral_Boolean(false, conCode), conCode);
        Iterator it1 = decl.getNames().iterator();
        while( it1.hasNext() )
        {
            String var = newLocalVariable( ((MName)it1.next()).getValue(), itemType );
            endCode = setCollectionIterator(collVarName, cppItem, var, conCode) + endCode;
        }
        String param = getParameterCode(pc,0,conCode);
        setOrStatement(helper, helper, param, conCode);
        conCode.helpers_ += endCode;
        popLocalVariables();
        return helper;
    }


    private String make_Collection_forAll( MPropertyCall pc, String cppItem, String collVarName,
                                           ConstraintCode conCode, OclType itemType )
    {
        MPropertyCallParameters pcp = pc.getCallParameters();
        if( pcp==null )
        {
            return error("[forAll]: no parameters");
        }
        MDeclarator decl = pcp.getDeclarator();
        if( decl==null )
        {
            return error("[forAll]: no declarator");
        }
        pushLocalVariables();
        String endCode = "";
        String helper = setBooleanHelper(getLiteral_Boolean(true, conCode), conCode);
        Iterator it1 = decl.getNames().iterator();
        while( it1.hasNext() )
        {
            String var = newLocalVariable( ((MName)it1.next()).getValue(), itemType );
            endCode = setCollectionIterator(collVarName, cppItem, var, conCode) + endCode;
        }
        String param = getParameterCode(pc,0,conCode);
        setAndStatement(helper, helper, param, conCode);
        conCode.helpers_ += endCode;
        popLocalVariables();
        return helper;
    }


    /**
     * Starts the collection operations 'select' and 'reject'.
     *
     * @param cppCollection     type of the collection
     * @param cppItem           type of one element of the collection
     * @param collVarName       variable name of the collection
     * @param iteratorVarName   variable name of the iterator
     * @param conCode           only 'conCode.helpers_' will be changed
     *
     * @return variable name of the result of the operation
     */
    abstract protected String start_select( String cppCollection, String cppItem, String collVarName,
                                            String iteratorVarName, ConstraintCode conCode);

    /**
     * Finishes the collection operations 'select' and 'reject'.
     *
     * @param condition         the source code of the condition
     * @param iteratorVarName   variable name of the iterator
     * @param resultVarName     the return value of {@link start_select}
     * @param conCode           only 'conCode.helpers_' will be changed
     * @param reject            true if the operation is 'reject' (and not 'select')
     */
    abstract protected void finish_select( String condition, String iteratorVarName,
                                           String resultVarName, ConstraintCode conCode,
                                           boolean reject);


    private String make_Collection_select( MPropertyCall pc, String cppCollection, String cppItem,
                                           String collVarName, ConstraintCode conCode,
                                           boolean reject, OclType itemType )
    {
        String operation = reject ? "[reject]" : "[select]";
        MPropertyCallParameters pcp = pc.getCallParameters();
        if( pcp==null )
        {
            return error(operation+": no parameters");
        }
        MDeclarator decl = pcp.getDeclarator();
        if( decl==null )
        {
            return error(operation+": no declarator");
        }
        Iterator it1 = decl.getNames().iterator();
        if( !it1.hasNext() )
        {
            return error(operation+": no iterator");
        }
        String var = ((MName)it1.next()).getValue();
        if( it1.hasNext() )
        {
            conCode.helpers_ += error(operation+": too much iterators")+"\n";
        }
        pushLocalVariables();
        var = newLocalVariable(var, itemType);
        String helper = start_select(cppCollection, cppItem, collVarName, var, conCode);
        String param = getParameterCode(pc,0,conCode);
        finish_select(param, var, helper, conCode, reject);
        popLocalVariables();
        return helper;
    }


    private static int dummyIteratorCounter_;


    private String make_collect( MPropertyCall pc, ConstraintCode conCode, OclType itemType,
                                 OclType collType, MExpression parent, String inputVarName,
                                 String inputItemType, String operationName,
                                 ElementType etCollection )
    {
        MPropertyCallParameters pcp = pc.getCallParameters();
        if( pcp==null )
        {
            return error("["+operationName+"]: no parameters  [pcp]");
        }
        MActualParameters ap = pcp.getParameters();
        if( ap==null )
        {
            return error("["+operationName+"]: no parameters  [ap]");
        }
        MDeclarator decl = pcp.getDeclarator();
        if( decl==null )
        {
            boolean everything_is_ok = false;
            Iterator param1it = ap.getExpressions().iterator();
            if( param1it.hasNext() )
            {
                Object param1obj = param1it.next();
                if( param1obj instanceof MPropertyCall )
                {
                    try
                    {
                        String dummyIteratorName = "DUMMY_ITERATOR_NAME_" + dummyIteratorCounter_;
                        dummyIteratorCounter_++;
                        decl = creator_.createDeclarator(dummyIteratorName);
                        MPropertyCall pcL = creator_.createPropertyCall(dummyIteratorName,false);
                        MPostfixExpression dummyPfe = creator_.createPostfixExpression(pcL,
                                                        (MPropertyCall)param1obj, false);
                        ap = creator_.createActualParameters(dummyPfe);
                        pcp = creator_.createPropertyCallParameters(decl, ap);
                        pc.setCallParameters(pcp);
                        everything_is_ok = true;
                    }
                    catch(Exception e)
                    {
                        return error("["+operationName+"]: exception: "+e);
                    }
                }
            }
            if( !everything_is_ok )
            {
                return error("["+operationName+"]: no declarator");
            }
        }
        Iterator it1 = decl.getNames().iterator();
        if( !it1.hasNext() )
        {
            return error("["+operationName+"]: no iterator");
        }
        String iteratorVarName = ((MName)it1.next()).getValue();
        if( it1.hasNext() )
        {
            conCode.helpers_ += error("["+operationName+"]: too much iterators")+"\n";
        }
        String resultVarName = getNextHelperName();
        pushLocalVariables();
        if( itemType==null && OCL_DEBUG_OUTPUT )
        {
            System.err.println("warning: OclStandardGenerator.make_collect : itemType==null");
        }
        ElementType etItem = new ElementType();
        etItem.oclType_ = itemType;
        etItem.idlType_ = OclNormalization.makeItemTypeOfCollection(etCollection.idlType_);
        iteratorVarName = newLocalVariable(iteratorVarName, etItem);
        String oldHelperCode = conCode.helpers_;
        conCode.helpers_ = "";
        lastIdlType_ = null;
        String expression = getParameterCode(pc,0,conCode);
        MIDLType idlExprType = lastIdlType_;
        OclType exprType = getParameterType(pc,0,conCode);
        String extraHelpers = conCode.helpers_;
        conCode.helpers_ = oldHelperCode;
        String resultItemType = getLanguageType(exprType, true, true);
        String resultCollectionType;
        if( collType instanceof OclSequence )
        {
            parent.setOclType(creator_.createTypeSequence(exprType));
            resultCollectionType = getName_ClassSequence();
        }
        else
        {
            parent.setOclType(creator_.createTypeBag(exprType));
            resultCollectionType = getName_ClassBag();
        }
        if( idlExprType!=null )
        {
            MSequenceDef idlSeq = new MSequenceDefImpl();
            idlSeq.setIdlType(idlExprType);
            lastIdlType_ = idlSeq;
            //System.out.println("OK: idlExprType="+idlExprType.getClass().getName());
        }
        //else System.out.println("-----> idlExprType==null");
        conCode.helpers_ += getStatements_collect( resultCollectionType, resultItemType,
            resultVarName, inputVarName, inputItemType, iteratorVarName, expression, extraHelpers );
        popLocalVariables();
        return resultVarName;
    }


    /**
     * Returns the statements of the collection operation 'collect'.
     *
     * @param resultCollectionType    collection type of the result
     * @param resultItemType          item type of the result
     * @param resultVarName           variable name of the result
     * @param inputVarName            variable name of the input collection
     * @param inputItemType           item type of the input collection
     * @param iteratorVarName         variable name of the iterator
     * @param expression              source code of the expression
     * @param extraHelpers            helper statements for 'expression'
     */
    abstract protected String getStatements_collect( String resultCollectionType,
        String resultItemType, String resultVarName, String inputVarName,
        String inputItemType, String iteratorVarName, String expression, String extraHelpers );


    private String make_iterate( MPropertyCall pc, ConstraintCode conCode, OclType itemType,
                                 OclType collType, MExpression parent, String inputVarName,
                                 String inputItemType )
    {
        MPropertyCallParameters pcp = pc.getCallParameters();
        if( pcp==null )
        {
            return error("[iterate]: no parameters");
        }
        MDeclarator decl = pcp.getDeclarator();
        if( decl==null )
        {
            return error("[iterate]: no declarator");
        }
        Iterator it1 = decl.getNames().iterator();
        if( !it1.hasNext() )
        {
            return error("[iterate]: no iterator");
        }
        String iteratorVarName = ((MName)it1.next()).getValue();
        if( it1.hasNext() )
        {
            conCode.helpers_ += error("[iterate]: too much iterators")+"\n";
        }
        //
        OclType accType = OclNormalization.convertOclType(decl.getOptType(), creator_);
        String accName = decl.getOptName();
        MExpression accExpr = decl.getOptExpression();
        if( accName==null || accExpr==null )
        {
            return error("[iterate]: no valid accumulator");
        }
        if( accType==null )
        {
            return error("[iterate]: invalid accumulator type");
        }
        parent.setOclType(accType);
        String accInitCode = makeCode(accExpr, conCode);
        //
        pushLocalVariables();
        iteratorVarName = newLocalVariable(iteratorVarName, itemType);
        accName = newLocalVariable(accName, accType);
        String oldHelperCode = conCode.helpers_;
        conCode.helpers_ = "";
        String expression = getParameterCode(pc,0,conCode);
        String extraHelpers = conCode.helpers_;
        conCode.helpers_ = oldHelperCode;
        String accCppType = getLanguageType(accType, true, true);
        conCode.helpers_ += getStatements_iterate( accCppType, accName, accInitCode,
            inputVarName, inputItemType, iteratorVarName, expression, extraHelpers );
        popLocalVariables();
        return accName;
    }


    /**
     * Returns the statements of the collection operation 'iterate'.
     *
     * @param accCppType        language type of the accumulator
     * @param accName           variable name of the accumulator
     * @param accInitCode       init. code of the accumulator
     * @param inputVarName      variable name of the input collection
     * @param inputItemType     item type of the input collection
     * @param iteratorVarName   variable name of the iterator
     * @param expression        source code of the expression
     * @param extraHelpers      helper statements for 'expression'
     */
    abstract protected String getStatements_iterate( String accCppType,
        String accName, String accInitCode, String inputVarName,
        String inputItemType, String iteratorVarName, String expression,
        String extraHelpers );


}
