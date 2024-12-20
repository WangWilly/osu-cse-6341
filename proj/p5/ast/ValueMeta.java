package ast;
import java.util.HashMap;

public final class ValueMeta {
    public enum ValueType {
        BOOL,
        INT,
        FLOAT,
        UNDEFINED,
        ABST_BOOL,
        ABST_INT,
        ABST_FLOAT,
    }

    public enum AbstValue {
        ILLEGAL,
        TRUE_BOOL,
        FALSE_BOOL,
        ANY_BOOL,
        POS_INT,
        NEG_INT,
        ZERO_INT,
        ANY_INT,
        POS_FLOAT,
        NEG_FLOAT,
        ZERO_FLOAT,
        ANY_FLOAT,
    }

    ////////////////////////////////////////////////////////////////////////////

    private String ident;
    private ValueType type;
    private Long intValue;
    private Double floatValue;
    private Boolean boolValue;

    private AbstValue abstValue;

    private ValueMeta(String ident, ValueType type, Long intValue, Double floatValue, Boolean boolValue, AbstValue abstValue) {
        this.ident = ident;
        this.type = type;
        this.intValue = intValue;
        this.floatValue = floatValue;
        this.boolValue = boolValue;

        this.abstValue = abstValue;
    }

    ////////////////////////////////////////////////////////////////////////////

    public static ValueMeta createNull(String ident, ValueType type) {
        return new ValueMeta(ident, type, null, null, null, null);
    }

    public static ValueMeta createZero(String ident, ValueType type) {
        // dummy value
        if (type == ValueType.INT) {
            return new ValueMeta(ident, type, Long.valueOf(0), null, null, null);
        }
        if (type == ValueType.FLOAT) {
            return new ValueMeta(ident, type, null, Double.valueOf(0.0), null, null);
        }
        if (type == ValueType.BOOL) {
            return new ValueMeta(ident, type, null, null, Boolean.FALSE, null);
        }
        if (type == ValueType.ABST_INT) {
            return new ValueMeta(ident, type, null, null, null, AbstValue.ZERO_INT);
        }
        if (type == ValueType.ABST_FLOAT) {
            return new ValueMeta(ident, type, null, null, null, AbstValue.ZERO_FLOAT);
        }

        throw new RuntimeException("ValueMeta: createZero() called on UNDEFINED type");
    }

    public static ValueMeta createInt(String ident, Long intValue) {
        ValueMeta value = new ValueMeta(ident, ValueType.INT, intValue, null, null, null);
        return value;
    }

    public static ValueMeta createFloat(String ident, Double floatValue) {
        ValueMeta value = new ValueMeta(ident, ValueType.FLOAT, null, floatValue, null, null);
        return value;
    }

    public static ValueMeta createBool(String ident, Boolean boolValue) {
        ValueMeta value = new ValueMeta(ident, ValueType.BOOL, null, null, boolValue, null);
        return value;
    }

    ////////////////////////////////////////////////////////////////////////////

    public static ValueMeta createAbstInt(String ident, AbstValue abstValue) {
        ValueMeta value = new ValueMeta(ident, ValueType.ABST_INT, null, null, null, abstValue);
        return value;
    }

    public static ValueMeta createAbstInt(String ident, Long intValue) {
        if (intValue > 0) {
            return createAbstInt(ident, AbstValue.POS_INT);
        }
        if (intValue < 0) {
            return createAbstInt(ident, AbstValue.NEG_INT);
        }
        return createAbstInt(ident, AbstValue.ZERO_INT);
    }

    public static ValueMeta createAbstFloat(String ident, AbstValue abstValue) {
        ValueMeta value = new ValueMeta(ident, ValueType.ABST_FLOAT, null, null, null, abstValue);
        return value;
    }

    public static ValueMeta createAbstFloat(String ident, Double floatValue) {
        if (floatValue > 0.0) {
            return createAbstFloat(ident, AbstValue.POS_FLOAT);
        }
        if (floatValue < 0.0) {
            return createAbstFloat(ident, AbstValue.NEG_FLOAT);
        }
        return createAbstFloat(ident, AbstValue.ZERO_FLOAT);
    }

    public static ValueMeta createAbstBool(String ident, AbstValue abstValue) {
        ValueMeta value = new ValueMeta(ident, ValueType.ABST_BOOL, null, null, null, abstValue);
        return value;
    }

