package net.rvanasa.lens.impl.expression;

import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.impl.AbstractStringFormer;
import net.rvanasa.lens.impl.expression.format.ExpressionFormatter;

public abstract class AbstractExpression extends AbstractStringFormer implements LensExpression
{
	@Override
	public abstract String toString();
	
	public String toString(ExpressionFormatter format)
	{
		return toString();
	}
}
