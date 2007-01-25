/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlStereotype.java,v 1.2 2005/02/21 10:58:27 rlechner Exp $
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


/**
UML stereotype. <br>Children:
<ul>
<li>{@link UmlModelElementName}</li>
</ul>

@author Robert Lechner (robert.lechner@salomon.at)
@version $Date: 2005/02/21 10:58:27 $
*/
class UmlStereotype extends uml_parser.uml.MStereotype implements Worker
{
    private String id_;


    UmlStereotype( Attributes attrs )
    {
        super(attrs);
        id_ = xmi_id_;
        if( id_==null )
        {
            id_ = Main.makeId();
        }
    }


    UmlStereotype( String name )
    {
        super(null);
        name_ = name;
        id_ = Main.makeId();
    }


	public String getName()
	{
        if( name_==null )
        {
            name_ = Main.makeModelElementName(this);
        }
	    return name_;
	}


    public String getId()
    {
        return id_;
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
	        Main.logChild("UmlStereotype",obj);
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


	boolean isStereotype( String name, Main main )
	{
	    if( xmi_idref_==null )
	    {
	        return getName().equals(name);
	    }
	    UmlStereotype type = (UmlStereotype)main.workers_.get(xmi_idref_);
	    if( type==null )
	    {
	        System.err.println("ERROR: UmlStereotype.isStereotype: xmi_idref_="+xmi_idref_);
	        return false;
	    }
	    return type.isStereotype(name, main);
	}


	public int createDependencyOrder( int number, Main main )
	{
	    return number;
	}

	public int getDependencyNumber()
	{
	    return 0;
	}
}
