package edu.mcs.domain;


import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;


import org.apache.log4j.Logger;



/**
 * ExtractionRecord represents the text extracted from PDF grant application, 
 * Intramural Research Program (IRP), and Contract data entered via Online 1688. 
 * 
 * The PDF Text is extracted from different sections. Sections and bookmarks are 
 * defined in a template file in XML format. Please refer to TEMPLATES_T for more details on
 * template files.
 * 
 * @author RCDC Team (SAIC)
 *
 */

public class ExtractionRecord implements Serializable {

	/** Standard logger. */
    private static final Logger log = Logger.getLogger(ExtractionRecord.class);

	private static final long serialVersionUID = 1L;

	
	private Long extractionId;

	
	private Long applId = (long) 0;

	
	private Long templateId;
	
	private Integer fy;
	
	
	private String extractedText;

	
	private String errorText;

	
	private String templateSectionCode;

	

	
	


	/**
	 * Returns a unique number identifying an extracted text section.
	 * @return
	 */
	public Long getExtractionId() {
		return this.extractionId;
	}

	/**
	 * Sets the unique number identifying an extracted text section.
	 * @param extractionId
	 */
	public void setExtractionId(Long extractionId) {
		this.extractionId = extractionId;
	}

	/**
	 * Returns the identify of the application of the extracted text.
	 * @return
	 */
	public Long getApplId() {
		return this.applId;
	}

	/**
	 * Sets the identify of the application of the extracted text.
	 * @param applId
	 */
	public void setApplId(Long applId) {
		this.applId = applId;
	}
	
	/**
	 * Returns the template identifier that was used to extract the text.
	 * @return
	 */
	public Long getTemplateId() {
		return this.templateId;
	}

	/**
	 * Sets the template identifier that was used to extract the text.
	 * @param templateId
	 */
	public void setTemplateId(Long templateId) {
		this.templateId = templateId;
	}

	/**
	 * Returns the text that was extracted from the document
	 * or entered via the application.
	 * 
	 * @return
	 */
	public String getExtractedText() {
		return this.extractedText;
	}

	/**
	 * Sets the text that was extracted from the document
	 * or entered via the application.
	 * 
	 * @param extractedText
	 */
	public void setExtractedText(String extractedText) {
		this.extractedText = extractedText;
	}

	/**
	 * Returns the error text to describe an extraction error that might
	 * have occurred on this extraction.
	 * @return
	 */
	public String getErrorText() {
		return this.errorText;
	}

	/**
	 * See {@link #getErrorText()}
	 */
	public void setErrorText(String errorText) {
		this.errorText = errorText;
	}

	/**
	 * The section of template to which this extracted text belongs.
	 * 	Possible Section Codes are:
	 *     ABS (Abstract)
	 *     SA (Specific Aims)
     *     RDM (Research Design Methods)
     *     NAR (Narrative/Public Health Relevance)
     * 
     * These section codes are related to elements in template XML files
	 */
	public String getTemplateSectionCode() {
		return this.templateSectionCode;
	}

	/**
	 * See {@link #getTemplateSectionCode()} 
	 */
	public void setTemplateSectionCode(String templateSectionCode) {
		this.templateSectionCode = templateSectionCode;
	}

	public Integer getFy() {
		return fy;
	}

	public void setFy(Integer fy) {
		this.fy = fy;
	}

	
	
		
}
