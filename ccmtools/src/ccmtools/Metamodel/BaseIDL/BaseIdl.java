/* CCM Tools : CCM Metamodel Library
 * Egon Teiniker <egon.teiniker@tugraz.at>
 * Copyright (C) 2002, 2003 Salomon Automation
 *
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

/**
 * The IDL3 Meta Object Framework (MOF) Library implements the IDL3 MOF classes
 * and navigation between these classes. The library consists of two parts, the
 * Base IDL and the Component IDL.
 *
 * The navigation is inspired by the nsuml navigation methodology.
 *
 * Access to attributes
 * ====================
 * Access to object attributes is organized with the help of so-called
 * Setter and Getter methods, declared in corresponding interface and
 * implemented in class.
 *
 * Access to boolean attributes (e.g.: isEmpty:boolean):
 *    boolean isEmpty();             // Getter method
 *    void setEmpty(boolean __arg);  // Setter method
 *
 * Access to non-boolen attributes (e.g.: value:integer):
 *    int getValue();                // Getter method
 *    void setValue(int __arg);      // Setter method
 *
 * Access to non-boolean attributes set (e.g.: Set of Strings Member):
 *    Set getMembers();
 *    void setMembers(Set __arg);
 *    void addMember(String __arg);
 *    void removeMember(String __arg);
 *
 * Access to associations
 * ======================
 * An association have two roles, each role has name and multiplicity.
 * The role attached to an element is the direct role, the other role is
 * called the opposite role.
 *
 * Reference role (e.g.: direct role: owner[0..1], opposite role: feature[0..1]):
 *    MFeature getFeature();
 *    void setFeature(MFeature __arg);
 *
 * Bag role (e.g.: direct role: owner[0..1], opposite role: feature[*]):
 *    Set getFeatures();
 *    void setFeatures(Set __arg);
 *    void addFeature(MFeature __arg);
 *    void removeFeature(MFeature __arg);
 *
 * List role (e.g.: direct role: owner[0..1], opposite role: feature[*]{ordered}):
 *    List getFeatures();
 *    MFeature getFeature(int __pos);
 *    void setFeatures(List __arg);
 *    void setFeature(int __pos, MFeature __arg);
 *    void addFeature(MFeature __arg);
 *    void addFeature(int __pos, MFeature __arg);
 *    void removeFeature(MFeature __arg);
 *    void removeFeature(int __pos);
 *
 * More detailed information can be found at the CCM Tools project web site,
 * http://ccmtools.sourceforge.net/.
 */

package ccmtools.Metamodel.BaseIDL;

