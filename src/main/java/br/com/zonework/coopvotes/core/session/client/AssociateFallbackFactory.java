package br.com.zonework.coopvotes.core.session.client;

import feign.FeignException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AssociateFallbackFactory implements FallbackFactory<AssociateService> {

    @Override
    public AssociateService create(Throwable cause) {
        log.error("receive error from client feign {}", cause.getMessage());
        if (cause instanceof FeignException feignException) {
            var status = feignException.status();
            log.warn("Request from feign client, call fallback response with status {}", status);
        }

        return new AssociateReprocessFallback();
    }
}
