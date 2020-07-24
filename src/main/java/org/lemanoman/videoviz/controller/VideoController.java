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
@RequestMapping(value = Constants.API_BASE_URL + "/video", produces = MediaType.APPLICATION_JSON_VALUE)

public class VideoController {

    @Value("${addToRealQueue}")
    private Boolean addToRealQueue;


    @Autowired
    private VideoJDBCRepository jdbcRepository;

    @Autowired
    private VideoFileService videoFileService;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private VideoUrlsRepository videoUrlsRepository;

    @Autowired
    private DownloadQueueRepository downloadQueueRepository;

    @Autowired
    private VideoDownloadService videoDownloadService;

    @Autowired
    private VideoHistoryRepository videoHistoryRepository;

    @GetMapping("/")
    public Resposta listAll() {
        try {
            return new Resposta(videoRepository.findAll()).success();
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @PostMapping("/{id}/set/{field}/{value}")
    public Resposta getById(@PathVariable Integer id, @PathVariable String field, @PathVariable String value) {
        try {
            if (field != null && value != null) {
                if ("rating".equals(field)) {
                    Optional<VideoModel> optVM = videoRepository.findById(id);
                    if (optVM.isPresent()) {
                        VideoModel vm = optVM.get();
                        vm.setRating(Integer.parseInt(value));
                        videoRepository.save(vm);
                        videoRepository.flush();
                        return new Resposta(vm).success();
                    }
                }
            }
            return new Resposta().failed(new Exception("O Field e o Value não deve ser nulo"));
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @PostMapping("/novo")
    public Resposta novo(@RequestBody VideoJS videoJS) {
        try {
            VideoModel videoModel = null;
            if(videoJS.getIdVideo()==null){
                videoModel = new VideoModel();
                videoModel.setCode(getNextCode());
                videoModel.setInvalid(0);
                videoModel.setIsdeleted(0);
                videoModel.setDateAdded(new Timestamp(new Date().getTime()));
                videoModel.setIsfileexist(1);
            }else{
                videoModel = videoRepository.findById(videoJS.getIdVideo()).orElse(new VideoModel());
            }
            videoModel.setOriginalTags(videoJS.getOriginalTags());
            videoModel.setTitle(videoJS.getTitle());
            videoRepository.saveAndFlush(videoModel);

            VideoUrlsModel videoUrlsModel = new VideoUrlsModel();
            videoUrlsModel.setIdVideo(videoModel.getIdVideo());
            videoUrlsModel.setPageUrl(videoJS.getPageUrl());
            videoUrlsModel.setMidiaUrl(videoJS.getMediaUrl());
            videoUrlsRepository.saveAndFlush(videoUrlsModel);
            return new Resposta(jdbcRepository.getInfoAsJS(videoModel.getIdVideo())).success();
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Resposta().failed(ex);
        }
    }

    @GetMapping("/{id}")
    public Resposta getById(@PathVariable Integer id) {
        try {
            return new Resposta(jdbcRepository.getInfo(id));
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @PutMapping("/updateHistory/{id}")
    public Resposta updateHistory(@PathVariable Integer id) {
        try {
            VideoModel videoModel = videoRepository.findById(id).get();
            videoModel.setLastwatched(new Timestamp(new Date().getTime()));
            Integer total = videoModel.getTotalWatched();
            if (total != null) {
                videoModel.setTotalWatched(total + 1);
            } else {
                videoModel.setTotalWatched(1);
            }
            VideoHistoryModel videoHistoryModel = new VideoHistoryModel();
            videoHistoryModel.setIdVideo(id);
            videoHistoryModel.setWatched(new Timestamp(new Date().getTime()));
            videoHistoryRepository.save(videoHistoryModel);
            videoRepository.saveAndFlush(videoModel);
            return new Resposta(videoModel);
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @GetMapping("/type/")
    public Resposta listTypes() {
        try {
            return new Resposta(jdbcRepository.listTypes());
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @GetMapping("/type/{type}")
    public Resposta listType(@PathVariable String type) {
        try {
            return new Resposta(jdbcRepository.listVideo(type, null, null));
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @GetMapping("/lastBusca")
    public Resposta getLastBusca() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File lastFile = new File("lastBusca.json");
            if (lastFile.exists()) {
                ArrayNode arrayNode = mapper.readValue(lastFile, ArrayNode.class);
                return new Resposta(arrayNode);
            } else {
                return new Resposta().failed("Erro ao encontrar o arquivo");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            return new Resposta().failed(ex);
        }
    }

    private Resposta saveFile(String filename, InputStream inputStream, String pageUrl, long fileSize) {
        StoreResult storeResult = null;
        try {

            if (filename != null) {
                ScrapResult result = null;
                try {
                    result = Scrapper.getScrapResult(pageUrl);
                } catch (VideoNotFoundException | PageNotFoundException | IOException e) {
                    e.printStackTrace();
                }


                String code = getNextCode();

                VideoModel videoModel = new VideoModel();
                videoModel.setCode(code);
                try {
                    videoRepository.saveAndFlush(videoModel);
                } catch (Exception ex) {
                    videoModel.setCode(getNextCode());
                    videoRepository.saveAndFlush(videoModel);
                    ex.printStackTrace();
                }

                storeResult = videoFileService.storeVideo(videoModel.getCode() + ".mp4", inputStream);

                if (storeResult != null) {
                    if (jdbcRepository.getByMD5(storeResult.getMd5sum()).isEmpty()) {
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
                        videoModel.setInvalid(0);
                        videoModel.setIsdeleted(0);
                        videoModel.setDateAdded(new Timestamp(new Date().getTime()));
                        videoModel.setIsfileexist(1);
                        videoModel.setMd5Sum(storeResult.getMd5sum());
                        videoModel.setVideoSize(String.valueOf(fileSize));
                        videoRepository.saveAndFlush(videoModel);

                        VideoUrlsModel videoUrlsModel = new VideoUrlsModel();
                        videoUrlsModel.setIdVideo(videoModel.getIdVideo());
                        videoUrlsModel.setPageUrl(pageUrl);
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
            } else {
                throw new Exception("Filename invalido");
            }
        } catch (Exception e) {
            if (storeResult != null && storeResult.getVideoAdded() != null) {
                storeResult.getVideoAdded().delete();
            }
            e.printStackTrace();
            return new Resposta().failed(e);
        }

    }

    @PostMapping("/salvar")
    public Resposta handleFileUpload(
            @RequestParam("file") MultipartFile file,
            @RequestParam("url") String url
    ) {
        String filename = file.getOriginalFilename();
        StoreResult storeResult = null;
        try {

            if (filename != null) {
                ScrapResult result = null;
                try {
                    result = Scrapper.getScrapResult(url);
                } catch (VideoNotFoundException | PageNotFoundException | IOException e) {
                    e.printStackTrace();
                }
                String code = getNextCode();
                storeResult = videoFileService.storeVideo(code + ".mp4", file.getInputStream());

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
            } else {
                throw new Exception("Filename invalido");
            }
        } catch (Exception e) {
            if (storeResult != null && storeResult.getVideoAdded() != null) {
                storeResult.getVideoAdded().delete();
            }
            e.printStackTrace();
            return new Resposta().failed(e);
        }
    }

    @PostMapping("/adicionarVideo")
    public Resposta adicionarVideo(@RequestBody VideoModel videoModel) {
        if (videoModel.getCode() == null || videoModel.getCode().isEmpty()) {
            videoModel.setCode(getNextCode());
        }
        videoModel.setDateAdded(new Timestamp(new Date().getTime()));
        VideoModel saved = videoRepository.saveAndFlush(videoModel);
        return new Resposta(saved).success();
    }

    @PostMapping("/adicionarArquivo/{idVideo}")
    public Resposta handleFileUpload(
            @PathVariable("idVideo") Integer idVideo,
            @RequestParam("file") MultipartFile file
    ) {
        String filename = file.getOriginalFilename();
        StoreResult storeResult = null;
        try {
            if (filename != null) {
                VideoModel vm = videoRepository.findById(idVideo).orElse(null);
                if (vm != null && vm.getCode() != null) {
                    storeResult = videoFileService.storeVideo(vm.getCode() + ".mp4", file.getInputStream());
                    if (storeResult != null) {
                        vm.setMd5Sum(storeResult.getMd5sum());
                        if (jdbcRepository.getByMD5(storeResult.getMd5sum()).isEmpty()) {
                            vm.setInvalid(0);
                            vm.setIsfileexist(1);
                            vm.setVideoSize("" + storeResult.getVideoAdded().length());
                            videoRepository.saveAndFlush(vm);

                            ObjectMapper mapper = new ObjectMapper();
                            ObjectNode node = mapper.convertValue(vm, ObjectNode.class);
                            node.put("filepath", storeResult.getVideoAdded().getAbsolutePath());
                            return new Resposta(jdbcRepository.getInfoAsJS(vm.getIdVideo())).success();

                        } else {
                            throw new Exception("Video já existe");
                        }
                    } else {
                        throw new Exception("Erro ao salvar o arquivo");
                    }
                } else {
                    throw new Exception("Sem codigo relacionado");
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


    private String getNextCode() {

        int max = 7;

        String oldCode = jdbcRepository.getLastCode();
        Integer intCode;
        if(oldCode==null || oldCode.equals("null")){
            intCode = 0;
        }else{
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

    @PostMapping("/addURLv2")
    public Resposta addUrlJson(@RequestBody WebHeaderJS webHeaderJS) {
        try {
            String downloadUrl = webHeaderJS.getDownloadUrl();
            String pageUrl = webHeaderJS.getPageUrl();
            return addUrl(downloadUrl,pageUrl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new Resposta().failed("Erro ao baixar");
    }

    @GetMapping("/recreate/{code}")
    public Resposta rebuild(@PathVariable("code") String code) {
        try {
            File mp4File = videoFileService.getVideoFileByCode(code);
            if(mp4File.isFile() && mp4File.exists()){
                File image = videoFileService.createPreviewImage(mp4File);
                if(image.exists()){
                    String imageUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                            .path("/media/image/")
                            .path(code)
                            .queryParam("time", new Date().getTime()/1000)
                            .toUriString();
                    return new Resposta(imageUrl).success();
                }else{
                    return new Resposta().failed("Erro ao atualizar");
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Resposta().failed(ex);
        }
        return new Resposta().failed("Erro ao atualizar");
    }

    @PostMapping("/addURL")
    public Resposta addUrl(@RequestParam Map<String, String> body) {
        try {
            Base64.Decoder decoder = Base64.getDecoder();
            String downloadUrl = new String(decoder.decode(body.get("downloadUrl")), StandardCharsets.UTF_8);
            String pageUrl = new String(decoder.decode(body.get("pageUrl")), StandardCharsets.UTF_8);
            return addUrl(downloadUrl,pageUrl);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new Resposta().failed("Erro ao baixar");
    }

    private Resposta addUrl(String downloadUrl,String pageUrl) {
        try {
            VideoModel videoModel = new VideoModel();
            videoModel.setCode(getNextCode());
            videoRepository.saveAndFlush(videoModel);

            DownloadQueue downloadQueue = new DownloadQueue();
            downloadQueue.setIdVideo(videoModel.getIdVideo());
            downloadQueue.setPageUrl(pageUrl);
            downloadQueue.setSituacao("Aguardando");
            downloadQueue.setVideoUrl(downloadUrl);
            downloadQueue.setCode(videoModel.getCode());
            downloadQueueRepository.saveAndFlush(downloadQueue);
            if(addToRealQueue){
                videoDownloadService.addToQueue(downloadQueue);
            }
            return new Resposta().success();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new Resposta().failed("Erro ao baixar");
    }

    @PostMapping("/buscaAvancada")
    public Resposta buscaAvancada(@RequestBody PesquisaAvancadaJS pesquisaAvancadaJS) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            ArrayNode lastBusca = jdbcRepository.buscaAvancada(pesquisaAvancadaJS);
            File lastFile = new File("lastBusca.json");
            if (lastFile.exists()) {
                lastFile.delete();
            }
            mapper.writeValue(lastFile, lastBusca);
            return new Resposta(lastBusca);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Resposta().failed(ex);
        }
    }

    @GetMapping("/type/{type}/{max}")
    public Resposta listType(@PathVariable String type, @PathVariable Integer max) {
        try {
            if (max == null) {
                max = 200;
            }
            return new Resposta(jdbcRepository.listVideo(type, max, null));
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @GetMapping("/playlist/{idPlaylist}")
    public Resposta listPlaylist(@PathVariable Integer idPlaylist) {
        try {
            return new Resposta(videoRepository.findByIdPlaylist(idPlaylist));
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @GetMapping("/type/{type}/{max}/{page}")
    public Resposta listType(@PathVariable String type, @PathVariable Integer max, @PathVariable Integer page) {
        try {
            if (max == null) {
                max = 200;
            }
            if (page == null) {
                page = 0;
            }
            return new Resposta(jdbcRepository.listVideo(type, max, page));
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }

    @DeleteMapping("/{id}")
    public Resposta deleteById(@PathVariable Integer id) {
        try {
            return new Resposta().success();
        } catch (Exception ex) {
            return new Resposta().failed(ex);
        }
    }
}
