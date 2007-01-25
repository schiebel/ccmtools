/* CCM Tools : CppRemoteGenerator test cases
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

package ccmtools.test.CppRemoteGenerator;

import ccmtools.test.CcmtoolsTestCase;

/*******************************************************************************
 * @author eteinik
 * 
 * This test case implements test cases for the remote c++ generator. These
 * tests check the code generation process, and the runtime behavior of the
 * generated code (using Confix' _check_*.cc files).
 ******************************************************************************/
public class UserTypesTest extends CcmtoolsTestCase
{

    private String ccmtoolsDir;

    public UserTypesTest(String name)
    {
        super(name);
        // get current working directory (this is where build.xml is executed)
        ccmtoolsDir = System.getProperty("user.dir");
    }

    // ------------------------------------------------------------------------
    // Facet test cases
    // ------------------------------------------------------------------------

    public void testFacetUserTypes()
    {
        String testDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/facet_user_types";
        String sandboxDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/sandbox/facet_user_types";

        try {
            runDefaultCcmtoolsGenerate(testDir, sandboxDir);

            String testFile = "/test/_check_CCM_Remote_CCM_Session_Test_remote.cc";
            String implFile = "/impl/Test_console_impl.cc";
            copyFile(testDir + "/impl/Makefile.py", sandboxDir + "/Makefile.py");
            copyFile(testDir + testFile, sandboxDir + testFile);
            copyFile(testDir + implFile, sandboxDir + implFile);

            runDefaultConfix(sandboxDir);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testFacetModuleUserTypes()
    {
        String testDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/facet_module_user_types";
        String sandboxDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/sandbox/facet_module_user_types";

        try {
            runDefaultCcmtoolsGenerate(testDir, sandboxDir);
            
            String implFile = "/impl/Test_console_impl.cc";
            String testFile = "/test/_check_CCM_Remote_world_europe_austria_CCM_Session_Test_remote.cc";
            copyFile(testDir + "/impl/Makefile.py", sandboxDir + "/Makefile.py");
            copyFile(testDir + testFile, sandboxDir + testFile);
            copyFile(testDir + implFile, sandboxDir + implFile);

            runDefaultConfix(sandboxDir);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testFacetException()
    {
        String testDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/facet_exception";
        String sandboxDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/sandbox/facet_exception";

        try {
            runDefaultCcmtoolsGenerate(testDir, sandboxDir);

            String testFile = "/test/_check_CCM_Remote_CCM_Session_Test_remote.cc";
            String implFile = "/impl/Test_iface_impl.cc";
            copyFile(testDir + "/impl/Makefile.py", sandboxDir + "/Makefile.py");
            copyFile(testDir + testFile, sandboxDir + testFile);
            copyFile(testDir + implFile, sandboxDir + implFile);

            runDefaultConfix(sandboxDir);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testFacetModuleException()
    {
        String testDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/facet_module_exception";
        String sandboxDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/sandbox/facet_module_exception";

        try {
            runDefaultCcmtoolsGenerate(testDir, sandboxDir);

            String testFile = "/test/_check_CCM_Remote_world_europe_austria_CCM_Session_Test_remote.cc";
            String implFile = "/impl/Test_iface_impl.cc";
            copyFile(testDir + "/impl/Makefile.py", sandboxDir + "/Makefile.py");
            copyFile(testDir + testFile, sandboxDir + testFile);
            copyFile(testDir + implFile, sandboxDir + implFile);

            runDefaultConfix(sandboxDir);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    // ------------------------------------------------------------------------
    // Receptacle test cases
    // ------------------------------------------------------------------------
    public void testReceptacleUserTypes()
    {
        String testDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/receptacle_user_types";
        String sandboxDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/sandbox/receptacle_user_types";

        try {
            runDefaultCcmtoolsGenerate(testDir, sandboxDir);

            String testFile = "/test/_check_CCM_Remote_CCM_Session_Test_remote.cc";
            copyFile(testDir + testFile, sandboxDir + testFile);

            String implFile1 = "/impl/Test_impl.cc";
            String implFile2 = "/impl/Test_inPort_impl.cc";
            copyFile(testDir + "/impl/Makefile.py", sandboxDir + "/Makefile.py");
            copyFile(testDir + implFile1, sandboxDir + implFile1);
            copyFile(testDir + implFile2, sandboxDir + implFile2);

            runDefaultConfix(sandboxDir);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

    public void testReceptacleModuleUserTypes()
    {
        String testDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/receptacle_module_user_types";
        String sandboxDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/sandbox/receptacle_module_user_types";

        try {
            runDefaultCcmtoolsGenerate(testDir, sandboxDir);

            String testFile = "/test/_check_CCM_Remote_world_europe_austria_CCM_Session_Test_remote.cc";
            String implFile1 = "/impl/Test_impl.cc";
            String implFile2 = "/impl/Test_inPort_impl.cc";
            copyFile(testDir + "/impl/Makefile.py", sandboxDir + "/Makefile.py");
            copyFile(testDir + testFile, sandboxDir + testFile);
            copyFile(testDir + implFile1, sandboxDir + implFile1);
            copyFile(testDir + implFile2, sandboxDir + implFile2);

            runDefaultConfix(sandboxDir);
        }
        catch(Exception e) {
            System.out.println(e.getMessage());
            fail();
        }
    }

}