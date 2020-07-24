package org.lemanoman.videoviz.dto;

public class PesquisaJS {
    private String[] includeTags;
    private String[] excludeTags;
    private Integer ratingMin;
    private Integer ratingMax;
    private String title;
    private String tipoPesquisa;

    public String[] getIncludeTags() {
        return includeTags;
    }

    public void setIncludeTags(String[] includeTags) {
        this.includeTags = includeTags;
    }

    public String[] getExcludeTags() {
        return excludeTags;
    }

    public void setExcludeTags(String[] excludeTags) {
        this.excludeTags = excludeTags;
    }

    public Integer getRatingMin() {
        return ratingMin;
    }

    public void setRatingMin(Integer ratingMin) {
        this.ratingMin = ratingMin;
    }

    public Integer getRatingMax() {
        return ratingMax;
    }

    public void setRatingMax(Integer ratingMax) {
        this.ratingMax = ratingMax;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTipoPesquisa() {
        return tipoPesquisa;
    }

    public void setTipoPesquisa(String tipoPesquisa) {
        this.tipoPesquisa = tipoPesquisa;
    }
}
