import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import edu.rit.pj.*;
import edu.rit.pj.reduction.*;

public class EvalJob
{
	private Lambda toExecute;
	private Lambda done;
	private LinkedList<EvalJob> todoFirst;
	public Object result;

	public EvalJob(Lambda toExecute)
	{
		this.toExecute = toExecute;
		todoFirst = new LinkedList<EvalJob>();
		done = new Lambda()
		{
			public Object exec(Object... args)
			{
				return false;
			}
		};
	}

	public void addTodoFirst(EvalJob pair)
	{
		todoFirst.add(pair);
	}

	public void execute()
	{
		result = toExecute.exec();
		if (result instanceof EvalJob)
			done = new Lambda()
			{
				public Object exec(Object... args)
				{
					if (((EvalJob)result).isDone())
					{
						result = ((EvalJob)result).result;
						done = new Lambda()
						{
							public Object exec(Object... args)
							{
								return true;
							}
						};
						return true;
					}
					return false;
				}
			};
		else
			done = new Lambda()
			{
				public Object exec(Object... args)
				{
					return true;
				}
			};
	}

	public boolean isDone()
	{
		return (Boolean)(done.exec());
	}

	public boolean checkReady()
	{
		for (EvalJob p : todoFirst)
			if (!p.isDone())
				return false;
		return true;
	}
}
