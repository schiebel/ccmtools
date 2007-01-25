/* CCM Tools : CppGenerator test cases
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
 * This test case is derived from CcmtoolsTestCase class and implements tests
 * for idl3, idl3mirror, c++local, c++local-test code generators. These tests
 * check the code generation process, and the runtime behavior of the generated
 * code (using Confix' _check_*.cc files).
 ******************************************************************************/
public class SimpleTest extends CcmtoolsTestCase
{

    private String ccmtoolsDir;

    public SimpleTest(String name)
    {
        super(name);
        // get current working directory
        // (this is where build.xml is executed)
        ccmtoolsDir = System.getProperty("user.dir");
    }

    // ------------------------------------------------------------------------
    // Attribute test cases
    // ------------------------------------------------------------------------

    public void testAttributeSimple()
    {
        String testDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/attribute_simple";
        String sandboxDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/sandbox/attribute_simple";

        try {
            runDefaultCcmtoolsGenerate(testDir, sandboxDir);
            copyFile(testDir + "/impl/Makefile.py", sandboxDir + "/Makefile.py");
            copyFile(
                     testDir
                             + "/test/_check_CCM_Remote_CCM_Session_Test_remote.cc",
                     sandboxDir
                             + "/test/_check_CCM_Remote_CCM_Session_Test_remote.cc");
            runDefaultConfix(sandboxDir);
        }
        catch(Exception e) {
            fail();
        }
    }

    public void testAttributeModuleSimple()
    {
        String testDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/attribute_module_simple";
        String sandboxDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/sandbox/attribute_module_simple";

        try {
            runDefaultCcmtoolsGenerate(testDir, sandboxDir);
            copyFile(testDir + "/impl/Makefile.py", sandboxDir + "/Makefile.py");
            copyFile(
                     testDir
                             + "/test/_check_CCM_Remote_world_europe_austria_CCM_Session_Test_remote.cc",
                     sandboxDir
                             + "/test/_check_CCM_Remote_world_europe_austria_CCM_Session_Test_remote.cc");
            runDefaultConfix(sandboxDir);
        }
        catch(Exception e) {
            fail();
        }
    }

    // ------------------------------------------------------------------------
    // Supports test cases
    // ------------------------------------------------------------------------

    public void testSupportsSimple()
    {
        String testDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/supports_simple";
        String sandboxDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/sandbox/supports_simple";

        try {
            runDefaultCcmtoolsGenerate(testDir, sandboxDir);
            copyFile(testDir + "/impl/Makefile.py", sandboxDir + "/Makefile.py");
            copyFile(testDir + "/impl/Test_impl.cc", sandboxDir
                    + "/impl/Test_impl.cc");
            copyFile(
                     testDir
                             + "/test/_check_CCM_Remote_CCM_Session_Test_remote.cc",
                     sandboxDir
                             + "/test/_check_CCM_Remote_CCM_Session_Test_remote.cc");
            runDefaultConfix(sandboxDir);
        }
        catch(Exception e) {
            fail();
        }
    }

    public void testSupportsModuleSimple()
    {
        String testDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/supports_module_simple";
        String sandboxDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/sandbox/supports_module_simple";

        try {
            runDefaultCcmtoolsGenerate(testDir, sandboxDir);
            copyFile(testDir + "/impl/Makefile.py", sandboxDir + "/Makefile.py");
            copyFile(testDir + "/impl/Test_impl.cc", sandboxDir
                    + "/impl/Test_impl.cc");
            copyFile(
                     testDir
                             + "/test/_check_CCM_Remote_world_europe_austria_CCM_Session_Test_remote.cc",
                     sandboxDir
                             + "/test/_check_CCM_Remote_world_europe_austria_CCM_Session_Test_remote.cc");
            runDefaultConfix(sandboxDir);
        }
        catch(Exception e) {
            fail();
        }
    }

    // ------------------------------------------------------------------------
    // Facet test cases
    // ------------------------------------------------------------------------

    public void testFacetSimple()
    {
        String testDir = ccmtoolsDir + "/test/CppRemoteGenerator/facet_simple";
        String sandboxDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/sandbox/facet_simple";

        try {
            runDefaultCcmtoolsGenerate(testDir, sandboxDir);
            copyFile(testDir + "/impl/Makefile.py", sandboxDir + "/Makefile.py");
            copyFile(testDir + "/impl/Test_my_facet_impl.cc", sandboxDir
                    + "/impl/Test_my_facet_impl.cc");
            copyFile(
                     testDir
                             + "/test/_check_CCM_Remote_CCM_Session_Test_remote.cc",
                     sandboxDir
                             + "/test/_check_CCM_Remote_CCM_Session_Test_remote.cc");
            runDefaultConfix(sandboxDir);
        }
        catch(Exception e) {
            fail();
        }
    }

    public void testFacetModuleSimple()
    {
        String testDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/facet_module_simple";
        String sandboxDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/sandbox/facet_module_simple";

        try {
            runDefaultCcmtoolsGenerate(testDir, sandboxDir);
            copyFile(testDir + "/impl/Makefile.py", sandboxDir + "/Makefile.py");
            copyFile(testDir + "/impl/Test_my_facet_impl.cc", sandboxDir
                    + "/impl/Test_my_facet_impl.cc");
            copyFile(
                     testDir
                             + "/test/_check_CCM_Remote_world_CCM_Session_Test_remote.cc",
                     sandboxDir
                             + "/test/_check_CCM_Remote_world_CCM_Session_Test_remote.cc");
            runDefaultConfix(sandboxDir);
        }
        catch(Exception e) {
            fail();
        }
    }

    // ------------------------------------------------------------------------
    // Receptacle test cases
    // ------------------------------------------------------------------------

    public void testReceptacleSimple()
    {
        String testDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/receptacle_simple";
        String sandboxDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/sandbox/receptacle_simple";

        try {
            runDefaultCcmtoolsGenerate(testDir, sandboxDir);
            copyFile(testDir + "/impl/Makefile.py", sandboxDir + "/Makefile.py");
            copyFile(testDir + "/impl/Test_in_port_impl.cc", sandboxDir
                    + "/impl/Test_in_port_impl.cc");
            copyFile(testDir + "/impl/Test_impl.cc", sandboxDir
                    + "/impl/Test_impl.cc");
            copyFile(
                     testDir
                             + "/test/_check_CCM_Remote_CCM_Session_Test_remote.cc",
                     sandboxDir
                             + "/test/_check_CCM_Remote_CCM_Session_Test_remote.cc");
            runDefaultConfix(sandboxDir);
        }
        catch(Exception e) {
            fail();
        }
    }

    public void testReceptacleModuleSimple()
    {
        String testDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/receptacle_module_simple";
        String sandboxDir = ccmtoolsDir
                + "/test/CppRemoteGenerator/sandbox/receptacle_module_simple";

        try {
            runDefaultCcmtoolsGenerate(testDir, sandboxDir);
            copyFile(testDir + "/impl/Makefile.py", sandboxDir + "/Makefile.py");
            copyFile(testDir + "/impl/Test_impl.cc", sandboxDir
                    + "/impl/Test_impl.cc");
            copyFile(testDir + "/impl/Test_in_port_impl.cc", sandboxDir
                    + "/impl/Test_in_port_impl.cc");
            copyFile(
                     testDir
                             + "/test/_check_CCM_Remote_world_europe_austria_CCM_Session_Test_remote.cc",
                     sandboxDir
                             + "/test/_check_CCM_Remote_world_europe_austria_CCM_Session_Test_remote.cc");
            runDefaultConfix(sandboxDir);
        }
        catch(Exception e) {
            fail();
        }
    }

}