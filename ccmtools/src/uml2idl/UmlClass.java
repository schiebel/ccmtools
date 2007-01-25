/*
 * UML to IDL/OCL converter
 * 
 * 2004 by Robert Lechner (rlechner@gmx.at)
 * 
 * $Id: UmlClass.java,v 1.2 2005/02/21 10:58:31 rlechner Exp $
 * 
 * This library is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License along with this library;
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA
 * 02111-1307 USA
 */
package uml2idl;

import org.xml.sax.Attributes;
import java.util.HashMap;
import java.util.Vector;
import java.io.File;
import uml_parser.uml.MGeneralizableElement_isAbstract;
import uml_parser.uml.MGeneralizableElement_generalization;
import uml_parser.uml.MModelElement_clientDependency;

/**
 * UML-class. <br>
 * Children:
 * <ul>
 * <li>{@link UmlModelElementName}</li>
 * <li>{@link UmlModelElementStereotype}</li>
 * <li>{@link UmlModelElementTaggedValue}</li>
 * <li>{@link UmlModelElementConstraint}</li>
 * <li>{@link UmlClassifierFeature}</li>
 * <li>{@link UmlNamespaceElement}</li>
 * <li>{@link uml_parser.uml.MGeneralizableElement_isAbstract}</li>
 * <li>{@link uml_parser.uml.MGeneralizableElement_generalization}=={@link UmlGeneralization}
 * </li>
 * <li>{@link uml_parser.uml.MModelElement_clientDependency}=={@link UmlDependency}</li>
 * </ul>
 * 
 * @author Robert Lechner (robert.lechner@salomon.at)
 * @version $Date: 2005/02/21 10:58:31 $
 */
class UmlClass extends uml_parser.uml.MClass implements IdlContainer
{
    private String id_;

    private HashMap tagged_values_;

    private Worker idlParent_;

    // stores instances of UmlClassifierFeature
    private Vector myFeatures_ = new Vector();

    // stores instances of UmlNamespaceElement
    private Vector myElements_ = new Vector();

    // stores instances of UmlGeneralization
    private Vector myGeneralizations_ = new Vector();

    // stores instances of UmlDependency
    private Vector myClientDependency_ = new Vector();

    // stores instances of UmlModelElementConstraint
    private Vector myConstraints_ = new Vector();

    UmlClass( Attributes attrs )
    {
        super(attrs);
        id_ = xmi_id_;
        if (id_ == null)
        {
            id_ = Main.makeId();
        }
    }

    public String getId()
    {
        return id_;
    }

    private static int nameCounter_;

    public String getName()
    {
        if (name_ == null)
        {
            name_ = Main.makeModelElementName(this);
            if (name_ == null)
            {
                nameCounter_++;
                name_ = "Anonymous" + nameCounter_;
            }
        }
        return name_;
    }

    public void collectWorkers( java.util.HashMap map )
    {
        map.put(id_, this);
        int s = size();
        for (int index = 0; index < s; index++)
        {
            Object obj = get(index);
            if (obj instanceof Worker)
            {
                ((Worker) obj).collectWorkers(map);
            }
            //
            if (obj instanceof UmlClassifierFeature)
            {
                myFeatures_.add(obj);
            }
            else if (obj instanceof UmlNamespaceElement)
            {
                myElements_.add(obj);
            }
            else if (obj instanceof UmlModelElementConstraint)
            {
                myConstraints_.add(obj);
            }
            else if (obj instanceof MGeneralizableElement_generalization)
            {
                MGeneralizableElement_generalization g = (MGeneralizableElement_generalization) obj;
                for (int k = 0; k < g.size(); k++)
                {
                    Object o = g.get(k);
                    if (o instanceof UmlGeneralization)
                    {
                        UmlGeneralization gen = (UmlGeneralization) o;
                        if (gen.xmi_idref_ == null)
                        {
                            myGeneralizations_.add(o);
                            gen.collectWorkers(map);
                        }
                    }
                }
            }
            else if (obj instanceof MModelElement_clientDependency)
            {
                MModelElement_clientDependency cd = (MModelElement_clientDependency) obj;
                for (int k = 0; k < cd.size(); k++)
                {
                    Object o = cd.get(k);
                    if (o instanceof UmlDependency)
                    {
                        myClientDependency_.add(o);
                        ((UmlDependency) o).collectWorkers(map);
                    }
                }
            }
            Main.logChild("UmlClass", obj);
        }
    }

