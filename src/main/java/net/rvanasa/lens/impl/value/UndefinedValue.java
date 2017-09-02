package net.rvanasa.lens.impl.value;

import java.util.Collections;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.exception.LensPointerException;

public class UndefinedValue extends AbstractMemberValue
{
	public static final UndefinedValue INSTANCE = new UndefinedValue();
	
	private UndefinedValue()
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
		return Lens.VOID;
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
		throw new LensPointerException("Undefined pointer: " + id);
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		throw new LensPointerException("Undefined pointer assignment: " + id + " -> " + value);
	}
	
	@Override
	public LensFunction getFunction()
	{
		throw new LensPointerException("Undefined invocation target");
	}
	
	@Override
	public int size()
	{
		return 0;
	}
	
	@Override
	public boolean isVoid()
	{
		return true;
	}
	
	@Override
	public Iterable<LensValue> toIterable()
	{
		return Collections.emptyList();
	}
	
	@Override
	public String toString()
	{
		return "()";
	}
	
	@Override
	public String getPrintString()
	{
		return "undefined";
	}
}
