package net.rvanasa.lens.eval;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import net.rvanasa.lang.escape.EscapeSequencer;
import net.rvanasa.lang.interpreter.LexSequence;
import net.rvanasa.lang.interpreter.StringTerminals;
import net.rvanasa.lang.interpreter.Terminal;
import net.rvanasa.lang.interpreter.TokenNesting;
import net.rvanasa.lang.interpreter.scanner.BiScanner.ScanPair;
import net.rvanasa.lang.interpreter.scanner.IScanResult;
import net.rvanasa.lang.interpreter.scanner.IScanner;
import net.rvanasa.lang.interpreter.scanner.Scanners;
import net.rvanasa.lang.interpreter.scanner.SymbolScanner;
import net.rvanasa.lens.Lens;
import net.rvanasa.lens.LensExpression;
import net.rvanasa.lens.LensOperator;
import net.rvanasa.lens.LensOperator.OperatorPosition;
import net.rvanasa.lens.LensParam;
import net.rvanasa.lens.LensType;
import net.rvanasa.lens.LensValue;
import net.rvanasa.lens.TypeContext;
import net.rvanasa.lens.impl.expression.AnonymousExpression;
import net.rvanasa.lens.impl.expression.AssignExpression;
import net.rvanasa.lens.impl.expression.AsyncExpression;
import net.rvanasa.lens.impl.expression.BlockBaseExpression;
import net.rvanasa.lens.impl.expression.BlockExpression;
import net.rvanasa.lens.impl.expression.CastExpression;
import net.rvanasa.lens.impl.expression.ContextExpression;
import net.rvanasa.lens.impl.expression.EmptyExpression;
import net.rvanasa.lens.impl.expression.FunctionExpression;
import net.rvanasa.lens.impl.expression.IdentifierExpression;
import net.rvanasa.lens.impl.expression.IfElseExpression;
import net.rvanasa.lens.impl.expression.IfExpression;
import net.rvanasa.lens.impl.expression.ImportExpression;
import net.rvanasa.lens.impl.expression.IndexerExpression;
import net.rvanasa.lens.impl.expression.InvokeExpression;
import net.rvanasa.lens.impl.expression.LambdaExpression;
import net.rvanasa.lens.impl.expression.ListExpression;
import net.rvanasa.lens.impl.expression.LiteralExpression;
import net.rvanasa.lens.impl.expression.MapExpression;
import net.rvanasa.lens.impl.expression.MatchExpression;
import net.rvanasa.lens.impl.expression.MatchExpression.MatchCase;
import net.rvanasa.lens.impl.expression.MultiExpression;
import net.rvanasa.lens.impl.expression.NewExpression;
import net.rvanasa.lens.impl.expression.OperatorDefExpression;
import net.rvanasa.lens.impl.expression.OperatorExpression;
import net.rvanasa.lens.impl.expression.OperatorHandleExpression;
import net.rvanasa.lens.impl.expression.PathExpression;
import net.rvanasa.lens.impl.expression.ReturnExpression;
import net.rvanasa.lens.impl.expression.StaticExpression;
import net.rvanasa.lens.impl.expression.ThisExpression;
import net.rvanasa.lens.impl.expression.ThrowExpression;
import net.rvanasa.lens.impl.expression.TryCatchExpression;
import net.rvanasa.lens.impl.expression.TryCatchExpression.CatchCase;
import net.rvanasa.lens.impl.expression.TupleExpression;
import net.rvanasa.lens.impl.expression.TypeCheckExpression;
import net.rvanasa.lens.impl.expression.TypeDefExpression;
import net.rvanasa.lens.impl.expression.TypeExpression;
import net.rvanasa.lens.impl.expression.UsingExpression;
import net.rvanasa.lens.impl.expression.VariableExpression;
import net.rvanasa.lens.impl.expression.WhileExpression;
import net.rvanasa.lens.impl.param.AnonymousParam;
import net.rvanasa.lens.impl.param.LiteralParam;
import net.rvanasa.lens.impl.param.NamedParam;
import net.rvanasa.lens.impl.param.TupleParam;
import net.rvanasa.lens.impl.type.AsyncType;
import net.rvanasa.lens.impl.type.ConstraintType;
import net.rvanasa.lens.impl.type.FunctionType;
import net.rvanasa.lens.impl.type.ListType;
import net.rvanasa.lens.impl.type.ListType.AlwaysSizeConstraint;
import net.rvanasa.lens.impl.type.ListType.MinSizeConstraint;
import net.rvanasa.lens.impl.type.ListType.SizeConstraint;
import net.rvanasa.lens.impl.type.ListType.ValueSizeConstraint;
import net.rvanasa.lens.impl.type.LogicAndType;
import net.rvanasa.lens.impl.type.LogicOrType;
import net.rvanasa.lens.impl.type.OptionalType;
import net.rvanasa.lens.impl.type.TupleType;
import net.rvanasa.lens.impl.value.StringValue;
import net.rvanasa.lens.util.CollectionHelpers;

