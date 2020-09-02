package org.lemanoman.videoviz.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.lemanoman.videoviz.Constants;
import org.lemanoman.videoviz.Resposta;
import org.lemanoman.videoviz.Scrapper;
import org.lemanoman.videoviz.dto.*;
import org.lemanoman.videoviz.model.*;
import org.lemanoman.videoviz.repositories.*;
import org.lemanoman.videoviz.service.VideoDownloadService;
import org.lemanoman.videoviz.service.VideoFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.util.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = Constants.API_BASE_URL + "/upload", produces = MediaType.APPLICATION_JSON_VALUE)

public class UploadController {

    @Value("${addToRealQueue}")
    private Boolean addToRealQueue;
    @Autowired
    private VideoJDBCRepository jdbcRepository;

    @Autowired
    private VideoUrlsRepository videoUrlsRepository;

    @Autowired
    private VideoFileService videoFileService;

    @Autowired
    private LocationRepository locationRepository;

    @Autowired
    private VideoRepository videoRepository;
    @Autowired
    private DownloadQueueRepository downloadQueueRepository;

    @Autowired
    private VideoDownloadService videoDownloadService;

    @GetMapping("/")
    public Resposta listAll() {
        try {
            return new Resposta(videoRepository.findAll()).success();
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @PostMapping("/sendFile")
    public Resposta sendFile(
            @RequestParam("idLocation") Integer idLocation,
            @RequestParam("file") MultipartFile[] files
    ) {
        List<VideoJS> videoJSList = new ArrayList<>();
        if (idLocation != null) {
            Optional<LocationModel> giveLocation = locationRepository.findById(idLocation);
            if (giveLocation.isPresent()) {
                LocationModel locationModel = giveLocation.get();
                for (MultipartFile file : files) {
                    try {
                        String code = getNextCode();
                        StoreResult storeResult = videoFileService.storeVideo(locationModel.getPath(), code + ".mp4", file.getInputStream());
                        if (storeResult != null) {
                            VideoModel videoModel = new VideoModel();
                            videoModel.setCode(code);
                            videoModel.setTitle(file.getOriginalFilename());
                            videoModel.setInvalid(0);
                            videoModel.setIsdeleted(0);
                            videoModel.setDateAdded(new Timestamp(new Date().getTime()));
                            videoModel.setIsfileexist(1);
                            videoModel.setIdLocation(idLocation);
                            videoModel.setMd5Sum(storeResult.getMd5sum());
                            videoModel.setVideoSize(String.valueOf(file.getSize()));
                            videoRepository.saveAndFlush(videoModel);

                            VideoJS videoJS = new VideoJS();
                            videoJS.setCode(code);
                            videoJS.setIdVideo(videoModel.getIdVideo());
                            videoJS.setMd5Sum(storeResult.getMd5sum());
                            String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                                    .path("/media/image/")
                                    .path(locationModel.getContext())
                                    .path("/")
                                    .path(code)
                                    .queryParam("time", new Date().getTime() / 1000)
                                    .toUriString();

                            videoJS.setImageUrl(imageUrl);
                            videoJS.setVideoSize(videoModel.getVideoSize());
                            videoJSList.add(videoJS);
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                return new Resposta(videoJSList).success();
            } else {
                return new Resposta().failed("Localizacao Invalida");
            }

        }

        return new Resposta().success();

    }


    private String getNextCode() {

        int max = 7;

        String oldCode = jdbcRepository.getLastCode();
        Integer intCode;
        if (oldCode == null || oldCode.equals("null")) {
            intCode = 0;
        } else {
            intCode = Integer.parseInt(oldCode);
        }

        intCode++;
        char[] chars = intCode.toString().toCharArray();
        char[] newChars = new char[max];

        for (int i = (chars.length - 1); i >= 0; i--) {
            int y = (newChars.length - chars.length);
            newChars[i + y] = chars[i];
        }
        for (int i = 0; i < (newChars.length - chars.length); i++) {
            newChars[i] = '0';
        }
        return new String(newChars).trim();
    }

    private void downloadFile(String filePath, String downloadUrl) throws Exception {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(3000)
                .setConnectTimeout(3000)
                .setSocketTimeout(3000)
                .build();


        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet(downloadUrl);
        httpGet.setConfig(requestConfig);
        try {
            HttpResponse httpResponse = httpclient.execute(httpGet);
            if (httpResponse.getStatusLine().getStatusCode() == 200) {

                InputStream inputStream = httpResponse.getEntity().getContent();
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
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("Erro ao baixar", e);
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception("Erro ao fechar o http", e);
            }
        }
    }

    @PostMapping("/salvar")
    public Resposta handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("url") String url
    ) {
        String filename = file.getOriginalFilename();
        StoreResult storeResult = null;
        Integer idLocation = null;
        ScrapResult result = null;
        try {

            try {
                result = Scrapper.getScrapResult(url);
            } catch (VideoNotFoundException | PageNotFoundException | IOException e) {
                e.printStackTrace();
            }

            List<LocationModel> list = locationRepository.findAll();
            LocationModel bestLocation = null;
            long bestSize = 0l;
            for (LocationModel lm : list) {
                File path = new File(lm.getPath());
                long maxSize = path.getUsableSpace();
                if (maxSize > bestSize) {
                    bestLocation = lm;
                    bestSize = maxSize;
                }
            }

            if (bestLocation == null) {
                return new Resposta().failed("No location found");
            }

            String code = getNextCode();
            storeResult = videoFileService.storeVideo(bestLocation.getPath(), code + ".mp4", file.getInputStream());

            if (storeResult != null) {

                if (jdbcRepository.getByMD5(storeResult.getMd5sum()).isEmpty()) {
                    VideoModel videoModel = new VideoModel();
                    videoModel.setCode(code);
                    if (result != null) {
                        videoModel.setTitle(result.getTitle());
                        String tags = "";
                        if (result.getTags() != null && !result.getTags().isEmpty()) {
                            String tagsStr = "";
                            for (String tag : result.getTags()) {
                                tagsStr = tagsStr + "," + tag;
                            }
                            tags = (tagsStr.substring(1));
                        }
                        videoModel.setOriginalTags(tags);
                    } else {
                        videoModel.setTitle("Desconhecido");
                    }
                    videoModel.setIdLocation(idLocation);
                    videoModel.setInvalid(0);
                    videoModel.setIsdeleted(0);
                    videoModel.setDateAdded(new Timestamp(new Date().getTime()));
                    videoModel.setIsfileexist(1);
                    videoModel.setMd5Sum(storeResult.getMd5sum());
                    videoModel.setVideoSize(String.valueOf(file.getSize()));
                    videoRepository.saveAndFlush(videoModel);

                    VideoUrlsModel videoUrlsModel = new VideoUrlsModel();
                    videoUrlsModel.setIdVideo(videoModel.getIdVideo());
                    videoUrlsModel.setPageUrl(url);
                    videoUrlsRepository.saveAndFlush(videoUrlsModel);
                    ObjectMapper mapper = new ObjectMapper();

                    ObjectNode node = mapper.convertValue(videoModel, ObjectNode.class);
                    node.put("filepath", storeResult.getVideoAdded().getAbsolutePath());
                    return new Resposta(node).success();
                } else {
                    throw new Exception("Video já existe");
                }
            } else {
                throw new Exception("Erro ao salvar o arquivo");
            }
        } catch (Exception e) {
            if (storeResult != null && storeResult.getVideoAdded() != null) {
                storeResult.getVideoAdded().delete();
            }
            e.printStackTrace();
            return new Resposta().failed(e);
        }

    }

    @PostMapping("/addURLv2")
    public Resposta addUrlJson(@RequestBody WebHeaderJS webHeaderJS) {
        try {
            String downloadUrl = webHeaderJS.getDownloadUrl();
            String pageUrl = webHeaderJS.getPageUrl();
            Integer idLocation = webHeaderJS.getIdLocation();
            return addUrl(idLocation, downloadUrl, pageUrl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new Resposta().failed("Erro ao baixar");
    }

    @PostMapping("/addURL")
    public Resposta addUrl(@RequestParam Map<String, String> body) {
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            String downloadUrl = new String(decoder.decode(body.get("downloadUrl")), StandardCharsets.UTF_8);
            String pageUrl = new String(decoder.decode(body.get("pageUrl")), StandardCharsets.UTF_8);
            return addUrl(null, downloadUrl, pageUrl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new Resposta().failed("Erro ao baixar");
    }

    private Resposta addUrl(Integer idLocation, String downloadUrl, String pageUrl) {
        try {
            if (idLocation == null) {
                List<LocationModel> list = locationRepository.findAll();
                LocationModel bestLocation = null;
                long bestSize = 0l;
                for (LocationModel lm : list) {
                    File path = new File(lm.getPath());
                    long maxSize = path.getUsableSpace();
                    if (maxSize > bestSize) {
                        bestLocation = lm;
                        bestSize = maxSize;
                    }
                }
                idLocation = bestLocation.getIdLocation();
            }
            VideoModel videoModel = new VideoModel();
            videoModel.setCode(getNextCode());
            videoModel.setIdLocation(idLocation);
            videoRepository.saveAndFlush(videoModel);

            DownloadQueue downloadQueue = new DownloadQueue();
            downloadQueue.setIdVideo(videoModel.getIdVideo());
            downloadQueue.setPageUrl(pageUrl);
            downloadQueue.setIdLocation(idLocation);
            downloadQueue.setSituacao("Aguardando");
            downloadQueue.setVideoUrl(downloadUrl);
            downloadQueue.setCode(videoModel.getCode());
            downloadQueueRepository.saveAndFlush(downloadQueue);
            if (addToRealQueue) {
                videoDownloadService.addToQueue(downloadQueue);
            }
            return new Resposta().success();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new Resposta().failed("Erro ao baixar");
    }
}
