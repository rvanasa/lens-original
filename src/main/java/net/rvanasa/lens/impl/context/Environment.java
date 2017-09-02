package net.rvanasa.lens.impl.context;

import static net.rvanasa.lens.Lens.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.rvanasa.lens.InvokeStack;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensContext;
import net.rvanasa.lens.LensFunction;
import net.rvanasa.lens.LensImportManager;
import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.LensOperator.OperatorPosition;
import net.rvanasa.lens.LensRuntime;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.ParamMatcher;
import net.rvanasa.lens.TypeContext;
import net.rvanasa.lens.eval.LensEvaluator;
import net.rvanasa.lens.exception.LensAssertionException;
import net.rvanasa.lens.exception.LensException;
import net.rvanasa.lens.impl.context.type.BaseTypeContext;
import net.rvanasa.lens.impl.expression.LiteralExpression;
import net.rvanasa.lens.impl.function.BasicFunction;
import net.rvanasa.lens.impl.function.BinaryFunction;
import net.rvanasa.lens.impl.function.ExpressionFunction;
import net.rvanasa.lens.impl.function.SeqFunction;
import net.rvanasa.lens.impl.operator.ComparisonOperator;
import net.rvanasa.lens.impl.operator.ExpressionOperator;
import net.rvanasa.lens.impl.operator.IncrementOperator;
import net.rvanasa.lens.impl.operator.LogicOperator;
import net.rvanasa.lens.impl.operator.LogicOperator.LogicOperatorType;
import net.rvanasa.lens.impl.operator.MidfixOperator;
import net.rvanasa.lens.impl.operator.NumericOperator;
import net.rvanasa.lens.impl.operator.PostfixOperator;
import net.rvanasa.lens.impl.operator.PrefixOperator;
import net.rvanasa.lens.impl.resolver.JavaLensValueResolver;
import net.rvanasa.lens.impl.runtime.StandardRuntime;
import net.rvanasa.lens.impl.type.AsyncType;
import net.rvanasa.lens.impl.type.FunctionType;
import net.rvanasa.lens.impl.type.ListType;
import net.rvanasa.lens.impl.type.OptionalType;
import net.rvanasa.lens.impl.type.TupleType;
import net.rvanasa.lens.impl.value.AsyncValue;
import net.rvanasa.lens.impl.value.BooleanValue;
import net.rvanasa.lens.impl.value.FunctionValue;
import net.rvanasa.lens.impl.value.JavaLensValue;
import net.rvanasa.lens.impl.value.ListValue;
import net.rvanasa.lens.impl.value.MapValue;
import net.rvanasa.lens.impl.value.NumberValue;
import net.rvanasa.lens.impl.value.StringValue;
import net.rvanasa.lens.impl.value.TupleValue;
import net.rvanasa.lens.impl.value.TypeValue;
import net.rvanasa.lens.util.CollectionHelpers;

public final class Environment implements LensContext
{
	private final LensRuntime runtime;
	private final LensImportManager importResolver;
	private final LensEvaluator evaluator;
	
	private final JavaLensValueResolver javaResolver = new JavaLensValueResolver();
	
	private final Map<String, LensType> types = new HashMap<>();
	private final Map<String, LensValue> globals = new HashMap<>();
	
	private final List<LensOperator> operators = new ArrayList<>();
	
	private final EmptyContext emptyContext = new EmptyContext(this);
	private final PathContext pathContext = new PathContext(this);
	
	private final MapValue globalValue = new MapValue(getGlobals());
	
	public Environment()
	{
		this(new StandardRuntime());
	}
	
	public Environment(LensRuntime runtime)
	{
		this(runtime, null, new LensEvaluator());
	}
	
