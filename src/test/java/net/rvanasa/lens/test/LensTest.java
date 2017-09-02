package net.rvanasa.lens.test;

import static net.rvanasa.lens.Lens.STR;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

import org.junit.Test;

import net.rvanasa.lens.impl.context.Environment;
import net.rvanasa.lens.impl.function.BasicFunction;
import net.rvanasa.lens.impl.value.StringValue;

public class LensTest
{
	public static void main(String... argv) throws Exception
	{
		new LensTest().testAssertions();
	}
	
	@Test
	public void testAssertions() throws IOException
	{
		Environment env = new Environment();
		
		env.add("resource", new BasicFunction(STR, STR, value -> {
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getResourceAsStream("/" + value.getPrintString()))))
			{
				String data = reader.lines().collect(Collectors.joining("\n"));
				return new StringValue(data);
			}
		}));
		
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(LensTest.class.getResourceAsStream("/Assert_10.ls"))))
		{
			String data = reader.lines().collect(Collectors.joining("\n"));
			env.eval(data);
		}
	}
}