    void addGeneralization( UmlGeneralization gen )
    {
        myGeneralizations_.add(gen);
    }

    public void makeConnections( Main main, Worker parent )
    {
        idlParent_ = parent;
        int index, s = myFeatures_.size();
        for (index = 0; index < s; index++)
        {
            ((Worker) myFeatures_.get(index)).makeConnections(main, this);
        }
        s = myElements_.size();
        for (index = 0; index < s; index++)
        {
            ((Worker) myElements_.get(index)).makeConnections(main, this);
        }
        /*
         * s = myGeneralizations_.size(); for( index=0; index <s; index++ ) {
         * ((Worker)myGeneralizations_.get(index)).makeConnections(main, this); }
         */
        s = myConstraints_.size();
        for (index = 0; index < s; index++)
        {
            ((Worker) myConstraints_.get(index)).makeConnections(main, this);
        }
    }

    public String getPathName()
    {
        if (idlParent_ != null && (idlParent_ instanceof IdlContainer))
        {
            return ((IdlContainer) idlParent_).getPathName() + Main.PATH_SEPERATOR + getName();
        }
        return getName();
    }

    static final String CORBA_INTERFACE = "CORBAInterface";

    static final String CORBA_STRUCT = "CORBAStruct";

    static final String CORBA_UNION = "CORBAUnion";

    static final String CORBA_CONSTANTS = "CORBAConstants";

    static final String CORBA_ENUM = "CORBAEnum";

    static final String CORBA_TYPEDEF = "CORBATypedef";

    static final String CORBA_SEQUENCE = "CORBASequence";

    static final String CORBA_ANONYMOUS_SEQUENCE = "CORBAAnonymousSequence";

    static final String CORBA_ARRAY = "CORBAArray";

    static final String CORBA_ANONYMOUS_ARRAY = "CORBAAnonymousArray";

    static final String CORBA_EXCEPTION = "CORBAException";

    static final String CORBA_VALUE = "CORBAValue";

    static final String CORBA_CUSTOM_VALUE = "CORBACustomValue";

    static final String CORBA_BOXED_VALUE = "CORBABoxedValue";

    static final String CCM_EVENT = "CCMEvent";

    static final String CCM_CUSTOM_EVENT = "CCMCustomEvent";

    static final String CCM_COMPONENT = "CCMComponent";

    static final String CCM_HOME = "CCMHome";

    static final String CCM_PRIMARY_KEY = "CCMPrimaryKey";

    static final String CORBA_PRIMITIVE = "CORBAPrimitive";

    static final String EXTERN = "extern";

    boolean isCorbaStereotype( String type, Main main )
    {
        if (stereotype_ != null)
        {
            return stereotype_.equals(type) || main.isStereotype(stereotype_, type);
        }
        return main.isModelElementStereotype(this, type);
    }

    boolean isException( Main main )
    {
        return isCorbaStereotype(CORBA_EXCEPTION, main);
    }

    boolean needAttributeToken( Main main )
    {
        return isCorbaStereotype(CORBA_INTERFACE, main) || isCorbaStereotype(CCM_COMPONENT, main)
                || isCorbaStereotype(CCM_HOME, main);
    }

    boolean isCollection( Main main )
    {
        return isCorbaStereotype(CORBA_SEQUENCE, main)
                || isCorbaStereotype(CORBA_ANONYMOUS_SEQUENCE, main)
                || isCorbaStereotype(CORBA_ARRAY, main)
                || isCorbaStereotype(CORBA_ANONYMOUS_ARRAY, main);
    }

    boolean isValueType( Main main )
    {
        return isCorbaStereotype(CORBA_VALUE, main) || isCorbaStereotype(CORBA_CUSTOM_VALUE, main)
                || isCorbaStereotype(CCM_EVENT, main) || isCorbaStereotype(CCM_CUSTOM_EVENT, main);
    }

