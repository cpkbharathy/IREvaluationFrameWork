package edu.mcs.solrj.view;

import java.time.LocalDate;

import org.apache.solr.client.solrj.beans.Field;


public class ArchivedExtraction {
	@Field
	String id;
	
	@Field
	String templateSectionCode;
	
	@Field
	String applId;
	
	@Field
	String extractText;

	@Field
	LocalDate docCreatedDate;
	
	@Field
	Integer fy;
	
	public Integer getFy() {
		return fy;
	}

	public void setFy(Integer fy) {
		this.fy = fy;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTemplateSectionCode() {
		return templateSectionCode;
	}

	public void setTemplateSectionCode(String templateSectionCode) {
		this.templateSectionCode = templateSectionCode;
	}

	public String getApplId() {
		return applId;
	}

	public void setApplId(String l) {
		this.applId = l;
	}

	public String getExtractText() {
		return extractText;
	}

	public void setExtractText(String extractText) {
		this.extractText = extractText;
	}

	public LocalDate getDocCreatedDate() {
		return docCreatedDate;
	}

	public void setDocCreatedDate(LocalDate localDate) {
		this.docCreatedDate = localDate;
	}

	
}