    public static ValueMeta createAbstBool(String ident, Boolean boolValue) {
        if (boolValue) {
            return createAbstBool(ident, AbstValue.TRUE_BOOL);
        }
        return createAbstBool(ident, AbstValue.FALSE_BOOL);
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

    public AbstValue getAbstIntValue() {
        if (type != ValueType.ABST_INT) {
            throw new RuntimeException("ValueMeta: getAbstIntValue() called on non-ABST_INT type");
        }
        return abstValue;
    }

    public AbstValue getAbstFloatValue() {
        if (type != ValueType.ABST_FLOAT) {
            throw new RuntimeException("ValueMeta: getAbstFloatValue() called on non-ABST_FLOAT type");
        }
        return abstValue;
    }

    public AbstValue getAbstBoolValue() {
        if (type != ValueType.ABST_BOOL) {
            throw new RuntimeException("ValueMeta: getAbstBoolValue() called on non-ABST_BOOL type");
        }
        return abstValue;
    }

    public boolean hasValue() {
        return intValue != null || floatValue != null || boolValue != null || abstValue != null;
    }

    public boolean isIllegal() {
        return abstValue == AbstValue.ILLEGAL;
    }

    public boolean isTrue() {
        if (type == ValueType.BOOL) {
            return boolValue;
        }
        if (type == ValueType.ABST_BOOL) {
            return abstValue == AbstValue.TRUE_BOOL;
        }
        return false;
    }

    public boolean isFalse() {
        if (type == ValueType.BOOL) {
            return !boolValue;
        }
        if (type == ValueType.ABST_BOOL) {
            return abstValue == AbstValue.FALSE_BOOL;
        }
        return false;
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
        if (type == ValueType.ABST_INT) {
            return ident + " (abst_int): " + abstValue;
        }
        if (type == ValueType.ABST_FLOAT) {
            return ident + " (abst_float): " + abstValue;
        }
        if (type == ValueType.ABST_BOOL) {
            return ident + " (abst_bool): " + abstValue;
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
        if (type == ValueType.ABST_INT && abstValue != null) {
            switch (abstValue) {
                case POS_INT:
                    System.out.println("PosInt");
                    break;
                case NEG_INT:
                    System.out.println("NegInt");
                    break;
                case ZERO_INT:
                    System.out.println("ZeroInt");
                    break;
                case ANY_INT:
                    System.out.println("AnyInt");
                    break;
                default:
                    throw new RuntimeException("ValueMeta: print() called on invalid ABST_INT");
            }
            return;
        }
        if (type == ValueType.ABST_FLOAT && abstValue != null) {
            switch (abstValue) {
                case POS_FLOAT:
                    System.out.println("PosFloat");
                    break;
                case NEG_FLOAT:
                    System.out.println("NegFloat");
                    break;
                case ZERO_FLOAT:
                    System.out.println("ZeroFloat");
                    break;
                case ANY_FLOAT:
                    System.out.println("AnyFloat");
                    break;
                default:
                    throw new RuntimeException("ValueMeta: print() called on invalid ABST_FLOAT");
            }
            return;
        }
        if (type == ValueType.ABST_BOOL && abstValue != null) {
            switch (abstValue) {
                case TRUE_BOOL:
                    System.out.println("TrueBool");
                    break;
                case FALSE_BOOL:
                    System.out.println("FalseBool");
                    break;
                case ANY_BOOL:
                    System.out.println("AnyBool");
                    break;
                default:
                    throw new RuntimeException("ValueMeta: print() called on invalid ABST_BOOL");
            }
            return;
        }

        throw new RuntimeException("ValueMeta: " + toString() + " is not printable");
    }

    ////////////////////////////////////////////////////////////////////////////

    public ValueMeta copy() {
        return new ValueMeta(this.ident, this.type, this.intValue, this.floatValue, this.boolValue, this.abstValue);
    }

    public ValueMeta copyWithIdent(String ident) {
        return new ValueMeta(ident, this.type, this.intValue, this.floatValue, this.boolValue, this.abstValue);
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
        if (left.getType() == ValueType.ABST_INT) {
            return left.getAbstIntValue() == right.getAbstIntValue();
        }
        if (left.getType() == ValueType.ABST_FLOAT) {
            return left.getAbstFloatValue() == right.getAbstFloatValue();
        }
        if (left.getType() == ValueType.ABST_BOOL) {
            return left.getAbstBoolValue() == right.getAbstBoolValue();
        }

        throw new RuntimeException("ValueMeta: equals() called on UNDEFINED type");
    }

    public static HashMap<AbstValue, HashMap<AbstValue, AbstValue>> eqTable = new HashMap<AbstValue, HashMap<AbstValue, AbstValue>>() {{
        // ABST_INT
        put(AbstValue.POS_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.NEG_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.NEG_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ZERO_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.NEG_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        // ABST_FLOAT
        put(AbstValue.POS_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.NEG_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ZERO_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        // ABST_BOOL
        put(AbstValue.TRUE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.FALSE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.ANY_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
    }};

    public static ValueMeta vEquals(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: vEquals() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: vEquals() called on different types");
        }

        if (left.getType() == ValueType.BOOL ||
            left.getType() == ValueType.INT ||
            left.getType() == ValueType.FLOAT
        ) {
            return ValueMeta.createBool(null, equals(left, right));
        }

        if (left.getType() == ValueType.ABST_BOOL ||
            left.getType() == ValueType.ABST_INT ||
            left.getType() == ValueType.ABST_FLOAT
        ) {
            if (eqTable.containsKey(left.abstValue) && eqTable.get(left.abstValue).containsKey(right.abstValue)) {
                return ValueMeta.createAbstBool(null, eqTable.get(left.abstValue).get(right.abstValue));
            }
        }
        
        throw new RuntimeException("ValueMeta: vEquals() called on UNDEFINED type");
    }

    public static boolean notEquals(ValueMeta left, ValueMeta right) {
        return !equals(left, right);
    }

    public static HashMap<AbstValue, HashMap<AbstValue, AbstValue>> neTable = new HashMap<AbstValue, HashMap<AbstValue, AbstValue>>() {{
        // ABST_INT
        put(AbstValue.POS_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.NEG_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.NEG_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ZERO_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.NEG_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        // ABST_FLOAT
        put(AbstValue.POS_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.NEG_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ZERO_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        // ABST_BOOL
        put(AbstValue.TRUE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.FALSE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.ANY_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
    }};

    public static ValueMeta vNotEquals(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: vNotEquals() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: vNotEquals() called on different types");
        }

        if (left.getType() == ValueType.BOOL ||
            left.getType() == ValueType.INT ||
            left.getType() == ValueType.FLOAT
        ) {
            return ValueMeta.createBool(null, notEquals(left, right));
        }

        if (left.getType() == ValueType.ABST_BOOL ||
            left.getType() == ValueType.ABST_INT ||
            left.getType() == ValueType.ABST_FLOAT
        ) {
            if (neTable.containsKey(left.abstValue) && neTable.get(left.abstValue).containsKey(right.abstValue)) {
                return ValueMeta.createAbstBool(null, neTable.get(left.abstValue).get(right.abstValue));
            }
        }
        
        throw new RuntimeException("ValueMeta: vNotEquals() called on UNDEFINED type");
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

    private static HashMap<AbstValue, HashMap<AbstValue, AbstValue>> ltTable = new HashMap<AbstValue, HashMap<AbstValue, AbstValue>>() {{
        // ABST_INT
        put(AbstValue.POS_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.NEG_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.NEG_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ZERO_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.NEG_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        // ABST_FLOAT
        put(AbstValue.POS_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.NEG_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ZERO_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        // ABST_BOOL
        put(AbstValue.TRUE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.FALSE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.ANY_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
    }};

    public static ValueMeta vLessThan(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: vLessThan() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: vLessThan() called on different types");
        }

        if (left.getType() == ValueType.BOOL ||
            left.getType() == ValueType.INT ||
            left.getType() == ValueType.FLOAT
        ) {
            return ValueMeta.createBool(null, lessThan(left, right));
        }

        if (left.getType() == ValueType.ABST_BOOL ||
            left.getType() == ValueType.ABST_INT ||
            left.getType() == ValueType.ABST_FLOAT
        ) {
            if (ltTable.containsKey(left.abstValue) && ltTable.get(left.abstValue).containsKey(right.abstValue)) {
                return ValueMeta.createAbstBool(null, ltTable.get(left.abstValue).get(right.abstValue));
            }
        }
        
        throw new RuntimeException("ValueMeta: vLessThan() called on UNDEFINED type");
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

    private static HashMap<AbstValue, HashMap<AbstValue, AbstValue>> gtTable = new HashMap<AbstValue, HashMap<AbstValue, AbstValue>>() {{
        // ABST_INT
        put(AbstValue.POS_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.NEG_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.NEG_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ZERO_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.NEG_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        // ABST_FLOAT
        put(AbstValue.POS_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.NEG_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ZERO_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        // ABST_BOOL
        put(AbstValue.TRUE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.FALSE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.ANY_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
    }};

    public static ValueMeta vGreaterThan(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: vGreaterThan() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: vGreaterThan() called on different types");
        }

        if (left.getType() == ValueType.BOOL ||
            left.getType() == ValueType.INT ||
            left.getType() == ValueType.FLOAT
        ) {
            return ValueMeta.createBool(null, greaterThan(left, right));
        }

        if (left.getType() == ValueType.ABST_BOOL ||
            left.getType() == ValueType.ABST_INT ||
            left.getType() == ValueType.ABST_FLOAT
        ) {
            if (gtTable.containsKey(left.abstValue) && gtTable.get(left.abstValue).containsKey(right.abstValue)) {
                return ValueMeta.createAbstBool(null, gtTable.get(left.abstValue).get(right.abstValue));
            }
        }
        
        throw new RuntimeException("ValueMeta: vGreaterThan() called on UNDEFINED type");
    }

    public static boolean lessThanOrEqual(ValueMeta left, ValueMeta right) {
        return lessThan(left, right) || equals(left, right);
    }

    public static HashMap<AbstValue, HashMap<AbstValue, AbstValue>> leTable = new HashMap<AbstValue, HashMap<AbstValue, AbstValue>>() {{
        // ABST_INT
        put(AbstValue.POS_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.NEG_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.NEG_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ZERO_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.NEG_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        // ABST_FLOAT
        put(AbstValue.POS_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.NEG_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ZERO_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        // ABST_BOOL
        put(AbstValue.TRUE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.FALSE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.TRUE_BOOL);
        }});
        put(AbstValue.ANY_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
    }};

    public static ValueMeta vLessThanOrEqual(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: vLessThanOrEqual() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: vLessThanOrEqual() called on different types");
        }

        if (left.getType() == ValueType.BOOL ||
            left.getType() == ValueType.INT ||
            left.getType() == ValueType.FLOAT
        ) {
            return ValueMeta.createBool(null, lessThanOrEqual(left, right));
        }

        if (left.getType() == ValueType.ABST_BOOL ||
            left.getType() == ValueType.ABST_INT ||
            left.getType() == ValueType.ABST_FLOAT
        ) {
            if (leTable.containsKey(left.abstValue) && leTable.get(left.abstValue).containsKey(right.abstValue)) {
                return ValueMeta.createAbstBool(null, leTable.get(left.abstValue).get(right.abstValue));
            }
        }
        
        throw new RuntimeException("ValueMeta: vLessThanOrEqual() called on UNDEFINED type");
    }

    public static boolean greaterThanOrEqual(ValueMeta left, ValueMeta right) {
        return greaterThan(left, right) || equals(left, right);
    }

    public static HashMap<AbstValue, HashMap<AbstValue, AbstValue>> geTable = new HashMap<AbstValue, HashMap<AbstValue, AbstValue>>() {{
        // ABST_INT
        put(AbstValue.POS_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.NEG_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.NEG_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ZERO_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.FALSE_BOOL);
            put(AbstValue.NEG_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_INT, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_INT, AbstValue.ANY_BOOL);
        }});
        // ABST_FLOAT
        put(AbstValue.POS_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.NEG_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ZERO_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.FALSE_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ZERO_FLOAT, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_BOOL);
        }});
        // ABST_BOOL
        put(AbstValue.TRUE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.TRUE_BOOL);
        }});
        put(AbstValue.FALSE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.ANY_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
    }};

    public static ValueMeta vGreaterThanOrEqual(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: vGreaterThanOrEqual() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: vGreaterThanOrEqual() called on different types");
        }

        if (left.getType() == ValueType.BOOL ||
            left.getType() == ValueType.INT ||
            left.getType() == ValueType.FLOAT
        ) {
            return ValueMeta.createBool(null, greaterThanOrEqual(left, right));
        }

        if (left.getType() == ValueType.ABST_BOOL ||
            left.getType() == ValueType.ABST_INT ||
            left.getType() == ValueType.ABST_FLOAT
        ) {
            if (geTable.containsKey(left.abstValue) && geTable.get(left.abstValue).containsKey(right.abstValue)) {
                return ValueMeta.createAbstBool(null, geTable.get(left.abstValue).get(right.abstValue));
            }
        }
        
        throw new RuntimeException("ValueMeta: vGreaterThanOrEqual() called on UNDEFINED type");
    }

    ////////////////////////////////////////////////////////////////////////////
    
    private static HashMap<AbstValue, AbstValue> notTable = new HashMap<AbstValue, AbstValue>() {{
        put(AbstValue.TRUE_BOOL, AbstValue.FALSE_BOOL);
        put(AbstValue.FALSE_BOOL, AbstValue.TRUE_BOOL);
        put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
    }};

    public static ValueMeta not(ValueMeta value) {
        if (value == null) {
            throw new RuntimeException("ValueMeta: not() called on null");
        }

        if (value.getType() == ValueType.BOOL) {
            return ValueMeta.createBool(null, !value.getBoolValue());
        }

        if (value.getType() == ValueType.ABST_BOOL) {
            if (notTable.containsKey(value.abstValue)) {
                return ValueMeta.createAbstBool(null, notTable.get(value.abstValue));
            }
        }
        
        throw new RuntimeException("ValueMeta: not() called on UNDEFINED type");
    }

    /**
    private static HashMap<AbstValue, HashMap<AbstValue, AbstValue>> andTable = new HashMap<AbstValue, HashMap<AbstValue, AbstValue>>() {{
        put(AbstValue.TRUE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.FALSE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.FALSE_BOOL);
        }});
        put(AbstValue.ANY_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.ANY_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
    }};

    public static ValueMeta and(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: and() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: and() called on different types");
        }

        if (left.getType() == ValueType.BOOL) {
            return ValueMeta.createBool(null, left.getBoolValue() && right.getBoolValue());
        }

        if (left.getType() == ValueType.ABST_BOOL) {
            if (andTable.containsKey(left.abstValue) && andTable.get(left.abstValue).containsKey(right.abstValue)) {
                return ValueMeta.createAbstBool(null, andTable.get(left.abstValue).get(right.abstValue));
            }
        }
        
        throw new RuntimeException("ValueMeta: and() called on UNDEFINED type");
    }

    private static HashMap<AbstValue, HashMap<AbstValue, AbstValue>> orTable = new HashMap<AbstValue, HashMap<AbstValue, AbstValue>>() {{
        put(AbstValue.TRUE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.TRUE_BOOL);
        }});
        put(AbstValue.FALSE_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.FALSE_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
        put(AbstValue.ANY_BOOL, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.TRUE_BOOL, AbstValue.TRUE_BOOL);
            put(AbstValue.FALSE_BOOL, AbstValue.ANY_BOOL);
            put(AbstValue.ANY_BOOL, AbstValue.ANY_BOOL);
        }});
    }};

    public static ValueMeta or(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: or() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: or() called on different types");
        }

        if (left.getType() == ValueType.BOOL) {
            return ValueMeta.createBool(null, left.getBoolValue() || right.getBoolValue());
        }

        if (left.getType() == ValueType.ABST_BOOL) {
            if (orTable.containsKey(left.abstValue) && orTable.get(left.abstValue).containsKey(right.abstValue)) {
                return ValueMeta.createAbstBool(null, orTable.get(left.abstValue).get(right.abstValue));
            }
        }
        
        throw new RuntimeException("ValueMeta: or() called on UNDEFINED type");
    }
    */

    ////////////////////////////////////////////////////////////////////////////

    private static HashMap<AbstValue, HashMap<AbstValue, AbstValue>> addTable = new HashMap<AbstValue, HashMap<AbstValue, AbstValue>>() {{
        // ABST_INT
        put(AbstValue.POS_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.POS_INT);
            put(AbstValue.NEG_INT, AbstValue.ANY_INT);
            put(AbstValue.ZERO_INT, AbstValue.POS_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        put(AbstValue.NEG_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_INT);
            put(AbstValue.NEG_INT, AbstValue.NEG_INT);
            put(AbstValue.ZERO_INT, AbstValue.NEG_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        put(AbstValue.ZERO_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.POS_INT);
            put(AbstValue.NEG_INT, AbstValue.NEG_INT);
            put(AbstValue.ZERO_INT, AbstValue.ZERO_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        put(AbstValue.ANY_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_INT);
            put(AbstValue.NEG_INT, AbstValue.ANY_INT);
            put(AbstValue.ZERO_INT, AbstValue.ANY_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        // ABST_FLOAT
        put(AbstValue.POS_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.POS_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.POS_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
        put(AbstValue.NEG_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.NEG_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.NEG_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
        put(AbstValue.ZERO_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.POS_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.NEG_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ZERO_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
        put(AbstValue.ANY_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
    }};

    public static ValueMeta add(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: add() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: add() called on different types");
        }

        if (left.getType() == ValueType.INT) {
            return ValueMeta.createInt(null, left.getIntValue() + right.getIntValue());
        }
        if (left.getType() == ValueType.FLOAT) {
            return ValueMeta.createFloat(null, left.getFloatValue() + right.getFloatValue());
        }
        if (left.getType() == ValueType.ABST_INT && addTable.containsKey(left.abstValue) && addTable.get(left.abstValue).containsKey(right.abstValue)) {
            return ValueMeta.createAbstInt(null, addTable.get(left.abstValue).get(right.abstValue));
        }
        if (left.getType() == ValueType.ABST_FLOAT && addTable.containsKey(left.abstValue) && addTable.get(left.abstValue).containsKey(right.abstValue)) {
            return ValueMeta.createAbstFloat(null, addTable.get(left.abstValue).get(right.abstValue));
        }

        throw new RuntimeException("ValueMeta: add() called on UNDEFINED type");
    }

    ////////////////////////////////////////////////////////////////////////////

    private static HashMap<AbstValue, HashMap<AbstValue, AbstValue>> subTable = new HashMap<AbstValue, HashMap<AbstValue, AbstValue>>() {{
        // ABST_INT
        put(AbstValue.POS_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_INT);
            put(AbstValue.NEG_INT, AbstValue.POS_INT);
            put(AbstValue.ZERO_INT, AbstValue.POS_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        put(AbstValue.NEG_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.NEG_INT);
            put(AbstValue.NEG_INT, AbstValue.ANY_INT);
            put(AbstValue.ZERO_INT, AbstValue.NEG_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        put(AbstValue.ZERO_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.NEG_INT);
            put(AbstValue.NEG_INT, AbstValue.POS_INT);
            put(AbstValue.ZERO_INT, AbstValue.ZERO_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        put(AbstValue.ANY_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_INT);
            put(AbstValue.NEG_INT, AbstValue.ANY_INT);
            put(AbstValue.ZERO_INT, AbstValue.ANY_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        // ABST_FLOAT
        put(AbstValue.POS_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.POS_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.POS_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
        put(AbstValue.NEG_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.NEG_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.NEG_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
        put(AbstValue.ZERO_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.NEG_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.POS_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ZERO_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
        put(AbstValue.ANY_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
    }};

    public static ValueMeta sub(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: sub() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: sub() called on different types");
        }

        if (left.getType() == ValueType.INT) {
            return ValueMeta.createInt(null, left.getIntValue() - right.getIntValue());
        }
        if (left.getType() == ValueType.FLOAT) {
            return ValueMeta.createFloat(null, left.getFloatValue() - right.getFloatValue());
        }
        if (left.getType() == ValueType.ABST_INT && subTable.containsKey(left.abstValue) && subTable.get(left.abstValue).containsKey(right.abstValue)) {
            return ValueMeta.createAbstInt(null, subTable.get(left.abstValue).get(right.abstValue));
        }
        if (left.getType() == ValueType.ABST_FLOAT && subTable.containsKey(left.abstValue) && subTable.get(left.abstValue).containsKey(right.abstValue)) {
            return ValueMeta.createAbstFloat(null, subTable.get(left.abstValue).get(right.abstValue));
        }

        throw new RuntimeException("ValueMeta: sub() called on UNDEFINED type");
    }

    ////////////////////////////////////////////////////////////////////////////

    private static HashMap<AbstValue, HashMap<AbstValue, AbstValue>> mulTable = new HashMap<AbstValue, HashMap<AbstValue, AbstValue>>() {{
        // ABST_INT
        put(AbstValue.POS_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.POS_INT);
            put(AbstValue.NEG_INT, AbstValue.NEG_INT);
            put(AbstValue.ZERO_INT, AbstValue.ZERO_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        put(AbstValue.NEG_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.NEG_INT);
            put(AbstValue.NEG_INT, AbstValue.POS_INT);
            put(AbstValue.ZERO_INT, AbstValue.ZERO_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        put(AbstValue.ZERO_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ZERO_INT);
            put(AbstValue.NEG_INT, AbstValue.ZERO_INT);
            put(AbstValue.ZERO_INT, AbstValue.ZERO_INT);
            put(AbstValue.ANY_INT, AbstValue.ZERO_INT);
        }});
        put(AbstValue.ANY_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_INT);
            put(AbstValue.NEG_INT, AbstValue.ANY_INT);
            put(AbstValue.ZERO_INT, AbstValue.ZERO_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        // ABST_FLOAT
        put(AbstValue.POS_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.POS_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.NEG_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ZERO_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
        put(AbstValue.NEG_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.NEG_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.POS_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ZERO_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
        put(AbstValue.ZERO_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ZERO_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.ZERO_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ZERO_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ZERO_FLOAT);
        }});
        put(AbstValue.ANY_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ZERO_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
    }};

    public static ValueMeta mul(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: mul() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: mul() called on different types");
        }

        if (left.getType() == ValueType.INT) {
            return ValueMeta.createInt(null, left.getIntValue() * right.getIntValue());
        }
        if (left.getType() == ValueType.FLOAT) {
            return ValueMeta.createFloat(null, left.getFloatValue() * right.getFloatValue());
        }
        if (left.getType() == ValueType.ABST_INT && mulTable.containsKey(left.abstValue) && mulTable.get(left.abstValue).containsKey(right.abstValue)) {
            return ValueMeta.createAbstInt(null, mulTable.get(left.abstValue).get(right.abstValue));
        }
        if (left.getType() == ValueType.ABST_FLOAT && mulTable.containsKey(left.abstValue) && mulTable.get(left.abstValue).containsKey(right.abstValue)) {
            return ValueMeta.createAbstFloat(null, mulTable.get(left.abstValue).get(right.abstValue));
        }

        throw new RuntimeException("ValueMeta: mul() called on UNDEFINED type");
    }

    ////////////////////////////////////////////////////////////////////////////

    private static HashMap<AbstValue, HashMap<AbstValue, AbstValue>> divTable = new HashMap<AbstValue, HashMap<AbstValue, AbstValue>>() {{
        // ABST_INT
        put(AbstValue.POS_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_INT);
            put(AbstValue.NEG_INT, AbstValue.ANY_INT);
            put(AbstValue.ZERO_INT, AbstValue.ILLEGAL);
            put(AbstValue.ANY_INT, AbstValue.ILLEGAL);
        }});
        put(AbstValue.NEG_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_INT);
            put(AbstValue.NEG_INT, AbstValue.ANY_INT);
            put(AbstValue.ZERO_INT, AbstValue.ILLEGAL);
            put(AbstValue.ANY_INT, AbstValue.ILLEGAL);
        }});
        put(AbstValue.ZERO_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ZERO_INT);
            put(AbstValue.NEG_INT, AbstValue.ZERO_INT);
            put(AbstValue.ZERO_INT, AbstValue.ILLEGAL);
            put(AbstValue.ANY_INT, AbstValue.ILLEGAL);
        }});
        put(AbstValue.ANY_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_INT);
            put(AbstValue.NEG_INT, AbstValue.ANY_INT);
            put(AbstValue.ZERO_INT, AbstValue.ILLEGAL);
            put(AbstValue.ANY_INT, AbstValue.ILLEGAL);
        }});
        // ABST_FLOAT
        put(AbstValue.POS_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ILLEGAL);
            put(AbstValue.ANY_FLOAT, AbstValue.ILLEGAL);
        }});
        put(AbstValue.NEG_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ILLEGAL);
            put(AbstValue.ANY_FLOAT, AbstValue.ILLEGAL);
        }});
        put(AbstValue.ZERO_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ZERO_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.ZERO_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ILLEGAL);
            put(AbstValue.ANY_FLOAT, AbstValue.ILLEGAL);
        }});
        put(AbstValue.ANY_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ILLEGAL);
            put(AbstValue.ANY_FLOAT, AbstValue.ILLEGAL);
        }});
    }};

    public static ValueMeta div(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: div() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: div() called on different types");
        }

        if (left.getType() == ValueType.INT) {
            if (right.getIntValue() == 0) {
                ValueMeta res = ValueMeta.createNull(null, ValueType.INT);
                res.abstValue = AbstValue.ILLEGAL;
                return res;
            }
            return ValueMeta.createInt(null, left.getIntValue() / right.getIntValue());
        }
        if (left.getType() == ValueType.FLOAT) {
            if (right.getFloatValue() == 0.0) {
                ValueMeta res = ValueMeta.createNull(null, ValueType.FLOAT);
                res.abstValue = AbstValue.ILLEGAL;
                return res;
            }
            return ValueMeta.createFloat(null, left.getFloatValue() / right.getFloatValue());
        }
        if (left.getType() == ValueType.ABST_INT && divTable.containsKey(left.abstValue) && divTable.get(left.abstValue).containsKey(right.abstValue)) {
            return ValueMeta.createAbstInt(null, divTable.get(left.abstValue).get(right.abstValue));
        }
        if (left.getType() == ValueType.ABST_FLOAT && divTable.containsKey(left.abstValue) && divTable.get(left.abstValue).containsKey(right.abstValue)) {
            return ValueMeta.createAbstFloat(null, divTable.get(left.abstValue).get(right.abstValue));
        }

        throw new RuntimeException("ValueMeta: div() called on UNDEFINED type");
    }

    ////////////////////////////////////////////////////////////////////////////

    private static HashMap<AbstValue, AbstValue> negTable = new HashMap<AbstValue, AbstValue>() {{
        put(AbstValue.POS_INT, AbstValue.NEG_INT);
        put(AbstValue.NEG_INT, AbstValue.POS_INT);
        put(AbstValue.ZERO_INT, AbstValue.ZERO_INT);
        put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        put(AbstValue.POS_FLOAT, AbstValue.NEG_FLOAT);
        put(AbstValue.NEG_FLOAT, AbstValue.POS_FLOAT);
        put(AbstValue.ZERO_FLOAT, AbstValue.ZERO_FLOAT);
        put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
    }};

    public static ValueMeta neg(ValueMeta value) {
        if (value == null) {
            throw new RuntimeException("ValueMeta: neg() called on null");
        }

        if (value.getType() == ValueType.INT) {
            return ValueMeta.createInt(null, -value.getIntValue());
        }
        if (value.getType() == ValueType.FLOAT) {
            return ValueMeta.createFloat(null, -value.getFloatValue());
        }
        if (value.getType() == ValueType.ABST_INT && negTable.containsKey(value.abstValue)) {
            return ValueMeta.createAbstInt(null, negTable.get(value.abstValue));
        }
        if (value.getType() == ValueType.ABST_FLOAT && negTable.containsKey(value.abstValue)) {
            return ValueMeta.createAbstFloat(null, negTable.get(value.abstValue));
        }

        throw new RuntimeException("ValueMeta: neg() called on UNDEFINED type");
    }

    ////////////////////////////////////////////////////////////////////////////

    private static HashMap<AbstValue, HashMap<AbstValue, AbstValue>> mergeTable = new HashMap<AbstValue, HashMap<AbstValue, AbstValue>>() {{
        // ABST_INT
        put(AbstValue.POS_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.POS_INT);
            put(AbstValue.NEG_INT, AbstValue.ANY_INT);
            put(AbstValue.ZERO_INT, AbstValue.ANY_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        put(AbstValue.NEG_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_INT);
            put(AbstValue.NEG_INT, AbstValue.NEG_INT);
            put(AbstValue.ZERO_INT, AbstValue.ANY_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        put(AbstValue.ZERO_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_INT);
            put(AbstValue.NEG_INT, AbstValue.ANY_INT);
            put(AbstValue.ZERO_INT, AbstValue.ZERO_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        put(AbstValue.ANY_INT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_INT, AbstValue.ANY_INT);
            put(AbstValue.NEG_INT, AbstValue.ANY_INT);
            put(AbstValue.ZERO_INT, AbstValue.ANY_INT);
            put(AbstValue.ANY_INT, AbstValue.ANY_INT);
        }});
        // ABST_FLOAT
        put(AbstValue.POS_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.POS_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
        put(AbstValue.NEG_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.NEG_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
        put(AbstValue.ZERO_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ZERO_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
        put(AbstValue.ANY_FLOAT, new HashMap<AbstValue, AbstValue>() {{
            put(AbstValue.POS_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.NEG_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ZERO_FLOAT, AbstValue.ANY_FLOAT);
            put(AbstValue.ANY_FLOAT, AbstValue.ANY_FLOAT);
        }});
    }};

    public static ValueMeta merge(ValueMeta left, ValueMeta right) {
        if (left == null || right == null) {
            throw new RuntimeException("ValueMeta: merge() called on null");
        }

        if (left.getType() != right.getType()) {
            throw new RuntimeException("ValueMeta: merge() called on different types");
        }

        if (left.getType() == ValueType.INT) {
            return ValueMeta.createInt(left.getIdent(), right.getIntValue());
        }
        if (left.getType() == ValueType.FLOAT) {
            return ValueMeta.createFloat(left.getIdent(), right.getFloatValue());
        }
        if (left.getType() == ValueType.ABST_INT && mergeTable.containsKey(left.abstValue) && mergeTable.get(left.abstValue).containsKey(right.abstValue)) {
            return ValueMeta.createAbstInt(left.getIdent(), mergeTable.get(left.abstValue).get(right.abstValue));
        }
        if (left.getType() == ValueType.ABST_FLOAT && mergeTable.containsKey(left.abstValue) && mergeTable.get(left.abstValue).containsKey(right.abstValue)) {
            return ValueMeta.createAbstFloat(left.getIdent(), mergeTable.get(left.abstValue).get(right.abstValue));
        }

        throw new RuntimeException("ValueMeta: merge() called on UNDEFINED type");
    }
}