    boolean canUseDummyName( Main main )
    {
        return isCollection(main) || isCorbaStereotype(CORBA_UNION, main);
    }

    boolean isLocal( Main main )
    {
        if (tagged_values_ == null)
        {
            tagged_values_ = main.makeModelElementTaggedValues(this);
        }
        String value = (String) tagged_values_.get("isLocal");
        if (value == null)
        {
            return false;
        }
        return value.equalsIgnoreCase("true");
    }

    boolean isAbstract()
    {
        if (isAbstract_ != null)
        {
            return isAbstract_.equalsIgnoreCase("true");
        }
        Vector v = findChildren(MGeneralizableElement_isAbstract.xmlName__);
        if (v.size() < 1)
        {
            return false;
        }
        String value = ((MGeneralizableElement_isAbstract) v.get(0)).xmi_value_;
        if (value == null)
        {
            return false;
        }
        return value.equalsIgnoreCase("true");
    }

    public String getIdlCode( Main main, String prefix, StringBuffer includeStatements )
    {
        StringBuffer code = new StringBuffer();
        code.append(Main.makeModelElementComments(this, prefix));
        //code.append("/* "+dependencyNumber_+" */ ");
        if (isCorbaStereotype(CORBA_INTERFACE, main))
        {
            makeInterface(code, main, prefix, includeStatements);
        }
        else if (isCorbaStereotype(CORBA_STRUCT, main))
        {
            makeStruct(code, main, prefix, includeStatements);
        }
        else if (isCorbaStereotype(CORBA_EXCEPTION, main))
        {
            makeException(code, main, prefix, includeStatements);
        }
        else if (isCorbaStereotype(CORBA_TYPEDEF, main))
        {
            makeTypedef(code, main, prefix);
        }
        else if (isCorbaStereotype(CORBA_CONSTANTS, main))
        {
            makeConstants(code, main, prefix, includeStatements);
        }
        else if (isCorbaStereotype(CORBA_ENUM, main))
        {
            makeEnum(code, main, prefix);
        }
        else if (isCorbaStereotype(CORBA_SEQUENCE, main)
                || isCorbaStereotype(CORBA_ANONYMOUS_SEQUENCE, main))
        {
            makeSequence(code, main, prefix);
        }
        else if (isCorbaStereotype(CORBA_ARRAY, main)
                || isCorbaStereotype(CORBA_ANONYMOUS_ARRAY, main))
        {
            makeArray(code, main, prefix);
        }
        else if (isCorbaStereotype(CORBA_VALUE, main))
        {
            makeValueType(code, main, prefix, "valuetype", includeStatements);
        }
        else if (isCorbaStereotype(CORBA_CUSTOM_VALUE, main))
        {
            makeValueType(code, main, prefix, "custom valuetype", includeStatements);
        }
        else if (isCorbaStereotype(CCM_EVENT, main))
        {
            makeValueType(code, main, prefix, "eventtype", includeStatements);
        }
        else if (isCorbaStereotype(CCM_CUSTOM_EVENT, main))
        {
            makeValueType(code, main, prefix, "custom eventtype", includeStatements);
        }
        else if (isCorbaStereotype(CORBA_BOXED_VALUE, main))
        {
            makeBoxedValue(code, main, prefix);
        }
        else if (isCorbaStereotype(CCM_COMPONENT, main))
        {
            makeComponent(code, main, prefix, includeStatements);
        }
        else if (isCorbaStereotype(CCM_HOME, main))
        {
            makeHome(code, main, prefix, includeStatements);
        }
        else if (isCorbaStereotype(CCM_PRIMARY_KEY, main)
                || isCorbaStereotype(CORBA_PRIMITIVE, main))
        {
            // nothing to do
            return "";
        }
        else if (isCorbaStereotype(CORBA_UNION, main))
        {
            makeUnion(code, main, prefix);
        }
        else if (isCorbaStereotype(EXTERN, main))
        {
            String text = replace(getPathName(), Main.PATH_SEPERATOR, File.separator);
            includeStatements.append("#include<");
            includeStatements.append(text);
            includeStatements.append(".idl>\n");
            return "";
        }
        else
        {
            code.append(prefix);
            code.append("/* UML class '");
            code.append(getName());
            code.append("': wrong stereotype */\n\n");
        }
        return code.toString();
    }

