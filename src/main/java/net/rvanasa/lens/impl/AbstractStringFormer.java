package net.rvanasa.lens.impl;

import java.util.Arrays;
import java.util.Collection;

import net.rvanasa.lang.reflect.Inspectors;
import net.rvanasa.lens.util.CollectionHelpers;

public abstract class AbstractStringFormer
{
	protected String inspect(Object value)
	{
		return Inspectors.toString(value);
	}
	
	protected String join(Collection<?> values)
	{
		return joinDelim(" ", values);
	}
	
	protected String join(Object... values)
	{
		return join(Arrays.asList(values));
	}
	
	protected String joinDelim(String delim, Collection<?> values)
	{
		return String.join(delim, CollectionHelpers.map(values, String::valueOf));
	}
	
	protected String joinDelim(String delim, Object... values)
	{
		return String.join(delim, CollectionHelpers.map(values, String::valueOf));
	}
	
	protected String joinDelim(String delim, String... values)
	{
		return String.join(delim, values);
	}
}
