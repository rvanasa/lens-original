package net.rvanasa.lens;

public interface LensProperty
{
	public LensValue get();
	
	public void set(LensValue value);
	
	default String getDescription(String id)
	{
		return id;
	}
}