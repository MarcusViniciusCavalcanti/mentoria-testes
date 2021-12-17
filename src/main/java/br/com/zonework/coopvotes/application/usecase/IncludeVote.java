package br.com.zonework.coopvotes.application.usecase;

import static java.lang.Boolean.FALSE;
import static java.util.concurrent.CompletableFuture.runAsync;

import br.com.zonework.coopvotes.core.session.client.AssociateService;
import br.com.zonework.coopvotes.core.session.entity.VoteEntity;
import br.com.zonework.coopvotes.core.session.factory.VoteFactory;
import br.com.zonework.coopvotes.core.session.model.AssociateGrandAuthorizeVote;
import br.com.zonework.coopvotes.core.session.service.AggregatorVoteService;
import br.com.zonework.coopvotes.structure.repository.VoteRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IncludeVote {

    private final VoteRepository voteRepository;
    private final AssociateService associateService;
    private final AggregatorVoteService aggregatorVoteService;

    @Async
    public void includePositive(Long id, String legalDocument) {
        log.debug("Running include vote positive in stave {}", id);
        runAsync(() -> {
            var legalDocumentClean = getLegalDocumentClean(legalDocument);
            var hasVote = voteRepository
                .existsByAssociatedIdentifierAndStaveId(legalDocumentClean, id);
            if (FALSE.equals(hasVote)) {
                var associateGrandAuthorizeVote = callServiceAssociate(legalDocumentClean);
                if (Objects.nonNull(associateGrandAuthorizeVote)) {
                    log.debug("Receive status service {}", associateGrandAuthorizeVote.getStatus());
                    associateGrandAuthorizeVote.setLegalDocument(legalDocumentClean);
                    var vote = VoteFactory.getInstance()
                        .createVotePositive(associateGrandAuthorizeVote);
                    completeProcess(id, vote, "Include vote positive in stave by id {}");
                }
            }
        });
    }

    @Async
    public void includeNegative(Long id, String legalDocument) {
        log.debug("Running include vote negative in stave {}", id);
        runAsync(() -> {
            var legalDocumentClean = getLegalDocumentClean(legalDocument);
            var hasVote = voteRepository
                .existsByAssociatedIdentifierAndStaveId(legalDocumentClean, id);
            if (FALSE.equals(hasVote)) {
                var associateGrandAuthorizeVote = callServiceAssociate(legalDocumentClean);
                if (Objects.nonNull(associateGrandAuthorizeVote)) {
                    log.debug("Receive status service {}", associateGrandAuthorizeVote.getStatus());
                    associateGrandAuthorizeVote.setLegalDocument(legalDocumentClean);
                    var vote =
                        VoteFactory.getInstance().createVoteNegative(associateGrandAuthorizeVote);
                    completeProcess(id, vote, "Include vote negative in stave by id {}");
                }
            }
        });
    }

    private void completeProcess(Long id, VoteEntity voteEntity, String logMessage) {
        log.debug(logMessage, id);
        aggregatorVoteService.includeVote(id, voteEntity);
    }

    private AssociateGrandAuthorizeVote callServiceAssociate(String legalDocument) {
        log.debug("Calling service associate get grand authorize associate");
        return associateService.getGrandAuthorizedAssociate(legalDocument);
    }

    private String getLegalDocumentClean(String legalDocument) {
        return legalDocument
            .replace(".", "")
            .replace("-", "");
    }
}
