/* CCM Tools : OCL generators
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003, 2004 Salomon Automation
 *
 * $Id: OclCppGenerator.java,v 1.1 2004/07/20 16:21:19 teiniker Exp $
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

import oclmetamodel.*;
import ccmtools.OCL.utils.*;
import ccmtools.OCL.parser.OclConstants;
import ccmtools.Metamodel.BaseIDL.MInterfaceDef;


/**
 * An OCL generator for C++ code.
 *
 * @author Robert Lechner
 * @version $Date: 2004/07/20 16:21:19 $
 */
public class OclCppGenerator extends OclStandardGenerator
{
    /**
     * Creates an OCL code generator for C++.
     *
     * @param creator  The parse tree creator.
     * @param parseTree  The normalized parse tree.
     * @param checker  Calculates the type of OCL expressions.
     */
    public OclCppGenerator( OclElementCreator creator, MFile parseTree, OclTypeChecker checker )
    {
        super(creator, parseTree, checker);
    }


    /**
     * Creates a complete DbC statement.
     *
     * @param expression  the code of the boolean expression
     * @param helpers  the statements of helper variables
     * @param title  the name of the statement
     * @param identifier  the unique name of the OCL context
     * @param type  the type of the constraint ({@link ccmtools.OCL.utils.OclConstants#STEREOTYPE_INVARIANT},
                    {@link ccmtools.OCL.utils.OclConstants#STEREOTYPE_PRECONDITION} or
                    {@link ccmtools.OCL.utils.OclConstants#STEREOTYPE_POSTCONDITION})
     */
    protected String makeDbcCondition( String expression, String helpers, String title,
                                       String identifier, String type )
    {
        String e, onExit="";
        if( type.equals(OclConstants.STEREOTYPE_INVARIANT) )
        {
            e = "InvariantException";
            onExit = ",DbC_onExit";
        }
        else if( type.equals(OclConstants.STEREOTYPE_PRECONDITION) )
        {
            e = "PreconditionException";
        }
        else if( type.equals(OclConstants.STEREOTYPE_POSTCONDITION) )
        {
            e = "PostconditionException";
        }
        else
        {
            e = "OclException";
        }
        return "  /* "+title+" */\n"+
               helpers+
               "  if(!(\n\t"+
               expression+"\n"+
               "  )) {const char* msg=\""+identifier+": "+title+" failed\";\n"+
               "      DEBUGNL(msg); throw "+e+"(msg,DbC_FUNCTION_NAME,__FILE__,__LINE__"+onExit+");}\n";
    }


    /**
     * Converts an OCL operator to a C++ operator.
     */
    protected String getLanguageOperator( String op )
    {
        if( op.equals(OclConstants.OPERATOR_EQUAL) )  { return "=="; }
        if( op.equals(OclConstants.OPERATOR_PLUS) )   { return "+"; }
        if( op.equals(OclConstants.OPERATOR_MINUS) )  { return "-"; }
        if( op.equals(OclConstants.OPERATOR_LT) )     { return "<"; }
        if( op.equals(OclConstants.OPERATOR_LE) )     { return "<="; }
        if( op.equals(OclConstants.OPERATOR_GE) )     { return ">="; }
        if( op.equals(OclConstants.OPERATOR_GT) )     { return ">"; }
        if( op.equals(OclConstants.OPERATOR_DIVIDE) ) { return "/"; }
        if( op.equals(OclConstants.OPERATOR_MULT) )   { return "*"; }
        if( op.equals(OclConstants.OPERATOR_NEQUAL) ) { return "!="; }
        if( op.equals(OclConstants.OPERATOR_NOT) )    { return "!"; }
        if( op.equals(OclConstants.OPERATOR_OR) )     { return "||"; }
        if( op.equals(OclConstants.OPERATOR_AND) )    { return "&&"; }
        return error("OclCppGenerator.getLanguageOperator:  unsupported OCL operator: "+op);
    }


