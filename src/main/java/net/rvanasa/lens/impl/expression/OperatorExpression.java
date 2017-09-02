package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.LensOperator.OperatorPosition;
import net.rvanasa.lens.impl.reference.OperatorTypeReference;

public class OperatorExpression extends AbstractExpression
{
	private final String symbol;
	private final OperatorPosition type;
	
	private final OperatorTypeReference typeRef;
	
	private final LensExpression a, b;
	
	public OperatorExpression(String symbol, OperatorPosition type, LensExpression a)
	{
		this(symbol, type, a, EmptyExpression.INSTANCE);
	}
	
	public OperatorExpression(String symbol, LensExpression a, LensExpression b)
	{
		this(symbol, OperatorPosition.MIDFIX, a, b);
	}
	
	public OperatorExpression(String symbol, OperatorPosition type, LensExpression a, LensExpression b)
	{
		this.symbol = symbol;
		this.type = type;
		
		this.a = a;
		this.b = b;
		
		this.typeRef = new OperatorTypeReference(symbol, type, a.getExpType(), b.getExpType());
	}
	
	public String getSymbol()
	{
		return symbol;
	}
	
	public OperatorPosition getPosition()
	{
		return type;
	}
	
	public LensExpression getA()
	{
		return a;
	}
	
	public LensExpression getB()
	{
		return b;
	}
	
	@Override
	public OperatorTypeReference getExpType()
	{
		return typeRef;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		LensOperator op = stack.getContext().getOperator(getSymbol(), getPosition(), getA().getExpType().resolve(stack), getB().getExpType().resolve(stack));
		return op.operate(getA(), getB(), stack);
	}
	
	@Override
	public String toString()
	{
		return getPosition().getStringValue(getSymbol(), format(getA(), false), format(getB(), true));
	}
	
	private String format(LensExpression exp, boolean postfix)
	{
		if(exp instanceof OperatorExpression)
		{
			return "(" + exp + ")";
		}
		else
		{
			return exp.toString();
		}
	}
}
