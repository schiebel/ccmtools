package ccmtools.Coda;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import org.apache.xerces.parsers.SAXParser;

//import com.sleepycat.dbxml.XmlContainer;
//import com.sleepycat.dbxml.XmlDocumentConfig;
//import com.sleepycat.dbxml.XmlException;
//import com.sleepycat.dbxml.XmlManager;
//import com.sleepycat.dbxml.XmlModify;
//import com.sleepycat.dbxml.XmlQueryContext;
//import com.sleepycat.dbxml.XmlQueryExpression;
//import com.sleepycat.dbxml.XmlResults;
//import com.sleepycat.dbxml.XmlUpdateContext;
//import com.sleepycat.dbxml.XmlValue;
//import com.sleepycat.dbxml.XmlDocument;

import ccmtools.utils.Text;


public class XMLFileManager extends CodaManager {
	private String directory = null;
	private static Map components = new HashMap();
	private static Map conversions = new HashMap();
	private static Map polymorphs = new HashMap();
	private static Map any_conversions = new HashMap();
	private static Map exports = new HashMap();
	private static Map macros = new HashMap();
	private static List loaded = new ArrayList( );
	private static Map privates = new HashMap();
	private static Map substitute = new HashMap();
	private static Map implement = new HashMap();

	private static String export_file = null;
//	private static XmlManager export_mgr = null;
//	private static XmlContainer export_container = null;
	private static boolean export_aborted = false;
//	private static Map exported = null;
//	private static long state_index = 0;
	
    public static String join(Collection s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }
    public static String replaceAll( String source, String toReplace, String replacement ) {
    	int idx = source.lastIndexOf( toReplace );
    	if ( idx != -1 ) {
    		StringBuffer ret = new StringBuffer( source );
    		ret.replace( idx, idx+toReplace.length(), replacement );
    		while( (idx=source.lastIndexOf(toReplace, idx-1)) != -1 ) {
    			ret.replace( idx, idx+toReplace.length(), replacement );
    		}
    		return ret.toString();
    	}
    	return source;
    }
    
	private static class CodaFileReader extends FileReader {
		public CodaFileReader(File file) throws FileNotFoundException { super(file); }
		public CodaFileReader(FileDescriptor fd) { super(fd); }
		public CodaFileReader(String fileName) throws FileNotFoundException { super(fileName); }
		public int read( ) throws IOException {
			return super.read( );
		}
		public int read( char[] cbuf, int offset, int length) throws IOException {
			int result = super.read(cbuf, offset, length);
			return result;
		}
		public boolean ready( ) throws IOException {
			return super.ready( );
		}
	}
	
	public XMLFileManager(String setup_file) {
		super();
		if ( setup_file != null &&
				setup_file != "" ) {
			File f = new File(setup_file);
			if (f.exists() && f.isFile() && f.canRead()) {
				XMLReader xr = new SAXParser( );
				SetupReader handler = new SetupReader( );
				xr.setContentHandler(handler);
				xr.setErrorHandler(handler);
				try {
					FileReader r = new FileReader(setup_file);
					xr.parse(new InputSource(r));
				} catch (SAXParseException pe) {
					System.out.println(">>>> SAX Exception occurred while reading: " + f.getPath() + " (line " + pe.getLineNumber() + ")\n");
					System.out.println("     " + pe.getLocalizedMessage() );	
				} catch (SAXException e) {
					System.out.println(">>>> SAX Exception occurred while reading: " + f.getPath() + "\n");
					System.out.println("     " + e.getLocalizedMessage() );
				} catch (RuntimeException rte) {
					System.out.println(">>>> SAX/XML-consistency error occurred while reading: " + f.getPath() + "\n");
					System.out.println("     " + rte.getLocalizedMessage() );
				} catch (Exception file_not_found) {
					// we checked...
				}
			}
		}
	}
	
	protected void loadComponent(String component) {
		if (loaded.contains(component)) return;
		loaded.add(component);
		String file = (directory != null ? (directory + File.separator) : "")
						+ component + ".xml";
		File f = new File(file);
		if (f.exists() && f.isFile() && f.canRead()) {
			XMLReader xr = new SAXParser( );
			ComponentReader handler = new ComponentReader( );
			xr.setContentHandler(handler);
			xr.setErrorHandler(handler);
			try {
				FileReader r = new CodaFileReader(file);
				xr.parse(new InputSource(r));
			} catch (SAXParseException pe) {
				System.out.println(">>>> SAX Exception occurred while reading:" + f.getPath() + " (line " + pe.getLineNumber() + ")\n");
				System.out.println("     " + pe.getLocalizedMessage() );	
			} catch (SAXException e) {
				System.out.println(">>>> SAX Exception occurred while reading:" + f.getPath() + "\n");
				System.out.println("     " + e.getLocalizedMessage() );
			} catch (RuntimeException rte) {
				System.out.println(">>>> SAX/XML-consistency error occurred while reading: " + f.getPath() + "\n");
				System.out.println("     " + rte.getLocalizedMessage() );
			} catch (Exception file_not_found) {
				// we checked...
			}
			
		}
	}

