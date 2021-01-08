package org.lemanoman.videoviz;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    public static String getMD5SUM(File f) {
        try {
            if (f.exists() && f.isFile()) {
                if(new File("/usr/bin/md5sum").exists()){
                    Runtime rt = Runtime.getRuntime();
                    String[] commands = {"/usr/bin/md5sum", f.getAbsolutePath()};

                    Process proc = rt.exec(commands);

                    BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

                    BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));

                    StringBuffer buffer = new StringBuffer();
                    String s = null;
                    while ((s = stdInput.readLine()) != null) {
                        buffer.append(s);
                    }
                    String md5 = buffer.toString().split(" ")[0];
                    return md5;
                }
                throw new Exception("File not found "+f.getAbsolutePath());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return null;

    }

    public static String getMD5SumJava(File file){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        try (InputStream is = Files.newInputStream(Paths.get(file.getAbsolutePath()))){
            return org.apache.commons.codec.digest.DigestUtils.md5Hex(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
