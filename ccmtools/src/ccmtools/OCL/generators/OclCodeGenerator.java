/* CCM Tools : OCL generators
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003, 2004 Salomon Automation
 *
 * $Id: OclCodeGenerator.java,v 1.3 2005/02/02 09:55:00 rlechner Exp $
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

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;
import java.util.Vector;

import oclmetamodel.MActualParameters;
import oclmetamodel.MClassifierContext;
import oclmetamodel.MConstraint;
import oclmetamodel.MConstraintExpression;
import oclmetamodel.MExpression;
import oclmetamodel.MFile;
import oclmetamodel.MOperationContext;
import oclmetamodel.MPackage;
import oclmetamodel.MPropertyCall;
import oclmetamodel.MPropertyCallParameters;
import oclmetamodel.OclType;
import ccmtools.Metamodel.BaseIDL.MContained;
import ccmtools.Metamodel.BaseIDL.MContainer;
import ccmtools.Metamodel.BaseIDL.MDefinitionKind;
import ccmtools.Metamodel.BaseIDL.MIDLType;
import ccmtools.Metamodel.BaseIDL.MOperationDef;
import ccmtools.Metamodel.BaseIDL.MParameterDef;
import ccmtools.OCL.parser.OclConstants;
import ccmtools.OCL.utils.OclElementCreator;
import ccmtools.OCL.utils.OclNormalization;
import ccmtools.OCL.utils.OclTextWriter;


/**
 * Base class for all OCL generators.
 *
 * @author Robert Lechner
 * @version $Date: 2005/02/02 09:55:00 $
 */
public abstract class OclCodeGenerator
{
    /**
     * The name of the pseudo package which is used for global interfaces and components.
     */
    public static final String GLOBAL_CONTEXT_NAME = "GLOBAL";


    /**
     * true for extra debug output
     */
    public static boolean OCL_DEBUG_OUTPUT;


    /**
     * The parse tree creator.
     *
     * @see {@link ccmtools.utils.Factory#getElementCreator}
     */
    protected OclElementCreator creator_;

    /**
     * The normalized parse tree.
     */
    protected MFile oclParseTree_;

    /**
     * Calculates the type of OCL expressions.
     */
    protected OclTypeChecker typeChecker_;


    /**
     * Stores the source code of all invariants.
     * The map stores instances of {@link ConstraintCode}.
     */
    protected HashMap invariantCodeMap_ = new HashMap();

    /**
     * Stores the source code of all preconditions.
     * The map stores instances of {@link ConstraintCode}.
     */
    protected HashMap preconditionCodeMap_ = new HashMap();

    /**
     * Stores the source code of all postconditions.
     * The map stores instances of {@link ConstraintCode}.
     */
    protected HashMap postconditionCodeMap_ = new HashMap();


    /**
     * Creates an OCL code generator.
     *
     * @param creator  The parse tree creator.
     * @param parseTree  The normalized parse tree.
     * @param checker  Calculates the type of OCL expressions.
     */
    protected OclCodeGenerator( OclElementCreator creator, MFile parseTree, OclTypeChecker checker )
    {
        creator_ = creator;
        oclParseTree_ = parseTree;
        typeChecker_ = checker;
    }


    private int error_counter_;

    public void resetErrorCounter()
    {
        error_counter_ = 0;
    }

    public int getErrorCounter()
    {
        return error_counter_;
    }

    /**
     * Prints an error message.
     * @returns a C++/Java comment including the message
     */
    protected String error( String message )
    {
        error_counter_++;
        System.err.println("--->  "+message);
        return "/* "+message+" */";
    }


    //////////////////////////////////////////////////////////////////////////


    private HashMap localVariableNames_ = new HashMap();
    private HashMap localVariableTypes_ = new HashMap();
    private Stack variableNameStack_ = new Stack();
    private Stack variableTypeStack_ = new Stack();


    protected void pushLocalVariables()
    {
        HashMap oldNames = localVariableNames_;
        HashMap oldTypes = localVariableTypes_;
        variableNameStack_.push(oldNames);
        variableTypeStack_.push(oldTypes);
        localVariableNames_ = new HashMap(oldNames);
        localVariableTypes_ = new HashMap(oldTypes);
    }


