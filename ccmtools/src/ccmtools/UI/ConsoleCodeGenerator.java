/*
 * CCM Tools : User Interface Library Leif Johnson <leif@ambient.2y.net> Egon
 * Teiniker <egon.teiniker@salomon.at> Copyright (C) 2002, 2003 Salomon
 * Automation
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

package ccmtools.UI;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import ccmtools.Constants;
import ccmtools.CodeGenerator.CCMMOFGraphTraverserImpl;
import ccmtools.CodeGenerator.CodeGenerator;
import ccmtools.CodeGenerator.Driver;
import ccmtools.CodeGenerator.GraphTraverser;
import ccmtools.CodeGenerator.TemplateHandler;
import ccmtools.CppGenerator.CppLocalDbcGeneratorImpl;
import ccmtools.CppGenerator.CppLocalGeneratorImpl;
import ccmtools.CppGenerator.CppLocalTestGeneratorImpl;
import ccmtools.CppGenerator.CppRemoteGeneratorImpl;
import ccmtools.CppGenerator.CppRemoteTestGeneratorImpl;
import ccmtools.CppGenerator.CppPythonGeneratorImpl;
import ccmtools.IDL3Parser.ParserManager;
import ccmtools.IDLGenerator.IDL2GeneratorImpl;
import ccmtools.IDLGenerator.IDL3GeneratorImpl;
import ccmtools.IDLGenerator.IDL3MirrorGeneratorImpl;
import ccmtools.Metamodel.BaseIDL.MContainer;

public class ConsoleCodeGenerator
{

    private static final int GENERATE_APPLICATION_FILES = 0x0001;
    private static final int GENERATE_CODA_INFO = 0x0010;
    private static final int OVERWRITE_FILES = 0x0100;

    private static final String usage = "Usage: ccmtools-generate LANGUAGE [OPTIONS]... FILES...\n"
            + "Options:\n"
            + "  -a, --application             Generate skeletons for business logic *\n"
            + "  --coda[=<setup file>]         Generate argument defaults via info outside IDL"
            + "                                (implies --application)\n"
            + "  --templates=<path>            Specify the directory which contains the templates\n"
            + "  --overwrite                   Don't create *.new files, always overwrite\n"
            + "  -h, --help                    Display this help\n"
            + "  -Ipath                        Add path to the preprocessor include path\n"
            + "  -o DIR, --output=DIR          Base output in DIR (default .)\n"
            + "  -V, --version                 Display CCM Tools version information\n"
            + "      --generator-mask=<flags>  Mask for generator debug output\n"
            + "      --parser-mask=<flags>     Mask for parser debug output\n"
            + "Languages available:\n"
            + "LANGUAGES\n"
            + "Generates code in the given output language after parsing FILES.\n"
            + "Options marked with a star (*) are generally used once per project.\n";

    private static final String[] local_language_types = {
            "c++local", "c++local-test", "c++dbc", "c++remote",
            "c++remote-test", "c++python", "idl3", "idl3mirror", "idl2"
    };

    private static List language_types = null;

    private static List languages;

    private static long gen_mask = ConsoleDriverImpl.M_OUTPUT_FILE; //ConsoleDriverImpl.M_MESSAGE;

    private static long par_mask = 0x00000000;

    private static String include_path;

    private static String code_version = "0.0.0";
    private static String coda_startup_file = "";

    private static List filenames;

    private static File output_directory = 
        new File(System.getProperty("user.dir"));

    private static File base_output_directory = new File(output_directory, "");

    private static int generate_flags = 0;
	private static String inputPath;

	public static void main(String args[])
	{
		if ( main_(args) == false )
			System.exit(1);
	}
	
    /**
     * Parse and generate code for each input IDL3 file. For each input file, we
     * need to (0) run the C preprocessor on the file to assemble includes and
     * do ifdef parsing and such, then (1) parse the file, then (2) generate
     * output code. Exits with nonzero status if errors are encountered during
     * parsing or generation.
     */
    public static boolean main_(String args[])
    {
        if(!parseArgs(args)) {
            return true; // No further processing needed
        }

        ArrayList handlers = new ArrayList();
        
        try {
            printVersion();  // Print out the current version of ccmtools

            //Driver driver = createDriver();
            Driver driver = new ConsoleDriverImpl(gen_mask);
            
            driver.message(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
            
            GraphTraverser traverser = new CCMMOFGraphTraverserImpl();
            if(traverser == null) {
                printUsage("failed to create a graph traverser");
                return false;
            }

            ParserManager manager = new ParserManager(par_mask);
            if(manager == null) {
                printUsage("failed to create a parser manager");
                return false;
            }
       
            for(Iterator l = languages.iterator(); l.hasNext();) {
                TemplateHandler handler = 
                    createTemplateHandler(driver,(String) l.next());
                handlers.add(handler);
                traverser.addHandler(handler);
            }

            Runtime rt = Runtime.getRuntime();

            MContainer kopf = null;

            for(Iterator f = filenames.iterator(); f.hasNext();) {
                File source = new File((String) f.next());
                File idlfile = new File(System.getProperty("user.dir"), "_CCM_"
                        + source.getName());

                // step (0). run the C preprocessor on the input file.
                try {
                	   // handler may need to know the path for the current idl file
                	   // so it can pick up supplemental information...
                	   inputPath = source.getPath();
                		   
                    // Run the GNU preprocessor cpp in a separate process.
                    System.out.println("> " + Constants.CPP_PATH + " -o " + idlfile + " "
                            + include_path + " " + source);
                    Process preproc = 
                        Runtime.getRuntime().exec( Constants.CPP_PATH + " -o " + idlfile + " " + include_path
                            + " " + source);
                    BufferedReader stdInput = 
                        new BufferedReader(new InputStreamReader(preproc.getInputStream()));
                    BufferedReader stdError = 
                        new BufferedReader(new InputStreamReader(preproc.getErrorStream()));

                    // Read the output and any errors from the command
                    String s;
                    while((s = stdInput.readLine()) != null)
                        System.out.println(s);
                    while((s = stdError.readLine()) != null)
                        System.out.println(s);

                    // Wait for the process to complete and evaluate the return
                    // value of the attempted command
                    preproc.waitFor();
                    if(preproc.exitValue() != 0)
                        throw new RuntimeException();
                }
                catch(Exception e) {
                    System.err.println("Error preprocessing " + source
                            + ": Please verify your include paths.");
                    return false;
                }

                // step (1). parse the resulting preprocessed file.
                System.out.println("> parse " + idlfile.toString());
                manager.reset();
                manager.setOriginalFile(source.toString());
                try {
                    kopf = manager.parseFile(idlfile.toString());
                    if(kopf == null)
                        throw new RuntimeException("Parser returned a null container");
                }
                catch(Exception e) {
                    System.err.println("Error parsing " + source + ":\n" + e);
                    return false;
                }
                String kopf_name = source.getName().split("\\.")[0];
                kopf_name = kopf_name.replaceAll("[^\\w]", "_");
                kopf.setIdentifier(kopf_name);

                // step (2). traverse the resulting metamodel graph.
                try {
                    System.out.println("> traverse CCM model");
                    traverser.traverseGraph(kopf);
                }
                catch(Exception e) {
                    System.err.println("Error generating code from " 
                                       + source + ":\n" + e);
                    e.printStackTrace(System.err);
                    return false;
                }

                // delete the preprocessed temporary file if everything worked.
                idlfile.deleteOnExit();

                System.out.println("> done.");
            }
        }
        catch(Exception e) {
            System.err.println("Error: CCM Tools have been finished with an error:");
            System.err.println(e.getMessage());
            System.err.println("Please post a bug report to <ccmtools-devel@lists.sourceforge.net>");
            return false;
        } finally {
        		for (Iterator i = handlers.iterator(); i.hasNext(); )
        			((TemplateHandler)i.next()).close();
        }
        return true;
    }

    /** *********************************************************************** */
    /* USAGE / VERSION INFO */

    private static void printUsage(String err)
    {
        if(err.length() > 0)
            System.err.println("Error: " + err);

        StringBuffer langs = new StringBuffer("  ");
        for(int i = 0; i < language_types.size(); i++) {
            langs.append((String) language_types.get(i) + " ");
            if((langs.length() % 80) > 60)
                langs.append("\n  ");
        }

        System.out.print(usage.replaceAll("LANGUAGES", langs.toString()));

        if(err.length() > 0) {
            System.exit(1);
        }
    }

    private static void printVersion()
    {
        System.out.println("CCM Tools version " + Constants.VERSION);
        System.out.println("Copyright (C) 2002 - 2005 Salomon Automation");
        System.out.println("Copyright (C) 2007 - 2011 Associated Universities Inc.");
        System.out.println("The CCM Tools library is distributed under the");
        System.out.println("terms of the GNU Lesser General Public License.");
    }

    /** *********************************************************************** */
    /* SETUP FUNCTIONS */

    /**
     * Try to create a driver for the code generator. This will handle output
     * messages and could possibly react to input from the user as well.
     * 
     * @return a newly created driver, or exit if there was an error.
     *
    private static Driver createDriver()
    {
        Driver driver = null;

        try {
            driver = new ConsoleDriverImpl(gen_mask);
        }
        catch(FileNotFoundException e) {
            printUsage("constructing the driver object\n" + e);
            // and exit
        }

        if(driver == null) {
            printUsage("failed to create a driver object");
            // and exit
        }
        return driver;
    }
    */

    /**
     * Set up the node handler (i.e. code generator) object based on the output
     * language provided. Use the given driver to control the handler.
     * 
     * @param driver
     *            the user interface driver object to assign to this handler.
     * @param lang
     *            the language to generate.
     * @return the newly created node handler (i.e. code generator), or exit if
     *         there was an error.
     */
    private static TemplateHandler createTemplateHandler(Driver driver, String lang)
    {
        TemplateHandler handler = null;

        try {
            if(lang.equalsIgnoreCase("c++local")) {
                handler = new CppLocalGeneratorImpl(driver, output_directory);
            }
            else if(lang.equalsIgnoreCase("c++local-test")) {
                handler = new CppLocalTestGeneratorImpl(driver,
                                                        output_directory);
            }
            else if(lang.equalsIgnoreCase("c++dbc")) {
                handler = new CppLocalDbcGeneratorImpl(driver, output_directory);
            }
            else if(lang.equalsIgnoreCase("c++remote")) {
                handler = new CppRemoteGeneratorImpl(driver, output_directory);
            }
            else if(lang.equalsIgnoreCase("c++remote-test")) {
                handler = new CppRemoteTestGeneratorImpl(driver,
                                                         output_directory);
            }
            else if(lang.equalsIgnoreCase("c++python")) {
            	   // the python generator depends upon recursive template expansion...
            	   // so clean up a tempalte subdirectory in the output directory each time around.
                File template = new File( output_directory.getPath(), "templates" );
                if ( template.exists() )
                		resetDirectory(template);
                handler = new CppPythonGeneratorImpl(driver, output_directory);
            }            
            else if(lang.equalsIgnoreCase("idl3")) {
                handler = new IDL3GeneratorImpl(driver, output_directory);
            }
            else if(lang.equalsIgnoreCase("idl3mirror")) {
                handler = new IDL3MirrorGeneratorImpl(driver, output_directory);
            }
            else if(lang.equalsIgnoreCase("idl2")) {
                handler = new IDL2GeneratorImpl(driver, output_directory);
            }
        }
        catch(IOException e) {
            printUsage("while constructing a generator for " + lang + "\n" + e);
        }

        if(handler == null) {
            printUsage("ERROR: failed to create a language generator for " + lang);
        }

        if((generate_flags & GENERATE_APPLICATION_FILES) != 0) {
            // handler.setFlag(((CodeGenerator)
            // handler).FLAG_APPLICATION_FILES);
            handler.setFlag(CodeGenerator.FLAG_APPLICATION_FILES);
        }
        if ((generate_flags & GENERATE_CODA_INFO) != 0) {
        		handler.setFlag(CodeGenerator.FLAG_APPLICATION_FILES);
        		handler.setFlag(CodeGenerator.FLAG_CODA_INFO);
        		handler.setCodaStartupFile(coda_startup_file);
        }
        if ((generate_flags & OVERWRITE_FILES) != 0) {
        		handler.setFlag(CodeGenerator.FLAG_OVERWRITE_FILES);
        }
        return handler;
    }

    /**
	 * @param template
	 */
	private static void resetDirectory(File dir) {
		if (dir.exists()) {
			deleteDirectory(dir);
		}
		dir.mkdir();	
	}

	/**
	 * @param dir
	 */
	private static void deleteDirectory(File dir) {
		if (dir.exists()) {
			if (dir.isFile())
				dir.delete();
			else if (dir.isDirectory()) {
				String contents[] = dir.list();
				for (int i=0; i < contents.length; ++i) {
					File f = new File(dir.getPath() + File.separator + contents[i]);
					if (f.isFile())
						f.delete();
					else if(f.isDirectory()) {
						deleteDirectory(f);
						f.delete();
					}
				}
			}

		}	
	}

	/** *********************************************************************** */
    /* ARGUMENT PARSING */

    /**
     * Parse the command line arguments to the console code generator front end.
     * We might want to replace this in the future with a more flexible
     * interface (like getopt, if it exists in java), but for now the number of
     * options is fairly manageable.
     * 
     * A side effect of this function (actually the point of the function) is
     * that it changes internal class instance values, if switches for them are
     * provided on the command line. If errors are encountered then warning
     * messages might be printed out, and if at the end of processing no valid
     * language has been found, the program will exit.
     * 
     * @param args
     *            the arguments passed on the command line.
     */
    private static boolean parseArgs(String args[])
    {
        languages = new ArrayList();
        language_types = new ArrayList();
        filenames = new ArrayList();
        include_path = "";

        for(int i = 0; i < local_language_types.length; i++) {
            language_types.add(local_language_types[i]);
        }

        setHome(System.getProperty("user.dir"));

        List argv = new ArrayList();
        for(int i = 0; i < args.length; i++)
            argv.add(args[i]);

        if(argv.contains("-h") || argv.contains("--help")) {
            printUsage("");
            return false; // No further processing needed
        }
        else if(argv.contains("-V") || argv.contains("--version")) {
            printVersion();
            return false; // No further processing needed
        }

        for(Iterator a = argv.iterator(); a.hasNext();) {
            String arg = (String) a.next();
            if(arg.equals(""))
                continue;
            else if(arg.startsWith("--generator-mask="))
                setGeneratorMask(arg.split("=")[1]);
            else if(arg.startsWith("--parser-mask="))
                setParserMask(arg.split("=")[1]);
            else if(arg.startsWith("--output="))
                setOutputDirectory(arg.split("=")[1]);
            else if(arg.startsWith("--code-version="))
                setCodeVersion(arg.split("=")[1]);
            else if(arg.startsWith("--app"))
                generate_flags |= GENERATE_APPLICATION_FILES;
            else if(arg.startsWith("--coda=")) {
            		setCodaStartupFile(arg.split("=")[1]);
            		generate_flags |= GENERATE_CODA_INFO;
            }
            else if(arg.startsWith("--coda"))
            		generate_flags |= GENERATE_CODA_INFO;
            else if(arg.equals("--overwrite"))
            		generate_flags |= OVERWRITE_FILES;
            else if(arg.startsWith("--home="))
                setHome(arg.split("=")[1]);
            else if(arg.startsWith("--templates=")) {
            	String[] templ_arg = arg.split("=");
            	if ( templ_arg.length > 1 ) setTemplates(templ_arg[1]);
            } else if(arg.charAt(0) == '-')
                do {
                    if(arg.charAt(0) == 'a') {
                        generate_flags |= GENERATE_APPLICATION_FILES;
                    }
                    else if(arg.charAt(0) == 'c') {
                        if(a.hasNext())
                            setCodeVersion((String) a.next());
                        else
                            printUsage("unspecified code version");
                        break;
                    }
                    else if(arg.charAt(0) == 'o') {
                        if(a.hasNext())
                            setOutputDirectory((String) a.next());
                        else
                            printUsage("unspecified output directory");
                        break;
                    }
                    else if(arg.charAt(0) == 'I') {
                        File path = new File(arg.substring(1));
                        if(path.isDirectory())
                            include_path += " -I" + path;
                        break;
                    }
                    arg = arg.substring(1);
                } while(arg.length() > 0);
            else if(language_types.contains(arg.toLowerCase())
                    && !languages.contains(arg)) {
                languages.add(arg);
            }
            else {
                filenames.add(arg);
            }
        }

        if(languages.size() == 0)
            printUsage("no valid output language specified");

        if(include_path.trim().equals(""))
            include_path = " -I" + System.getProperty("user.dir");

        return true;
    }

    private static void setGeneratorMask(String val)
    {
        try {
            if(val.startsWith("0x"))
                gen_mask = Long.parseLong(val.substring(2), 16);
            else if(val.startsWith("0"))
                gen_mask = Long.parseLong(val.substring(1), 8);
            else
                gen_mask = Long.parseLong(val, 10);
        }
        catch(NumberFormatException e) {
            System.err.println("Could not convert " + val
                    + " to a generator mask. Ignoring.");
        }
    }

    private static void setParserMask(String val)
    {
        try {
            if(val.startsWith("0x"))
                par_mask = Long.parseLong(val.substring(2), 16);
            else if(val.startsWith("0"))
                par_mask = Long.parseLong(val.substring(1), 8);
            else
                par_mask = Long.parseLong(val, 10);
        }
        catch(NumberFormatException e) {
            System.err.println("Could not convert " + val
                    + " to a parser mask. Ignoring.");
        }
    }

    private static void setOutputDirectory(String val)
    {
        if(val.trim().equals(""))
            printUsage("unspecified output directory");
        File test = new File(val);
        if(test.isAbsolute()) {
            output_directory = test;
        }
        else {
            output_directory = new File(base_output_directory, val);
        }
    }

    private static void setHome(String val)
    {
        if(val.trim().equals(""))
            printUsage("unspecified CCM Tools home");
        Properties props = System.getProperties();
        props.setProperty("CCMTOOLS_HOME", val);
        System.setProperties(props);
    }
    
    private static void setTemplates(String val)
    {
        if( ! val.trim().equals("")) {
        	Properties props = System.getProperties();
        	props.setProperty("CCMTOOLS_TEMPLATES", val);
        	System.setProperties(props);
        }
    }
    
    private static void setCodaStartupFile(String val)
    {
        if(val.trim().equals(""))
            printUsage("--coda=... used with no file");
        File f = new File(val);
        if(!f.exists() || !f.isFile() || !f.canRead())
        		printUsage("cannot read coda setup file: " + val);
        coda_startup_file = val;
    }

    private static void setCodeVersion(String val)
    {
        if(val.trim().equals(""))
            printUsage("unspecified code version");
        code_version = new String(val);
    }
}

