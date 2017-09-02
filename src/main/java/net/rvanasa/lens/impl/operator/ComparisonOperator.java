package net.rvanasa.lens.impl.operator;

import java.util.function.BiFunction;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.impl.value.BooleanValue;
import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.LensType;

public class ComparisonOperator implements LensOperator
{
	private final String symbol;
	private final BiFunction<Double, Double, Boolean> handle;
	
	public ComparisonOperator(String symbol, BiFunction<Double, Double, Boolean> handle)
	{
		this.symbol = symbol;
		this.handle = handle;
	}
	
	@Override
	public String getSymbol()
	{
		return symbol;
	}
	
	public BiFunction<Double, Double, Boolean> getHandle()
	{
		return handle;
	}
	
	@Override
	public OperatorPosition getPosition()
	{
		return OperatorPosition.MIDFIX;
	}
	
	@Override
	public LensType getReturnType(LensType a, LensType b)
	{
		return Lens.BOOL;
	}
	
	@Override
	public boolean isValidParams(LensType a, LensType b)
	{
		return Lens.NUM.isAssignableFrom(a) && Lens.NUM.isAssignableFrom(b);
	}
	
	@Override
	public LensValue operate(LensValue a, LensValue b)
	{
		boolean result = getHandle().apply(Lens.DOUBLE.getTyped(a).handle(), Lens.DOUBLE.getTyped(b).handle());
		return BooleanValue.get(result);
	}
}