    private String replace( String text, String oldPart, String newPart )
    {
        int index = text.indexOf(oldPart);
        if (index < 0)
        {
            return text;
        }
        if (index == 0)
        {
            return newPart + replace(text.substring(oldPart.length()), oldPart, newPart);
        }
        return text.substring(0, index) + newPart
                + replace(text.substring(index + oldPart.length()), oldPart, newPart);
    }

    private void makeInterface( StringBuffer code, Main main, String prefix,
            StringBuffer includeStatements )
    {
        code.append(prefix);
        if (isLocal(main))
        {
            code.append("local ");
        }
        else if (isAbstract())
        {
            code.append("abstract ");
        }
        code.append("interface ");
        code.append(getName());
        int s = myGeneralizations_.size();
        if (s > 0)
        {
            code.append(" : ");
            for (int k = 0; k < s; k++)
            {
                if (k > 0)
                {
                    code.append(", ");
                }
                String baseClass = ((UmlGeneralization) myGeneralizations_.get(k))
                        .getParentName(main);
                if (idlParent_ != null && (idlParent_ instanceof IdlContainer))
                {
                    baseClass = Main.reducePathname(baseClass, ((IdlContainer) idlParent_)
                            .getPathName());
                }
                code.append(baseClass);
            }
        }
        code.append(" {\n");
        doChildren(code, main, prefix, includeStatements);
        code.append(prefix);
        code.append("};\n\n");
    }

    private void doChildren( StringBuffer code, Main main, String prefix,
            StringBuffer includeStatements )
    {
        String p2 = prefix + Main.SPACES;
        int index, s;
        s = myElements_.size();
        for (index = 0; index < s; index++)
        {
            Object child = myElements_.get(index);
            code.append(((Worker) child).getIdlCode(main, p2, includeStatements));
        }
        s = myFeatures_.size();
        for (index = 0; index < s; index++)
        {
            Object child = myFeatures_.get(index);
            code.append(((Worker) child).getIdlCode(main, p2, includeStatements));
        }
    }

    private void makeStruct( StringBuffer code, Main main, String prefix,
            StringBuffer includeStatements )
    {
        code.append(prefix);
        code.append("struct ");
        code.append(getName());
        code.append(" {\n");
        doChildren(code, main, prefix, includeStatements);
        code.append(prefix);
        code.append("};\n\n");
    }

    private void makeException( StringBuffer code, Main main, String prefix,
            StringBuffer includeStatements )
    {
        code.append(prefix);
        code.append("exception ");
        code.append(getName());
        code.append(" {\n");
        doChildren(code, main, prefix, includeStatements);
        code.append(prefix);
        code.append("};\n\n");
    }

    private void makeTypedef( StringBuffer code, Main main, String prefix )
    {
        code.append(prefix);
        if (myGeneralizations_.size() != 1)
        {
            code.append("/* typedef '");
            code.append(getName());
            code.append("': wrong number of generalizations */\n\n");
            return;
        }
        String typeName = ((UmlGeneralization) myGeneralizations_.get(0)).getParentName(main);
        if (idlParent_ != null && (idlParent_ instanceof IdlContainer))
        {
            typeName = Main.reducePathname(typeName, ((IdlContainer) idlParent_).getPathName());
        }
        code.append("typedef ");
        code.append(typeName);
        code.append(" ");
        code.append(getName());
        code.append(";\n\n");
    }

    private void makeConstants( StringBuffer code, Main main, String prefix,
            StringBuffer includeStatements )
    {
        int s = myFeatures_.size();
        for (int index = 0; index < s; index++)
        {
            Vector v = ((UmlClassifierFeature) myFeatures_.get(index)).getAttributes();
            for (int k = 0; k < v.size(); k++)
            {
                code.append(((Worker) v.get(k)).getIdlCode(main, prefix, includeStatements));
            }
        }
        code.append("\n");
    }

