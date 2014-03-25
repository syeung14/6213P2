package threading;

import java.util.concurrent.Callable;

public class LoadAndCalc implements Callable<Long> {

	private int cnt;
	
	public LoadAndCalc(int cnt) {
		super();
		this.cnt = cnt;
	}

	private long fibonacci(int cnt) {
		if (cnt == 1) {
			return 1;
		} else if (cnt == 0) {
			return 0;
		} else
			return fibonacci(cnt - 1) + fibonacci(cnt - 2);

	}

	private long fib(int cnt) {
		for (int i = 2; i <= cnt; i++) {
			
		}

		return 0;

	}
	@Override
	public Long call() throws Exception {

		long fib = fibonacci(cnt);
		System.out.println("inside call method "  + fib);
		
		return fib;
		
	}

}
