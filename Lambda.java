
public abstract class Lambda {
	
	protected Object[] passData;
	
	public Lambda() {
		this.passData = null;
	}
	
	public Lambda(Object... passData) {
		this.passData = passData;
	}
	
	public abstract Object exec(Object... args);
}
