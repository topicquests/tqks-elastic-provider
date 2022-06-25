/**
 * 
 */
package org.topicquests.es;
import java.util.*;

import org.topicquests.es.api.IQueryDSL;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.MatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch._types.query_dsl.SimpleQueryStringQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;

/**
 * @author jackpark
 * @see https://www.elastic.co/guide/en/elasticsearch/reference/8.3/query-dsl.html
 * @see https://www.elastic.co/guide/en/elasticsearch/client/java-api-client/current/searching.html ****
 */
public class QueryDSL implements IQueryDSL {
	private ProviderEnvironment environment;
	/**
	 * 
	 */
	public QueryDSL(ProviderEnvironment env) {
		environment = env;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IQueryDSL#getMatchQueryString(java.lang.String, java.lang.String)
	 */
	/*public SearchRequest getMatchQueryString(String key, String value, String[] indices) {
		TermQuery.Builder tqbs = QueryBuilders.term();
		FieldValue fv;
		tqbs.field(key).v
		searchSourceBuilder.query(QueryBuilders.match(key, value));
		return new SearchRequest(indices, searchSourceBuilder);
		//return null;
	}*/

	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IQueryDSL#getMatchQueryString(java.lang.String, java.lang.String, int, int)
	 */
	public SearchRequest getMatchQueryString(String key, String value, int start, int count, String[] indices) {
		TermQuery.Builder tqbs = QueryBuilders.term();
		tqbs.field(key).value(FieldValue.of(value));
		Query.Builder qb = new Query.Builder();
		qb.term(tqbs.build());
		SearchRequest.Builder srb = new SearchRequest.Builder();
		srb.index(toIndexList(indices));
		srb.query(qb.build());
		srb.from(start);
		if (count > -1)
			srb.size(new Integer(count));
		return srb.build();
	}
	
	List<String> toIndexList(String [] indices) {
		List<String> result= Arrays.asList(indices);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IQueryDSL#getTextQueryString(java.lang.String, int, int, java.lang.String[])
	 */
	public SearchRequest getTextQueryString(String textQuery, int start, int count, String[] indices, String field) {
		MatchQuery.Builder mqb = QueryBuilders.match();
		mqb.query(textQuery);
		mqb.field(field);
		Query.Builder qb = new Query.Builder();
		qb.match(mqb.build());
		SearchRequest.Builder srb = new SearchRequest.Builder();
		srb.index(toIndexList(indices));
		srb.query(qb.build());
		srb.from(start);
		if (count > -1)
			srb.size(new Integer(count));
		return srb.build();
	}

	public SearchRequest getTextQueryString(String textQuery, int start, int count, String[] indices, String[] fields) {
		MultiMatchQuery.Builder mqb = QueryBuilders.multiMatch();
		mqb.query(textQuery);
		mqb.fields(toIndexList(fields));
		Query.Builder qb = new Query.Builder();
		qb.multiMatch(mqb.build());
		SearchRequest.Builder srb = new SearchRequest.Builder();
		srb.index(toIndexList(indices));
		srb.query(qb.build());
		srb.from(start);
		if (count > -1)
			srb.size(new Integer(count));
		return srb.build();
	}

	/* (non-Javadoc)
	 * @see org.topicquests.es.api.IQueryDSL#getFuzzyQueryString(java.lang.String, int, int, java.lang.String[])
	 * /
	public String getFuzzyQueryString(String textQuery, int start, int count, List<String> fields) {
		// TODO Auto-generated method stub
		return null;
	}
	*/

}
