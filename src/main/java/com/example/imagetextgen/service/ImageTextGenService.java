package com.example.imagetextgen.service;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestTemplate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ImageTextGenService {

    @Value("classpath:/system.message")
    private Resource defaultSystemMessage;

    private final ChatModel chatModel;
    public ImageTextGenService(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

     public String analyzeImage(MultipartFile imageFile, String message) throws IOException {
         String contentType = imageFile.getContentType();
         if (!MimeTypeUtils.IMAGE_PNG_VALUE.equals(contentType) &&
                 !MimeTypeUtils.IMAGE_JPEG_VALUE.equals(contentType)) {
             throw new IllegalArgumentException("지원되지 않는 이미지 형식입니다.");
         }

         var media = new Media(MimeType.valueOf(contentType), imageFile.getResource());
         var userMessage = new UserMessage(message, media);
         var systemMessage = new SystemMessage(defaultSystemMessage);
        return chatModel.call(userMessage, systemMessage);
    }
    //                                                                         query=EBS 세제곱근, 제곱근, 곱셈
    public List<String> searchYouTubeVideos(String query) {
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&q=EBS " +
                query + "&order=relevance&key=";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        System.out.println(response.getBody());

        List<String> videoUrls = new ArrayList<>();
        JSONObject jsonResponse = new JSONObject(response.getBody());
        JSONArray items = jsonResponse.getJSONArray("items");

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String videoId = item.getJSONObject("id").getString("videoId");
            videoUrls.add(videoId);
        }
        return videoUrls;
    }

    public String extractKeyYouTubeSearch(String analysisText) {
        String keyword=null;
        if(analysisText.indexOf("핵심 키워드:")!=-1){
               //                                                                    핵심 키워드: 세제곱근, 제곱근, 곱셈
               keyword=analysisText.substring(analysisText.indexOf("핵심 키워드:")).split(":")[1].trim();
        }
        //          세제곱근, 제곱근, 곱셈
        return keyword;
    }
}