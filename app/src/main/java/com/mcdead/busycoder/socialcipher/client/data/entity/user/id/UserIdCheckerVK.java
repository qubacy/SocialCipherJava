package com.mcdead.busycoder.socialcipher.client.data.entity.user.id;

public class UserIdCheckerVK implements UserIdChecker {
    @Override
    public boolean isValid(final long userId) {
        if (userId == 0) return false;

        return true;
    }
}
