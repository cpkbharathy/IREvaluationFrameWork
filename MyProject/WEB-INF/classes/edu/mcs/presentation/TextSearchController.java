package edu.mcs.presentation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.mcs.domain.ExtractionRecord;
import edu.mcs.service.TextSearchService;
import edu.mcs.solrj.view.ArchivedExtraction;
import edu.mcs.util.BatchedList;


@Controller
public class TextSearchController {
	@Autowired
	TextSearchService textSearchService;
	
	@RequestMapping("/textSearch")
	public ModelAndView searchText() {
		/*try {
			//deleteIndex();
			addIndex();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		String message = "<br><div style='text-align:center;'>"
				+ "<h3>********** Hello World **********</div><br><br>" + textSearchService.searchForText("dd");
		return new ModelAndView("searchTerm", "searchTerm", "fever");
	}
	@RequestMapping("/addToStats")
	public void statsCalc(@RequestParam(value="searchTerm") String searchTerm){	
		//String finalQueryTerm = getConvertedQueryTerm(searchTerm, "+");
		//String finalQueryTerm = getAnySearchTerm(searchTerm);
		String finalQueryTerm = getNearTerm(searchTerm,"3");
		HashSet<Long> applSet = (HashSet<Long>) selectData(finalQueryTerm);
		for (Long applId : applSet) {
			textSearchService.setStatsCalc(applId, finalQueryTerm, "solr_idx_flag");;	
		}
		//Following Code can be used to populate stats for Oracle FTS flag
		/*List<Long> applIds = textSearchService.getftsApplIds(queryTerm);
		for (Long applId : applIds) {
			textSearchService.setStatsCalc(applId, queryTerm, "oracle_fts_flag");;	
		}*/
		
	}
	
	@RequestMapping("/searchTerm")
	public ModelAndView queryData(@RequestParam(value="searchTerm") String searchTerm){	
		String finalQueryTerm = getNearTerm(searchTerm,"3");
		Map<Long,String> applText = (Map<Long, String>) selectData(finalQueryTerm);
		return new ModelAndView("searchTerm", "queryDataList", applText);

	}
	
	private  Map<Long,String> selectData( String queryTerm) {
		SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/archived_extractions").build();
		Map<Long,String> applText = new HashMap();
	    try {
	    	SolrQuery query = new SolrQuery();
	    	Long fy = (long) 2017;
	    	String searchquery ="fy:"+fy;
	        //query.setQuery("extractText:\""+queryTerm +"\"" );
	    	query.setQuery("extractText:"+queryTerm );
	        
	        query.addFilterQuery(searchquery);
	        QueryResponse response = client.query(query);
	        //response.getResults().getNumFound();
	        SolrDocumentList results = response.getResults();
	        for (int i = 0; i < results.size(); ++i) {
	            //System.out.println(results.get(i).getFieldValue("applId"));
	        	applText.put((Long) results.get(i).getFieldValue("applId"), (String) results.get(i).getFieldValue("extractText"));
	        }	        
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return applText;
	}
	private String getAllTerms(String searchTerm,String operator) {
		Set<String> tokenList = parsePhrase(searchTerm);
		
		Iterator<String> iter = tokenList.iterator();
		StringBuilder freeText = new StringBuilder();
		freeText.append("( ");
	    while (iter.hasNext()) {
	    	freeText.append(operator);
	    	freeText.append(iter.next().toString());
	    	freeText.append(" ");
	    }
	    freeText.append(" )");
	    return freeText.toString();
	}
	private String getExactPhrase(String searchTerm) {
		return "(\"" + searchTerm +"\")";	    
	}
	private String getNearTerm(String searchTerm,String proximity) {
		return "(\"" + searchTerm +"\"~"+ proximity+")";	    
	}
	private String getAnySearchTerm(String searchTerm){
		return "(+(" + searchTerm +" ))";
	}
	private Set<String> parsePhrase(String phrase){
		Set<String> tokenList = new LinkedHashSet<String>();
		String regex = "\"([^\"]*)\"|(\\S+)";
	    Matcher m = Pattern.compile(regex).matcher(phrase);
	    while (m.find()) {
	    	tokenList.add(m.group());
	    }
	    return tokenList;
	}

	private  void deleteIndex() {
		SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/archived_extractions").build();
		try {
			client.deleteByQuery( "*:*" );
			client.commit();

		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private  void addIndex() throws IOException, SolrServerException {
	    SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/archived_extractions").build();
	    
	    Collection<ArchivedExtraction> docs = new ArrayList<ArchivedExtraction>();
	    int i = 0;
	    List<ExtractionRecord> extRecords = null;
	    //List<ExtractionRecord> extRecords = (SpringUtil.getManagedBean(FreeTextSearchService.class)).getFreeTextSearchResults(null, null);
	    List<Integer> fys = new ArrayList<>();
	    //fys.add(2013);
	    //fys.add(2014);
	   // fys.add(2015);
	   // fys.add(2016);
	    fys.add(2017);
	    
	    for (Integer fy :fys) {
	    	 List<Long> applIDs =  textSearchService.getApplIDsByFy(fy);
			 BatchedList<Long> batchedList = new BatchedList<Long>(new ArrayList<Long>(applIDs), BatchedList.ORACLE_IN_CLAUSE_SIZE);
	         while(batchedList.hasNextBatch()) 
	         {	        	 
	         	List<Long> applIdList = batchedList.nextBatch();
	         	extRecords = textSearchService.getFreeTextSearchResults(applIdList);
	         	for (ExtractionRecord extRecord : extRecords) {			
	         		ArchivedExtraction doc = new ArchivedExtraction();
	         		doc.setApplId(extRecord.getApplId().toString());
	    		    doc.setId(extRecord.getExtractionId()+"-"+extRecord.getApplId());
	    		    //doc.setDocCreatedDate(LocalDate.now());
	    		    doc.setExtractText(extRecord.getExtractedText());
	    		    doc.setTemplateSectionCode(extRecord.getTemplateSectionCode());
	    		    doc.setFy(extRecord.getFy());
	    		    client.addBean(doc);
	    		    i++;
	    		    if(i%100==0) client.commit(); 
//	    		    docs.add(doc);
//	    		    if(i%100==0){
//	    		    	client.addBeans(docs);
//	    		    	client.commit();
//	    		    }
		 }
        }	
	    client.commit();
	  }
	 }
} 