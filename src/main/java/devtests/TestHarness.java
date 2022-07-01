/**
 * 
 */
package devtests;

/**
 * @author jackpark
 *
 */
public class TestHarness {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
          // new BootTest();
          //new Store_FetchTest(); deprecated due to mappings change
          // new Store_Fetch_2();
          //new FirstQueryTest();
         // new Exists();
         // new RemoveDoc();
         // new PartialUpdateTest();
		//new SetupTest();
		 new HNTest();
	}

}
/*
co.elastic.clients.elasticsearch._types.ElasticsearchException: 
[es/cat.indices] failed: [security_exception] unable to authenticate user [elastic] for REST request 
[/_cat/indices?format=json]

*/