package org.zerock.photo1107;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Log4j2
public class ResultController {

    private String saveResult;


    @PostMapping("saveState")
    public String saveState(@RequestBody String result) {
        log.info(result);

        this.saveResult = result;
        return null;
    }

    @GetMapping("saveState")
    public String saveState() throws JsonProcessingException {

        log.info("-------------");

        ObjectMapper objectMapper = new ObjectMapper();

        String jsonResult = objectMapper.writeValueAsString(saveResult);

        log.info("saveState 호출"+jsonResult);

        return jsonResult;
    }

    @GetMapping("/getData")
    public String getData() {
        log.info("GET /api/getData 호출됨");
        return "{\"message\": \"Hello from Spring!\", \"status\": \"success\"}";
    }
}