    private void makeUnion( StringBuffer code, Main main, String prefix )
    {
        code.append(prefix);
        Vector vSwitch = new Vector();
        Vector vCase = new Vector();
        UmlAttribute aDefault = null;
        int index, s;
        s = myFeatures_.size();
        for (index = 0; index < s; index++)
        {
            Vector v = ((UmlClassifierFeature) myFeatures_.get(index)).getAttributes();
            for (int k = 0; k < v.size(); k++)
            {
                UmlAttribute attr = (UmlAttribute) v.get(k);
                if (attr.isStereotype(UmlAttribute.SWITCH, main))
                {
                    vSwitch.add(attr);
                }
                else if (attr.isCaseDefault(main))
                {
                    aDefault = attr;
                }
                else
                {
                    vCase.add(attr);
                }
            }
        }
        if (vSwitch.size() != 1 || (vCase.size() < 1 && aDefault == null))
        {
            code.append("/* union: syntax error */\n\n");
            return;
        }
        String typeName = ((UmlAttribute) vSwitch.get(0)).getTypeName(main);
        if (typeName == null)
        {
            code.append("/* union: no switch type */\n\n");
            return;
        }
        if (idlParent_ != null && (idlParent_ instanceof IdlContainer))
        {
            typeName = Main.reducePathname(typeName, ((IdlContainer) idlParent_).getPathName());
        }
        code.append("union ");
        code.append(getName());
        code.append(" switch(");
        code.append(typeName);
        code.append(") {\n");
        String p2 = prefix + Main.SPACES;
        s = vCase.size();
        for (index = 0; index < s; index++)
        {
            code.append(p2);
            code.append(((UmlAttribute) vCase.get(index)).getIdlCodeForCase(main));
        }
        if (aDefault != null)
        {
            code.append(p2);
            code.append(aDefault.getIdlCodeForCase(main));
        }
        code.append(prefix);
        code.append("};\n\n");
    }

    private void makeEnum( StringBuffer code, Main main, String prefix )
    {
        Vector names = new Vector();
        int index, s = myFeatures_.size();
        for (index = 0; index < s; index++)
        {
            Object child = myFeatures_.get(index);
            ((UmlClassifierFeature) child).addAttributeNames(names);
        }
        code.append(prefix);
        code.append("enum ");
        code.append(getName());
        code.append(" {\n");
        String p2 = prefix + Main.SPACES;
        s = names.size();
        for (index = 0; index < s; index++)
        {
            code.append(p2);
            code.append((String) names.get(index));
            if (index < s - 1)
            {
                code.append(",");
            }
            code.append("\n");
        }
        code.append(prefix);
        code.append("};\n\n");
    }

    private void makeSequence( StringBuffer code, Main main, String prefix )
    {
        code.append(prefix);
        UmlAttribute typeAttribute = null;
        int s = myFeatures_.size();
        for (int index = 0; index < s; index++)
        {
            Object child = myFeatures_.get(index);
            typeAttribute = ((UmlClassifierFeature) child).getFirstAttribute();
            if (typeAttribute != null)
            {
                break;
            }
        }
        if (typeAttribute == null)
        {
            code.append("/* sequence '" + getName() + "': no type */\n\n");
            return;
        }
        String typeName = typeAttribute.getTypeName(main);
        if (idlParent_ != null && (idlParent_ instanceof IdlContainer))
        {
            typeName = Main.reducePathname(typeName, ((IdlContainer) idlParent_).getPathName());
        }
        int lower = 0, upper = -1;
        UmlAttribute qualifier = typeAttribute.getFirstAssociationEndQualifier();
        if (qualifier != null)
        {
            try
            {
                lower = Integer.parseInt(qualifier.getRangeLower());
                upper = Integer.parseInt(qualifier.getRangeUpper());
            }
            catch (Exception e)
            {
                lower = 0;
                upper = -1;
            }
        }
        if (lower > upper)
        {
            code.append("typedef sequence< ");
            code.append(typeName);
            code.append(" > ");
            code.append(getName());
            code.append(";\n\n");
        }
        else
        {
            code.append("typedef sequence< ");
            code.append(typeName);
            code.append(" , " + (upper - lower + 1) + " > ");
            code.append(getName());
            code.append(";  /* range: " + lower + ".." + upper + " */\n\n");
        }
    }

