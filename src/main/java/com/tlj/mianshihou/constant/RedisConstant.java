package com.tlj.mianshihou.constant;

public interface RedisConstant {
    /**
     * 用户签到记录的redis key前缀
     */
    String USER_SIGN_IN_REDIS_KEY_PREFIX = "user:signins:";

    /**
     * 用户签到记录的redis key
     * @param year
     * @param userId
     * @return
     */
    static String getUserSignInRedisKey(int year,long userId) {
//        return USER_SIGN_IN_REDIS_KEY_PREFIX + year + ":" + userId;
        return String.format("%s%d:%d",USER_SIGN_IN_REDIS_KEY_PREFIX,year,userId);
    }


}
