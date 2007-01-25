/* CCM Tools : OCL helpers
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003, 2004 Salomon Automation
 *
 * $Id: OclCreatorImp.java,v 1.1 2004/07/20 16:21:23 teiniker Exp $
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
import ccmtools.OCL.parser.*;


/**
 * Creates the parsetree of an OCL file by using the NetBeans Metadata Repository (MDR).
 *
 * @see {@link OclModelCreator}
 *
 * @author Robert Lechner
 */
public class OclCreatorImp extends OclModelCreator implements OclElementCreator
{
    /**
     * Creates the connection to the NetBeans Metadata Repository.
     * The name of the OCL metamodel is set to
     * {@link OclMdrAdapter#DEFAULT_METAMODEL_NAME}.
     *
     * @param mofFilename  the name of the MOF file (the OCL metamodel)
     *
     * @throws CreationFailedException  MDR error
     * @throws MalformedXMIException  XMI error
     * @throws IOException  file IO error
     * @throws NullPointerException  mofFilename or metamodel_ are null
     */
    public OclCreatorImp( String mofFilename )
     throws org.netbeans.api.mdr.CreationFailedException,
            javax.jmi.xmi.MalformedXMIException,
            java.io.IOException
    {
        super( mofFilename );
    }


    /**
     * Creates the connection to the NetBeans Metadata Repository.
     *
     * @param mofFilename  the name of the MOF file (the OCL metamodel)
     * @param metamodelname  the name of the OCL metamodel (the XMI package name)
     *
     * @throws CreationFailedException  MDR error
     * @throws MalformedXMIException  XMI error
     * @throws IOException  file IO error
     * @throws NullPointerException  mofFilename, metamodelname or metamodel_ are null
     */
    public OclCreatorImp( String mofFilename, String metamodelname )
     throws org.netbeans.api.mdr.CreationFailedException,
            javax.jmi.xmi.MalformedXMIException,
            java.io.IOException
    {
        super( mofFilename, metamodelname );
    }


    //////////////////////////////////////////////////////////////////////////


    public MFile createFile()
    {
        return metamodel_.getMFile().createMFile();
    }

    public MPackage createPackage( String name )
    {
        MPackage pkg = metamodel_.getMPackage().createMPackage();
        pkg.setName(name);
        return pkg;
    }

    public void add( MFile file, MPackage pkg )
    {
        metamodel_.getHasPackages().add(file,pkg);
    }

    public void add( MPackage pkg, MContext ctxt ) throws OclParserException
    {
        if( ctxt instanceof MClassifierContext )
        {
            ctxt.setIdentifier( ((MClassifierContext)ctxt).getClassName() );
        }
        else if( ctxt instanceof MOperationContext )
        {
            ctxt.setIdentifier( OclNormalization.calculateIdentifier( (MOperationContext)ctxt) );
        }
        metamodel_.getHasContexts().add(pkg,ctxt);
    }

    public MClassifierContext createClassifierContext( String selfName, String className )
    {
        MClassifierContext ctxt = metamodel_.getMClassifierContext().createMClassifierContext();
        ctxt.setSelfName(selfName);
        ctxt.setClassName(className);
        return ctxt;
    }

    public MOperationContext createOperationContext( String className, String operationName )
    {
        MOperationContext ctxt = metamodel_.getMOperationContext().createMOperationContext();
        ctxt.setClassName(className);
        ctxt.setOperationName(operationName);
        return ctxt;
    }

    public MFormalParameter createFormalParameter( String name, MTypeSpecifier type )
    {
        MFormalParameter parameter = metamodel_.getMFormalParameter().createMFormalParameter();
        parameter.setName(name);
        parameter.setType(type);
        return parameter;
    }

    public void add( MOperationContext ctxt, MFormalParameter parameter )
    {
        metamodel_.getContextParameters().add(ctxt,parameter);
    }

    public MSimpleTypeSpec createSimpleTypeSpec( String name )
    {
        MSimpleTypeSpec ts = metamodel_.getMSimpleTypeSpec().createMSimpleTypeSpec();
        ts.setName(name);
        return ts;
    }