    protected void popLocalVariables()
    {
        localVariableNames_ = (HashMap)variableNameStack_.pop();
        localVariableTypes_ = (HashMap)variableTypeStack_.pop();
    }


    protected String newLocalVariable( String key, OclType ot )
    {
        String value = getNextHelperName();
        localVariableNames_.put(key, value);
        ElementType et = new ElementType();
        et.oclType_ = ot;
        localVariableTypes_.put(key, et);
        return value;
    }


    protected String newLocalVariable( String key, ElementType et )
    {
        String value = getNextHelperName();
        localVariableNames_.put(key, value);
        localVariableTypes_.put(key, et);
        return value;
    }


    protected String getLocalVariableName( String key )
    {
        return (String)localVariableNames_.get(key);
    }


    protected ElementType getLocalVariableType( String key )
    {
        return (ElementType)localVariableTypes_.get(key);
    }


    //////////////////////////////////////////////////////////////////////////


    /**
     * Returns the unique name of an IDL object.
     */
    public static String getPathname( MContained obj )
    {
        String suffix = obj.getIdentifier();
        String prefix = getPackage( obj.getDefinedIn(), true );
        return prefix+OclConstants.PATHNAME_SEPARATOR+suffix;
    }


    /**
     * Returns the package name of an IDL object.
     */
    public static String getPackage( MContained obj, boolean setGlobal )
    {
        if( obj!=null )
        {
            MDefinitionKind defKind = obj.getDefinitionKind();
            if( defKind.equals(MDefinitionKind.DK_MODULE) )
            {
                String suffix = obj.getIdentifier();
                String prefix = getPackage( obj.getDefinedIn(), false );
                if( prefix==null )
                {
                    return suffix;
                }
                return prefix+OclConstants.PATHNAME_SEPARATOR+suffix;
            }
        }
        return setGlobal ? GLOBAL_CONTEXT_NAME : null;
    }


    /**
     * Returns the OCL parameters of an IDL operation.
     */
    public static String getOperationParameters( MOperationDef op )
    {
        Iterator it = op.getParameters().iterator();
        if( it.hasNext() )
        {
            String result = OclNormalization.makeOclTypeName( (MParameterDef)it.next() );
            while( it.hasNext() )
            {
                String typeName = OclNormalization.makeOclTypeName( (MParameterDef)it.next() );
                result += ","+typeName;
            }
//            System.err.println("---> operation parameters: "+result);
            return result;
        }
        else
        {
            return "";
        }
    }


    //////////////////////////////////////////////////////////////////////////


    /**
     * The prefix of all helper variables.
     */
    public static final String HELPER_NAME_PREFIX = "DbC_var_";

    private int helpersCounter_;

    /**
     * Returns a new name for local variables or other helpers.
     */
    protected String getNextHelperName()
    {
        helpersCounter_++;
        return HELPER_NAME_PREFIX + helpersCounter_;
    }


    /**
     * Tests if the expression is a helper variable name.
     */
    boolean isHelper( String expr )
    {
        if( !expr.startsWith(HELPER_NAME_PREFIX) )
        {
            return false;
        }
        for( int index=HELPER_NAME_PREFIX.length(); index<expr.length(); index++ )
        {
            char c = expr.charAt(index);
            if( c<'0' || c>'9' )
            {
                return false;
            }
        }
        return true;
    }


    //////////////////////////////////////////////////////////////////////////


    /**
     * the local adapter
     */
    protected MContainer baseModuleType_;


