/* CCM Tools : ccmtools test case base class
 * Egon Teiniker <egon.teiniker@salomon.at>
 * Copyright (C) 2002, 2003, 2004 Salomon Automation
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

package ccmtools.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import junit.framework.TestCase;

/*******************************************************************************
 * This test case is derived from JUnit's TestCase class and implements tests
 * for ccmtools code generators. These tests only check the code generation
 * process, while the runtime behavior of the generated coed is checked by
 * Confix' _check_* files.
 * 
 * @author Egon Teiniker
 ******************************************************************************/
public class CcmtoolsTestCase extends TestCase
{

    public CcmtoolsTestCase(String name)
    {
        super(name);
    }

    /**
     * Run the ccmtools generator by calling ccmtools.UI.ConsoleCodeGenerator's
     * main function.
     * 
     * @param Commandline
     *            parameters are collected in a List of String.
     */
    protected void runCcmtoolsGenerate(List args)
    {
        String[] parameters = new String[args.size()];
        for(int i = 0; i < args.size(); i++) {
            parameters[i] = (String) args.get(i);
        }
        System.out.print(">>> ccmtools-generate");
        for(int i = 0; i < parameters.length; i++)
            System.out.print(" " + parameters[i]);
        System.out.println();
        ccmtools.UI.ConsoleCodeGenerator.main(parameters);
    }

    /**
     * Run the ccmtools generator by calling ccmtools.UI.ConsoleCodeGenerator's
     * main function.
     * 
     * @param Commandline
     *            parameters are collected in a single String (separated by a
     *            space character).
     */
    protected void runCcmtoolsGenerate(String args)
    {
        String[] parameters = args.split(" ");
        System.out.print(">>> ccmtools-generate");
        for(int i = 0; i < parameters.length; i++)
            System.out.print(" " + parameters[i]);
        System.out.println();
        ccmtools.UI.ConsoleCodeGenerator.main(parameters);
    }

    /**
     * Create an empty Makefile.py file within the given package directory. This
     * file is needed by Confix to detect package directories.
     * 
     * @param Path
     *            to the Confix package directory in which the Makefile.py
     *            should be put.
     */
    protected void createMakefile(String package_root)
    {
        try {
            File outputFile = new File(package_root + File.separator
                    + "Makefile.py");
            FileWriter out = new FileWriter(outputFile);
            out.close();
            System.out.println(">>> create " + package_root + File.separator
                    + "Makefile.py");
        }
        catch(Exception e) {
            fail("Can't create Makefile.py!");
        }
    }

    /**
     * Copy a given source file to a given destination. This method is used to
     * copy impl and test files into the generated directories to check a
     * component's runtime behavior.
     *  
     */
    protected void copyFile(String from, String to)
    {
        try {
            File inputFile = new File(from);
            File outputFile = new File(to);
            FileReader in = new FileReader(inputFile);
            FileWriter out = new FileWriter(outputFile);
            int c;
            while((c = in.read()) != -1)
                out.write(c);
            in.close();
            out.close();
            System.out.println(">>> copy from " + from);
        }
        catch(Exception e) {
            fail("Can't copy file from [" + from + "] to [" + to + "]");
        }
    }

    protected void runConfix(String args) throws CcmtoolsTestCaseException
    {
        try {
            // Run the GNU preprocessor cpp in a separate process.
            String cmd = "confix.py ";
            System.out.println(">>>" + cmd + args);
            Process proc = Runtime.getRuntime().exec(cmd + args);

            // copy input and error to the output stream
            StreamPumper inputPumper = new StreamPumper(proc.getInputStream());
            StreamPumper errorPumper = new StreamPumper(proc.getErrorStream());

            // starts pumping away the generated output/error
            inputPumper.start();
            errorPumper.start();

            // Wait for everything to finish
            proc.waitFor();
            inputPumper.join();
            errorPumper.join();
            proc.destroy();

            if(proc.exitValue() != 0)
                throw new CcmtoolsTestCaseException("Confix error: result != 0");
        }
        catch(Exception e) {
            throw new CcmtoolsTestCaseException("Confix error");
        }
    }

    // Inner class for continually pumping the input stream during
    // Process's runtime.
    class StreamPumper extends Thread
    {

        private BufferedReader din;

        private boolean endOfStream = false;

        private int SLEEP_TIME = 5;

        public StreamPumper(InputStream is)
        {
            this.din = new BufferedReader(new InputStreamReader(is));
        }

        public void pumpStream() throws IOException
        {
            if(!endOfStream) {
                String line = din.readLine();

                if(line != null) {
                    System.out.println(line);
                }
                else {
                    endOfStream = true;
                }
            }
        }

        public void run()
        {
            try {
                try {
                    while(!endOfStream) {
                        pumpStream();
                        sleep(SLEEP_TIME);
                    }
                }
                catch(InterruptedException ie) {
                    //ignore
                }
                din.close();
            }
            catch(IOException ioe) {
                // ignore
            }
        }
    }

    protected void runDefaultConfix(String sandboxDir)
        throws CcmtoolsTestCaseException
    {
        runConfix("--packageroot=" + sandboxDir
                + " --bootstrap --configure --make --targets=check");
        runConfix("--packageroot=" + sandboxDir + " --make --targets=clean");
    }

    protected void runDefaultCcmtoolsGenerate(String testDir, String sandboxDir)
    {
        runCcmtoolsGenerate("c++local -a -o " + sandboxDir + " " + testDir
                + "/Test.idl");
        runCcmtoolsGenerate("idl2 -o " + sandboxDir + "/CORBA_Stubs" + " "
                + testDir + "/Test.idl");
        runCcmtoolsGenerate("c++remote -o " + sandboxDir + " " + testDir
                + "/Test.idl");
        runCcmtoolsGenerate("c++remote-test -o " + sandboxDir + " " + testDir
                + "/Test.idl");
    }
}