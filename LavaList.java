import java.util.*;

public class LavaList extends ArrayList<Object> {
	public String toString() {
		return super.toString().replaceAll("\\[", "(").replaceAll("\\]", ")").replaceAll(",", "");
	}
}
