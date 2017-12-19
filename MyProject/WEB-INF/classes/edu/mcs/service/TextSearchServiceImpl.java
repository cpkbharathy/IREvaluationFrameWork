package edu.mcs.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.mcs.domain.ExtractionRecord;
import edu.mcs.persistence.TextSearchDAO;

@Service
public class TextSearchServiceImpl implements TextSearchService {
	@Autowired
	private TextSearchDAO textSearchDAO;
	
	public List<String> searchForText(String text) {
		return textSearchDAO.searchForText(text);
	}

	@Override
	public List<Long> getApplIDsByFy(Integer fy) {
		// TODO Auto-generated method stub
		return textSearchDAO.getApplIDsByFy(fy);
	}

	@Override
	public List<ExtractionRecord> getFreeTextSearchResults(List<Long> applIds) {
		// TODO Auto-generated method stub
		return textSearchDAO.getFreeTextSearchResults(applIds);
	}

	@Override
	public void setStatsCalc(Long applId, String searchTerm, String flag) {
		// TODO Auto-generated method stub
		textSearchDAO.setStatsCalc(applId,searchTerm,flag);
	}

	@Override
	public List<Long> getftsApplIds(String searchTerm) {
		// TODO Auto-generated method stub
		return textSearchDAO.getftsApplIds(searchTerm);
	}
	
	
}