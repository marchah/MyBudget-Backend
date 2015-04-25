package security

import bcrypt.BcryptService

/**
 * Created by marcha on 4/23/15.
 */
class PasswordEncoder {

    def bcryptService = new BcryptService()

    def encodePassword(String password) {
        return bcryptService.hashPassword(password)
    }

    def isPasswordValid(String candidate, String hashedPassword) {
        if(candidate == null || hashedPassword ==  null) {
            return false
        }

        bcryptService.checkPassword(candidate, hashedPassword)
    }
}