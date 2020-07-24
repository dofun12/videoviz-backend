package org.lemanoman.videoviz.service;


import org.lemanoman.videoviz.SysCommandsUtils;
import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.dto.StoreResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

@Service
public class VideoFileService {

    @Value("${custom.imagelocation}")
    private String imgLocation;

    @Value("${ffmpeg.location}")
    private String ffmpegLocation;

    @Value("${custom.uploadlocation}")
    private String uploadlocation;

    public Resource getImagem(String filename){
        Path path = Paths.get(uploadlocation + "/"+filename);
        Resource resource = null;
        try {
            resource = new UrlResource(path.toUri());
            return resource;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File getImageFileByCode(String code){
        return new File(imgLocation+File.separator+code+".mp4");
    }

    public File getVideoFileByCode(String code){
        return new File(uploadlocation+File.separator+code+".mp4");
    }

    public File createPreviewImage(File mp4File) throws IOException, InterruptedException {
        System.out.println("Verificando ffmpeg ");
        //File ffmpeg = new File("/usr/bin/ffmpeg");
        File ffmpeg = new File(ffmpegLocation);
        if(ffmpeg.exists()){
            File imgFolderFile = new File(imgLocation);
            if(!imgFolderFile.exists()){
                if(!imgFolderFile.mkdirs()){
                    throw new IOException("Erro ao criar diretorio: "+imgLocation);
                }
            }
            //String imagePath = "/var/www/html/v1/ltimg/"+mp4File.getName().replace(".mp4",".png");
            String imagePath = imgLocation+File.separator+mp4File.getName().replace(".mp4",".png");
            System.out.println("Verificando se existe a imagem: "+imagePath);
            File imageFile = new File(imagePath);
            if(imageFile.exists()){
                imageFile.delete();
            }
            if (mp4File.exists() && !imageFile.exists()) {
                String command = ffmpegLocation+" -y -i  " + mp4File.getAbsolutePath() + " -ss 10 -vframes 1 -vcodec png -filter:v scale=\"200:-1\" " + imageFile.getAbsolutePath();/// + " &> /dev/null";
                SysCommandsUtils.runCommandBash(command,true);
                System.out.println("Running: " + command);
                System.out.println("Alterando permissões: " + command);
                Set<PosixFilePermission> perms = new HashSet<>();
                perms.add(PosixFilePermission.OTHERS_READ);
                perms.add(PosixFilePermission.OTHERS_WRITE);
                perms.add(PosixFilePermission.GROUP_WRITE);
                perms.add(PosixFilePermission.GROUP_READ);
                try {
                    Files.setPosixFilePermissions(imageFile.toPath(), perms);
                }catch (UnsupportedOperationException unsupportedOperationException){
                    System.out.println("Mudar permissao nao suportada");
                }
                return imageFile;
            }else if(mp4File.exists() && imageFile.exists()){
                return  imageFile;
            }

        }
        return null;
    }

    public StoreResult storeVideo(String filename, InputStream inputStream){
        StoreResult result = new StoreResult();
        System.out.println("Salvando filename: "+ filename+" ...");
        File dir = new File(uploadlocation);
        if(!dir.exists()){
            dir = new File(uploadlocation);
            if(!dir.exists()){
                dir.mkdirs();
            }
        }

        File file1 = new File(dir.getAbsolutePath()+"/"+filename);
        System.out.println("Salvando filename at: "+ dir.getAbsolutePath()+"/"+filename+" ...");
        try {
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            FileOutputStream fileOutputStream = new FileOutputStream(file1);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

            byte[] buffer = new byte[1048576];
            System.out.println("Tentando com buff de "+buffer.length);
            int count;
            while ((count = bis.read(buffer)) != -1) {
                bos.write(buffer, 0, count);
            }
            System.out.println("Dando um flush");

            bis.close();
            inputStream.close();

            bos.flush();
            bos.close();
            fileOutputStream.close();
            System.out.println("Finalizando flush");
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OTHERS_READ);
            perms.add(PosixFilePermission.OTHERS_WRITE);
            perms.add(PosixFilePermission.GROUP_WRITE);
            perms.add(PosixFilePermission.GROUP_READ);
            try {
                Files.setPosixFilePermissions(file1.toPath(), perms);
            }catch (UnsupportedOperationException ex){
                System.out.println("Nao da para mudar as permissoes, chora ae");
            }
            try {
                createPreviewImage(file1);
            }catch (Exception ex){
                ex.printStackTrace();
            }
            result.setVideoAdded(file1);
            result.setMd5sum(Utils.getMD5SUM(file1));
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

}