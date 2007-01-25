/* CCM Tools : OCL parser
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003, 2004 Salomon Automation
 *
 * $Id: OclParsetreeCreator.java,v 1.1 2004/07/20 16:21:23 teiniker Exp $
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

import oclmetamodel.*;


/**
 * The interface for creating the parsetree of an OCL file.
 *
 * @author Robert Lechner
 * @version $Revision: 1.1 $
 */
public interface OclParsetreeCreator
{
    public MFile createFile() throws OclParserException;
    public MPackage createPackage( String name ) throws OclParserException;
    public void add( MFile file, MPackage pkg ) throws OclParserException;
    public void add( MPackage pkg, MContext ctxt ) throws OclParserException;
    public MClassifierContext createClassifierContext( String selfName, String className ) throws OclParserException;
    public MOperationContext createOperationContext( String className, String operationName ) throws OclParserException;
    public MFormalParameter createFormalParameter( String name, MTypeSpecifier type ) throws OclParserException;
    public void add( MOperationContext ctxt, MFormalParameter parameter ) throws OclParserException;
    public MSimpleTypeSpec createSimpleTypeSpec( String name ) throws OclParserException;
    public MCollectionTypeSpec createCollectionTypeSpec( String kind, MSimpleTypeSpec type ) throws OclParserException;
    public MDefinition createDefinition( String name ) throws OclParserException;
    public void add( MDefinition def, MLetStatement let ) throws OclParserException;
    public void add( MContext ctxt, MDefinition def ) throws OclParserException;
    public void add( MContext ctxt, MConstraint cons ) throws OclParserException;
    public MConstraint createConstraint( String stereotype, String name, MConstraintExpression expr ) throws OclParserException;
    public MConstraintExpression createConstraintExpression() throws OclParserException;
    public void add( MConstraintExpression expr, MLetStatement let ) throws OclParserException;
    public void set( MConstraintExpression ce, MExpression expr ) throws OclParserException;
    public MLetStatement createLetStatement( String name ) throws OclParserException;
    public void add( MLetStatement let, MFormalParameter parameter ) throws OclParserException;
    public MOperationExpression createOperationExpression( MExpression expr1, String operator, MExpression expr2 ) throws OclParserException;
    public MPostfixExpression createPostfixExpression( MExpression expr, MPropertyCall call, boolean isCollection ) throws OclParserException;
    public MPropertyCall createPropertyCall( String name, boolean previous ) throws OclParserException;
    public MActualParameters createActualParameters( MExpression expr ) throws OclParserException;
    public void add( MActualParameters parameters, MExpression expr ) throws OclParserException;
    public MPropertyCallParameters createPropertyCallParameters( MDeclarator decl, MActualParameters parameters ) throws OclParserException;
    public MDeclarator createDeclarator( String firstName ) throws OclParserException;
    public void add( MDeclarator decl, String nextName ) throws OclParserException;
    public MIfExpression createIfExpression( MExpression condition, MExpression trueExpr, MExpression falseExpr ) throws OclParserException;
    public MStringLiteral createStringLiteral( String value ) throws OclParserException;
    public MNumericLiteral createNumericLiteral( String value ) throws OclParserException;
    public MBooleanLiteral createBooleanLiteral( boolean value ) throws OclParserException;
    public MEnumLiteral createEnumLiteral( String firstPart, String secondPart ) throws OclParserException;
    public void add( MEnumLiteral enm, String nextPart ) throws OclParserException;
    public MCollectionLiteral createCollectionLiteral( String kind ) throws OclParserException;
    public void add( MCollectionLiteral literal, MCollectionPart item ) throws OclParserException;
    public MCollectionRange createCollectionRange( MExpression lowerRange, MExpression upperRange ) throws OclParserException;
    public MCollectionItem createCollectionItem( MExpression value ) throws OclParserException;
    public MIntegerLiteral createIntegerLiteral( int value, String text ) throws OclParserException;
    public MRealLiteral createRealLiteral( double value, String text ) throws OclParserException;
}
