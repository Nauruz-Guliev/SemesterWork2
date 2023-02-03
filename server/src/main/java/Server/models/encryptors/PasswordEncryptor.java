package Server.models.encryptors;

import org.apache.commons.codec.digest.DigestUtils;

public class PasswordEncryptor implements Encryptor<String>{

    @Override
    public String encrypt(String object) {
        return DigestUtils.md5Hex(object).toUpperCase();
    }
}
