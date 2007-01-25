/* UML to IDL/OCL converter
 *
 * 2004 by Robert Lechner (rlechner@gmx.at)
 *
 * $Id: UmlMultiplicity.java,v 1.2 2005/02/21 10:58:19 rlechner Exp $
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

package uml2idl;

import org.xml.sax.Attributes;
import uml_parser.uml.MMultiplicity_range;
import uml_parser.uml.MMultiplicityRange;


/**
Multiplicity of UML elements. <br>Children:
<ul>
<li>{@link uml_parser.uml.MMultiplicity_range} == {@link UmlMultiplicityRange}</li>
</ul>

@author Robert Lechner (rlechner@gmx.at)
@version January 2004
*/
class UmlMultiplicity extends uml_parser.uml.MMultiplicity implements Worker
{
    private String id_;
    private UmlMultiplicityRange range_;


    UmlMultiplicity( Attributes attrs )
    {
        super(attrs);
        id_ = xmi_id_;
        if( id_==null )
        {
            id_ = Main.makeId();
        }
    }


    /**
     * Default range. See {@link UmlMultiplicityRange#UmlMultiplicityRange()}.
     */
    UmlMultiplicity()
    {
        super(null);
        id_ = Main.makeId();
        range_ = new UmlMultiplicityRange();
    }


	public String getName()
	{
	    return null;
	}


    public String getId()
    {
        return id_;
    }


	public void collectWorkers( java.util.HashMap map )
	{
	    map.put(id_, this);
	    int s = size();
	    for( int index=0; index<s; index++ )
	    {
	        Object obj = get(index);
	        if( obj instanceof Worker )
	        {
	            ((Worker)obj).collectWorkers(map);
	        }
	        Main.logChild("UmlMultiplicity",obj);
	    }
	    if( range_==null )
	    {
	        setRange();
	    }
	    range_.collectWorkers(map);
	}


	private void setRange()
	{
        java.util.Vector v1 = findChildren(MMultiplicity_range.xmlName__);
        if( v1.size()>0 )
        {
            java.util.Vector v2 = ((MMultiplicity_range)v1.get(0)).findChildren(MMultiplicityRange.xmlName__);
            if( v2.size()>0 )
            {
                range_ = (UmlMultiplicityRange)v2.get(0);
                String rangeLower = range_.getLower();
                if( rangeLower.equals("-1") )
                {
                    range_ = new UmlMultiplicityRange();
                }
            }
        }
        if( range_==null )
        {
            range_ = new UmlMultiplicityRange();
        }
	}


	public void makeConnections( Main main, Worker parent )
	{
	    // nothing to do
	}


    public String getIdlCode( Main main, String prefix, StringBuffer includeStatements )
    {
        return "";
    }


    public String getOclCode( Main main )
    {
        return "";
    }


    public String getLower()
    {
        if( range_==null )
        {
	        setRange();
        }
        return range_.getLower();
    }


    public String getUpper()
    {
        if( range_==null )
        {
	        setRange();
        }
        return range_.getUpper();
    }


	public int createDependencyOrder( int number, Main main )
	{
	    return number;
	}

	public int getDependencyNumber()
	{
	    return 0;
	}
}
