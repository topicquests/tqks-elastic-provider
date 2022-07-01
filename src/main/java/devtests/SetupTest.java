/**
 * 
 */
package devtests;

import java.util.List;

import org.topicquests.es.ProviderEnvironment;
import org.topicquests.es.api.IClient;
import org.topicquests.es.util.TextQueryUtil;
import org.topicquests.support.api.IResult;

/**
 * @author jackpark
 *
 */
public class SetupTest {
	  private ProviderEnvironment environment;
	  private TextQueryUtil textQueryUtil;
	  private IClient provider;
	  private final String
	  INDEX = "topics";

	/**
	 * 
	 */
	public SetupTest() {
	    environment = new ProviderEnvironment();
	    provider = environment.getProvider();
	    
		List<List<String>> l = (List<List<String>>)environment.getProperty("IndexNames");
		List<String> mpx = l.get(0);
		String indexName = mpx.get(0);
		String mapPath = mpx.get(1);
		String numShards = environment.getStringProperty("NumShards");
		String numReps = environment.getStringProperty("NumDuplicates");
		String mappings = environment.getUtil().getMappings(mapPath);
		IResult r = provider.createIndex(indexName, mappings, numShards, numReps);
		System.out.println("INDXA "+r.getErrorString());
		System.out.println("INDXB "+r.getResultObject());

		System.exit(0);
	}

}
/**
 * CAT [{"health":"green","status":"open","index":"topics","uuid":"XcGafF3FQ46-CDD5xXM-0w","pri":"1","rep":"0","docs.count":"0","docs.deleted":"0","store.size":"225b","pri.store.size":"225b"}]
 */
