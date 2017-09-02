package net.rvanasa.lens.impl.type;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.value.ListValue;
import net.rvanasa.lens.impl.value.TupleValue;
import net.rvanasa.lens.util.CollectionHelpers;

public class TupleType implements LensType
{
	public static TupleType ANY_2 = new TupleType(new LensType[] {Lens.ANY, Lens.ANY});
	
	public static LensType get(LensType... types)
	{
		if(types.length == 0)
		{
			return Lens.VOID;
		}
		else if(types.length == 1)
		{
			return types[0];
		}
		else
		{
			return new TupleType(types);
		}
	}
	
	private final LensType[] array;
	
	public TupleType(LensType[] array)
	{
		this.array = array;
		
		if(array.length < 2)
		{
			throw new LensException("Invalid tuple type length: " + array.length);
		}
	}
	
	public LensType[] getTypeArray()
	{
		return array;
	}
	
	public int size()
	{
		return getTypeArray().length;
	}
	
	public boolean isIndex(int index)
	{
		return index >= 0 && index < size();
	}
	
	@Override
	public boolean isAssignableFrom(LensType type)
	{
		if(type instanceof TupleType)
		{
			TupleType tupleType = (TupleType)type;
			if(tupleType.size() == size())
			{
				for(byte i = 0; i < size(); i++)
				{
					if(!getTypeArray()[i].isAssignableFrom(tupleType.getTypeArray()[i]))
					{
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean isInstance(LensValue value)
	{
		if(value instanceof TupleValue)
		{
			TupleValue tuple = (TupleValue)value;
			if(tuple.size() == size())
			{
				for(byte i = 0; i < size(); i++)
				{
					if(!getTypeArray()[i].isInstance(tuple.getArray()[i]))
					{
						return false;
					}
				}
				return true;
			}
		}
		else if(value instanceof ListValue)
		{
			ListValue list = ((ListValue)value);
			if(list.size() == size())
			{
				for(byte i = 0; i < size(); i++)
				{
					if(!getTypeArray()[i].isInstance(list.getList().get(i)))
					{
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}
	
	@Override
	public TupleValue getTyped(LensValue value)
	{
		if(value instanceof TupleValue)
		{
			TupleValue tuple = (TupleValue)value;
			if(isInstance(tuple))
			{
				return tuple;
			}
			else if(tuple.size() >= size())
			{
				LensValue[] array = new LensValue[size()];
				for(byte i = 0; i < size(); i++)
				{
					array[i] = getTypeArray()[i].getTyped(tuple.getArray()[i]);
				}
				return new TupleValue(this, array);
			}
		}
		else if(value instanceof ListValue)
		{
			ListValue list = (ListValue)value;
			if(list.size() >= size())
			{
				LensValue[] array = new LensValue[size()];
				for(byte i = 0; i < size(); i++)
				{
					array[i] = getTypeArray()[i].getTyped(list.getList().get(i));
				}
				return new TupleValue(this, array);
			}
		}
		
		throw new LensException(value + " cannot be assigned to " + this);
	}
	
	@Override
	public String toString()
	{
		return "(" + String.join(", ", CollectionHelpers.map(getTypeArray(), LensType::toString, String[]::new)) + ")";
	}
}
