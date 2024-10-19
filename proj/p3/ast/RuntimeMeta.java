package ast;

public class RuntimeMeta {
    private ValueMeta value;
    private AstErrorHandler.ErrorCode errorCode;

    ////////////////////////////////////////////////////////////////////////////

    private RuntimeMeta(ValueMeta value) {
        this.value = value;
        this.errorCode = AstErrorHandler.ErrorCode.SUCCESS;
    }

    private RuntimeMeta(AstErrorHandler.ErrorCode errorCode) {
        this.value = null;
        this.errorCode = errorCode;
    }

    ////////////////////////////////////////////////////////////////////////////

    public static RuntimeMeta createSuccess(ValueMeta value) {
        return new RuntimeMeta(value);
    }
    
    public static RuntimeMeta createError(AstErrorHandler.ErrorCode errorCode) {
        return new RuntimeMeta(errorCode);
    }

    ////////////////////////////////////////////////////////////////////////////

    public ValueMeta getValue() {
        return value;
    }

    public AstErrorHandler.ErrorCode getErrorCode() {
        return errorCode;
    }

    public boolean hasValue() {
        return value != null;
    }

    public boolean isSuccessful() {
        return errorCode == AstErrorHandler.ErrorCode.SUCCESS;
    }
}
