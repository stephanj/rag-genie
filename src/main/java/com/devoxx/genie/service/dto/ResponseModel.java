package com.devoxx.genie.service.dto;

import java.util.List;

public class ResponseModel {
    private List<DocumentDTO> usedDocuments;
    private InteractionDTO interactionDTO;

    public List<DocumentDTO> getUsedDocuments() {
        return usedDocuments;
    }

    public void setUsedDocuments(List<DocumentDTO> usedDocuments) {
        this.usedDocuments = usedDocuments;
    }

    public InteractionDTO getInteractionDTO() {
        return interactionDTO;
    }

    public void setInteractionDTO(InteractionDTO interactionDTO) {
        this.interactionDTO = interactionDTO;
    }
}
