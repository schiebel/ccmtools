/*
 * Created on Aug 28, 2004
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package ccmtools.test.CppLocalGenerator;

import ccmtools.test.CcmtoolsTestCase;

/**
 * @author eteinik
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class DebugTest extends CcmtoolsTestCase
{

    private String ccmtools_dir;

    public DebugTest(String name)
    {
        super(name);
        // get current working directory (this is where build.xml is executed)
        ccmtools_dir = System.getProperty("user.dir");
    }

    public void testFacetException()
    {
        String test_dir = ccmtools_dir + "/test/CppGenerator/facet_exception";
        String sandbox_dir = ccmtools_dir
                + "/test/CppGenerator/sandbox/facet_exception";

        try {
            runCcmtoolsGenerate("idl3 -o " + sandbox_dir + "/idl3" + " "
                    + test_dir + "/Test.idl");

            runCcmtoolsGenerate("idl3mirror -o " + sandbox_dir + "/idl3" + " "
                    + test_dir + "/Test.idl");

            runCcmtoolsGenerate("c++local -o " + sandbox_dir + " -I"
                    + sandbox_dir + "/idl3/interface" + " " + sandbox_dir
                    + "/idl3/interface/Console.idl" + " " + sandbox_dir
                    + "/idl3/interface/Error.idl" + " " + sandbox_dir
                    + "/idl3/interface/ErrorInfo.idl" + " " + sandbox_dir
                    + "/idl3/interface/ErrorInfoList.idl" + " " + sandbox_dir
                    + "/idl3/interface/FatalError.idl" + " " + sandbox_dir
                    + "/idl3/interface/SuperError.idl");

            runCcmtoolsGenerate("c++local -a -o " + sandbox_dir + " -I"
                    + sandbox_dir + "/idl3/interface" + " -I" + sandbox_dir
                    + "/idl3/component" + " " + sandbox_dir
                    + "/idl3/component/Test.idl" + " " + sandbox_dir
                    + "/idl3/component/TestHome.idl" + " " + sandbox_dir
                    + "/idl3/component/Test_mirror.idl" + " " + sandbox_dir
                    + "/idl3/component/TestHome_mirror.idl");

            runCcmtoolsGenerate("c++local-test -o " + sandbox_dir + " -I"
                    + sandbox_dir + "/idl3/interface" + " -I" + sandbox_dir
                    + "/idl3/component" + " " + sandbox_dir
                    + "/idl3/component/Test.idl");

            copyFile(test_dir + "/impl/Test_console_impl.cc", sandbox_dir
                    + "/impl/Test_console_impl.cc");
            copyFile(test_dir + "/impl/Test_mirror_impl.cc", sandbox_dir
                    + "/impl/Test_mirror_impl.cc");

            runConfix("--packageroot=" + sandbox_dir
                    + " --bootstrap --configure --make --targets=check");
            runConfix("--packageroot=" + sandbox_dir
                    + " --make --targets=clean");

        }
        catch(Exception e) {
            fail();
        }
    }
   
    
    
}