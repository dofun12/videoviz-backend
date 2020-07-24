package org.lemanoman.videoviz.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class VideoUtil {



    public static void storeImagem(String fileUploadLocation,String filename, InputStream inputStream){
        File dir = new File(fileUploadLocation);
        if(!dir.exists()){
            dir.mkdirs();
        }
        File file1 = new File(dir.getAbsolutePath()+"/"+filename);
        try {
            FileOutputStream outputStream = new FileOutputStream(file1);

            byte[] buffer = new byte[8192];
            int count;
            while ((count = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, count);

            }
            outputStream.flush();
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
