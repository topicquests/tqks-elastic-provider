/**
 * 
 */
package devtests;

import org.topicquests.es.ProviderEnvironment;
import org.topicquests.es.api.IClient;
import org.topicquests.es.api.IQueryDSL;
import org.topicquests.es.util.TextQueryUtil;
import org.topicquests.support.api.IResult;
import java.util.*;
import net.minidev.json.JSONObject;

/**
 *
 */
public class FirstQueryTest {
  private ProviderEnvironment environment;
  private TextQueryUtil textQueryUtil;
  private IClient provider;
  private final String
  INDEX = "topics",
    ID = Long.toString(System.currentTimeMillis()),
    EN_LANG = "en",
    FR_LANG = "fr",
    LAB = "Now is a good time!",
    FR_LAB = "C'est un bon moment",
    LAB2 = "So what?",
    DET	= "For all good men to do something nice for their families.",
    Q1 = "good time",
    Q2 = "So what?",
    Q3 = "good men",
    Q4 = "bogus",
    Q5 = "un bon moment";
	
  public FirstQueryTest() {
    System.out.println("--- First Query Test ---");
    environment = new ProviderEnvironment();
    provider = environment.getProvider();
    
    setMappings();
    
    JSONObject jo = new JSONObject();

    // got keys, see /config/mappings.json
    jo.put("lox", ID);

    List<String> en_labels = new ArrayList<String>();
    en_labels.add("Funky label");
    en_labels.add(LAB);
    JSONObject jo_label = new JSONObject();
    jo_label.put(EN_LANG, "Funky label");
                
    jo.put("label", jo_label);

    JSONObject jo_detail = new JSONObject();
    en_labels = new ArrayList<String>();
    en_labels.add(DET);
    jo_detail.put(EN_LANG, "Funky label");
    jo.put("details", jo_detail);
    System.out.println("JO #1" + jo);
                
    IResult r = provider.put(ID, INDEX, jo);
    System.out.println("Foo "+r.getErrorString());
    
    String ID2 = Long.toString(System.currentTimeMillis())+19;
    en_labels = new ArrayList<String>();
    en_labels.add(LAB2);
    jo_label = new JSONObject();
    jo_label.put(EN_LANG, "Funky label");

    jo = new JSONObject();
    jo.put("lox", ID2);
		
    jo.put("label", jo_label);
   // jo_detail.remove(EN_LANG);
    jo.put("details", jo_detail);
    System.out.println("JO #2" + jo);
    r = provider.put(ID2, INDEX, jo);

    String ID3 = Long.toString(System.currentTimeMillis())+33;
    List<String> fr_labels = new ArrayList<String>();
    fr_labels.add(FR_LAB);
    jo_label = new JSONObject();
    jo_label.put(FR_LANG, fr_labels);
    jo_label.put(EN_LANG, en_labels);

    jo = new JSONObject();
    jo.put("lox", ID3);
		
    jo.put("label", jo_label);
    jo.put("details", jo_detail);
    System.out.println("JO #3" + jo);
    r = provider.put(ID3, INDEX, jo);

    System.out.println("--- calling refresh...");
    r = provider.refresh(INDEX);
    System.out.println("--- DONE calling refresh...");

    textQueryUtil = environment.getTextQueryUtil();
    String [] indices = new String [1];
    indices[0]=INDEX;
    String [] fields = new String[2];
    fields[0]="label.en";
    fields[1]="details.en";

    r = textQueryUtil.queryText(Q1, 0, 5, INDEX, indices, fields);
    System.out.println("AAA "+r.getErrorString()+" | "+r.getResultObject());

    r = textQueryUtil.queryText(Q2, 0, 5, INDEX, indices, fields);
    System.out.println("BBB "+r.getErrorString()+" | "+r.getResultObject());

    r = textQueryUtil.queryText(Q3, 0, 5, INDEX, indices, fields);
    System.out.println("CCC "+r.getErrorString()+" | "+r.getResultObject());

    r = textQueryUtil.queryText(Q4, 0, 5, INDEX, indices, fields);
    System.out.println("DDD "+r.getErrorString()+" | "+r.getResultObject());

    //
    // To query labels from any language, use the wildcard:
    // fields[0]="label.*";
    //
    fields[0]="label.fr";
    r = textQueryUtil.queryText(Q5, 0, 5, INDEX, indices, fields);
    System.out.println("EEE "+r.getErrorString()+" | "+r.getResultObject());

    fields[0] = "lox";
    r = textQueryUtil.queryText(ID, 0, 5, INDEX, indices, fields);
    System.out.println("FFF "+r.getErrorString()+" | "+r.getResultObject());

    environment.shutDown();
  }
  