    private final static String OCL_INTEGER = "OCL_Integer";
    private final static String OCL_REAL = "OCL_Real";
    private final static String OCL_STRING = "OCL_String";
    private final static String OCL_BOOLEAN = "OCL_Boolean";


    protected String getLanguageType( ElementType et )
    {
        if( et==null )
        {
            return error("OclCppGenerator.getLanguageType:  ElementType==null");
        }
        String result = getLanguageType(et.oclType_, true, true);
        if( et.oclType_!=null && (et.oclType_ instanceof OclCollection) )
        {
            return result;
        }
        if( isSmartPtr(et) )
        {
            return "WX::Utils::SmartPtr<"+result+">";
        }
        return result;
    }


    private boolean isSmartPtr( ElementType et )
    {
        if( et.idlType_==null )
        {
            return false;
        }
        return et.idlType_ instanceof MInterfaceDef;
    }


    /**
     * Creates the source code, which reads the attribute of a class.
     *
     * @param parentCode  the access code of the class
     * @param parentType  the type of the class
     * @param childCode   the name of the attribute
     */
    protected String getAccess( String parentCode, ElementType parentType, String childCode )
    {
        if( isSmartPtr(parentType) )
        {
            return parentCode+"->"+childCode;
        }
        return parentCode+"."+childCode;
    }


    /**
     * Converts an OCL type to a C++ type or class.
     *
     * @param type  the OCL type
     * @param collAlias  true=return the alias name if the type is a collection
     * @param itemAlias  true=use the alias name of the item type if the item is also a collection
     */
    protected String getLanguageType( OclType type, boolean collAlias, boolean itemAlias )
    {
        if( type==null )
        {
            return error("OclCppGenerator.getLanguageType:  null-type");
        }
        if( type instanceof OclInteger )
        {
            return OCL_INTEGER;
        }
        if( type instanceof OclReal )
        {
            return OCL_REAL;
        }
        if( type instanceof OclString )
        {
            return OCL_STRING;
        }
        if( type instanceof OclBoolean )
        {
            return OCL_BOOLEAN;
        }
        if( type instanceof OclCollection )
        {
            OclCollection coll = (OclCollection)type;
            String alias = coll.getAlias();
            if( collAlias && alias!=null )
            {
                return alias;
            }
            if( type instanceof OclSequence )
            {
                return getName_ClassSequence()+"< "+
                       getLanguageType( ((OclSequence)type).getType(), itemAlias, itemAlias )+" >";
            }
            if( type instanceof OclSet )
            {
                return getName_ClassSet()+"< "+
                       getLanguageType( ((OclSet)type).getType(), itemAlias, itemAlias )+" >";
            }
            if( type instanceof OclBag )
            {
                return getName_ClassBag()+"< "+
                       getLanguageType( ((OclBag)type).getType(), itemAlias, itemAlias )+" >";
            }
            return getName_ClassCollection()+"< "+
                   getLanguageType( coll.getType(), itemAlias, itemAlias )+" >";
        }
        if( type instanceof OclVoid )
        {
            return "void";
        }
        if( type instanceof OclUser )
        {
            return type.getName();
        }
        return error("OclCppGenerator.getLanguageType:  unsupported OCL type: "+type.getName());
    }


    protected String getLanguagePathName( String prefix, String suffix )
    {
        return prefix+"::"+suffix;
    }


    protected String getThis()
    {
        return "this->";
    }


    protected String getName_ClassCollection()
    {
        return "OCL_Collection";
    }

    protected String getName_ClassSequence()
    {
        return "OCL_Sequence";
    }

    protected String getName_ClassSet()
    {
        return "OCL_Set";
    }

    protected String getName_ClassBag()
    {
        return "OCL_Bag";
    }

    protected String getName_ClassRange()
    {
        return "OCL_Sequence_Integer";
    }


