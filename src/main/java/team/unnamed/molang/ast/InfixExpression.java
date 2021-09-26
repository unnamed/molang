package team.unnamed.molang.ast;

/**
 * Represents an infix (binary) expression, composed
 * by two {@link Expression} called {@code left} and
 * {@code right}
 *
 * <p>The operator is specified by the node type</p>
 */
public class InfixExpression
        implements Expression {

    private final int operator;
    private final Expression left;
    private final Expression right;

    public InfixExpression(
            int operator,
            Expression left,
            Expression rightHand
    ) {
        this.operator = operator;
        this.left = left;
        this.right = rightHand;
    }

    @Override
    public int getNodeType() {
        return operator;
    }

    /**
     * Returns the operator of this binary expression,
     * this determines how to operate both {@code left}
     * and {@code right} expression values
     */
    public int getOperator() {
        return operator;
    }

    /**
     * Returns the left-side expression
     */
    public Expression getLeft() {
        return left;
    }

    /**
     * Returns the right-side expression
     */
    public Expression getRight() {
        return right;
    }

    @Override
    public String toSource() {
        return left.toSource()
                + " "
                + Tokens.getNameForOperator(operator)
                + " "
                + right.toSource();
    }

}
