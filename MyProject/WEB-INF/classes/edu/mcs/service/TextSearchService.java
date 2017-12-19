package edu.mcs.service;

import java.util.List;

import edu.mcs.domain.ExtractionRecord;

public interface TextSearchService {
	public List<String> searchForText(String text);
	
	public List<Long> getApplIDsByFy(Integer fy);

	public List<ExtractionRecord> getFreeTextSearchResults(List<Long> applIds);

	public void setStatsCalc(Long applId, String searchTerm,String flag);

	public List<Long> getftsApplIds(String searchTerm);
	
}