    /**
     * Returns a new instance of {@link ConstraintCppCode}.
     */
    protected ConstraintCode getConstraintCode()
    {
        return new ConstraintCppCode();
    }

    /**
     * Returns a new instance of {@link ConstraintCppCode}.
     *
     * @param reference  reference parameters
     */
    protected ConstraintCode getConstraintCode( ConstraintCode reference )
    {
        return new ConstraintCppCode(reference);
    }


    protected String getExpr_If( String exprCond, String exprTrue, String exprFalse )
    {
        return "("+exprCond+")?("+exprTrue+"):("+exprFalse+")";
    }


    protected String getExpr_Divide( String z, String n, ConstraintCode code )
    {
        String helper = getNextHelperName();
        code.helpers_ += "  const "+OCL_REAL+" "+helper+" = double("+z+")/double("+n+");\n";
        return helper;
    }


    protected String getExpr_Xor( String p1, String p2, ConstraintCode code )
    {
        // no logic xor in C++
        String helper = getNextHelperName();
        code.helpers_ += "  const "+OCL_BOOLEAN+" "+helper+" = (int("+p1+")^int("+p2+"))!=0;\n";
        return helper;
    }


    protected String getExpr_Implies( String p1, String p2, ConstraintCode code )
    {
        return "(!("+p1+"))||("+p2+")";   // (x => y) == (!x or y)
    }


    protected String getStatement_CollectionInit( String cppClass, String cppType, String result )
    {
        return "  "+cppClass+"< "+cppType+" > "+result+";\n";
    }


    protected String getStatement_CollectionAdd( String collection, String code )
    {
        return "  "+collection+".add("+code+");\n";
    }


    protected String getStatements_CollectionRange( MCollectionRange range, String result,
                                                    ConstraintCode conCode )
    {
        String code1 = makeCode(range.getLowerRange(), conCode);
        String code2 = makeCode(range.getUpperRange(), conCode);
        String lower = getNextHelperName();
        String upper = getNextHelperName();
        String index = getNextHelperName();
        return "  const "+OCL_INTEGER+" "+lower+" = "+code1+";\n"+
               "  const "+OCL_INTEGER+" "+upper+" = "+code2+";\n"+
               "  for("+OCL_INTEGER+" "+index+"="+lower+"; "+index+"<="+upper+"; "+index+"++)\n"+
               "    "+result+".add("+index+");\n";
    }


    /**
     * Creates a new string variable with an constant text.
     * @param value  the text (not the source code!)
     * @return the name of the variable
     */
    protected String getLiteral_String( String value, ConstraintCode code )
    {
        String helper = getNextHelperName();
        code.helpers_ += "  const "+OCL_STRING+" "+helper+" = \""+value+"\";\n";
        return helper;
    }


    /**
     * Creates a new real variable.
     * @param value  the source code of the initial value
     * @return the name of the variable
     */
    protected String getLiteral_Real( String value, ConstraintCode code )
    {
        String helper = getNextHelperName();
        code.helpers_ += "  const "+OCL_REAL+" "+helper+" = "+value+";\n";
        return helper;
    }


    /**
     * Returns an integer constant.
     */
    protected String getLiteral_Integer( int value, ConstraintCode code )
    {
    	return OCL_INTEGER+"("+value+")";
    }


    protected String getExpr_String_size( String exprCode, ConstraintCode conCode )
    {
        return OCL_INTEGER+"("+exprCode+".length())";
    }


    protected String getExpr_String_concat( String exprCode, String parameter, ConstraintCode conCode )
    {
        String helper = getNextHelperName();
        conCode.helpers_ += "  const "+OCL_STRING+" "+helper+" = "+exprCode+"+("+parameter+");\n";
        return helper;
    }


    protected String getExpr_String_toUpper( String exprCode, ConstraintCode conCode )
    {
        String h = getNextHelperName();
        conCode.helpers_ += "  const "+OCL_STRING+" "+h+" = OCL_toUpper("+exprCode+");\n";
        return h;
    }


