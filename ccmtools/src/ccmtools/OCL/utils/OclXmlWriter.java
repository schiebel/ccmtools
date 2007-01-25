/* CCM Tools : OCL helpers
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003, 2004 Salomon Automation
 *
 * $Id: OclXmlWriter.java,v 1.1 2004/07/20 16:21:23 teiniker Exp $
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

import java.util.Iterator;


/**
 * Converts the OCL model to XML. This is useful for debugging.
 *
 * @author Robert Lechner
 */
public class OclXmlWriter
{
    protected java.io.Writer writer_;

    public OclXmlWriter( java.io.Writer writer )
    {
        writer_ = writer;
    }

    public void close() throws java.io.IOException
    {
        writer_.close();
        writer_ = null;
    }

    protected void write( String text )
    {
        try
        {
            writer_.write(text);
        }
        catch( java.io.IOException e )
        {
            System.err.println(e);
        }
    }

    protected void writeStart( String className, String attrName, String attrValue )
    {
        write( "<"+className+" "+attrName+"=\""+attrValue+"\">" );
    }

    protected void writeTag( String className, String attrName, String attrValue )
    {
        write( "<"+className+" "+attrName+"=\""+attrValue+"\" />" );
    }


    public void write( MFile file )
    {
        write("<MFile>");
        Iterator it = file.getPackages().iterator();
        while( it.hasNext() )
        {
            write( (MPackage)it.next() );
        }
        write("</MFile>");
    }


    public void write( MPackage pkg )
    {
        writeStart( "MPackage", "name", pkg.getName() );
        Iterator it = pkg.getContexts().iterator();
        while( it.hasNext() )
        {
            write( (MContext)it.next() );
        }
        write("</MPackage>");
    }


    public void write( MContext ctxt )
    {
        String endTag;
        if( ctxt instanceof MClassifierContext )
        {
            endTag = writeContext( (MClassifierContext)ctxt );
        }
        else if( ctxt instanceof MOperationContext )
        {
            endTag = writeContext( (MOperationContext)ctxt );
        }
        else
        {
            write("<unknown:MContext>");
            endTag = "</unknown:MContext>";
        }
        Iterator it = ctxt.getDefinitions().iterator();
        while( it.hasNext() )
        {
            write( (MDefinition)it.next() );
        }
        it = ctxt.getConstraints().iterator();
        while( it.hasNext() )
        {
            write( (MConstraint)it.next() );
        }
        write(endTag);
    }

    protected String writeContext( MClassifierContext ctxt )
    {
        writeStart( "MClassifierContext", "ID", ctxt.getIdentifier() );
        String selfName = ctxt.getSelfName();
        if( !selfName.equals(OclConstants.KEYWORD_SELF) )
        {
            writeTag( "SELF", "value", selfName );
        }
        return "</MClassifierContext>";
    }

    protected String writeContext( MOperationContext ctxt )
    {
        writeStart( "MOperationContext", "ID", ctxt.getIdentifier() );
        Iterator it = ctxt.getParameters().iterator();
        while( it.hasNext() )
        {
            write( (MFormalParameter)it.next() );
        }
        MTypeSpecifier ts = ctxt.getReturnType();
        if( ts!=null )
        {
            write( ts );
        }
        return "</MOperationContext>";
    }


    public void write( MTypeSpecifier ts )
    {
        if( ts instanceof MSimpleTypeSpec )
        {
            write( (MSimpleTypeSpec)ts );
        }
        else if( ts instanceof MCollectionTypeSpec )
        {
            write( (MCollectionTypeSpec)ts );
        }
        else
        {
            write("<unknown:MTypeSpecifier />");
        }
    }


    public void write( MSimpleTypeSpec ts )
    {
        writeTag( "MSimpleTypeSpec", "name", ts.getName() );
    }


    public void write( MCollectionTypeSpec ts )
    {
        writeStart( "MCollectionTypeSpec", "kind", ts.getKind() );
        write( ts.getType() );
        write("</MCollectionTypeSpec>");
    }