public class LensInterpreter
{
	static final StringTerminals TERMS = new StringTerminals()
			.add("def", "var", "opr", "type", "is", "as"/* , "assert" */, "new", "return")
			.add("if", "else", "while", "throw", "try", "catch", "import", "using", "match", "case")
			.add("static", "async", "true", "false", "null", "undefined", "this")
			.add("#", "@", ".", ",", ";", ":", "=>")
			.add("(", ")", "{", "}", "[", "]")
			.add(new Terminal("comment_line", Pattern.compile("//.*\n?")))
			.add(new Terminal("comment_block", Pattern.compile("/\\*.*\\*/")))
			.add(new Terminal("ident", Pattern.compile("[$a-zA-Z_][$a-zA-Z0-9_]*")))
			.add(new Terminal("string", Pattern.compile("\"([^\"\\\\]*(\\\\.[^\"\\\\]*)*)\"|'([^'\\\\]*(\\\\.[^'\\\\]*)*)'"), s -> EscapeSequencer.JAVA.unescape(s.substring(1, s.length() - 1))))
			.add(new Terminal("double", Pattern.compile("((\\s-)?(([0-9]\\.[0-9]*|\\.[0-9]+))([Ee]-?[0-9]+)?|([0-9]+[Ee]-?[0-9]+))"), String::trim))
			.add(new Terminal("integer", Pattern.compile("(\\s-)?([0-9]+|0x[0-9a-fA-F]+)"), String::trim))
			.add(new Terminal("operator", Pattern.compile("[" + Pattern.quote("<>+-/%*~^&|=`!?:") + "]+")));
	
	static final TokenNesting NESTING = new TokenNesting()
			.add(term("{"), term("}"))
			.add(term("("), term(")"))
			.add(term("["), term("]"));
	
	static Terminal term(String id)
	{
		return TERMS.get(id);
	}
	
	static IScanner<String> token(String id)
	{
		Terminal terminal = term(id);
		return Scanners.term(terminal);
	}
	
	static SymbolScanner symbol(String symbol)
	{
		return new SymbolScanner(symbol);
	}
	
	static IScanner<?> sameLine = Scanners.newline().not();
	
	private final TypeContext context;
	
	public LensInterpreter(TypeContext context)
	{
		this.context = context;
	}
	
	public TypeContext getContext()
	{
		return context;
	}
	
	public IScanner<LensExpression> entryPointExp()
	{
		return Scanners.or(
				valueExp.map(ReturnExpression::new),
				multiExp.map(array -> array.length == 1 ? array[0] : new MultiExpression(array))).complete();
	}
	
	IScanner<LensExpression> statementExp = Scanners.lazy("statement", () -> Scanners.or(
			conditionalExp(false),
			whileExp(),
			returnExp(),
			varExp(),
			defExp(),
			implicitDefExp(),
			oprDefExp(),
			usingExp(false),
			throwExp(),
			tryCatchExp(false),
			asyncExp(false),
			oprStatementExp(),
			importExp(),
			typeDefExp(),
			pathExp(false)));
	
	IScanner<LensExpression> basicValueExp = Scanners.lazy("value", () -> Scanners.or(
			lambdaExp(),
			conditionalExp(true),
			usingExp(true),
			throwExp(),
			tryCatchExp(true),
			asyncExp(true),
			implicitDefExp(),
			pathExp(true),
			contextExp()));
	
	IScanner<LensExpression> valueExp = valueExp(0);
	
	IScanner<LensExpression> valueExp(int precedence)
	{
		return complexValueExp(basicValueExp, precedence);
	}
	
