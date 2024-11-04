package ast;
import java.util.function.Supplier;
import java.util.ArrayList;

public final class AstErrorHandler {
    public enum ErrorCode {
        SUCCESS,
        STATIC_CHECKING_ERROR,
        UNINITIALIZED_VAR_ERROR,
        DIV_BY_ZERO_ERROR,
        FAILED_STDIN_READ,
        DEAD_CODE_ERROR,
    }

    public static ErrorCode handle(ArrayList<Supplier<ErrorCode>> fns) {
        for (Supplier<ErrorCode> fn : fns) {
            ErrorCode code = fn.get();
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
