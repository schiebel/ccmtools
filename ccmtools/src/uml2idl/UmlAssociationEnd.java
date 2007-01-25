/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlAssociationEnd.java,v 1.2 2005/02/21 10:58:27 rlechner Exp $
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
import uml_parser.uml.MAssociationEnd_isNavigable;
import uml_parser.uml.MAssociationEnd_multiplicity;
import uml_parser.uml.MAssociationEnd_participant;
import uml_parser.uml.MMultiplicity;
import uml_parser.uml.MClassifier;
import uml_parser.uml.MAssociationEnd_qualifier;
import uml_parser.uml.MModelElement_visibility;


/**
Association end. <br>Children:
<ul>
<li>{@link UmlModelElementName}</li>
<li>{@link UmlModelElementStereotype}</li>
<li>{@link UmlModelElementConstraint}</li>
<li>{@link uml_parser.uml.MModelElement_visibility}</li>
<li>{@link uml_parser.uml.MAssociationEnd_participant}</li>
<li>{@link uml_parser.uml.MAssociationEnd_isNavigable}</li>
<li>{@link uml_parser.uml.MAssociationEnd_multiplicity} == {@link UmlMultiplicity}</li>
<li>{@link uml_parser.uml.MAssociationEnd_qualifier} == {@link UmlAttribute}</li>
<li>{@link uml_parser.uml.MModelElement_comment}</li>
</ul>

@author Robert Lechner (robert.lechner@salomon.at)
@version $Date: 2005/02/21 10:58:27 $
*/
class UmlAssociationEnd extends uml_parser.uml.MAssociationEnd implements Worker
{
    private String id_;
    private UmlMultiplicity multiplicity_;


    UmlAssociationEnd( Attributes attrs )
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
	        }
	        Main.logChild("UmlAssociationEnd",obj);
	    }
		if( multiplicity_==null )
	    {
	        setMultiplicity();
	    }
	    multiplicity_.collectWorkers(map);
    }


    private void setMultiplicity()
    {
        Vector v1 = findChildren(MAssociationEnd_multiplicity.xmlName__);
        if( v1.size()>0 )
        {
            Vector v2 = ((MAssociationEnd_multiplicity)v1.get(0)).findChildren(MMultiplicity.xmlName__);
            if( v2.size()>0 )
            {
                multiplicity_ = (UmlMultiplicity)v2.get(0);
            }
        }
        if( multiplicity_==null )
        {
            multiplicity_ = new UmlMultiplicity();
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


    public boolean isNavigable()
    {
        if( isNavigable_==null )
        {
            Vector v = findChildren(MAssociationEnd_isNavigable.xmlName__);
            if( v.size()>0 )
            {
                isNavigable_ = ((MAssociationEnd_isNavigable)v.get(0)).xmi_value_;
            }
            else
            {
                isNavigable_ = "false";
                return false;
            }
        }
        return isNavigable_.equalsIgnoreCase("true");
    }


    public String getParticipantId()
    {
        if( participant_==null )
        {
            Vector v1 = findChildren(MAssociationEnd_participant.xmlName__);
            if( v1.size()>0 )
            {
                Vector v2 = ((MAssociationEnd_participant)v1.get(0)).findChildren(MClassifier.xmlName__);
                if( v2.size()>0 )
                {
                    participant_ = ((MClassifier)v2.get(0)).xmi_idref_;
                }
            }
        }
        return participant_;
    }


    public UmlMultiplicity getMultiplicity()
    {
		if( multiplicity_==null )
	    {
	        setMultiplicity();
	    }
	    return multiplicity_;
    }


	public int createDependencyOrder( int number, Main main )
	{
	    return number;
	}

	public int getDependencyNumber()
	{
	    return 0;
	}


	void addQualifiersAndStereotypes( UmlAttribute attr, Main main )
	{
	    Vector v1 = findChildren(MAssociationEnd_qualifier.xmlName__);
	    int s1 = v1.size();
	    for(int index1=0; index1<s1; index1++ )
	    {
	        MAssociationEnd_qualifier q = (MAssociationEnd_qualifier)v1.get(index1);
	        Vector v2 = q.findChildren(UmlAttribute.xmlName__);
	        attr.addQualifiers(v2);
	    }
	    if( isStereotype("readonlyEnd", main) )
	    {
	        UmlStereotype ust = new UmlStereotype(UmlAttribute.READONLY);
	        UmlModelElementStereotype mes = new UmlModelElementStereotype(null);
	        mes.add(ust);
	        attr.add(mes);
	    }
	    if( isStereotype("switchEnd", main) )
	    {
	        UmlStereotype ust = new UmlStereotype(UmlAttribute.SWITCH);
	        UmlModelElementStereotype mes = new UmlModelElementStereotype(null);
	        mes.add(ust);
	        attr.add(mes);
	    }
	    Vector v3 = findChildren(UmlModelElementTaggedValue.xmlName__);
	    for( int k=0; k<v3.size(); k++ )
	    {
	        attr.add(v3.get(k));
	    }
	    v3 = findChildren(UmlModelElementConstraint.xmlName__);
	    for( int k=0; k<v3.size(); k++ )
	    {
	        attr.add(v3.get(k));
	    }
	}


    /**
     * Returns 'public', 'protected', 'private' or 'package'.
     */
    public String getVisibility()
    {
        if( visibility_==null )
        {
            Vector v = findChildren(MModelElement_visibility.xmlName__);
            if( v.size()>0 )
            {
                visibility_ = ((MModelElement_visibility)v.get(0)).xmi_value_;
            }
            if( visibility_==null )
            {
                return "private";   // MagicDraw doesn't write it
            }
        }
        return visibility_;
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
            return ((UmlAssociationEnd)obj).isStereotype(type, main);
        }
        if( stereotype_!=null )
        {
            return stereotype_.equals(type) ||
                    main.isStereotype(stereotype_, type);
        }
        return main.isModelElementStereotype(this, type);
    }
}
