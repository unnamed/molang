package team.unnamed.molang.ast;

import team.unnamed.molang.context.EvalContext;

/**
 * The null coalescing expression implementation,
 * if the result of evaluating the 'leftHand' expression
 * is considered invalid, then it returns the 'rightHand'
 * result.
 *
 * See https://bedrock.dev/docs/1.17.0.0/1.17.30.4/
 * Molang#%3F%3F%20Null%20Coalescing%20Operator
 */
public class NullCoalescingExpression
        extends InfixExpression {

    public NullCoalescingExpression(
            Expression leftHand,
            Expression rightHand
    ) {
        super(leftHand, rightHand);
    }

    @Override
    public Object eval(EvalContext context) {
        Object value = leftHand.eval(context);
        // TODO: I don't know how to implement this yet following the specification
        return value;
    }

    @Override
    public String toSource() {
        return leftHand.toSource() + " ?? "
                + rightHand.toSource();
    }

    @Override
    public String toString() {
        return "NullCoalescing(" + leftHand + ", " + rightHand + ")";
    }

}