	IScanner<LensType> basicType = Scanners.lazy("type", () -> Scanners.or(
			tupleType(),
			voidType(),
			namedType(),
			asyncType(),
			constraintType(),
			repeatedType()));
	
	IScanner<LensType> type;
	
	{
		type = basicType.reduceForward(Scanners.between(token("["), sizeConstraint().opt().orElse(AlwaysSizeConstraint.INSTANCE), token("]")).rep(), ListType::new);
		type = Scanners.or(type.optWrap(symbol(">").next(type.opt().orElse(Lens.ANY)), FunctionType::new), symbol(">").always(Lens.ANY).seq(type).map(FunctionType::new));
		type = type.reduceForward(symbol("&").or(symbol("|")).seq(type).rep(), (a, kv) -> kv.getA().getData().equals("&")
				? new LogicAndType(a, kv.getB())
				: new LogicOrType(a, kv.getB()));
	}
	
	IScanner<LensExpression> targetExp = Scanners.or(
			invokeExp(),
			identExp(),
			literalExp(),
			tupleExp(),
			listExp(),
			mapExp(),
			typeValueExp(),
			staticExp(),
			oprFuncExp(),
			thisExp(),
			newExp(),
			undefinedExp());
	
	IScanner<LensExpression> complexValueExp(IScanner<LensExpression> valueScan, int precedence)
	{
		return Scanners.build("value", lex -> {
			LensExpression exp;
			if(term("operator").isType(lex.next()))
			{
				exp = new OperatorExpression(lex.consumeData(), OperatorPosition.PREFIX, valueScan.scan(lex));
			}
			else
			{
				exp = valueScan.scan(lex);
			}
			
			if(lex.optSkip(term("is")))
			{
				exp = new TypeCheckExpression(exp, type.scan(lex));
			}
			else if(lex.optSkip(term("as")))
			{
				exp = new CastExpression(type.scan(lex), exp);
			}
			
			while(lex.hasNext() && !lex.isNewline() && term("operator").isType(lex.next()))
			{
				String op = lex.next().getData();
				int prec = LensOperator.getSymbolPrecedence(op);
				
				LexSequence copy = lex.copy();
				IScanResult<LensExpression> nextScan = symbol(op).next(Scanners.or(Scanners.newline(), token("operator")).not()).next(valueExp(prec)).opt().scan(copy);
				
				if(!nextScan.success())
				{
					// postfix ambiguity resolver
					if(lex.get(1).getType() == term("operator"))
					{
						int p = LensOperator.getSymbolPrecedence(lex.get(1).getData());
						if(p >= prec)
						{
							if(prec > precedence)
							{
								lex.skip();
								exp = new OperatorExpression(op, exp, valueExp(prec).scan(lex));
								continue;
							}
							else
							{
								break;
							}
						}
					}
					
					lex.skip();
					exp = new OperatorExpression(op, OperatorPosition.POSTFIX, exp);
				}
				else if(prec > precedence)
				{
					lex.update(copy);
					exp = new OperatorExpression(op, exp, nextScan.get());
				}
				else
				{
					break;
				}
			}
			return exp;
		});
	}
	
	IScanner<LensValue> literalValue()
	{
		return Scanners.or(
				token("string").map(StringValue::new),
				token("true").always(Lens.TRUE), token("false").always(Lens.FALSE),
				token("null").always(Lens.NULL), token("undefined").always(Lens.UNDEFINED),
				token("integer").map(Lens.INT::parse), token("double").map(Lens.DOUBLE::parse));
	}
	
	IScanner<LensExpression> literalExp()
	{
		return literalValue().map(LiteralExpression::new);
	}
	
	IScanner<LensExpression[]> multiExp = statementExp.skip(token(";").rep()).sep(Scanners.newline()).array(LensExpression[]::new);
	
	IScanner<LensExpression> blockExp(LensType type)
	{
		return Scanners.between(token("{"), new LensInterpreter(getContext().createChild()).multiExp, token("}")).map(array -> new BlockBaseExpression(type, array));
	}
	
	IScanner<LensExpression> blockExp()
	{
		return Scanners.between(token("{"), new LensInterpreter(getContext().createChild()).multiExp, token("}")).map(BlockExpression::new);
	}
	
