package org.zerock.photo1107.photo.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.zerock.photo1107.photo.service.PhotoService;

import java.io.File;
import java.util.Arrays;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@Log4j2
@RequiredArgsConstructor
public class PhotoController {

    private final PhotoService photoService;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper;

    @PostMapping("saveState")
    public ResponseEntity<String> saveState(@RequestBody String result) throws JsonProcessingException {
        Map<String, String> resultMap = objectMapper.readValue(result, Map.class);
        String encodingImage = resultMap.get("image");

        log.info("Received encoded image data: {}", encodingImage);

        // 디코딩된 파일명을 배열로 받아옴
        String[] filenames = photoService.decoding(encodingImage);
        log.info("Decoded filenames: {}", Arrays.toString(filenames));


        String fastApiUrl = "http://127.0.0.1:9000/upload"; // 이미지 전송
        String searchUrl = "http://127.0.0.1:9000/search";  // 유사 이미지 검색

        for (String filename : filenames) {
            try {
                // 디코딩된 파일을 읽어서 전송
                File file = new File("C:\\decoding\\" + filename);
                if (!file.exists()) {
                    log.warn("File does not exist: {}", filename);
                    continue;
                }

                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.MULTIPART_FORM_DATA);

                // FileSystemResource를 사용하여 파일을 MultiValueMap에 추가
                MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
                body.add("file", new FileSystemResource(file));

                HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

                // FastAPI 서버로 파일 전송
                ResponseEntity<String> uploadResponse = restTemplate.exchange(fastApiUrl, HttpMethod.POST, requestEntity, String.class);
                log.info("File '{}' sent to FastAPI, response: {}", filename, uploadResponse.getBody());

                // 파일을 성공적으로 업로드한 후, 유사 이미지 검색 요청
                HttpEntity<MultiValueMap<String, Object>> searchRequestEntity = new HttpEntity<>(body, headers);
                ResponseEntity<Map> searchResponse = restTemplate.exchange(searchUrl, HttpMethod.POST, searchRequestEntity, Map.class);

                // 유사 이미지 검색 결과 처리
                if (searchResponse.getStatusCode().is2xxSuccessful()) {
                    Map<String, Object> responseMap = searchResponse.getBody();
                    log.info("Similar images for '{}' : {}", filename, responseMap.get("similar_images"));
                    // 유사 이미지 결과에 대한 추가 처리 로직을 여기에 추가할 수 있습니다.
                } else {
                    log.error("Failed to fetch similar images for file '{}'", filename);
                }

            } catch (Exception e) {
                log.error("Error processing file: {}", filename, e);
                return ResponseEntity.status(500).body("Failed to process file: " + filename);
            }
        }

        return ResponseEntity.ok("Files sent to FastAPI and search executed successfully");
    }

}
