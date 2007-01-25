/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlExpression.java,v 1.2 2005/02/21 10:58:29 rlechner Exp $
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

import java.util.Vector;
import org.xml.sax.Attributes;
import uml_parser.uml.MExpression_body;


/**
An expression. <br>Children:
<ul>
<li>{@link uml_parser.uml.MExpression_body}</li>
</ul>

@author Robert Lechner (rlechner@gmx.at)
@version January 2004
*/
class UmlExpression extends uml_parser.uml.MExpression implements Worker
{
    private String id_;


    UmlExpression( Attributes attrs )
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
		return null;
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
	        Main.logChild("UmlExpression",obj);
	    }
    }


	public void makeConnections( Main main, Worker parent )
	{
	    // nothing to do
	}


    public String getIdlCode( Main main, String prefix, StringBuffer includeStatements )
    {
    	return "";
    }


    public String getOclCode( Main main )
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
	
	
	public String getValue()
	{
	    if( body_==null )
	    {
	        Vector v = findChildren(MExpression_body.xmlName__);
	        if( v.size()>0 )
	        {
	            MExpression_body b = (MExpression_body)v.get(0);
                StringBuffer text = new StringBuffer();
                int s = b.size();
                for( int i=0; i<s; i++ )
                {
                    text.append(b.get(i).toString());
                }
	            body_ = text.toString();
	        }
	    }
	    return body_;
	}
}
