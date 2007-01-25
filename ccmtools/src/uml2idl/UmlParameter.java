/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlParameter.java,v 1.2 2005/02/21 10:58:31 rlechner Exp $
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
import uml_parser.uml.MParameter_kind;
import uml_parser.uml.MParameter_type;
import uml_parser.uml.MClassifier;


/**
Operation parameter. <br>Children:
<ul>
<li>{@link UmlModelElementName}</li>
<li>{@link UmlModelElementConstraint}</li>
<li>{@link uml_parser.uml.MParameter_kind}</li>
<li>{@link uml_parser.uml.MParameter_type}</li>
</ul>

@author Robert Lechner (rlechner@gmx.at)
@version January 2004
*/
class UmlParameter extends uml_parser.uml.MParameter implements Worker
{
    private String id_;
    private Worker idlParent_;


    UmlParameter( Attributes attrs )
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
	        Main.logChild("UmlParameter",obj);
	    }
	}


	public void makeConnections( Main main, Worker parent )
	{
	    idlParent_ = parent;
	    int s = size();
	    for( int index=0; index<s; index++ )
	    {
	        Object obj = get(index);
	        if( obj instanceof UmlModelElementConstraint )
	        {
	            ((UmlModelElementConstraint)obj).makeConnections(main, parent);
	        }
	        else if( obj instanceof Worker )
	        {
	            ((Worker)obj).makeConnections(main, this);
	        }
	    }
	}


    public String getIdlCode( Main main, String prefix, StringBuffer includeStatements )
    {
    	return "";
    }


    /**
     * Returns the ID of the parameter type, or null.
     */
    public String getTypeId()
    {
        if( type_==null )
        {
            java.util.Vector v1 = findChildren(MParameter_type.xmlName__);
            if( v1.size()>0 )
            {
                java.util.Vector v2 = ((MParameter_type)v1.get(0)).findChildren(MClassifier.xmlName__);
                if( v2.size()>0 )
                {
                    type_ = ((MClassifier)v2.get(0)).xmi_idref_;
                }
            }
        }
        return type_;
    }


    /**
     * Returns the name of the parameter type, or null.
     */
    String getTypeName( Main main, Worker container )
    {
        String typeId = getTypeId();
        if( typeId==null )
        {
            return null;
        }
        Object typeObj = main.workers_.get(typeId);
        if( typeObj==null )
        {
            return null;
        }
        if( typeObj instanceof UmlDataType )
        {
            return ((UmlDataType)typeObj).getName();
        }
        if( typeObj instanceof UmlClass )
        {
            String typeName = ((UmlClass)typeObj).getPathName();
            if( container!=null && (container instanceof UmlClass) )
            {
                return Main.reducePathname(typeName, ((UmlClass)container).getPathName());
            }
            return typeName;
        }
        return null;
    }


    /**
     * Returns the kind of the parameter.
     *
     * @return 'in', 'inout', 'out' or 'return'
     */
    public String getKind()
    {
        if( kind_==null )
        {
            java.util.Vector v = findChildren(MParameter_kind.xmlName__);
            if( v.size()>0 )
            {
                kind_ = ((MParameter_kind)v.get(0)).xmi_value_;
            }
            else
            {
                kind_ = "in";
            }
        }
        return kind_;
    }


    private int dependencyNumber_=-1;

    public int createDependencyOrder( int number, Main main )
    {
	    if( dependencyNumber_>0 )
	    {
	        return number;
	    }
        String typeId = getTypeId();
        if( typeId!=null )
        {
            Object typeObj = main.workers_.get(typeId);
            if( typeObj!=null && (typeObj instanceof IdlContainer) )
            {
                number = ((IdlContainer)typeObj).updateDependencyOrder(number, main);
            }
        }
	    dependencyNumber_ = number;
	    return number+1;
    }

    public int getDependencyNumber()
    {
	    return dependencyNumber_;
    }


    public String getOclCode( Main main )
    {
        StringBuffer code = new StringBuffer();
        int s = size();
        for( int index=0; index<s; index++ )
        {
            Object o = get(index);
            if( o instanceof Worker )
            {
                code.append( ((Worker)o).getOclCode(main) );
            }
        }
        return code.toString();
    }
}