    /**
     * Returns the source code of an invariant.
     *
     * @param theClass  the class (interface, component, etc.)
     */
    public ConstraintCode makeCodeForInvariant( MContainer theClass )
    {
        String packageName = getPackage( theClass.getDefinedIn(), true );
        String className = theClass.getIdentifier();
        String key = packageName + OclConstants.PATHNAME_SEPARATOR + className;
        if( invariantCodeMap_.containsKey(key) )
        {
            return (ConstraintCode)invariantCodeMap_.get(key);
        }
        baseModuleType_ = theClass;
        String identifier = "class '"+key+"'";
        ConstraintCode code = getConstraintCode();
        code.theClass_ = theClass;
        MClassifierContext context = getClassifierContext(packageName,className);
        if( context!=null )
        {
            Iterator it = context.getConstraints().iterator();
            while( it.hasNext() )
            {
                MConstraint con = (MConstraint)it.next();
                String conTitle = makeTitle(con);
                MConstraintExpression e = con.getExpression();
                ConstraintCode c2 = makeCode(e, code);
                code.helpers_ += c2.helpers_;
                code.addExpression_And(c2.expression_, e.getExpression(), creator_);
                code.statements_ += makeDbcCondition(c2.expression_, c2.helpers_, conTitle,
                                                     identifier, OclConstants.STEREOTYPE_INVARIANT);
            }
        }
        ClassIterator it = ClassIterator.getIterator(theClass,false);
        while( it.hasNext() )
        {
            ConstraintCode c2 = makeCodeForInvariant( it.next() );
            code.helpers_ += c2.helpers_;
            code.addExpression_And(c2.expression_, c2.constraint_, creator_);
            code.statements_ += c2.statements_;
        }
        invariantCodeMap_.put(key,code);
        return code;
    }


    /**
     * Returns the source code of a postcondition.
     *
     * @param theClass  the class (interface, component, etc.)
     * @param op  the operation definition
     */
    public ConstraintCode makeCodeForPostcondition( MContainer theClass, MOperationDef op )
    {
        String packageName = getPackage( theClass.getDefinedIn(), true );
        String className = theClass.getIdentifier();
        String operationName = op.getIdentifier();
        String operationContextIdentifier = className+OclConstants.PATHNAME_SEPARATOR+
                                            operationName+"("+getOperationParameters(op)+")";
        String key = packageName+OclConstants.PATHNAME_SEPARATOR+operationContextIdentifier;
        if( postconditionCodeMap_.containsKey(key) )
        {
            return (ConstraintCode)postconditionCodeMap_.get(key);
        }
        baseModuleType_ = theClass;
        String identifier = "method '"+key+"'";
        ConstraintCode conCode = getConstraintCode();
        conCode.theClass_ = theClass;
        conCode.opCtxt_ = getOperationContext(packageName,operationContextIdentifier);
        conCode.returnType_ = op;
        if( conCode.opCtxt_!=null )
        {
            Iterator it = conCode.opCtxt_.getConstraints().iterator();
            while( it.hasNext() )
            {
                MConstraint con = (MConstraint)it.next();
                if( con.getStereotype().equals(OclConstants.STEREOTYPE_POSTCONDITION) )
                {
                    String conTitle = makeTitle(con);
                    MConstraintExpression e = con.getExpression();
                    ConstraintCode c2 = makeCode(e, conCode);
                    conCode.helpers_ += c2.helpers_;
                    conCode.preStatements_ += c2.preStatements_;
                    conCode.addExpression_And(c2.expression_, e.getExpression(), creator_);
                    conCode.statements_ += makeDbcCondition(c2.expression_, c2.helpers_, conTitle,
                                                            identifier, OclConstants.STEREOTYPE_POSTCONDITION);
                }
            }
        }
        ClassIterator it = ClassIterator.getIterator(theClass,false);
        while( it.hasNext() )
        {
            ConstraintCode c2 = makeCodeForPostcondition( it.next(), op );
            conCode.helpers_ += c2.helpers_;
            conCode.preStatements_ += c2.preStatements_;
            conCode.addExpression_And(c2.expression_, c2.constraint_, creator_);
            conCode.statements_ += c2.statements_;
        }
        postconditionCodeMap_.put(key,conCode);
        return conCode;
    }