    protected String getExpr_String_toLower( String exprCode, ConstraintCode conCode )
    {
        String h = getNextHelperName();
        conCode.helpers_ += "  const "+OCL_STRING+" "+h+" = OCL_toLower("+exprCode+");\n";
        return h;
    }


    protected String getExpr_String_substring( String exprCode, String p1, String p2, ConstraintCode conCode )
    {
        String lower = getNextHelperName();
        String upper = getNextHelperName();
        String result = getNextHelperName();
        conCode.helpers_ += "  const int "+lower+" = "+p1+";\n"+      // not OCL_INTEGER !
                            "  const int "+upper+" = "+p2+";\n"+      // not OCL_INTEGER !
                            "  const "+OCL_STRING+" "+result+" = "+exprCode+".substr("+lower+"-1,"+
                                                                   upper+"-"+lower+"+1);\n";
        return result;
    }


    protected String getExpr_Integer_abs( String exprCode, ConstraintCode conCode )
    {
        return OCL_INTEGER+"(labs("+exprCode+"))";
    }


    protected String getExpr_Integer_div( String exprCode, String param, ConstraintCode conCode )
    {
        String h = getNextHelperName();
        conCode.helpers_ += "  const "+OCL_INTEGER+" "+h+" = OCL_div("+exprCode+","+param+");\n";
        return h;
    }


    protected String getExpr_Integer_mod( String exprCode, String param, ConstraintCode conCode )
    {
        String h = getNextHelperName();
        conCode.helpers_ += "  const "+OCL_INTEGER+" "+h+" = OCL_mod("+exprCode+","+param+");\n";
        return h;
    }


    protected String getExpr_Integer_max( String exprCode, String param, ConstraintCode conCode )
    {
        String h1 = getNextHelperName();
        String h2 = getNextHelperName();
        String h3 = getNextHelperName();
        conCode.helpers_ += "  const "+OCL_INTEGER+" "+h1+" = "+exprCode+";\n"+
                            "  const "+OCL_INTEGER+" "+h2+" = "+param+";\n"+
                            "  const "+OCL_INTEGER+" "+h3+" = "+h1+">="+h2+" ? "+h1+" : "+h2+";\n";
        return h3;
    }


    protected String getExpr_Integer_min( String exprCode, String param, ConstraintCode conCode )
    {
        String h1 = getNextHelperName();
        String h2 = getNextHelperName();
        String h3 = getNextHelperName();
        conCode.helpers_ += "  const "+OCL_INTEGER+" "+h1+" = "+exprCode+";\n"+
                            "  const "+OCL_INTEGER+" "+h2+" = "+param+";\n"+
                            "  const "+OCL_INTEGER+" "+h3+" = "+h1+"<="+h2+" ? "+h1+" : "+h2+";\n";
        return h3;
    }


    protected String getExpr_Real_abs( String exprCode, ConstraintCode conCode )
    {
        return "fabs("+exprCode+")";
    }


    protected String getExpr_Real_floor( String exprCode, ConstraintCode conCode )
    {
        return OCL_INTEGER+"(floor("+exprCode+"))";
    }


    protected String getExpr_Real_max( String exprCode, String param, ConstraintCode conCode )
    {
        String h1 = getNextHelperName();
        String h2 = getNextHelperName();
        String h3 = getNextHelperName();
        conCode.helpers_ += "  const "+OCL_REAL+" "+h1+" = "+exprCode+";\n"+
                            "  const "+OCL_REAL+" "+h2+" = "+param+";\n"+
                            "  const "+OCL_REAL+" "+h3+" = "+h1+">="+h2+" ? "+h1+" : "+h2+";\n";
        return h3;
    }


