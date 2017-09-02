package net.rvanasa.lens.impl.operator;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.exception.LensException;

public class ExpressionOperator implements LensOperator
{
	private final String symbol;
	private final OperatorPosition position;
	private final LensType returnType;
	private final ExpressionOperatorHandle handle;
	
	public ExpressionOperator(String symbol, OperatorPosition pos, LensType returnType, ExpressionOperatorHandle handle)
	{
		this.symbol = symbol;
		this.position = pos;
		this.returnType = returnType;
		this.handle = handle;
	}
	
	@Override
	public String getSymbol()
	{
		return symbol;
	}
	
	@Override
	public OperatorPosition getPosition()
	{
		return position;
	}
	
	public LensType getReturnType()
	{
		return returnType;
	}
	
	@Override
	public LensType getReturnType(LensType a, LensType b)
	{
		return getReturnType();
	}
	
	@Override
	public boolean isValidParams(LensType a, LensType b)
	{
		return true;
	}
	
	@Override
	public LensValue operate(LensExpression a, LensExpression b, InvokeStack stack)
	{
		return handle.operate(a, b, stack);
	}
	
	@Override
	public LensValue operate(LensValue a, LensValue b)
	{
		throw new LensException("Operator '" + getSymbol() + "' must be invoked as an expression");
	}
	
	public interface ExpressionOperatorHandle
	{
		public LensValue operate(LensExpression a, LensExpression b, InvokeStack stack);
	}
}
