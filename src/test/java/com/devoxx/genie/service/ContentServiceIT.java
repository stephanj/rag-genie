package com.devoxx.genie.service;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.repository.UserRepository;
import com.devoxx.genie.service.dto.ContentDTO;
import com.devoxx.genie.service.dto.enumeration.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@GenieServiceTest
class ContentServiceIT {

    @Autowired
    ContentService contentService;

    @Autowired
    UserRepository userRepository;

    @Test
    void testSave() {
        ContentDTO contentDTO = new ContentDTO();
        ContentDTO saved = contentService.save(contentDTO);
        assertThat(saved.getId()).isNotNull();
    }

    @Test
    void testFindAllByUserId_noContent() {
        User user = userRepository.findAll().getFirst();
        Page<ContentDTO> allByUserId =
            contentService.findAllByUserId(Pageable.unpaged(), user.getId(), "test", "contains");
        assertThat(allByUserId.getTotalElements()).isZero();
    }

    @Test
    void testFindAllByUserId_withContent() {
        User user = userRepository.findAll().getFirst();

        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setName("test");
        contentDTO.setUserId(user.getId());
        contentDTO.setContentType(ContentType.CODE);
        contentDTO.setValue("Test");
        contentDTO.setSource("GitHub");
        contentDTO.setDescription("test");
        ContentDTO savedContent = contentService.save(contentDTO);
        assertThat(savedContent.getId()).isNotNull();

        Page<ContentDTO> allByUserId =
            contentService.findAllByUserId(Pageable.unpaged(), user.getId(), "test", "contains");
        assertThat(allByUserId.getTotalElements()).isEqualTo(1);

        ContentDTO foundContent = allByUserId.stream().toList().getFirst();

        assertThat(foundContent.getUserId()).isEqualTo(user.getId());
        assertThat(foundContent.getName()).isEqualTo("test");
        assertThat(foundContent.getContentType()).isEqualTo(ContentType.CODE);
        assertThat(foundContent.getValue()).isEqualTo("Test");
        assertThat(foundContent.getDescription()).isEqualTo("test");
        assertThat(foundContent.getTokenCount()).isEqualTo(1L);
        assertThat(foundContent.getSource()).isEqualTo("GitHub");
    }

    @Test
    void testFindAllByUserId() {
        User user = userRepository.findAll().getFirst();

        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setName("test");
        contentDTO.setUserId(user.getId());
        contentDTO.setContentType(ContentType.CODE);
        contentDTO.setValue("Test");
        contentDTO.setSource("GitHub");
        contentDTO.setDescription("test");
        ContentDTO savedContent = contentService.save(contentDTO);
        assertThat(savedContent.getId()).isNotNull();

        Page<ContentDTO> allByUserId = contentService.findAllByUserId(Pageable.unpaged(), user.getId());
        assertThat(allByUserId.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testFindById() {
        User user = userRepository.findAll().getFirst();
        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setName("test");
        contentDTO.setUserId(user.getId());
        contentDTO.setContentType(ContentType.CODE);
        contentDTO.setValue("Test");
        contentDTO.setSource("GitHub");
        contentDTO.setDescription("test");
        ContentDTO savedContent = contentService.save(contentDTO);

        assertThat(savedContent.getId()).isNotNull();

        ContentDTO byId = contentService.findById(savedContent.getId());
        assertThat(byId.getName()).isEqualTo("test");
        assertThat(byId.getUserId()).isEqualTo(user.getId());
        assertThat(byId.getContentType()).isEqualTo(ContentType.CODE);
    }

    @Test
    void testFindAll() {
        User user = userRepository.findAll().getFirst();

        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setName("test");
        contentDTO.setUserId(user.getId());
        contentDTO.setContentType(ContentType.CODE);
        contentDTO.setValue("Test");
        contentDTO.setSource("GitHub");
        contentDTO.setDescription("test");
        ContentDTO savedContent = contentService.save(contentDTO);
        assertThat(savedContent.getId()).isNotNull();

        List<ContentDTO> all = contentService.findAll();

        assertThat(all.size()).isEqualTo(1);
    }

    @Test
    void testFindAll_withPaging() {
        User user = userRepository.findAll().getFirst();

        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setName("test");
        contentDTO.setUserId(user.getId());
        contentDTO.setContentType(ContentType.CODE);
        contentDTO.setValue("Test");
        contentDTO.setSource("GitHub");
        contentDTO.setDescription("test");
        ContentDTO savedContent = contentService.save(contentDTO);
        assertThat(savedContent.getId()).isNotNull();

        Page<ContentDTO> all = contentService.findAll(Pageable.unpaged());

        assertThat(all.getTotalElements()).isEqualTo(1);
    }

    @Test
    void testDeleteById() {
        User user = userRepository.findAll().getFirst();

        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setName("Devoxx FAQ");
        contentDTO.setUserId(user.getId());
        contentDTO.setValue("Test content");
        contentDTO.setContentType(ContentType.TEXT);
        contentDTO.setSource("User input");
        ContentDTO save = contentService.save(contentDTO);
        assertThat(save.getId()).isNotNull();

        contentService.deleteById(save.getId())
            .ifPresentOrElse(aBoolean -> assertThat(true).isTrue(),
                () -> assertThat(false).isTrue());
    }

    @Test
    void testCalcTotalTokensForUser() {
        User user = userRepository.findAll().getFirst();

        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setName("Devoxx FAQ");
        contentDTO.setUserId(user.getId());
        contentDTO.setValue("Test content");
        contentDTO.setContentType(ContentType.TEXT);
        contentDTO.setSource("User input");
        ContentDTO save = contentService.save(contentDTO);
        assertThat(save.getId()).isNotNull();

        int totalTokens = contentService.calcTotalTokensForUser(user.getId());
        assertThat(totalTokens).isPositive();
    }
}