    private void makeArray( StringBuffer code, Main main, String prefix )
    {
        code.append(prefix);
        UmlAttribute typeAttribute = null;
        int index, s = myFeatures_.size();
        for (index = 0; index < s; index++)
        {
            Object child = myFeatures_.get(index);
            typeAttribute = ((UmlClassifierFeature) child).getFirstAttribute();
            if (typeAttribute != null)
            {
                break;
            }
        }
        if (typeAttribute == null)
        {
            code.append("/* array '" + getName() + "': no type */\n\n");
            return;
        }
        String typeName = typeAttribute.getTypeName(main);
        if (idlParent_ != null && (idlParent_ instanceof IdlContainer))
        {
            typeName = Main.reducePathname(typeName, ((IdlContainer) idlParent_).getPathName());
        }
        Vector qualifiers = typeAttribute.getAllAssociationEndQualifiers();
        s = qualifiers.size();
        if (s < 1)
        {
            code.append("/* array '" + getName() + "': no qualifiers */\n\n");
            return;
        }
        code.append("typedef ");
        code.append(typeName);
        code.append(" ");
        code.append(getName());
        StringBuffer range = new StringBuffer();
        for (index = 0; index < s; index++)
        {
            UmlAttribute qualifier = (UmlAttribute) qualifiers.get(index);
            int lower = 0, upper = 1;
            try
            {
                lower = Integer.parseInt(qualifier.getRangeLower());
                upper = Integer.parseInt(qualifier.getRangeUpper());
            }
            catch (Exception e)
            {
                lower = 0;
                upper = 1;
            }
            code.append("[" + (upper - lower + 1) + "]");
            range.append("[" + lower + ".." + upper + "]");
        }
        code.append(";  /* range: ");
        code.append(range.toString());
        code.append(" */\n\n");
    }

    private void makeValueType( StringBuffer code, Main main, String prefix, String typeName,
            StringBuffer includeStatements )
    {
        code.append(prefix);
        if (isAbstract())
        {
            code.append("abstract ");
        }
        code.append(typeName);
        code.append(" ");
        code.append(getName());
        int s = myGeneralizations_.size();
        if (s > 0)
        {
            int k;
            Vector inheritance = new Vector();
            Vector supports = new Vector();
            boolean truncatable = true;
            for (k = 0; k < s; k++)
            {
                UmlGeneralization gen = (UmlGeneralization) myGeneralizations_.get(k);
                String baseClass = gen.getParentName(main);
                if (idlParent_ != null && (idlParent_ instanceof IdlContainer))
                {
                    baseClass = Main.reducePathname(baseClass, ((IdlContainer) idlParent_)
                            .getPathName());
                }
                if (gen.isStereotype("CORBAValueSupports", main))
                {
                    supports.add(baseClass);
                }
                else if (gen.isStereotype("CORBATruncatable", main))
                {
                    inheritance.add(baseClass);
                }
                else
                {
                    inheritance.add(baseClass);
                    truncatable = false;
                }
            }
            s = inheritance.size();
            if (s > 0)
            {
                code.append(" : ");
                if (truncatable)
                {
                    code.append("truncatable ");
                }
                for (k = 0; k < s; k++)
                {
                    if (k > 0)
                    {
                        code.append(", ");
                    }
                    code.append((String) inheritance.get(k));
                }
            }
            s = supports.size();
            if (s > 0)
            {
                code.append(" supports ");
                for (k = 0; k < s; k++)
                {
                    if (k > 0)
                    {
                        code.append(", ");
                    }
                    code.append((String) supports.get(k));
                }
            }
        }
        code.append(" {\n");
        doChildren(code, main, prefix, includeStatements);
        code.append(prefix);
        code.append("};\n\n");
    }

    private void makeBoxedValue( StringBuffer code, Main main, String prefix )
    {
        code.append(prefix);
        if (myGeneralizations_.size() != 1)
        {
            code.append("/* boxed value '");
            code.append(getName());
            code.append("': wrong number of generalizations */\n\n");
            return;
        }
        String typeName = ((UmlGeneralization) myGeneralizations_.get(0)).getParentName(main);
        if (idlParent_ != null && (idlParent_ instanceof IdlContainer))
        {
            typeName = Main.reducePathname(typeName, ((IdlContainer) idlParent_).getPathName());
        }
        code.append("valuetype ");
        code.append(getName());
        code.append(" ");
        code.append(typeName);
        code.append(";\n\n");
    }

