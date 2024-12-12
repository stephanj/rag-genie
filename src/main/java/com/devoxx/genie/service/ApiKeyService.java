package com.devoxx.genie.service;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.domain.UserAPIKey;
import com.devoxx.genie.domain.enumeration.LanguageModelType;
import com.devoxx.genie.repository.UserApiKeyRepository;
import com.devoxx.genie.service.dto.ChatModelDTO;
import com.devoxx.genie.service.dto.UserAPIKeyDTO;
import com.devoxx.genie.service.mapper.UserApiKeyMapper;
import com.devoxx.genie.service.security.EncryptionException;
import com.devoxx.genie.service.security.SecurityKeyService;
import com.devoxx.genie.web.rest.errors.EncryptingAesKeyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

import static com.devoxx.genie.security.AuthoritiesConstants.HARD_CODED_USER_ID;

@Service
public class ApiKeyService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiKeyService.class);
    private final SecurityKeyService securityKeyService;
    private final UserApiKeyRepository userApiKeyRepository;
    private final UserApiKeyMapper userApiKeyMapper;

    public ApiKeyService(SecurityKeyService securityKeyService,
                         UserApiKeyRepository userApiKeyRepository,
                         UserApiKeyMapper userApiKeyMapper) {
        this.securityKeyService = securityKeyService;
        this.userApiKeyRepository = userApiKeyRepository;
        this.userApiKeyMapper = userApiKeyMapper;
    }

    /**
     * Save user API key.
     * @param user the user
     * @param userAPIKeyDTO the api key details
     * @return the saved api key
     */
    @Transactional
    public UserAPIKey save(User user, UserAPIKeyDTO userAPIKeyDTO) {
        UserAPIKey entity = userApiKeyMapper.toEntity(userAPIKeyDTO);

        entity.setCreatedBy(user.getEmail());
        entity.setUser(user);

        String prefix = userAPIKeyDTO.getApiKey().substring(0, 3);
        String suffix = userAPIKeyDTO.getApiKey().substring(userAPIKeyDTO.getApiKey().length() - 4);
        entity.setKeyMask(prefix + "..." + suffix);

        try {
            String encryptedKey = securityKeyService.encrypt(entity.getApiKey());
            entity.setApiKey(encryptedKey);
        } catch (EncryptionException e) {
            LOGGER.error("Error while encrypting the AES Key", e);
            throw new EncryptingAesKeyException();
        }

        return userApiKeyRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public List<UserAPIKeyDTO> findAllByUserId(Long userId) {
        LOGGER.debug("Request to get all API Keys for user {}", userId);
        List<UserAPIKey> allByUserId = userApiKeyRepository.findAllByUserId(userId);
        allByUserId.forEach(apiKey -> apiKey.setApiKey(""));
        return userApiKeyMapper.toDto(allByUserId);
    }

    @Transactional(readOnly = true)
    public boolean isLanguageModelKeyDefined(Long userId, LanguageModelType languageModelType) {
        LOGGER.debug("Request to get all API Keys for user {}", userId);
        return userApiKeyRepository.findByUserIdAndLanguageType(userId, languageModelType).isPresent();
    }

    @Transactional
    public Optional<Boolean> deleteById(Long userId, Long id) {
        LOGGER.debug("Request to delete API Key : {} for user {}", id, userId);
        return userApiKeyRepository.findByUserIdAndId(userId, id)
            .map(key -> {
                userApiKeyRepository.deleteById(key.getId());
                return true;
            });
    }

    @Transactional
    public Optional<String> getApiKeyForUserIdAndLanguageModelType(Long userId,
                                                                   LanguageModelType languageModelType) {
        Optional<UserAPIKey> byUserIdAndLanguageType =
            userApiKeyRepository.findByUserIdAndLanguageType(userId, languageModelType);

        if (byUserIdAndLanguageType.isPresent()) {
            UserAPIKey userAPIKey = byUserIdAndLanguageType.get();
            try {
                String decrypt = securityKeyService.decrypt(userAPIKey.getApiKey());
                userAPIKey.setLastUsed(ZonedDateTime.now());
                userApiKeyRepository.save(userAPIKey);
                return Optional.of(decrypt);
            } catch (EncryptionException e) {
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public Optional<String> getApiKeyForUserIdAndLanguageModelType(ChatModelDTO chatModelDTO) {
        return getApiKeyForUserIdAndLanguageModelType(HARD_CODED_USER_ID,
                                                      chatModelDTO.getLanguageModelDTO().getModelType());
    }
}
