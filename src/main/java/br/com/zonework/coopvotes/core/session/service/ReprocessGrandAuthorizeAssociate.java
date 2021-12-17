package br.com.zonework.coopvotes.core.session.service;

import br.com.zonework.coopvotes.core.session.client.AssociateService;
import br.com.zonework.coopvotes.core.session.model.ReprocessGrandAuthorizeAssociateMessage;
import br.com.zonework.coopvotes.structure.repository.VoteRepository;
import com.github.sonus21.rqueue.annotation.RqueueListener;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReprocessGrandAuthorizeAssociate {

    private final VoteRepository voteRepository;
    private final AssociateService associateService;

    @Transactional
    @RqueueListener(value = SchedulerExecutor.REPROCESS_GRAND_AUTHORIZE_ASSOCIATE)
    public void receiveReprocessGrandAuthorizedAssociate(
        ReprocessGrandAuthorizeAssociateMessage message) {
        log.info(
            "Receive message from topic {}",
            SchedulerExecutor.REPROCESS_GRAND_AUTHORIZE_ASSOCIATE
        );
        var staveId = message.staveId();
        var identifier = message.identifier();
        voteRepository.findByStaveIdAndAssociatedIdentifier(staveId, identifier)
            .ifPresentOrElse(voteEntity -> {
                log.info("Running reprocess grand authorize associate");
                var grandAuthorizedAssociate = associateService
                    .getGrandAuthorizedAssociate(voteEntity.getAssociatedIdentifier());

                if (Objects.nonNull(grandAuthorizedAssociate)) {
                    voteEntity.setStatusAssociate(grandAuthorizedAssociate.getStatus());
                    voteRepository.saveAndFlush(voteEntity);
                }
            }, () -> log.warn("Receive Message reprocess vote but not found"));
    }
}
