import java.util.*;

public class LavaMain {
	public static Env global_env;
	
	public static int c2e = 0;
	
	public static boolean debug = false;
	
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
	
	public static Object eval(Object exp, Env e) {
		++c2e;
		
		if(exp instanceof String) {
			return e.find((String)exp).get(exp);
		} else if(!(exp instanceof LinkedList<?>)) {
			return exp;
		}else {
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
			} else if(car.equals("bring-me-back-something-good")) {
				LavaList lambVars = (LavaList)llexp.get(1);
				Object lambExp = llexp.get(2);
				
				Lambda l = new Lambda(lambVars, lambExp, e) {
					public Object exec(Object... args) {
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
				return proc.exec(args);
			}
		}
		
		return null;
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

	public static void main(String[] args) {
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
			
			Object sexp = parseSexp(tokenize(line));
			
			c2e = 0;
			
			long time = -System.currentTimeMillis();
			
			Object result = eval(sexp);
			
			time += System.currentTimeMillis();
			
			if(result != null) {
				System.out.println(result);
			}
			
			if(debug) {
				System.out.println(time + " msec");
				System.out.println(c2e + " calls to eval");
			}
		}
		
		scan.close();
	}

}
