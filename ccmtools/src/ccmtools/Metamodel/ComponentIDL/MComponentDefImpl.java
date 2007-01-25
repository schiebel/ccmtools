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

package ccmtools.Metamodel.ComponentIDL;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.omg.CORBA.TypeCode;

import ccmtools.Metamodel.BaseIDL.MContained;
import ccmtools.Metamodel.BaseIDL.MContainer;
import ccmtools.Metamodel.BaseIDL.MDefinitionKind;
import ccmtools.Metamodel.BaseIDL.MInterfaceDef;
import ccmtools.Metamodel.BaseIDL.MModuleDef;
import ccmtools.Metamodel.BaseIDL.MValueDef;

public class MComponentDefImpl
    implements MComponentDef
{
    private final static MDefinitionKind definitionKind =
        MDefinitionKind.DK_COMPONENT;

    private String absoluteName;
    private String identifier;
    private String repositoryId;
    private String version;
    private String sourceFile;

    private boolean isAbstract;
    private boolean isLocal;

    private TypeCode typeCode_;
    private List HomeList_;
    private List FacetList_;
    private List ReceptacleList_;
    private List SupportsList_;
    private List EmitsList_;
    private List PublishesList_;
    private List ConsumesList_;
    private MContainer Contains;
    private List ContainsList;
    private List InterfaceDerivedFromList;

    public MComponentDefImpl()
    {
        isAbstract = false;
        isLocal = false;
        typeCode_ = null;
	HomeList_ = new ArrayList();
	FacetList_ = new ArrayList();
	ReceptacleList_ = new ArrayList();
	SupportsList_ = new ArrayList();
	EmitsList_ = new ArrayList();
	PublishesList_ = new ArrayList();
	ConsumesList_ = new ArrayList();
        Contains = null;
	ContainsList = new ArrayList();
	InterfaceDerivedFromList = new ArrayList();
        sourceFile = "";
    }

    // override toString()
    public String toString()
    {
	String tmp = "MComponentDef: "+ identifier;
	if (ContainsList.size() > 0) tmp  += " " + ContainsList;
        if (InterfaceDerivedFromList.size() > 0)
            tmp += " (bases: " + InterfaceDerivedFromList + ")";
	return tmp;
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

    // attribute isLocal:boolean
    public boolean isLocal()                    {return isLocal;}
    public void setLocal(boolean __arg)         {isLocal = __arg;}

    // attribute typeCode:TypeCode
    public TypeCode getTypeCode()               {return typeCode_;}
    public void setTypeCode(TypeCode __arg)     {typeCode_ = __arg;}

    //----------------------------------------------------------------
    // implementation of navigation
    //----------------------------------------------------------------

    // composition: direct role: component[1] <-> opposite role: facet[*]
    public List getFacets()                     {return FacetList_;}
    public void setFacets(List __arg)           {FacetList_ = new ArrayList(__arg);}
    public void addFacet(MProvidesDef __arg)    {FacetList_.add(__arg);}
    public void removeFacet(MProvidesDef __arg) {FacetList_.remove(__arg);}

    // composition: direct role: component[1] <-> opposite role: receptacle[*]
    public List getReceptacles()                 {return ReceptacleList_;}
    public void setReceptacles(List __arg)       {ReceptacleList_ = new ArrayList(__arg);}
    public void addReceptacle(MUsesDef __arg)    {ReceptacleList_.add(__arg);}
    public void removeReceptacle(MUsesDef __arg) {ReceptacleList_.remove(__arg);}

    // association: direct role: component[*] --> opposite role: supports[*]
    public List getSupportss()                     {return SupportsList_;}
    public void setSupportss(List __arg)           {SupportsList_ = new ArrayList(__arg);}
    public void addSupports(MSupportsDef __arg)    {SupportsList_.add(__arg);}
    public void removeSupports(MSupportsDef __arg) {SupportsList_.remove(__arg);}

    // composition: direct role: component[1] --> opposite role: emits[*]
    public List getEmitss()                     {return EmitsList_;}
    public void setEmitss(List __arg)           {EmitsList_ = new ArrayList(__arg);}
    public void addEmits(MEmitsDef __arg)       {EmitsList_.add(__arg);}
    public void removeEmits(MEmitsDef __arg)    {EmitsList_.remove(__arg);}

    // composition: direct role: component[1] --> opposite role: publishes[*]
    public List getPublishess()                      {return PublishesList_;}
    public void setPublishess(List __arg)            {PublishesList_ = new ArrayList(__arg);}
    public void addPublishes(MPublishesDef __arg)    {PublishesList_.add(__arg);}
    public void removePublishes(MPublishesDef __arg) {PublishesList_.remove(__arg);}

     // composition: direct role: component[1] --> opposite role: consumes[*]
    public List getConsumess()                     {return ConsumesList_;}
    public void setConsumess(List __arg)           {ConsumesList_ = new ArrayList(__arg);}
    public void addConsumes(MConsumesDef __arg)    {ConsumesList_.add(__arg);}
    public void removeConsumes(MConsumesDef __arg) {ConsumesList_.remove(__arg);}

    // association: direct role: contants[*] <-> opposite role: definedIn[0..1]
    public MContainer getDefinedIn()            {return Contains;}
    public void setDefinedIn(MContainer __arg)  {Contains = __arg;}

    // assocation: direct role: definedIn[0..1] <-> oposide role: contents[*]
    public List getContentss()                   {return ContainsList;}
    public void setContentss(List __arg)         {ContainsList = new ArrayList(__arg);}
    public void addContents(MContained __arg)    {ContainsList.add(__arg);}
    public void removeContents(MContained __arg) {ContainsList.remove(__arg);}

    // association: direct role: [*] --> opposite role: base[*]
    public List getBases()                      {return InterfaceDerivedFromList;}
    public void setBases(List __arg)            {InterfaceDerivedFromList = new ArrayList(__arg);}
    public void addBase(MInterfaceDef __arg)    {InterfaceDerivedFromList.add(__arg);}
    public void removeBase(MInterfaceDef __arg) {InterfaceDerivedFromList.remove(__arg);}

    // we have extended the CCM MOF specification to ease the navigation from
    // component to its home(s) ; our implementation generally encourages 1-1
    // relations between homes and components but allows *-0..1 relations.
    //
    // association: direct role: component[1] <--> oposite role: is managed by [*]
    public List getHomes()                      {return HomeList_;}
    public void setHomes(List __arg)            {HomeList_ = new ArrayList(__arg);}
    public void addHome(MHomeDef __arg)         {HomeList_.add(__arg);}
    public void removeHome(MHomeDef __arg)      {HomeList_.remove(__arg);}

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

    // a helper function for the lookupName function below.
    private MContained searchBin(List bin, String searchName,
                                 long levelsToSearch,
                                 MDefinitionKind limitToType,
                                 boolean excludeInherited)
    {
        MContained elem;
        Iterator it = bin.iterator();

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

        MContained elem = null;

        elem = searchBin(ContainsList, searchName, levelsToSearch,limitToType, excludeInherited);
        if (elem != null) return elem;
        elem = searchBin(FacetList_, searchName, levelsToSearch, limitToType, excludeInherited);
        if (elem != null) return elem;
        elem = searchBin(ReceptacleList_, searchName, levelsToSearch, limitToType, excludeInherited);
        if (elem != null) return elem;
        elem = searchBin(SupportsList_, searchName, levelsToSearch, limitToType, excludeInherited);
        if (elem != null) return elem;
        elem = searchBin(EmitsList_, searchName, levelsToSearch, limitToType, excludeInherited);
        if (elem != null) return elem;
        elem = searchBin(PublishesList_, searchName, levelsToSearch, limitToType, excludeInherited);
        if (elem != null) return elem;
        elem = searchBin(ConsumesList_, searchName, levelsToSearch, limitToType, excludeInherited);

        if (elem != null) return elem;

	return null;
    }
}

