package ccmtools.Coda;

import java.util.Map;

public abstract class CodaManager implements CodaHandler {
	
	CodaManager( ) { }
	
	abstract public Map getParamInfo(String component, String function, String param);
	abstract public String getAnyType(String component, String method);
	abstract public String getAnyType(String component, String method, String param);
	abstract public Map getMethodInfo(String component, String function);
	abstract public Map getMethodDoc(String component, String method);
	abstract public Map getConversionInfo(String conversion, String target);
	abstract public Map getAnyInfo(String transform_id, String target);
	abstract public String getCodeInfo(String type,String section);
	abstract public void setInputSource(String source);
	abstract public Map getPolymorphInfo(String type, String target);
	abstract public Map getExports( );
	abstract public Map getMacros( );
	abstract public String translateType(String type, String target);
	abstract public Map getInclude(String type, String target, String include_use);
	abstract public String getTie(String tie_type, String component, String name);
	abstract public String getTie(String tie_type, String name);
	abstract public  boolean doExportDefaults( );
	abstract public void exportDefault(String component, String op, String param, String xmldflt);
	abstract public void close( );
	abstract public Map getPython();
}
