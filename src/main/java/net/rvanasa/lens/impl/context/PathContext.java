package net.rvanasa.lens.impl.context;

import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensOperator.OperatorPosition;

public class PathContext extends EmptyContext
{
	public PathContext(Environment env)
	{
		super(env);
	}
	
	@Override
	public LensOperator getOperator(String symbol, OperatorPosition type, LensType a, LensType b)
	{
		return getEnv().getOperator(symbol, type, a, b);
	}
}
