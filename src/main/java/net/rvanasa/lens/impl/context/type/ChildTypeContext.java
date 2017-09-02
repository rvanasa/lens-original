package net.rvanasa.lens.impl.context.type;

import java.util.Map;

import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.TypeContext;

public class ChildTypeContext implements TypeContext
{
	private final TypeContext parent;
	
	public ChildTypeContext(TypeContext parent)
	{
		this.parent = parent;
	}
	
	@Override
	public TypeContext getParent()
	{
		return parent;
	}
	
	@Override
	public LensContext getLensContext()
	{
		return getParent().getLensContext();
	}
	
	@Override
	public Map<String, LensType> getTypeMap()
	{
		return getParent().getTypeMap();
	}
	
	@Override
	public boolean isImported(String path)
	{
		return getParent().isImported(path);
	}
	
	@Override
	public LensValue handleImport(String path)
	{
		return getParent().handleImport(path);
	}
}
