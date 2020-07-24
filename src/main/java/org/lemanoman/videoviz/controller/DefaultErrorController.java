package org.lemanoman.videoviz.controller;

import org.lemanoman.videoviz.Resposta;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class DefaultErrorController implements ErrorController {



    @RequestMapping("/error")
    @ResponseBody
    public Resposta handleError(HttpServletRequest request) {
        try {
            Exception ex = (Exception) request.getAttribute("javax.servlet.error.exception");
            if(ex!=null){
                throw ex;
            }
            String statusCode = (String) request.getAttribute("javax.servlet.error.status_code");
            String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
            Resposta resposta = new Resposta().failed(statusCode,requestUri);
            resposta.setStatusCode((Integer) request.getAttribute("javax.servlet.error.status_code"));
            return resposta;
        }catch (Exception ex){
            Resposta resposta = new Resposta().failed(ex);
            resposta.setStatusCode((Integer) request.getAttribute("javax.servlet.error.status_code"));
            return resposta;
        }
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}
