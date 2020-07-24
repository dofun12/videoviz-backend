package org.lemanoman.videoviz.dto;

public class PesquisaAvancadaJS {
    private CondicaoJS[] condicoes;
    private String sortColumn;
    private String sortType;

    public CondicaoJS[] getCondicoes() {
        return condicoes;
    }

    public void setCondicoes(CondicaoJS[] condicoes) {
        this.condicoes = condicoes;
    }

    public String getSortColumn() {
        return sortColumn;
    }

    public void setSortColumn(String sortColumn) {
        this.sortColumn = sortColumn;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }
}
