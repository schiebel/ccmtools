/* CCM Tools : OCL parser
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003 Salomon Automation
 *
 * $Id: Main.java,v 1.1 2004/07/20 16:21:23 teiniker Exp $
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

package ccmtools.OCL.utils;

import oclmetamodel.*;
import ccmtools.OCL.parser.*;
import java.io.*;


/**
 * Test program for the OCL parser.
 *
 * @author Robert Lechner
 */
public class Main
{
	public static void main( String[] args )
	{
        try
        {
    	    OclElementCreator creator = Factory.getElementCreator();
    	    OclNormalization normalization = new OclNormalization(creator);
    	    for( int index=0; index<args.length; index++ )
    	    {
	            String name = args[index];
	            System.out.println("OCL file: "+name);
	            MFile file = OclParser.parseFile( name, creator );
	            OclXmlWriter writer = new OclXmlWriter( new FileWriter( name+".xml" ) );
	            writer.write(file);
	            writer.close();
	            file = normalization.normalize(file);
	            writer = new OclXmlWriter( new FileWriter( name+".norm.xml" ) );
	            writer.write(file);
	            writer.close();
    	    }
    	    if( creator instanceof OclCreatorImp )
    	    {
    	        ((OclCreatorImp)creator).cleanUp();
    	    }
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
	}
}
