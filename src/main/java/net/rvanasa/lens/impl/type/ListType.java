package net.rvanasa.lens.impl.type;

import java.util.ArrayList;
import java.util.List;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.value.ListValue;
import net.rvanasa.lens.impl.value.TupleValue;
import net.rvanasa.lens.util.CollectionHelpers;

public final class ListType implements LensType
{
	private final LensType elementType;
	private final SizeConstraint sizeConstraint;
	
	public ListType(LensType type)
	{
		this(type, AlwaysSizeConstraint.INSTANCE);
	}
	
	public ListType(LensType type, SizeConstraint constraint)
	{
		this.elementType = type;
		this.sizeConstraint = constraint;
	}
	
	public LensType getElementType()
	{
		return elementType;
	}
	
	public SizeConstraint getSizeConstraint()
	{
		return sizeConstraint;
	}
	
	@Override
	public boolean isAssignableFrom(LensType type)
	{
		if(type instanceof ListType)
		{
			ListType listType = ((ListType)type);
			return getElementType().isAssignableFrom(listType.getElementType());
		}
		else if(type instanceof TupleType)
		{
			TupleType tupleType = (TupleType)type;
			return getSizeConstraint().isValidSize(tupleType.size())
					&& (getElementType() == Lens.ANY || CollectionHelpers.allMatch(tupleType.getTypeArray(), param -> getElementType().isAssignableFrom(param)));
		}
		else
		{
			// return getElementType().isAssignableFrom(type);
			return false;
		}
	}
	
	@Override
	public boolean isInstance(LensValue value)
	{
		if(!getSizeConstraint().isValidSize(value.size()))
		{
			return false;
		}
		else if(value instanceof ListValue)
		{
			ListValue list = (ListValue)value;
			return getElementType() == Lens.ANY || CollectionHelpers.allMatch(list.getList(), getElementType()::isInstance);
		}
		else if(value instanceof TupleValue)
		{
			TupleValue tuple = (TupleValue)value;
			return getElementType() == Lens.ANY || CollectionHelpers.allMatch(tuple.getArray(), getElementType()::isInstance);
		}
		return false;
	}
	
	@Override
	public ListValue getTyped(LensValue value)
	{
		if(value instanceof ListValue)
		{
			ListValue listValue = (ListValue)value;
			if(isInstance(listValue))
			{
				return listValue;
			}
			if(getSizeConstraint().isValidSize(listValue.size()))
			{
				List<LensValue> list = new ArrayList<>();
				for(int i = 0; i < listValue.size(); i++)
				{
					list.add(getElementType().getTyped(listValue.getList().get(i)));
				}
				return new ListValue(this, list);
			}
		}
		else if(value instanceof TupleValue)
		{
			TupleValue tuple = (TupleValue)value;
			if(getSizeConstraint().isValidSize(tuple.size()))
			{
				List<LensValue> list = new ArrayList<>();
				for(int i = 0; i < tuple.size(); i++)
				{
					list.add(getElementType().getTyped(tuple.getArray()[i]));
				}
				return new ListValue(this, list);
			}
		}
		else if(getElementType().isInstance(value) && getSizeConstraint().isValidSize(1))
		{
			return ListValue.one(value);
		}
		
		throw new LensException("<" + value.getType() + "> " + value + " is not assignable to " + this);
	}
	
	@Override
	public String toString()
	{
		return getElementType() + "[" + getSizeConstraint().getParamString() + "]";
	}
	
	public interface SizeConstraint
	{
		public int getMinSize();
		
		public boolean isValidSize(int size);
		
		public String getParamString();
	}
	
	public static class AlwaysSizeConstraint implements SizeConstraint
	{
		public static final AlwaysSizeConstraint INSTANCE = new AlwaysSizeConstraint();
		
		private AlwaysSizeConstraint()
		{
		}
		
		@Override
		public int getMinSize()
		{
			return 0;
		}
		
		@Override
		public boolean isValidSize(int size)
		{
			return true;
		}
		
		@Override
		public String getParamString()
		{
			return "";
		}
	}
	
	public static class ValueSizeConstraint implements SizeConstraint
	{
		private final int size;
		
		public ValueSizeConstraint(int size)
		{
			this.size = size;
		}
		
		public int getSize()
		{
			return size;
		}
		
		@Override
		public int getMinSize()
		{
			return getSize();
		}
		
		@Override
		public boolean isValidSize(int size)
		{
			return getSize() == size;
		}
		
		@Override
		public String getParamString()
		{
			return String.valueOf(getSize());
		}
	}
	
	public static class MinSizeConstraint implements SizeConstraint
	{
		private final int min;
		
		public MinSizeConstraint(int min)
		{
			this.min = min;
		}
		
		@Override
		public int getMinSize()
		{
			return min;
		}
		
		@Override
		public boolean isValidSize(int size)
		{
			return getMinSize() <= size;
		}
		
		@Override
		public String getParamString()
		{
			return getMinSize() + "+";
		}
	}
}
