package net.rvanasa.lens.impl.type;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.ParamMatcher;
import net.rvanasa.lens.impl.value.FunctionValue;

public class FunctionType implements LensType
{
	public static final FunctionType FUNCTION = new FunctionType(Lens.ANY, Lens.ANY);
	public static final FunctionType PREDICATE = new FunctionType(Lens.ANY, Lens.BOOL);
	public static final FunctionType CONSUMER = new FunctionType(Lens.ANY, Lens.VOID);
	public static final FunctionType SUPPLIER = new FunctionType(Lens.VOID, Lens.ANY);
	public static final FunctionType RUNNABLE = new FunctionType(Lens.VOID, Lens.VOID);
	
	public static final FunctionType BI_FUNCTION = new FunctionType(TupleType.ANY_2, Lens.ANY);
	
	private final LensType paramType;
	private final LensType returnType;
	
	public FunctionType(LensType paramType, LensType returnType)
	{
		this.paramType = paramType;
		this.returnType = returnType;
	}
	
	public LensType getParamType()
	{
		return paramType;
	}
	
	public LensType getReturnType()
	{
		return returnType;
	}
	
	@Override
	public boolean isAssignableFrom(LensType type)
	{
		if(type instanceof FunctionType)
		{
			return true;
		}
		return false;
	}
	
	@Override
	public FunctionValue getTyped(LensValue value)
	{
		if(value instanceof FunctionValue)
		{
			
		}
		return Lens.cast(LensType.super.getTyped(value));
	}
	
	@Override
	public LensType getInvokeType(ParamMatcher arg)
	{
		return getReturnType();
	}
	
	@Override
	public String toString()
	{
		return getParamType() + " > " + getReturnType();
	}
}
