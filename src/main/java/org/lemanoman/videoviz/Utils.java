package org.lemanoman.videoviz;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

public class Utils {
    static String abc = "abcdefghijklmnopgrstuvxyz";
    private static char getChar(int i){
        if(i>abc.length()){
            return 'a';
        }
        return abc.charAt(i);
    }

    public static Integer toInt(Object object) {
        if (object == null) return null;
        if (object instanceof String) {
            try {
                String strNum = (String) object;
                return Integer.parseInt(strNum);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        if (object instanceof Long) {
            Long longNumber = (Long) object;
            return longNumber.intValue();
        }
        if (object instanceof Integer) {
            return (Integer) object;
        }
        return null;
    }

    public static String toStr(Object object) {
        if (object == null) return null;
        if (object instanceof String) {
            return (String) object;
        }
        return object.toString();
    }

    public static List<Integer> toListInt(Object object) {
        if (object == null) return null;
        String listAsString = object.toString();

        List<Integer> list = new ArrayList<>();
        if (listAsString.isEmpty()) {
            return list;
        }
        if (!listAsString.contains(",")) {
            return list;
        }
        String[] arrayString = listAsString.split(",");
        return Arrays.stream(arrayString)
                .map(String::trim)
                .map(Utils::toInt)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public static String getRandomString(int maxLength){
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for(int i=0;i<maxLength;i++){
            builder.append(getChar(random.nextInt(abc.length())));
        }
        return builder.toString();
    }
    public static String getRandomName(int max){
        long now = new Date().getTime();
        String prefix = getRandomString(max);
        String text = now+prefix;
        String base64Str = Base64.getEncoder().encodeToString(text.getBytes());
        return base64Str
                .replaceAll("=","")
                .replaceAll("_","")
                .replaceAll("\\.","")
                .replaceAll("-","");
    }

    public static String getRandomName(){
        return getRandomName(4);
    }

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
