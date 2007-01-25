/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: Factory.java,v 1.1 2005/01/24 14:47:19 teiniker Exp $
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
import uml_parser.uml.*;


/**
 * Factory (used by the UML parser).
 *
 * @author Robert Lechner (rlechner@gmx.at)
 * @version January 2004
 */
class Factory extends uml_parser.DTD_Creator
{
    public uml_parser.DTD_Container create( String qName, Attributes attrs )
    {
        if(qName.equals(MModel.xmlName__)) return new UmlModel(attrs);
        if(qName.equals(MNamespace_ownedElement.xmlName__)) return new UmlNamespaceElement(attrs);
        if(qName.equals(MPackage.xmlName__)) return new UmlPackage(attrs);
        if(qName.equals(MStereotype.xmlName__)) return new UmlStereotype(attrs);
        if(qName.equals(MConstraint.xmlName__)) return new UmlConstraint(attrs);
        if(qName.equals(MDataType.xmlName__)) return new UmlDataType(attrs);
        if(qName.equals(MTagDefinition.xmlName__)) return new UmlTagDefinition(attrs);
        if(qName.equals(MClass.xmlName__)) return new UmlClass(attrs);
        if(qName.equals(MModelElement_name.xmlName__)) return new UmlModelElementName(attrs);
        if(qName.equals(MModelElement_stereotype.xmlName__)) return new UmlModelElementStereotype(attrs);
        if(qName.equals(MClassifier_feature.xmlName__)) return new UmlClassifierFeature(attrs);
        if(qName.equals(MModelElement_taggedValue.xmlName__)) return new UmlModelElementTaggedValue(attrs);
        if(qName.equals(MTaggedValue.xmlName__)) return new UmlTaggedValue(attrs);
        if(qName.equals(MAttribute.xmlName__)) return new UmlAttribute(attrs);
        if(qName.equals(MMultiplicity.xmlName__)) return new UmlMultiplicity(attrs);
        if(qName.equals(MMultiplicityRange.xmlName__)) return new UmlMultiplicityRange(attrs);
        if(qName.equals(MOperation.xmlName__)) return new UmlOperation(attrs);
        if(qName.equals(MParameter.xmlName__)) return new UmlParameter(attrs);
        if(qName.equals(MAssociation.xmlName__)) return new UmlAssociation(attrs);
        if(qName.equals(MAssociation_connection.xmlName__)) return new UmlAssociationConnection(attrs);
        if(qName.equals(MAssociationEnd.xmlName__)) return new UmlAssociationEnd(attrs);
        if(qName.equals(MGeneralization.xmlName__)) return new UmlGeneralization(attrs);
        if(qName.equals(MExpression.xmlName__)) return new UmlExpression(attrs);
        if(qName.equals(MDependency.xmlName__)) return new UmlDependency(attrs);
        if(qName.equals(MModelElement_constraint.xmlName__)) return new UmlModelElementConstraint(attrs);
        return super.create(qName, attrs);
    }
}
