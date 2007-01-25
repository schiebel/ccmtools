/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlModel.java,v 1.2 2005/02/21 10:58:27 rlechner Exp $
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


/**
UML model. <br>Children:
<ul>
<li>{@link UmlModelElementName}</li>
<li>{@link UmlNamespaceElement}</li>
</ul>

@author Robert Lechner (rlechner@gmx.at)
@version $Date: 2005/02/21 10:58:27 $
*/
class UmlModel extends uml_parser.uml.MModel implements Worker
{
    private String id_;
    private Vector myWorkers_ = new Vector();


    UmlModel( Attributes attrs )
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
            if( name_==null )
            {
                name_ = "";
            }
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
	            myWorkers_.add(obj);
	        }
	        Main.logChild("UmlModel",obj);
	    }
	}


	public void makeConnections( Main main, Worker parent )
	{
	    int s = myWorkers_.size();
	    for( int index=0; index<s; index++ )
	    {
	        ((Worker)myWorkers_.get(index)).makeConnections(main, parent);
	    }
	}


	public String getIdlCode( Main main, String prefix, StringBuffer includeStatements )
	{
	    StringBuffer code = new StringBuffer();
	    code.append("\n");
	    code.append(prefix);
	    code.append("/*****   begin model '");
	    code.append(getName());
	    code.append("'   *****/\n\n\n");
	    int s = myWorkers_.size();
	    for( int index=0; index<s; index++ )
	    {
	        code.append( ((Worker)myWorkers_.get(index)).getIdlCode(main, prefix, includeStatements) );
	    }
	    code.append("\n");
	    code.append(prefix);
	    code.append("/*****   end model '");
	    code.append(getName());
	    code.append("'   *****/\n\n\n");
	    return code.toString();
	}


    public String getOclCode( Main main )
    {
	    StringBuffer code = new StringBuffer();
	    code.append("--  begin model '");
	    code.append(getName());
	    code.append("'\n\n");
	    int s = myWorkers_.size();
	    for( int index=0; index<s; index++ )
	    {
	        code.append( ((Worker)myWorkers_.get(index)).getOclCode(main) );
	    }
	    code.append("--  end model '");
	    code.append(getName());
	    code.append("'\n\n");
	    return code.toString();
    }


    private int dependencyNumber_=-1;

	public int createDependencyOrder( int number, Main main )
	{
	    if( dependencyNumber_>0 )
	    {
	        return number;
	    }
	    int index, s=myWorkers_.size();
	    Vector children = new Vector();
	    for( index=0; index<s; index++ )
	    {
	        Object o = myWorkers_.get(index);
	        if( (o instanceof UmlNamespaceElement) && ((UmlNamespaceElement)o).hasException(main) )
	        {
	            number = ((UmlNamespaceElement)o).createDependencyOrder(number, main);
	        }
	        else
	        {
	            children.add(o);
	        }
	    }
	    s = children.size();
	    for( index=0; index<s; index++ )
	    {
	        number = ((Worker)children.get(index)).createDependencyOrder(number, main);
	    }
	    Main.sort(myWorkers_);
	    dependencyNumber_ = number;
	    return number+1;
	}

	public int getDependencyNumber()
	{
	    return dependencyNumber_;
	}
}
