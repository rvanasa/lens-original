package net.rvanasa.lens.impl.operator;

import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.impl.type.TupleType;

public abstract class AbstractOperator implements LensOperator
{
	private final String symbol;
	private final LensFunction function;
	
	public AbstractOperator(String symbol, LensFunction function)
	{
		this.symbol = symbol;
		this.function = function;
	}
	
	@Override
	public String getSymbol()
	{
		return symbol;
	}
	
	@Override
	public LensFunction asFunction()
	{
		return function;
	}
	
	@Override
	public LensType getReturnType(LensType a, LensType b)
	{
		return asFunction().getReturnType(TupleType.get(a, b));
	}
	
	@Override
	public String toString()
	{
		return getSymbol() + asFunction();
	}
}
