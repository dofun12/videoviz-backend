package org.lemanoman.videoviz.repositories;

import java.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.lemanoman.videoviz.dto.CondicaoJS;
import org.lemanoman.videoviz.dto.PesquisaAvancadaJS;
import org.lemanoman.videoviz.dto.PesquisaJS;
import org.lemanoman.videoviz.dto.VideoJS;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;


@Repository

public class VideoJDBCRepository extends DefaultJDBCRepository {

    final ObjectMapper mapper = new ObjectMapper();


    public ObjectNode getInfo(Integer idVideo) {

        Map<String, Object> map = getJdbcTemplate().queryForMap(
                "select vu.pageUrl,\n" +
                        "                       DATE_FORMAT((SELECT lvh.watched\n" +
                        "                                    from videoHistory lvh\n" +
                        "                                    where lvh.idVideo = v.idVideo\n" +
                        "                                    order by lvh.watched desc\n" +
                        "                                    limit 1), '%d/%m/%Y %H:%i')    as lastWached,\n" +
                        "                       DATE_FORMAT(v.dateAdded, '%d/%m/%Y %H:%i') as dateformated,\n" +
                        "                       v.location,\n" +
                        "                       vu.midiaUrl,\n" +
                        "                       v.*,\n" +
                        "                       v.title,\n" +
                        "                       v.code,\n" +
                        "                       (select count(0) from advancedvideomanager.videoHistory where idVideo = v.idVideo) as timesWatched,\n" +
                        "                       v.original_tags                            as originalTags\n" +
                        "                from video v\n" +
                        "                         left join advancedvideomanager.videoUrls vu on v.idVideo = vu.idVideo\n" +
                        "                where v.idVideo = ? \n", idVideo);
        return mapper.convertValue(map, ObjectNode.class);
    }

    public VideoJS getInfoAsJS(Integer idVideo) {

        ObjectNode node =  getInfo(idVideo);
        VideoJS retorno = new VideoJS();
        retorno.setCode(node.get("code").asText());
        retorno.setVideoSize(node.get("video_size").asText());
        retorno.setTitle(node.get("title").asText());
        retorno.setDateAddedFormated(node.get("dateformated").asText());
        retorno.setLastWatchedFormated(node.get("lastWached").asText());
        retorno.setOriginalTags(node.get("original_tags").asText());
        retorno.setPageUrl(node.get("pageUrl").asText());
        retorno.setImageUrl( ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/media/image/")
                .path(node.get("code").asText())
                .queryParam("time", new Date().getTime()/1000)
                .toUriString());
        retorno.setRating(node.get("rating").asInt());
        retorno.setIdVideo(node.get("idVideo").asInt());
        return retorno;
    }

    private ObjectNode createType(String key, String description) {
        ObjectNode objectNode = mapper.createObjectNode();
        objectNode.put("key", key);
        objectNode.put("description", description);
        return objectNode;
    }

    private String getSelectBusca(){
        return "select *\n" +
                "from (select v.invalid,\n" +
                "             v.isdeleted,\n" +
                "             v.idVideo,\n" +
                "             v.title,\n" +
                "             v.code,\n" +
                "             v.rating,\n" +
                "             v.md5sum,\n" +
                "             v.video_size,\n" +
                "             v.favorite,\n" +
                "             v.lastwatched,\n" +
                "             v.dateAdded,\n" +
                "             v.duration,\n" +
                "             v.original_tags as tags\n" +
                "      from video v\n" +
                "      where v.idVideo not in (select idVideo from videoTags vt)\n" +
                "      group by v.idVideo\n" +
                "      union\n" +
                "      select v.invalid,\n" +
                "             v.isdeleted,\n" +
                "             v.idVideo,\n" +
                "             v.title,\n" +
                "             v.code,\n" +
                "             v.rating,\n" +
                "             v.md5sum,\n" +
                "             v.video_size,\n" +
                "             v.favorite,\n" +
                "             v.lastwatched,\n" +
                "             v.dateAdded,\n" +
                "             v.duration,\n" +
                "             concat(group_concat(t.tag order by t.idTag),ifnull('',v.original_tags)) as tags\n" +
                "      from videoTags vt\n" +
                "               inner join tags t on vt.idTag = t.idTag\n" +
                "               inner join video v on vt.idVideo = v.idVideo\n" +
                "      group by v.idVideo\n" +
                "     ) as x \n" +
                "where\n x.invalid = 0 and x.invalid is not null and x.isdeleted = 0 ";
    }

