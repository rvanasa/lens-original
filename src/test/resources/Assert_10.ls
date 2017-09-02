var time = timestamp()
var failedTests = []

def test(name: str)(action: >bool?)
{
	var time = timestamp()
	
	var result = action() ? true
	var display = " " + name + " [" + (timestamp() - time) + " ms]"
	print((if(result) "+" else "-") + display)
	if(!result) failedTests |+ action
}

test("empty test") => ()

test("tuple values") =>
{
	var tuple = (1, 2, (3, 4))
	assert(size(tuple) -> size(tuple[2]) == 3 -> 2)
	assert(tuple == (1, 2, 3 -> 4))
	assert((1 -> 2 -> 3) == ((1, 2), 3))
	
	assert(5 == (5))
	
	assert((1, 2) as 2(int) == (1, 2))
	assert((1 <> 2) as 2(int) == (1, 2))
	
	assert((() % a => a + "/") == "undefined/")
	assert(((1 <> 3) % => # :: 4) == (1 <> 4))
	assert(((1 <> 3) % (a, b, c) => a + b + c) == 6)
	
	assert(((), (), ((), ())) is void)
	assert(((), (), ((), ())) != ())
	
	var anchor = 0
}

test("list values") =>
{
	assert([] == [])
	assert([5] == [5])
	assert([5] == 5)
	
	var list = [1, [2], 3 -> 3, (4)] :: (5, 6) :: 7 <> 8 :: 9 << 11
	assert(list == [1, [2], (3, 3), 4, 5, 6, 7, 8, 9, 10])
	assert(size(list) == 10)
	assert(list is (int | int[] | (int, int))[10])
	assert(list is val[5+])
	
	assert([[1, 2, 3], [4, 5, 6]] is int[3][2])
	
	assert(!([(), ((), ())] is void))
	assert(!([] is void))
	assert([] != ())
	
	assert((1, 2, 3) as int[2+] == [1, 2, 3])
	assert((1 <> 3) as int[2+] == [1, 2, 3])
}

test("map values") =>
{
	var map = {
		a: 1
		b: {a: 2, c: ["a", "b"]}
		def supplier = 5
		def action(a: num) = a + 1
	}
	
	assert(map["a"] == map.a)
	assert(map.b.a == 2)
	assert(map[map["b"]["c"][0]] == 1)
	assert(map.c ? 0 == 0)
	assert(map.supplier() == 5)
	assert(map.action(9) == 10)
}

test("collection operations") =>
{
	assert((1, 2, 3) == [1, 2, 3])
	assert([1, 2.0, "3"] is (int, double, str))
	assert((1 <> 3) is (num, num, num))
	
	var list = (1, 2, 3) :: 4 <> 10 ^ {n: t => # + t}
	assert(size(list) == 10)
	assert(list[0]["n"](1) == 2)
	
	assert(([] ^ 5) == [])
	assert((undefined ^ 5) == ())
	assert((list.n ^ #(0)) == (1 <> 10))
	assert(list.x == ())
	
	assert(reverse(0 << 3) == (2 <> 0))
	
	assert(((1, 2, 4) ~ # > 2) == [4])
	
	list = 1 <> 5
	var collect = []
	foreach(list) n => collect |+ n
	
	collect +| 0
	assert(collect == (0 :: list))
	
	var z = zip(1 <> 4, (1, 2, 3))
	assert(size(z) == 4)
	foreach(z) =>
	{
		assert(#[0] == #[1] ? 4)
	}
	
	assert(reduce(z ^ ((a, b) => (a, b?0)) ^ opr(num * num), 0, opr(num + num)) == 1 + 4 + 9)
	
	assert((1 <> 3 ^^ (a, b) => a + b) == 1 + 2 + 3)
	assert(([5] ^^ (a, b) => a + b) == 5)
}

test("# value precedence") =>
{
	def action(#int, (#int, #str)) = (# * #, # + 1)
	assert(action(2, (3, 4)) == (6, "41"))
}

test("using statements") =>
{
	var a = {action: (s: str) => "|" + s + "|"}
	using a
	{
		assert(this == a)
		assert(action(":") == "|:|")
		
		using value: a, action: a.action
		{
			assert(value == a)
			assert(action == value.action)
		}
	}
}

test("variable/assignment scope") =>
{
	def a() = 5
	using {
		a = () => 3
		assert(a() == 3)
		
		var a = 2
		assert(a == 2)
	}
	assert(a() == 3)
	
	var outside = 0
	var map = {abcd: 0}
	using map
	{
		abcd = 5
		var b = 5
		
		outside = 1
	}
	
	assert(map.abcd == 5)
	assert(map.b == ())
	assert(outside == 1)
}

test("this scope") =>
{
	var t = this
	data = func()
	def func()
	{
		assert(this == t)
		return {a: 1, b: 2}
	}
	using data
	{
		assert((this, a, b) == (data, 1, 2))
	}
}

test("block/return scope") =>
{
	def squoteData(block: str>): str = data = block("'" + data + "'")
	
	var result, data = "data string"
	assert(result == ())
	
	squoteData =>
	{
		result = #
		assert(result == #)
		return "!" + result + "!"
	}
	
	assert(result == "'data string'")
	assert(data == "!'data string'!")
	
	using result
	{
		return
	}
	
	assert(false)
}

test("numeric operations") =>
{
	assert(1.0 is int)
	assert(!(1.5 is int))
	
	assert(1 != "1")
	assert(1 == 1.0)
	assert(.5 == 0.5)
	assert(1.0 != 1.001)
	assert(1 > 0)
	assert(0 < 1)
	assert(1 >= 1 && 1 >= 0)
	assert(0 <= 1 && 1 <= 1)
	
	assert(1-2+10 == 9)
	assert(1-(2+10) == -11)
	assert(1 + 2 * 10 == 21)
	assert((1 + 2) * 10 == 30)
	assert(1 * 2 + 10 == 12)
	assert(1 * -0.5 == 0 - 0.5)
	assert(4 ** -.5 == 1/2)
	
	assert(1 + - 2 == -1)
	assert(- 2 + 1 == -1)
	assert(1 - - 2 == 3)
	
	assert(1 / 2 == 0.5)
	assert((2 / 1) as str == "2")
	assert((1.0 + 1) as str == "2.0")
	assert((2 ** 3.0) as str == "8")
	assert((1 as double) as str == "1.0")
	
	assert(5 % 2 == 1)
	assert(5 % -2 == 1)
	assert(5 % 0 == undefined)
}

test("plus operator") =>
{
	assert(1 + 1 == 2)
	assert(1.5 + "1" == "1.51")
	assert("1" + 1.5 == "11.5")
	assert("1" as int + 1 == 2)
	assert(1 + 2 as str == "12")
}

test("incremental operators") =>
{
	var i = 1, j = 2
	i += 4
	assert(i == 5)
	
	i -= j - 1
	assert(i == 4)
	
	var s = "3"
	i += s
	assert(i == "43")
	
	var list = 1 <> 5
	list ^= 1.0 / #
	assert(list == [1, 1/2, 1/3, 1/4, 1/5])
	
	i = 0
	assert(++i == 1 && i++ == 1)
	assert(i == 2)
	i--
	--i
	assert(i == 0)
}

test("boolean logic/evaluation") =>
{
	var a = true
	var b = 1
	
	assert(a || b++)
	assert(b == 1)
	
	assert(!(!a && b++))
	assert(b == 1)
	
	assert(false && true || true)
	assert(!(true || false && false))
}

test("optional values") =>
{
	assert(1 == () ? 1)
	assert(() ? 1 ? 2 + 3 == 4)
	assert(((), 1)? == true)
	
	def optReturn(a: int?): int?
	{
		if(a?)
		{
			return a
		}
	}
	
	assert(optReturn() ? 1 == 1)
	assert(optReturn(1) ? 2 == 1)
}

test("type constraints") =>
{
	assert(type(int | str) == type(int | str))
	assert(type(int | str) == type(str | int))
	assert(type(int | str) != type(str | str))
	
	assert(type(int & str) == type(int & str))
	assert(type(int & str) == type(str & int))
	assert(type(int & str) != type(str & str))
	
	assert(type(str) == type(str | str))
	assert(type(str) == type(str & str))
	
	type T = int | str & double
	assert(type(T) == type(double & (int | str)))
	assert(type(T) != type(int | (str & double)))
	
	assert(1 is T)
	assert(1.0 is T)
	assert(!("abc" is T || 5.5 is T))
	
	assert(type(2 num) == type(num, num))
}

test("Java integration") =>
{
	import java.lang.Math
	
	assert(Math == static(Math))
	assert(Math == static(java.lang.Math))
	
	assert(Math.floor(1.5) == 1)
	
	assert(Lens.expf == expf)
	
	var exp = expf(=> abcdef(2 + x * 3))
	
	assert(exp.getClass().getSimpleName() == "InvokeExpression")
}

test("pattern matching") =>
{
	var value = (1, [2, 3] -> 4)
	var result = value match
	{
		case a, b, c => {assert(false)}
		case a, (b, c) => a :: b :: c
	}
	
	assert(result == (1 <> 4))
	
	def s: str? = # match
	{
		case 1 => "one"
		case 2 => "two"
		case "3" => "three"
		case #str => ":" + # as str + ":"
		case d: double, s: str => d as int + s
		case # => ()
	}
	
	assert(s(1) == "one")
	assert(s(2) == "two")
	assert(s(3) == ())
	assert(s("3") == "three")
	assert(s("abc") == ":abc:")
	assert(s(5.5, "abc") == "5abc")
	assert(s(5, "abc") == "5abc")
	assert(s("abc", 5) == ())
}

test("function definitions") =>
{
	def func1 = 5
	
	assert(func1 != 5)
	assert(func1() == 5)
	
	assert(func2("1") == 1)
	def func2(a: int) = a
	
	def func3(a: int)(b: int)(c: 2(int) > int) = a + b + c(a, b)
	assert(func3(1)(2)((a, b) => a * b) == 5)
	
	def plus(a, b) = a + b
	
	assert(plus("abc", "defg") == "abcdefg")
	assert(plus(1, 2) == 3)
	
	var a = 1, b = 2
	assert(plus(a + 2, plus(b, b) - b) == 5)
	
	f(x) = 2 * x + 1
	assert(f(5) == 11)
	
	f1() = true
	f2(a, b) = 0
	
	assert(f1())
	
	(a, b) % f2
}

test("statements/expressions") =>
{
	def test1(#num) =
		if(# == 1) 2
		else if(# == 2) (3, 4)
		else 5
	
	assert(test1(0) == 5)
	assert(test1(1) == 2)
	assert(test1(2) == 3 -> 4)
	assert(test1(3) == 5)
	
	var flag = false
	
	def test2
	{
		if(flag) return "t"
		else return "f"
	}
	
	assert(test2() == "f")
	flag = true
	assert(test2() == "t")
	
	var list = []
	var ct = 0
	while(ct < 10)
	{
		list |+ ct++
	}
	assert(list == (0 << 10))
	
	flag = false
	var a = 0, b = 0
	(if(flag) a else b) = 1
	assert((a, b) == (0, 1))
	
	def f1 = # + 1
	def f2 = # * 2
	def f3 = # == -12
	
	assert((f1 +> f2 +> t => f3(-t))(5))
}

test("path closures") =>
{
	map = {a: 1, b: 2, f: (n: int) => n + 1}
	
	assert(map.(v ? v ? a) == 1)
	assert(map.(v ? g) == ())
	
	map.(c = a + b)
	
	assert(map.c == 3)
	
	assert(map.(f(map.a * 2)) == 3)
}

test("exception handling") =>
{
	var flag = false
	
	try
	{
		assert(true)
		assert(false)
	}
	catch(#str) if(# == "net.rvanasa.lens.exception.LensAssertionException: Assertion failed: `false`") flag = true
	
	assert(flag)
	
	def errHandle(x)
	{
		var n = 3
		return try throw x
			catch(e: str) x == "abc"
			catch(#int) # == n
			catch(a, b) a == b
			catch # == (0, 0, 0)
	}
	
	assert(errHandle("abc"))
	assert(errHandle(3))
	assert(errHandle(5, 5))
	assert(!errHandle(5))
	assert(errHandle(0, 0, 0))
	assert(!errHandle(0, 0, 1))
	
	assert((try abcd.efgh catch 5) == 5)
}

test("async values") =>
{
	var time = timestamp()
	var delay = async
	{
		sleep(0.2)
		return [0, 1, {key: "value"}]
	}
	
	var dProp = delay[1]
	
	delay[1] = "testReplace"
	
	delay =>
	{
		assert(timestamp() - time > 100 && timestamp() - time < 300)
		assert(# == [0, "testReplace", {key: "value"}])
	}
	
	await(delay, dProp)((a, b) =>
	{
		assert(size(a) == 3)
	})
}

test("text formatting") =>
{
	var a = 1, b = 2
	
	var text = "$a | $b" % format
	assert(text == "1 | 2")
}

test("context values") =>
{
	var ctxt = @
	assert(@ == ctxt)
	
	var a = 123
	
	assert(ctxt.a == 123)
	
	var b = 456, c = "efg"
	
	def useContext(c: context)
	{
		assert(c.(a, b, c) == (123, 456, 'efg'))
	}
	
	useContext(ctxt)
}

test("command notation") =>
{
	f(x: int) = x + 1
	
	var s = f(4 + 3)
	
	assert(s == 8)
}

if(!empty(failedTests)) print("Failed tests: " + failedTests)
else print("All tests completed successfully [" + (timestamp() - time) + " ms]")
