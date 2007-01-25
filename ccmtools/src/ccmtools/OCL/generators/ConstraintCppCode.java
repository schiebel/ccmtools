/* CCM Tools : OCL generators
 * Robert Lechner <rlechner@sbox.tugraz.at>
 * copyright (c) 2003 Salomon Automation
 *
 * $Id: ConstraintCppCode.java,v 1.1 2004/07/20 16:21:17 teiniker Exp $
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

package ccmtools.OCL.generators;


/**
 * The C++ code of one constraint.
 *
 * @author Robert Lechner
 * @version 0.1
 */
public class ConstraintCppCode extends ConstraintCode
{
    ConstraintCppCode()
    {
        super();
    }


    ConstraintCppCode( ConstraintCode ref )
    {
        super(ref);
    }


    protected String makeAnd( String param1, String param2 )
    {
        return "("+param1+")&&("+param2+")";
    }


    protected String makeOr( String param1, String param2 )
    {
        return "("+param1+")||("+param2+")";
    }
}
