/* CCM Tools : OCL helpers
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003, 2004 Salomon Automation
 *
 * $Id: OclTextWriter.java,v 1.1 2004/07/20 16:21:23 teiniker Exp $
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
 * Converts OCL expressions to text.
 *
 * @author Robert Lechner
 */
public class OclTextWriter
{
    protected java.io.Writer writer_;

    /**
     * Creates an OCL-text converter.
     *
     * @param writer  output
     */
    public OclTextWriter( java.io.Writer writer )
    {
        writer_ = writer;
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

    public void close() throws java.io.IOException
    {
        writer_.close();
        writer_ = null;
    }
    
    public void write( MExpression expr, boolean bePretty )
    {
        write(expr, 0, bePretty);
    }
    
    private void write( MExpression expr, int level, boolean bePretty )
    {
        if( expr instanceof MPropertyCall )
        {
            write( (MPropertyCall)expr, level, bePretty );
        }
        else if( expr instanceof MPostfixExpression )
        {
            write( (MPostfixExpression)expr, level, bePretty );
        }
        else if( expr instanceof MOperationExpression )
        {
            write( (MOperationExpression)expr, level, bePretty );
        }
        else if( expr instanceof MLiteralExpression )
        {
            write( (MLiteralExpression)expr, level, bePretty );
        }
        else if( expr instanceof MIfExpression )
        {
            write( (MIfExpression)expr, level, bePretty );
        }
        else
        {
            write("\"unknown expression\"");
        }
    }

    private void write( MPropertyCall call, int level, boolean bePretty )
    {
        if( level>7 )  write("(");
        //
        write( call.getName() );
        if( call.isPrevious() )  write("@pre");
        MActualParameters ap = call.getQualifiers();
        if( ap!=null )
        {
            write("[");
            write(ap);
            write("]");
        }
        MPropertyCallParameters pcp = call.getCallParameters();
        if( pcp!=null )
        {
            write("(");
            MDeclarator dec = pcp.getDeclarator();
            if( dec!=null )
            {
                Iterator it1 = dec.getNames().iterator();
                write( ((MName)it1.next()).getValue() );
                while( it1.hasNext() )
                {
                    write( "," + ((MName)it1.next()).getValue() );
                }
                MSimpleTypeSpec sts = dec.getSimpleType();
                if( sts!=null )
                {
                    write( ":"+sts.getName() );
                }
                String optName = dec.getOptName();
                MTypeSpecifier optTS = dec.getOptType();
                MExpression optExpr = dec.getOptExpression();
                if( optName!=null && optTS!=null && optExpr!=null )
                {
                    write(";"+optName+":");
                    write(optTS);
                    write("=");
                    write(optExpr,0,false);
                }
                write("|");
            }
            MActualParameters ap2 = pcp.getParameters();
            if( ap2!=null )
            {
                write(ap2);
            }
            write(")");
        }
        //
        if( level>7 )  write(")");
    }
    
    private void write( MActualParameters ap )
    {
        Iterator it = ap.getExpressions().iterator();
        write( (MExpression)it.next(), 0, false );
        while( it.hasNext() )
        {
            write(",");
            write( (MExpression)it.next(), 0, false );
        }
    }
    
    private void write( MTypeSpecifier ts )
    {
        if( ts instanceof MSimpleTypeSpec )
        {
            write( ((MSimpleTypeSpec)ts).getName() );
        }
        else if( ts instanceof MCollectionTypeSpec )
        {
            MCollectionTypeSpec cts = (MCollectionTypeSpec)ts;
            write( cts.getKind()+"("+cts.getType().getName()+")" );
        }
        else
        {
            write("\"unknown type specifier\"");
        }
    }
    
    private void write( MPostfixExpression pfe, int level, boolean bePretty )
    {
        if( level>6 )  write("(");
        //
        write( pfe.getExpression(), 6, false );
        write( pfe.isCollection() ? "->" : "." );
        write( pfe.getPropertyCall(), 6, false );
        //
        if( level>6 )  write(")");
    }
    
    private static final String[] operators_ = {
        OclConstants.OPERATOR_AND, OclConstants.OPERATOR_OR,OclConstants.OPERATOR_XOR,
        OclConstants.OPERATOR_IMPLIES, OclConstants.OPERATOR_EQUAL, OclConstants.OPERATOR_NEQUAL,
        OclConstants.OPERATOR_LT, OclConstants.OPERATOR_LE, OclConstants.OPERATOR_GT, OclConstants.OPERATOR_GE,
        OclConstants.OPERATOR_PLUS, OclConstants.OPERATOR_MINUS, OclConstants.OPERATOR_MULT,
        OclConstants.OPERATOR_DIVIDE, OclConstants.OPERATOR_NOT };
        
    private static final int[] levels_ = {1,1,1,1, 2,2,2,2,2,2, 3,3, 4,4, 5};
    
    private static final String[] code_ = {" and ", " or ", " xor ", " implies ", "=", "<>", "<", "<=", ">", ">=",
        "+", "-", "*", "/", "not "};
    
    private void write( MOperationExpression oe, int level, boolean bePretty )
    {
        MExpression expr1 = oe.getLeftParameter();
        MExpression expr2 = oe.getRightParameter();
        String oclOp = oe.getOperator();
        String cppOp = oclOp;
        int opLevel=0;
        if( oclOp.equals(OclConstants.OPERATOR_MINUS) && expr1==null )
        {
            cppOp = "-";
            opLevel = 5;
        }
        else
        {
            for( int index=0; index<operators_.length; index++ )
            {
                if( oclOp.equals(operators_[index]) )
                {
                    opLevel = levels_[index];
                    cppOp = code_[index];
                    break;
                }
            }
        }
        if( level>opLevel )
        {
            write("(");
            if( expr1!=null )  write(expr1,opLevel,false);
            write(cppOp);
            if( expr2!=null )  write(expr2,opLevel,false);
            write(")");
        }
        else if( bePretty && (oclOp.equals(OclConstants.OPERATOR_AND)||oclOp.equals(OclConstants.OPERATOR_OR)) )
        {
            print( expr1, opLevel, oclOp );
            write("\n");
            write(cppOp);
            write("\n");
            print( expr2, opLevel, oclOp );
        }
        else
        {
            if( expr1!=null )  write(expr1,opLevel,false);
            write(cppOp);
            if( expr2!=null )  write(expr2,opLevel,false);
        }
    }
    
    private void print( MExpression expr, int level, String operator )
    {
        if( expr instanceof MOperationExpression )
        {
            if( ((MOperationExpression)expr).getOperator().equals(operator) )
            {
                write(expr,level,true);
                return;
            }
        }
        write("  ");
        write(expr,level,false);
    }
    
    private void write( MIfExpression ife, int level, boolean bePretty )
    {
        if( bePretty )
        {
            write(" if\n  ");
            write( ife.getCondition(), 0, false );
            write("\n then\n  ");
            write( ife.getTrueExpression(), 0, false );
            write("\n else\n  ");
            write( ife.getFalseExpression(), 0, false );
            write("\n endif");
        }
        else
        {
            if( level>7 )  write("(");
            write("if ");
            write( ife.getCondition(), 0, false );
            write(" then ");
            write( ife.getTrueExpression(), 0, false );
            write(" else ");
            write( ife.getFalseExpression(), 0, false );
            write(" endif");
            if( level>7 )  write(")");
        }
    }
    
    private void write( MLiteralExpression expr, int level, boolean bePretty )
    {
        if( level>7 )  write("(");
        if( expr instanceof MCollectionLiteral )
        {
            MCollectionLiteral cl = (MCollectionLiteral)expr;
            write(cl.getKind()+"{");
            Iterator it1 = cl.getItems().iterator();
            if( it1.hasNext() )
            {
                write((MCollectionPart)it1.next());
                while( it1.hasNext() )
                {
                    write(",");
                    write((MCollectionPart)it1.next());
                }
            }
            write("}");
        }
        else if( expr instanceof MStringLiteral )
        {
            write("'"+ ((MStringLiteral)expr).getValue() +"'");
        }
        else if( expr instanceof MNumericLiteral )
        {
            if( expr instanceof MIntegerLiteral )
            {
                write(Integer.toString( ((MIntegerLiteral)expr).getValue() ));
            }
            else
            {
                String value = ((MNumericLiteral)expr).getText();
                if( level==0 )
                {
                    write(value);
                }
                else
                {
                    write("("+value+")");
                }
            }
        }
        else if( expr instanceof MBooleanLiteral )
        {
            write( ((MBooleanLiteral)expr).isValue() ? "true" : "false" );
        }
        else if( expr instanceof MEnumLiteral )
        {
            Iterator it2 = ((MEnumLiteral)expr).getNames().iterator();
            write( ((MName)it2.next()).getValue() );
            while( it2.hasNext() )
            {
                write("::");
                write( ((MName)it2.next()).getValue() );
            }
        }
        else
        {
            write("<unknown:MLiteralExpression />");
        }
        if( level>7 )  write(")");
    }
    
    private void write( MCollectionPart part )
    {
        if( part instanceof MCollectionItem )
        {
            write( ((MCollectionItem)part).getExpression(), 0, false );
        }
        else if( part instanceof MCollectionRange )
        {
            MCollectionRange range = (MCollectionRange)part;
            write( range.getLowerRange(), 0, false );
            write("..");
            write( range.getUpperRange(), 0, false );
        }
        else
        {
            write("\"unknown collection part\"");
        }
    }
}
