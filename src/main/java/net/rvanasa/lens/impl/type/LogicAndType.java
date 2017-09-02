package net.rvanasa.lens.impl.type;

import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.ParamMatcher;

public class LogicAndType implements LensType
{
	private final LensType a, b;
	
	public LogicAndType(LensType a, LensType b)
	{
		this.a = a;
		this.b = b;
	}
	
	public LensType getA()
	{
		return a;
	}
	
	public LensType getB()
	{
		return b;
	}
	
	@Override
	public boolean isAssignableFrom(LensType type)
	{
		if(type instanceof LogicAndType)
		{
			LogicAndType logic = (LogicAndType)type;
			return (getA().isAssignableFrom(logic.getA()) && getB().isAssignableFrom(logic.getB()))
					|| (getB().isAssignableFrom(logic.getA()) && getA().isAssignableFrom(logic.getB()));
		}
		return getA().isAssignableFrom(type) && getB().isAssignableFrom(type);
	}
	
	@Override
	public boolean isEqualComponent(LensType type)
	{
		return getA().isEqualComponent(type) && getB().isEqualComponent(type) || LensType.super.isEqualComponent(type);
	}
	
	@Override
	public boolean isInstance(LensValue value)
	{
		return getA().isInstance(value) && getB().isInstance(value);
	}
	
	@Override
	public LensType getInvokeType(ParamMatcher arg)
	{
		return new LogicAndType(getA().getInvokeType(arg), getB().getInvokeType(arg));
	}
	
	@Override
	public LensType getIndexerType()
	{
		return new LogicAndType(getA().getIndexerType(), getB().getIndexerType());
	}
	
	@Override
	public String toString()
	{
		return "(" + getA() + " & " + getB() + ")";
	}
}
