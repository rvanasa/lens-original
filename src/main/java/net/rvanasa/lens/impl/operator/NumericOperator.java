package net.rvanasa.lens.impl.operator;

import java.util.function.DoubleBinaryOperator;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.impl.type.NumberType;
import net.rvanasa.lens.impl.type.OptionalType;
import net.rvanasa.lens.impl.value.NumberValue;

public class NumericOperator implements LensOperator
{
	private static final LensType OPT_NUM = new OptionalType(Lens.NUM);
	
	private final String symbol;
	private final boolean preserve;
	private final DoubleBinaryOperator handle;
	
	public NumericOperator(String symbol, boolean preserve, DoubleBinaryOperator handle)
	{
		this.symbol = symbol;
		this.preserve = preserve;
		this.handle = handle;
	}
	
	@Override
	public String getSymbol()
	{
		return symbol;
	}
	
	public boolean isPreserveGuaranteed()
	{
		return preserve;
	}
	
	public DoubleBinaryOperator getHandle()
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
		return isPreserveGuaranteed() ? getCommonType(a, b) : OPT_NUM;
	}
	
	public NumberType<?> getCommonType(LensType a, LensType b)
	{
		LensType type = Lens.getCommonType(a, b);
		return type instanceof NumberType ? (NumberType<?>)type : Lens.NUM;
	}
	
	@Override
	public boolean isValidParams(LensType a, LensType b)
	{
		return Lens.NUM.isAssignableFrom(a) && Lens.NUM.isAssignableFrom(b);
	}
	
	@Override
	public LensValue operate(LensValue a, LensValue b)
	{
		NumberType<?> type = getCommonType(a.getType(), b.getType());
		double result = getHandle().applyAsDouble(Lens.DOUBLE.getTyped(a).handle(), Lens.DOUBLE.getTyped(b).handle());
		
		NumberValue<?> value = type.getTyped(result);
		if(Double.isNaN(result))
		{
			return Lens.UNDEFINED;
		}
		else if(isPreserveGuaranteed())
		{
			return value;
		}
		else if(result % 1 == 0)
		{
			return Lens.LONG.getTyped(result);
		}
		else
		{
			return Lens.NUM.getTyped(result);
		}
	}
}