    protected String getExpr_Real_min( String exprCode, String param, ConstraintCode conCode )
    {
        String h1 = getNextHelperName();
        String h2 = getNextHelperName();
        String h3 = getNextHelperName();
        conCode.helpers_ += "  const "+OCL_REAL+" "+h1+" = "+exprCode+";\n"+
                            "  const "+OCL_REAL+" "+h2+" = "+param+";\n"+
                            "  const "+OCL_REAL+" "+h3+" = "+h1+"<="+h2+" ? "+h1+" : "+h2+";\n";
        return h3;
    }


    protected String getExpr_Sequence_union( String exprCode, String param, ConstraintCode conCode )
    {
        return "OCL_union_Sequence("+exprCode+","+param+")";
    }


    protected String getExpr_Sequence_append( String exprCode, String param, ConstraintCode conCode )
    {
        return "OCL_append("+exprCode+","+param+")";
    }


    protected String getExpr_Sequence_prepend( String exprCode, String param, ConstraintCode conCode )
    {
        return "OCL_prepend("+exprCode+","+param+")";
    }


    protected String getExpr_subSequence( String exprCode, String lower, String upper, ConstraintCode conCode )
    {
        return "OCL_subSequence("+exprCode+","+lower+","+upper+")";
    }


    protected String getExpr_Sequence_at( String exprCode, String param, ConstraintCode conCode )
    {
        return exprCode+".at(("+param+")-1)";
    }


    protected String getExpr_Sequence_first( String exprCode, ConstraintCode conCode )
    {
        return exprCode+".front()";
    }


    protected String getExpr_Sequence_last( String exprCode, ConstraintCode conCode )
    {
        return exprCode+".back()";
    }


    protected String getExpr_Set_union( String exprCode, String param, ConstraintCode conCode )
    {
        return "OCL_union_Set("+exprCode+","+param+")";
    }


    protected String getExpr_Collection_union( String exprCode, String param, ConstraintCode conCode )
    {
        return "OCL_union("+exprCode+","+param+")";
    }


    protected String getExpr_Bag_intersection( String exprCode, String param, ConstraintCode conCode )
    {
        return "OCL_intersection_Bag("+exprCode+","+param+")";
    }


    protected String getExpr_Collection_intersection( String exprCode, String param, ConstraintCode conCode )
    {
        return "OCL_intersection("+exprCode+","+param+")";
    }


    protected String getExpr_Collection_size( String exprCode, ConstraintCode conCode )
    {
        return OCL_INTEGER+"("+exprCode+".size())";
    }


    protected String getExpr_Collection_includes( String exprCode, String param, ConstraintCode conCode )
    {
        return "OCL_includes("+exprCode+","+param+")";
    }


    protected String getExpr_Collection_count( String exprCode, String param, ConstraintCode conCode )
    {
        return "OCL_count("+exprCode+","+param+")";
    }


    protected String getExpr_Collection_includesAll( String exprCode, String param, ConstraintCode conCode )
    {
        return "OCL_includesAll("+exprCode+","+param+")";
    }


    protected String getExpr_Collection_excludesAll( String exprCode, String param, ConstraintCode conCode )
    {
        return "OCL_excludesAll("+exprCode+","+param+")";
    }


    protected String getExpr_Collection_isEmpty( String exprCode, ConstraintCode conCode )
    {
        return exprCode+".empty()";
    }


    protected String getExpr_Collection_including( String collClass, String collItem,
                                                   String exprCode, String param, ConstraintCode conCode )
    {
        String h = getNextHelperName();
        conCode.helpers_ += "  "+collClass+"< "+collItem+" > "+h+"("+exprCode+");\n";
        return h+".including("+param+")";
    }


    protected String getExpr_Collection_excluding( String collClass, String collItem,
                                                   String exprCode, String param, ConstraintCode conCode )
    {
        String h = getNextHelperName();
        conCode.helpers_ += "  "+collClass+"< "+collItem+" > "+h+"("+exprCode+");\n";
        return h+".excluding("+param+")";
    }


