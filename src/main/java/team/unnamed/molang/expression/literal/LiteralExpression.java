package team.unnamed.molang.expression.literal;

import team.unnamed.molang.context.EvalContext;
import team.unnamed.molang.context.ParseContext;
import team.unnamed.molang.expression.Expression;
import team.unnamed.molang.parser.ParseException;
import team.unnamed.molang.parser.Tokens;

/**
 * Literal {@link Expression} implementation,
 * the parsed value is the same as the
 * evaluated
 */
public class LiteralExpression<T>
        implements Expression {

    private final Class<T> type;
    private final T value;

    public LiteralExpression(Class<T> type, T value) {
        this.type = type;
        this.value = value;
    }

    @Override
    public Class<T> getType() {
        return type;
    }

    @Override
    public Object eval(EvalContext context) {
        return value;
    }

    @Override
    public String toString() {
        if (value instanceof String) {
            return Tokens.QUOTE + value.toString() + Tokens.QUOTE;
        }
        return value.toString();
    }

    public static Expression parseFloat(
            ParseContext context,
            float divideByInitial
    ) throws ParseException {

        int current = context.getCurrent();
        boolean readingDecimalPart = false;
        float value = 0;
        float divideBy = divideByInitial;

        while (true) {
            if (Character.isDigit(current)) {
                value *= 10;
                value += Character.getNumericValue(current);
                if (readingDecimalPart) {
                    divideBy *= 10;
                }
                current = context.next();
            } else if (current == Tokens.DOT) {
                if (readingDecimalPart) {
                    throw new ParseException(
                            "Numbers can't have multiple floating points!",
                            context.getCursor()
                    );
                }
                readingDecimalPart = true;
                current = context.next();
            } else {
                // skip whitespace
                context.skipWhitespace();
                break;
            }
        }

        return new LiteralExpression<>(Float.class, value / divideBy);
    }

}