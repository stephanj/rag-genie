package com.devoxx.genie.web.rest;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.service.ContentService;
import com.devoxx.genie.service.dto.ContentDTO;
import com.devoxx.genie.service.user.UserService;
import com.devoxx.genie.web.rest.errors.BadRequestAlertException;
import com.devoxx.genie.web.rest.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.devoxx.genie.security.SecurityUtils.isCurrentUserNotAdmin;

@RestController
@RequestMapping("/api")
public class ContentResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentResource.class);
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_NOT_FOUND_CODE = "Usernotfound";
    private final ContentService contentService;
    private final UserService userService;

    public ContentResource(ContentService contentService,
                           UserService userService) {
        this.contentService = contentService;
        this.userService = userService;
    }

    /**
     * GET /content: Get all content
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body the content
     */
    @GetMapping("/content")
    public ResponseEntity<List<ContentDTO>> getAllContent(Pageable pageable,
                                                          @RequestParam(required = false) String value,
                                                          @RequestParam(required = false) String matchMode) {

        LOGGER.debug("Get all content using paging {}", pageable);

        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        Page<ContentDTO> page = contentService.findAllByUserId(pageable, user.getId(), value, matchMode);

        var headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/content");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    @GetMapping("/content/tokens")
    public ResponseEntity<Integer> getTotalTokens() {
        LOGGER.debug("Get all content for admin");

        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        int totalTokens = contentService.calcTotalTokensForUser(user.getId());
        return ResponseEntity.ok().body(totalTokens);
    }

    /**
     * GET /content/admin: Get all content for admin
     * @param pageable the pagination information
     * @return the ResponseEntity with status 200 (OK) and with body the content
     */
    @GetMapping("/content/admin")
    public ResponseEntity<List<ContentDTO>> getAllContentForAdmin(Pageable pageable) {
        LOGGER.debug("Get all content for admin");

        Page<ContentDTO> page = contentService.findAll(pageable);
        var headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/content/admin");
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * GET /content/:id: Get content by ID
     * @param id the content ID
     * @return the ResponseEntity with status 200 (OK) and with body the content
     */
    @GetMapping("/content/{id}")
    public ResponseEntity<ContentDTO> getContentById(@PathVariable Long id) {
        LOGGER.debug("Get content by ID: {}", id);

        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        ContentDTO content = contentService.findById(id);

        if (isCurrentUserNotAdmin() &&
            !content.getUserId().equals(user.getId())) {
            throw new BadRequestAlertException("User does not own the content", "CONTENT", "permission");
        }

        return ResponseEntity.ok().body(content);
    }

    /**
     * PUT /content: Update content
     * @param contentDTO the content to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated content
     */
    @PutMapping("/content")
    public ResponseEntity<ContentDTO> update(@RequestBody ContentDTO contentDTO) {
        LOGGER.debug("Adding text content: {}", contentDTO);

        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        if (isCurrentUserNotAdmin()) {
            ContentDTO foundContent = contentService.findById(contentDTO.getId());

            // Check if user owns the content
            if (!foundContent.getUserId().equals(user.getId())) {
                throw new BadRequestAlertException("User does not own the content", "CONTENT", "permission");
            }
        }

        ContentDTO savedContent = contentService.save(contentDTO);
        return ResponseEntity.ok().body(savedContent);
    }

    /**
     * DELETE /content Delete all content
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/content")
    public ResponseEntity<Void> deleteAll() {
        LOGGER.debug("Deleting all content");

        // Check if user owns the content
        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        contentService.findAllByUserId(Pageable.unpaged(), user.getId())
            .forEach(contentDTO -> contentService.deleteById(contentDTO.getId()));

        return ResponseEntity.noContent().build();
    }

    /**
     * DELETE /content/:id Delete content by ID
     * @param id the content ID
     * @return the ResponseEntity with status 204 (NO_CONTENT)
     */
    @DeleteMapping("/content/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        LOGGER.debug("Deleting content: {}", id);

        // Check if user owns the content
        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        if (isCurrentUserNotAdmin()) {
            ContentDTO content = contentService.findById(id);

            if (!content.getUserId().equals(user.getId())) {
                throw new BadRequestAlertException("User does not own the content", "USER", "permission");
            }
        }

        contentService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
