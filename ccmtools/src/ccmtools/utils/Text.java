/* CCM Tools : Utilities
 * Egon Teiniker <egon.teiniker@tugraz.at>
 * copyright (c) 2002, 2003, 2004 Salomon Automation
 *
 * $Id: Text.java,v 1.2 2004/11/06 22:33:05 teiniker Exp $
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

package ccmtools.utils;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/***
 * This class collects some methods that can help to handle Strings and
 * Lists of Strings.
 *
 ***/
public class Text
{
    /**
     * Defines the number of spaces used by a tab.
     */
    protected static final String TAB = "    ";


    /**
     * Create n tabs (in form of spaces) and write them into a string.
     * 
     * @param n the number of tabs, the resulting string should contain.
     * @return A string containing n*TAB. 
     */
    public static String insertTab(int n)
    {
	StringBuffer buffer = new StringBuffer();
	for(int i=0; i<n; i++) {
	    buffer.append(TAB);
	}
	return buffer.toString();
    }


    /**
     * Join a collection of strings (a, b, c, ..., z) by combining each element
     * with the given separator A. The resulting string will be of the form
     * aAbAcA...Az.
     *
     * @param sep the string to use as a separator.
     * @param parts a collection of strings to join.
     * @return a string containing the joined parts separated by the given
     *         separator.
     */
    public static String join(String separator, Collection parts)
    {
        if (parts != null) {
            if (parts.size() > 1) {
                StringBuffer ret = new StringBuffer("");
                for (Iterator i = parts.iterator(); i.hasNext(); ) {
                    String part = (String) i.next();
                    ret.append(part + (i.hasNext() ? separator : ""));
                }
                String s = ret.toString();
                return s;
//                ret = ret.reverse();
//                ret = new StringBuffer(ret.substring(separator.length()));
//                return ret.reverse().toString();
            }
            if (parts.size() == 1) return "" + parts.iterator().next();
        }
        return new String("");
    }


    public static String join(String separator, String func, Collection parts)
    {
        if (parts != null) {
            if (parts.size() > 1) {
                StringBuffer ret = new StringBuffer("");
                for (Iterator i = parts.iterator(); i.hasNext(); ) {
                    String part = (String) i.next();
                    ret.append( func + "(" + part + ")" + (i.hasNext() ? separator : ""));
                }
                String s = ret.toString();
                return s;
//                ret = ret.reverse();
//                ret = new StringBuffer(ret.substring(separator.length()));
//                return ret.reverse().toString();
            }
            if (parts.size() == 1) return func + "(" + parts.iterator().next() + ")";
        }
        return new String("");
    }

    /**
     * Slice a part of the given list. If start is negative, the function will
     * return the part of the collection that includes all but the last "start"
     * elements. Otherwise slice will return the subcollection that includes all
     * but the first "start" elements.
     *
     * @param parts the source list to slice.
     * @param start the portion of the list to remove.
     * @return a new sublist that includes only the desired sublist from the
     *         original parts.
     */
    public static List slice(List parts, int start)
    {
        if (start == 0) 
	    return parts;
        if (parts == null) 
	    return new ArrayList();
        int size = parts.size();
        if (size == 0) 
	    return new ArrayList();
        if ((start >= size) || (start <= -size)) 
	    return new ArrayList();
        if (start < 0) 
	    return parts.subList(0, size + start);
        return parts.subList(start, size);
    }


	/**
	 * @param string
	 * @param ret
	 * @return
	 */
	public static String join(String string, String[] ary) {
		String result = "";
		
		if (ary.length == 0)
			return "";
		
		else if (ary.length == 1)
			return ary[0];
		
		else {
			for ( int i=0; i < ary.length; ++i ) {
				if (ary[i].length()>0) {
					if (result.length()>0)
						result = result + string + ary[i];
					else
						result = ary[i];
				}
			}
		}
		return result;
	}


	/**
	 * @param string
	 * @param ret_map
	 * @return
	 */
	public static String join(String sep, Map map) {
		Collection values = map.values();
		String result = "";
		for ( Iterator i=values.iterator(); i.hasNext(); ) {
			String cur = (String) i.next();
			if (result.length() > 0)
				result = result + sep + cur;
			else
				result = cur;
		}
		return result;
	}
	
	public static String capitalize(String s) {
		char c = Character.toUpperCase(s.charAt(0));
        return c + s.substring(1);
	}
	
}
