package br.com.zonework.coopvotes.core.session.client;

import br.com.zonework.coopvotes.core.session.model.AssociateGrandAuthorizeVote;
import org.springframework.stereotype.Service;

@Service
public class AssociateReprocessFallback implements AssociateService {

    @Override
    public AssociateGrandAuthorizeVote getGrandAuthorizedAssociate(String legalDocumentNumber) {
        var associateGrandAuthorizeVote = new AssociateGrandAuthorizeVote();
        associateGrandAuthorizeVote.setStatus("ABLE_TO_VOTE_CB");
        associateGrandAuthorizeVote.setLegalDocument(legalDocumentNumber);
        return associateGrandAuthorizeVote;
    }
}
