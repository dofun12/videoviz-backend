package org.lemanoman.videoviz;

import java.io.Serializable;
import java.util.List;

public class Resposta implements Serializable {
    private Integer statusCode = 200;
    private boolean success = false;
    private boolean hasData = false;
    private String detail;
    private Object data;

    public Resposta() {
    }

    public Resposta(List list) {
        if (list != null && !list.isEmpty()) {
            this.hasData = true;
        }
        this.data = list;
    }

    public Resposta(Object object, boolean isList) {
        if (isList) {
            List list = (List) object;
            this.data = list;
            this.hasData = true;
            this.success = true;
        } else {
            this.data = object;
            this.hasData = true;
        }
    }

    public Resposta(Object model) {
        if (model != null) {
            this.hasData = true;
            this.success = true;
        }
        this.data = model;
    }

    public Resposta failed(String code, String path) {
        this.success = false;
        this.detail = code + " - " + path;
        return this;
    }

    public Resposta failed(Exception ex) {
        this.success = false;
        if (ex != null) {
            this.detail = ex.getMessage();
        } else {
            this.detail = "Erro bizarro...";
        }

        return this;
    }

    public Resposta failed(String error) {
        this.success = false;
        this.detail = error;
        return this;
    }

    public Resposta success() {
        this.success = true;
        return this;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public boolean isHasData() {
        return hasData;
    }

    public void setHasData(boolean hasData) {
        this.hasData = hasData;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
