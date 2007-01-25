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

import java.util.Set;

public interface MContained
{
	String toString();
	String toString(Set been_there);
	
    // read-only attribute definitionKind:ccmtools.Metamodel.BaseIDL.MDefinitionKind
    MDefinitionKind getDefinitionKind();

    // attribute absoluteName:String
    String getAbsoluteName();
    void setAbsoluteName(String __arg);

    // attribute identifier:String
    String getIdentifier();
    void setIdentifier(String __arg);

    // attribute repositoryId:String
    String getRepositoryId();
    void setRepositoryId(String __arg);

    // attribute version:String
    String getVersion();
    void setVersion(String __arg);

    // attribute sourceFile:String
    String getSourceFile();
    void setSourceFile(String __arg);

    // association: direct role: contants[*] <-> opposite role: definedIn[0..1]
    MContainer getDefinedIn();
    void setDefinedIn(MContainer __arg);
}

