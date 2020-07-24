package org.lemanoman.videoviz.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ObjectNodeMapper implements RowMapper {
    final ObjectMapper mapper = new ObjectMapper();
    @Override
    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
        ResultSetMetaData metadata = rs.getMetaData();
        ObjectNode node = mapper.createObjectNode();
        for(int c=1;c<=metadata.getColumnCount();c++){
            Object object = rs.getObject(c);
            if(object instanceof Integer){
                node.put(metadata.getColumnName(c),(Integer)object);
            }else if(object instanceof String){
                node.put(metadata.getColumnName(c),(String) object);
            }else if(object instanceof Double){
                node.put(metadata.getColumnName(c),(Double) object);
            }

        }
        return node;
    }
}