    public void write( MDefinition def )
    {
        String name = def.getName();
        writeStart( "MDefinition", "name", name==null ? "" : name );
        Iterator it = def.getStatements().iterator();
        while( it.hasNext() )
        {
            write( (MLetStatement)it.next() );
        }
        write("</MDefinition>");
    }


    public void write( MConstraint con )
    {
        String name = con.getName();
        if( name==null )
        {
            write("<MConstraint stereotype=\""+con.getStereotype()+"\">");
        }
        else
        {
            write("<MConstraint stereotype=\""+con.getStereotype()+"\" name=\""+name+"\">");
        }
        write( (MConstraintExpression)con.getExpression() );
        write("</MConstraint>");
    }


    public void write( MFormalParameter param )
    {
        writeStart( "MFormalParameter", "name", param.getName() );
        write( param.getType() );
        write("</MFormalParameter>");
    }


    public void write( MLetStatement let )
    {
        writeStart( "MLetStatement", "name", let.getName() );
        Iterator it = let.getParameters().iterator();
        while( it.hasNext() )
        {
            write( (MFormalParameter)it.next() );
        }
        MTypeSpecifier ts = let.getType();
        if( ts!=null )
        {
            write( ts );
        }
        write( let.getExpression() );
        write("</MLetStatement>");
    }


    public void write( MConstraintExpression expr )
    {
        write("<MConstraintExpression>");
        Iterator it = expr.getStatements().iterator();
        while( it.hasNext() )
        {
            write( (MLetStatement)it.next() );
        }
        write( expr.getExpression() );
        write("</MConstraintExpression>");
    }


    public void write( MExpression expr )
    {
        OclType type = expr.getOclType();
        if( type!=null )
        {
            write("<MExpression type=\""+type.getName()+"\" >");
        }
        if( expr instanceof MPropertyCall )
        {
            write( (MPropertyCall)expr );
        }
        else if( expr instanceof MPostfixExpression )
        {
            write( (MPostfixExpression)expr );
        }
        else if( expr instanceof MOperationExpression )
        {
            write( (MOperationExpression)expr );
        }
        else if( expr instanceof MLiteralExpression )
        {
            write( (MLiteralExpression)expr );
        }
        else if( expr instanceof MIfExpression )
        {
            write( (MIfExpression)expr );
        }
        else
        {
            write("<unknown:MExpression />");
        }
        if( type!=null )
        {
            write("</MExpression>");
        }
    }


    public void write( MPropertyCall call )
    {
        writeStart( "MPropertyCall", "name", call.getName() );
        if( call.isPrevious() )
        {
            write("<PREVIOUS />");
        }
        MPropertyCallParameters pcp = call.getCallParameters();
        if( pcp!=null )
        {
            write( pcp );
        }
        MActualParameters ap = call.getQualifiers();
        if( ap!=null )
        {
            write( ap );
        }
        write("</MPropertyCall>");
    }


    public void write( MPropertyCallParameters param )
    {
        write("<MPropertyCallParameters>");
        MDeclarator dec = param.getDeclarator();
        if( dec!=null )
        {
            write( dec );
        }
        MActualParameters ap = param.getParameters();
        if( ap!=null )
        {
            write( ap );
        }
        write("</MPropertyCallParameters>");
    }


    public void write( MDeclarator dec )
    {
        write("<MDeclarator>");
        Iterator it = dec.getNames().iterator();
        while( it.hasNext() )
        {
            write( (MName)it.next() );
        }
        MSimpleTypeSpec sts = dec.getSimpleType();
        if( sts!=null )
        {
            write( sts );
        }
        String optName = dec.getOptName();
        if( optName!=null )
        {
            write("<optName value=\""+optName+"\" />");
        }
        MTypeSpecifier ts = dec.getOptType();
        if( ts!=null )
        {
            write( ts );
        }
        MExpression optExpr = dec.getOptExpression();
        if( optExpr!=null )
        {
            write( optExpr );
        }
        write("</MDeclarator>");
    }


    public void write( MName name )
    {
        write("<MName value=\""+name.getValue()+"\" />");
    }


