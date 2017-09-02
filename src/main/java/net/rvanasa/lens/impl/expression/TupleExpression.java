package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.reference.TupleTypeReference;
import net.rvanasa.lens.impl.value.TupleValue;

public class TupleExpression extends AbstractExpression
{
	private final TupleTypeReference type;
	private final LensExpression[] values;
	
	public TupleExpression(LensExpression[] values)
	{
		if(values.length < 2 || values.length > 255)
		{
			throw new LensException("Invalid tuple expression length: " + values.length);
		}
		
		TypeReference[] array = new TypeReference[values.length];
		for(byte i = 0; i < values.length; i++)
		{
			array[i] = values[i].getExpType();
		}
		this.type = new TupleTypeReference(array);
		this.values = values;
	}
	
	public LensExpression[] getValues()
	{
		return values;
	}
	
	@Override
	public TupleTypeReference getExpType()
	{
		return type;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		LensValue[]array = new LensValue[getValues().length];
		for(byte i = 0; i < array.length; i++)
		{
			array[i] = getValues()[i].eval(stack);
		}
		return new TupleValue(array);
	}
	
	@Override
	public void assign(LensValue value, InvokeStack stack)
	{
		TupleValue tuple = TupleValue.one(value);
		for(byte i = 0; i < getValues().length; i++)
		{
			getValues()[i].assign(tuple.get(i), stack);
		}
	}
	
	@Override
	public String toString()
	{
		return "(" + joinDelim(", ", (Object[])getValues()) + ")";
	}
}
