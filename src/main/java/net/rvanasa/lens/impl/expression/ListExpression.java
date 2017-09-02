package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeReference;
import net.rvanasa.lens.impl.reference.CommonTypeReference;
import net.rvanasa.lens.impl.reference.ListTypeReference;
import net.rvanasa.lens.impl.value.ListValue;
import net.rvanasa.lens.util.CollectionHelpers;

public class ListExpression extends AbstractExpression
{
	private final ListTypeReference type;
	
	private final LensExpression[] values;
	
	public ListExpression(LensExpression[] values)
	{
		TypeReference[] array = new TypeReference[values.length];
		for(int i = 0; i < values.length; i++)
		{
			array[i] = values[i].getExpType();
		}
		this.type = new ListTypeReference(CommonTypeReference.get(array));
		
		this.values = values;
	}
	
	@Override
	public ListTypeReference getExpType()
	{
		return type;
	}
	
	public LensExpression[] getValues()
	{
		return values;
	}
	
	@Override
	public LensValue eval(InvokeStack stack)
	{
		return new ListValue(getExpType().resolve(stack), CollectionHelpers.map(getValues(), exp -> exp.eval(stack)));
	}
	
	@Override
	public String toString()
	{
		return "[" + joinDelim(", ", (Object[])getValues()) + "]";
	}
}
