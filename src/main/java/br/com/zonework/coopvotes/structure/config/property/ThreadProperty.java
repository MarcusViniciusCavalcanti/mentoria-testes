package br.com.zonework.coopvotes.structure.config.property;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("coopvotes.configurations.thread")
public class ThreadProperty {

    @Positive(message = "Please configure core pool size")
    private Integer core;

    @Positive(message = "Please configure pool size")
    private Integer poolSize;

    @Positive(message = "Please configure queue capacity pool thread")
    private Integer queueCapacity;

    @NotBlank(message = "Please configure prefix thread pool name")
    private String prefix;
}
