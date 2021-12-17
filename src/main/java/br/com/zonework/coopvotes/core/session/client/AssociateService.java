package br.com.zonework.coopvotes.core.session.client;

import br.com.zonework.coopvotes.core.session.model.AssociateGrandAuthorizeVote;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
    decode404 = true,
    name = "associate-service",
    url = "${coopvotes.configurations.associateService.url}",
    fallbackFactory = AssociateFallbackFactory.class)
public interface AssociateService {


    @GetMapping("/user/{legalDocumentNumber}")
    AssociateGrandAuthorizeVote getGrandAuthorizedAssociate(
        @PathVariable("legalDocumentNumber") String legalDocumentNumber);
}
