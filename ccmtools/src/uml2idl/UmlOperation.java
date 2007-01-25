/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlOperation.java,v 1.2 2005/02/21 10:58:29 rlechner Exp $
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
import java.util.HashMap;
import uml_parser.uml.MModelElement_visibility;
import uml_parser.uml.MBehavioralFeature_parameter;
import uml_parser.uml.MParameter;


/**
Class operation. <br>Children:
<ul>
<li>{@link UmlModelElementName}</li>
<li>{@link UmlModelElementStereotype}</li>
<li>{@link UmlModelElementTaggedValue}</li>
<li>{@link UmlModelElementConstraint}</li>
<li>{@link uml_parser.uml.MModelElement_visibility}</li>
<li>{@link uml_parser.uml.MBehavioralFeature_parameter} == {@link UmlParameter}</li>
</ul>

@author Robert Lechner (robert.lechner@salomon.at)
@version $Date: 2005/02/21 10:58:29 $
*/
class UmlOperation extends uml_parser.uml.MOperation implements Worker
{
    private String id_;
    private UmlClass idlParent_;
    private HashMap tagged_values_;

    // stores instances of UmlParameter
    private Vector parameter_ = new Vector();


    UmlOperation( Attributes attrs )
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
	        else if( obj instanceof MBehavioralFeature_parameter )
	        {
	            Vector v = ((MBehavioralFeature_parameter)obj).findChildren(MParameter.xmlName__);
	            parameter_.addAll(v);
	            for( int k=0; k<v.size(); k++ )
	            {
	                ((UmlParameter)v.get(k)).collectWorkers(map);
	            }
	        }
	        Main.logChild("UmlOperation",obj);
	    }
	}


	public void makeConnections( Main main, Worker parent )
	{
	    idlParent_ = (UmlClass)parent;
	    int index, s=size();
	    for( index=0; index<s; index++ )
	    {
	        Object obj = get(index);
	        if( obj instanceof Worker )
	        {
	            ((Worker)obj).makeConnections(main, this);
	        }
	    }
	    s = parameter_.size();
	    for( index=0; index<s; index++ )
	    {
	        ((UmlParameter)parameter_.get(index)).makeConnections(main, this);
	    }
	}


    /**
     * Returns 'public', 'protected', 'private' or 'package'.
     */
    public String getVisibility()
    {
        if( visibility_==null )
        {
            java.util.Vector v = findChildren(MModelElement_visibility.xmlName__);
            if( v.size()>0 )
            {
                visibility_ = ((MModelElement_visibility)v.get(0)).xmi_value_;
            }
            else
            {
                visibility_ = "public";
            }
        }
        return visibility_;
    }


    /**
     * Returns the ID of the return type, or null.
     */
    public String getTypeId()
    {
        int s = parameter_.size();
        for( int index=0; index<s; index++ )
        {
            UmlParameter p = (UmlParameter)parameter_.get(index);
            if( p.getKind().equals("return") )
            {
                return p.getTypeId();
            }
        }
        return null;
    }


    public String getIdlCode( Main main, String prefix, StringBuffer includeStatements )
    {
        StringBuffer code = new StringBuffer();
	    code.append(Main.makeModelElementComments(this, prefix));
        code.append(prefix);
        StringBuffer params = new StringBuffer();
        boolean addComma = false;
        String typeName = null;
        int s = parameter_.size();
        for( int index=0; index<s; index++ )
        {
            UmlParameter p = (UmlParameter)parameter_.get(index);
            String kind = p.getKind();
            if( kind.equals("return") )
            {
                typeName = p.getTypeName(main, idlParent_);
            }
            else
            {
                if( addComma )
                {
                    params.append(", ");
                }
                else
                {
                    addComma = true;
                }
                params.append(kind);
                params.append(" ");
                params.append(p.getTypeName(main, idlParent_));
                params.append(" ");
                params.append(p.getName());
            }
        }
        if( isFactory(main) )
        {
            code.append("factory ");
            typeName = null;
        }
        else if( isStereotype("CCMFinder", main) )
        {
            code.append("finder ");
            typeName = null;
        }
        else if( isStereotype("oneway", main) )
        {
            code.append("oneway ");
        }
        if( typeName!=null )
        {
            code.append(typeName);
            code.append(" ");
        }
        code.append(getName());
        code.append("(");
        code.append(params.toString());
        code.append(")");
        String raisesValue = getRaises(main);
        if( raisesValue!=null )
        {
	    code.append(" raises ");
            code.append(raisesValue);
        }
        String contextValue = getContext(main);
        if( contextValue!=null )
        {
            code.append(" context ");
            code.append(contextValue);
        }
        code.append(";\n");
        return code.toString();
    }


    private int dependencyNumber_=-1;

    public int createDependencyOrder( int number, Main main )
    {
	    if( dependencyNumber_>0 )
	    {
	        return number;
	    }
        int s = parameter_.size();
        for( int index=0; index<s; index++ )
        {
            number = ((UmlParameter)parameter_.get(index)).createDependencyOrder(number, main);
        }
	    dependencyNumber_ = number;
	    return number+1;
    }

    public int getDependencyNumber()
    {
	    return dependencyNumber_;
    }


    /**
     * Returns the tagged value 'raises', or null.
     */
    String getRaises( Main main )
    {
        if( tagged_values_==null )
        {
            tagged_values_ = main.makeModelElementTaggedValues(this);
        }
        String result = (String)tagged_values_.get("raises");
        if( result==null )
        {
            result = (String)tagged_values_.get("Raises");
        }
        return result;
    }


    /**
     * Returns the tagged value 'context', or null.
     */
    String getContext( Main main )
    {
        if( tagged_values_==null )
        {
            tagged_values_ = main.makeModelElementTaggedValues(this);
        }
        return (String)tagged_values_.get("context");
    }


    boolean isFactory( Main main )
    {
        return isStereotype("CORBAValueFactory", main) ||
               isStereotype("CCMFactory", main);
    }


    boolean isStereotype( String type, Main main )
    {
        if( stereotype_!=null )
        {
            return stereotype_.equals(type) ||
                    main.isStereotype(stereotype_, type);
        }
        return main.isModelElementStereotype(this, type);
    }


    public String getOclCode( Main main )
    {
        StringBuffer code = new StringBuffer();
        int s, index;
        s = size();
        for( index=0; index<s; index++ )
        {
            Object o = get(index);
            if( o instanceof Worker )
            {
                code.append( ((Worker)o).getOclCode(main) );
            }
        }
        s = parameter_.size();
        for( index=0; index<s; index++ )
        {
            code.append( ((UmlParameter)parameter_.get(index)).getOclCode(main) );
        }
        return code.toString();
    }


    /**
     * Calculates the OCL statement for pre- and postconditions.
     *
     * @param main  main class
     * @param body  the body of the OCL statement
     */
    String getOclCode( Main main, String body )
    {
        StringBuffer params = new StringBuffer();
        boolean addComma = false;
        int s = parameter_.size();
        for( int index=0; index<s; index++ )
        {
            UmlParameter p = (UmlParameter)parameter_.get(index);
            String kind = p.getKind();
            if( !kind.equals("return") )
            {
                if( addComma )
                {
                    params.append(", ");
                }
                else
                {
                    addComma = true;
                }
                params.append(p.getName());
                params.append(":");
                params.append( convertIdlType(p.getTypeName(main, idlParent_)) );
            }
        }
        String sig = getName()+"("+params.toString()+")";
        if( idlParent_==null )
        {
            return "-- ERROR: NO PARENT\n"+
                   "package GLOBAL\n"+
                   "  context GLOBAL::"+sig+"\n"+
                   "    "+body+"\n"+
                   "endpackage\n\n";
        }
        return idlParent_.getOclCode(main, sig, body);
    }
    
    // from 'ccmtools.OCL.utils.OclConstants' and 'ccmtools.OCL.utils.OclNormalization':
    private static final String OCL_TYPE_NAME_VOID        = "OclVoid";
    private static final String OCL_TYPE_NAME_ANY         = "OclAny";
    private static final String OCL_TYPE_NAME_BOOLEAN     = "Boolean";
    private static final String OCL_TYPE_NAME_REAL        = "Real";
    private static final String OCL_TYPE_NAME_INTEGER     = "Integer";
    private static final String OCL_TYPE_NAME_STRING      = "String";
    private static final String OCL_TYPE_NAME_ENUMERATION = "Enumeration";

    /**
     * Converts an IDL type name to OCL.
     */
    static String convertIdlType( String idlType )
    {
        if( idlType.equals("void") )
        {
            return OCL_TYPE_NAME_VOID;
        }
        if( idlType.equals("any") )
        {
            return OCL_TYPE_NAME_ANY;
        }
        if( idlType.equals("boolean") )
        {
            return OCL_TYPE_NAME_BOOLEAN;
        }
        if( idlType.equals("float") || idlType.equals("double") || idlType.equals("long double") )
        {
            return OCL_TYPE_NAME_REAL;
        }
        if( idlType.equals("byte") || idlType.equals("char") ||
            idlType.equals("int") || idlType.equals("long") ||
            idlType.equals("short") || idlType.equals("long long") ||
            idlType.equals("octet") || idlType.equals("wchar") )
        {
            return OCL_TYPE_NAME_INTEGER;
        }
        if( idlType.equals("string") || idlType.equals("wstring") )
        {
            return OCL_TYPE_NAME_STRING;
        }
        return idlType;
    }
}
