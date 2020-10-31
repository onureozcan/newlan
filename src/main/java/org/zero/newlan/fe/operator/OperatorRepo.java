package org.zero.newlan.fe.operator;

import java.util.HashMap;
import java.util.Map;

public class OperatorRepo {

    private static Map<String, Operator> operatorMap = new HashMap<>();

    static {
        operatorMap.putIfAbsent("-", new SubtractionOperator());
        operatorMap.putIfAbsent("+", new AdditionOperator());
    }

    public static Operator get(String token) {
        return operatorMap.get(token);
    }
}
