package com.mcdead.busycoder.socialcipher.client.data.entity.user;

import com.mcdead.busycoder.socialcipher.client.data.entity.user.id.UserIdChecker;
import com.mcdead.busycoder.socialcipher.client.data.entity.user.id.UserIdCheckerGenerator;

public class UserEntityGenerator {
    public static UserEntity generateUserEntity(
            final long peerId,
            final String name)
    {
        if (name == null) return null;
        if (name.isEmpty()) return null;

        UserIdChecker userIdChecker = UserIdCheckerGenerator.generateUserIdChecker();

        if (userIdChecker == null)
            return null;
        if (!userIdChecker.isValid(peerId))
            return null;

        return new UserEntity(peerId, name);
    }
}
