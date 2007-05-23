/*
 * CCM Tools : C++ Code Generator Library Leif Johnson <leif@ambient.2y.net>
 * Egon Teiniker <egon.teiniker@salomon.at> Copyright (C) 2002, 2003 Salomon
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

package ccmtools.CppGenerator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ccmtools.CodeGenerator.Driver;
import ccmtools.CodeGenerator.Template;
import ccmtools.Metamodel.BaseIDL.MAliasDef;
import ccmtools.Metamodel.BaseIDL.MContained;
import ccmtools.Metamodel.BaseIDL.MContainer;
import ccmtools.Metamodel.BaseIDL.MDefinitionKind;
import ccmtools.Metamodel.BaseIDL.MEnumDef;
import ccmtools.Metamodel.BaseIDL.MExceptionDef;
import ccmtools.Metamodel.BaseIDL.MFieldDef;
import ccmtools.Metamodel.BaseIDL.MIDLType;
import ccmtools.Metamodel.BaseIDL.MInterfaceDef;
import ccmtools.Metamodel.BaseIDL.MModuleDef;
import ccmtools.Metamodel.BaseIDL.MOperationDef;
import ccmtools.Metamodel.BaseIDL.MParameterDef;
import ccmtools.Metamodel.BaseIDL.MPrimitiveDef;
import ccmtools.Metamodel.BaseIDL.MPrimitiveKind;
import ccmtools.Metamodel.BaseIDL.MSequenceDef;
import ccmtools.Metamodel.BaseIDL.MStringDef;
import ccmtools.Metamodel.BaseIDL.MStructDef;
import ccmtools.Metamodel.BaseIDL.MTyped;
import ccmtools.Metamodel.BaseIDL.MTypedefDef;
import ccmtools.Metamodel.BaseIDL.MUnionDef;
import ccmtools.Metamodel.BaseIDL.MWstringDef;
import ccmtools.Metamodel.ComponentIDL.MComponentDef;
import ccmtools.Metamodel.ComponentIDL.MHomeDef;
import ccmtools.Metamodel.ComponentIDL.MProvidesDef;
import ccmtools.Metamodel.ComponentIDL.MSupportsDef;
import ccmtools.Metamodel.ComponentIDL.MUsesDef;
import ccmtools.utils.Text;

public class CppLocalGeneratorImpl extends CppGenerator
{

    // types for which we have a global template ; that is, a template that is
    // not contained inside another template.

    private final static String[] local_output_types = {
            "MComponentDef", "MInterfaceDef", "MHomeDef", "MStructDef",
            "MUnionDef", "MAliasDef", "MEnumDef", "MExceptionDef",
            "MProvidesDef", "MModuleDef"
    };

    /** *********************************************************************** */

    public CppLocalGeneratorImpl(Driver d, File out_dir) throws IOException
    {
        super("CppLocal", d, out_dir, local_output_types);
        base_namespace.add("CCM_Local");
    }

    protected String data_MComponentDef(String data_type, String data_value)
    {
    		MComponentDef component = (MComponentDef) current_node;
    		
    		if (data_type.startsWith("Wrapper")) {
    			if (( flags & FLAG_CODA_INFO) != 0) {
    				Template t = template_manager.getTemplate(data_type, current_name);
    				for (Iterator i = t.findVariables().iterator(); i.hasNext(); ) {
    					String key = (String) i.next();
    					String scoped_key = getScopeID(key);
    					if (output_variables.containsKey(scoped_key)) {
    						String val = (String) output_variables.get(scoped_key);
    						if (val.equals("")) {
    							Object newval = getLocalValue(key);
    							output_variables.put(scoped_key,newval);
    						}
    					}
    				}
    				return t.substituteVariables(output_variables);
    			}
    			else return "";
    		}

    		if (data_type.equals("StructInclude")) {
    			Set thestuff = getOpIncludes(component, null, "coda");
    			//List contents = component.getContentss();
//    			List ops  = component.getFilteredContents(MDefinitionKind.DK_OPERATION,true);
//    			for ( Iterator i=ops.iterator(); i.hasNext(); ) {
//    				String fubar = ((MOperationDef) i.next()).getIdentifier();
//    				String xxx = fubar + fubar;
//    			}
//    			List supports = component.getSupportss();
//    			for ( Iterator it=supports.iterator(); it.hasNext();) {
//        			MInterfaceDef iface = ((MSupportsDef)it.next()).getSupports();
//        			String str = iface.getIdentifier();
//        			List iop = iface.getFilteredContents(MDefinitionKind.DK_OPERATION,true);
//        			for ( Iterator bit=iop.iterator(); bit.hasNext();) {
//        				String fubar = ((MOperationDef) bit.next()).getIdentifier();
//        				String xxx = fubar + fubar;
//        			}
//        		}
    			String result = Text.join("\n",thestuff);
    			return result;
    		}
    		else if ( data_type.startsWith("Coda")) {
    			if (( flags & FLAG_CODA_INFO) != 0 ) {
    				if (data_type.equals("CodaConvertInclude")) {
    					Set thestuff = getOpIncludes(component,"all", "coda");
    					String exception_includes = "";
    					if (coda.getCodeInfo("catch","code") != null) {
    						exception_includes = "\n" + coda.getCodeInfo("catch", "include");
    					}
    					return Text.join("\n",thestuff) + exception_includes;
    				}
    				else if (data_type.equals("CodaTypeInclude")) {
    					Set thestuff = getOpIncludes(component,"type", "coda");
    					return Text.join("\n",thestuff);
    				} else if (data_type.equals("CodaPrivateInclude") ||
    							data_type.equals("CodaProtectedCode") ||
    							data_type.equals("CodaPrivateCode")) {
    					
    					String key = "";
    					String prefix = "";
    					if (data_type.equals("CodaPrivateCode")) {
    						key = "private";
    						prefix = "    private:\n";
    					}
    					else if (data_type.equals("CodaProtectedCode")) {
    						key = "protected";
    						prefix = "    protected:\n";
    					}
    					else if (data_type.equals("CodaPrivateInclude")) {
    						key = "include";
    					}

    					StringBuffer buffer = new StringBuffer();
    					String addition = coda.getCodeInfo(component.getIdentifier(),key);
    					if (addition != null)
    						buffer.append(addition);
    					List supports = component.getSupportss();
    					for (Iterator iter = supports.iterator(); iter.hasNext(); ) {
    						MContainer cmpt = ((MSupportsDef) iter.next()).getSupports();
    						addition = coda.getCodeInfo(cmpt.getIdentifier(),key);
    						if (addition != null)
    							buffer.append(addition);
    					}
    					String ret = buffer.toString();
    					return ret != null && ret.length() > 0 ? prefix + ret : "";
    				}
    			}
    		}

        return super.data_MComponentDef(data_type,data_value);
    }
    
 //   protected String data_MSupportsDef(String data_type, String data_value)
 //   {
 //       if (data_type.startsWith("Wrapper")) {