    /**
     * Returns the source code of a precondition.
     *
     * @param theClass  the class (interface, component, etc.)
     * @param op  the operation definition
     */
    public ConstraintCode makeCodeForPrecondition( MContainer theClass, MOperationDef op )
    {
        String packageName = getPackage( theClass.getDefinedIn(), true );
        String className = theClass.getIdentifier();
        String operationName = op.getIdentifier();
        String suffix = OclConstants.PATHNAME_SEPARATOR+operationName+"("+getOperationParameters(op)+")";
        String operationContextIdentifier = className+suffix;

        String key = packageName+OclConstants.PATHNAME_SEPARATOR+operationContextIdentifier;
        if( preconditionCodeMap_.containsKey(key) )
        {
            return (ConstraintCode)preconditionCodeMap_.get(key);
        }
        baseModuleType_ = theClass;
        String identifier = "method '"+key+"'";
        ConstraintCode conCode = getConstraintCode();
        conCode.theClass_ = theClass;
        conCode.opCtxt_ = getOperationContext(packageName,operationContextIdentifier);
        conCode.returnType_ = op;
        //
        Vector classes = new Vector();
        if( hasPrecondition(theClass,suffix) )
        {
            classes.add(theClass);
        }
        ClassIterator it = ClassIterator.getIterator(theClass,true);
        while( it.hasNext() )
        {
            MContainer c2 = it.next();
            if( hasPrecondition(c2,suffix) )
            {
                classes.add(c2);
            }
        }
        if( classes.size()>1 )
        {
            preCodeMultiple(conCode,theClass,op,identifier);
        }
        else if( classes.size()==1 )
        {
            MContainer c2 = (MContainer)classes.get(0);
            if( c2==theClass )
            {
                Iterator it2 = conCode.opCtxt_.getConstraints().iterator();
                while( it2.hasNext() )
                {
                    MConstraint con = (MConstraint)it2.next();
                    if( con.getStereotype().equals(OclConstants.STEREOTYPE_PRECONDITION) )
                    {
                        String conTitle = makeTitle(con);
                        MConstraintExpression e = con.getExpression();
                        ConstraintCode c4 = makeCode(e, conCode);
                        conCode.helpers_ += c4.helpers_;
                        conCode.addExpression_And(c4.expression_, e.getExpression(), creator_);
                        conCode.statements_ += makeDbcCondition(c4.expression_, c4.helpers_, conTitle,
                                                    identifier, OclConstants.STEREOTYPE_PRECONDITION);
                    }
                }
            }
            else
            {
                ConstraintCode c3 = makeCodeForPrecondition(c2,op);
                conCode.helpers_ = c3.helpers_;
                conCode.expression_ = c3.expression_;
                conCode.statements_ = c3.statements_;
                conCode.constraint_ = c3.constraint_;
            }
        }
        //
        preconditionCodeMap_.put(key,conCode);
        return conCode;
    }

