package net.rvanasa.lens;

public interface LensValueResolver
{
	public LensType getTypeUncertain(String type);
	
	public LensValue getValue(Object object);
}
