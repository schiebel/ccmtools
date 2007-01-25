/* CCM Tools : OCL helpers
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003, 2004 Salomon Automation
 *
 * $Id: OclElementCreator.java,v 1.1 2004/07/20 16:21:23 teiniker Exp $
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
import ccmtools.OCL.parser.OclParsetreeCreator;


/**
 * The interface for creating any OCL elements.
 *
 * @author Robert Lechner
 */
public interface OclElementCreator extends OclParsetreeCreator
{
    public OclVoid createTypeVoid();
    public OclUser createTypeUser( String name );
    public OclBoolean createTypeBoolean();
    public OclReal createTypeReal();
    public OclInteger createTypeInteger();
    public OclString createTypeString();
    public OclEnumeration createTypeEnumeration();
    public OclSet createTypeSet( OclType type );
    public OclBag createTypeBag( OclType type );
    public OclSequence createTypeSequence( OclType type );
    public OclCollection createTypeCollection( OclType type );
}