    private void makeComponent( StringBuffer code, Main main, String prefix,
            StringBuffer includeStatements )
    {
        code.append(prefix);
        code.append("component ");
        code.append(getName());
        int s = myGeneralizations_.size();
        if (s > 0)
        {
            int k;
            Vector inheritance = new Vector();
            Vector supports = new Vector();
            for (k = 0; k < s; k++)
            {
                UmlGeneralization gen = (UmlGeneralization) myGeneralizations_.get(k);
                String baseClass = gen.getParentName(main);
                if (idlParent_ != null && (idlParent_ instanceof IdlContainer))
                {
                    baseClass = Main.reducePathname(baseClass, ((IdlContainer) idlParent_)
                            .getPathName());
                }
                if (gen.isStereotype("CCMSupports", main))
                {
                    supports.add(baseClass);
                }
                else
                {
                    inheritance.add(baseClass);
                }
            }
            s = inheritance.size();
            if (s > 0)
            {
                code.append(" : ");
                for (k = 0; k < s; k++)
                {
                    if (k > 0)
                    {
                        code.append(", ");
                    }
                    code.append((String) inheritance.get(k));
                }
            }
            s = supports.size();
            if (s > 0)
            {
                code.append(" supports ");
                for (k = 0; k < s; k++)
                {
                    if (k > 0)
                    {
                        code.append(", ");
                    }
                    code.append((String) supports.get(k));
                }
            }
        }
        code.append(" {\n");
        doChildren(code, main, prefix, includeStatements);
        code.append(prefix);
        code.append("};\n\n");
    }

    private void makeHome( StringBuffer code, Main main, String prefix,
            StringBuffer includeStatements )
    {
        code.append(prefix);
        code.append("home ");
        code.append(getName());
        int s = myGeneralizations_.size();
        if (s > 0)
        {
            int k;
            Vector inheritance = new Vector();
            Vector supports = new Vector();
            for (k = 0; k < s; k++)
            {
                UmlGeneralization gen = (UmlGeneralization) myGeneralizations_.get(k);
                String baseClass = gen.getParentName(main);
                if (idlParent_ != null && (idlParent_ instanceof IdlContainer))
                {
                    baseClass = Main.reducePathname(baseClass, ((IdlContainer) idlParent_)
                            .getPathName());
                }
                if (gen.isStereotype("CCMSupports", main))
                {
                    supports.add(baseClass);
                }
                else
                {
                    inheritance.add(baseClass);
                }
            }
            s = inheritance.size();
            if (s > 0)
            {
                code.append(" : ");
                for (k = 0; k < s; k++)
                {
                    if (k > 0)
                    {
                        code.append(", ");
                    }
                    code.append((String) inheritance.get(k));
                }
            }
            s = supports.size();
            if (s > 0)
            {
                code.append(" supports ");
                for (k = 0; k < s; k++)
                {
                    if (k > 0)
                    {
                        code.append(", ");
                    }
                    code.append((String) supports.get(k));
                }
            }
        }
        if (componentId_ != null)
        {
            Object componentObj = main.workers_.get(componentId_);
            if (componentObj != null)
            {
                String componentName = ((IdlContainer) componentObj).getPathName();
                if (idlParent_ != null && (idlParent_ instanceof IdlContainer))
                {
                    componentName = Main.reducePathname(componentName, ((IdlContainer) idlParent_)
                            .getPathName());
                }
                code.append(" manages ");
                code.append(componentName);
            }
        }
        if (primarykey_ != null)
        {
            code.append(" primarykey ");
            code.append(primarykey_);
        }
        code.append(" {\n");
        doChildren(code, main, prefix, includeStatements);
        code.append(prefix);
        code.append("};\n\n");
    }

    /**
     * Adds a new attribute.
     */
    void addNewAttribute( UmlAttribute attr )
    {
        UmlClassifierFeature cf;
        if (myFeatures_.size() > 0)
        {
            cf = (UmlClassifierFeature) myFeatures_.get(0);
        }
        else
        {
            cf = new UmlClassifierFeature(null);
            myFeatures_.add(cf);
        }
        cf.add(attr);
    }