	public Map getParamInfo(String component, String method, String param) {
		if (!loaded.contains(component))
			loadComponent(component);
		if (components.containsKey(component)) {
			Map cmpt = (Map) components.get(component);
			if (cmpt.containsKey("methods")) {
				Map methods = (Map) cmpt.get("methods");
				if (methods.containsKey(method)) {
					Map m = (Map) methods.get(method);
					if (m.containsKey("params")) {
						Map parameters = (Map) m.get("params");
						if (parameters.containsKey(param))
							return (Map) parameters.get(param);
					}
				}
			}
		}
		return null;
	}

    public Map getMethodInfo(String component, String method) {
        if (!loaded.contains(component))
                loadComponent(component);
        if (components.containsKey(component)) {
        		Map cmpt = (Map) components.get(component);
        		if (cmpt.containsKey("methods")) {
        			Map methods = (Map) cmpt.get("methods");
        			if (methods.containsKey(method))
                        return (Map) methods.get(method);
        		}
        }
        return null;
}

	public Map getMethodDoc(String component, String method) {
		if (!loaded.contains(component))
			loadComponent(component);
		if (components.containsKey(component)) {
			Map cmpt = (Map) components.get(component);
			if (cmpt.containsKey("methods")) {
				Map methods = (Map) cmpt.get("methods");
				if (methods.containsKey(method)) {
					Map methodm = (Map) methods.get(method);
					if (methodm.containsKey("desc")) {
						return (Map) methodm.get("desc");
					}
				}
			}
		}
		return null;
	}
	
	public String getAnyType(String component, String method, String param) {
		if (!loaded.contains(component))
			loadComponent(component);
		if (components.containsKey(component)) {
			Map cmpt = (Map) components.get(component);
			if (cmpt.containsKey("methods")) {
				Map methods = (Map) cmpt.get("methods");
				if (methods.containsKey(method)) {
					Map methodm = (Map) methods.get(method);
					if (methodm.containsKey("params")) {
						Map params = (Map) methodm.get("params");
						if (params.containsKey(param)) {
							Map paraminfo = (Map) params.get(param);
							if (paraminfo.containsKey("any-type")) {
								return (String) paraminfo.get("any-type");	
							}
						}
					}
				}
			}	
		}
		return null;
	}

	public String getAnyType(String component, String method) {
		if (!loaded.contains(component))
			loadComponent(component);
		if (components.containsKey(component)) {
			Map cmpt = (Map) components.get(component);
			if (cmpt.containsKey("methods")) {
				Map methods = (Map) cmpt.get("methods");
				if (methods.containsKey(method)) {
					Map methodinfo = (Map) methods.get(method);
					if (methodinfo.containsKey("anytype")) {
						return (String) methodinfo.get("anytype");
					}
				}
			}
		}
		return null;
	}

	public Map getConversionInfo(String type, String target) {
		if (conversions.containsKey(type)) {
			Map type_map = (Map) conversions.get(type);
			if (type_map.containsKey(target))
				return (Map) type_map.get(target);
		}
		return new HashMap();
	}
	
	public boolean doExportDefaults( ) {
		return export_file != null && export_aborted == false;
	}
	
