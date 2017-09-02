package net.rvanasa.lens.impl.context;

import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.ParamMatcher;
import net.rvanasa.lens.TypeContext;
import net.rvanasa.lens.LensOperator.OperatorPosition;
import net.rvanasa.lens.exception.LensException;

public class ChildContext implements LensContext
{
	private final LensContext parent;
	
	public ChildContext(LensContext parent)
	{
		if(parent == null)
		{
			throw new LensException("Parent context not defined for child: " + this);
		}
		this.parent = parent;
	}
	
	@Override
	public Environment getEnv()
	{
		return getParent().getEnv();
	}
	
	@Override
	public LensContext getParent()
	{
		return parent;
	}
	
	@Override
	public LensValue getTargetValue()
	{
		return getParent().getTargetValue();
	}
	
	@Override
	public boolean isValue(String id)
	{
		return getParent().isValue(id);
	}
	
	@Override
	public LensValue get(String id)
	{
		return getParent().get(id);
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		getParent().set(id, value);
	}
	
	@Override
	public void add(String id, LensValue value)
	{
		getParent().add(id, value);
	}
	
	@Override
	public LensFunction getFunction(String id, ParamMatcher args)
	{
		return getParent().getFunction(id, args);
	}
	
	@Override
	public LensOperator getOperator(String symbol, OperatorPosition type, LensType a, LensType b)
	{
		return getParent().getOperator(symbol, type, a, b);
	}
	
	@Override
	public boolean isType(String name)
	{
		return getParent().isType(name);
	}
	
	@Override
	public LensType getType(String name)
	{
		return getParent().getType(name);
	}
	
	@Override
	public LensValue getNextAnonymous()
	{
		return getParent().getNextAnonymous();
	}
	
	@Override
	public LensValue viewNextAnonymous()
	{
		return getParent().viewNextAnonymous();
	}
	
	@Override
	public boolean isAnonymousPresent()
	{
		return getParent().isAnonymousPresent();
	}
	
	@Override
	public void addAnonymousValue(LensValue value)
	{
		getParent().addAnonymousValue(value);
	}
	
	@Override
	public void clearAnonymousValues()
	{
		getParent().clearAnonymousValues();
	}
	
	@Override
	public TypeContext createTypeContext()
	{
		return getParent().createTypeContext();
	}
}
