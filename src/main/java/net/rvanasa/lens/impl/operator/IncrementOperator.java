package net.rvanasa.lens.impl.operator;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.expression.LiteralExpression;

public class IncrementOperator implements LensOperator
{
	private final LensOperator operator;
	private final String symbol;
	
	public IncrementOperator(LensOperator operator)
	{
		this(operator, operator.getSymbol() + "=");
	}
	
	public IncrementOperator(LensOperator operator, String symbol)
	{
		if(operator.getPosition() != OperatorPosition.MIDFIX)
		{
			throw new LensException("Cannot increment non-midfix operator: " + operator);
		}
		
		this.operator = operator;
		this.symbol = symbol;
	}
	
	public LensOperator getOperator()
	{
		return operator;
	}
	
	@Override
	public String getSymbol()
	{
		return symbol;
	}
	
	@Override
	public OperatorPosition getPosition()
	{
		return OperatorPosition.MIDFIX;
	}
	
	@Override
	public LensType getReturnType(LensType a, LensType b)
	{
		return getOperator().getReturnType(a, b);
	}
	
	@Override
	public boolean isValidParams(LensType a, LensType b)
	{
		return getOperator().isValidParams(a, b);
	}
	
	@Override
	public LensValue operate(LensExpression a, LensExpression b, InvokeStack stack)
	{
		LensValue value = getOperator().operate(new LiteralExpression(a.eval(stack)), b, stack);
		a.assign(value, stack);
		
		return value;
	}
	
	@Override
	public LensValue operate(LensValue a, LensValue b)
	{
		throw new LensException("Incrementor '" + getSymbol() + "' cannot be invoked without a context");
	}
}