    public ArrayNode buscar(PesquisaJS pesquisaJS) {
        StringBuilder sql = new StringBuilder();
        sql.append(getSelectBusca());
        if (pesquisaJS.getIncludeTags() != null) {
            for (String includeTag : pesquisaJS.getIncludeTags()) {
                sql.append("  and    x.tags like '%" + includeTag + "%'\n");
            }
        }
        if (pesquisaJS.getExcludeTags() != null) {
            for (String excludeTag : pesquisaJS.getExcludeTags()) {
                sql.append("  and    x.tags not like '%" + excludeTag + "%'\n");
            }
        }

        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql.toString());
        return mapper.convertValue(list, ArrayNode.class);
    }

    public String getLastCode() {
        String sql = "select code from video where code is not null order by idVideo desc limit 1 ";

        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
        if(list!=null && !list.isEmpty()){
            return (String) list.get(0).get("code");

        }return  null;
    }

    public ArrayNode buscaAvancada(PesquisaAvancadaJS pesquisaAvancadaJS) {
        StringBuilder sql = new StringBuilder();
        sql.append(getSelectBusca());
        if(pesquisaAvancadaJS!=null && pesquisaAvancadaJS.getCondicoes()!=null){
            Map<String,List<CondicaoJS>> summaryCampos = new HashMap<>();
            for(CondicaoJS condicaoJS:pesquisaAvancadaJS.getCondicoes()) {
                if(summaryCampos.get(condicaoJS.getCampo())==null){
                    List<CondicaoJS> list =  new ArrayList<>();
                    list.add(condicaoJS);
                    summaryCampos.put(condicaoJS.getCampo(),list);
                }else{
                    summaryCampos.get(condicaoJS.getCampo()).add(condicaoJS);
                }
            }
            for(Map.Entry<String,List<CondicaoJS>> entry:summaryCampos.entrySet()){
                sql.append(" and ( ");
                int index = 0;
                for(CondicaoJS condicaoJS: entry.getValue()){
                    if(index > 0){
                        sql.append(" ");
                        sql.append(condicaoJS.getCriterio());
                        sql.append(" ");
                    }
                    sql.append("x.");
                    sql.append(condicaoJS.getCampo());
                    sql.append(" ");
                    sql.append(condicaoJS.getCondicao());
                    sql.append(" ");
                    if("texto".equals(condicaoJS.getTipo())){
                        sql.append("'%");
                        sql.append(condicaoJS.getValor());
                        sql.append("%'");
                    }
                    if("numero".equals(condicaoJS.getTipo())){
                        sql.append(condicaoJS.getValor());
                    }
                    sql.append(" ");
                    index++;
                }
                sql.append(" ) ");


            }
        }
        if(pesquisaAvancadaJS.getSortColumn()!=null){
            sql.append(" order  by ");
            sql.append(pesquisaAvancadaJS.getSortColumn());
            sql.append(" ");

            if(pesquisaAvancadaJS.getSortType()!=null){
                sql.append(" ");
                sql.append(pesquisaAvancadaJS.getSortType());
                sql.append(" ");
            }
        }
        sql.append(" limit 1000 ");
        System.out.println(sql.toString());
        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql.toString());
        return mapper.convertValue(list, ArrayNode.class);
    }

    //@Cacheable("videoListTypes")
    public ArrayNode listTypes() {
        ArrayNode listType = mapper.createArrayNode();
        listType.add(createType("all", "Todos"));
        listType.add(createType("recents", "Recentes"));
        listType.add(createType("watched", "Assistidos"));
        listType.add(createType("unwatched", "Não assistidos"));
        listType.add(createType("favorites", "Favoritos"));
        listType.add(createType("new-rand", "Novos aleatorios"));
        listType.add(createType("new", "Novos"));
        listType.add(createType("random", "Aleatorios"));
        listType.add(createType("unrated", "Sem nota"));
        listType.add(createType("rated", "Com nota"));
        listType.add(createType("unrated-random", "Sem nota aleatorios"));
        return listType;
    }

    //@Cacheable(value = "videoListVideos", key = "{#type,#page,#max}")
    public ArrayNode listVideo(String type, Integer max, Integer page) {

        if (max == null) {
            max = 200;
        }
        if (page == null) {
            page = 0;
        }
        final int offset = page * max;
        String sql = buildSelect(null, null, max, page);
        switch (type) {
            case "all": {
                break;
            }
            case "recents": {
                sql = buildSelect(null, " v.lastwatched desc ", max, page);
                //sql = "select count(vh.id) as totalWatched,v.title,v.location,v.code,v.* from video v  inner join videoHistory vh on vh.idVideo = v.idVideo  where  1  and (v.isdeleted = 0 or v.isdeleted is null) and v.isfileexist=1 and v.invalid=0 group by v.idVideo   order by v.lastwatched desc  limit " + max + " OFFSET " + offset + "";
                break;
            }
            case "watched": {
                sql = buildSelect(" and v.lastwatched is not null ", " v.lastwatched desc ", max, page);
                //sql = "select count(vh.id) as totalWatched,v.title,v.location,v.code,v.* from video v  inner join videoHistory vh on vh.idVideo = v.idVideo  where  1  and (v.isdeleted = 0 or v.isdeleted is null) and v.isfileexist=1 and v.invalid=0 group by v.idVideo   order by v.lastwatched desc  limit " + max + " OFFSET " + offset + "";
                break;
            }
            case "unwatched": {
                sql = buildSelect(" and v.lastwatched is null ", " v.idVideo desc ", max, page);
                //sql = "select v.idVideo,v.title,v.location,v.code,v.favorite,v.isdeleted,v.rating,v.dateAdded from video v  where v.lastwatched is null and v.isdeleted = 0 and v.invalid=0 and isfileexist = 1 and (v.isdeleted = 0 or v.isdeleted is null) and v.isfileexist=1 and v.invalid=0 group by v.idVideo   order by v.idVideo asc  limit " + max + " OFFSET " + offset + "\n";
                break;
            }
            case "favorites": {
                sql = buildSelect(" and v.favorite = 1 ", " v.lastwatched desc ", max, page);
                // sql = "select count(vh.id) as totalWatched,v.title,v.location,v.code,v.* from video v  inner join videoHistory vh on vh.idVideo = v.idVideo  where v.favorite  and v.isdeleted = 0 and (v.isdeleted = 0 or v.isdeleted is null) and v.isfileexist=1 and v.invalid=0 group by v.idVideo    order by rand()  limit " + max + " OFFSET " + offset + "";
                break;
            }
            case "new-rand": {
                sql = buildSelect(" and (v.rating = 0 or v.rating is null) ", " rand() ", max, page);
                // sql = "select count(vh.id) as totalWatched,v.title,v.location,v.code,v.* from video v  inner join videoHistory vh on vh.idVideo = v.idVideo  where  1  and (v.isdeleted = 0 or v.isdeleted is null) and v.isfileexist=1 and v.invalid=0 group by v.idVideo   order by v.lastwatched desc  limit " + max + " OFFSET " + offset + "";
                break;
            }
            case "new": {
                sql = buildSelect(" and (v.rating is null or v.rating = 0 or v.lastwatched is null )", " idVideo desc ", max, page);
                //sql = "select v.idVideo,v.title,v.location,v.code,v.favorite,v.isdeleted,v.rating,v.dateAdded from video v  where v.lastwatched is null and v.isdeleted = 0 and v.invalid=0 and isfileexist = 1 and v.dateAdded >= DATE_FORMAT( CURRENT_DATE - INTERVAL 3 MONTH, '%Y/%m/01' )  and (v.isdeleted = 0 or v.isdeleted is null) and v.isfileexist=1 and v.invalid=0 group by v.idVideo   order by v.dateAdded desc  limit " + max + " OFFSET " + offset + "\n";
                break;
            }
            case "random": {
                sql = buildSelect(" ", " rand() ", max, page);
                //sql = "select count(vh.id) as totalWatched,v.title,v.location,v.code,v.* from video v  inner join videoHistory vh on vh.idVideo = v.idVideo  where  1  and (v.isdeleted = 0 or v.isdeleted is null) and v.isfileexist=1 and v.invalid=0 group by v.idVideo   order by rand()  limit " + max + " OFFSET " + offset + "\n";
                break;
            }
            case "unrated": {
                sql = buildSelect(" and (v.rating = 0 or v.rating is null) ", " ", max, page);
                //sql = "select count(vh.id) as totalWatched,v.title,v.location,v.code,v.* from video v  inner join videoHistory vh on vh.idVideo = v.idVideo  where v.rating = 0  and v.isdeleted = 0 and (v.isdeleted = 0 or v.isdeleted is null) and v.isfileexist=1 and v.invalid=0 group by v.idVideo    order by v.lastwatched  limit " + max + " OFFSET " + offset + "";
                break;
            }
            case "unrated-random": {
                sql = buildSelect(" and (v.rating = 0 or v.rating is null) ", " rand() ", max, page);
                break;
            }
            case "rated": {
                sql = buildSelect(" and v.rating > 0 ", " v.rating desc ", max, page);
                break;
            }

        }
        List<Map<String, Object>> list = getJdbcTemplate().queryForList(sql);
        return mapper.convertValue(list, ArrayNode.class);
    }

    public List<Map<String,Object>> getListMD5(){
        String sql = "select w.idVideo,w.code,w.md5sum from video w where w.md5sum is null order by idVideo desc limit 100";
        return getJdbcTemplate().queryForList(sql);
    }

    public List<Map<String,Object>> getByMD5(String md5Sum){
        String sql = "select w.idVideo,w.code,w.md5sum from video w where w.md5sum = '"+md5Sum+"' and w.invalid=0 and w.isfileexist = 1 order by idVideo desc limit 1";
        return getJdbcTemplate().queryForList(sql);
    }


    public String buildSelect(String where, String orderBy, int max, int offset) {
        StringBuilder builder = new StringBuilder("select\n" +
                "       1 as totalWatched,\n" +
                "        v.*\n" +
                "    from\n" +
                "        video v\n" +
                "  where\n" +
                "    (v.isdeleted = 0 or v.isdeleted is null)" +
                "    and v.isfileexist=1 " +
                "    and v.invalid=0 ");
        if (where != null) {
            builder.append(where);
        }

        if (orderBy != null) {
            builder.append(" \n order by ");
            builder.append(orderBy);
        } else {
            builder.append(" \n order by v.idVideo");
        }
        builder.append(" limit " + max + " OFFSET " + offset + "");
        String sql = null;
        System.out.println(sql = builder.toString());
        return sql;
    }



}
