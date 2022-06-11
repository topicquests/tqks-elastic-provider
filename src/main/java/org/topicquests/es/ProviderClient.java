/**
 * 
 */
package org.topicquests.es;

import java.io.StringReader;
import java.util.*;

import org.topicquests.es.api.IClient;
import org.topicquests.support.ResultPojo;
import org.topicquests.support.api.IResult;
import org.topicquests.support.util.ConfigurationHelper;
import org.topicquests.support.util.LRUCache;
import org.topicquests.support.util.TextFileHandler;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.mapping.TypeMapping;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ElasticsearchIndicesClient;
import co.elastic.clients.elasticsearch.indices.IndexSettings;
import co.elastic.clients.elasticsearch.indices.PutIndicesSettingsRequest;
import co.elastic.clients.elasticsearch.indices.TemplateMapping;
import co.elastic.clients.elasticsearch.indices.put_index_template.IndexTemplateMapping;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * @author jackpark
 * 
 * 
 */
public class ProviderClient implements IClient {
	private ProviderEnvironment environment;
	private ElasticsearchClient client;
	private RestClient restClient;
    private ElasticsearchTransport transport;
    private LRUCache objectCache;
	private final String _TYPE = "core";
	/**
	 * 
	 */
	public ProviderClient(ProviderEnvironment env) {
		environment = env;
		objectCache = new LRUCache(1024);
		environment.logDebug("ProviderClient-");
		setup();
	}

	//https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-low-usage-initialization.html
	private void setup() {
		environment.logDebug("ProviderClient.setup-");
		List<List<String>>clstr = (List<List<String>>)environment.getProperties().get("Clusters");
		List<String>cx = clstr.get(0);
		String name = cx.get(0);
		String p = cx.get(1);
		int port = Integer.parseInt(p);
		//String admin = environment.getStringProperty("AdminName");
		String pwd = environment.getStringProperty("AdminPWD");
		BasicCredentialsProvider credsProv = new BasicCredentialsProvider();
        credsProv.setCredentials(
            AuthScope.ANY, new UsernamePasswordCredentials("elastic", pwd)
        );
        restClient = RestClient.builder(new HttpHost("localhost", port))
            .setHttpClientConfigCallback(hc -> hc.setDefaultCredentialsProvider(credsProv))
            .build();
        transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        client = new ElasticsearchClient(transport);
		//createIndex();
		environment.logDebug("ProviderClient.setup+");
	}
	
	/**
	 * @see https://github.com/elastic/elasticsearch-java/blob/5a2bb06b661adac122b523a84b52d80768446897/java-client/src/test/java/co/elastic/clients/elasticsearch/json/JsonDataTest.java
	 * @see https://github.com/elastic/elasticsearch-java/blob/2b5f3d2211d3c949b0d8e404e8582ab66faf762d/java-client/src/main/java/co/elastic/clients/elasticsearch/indices/ElasticsearchIndicesClient.java
	 * @see https://github.com/elastic/elasticsearch-java/blob/0c10f1893f3e2db71707cdd61c1f8beaf501ff38/java-client/src/test/java/co/elastic/clients/documentation/api_conventions/ApiConventionsTest.java
	 * @param indexName
	 */
	public IResult createIndex(String indexName, String mappings, String numberOfShards, String numberOfReplicas)  {
		System.out.println("ProviderClient.createIndex "+mappings);
		IResult result = new ResultPojo();
		IndexSettings.Builder isb = new IndexSettings.Builder();
		isb.numberOfShards(numberOfShards);
		isb.numberOfReplicas(numberOfReplicas);
		
		//@see https://github.com/elastic/elasticsearch-java/blob/66da097630dccc29da677f4a32ed8b468bceff3d/java-client/src/test/java/co/elastic/clients/json/WithJsonTest.java
		CreateIndexRequest.Builder b = new CreateIndexRequest.Builder()
				.index(indexName)
				.settings(isb.build())
				.withJson(new StringReader(mappings));
		try {
			CreateIndexResponse createIndexResponse = client.indices().create(b.build());
			result.setResultObject(new Boolean(createIndexResponse.acknowledged()));
		} catch (Exception e) {
			e.printStackTrace();
			environment.logError(e.getMessage(), e);
			
		}
		return result;
	}
	//https://www.elastic.co/guide/en/elasticsearch/client/java-rest/6.x/java-rest-high-put-mapping.html
	//https://www.elastic.co/guide/en/elasticsearch/reference/7.x/indices-put-mapping.html
	private void createMapping(JSONObject mapping, String index, int numShards, int numReplicas) {
	/*	try {
			environment.logDebug("ProviderClient.createMapping- "+index+" "+numShards+" "+" "+numReplicas+" "+mapping);
			//https://www.elastic.co/guide/en/elasticsearch/client/java-rest/master/java-rest-high-create-index.html
			CreateIndexRequest request = new CreateIndexRequest(index);
			request.settings(Settings.builder() 
					.put("index.number_of_shards", numShards)
					.put("index.number_of_replicas", numReplicas)
			);
			request.mapping(mapping);
			environment.logDebug("ProviderClient.createMapping-1 "+request.toString());
			CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
			environment.logDebug("ProviderClient.createMapping+ "+createIndexResponse.toString());
		} catch (Exception e) {
			environment.logError(e.getMessage(), e);
			e.printStackTrace();			
		}*/
	}
	
