package net.rvanasa.lens;

import net.rvanasa.lens.impl.function.BinaryFunction;

public interface LensOperator
{
	public OperatorPosition getPosition();
	
	public String getSymbol();
	
	public LensType getReturnType(LensType a, LensType b);
	
	public boolean isValidParams(LensType a, LensType b);
	
	default LensValue operate(LensExpression a, LensExpression b, InvokeStack stack)
	{
		return operate(a.eval(stack), b.eval(stack));
	}
	
	public LensValue operate(LensValue a, LensValue b);
	
	default LensFunction asFunction()
	{
		return new BinaryFunction(Lens.ANY, Lens.ANY, Lens.ANY, this::operate);
	}
	
	public static int getSymbolPrecedence(String symbol)
	{
		if(symbol.equals("=") || (symbol.endsWith("=") && !(symbol.equals("==") || symbol.equals("<=") || symbol.equals(">="))))
		{
			return 20;
		}
		
		if(symbol.startsWith("?"))
		{
			return 100;
		}
		if(symbol.startsWith("**"))
		{
			return 90;
		}
		if(symbol.startsWith("*") || symbol.startsWith("/") || symbol.startsWith("%"))
		{
			return 80;
		}
		if(symbol.startsWith("+") || symbol.startsWith("-"))
		{
			return 60;
		}
		if(symbol.startsWith("=") || symbol.startsWith("!"))
		{
			return 50;
		}
		if(symbol.startsWith("<") || symbol.startsWith(">"))
		{
			return 40;
		}
		if(symbol.startsWith("&") || symbol.startsWith("|"))
		{
			return 30;
		}
		if(symbol.startsWith(":"))
		{
			return 20;
		}
		if(symbol.startsWith("^") || symbol.startsWith("~"))
		{
			return 10;
		}
		return 1;
	}
	
	public enum OperatorPosition
	{
		MIDFIX
		{
			
			@Override
			public String getStringValue(String symbol, String a, String b)
			{
				return a + " " + symbol + " " + b;
			}
		},
		PREFIX
		{
			
			@Override
			public String getStringValue(String symbol, String a, String b)
			{
				return symbol + a;
			}
		},
		POSTFIX
		{
			
			@Override
			public String getStringValue(String symbol, String a, String b)
			{
				return a + symbol;
			}
		};
		
		public static OperatorPosition from(boolean a, boolean b)
		{
			if(a && b)
			{
				return MIDFIX;
			}
			else if(a)
			{
				return POSTFIX;
			}
			else if(b)
			{
				return PREFIX;
			}
			return null;
		}
		
		public abstract String getStringValue(String symbol, String a, String b);
	}
}
