import java.util.*;

public class LavaList extends LinkedList<Object> {
	public String toString() {
		return super.toString().replaceAll("\\[", "(").replaceAll("\\]", ")").replaceAll(",", "");
	}
}