	private JSONObject getMappings(String fileName) throws Exception {
		TextFileHandler handler = new TextFileHandler();
		String mappings = handler.readFile(ConfigurationHelper.findPath(fileName));		
		JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		return (JSONObject)p.parse(mappings);
	}
	
	/* (non-Javadoc)
	 * "https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-index.html"
	 * @see org.topicquests.es.api.IClient#put(java.lang.String, java.lang.String, net.minidev.json.JSONObject)
	 */
	//https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-index.html
	public IResult put(String id, String index, JSONObject node) {
environment.logDebug("ProviderClient.put "+id+" "+index+" "+node.toJSONString());
		IResult result = new ResultPojo();
		/*try {
			IndexRequest request = new IndexRequest(index);
			request.id(id);
			request.source(node.toJSONString(), XContentType.JSON);
			IndexResponse indexResponse = client.index(request, RequestOptions.DEFAULT);
			result.setResultObject(Integer.toString(indexResponse.status().getStatus()));
		} catch (Exception e) {
			e.printStackTrace();
			environment.logError("ProviderClient.put: "+e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}*/
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IClient#updateFullNode(java.lang.String, java.lang.String, net.minidev.json.JSONObject, boolean)
	 */
	public IResult updateFullNode(String id, String index, JSONObject object, boolean checkVersion) {
          IResult result = new ResultPojo();
      /*    try {
            UpdateRequest request = new UpdateRequest(index, _TYPE, id)
                .doc(object.toJSONString(), XContentType.JSON);
            UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
            result.setResultObject(Integer.toString(updateResponse.status().getStatus()));
          } catch (Exception e) {
            e.printStackTrace();
            environment.logError("ProviderClient.put: "+e.getMessage(), e);
            result.addErrorString(e.getMessage());
          }*/
          return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IClient#partialUpdateNode(java.lang.String, java.lang.String, net.minidev.json.JSONObject)
	 */
	//https://www.elastic.co/guide/en/elasticsearch/client/java-api/7.x/java-docs-update.html
	public IResult partialUpdateNode(String id, String index, JSONObject object) {
          IResult result = new ResultPojo();
       /*   try {
            UpdateRequest request = new UpdateRequest()
            	.index(index)
                .doc(object.toJSONString(), XContentType.JSON);
            UpdateResponse updateResponse = client.update(request, RequestOptions.DEFAULT);
            result.setResultObject(Integer.toString(updateResponse.status().getStatus()));
          } catch (Exception e) {
            e.printStackTrace();
            environment.logError("ProviderClient.put: "+e.getMessage(), e);
            result.addErrorString(e.getMessage());
          }*/
          return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IClient#remove(java.lang.String, java.lang.String)
	 */
	public IResult remove(String id, String index) {
          IResult result = new ResultPojo();
       /*   try {
            DeleteRequest request = new DeleteRequest(index); //, _TYPE, id);
            DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
            result.setResultObject(Integer.toString(response.status().getStatus()));
          } catch (Exception e) {
            e.printStackTrace();
            environment.logError("ProviderClient.delete: " + e.getMessage(), e);
            result.addErrorString(e.getMessage());
          }*/
          return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IClient#exists(java.lang.String, java.lang.String)
	 */
	public IResult exists(String id, String index) {
          IResult result = new ResultPojo();
      /*   try {
            GetRequest request = new GetRequest(index);
            request.id(id);
           
            GetResponse response = client.get(request, RequestOptions.DEFAULT);
            result.setResultObject(Boolean.toString(response.isExists()));
          } catch (Exception e) {
            e.printStackTrace();
            environment.logError("ProviderClient.exists: " + e.getMessage(), e);
            result.addErrorString(e.getMessage());
          }*/
          return result;
	}
		
	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IClient#get(java.lang.String, java.lang.String)
	 */
	//https://www.elastic.co/guide/en/elasticsearch/client/java-rest/current/java-rest-high-document-get.html
	public IResult get(String id, String index) {
		IResult result = new ResultPojo();
		/*try {
			GetRequest greq = new GetRequest(index, id);
			GetResponse gres = client.get(greq, RequestOptions.DEFAULT);// new BasicHeader("Accep", "application/json"));
			
			String json = gres.getSourceAsString();
                        if (json != null) {
                          JSONObject jo = toJSONObject(json);
                          result.setResultObject(jo);
                        }
		
		} catch (Exception e) {
			e.printStackTrace();
			environment.logError("ProviderClient.get: "+e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}*/
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IClient#multiGet(java.util.List, java.lang.String)
	 */
	public IResult multiGet(List<String> locators, String index) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IClient#search(java.lang.String, java.lang.String)
	 */
	public IResult search(SearchRequest query, String index) {
          IResult result = new ResultPojo();
         /* try {
            SearchResponse searchResponse = client.search(query, RequestOptions.DEFAULT);
            SearchHits hits = searchResponse.getHits();
            Iterator<SearchHit> itr = hits.iterator();
            SearchHit hit;
            String json;
            List<JSONObject> vals = new ArrayList<JSONObject>();

            result.setResultObject(vals);
            while (itr.hasNext()) {
              hit = itr.next();
              json = hit.getSourceAsString();
              vals.add(toJSONObject(json));
            }
          } catch (Exception e) {
            e.printStackTrace();
            environment.logError("ProviderClient.listSearch: "+e.getMessage(), e);
            result.addErrorString(e.getMessage());
          }*/
          return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IClient#listSearch(java.lang.String, java.lang.String)
	 */
	public IResult listSearch(SearchRequest query, String index) {
		IResult result = new ResultPojo();
		/*try {
			SearchResponse searchResponse = client.search(query, RequestOptions.DEFAULT);
			SearchHits hits = searchResponse.getHits();
			Iterator<SearchHit> itr = hits.iterator();
			SearchHit hit;
			String json;
			List<JSONObject> vals = new ArrayList<JSONObject>();
			result.setResultObject(vals);
			while (itr.hasNext()) {
				hit = itr.next();
				json = hit.getSourceAsString();
				if (json != null)
					vals.add(toJSONObject(json));
			}
		} catch (Exception e) {
			e.printStackTrace();
			environment.logError("ProviderClient.listSearch: "+e.getMessage(), e);
			result.addErrorString(e.getMessage());
		}*/
		return result;
	}

	JSONObject toJSONObject(String json) throws Exception {
		JSONParser p = new JSONParser(JSONParser.MODE_JSON_SIMPLE);
		JSONObject jo = (JSONObject)p.parse(json);
		return jo;
	}
	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IClient#multiSearch(java.util.List, java.lang.String)
	 */
	public IResult multiSearch(List<String> query, String index) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IClient#count(java.lang.String, java.lang.String)
	 */
	public IResult count(String query, String index) {
		IResult result = new ResultPojo();
		// TODO Auto-generated method stub
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IClient#refresh()
	 */
	//https://www.elastic.co/guide/en/elasticsearch/reference/7.x/indices-refresh.html
	public IResult refresh(String index) {
          IResult result = new ResultPojo();

       /*   try {
			Request req  = new Request("POST", "/" + index + "/_refresh");

            Response resp = client.getLowLevelClient().performRequest(req);
            int foundCode = resp.getStatusLine().getStatusCode();
            result.setResultObject(Integer.toString(foundCode));
          } catch (Exception e) {
            e.printStackTrace();
            environment.logError("ProviderClient.exists: " + e.getMessage(), e);
            result.addErrorString(e.getMessage());
          }*/

          return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IClient#clearCache()
	 */
	public void clearCache() {
		// TODO Auto-generated method stub

	}

	public void shutDown() {
	/*	try {
			if (client != null)
				client.close();
		} catch (Exception e) {
			e.printStackTrace();
			environment.logError(e.getMessage(), e);
		}*/
	}

}
