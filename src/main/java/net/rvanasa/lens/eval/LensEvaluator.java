package net.rvanasa.lens.eval;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.rvanasa.lang.interpreter.LexSequence;
import net.rvanasa.lang.interpreter.scanner.IScanner;
import net.rvanasa.lang.interpreter.token.TokenLexer;
import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeContext;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.expression.EmptyExpression;

public class LensEvaluator
{
	public LensEvaluator()
	{
	}
	
	public LensExpression parse(LensContext context, String data)
	{
		if(data == null)
		{
			return EmptyExpression.INSTANCE;
		}
		else if(context == null)
		{
			throw new LensException("Parser context cannot be null");
		}
		
		TokenLexer lexer = new TokenLexer(LensInterpreter.TERMS)
				.ignore(LensInterpreter.term("comment_line"))
				.ignore(LensInterpreter.term("comment_block"));
				
		TypeContext typeContext = context.createTypeContext();
		IScanner<LensExpression> scanner = new LensInterpreter(typeContext).entryPointExp();
		
		try
		{
			LexSequence lex = lexer.tokenize(data);
			LensExpression expr = scanner.scan(lex);
			
			return expr;
		}
		catch(Throwable e)
		{
			String truncated = data.substring(0, Math.min(50, data.length())).replaceAll("[\n\t ]+", " ").trim();
			throw new LensException("Failed to parse input:  " + truncated + " ...", e);
		}
	}
	
	public LensValue eval(LensContext context, String data)
	{
		return eval(new InvokeStack(context), data);
	}
	
	public LensValue eval(InvokeStack stack, String data)
	{
		LensExpression exp = parse(stack.getContext(), data);
		LensValue value = exp.eval(stack);
		return stack.isReturned() ? stack.getReturnValue() : value;
	}
	
	public String formatString(String data, LensContext context)
	{
		Pattern pattern = Pattern.compile("\\$[^\\s]+");
		Matcher matcher;
		while((matcher = pattern.matcher(data)).find())
		{
			data = data.replace(matcher.group(), context.get(matcher.group().substring(1).trim()).getPrintString());
		}
		return data;
	}
}
