package br.com.zonework.coopvotes.core.session.factory;

import br.com.zonework.coopvotes.core.session.entity.VoteEntity;
import br.com.zonework.coopvotes.core.session.model.AssociateGrandAuthorizeVote;
import java.time.LocalDateTime;
import java.time.ZoneId;

public interface VoteFactory {

    static VoteFactory getInstance() {
        return factory();
    }

    private static VoteFactory factory() {
        return new VoteFactory() {
            @Override
            public VoteEntity createVotePositive(AssociateGrandAuthorizeVote authorizeVote) {
                return getVoteEntity(authorizeVote, Boolean.TRUE);
            }

            @Override
            public VoteEntity createVoteNegative(AssociateGrandAuthorizeVote authorizeVote) {
                return getVoteEntity(authorizeVote, Boolean.FALSE);
            }

            private VoteEntity getVoteEntity(
                AssociateGrandAuthorizeVote authorizeVote,
                Boolean type) {
                var vote = new VoteEntity();
                vote.setVoteType(type);
                vote.setAssociatedIdentifier(authorizeVote.getLegalDocument());
                vote.setStatusAssociate(authorizeVote.getStatus());
                vote.setDate(LocalDateTime.now(ZoneId.of("UTC")));
                vote.setProcessed(Boolean.FALSE);
                return vote;
            }
        };
    }

    VoteEntity createVotePositive(AssociateGrandAuthorizeVote associateGrandAuthorizeVote);

    VoteEntity createVoteNegative(AssociateGrandAuthorizeVote associateGrandAuthorizeVote);
}