    protected String getExpr_symmetricDifference( String collItem, String exprCode, String param,
                                                  ConstraintCode conCode )
    {
        String h1 = getNextHelperName();
        String h2 = getNextHelperName();
        conCode.helpers_ += "  "+getName_ClassSet()+"< "+collItem+" > "+h1+"("+exprCode+");\n"+
                            "  "+getName_ClassSet()+"< "+collItem+" > "+h2+"("+param+");\n";
        return "OCL_symmetricDifference("+h1+","+h2+")";
    }


    protected String copyCollection( String collClass, String collItem,
                                     String exprCode, ConstraintCode conCode )
    {
        String h = getNextHelperName();
        conCode.helpers_ += "  "+collClass+"< "+collItem+" > "+h+"("+exprCode+");\n";
        return h;
    }


    protected String getExpr_Collection_asSet( String collItem, String exprCode, ConstraintCode conCode )
    {
        String h = getNextHelperName();
        conCode.helpers_ += "  "+getName_ClassSet()+"< "+collItem+" > "+h+"("+exprCode+");\n";
        return h;
    }


    protected String getExpr_Collection_asBag( String collItem, String exprCode, ConstraintCode conCode )
    {
        String h = getNextHelperName();
        conCode.helpers_ += "  "+getName_ClassBag()+"< "+collItem+" > "+h+"("+exprCode+");\n";
        return h;
    }


    protected String getExpr_Collection_asSequence( String collItem, String exprCode, ConstraintCode conCode )
    {
        String h = getNextHelperName();
        conCode.helpers_ += "  "+getName_ClassSequence()+"< "+collItem+" > "+h+"("+exprCode+");\n";
        return h;
    }


    protected String getExpr_Collection_sum( String exprCode, String cppCollection, String cppItem,
                                             ConstraintCode conCode )
    {
        String coll = getNextHelperName();
        conCode.helpers_ += "  "+cppCollection+"< "+cppItem+" > "+coll+"("+exprCode+");\n";
        String result = getNextHelperName();
        conCode.helpers_ += "  "+cppItem+" "+result+" = 0;\n";
        String index = getNextHelperName();
        conCode.helpers_ += "  for(OCL_Size "+index+"=0; "+index+"<"+coll+".size(); "+index+"++)\n"+
                            "    "+result+" += "+coll+"["+index+"];\n";
        return result;
    }


    protected String getExpr_Real_equals( String real1, String real2, ConstraintCode conCode )
    {
        return "OCL_equals("+real1+","+real2+")";
    }


    protected String setCollectionIterator( String collVarName, String cppItem,
                                            String iteratorVarName, ConstraintCode conCode )
    {
        String index = getNextHelperName();
        conCode.helpers_ += "  for(OCL_Size "+index+"=0; "+index+"<"+collVarName+".size(); "+index+"++)\n"+
                            "  {\n"+
                            "    "+cppItem+" "+iteratorVarName+"="+collVarName+"["+index+"];\n";
        return "  }\n";
    }


    protected String setBooleanHelper( String initValue, ConstraintCode conCode )
    {
        String h = getNextHelperName();
        conCode.helpers_ += "  "+OCL_BOOLEAN+" "+h+" = "+initValue+";\n";
        return h;
    }


    protected void setOrStatement( String result, String param1, String param2,
                                   ConstraintCode conCode )
    {
        conCode.helpers_ += "    "+result+" = ("+param1+")||("+param2+");\n";
    }


