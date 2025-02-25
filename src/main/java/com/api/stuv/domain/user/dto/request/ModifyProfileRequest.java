package com.api.stuv.domain.user.dto.request;

import com.api.stuv.domain.user.entity.User;

public record ModifyProfileRequest(
        String context,
        String link
) {

}
