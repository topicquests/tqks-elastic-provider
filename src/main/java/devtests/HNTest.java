/**
 * 
 */
package devtests;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.topicquests.es.ProviderEnvironment;
import org.topicquests.es.api.IClient;
import org.topicquests.es.api.IQueryDSL;
import org.topicquests.es.util.TextQueryUtil;
import org.topicquests.support.api.IResult;

import co.elastic.clients.elasticsearch.core.SearchRequest;
import jakarta.json.JsonObject;
import net.minidev.json.JSONObject;

/**
 * @author jackpark
 *
 */
public class HNTest {
	  private ProviderEnvironment environment;
	  private TextQueryUtil textQueryUtil;
	  private IClient provider;
	  private IQueryDSL dsl;
	  private final String
	  INDEX = "hnrss";


	/**
	 * 
	 */
	public HNTest() {
	    environment = new ProviderEnvironment();
	    provider = environment.getProvider();
	    dsl = environment.getQueryDSL();
	    textQueryUtil = environment.getTextQueryUtil();
	    IResult r;
	    //Story
	    final String ID_1 = "31633857";
	    JSONObject jo = new JSONObject();
	    jo.put("by", "gnicholas");
	    jo.put("descendants", 14);
	    jo.put("id", 31633857);
	    String [] ka = new String [] {"31634150", "31634240", "31634129", "31634192", "31633954", "31634056", "31633986", "31634028"};
	    List<String> kx = Arrays.asList(ka);
	    jo.put("kids", kx );
	    jo.put("score", 22);
	    jo.put("time", 1654455694);
	    jo.put("title", "Eating Sea Squirts May Reverse the Signs of Ageing, Study Shows");
	    jo.put("type", "story");
	    jo.put("url", "https://www.xjtlu.edu.cn/en/news/2022/may/eating-sea-squirts-may-reverse-the-signs-of-ageing-study-shows");
	    

	    //Comment
	   /* final String ID_2 = "31633212";
	    JSONObject jc = new JSONObject();
	    jc.put("by", "TedShiller");
	    jc.put("descendants", 4);
	    jc.put("id", 31633212);
	    String [] kb = new String [] {"31633746", "31633445", "31633272", "31633271"};
	    List<String> kx2 = Arrays.asList(kb);
	    jc.put("kids", kx2);
	    jc.put("score", 2);
	    jc.put("text", "I&#x27;m not a recruiter, and I deleted my LinkedIn account over 15 years ago, and found I have never needed it. If someone asks me for my LinkedIn, I give my email address instead. This works quite well.<p>What would happen if you deleted your LinkedIn account?");
	    jc.put("time", 1654451208);
	    jc.put("title", "Why do you need LinkedIn? (non-recruiters)");
	    jc.put("type", "comment");
	    setMappings();
	    r = provider.put(ID_2, INDEX, jo);
	    System.out.println("Foo "+r.getErrorString()+" | "+r.getResultObject());
	    r = provider.put(ID_2, INDEX, jc);
	    System.out.println("Foo "+r.getErrorString()+" | "+r.getResultObject());
	    */
	    /*final String ID_3 = "31945189";
	    jo = new JSONObject();
	    jo.put("by", "mooreds");
	    jo.put("descendants", 0);
	    jo.put("id", 31945189);
	    jo.put("score", 2);
	    jo.put("time", 1656676761);
	    jo.put("title", "Professional Allies Can Help You Land Your First Jr Dev Role (2021)");
	    jo.put("type", "story");
	    jo.put("url", "https://letterstoanewdeveloper.com/2021/04/05/fewer-applications-more-interviews-how-professional-allies-can-help-you-land-your-first-jr-dev-role/");
	    r = provider.put(ID_3, INDEX, jo);
	    
	    System.out.println("Foo "+r.getErrorString()+" | "+r.getResultObject());
	    */
	    //Testing existence
	    r = provider.exists(ID_1, INDEX);
	    System.out.println("EX1 "+r.getErrorString()+" | "+r.getResultObject());
		// EX1  | true
	    r = provider.exists("fooiex", INDEX);
	    System.out.println("EX2 "+r.getErrorString()+" | "+r.getResultObject());
	   // EX2  | false
	    // term test
	    SearchRequest sr = dsl.getMatchQueryString("type", "story", 0, -1, dsl.toArray(INDEX));
	    System.out.println("Q1: "+sr.toString());
	    r = provider.search(sr, INDEX);
	    System.out.println("QX1: "+r.getErrorString()+"\n"+r.getResultObject());
	    // text/field test
	    String [] fields = {"title", "text"};
	    SearchRequest smm = dsl.getTextQueryString("you", 0, -1, dsl.toArray(INDEX), fields);
	    r = provider.search(smm, INDEX);
	    System.out.println("QX2: "+r.getErrorString()+"\n"+r.getResultObject());
	    // delete test
	    //r = provider.remove(ID_3, INDEX);
	    //System.out.println("REM1: "+r.getErrorString()+"\n"+r.getResultObject());
	    //REM1: 
	    //Deleted
	    System.exit(0);
	}

