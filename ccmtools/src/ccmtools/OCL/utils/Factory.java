/* CCM Tools : OCL helpers
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003, 2004 Salomon Automation
 *
 * $Id: Factory.java,v 1.2 2004/12/20 13:11:33 teiniker Exp $
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

import java.io.File;


/**
 * A factory for OCL helpers.
 *
 * @author Robert Lechner
 */
public class Factory
{
    /**
     * Returns the MOF file of the OCL metamodel.
     * The file has the (unix) filename '$CCMTOOLS_HOME/share/ccmtools/MDR/OCL.xml'.
     *
     * @throws IllegalStateException  the file doesn't exist
     */
    public static File getMofFile() throws IllegalStateException
    {
        String ccmHome = System.getProperty("CCMTOOLS_HOME");
        File modelFile = new File(ccmHome,"templates/MDR/OCL.xml");
        if( !modelFile.isFile() )
        {
            throw new IllegalStateException("cannot find OCL metamodel: "+modelFile);
        }
        return modelFile;
    }


    private static OclElementCreator creator_;

    /**
     * Returns an instance of {@link OclCreatorImp}.
     *
     * @throws IllegalStateException  the MOF file doesn't exist or a problem with the MDR
     */
    public static OclElementCreator getElementCreator() throws IllegalStateException
    {
        if( creator_==null )
        {
            File modelFile = getMofFile();
            String oclMetamodel = modelFile.toURI().toString();
            System.out.println("> load OCL metamodel from " + oclMetamodel);
            try
            {
                creator_ = new OclCreatorImp(oclMetamodel);
            }
            catch( Exception e )
            {
                e.printStackTrace();
                creator_ = null;
                throw new IllegalStateException("cannot create OCL parse tree creator");
            }
        }
        return creator_;
    }
}