	public void exportDefault(String component, String op, String param, String xmldflt) {
		
//		if (xmldflt == null || xmldflt.length() == 0)
//			xmldflt = "<value unset=\"true\"" + (param != null && param.length() > 0 ? " param=\"" + param + "\"" : "") + " />";
//
//		// export wasn't specified in coda setup file
//		if ( export_file == null ) return;
//
//		// database uninitialized
//		if ( export_container == null && export_aborted == false ) {
//			try {
//				export_mgr = new XmlManager();
//				File out = new File(export_file);
//				if (out.exists())
//					export_container = export_mgr.openContainer(export_file);
//				else
//					export_container = export_mgr.createContainer(export_file);
//			} catch (XmlException e) {
//				System.out.println(">>>> dbxml " + e.getMessage());
//				export_aborted = true;
//
//				if (export_mgr != null) {
//					try {
//						export_mgr.close();
//					} catch (XmlException e1) {  }
//					export_mgr = null;
//				}
//			} catch (FileNotFoundException e) {
//				export_aborted = true;
//				System.out.println(">>>> dbxml file does not exist");
//				
//				if (export_mgr != null) {
//					try {
//						export_mgr.close();
//					} catch (XmlException e1) { }
//					export_mgr = null;
//				}
//			}
//			
//			if (export_container != null) {
//				try {
//					XmlUpdateContext ucontext = export_mgr.createUpdateContext();
//					state_index += 1;
//					export_container.putDocument("casa-sig","<casasig><type>system</type><version>1</version></casasig>", ucontext, null);
//				} catch(XmlException e) {
//					System.out.println(">>>> error creating component \"" + component + "\" document in defaults db");
//				}
//			}
//		}
//		
//		
//
//		// add component document
//		if ( exported == null ) {
//			exported = new HashMap();
//		}
//		
//		if (exported.containsKey(component + "*" + op + "*" + param))
//			return;
//		
//		exported.put(component + "*" + op + "*" + param, "1");
//
//		try {
//			// do we have a node for this 'op'?
//			XmlQueryContext context = export_mgr.createQueryContext();
//			XmlQueryExpression expr = export_mgr.prepare("collection('" + export_file + "')/components/component[name='" + component + "']/methods/op[name='" + op + "']/param/state", context);
//			XmlResults opr = expr.execute(context);
//			
//			if (opr.hasNext()) {
//				// we need to append new param
//				context = export_mgr.createQueryContext();
//				XmlUpdateContext ucontext = export_mgr.createUpdateContext();
//				String qs = "/components/component[name='" + component + "']/methods/op[name='" + op + "']";
//				XmlQueryExpression select = export_mgr.prepare(qs, context);
//				XmlModify mod = export_mgr.createModify();
//				state_index += 1;
//				// here we use '0' as the "location" because we get the parameters in the reverse order
//				mod.addAppendStep(select,XmlModify.Element, "param", "<name>" + param + "</name><state>" + xmldflt + "<index>" + state_index +
//															"</index><invocation>0</invocation><tag>system</tag><tag>current</tag></state>", 0);
//				XmlDocument doc = export_container.getDocument(component);
//				XmlValue docval = new XmlValue(doc);
//				mod.execute(docval,context,ucontext);
//
//			} else {
//				context = export_mgr.createQueryContext();
//				expr = export_mgr.prepare("collection('" + export_file + "')/components/component[name='" + component + "']", context);
//				XmlResults cmr = expr.execute(context);
//				if (cmr.hasNext()) {
//					// we need to insert new op
//					XmlQueryContext scontext = export_mgr.createQueryContext();
//					XmlUpdateContext ucontext = export_mgr.createUpdateContext();
//					XmlQueryExpression select = export_mgr.prepare( "/components/component[name='" + component + "']/methods", scontext );
//					XmlModify mod = export_mgr.createModify();
//					state_index += 1;
//					mod.addAppendStep(select,XmlModify.Element, "op","<name>" + op + "</name><param><name>" + param + "</name><state>" + xmldflt + "<index>" + 
//															state_index + "</index><invocation>0</invocation><tag>system</tag><tag>current</tag></state></param>");
//					XmlDocument doc = export_container.getDocument(component);
//					XmlValue docval = new XmlValue(doc);
//					mod.execute(docval,scontext,ucontext);
//				} else {
//					// we need to insert new component
//					try {
//						XmlUpdateContext ucontext = export_mgr.createUpdateContext();
//						state_index += 1;
//						export_container.putDocument(component,"<components><component><name>" + component + "</name>" + 
//														"<invocation_count>0</invocation_count>" +
//														"<methods><op><name>" + op + "</name>" + 
//														"<param><name>" + param + "</name>" + 
//														"<state>" + xmldflt + "<index>" + state_index + "</index>" + 
//														"<invocation>0</invocation><tag>system</tag><tag>current</tag></state></param></op></methods></component></components>", ucontext, null);
//					} catch(XmlException e) {
//						System.out.println(">>>> error creating component \"" + component + "\" document in defaults db");
//					}
//				}
//			}
//		} catch (XmlException e) {
//			System.out.println(">>>> dbxml " + e.getMessage());
//		}	
	}
	