    public MCollectionTypeSpec createCollectionTypeSpec( String kind, MSimpleTypeSpec type )
    {
        MCollectionTypeSpec ts = metamodel_.getMCollectionTypeSpec().createMCollectionTypeSpec();
        ts.setKind(kind);
        ts.setType(type);
        return ts;
    }

    public MDefinition createDefinition( String name )
    {
        MDefinition def = metamodel_.getMDefinition().createMDefinition();
        def.setName(name);
        return def;
    }

    public void add( MDefinition def, MLetStatement let )
    {
        metamodel_.getHasLetStmnt().add(def,let);
    }

    public void add( MContext ctxt, MDefinition def )
    {
        metamodel_.getHasDefinitions().add(ctxt,def);
    }

    public void add( MContext ctxt, MConstraint cons )
    {
        metamodel_.getHasConstraints().add(ctxt,cons);
    }

    public MConstraint createConstraint( String stereotype, String name,
                                         MConstraintExpression expr )
    {
        MConstraint cons = metamodel_.getMConstraint().createMConstraint();
        cons.setStereotype(stereotype);
        cons.setName(name);
        cons.setExpression(expr);
        return cons;
    }

    public MConstraintExpression createConstraintExpression()
    {
        return metamodel_.getMConstraintExpression().createMConstraintExpression();
    }

    public void add( MConstraintExpression expr, MLetStatement let )
    {
        metamodel_.getHasStatement().add(expr,let);
    }

    public MLetStatement createLetStatement( String name )
    {
        MLetStatement let = metamodel_.getMLetStatement().createMLetStatement();
        let.setName(name);
        return let;
    }

    public void add( MLetStatement let, MFormalParameter parameter )
    {
        metamodel_.getLetParameters().add(let,parameter);
    }

    protected boolean isCollDiff( MExpression expr1, String operator, MExpression expr2 ) throws OclParserException
    {
        if( !operator.equals(OclConstants.OPERATOR_MINUS) )
        {
            return false;
        }
        short res1 = OclChecker.isSet(expr1);
        short res2 = OclChecker.isSet(expr2);
        if( res1==OclChecker.YES || res2==OclChecker.YES )
        {
            if( res1==OclChecker.NO || res2==OclChecker.NO )
            {
                OclParser.throwException("parameter types mismatch");
            }
            return true;
        }
        if( res1==OclChecker.NO || res2==OclChecker.NO )
        {
            return false;
        }
        return true;
    }

    public MOperationExpression createOperationExpression( MExpression expr1, String operator,
                                                           MExpression expr2 ) throws OclParserException
    {
        if( !isCollDiff(expr1,operator,expr2) )
        {
            if( OclChecker.isLogicalOperator(operator) )
            {
                short res1 = OclChecker.isBoolean(expr1);
                short res2 = OclChecker.isBoolean(expr2);
                if( res1==OclChecker.NO || res2==OclChecker.NO )
                {
                    OclParser.throwException("parameter is not a boolean expression");
                }
            }
            else if( OclChecker.isNumericOperator(operator) )
            {
                short res1 = OclChecker.isNumeric(expr1);
                short res2 = OclChecker.isNumeric(expr2);
                if( res1==OclChecker.NO || res2==OclChecker.NO )
                {
                    OclParser.throwException("parameter is not a numeric expression");
                }
            }
        }
        MOperationExpression expression = metamodel_.getMOperationExpression().createMOperationExpression();
        expression.setLeftParameter(expr1);
        expression.setOperator(operator);
        expression.setRightParameter(expr2);
        return expression;
    }

    public MPostfixExpression createPostfixExpression( MExpression expr, MPropertyCall call,
                                                       boolean isCollection )
    {
        MPostfixExpression pfe = metamodel_.getMPostfixExpression().createMPostfixExpression();
        pfe.setExpression(expr);
        pfe.setPropertyCall(call);
        pfe.setCollection(isCollection);
        return pfe;
    }

    public MPropertyCall createPropertyCall( String name, boolean previous )
    {
        MPropertyCall call = metamodel_.getMPropertyCall().createMPropertyCall();
        call.setName(name);
        call.setPrevious(previous);
        return call;
    }

