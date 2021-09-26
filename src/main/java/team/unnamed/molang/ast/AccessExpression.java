package team.unnamed.molang.ast;

import team.unnamed.molang.context.EvalContext;

/**
 * {@link Expression} implementation for
 * representing field accessing
 */
public class AccessExpression
        implements Expression {

    private final Expression object;
    // todo: maybe use a string?
    private final Expression property;

    public AccessExpression(Expression object, Expression property) {
        this.object = object;
        this.property = property;
    }

    @Override
    public Object eval(EvalContext context) {
        return object.evalProperty(context, property); // temporary
    }

    @Override
    public String toSource() {
        return object.toSource() + '.' + property.toSource();
    }

    @Override
    public String toString() {
        return "Access(" + object + ", " + property + ")";
    }

    @Override
    public int getNodeType() {
        return 0;
    }

}
