package com.ticketbooking.util;

import org.mindrot.jbcrypt.BCrypt;

public class UserServiceUtil
{
    public static String hashedPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean checkPassword(String plainPassword, String hashPassword) {
        return BCrypt.checkpw(plainPassword, hashPassword);
    }
}
