package com.devoxx.genie.service;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.repository.UserRepository;
import com.devoxx.genie.service.dto.ContentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.net.MalformedURLException;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@GenieServiceTest
class ContentImportServiceIT {

    @Autowired
    ContentImportService contentImportService;

    @Autowired
    UserRepository userRepository;

    @Test
    void testAddContentFromUrl() throws MalformedURLException {
        User user = userRepository.findAll().getFirst();

        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setName("Devoxx FAQ");
        contentDTO.setUserId(user.getId());
        contentDTO.setSource("https://devoxx.be/faq");

        String webPageContent = contentImportService.getContentFromUrl(contentDTO.getSource());

        assertThat(webPageContent).isNotNull();
        assertThat(webPageContent).isNotEmpty();
        assertThat(webPageContent).contains("Devoxx");
    }
}
