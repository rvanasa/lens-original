package net.rvanasa.lens.impl.param;

import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.impl.context.Environment;

public class LiteralParam implements LensParam
{
	private final LensValue value;
	private final Environment env;
	
	public LiteralParam(LensValue value, Environment env)
	{
		this.value = value;
		this.env = env;
	}
	
	public LensValue getValue()
	{
		return value;
	}
	
	public Environment getEnv()
	{
		return env;
	}
	
	@Override
	public LensType getType()
	{
		return getValue().getType();
	}
	
	@Override
	public boolean isValid(LensValue value)
	{
		return getEnv().compare(getValue(), value);
	}
	
	@Override
	public void setupContext(LensValue value, LensContext context)
	{
	}
	
	@Override
	public String toString()
	{
		return getValue().toString();
	}
}
