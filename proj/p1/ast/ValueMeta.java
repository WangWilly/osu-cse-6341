package ast;

public final class ValueMeta {
    public enum ValueType {
        INT, FLOAT
    }

    private String ident;
    private ValueType type;
    private Integer intValue;
    private Float floatValue;

    public ValueMeta(String ident, ValueType type) {
        this.ident = ident;
        this.type = type;
    }

    public ValueMeta(String ident, ValueType type, Integer intValue) {
        this.ident = ident;
        this.type = type;
        this.intValue = intValue;
    }

    public ValueMeta(String ident, ValueType type, Float floatValue) {
        this.ident = ident;
        this.type = type;
        this.floatValue = floatValue;
    }

    public String getIdent() {
        return ident;
    }

    public ValueType getType() {
        return type;
    }

    public Integer getIntValue() {
        return intValue;
    }

    public Float getFloatValue() {
        return floatValue;
    }
}