	IScanner<LensExpression> oprStatementExp()
	{
		IScanner<IScanResult<LensExpression>> pathScan = pathExp(true).skip(sameLine).opt();
		IScanner<String> symbolScan = token("operator");
		IScanner<LensExpression> targetScan = valueExp;
		return Scanners.build("OprStatement", lex -> {
			IScanResult<LensExpression> pointer = pathScan.scan(lex);
			String symbol = symbolScan.scan(lex);
			IScanResult<LensExpression> target = (pointer.success() && symbol.endsWith("=") ? targetScan : sameLine.next(targetScan)).opt().scan(lex);
			lex.expect(pointer.success() || target.success(), "No operands present");
			boolean a = pointer.success(), b = target.success();
			return new OperatorExpression(symbol,
					OperatorPosition.from(a, b),
					(a ? pointer : target).get(),
					target.orElse(EmptyExpression.INSTANCE));
		});
	}
	
	// IScanner<LensExpression> assertExp()
	// {
	// return token("assert").next(valueExp.or(blockExp(Lens.BOOL)))
	// .seq(symbol("else").next(valueExp).opt().orElse(null)).map(AssertExpression::new);
	// }
	
	IScanner<LensExpression> returnExp()
	{
		return token("return").next(Scanners.or(
				Scanners.newline().always(EmptyExpression.INSTANCE),
				valueExp.opt().orElse(EmptyExpression.INSTANCE))).map(ReturnExpression::new);
	}
	
	IScanner<LensExpression> usingExp(boolean value)
	{
		return token("using").next(identPropExp().sep1(token(",")).<LensExpression>map(MapExpression::new).or(valueExp).opt().seq(value ? valueExp : blockExp())
				.<LensExpression>map((target, block) -> target.success() ? new UsingExpression(target.get(), block) : new BlockExpression(new LensExpression[] {block})).certain());
	}
	
	IScanner<LensExpression> importExp()
	{
		return token("import").next(token("string").or(token("ident").sep1(symbol(".")).map(list -> String.join(".", list))).seq(token("as").next(token("ident")).opt())
				.<LensExpression>map((path, name) -> new ImportExpression(path, name.orElse(path.substring(path.lastIndexOf('.') + 1)), getContext())));
	}
	
	IScanner<LensExpression> typeDefExp()
	{
		return token("type").next(token("ident")).map(list -> String.join(".", list)).skip(symbol("=")).seq(type)
				.map((name, type) -> new TypeDefExpression(name, type, getContext()));
	}
	
	IScanner<LensExpression> pathExp(boolean value)
	{
		IScanner<LensExpression> pathScan = Scanners.build("PathExp", lex -> {
			LensExpression target = targetExp.scan(lex);
			
			IScanResult<LensExpression> post;
			while(!lex.isNewline() && (post = sameLine.next(Scanners.or(indexerExp(target), curryExp(target))).opt().scan(lex)).success())
			{
				target = post.get();
			}
			
			return lex.optSkip(term(".")) ? new PathExpression(target, pathExp(true).scan(lex)) : target;
		});
		
		pathScan = pathScan.optNext(this::matchExp);
		pathScan = pathScan.optWrap(sameLine.next(lambdaExp()), InvokeExpression::new);
		
		return pathScan;
	}
	
	IScanner<LensExpression> curryExp(LensExpression target)
	{
		return tupleExp().map(args -> new InvokeExpression(target, args));
	}
	
	IScanner<LensExpression> identExp()
	{
		return Scanners.or(token("ident").map(IdentifierExpression::new), token("#").always(AnonymousExpression.INSTANCE));
	}
	
	IScanner<LensExpression> thisExp()
	{
		return token("this").always(ThisExpression.INSTANCE);
	}
	
	IScanner<LensExpression> indexerExp(LensExpression target)
	{
		return Scanners.between(token("["), valueExp, token("]")).map(value -> new IndexerExpression(target, value));
	}
	
	IScanner<LensExpression> tupleExp()
	{
		return Scanners.between(token("("), valueExp.sep1(token(",")), token(")"))
				.map(list -> list.size() == 1 ? list.get(0) : new TupleExpression(list.toArray(new LensExpression[list.size()])));
	}
	
	IScanner<LensExpression> undefinedExp()
	{
		return Scanners.seq(token("("), token(")")).always(EmptyExpression.INSTANCE);
	}
	
