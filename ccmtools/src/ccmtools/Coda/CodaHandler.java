package ccmtools.Coda;

import java.util.Map;

public interface CodaHandler {
	
	Map getParamInfo(String component, String function, String param );
	String getAnyType(String component, String method);
	String getAnyType(String component, String method, String param);
	Map getMethodInfo(String component, String function);
	Map getMethodDoc(String component, String method);
	Map getConversionInfo(String conversion, String target);
	Map getAnyInfo(String transform_id, String target);
	String getCodeInfo(String type,String section);
	Map getPolymorphInfo(String type, String target);
	Map getExports( );
	Map getMacros( );
	String translateType(String type, String target);
	Map getInclude(String type, String target, String include_use);
	void setInputSource(String source);
	String getTie(String tie_type, String component, String name);
	String getTie(String tie_type, String name);
	boolean doExportDefaults( );
	void exportDefault(String component, String op, String param, String xmldflt);
	void close( );
}