	public Environment(LensRuntime runtime, LensImportManager resolver, LensEvaluator evaluator)
	{
		this.runtime = runtime;
		
		this.importResolver = resolver != null ? resolver : getValueResolver();
		this.evaluator = evaluator;
		
		for(LensType type : Lens.getTypes())
		{
			getTypes().put(type.toString(), type);
		}
		
		add("Infinity", new NumberValue<>(DOUBLE, Double.POSITIVE_INFINITY));
		
		add("Lens", getTargetValue());
		
		add("timestamp", new BasicFunction(VOID, LONG, value -> {
			return LONG.getTyped(System.currentTimeMillis());
		}));
		
		add("typeof", new BasicFunction(ANY, TYPE, value -> {
			return new TypeValue(value.getType());
		}));
		
		add("format", new ExpressionFunction(STR, STR, (exp, stack) -> {
			String data = getEvaluator().formatString(exp.eval(stack).getPrintString(), stack.getContext());
			return new StringValue(data);
		}));
		
		add("assert", new ExpressionFunction(BOOL, VOID, (exp, stack) -> {
			if(!BOOL.getTyped(exp.eval(stack)).handle())
			{
				throw new LensAssertionException(exp);
			}
			return UNDEFINED;
		}));
		
		add("print", new BasicFunction(ANY, VOID, value -> {
			getRuntime().print(value);
			return UNDEFINED;
		}));
		
		add("printf", new ExpressionFunction(ANY, VOID, (exp, stack) -> {
			String data = getEvaluator().formatString(exp.eval(stack).getPrintString(), stack.getContext());
			getRuntime().print(data);
			// return new StringObject(data);
			return UNDEFINED;
		}));
		
		add("size", new BasicFunction(ANY, INT, value -> {
			return INT.getTyped(value.size());
		}));
		
		add("empty", new BasicFunction(ANY, BOOL, value -> {
			return BooleanValue.get(value.isEmpty());
		}));
		
		add("expf", new BasicFunction(DEF, VAL, value -> {
			return new JavaLensValue<>(value.getFunction().getBodyExp(), getValueResolver());
		}));
		
		add("eval", new BasicFunction(STR, ANY, value -> {
			return eval(value.getPrintString());
		}));
		
		add("readURL", new BasicFunction(STR, STR, value -> {
			try(BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(value.getPrintString()).openStream())))
			{
				String data = reader.lines().collect(Collectors.joining("\n"));
				return new StringValue(data);
			}
		}));
		
		add("random", new BasicFunction(VOID, NUM, value -> {
			return new NumberValue<>(DOUBLE, Math.random());
		}));
		
		add("reverse", new BasicFunction(LIST, LIST, value -> {
			if(value.size() <= 1)
			{
				return value;
			}
			ListValue listValue = LIST.getTyped(value);
			List<LensValue> list = new ArrayList<>(value.size());
			for(int i = listValue.size() - 1; i >= 0; i--)
			{
				list.add(listValue.get(i));
			}
			return new ListValue(listValue.getType(), list);
		}));
		
		add("flatten", new BasicFunction(ANY, LIST, value -> {
			List<LensValue> list = new ArrayList<>();
			for(LensValue each : value.toIterable())
			{
				for(LensValue item : each.toIterable())
				{
					list.add(item);
				}
			}
			return new ListValue(new ListType(getCommonType(CollectionHelpers.map(list, LensValue::getType, LensType[]::new))), list);
		}));
		
		add("first", new BasicFunction(ANY, ANY, value -> {
			Iterator<LensValue> iterator = value.toIterable().iterator();
			return iterator.hasNext() ? iterator.next() : UNDEFINED;
		}));

		add("foreach", new BasicFunction(ANY, FunctionType.CONSUMER, value -> {
			return new FunctionValue(new BasicFunction(ANY, VOID, func -> {
				for(LensValue item : value.toIterable())
				{
					func.invoke(item);
				}
				return UNDEFINED;
			}));
		}));
		
		ListType zipListType = new ListType(TupleType.ANY_2);
		add("zip", new BinaryFunction(ANY, ANY, zipListType, (a1, b1) -> {
			Iterator<LensValue> a = a1.toIterable().iterator();
			Iterator<LensValue> b = b1.toIterable().iterator();
			
			ListValue list = new ListValue(zipListType, new ArrayList<>());
			boolean hasA = a.hasNext(), hasB = b.hasNext();
			LensValue valueA, valueB;
			while(hasA || hasB)
			{
				valueA = hasA ? a.next() : UNDEFINED;
				valueB = hasB ? b.next() : UNDEFINED;
				list.getList().add(new TupleValue(new LensValue[] {valueA, valueB}));
				
				hasA = a.hasNext();
				hasB = b.hasNext();
			}
			return list;
		}));
		
		add("reduce", new SeqFunction(TupleType.get(ANY, ANY, FunctionType.BI_FUNCTION), ANY, args -> {
			LensValue seq = args[0];
			LensValue value = args[1];
			FunctionValue func = cast(args[2]);
			for(LensValue item : seq.toIterable())
			{
				value = func.invoke(value, item);
			}
			return value;
		}));
		
		add("sleep", new BasicFunction(DOUBLE, BOOL, value -> {
			try
			{
				Thread.sleep((long)(DOUBLE.getTyped(value).handle() * 1000));
				return TRUE;
			}
			catch(InterruptedException e)
			{
				return FALSE;
			}
		}));
		
		add("await", new BasicFunction(new ListType(ASYNC), new AsyncType(ANY), value -> {
			ListValue arg = cast(value);
			ArrayList<LensValue> list = new ArrayList<>();
			AsyncValue async = new AsyncValue(LIST);
			for(int i = 0; i < arg.size(); i++)
			{
				int index = i;
				list.add(null);
				ASYNC.getTyped(arg.get(i)).callback(a -> {
					list.set(index, a);
					if(!list.contains(null))
					{
						async.update(TupleValue.get(list.toArray(new LensValue[list.size()])));
					}
				});
			}
			return async;
		}));
		
		addOperator(new ExpressionOperator("=", OperatorPosition.MIDFIX, ANY, (pointer, target, stack) -> {
			LensValue value = target.eval(stack);
			pointer.assign(value, stack);
			return value;
		}));
		
		addOperator(new MidfixOperator("+", new BinaryFunction(STR, ANY, STR, (a, b) -> {
			return new StringValue(a.getPrintString() + b.getPrintString());
		})), true);
		
		addOperator(new MidfixOperator("+", new BinaryFunction(ANY, STR, STR, (a, b) -> {
			return new StringValue(a.getPrintString() + b.getPrintString());
		})), true);
		
		addOperator(new NumericOperator("+", true, (a, b) -> a + b), true);
		addOperator(new NumericOperator("-", true, (a, b) -> a - b), true);
		addOperator(new NumericOperator("*", true, (a, b) -> a * b), true);
		addOperator(new NumericOperator("/", false, (a, b) -> a / b), true);
		addOperator(new NumericOperator("%", true, (a, b) -> a % b), true);
		addOperator(new NumericOperator("**", false, Math::pow)
		{
			@Override
			public LensType getReturnType(LensType a, LensType b)
			{
				return new OptionalType(super.getReturnType(a, b));
			}
		}, true);
		
		addOperator(new PrefixOperator("-", new BasicFunction(NUM, NUM, value -> {
			NumberValue<?> num = cast(value);
			return num.getType().getTyped(-num.handle().doubleValue());
		}))
		{
			@Override
			public LensType getReturnType(LensType a, LensType b)
			{
				return a;
			}
		});
		
		addOperator(new ExpressionOperator("++", OperatorPosition.PREFIX, NUM, (a, b, stack) -> {
			return getOperator("+=", OperatorPosition.MIDFIX, NUM, NUM).operate(a, new LiteralExpression(new NumberValue<>(INT, 1)), stack);
		}));
		
		addOperator(new ExpressionOperator("++", OperatorPosition.POSTFIX, NUM, (a, b, stack) -> {
			
			LensValue value = a.eval(stack);
			
			getOperator("+=", OperatorPosition.MIDFIX, NUM, NUM).operate(a, new LiteralExpression(new NumberValue<>(INT, 1)), stack);
			return value;
		}));
		
		addOperator(new ExpressionOperator("--", OperatorPosition.PREFIX, NUM, (a, b, stack) -> {
			return getOperator("-=", OperatorPosition.MIDFIX, NUM, NUM).operate(a, new LiteralExpression(new NumberValue<>(INT, 1)), stack);
		}));
		
		addOperator(new ExpressionOperator("--", OperatorPosition.POSTFIX, NUM, (a, b, stack) -> {
			LensValue value = a.eval(stack);
			getOperator("-=", OperatorPosition.MIDFIX, NUM, NUM).operate(a, new LiteralExpression(new NumberValue<>(INT, 1)), stack);
			return value;
		}));
		
		addOperator(new PrefixOperator("!", new BasicFunction(BOOL, BOOL, value -> {
			BooleanValue bool = cast(value);
			return BooleanValue.get(!bool.handle());
		})));
		
		addOperator(new PostfixOperator("?", new BasicFunction(ANY, BOOL, value -> {
			return BooleanValue.get(!TupleValue.get(value).isEmpty());
		})));
		
		addOperator(new MidfixOperator("?", new BinaryFunction(ANY, ANY, ANY, (a, b) -> {
			LensValue value = a;
			return value.isEmpty() ? b : value;
		}))
		{
			
			@Override
			public LensType getReturnType(LensType a, LensType b)
			{
				if(a instanceof OptionalType)
				{
					return ((OptionalType)a).getValueType();
				}
				return b;
			}
			
		}, true);
		
		addOperator(new ComparisonOperator("<", (a, b) -> a < b));
		addOperator(new ComparisonOperator(">", (a, b) -> a > b));
		addOperator(new ComparisonOperator("<=", (a, b) -> a <= b));
		addOperator(new ComparisonOperator(">=", (a, b) -> a >= b));
		
		addOperator(new LogicOperator("&&", LogicOperatorType.AND_LAZY));
		addOperator(new LogicOperator("||", LogicOperatorType.OR_LAZY));
		
		addOperator(new LogicOperator("&&:", LogicOperatorType.AND_EAGER));
		addOperator(new LogicOperator("||:", LogicOperatorType.OR_EAGER));
		
		addOperator(new MidfixOperator("==", new BinaryFunction(ANY, ANY, BOOL, (a, b) -> BooleanValue.get(compare(a, b)))));
		addOperator(new MidfixOperator("!=", new BinaryFunction(ANY, ANY, BOOL, (a, b) -> BooleanValue.get(compare(a, b)).not())));
		
		addOperator(new MidfixOperator("->", new BinaryFunction(ANY, ANY, TupleType.ANY_2, TupleValue::get)));
		
		addOperator(new MidfixOperator("+>", new BinaryFunction(FunctionType.FUNCTION, FunctionType.FUNCTION, FunctionType.FUNCTION, (a, b) -> {
			return new FunctionValue(new BasicFunction(ANY, ANY, value -> b.invoke(a.invoke(value))));
		})), true);
		
		addOperator(new MidfixOperator("::", new BinaryFunction(LIST, LIST, LIST, (a, b) -> {
			ListValue listA = LIST.getTyped(a);
			ListValue listB = LIST.getTyped(b);
			List<LensValue> list = new ArrayList<>();
			list.addAll(listA.getList());
			list.addAll(listB.getList());
			return new ListValue(new ListType(getCommonType(listA.getType().getElementType(), listB.getType().getElementType())), list);
		})), true);
		
		addOperator(new MidfixOperator("`", new BinaryFunction(FunctionType.FUNCTION, ANY, ANY, (function, value) -> {
			return ((FunctionValue)function).invoke(value);
		})), true);
		
		addOperator(new ExpressionOperator("%", OperatorPosition.MIDFIX, ANY, (a, b, stack) -> {
			return FunctionType.FUNCTION.getTyped(b.eval(stack)).getFunction().invoke(a, stack);
		}), true);
		
		addOperator(new MidfixOperator("<<", new BinaryFunction(INT, INT, LIST, (a, b) -> {
			int start = INT.getTyped(a).handle();
			int end = INT.getTyped(b).handle();
			List<LensValue> list = new ArrayList<>();
			for(int i = start; i < end; i++)
			{
				list.add(INT.getTyped(i));
			}
			return new ListValue(new ListType(INT), list);
		})));
		
		addOperator(new MidfixOperator("<>", new BinaryFunction(INT, INT, LIST, (a, b) -> {
			int start = INT.getTyped(a).handle();
			int end = INT.getTyped(b).handle();
			List<LensValue> list = new ArrayList<>();
			if(start <= end)
			{
				for(int i = start; i <= end; i++)
				{
					list.add(INT.getTyped(i));
				}
			}
			else
			{
				for(int i = start; i >= end; i--)
				{
					list.add(INT.getTyped(i));
				}
			}
			return new ListValue(new ListType(INT), list);
		})));
		
		addOperator(new MidfixOperator("|+", new BinaryFunction(LIST, ANY, LIST, (a, b) -> {
			ListValue list = LIST.getTyped(a);
			list.add(b);
			return list;
		})));
		
		addOperator(new MidfixOperator("+|", new BinaryFunction(LIST, ANY, LIST, (a, b) -> {
			ListValue list = cast(a);
			list.add(0, b);
			return list;
		})));
		
		addOperator(new MidfixOperator("^", new BinaryFunction(ANY, FunctionType.FUNCTION, ANY, (seq, func) -> {
			if(seq.size() == 0)
			{
				return seq;
			}
			List<LensValue> out = CollectionHelpers.map(seq.toIterable(), func::invoke);
			return new ListValue(new ListType(getCommonType(getValueTypes(out.stream().toArray(LensValue[]::new)))), out);
		})), true);
		
		addOperator(new MidfixOperator("^!", new BinaryFunction(ANY, ANY, LIST, (a, b) -> {
			LensValue seq = a;
			LensValue value = b;
			List<LensValue> out = CollectionHelpers.map(seq.toIterable(), t -> value);
			return new ListValue(new ListType(getCommonType(getValueTypes(out.toArray(new LensValue[out.size()])))), out);
		})), true);
		
		addOperator(new MidfixOperator("~", new BinaryFunction(ANY, FunctionType.PREDICATE, LIST, (a, b) -> {
			LensValue seq = a;
			LensValue function = cast(b);
			List<LensValue> out = CollectionHelpers.filter(seq.toIterable(), element -> BOOL.getTyped(function.invoke(element)).handle());
			return new ListValue(new ListType(getCommonType(getValueTypes(out.toArray(new LensValue[out.size()])))), out);
		})), true);
		
		addOperator(new MidfixOperator("~?", new BinaryFunction(ANY, FunctionType.PREDICATE, BOOL, (a, b) -> {
			LensValue seq = a;
			LensValue function = cast(b);
			return BooleanValue.get(CollectionHelpers.stream(seq.toIterable()).anyMatch(element -> BOOL.getTyped(function.invoke(element)).handle()));
		})), true);
		
		addOperator(new MidfixOperator("~!", new BinaryFunction(ANY, FunctionType.PREDICATE, BOOL, (a, b) -> {
			LensValue seq = a;
			LensValue function = cast(b);
			return BooleanValue.get(CollectionHelpers.stream(seq.toIterable()).allMatch(element -> BOOL.getTyped(function.invoke(element)).handle()));
		})), true);
		
		addOperator(new MidfixOperator("^^", new BinaryFunction(ANY, FunctionType.BI_FUNCTION, ANY, (a, b) -> {
			LensValue seq = a;
			if(seq.size() == 0)
			{
				return UNDEFINED;
			}
			else if(seq.size() == 1)
			{
				return seq.toIterable().iterator().next();
			}
			LensValue function = FunctionType.BI_FUNCTION.getTyped(b);
			LensValue value = null;
			for(LensValue item : seq.toIterable())
			{
				if(value == null)
				{
					value = item;
				}
				else
				{
					value = function.invoke(value, item);
				}
			}
			return value == null ? UNDEFINED : value;
		})), true);
		
		addOperator(new ExpressionOperator("^", OperatorPosition.MIDFIX, LIST, (a, b, stack) -> {
			LensValue seq = a.eval(stack);
			if(seq.size() == 0)
			{
				return seq;
			}
			List<LensValue> out = CollectionHelpers.map(seq.toIterable(), value -> {
				InvokeStack invoke = stack.createContextual(new InvokeContext(stack.getContext()));
				invoke.getContext().addAnonymousValue(value);
				return b.eval(invoke);
			});
			return new ListValue(new ListType(getCommonType(getValueTypes(out.toArray(new LensValue[out.size()])))), out);
		}), true);
		
		addOperator(new ExpressionOperator("~", OperatorPosition.MIDFIX, LIST, (a, b, stack) -> {
			LensValue seq = a.eval(stack);
			List<LensValue> out = CollectionHelpers.filter(seq.toIterable(), value -> {
				InvokeStack invoke = stack.createContextual(new InvokeContext(stack.getContext()));
				invoke.getContext().addAnonymousValue(value);
				return BOOL.getTyped(b.eval(invoke)).handle();
			});
			return new ListValue(new ListType(getCommonType(getValueTypes(out.toArray(new LensValue[out.size()])))), out);
		}), true);
		
		addOperator(new ExpressionOperator("~?", OperatorPosition.MIDFIX, BOOL, (a, b, stack) -> {
			LensValue seq = a.eval(stack);
			return BooleanValue.get(CollectionHelpers.stream(seq.toIterable()).anyMatch(value -> {
				InvokeStack invoke = stack.createContextual(new InvokeContext(stack.getContext()));
				invoke.getContext().addAnonymousValue(value);
				return BOOL.getTyped(b.eval(invoke)).handle();
			}));
		}), true);
		
		addOperator(new ExpressionOperator("~!", OperatorPosition.MIDFIX, BOOL, (a, b, stack) -> {
			LensValue seq = a.eval(stack);
			return BooleanValue.get(CollectionHelpers.stream(seq.toIterable()).allMatch(value -> {
				InvokeStack invoke = stack.createContextual(new InvokeContext(stack.getContext()));
				invoke.getContext().addAnonymousValue(value);
				return BOOL.getTyped(b.eval(invoke)).handle();
			}));
		}), true);
	}
	
	public LensRuntime getRuntime()
	{
		return runtime;
	}
	
	public LensImportManager getImportResolver()
	{
		return importResolver;
	}
	
	public LensEvaluator getEvaluator()
	{
		return evaluator;
	}
	
	public JavaLensValueResolver getValueResolver()
	{
		return javaResolver;
	}
	
	public Map<String, LensType> getTypes()
	{
		return types;
	}
	
	public Map<String, LensValue> getGlobals()
	{
		return globals;
	}
	
	public List<LensOperator> getOperators()
	{
		return operators;
	}
	
	public LensContext getEmptyContext()
	{
		return emptyContext;
	}
	
	public LensContext getPathContext()
	{
		return pathContext;
	}
	
	@Override
	public Environment getEnv()
	{
		return this;
	}
	
	@Override
	public LensValue getTargetValue()
	{
		return globalValue;
	}
	
	@Override
	public LensContext getParent()
	{
		return getEmptyContext();
	}
	
	@Override
	public boolean isValue(String id)
	{
		return getGlobals().containsKey(id);
	}
	
	@Override
	public LensValue get(String id)
	{
		LensValue value = getGlobals().get(id);
		if(value != null)
		{
			return value;
		}
		return getEmptyContext().get(id);
	}
	
	@Override
	public void set(String id, LensValue value)
	{
		throw new LensException("Cannot modify environment value: " + id);
	}
	
	public void setGlobal(String id, LensValue value)
	{
		getGlobals().put(id, value);
	}
	
	public void add(String id, LensFunction function)
	{
		add(id, new FunctionValue(function));
	}
	
	public void addResolved(String id, Object obj)
	{
		add(id, getValueResolver().getValue(obj));
	}
	
	@Override
	public void add(String id, LensValue value)
	{
		if(getGlobals().containsKey(id))
		{
			throw new LensException("Value already exists: " + id);
		}
		else
		{
			setGlobal(id, value);
		}
	}
	
	@Override
	public LensFunction getFunction(String id, ParamMatcher args)
	{
		return get(id).getFunction();
	}
	
	@Override
	public LensOperator getOperator(String symbol, OperatorPosition type, LensType a, LensType b)
	{
		LensOperator fallback = null;
		for(LensOperator op : getOperators())
		{
			if(op.getSymbol().equals(symbol) && op.getPosition() == type)
			{
				if(op.isValidParams(a, b))
				{
					return op;
				}
				else if(fallback == null)
				{
					fallback = op;
				}
			}
		}
		
		return fallback != null ? fallback : getEmptyContext().getOperator(symbol, type, a, b);
	}
	
	@Override
	public LensType getOperatorReturnType(String symbol, OperatorPosition type, LensType a, LensType b)
	{
		for(LensOperator op : getOperators())
		{
			if(op.getSymbol().equals(symbol) && op.getPosition() == type && op.isValidParams(a, b))
			{
				return op.getReturnType(a, b);
			}
		}
		
		return UNKNOWN;
	}
	
	public void addOperator(LensOperator operator)
	{
		addOperator(operator, false);
	}
	
	public void addOperator(LensOperator operator, boolean incrementor)
	{
		getOperators().add(operator);
		
		if(incrementor)
		{
			addOperator(new IncrementOperator(operator));
		}
	}
	
	public boolean compare(LensValue a, LensValue b)
	{
		return a == b || a.isEqualComponent(b, this) || b.isEqualComponent(a, this);
	}
	
	@Override
	public boolean isType(String name)
	{
		return getTypes().containsKey(name) || getValueResolver().getTypeUncertain(name) != UNKNOWN;
	}
	
	@Override
	public LensType getType(String name)
	{
		LensType type = getTypes().get(name);
		if(type != null)
		{
			return type;
		}
		
		type = getValueResolver().getTypeUncertain(name);
		if(type != UNKNOWN)
		{
			return type;
		}
		
		return getEmptyContext().getType(name);
	}
	
	@Override
	public boolean isAnonymousPresent()
	{
		return getEmptyContext().isAnonymousPresent();
	}
	
	@Override
	public LensValue getNextAnonymous()
	{
		return getEmptyContext().getNextAnonymous();
	}
	
	@Override
	public LensValue viewNextAnonymous()
	{
		return getEmptyContext().viewNextAnonymous();
	}
	
	@Override
	public void addAnonymousValue(LensValue value)
	{
		getEmptyContext().addAnonymousValue(value);
	}
	
	@Override
	public void clearAnonymousValues()
	{
		getEmptyContext().clearAnonymousValues();
	}
	
	@Override
	public TypeContext createTypeContext()
	{
		BaseTypeContext context = new BaseTypeContext(this);
		
		getTypes().forEach(context::setType);
		
		return context;
	}
}
