package br.com.zonework.coopvotes.application.controller;

import static java.lang.Boolean.TRUE;

import br.com.zonework.coopvotes.application.api.VoteApi;
import br.com.zonework.coopvotes.application.dto.InputIncludeVoteDto;
import br.com.zonework.coopvotes.application.usecase.IncludeVote;
import br.com.zonework.coopvotes.core.session.validator.ValidatorLegalDocumentNumberAssociate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/vote")
@RequiredArgsConstructor
public class VoteEndpoint implements VoteApi {

    private final IncludeVote includeVote;

    @Override
    @PostMapping
    public ResponseEntity<Void> includeVote(InputIncludeVoteDto inputIncludeVoteDto) {
        var idStave = inputIncludeVoteDto.getIdStave();
        var legalDocumentNumber = inputIncludeVoteDto.getLegalDocumentNumberAssociate();
        log.debug("Receive voto to stave {}", idStave);
        log.debug("With payload {}", inputIncludeVoteDto);

        ValidatorLegalDocumentNumberAssociate.validate(legalDocumentNumber);

        if (TRUE.equals(inputIncludeVoteDto.getValue())) {
            includeVote.includePositive(idStave, legalDocumentNumber);
        } else {
            includeVote.includeNegative(idStave, legalDocumentNumber);
        }

        return ResponseEntity.accepted().build();
    }
}
