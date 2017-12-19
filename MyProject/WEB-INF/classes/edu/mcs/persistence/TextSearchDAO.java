package edu.mcs.persistence;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import edu.mcs.domain.ExtractionRecord;
  
@Repository
public class TextSearchDAO {
	
	@Autowired
	NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	JdbcTemplate jdbcTemplate;
	
	public List<String> searchForText(String text) {
		List<String> dummy = new ArrayList<>();
		String test = jdbcTemplate.queryForObject("Select appl_id from appls_t where rownum = 1", String.class);
		dummy.add("hi there - " + text + test);
		return dummy;
	}
	
	
	public List<Long> getftsApplIds(String searchTerm){
		StringBuilder nativeQL = null;
		nativeQL = new StringBuilder("   select appl_id from  ");
		nativeQL.append("\n   ( select distinct arch.appl_id appl_id  ");
		nativeQL.append("\n   from archived_extractions_t arch  ");
		nativeQL.append("\n   where contains(arch.extracted_text, :text, 1) > 0 ");
		nativeQL.append("\n     and latest_code = 'Y' ");
		nativeQL.append("\n     and arch.template_section_code is null ");
		nativeQL.append("\n     and arch.fy= :fy ");
		nativeQL.append("\n   ) ");
       
		MapSqlParameterSource params = new MapSqlParameterSource();
		
		
		params.addValue("text", searchTerm);
		params.addValue("fy", 2017);
        List<Long> filteredApplIDs =   namedParameterJdbcTemplate.query(nativeQL.toString(),params,new ApplRecordMapper());
      	//this.namedParameterJdbcTemplate.query(nativeQL.toString(), params, new ApplRecordMapper());
		
		return filteredApplIDs;
	}
	public static class ApplRecordMapper implements RowMapper<Long> {

		@Override
		public Long mapRow(ResultSet rs, int rowNum) throws SQLException {
			Long applID  = rs.getLong("appl_id");			
			return applID;
		}
	}
	public List<ExtractionRecord> getFreeTextSearchResults(List<Long> applIds) {
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		/*StringBuilder nativeQL = new StringBuilder(" INSERT into pachiyappank.ERA_GTMP_KEY_VALUES_T (key_id,numeric_value)  select distinct 'temp',appl_id from extractions_mv where fy = :fy "); 
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fy", 2017);
		this.namedParameterJdbcTemplate.update(nativeQL.toString(), params);*/
		
		StringBuilder nativeQL = new StringBuilder(" select arch.appl_id as appl_id, arch.extraction_id extraction_id,");
		nativeQL.append(" nvl(arch.template_section_code,'COMB') as template_section_code,");
		nativeQL.append(" arch.extracted_text snippet, arch.fy ");
		nativeQL.append(" from archived_extractions_t arch ");
		nativeQL.append(" where arch.APPL_ID in (:appls) ");
		nativeQL.append(" and (arch.template_section_code is null OR arch.template_section_code =  :ts)");	
		
		
		
		//get title and combined extracted text		
		
		params = new MapSqlParameterSource();
		params.addValue("appls", applIds );
		params.addValue("ts", "TI" );
		return this.namedParameterJdbcTemplate.query(nativeQL.toString(), params, new ExtractionRecordMapper());			
	}
	private static class ExtractionRecordMapper implements RowMapper<ExtractionRecord> {

		@Override
		public ExtractionRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
			ExtractionRecord record =  new ExtractionRecord();
			record.setApplId(rs.getLong("appl_id"));
			record.setTemplateSectionCode(rs.getString("template_section_code"));
			record.setExtractedText(rs.getString("snippet"));
			record.setExtractionId(rs.getLong("extraction_id"));
			record.setFy(rs.getInt("fy"));
			return record;
		}
	}
	@SuppressWarnings("unchecked")
	public List<Long> getApplIDsByFy(Integer fy) {
		List<Long> filteredApplIDs = new ArrayList<Long>();
		
		StringBuilder nativeQL = new StringBuilder("select distinct appl_id from archived_Extractions_t where fy = :fy ");
		
		MapSqlParameterSource params = new MapSqlParameterSource();
		params.addValue("fy", fy);
		filteredApplIDs =   this.namedParameterJdbcTemplate.query(nativeQL.toString(), params, new ApplRecordMapper());
		
		return filteredApplIDs;
		
	}
	
	public void  setStatsCalc(Long applId, String searchTerm , String flag) {
		MapSqlParameterSource params = new MapSqlParameterSource();
		
		StringBuilder nativeQL = new StringBuilder("MERGE INTO pachiyappank.stats_Calc sc  USING (SELECT :appl appl_id,:st search_term FROM dual) ex " );
		nativeQL.append("  ON (ex.appl_id = sc.appl_id AND ex.search_term = sc.search_Term) " );
		nativeQL.append("  WHEN MATCHED THEN UPDATE SET sc." + flag + " =1 " );
		nativeQL.append("  WHEN NOT MATCHED THEN INSERT(appl_id,search_Term,"+flag+" ) VALUES (ex.appl_id,ex.search_term,1) " );
		
		params.addValue("appl",applId);
		params.addValue("st", searchTerm);
		this.namedParameterJdbcTemplate.update(nativeQL.toString(), params);
	}

}