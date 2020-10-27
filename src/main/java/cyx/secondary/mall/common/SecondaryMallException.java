package cyx.secondary.mall.common;

public class SecondaryMallException extends RuntimeException {
    public SecondaryMallException() {
    }

    public SecondaryMallException(String message) {
        super(message);
    }

    /**
     * 丢出一个异常
     *
     * @param message
     */
    public static void fail(String message) {
        throw new SecondaryMallException(message);
    }

}
