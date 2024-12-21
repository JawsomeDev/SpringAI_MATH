package com.example.imagetextgen.service;

import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.model.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ImageTextGenService {

    private final ChatModel chatModel;

    @Value("classpath:/system.message")
    private Resource defaultSystemMessage;

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

    public List<String> searchYouTubeVideos(String query) {
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&q=EBS " +
                query + "&order=relevance&key=본인의 key를 적으세요";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        List<String> videoUrls = new ArrayList<>();
        JSONObject jsonResponse = new JSONObject(response.getBody());
        JSONArray items = jsonResponse.getJSONArray("items");

        for (int i = 0; i < items.length(); i++) {
            JSONObject item = items.getJSONObject(i);
            String videoId = item.getJSONObject("id").getString("videoId");
            videoUrls.add("https://www.youtube.com/watch?v=" + videoId);
        }
        return videoUrls;
    }

    public String extractKeyPhraseForYouTubeSearch(String analysisText) {

        String keyword = "";
        // '핵심 키워드:' 부분을 찾아 키워드 추출
        Pattern pattern = Pattern.compile("핵심 키워드:\\s*(.*)");
        Matcher matcher = pattern.matcher(analysisText);
        if (matcher.find()) {
            keyword = matcher.group(1).trim(); // 핵심 키워드 추출
        } else {
            // 핵심 키워드가 없는 경우 기본값 설정 (예: analysisText의 첫 100자 사용)
            keyword = analysisText.length() > 100 ? analysisText.substring(0, 100) : analysisText;
        }
        return keyword;
    }
}
