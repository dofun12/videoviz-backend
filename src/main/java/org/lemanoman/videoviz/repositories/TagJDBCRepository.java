package org.lemanoman.videoviz.repositories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Repository
public class TagJDBCRepository extends DefaultJDBCRepository {

    final ObjectMapper mapper = new ObjectMapper();

    public ArrayNode findSelectedByIdVideo(Integer idVideo) {
        String sql = "select t.idTag,t.tag,sum(t.selected) as selected from (\n" +
                "select\n" +
                "   t.*, false as selected\n" +
                "from\n" +
                "     tags t\n" +
                "union\n" +
                "select\n" +
                "    t.*,true as selected\n" +
                "from tags t\n" +
                "inner join videoTags vt\n" +
                "    on vt.idTag = t.idTag\n" +
                "where idVideo=?\n" +
                ") as t group by t.idTag, t.tag order by t.tag;";
        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql,idVideo);
        return mapper.convertValue(list, ArrayNode.class);
    }


    public ArrayNode allTagsExistent() {
        String sql = "select distinct  original_tags as tags from video v where v.invalid = 0\n" +
                "union\n" +
                "select group_concat(tag) as tags from tags;";
        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
        Set<String> listaUnica = new HashSet<>();
        for(Map<String, Object> map:list){
            if(map.get("tags")!=null){
                String tags = map.get("tags").toString();
                if(tags!=null){
                    String[] miniLista = tags.split(",");
                    for(String tagzinha:miniLista){
                        if(tagzinha!=null && !tagzinha.isEmpty()){
                            listaUnica.add(tagzinha);
                        }
                    }
                }
            }
        }
        return mapper.convertValue(listaUnica, ArrayNode.class);
    }

}