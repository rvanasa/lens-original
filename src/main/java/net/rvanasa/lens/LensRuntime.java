package net.rvanasa.lens;

import net.rvanasa.lens.exception.LensException;

public interface LensRuntime
{
	public void print(LensValue value);
	
	public void print(String message);
	
	public void error(String message);
	
	public void error(LensException e);
	
	public void async(Runnable action);
}
