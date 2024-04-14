package tech.flycat.shortlink.exception;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/5
 */
public class NotFoundException extends BaseException{

    public NotFoundException() {
        super();
    }

    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotFoundException(Throwable cause) {
        super(cause);
    }

    protected NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
