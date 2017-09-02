package net.rvanasa.lens.impl.value;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.impl.context.Environment;
import net.rvanasa.lens.impl.type.ListType;
import net.rvanasa.lens.util.CollectionHelpers;

public class ListValue extends AbstractIndexedValue
{
	private ListType type;
	
	private final List<LensValue> list;
	
	public static ListValue one(LensValue value)
	{
		return new ListValue(new ListType(value.getType()), new ArrayList<>(Collections.singleton(value)));
	}
	
	public ListValue(ListType type, List<LensValue> list)
	{
		this.type = type;
		this.list = list;
	}
	
	@Override
	public ListType getType()
	{
		return type;
	}
	
	private void setType(ListType type)
	{
		this.type = type;
	}
	
	public List<LensValue> getList()
	{
		return list;
	}
	
	@Override
	public Iterable<LensValue> toIterable()
	{
		return getList();
	}
	
	@Override
	public List<Object> handle(LensValueResolver resolver)
	{
		return CollectionHelpers.map(getList(), value -> value.handle(resolver));
	}
	
	@Override
	public int size()
	{
		return getList().size();
	}
	
	@Override
	public boolean isValidIndex(int index)
	{
		return index >= 0 && index < size();
	}
	
	@Override
	public LensValue get(int index)
	{
		return getList().get(index);
	}
	
	@Override
	public void set(int index, LensValue value)
	{
		getList().set(index, value);
		insertType(value.getType());
	}
	
	public void add(int index, LensValue value)
	{
		getList().add(index, value);
		insertType(value.getType());
	}
	
	public void add(LensValue value)
	{
		add(size(), value);
	}
	
	public void insertType(LensType type)
	{
		if(isEmpty())
		{
			setType(new ListType(type));
		}
		else if(size() == 1)
		{
			setType(new ListType(Lens.getCommonType(get(0).getType(), type)));
		}
		else if(!getType().isAssignableFrom(type))
		{
			setType(new ListType(Lens.getCommonType(getType().getElementType(), type)));
		}
	}
	
	@Override
	public boolean isValue(String id)
	{
		return CollectionHelpers.allMatch(getList(), value -> value.isValue(id));
	}
	
	@Override
	public boolean isAssignable(String id)
	{
		return CollectionHelpers.allMatch(getList(), value -> value.isAssignable(id));
	}
	
	@Override
	public LensValue get(String id)
	{
		ListType type = new ListType(Lens.getCommonType(CollectionHelpers.map(getList(), LensValue::getType, LensType[]::new)));
		return new ListValue(type, CollectionHelpers.map(getList(), element -> element.get(id)));
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		for(int i = 0; i < size(); i++)
		{
			getList().get(i).set(id, value);
		}
	}
	
	@Override
	public boolean isEqualComponent(LensValue value, Environment env)
	{
		if(value instanceof ListValue)
		{
			ListValue list = (ListValue)value;
			return CollectionHelpers.compare(getList(), list.getList(), env::compare);
		}
		else if(value instanceof TupleValue)
		{
			TupleValue tuple = (TupleValue)value;
			return CollectionHelpers.compare(getList(), Arrays.asList(tuple.getArray()), env::compare);
		}
		else if(size() == 1)
		{
			return env.compare(getList().get(0), value);
		}
		return false;
	}
	
	@Override
	public boolean isEmpty()
	{
		return size() == 0 || getList().stream().allMatch(LensValue::isEmpty);
	}
	
	@Override
	public String toString()
	{
		return "[" + String.join(", ", CollectionHelpers.map(getList(), String::valueOf, String[]::new)) + "]";
	}
}
