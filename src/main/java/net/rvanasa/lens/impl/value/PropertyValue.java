package net.rvanasa.lens.impl.value;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensProperty;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensValueResolver;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.property.ReadonlyProperty;
import net.rvanasa.lens.util.CollectionHelpers;

public class PropertyValue implements LensValue
{
	private final Map<String, LensProperty> propertyMap;
	
	public PropertyValue()
	{
		this(new LinkedHashMap<>());
	}
	
	public PropertyValue(HashMap<String, LensProperty> propertyMap)
	{
		this.propertyMap = propertyMap;
	}
	
	public Map<String, LensProperty> getPropertyMap()
	{
		return propertyMap;
	}
	
	public PropertyValue setProp(String id, LensProperty prop)
	{
		getPropertyMap().put(id, prop);
		return this;
	}
	
	public PropertyValue setProp(String id, LensFunction function)
	{
		return setProp(id, new ReadonlyProperty(function));
	}
	
	@Override
	public LensType getType()
	{
		return Lens.UNKNOWN;
	}
	
	@Override
	public Object handle(LensValueResolver resolver)
	{
		return getPropertyMap();
	}
	
	@Override
	public boolean isValue(String id)
	{
		return getPropertyMap().containsKey(id);
	}
	
	@Override
	public boolean isAssignable(String id)
	{
		return isValue(id);
	}
	
	public LensProperty getProperty(String id)
	{
		LensProperty prop = getPropertyMap().get(id);
		if(prop == null)
		{
			throw new LensException("Property not defined: " + id);
		}
		return prop;
	}
	
	@Override
	public LensValue get(String id)
	{
		return getProperty(id).get();
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		getProperty(id).set(value);
	}
	
	@Override
	public String toString()
	{
		return "<" + String.join(", ", CollectionHelpers.map(getPropertyMap().entrySet(), kv -> kv.getValue().getDescription(kv.getKey()))) + ">";
	}
}
