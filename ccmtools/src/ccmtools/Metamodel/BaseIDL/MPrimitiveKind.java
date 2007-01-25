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

public class MPrimitiveKind
{
    private final static String[] Labels = {"",
                                            "PK_ANY",
                                            "PK_BOOLEAN",
                                            "PK_CHAR",
                                            "PK_DOUBLE",
                                            "PK_FIXED",
                                            "PK_FLOAT",
                                            "PK_LONG",
                                            "PK_LONGDOUBLE",
                                            "PK_LONGLONG",
                                            "PK_NULL",
                                            "PK_OBJREF",
                                            "PK_OCTET",
                                            "PK_PRINCIPAL",
                                            "PK_SHORT",
                                            "PK_STRING",
                                            "PK_TYPECODE",
                                            "PK_ULONG",
                                            "PK_ULONGLONG",
                                            "PK_USHORT",
                                            "PK_VALUEBASE",
                                            "PK_VOID",
                                            "PK_WCHAR",
                                            "PK_WSTRING"};

    // Typesafe enum pattern (Effective Java Item 21)
    private final int primitiveKind_;

    private MPrimitiveKind(int pk)
    {
	primitiveKind_ = pk;
    }

    public String toString()
    {
	// TODO map int values to strings
	return Labels[primitiveKind_];
    }

    public final static String[] getLabels()
    {
        return Labels;
    }

    // <<enumeration>>
    public static final MPrimitiveKind PK_ANY        = new MPrimitiveKind(1);
    public static final MPrimitiveKind PK_BOOLEAN    = new MPrimitiveKind(2);
    public static final MPrimitiveKind PK_CHAR       = new MPrimitiveKind(3);
    public static final MPrimitiveKind PK_DOUBLE     = new MPrimitiveKind(4);
    public static final MPrimitiveKind PK_FIXED      = new MPrimitiveKind(5);
    public static final MPrimitiveKind PK_FLOAT      = new MPrimitiveKind(6);
    public static final MPrimitiveKind PK_LONG       = new MPrimitiveKind(7);
    public static final MPrimitiveKind PK_LONGDOUBLE = new MPrimitiveKind(8);
    public static final MPrimitiveKind PK_LONGLONG   = new MPrimitiveKind(9);
    public static final MPrimitiveKind PK_NULL       = new MPrimitiveKind(10);
    public static final MPrimitiveKind PK_OBJREF     = new MPrimitiveKind(11);
    public static final MPrimitiveKind PK_OCTET      = new MPrimitiveKind(12);
    public static final MPrimitiveKind PK_PRINCIPAL  = new MPrimitiveKind(13);
    public static final MPrimitiveKind PK_SHORT      = new MPrimitiveKind(14);
    public static final MPrimitiveKind PK_STRING     = new MPrimitiveKind(15);
    public static final MPrimitiveKind PK_TYPECODE   = new MPrimitiveKind(16);
    public static final MPrimitiveKind PK_ULONG      = new MPrimitiveKind(17);
    public static final MPrimitiveKind PK_ULONGLONG  = new MPrimitiveKind(18);
    public static final MPrimitiveKind PK_USHORT     = new MPrimitiveKind(19);
    public static final MPrimitiveKind PK_VALUEBASE  = new MPrimitiveKind(20);
    public static final MPrimitiveKind PK_VOID       = new MPrimitiveKind(21);
    public static final MPrimitiveKind PK_WCHAR      = new MPrimitiveKind(22);
    public static final MPrimitiveKind PK_WSTRING    = new MPrimitiveKind(23);
}

