package net.rvanasa.lens.impl.value;

import java.util.Arrays;
import java.util.List;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.context.Environment;
import net.rvanasa.lens.impl.type.TupleType;
import net.rvanasa.lens.util.CollectionHelpers;

public class TupleValue extends AbstractIndexedValue
{
	public static LensValue get(LensValue... values)
	{
		if(values.length == 0)
		{
			return Lens.UNDEFINED;
		}
		else if(values.length == 1)
		{
			return values[0];
		}
		else
		{
			return new TupleValue(values);
		}
	}
	
	public static TupleValue one(LensValue value)
	{
		if(value instanceof TupleValue)
		{
			return (TupleValue)value;
		}
		else
		{
			return new TupleValue(new TupleType(new LensType[] {value.getType()}), new LensValue[] {value});
		}
	}
	
	private final TupleType type;
	
	private final LensValue[] array;
	
	public TupleValue(LensValue[] values)
	{
		this(new TupleType(Lens.getValueTypes(values)), values);
		
		if(values.length < 2)
		{
			throw new LensException("Invalid tuple length: " + values.length);
		}
	}
	
	public TupleValue(TupleType type, LensValue[] values)
	{
		super(new MemberLookup());
		
		this.type = type;
		
		this.array = values;
	}
	
	public LensValue[] getArray()
	{
		return array;
	}
	
	@Override
	public List<LensValue> toIterable()
	{
		return Arrays.asList(getArray());
	}
	
	@Override
	public Object[] handle(LensValueResolver resolver)
	{
		return CollectionHelpers.map(getArray(), value -> value.handle(resolver), Object[]::new);
	}
	
	@Override
	public TupleType getType()
	{
		return type;
	}
	
	@Override
	public int size()
	{
		return getArray().length;
	}
	
	@Override
	public boolean isEmpty()
	{
		return super.isEmpty() || CollectionHelpers.allMatch(getArray(), LensValue::isEmpty);
	}
	
	@Override
	public boolean isVoid()
	{
		return isEmpty();
	}
	
	@Override
	public boolean isValidIndex(int index)
	{
		return getType().isIndex(index);
	}
	
	@Override
	public LensValue get(int index)
	{
		return getArray()[index];
	}
	
	@Override
	public void set(int index, LensValue value)
	{
		getArray()[index] = getType().getTypeArray()[index].getTyped(value);
	}
	
	@Override
	public boolean isValue(String id)
	{
		return CollectionHelpers.allMatch(getArray(), value -> value.isValue(id));
	}
	
	@Override
	public boolean isAssignable(String id)
	{
		return CollectionHelpers.allMatch(getArray(), value -> value.isAssignable(id));
	}
	
	@Override
	public LensValue get(String id)
	{
		return TupleValue.get(CollectionHelpers.map(getArray(), t -> t.get(id), LensValue[]::new));
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		for(int i = 0; i < size(); i++)
		{
			getArray()[i].set(id, value);
		}
	}
	
	@Override
	public boolean isEqualComponent(LensValue value, Environment env)
	{
		if(value instanceof TupleValue)
		{
			TupleValue tuple = (TupleValue)value;
			return CollectionHelpers.compare(getArray(), tuple.getArray(), env::compare);
		}
		else if(value instanceof ListValue)
		{
			ListValue list = (ListValue)value;
			return CollectionHelpers.compare(getArray(), list.getList().toArray(new LensValue[list.size()]), env::compare);
		}
		return false;
	}
	
	@Override
	public String toString()
	{
		return "(" + String.join(", ", CollectionHelpers.map(getArray(), LensValue::toString, String[]::new)) + ")";
	}
}
