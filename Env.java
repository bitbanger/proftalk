import java.util.*;

public class Env extends HashMap<String, Object> {
	
	private static final long serialVersionUID = 4781112268900456924L;
	private Env outer;
	
	public Env() {
		super();
		
		this.outer = null;
	}
	
	public Env(String[] parms, Object[] args) {
		super();
		
		for(int i = 0; i < parms.length; ++i) {
			this.put(parms[i], args[i]);
		}
		
		this.outer = null;
	}
	
	public Env(String[] parms, Object[] args, Env outer) {
		super();
		
		for(int i = 0; i < parms.length; ++i) {
			this.put(parms[i], args[i]);
		}
		
		this.outer = outer;
	}
	
	public Env find(String val) {
		if(this.containsKey(val)) {
			return this;
		} else if(this.outer != null) {
			return this.outer.find(val);
		} else {
			System.err.println("Environment failure!");
			System.err.println("Couldn't find '" + val + "'");
			return null;
		}
	}
}
