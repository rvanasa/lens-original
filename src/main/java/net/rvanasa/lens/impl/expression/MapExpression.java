package net.rvanasa.lens.impl.expression;

import java.util.List;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.impl.context.ObjectContext;
import net.rvanasa.lens.impl.value.MapValue;

public class MapExpression extends AbstractTypedExpression
{
	private final List<LensExpression> values;
	
	public MapExpression(List<LensExpression> statements)
	{
		super(Lens.MAP);
		
		this.values = statements;
		
	}
	
	public List<LensExpression> getValues()
	{
		return values;
	}
	
	@Override
	public MapValue eval(InvokeStack stack)
	{
		MapValue value = new MapValue();
		
		stack = stack.createContextual(new ObjectContext(value, stack.getContext()));
		for(LensExpression exp : getValues())
		{
			exp.eval(stack);
		}
		
		return value;
	}
	
	@Override
	public String toString()
	{
		return join("{", joinDelim(", ", getValues()), "}");
	}
}