	 void setMappings() {
			List<List<String>> l = (List<List<String>>)environment.getProperty("IndexNames");
			List<String> mpx;
			String indexName;
			String mapPath;
			String numShards = environment.getStringProperty("NumShards");
			String numReps = environment.getStringProperty("NumDuplicates");
			String mappings;
			Iterator<List<String>> itr = l.iterator();
			IResult r;
			while (itr.hasNext()) {
				mpx = itr.next();
				indexName = mpx.get(0);
				mapPath = mpx.get(1);
				mappings = environment.getUtil().getMappings(mapPath);
				r = provider.createIndex(indexName, mappings, numShards, numReps);
				System.out.println("INDXA "+r.getErrorString());
				System.out.println("INDXB "+r.getResultObject());
			}

	  }
}
/**
 * KIBANA
{
	"_index": "hnrss",
	"_id": "31633857",
	"_score": 1.0,
	"_source": {
		"score": 22,
		"by": "gnicholas",
		"id": 31633857,
		"time": 1654455694,
		"title": "Eating Sea Squirts May Reverse the Signs of Ageing, Study Shows",
		"type": "story",
		"descendants": 14,
		"url": "https://www.xjtlu.edu.cn/en/news/2022/may/eating-sea-squirts-may-reverse-the-signs-of-ageing-study-shows",
		"kids": [
			"31634150",
			"31634240",
			"31634129",
			"31634192",
			"31633954",
			"31634056",
			"31633986",
			"31634028"
		]
	}
}
{
	"_index": "hnrss",
	"_id": "31633212",
	"_score": 1.0,
	"_source": {
		"score": 2,
		"by": "TedShiller",
		"id": 31633212,
		"text": "I&#x27;m not a recruiter, and I deleted my LinkedIn account over 15 years ago, and found I have never needed it. If someone asks me for my LinkedIn, I give my email address instead. This works quite well.<p>What would happen if you deleted your LinkedIn account?",
		"time": 1654451208,
		"title": "Why do you need LinkedIn? (non-recruiters)",
		"type": "comment",
		"descendants": 4,
		"kids": [
			"31633746",
			"31633445",
			"31633272",
			"31633271"
		]
	}
}
QX1:
[{
	"score": 2,
	"by": "mooreds",
	"id": 31945189,
	"time": 1656676761,
	"title": "Professional Allies Can Help You Land Your First Jr Dev Role (2021)",
	"type": "story",
	"descendants": 0,
	"url": "https:\/\/letterstoanewdeveloper.com\/2021\/04\/05\/fewer-applications-more-interviews-how-professional-allies-can-help-you-land-your-first-jr-dev-role\/"
}, {
	"score": 22,
	"by": "gnicholas",
	"id": 31633857,
	"time": 1654455694,
	"title": "Eating Sea Squirts May Reverse the Signs of Ageing, Study Shows",
	"type": "story",
	"descendants": 14,
	"url": "https:\/\/www.xjtlu.edu.cn\/en\/news\/2022\/may\/eating-sea-squirts-may-reverse-the-signs-of-ageing-study-shows",
	"kids": ["31634150", "31634240", "31634129", "31634192", "31633954", "31634056", "31633986", "31634028"]
}]
QX2:
[{
	"score": 2,
	"by": "TedShiller",
	"id": 31633212,
	"text": "I&#x27;m not a recruiter, and I deleted my LinkedIn account over 15 years ago, and found I have never needed it. If someone asks me for my LinkedIn, I give my email address instead. This works quite well.<p>What would happen if you deleted your LinkedIn account?",
	"time": 1654451208,
	"title": "Why do you need LinkedIn? (non-recruiters)",
	"type": "comment",
	"descendants": 4,
	"kids": ["31633746", "31633445", "31633272", "31633271"]
}, {
	"score": 2,
	"by": "mooreds",
	"id": 31945189,
	"time": 1656676761,
	"title": "Professional Allies Can Help You Land Your First Jr Dev Role (2021)",
	"type": "story",
	"descendants": 0,
	"url": "https:\/\/letterstoanewdeveloper.com\/2021\/04\/05\/fewer-applications-more-interviews-how-professional-allies-can-help-you-land-your-first-jr-dev-role\/"
}]
 */
