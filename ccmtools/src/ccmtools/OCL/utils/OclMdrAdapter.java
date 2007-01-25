/* CCM Tools : OCL helpers
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003, 2004 Salomon Automation
 *
 * $Id: OclMdrAdapter.java,v 1.2 2004/08/28 09:17:25 teiniker Exp $
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

import java.io.IOException;
import java.util.Iterator;

import javax.jmi.model.ModelPackage;
import javax.jmi.model.MofPackage;
import javax.jmi.reflect.RefPackage;
import javax.jmi.xmi.MalformedXMIException;
import javax.jmi.xmi.XmiReader;

import oclmetamodel.OclMetamodelPackage;

import org.netbeans.api.mdr.CreationFailedException;
import org.netbeans.api.mdr.MDRManager;
import org.netbeans.api.mdr.MDRepository;
import org.openide.util.Lookup;


/**
 * Manages the connection to the NetBeans Metadata Repository (MDR).
 *
 * @see <a href="http://mdr.netbeans.org/index.html">NetBeans Metadata Repository</a> 
 *
 * @author Robert Lechner
 */
public final class OclMdrAdapter
{
    /**
     * The name of the MOF file.
     */
    private String mofFilename_;
    
    
    /**
     * The name of the OCL metamodel (XMI package name).
     */
    private String metamodelname_;


    /**
     * The NetBeans Metadata Repository.
     */
    private MDRepository repository_;


    /**
     * The OCL metamodel.
     */
    private OclMetamodelPackage oclMetamodelPackage_;
    
    
    private ModelPackage mofInstance_;  // ???  [rlechner]

    private static final String MOF_INSTANCE = "MOFInstance";
    private static final String METAMODEL_INSTANCE = "OCLModelInstance";
    
    
    /**
     * The default name of the OCL metamodel.
     */
    public static final String DEFAULT_METAMODEL_NAME = "OCL metamodel";
    

    /// debug output    
    private void println( String text )
    {
        System.out.println( text );
    }
    
    
    /**
     * Returns the NetBeans Metadata Repository (MDR).
     */
    public MDRepository getRepository()
    {
        return repository_;
    }


    /**
     * Returns the OCL metamodel.
     */
    public OclMetamodelPackage getMetamodel()
    {
        return oclMetamodelPackage_;
    }


    /**
     * Creates the connection to the NetBeans Metadata Repository.
     * The name of the OCL metamodel is set to {@link #DEFAULT_METAMODEL_NAME}.
     *
     * @param mofFilename  the name of the MOF file (the OCL metamodel)
     *
     * @throws CreationFailedException  MDR error
     * @throws MalformedXMIException  XMI error
     * @throws IOException  file IO error
     * @throws NullPointerException  mofFilename is null
     */    
    public OclMdrAdapter( String mofFilename )
     throws org.netbeans.api.mdr.CreationFailedException,
            javax.jmi.xmi.MalformedXMIException,
            java.io.IOException
    {
        if( mofFilename==null )
        {
            throw new NullPointerException();
        }
        mofFilename_ = mofFilename;
        metamodelname_ = DEFAULT_METAMODEL_NAME;
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
     * @throws NullPointerException  mofFilename or metamodelname are null
     */    
    public OclMdrAdapter( String mofFilename, String metamodelname )
     throws org.netbeans.api.mdr.CreationFailedException,
            javax.jmi.xmi.MalformedXMIException,
            java.io.IOException
    {
        if( mofFilename==null || metamodelname==null )
        {
            throw new NullPointerException();
        }
        mofFilename_ = mofFilename;
        metamodelname_ = metamodelname;
        init();
    }
    
    
    /**
     * Inits the repository and makes sure that MOF and UML models are present.
     *
     * @throws CreationFailedException  MDR error
     * @throws MalformedXMIException  XMI error
     * @throws IOException  file IO error
     */
    private void init()
     throws org.netbeans.api.mdr.CreationFailedException,
            javax.jmi.xmi.MalformedXMIException,
            java.io.IOException
    {
        Thread shutDownThread = new Thread(new Runnable () {
            public void run() {
                MDRManager.getDefault().shutdownAll();
            }
        });
        Runtime.getRuntime().addShutdownHook(shutDownThread);
        repository_ = MDRManager.getDefault().getDefaultRepository();
        mofInstance_ = (ModelPackage) repository_.getExtent(MOF_INSTANCE);  // ???  (rle)
        if( mofInstance_==null )
        {   // create MOF-instance in MDR if Metamodel does not exist
            mofInstance_ = (ModelPackage) repository_.createExtent(MOF_INSTANCE);
        }
        oclMetamodelPackage_ = (OclMetamodelPackage) repository_.getExtent(METAMODEL_INSTANCE);
        if( oclMetamodelPackage_==null )
        {   // create a METAMODEL instance in MDR if Metamodel does not exist
            MofPackage mofPackage = getMetaModelPackage();
            RefPackage refPkg = repository_.createExtent(METAMODEL_INSTANCE, mofPackage);
            oclMetamodelPackage_ = (OclMetamodelPackage) refPkg;
        }
    }


    private MofPackage getMetaModelPackage()
     throws org.netbeans.api.mdr.CreationFailedException,
            javax.jmi.xmi.MalformedXMIException,
            java.io.IOException
    {
        ModelPackage metaModelPackage = (ModelPackage) repository_.getExtent(metamodelname_);
        if (metaModelPackage == null) {
            metaModelPackage = (ModelPackage) repository_.createExtent(metamodelname_);
        }
        MofPackage result = getMetaModelPackage(metaModelPackage);
        if (result == null) {
            println("READING MODEL: " + mofFilename_);
            XmiReader reader = (XmiReader) Lookup.getDefault().lookup(XmiReader.class);
            reader.read(mofFilename_, metaModelPackage);            
            result = getMetaModelPackage(metaModelPackage);
        }
        return result;
    }


    private MofPackage getMetaModelPackage(ModelPackage mm)
    {
        for (Iterator it = mm.getMofPackage().refAllOfClass().iterator(); it.hasNext();) {
            MofPackage pkg = (MofPackage) it.next();
            println("PackageName:" + pkg.getName());
            if (pkg.getContainer() == null && metamodelname_.equals(pkg.getName())) {
                return pkg;
            }
        }
        return null;
    }
}
