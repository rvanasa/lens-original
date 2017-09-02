package net.rvanasa.lens.impl.value;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.exception.LensPointerException;

public class NullValue extends AbstractMemberValue
{
	public static final NullValue INSTANCE = new NullValue();
	
	private NullValue()
	{
	}
	
	@Override
	public Object handle(LensValueResolver resolver)
	{
		return null;
	}
	
	@Override
	public LensType getType()
	{
		return Lens.VAL;
	}
	
	@Override
	public boolean isValue(String id)
	{
		return false;
	}
	
	@Override
	public boolean isAssignable(String id)
	{
		return false;
	}
	
	@Override
	public LensValue get(String id)
	{
		throw new LensPointerException("Null pointer: " + id);
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		throw new LensPointerException("Null pointer assignment: " + id + " -> " + value);
	}
	
	@Override
	public LensFunction getFunction()
	{
		throw new LensPointerException("Null invocation target");
	}
	
	@Override
	public String toString()
	{
		return "null";
	}
}
