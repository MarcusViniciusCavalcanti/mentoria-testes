package br.com.zonework.coopvotes.core.session.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ReprocessGrandAuthorizeAssociateMessage(
    @JsonProperty("staveId") Long staveId,
    @JsonProperty("identifier") String identifier
) {

}
