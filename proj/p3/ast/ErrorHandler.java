import java.util.function.Function;
import java.util.Arrays;

public final class ErrorHandler {
    public enum ErrorCode {
        SUCCESS,
        STATIC_CHECKING_ERROR,
        UNINITIALIZED_VAR_ERROR,
        DIV_BY_ZERO_ERROR,
        FAILED_STDIN_READ
    }

    public static ErrorCode handle(Function<Void, ErrorCode>[] fns) {
        for (Function<Void, ErrorCode> fn : fns) {
            ErrorCode code = fn.apply(null);
            if (code != ErrorCode.SUCCESS) {
                return code;
            }
        }

        return ErrorCode.SUCCESS;
    }

    public static boolean isSuccessful(ErrorCode code) {
        return code == ErrorCode.SUCCESS;
    }
}
