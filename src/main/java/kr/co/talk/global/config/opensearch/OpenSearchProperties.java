package kr.co.talk.global.config.opensearch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * backend_config statistics-opensearch.yml에 등록
 */
@Data
@ConfigurationProperties(prefix = OpenSearchProperties.PREFIX)
public class OpenSearchProperties {
    public static final String PREFIX = "opensearch";

    private String host = "localhost";
    private String username = "username";
    private String password = "password";
}
