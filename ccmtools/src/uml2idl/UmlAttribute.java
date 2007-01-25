/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlAttribute.java,v 1.2 2005/02/21 10:58:29 rlechner Exp $
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
import uml_parser.uml.MStructuralFeature_multiplicity;
import uml_parser.uml.MMultiplicity;
import uml_parser.uml.MStructuralFeature_type;
import uml_parser.uml.MClassifier;
import uml_parser.uml.MAttribute_initialValue;
import uml_parser.uml.MExpression;


/**
Class attribute. <br>Children:
<ul>
<li>{@link UmlModelElementName}</li>
<li>{@link UmlModelElementStereotype}</li>
<li>{@link UmlModelElementConstraint}</li>
<li>{@link uml_parser.uml.MStructuralFeature_type}</li>
<li>{@link uml_parser.uml.MModelElement_visibility}</li>
<li>{@link uml_parser.uml.MStructuralFeature_multiplicity} == {@link UmlMultiplicity}</li>
<li>{@link uml_parser.uml.MAttribute_initialValue}</li>
</ul>

@author Robert Lechner (robert.lechner@salomon.at)
@version $Date: 2005/02/21 10:58:29 $
*/
class UmlAttribute extends uml_parser.uml.MAttribute implements Worker
{
    private String id_;
    private UmlMultiplicity multiplicity_;
    private Worker idlParent_;
    private HashMap tagged_values_;

    // stores instances of UmlAttribute
    private Vector myAssociationEndQualifiers_ = new Vector();


    UmlAttribute( Attributes attrs )
    {
        super(attrs);
        id_ = xmi_id_;
        if( id_==null )
        {
            id_ = Main.makeId();
        }
    }


