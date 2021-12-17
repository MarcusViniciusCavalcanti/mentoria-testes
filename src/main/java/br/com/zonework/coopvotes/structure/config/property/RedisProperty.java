package br.com.zonework.coopvotes.structure.config.property;

import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("coopvotes.configurations.redis")
public class RedisProperty {

    @NotNull(message = "redis host not be null, configure redis host")
    private String host;

    @NotNull(message = "redis port not be null, configure redis port")
    private Integer port;
}