	IScanner<LensExpression> lambdaExp()
	{
		IScanner<LensParam> paramScan = parameter().opt().orElse(AnonymousParam.ANY);
		IScanner<IScanResult<LensType>> typeScan = token(":").next(type).opt();
		IScanner<LensExpression> bodyScan = Scanners.or(blockExp(Lens.ANY), valueExp);
		return paramScan.skip(token("=>")).seq(typeScan.next(type -> type.success() ? bodyScan.map(body -> new CastExpression(type.get(), body)) : bodyScan).certain()).map(LambdaExpression::new);
	}
	
	IScanner<LensExpression> mapExp()
	{
		IScanner<LensExpression> propScan = propertyExp();
		IScanner<LensExpression> mapScan = Scanners.between(token("{"), propScan.sep(Scanners.or(token(","), token(";"), Scanners.newline())), token("}")).map(MapExpression::new);
		return token("new").opt().next(mapScan);
	}
	
	IScanner<LensExpression> propertyExp()
	{
		return Scanners.or(
				identPropExp(),
				defPropExp(),
				typeDefPropExp());
	}
	
	IScanner<LensExpression> identPropExp()
	{
		return identExp().skip(symbol(":")).seq(valueExp.certain()).map(AssignExpression::new);
	}
	
	IScanner<LensExpression> defPropExp()
	{
		return defExp();
	}
	
	IScanner<LensExpression> typeDefPropExp()
	{
		return token("type").next(token("ident")).skip(symbol(":")).seq(type.certain())
				.map((name, type) -> new TypeDefExpression(name, type, getContext()));
	}
	
	IScanner<LensExpression> listExp()
	{
		return Scanners.between(token("["), valueExp.sep(Scanners.or(token(","), Scanners.newline()).opt()).array(LensExpression[]::new), token("]")).map(ListExpression::new);
	}
	
	IScanner<LensExpression> invokeExp()
	{
		IScanner<LensExpression> targetScan = identExp().or(thisExp()).skip(sameLine);
		IScanner<LensExpression> tupleScan = Scanners.or(tupleExp(), undefinedExp());
		return targetScan.seq(tupleScan).map(InvokeExpression::new);
	}
	
	IScanner<LensExpression> conditionalExp(boolean value)
	{
		IScanner<LensExpression> conditionScan = valueExp.between(token("("), token(")"));
		IScanner<LensExpression> valueScan = value ? valueExp : statementExp.or(blockExp());
		return token("if").next(Scanners.<LensExpression>build("ConditionalExp", lex -> {
			LensExpression condition = conditionScan.scan(lex);
			LensExpression ifValue = valueScan.scan(lex);
			if(lex.optSkip(term("else")))
			{
				return new IfElseExpression(condition, ifValue, valueScan.scan(lex));
			}
			else
			{
				return new IfExpression(condition, ifValue);
			}
		}));
	}
	
	IScanner<LensExpression> whileExp()
	{
		IScanner<LensExpression> valueScan = valueExp.between(token("("), token(")"));
		IScanner<LensExpression> statementScan = statementExp.or(blockExp()).opt().orElse(EmptyExpression.INSTANCE);
		return token("while").next(valueScan.seq(statementScan).map(WhileExpression::new));
	}
	
	IScanner<LensExpression> varExp()
	{
		return token("var").next(token("ident").seq(symbol("=").next(valueExp).opt().orElse(EmptyExpression.INSTANCE)).sep1(token(","))
				.map(list -> new MultiExpression(CollectionHelpers.map(list, ab -> new VariableExpression(ab.getA(), ab.getB()), LensExpression[]::new))));
	}
	
	IScanner<LensExpression> defExp()
	{
		IScanner<LensParam> paramScan = sameLine.next(tupleParam());
		IScanner<IScanResult<LensType>> typeScan = token(":").next(type).opt();
		return token("def").next(Scanners.<LensExpression>build("DefExp", lex -> {
			String name = lex.consumeData(term("ident"));
			
			LensParam param = paramScan.opt().orElse(null).scan(lex);
			List<LensParam> paramCurry = new ArrayList<>();
			while(term("(").isType(lex.next()))
			{
				paramCurry.add(paramScan.scan(lex));
			}
			
			IScanResult<LensType> explicitType = typeScan.scan(lex);
			LensType type = explicitType.orElse(Lens.ANY);
			LensExpression body = Scanners.or(
					symbol("=").next(valueExp),
					symbol("=").next(identExp().opt().orElse(AnonymousExpression.INSTANCE)).next(this::matchExp),
					symbol("=").opt().next(blockExp(type).certain())).scan(lex);
			
			for(int i = paramCurry.size() - 1; i >= 0; i--)
			{
				LensParam p = paramCurry.get(i);
				type = new FunctionType(p.getType(), type);
				body = new LambdaExpression(p, body);
			}
			
			return new FunctionExpression(name, param != null ? param : AnonymousParam.ANY, explicitType.success() && paramCurry.isEmpty() ? new CastExpression(explicitType.get(), body) : body);
		}));
	}
	
