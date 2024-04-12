package com.devoxx.genie.service.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * A DTO representing a password change required data - current and new password.
 */
@Setter
@Getter
public class PasswordChangeDTO {
    private String currentPassword;
    private String newPassword;
}
