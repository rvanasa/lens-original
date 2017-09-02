package net.rvanasa.lens.impl.runtime;

import net.rvanasa.common.monitor.IMonitor;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.LensRuntime;
import net.rvanasa.lens.exception.LensException;

public class StandardRuntime implements LensRuntime
{
	private IMonitor monitor;
	
	public StandardRuntime()
	{
		this(IMonitor.SYSTEM);
	}
	
	public StandardRuntime(IMonitor monitor)
	{
		this.monitor = monitor;
	}
	
	public IMonitor getMonitor()
	{
		return monitor;
	}
	
	public void setMonitor(IMonitor monitor)
	{
		this.monitor = monitor;
	}
	
	@Override
	public void print(LensValue value)
	{
		getMonitor().log(value.getPrintString());
	}
	
	@Override
	public void print(String message)
	{
		getMonitor().log(message);
	}
	
	@Override
	public void error(String message)
	{
		getMonitor().err(message);
	}
	
	@Override
	public void error(LensException e)
	{
		getMonitor().err(e);
	}
	
	@Override
	public void async(Runnable action)
	{
		new Thread(Thread.currentThread().getThreadGroup(), action, "lens-async").start();
	}
}
