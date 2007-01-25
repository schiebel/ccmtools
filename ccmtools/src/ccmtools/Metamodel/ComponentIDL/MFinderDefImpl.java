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

import ccmtools.Metamodel.BaseIDL.MContainer;
import ccmtools.Metamodel.BaseIDL.MDefinitionKind;
import ccmtools.Metamodel.BaseIDL.MExceptionDef;
import ccmtools.Metamodel.BaseIDL.MIDLType;
import ccmtools.Metamodel.BaseIDL.MParameterDef;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MFinderDefImpl
    implements MFinderDef
{
    private final static MDefinitionKind definitionKind =
        MDefinitionKind.DK_FINDER;

    private String contexts_;
    private String absoluteName;
    private String identifier;
    private String repositoryId;
    private String version;
    private String sourceFile;

    private boolean isOneway_;

    private MParameterDef parameters_;
    private MHomeDef Home_;
    private Set CanRaiseSet_;
    private List ParameterList_;
    private MContainer Contains;
    private MIDLType TypedBy_;

    public MFinderDefImpl()
    {
        isOneway_ = false;
        parameters_ = null;
        Home_ = null;
        CanRaiseSet_ = new HashSet();
        ParameterList_ = new ArrayList();
        Contains = null;
        TypedBy_ = null;
        sourceFile = "";
    }

    // override toString()
    public String toString()
    {
    		return "MFinderDef: "+ identifier;
    }
    // required by MContained
    public String toString(Set been_there)
    		{ return toString(); }


    //----------------------------------------------------------------
    // implementation of attribute access
    //----------------------------------------------------------------

    // read-only attribute definitionKind:ccmtools.Metamodel.BaseIDL.MDefinitionKind
    public MDefinitionKind getDefinitionKind()  {return definitionKind;}

    // attribute contexts:string
    public String getContexts()                 {return contexts_;}
    public void setContexts(String __arg)       {contexts_ = __arg;}

    // attribute isOneway:boolean
    public boolean isOneway()                   {return isOneway_;}
    public void setOneway(boolean __arg)        {isOneway_ = __arg;}

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

    //----------------------------------------------------------------
    // implementation of navigation
    //----------------------------------------------------------------

    // association: direct role: factory[*] <-> opposite role: home[1]
    public MHomeDef getHome()                   {return Home_;}
    public void setHome(MHomeDef __arg)         {Home_ = __arg;}

    // association: direct role: [*] --> opposite role: exceptionDef[*]
    public Set getExceptionDefs()                       {return CanRaiseSet_;}
    public void setExceptionDefs(Set __arg)             {CanRaiseSet_ = new HashSet(__arg);}
    public void addExceptionDef(MExceptionDef __arg)    {CanRaiseSet_.add(__arg);}
    public void removeExceptionDef(MExceptionDef __arg) {CanRaiseSet_.remove(__arg);}

    // aggregation: direct role: operation[0..1] <>- opposite role: parameter[*]
    public List getParameters()                      {return ParameterList_;}
    public void setParameters(List __arg)            {ParameterList_ = new ArrayList(__arg);}
    public void addParameter(MParameterDef __arg)    {ParameterList_.add(__arg);}
    public void removeParameter(MParameterDef __arg) {ParameterList_.remove(__arg);}

    // association: direct role: contants[*] <-> opposite role: definedIn[0..1]
    public MContainer getDefinedIn()            {return Contains;}
    public void setDefinedIn(MContainer __arg)  {Contains = __arg;}

    // association: direct role: [*] --> opposite role: idlType[1]
    public MIDLType getIdlType()                {return TypedBy_;}
    public void setIdlType(MIDLType __arg)      {TypedBy_ = __arg;}
}

