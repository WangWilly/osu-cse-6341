package ast;

public final class ValueMeta {
    public enum ValueType {
        BOOL, INT, FLOAT, UNDEFINED
    }

    private String ident;
    private ValueType type;
    private Long intValue;
    private Double floatValue;

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

    private ValueMeta(String ident, ValueType type, Long intValue, Double floatValue) {
        this.ident = ident;
        this.type = type;
        this.intValue = intValue;
        this.floatValue = floatValue;
    }

    public String getIdent() {
        return ident;
    }

    public ValueType getType() {
        return type;
    }

    public Long getIntValue() {
        return intValue;
    }

    public Double getFloatValue() {
        return floatValue;
    }

    public boolean hasValue() {
        return intValue != null || floatValue != null;
    }

    ////////////////////////////////////////////////////////////////////////////

    public ValueMeta copyWithIdent(String ident) {
        return new ValueMeta(ident, this.type, this.intValue, this.floatValue);
    }
}