    private int dependencyNumber_ = -1;

    public int createDependencyOrder( int number, Main main )
    {
        if (dependencyNumber_ >= 0)
        {
            return number;
        }
        dependencyNumber_ = 0;
        int index, s;
        s = myElements_.size();
        for (index = 0; index < s; index++)
        {
            number = ((Worker) myElements_.get(index)).createDependencyOrder(number, main);
        }
        Main.sort(myElements_);
        s = myFeatures_.size();
        for (index = 0; index < s; index++)
        {
            number = ((Worker) myFeatures_.get(index)).createDependencyOrder(number, main);
        }
        Main.sort(myFeatures_);
        s = myGeneralizations_.size();
        for (index = 0; index < s; index++)
        {
            number = ((Worker) myGeneralizations_.get(index)).createDependencyOrder(number, main);
        }
        Main.sort(myGeneralizations_);
        s = myClientDependency_.size();
        for (index = 0; index < s; index++)
        {
            number = ((Worker) myClientDependency_.get(index)).createDependencyOrder(number, main);
        }
        if (componentId_ != null)
        {
            Object componentObj = main.workers_.get(componentId_);
            if (componentObj != null)
            {
                number = ((Worker) componentObj).createDependencyOrder(number, main);
            }
        }
        dependencyNumber_ = number;
        return number + 1;
    }

    public int getDependencyNumber()
    {
        return dependencyNumber_;
    }

    public int updateDependencyOrder( int number, Main main )
    {
        if (idlParent_ != null && idlParent_.getDependencyNumber() < 0)
        {
            if (idlParent_ instanceof IdlContainer)
            {
                return ((IdlContainer) idlParent_).updateDependencyOrder(number, main);
            }
        }
        return createDependencyOrder(number, main);
    }

    private String componentId_, primarykey_;

    void setHomeData( String componentId, String primarykey )
    {
        componentId_ = componentId;
        primarykey_ = primarykey;
    }

    public String getOclCode( Main main )
    {
        StringBuffer code = new StringBuffer();
        int s = size();
        for (int index = 0; index < s; index++)
        {
            Object o = get(index);
            if (o instanceof Worker)
            {
                code.append(((Worker) o).getOclCode(main));
            }
        }
        return code.toString();
    }

    /**
     * Calculates the OCL statement for an invariant.
     * 
     * @param main main class
     * @param body the body of the OCL statement
     */
    String getOclCode( Main main, String body )
    {
        String pkg = getOclPackage(main);
        String sig = getOclSignature(main);
        return "package " + pkg + "\n" + "  context " + sig + "\n" + "    " + body + "\n"
                + "endpackage\n\n";
    }

    /**
     * Calculates the OCL statement for pre- and postconditions.
     * 
     * @param main main class
     * @param operation the signature of the operation
     * @param body the body of the OCL statement
     */
    String getOclCode( Main main, String operation, String body )
    {
        String pkg = getOclPackage(main);
        String sig = getOclSignature(main);
        return "package " + pkg + "\n" + "  context " + sig + "::" + operation + "\n" + "    "
                + body + "\n" + "endpackage\n\n";
    }

    String getOclPackage( Main main )
    {
        if (idlParent_ == null)
        {
            return "GLOBAL";
        }
        if (idlParent_ instanceof UmlClass)
        {
            return ((UmlClass) idlParent_).getOclPackage(main);
        }
        if (idlParent_ instanceof UmlPackage)
        {
            return ((UmlPackage) idlParent_).getOclPackageForClass(main);
        }
        return idlParent_.getName();
    }

    String getOclSignature( Main main )
    {
        if (idlParent_ != null)
        {
            if (idlParent_ instanceof UmlClass)
            {
                return ((UmlClass) idlParent_).getOclSignature(main) + "::" + getName();
            }
            if (idlParent_ instanceof UmlPackage)
            {
                return ((UmlPackage) idlParent_).getOclSignatureForClass(main, getName());
            }
        }
        return getName();
    }
}