/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlGeneralization.java,v 1.2 2005/02/21 10:58:27 rlechner Exp $
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
import uml_parser.uml.MGeneralization_child;
import uml_parser.uml.MGeneralization_parent;
import uml_parser.uml.MGeneralizableElement;


/**
Generalization. <br>Children:
<ul>
<li>{@link UmlModelElementStereotype}</li>
<li>{@link uml_parser.uml.MGeneralization_parent}</li>
</ul>

@author Robert Lechner (robert.lechner@salomon.at)
@version $Date: 2005/02/21 10:58:27 $
*/
class UmlGeneralization extends uml_parser.uml.MGeneralization implements Worker
{
    private String id_;


    UmlGeneralization( Attributes attrs )
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
	        Main.logChild("UmlGeneralization",obj);
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
	    Object parent = getParent(main);
        if( parent!=null && (parent instanceof IdlContainer) )
        {
            return ((IdlContainer)parent).updateDependencyOrder(number, main);
        }
	    return number;
	}

	public int getDependencyNumber()
	{
	    return 0;
	}


    boolean isStereotype( String type, Main main )
    {
        if( xmi_idref_!=null )
        {
            Object obj = main.workers_.get(xmi_idref_);
            if( obj==null )
            {
                return false;
            }
            return ((UmlGeneralization)obj).isStereotype(type, main);
        }
        if( stereotype_!=null )
        {
            return stereotype_.equals(type) ||
                    main.isStereotype(stereotype_, type);
        }
        return main.isModelElementStereotype(this, type);
    }


    /**
     *  Returns the base class (or null);
     */
    public Object getParent( Main main )
    {
        if( xmi_idref_!=null )
        {
            Object obj = main.workers_.get(xmi_idref_);
            if( obj==null )
            {
                return null;
            }
            return ((UmlGeneralization)obj).getParent(main);
        }
        if( parent_==null )
        {
            Vector v1 = findChildren(MGeneralization_parent.xmlName__);
            if( v1.size()<1 )
            {
                return null;
            }
            MGeneralization_parent p1 = (MGeneralization_parent)v1.get(0);
            Vector v2 = p1.findChildren(MGeneralizableElement.xmlName__);
            if( v2.size()<1 )
            {
                return null;
            }
            MGeneralizableElement e1 = (MGeneralizableElement)v2.get(0);
            parent_ = e1.xmi_idref_;
            if( parent_==null )
            {
                return null;
            }
        }
        return main.workers_.get(parent_);
    }


    /**
     *  Returns the sub class (or null);
     */
    public Object getChild( Main main )
    {
        if( xmi_idref_!=null )
        {
            Object obj = main.workers_.get(xmi_idref_);
            if( obj==null )
            {
                return null;
            }
            return ((UmlGeneralization)obj).getChild(main);
        }
        if( child_==null )
        {
            Vector v1 = findChildren(MGeneralization_child.xmlName__);
            if( v1.size()<1 )
            {
                return null;
            }
            MGeneralization_child p1 = (MGeneralization_child)v1.get(0);
            Vector v2 = p1.findChildren(MGeneralizableElement.xmlName__);
            if( v2.size()<1 )
            {
                return null;
            }
            MGeneralizableElement e1 = (MGeneralizableElement)v2.get(0);
            child_ = e1.xmi_idref_;
            if( child_==null )
            {
                return null;
            }
        }
        return main.workers_.get(child_);
    }


    /**
     *  Returns the name of the base class (or null);
     */
    public String getParentName( Main main )
    {
        Object parent = getParent(main);
        if( parent!=null )
        {
            if( parent instanceof UmlClass )
            {
                return ((UmlClass)parent).getPathName();
            }
            if( parent instanceof Worker )
            {
                return ((Worker)parent).getName();
            }
        }
        return null;
    }
}
