package org.lemanoman.videoviz.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Controller
public class GUIController {
    @Value("${gui.location}")
    private String guiLocation;

    @GetMapping(value = {"/"})
    public String home(){
        return "redirect:gui/";
    }
    @GetMapping(value = {
            "/gui/**"
    })
    @ResponseBody
    public ResponseEntity actions(HttpServletRequest httpRequest) throws IOException {
        String requestUrl = httpRequest.getRequestURL().toString();
        if (
                requestUrl.endsWith(".js")
                        || requestUrl.endsWith(".png")
                        || requestUrl.endsWith(".css")
                        || requestUrl.endsWith(".woff2")
                        || requestUrl.endsWith(".woff")
                        || requestUrl.endsWith(".tff")
                        || requestUrl.endsWith(".ico")
        ) {
            String[] paths = requestUrl.split("/");
            String filename = paths[paths.length - 1];
            File requestedFile = new File(guiLocation + File.separator + filename);
            if (requestedFile.exists()) {
                FileInputStream fis = new FileInputStream(requestedFile);
                return ResponseEntity.ok(new InputStreamResource(fis));
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            File requestedFile = new File(guiLocation + File.separator + "/index.html");

            if (requestedFile.exists()) {
                FileInputStream fis = new FileInputStream(requestedFile);
                return ResponseEntity.ok(new InputStreamResource(fis));
            } else {
                return ResponseEntity.notFound().build();
            }

        }

    }

    public static InputStream getResourceFileAsInputStream(String fileName) {
        ClassLoader classLoader = GUIController.class.getClassLoader();
        return classLoader.getResourceAsStream(fileName);
    }
}
