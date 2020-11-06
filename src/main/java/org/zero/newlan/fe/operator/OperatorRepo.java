package org.zero.newlan.fe.operator;

import java.util.HashMap;
import java.util.Map;

public class OperatorRepo {

    private static Map<String, BinaryOperator> binaryOperatorMap = new HashMap<>();
    private static Map<String, PrefixOperator> prefixOperatorMap = new HashMap<>();

    static {
        binaryOperatorMap.putIfAbsent("-", new SubtractionOperator());
        binaryOperatorMap.putIfAbsent("+", new AdditionOperator());
        binaryOperatorMap.putIfAbsent("/", new DivisionOperator());
        binaryOperatorMap.putIfAbsent("*", new MultiplicationOperator());
        binaryOperatorMap.putIfAbsent("=", new AssignmentOperator());

        prefixOperatorMap.putIfAbsent("-", new NegativeOperator());
    }

    public static BinaryOperator getBinary(String token) {
        return binaryOperatorMap.get(token);
    }
    public static PrefixOperator getPrefix(String token) {
        return prefixOperatorMap.get(token);
    }
}
