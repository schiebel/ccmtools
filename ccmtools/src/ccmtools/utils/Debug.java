/* CCM Tools : Java Utilities
 * Egon Teiniker <egon.teiniker@tugraz.at>
 * copyright (c) 2002, 2003 Salomon Automation
 *
 * $Id: Debug.java,v 1.1 2004/07/20 16:21:24 teiniker Exp $
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

import java.io.PrintStream;


/**
 * Class that handles debug output. Messages are printed out
 * depending on the debugLevel value.
 * 
 * 
 */
public class Debug
{
    private static int debugLevel;
    private static PrintStream outputStream = System.out;
    
    public static final int NONE      = 0x00;
    public static final int ALL       = 0xff;
    public static final int METHODS   = 0x02;	
    public static final int VALUES    = 0x04;	
    public static final int COMMENTS  = 0x08;	


    public static void setDebugLevel(int level)
    {
	debugLevel = level;
    }


    public static int getDebugLevel()
    {
	return debugLevel;
    }

    public static void setOutputStream(PrintStream o)
    {
	outputStream = o;
    }

    public static PrintStream getOutputStream()
    {
	return outputStream;
    }

    public static void println(int level, String s) {
	if((debugLevel & level) != 0) 
	    outputStream.println(s);
    }


    public static void print(int level, String s) {
	if((debugLevel & level) != 0) 
	    outputStream.print(s);
    }
}

