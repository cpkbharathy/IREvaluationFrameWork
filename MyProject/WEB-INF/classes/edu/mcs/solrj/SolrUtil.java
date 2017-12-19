package edu.mcs.solrj;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import edu.mcs.domain.ExtractionRecord;
import edu.mcs.service.TextSearchService;
import edu.mcs.service.TextSearchServiceImpl;
import edu.mcs.solrj.view.ArchivedExtraction;
import edu.mcs.util.BatchedList;

@Component
public class SolrUtil {
	@Autowired 
	TextSearchService textSearchService;
	
	 @Bean 
	   public TextSearchService textSearchService(){
	      return new TextSearchServiceImpl ();
	   }
	 
	public void statsCalc(String queryTerm){
		//HashSet<Long> applSet = (HashSet<Long>) selectData(queryTerm);
		//for (Long applId : applSet) {
		//	(SpringUtil.getManagedBean(FreeTextSearchService.class)).setStatsCalc(applId, queryTerm, "solr_idx_flag");;	
		//}
		List<Long> applIds = textSearchService().getftsApplIds(queryTerm);
		for (Long applId : applIds) {
			textSearchService().setStatsCalc(applId, queryTerm, "oracle_fts_flag");;	
		}
		
	}
	public  Set<Long> selectData(String queryTerm) {
		SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/archived_extractions").build();
		HashSet<Long> applSet = new HashSet();
	    try {
	    	SolrQuery query = new SolrQuery();
	        query.setQuery("extractText:\""+queryTerm +"\"");
	        QueryResponse response = client.query(query);
	        SolrDocumentList results = response.getResults();
	        for (int i = 0; i < results.size(); ++i) {
	            System.out.println(results.get(i).getFieldValue("applId"));
	            applSet.add((Long) results.get(i).getFieldValue("applId"));
	        }	        
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return applSet;
	}
	public  void deleteIndex() {
		SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/archived_extractions1").build();
	    try {
			client.deleteByQuery( "*:*" );
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public  void addIndex() throws IOException, SolrServerException {
	    SolrClient client = new HttpSolrClient.Builder("http://localhost:8983/solr/archived_extractions1").build();
	    
	    Collection<ArchivedExtraction> docs = new ArrayList<ArchivedExtraction>();
	    int i = 0;
	    List<ExtractionRecord> extRecords = null;
	    //List<ExtractionRecord> extRecords = (SpringUtil.getManagedBean(FreeTextSearchService.class)).getFreeTextSearchResults(null, null);
	    List<Long> applIDs =  textSearchService().getApplIDsByFy();
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
	    		    client.addBean(doc);
	    		    i++;
	    		    if(i%100==0) client.commit(); 
//	    		    docs.add(doc);
//	    		    
//	    		    if(i%100==0){
//	    		    	client.addBeans(docs);
//	    		    	client.commit();
//	    		    }
		 }
		/*for (ExtractionRecord extRecord : extRecords) {			
			doc.setApplId(extRecord.getApplId().toString());
		    doc.setId(extRecord.getExtractionId()+"-"+extRecord.getApplId());
		    //doc.setDocCreatedDate(LocalDate.now());
		    doc.setExtractText(extRecord.getExtractedText());
		    doc.setTemplateSectionCode(extRecord.getTemplateSectionCode());
		    docs.add(doc);
		    if(i%100==0){
		    	client.addBeans(docs);
		    	client.commit();
		    }*/
        }	
		
	    
	   
	    
	    //doc.setApplId("1234");
	    //doc.setId(123 + doc.getApplId());
	    //doc.setDocCreatedDate(LocalDate.now());
	    //doc.setExtractText("test for solr index");
	    //doc.setTemplateSectionCode("title");
	    
	    //client.addBean(doc);
	    client.addBeans(docs);
	    client.commit();
	    
	   /* for(int i=0;i<10;++i) {
	      SolrInputDocument doc = new SolrInputDocument();
	      doc.addField("applid", i+1002);
	      doc.addField("section", "title-" + i);
	      doc.addField("text", "Study of Zika Virus and Related Arbovirus Infections in Deferred Blood Donors " + i);
	      client.add(doc);
	      if(i%100==0) client.commit();  // periodically flush
	    }
	    client.commit();*/
	    
        

       /* SolrQuery query = new SolrQuery();
        query.setQuery("extractText:0");
        QueryResponse response = client.query(query);
        SolrDocumentList results = response.getResults();
        for (int i = 0; i < results.size(); ++i) {
            System.out.println(results.get(i));
        }*/
	  }
}
