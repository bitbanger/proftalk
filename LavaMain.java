import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import edu.rit.pj.*;
import edu.rit.pj.reduction.*;

public class LavaMain {
	public static Env global_env;
	
	public static int c2e = 0;
	
	public static boolean debug = false;

	public static ConcurrentLinkedQueue<EvalJob> thingsToDo;
	
	public static Object runOp(Env e, String opName, Object... args) {
		Lambda op = (Lambda)e.get(opName);
		
		return op.exec(args);
	}
	
	public static Object atom(String a) {
		try {
			return Integer.parseInt(a);
		} catch(NumberFormatException nfe) {
			return a;
		}
	}
	
	public static LinkedList<String> tokenize(String str) {
		LinkedList<String> tokens = new LinkedList<String>();
		String[] toks = str.replaceAll("\\(", " ( ").replaceAll("\\)", " ) ").trim().split("\\s+");
		
		for(String tok : toks) {
			tokens.add(tok);
		}
		
		return tokens;
	}
	
	public static Object parseSexp(LinkedList<String> tokens) {
		String token = tokens.pop();
		
		if(token.equals("(")) {
			LavaList sexp = new LavaList();
			while(!(tokens.get(0).equals(")"))) {
				sexp.add(parseSexp(tokens));
			}
			tokens.pop();
			return sexp;
		} else if(token.equals(")")) {
			System.err.println("Unexpected )");
			return null;
		} else {
			return atom(token);
		}
	}
	
	public static Object parseString(String str) {
		return null;
	}
	
	public static int fib(int n) {
		if(n < 2) {
			return n;
		} else {
			return fib(n-1) + fib(n-2);
		}
	}
	
	public static EvalJob eval(Object exp, Env ee) {
		++c2e;
		
		final Env e = ee;
		if(exp instanceof String) {
			final Object fExp = exp;
			Lambda l = new Lambda()
			{
				public Object exec(Object... args)
				{
					return e.find((String)fExp).get(fExp);
				}
			};
			EvalJob pair = new EvalJob(l);
			thingsToDo.add(pair);
			return pair;
		} else if(!(exp instanceof LinkedList<?>)) {
			final Object fExp = exp;
			Lambda l = new Lambda()
			{
				public Object exec(Object... args)
				{
					return fExp;
				}
			};
			EvalJob pair = new EvalJob(l);
			thingsToDo.add(pair);
			return pair;
		}else {
			LavaList llexp = (LavaList)exp;
			Object car = llexp.get(0);
			
			if(car.equals("insofaras")) {
				final Object test = llexp.get(1);
				final Object conseq = llexp.get(2);
				final Object alt = llexp.get(3);

				Lambda testL = new Lambda()
				{
					public Object exec(Object... args)
					{
						return eval(test, e);
					}
				};
	
				final EvalJob testPair = new EvalJob(testL);
				thingsToDo.add(testPair);

				Lambda l = new Lambda()
				{
					public Object exec(Object... args)
					{
						Object result = testPair.result;
						if(result instanceof Integer && (Integer)result > 0) {
							return eval(conseq, e);
						} else {
							return eval(alt, e);
						}
					}
				};
				EvalJob pair = new EvalJob(l);
				pair.addTodoFirst(testPair);
				thingsToDo.add(pair);
				return pair;
				
			} else if(car.equals("you-folks")) {
				final LavaList literal = new LavaList();
				
				for(int i = 1; i < llexp.size(); ++i) {
					literal.add(llexp.get(i));
				}
				
				Lambda l = new Lambda()
				{
					public Object exec(Object... args)
					{
						return literal;
					}
				};

				EvalJob pair = new EvalJob(l);
				thingsToDo.add(pair);
				return pair;
			} else if(car.equals("yknow")) {
				final Object var = llexp.get(1);
				final Object varExp = llexp.get(2);

				Lambda el = new Lambda()
				{
					public Object exec(Object... args)
					{
						return eval(varExp, e);
					}
				};

				final EvalJob elp = new EvalJob(el);
				thingsToDo.add(elp);
				
				Lambda l = new Lambda()
				{
					public Object exec(Object... args)
					{
						e.put((String)var, elp.result);
						return null;
					}
				};

				EvalJob pair = new EvalJob(l);
				pair.addTodoFirst(elp);
				thingsToDo.add(pair);
				return pair;
			} else if(car.equals("bring-me-back-something-good")) {
				final LavaList lambVars = (LavaList)llexp.get(1);
				final Object lambExp = llexp.get(2);
				
				final Lambda l = new Lambda(lambVars, lambExp, e) {
					public Object exec(Object... args) {
						final Object[] lambVarsObj = ((LavaList)this.passData[0]).toArray();
						final String[] lambVars = new String[lambVarsObj.length];
						for(int i = 0; i < lambVarsObj.length; ++i) {
							lambVars[i] = (String)lambVarsObj[i];
						}
						final Object lambExp = passData[1];
						final Env outerEnv = (Env)passData[2];
						final Object[] hoijgjio = args;
						
						Lambda l = new Lambda()
						{
							public Object exec(Object... crap)
							{
								return eval(lambExp, new Env(lambVars, hoijgjio, outerEnv));
							}
						};
						EvalJob pair = new EvalJob(l);
						thingsToDo.add(pair);
						return pair;
					}
				};

				Lambda ll = new Lambda()
				{
					public Object exec(Object... args)
					{
						return l;
					}
				};
				EvalJob pair = new EvalJob(ll);
				thingsToDo.add(pair);
				return pair;
			}
			else
			{
				final LinkedList<EvalJob> stuffs = new LinkedList<EvalJob>();
				
				for(int i = 0; i < llexp.size(); ++i)
				{
					final Object o = llexp.get(i);
					final Env noE = e; // heh heh heh...e e e
					final int tempI = i;

					Lambda l = new Lambda()
					{
						public Object exec(Object... crap)
						{
							return eval(o, noE);
						}
					};
					EvalJob pair = new EvalJob(l);
					thingsToDo.add(pair);
					stuffs.add(pair);
				}
				
				Lambda bfokewm = new Lambda()
				{
					public Object exec(Object... args)
					{
						Object[] argsc = new Object[stuffs.size() - 1];
						for (int i=1;i<stuffs.size();++i)
							argsc[i-1]=stuffs.get(i).result;
						
						Lambda proc = (Lambda)stuffs.get(0).result;
						return proc.exec(argsc);
					}
				};
				EvalJob wholeThing = new EvalJob(bfokewm);
				for (EvalJob arg : stuffs)
					wholeThing.addTodoFirst(arg);
				thingsToDo.add(wholeThing);
				return wholeThing;
			}
		}
	}
	
