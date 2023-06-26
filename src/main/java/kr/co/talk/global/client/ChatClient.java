package kr.co.talk.global.client;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import kr.co.talk.global.config.FeignLoggingConfig;

@FeignClient(name = "CHAT-SERVICE", configuration = FeignLoggingConfig.class)
public interface ChatClient {
    @GetMapping("/chat/keyword/name/{questionIds}")
    public List<String> keywordName(@PathVariable("questionIds") List<Long> questionIds);
}