	public void close( ) {
//		try {
//			if (export_container != null) {
//				// save our index
//				XmlQueryContext scontext = export_mgr.createQueryContext();
//				XmlUpdateContext ucontext = export_mgr.createUpdateContext();
//				XmlQueryExpression select = export_mgr.prepare( "/casasig", scontext );
//				XmlModify mod = export_mgr.createModify();
//				state_index += 1;
//				mod.addAppendStep(select,XmlModify.Element, "index",String.valueOf(state_index));
//				XmlDocument doc = export_container.getDocument("casa-sig");
//				XmlValue docval = new XmlValue(doc);
//				mod.execute(docval,scontext,ucontext);
//
//				export_container.close();
//			}
//			if (export_mgr != null)
//				export_mgr.close();
//		} catch (XmlException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
	public Map getAnyInfo(String transform_id, String target) {
		if (any_conversions.containsKey(transform_id)) {
			Map type_map = (Map) any_conversions.get(transform_id);
			if (type_map.containsKey(target))
				return (Map) type_map.get(target);
		}
		return new HashMap();
	}
	
	public String getCodeInfo(String type,String section) {
		if (type.equals("catch")) {
			if (implement.containsKey(type)) {
				Map values = (Map) implement.get(type);
				if (values.containsKey(section)) {
					List items = (List) values.get(section);
					String ret="";
					if (section.equals("include")) {
						for (Iterator i=items.iterator(); i.hasNext(); ) {
							ret += "#include <" + i.next() + ">\n";
						}
					} else {
						return Text.join("\n",items);
					}
					return ret;	
				}
			}
			return null;
		}
		if (privates.containsKey(type) &&
				((Map)privates.get(type)).containsKey(section))
			return (String) ((Map)privates.get(type)).get(section);
		return null;
	}
	
	public Map getPolymorphInfo(String type, String target) {
		if (polymorphs.containsKey(type)) {
			Map type_map = (Map) polymorphs.get(type);
			if (type_map.containsKey(target))
				return (Map) type_map.get(target);
		}
		return new HashMap();
	}
	
	public Map getExports( ) {
		return exports;
	}
	
	public Map getMacros( ) {
		return macros;
	}
	
	public String translateType(String type, String target) {
		if (conversions.containsKey(type)) {
			Map type_map = (Map) conversions.get(type);
			if (type_map.containsKey(target)) {
				Map target_map = (Map) type_map.get(target);
				if (target_map.containsKey("type"))
					return (String) target_map.get("type");
			}
		}
		return null;
	}
	
	public Map getInclude(String type, String target, String include_use) {
		if (include_use == null)
			return new HashMap();
		
		if (conversions.containsKey(type)) {
			Map type_map = (Map) conversions.get(type);
			if (type_map.containsKey(target)) {
				Map target_map = (Map) type_map.get(target);
				if (target_map.containsKey("include")) {
					Map include_map = (Map) target_map.get("include");
					HashMap ret = new HashMap();
					if (include_use.equals("all")) {
						for (Iterator i=include_map.values().iterator(); i.hasNext(); )
							ret.putAll((Map)i.next());
					}
					else if (include_map.containsKey(include_use) || include_map.containsKey("all")) {
						Map use = (Map) include_map.get(include_use);
						Map all = (Map) include_map.get("all");
						
						if (all != null)
							ret.putAll(all);
						if (use != null)
							ret.putAll(use);
					}
					return ret;
				}
			}
		}
		return new HashMap();
	}
	
	public String getTie(String tie_type, String component, String name) {
		String result = null;
		if (substitute.containsKey(tie_type)) {
			Map tie = (Map) substitute.get(tie_type);
			if (tie.containsKey(component)) {
				Map info = (Map) tie.get(component);
				result = (String) info.get(name);
			}
		}
		return result;
	}
	
	public String getTie(String tie_type, String name) {
		String result = null;
		if (substitute.containsKey(tie_type)) {
			Map tie = (Map) substitute.get(tie_type);
			result = (String) tie.get(name);
		}
		return result;
	}
	
	public static void main(String args[]) throws Exception {
		XMLReader xr = new SAXParser( );
		ComponentReader handler = new ComponentReader( );
		xr.setContentHandler(handler);
		xr.setErrorHandler(handler);
		FileReader r = new FileReader(args[0]);
		xr.parse(new InputSource(r));

		for (Iterator i=components.keySet().iterator(); i.hasNext(); ) {
			String key = (String) i.next( );
			System.out.println(key + ":");
			Map cmpt = (Map) components.get(key);
			Map methods = (Map) cmpt.get("methods");
			for (Iterator j=methods.keySet().iterator(); j.hasNext(); ) {
				String m = (String) j.next( );
				System.out.println("\t" + m + ":");
				Map params = (Map) ((Map) methods.get(m)).get("params");
				for (Iterator k=params.keySet().iterator(); k.hasNext(); ) {
					String p = (String)(((Map) k.next( )).get("default"));
					System.out.println("\t\t" + p + ": " + ((String)params.get(p)));
				}
			}
		}
	}
	
	
	private static class SetupReader extends DefaultHandler {

		private boolean collecting_contents = false;
		private boolean collecting_polymorph = false;
		private boolean collecting_polymorph_target = false;
		private boolean collecting_polymorph_include = false;
		private boolean collecting_polymorph_type = false;
		private boolean collecting_polymorph_mod = false;
		private boolean collecting_polymorph_mod_detail = false;
		
		private boolean collecting_implement = false;
		private boolean collecting_implement_catch = false;
		private boolean collecting_implement_catch_value = false;
		private String collecting_implement_catch_type = "";
		private List catch_code = null;
		private List catch_include = null;
		
		private String polymorph_tag = "";
		private String polymorph_type = "";
		private String polymorph_target = "";
		private String polymorph_mod_direction = "";
		private String polymorph_mod_detail = "";
		
		private Map polymorph_include = null;
		private Map polymorph_include_type = null;
		private Map polymorph_include_convert = null;
		private String polymorph_include_use = "";
		private Map polymorph_mod = null;
		private Map polymorph = null;
		
		private String contents = "";
		
		public SetupReader( ) { super(); }
		
		public void startDocument( ) { 
			collecting_contents = false;
			collecting_polymorph = false;
			collecting_polymorph_target = false;
			collecting_polymorph_include = false;
			collecting_polymorph_mod = false;
			collecting_polymorph_mod_detail = false;
		}
		
		public void endDocument( ) { }
		
		public void startElement(String uri, String name, String qName, Attributes attr) {
			String new_element = ("".equals(uri) ? qName : name);
			
			if ( collecting_polymorph_include || collecting_polymorph_mod_detail ) {
				throw new RuntimeException("tag found within a parameter " +
						(collecting_polymorph_include ? "include for polymorph" : 
											collecting_polymorph_mod_detail ? "polymorph detail" : "<>"));
			} else if ( collecting_implement_catch_value ) {
				throw new RuntimeException("tag found within " + collecting_implement_catch_type +
											" catch detail");
			} else if (collecting_polymorph_mod) {
				if (new_element.equals("check")) {
					collecting_polymorph_mod_detail = true;
					polymorph_mod_detail = "check";
					collecting_contents = true;
					contents = "";
				} else if (new_element.equals("func")) {
					collecting_polymorph_mod_detail = true;
					polymorph_mod_detail = "func";
					collecting_contents = true;
					contents = "";
				}
			} else if (collecting_polymorph_target) {
				if (new_element.equals("include")) {
					contents = "";
					collecting_contents = true;
					collecting_polymorph_include = true;
					polymorph_include_use = attr.getValue("use");
					polymorph_include_use = polymorph_include_use == null ? "all" : polymorph_include_use;
				} else if (new_element.equals("type")) {
					contents = "";
					collecting_contents = true;
					collecting_polymorph_type = true;
				} else if (new_element.equals("to")) {
					collecting_polymorph_mod = true;
					polymorph_mod_direction = "to";
					polymorph_mod = new HashMap();
					String style = attr.getValue("style");
					if (style == null)
						polymorph_mod.put("style","reference");
					else
						polymorph_mod.put("style",style);
				} else if (new_element.equals("from")) {
					collecting_polymorph_mod = true;
					polymorph_mod_direction = "from";
					polymorph_mod = new HashMap();
					String style = attr.getValue("style");
					if (style == null)
						polymorph_mod.put("style","reference");
					else
						polymorph_mod.put("style",style);
				} else if (new_element.equals("init")) {
					collecting_polymorph_mod = true;
					polymorph_mod_direction = "init";
					polymorph_mod = new HashMap();
				}
			} else if (collecting_polymorph) {
				if (new_element.equals("target")) {
					if (attr.getValue("name").length() > 0) {
						collecting_polymorph_target = true;
						polymorph_target = attr.getValue("name");
						polymorph = new HashMap();
						polymorph_include = new HashMap();
					} else
						throw new RuntimeException("encountered " + new_element + " tag with no 'name' attribute");
				}
			} else if ( collecting_implement_catch ) {
				if (new_element.equals("include") || new_element.equals("code")) {
					contents = "";
					collecting_contents = true;
					collecting_implement_catch_value = true;
					collecting_implement_catch_type = new_element;
				}
			} else if ( collecting_implement ) {
				if ( new_element.equals("catch")) {
					collecting_implement_catch = true;
					catch_code = new ArrayList();
					catch_include = new ArrayList();
				}
			}
			else if (new_element.equals("export")) {
				if (attr.getValue("type").length() > 0 &&
						attr.getValue("name").length() > 0) {
					exports.put(attr.getValue("type"), attr.getValue("name"));
				}
			} else if (new_element.equals("macro")) {
				if (attr.getValue("name").length() > 0) {
					macros.put(attr.getValue("name"), attr.getValue("value"));
				}
			} else if (new_element.equals("polymorph") ||
						new_element.equals("conversion") ||
						new_element.equals("any")) {
				if (attr.getValue("type").length() > 0) {
					collecting_polymorph = true;
					polymorph_tag = new_element;
					polymorph_type = attr.getValue("type");
				} else
					throw new RuntimeException("encountered " + new_element + " tag with no 'type' attribute");
			} else if (new_element.equals("substitute")) {
				if (attr.getValue("interface").length() > 0 &&
						attr.getValue("as").length() > 0 &&
						attr.getValue("in").length() > 0) {
					if (! substitute.containsKey("interface"))
						substitute.put("interface",new HashMap());
					Map tiedif = (Map) substitute.get("interface");
					String component = attr.getValue("in");
					if (! tiedif.containsKey(component))
						tiedif.put(component,new HashMap());
					Map component_info = (Map) tiedif.get(component);
					String ifname = attr.getValue("interface");
					String ifto = attr.getValue("as");
					component_info.put(ifname,ifto);
					if (! substitute.containsKey("mapping"))
						substitute.put("mapping",new HashMap());
					Map tiedmp = (Map) substitute.get("mapping");
					tiedmp.put(ifname,ifto);
				}
			} else if ( new_element.equals("implement")) {
				collecting_implement = true;
			} else if ( new_element.equals("output") ) {
				String type = attr.getValue("type");
				if (type != null) {
					if (type.equals("defaults")) {
						String attrfile = attr.getValue("filename");
						if (attrfile == null) attrfile = "defaults.dbxml";

						File f = new File(attrfile);
						File fa = new File(f.getAbsolutePath());
						File d = fa.getParentFile();
						if (f.exists() && f.canWrite()) {
							f.delete();
						}
						if (d != null && d.exists() && d.canWrite()) {
							export_file = f.getAbsolutePath();
						} else {
							System.out.println(">>>> Discarded output tag: file " + f.getPath() + "does not exist, is not writable or cannot be created\n");
						}
					} else {
						System.out.println(">>>> Discarded output tag: unknown output type \"" + type + "\"\n");
					}
				} else {
					System.out.println(">>>> Discarded output tag: missing type attribute tag");
				}
			}
		}
		
		public void endElement(String uri, String name, String qName) {
			String new_element = ("".equals(uri) ? qName : name);
			collecting_contents = false;
			if (collecting_polymorph_mod_detail) {
				if (new_element.equals(polymorph_mod_detail)) {
					polymorph_mod.put(polymorph_mod_detail,contents);
					collecting_polymorph_mod_detail = false;
				} else
					throw new RuntimeException("internal inconsistency: found </" + new_element + ">, but collecting <" + polymorph_mod_detail + ">");
			} else if (collecting_polymorph_include) {
				if (new_element.equals("include")) {
					if (! polymorph_include.containsKey(polymorph_include_use))
						polymorph_include.put(polymorph_include_use,new HashMap());
					((Map)polymorph_include.get(polymorph_include_use)).put("#include <" + contents + ">","");
					collecting_polymorph_include = false;
				} else
					throw new RuntimeException("internal inconsistency: found </" + new_element + ">, but collecting <include>");
			} else if (collecting_polymorph_type) {
				if (new_element.equals("type")) {
					polymorph.put("type",contents);
					collecting_polymorph_type = false;
				} else
					throw new RuntimeException("internal inconsistency: found </" + new_element + ">, but collecting <type>");
			} else if (collecting_polymorph_mod) {
				if (new_element.equals(polymorph_mod_direction)) {
					if (! polymorph.containsKey(polymorph_mod_direction)) {
						polymorph.put(polymorph_mod_direction,new ArrayList());
					}
					((List)polymorph.get(polymorph_mod_direction)).add(polymorph_mod);
					collecting_polymorph_mod = false;
				} else
					throw new RuntimeException("internal inconsistency: found </" + new_element + ">, but collecting <" + polymorph_mod_direction + ">");
			} else if (new_element.equals("target")) {
				if (! collecting_polymorph_target)
					throw new RuntimeException("internal inconsistency: found </target>, but not collecting target");
				collecting_polymorph_target = false;
				polymorph.put("include",polymorph_include);
				if (polymorph_tag.equals("polymorph")) {
					if (! polymorphs.containsKey(polymorph_type)) {
						polymorphs.put(polymorph_type,new HashMap());
						((Map)polymorphs.get(polymorph_type)).put(polymorph_target,polymorph);
					} else {
						Object merged = merge_polymorph(((Map)polymorphs.get(polymorph_type)).get(polymorph_target),polymorph);
						((Map)polymorphs.get(polymorph_type)).put(polymorph_target,merged);
					}
//					if (! ((Map)polymorphs.get(polymorph_type)).containsKey(polymorph_target))
//						((Map)polymorphs.get(polymorph_type)).put(polymorph_target,new ArrayList());
//					((List) ((Map)polymorphs.get(polymorph_type)).get(polymorph_target)).add(polymorph);
					
				} else if (polymorph_tag.equals("conversion") ||
							polymorph_tag.equals("any")) {
					if (! conversions.containsKey(polymorph_type))
						conversions.put(polymorph_type,new HashMap());
					((Map)conversions.get(polymorph_type)).put(polymorph_target,polymorph);
				}
			} else if (new_element.equals("polymorph") ||
						new_element.equals("conversion") ||
						new_element.equals("any")) {
				if (! collecting_polymorph)
					throw new RuntimeException("internal inconsistency: found </" + new_element + ">, but not collecting " + new_element);
				if (! new_element.equals(polymorph_tag))
					throw new RuntimeException("internal inconsistency: found </" + new_element + ">, expected " + polymorph_tag);
				collecting_polymorph = false;
			} else if (collecting_implement_catch_value ) {
				if (new_element.equals(collecting_implement_catch_type)) {
					if (new_element.equals("code"))
						catch_code.add(contents);
					else if (new_element.equals("include"))
						catch_include.add(contents);
					collecting_implement_catch_value = false;
					collecting_implement_catch_type = "";
				} else {
					throw new RuntimeException("internal inconsistency: found </" + new_element + ">, expecting " + collecting_implement_catch_type);
				}
			} else if (collecting_implement_catch) {
				if (new_element.equals("catch")) {
					if (! implement.containsKey("catch"))
						implement.put("catch",new HashMap());
					((Map)implement.get("catch")).put("code",catch_code);
					((Map)implement.get("catch")).put("include",catch_include);
					collecting_implement_catch = false;
				} else {
					throw new RuntimeException("internal inconsistency: found </" + new_element + ">, expecting </catch>");
				}
			} else if (collecting_implement) {
				if (new_element.equals("implement")) {
					collecting_implement = false;
				}
			}
		}
		
		public void characters(char ch[], int start, int length) {
			if (collecting_contents) {
				for (int i=start; i < start+length; ++i) {
					switch(ch[i]) {
					case '\n':
					case '\t':
					case '\r':	contents += ' ';
								break;
					default:		contents += ch[i];
								break;
					}
				}
			}
		}
	}

	
	private static class ComponentReader extends DefaultHandler {

		private class ValueState {
			
			private String name = null;
			private String type = null;
			private Map struct = null;
			private List vector = null;
			private String value = null;
			
			public ValueState( String type_ ) {
				type = type_;
				if ( type.equals("vector"))
					vector = new ArrayList();
				else if ( type.equals("struct"))
					struct = new HashMap();
				else if ( ! type.equals("direct") && ! conversions.containsKey(type) )
					throw new RuntimeException("unknown value type: " + type);
			}
			
			public ValueState( String type_, String name_ ) {
				type =  type_;
				name = name_;
				if ( type.equals("vector"))
					vector = new ArrayList();
				else if ( type.equals("struct"))
					struct = new HashMap();
				else if ( ! type.equals("direct") && ! conversions.containsKey(type) )
					throw new RuntimeException("unknown value type: " + type);
			}
			
			public void add( Object val ) {
				if (type.equals("vector"))
					vector.add(val);
				else if (type.equals("direct") || conversions.containsKey(type)) {
					if (val instanceof String)
						value = (String) val;
					else
						throw new RuntimeException("non-string used for direct value type");
				} else
					throw new RuntimeException("adding single value (without name) is not permitted for value type " + type);
			}
			
			public void add( String name_, Object val ) {
				if (name_ != null && type.equals("struct"))
					struct.put(name_,val);
				else if (name_ == null && type.equals("vector"))
					vector.add(val);
				else if (name_ == null && type.equals("direct") || conversions.containsKey(type)) {
					if (val instanceof String)
						value = (String) val;
					else
						throw new RuntimeException("non-string used for direct value type");
				} else
					throw new RuntimeException("adding a name with a value is not permitted for value type " + type);
			}
			
			public String getType( ) { return type; }
			public String getName( ) { return name; }
			
			public Object getValue( ) {
				if ( type.equals("vector"))
					return vector;
				else if ( type.equals("struct"))
					return struct;
				else if ( type.equals("direct") || conversions.containsKey(type) )
					return value;
				else
					throw new RuntimeException("internal inconsistency (unknown type)");
			}
		}
		private boolean skipping = false;
		private boolean collecting_component = false;
		private boolean collecting_method = false;
		private boolean collecting_code = false;
		private boolean collecting_code_include = false;
		private boolean collecting_code_section = true;
		private boolean collecting_parameter = false;
		private int collecting_value = 0;

		private boolean collecting_field = false;
		private boolean collecting_element = false;
		private boolean collecting_contents = false;
		private boolean collecting_contents_preserve = false;
		private boolean collecting_description = false;
		
		private List value_list = null;
		
		private String parameter_name = null;

		private String code_section_name = null;
		private Map parameter = null;
		private Map parameters = null;
		private Map methods = null;
		private Map code = null;


		private Map code_includes = null;
		private Map description = null;
		
		private String skipping_tag = null;
		private String component_name = null;
		private String method_name = null;
		private String description_type = null;
		private String contents = "";
		private String anytype = null;
		
		public ComponentReader( ) { super(); }
		
		public void startDocument( ) { 
			collecting_component = false;
			collecting_method = false;
			collecting_code = false;
			collecting_code_include = false;
			collecting_code_section = false;
			collecting_parameter = false;
			collecting_contents = false;
			collecting_contents_preserve = false;
			collecting_value = 0;

			collecting_field = false;
			collecting_element = false;
			collecting_description = false;
			skipping = false;
		}
		
		public void endDocument( ) { }
		
		public void startElement(String uri, String name, String qName, Attributes attr) {
			String new_element = ("".equals(uri) ? qName : name);
			if (collecting_component) {
				if (collecting_value > 0) {
					if (new_element.equals("value")) {
						String type = attr.getValue("type");
						String nme = attr.getValue("name");
						contents = "";
						collecting_contents = true;
						value_list.add(0,new ValueState((type == null ? "direct" : type),nme));
						collecting_value += 1;
					}
					else
						throw new RuntimeException("tag found within a parameter value");
				}
				else if (collecting_parameter) {
					if (new_element.equals("value")) {
						collecting_value = 1;
						String type = attr.getValue("type");
						String nme = attr.getValue("name");
						contents = "";
						collecting_contents = true;
						value_list.add(0,new ValueState((type == null ? "direct" : type),nme));
					}
					else if (new_element.equals("any")) {
						if (attr.getValue("type").length() > 0) {
							parameter.put("any-type", attr.getValue("type"));
						} else
							throw new RuntimeException("<any> with no 'type' attribute");
					}
					else if (new_element.equals("description")) {
						contents = "";
						collecting_contents = true;
						collecting_description = true;
						description_type = "description";
					}
				}
				else if (collecting_method) {
					if (new_element.equals("param")) {
						parameter_name = attr.getValue("name");
						if (parameter_name == null)
							throw new RuntimeException("parameter with no name");
						parameter = new HashMap();
						collecting_parameter = true;
						value_list = new ArrayList();
					}
					else if (new_element.equals("any")) {
						if (attr.getValue("type").length() > 0) {
							anytype = attr.getValue("type");
						} else
							throw new RuntimeException("<any> with no 'type' attribute");
					}
					else if (new_element.equals("shortdescription")) {
						contents = "";
						collecting_contents = true;
						collecting_description = true;
						description_type = "shortdescription";
					}
				}
				else if (collecting_code) {
					if (new_element.equals("include")) {
						if (code_includes == null)
							code_includes = new HashMap();
						contents = "";
						collecting_contents = true;
						collecting_code_include = true;
					}
					else if (new_element.equals("private") ||
							new_element.equals("protected")) {
						contents = "";
						collecting_contents = true;
						collecting_contents_preserve = true;
						collecting_code_section = true;
						code_section_name = new_element;
					}
				}
				else if (new_element.equals("method")) {
					String type = attr.getValue("type");
					if (type == null || type.equals("function")) {
						method_name = attr.getValue("name");
						if (method_name == null)
							throw new RuntimeException("function method has no name");
					}
					else if (type.equals("constructor")) {
						method_name = component_name;
					} else {
						throw new RuntimeException("unknown method type: '" + type + "'");
					}
					parameters = new HashMap();
					description = new HashMap();
					description.put("params", new ArrayList());
					anytype = null;
					collecting_method = true;
				}
				else if (new_element.equals("code")) {
					code = new HashMap();
					code_includes = null;
					collecting_code_include = false;
					collecting_code_section = false;
					collecting_code = true;
				}
				else if (new_element.equals("shortdescription")) {
					skipping = true;
					skipping_tag = new_element;
				}
			}
			else if (new_element.equals("interface")) {
				component_name = attr.getValue("name");
				if (component_name == null)
					throw new RuntimeException("component with no name");
				methods = new HashMap();
				collecting_component = true;
			}
		}
		
		public void endElement(String uri, String name, String qName) {
			String new_element = ("".equals(uri) ? qName : name);
			collecting_contents = false;
			collecting_contents_preserve = false;
			if (skipping) {
				if (new_element.equals(skipping_tag)) {
					skipping = false;
					skipping_tag = null;
				}
			}
			else if (collecting_value > 0) {
				if (new_element.equals("value")) {
					ValueState vs = (ValueState) value_list.remove(0);
					if (value_list.size() > 0)
						((ValueState)value_list.get(0)).add(vs.getName(),(vs.getType().equals("direct") ? contents : vs.getValue()));
					else {
						if ( vs.getType().equals("vector") || vs.getType().equals("struct"))
							parameter.put("default", vs.getValue());
						else if ( vs.getType().equals("direct") || conversions.containsKey(vs.getType()) )
							parameter.put( "default", contents );
						else
							throw new RuntimeException("encountered unknown type: " + vs.getType());
					}
					collecting_value -= 1;
				}
				else
					throw new RuntimeException("found non-</value> end-element within <value>: " + new_element);
			}
			else if (new_element.equals("param")) {
				if (! collecting_parameter)
					throw new RuntimeException("internal inconsistency: found </param>, but not collecting param");
				parameters.put(parameter_name,parameter);
				collecting_parameter = false;
			}
			else if (new_element.equals("description")) {
				if (! collecting_description || ! description_type.equals(new_element))
					throw new RuntimeException("internal inconsistency: found </" + new_element + ">, but not collecting" + new_element );
				if ( collecting_parameter ) {
					((List)description.get("params")).add("    " + parameter_name + ": " + contents);
				}
			}
			else if (new_element.equals("shortdescription")) {
				if (! collecting_description || ! description_type.equals(new_element))
					throw new RuntimeException("internal inconsistency: found </" + new_element + ">, but not collecting" + new_element );
				description.put("short",contents);
			}
			else if (new_element.equals("method")) {
				if (! collecting_method)
					throw new RuntimeException("internal inconsistency: found </method>, but not collecting method");
				// build description string...
				List params = (List) description.get("params");
				String st = (String) description.get("short");
				String desc = st;
				if (params.size() > 0) {
					desc += "\\n";
					desc += "--- --- --- --- --- --- Parameters  --- --- --- --- --- ---\\n";
					desc += join(params,"\\n");
					desc += "\\n";
					desc += "--- --- --- --- --- --- --- --- --- --- --- --- --- --- ---";
				}
				desc = replaceAll(desc, "\"", "\\\"");
				// remove idiot math formatting
				desc = replaceAll(desc, "\\c", "c");
				desc = replaceAll(desc, "\\/", "/");
				desc = replaceAll(desc, "\\ ", " ");
				desc = replaceAll(desc, "\\_", "_");
				desc = replaceAll(desc, "\\l", "l");
				desc = replaceAll(desc, "\\~", "~");
				desc = replaceAll(desc, "\\^", "^");
				// ----------------------------
				description.put("short",desc);
				Map info = new HashMap();
				info.put("params",parameters);
				info.put("desc",description);
				if (anytype != null)
					info.put("anytype",anytype);
				methods.put(method_name,info);
				collecting_method = false;
			}
			else if (new_element.equals("include")) {
				if (! collecting_code_include)
					throw new RuntimeException("internal inconsistency: found </include>, but not collecting include");
				code_includes.put("#include <" + contents + ">","");
				collecting_code_include = false;
			}
			else if (new_element.equals("private") ||
					new_element.equals("protected")) {
				if (! collecting_code_section || ! code_section_name.equals(new_element))
					throw new RuntimeException("internal inconsistency: found </" + new_element + ">, but collecting " + code_section_name);
				code.put(code_section_name,contents);
				collecting_code_section = false;
			}
			else if (new_element.equals("code")) {
				if (! collecting_code)
					throw new RuntimeException("internal inconsistency: found </code>, but not collecting code");
				collecting_code = false;
			}
			else if (new_element.equals("interface")) {
				Map info = new HashMap();
				info.put("methods",methods);
				components.put(component_name,info);
				if (code != null) {
					code.put("include",Text.join("\n",code_includes.keySet()));
					privates.put(component_name,code);
				}
				code = null;
				code_includes = null;
				collecting_component = false;
			}
		}
		
		public void characters(char ch[], int start, int length) {
			String xxx = new String(ch,start,length);
			if (collecting_contents) {
				if (collecting_contents_preserve) {
					for (int i=start; i < start+length; ++i) {
						switch(ch[i]) {
						case '\r':	contents += ' ';
									break;
						default:		contents += ch[i];
									break;
						}
					}
				} else {
					for (int i=start; i < start+length; ++i) {
						switch(ch[i]) {
						case '\n':
						case '\t':
						case '\r':	contents += ' ';
									break;
						default:		contents += ch[i];
									break;
						}
					}
				}
			}
		}
	}

	public void setInputSource(String source) {
		directory = source;	
	}

	private static Object merge_polymorph(Object target, Object source) {
		if (target instanceof List) {
			if (source instanceof List) {
				((List)target).addAll((List)source);
				return target;
			} else
				throw(new RuntimeException("type mismatch in XMLFileManager::merge <list>"));
		}
		if (target instanceof Map) {
			if (source instanceof Map) {
				for (Iterator keys = ((Map)source).keySet().iterator(); keys.hasNext(); ) {
					String key = (String) keys.next();
					if (((Map)target).containsKey(key))
						((Map)target).put(key,merge_polymorph(((Map)target).get(key),((Map)source).get(key)));
					else
						((Map)target).put(key,((Map)source).get(key));
				}
				return target;
			} else
				throw(new RuntimeException("type mismatch in XMLFileManager::merge <map>"));
		}
		return target;
	}
}

