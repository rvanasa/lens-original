package net.rvanasa.lens.impl.operator;

import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensType;

public class PrefixOperator extends AbstractOperator
{
	public PrefixOperator(String symbol, LensFunction function)
	{
		super(symbol, function);
	}
	
	@Override
	public OperatorPosition getPosition()
	{
		return OperatorPosition.PREFIX;
	}
	
	@Override
	public boolean isValidParams(LensType a, LensType b)
	{
		return asFunction().isValidArg(a);
	}
	
	@Override
	public LensValue operate(LensValue a, LensValue b)
	{
		return asFunction().invoke(a);
	}
}
