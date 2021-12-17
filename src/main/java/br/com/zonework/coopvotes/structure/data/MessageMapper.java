package br.com.zonework.coopvotes.structure.data;

import lombok.Getter;

public enum MessageMapper {
    KNOWN("ERROR-000"),
    ARGUMENT_INVALID("ERROR-001"),
    RESOURCE_NOT_FOUND("ERROR-002"),
    REQUEST_METHOD_INVALID("ERROR-003"),
    URL_NOT_FOUND("ERROR-004"),
    MEDIA_TYPE_NOT_SUPPORTED("ERROR-005"),
    REQUEST_INVALID("ERROR-006"),
    ILLEGAL_STATE_STAVE("ERROR-007"),
    THEME_CONFLICT("ERROR-008"),
    STAVE_CANCEL_ERROR("ERROR-009"),
    LEGAL_DOCUMENT_NUMBER("ERROR-010");

    @Getter
    private final String code;

    MessageMapper(String code) {
        this.code = code;
    }
}
