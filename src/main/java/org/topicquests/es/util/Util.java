/**
 * 
 */
package org.topicquests.es.util;

//import org.apache.http.ParseException;
import org.topicquests.es.ProviderEnvironment;
import org.topicquests.support.util.TextFileHandler;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * @author jackpark
 *
 */
public class Util {
	private ProviderEnvironment environment;

	/**
	 * 
	 */
	public Util(ProviderEnvironment env) {
		environment = env;
	}
	
	public JSONObject toJSON(String json) throws Exception  {
		JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		return (JSONObject)p.parse(json);
	}

	public String getMappings(String pathToMappings) {
		TextFileHandler h = new TextFileHandler();
		String mappings = h.readFile(pathToMappings);
		return mappings;
	}
}
