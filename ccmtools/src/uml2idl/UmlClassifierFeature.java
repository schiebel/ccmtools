/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlClassifierFeature.java,v 1.2 2005/02/21 10:58:29 rlechner Exp $
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
Container for children of {@link UmlClass}. <br>Children:
<ul>
<li>{@link UmlAttribute}</li>
<li>{@link UmlOperation}</li>
</ul>

@author Robert Lechner (rlechner@gmx.at)
@version January 2004
*/
class UmlClassifierFeature extends uml_parser.uml.MClassifier_feature implements Worker
{
    private String id_;

    // stores instances of UmlAttribute
    private Vector myAttributes_ = new Vector();

    // stores instances of UmlOperation
    private Vector myOperations_ = new Vector();


    UmlClassifierFeature( Attributes attrs )
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


	Vector getAttributes()
	{
	    return myAttributes_;
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
    	        if( obj instanceof UmlAttribute )
    	        {
    	            myAttributes_.add(obj);
    	        }
    	        else if( obj instanceof UmlOperation )
    	        {
    	            myOperations_.add(obj);
    	        }
	        }
	        Main.logChild("UmlClassifierFeature",obj);
	    }
	}


	public void makeConnections( Main main, Worker parent )
	{
	    int index, s=myAttributes_.size();
	    for( index=0; index<s; index++ )
	    {
	        ((Worker)myAttributes_.get(index)).makeConnections(main, parent);
	    }
	    s = myOperations_.size();
	    for( index=0; index<s; index++ )
	    {
	        ((Worker)myOperations_.get(index)).makeConnections(main, parent);
	    }
	}


	public String getIdlCode( Main main, String prefix, StringBuffer includeStatements )
	{
	    StringBuffer code = new StringBuffer();
	    int index, s=myAttributes_.size();
	    for( index=0; index<s; index++ )
	    {
	        code.append( ((Worker)myAttributes_.get(index)).getIdlCode(main, prefix, includeStatements) );
	    }
	    s = myOperations_.size();
	    for( index=0; index<s; index++ )
	    {
	        code.append( ((Worker)myOperations_.get(index)).getIdlCode(main, prefix, includeStatements) );
	    }
	    return code.toString();
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


	/**
	 * Adds a new attribute.
	 */
	void add( UmlAttribute attr )
	{
	    myAttributes_.add(attr);
	}


    private int dependencyNumber_=-1;

	public int createDependencyOrder( int number, Main main )
	{
	    if( dependencyNumber_>0 )
	    {
	        return number;
	    }
	    int index, s=myAttributes_.size();
	    for( index=0; index<s; index++ )
	    {
	        number = ((Worker)myAttributes_.get(index)).createDependencyOrder(number, main);
	    }
	    s=myOperations_.size();
	    for( index=0; index<s; index++ )
	    {
	        number = ((Worker)myOperations_.get(index)).createDependencyOrder(number, main);
	    }
	    dependencyNumber_ = number;
	    return number+1;
	}

	public int getDependencyNumber()
	{
	    return dependencyNumber_;
	}


	void addAttributeNames( Vector names )
	{
	    int s = myAttributes_.size();
	    for( int index=0; index<s; index++ )
	    {
	        names.add( ((UmlAttribute)myAttributes_.get(index)).getName() );
	    }
	}


	/**
	 * Returns the first attribute, or null.
	 */
	UmlAttribute getFirstAttribute()
	{
	    if( myAttributes_.size()<1 )
	    {
	        return null;
	    }
	    return (UmlAttribute)myAttributes_.get(0);
	}
}
