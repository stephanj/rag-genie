package com.devoxx.genie.web.rest;

import com.devoxx.genie.service.dto.ChatDTO;
import com.devoxx.genie.service.entity.ChatService;
import com.devoxx.genie.web.rest.errors.BadRequestAlertException;
import io.github.jhipster.web.util.HeaderUtil;
import io.github.jhipster.web.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/**
 * REST controller for managing {@link com.devoxx.genie.domain.Chat}.
 */
@RestController
@RequestMapping("/api")
public class ChatResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChatResource.class);

    private static final String ENTITY_NAME = "chat";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ChatService chatService;

    public ChatResource(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * {@code PUT /chat}: Updates an existing chat.
     *
     * @param chatDTO the chatDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated chatDTO,
     * or with status {@code 400 (Bad Request)} if the chatDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the chatDTO couldn't be updated.
     */
    @PutMapping("/chat")
    public ResponseEntity<ChatDTO> updateCompliment(@RequestBody ChatDTO chatDTO) {
        LOGGER.debug("REST request to update Chat : {}", chatDTO);
        if (chatDTO.id() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        var result = chatService.save(chatDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, chatDTO.id().toString()))
            .body(result);
    }

    /**
     * {@code GET  /compliments} : get all the compliments.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of compliments in body.
     */
    @GetMapping("/chat")
    public ResponseEntity<List<ChatDTO>> getAllChats(Pageable pageable) {
        LOGGER.debug("REST request to get Compliments");
        var page = chatService.findAll(pageable);
        var headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /chat/:id} : get the "id" chat.
     *
     * @param id the id of the chatDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the chatDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/chat/{id}")
    public ResponseEntity<ChatDTO> getChat(@PathVariable Long id) {
        LOGGER.debug("REST request to get Chat : {}", id);
        return ResponseEntity.ok().body(chatService.findOne(id));
    }

    /**
     * {@code DELETE  /chat/:id} : delete the "id" chat.
     *
     * @param id the id of the chatDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/chat/{id}")
    public ResponseEntity<Object> deleteChat(@PathVariable Long id) {
        LOGGER.debug("REST request to delete Compliment : {}", id);
        chatService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(com.devoxx.genie.web.rest.util.HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString()))
            .build();
    }
}
