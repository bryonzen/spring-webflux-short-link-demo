package tech.flycat.shortlink.admin.utils;

/**
 * @author <a href="mailto:zengbin@hltn.com">zengbin</a>
 * @since 2024/4/6
 */
public class NumberScaleUtil {

    private static final String BASE_64_CHARSET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_-";

    public static String convertTo64(long decimalNum) {
        StringBuilder result = new StringBuilder();
        while (decimalNum > 0) {
            long remainder = decimalNum % 64;
            result.insert(0, BASE_64_CHARSET.charAt((int)remainder));
            decimalNum /= 64;
        }
        return result.toString();
    }
}
