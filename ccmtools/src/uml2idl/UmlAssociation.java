/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlAssociation.java,v 1.2 2005/02/21 10:58:28 rlechner Exp $
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
import uml_parser.uml.MModelElement_comment;


/**
UML-association. <br>Children:
<ul>
<li>{@link UmlModelElementName}</li>
<li>{@link UmlAssociationConnection}</li>
<li>{@link UmlModelElementStereotype}</li>
<li>{@link uml_parser.uml.MModelElement_comment}</li>
</ul>

@author Robert Lechner (robert.lechner@salomon.at)
@version $Date: 2005/02/21 10:58:28 $
*/
class UmlAssociation extends uml_parser.uml.MAssociation implements Worker
{
    private String id_;
    private Worker idlParent_;
    private UmlAssociationConnection connection_;


    UmlAssociation( Attributes attrs )
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
	        if( connection_==null && (obj instanceof UmlAssociationConnection) )
	        {
	            connection_ = (UmlAssociationConnection)obj;
	        }
	        Main.logChild("UmlAssociation",obj);
	    }
	}


	public void makeConnections( Main main, Worker parent )
	{
	    idlParent_ = parent;
	    connection_.makeConnections(main, this);
	}


    public String getIdlCode( Main main, String prefix, StringBuffer includeStatements )
    {
    	return "";
    }


    public String getOclCode( Main main )
    {
        return "";
    }


    /**
     * Creates attributes on both association ends.
     */
    void createAttributes( Main main )
    {
        if( isStereotype("CCMManages", main) )
        {
            String primarykey = (String)main.makeModelElementTaggedValues(this).get("primarykey");
            connection_.createHomeManages(primarykey, main);
        }
        else
        {
            UmlModelElementStereotype newStereotypes = new UmlModelElementStereotype(null);
            copyStereotype(UmlAttribute.CCM_PROVIDES, newStereotypes, main);
            copyStereotype(UmlAttribute.CCM_USES, newStereotypes, main);
            copyStereotype(UmlAttribute.CCM_EMITS, newStereotypes, main);
            copyStereotype(UmlAttribute.CCM_PUBLISHES, newStereotypes, main);
            copyStereotype(UmlAttribute.CCM_CONSUMES, newStereotypes, main);
            Vector comments = findChildren(MModelElement_comment.xmlName__);
            connection_.createAttributes(newStereotypes, main, comments);
        }
    }

	private void copyStereotype( String name, UmlModelElementStereotype newStereotypes, Main main )
	{
	    if( isStereotype(name, main) )
	    {
	        newStereotypes.add(new UmlStereotype(name));
	    }
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
            return ((UmlAssociation)obj).isStereotype(type, main);
        }
        if( stereotype_!=null )
        {
            return stereotype_.equals(type) ||
                    main.isStereotype(stereotype_, type);
        }
        return main.isModelElementStereotype(this, type);
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
