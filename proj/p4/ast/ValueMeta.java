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

    public static ValueMeta createZero(String ident, ValueType type) {
        // dummy value
        if (type == ValueType.INT) {
            return new ValueMeta(ident, type, Long.valueOf(0), null, null);
        }
        if (type == ValueType.FLOAT) {
            return new ValueMeta(ident, type, null, Double.valueOf(0.0), null);
        }
        if (type == ValueType.BOOL) {
            return new ValueMeta(ident, type, null, null, Boolean.FALSE);
        }

        throw new RuntimeException("ValueMeta: createZero() called on UNDEFINED type");
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

    public String toString() {
        if (type == ValueType.INT) {
            return ident + " (int): " + intValue;
        }
        if (type == ValueType.FLOAT) {
            return ident + " (float): " + floatValue;
        }
        if (type == ValueType.BOOL) {
            return ident + " (bool): " + boolValue;
        }
        return ident + " (undefined)";
    }

    public void print() {
        if (type == ValueType.INT && intValue != null) {
            System.out.println(intValue);
            return;
        }
        if (type == ValueType.FLOAT && floatValue != null) {
            System.out.println(floatValue);
            return;
        }
        if (type == ValueType.BOOL && boolValue != null) {
            System.out.println(boolValue);
            return;
        }

        throw new RuntimeException("ValueMeta: " + toString() + " is not printable");
    }

    ////////////////////////////////////////////////////////////////////////////

    public ValueMeta copyWithIdent(String ident) {
        return new ValueMeta(ident, this.type, this.intValue, this.floatValue, this.boolValue);
    }

    ////////////////////////////////////////////////////////////////////////////

    public static boolean equals(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: equals() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: equals() called on different types");
        }

        if (left.getType() == ValueType.INT) {
            return left.getIntValue() == right.getIntValue();
        }
        if (left.getType() == ValueType.FLOAT) {
            return left.getFloatValue() == right.getFloatValue();
        }
        if (left.getType() == ValueType.BOOL) {
            return left.getBoolValue() == right.getBoolValue();
        }

        throw new RuntimeException("ValueMeta: equals() called on UNDEFINED type");
    }

    public static boolean notEquals(ValueMeta left, ValueMeta right) {
        return !equals(left, right);
    }

    public static boolean lessThan(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: lessThan() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: lessThan() called on different types");
        }

        if (left.getType() == ValueType.INT) {
            return left.getIntValue() < right.getIntValue();
        }
        if (left.getType() == ValueType.FLOAT) {
            return left.getFloatValue() < right.getFloatValue();
        }

        throw new RuntimeException("ValueMeta: lessThan() called on non-INT/FLOAT type");
    }

    public static boolean greaterThan(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: greaterThan() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: greaterThan() called on different types");
        }

        if (left.getType() == ValueType.INT) {
            return left.getIntValue() > right.getIntValue();
        }
        if (left.getType() == ValueType.FLOAT) {
            return left.getFloatValue() > right.getFloatValue();
        }

        throw new RuntimeException("ValueMeta: greaterThan() called on non-INT/FLOAT type");
    }

    public static boolean lessThanOrEqual(ValueMeta left, ValueMeta right) {
        return lessThan(left, right) || equals(left, right);
    }

    public static boolean greaterThanOrEqual(ValueMeta left, ValueMeta right) {
        return greaterThan(left, right) || equals(left, right);
    }
}
