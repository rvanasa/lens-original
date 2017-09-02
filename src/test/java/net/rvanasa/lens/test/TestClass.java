package net.rvanasa.lens.test;

import java.util.function.Supplier;

import net.rvanasa.common.Debug;
import net.rvanasa.lens.Lens.LensInvoke;

public class TestClass
{
	public static void message(Supplier<String> text)
	{
		Debug.log(text.get());
	}
	
	public static Supplier<String> messageWrap(String text)
	{
		return () -> text;
	}
	
	public static int TEST_GLOBAL = 5;
	
	public TestClass()
	{
	}
	
	public int testProp = 1;
	
	public int subLib(int a, int b)
	{
		return a - b;
	}
	
	@Override
	public String toString()
	{
		return getClass().getSimpleName() + ":" + testProp;
	}
	
	@LensInvoke
	public void invoke()
	{
		System.out.println("Invoked internal");
	}
}
