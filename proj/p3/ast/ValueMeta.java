package ast;

public final class ValueMeta {
    public enum ValueType {
        BOOL, INT, FLOAT, UNDEFINED
    }

    private String ident;
    private ValueType type;
    private Long intValue;
    private Double floatValue;
    private Boolean boolValue;

    /**
    public ValueMeta(String ident, ValueType type) {
        this.ident = ident;
        this.type = type;
    }

    public ValueMeta(String ident, ValueType type, Long intValue) {
        this.ident = ident;
        this.type = type;
        this.intValue = intValue;
    }

    public ValueMeta(String ident, ValueType type, Double floatValue) {
        this.ident = ident;
        this.type = type;
        this.floatValue = floatValue;
    }
    */

    private ValueMeta(String ident, ValueType type, Long intValue, Double floatValue, Boolean boolValue) {
        this.ident = ident;
        this.type = type;
        this.intValue = intValue;
        this.floatValue = floatValue;
        this.boolValue = boolValue;
    }

    public static ValueMeta createNull(String ident, ValueType type) {
        return new ValueMeta(ident, type, null, null, null);
    }

    public static ValueMeta createInt(String ident, Long intValue) {
        ValueMeta value = new ValueMeta(ident, ValueType.INT, intValue, null, null);
        return value;
    }

    public static ValueMeta createFloat(String ident, Double floatValue) {
        ValueMeta value = new ValueMeta(ident, ValueType.FLOAT, null, floatValue, null);
        return value;
    }

    public static ValueMeta createBool(String ident, Boolean boolValue) {
        ValueMeta value = new ValueMeta(ident, ValueType.BOOL, null, null, boolValue);
        return value;
    }

    ////////////////////////////////////////////////////////////////////////////

    public String getIdent() {
        return ident;
    }

    public ValueType getType() {
        return type;
    }

    ////////////////////////////////////////////////////////////////////////////

    public Long getIntValue() {
        if (type != ValueType.INT) {
            throw new RuntimeException("ValueMeta: getIntValue() called on non-INT type");
        }
        return intValue;
    }

    public Double getFloatValue() {
        if (type != ValueType.FLOAT) {
            throw new RuntimeException("ValueMeta: getFloatValue() called on non-FLOAT type");
        }
        return floatValue;
    }

    public Boolean getBoolValue() {
        if (type != ValueType.BOOL) {
            throw new RuntimeException("ValueMeta: getBoolValue() called on non-BOOL type");
        }
        return boolValue;
    }

    public boolean hasValue() {
        return intValue != null || floatValue != null || boolValue != null;
    }

    ////////////////////////////////////////////////////////////////////////////

    public ValueMeta copyWithIdent(String ident) {
        return new ValueMeta(ident, this.type, this.intValue, this.floatValue, this.boolValue);
    }
}
