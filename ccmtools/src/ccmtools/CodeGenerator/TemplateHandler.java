/* CCM Tools : Code Generator Library
 * Leif Johnson <leif@ambient.2y.net>
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

package ccmtools.CodeGenerator;


/**
 * Template handler objects are capable of handling graph traversal events and
 * interfacing with a template set for providing code generation. They extend
 * the more general node handler interface by providing a set of functions for
 * dealing with a template set via a template manager.
 */
public interface TemplateHandler
    extends NodeHandler
{
    /**
     * Return a boolean indicating whether the given flag is set.
     *
     * @return the value of the current implementing object's flag.
     */
    boolean getFlag(int flag);

    /**
     * Set a flag on this handler object.
     *
     * @param flag the flag to set.
     */
    void setFlag(int flag);

    /**
     * Clear a flag on this handler object.
     *
     * @param flag the flag to set.
     */
    void clearFlag(int flag);
    
    /**
     * set the startup file for coda processing
     */
    void setCodaStartupFile(String file);

    /**
     * Get the template manager object responsible for handling this node
     * handler's templates.
     *
     * @return the current implementing object's template manager.
     */
    TemplateManager getTemplateManager();
    
    void close();
}

