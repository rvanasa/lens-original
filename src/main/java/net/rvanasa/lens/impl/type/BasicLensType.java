package net.rvanasa.lens.impl.type;

import java.util.function.Predicate;

import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensType;

public class BasicLensType implements LensType
{
	private final String name;
	
	private final Predicate<LensType> assignable;
	private final Predicate<LensValue> constraint;
	
	public BasicLensType(String name, Predicate<LensType> assignable, Predicate<LensValue> constraint)
	{
		this.name = name;
		this.assignable = assignable == null ? Predicate.isEqual(this) : assignable;
		this.constraint = constraint;
	}
	
	public String getName()
	{
		return name;
	}
	
	@Override
	public boolean isAssignableFrom(LensType type)
	{
		return assignable.test(type);
	}
	
	@Override
	public boolean isInstance(LensValue value)
	{
		return constraint.test(value);
	}
	
	@Override
	public String toString()
	{
		return getName();
	}
}
