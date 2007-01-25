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

public class MDefinitionKind
{
    private final static String[] Labels = { "",
                                             "DK_ALIAS",
                                             "DK_ALL",
                                             "DK_ARRAY",
                                             "DK_ATTRIBUTE",
                                             "DK_COMPONENT",
                                             "DK_CONSTANT",
                                             "DK_CONSUMES",
                                             "DK_EMITS",
                                             "DK_ENUM",
                                             "DK_EVENT",
                                             "DK_EVENTPORT",
                                             "DK_EXCEPTION",
                                             "DK_FACTORY",
                                             "DK_FIELD",
                                             "DK_FINDER",
                                             "DK_FIXED",
                                             "DK_HOME",
                                             "DK_INTERFACE",
                                             "DK_MODULE",
                                             "DK_NONE",
                                             "DK_OPERATION",
                                             "DK_PARAMETER",
                                             "DK_PRIMITIVE",
                                             "DK_PROVIDES",
                                             "DK_PUBLISHES",
                                             "DK_REPOSITORY",
                                             "DK_SEQUENCE",
                                             "DK_STRING",
                                             "DK_STRUCT",
                                             "DK_SUPPORTS",
                                             "DK_TYPEDEF",
                                             "DK_UNION",
                                             "DK_UNIONFIELD",
                                             "DK_USES",
                                             "DK_VALUE",
                                             "DK_VALUEBOX",
                                             "DK_VALUEMEMBER",
                                             "DK_WSTRING"};

    // Typesafe enum pattern (Item 21 from _Effective Java_)
    private final int definitionKind_;
    private MDefinitionKind(int pk)
    {
	definitionKind_ = pk;
    }

    public String toString()
    {
	return Labels[definitionKind_];
    }

    // <<enumeration>>
    public static final MDefinitionKind DK_ALIAS       = new MDefinitionKind(1);
    public static final MDefinitionKind DK_ALL         = new MDefinitionKind(2);
    public static final MDefinitionKind DK_ARRAY       = new MDefinitionKind(3);
    public static final MDefinitionKind DK_ATTRIBUTE   = new MDefinitionKind(4);
    public static final MDefinitionKind DK_COMPONENT   = new MDefinitionKind(5);
    public static final MDefinitionKind DK_CONSTANT    = new MDefinitionKind(6);
    public static final MDefinitionKind DK_CONSUMES    = new MDefinitionKind(7);
    public static final MDefinitionKind DK_EMITS       = new MDefinitionKind(8);
    public static final MDefinitionKind DK_ENUM        = new MDefinitionKind(9);
    public static final MDefinitionKind DK_EVENT       = new MDefinitionKind(10);
    public static final MDefinitionKind DK_EVENTPORT   = new MDefinitionKind(11);
    public static final MDefinitionKind DK_EXCEPTION   = new MDefinitionKind(12);
    public static final MDefinitionKind DK_FACTORY     = new MDefinitionKind(13);
    public static final MDefinitionKind DK_FIELD       = new MDefinitionKind(14);
    public static final MDefinitionKind DK_FINDER      = new MDefinitionKind(15);
    public static final MDefinitionKind DK_FIXED       = new MDefinitionKind(16);
    public static final MDefinitionKind DK_HOME        = new MDefinitionKind(17);
    public static final MDefinitionKind DK_INTERFACE   = new MDefinitionKind(18);
    public static final MDefinitionKind DK_MODULE      = new MDefinitionKind(19);
    public static final MDefinitionKind DK_NONE        = new MDefinitionKind(20);
    public static final MDefinitionKind DK_OPERATION   = new MDefinitionKind(21);
    public static final MDefinitionKind DK_PARAMETER   = new MDefinitionKind(22);
    public static final MDefinitionKind DK_PRIMITIVE   = new MDefinitionKind(23);
    public static final MDefinitionKind DK_PROVIDES    = new MDefinitionKind(24);
    public static final MDefinitionKind DK_PUBLISHES   = new MDefinitionKind(25);
    public static final MDefinitionKind DK_REPOSITORY  = new MDefinitionKind(26);
    public static final MDefinitionKind DK_SEQUENCE    = new MDefinitionKind(27);
    public static final MDefinitionKind DK_STRING      = new MDefinitionKind(28);
    public static final MDefinitionKind DK_STRUCT      = new MDefinitionKind(29);
    public static final MDefinitionKind DK_SUPPORTS    = new MDefinitionKind(30);
    public static final MDefinitionKind DK_TYPEDEF     = new MDefinitionKind(31);
    public static final MDefinitionKind DK_UNION       = new MDefinitionKind(32);
    public static final MDefinitionKind DK_UNIONFIELD  = new MDefinitionKind(33);
    public static final MDefinitionKind DK_USES        = new MDefinitionKind(34);
    public static final MDefinitionKind DK_VALUE       = new MDefinitionKind(35);
    public static final MDefinitionKind DK_VALUEBOX    = new MDefinitionKind(36);
    public static final MDefinitionKind DK_VALUEMEMBER = new MDefinitionKind(37);
    public static final MDefinitionKind DK_WSTRING     = new MDefinitionKind(38);
}
