/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlConstraint.java,v 1.2 2005/02/21 10:58:31 rlechner Exp $
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

package uml2idl;

import org.xml.sax.Attributes;
import java.util.Vector;
import uml_parser.uml.MConstraint_body;
import uml_parser.uml.MBooleanExpression;
import uml_parser.uml.MExpression_body;


/**
Constraints. <br>Children:
<ul>
<li>{@link UmlModelElementName}</li>
<li>{@link uml_parser.uml.MConstraint_body}</li>
<li>{@link uml_parser.uml.MModelElement_comment}</li>
</ul>

@author Robert Lechner (rlechner@gmx.at)
@version January 2004
*/
class UmlConstraint extends uml_parser.uml.MConstraint implements Worker
{
    private String id_;
    private Worker idlParent_;


    UmlConstraint( Attributes attrs )
    {
        super(attrs);
        id_ = xmi_id_;
        if( id_==null )
        {
            id_ = Main.makeId();
        }
    }


    public String getId()
    {
        return id_;
    }


	public String getName()
	{
        if( name_==null )
        {
            name_ = Main.makeModelElementName(this);
        }
	    return name_;
	}


	public void collectWorkers( java.util.HashMap map )
	{
	    map.put(id_, this);
	    int s = size();
	    for( int index=0; index<s; index++ )
	    {
	        Object obj = get(index);
	        if( obj instanceof Worker )
	        {
	            ((Worker)obj).collectWorkers(map);
	        }
	        Main.logChild("UmlConstraint",obj);
	    }
	}


	public void makeConnections( Main main, Worker parent )
	{
	    idlParent_ = parent;
	    int s = size();
	    for( int index=0; index<s; index++ )
	    {
	        Object obj = get(index);
	        if( obj instanceof Worker )
	        {
	            ((Worker)obj).makeConnections(main, this);
	        }
	    }
	}


    public String getIdlCode( Main main, String prefix, StringBuffer includeStatements )
    {
        return "";
    }


	public int createDependencyOrder( int number, Main main )
	{
	    return number;
	}

	public int getDependencyNumber()
	{
	    return 0;
	}


    public String getOclCode( Main main )
    {
        return getOclCode(main, idlParent_);
    }
    
    boolean code_written_;
    
    private String getOclCode( Main main, Worker parent )
    {
        if( xmi_idref_!=null )
        {
            UmlConstraint c = (UmlConstraint)main.workers_.get(xmi_idref_);
    	    if( c==null )
    	    {
    	        System.err.println("ERROR: UmlConstraint.getOclCode: xmi_idref_="+xmi_idref_);
    	        return "";
    	    }
    	    return c.getOclCode(main, parent);
        }
        Vector v1 = findChildren(MConstraint_body.xmlName__);
        if( v1.size()<1 )
        {
            return "";
        }
        MConstraint_body body = (MConstraint_body)v1.get(0);
        v1 = body.findChildren(MBooleanExpression.xmlName__);
        if( v1.size()<1 )
        {
            return "";
        }
        MBooleanExpression expr = (MBooleanExpression)v1.get(0);
        if( expr.body_==null )
        {
            v1 = expr.findChildren(MExpression_body.xmlName__);
            if( v1.size()<1 )
            {
                return "";
            }
            MExpression_body eb = (MExpression_body)v1.get(0);
            StringBuffer code = new StringBuffer();
            int s = eb.size();
            for( int i=0; i<s; i++ )
            {
                code.append( eb.get(i).toString() );
            }
            expr.body_ = code.toString();
        }
        expr.body_ = expr.body_.trim();
        if( expr.body_.startsWith("package") )
        {
            if( code_written_ )
            {
                return "";
            }
            code_written_ = true;
            return expr.body_+"\n\n";
        }
        if( parent==null )
        {
            return "";
        }
        if( parent instanceof UmlClass )
        {
            return ((UmlClass)parent).getOclCode(main, expr.body_);
        }
        if( parent instanceof UmlOperation )
        {
            return ((UmlOperation)parent).getOclCode(main, expr.body_);
        }
        return "-- ERROR: WRONG_PARENT: "+expr.body_.replace('\n', ' ').replace('\r', ' ')+"\n\n";
    }
}
