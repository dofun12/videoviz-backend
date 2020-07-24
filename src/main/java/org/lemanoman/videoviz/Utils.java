package org.lemanoman.videoviz;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

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
}
