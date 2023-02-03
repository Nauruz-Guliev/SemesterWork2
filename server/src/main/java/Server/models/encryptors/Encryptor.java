package Server.models.encryptors;

public interface Encryptor<T> {

    String encrypt(T object);

}
