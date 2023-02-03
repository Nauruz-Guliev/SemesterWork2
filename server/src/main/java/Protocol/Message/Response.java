package Protocol.Message;


import Protocol.Message.ResponseValues.ResponseValue;

public record Response(Type type, ResponseValue value) {

    @Override
    public String toString() {
        return "Response{" +
                "type=" + type +
                ", value=" + value +
                '}';
    }

    public enum Type {
        RESPONSE_ERROR((byte) 1),  //содержит Error
        RESPONSE_SUCCESS((byte) 2); //содержит Success с объектом

        final byte value;
        public byte getValue(){
            return value;
        }
        Type(byte value) {
            this.value = value;
        }
    }
}
