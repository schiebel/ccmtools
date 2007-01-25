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

import java.util.Set;

import ccmtools.Metamodel.BaseIDL.MContainer;
import ccmtools.Metamodel.BaseIDL.MDefinitionKind;
import ccmtools.Metamodel.BaseIDL.MInterfaceDef;

public class MSupportsDefImpl
    implements MSupportsDef
{
    private final static MDefinitionKind definitionKind =
        MDefinitionKind.DK_SUPPORTS;

    private String absoluteName;
    private String identifier;
    private String repositoryId;
    private String version;
    private String sourceFile;

    private MInterfaceDef Supports_;
    private MComponentDef Component_;
    private MHomeDef Home_;
    private MContainer Contains;

    public MSupportsDefImpl()
    {
        Supports_ = null;
        Component_ = null;
        Home_ = null;
        Contains = null;
        sourceFile = "";
    }

    // override toString()
    public String toString()
    {
    		return "MSupportsDef: " + identifier;
    }
    // required by MContained
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

    //----------------------------------------------------------------
    // implementation of navigation
    //----------------------------------------------------------------

    // association: direct role: [*] --> opposite role: supports[1]
    public MInterfaceDef getSupports()           {return Supports_;}
    public void setSupports(MInterfaceDef __arg) {Supports_ = __arg;}

    // association: direct role: facet[*] <-> opposite role: component[1]
    public MComponentDef getComponent()           {return Component_;}
    public void setComponent(MComponentDef __arg) {Component_ = __arg;}

    // association: direct role: facet[*] <-> opposite role: home[1]
    public MHomeDef getHome()                   {return Home_;}
    public void setHome(MHomeDef __arg)         {Home_ = __arg;}

    // association: direct role: contants[*] <-> opposite role: definedIn[0..1]
    public MContainer getDefinedIn()            {return Contains;}
    public void setDefinedIn(MContainer __arg)  {Contains = __arg;}
}

