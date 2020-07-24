package org.lemanoman.videoviz.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class DefaultJDBCRepository {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	final ObjectMapper mapper = new ObjectMapper();

    public List<Map<String,Object>> getListMap(String sql,MapSqlParameterSource parameters){
		return jdbcTemplate.queryForList(sql,parameters);
	}

	public Map<String,Object> getMap(String sql, MapSqlParameterSource parameters){
	    List<Map<String,Object>> list = getListMap(sql,parameters);
	    if(list!=null && !list.isEmpty()){
	        return list.get(0);
        }else{
	        return null;
        }

	}

    public JdbcTemplate getJdbcTemplate(){
		return jdbcTemplate;
	}


}