    protected void setAndStatement( String result, String param1, String param2,
                                    ConstraintCode conCode )
    {
        conCode.helpers_ += "    "+result+" = ("+param1+")&&("+param2+");\n";
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
    protected String start_select( String cppCollection, String cppItem, String collVarName,
                                   String iteratorVarName, ConstraintCode conCode)
    {
        String result = getNextHelperName();
        String index = getNextHelperName();
        conCode.helpers_ += "  "+cppCollection+"< "+cppItem+" > "+result+";\n"+
                            "  for(OCL_Size "+index+"=0; "+index+"<"+collVarName+".size(); "+index+"++)\n"+
                            "  {\n"+
                            "    "+cppItem+" "+iteratorVarName+"="+collVarName+"["+index+"];\n";
        return result;
    }


    /**
     * Finishes the collection operations 'select' and 'reject'.
     *
     * @param condition         the source code of the condition
     * @param iteratorVarName   variable name of the iterator
     * @param resultVarName     the return value of {@link start_select}
     * @param conCode           only 'conCode.helpers_' will be changed
     * @param reject            true if the operation is 'reject' (and not 'select')
     */
    protected void finish_select( String condition, String iteratorVarName,
                                  String resultVarName, ConstraintCode conCode,
                                  boolean reject)
    {
        if( reject )
        {
            conCode.helpers_ += "    if(!("+condition+"))\n";
        }
        else
        {
            conCode.helpers_ += "    if("+condition+")\n";
        }
        conCode.helpers_ += "    {\n"+
                            "      "+resultVarName+".add("+iteratorVarName+");\n"+
                            "    }\n"+
                            "  }\n";
    }


    protected String getExpr_Collection_any( String collVarName, String cppItem,
                                             ConstraintCode conCode )
    {
        String result = getNextHelperName();
        conCode.helpers_ +=
            "  if("+collVarName+".size()<1)\n"+
            "  {\n"+
            "    const char* msg=\"collection operation 'any': no element found\";\n"+
            "    DEBUGNL(msg);\n"+
            "    throw OclException(msg,DbC_FUNCTION_NAME,__FILE__,__LINE__);\n"+
            "  }\n"+
            "  "+cppItem+" "+result+" = "+collVarName+"[OCL_random(0,int("+collVarName+".size())-1)];\n";
        return result;
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
    protected String getStatements_collect( String resultCollectionType,
        String resultItemType, String resultVarName, String inputVarName,
        String inputItemType, String iteratorVarName, String expression,
        String extraHelpers )
    {
        String index = getNextHelperName();
        return "  "+resultCollectionType+"< "+resultItemType+" > "+resultVarName+";\n"+
               "  for(OCL_Size "+index+"=0; "+index+"<"+inputVarName+".size(); "+index+"++)\n"+
               "  {\n"+
               "    "+inputItemType+" "+iteratorVarName+" = "+inputVarName+"["+index+"];\n"+
               extraHelpers+
               "    "+resultVarName+".add("+expression+");\n"+
               "  }\n";
    }


    protected String getExpr_Collection_isUnique( String collVarName,
                                                  ConstraintCode conCode )
    {
        return collVarName+".isUnique()";
    }


    protected String getExpr_Collection_sortedBy( String collVarName, String refVarName,
                                                  String itemType, ConstraintCode conCode )
    {
        String result = getNextHelperName();
        conCode.helpers_ += "  OCL_Sortable_Sequence< "+itemType+" > "+result+"("+collVarName+");\n"+
                            "  "+result+".sortBy("+refVarName+");\n";
        return result;
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
    protected String getStatements_iterate( String accCppType,
        String accName, String accInitCode, String inputVarName,
        String inputItemType, String iteratorVarName, String expression,
        String extraHelpers )
    {
        String index = getNextHelperName();
        return "  "+accCppType+" "+accName+" = "+accInitCode+";\n"+
               "  for(OCL_Size "+index+"=0; "+index+"<"+inputVarName+".size(); "+index+"++)\n"+
               "  {\n"+
               "    "+inputItemType+" "+iteratorVarName+" = "+inputVarName+"["+index+"];\n"+
               extraHelpers+
               "    "+accName+" = "+expression+";\n"+
               "  }\n";
    }
}