    public void write( MActualParameters param )
    {
        write("<MActualParameters>");
        Iterator it = param.getExpressions().iterator();
        while( it.hasNext() )
        {
            write( (MExpression)it.next() );
        }
        write("</MActualParameters>");
    }


    public void write( MPostfixExpression pfe )
    {
        writeStart( "MPostfixExpression", "kind", pfe.isCollection() ? "collection" : "item" );
        write( pfe.getExpression() );
        write( pfe.getPropertyCall() );
        write("</MPostfixExpression>");
    }


    public void write( MOperationExpression op )
    {
        writeStart( "MOperationExpression", "operator", op.getOperator() );
        MExpression expr = op.getLeftParameter();
        if( expr!=null )
        {
            write( expr );
        }
        expr = op.getRightParameter();
        if( expr!=null )
        {
            write( expr );
        }
        write("</MOperationExpression>");
    }


    public void write( MIfExpression expr )
    {
        write("<MIfExpression>");
        write( expr.getCondition() );
        write( expr.getTrueExpression() );
        write( expr.getFalseExpression() );
        write("</MIfExpression>");
    }


    public void write( MLiteralExpression expr )
    {
        if( expr instanceof MCollectionLiteral )
        {
            write( (MCollectionLiteral)expr );
        }
        else if( expr instanceof MStringLiteral )
        {
            write( (MStringLiteral)expr );
        }
        else if( expr instanceof MNumericLiteral )
        {
            write( (MNumericLiteral)expr );
        }
        else if( expr instanceof MBooleanLiteral )
        {
            write( (MBooleanLiteral)expr );
        }
        else if( expr instanceof MEnumLiteral )
        {
            write( (MEnumLiteral)expr );
        }
        else
        {
            write("<unknown:MLiteralExpression />");
        }
    }


    public void write( MCollectionLiteral literal )
    {
        writeStart( "MCollectionLiteral", "kind", literal.getKind() );
        Iterator it = literal.getItems().iterator();
        while( it.hasNext() )
        {
            write( (MCollectionPart)it.next() );
        }
        write("</MCollectionLiteral>");
    }


    public void write( MCollectionPart part )
    {
        if( part instanceof MCollectionItem )
        {
            write( (MCollectionItem)part );
        }
        else if( part instanceof MCollectionRange )
        {
            write( (MCollectionRange)part );
        }
        else
        {
            write("<unknown:MCollectionPart />");
        }
    }


    public void write( MCollectionItem item )
    {
        write("<MCollectionItem>");
        write( item.getExpression() );
        write("</MCollectionItem>");
    }


    public void write( MCollectionRange range )
    {
        write("<MCollectionRange>");
        write( range.getLowerRange() );
        write( range.getUpperRange() );
        write("</MCollectionRange>");
    }


    public void write( MStringLiteral literal )
    {
        writeTag( "MStringLiteral", "value", literal.getValue() );
    }


    public void write( MBooleanLiteral literal )
    {
        writeTag( "MBooleanLiteral", "value", literal.isValue() ? "true" : "false" );
    }


    public void write( MEnumLiteral literal )
    {
        write("<MEnumLiteral>");
        Iterator it = literal.getNames().iterator();
        while( it.hasNext() )
        {
            write( (MName)it.next() );
        }
        write("</MEnumLiteral>");
    }


    public void write( MNumericLiteral literal )
    {
        if( literal instanceof MRealLiteral )
        {
            write( (MRealLiteral)literal );
        }
        else if( literal instanceof MIntegerLiteral )
        {
            write( (MIntegerLiteral)literal );
        }
        else
        {
            write("<unknown:MNumericLiteral />");
        }
    }


    public void write( MRealLiteral literal )
    {
        String text = Double.toString( literal.getValue() );
        write("<MRealLiteral value=\""+text+"\" text=\""+literal.getText()+"\" />");
    }


    public void write( MIntegerLiteral literal )
    {
        String text = Integer.toString( literal.getValue() );
        write("<MIntegerLiteral value=\""+text+"\" text=\""+literal.getText()+"\" />");
    }
}