	public static Object eval(Object exp) {
		return eval(exp, global_env);
	}
	
	public static Object parseEval(String sexp, Env e) {
		return eval(parseSexp(tokenize(sexp)), e);
	}
	
	public static Object parseEval(String sexp) {
		return parseEval(sexp, global_env);
	}

	public static void main(String[] args) throws Exception {
		String[] vals = {
				"+",
				"-",
				"*",
				"/",
				">",
				"<",
				">=",
				"<=",
				"=",
				"equal?",
				"mostprobably",
				"eq?",
				"car",
				"come-from-behind",
				"cons",
				"debug",
				"true",
				"false"
		};
		Object[] ops = {
			GlobalEnvOps.add,
			GlobalEnvOps.sub,
			GlobalEnvOps.mul,
			GlobalEnvOps.div,
			GlobalEnvOps.gt,
			GlobalEnvOps.lt,
			GlobalEnvOps.ge,
			GlobalEnvOps.le,
			GlobalEnvOps.equal,
			GlobalEnvOps.equal,
			GlobalEnvOps.equal,
			GlobalEnvOps.eq,
			GlobalEnvOps.car,
			GlobalEnvOps.cdr,
			GlobalEnvOps.cons,
			new Lambda() {
				public Object exec(Object... args) {
					debug = ((Integer)args[0] > 0);
					return null;
				}
			},
			1,
			0
		};
		
		global_env = new Env(vals, ops);
		
		Scanner scan = new Scanner(System.in);
		
		for(;;) {
			System.out.print("$ ");
			String line = scan.nextLine();
			if(line.equals("quit")) {
				break;
			}
			
			final Object sexp = parseSexp(tokenize(line));
			
			c2e = 0;
			
			long time = -System.currentTimeMillis();

			Lambda l = new Lambda()
			{
				public Object exec(Object... args)
				{
					return eval(sexp, global_env);
				}
			};
			
			EvalJob resultPair = new EvalJob(l);
			thingsToDo = new ConcurrentLinkedQueue<EvalJob>();
			thingsToDo.add(resultPair);
			final SharedInteger done = new SharedInteger(0);
			new ParallelTeam().execute(new ParallelRegion()
			{
				public void run()
				{
					boolean first = true;
					while (done.get() < getThreadCount())
					{
						if (first)
							first = false;
						else
							done.getAndDecrement();

						EvalJob pair;
						while ((pair = thingsToDo.poll()) != null)
						{
							if (pair.checkReady())
							{
								pair.execute();
							}
							else
							{
								thingsToDo.add(pair);
							}
						}
						done.getAndIncrement();
					}
				}
			});
			
			time += System.currentTimeMillis();

			resultPair.isDone();
			Object result = resultPair.result;
			
			if(result != null) {
				System.out.println(resultPair.result);
			}
			
			if(debug) {
				System.out.println(time + " msec");
				System.out.println(c2e + " calls to eval");
			}
		}
		
		scan.close();
	}

}

