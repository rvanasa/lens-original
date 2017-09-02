package net.rvanasa.lens.impl.value;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.impl.context.Environment;

public class TypeValue implements LensValue
{
	private final LensType typeValue;
	
	public TypeValue(LensType type)
	{
		this.typeValue = type;
	}
	
	public LensType getTypeValue()
	{
		return typeValue;
	}
	
	@Override
	public LensType getType()
	{
		return Lens.TYPE;
	}
	
	@Override
	public Object handle(LensValueResolver resolver)
	{
		return getType();
	}
	
	@Override
	public boolean isValue(String id)
	{
		return getType().getStaticValue().isValue(id);
	}
	
	@Override
	public boolean isAssignable(String id)
	{
		return getType().getStaticValue().isAssignable(id);
	}
	
	@Override
	public LensValue get(String id)
	{
		return getType().getStaticValue().get(id);
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		getType().getStaticValue().set(id, value);
	}
	
	@Override
	public LensFunction getFunction()
	{
		return getType().getStaticValue().getFunction();
	}
	
	@Override
	public boolean isEqualComponent(LensValue value, Environment env)
	{
		if(value instanceof TypeValue)
		{
			LensType type = ((TypeValue)value).getTypeValue();
			return getTypeValue() == type || getTypeValue().isEqualComponent(type) || type.isEqualComponent(getTypeValue());
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		String value = getTypeValue().toString();
		if(!value.startsWith("("))
		{
			value = "(" + value + ")";
		}
		return "type" + value;
	}
}
