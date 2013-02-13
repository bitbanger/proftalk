public class GlobalEnvOps {
	
	public static Lambda add = new Lambda() {
		public Object exec(Object... args) {
			return (Integer)args[0] + (Integer)args[1];
		}
	};
	
	public static Lambda sub = new Lambda() {
		public Object exec(Object... args) {
			return (Integer)args[0] - (Integer)args[1];
		}
	};
	
	public static Lambda mul = new Lambda() {
		public Object exec(Object... args) {
			return (Integer)args[0] * (Integer)args[1];
		}
	};
	
	public static Lambda div = new Lambda() {
		public Object exec(Object... args) {
			return (Integer)args[0] / (Integer)args[1];
		}
	};
	
	public static Lambda lt = new Lambda() {
		public Object exec(Object... args) {
			if((Integer)args[0] < (Integer)args[1]) {
				return 1;
			} else {
				return 0;
			}
		}
	};
	
	public static Lambda gt = new Lambda() {
		public Object exec(Object... args) {
			if((Integer)args[0] > (Integer)args[1]) {
				return 1;
			} else {
				return 0;
			}
		}
	};
	
	public static Lambda le = new Lambda() {
		public Object exec(Object... args) {
			if((Integer)args[0] <= (Integer)args[1]) {
				return 1;
			} else {
				return 0;
			}
		}
	};
	
	public static Lambda ge = new Lambda() {
		public Object exec(Object... args) {
			if((Integer)args[0] >= (Integer)args[1]) {
				return 1;
			} else {
				return 0;
			}
		}
	};
	
	public static Lambda equal = new Lambda() {
		public Object exec(Object... args) {
			if(args[0].equals(args[1])) {
				return 1;
			} else {
				return 0;
			}
		}
	};
	
	public static Lambda eq = new Lambda() {
		public Object exec(Object... args) {
			if(args[0] == args[1]) {
				return 1;
			} else {
				return 0;
			}
		}
	};
	
	public static Lambda car = new Lambda() {
		public Object exec(Object... args) {
			LavaList lst = (LavaList)args[0];
			
			return lst.get(0);
		}
	};
	
	public static Lambda cdr = new Lambda() {
		public Object exec(Object... args) {
			LavaList old = (LavaList)args[0];
			LavaList cdr = new LavaList();
			
			for(int i = 1; i < old.size(); ++i) {
				cdr.add(old.get(i));
			}
			
			return cdr;
		}
	};
	
	public static Lambda cons = new Lambda() {
		public Object exec(Object... args) {
			LavaList copy;
			
			if(!(args[0] instanceof LavaList)) {
				copy = (LavaList)((LavaList)args[1]).clone();
				copy.add(0, args[0]);
				return copy;
			} else {
				copy = (LavaList)((LavaList)args[0]).clone();
				copy.addAll((LavaList)args[1]);
				return copy;
			}
		}
	};
	
	public static Lambda length = new Lambda() {
		public Object exec(Object... args) {
			return ((LavaList)args[0]).size();
		}
	};
	
	public static Lambda isnull = new Lambda() {
		public Object exec(Object... args) {
			if(((LavaList)args[0]).size() == 0) {
				return 1;
			} else {
				return 0;
			}
		}
	};
}