	IScanner<LensExpression> implicitDefExp()
	{
		IScanner<LensParam> paramScan = sameLine.next(tupleParam());
		IScanner<IScanResult<LensType>> typeScan = token(":").next(type).opt();
		return Scanners.<LensExpression>build("ImplicitDefExp", lex -> {
			String name = lex.consumeData(term("ident"));
			
			LensParam param = paramScan.scan(lex);
			List<LensParam> paramCurry = new ArrayList<>();
			while(term("(").isType(lex.next()))
			{
				paramCurry.add(paramScan.scan(lex));
			}
			
			IScanResult<LensType> explicitType = typeScan.scan(lex);
			LensType type = explicitType.orElse(Lens.ANY);
			LensExpression body = Scanners.or(
					symbol("=").next(valueExp))
					.scan(lex);
			
			for(int i = paramCurry.size() - 1; i >= 0; i--)
			{
				LensParam p = paramCurry.get(i);
				type = new FunctionType(p.getType(), type);
				body = new LambdaExpression(p, body);
			}
			
			return new AssignExpression(new IdentifierExpression(name), new LambdaExpression(param, explicitType.success() && paramCurry.isEmpty() ? new CastExpression(explicitType.get(), body) : body));
		});
	}
	
	IScanner<LensExpression> oprDefExp()
	{
		IScanner<LensType> typeScan = type;
		return token("opr").next(Scanners.<LensExpression>build("OperatorDefExp", lex -> {
			lex.skip(term("("));
			IScanResult<LensType> a = typeScan.opt().scan(lex);
			String symbol = lex.consumeData(term("operator"));
			IScanResult<LensType> b = typeScan.opt().scan(lex);
			lex.skip(term(")"));
			
			lex.expect(a.success() || b.success(), "No parameters given for operator definition: " + symbol);
			
			LensType type = symbol(":").next(typeScan).opt().scan(lex).orElse(Lens.ANY);
			LensExpression body = Scanners.or(symbol("=").next(valueExp)).scan(lex);
			
			return new OperatorDefExpression(symbol, OperatorPosition.from(a.success(), b.success()), a.orElse(Lens.VOID), b.orElse(Lens.VOID), type, body);
		}).certain());
	}
	
	IScanner<LensExpression> newExp()
	{
		return token("new").next(namedType().skip(sameLine).seq(tupleExp().or(undefinedExp())).map(NewExpression::new));
	}
	
	IScanner<LensExpression> asyncExp(boolean value)
	{
		return token("async").next(((value ? valueExp : statementExp).or(blockExp(Lens.ANY))).certain())
				.map(AsyncExpression::new);
	}
	
	IScanner<LensExpression> matchExp(LensExpression target)
	{
		IScanner<MatchCase> caseScan = token("case").next(Scanners.or(parameter(), literalValue().map(value -> new LiteralParam(value, getContext().getEnv()))).sep1(token(",")).singletonOrElse(list -> new TupleParam(list.toArray(new LensParam[list.size()])))).skip(token("=>")).seq(valueExp.or(blockExp()))
				.map((param, result) -> new MatchCase(param, result));
		return token("match").next(Scanners.between(token("{"), caseScan.rep1().array(MatchCase[]::new), token("}"))
				.<LensExpression>map(cases -> new MatchExpression(target, cases)).certain());
	}
	
	IScanner<LensExpression> tryCatchExp(boolean value)
	{
		IScanner<LensExpression> bodyScan = value ? valueExp : statementExp.or(blockExp().certain());
		IScanner<CatchCase> catchScan = token("catch").next(tupleParam().opt().orElse(AnonymousParam.ANY)).seq(bodyScan).map(CatchCase::new);
		return token("try").next(bodyScan).seq(catchScan.rep().array(CatchCase[]::new)).map(TryCatchExpression::new);
	}
	
