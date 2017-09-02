package net.rvanasa.lens.impl.value;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.type.TupleType;
import net.rvanasa.lens.util.CollectionHelpers;

public class MapValue implements LensValue
{
	private final Map<String, LensValue> map;
	
	public MapValue()
	{
		this(new LinkedHashMap<>());
	}
	
	public MapValue(Map<String, LensValue> map)
	{
		this.map = map;
	}
	
	public Map<String, LensValue> getMap()
	{
		return map;
	}
	
	@Override
	public Object handle(LensValueResolver resolver)
	{
		return getMap().entrySet().stream().collect(Collectors.toMap(Entry::getKey, kv -> kv.getValue().handle(resolver)));
	}
	
	@Override
	public LensType getType()
	{
		return Lens.MAP;
	}
	
	@Override
	public boolean isValue(String id)
	{
		return getMap().containsKey(id);
	}
	
	@Override
	public boolean isAssignable(String id)
	{
		return true;
	}
	
	@Override
	public LensType getType(String id)
	{
		LensValue value = getMap().get(id);
		return value != null ? value.getType() : Lens.UNKNOWN;
	}
	
	@Override
	public LensValue get(String id)
	{
		return getMap().getOrDefault(id, Lens.UNDEFINED);
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		if(value == this)
		{
			throw new LensException("Map value cannot be added to itself");
		}
		else if(value == Lens.UNDEFINED)
		{
			getMap().remove(value);
		}
		else
		{
			getMap().put(id, value);
		}
	}
	
	@Override
	public LensFunction getFunction()
	{
		return get("invoke").getFunction();
	}
	
	@Override
	public Iterable<LensValue> toIterable()
	{
		TupleType type = new TupleType(new LensType[] {Lens.STR, Lens.VAL});
		return CollectionHelpers.map(getMap().entrySet(), kv -> new TupleValue(type, new LensValue[] {new StringValue(kv.getKey()), kv.getValue()}));
	}
	
	@Override
	public String toString()
	{
		return "{" + String.join(", ", CollectionHelpers.map(getMap().entrySet(), kv -> kv.getKey() + ": " + (kv.getValue() == this ? "this" : kv.getValue()), String[]::new)) + "}";
	}
}
