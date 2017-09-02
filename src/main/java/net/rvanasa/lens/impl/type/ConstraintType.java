package net.rvanasa.lens.impl.type;

import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;

public class ConstraintType implements LensType
{
	private final Map<String, LensType> typeMap;
	
	public ConstraintType(Map<String, LensType> typeMap)
	{
		this.typeMap = typeMap;
	}
	
	public Map<String, LensType> getTypeMap()
	{
		return typeMap;
	}
	
	@Override
	public boolean isAssignableFrom(LensType type)
	{
		if(type instanceof ConstraintType)
		{
			ConstraintType constraint = (ConstraintType)type;
			for(Entry<String, LensType> prop : constraint.getTypeMap().entrySet())
			{
				if(!getTypeMap().containsKey(prop.getKey()) || !getTypeMap().get(prop.getKey()).isAssignableFrom(prop.getValue()))
				{
					return false;
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public boolean isInstance(LensValue value)
	{
		boolean flag = true;
		for(Entry<String, LensType> prop : getTypeMap().entrySet())
		{
			if(!value.isValue(prop.getKey()) || !prop.getValue().isInstance(value.get(prop.getKey())))
			{
				return false;
			}
		}
		
		return flag || LensType.super.isInstance(value);
	}
	
	@Override
	public String toString()
	{
		return "{" + getTypeMap().entrySet().stream().map(kv -> kv.getKey() + ": " + kv.getValue()).collect(Collectors.joining(", ")) + "}";
	}
}
