package net.rvanasa.lens;

import java.util.Map;

import net.rvanasa.lens.impl.context.Environment;
import net.rvanasa.lens.impl.context.type.ChildTypeContext;

public interface TypeContext
{
	default Environment getEnv()
	{
		return getLensContext().getEnv();
	}
	
	public LensContext getLensContext();
	
	public TypeContext getParent();
	
	default TypeContext createChild()
	{
		return new ChildTypeContext(this);
	}
	
	public Map<String, LensType> getTypeMap();
	
	default boolean isType(String name)
	{
		return getTypeMap().containsKey(name) || getLensContext().isType(name);
	}
	
	default LensType getType(String name)
	{
		LensType type = getTypeMap().get(name);
		return type != null ? type : getLensContext().getType(name);
	}
	
	default void setType(String name, LensType type)
	{
		getTypeMap().put(name, type);
	}
	
	public boolean isImported(String path);
	
	public LensValue handleImport(String path);
}
