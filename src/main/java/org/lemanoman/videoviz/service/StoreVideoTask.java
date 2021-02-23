package org.lemanoman.videoviz.service;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.lemanoman.videoviz.Constants;
import org.lemanoman.videoviz.Utils;
import org.lemanoman.videoviz.model.DownloadQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.Set;

public class StoreVideoTask implements Runnable {
    private DownloadQueue downloadQueue;
    private OnStoreResult storeResult;
    private String baseDir;
    private static final Logger log = LoggerFactory.getLogger(StoreVideoTask.class);

    public StoreVideoTask(String baseDir, DownloadQueue downloadQueue, OnStoreResult onStoreResult) {
        this.storeResult = onStoreResult;
        this.downloadQueue = downloadQueue;
        this.baseDir = baseDir;
    }

    private boolean verifyLocation(String... customLocations) {
        boolean valid = true;
        for (String customLocation : customLocations) {
            try {
                File dir = new File(customLocation);
                if (!dir.exists()) {
                    if (dir.mkdirs()) {
                        valid = (valid && true);
                    } else {
                        throw new Exception("Cant create dirs");
                    }
                } else {
                    valid = (valid && true);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                valid = (valid && false);
            }
        }
        return valid;
    }

    @Override
    public void run() {
        if (!verifyLocation(baseDir,Constants.getBaseMP4Location(baseDir),Constants.getBaseImageLocation(baseDir))){
            if (storeResult != null)
                storeResult.onDownloadError(new Exception("Erro ao criar os diretorios: " + baseDir));
            return;
        }
        if (storeResult != null) storeResult.onServiceStart(downloadQueue);
        File dir = new File(baseDir);
        if (!dir.exists()) {
            dir = new File("E:\\\\data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
        }

        String filePath = Constants.getVideoFile(baseDir, Utils.getRandomString() + ".mp4");
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(20000)
                .setConnectTimeout(20000)
                .setSocketTimeout(20000)
                .build();


        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(downloadQueue.getVideoUrl());
        httpGet.setConfig(requestConfig);
        try {
            HttpResponse httpResponse = httpclient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = httpResponse.getEntity();
                long length = entity.getContentLength();
                InputStream inputStream = entity.getContent();

                if (storeResult != null) storeResult.onDownloadStart(downloadQueue, length);
                BufferedInputStream bis = new BufferedInputStream(inputStream);
                FileOutputStream fileOutputStream = new FileOutputStream(filePath);
                BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

                byte[] buffer = new byte[1048576];
                System.out.println("Tentando com buff de " + buffer.length);
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
                File file1 = new File(filePath);
                if (storeResult != null) storeResult.onDownloadSuccess(downloadQueue);
                try {
                    Set<PosixFilePermission> perms = new HashSet<>();
                    perms.add(PosixFilePermission.OTHERS_READ);
                    perms.add(PosixFilePermission.OTHERS_WRITE);
                    perms.add(PosixFilePermission.GROUP_WRITE);
                    perms.add(PosixFilePermission.GROUP_READ);
                    Files.setPosixFilePermissions(file1.toPath(), perms);
                    if (storeResult != null) storeResult.onPermissionSuccess(downloadQueue);
                }catch (UnsupportedOperationException e){
                    file1.setWritable(true);
                    file1.setReadable(true);
                    if (storeResult != null) storeResult.onPermissionSuccess(downloadQueue);
                } catch (Exception ex) {
                    log.error(ex.getMessage());
                }
                if (storeResult != null) storeResult.onReadyToFactoryImage(dir, file1, downloadQueue);
                if (storeResult != null) storeResult.onFinished(downloadQueue, file1);
            } else {
                if (storeResult != null)
                    storeResult.onDownloadError(new Exception("Status diferente de 200: " + httpResponse.getStatusLine().getReasonPhrase() + " - " + httpResponse.getStatusLine().getStatusCode()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (storeResult != null) storeResult.onDownloadError(e);
            new File(filePath).delete();
            return;
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
                if (storeResult != null) storeResult.onDownloadError(new Exception("Erro ao fechar o http", e));
                return;
            }
        }
    }
}
