import java.util.*;
import edu.rit.pj.*;

public class LavaMain {
	public static Env global_env;
	
	public static boolean debug = false;
	
	public static Object runOp(Env e, String opName, Object... args) throws Exception {
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
	
	public static Object parseSexp(LinkedList<String> tokens) throws Exception {
		String token = tokens.removeFirst();
		
		if(token.equals("(")) {
			LavaList sexp = new LavaList();
			while(!(tokens.get(0).equals(")"))) {
				sexp.add(parseSexp(tokens));
			}
			tokens.removeFirst();
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
	
	public static Object eval(Object exp, Env e) throws Exception {
		final Env myEnv = e;
		
		if(exp instanceof String) {
			Env lookupEnv = e.find((String)exp);
			if(lookupEnv != null) {
				return lookupEnv.get(exp);
			} else {
				System.err.printf("No.\n\t'%s' is an unresolvable symbol.\n", (String)exp);
				return null;
			}
		} else if(!(exp instanceof LinkedList<?>)) {
			return exp;
		} else {
			LavaList llexp = (LavaList)exp;
			Object car = llexp.get(0);
			
			if(car.equals("insofaras")) {
				Object test = llexp.get(1);
				Object conseq = llexp.get(2);
				Object alt = llexp.get(3);
				
				Object result = eval(test, e);
				
				if(result instanceof Integer && (Integer)result > 0) {
					return eval(conseq, e);
				} else {
					return eval(alt, e);
				}
			} else if(car.equals("you-folks")) {
				LavaList literal = new LavaList();
				
				for(int i = 1; i < llexp.size(); ++i) {
					literal.add(llexp.get(i));
				}
				
				return literal;
			} else if(car.equals("yknow")) {
				Object var = llexp.get(1);
				Object varExp = llexp.get(2);
				
				e.put((String)var, eval(varExp, e));
			} else if(car.equals("apply")) {
				Lambda func = (Lambda)(eval(llexp.get(1), myEnv));
				LavaList argList = (LavaList)(eval(llexp.get(2), myEnv));
				Object[] args = argList.toArray(new Object[argList.size()]);
				
				return func.exec(args);
			} else if(car.equals("eval-all")) {
				final Object[] sequentialSexps = new Object[llexp.size() - 1];
				final Object[] results = new Object[sequentialSexps.length];
				for(int i = 1; i < llexp.size(); ++i) {
					sequentialSexps[i - 1] = llexp.get(i);
				}
				
				for(int i = 0; i < sequentialSexps.length; ++i) {
					results[i] = eval(sequentialSexps[i], myEnv);
				}
				
				LavaList retList = new LavaList();
				for(int i = 0; i < results.length; ++i) {
					retList.add(results[i]);
				}
				
				return retList;
			} else if(car.equals("bring-me-back-something-good")) {
				LavaList lambVars = (LavaList)llexp.get(1);
				Object lambExp = llexp.get(2);
				
				Lambda l = new Lambda(lambVars, lambExp, e) {
					public Object exec(Object... args) throws Exception {
						Object[] lambVarsObj = ((LavaList)this.passData[0]).toArray();
						String[] lambVars = new String[lambVarsObj.length];
						for(int i = 0; i < lambVarsObj.length; ++i) {
							lambVars[i] = (String)lambVarsObj[i];
						}
						Object lambExp = passData[1];
						Env outerEnv = (Env)passData[2];
						
						return eval(lambExp, new Env(lambVars, args, outerEnv));
					}
				};
				
				return l;
			} else if(car.equals("map")) {
				Lambda theFunc = (Lambda)eval(llexp.get(1), myEnv);
				LavaList theArgs = (LavaList)eval(llexp.get(2), myEnv);
				LavaList theResults = new LavaList();
				
				for(int i = 0; i < theArgs.size(); ++i) {
					theResults.add(theFunc.exec(theArgs.get(i)));
				}
				
				return theResults;
			} else if(car.equals("par-map")) {
				final Lambda theFunc = (Lambda)eval(llexp.get(1), myEnv);
				LavaList theList = (LavaList)eval(llexp.get(2), myEnv);
				final Object[] theArgs = theList.toArray(new Object[theList.size()]);
				final Object[] theResults = new Object[theArgs.length];
				
				new ParallelTeam().execute(new ParallelRegion() {
					public void run() throws Exception {
						execute(0, theArgs.length - 1, new IntegerForLoop() {
							public void run(int first, int last) throws Exception {
								for(int i = first; i <= last; ++i) {
									theResults[i] = theFunc.exec(theArgs[i]);
								}
							}
						});
					}
				});
				
				LavaList returnList = new LavaList();
				for(int i = 0; i < theResults.length; ++i) {
					returnList.add(theResults[i]);
				}
				
				return returnList;
			} else if(car.equals("pv")) {
				final Object[] parallelSexps = new Object[llexp.size() - 1];
				final Object[] results = new Object[parallelSexps.length];
				for(int i = 1; i < llexp.size(); ++i) {
					parallelSexps[i - 1] = llexp.get(i);
				}
				
				new ParallelTeam().execute(new ParallelRegion() {
					public void run() throws Exception {
						execute(0, parallelSexps.length - 1, new IntegerForLoop() {
							public void run(int first, int last) throws Exception {
								for(int i = first; i <= last; ++i) {
									results[i] = eval(parallelSexps[i], myEnv);
								}
							}
						});
					}
				});
				
				LavaList retList = new LavaList();
				for(int i = 0; i < results.length; ++i) {
					retList.add(results[i]);
				}
				
				return retList;
			} else {
				LavaList evallexp = new LavaList();
				Object[] args = new Object[llexp.size() - 1];
				
				for(int i = 0; i < llexp.size(); ++i) {
					Object o = llexp.get(i);
					Object evalO = eval(o, e);
					
					evallexp.add(evalO);
					
					if(i > 0) {
						args[i - 1] = evalO;
					}
				}
				
				Lambda proc = (Lambda)evallexp.get(0);
				if(proc == null) {
					System.err.printf("No.\n\t'%s' is not a valid function.\n", llexp.get(0));
					return null;
				}
				return proc.exec(args);
			}
		}
		
		return null;
	}
	
	public static Object eval(Object exp) throws Exception {
		return eval(exp, global_env);
	}
	
	public static Object parseEval(String sexp, Env e) throws Exception {
		return eval(parseSexp(tokenize(sexp)), e);
	}
	
	public static Object parseEval(String sexp) throws Exception {
		return parseEval(sexp, global_env);
	}

	public static void main(String[] args) throws Exception {
		Comm.init(args);
		
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
				"length",
				"null?",
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
			GlobalEnvOps.length,
			GlobalEnvOps.isnull,
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
			System.out.print("proftalk~$ ");
			String line = scan.nextLine();
			if(line.equals("quit")) {
				break;
			}
			
			Object sexp = parseSexp(tokenize(line));
			
			long time = -System.currentTimeMillis();
			
			Object result = eval(sexp);
			
			time += System.currentTimeMillis();
			
			if(result != null) {
				System.out.println(result);
			}
			
			if(debug) {
				System.out.println(time + " msec");
			}
		}
		
		scan.close();
	}

}
