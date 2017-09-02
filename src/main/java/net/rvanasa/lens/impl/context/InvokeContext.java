package net.rvanasa.lens.impl.context;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.LensOperator.OperatorPosition;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.ParamMatcher;
import net.rvanasa.lens.TypeContext;
import net.rvanasa.lens.exception.LensException;

public class InvokeContext extends ChildContext
{
	private final Map<String, LensValue> variableMap = new HashMap<>();
	
	private final Queue<LensValue> anonymousValues = new ArrayDeque<>();
	
	public InvokeContext(LensContext parent)
	{
		super(parent);
	}
	
	public Map<String, LensValue> getVariableMap()
	{
		return variableMap;
	}
	
	public Queue<LensValue> getAnonymousValues()
	{
		return anonymousValues;
	}
	
	@Override
	public boolean isValue(String id)
	{
		return getVariableMap().containsKey(id) || super.isValue(id);
	}
	
	@Override
	public LensValue get(String id)
	{
		LensValue value = getVariableMap().get(id);
		return value != null ? value : super.get(id);
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		if(getVariableMap().containsKey(id) || !super.isValue(id))
		{
			getVariableMap().put(id, value);
		}
		else
		{
			super.set(id, value);
		}
	}
	
	@Override
	public void add(String id, LensValue value)
	{
		if(getVariableMap().containsKey(id))
		{
			throw new LensException("'" + id + "' is already defined");
		}
		else
		{
			getVariableMap().put(id, value);
		}
	}
	
	@Override
	public LensFunction getFunction(String id, ParamMatcher args)
	{
		LensValue value = getVariableMap().get(id);
		return value != null ? value.getFunction() : super.getFunction(id, args);
	}
	
	@Override
	public LensOperator getOperator(String symbol, OperatorPosition type, LensType a, LensType b)
	{
		return super.getOperator(symbol, type, a, b);
	}
	
	@Override
	public boolean isAnonymousPresent()
	{
		return !getAnonymousValues().isEmpty() || super.isAnonymousPresent();
	}
	
	@Override
	public LensValue getNextAnonymous()
	{
		if(!getAnonymousValues().isEmpty())
		{
			return getAnonymousValues().size() > 1 ? getAnonymousValues().poll() : getAnonymousValues().peek();
		}
		else
		{
			return super.getNextAnonymous();
		}
	}
	
	@Override
	public LensValue viewNextAnonymous()
	{
		if(!getAnonymousValues().isEmpty())
		{
			return getAnonymousValues().peek();
		}
		else
		{
			return super.viewNextAnonymous();
		}
	}
	
	@Override
	public void addAnonymousValue(LensValue value)
	{
		getAnonymousValues().add(value);
	}
	
	@Override
	public void clearAnonymousValues()
	{
		getAnonymousValues().clear();
	}
	
	@Override
	public TypeContext createTypeContext()
	{
		TypeContext context = super.createTypeContext();
		// getVariableMap().forEach((id, value) -> context.setTypeInference(id, value.getType()));
		
		return context;
	}
}