	IScanner<LensExpression> contextExp()
	{
		return token("@").always(ContextExpression.INSTANCE);
	}
	
	IScanner<LensExpression> throwExp()
	{
		return token("throw").next(valueExp).map(ThrowExpression::new);
	}
	
	IScanner<LensExpression> typeValueExp()
	{
		return token("type").next(Scanners.between(token("("), type.sep1(token(",")).array(LensType[]::new).map(TupleType::get).map(TupleType::get), token(")")))
				.map(TypeExpression::new);
	}
	
	IScanner<LensExpression> staticExp()
	{
		return token("static").next(type.sep1(token(",")).array(LensType[]::new).map(TupleType::get).map(TupleType::get)
				.between(token("("), token(")")))
				.map(StaticExpression::new);
	}
	
	IScanner<LensExpression> oprFuncExp()
	{
		IScanner<LensType> typeScan = type;
		return token("opr").next(Scanners.<LensExpression>build("OpreratorFunc", lex -> {
			lex.skip(term("("));
			IScanResult<LensType> a = typeScan.opt().scan(lex);
			String symbol = lex.consumeData(term("operator"));
			IScanResult<LensType> b = typeScan.opt().scan(lex);
			lex.skip(term(")"));
			lex.expect(a.success() || b.success(), "No parameters given for operator reference: " + symbol);
			return new OperatorHandleExpression(symbol, OperatorPosition.from(a.success(), b.success()), a.orElse(Lens.VOID), b.orElse(Lens.VOID));
		}).certain());
	}
	
	IScanner<SizeConstraint> sizeConstraint()
	{
		IScanner<Integer> intScan = token("integer").map(Integer::parseInt);
		return Scanners.or(
				intScan.opt().orElse(1).skip(symbol("+")).map(MinSizeConstraint::new),
				intScan.map(ValueSizeConstraint::new));
	}
	
	IScanner<LensType> tupleType()
	{
		return Scanners.between(token("("), type.sep1(token(",")).array(LensType[]::new), token(")")).map(TupleType::get);
	}
	
	IScanner<LensType> voidType()
	{
		return Scanners.seq(token("("), token(")")).always(Lens.VOID);
	}
	
	IScanner<LensType> repeatedType()
	{
		return token("integer").map(Integer::parseInt).filter(n -> n > 1, "Tuple length cannot be {result}")
				.seq(basicType).map((n, type) -> {
					LensType[] array = new LensType[n];
					for(int i = 0; i < n; i++)
					{
						array[i] = type;
					}
					return new TupleType(array);
				});
	}
	
	IScanner<LensType> constraintType()
	{
		return Scanners.between(token("{"), token("ident").seq(symbol(":").next(type).opt().orElse(Lens.VAL)).sep1(symbol(",")), token("}"))
				.map(list -> new ConstraintType(list.stream().collect(Collectors.toMap(ScanPair::getA, ScanPair::getB))));
	}
	
	IScanner<LensType> asyncType()
	{
		return token("async").next(type).map(AsyncType::new);
	}
	
	IScanner<LensType> namedType()
	{
		return token("ident").sep1(token(".")).map(list -> String.join(".", list)).map(id -> getContext().getType(id))
				.optWrap(symbol("?"), OptionalType::new);
	}
	
	IScanner<LensParam> parameter()
	{
		return Scanners.lazy("param", () -> Scanners.or(tupleParam(), anonymousParam(), namedParam()));
	}
	
	IScanner<LensParam> anonymousParam()
	{
		return symbol("#").skip(sameLine).next(type.opt().orElse(Lens.ANY)).map(AnonymousParam::new);
	}
	
	IScanner<LensParam> namedParam()
	{
		IScanner<LensType> typeScan = token(":").next(type.certain()).opt().orElse(Lens.ANY);
		return token("ident").skip(sameLine).seq(typeScan).map(NamedParam::new);
	}
	
	IScanner<LensParam> tupleParam()
	{
		return Scanners.between(token("("), parameter().sep(token(",")).array(LensParam[]::new), token(")")).map(TupleParam::get);
	}
}