    public MActualParameters createActualParameters( MExpression expr )
    {
        MActualParameters ap = metamodel_.getMActualParameters().createMActualParameters();
        add(ap,expr);
        return ap;
    }

    public void add( MActualParameters parameters, MExpression expr )
    {
        metamodel_.getActParamExpr().add(parameters,expr);
    }

    public MPropertyCallParameters createPropertyCallParameters( MDeclarator decl, MActualParameters parameters )
    {
        MPropertyCallParameters pcp = metamodel_.getMPropertyCallParameters().createMPropertyCallParameters();
        pcp.setDeclarator(decl);
        pcp.setParameters(parameters);
        return pcp;
    }

    public MDeclarator createDeclarator( String firstName )
    {
        MDeclarator decl = metamodel_.getMDeclarator().createMDeclarator();
        add(decl,firstName);
        return decl;
    }

    public void add( MDeclarator decl, String nextName )
    {
        metamodel_.getDeclaratorNames().add(decl,makeName(nextName));
    }

    public MIfExpression createIfExpression( MExpression condition, MExpression trueExpr,
                                             MExpression falseExpr ) throws OclParserException
    {
        if( OclChecker.isBoolean(condition)==OclChecker.NO )
        {
            OclParser.throwException("condition is not a boolean expression");
        }
        MIfExpression expr = metamodel_.getMIfExpression().createMIfExpression();
        expr.setCondition(condition);
        expr.setTrueExpression(trueExpr);
        expr.setFalseExpression(falseExpr);
        return expr;
    }

    public MStringLiteral createStringLiteral( String value )
    {
        MStringLiteral literal = metamodel_.getMStringLiteral().createMStringLiteral();
        literal.setValue(value);
        literal.setOclType(createTypeString());
        return literal;
    }

    public MNumericLiteral createNumericLiteral( String text ) throws OclParserException
    {
        MNumericLiteral literal=null;
        try
        {
            if( text.indexOf('.')>=0 || text.indexOf('e')>=0 || text.indexOf('E')>=0 )
            {
                MRealLiteral real = metamodel_.getMRealLiteral().createMRealLiteral();
                real.setValue( Double.parseDouble(text) );
                literal = real;
                literal.setOclType(createTypeReal());
            }
            else
            {
                try
                {
                    MIntegerLiteral integer = metamodel_.getMIntegerLiteral().createMIntegerLiteral();
                    integer.setValue( Integer.parseInt(text) );
                    literal = integer;
                    literal.setOclType(createTypeInteger());
                }
                catch( NumberFormatException e )
                {
                    MRealLiteral real = metamodel_.getMRealLiteral().createMRealLiteral();
                    real.setValue( Double.parseDouble(text) );
                    literal = real;
                    literal.setOclType(createTypeReal());
                }
            }
            literal.setText(text);
        }
        catch(Exception e)
        {
            OclParser.throwException( e.toString() );
        }
        return literal;
    }

    public MIntegerLiteral createIntegerLiteral( int value, String text )
    {
        MIntegerLiteral integer = metamodel_.getMIntegerLiteral().createMIntegerLiteral();
        integer.setValue(value);
        integer.setText(text);
        integer.setOclType(createTypeInteger());
        return integer;
    }
    
    public MRealLiteral createRealLiteral( double value, String text )
    {
        MRealLiteral real = metamodel_.getMRealLiteral().createMRealLiteral();
        real.setValue(value);
        real.setText(text);
        real.setOclType(createTypeReal());
        return real;
    }

    public MBooleanLiteral createBooleanLiteral( boolean value )
    {
        MBooleanLiteral literal = metamodel_.getMBooleanLiteral().createMBooleanLiteral();
        literal.setValue(value);
        literal.setOclType(createTypeBoolean());
        return literal;
    }

    public MEnumLiteral createEnumLiteral( String firstPart, String secondPart )
    {
        MEnumLiteral enm = metamodel_.getMEnumLiteral().createMEnumLiteral();
        add(enm,firstPart);
        add(enm,secondPart);
        enm.setOclType(createTypeEnumeration());
        return enm;
    }

