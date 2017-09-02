package net.rvanasa.lens.impl.operator;

import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.impl.type.TupleType;

public class MidfixOperator extends AbstractOperator
{
	public MidfixOperator(String symbol, LensFunction function)
	{
		super(symbol, function);
	}
	
	@Override
	public OperatorPosition getPosition()
	{
		return OperatorPosition.MIDFIX;
	}
	
	@Override
	public boolean isValidParams(LensType a, LensType b)
	{
		return asFunction().isValidArg(TupleType.get(a, b));
	}
	
	@Override
	public LensValue operate(LensValue a, LensValue b)
	{
		return asFunction().invoke(new LensValue[] {a, b});
	}
}