    private boolean hasPrecondition( MContainer theClass, String suffix )
    {
        String packageName = getPackage( theClass.getDefinedIn(), true );
        String className = theClass.getIdentifier();
        MOperationContext ctxt = getOperationContext(packageName,className+suffix);
        if( ctxt!=null )
        {
            Iterator it = ctxt.getConstraints().iterator();
            while( it.hasNext() )
            {
                MConstraint con = (MConstraint)it.next();
                if( con.getStereotype().equals(OclConstants.STEREOTYPE_PRECONDITION) )
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void preCodeMultiple( ConstraintCode conCode, MContainer theClass,
                                  MOperationDef op, String identifier )
    {
        if( conCode.opCtxt_!=null )
        {
            Iterator it = conCode.opCtxt_.getConstraints().iterator();
            while( it.hasNext() )
            {
                MConstraint con = (MConstraint)it.next();
                if( con.getStereotype().equals(OclConstants.STEREOTYPE_PRECONDITION) )
                {
                    MConstraintExpression e = con.getExpression();
                    ConstraintCode c2 = makeCode(e, conCode);
                    conCode.helpers_ += c2.helpers_;
                    conCode.addExpression_And(c2.expression_, e.getExpression(), creator_);
                }
            }
        }
        ClassIterator it = ClassIterator.getIterator(theClass,false);
        while( it.hasNext() )
        {
            ConstraintCode c2 = makeCodeForPrecondition( it.next(), op );
            conCode.helpers_ += c2.helpers_;
            conCode.addExpression_Or(c2.expression_, c2.constraint_, creator_);
        }
        if( conCode.expression_.length()>0 )
        {
            conCode.statements_ = makeDbcCondition( conCode.expression_, conCode.helpers_,
                                                 OclConstants.STEREOTYPE_PRECONDITION, identifier,
                                                 OclConstants.STEREOTYPE_PRECONDITION );
        }
    }


    /**
     * Returns the implementation of {@link ConstraintCode} for the current language.
     */
    abstract protected ConstraintCode getConstraintCode();

    /**
     * Returns the implementation of {@link ConstraintCode} for the current language.
     *
     * @param reference  reference parameters
     */
    abstract protected ConstraintCode getConstraintCode( ConstraintCode reference );


    /**
     * Returns a implementation of {@link ConstraintCode}, with idlType_ set to 'idlType'.
     */
    protected ConstraintCode getConstraintCode( ConstraintCode reference, MIDLType idlType )
    {
        ConstraintCode result = getConstraintCode(reference);
        if( idlType!=null )
        {
            result.idlType_ = idlType;
            result.theClass_ = null;
        }
        return result;
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
    abstract protected String makeDbcCondition( String expression, String helpers, String title,
                                                String identifier, String type );


    /**
     * Returns type and name of a constraint.
     */
    public static String makeTitle( MConstraint con )
    {
        String name = con.getName();
        if( name==null )
        {
            return con.getStereotype();
        }
        else
        {
            return con.getStereotype() + " '" + name + "'";
        }
    }


    /**
     * Searches for a classifier context in the OCL parse tree.
     *
     * @param packageName  the name of the package
     * @param className  the name of the classifier context
     * @return the context or null
     */
    protected MClassifierContext getClassifierContext( String packageName, String className )
    {
        MPackage pkg = getOclPackage(packageName);
        if( pkg!=null )
        {
            Iterator it = pkg.getContexts().iterator();
            while( it.hasNext() )
            {
                Object obj = it.next();
                if( obj instanceof MClassifierContext )
                {
                    MClassifierContext ctxt = (MClassifierContext)obj;
                    if( ctxt.getClassName().equals(className) )
                    {
                        return ctxt;
                    }
                }
            }
        }
        return null;
    }


    /**
     * Searches for an operation context in the OCL parse tree.
     *
     * @param packageName  the name of the package
     * @param contextIdentifier  the name of the operation context
     * @return the context or null
     */
    protected MOperationContext getOperationContext( String packageName, String contextIdentifier )
    {
        MPackage pkg = getOclPackage(packageName);
        if( pkg!=null )
        {
            Iterator it = pkg.getContexts().iterator();
            while( it.hasNext() )
            {
                Object obj = it.next();
                if( obj instanceof MOperationContext )
                {
                    MOperationContext ctxt = (MOperationContext)obj;
                    if( ctxt.getIdentifier().equals(contextIdentifier) )
                    {
                        return ctxt;
                    }
                }
            }
        }
        return null;
    }


    /**
     * Searches for a package in the OCL parse tree.
     *
     * @param packageName  the name of the package
     * @return the package or null
     */
    protected MPackage getOclPackage( String packageName )
    {
        Iterator it = oclParseTree_.getPackages().iterator();
        while( it.hasNext() )
        {
            MPackage pkg = (MPackage)it.next();
            if( pkg.getName().equals(packageName) )
            {
                return pkg;
            }
        }
        return null;
    }


    /**
     * Calculates the helper statements and the source code of an OCL expression.
     *
     * @param expression  the OCL expression
     * @param reference  reference parameters
     * @return the source code (only helpers_, preStatements_ and expression_)
     */
    protected ConstraintCode makeCode( MConstraintExpression expression, ConstraintCode reference )
    {
        boolean old_flag = OCL_DEBUG_OUTPUT;
        OCL_DEBUG_OUTPUT = true;
        ConstraintCode code = getConstraintCode(reference);
        code.expression_ = makeCode( expression.getExpression(), code );
        OCL_DEBUG_OUTPUT = old_flag;
        return code;
    }


    /**
     * Calculates the helper statements and the source code of an OCL expression.
     *
     * @param expression  the OCL expression
     * @return the source code (only helpers_, preStatements_ and expression_)
     */
    public ConstraintCode getCodeForOclExpression( MExpression expression )
    {
        ConstraintCode code = getConstraintCode();
        code.expression_ = makeCode( expression, code );
        return code;
    }


    /**
     * Calculates the helper statements, the source code and the type of an OCL expression.
     *
     * @param expr     the OCL expression
     * @param conCode  only helpers_ and preStatements_ will be changed
     * @return the source code
     */
    protected String makeCode( MExpression expr, ConstraintCode conCode )
    {
        String source = makeSourceCode(expr, conCode);
        OclType type = expr.getOclType();
        if( type==null )
        {
            type = typeChecker_.makeType(expr, conCode).oclType_;
            if( type==null && OCL_DEBUG_OUTPUT )
            {
                System.err.println("warning: OclCodeGenerator.makeCode : cannot calculate type of expression");
            }
        }
        return source;
    }


    /**
     * Calculates the helper statements and the source code of an OCL expression.
     *
     * @param expr  the OCL expression
     * @param code  only helpers_ and preStatements_ will be changed
     * @return the source code
     */
    abstract protected String makeSourceCode( MExpression expr, ConstraintCode code );


    /**
     * Calculates the source code for the parameter of a property call.
     *
     * @param pc  the property call
     * @param index  the (zero based) index of the parameter
     * @param conCode  only helpers_ and preStatements_ will be changed
     * @return the source code
     */
    protected String getParameterCode( MPropertyCall pc, int index, ConstraintCode conCode )
    {
        MPropertyCallParameters pcp = pc.getCallParameters();
        if( pcp!=null )
        {
            MActualParameters ap = pcp.getParameters();
            if( ap!=null )
            {
                Iterator it = ap.getExpressions().iterator();
                int counter = 0;
                while( it.hasNext() )
                {
                    MExpression expr = (MExpression)it.next();
                    if( counter==index )
                    {
                        return makeCode(expr,conCode);
                    }
                    counter++;
                }
            }
        }
        return error("property call '"+pc.getName()+"': no parameter #"+index);
    }


    /**
     * Calculates the type of a property call parameter.
     *
     * @param pc  the property call
     * @param index  the (zero based) index of the parameter
     * @param conCode  no change
     * @return the type (or null)
     */
    protected OclType getParameterType( MPropertyCall pc, int index, ConstraintCode conCode )
    {
        MPropertyCallParameters pcp = pc.getCallParameters();
        if( pcp!=null )
        {
            MActualParameters ap = pcp.getParameters();
            if( ap!=null )
            {
                Iterator it = ap.getExpressions().iterator();
                int counter = 0;
                while( it.hasNext() )
                {
                    MExpression expr = (MExpression)it.next();
                    if( counter==index )
                    {
                        OclType type = expr.getOclType();
                        if( type==null )
                        {
                            ConstraintCode dummy = getConstraintCode(conCode);
                            makeCode(expr,dummy);
                            type = expr.getOclType();
                        }
                        return type;
                    }
                    counter++;
                }
            }
        }
        return null;
    }
    
    
    //------------------------------------------------------------------------
    
    
    /**
     * Writes the content of {@link invariantCodeMap_}, {@link preconditionCodeMap_}
     * and {@link postconditionCodeMap_}.
     *
     * @param filename  the name of the text file
     */
    public void writeConstraintInformation( String filename, OclNormalization norm ) throws Exception
    {
        FileWriter w = new FileWriter(filename);
        w.write( "invariants:\n"+
                 "===========\n\n" );
        writeText( invariantCodeMap_, w, norm );
        w.write( "\n\n"+
                 "preconditions:\n"+
                 "==============\n\n" );
        writeText( preconditionCodeMap_, w, norm );
        w.write( "\n\n"+
                 "postconditions:\n"+
                 "===============\n\n" );
        writeText( postconditionCodeMap_, w, norm );
        w.close();
    }
    
    private static void writeText( HashMap map, FileWriter w, OclNormalization norm ) throws Exception
    {
        Iterator it = map.keySet().iterator();
        while( it.hasNext() )
        {
            String key = (String)it.next();
            ConstraintCode code = (ConstraintCode)map.get(key);
            if( code.constraint_!=null )
            {
                w.write(key+"\n");
                for( int index=0; index<key.length(); index++ )
                {
                    w.write("-");
                }
                w.write("\n");
                MExpression expr = norm.normalize(code.constraint_);
                OclTextWriter w2 = new OclTextWriter(w);
                w2.write(expr,true);
                w2 = null;
                w.write("\n\n\n");
            }
        }
    } 
}
