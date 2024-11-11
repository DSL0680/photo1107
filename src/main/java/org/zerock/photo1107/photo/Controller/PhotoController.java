package org.zerock.photo1107.photo.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.photo1107.photo.service.PhotoService;

import java.util.Map;
import java.util.Arrays;

@RestController
@RequestMapping("/api/v1")
@Log4j2
@RequiredArgsConstructor

public class PhotoController {

    private final PhotoService photoService;


    @PostMapping("saveState")
    public ResponseEntity<String[]> saveState(@RequestBody String result) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper(); // Map으로 변경하기 위한 mapper객체 선언

        Map<String, String> resultMap = objectMapper.readValue(result, Map.class); // 인코딩된값 map으로 가져오기
        String encodingImage = resultMap.get("image"); // image: encoding 값중에 encoding값을 가져오기

        log.info("---------------");
        log.info(result);



        String[] filename = photoService.decoding(encodingImage); // service를 통해 디코딩된 파일이름을 String배열에 넣어주기
        log.info(Arrays.toString(filename));




        return ResponseEntity.ok(filename);
    }

}
