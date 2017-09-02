package net.rvanasa.lens.impl.context;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.ParamMatcher;
import net.rvanasa.lens.TypeContext;
import net.rvanasa.lens.LensOperator.OperatorPosition;
import net.rvanasa.lens.exception.LensPointerException;
import net.rvanasa.lens.impl.context.type.BaseTypeContext;

public class EmptyContext implements LensContext
{
	private final Environment env;
	
	public EmptyContext(Environment env)
	{
		this.env = env;
	}
	
	@Override
	public Environment getEnv()
	{
		return env;
	}
	
	@Override
	public LensContext getParent()
	{
		throw new LensPointerException("Context parent does not exist");
	}
	
	@Override
	public LensValue getTargetValue()
	{
		return Lens.UNDEFINED;
	}
	
	@Override
	public boolean isValue(String id)
	{
		return false;
	}
	
	@Override
	public LensValue get(String id)
	{
		return Lens.UNDEFINED;
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		throw new LensPointerException("Cannot set '" + id + "' to " + value);
	}
	
	@Override
	public void add(String id, LensValue value)
	{
		throw new LensPointerException("Cannot add value '" + id + "' as " + value);
	}
	
	@Override
	public LensFunction getFunction(String id, ParamMatcher args)
	{
		throw new LensPointerException("Function " + id + " does not exist with args: " + args);
	}
	
	@Override
	public LensOperator getOperator(String symbol, OperatorPosition type, LensType a, LensType b)
	{
		throw new LensPointerException("Operator is not defined: " + type.getStringValue(symbol, String.valueOf(a), String.valueOf(b)));
	}
	
	@Override
	public boolean isType(String name)
	{
		return false;
	}
	
	@Override
	public LensType getType(String name)
	{
		throw new LensPointerException("Type not found: " + name);
	}
	
	@Override
	public boolean isAnonymousPresent()
	{
		return false;
	}
	
	@Override
	public LensValue getNextAnonymous()
	{
		throw new LensPointerException("# value not defined");
	}
	
	@Override
	public LensValue viewNextAnonymous()
	{
		throw new LensPointerException("# value not defined");
	}
	
	@Override
	public void addAnonymousValue(LensValue value)
	{
		throw new LensPointerException("No # context for value: " + value);
	}
	
	@Override
	public void clearAnonymousValues()
	{
		throw new LensPointerException("No # context");
	}
	
	@Override
	public TypeContext createTypeContext()
	{
		return new BaseTypeContext(getEnv());
	}
}