    public void add( MEnumLiteral enm, String nextPart )
    {
        metamodel_.getEnumNames().add(enm,makeName(nextPart));
    }

    protected MName makeName( String value )
    {
        MName name = metamodel_.getMName().createMName();
        name.setValue(value);
        return name;
    }

    public MCollectionLiteral createCollectionLiteral( String kind )
    {
        MCollectionLiteral literal = metamodel_.getMCollectionLiteral().createMCollectionLiteral();
        literal.setKind(kind);
        return literal;
    }

    public void add( MCollectionLiteral literal, MCollectionPart item )
    {
        metamodel_.getCollItems().add(literal,item);
    }

    public MCollectionRange createCollectionRange( MExpression lowerRange,
                                                   MExpression upperRange )
    {
        MCollectionRange range = metamodel_.getMCollectionRange().createMCollectionRange();
        range.setLowerRange(lowerRange);
        range.setUpperRange(upperRange);
        return range;
    }

    public MCollectionItem createCollectionItem( MExpression value )
    {
        MCollectionItem item = metamodel_.getMCollectionItem().createMCollectionItem();
        item.setExpression(value);
        return item;
    }

    public void set( MConstraintExpression ce, MExpression expr ) throws OclParserException
    {
        if( OclChecker.isBoolean(expr)==OclChecker.NO )
        {
            OclParser.throwException("constraint is not a boolean expression");
        }
        ce.setExpression(expr);
    }


    public OclVoid createTypeVoid()
    {
        OclVoid type = metamodel_.getOclVoid().createOclVoid();
        type.setName(OclConstants.TYPE_NAME_VOID);
        return type;
    }

    public OclUser createTypeUser( String name )
    {
        OclUser type = metamodel_.getOclUser().createOclUser();
        type.setName(name);
        return type;
    }

    public OclBoolean createTypeBoolean()
    {
        OclBoolean type = metamodel_.getOclBoolean().createOclBoolean();
        type.setName(OclConstants.TYPE_NAME_BOOLEAN);
        return type;
    }

    public OclReal createTypeReal()
    {
        OclReal type = metamodel_.getOclReal().createOclReal();
        type.setName(OclConstants.TYPE_NAME_REAL);
        return type;
    }

    public OclInteger createTypeInteger()
    {
        OclInteger type = metamodel_.getOclInteger().createOclInteger();
        type.setName(OclConstants.TYPE_NAME_INTEGER);
        return type;
    }

    public OclString createTypeString()
    {
        OclString type = metamodel_.getOclString().createOclString();
        type.setName(OclConstants.TYPE_NAME_STRING);
        return type;
    }

    public OclEnumeration createTypeEnumeration()
    {
        OclEnumeration type = metamodel_.getOclEnumeration().createOclEnumeration();
        type.setName(OclConstants.TYPE_NAME_ENUMERATION);
        return type;
    }

    public OclSet createTypeSet( OclType type )
    {
        String name = type==null ? "?" : type.getName();
        OclSet collection = metamodel_.getOclSet().createOclSet();
        collection.setName( OclConstants.COLLECTIONKIND_SET+"("+name+")" );
        collection.setType(type);
        return collection;
    }

    public OclBag createTypeBag( OclType type )
    {
        String name = type==null ? "?" : type.getName();
        OclBag collection = metamodel_.getOclBag().createOclBag();
        collection.setName( OclConstants.COLLECTIONKIND_BAG+"("+name+")" );
        collection.setType(type);
        return collection;
    }

    public OclSequence createTypeSequence( OclType type )
    {
        String name = type==null ? "?" : type.getName();
        OclSequence collection = metamodel_.getOclSequence().createOclSequence();
        collection.setName( OclConstants.COLLECTIONKIND_SEQUENCE+"("+name+")" );
        collection.setType(type);
        return collection;
    }

    public OclCollection createTypeCollection( OclType type )
    {
        String name = type==null ? "?" : type.getName();
        OclCollection collection = metamodel_.getOclCollection().createOclCollection();
        collection.setName( OclConstants.COLLECTIONKIND_COLLECTION+"("+name+")" );
        collection.setType(type);
        return collection;
    }
}
