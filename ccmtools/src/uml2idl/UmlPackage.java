/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlPackage.java,v 1.2 2005/02/21 10:58:27 rlechner Exp $
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
import uml_parser.uml.MModelElement_clientDependency;


/**
Stereotype 'CORBAModule'. <br>Children:
<ul>
<li>{@link UmlModelElementStereotype}</li>
<li>{@link UmlModelElementName}</li>
<li>{@link UmlNamespaceElement}</li>
<li>{@link uml_parser.uml.MModelElement_clientDependency} == {@link UmlDependency}</li>
<li>{@link uml_parser.uml.MModelElement_comment}</li>
</ul>

@author Robert Lechner (robert.lechner@salomon.at)
@version $Date: 2005/02/21 10:58:27 $
*/
class UmlPackage extends uml_parser.uml.MPackage implements IdlContainer
{
    private String id_;
    private Worker idlParent_;

    // true, if the package is not part of a class
    private boolean all_parents_are_packages_;

    // stores instances of UmlNamespaceElement
    private Vector myElements_ = new Vector();

    // stores instances of UmlDependency
    private Vector myClientDependency_ = new Vector();


    UmlPackage( Attributes attrs )
    {
        super(attrs);
        id_ = xmi_id_;
        if( id_==null )
        {
            id_ = Main.makeId();
        }
    }


    public static final String CORBA_MODULE = "CORBAModule";


    boolean isCorbaModule( Main main )
    {
        if( stereotype_!=null )
        {
            return stereotype_.equals(CORBA_MODULE) ||
                    main.isStereotype(stereotype_, CORBA_MODULE);
        }
        return main.isModelElementStereotype(this, CORBA_MODULE);
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
                name_ = "/* package has no name */";
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
	        }
	        if( obj instanceof UmlNamespaceElement )
	        {
	            myElements_.add(obj);
	        }
	        else if( obj instanceof MModelElement_clientDependency )
	        {
	            MModelElement_clientDependency cd = (MModelElement_clientDependency)obj;
	            for( int k=0; k<cd.size(); k++ )
	            {
	                Object o = cd.get(k);
	                if( o instanceof UmlDependency )
	                {
	                    myClientDependency_.add(o);
	                    ((UmlDependency)o).collectWorkers(map);
	                }
	            }
	        }
	        Main.logChild("UmlPackage",obj);
	    }
	}


	public void makeConnections( Main main, Worker parent )
	{
	    idlParent_ = parent;
	    int index, s=myElements_.size();
	    for( index=0; index<s; index++ )
	    {
	        ((Worker)myElements_.get(index)).makeConnections(main, this);
	    }
	    if( parent==null )
	    {
	        all_parents_are_packages_ = true;
	    }
	    else if( parent instanceof UmlPackage )
	    {
	        all_parents_are_packages_ = ((UmlPackage)parent).all_parents_are_packages_;
	    }
	    else
	    {
	        all_parents_are_packages_ = false;
	    }
	}


	public String getPathName()
	{
	    if( idlParent_!=null && (idlParent_ instanceof IdlContainer) )
	    {
	        return ((IdlContainer)idlParent_).getPathName() + Main.PATH_SEPERATOR + getName();
	    }
	    return getName();
	}


	public String getIdlCode( Main main, String prefix, StringBuffer includeStatements )
	{
	    if( !isCorbaModule(main) )
	    {
	        return "";
	    }
	    StringBuffer code = new StringBuffer();
	    code.append(Main.makeModelElementComments(this, prefix));
	    code.append(prefix);
	    code.append("module ");
	    code.append(getName());
   	    code.append(" {\n");
	    String p2 = prefix+Main.SPACES;
	    int s = myElements_.size();
	    for( int index=0; index<s; index++ )
	    {
	        code.append( ((Worker)myElements_.get(index)).getIdlCode(main, p2, includeStatements) );
	    }
	    code.append(prefix);
	    code.append("}; // /module ");
	    code.append(getName());
	    code.append("\n\n");
	    return code.toString();
	}


    public String getOclCode( Main main )
    {
	    if( !isCorbaModule(main) )
	    {
	        return "";
	    }
	    StringBuffer code = new StringBuffer();
	    int s = myElements_.size();
	    for( int index=0; index<s; index++ )
	    {
	        code.append( ((Worker)myElements_.get(index)).getOclCode(main) );
	    }
	    return code.toString();
    }


    private int dependencyNumber_=-1;

	public int createDependencyOrder( int number, Main main )
	{
	    if( dependencyNumber_>=0 )
	    {
	        return number;
	    }
	    dependencyNumber_ = 0;
	    int index, s=myElements_.size();
	    Vector non_exceptions = new Vector();
	    for( index=0; index<s; index++ )
	    {
	        UmlNamespaceElement element = (UmlNamespaceElement)myElements_.get(index);
	        if( element.hasException(main) )
	        {
	            number = element.createDependencyOrder(number, main);
	        }
	        else
	        {
	            non_exceptions.add(element);
	        }
	    }
	    s = non_exceptions.size();
	    for( index=0; index<s; index++ )
	    {
	        number = ((Worker)non_exceptions.get(index)).createDependencyOrder(number, main);
	    }
	    Main.sort(myElements_);
	    s = myClientDependency_.size();
	    for( index=0; index<s; index++ )
	    {
	        number = ((Worker)myClientDependency_.get(index)).createDependencyOrder(number, main);
	    }
	    dependencyNumber_ = number;
	    return number+1;
	}

	public int getDependencyNumber()
	{
	    return dependencyNumber_;
	}

	public int updateDependencyOrder( int number, Main main )
	{
	    if( idlParent_!=null && idlParent_.getDependencyNumber()<0 )
	    {
	        if( idlParent_ instanceof IdlContainer )
	        {
	            return ((IdlContainer)idlParent_).updateDependencyOrder(number, main);
	        }
	    }
	    return createDependencyOrder(number, main);
	}


    String getOclPackageForClass( Main main )
    {
        if( all_parents_are_packages_ || idlParent_==null )
        {
            return getPathName();
        }
        if( idlParent_ instanceof UmlClass )
        {
            return ((UmlClass)idlParent_).getOclPackage(main);
        }
        if( idlParent_ instanceof UmlPackage )
        {
            return ((UmlPackage)idlParent_).getOclPackageForClass(main);
        }
        return idlParent_.getName();
    }


    String getOclSignatureForClass( Main main, String className )
    {
        if( all_parents_are_packages_ || idlParent_==null )
        {
            return className;
        }
        className = getName()+"::"+className;
        if( idlParent_ instanceof UmlClass )
        {
            return ((UmlClass)idlParent_).getOclSignature(main)+"::"+className;
        }
        if( idlParent_ instanceof UmlPackage )
        {
            return ((UmlPackage)idlParent_).getOclSignatureForClass(main, className);
        }
        return className;
    }
    
    
    boolean hasException( Main main )
    {
	    int s = myElements_.size();
	    for( int index=0; index<s; index++ )
	    {
	        if( ((UmlNamespaceElement)myElements_.get(index)).hasException(main) )
	        {
	            return true;
	        }
	    }
	    return false;
    }
}