    UmlAttribute( String name, String typeId, UmlMultiplicity multiplicity, String visibility )
    {
        super(null);
        id_ = Main.makeId();
        name_ = name;
        multiplicity_ = multiplicity;
        type_ = typeId;
        visibility_ = visibility;
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
                return "/* no attribute name */";
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
	        Main.logChild("UmlAttribute",obj);
	    }
	    if( multiplicity_==null )
	    {
	        setMultiplicity();
	    }
	    multiplicity_.collectWorkers(map);
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
	    multiplicity_.makeConnections(main, this);
	}


    static final String CORBA_CONSTANT = "CORBAConstant";
    static final String READONLY = "readonly";
    static final String SWITCH = "switch";
    static final String CCM_PROVIDES = "CCMProvides";
    static final String CCM_USES = "CCMUses";
    static final String CCM_EMITS = "CCMEmits";
    static final String CCM_PUBLISHES = "CCMPublishes";
    static final String CCM_CONSUMES = "CCMConsumes";


    String getInitialValue()
    {
        Vector v = findChildren(MAttribute_initialValue.xmlName__);
        if( v.size()>0 )
        {
            MAttribute_initialValue iv = (MAttribute_initialValue)v.get(0);
            Vector v2 = iv.findChildren(MExpression.xmlName__);
            if( v2.size()>0 )
            {
                UmlExpression expr = (UmlExpression)v2.get(0);
                return expr.getValue();
            }
        }
        return "/* no initial value */";
    }


    /**
     * Returns the IDL-type of this attribute (or null).
     */
    String getTypeName( Main main )
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
            String result = ((UmlClass)typeObj).getPathName();
            if( idlParent_!=null && (idlParent_ instanceof IdlContainer) )
            {
                return Main.reducePathname(result, ((IdlContainer)idlParent_).getPathName());
            }
            return result;
        }
        return null;
    }


    String getIdlCodeForCase( Main main )
    {
        if( tagged_values_==null )
        {
            tagged_values_ = main.makeModelElementTaggedValues(this);
        }
        String caseLabel = (String)tagged_values_.get("Case");
        if( caseLabel==null )
        {
            caseLabel = (String)tagged_values_.get("case");
            if( caseLabel==null )
            {
                return "/* no case label */\n";
            }
        }
        String typeName = getTypeName(main);
        if( typeName==null )
        {
            return "/* no type name */\n";
        }
        String myName = getName();
        caseLabel = caseLabel.trim();
        if( caseLabel.equals("default") )
        {
            return "default: "+typeName+" "+myName+";\n";
        }
        Vector caseList = new Vector();
        addToCaseList( caseLabel, caseList );
        if( caseList.size()<1 )
        {
            return "case "+caseLabel+": "+typeName+" "+myName+";\n";
        }
        StringBuffer code = new StringBuffer();
        for( int index=0; index<caseList.size(); index++ )
        {
            code.append("case ");
            code.append((String)caseList.get(index));
            code.append(": ");
        }
        code.append(typeName);
        code.append(" ");
        code.append(myName);
        code.append(";\n");
        return code.toString();
    }

    private static void addToCaseList( String caseLabel, Vector caseList )
    {
        int len = caseLabel.length();
        if( len<1 )
        {
            return;
        }
        if( len>=2 )
        {
            if( caseLabel.charAt(0)=='(' && caseLabel.charAt(len-1)==')' )
            {
                caseLabel = caseLabel.substring(1,len-1).trim();
                addToCaseList( caseLabel, caseList );
                return;
            }
        }
        int index = caseLabel.indexOf(',');
        if( index<0 )
        {
            caseList.add(caseLabel);
            return;
        }
        if( index==0 )
        {
            addToCaseList(caseLabel.substring(1).trim(), caseList);
            return;
        }
        String part1 = caseLabel.substring(0, index).trim();
        String part2 = caseLabel.substring(index+1).trim();
        caseList.add(part1);
        addToCaseList(part2, caseList);
    }


    boolean isCaseDefault( Main main )
    {
        if( tagged_values_==null )
        {
            tagged_values_ = main.makeModelElementTaggedValues(this);
        }
        String caseLabel = (String)tagged_values_.get("Case");
        if( caseLabel==null )
        {
            return false;
        }
        return caseLabel.equals("default");
    }


    public String getIdlCode( Main main, String prefix, StringBuffer includeStatements )
    {
        StringBuffer code = new StringBuffer();
        code.append(Main.makeModelElementComments(this, prefix));
        //code.append("/* "+dependencyNumber_+" */ ");
        code.append(prefix);
        String typeId = getTypeId();
        if( typeId==null )
        {
            return prefix+"/* '"+getName()+"': no type id */\n";
        }
        Object typeObj = main.workers_.get(typeId);
        if( typeObj==null )
        {
            return prefix+"/* '"+getName()+"': no type object */\n";
        }
        String typeName;
        if( typeObj instanceof UmlDataType )
        {
            typeName = ((UmlDataType)typeObj).getName();
        }
        else if( typeObj instanceof UmlClass )
        {
            typeName = ((UmlClass)typeObj).getPathName();
            if( idlParent_ instanceof IdlContainer )
            {
                typeName = Main.reducePathname(typeName, ((IdlContainer)idlParent_).getPathName());
            }
        }
        else
        {
            return prefix+"/* '"+getName()+"': unknown type object: "+typeObj.getClass().getName()+" */\n";
        }
        if( isStereotype(CORBA_CONSTANT, main) )
        {
            code.append("const ");
            code.append(typeName);
            code.append(" ");
            code.append(getName());
            code.append(" = ");
            code.append( getInitialValue() );
            code.append(";\n");
        }
        else
        {
            String kind = null;
            if( isStereotype(CCM_PROVIDES, main) )
            {
                kind = "provides ";
            }
            else if( isStereotype(CCM_USES, main) )
            {
                if( getRangeUpper().equals("-1") )
                {
                    kind = "uses multiple ";
                }
                else
                {
                    kind = "uses ";
                }
            }
            else if( isStereotype(CCM_EMITS, main) )
            {
                kind = "emits ";
            }
            else if( isStereotype(CCM_PUBLISHES, main) )
            {
                kind = "publishes ";
            }
            else if( isStereotype(CCM_CONSUMES, main) )
            {
                kind = "consumes ";
            }
            if( kind!=null )
            {
                code.append(kind);
                code.append(typeName);
                code.append(" ");
                code.append(getName());
                code.append(";\n");
            }
            else
            {
                if( isStereotype(READONLY, main) )
                {
                    code.append("readonly ");
                }
                if( idlParent_ instanceof UmlClass )
                {
                    UmlClass theClass = (UmlClass)idlParent_;
                    if( theClass.needAttributeToken(main) )
                    {
                        code.append("attribute ");
                    }
                    else if( theClass.isValueType(main) )
                    {
                        code.append(getVisibility());
                        code.append(" ");
                    }
                }
                code.append(typeName);
                code.append(" ");
                code.append(getName());
                if( tagged_values_==null )
                {
                    tagged_values_ = main.makeModelElementTaggedValues(this);
                }
                String raisesText = (String)tagged_values_.get("raises");
                if( raisesText!=null )
                {
                    code.append(" raises");
                    code.append(raisesText);
                }
                else
                {
                    raisesText = (String)tagged_values_.get("getraises");
                    if( raisesText!=null )
                    {
                        code.append(" getraises");
                        code.append(raisesText);
                    }
                    raisesText = (String)tagged_values_.get("setraises");
                    if( raisesText!=null )
                    {
                        code.append(" setraises");
                        code.append(raisesText);
                    }
                }
                code.append(";\n");
            }
        }
	    return code.toString();
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


    public String getRangeLower()
    {
        if( multiplicity_==null )
        {
	        setMultiplicity();
        }
        return multiplicity_.getLower();
    }


    public String getRangeUpper()
    {
        if( multiplicity_==null )
        {
	        setMultiplicity();
        }
        return multiplicity_.getUpper();
    }


    private void setMultiplicity()
    {
        Vector v1 = findChildren(MStructuralFeature_multiplicity.xmlName__);
        if( v1.size()>0 )
        {
            Vector v2 = ((MStructuralFeature_multiplicity)v1.get(0)).findChildren(MMultiplicity.xmlName__);
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


    /**
     * Returns the XMI-ID of the type of this attribute (or null).
     */
    String getTypeId()
    {
        if( type_==null )
        {
            Vector v1 = findChildren(MStructuralFeature_type.xmlName__);
            if( v1.size()>0 )
            {
                MStructuralFeature_type sft = (MStructuralFeature_type)v1.get(0);
                Vector v2 = sft.findChildren(MClassifier.xmlName__);
                if( v2.size()>0 )
                {
                    MClassifier c = (MClassifier)v2.get(0);
                    type_ = c.xmi_idref_;
                }
                else
                {
                    v2 = sft.findChildren(UmlDataType.xmlName__);
                    if( v2.size()>0 )
                    {
                        UmlDataType dt = (UmlDataType)v2.get(0);
                        type_ = dt.xmi_idref_;
                    }
                }
            }
        }
        return type_;
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
            if( typeObj!=null )
            {
                if( typeObj instanceof IdlContainer )
                {
                    //System.out.println(idlParent_.getName()+" --> "+((IdlContainer)typeObj).getName());
                    number = ((IdlContainer)typeObj).updateDependencyOrder(number, main);
                }
                /*else
                {
                    System.out.println(idlParent_.getName()+" : typeObj="+typeObj.getClass().getName());
                }*/
            }
            /*else
            {
                System.out.println(idlParent_.getName()+" : typeObj==null");
            }*/
        }
	    dependencyNumber_ = number;
	    return number+1;
	}

	public int getDependencyNumber()
	{
	    return dependencyNumber_;
	}


	/**
	 * Adds association-end qualifiers.
	 *
	 * @param qualifiers stores instances of UmlAttribute
	 */
	void addQualifiers( Vector qualifiers )
	{
	    myAssociationEndQualifiers_.addAll(qualifiers);
	}


	/**
	 * Returns the first association-end qualifier, or null.
	 */
	UmlAttribute getFirstAssociationEndQualifier()
	{
	    if( myAssociationEndQualifiers_.size()<1 )
	    {
	        return null;
	    }
	    return (UmlAttribute)myAssociationEndQualifiers_.get(0);
	}

	Vector getAllAssociationEndQualifiers()
	{
	    return myAssociationEndQualifiers_;
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
