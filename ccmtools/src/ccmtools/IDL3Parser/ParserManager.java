/* CCM Tools : IDL3 Parser
 * Edin Arnautovic <edin.arnautovic@salomon.at>
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

package ccmtools.IDL3Parser;

import ccmtools.Metamodel.BaseIDL.MContainer;

import antlr.RecognitionException;
import antlr.TokenStreamException;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ParserManager {
    private long debug;

    private IDL3SymbolTable symbolTable;

    private String originalFile; // the original file being parsed.
    private String sourceFile;   // the source of the current section of code.
    private List errors;

    /**
     * Create a new ParserManager class instance with no debug output enabled.
     */
    public ParserManager()
    {
        this.debug = 0;
        reset();
    }

    /**
     * Create a new ParserManager class instance using the given debug flags to
     * control debug output. The debug flags are passed directly on to the
     * underlying lexer and parser class instances.
     *
     * @param debug the flags to enable for debug output. Pass -1 to enable all
     *              output.
     */
    public ParserManager(long debug)
    {
        this.debug = debug;
        reset();
    }

    /**
     * Reset the parser manager. This clears out the symbol table.
     */
    public void reset()
    {
        symbolTable = new IDL3SymbolTable();
        errors = new ArrayList();
        originalFile = null;
        sourceFile = null;
    }

    /**
     * Get the current symbol table being maintained by the parser manager.
     *
     * @return the current symbol table, normally for debug output. Be careful;
     *         externally manipulating the symbol table could have severe side
     *         effects.
     */
    public IDL3SymbolTable getSymbolTable() { return symbolTable; }

    /**
     * Find out which file the parser manager started with.
     *
     * @return the name of the original file given to parse.
     */
    public String getOriginalFile() { return originalFile; }

    /**
     * Set the name of the file that the parser manager started with.
     *
     * @param f the name of the original file to be parsed.
     */
    public void setOriginalFile(String f)
    {
        if (originalFile == null) {
            File file = new File(f);
            try { originalFile = file.getCanonicalPath(); }
            catch (IOException e) { originalFile = f; }
        }
    }

    /**
     * Find out which file the parser is currently handling.
     *
     * @return the name of the file that originated the current code section.
     */
    public String getSourceFile() { return sourceFile; }

    /**
     * Set the name of the file that the parser is currently handling.
     *
     * @param f the name of the file that provided the current code section.
     */
    public void setSourceFile(String f)
    {
        File file = new File(f);
        try { sourceFile = file.getCanonicalPath(); }
        catch (IOException e) { sourceFile = f; }
    }

    /**
     * Parse an IDL3 file.
     *
     * @param filename the name of a file to open and parse.
     * @return an MContainer object that contains a metamodel corresponding to
     *         the declarations in the source IDL3 file.
     */
    public MContainer parseFile(String filename)
        throws RecognitionException, TokenStreamException
    {
	MContainer spec = null;
        IDL3Parser parser = null;
        IDL3Lexer lexer = null;
        DataInputStream stream = null;

        try {
            stream = new DataInputStream(new FileInputStream(filename));
        } catch (Exception e) {
            throw new RuntimeException(
                "Error opening input file '" + filename + "': " + e);
        }

	try {
            lexer = new IDL3Lexer(stream);
            lexer.setDebug(debug);
            lexer.setManager(this);
        } catch (Exception e) {
            throw new RuntimeException("Error creating lexer: " + e);
        }

        try {
            parser = new IDL3Parser(lexer);
            parser.setDebug(debug);
            parser.setManager(this);
        } catch (Exception e) {
            throw new RuntimeException("Error creating parser: " + e);
        }

        setOriginalFile(filename);
        setSourceFile(filename);

        symbolTable.pushFile();

        try {
            spec = parser.specification();
        } catch (Exception e) {
            errors.add(e);
        }

        symbolTable.popFile();

        if (errors.size() > 0) {
            StringBuffer msg = new StringBuffer("Errors during parsing:");
            for (Iterator i = errors.iterator(); i.hasNext(); )
                msg.append("\n" + i.next().toString());
            throw new RuntimeException(msg.toString());
        }

	return spec;
    }
}
