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

package ccmtools.Metamodel.BaseIDL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.omg.CORBA.TypeCode;

public class MValueDefImpl
    implements MValueDef
{
    private final static MDefinitionKind definitionKind =
        MDefinitionKind.DK_VALUE;

    private String absoluteName;
    private String identifier;
    private String repositoryId;
    private String version;
    private String sourceFile;

    private boolean isAbstract;
    private boolean isCustom;
    private boolean isTruncatable;

    private TypeCode TypeCode;

    private MContainer Contains;
    private List ContainsList;
    private List AbstractDerivedFromList;
    private MValueDef ValueDerivedFrom;
    private MInterfaceDef interfaceDef;

    public MValueDefImpl()
    {
        isAbstract = false;
        isCustom = false;
        isTruncatable = false;
        Contains = null;
	ContainsList = new ArrayList();
	AbstractDerivedFromList = new ArrayList();
        ValueDerivedFrom = null;
        interfaceDef = null;
        sourceFile = "";
    }

    // override toString()
    public String toString()
    {
	return "MValueDef: "+ identifier;
    }
    // required by both MContained and MIDLType
    public String toString(Set been_there)
    		{ return toString(); }

    //----------------------------------------------------------------
    // implementation of attribute access
    //----------------------------------------------------------------

    // read-only attribute definitionKind:ccmtools.Metamodel.BaseIDL.MDefinitionKind
    public MDefinitionKind getDefinitionKind()  {return definitionKind;}

    // attribute absoluteName:String
    public String getAbsoluteName()             {return absoluteName;}
    public void setAbsoluteName(String __arg)   {absoluteName = __arg;}

    // attribute identifier:String
    public String getIdentifier()               {return identifier;}
    public void setIdentifier(String __arg)     {identifier = __arg;}

    // attribute repositoryId:String
    public String getRepositoryId()             {return repositoryId;}
    public void setRepositoryId(String __arg)   {repositoryId = __arg;}

    // attribute version:String
    public String getVersion()                  {return version;}
    public void setVersion(String __arg)        {version = __arg;}

    // attribute sourceFile:String
    public String getSourceFile()               {return sourceFile;}
    public void setSourceFile(String __arg)     {sourceFile = __arg;}

    // attribute isAbstract:boolean
    public boolean isAbstract()                 {return isAbstract;}
    public void setAbstract(boolean __arg)      {isAbstract = __arg;}

    // attribute isCustom:boolean
    public boolean isCustom()                   {return isCustom;}
    public void setCustom(boolean __arg)        {isCustom = __arg;}

    // attribute isTruncatable:boolean
    public boolean isTruncatable()              {return isTruncatable;}
    public void setTruncatable(boolean __arg)   {isTruncatable = __arg;}

    // attribute typeCode:TypeCode
    public TypeCode getTypeCode()               {return TypeCode;}
    public void setTypeCode(TypeCode __arg)     {TypeCode = __arg;}

    //----------------------------------------------------------------
    // implementation of navigation
    //----------------------------------------------------------------

    // association: direct role: contants[*] <-> opposite role: definedIn[0..1]
    public MContainer getDefinedIn()            {return Contains;}
    public void setDefinedIn(MContainer __arg)  {Contains = __arg;}

    // assocation: direct role: definedIn[0..1] <-> oposide role: contents[*]
    public List getContentss()                   {return ContainsList;}
    public void setContentss(List __arg)         {ContainsList = new ArrayList(__arg);}
    public void addContents(MContained __arg)    {ContainsList.add(__arg);}
    public void removeContents(MContained __arg) {ContainsList.remove(__arg);}

    // association: direct role: [*] --> opposite role: abstractBase[*]
    public List getAbstractBases()                  {return AbstractDerivedFromList;}
    public void setAbstractBases(List __arg)        {AbstractDerivedFromList = new ArrayList(__arg);}
    public void addAbstractBase(MValueDef __arg)    {AbstractDerivedFromList.add(__arg);}
    public void removeAbstractBase(MValueDef __arg) {AbstractDerivedFromList.remove(__arg);}

    // association: direct role: [*] --> opposite role: base[0..1]
    public MValueDef getBase()                  {return ValueDerivedFrom;}
    public void setBase(MValueDef __arg)        {ValueDerivedFrom = __arg ;}

    // association: direct role: [*] --> opposite role: interfaceDef[0..1]
    public MInterfaceDef getInterfaceDef()             {return interfaceDef;}
    public void setInterfaceDef(MInterfaceDef __arg)   {interfaceDef = __arg ;}

    //----------------------------------------------------------------
    // implementation of operations
    //----------------------------------------------------------------

    public List getFilteredContents(MDefinitionKind limitToType,
                                    boolean includeInherited)
    {
	List result = new ArrayList();
        for (Iterator i = ContainsList.iterator(); i.hasNext(); ) {
            MContained element = (MContained) i.next();
            MDefinitionKind dk = element.getDefinitionKind();
            if ((dk == limitToType) || (includeInherited &&
                (((limitToType == MDefinitionKind.DK_TYPEDEF) &&
                  ((dk == MDefinitionKind.DK_ALIAS) ||
                   (dk == MDefinitionKind.DK_ENUM) ||
                   (dk == MDefinitionKind.DK_STRUCT) ||
                   (dk == MDefinitionKind.DK_UNION) ||
                   (dk == MDefinitionKind.DK_VALUEBOX))) ||
                 ((limitToType == MDefinitionKind.DK_INTERFACE) &&
                  ((dk == MDefinitionKind.DK_COMPONENT) ||
                   (dk == MDefinitionKind.DK_HOME))) ||
                 ((limitToType == MDefinitionKind.DK_OPERATION) &&
                  ((dk == MDefinitionKind.DK_FACTORY) ||
                   (dk == MDefinitionKind.DK_FINDER))) ||
                 ((limitToType == MDefinitionKind.DK_EVENTPORT) &&
                  ((dk == MDefinitionKind.DK_CONSUMES) ||
                   (dk == MDefinitionKind.DK_EMITS) ||
                   (dk == MDefinitionKind.DK_PUBLISHES))) ||
                 ((limitToType == MDefinitionKind.DK_VALUE) &&
                  (dk == MDefinitionKind.DK_EVENT)))))
                result.add(element);
        }
        return result;
    }

    // a helper function for the lookupName function below.
    private MContainer checkContainer(MContained elem,
                                      boolean excludeInherited)
    {
        if ((elem instanceof MContainer) &&
            ((! excludeInherited) ||
             ((! ((elem instanceof MModuleDef) ||
                  (elem instanceof MInterfaceDef) ||
                  (elem instanceof MValueDef)))))) {
            return (MContainer) elem;
        }

        return null;
    }

    /**
     * Look up a descendant node of this node using the given name, using the
     * entire subgraph of the current element as a search space.
     *
     * @param searchName a string containing the name (identifier) of the
     *        desired metamodel element.
     * @return the element specified, or null if no such element was located.
     */
    public MContained lookup(String searchName)
    {
        return lookupName(searchName, 4294967296L, (MDefinitionKind) null, false);
    }

    /**
     * Look up a descendant node of this node using the given name, search
     * depth, and type limits.
     *
     * @param searchName a string containing the name (identifier) of the
     *        desired metamodel element.
     * @param levelsToSearch the number of levels in the metamodel graph to
     *        search. If this is negative the function will return immediately.
     * @param limitToType the desired type of the object. This can speed up the
     *        search by excluding checks for irrelevant element types.
     * @param excludeInherited will only search for elements contained in
     *        elements of the current node type.
     * @return the element specified, or null if no such element was located.
     */
    public MContained lookupName(String searchName, long levelsToSearch,
                                 MDefinitionKind limitToType,
                                 boolean excludeInherited)
    {
        if (levelsToSearch < 0) { return null; }

        MContained elem;
        Iterator it = ContainsList.iterator();

        while (it.hasNext()) {
            elem = (MContained) it.next();

            if ((limitToType == null)
                || (elem.getDefinitionKind() == limitToType)) {

                if (searchName.equals(elem.getIdentifier()))
                    return elem;

                MContained subelem = null;
                MContainer container = checkContainer(elem, excludeInherited);

                if (container != null) {
                    subelem = container.lookupName(searchName,
                                                   levelsToSearch - 1,
                                                   limitToType,
                                                   excludeInherited);
                    if (subelem != null)
                        return subelem;
                }
            }
        }

	return null;
    }
}

