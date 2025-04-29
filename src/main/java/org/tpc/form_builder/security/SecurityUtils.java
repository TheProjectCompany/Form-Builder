package org.tpc.form_builder.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.tpc.form_builder.constants.CommonConstants;

@Component
@RequiredArgsConstructor
@Log4j2
public class SecurityUtils {
    public static String getCurrentUserClient() {
        return CommonConstants.DEFAULT_CLIENT;
    }

    public static Long getCurrentUserId() {
        return CommonConstants.DEFAULT_USER_ID;
    }
}