//			if (( flags & FLAG_WRAPPER_FILES) != 0) {
//				Template t = template_manager.getTemplate(data_type, current_name);
//				return t.substituteVariables(output_variables);
//			}
//			else return "?// TODO : IMPLEMENT ME HERE !";
//        }
//        return super.data_MSupportsDef(data_type,data_value);
//    }
   
    /**
     * Write generated code to an output file.
     * 
     * @param template
     *            the template object to get the generated code structure from ;
     *            variable values should come from the node handler object.
     */
    protected void writeOutput(Template template)
    {
        List out_paths = getOutputFiles();
        String out_string = template.substituteVariables(output_variables);
        String[] out_strings = out_string.split("<<<<<<<SPLIT>>>>>>>");

        try {
            Iterator path_iterator = out_paths.iterator();
            for(int i = 0; i < out_strings.length; i++) {

                String generated_code = prettifyCode(out_strings[i]);

                // out_path = [directory, filename]
                List out_path = (List) path_iterator.next();

                // from the getOutputFiles function we know each entry in the
                // output file list has exactly two parts ... the dirname and
                // the
                // filename.
                String file_dir = (String) out_path.get(0);
                String file_name = (String) out_path.get(1);

                // don't add blank output files. this lets us discard parts of
                // the templates that we don't want to output (see the component
                // section of the getOutputFiles function)
                if(file_name.equals(""))
                    continue;

                File outFile = new File(output_dir + File.separator + file_dir, file_name);
                boolean wrote_file = false;
                if((file_dir == "impl") && outFile.isFile()) {
                    if(outFile.getName().endsWith("_entry.h") ||
                    		(((flags & FLAG_CODA_INFO) != 0) &&
                    				(outFile.getName().endsWith("_impl.h") ||
                    				 outFile.getName().endsWith("_impl.cc")))) {
                        // *_entry.h files must be overwritten by every generator
                        // call because they are part of the component logic
                        writeFinalizedFile(file_dir, file_name, generated_code);
                        wrote_file = true;
                    }
                    else if(!isCodeEqualWithFile(generated_code, outFile)) {
                        if ((flags & FLAG_OVERWRITE_FILES) == 0) {
                            System.out.println("WARNING: " + outFile
                            			+ " already exists!");
                            file_name += ".new";
                            outFile = new File(output_dir + File.separator
                            			+ file_dir, file_name);
                        }
                    }
                }
                if(isCodeEqualWithFile(generated_code, outFile) && ! wrote_file) {
                    System.out.println("Skipping " + outFile);
                }
                else {
                    writeFinalizedFile(file_dir, file_name, generated_code);
                }

                writeMakefile(output_dir, file_dir, "py", "");

                // FIXME ---------------------------------
                // This hack is only temporarily to compile generated structures
                // via PMM
                if(current_node instanceof MComponentDef
                        || current_node instanceof MHomeDef
                        || current_node instanceof MProvidesDef) {
                    // Makefile.pl is not needed by components, homes and facets
                }
                else {
                    writeMakefile(output_dir, file_dir, "pl", "1;");
                }
                // FIXME --------------------------------
            }
        }
        catch(Exception e) {
            System.out.println("!!!Error " + e.getMessage());
        }
    }

    // FIXME ---------------------------------
	    // This hack is only temporarily to compile generated structures via PMM
	    protected String getScopedInclude(MContained node)
	    {
	        List scope = getScope(node);
	        StringBuffer buffer = new StringBuffer();
	        Collections.reverse(base_namespace);
	        for(Iterator i = base_namespace.iterator(); i.hasNext();) {
	            scope.add(0, i.next());
	        }
	        Collections.reverse(base_namespace);
	        scope.add(node.getIdentifier());
	        buffer.append("#ifdef HAVE_CONFIG_H\n");
	        buffer.append("#  include <config.h>\n");
	        buffer.append("#endif\n\n");
	        
	//        buffer.append("#ifdef USING_CONFIX \n");
	//        buffer.append("#include <");
	//        buffer.append(join(file_separator, scope));
	//        buffer.append(".h> \n");
	//        buffer.append("#else \n");
	//        buffer.append("#include <");
	//        buffer.append(node.getIdentifier());
	//        buffer.append(".h> \n");
	//        buffer.append("#endif \n");
	        
	        buffer.append("#include <");
	        buffer.append(join(file_separator, scope));
	        buffer.append(".h> \n");
	        
	        return buffer.toString();
	    }

	    protected String handleNamespace(String data_type, String local) {
	    		if ((flags & FLAG_CODA_INFO) != 0 &&
	    				current_node instanceof MStructDef) {
		    		if (data_type.equals("OpenNamespace")) {
		    			return "namespace " + (String) namespace.lastElement() + " {\n";
		    		}
		    		else if (data_type.equals("CloseNamespace")) {
		    			return "} // / namespace " + (String) namespace.lastElement() + "\n";
		    		}
	    		}
	    		return super.handleNamespace(data_type, local);
	    }

	protected boolean writeMakefile(File outDir, String fileDir,
                                    String extension, String content)
        throws IOException
    {
        boolean result;
        File makeFile = new File(outDir, fileDir);
        makeFile = new File(makeFile, "Makefile." + extension);

        if(!makeFile.isFile()) {
            writeFinalizedFile(fileDir, "Makefile." + extension, content);
            result = true;
        }
        else {
            result = false; // no Makefile.py written
        }
        return result;
    }
	

    protected String data_MStructDef(String data_type, String data_value)
    {
    		if ((flags & FLAG_CODA_INFO) == 0)
    			return data_value;

    		if (current_node instanceof MStructDef) {
        		if (data_type.equals("MFieldDefInclude")) {
        			List result = new ArrayList();
    				boolean found_something_we_dont_understand = false;
    				MStructDef sd = (MStructDef) current_node;
    				Iterator members = sd.getMembers().iterator();
    				for (int i=0; members.hasNext(); ++i) {
    					MFieldDef fd = (MFieldDef) members.next();
    					MIDLType idl_type = ((MTyped)fd).getIdlType();
    					if (idl_type instanceof MPrimitiveDef || idl_type instanceof MStringDef
    							|| idl_type instanceof MWstringDef) {
    						continue;
    					} 
    					else if (idl_type instanceof MAliasDef) {
    						MAliasDef ct = (MAliasDef) idl_type;
						MIDLType ctidl = ct.getIdlType();
						if (ctidl instanceof MPrimitiveDef || ctidl instanceof MStringDef
								|| ctidl instanceof MWstringDef) {
							continue;
						}
						else if (ctidl instanceof MSequenceDef) {
							MIDLType ctx = ((MSequenceDef) ctidl).getIdlType();
    							if (ctx instanceof MPrimitiveDef || ctx instanceof MStringDef
    									|| ctx instanceof MWstringDef) {
    								result.add("#include <vector>");
    								continue;
    							} else {
    								found_something_we_dont_understand = true;
    								break;
    							}
						} else {
							found_something_we_dont_understand = true;
							break;
						}
    					}
    					else if (idl_type instanceof MSequenceDef) {
    						MIDLType ctx = ((MSequenceDef)idl_type).getIdlType();
    						if (ctx instanceof MPrimitiveDef || ctx instanceof MStringDef
    								|| ctx instanceof MWstringDef) {
    							result.add("#include <vector>");
    							continue;
    						} else {
    							found_something_we_dont_understand = true;
    							break;
    						}
    					} else {
    						found_something_we_dont_understand = true;
    						break;
    					}
    				}
        			return found_something_we_dont_understand ? data_value : Text.join("\n",result);
        		}
        		else if (data_type.equals("MFieldDef")) {
    				String result = "";
    				String[] current_types = data_value.split("\n");
    				boolean found_complex_type = false;
    				MStructDef sd = (MStructDef) current_node;
    				Iterator members = sd.getMembers().iterator();
    				for (int i=0; members.hasNext(); ++i) {
    					MFieldDef fd = (MFieldDef) members.next();
    					MIDLType idl_type = ((MTyped)fd).getIdlType();
    					if (idl_type instanceof MPrimitiveDef || idl_type instanceof MStringDef
    							|| idl_type instanceof MWstringDef) {
    						result += current_types[i] + "\n";
    					} else {
    						found_complex_type = true;
    						result += "  " + getLanguageTypeStd(fd) + " " + fd.getIdentifier() + ";\n";
    					}
    				}
    				if (found_complex_type)
    					return result;
    			}
    		}
    				
        return data_value;
    }

	/**
     * Get a variable hash table sutable for filling in the template from the
     * fillTwoStepTemplates function. This version of the function fills in
     * operation information from the given interface.
     * 
     * @param operation
     *            the particular interface operation that we're filling in a
     *            template for.
     * @param container
     *            the container in which the given interface is defined.
     * @return a map containing the keys and values needed to fill in the
     *         template for this interface.
     */
    protected Map getTwoStepOperationVariables(MOperationDef operation,
                                               MContained container)
    {
    		String lang_type = getLanguageType(operation);
    		Map vars = new Hashtable();
    		Map mods = new HashMap();

    		vars.put("Object", container.getIdentifier());
    		vars.put("Identifier", operation.getIdentifier());
    		vars.put("LanguageType", lang_type);

        vars.put("MExceptionDefThrows", getOperationExcepts(operation));
        vars.put("MParameterDefAll", getOperationParams(operation));
        vars.put("MParameterDefAllStdDecl", getOperationParamsStd(operation, true, null));
        vars.put("MParameterDefAllStdDeclCoda", getOperationParamsStd(operation, true, "coda"));
        vars.put("MParameterDefAllStdImpl", getOperationParamsStd(operation, false, null));
        vars.put("MParameterDefAllStdImplCoda", getOperationParamsStd(operation, false, "coda"));
        vars.put("MParameterDefName", getOperationParamNames(operation, null));
 
        MIDLType idl_op_type = operation.getIdlType();
        if ((flags & FLAG_CODA_INFO) != 0) {
            Map wvars = new Hashtable();

            List pre = new ArrayList();
            StringBuffer post = new StringBuffer();
            for (Iterator params=operation.getParameters().iterator(); params.hasNext(); ) {
            		MParameterDef p = (MParameterDef) params.next();
            		MIDLType idl_type = ((MTyped) p).getIdlType();
    			
            		if (! (idl_type instanceof MPrimitiveDef) ||
            				((MPrimitiveDef)idl_type).getKind() == MPrimitiveKind.PK_ANY) {
            			String param_type = (idl_type instanceof MPrimitiveDef) ?
            									coda.getAnyType(operation.getDefinedIn().getIdentifier(),
            											operation.getIdentifier(),p.getIdentifier()) : getBaseIdlType(p);
            			Map conversion = coda.getConversionInfo(param_type,"coda");
            			if (! conversion.isEmpty() && conversion.containsKey("type") &&
            					conversion.containsKey("to") && conversion.containsKey("from")) {
            				
            				Map to = (Map) (((List)conversion.get("to")).get(0));
            				Map from = (Map) (((List)conversion.get("from")).get(0));

            				mods.put(p.getIdentifier(),
            						(from.containsKey("style") && (from.get("style").equals("pointer")) ? "*" : "") + 
            						p.getIdentifier() + "_coda_");
            				
            				String conv_type = (String) conversion.get("type");
            				
            				if (from.containsKey("style") && from.get("style").equals("pointer"))
            					pre.add(conv_type + " *" + p.getIdentifier() + "_coda_ = " +
            								from.get("func") + "( " + p.getIdentifier() + " );");
            				else {
            					pre.add(conv_type + " " + p.getIdentifier() + "_coda_;");
            					pre.add(from.get("func") + "( " + p.getIdentifier() + ", " + p.getIdentifier() + "_coda_ );");
            				}
            			}
            		}
    			}

            wvars.put(getScopeID("Object"), container.getIdentifier());
            if (idl_op_type instanceof MInterfaceDef) {
            		String override = coda.getTie("mapping",((MInterfaceDef)idl_op_type).getIdentifier());
            		if (override != null && override.length() > 0) {
            			wvars.put(getScopeID("QualifiedReturnObject"), "CCM_Session_" + override + "::" + override);
            			wvars.put(getScopeID("ReturnObject"), override);
            			wvars.put(getScopeID("AbsoluteLocalHomeName"), Text.join("_",namespace) + "_" + override + "Home");
            			// scoped type of object returned by wrapped function
            			wvars.put(getScopeID("WrappedReturnObject"), "::" + (String) namespace.lastElement() + "::" + override);
            		}
            		else {
            			wvars.put(getScopeID("QualifiedReturnObject"),"");
            			wvars.put(getScopeID("ReturnObject"),"");
            			wvars.put(getScopeID("AbsoluteLocalHomeName"),"");
            			wvars.put(getScopeID("WrappedReturnObject"),"");
            		}
            }
            else {
            		wvars.put(getScopeID("QualifiedReturnObject"),"");
            		wvars.put(getScopeID("ReturnObject"),"");
            		wvars.put(getScopeID("AbsoluteLocalHomeName"),"");
            		wvars.put(getScopeID("WrappedReturnObject"),"");
            }

            wvars.put(getScopeID("Identifier"), operation.getIdentifier());
            wvars.put(getScopeID("MParameterDefName"), getOperationParamNames(operation, mods));
            wvars.put(getScopeID("Return"), (lang_type.equals("void") ? "" : "return"));
            
            String catch_clause = coda.getCodeInfo("catch","code");
            if (catch_clause != null) {
            	wvars.put(getScopeID("Try"), "try {");
            	wvars.put(getScopeID("Catch"), "} " + catch_clause );
            } else {
            	wvars.put(getScopeID("Try"), "");
            	wvars.put(getScopeID("Catch"), "");
            }
            
            String ot = getBaseIdlType(idl_op_type);
            if (idl_op_type instanceof MPrimitiveDef &&
            		((MPrimitiveDef) idl_op_type).getKind() == MPrimitiveKind.PK_ANY) {
            		String op_type = coda.getAnyType(operation.getDefinedIn().getIdentifier(),operation.getIdentifier());
            		vars.put("LanguageTypeCoda", (op_type == null ? lang_type : coda.translateType(op_type,"coda") + "*"));
            		Map conversion = coda.getConversionInfo(op_type,"coda");
            		if (! conversion.isEmpty() && conversion.containsKey("type") &&
            				conversion.containsKey("to") && conversion.containsKey("from")) {
            			Map to = (Map) (((List)conversion.get("to")).get(0));
            			wvars.put(getScopeID("Convert"),to.get("func"));
            		} else
            			wvars.put(getScopeID("Convert"), "");
            } else if ( !(idl_op_type instanceof MPrimitiveDef) &&
            			coda.translateType(ot,"coda") != null) {
            		vars.put("LanguageTypeCoda", coda.translateType(ot,"coda") + "*");
            		Map conversion = coda.getConversionInfo(ot,"coda");
            		if (! conversion.isEmpty() && conversion.containsKey("type") &&
            			conversion.containsKey("to") && conversion.containsKey("from")) {
            		Map to = (Map) (((List)conversion.get("to")).get(0));
            		wvars.put(getScopeID("Convert"),to.get("func"));
        		} else
        			wvars.put(getScopeID("Convert"), "");
	
            } else {
            		vars.put("LanguageTypeCoda", getLanguageTypeStd(operation));
            		wvars.put(getScopeID("Convert"), "");
            }

            Template t = template_manager.getTemplate((idl_op_type instanceof MInterfaceDef ? "WrapperForwardCmptResult" : "WrapperForward"), current_name);
	    System.err.println(">>>>>>>>>> " + vars + " - " + t + " - " + wvvars + "\n";
            vars.put("WrapperForward", t.substituteVariables(wvars));
        		
            if (idl_op_type instanceof MInterfaceDef) {
				String override = coda.getTie("mapping",((MInterfaceDef)idl_op_type).getIdentifier());
				if (override != null && override.length() > 0) {
					Template fwddcl = template_manager.getTemplate("MOperationDefRetFwdDecl", current_name);
					if (fwddcl == null)
						throw new RuntimeException("couldn't find template for: MOperationDefRetFwdDecl");
					Map varmap = new Hashtable();
					varmap.put(getScopeID("ResultType"), override);
					vars.put("MOperationDefRetFwdDecl", fwddcl.substituteVariables(varmap));
					Template geninc = template_manager.getTemplate("MOperationDefRetIncludeGen", current_name);
					vars.put("MOperationDefRetIncludeGen", geninc.substituteVariables(varmap));
				}
				else	 {
					vars.put("MOperationDefRetFwdDecl","");
					vars.put("MOperationDefRetIncludeGen","");
				}
            }
            else {
            		vars.put("MOperationDefRetFwdDecl","");
            		vars.put("MOperationDefRetIncludeGen","");
            }

            vars.put("CodaParameterConvertPre",Text.join("\n    ",pre));
            vars.put("CodaParameterConvertPost",post.toString());

		}
		else {
			vars.put("CodaParameterConvertPre","");
			vars.put("CodaParameterConvertPost","");
			vars.put("WrapperForward","?// TODO : IMPLEMENT ME HERE !");
		}

        String gen_return = "";
        String cmpt_return = "";
        if ((flags & FLAG_CODA_INFO) != 0 && idl_op_type instanceof MInterfaceDef) {
    			String override = coda.getTie("mapping",((MInterfaceDef)idl_op_type).getIdentifier());
    			if (override != null && override.length() > 0) {
    				gen_return = "#include \"CCM_Local/" + namespace.lastElement() + "/CCM_Session_" + override + "/" + override + "_gen.h\"\n";
    				cmpt_return = "#include \"impl/" + override + "_cmpt.h\"\n";
    			}
        }
        vars.put("CmptResultGenInclude",gen_return);
        vars.put("CmptResultCmptInclude",cmpt_return);

        if(!lang_type.equals("void"))
            vars.put("Return", "return ");
        else
            vars.put("Return", "");

        return vars;
    }

	protected String getLocalValue(String variable) {
    	
    		if (variable.equals("CodaCtor")) {
    			if ((flags & FLAG_CODA_INFO) == 0 ||
    					! (current_node instanceof MStructDef))
    				return "";
    			MStructDef sd = (MStructDef) current_node;
    			List args = new ArrayList();
    			List inits = new ArrayList();
    			Iterator members = sd.getMembers().iterator();
    			for (int i=0; members.hasNext(); ++i) {
    				MFieldDef fd = (MFieldDef) members.next();
    				args.add(getLanguageTypeStd(fd) + " arg" + i);
    				inits.add(fd.getIdentifier() + "(arg" + i + ")");
    			}
    			return "  " + sd.getIdentifier() + "(" + Text.join(", ",args) + ")" +
    					" : " + Text.join(", ",inits) + " { }";
    		} else if (variable.equals("MacroDefs")) {
    			if ((flags & FLAG_CODA_INFO) == 0) 
    				return "";
    			Map macros = coda.getMacros();
    			Iterator keys = macros.keySet().iterator();
    			List result = new ArrayList();
    			while (keys.hasNext()) {
    				String key = (String) keys.next();
    				String val = (String) macros.get(key);
    				result.add("#define " + key + (val != null ? " " + val : ""));
    			}
    			return join("\n", result);
    		}
//    		if ((flags & FLAG_CODA_INFO) != 0 &&
//    				variable.equals("LanguageTypeInclude")) {
//    			if(current_node instanceof MTyped) {
//    				MIDLType idl_type = ((MTyped) current_node).getIdlType();

//    				if(idl_type instanceof MTypedefDef) {
//    					MTypedefDef mt = (MTypedefDef) idl_type;
//    					if (mt instanceof MAliasDef) {
//    						MAliasDef ct = (MAliasDef) mt;
//    						MIDLType ctidl = ct.getIdlType();
//    						if (ctidl instanceof MPrimitiveDef || ctidl instanceof MStringDef
//    								|| ctidl instanceof MWstringDef)
//    							return "";
//    						else if (ctidl instanceof MSequenceDef) {
//    							MIDLType ctx = ((MSequenceDef)ctidl).getIdlType();
//    							if (ctx instanceof MPrimitiveDef || ctx instanceof MStringDef
//    									|| ctx instanceof MWstringDef)
//    								return "";
//    						}
//    					}
//    				}
//            }
//    		}
    		String result = super.getLocalValue(variable);
//    		if (variable.equals("LanguageTypeInclude")) {
//    			if (result.length() > 0) {
//    				String key = getScopeID("ShadowLanguageTypeInclude");
//    				output_variables.put(key,result);
//    				return result;
//    			}
//    		}
    		return result;
    }
    /** *********************************************************************** */

    private String getOutputDirectory(String local)
    {
        List names = new ArrayList(namespace);
        if(!local.equals("")) {
            names.add("CCM_Session_" + local);
        }
        return join("/", names);
    }

    /**
     * Create a list of lists of pathname components for output files needed by
     * the current node type.
     * 
     * @return a list of List objects containing file names for all output files
     *         to be generated for the current node.
     */
    private List getOutputFiles()
    {
        String node_name = ((MContained) current_node).getIdentifier();

        List files = new ArrayList();
        List f = null;

        if((current_node instanceof MComponentDef)
                || (current_node instanceof MHomeDef)) {
            String base_name = node_name;

            // we put home files in the dir with the component files to convince
            // confix to work with us. beware the evil voodoo that results when
            // home and component files are in separate directories !

            if(current_node instanceof MHomeDef) {
                base_name = ((MHomeDef) current_node).getComponent()
                        .getIdentifier();
            }
            String base = getOutputDirectory(base_name);

            f = new ArrayList();
            f.add(base);
            f.add(node_name + "_gen.h");
            files.add(f);
            f = new ArrayList();
            f.add(base);
            f.add(node_name + "_gen.cc");
            files.add(f);

            f = new ArrayList();
//            f.add(base + "_share");
            f.add(base);
            f.add(node_name + "_share.h");
            files.add(f);

            if(current_node instanceof MHomeDef) {
                f = new ArrayList();
                f.add("impl");
                f.add(node_name + "_entry.h");
                files.add(f);
            }

            if((flags & FLAG_APPLICATION_FILES) != 0) {
                f = new ArrayList();
                f.add("impl");
                f.add(node_name + "_impl.h");
                files.add(f);
                f = new ArrayList();
                f.add("impl");
                f.add(node_name + "_impl.cc");
                files.add(f);
                // ccmtools independent wrapper files
                if (( flags & FLAG_CODA_INFO) != 0) {
                    f = new ArrayList();
                    f.add("impl");
                    f.add(node_name + "_cmpt.h");
                    files.add(f);
                    f = new ArrayList();
                    f.add("impl");
                    f.add(node_name + "_cmpt.cc");
                    files.add(f);
                }
                else {
                    f = new ArrayList();
                    f.add("impl");
                    f.add("");
                    files.add(f);
                    f = new ArrayList();
                    f.add("impl");
                    f.add("");
                    files.add(f);
                }
            }
            else {
                f = new ArrayList();
                f.add("impl");
                f.add("");
                files.add(f);
                f = new ArrayList();
                f.add("impl");
                f.add("");
                files.add(f);
            }
        } else if((current_node instanceof MStructDef) ||
        			 (current_node instanceof MModuleDef)) {
        		if (( flags & FLAG_CODA_INFO) != 0) {
        			f = new ArrayList();
        			f.add("impl/" + (current_node instanceof MModuleDef ? node_name : (String) namespace.lastElement()));
        			f.add(node_name + ".h");
        			files.add(f);
        			if (current_node instanceof MStructDef) {
        				f = new ArrayList();
        				f.add(getOutputDirectory(""));
        				f.add(node_name + ".h");
        				files.add(f);
        			}
        		} else {
        			f = new ArrayList();
        			f.add(getOutputDirectory(""));
        			f.add(node_name + ".h");
        			files.add(f);
        			f = new ArrayList();
        			f.add("impl");
        			f.add("");
        			files.add(f);
        		}
        }
        else if((current_node instanceof MInterfaceDef)
                || (current_node instanceof MUnionDef)
                || (current_node instanceof MAliasDef)
                || (current_node instanceof MEnumDef)
                || (current_node instanceof MExceptionDef)) {
            f = new ArrayList();
            f.add(getOutputDirectory(""));
            f.add(node_name + ".h");
            files.add(f);
        }
        else if((current_node instanceof MProvidesDef)) {
            if((flags & FLAG_APPLICATION_FILES) != 0) {
                MComponentDef component = ((MProvidesDef) current_node)
                        .getComponent();
                f = new ArrayList();
                f.add("impl");
                f.add(component.getIdentifier() + "_" + node_name + "_impl.h");
                files.add(f);
                f = new ArrayList();
                f.add("impl");
                f.add(component.getIdentifier() + "_" + node_name + "_impl.cc");
                files.add(f);
                // ccmtools independent wrapper files
                if (( flags & FLAG_CODA_INFO) != 0) {
                    f = new ArrayList();
                    f.add("impl");
                    f.add(component.getIdentifier() + "_cmpt.h");
                    files.add(f);
                    f = new ArrayList();
                    f.add("impl");
                    f.add(component.getIdentifier() + "_cmpt.cc");
                    files.add(f);
                }
                else {
                    f = new ArrayList();
                    f.add("impl");
                    f.add("");
                    files.add(f);
                    f = new ArrayList();
                    f.add("impl");
                    f.add("");
                    files.add(f);
                }

            }
            else {
                f = new ArrayList();
                f.add("impl");
                f.add("");
                files.add(f);
                f = new ArrayList();
                f.add("impl");
                f.add("");
                files.add(f);
            }
        }
        else {
            f = new ArrayList();
            f.add("");
            f.add("");
            files.add(f);
        }

        return files;
    }
}
