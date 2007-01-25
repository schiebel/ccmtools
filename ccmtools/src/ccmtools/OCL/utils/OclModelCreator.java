/* CCM Tools : OCL helpers
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003, 2004 Salomon Automation
 *
 * $Id: OclModelCreator.java,v 1.1 2004/07/20 16:21:23 teiniker Exp $
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


/**
 * Baseclass for creators of an OCL model (using the NetBeans Metadata Repository).
 *
 * @see {@link OclMdrAdapter}
 *
 * @author Robert Lechner
 */
public abstract class OclModelCreator
{
    /**
     * Manages the connection to the NetBeans Metadata Repository (MDR).
     */
    private OclMdrAdapter metadataRepositoryAdapter_;


    /**
     * The OCL metamodel.
     */
    protected OclMetamodelPackage metamodel_;


    /**
     * Creates the connection to the NetBeans Metadata Repository.
     * The name of the OCL metamodel is set to {@link OclMdrAdapter#DEFAULT_METAMODEL_NAME}.
     *
     * @param mofFilename - the name of the MOF file (the OCL metamodel)
     *
     * @throws CreationFailedException  MDR error
     * @throws MalformedXMIException  XMI error
     * @throws IOException  file IO error
     * @throws NullPointerException  mofFilename or metamodel_ are null
     */
    protected OclModelCreator( String mofFilename )
     throws org.netbeans.api.mdr.CreationFailedException,
            javax.jmi.xmi.MalformedXMIException,
            java.io.IOException
    {
        metadataRepositoryAdapter_ = new OclMdrAdapter( mofFilename );
        init();
    }


    /**
     * Creates the connection to the NetBeans Metadata Repository.
     *
     * @param mofFilename  the name of the MOF file (the OCL metamodel)
     * @param metamodelname  the name of the OCL metamodel (the XMI package name)
     *
     * @throws CreationFailedException  MDR error
     * @throws MalformedXMIException  XMI error
     * @throws IOException  file IO error
     * @throws NullPointerException  mofFilename, metamodelname or metamodel_ are null
     */
    protected OclModelCreator( String mofFilename, String metamodelname )
     throws org.netbeans.api.mdr.CreationFailedException,
            javax.jmi.xmi.MalformedXMIException,
            java.io.IOException
    {
        metadataRepositoryAdapter_ = new OclMdrAdapter( mofFilename, metamodelname );
        init();
    }


    private void init()
    {
        metadataRepositoryAdapter_.getRepository().beginTrans(true);
        metamodel_ = metadataRepositoryAdapter_.getMetamodel();
        if( metamodel_==null )
        {
            throw new NullPointerException();
        }
    }


    /**
     * Call this function after the creation of the model.
     * After the call, this instance is invalid!
     */
    public void cleanUp()
    {
        metamodel_.refDelete();
        metadataRepositoryAdapter_.getRepository().endTrans();
        metamodel_ = null;
        metadataRepositoryAdapter_ = null;
    }


    protected void finalize()
    {
	if( metamodel_!=null )
	{
	    metamodel_.refDelete();
	    metamodel_ = null;
	}
	if( metadataRepositoryAdapter_!=null )
	{
	    metadataRepositoryAdapter_.getRepository().endTrans();
	    metadataRepositoryAdapter_ = null;
	}
    }

}