  void setMappings() {
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
  }
}


//AAA  | [{"lox":"1517766289998","language":"en","details":["For all good men to do something nice for their families."],"label":["Now is a good time!","Funky label"]}]
//BBB  | [{"lox":"1517766291967","language":"en","details":"","label":["So what?"]}]
//CCC  | [{"lox":"1517766289998","language":"en","details":["For all good men to do something nice for their families."],"label":["Now is a good time!","Funky label"]}]
//DDD  | []

/*
 --- First Query Test ---
ConfigPullParser provider-config.xml
Start document
Start tag properties
Start tag parameter
End tag parameter // 
Start tag parameter
End tag parameter // 
Start tag parameter
End tag parameter // 
Start tag parameter
End tag parameter // 
Start tag parameter
End tag parameter // 
Start tag parameter
End tag parameter // 
Start tag parameter
End tag parameter // 
Start tag list
Start tag parameter
End tag parameter // 
End tag list // 
Start tag list
Start tag parameter
End tag parameter // 
End tag list // 
End tag properties // 
Cert: /users/jackpark/Downloads/elasticsearch-8.2.3/config/certs/http_ca.crt
ABC /users/jackpark/Downloads/elasticsearch-8.2.3/config/elasticsearch.keystore
CAT []
CATTT: []
INDXA 
INDXB true
JO #1{"lox":"1656377178586","details":{"en":"Funky label"},"label":{"en":"Funky label"}}
ProviderClient.put::: 1656377178586 topics {"lox":"1656377178586","details":{"en":"Funky label"},"label":{"en":"Funky label"}}
Foo 
JO #2{"lox":"165637718147219","details":{"en":"Funky label"},"label":{"en":"Funky label"}}
ProviderClient.put::: 165637718147219 topics {"lox":"165637718147219","details":{"en":"Funky label"},"label":{"en":"Funky label"}}
JO #3{"lox":"165637718152433","details":{"en":"Funky label"},"label":{"en":["So what?"],"fr":["C'est un bon moment"]}}
ProviderClient.put::: 165637718152433 topics {"lox":"165637718152433","details":{"en":"Funky label"},"label":{"en":["So what?"],"fr":["C'est un bon moment"]}}
--- calling refresh...
--- DONE calling refresh...
Query: POST /topics/_search?typed_keys=true {"from":0,"query":{"multi_match":{"fields":["label.en","details.en"],"query":"good time"}},"size":5}
AAA  | []
Query: POST /topics/_search?typed_keys=true {"from":0,"query":{"multi_match":{"fields":["label.en","details.en"],"query":"So what?"}},"size":5}
BBB  | []
Query: POST /topics/_search?typed_keys=true {"from":0,"query":{"multi_match":{"fields":["label.en","details.en"],"query":"good men"}},"size":5}
CCC  | []
Query: POST /topics/_search?typed_keys=true {"from":0,"query":{"multi_match":{"fields":["label.en","details.en"],"query":"bogus"}},"size":5}
DDD  | []
Query: POST /topics/_search?typed_keys=true {"from":0,"query":{"multi_match":{"fields":["label.fr","details.en"],"query":"un bon moment"}},"size":5}
EEE  | []
Query: POST /topics/_search?typed_keys=true {"from":0,"query":{"multi_match":{"fields":["lox","details.en"],"query":"1656377178586"}},"size":5}
FFF  | []
 
 */
