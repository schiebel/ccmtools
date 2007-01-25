/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlAssociationConnection.java,v 1.2 2005/02/21 10:58:29 rlechner Exp $
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
Stores two instances of {@link UmlAssociationEnd}.

@author Robert Lechner (robert.lechner@salomon.at)
@version $Date: 2005/02/21 10:58:29 $
*/
class UmlAssociationConnection extends uml_parser.uml.MAssociation_connection implements Worker
{
    private String id_;
    private UmlAssociationEnd firstEnd_;
    private UmlAssociationEnd secondEnd_;


    UmlAssociationConnection( Attributes attrs )
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
	        }
	        if( firstEnd_==null && (obj instanceof UmlAssociationEnd) )
	        {
	            firstEnd_ = (UmlAssociationEnd)obj;
	        }
	        else if( secondEnd_==null && (obj instanceof UmlAssociationEnd) )
	        {
	            secondEnd_ = (UmlAssociationEnd)obj;
	        }
	        Main.logChild("UmlAssociationConnection",obj);
	    }
	}


	public void makeConnections( Main main, Worker parent )
	{
	    firstEnd_.makeConnections(main, parent);
	    secondEnd_.makeConnections(main, parent);
	}


    public String getIdlCode( Main main, String prefix, StringBuffer includeStatements )
    {
    	return "";
    }


    public String getOclCode( Main main )
    {
        return "";
    }


    void createHomeManages( String primarykey, Main main )
    {
        if( secondEnd_.isNavigable() )
        {
            createHomeManages(firstEnd_, secondEnd_, primarykey, main);
        }
        else if( firstEnd_.isNavigable() )
        {
            createHomeManages(secondEnd_, firstEnd_, primarykey, main);
        }
    }

    private static void createHomeManages( UmlAssociationEnd home, UmlAssociationEnd component,
                                           String primarykey, Main main )
    {
        String homeId = home.getParticipantId();
        String componentId = component.getParticipantId();
        if( homeId==null || componentId==null )
        {
            System.err.println("UmlAssociationConnection.createHomeManages: no participant id");
            return;
        }
        Object homeObj = main.workers_.get(homeId);
        if( homeObj==null )
        {
            System.err.println("UmlAssociationConnection.createHomeManages: no home object");
            return;
        }
        if( !(homeObj instanceof UmlClass) )
        {
            System.err.println("UmlAssociationConnection.createHomeManages: wrong home object");
            return;
        }
        ((UmlClass)homeObj).setHomeData(componentId, primarykey);
    }


    /**
     * Creates attributes on both association ends.
     */
    void createAttributes( UmlModelElementStereotype newStereotypes, Main main, Vector comments )
    {
        if( secondEnd_.isNavigable() )
        {
            update( createAttribute(firstEnd_, secondEnd_, main), newStereotypes, comments );
        }
        if( firstEnd_.isNavigable() )
        {
            update( createAttribute(secondEnd_, firstEnd_, main), newStereotypes, comments );
        }
    }

    private static void update( UmlAttribute attr, UmlModelElementStereotype newStereotypes, Vector comments )
    {
        if( attr!=null )
        {
            if( newStereotypes.size()>0 )
            {
                attr.add(newStereotypes);
            }
            for( int i=0; i<comments.size(); i++ )
            {
                attr.add(comments.get(i));
            }
        }
    }

    private static UmlAttribute createAttribute( UmlAssociationEnd src, UmlAssociationEnd dest, Main main )
    {
        String participantId = src.getParticipantId();
        if( participantId==null )
        {
            System.err.println("UmlAssociationConnection.createAttribute: no participant id");
            return null;
        }
        Object participantObj = main.workers_.get(participantId);
        if( participantObj==null )
        {
            System.err.println("UmlAssociationConnection.createAttribute: no participant object: "+participantId);
            return null;
        }
        String attributeName = dest.getName();
        if( attributeName==null )
        {
            if( participantObj instanceof UmlClass )
            {
                if( ((UmlClass)participantObj).canUseDummyName(main) )
                {
                    attributeName = "DUMMY";
                }
            }
            if( attributeName==null )
            {
                // print association participants to reduce UML bugfixing
                System.err.print("UmlAssociationConnection.createAttribute: no attribute name");
                try {
                System.err.print(" [");
                System.err.print(((UmlClass)main.workers_.get(src.getParticipantId())).getPathName()+ " -> ");
                System.err.println(((UmlClass)main.workers_.get(dest.getParticipantId())).getPathName()+ "]");
                }
                catch(Exception e) {
                    System.out.println("[unknown participants]");
                }
                return null;
            }
        }
        UmlMultiplicity multiplicity = dest.getMultiplicity();
        if( multiplicity==null )
        {
            System.err.println("UmlAssociationConnection.createAttribute: no multiplicity");
            return null;
        }
        String typeId = dest.getParticipantId();
        if( typeId==null )
        {
            System.err.println("UmlAssociationConnection.createAttribute: no type id");
            return null;
        }
        if( participantObj instanceof UmlClass )
        {
            UmlAttribute newAttribute = new UmlAttribute(attributeName, typeId, multiplicity, dest.getVisibility());
            dest.addQualifiersAndStereotypes(newAttribute, main);
            ((UmlClass)participantObj).addNewAttribute(newAttribute);
            Vector comments = dest.findChildren(MModelElement_comment.xmlName__);
            for( int i=0; i<comments.size(); i++ )
            {
                newAttribute.add(comments.get(i));
            }
            return newAttribute;
        }
        else
        {
            System.err.println("UmlAssociationConnection.createAttribute: unknown participant object: "+
                               participantObj.getClass().getName());
            return null;
        }
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
