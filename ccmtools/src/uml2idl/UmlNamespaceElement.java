/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlNamespaceElement.java,v 1.2 2005/02/21 10:58:26 rlechner Exp $
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
Container for children of {@link UmlModel} and {@link UmlPackage}. <br>Children:
<ul>
<li>{@link UmlPackage}</li>
<li>{@link UmlClass}</li>
<li>{@link UmlDataType}</li>
<li>{@link UmlConstraint}</li>
<li>{@link UmlTagDefinition}</li>
<li>{@link UmlStereotype}</li>
<li>{@link UmlAssociation}</li>
<li>{@link UmlDependency}</li>
<li>{@link UmlGeneralization}</li>
</ul>

@author Robert Lechner (robert.lechner@salomon.at)
@version $Date: 2005/02/21 10:58:26 $
*/
class UmlNamespaceElement extends uml_parser.uml.MNamespace_ownedElement implements Worker
{
    private String id_;
    private Vector myWorkers_ = new Vector();


    UmlNamespaceElement( Attributes attrs )
    {
        super(attrs);
        id_ = Main.makeId();
    }


    public String getId()
    {
        return id_;
    }


	public String getName()
	{
	    return null;
	}


    /**
     * Puts all children of type 'Worker' into the map.
     * The key value is the unique identifier.
     */
	public void collectWorkers( java.util.HashMap map )
	{
	    int s = size();
	    for( int index=0; index<s; index++ )
	    {
	        Object obj = get(index);
	        if( obj instanceof Worker )
	        {
	            ((Worker)obj).collectWorkers(map);
	            myWorkers_.add(obj);
	        }
	        Main.logChild("UmlNamespaceElement",obj);
	    }
	}


    public void makeConnections( Main main, Worker parent )
    {
        int s = myWorkers_.size();
        for( int index=0; index<s; index++ )
        {
            Object obj = myWorkers_.get(index);
            if( obj instanceof UmlGeneralization )
            {
                UmlGeneralization gen = (UmlGeneralization)obj;
                Object child = gen.getChild(main);
                if( child!=null )
                {
                    if( child instanceof UmlClass )
                    {
                        ((UmlClass)child).addGeneralization(gen);
                    }
                    else
                    {
                        System.err.println(
                            "ERROR in UmlNamespaceElement.makeConnections : child is of type "+
                            child.getClass().getName() );
                    }
                }
                else
                {
                    System.err.println("ERROR in UmlNamespaceElement.makeConnections : child==null");
                }
            }
            else
            {
                ((Worker)obj).makeConnections(main, parent);
            }
        }
    }


	public String getIdlCode( Main main, String prefix, StringBuffer includeStatements )
	{
	    StringBuffer code = new StringBuffer();
	    int s = myWorkers_.size();
	    for( int index=0; index<s; index++ )
	    {
	        code.append( ((Worker)myWorkers_.get(index)).getIdlCode(main, prefix, includeStatements) );
	    }
	    return code.toString();
	}


    public String getOclCode( Main main )
    {
	    StringBuffer code = new StringBuffer();
	    int s = myWorkers_.size();
	    for( int index=0; index<s; index++ )
	    {
	        code.append( ((Worker)myWorkers_.get(index)).getOclCode(main) );
	    }
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
	        if( (o instanceof UmlClass) && ((UmlClass)o).isException(main) )
	        {
	            number = ((UmlClass)o).createDependencyOrder(number, main);
	        }
	        else if( (o instanceof UmlPackage) && ((UmlPackage)o).hasException(main) )
	        {
	            number = ((UmlPackage)o).createDependencyOrder(number, main);
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
	
	
	boolean hasException( Main main )
	{
	    int s = myWorkers_.size();
	    for( int index=0; index<s; index++ )
	    {
	        Object o = myWorkers_.get(index);
	        if( (o instanceof UmlClass) && ((UmlClass)o).isException(main) )
	        {
	            return true;
	        }
	        if( (o instanceof UmlPackage) && ((UmlPackage)o).hasException(main) )
	        {
	            return true;
	        }
	    }
	    return false;
	}
}
