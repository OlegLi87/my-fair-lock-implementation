package li.oleg.mypackage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.concurrent.atomic.AtomicBoolean;

public class MyFairLockImplementation {
	
	private static Synchronizer sync = new Synchronizer();

	public static void main(String[] args) {
		
		Runnable worker = () -> {		
			Flag flag = new Flag();		                                     // Each Thread creates it's local flag object.
			sync.lock(flag);                                                 // Invoking shared object lock method.
			
			try {TimeUnit.SECONDS.sleep(1);}                                 // Critical section code.
			catch(InterruptedException e) {e.printStackTrace();}
			
			sync.unlock();			
		};
		
		IntStream.range(0,10).forEach(n -> new Thread(worker).start());
		
		try {TimeUnit.SECONDS.sleep(11);}
		catch(InterruptedException e) {e.printStackTrace();}		
		
		System.out.println(String.format("%1$d : Threads remained waiting.",sync.getSize()));
	}
}

//Locking class.
class Synchronizer{
	
	private boolean isLocked;                                                // Synchronizer state.
	private List<Flag> queue = new ArrayList<>(); 
                                                                             // List of each working thread flags,(representing the threads queue).	
	//Locking monitor object.
	synchronized void lock(Flag flag) {			
		
		if(isLocked) queue.add(flag);
			
		while(isLocked || queue.contains(flag)) {                            // The condition to pass the lock is 		                                        
			try {wait();}                                                    //1.Lock must not be locked and  2. Thread's flag must not be exist in the "queue list" (Removed in unlock() method).
			catch(InterruptedException e) {e.printStackTrace();}
		}
		isLocked = true;                                                     //Each passing thread must denote that lock is locked.				          
	}
	
	//Releasing monitor object.
	synchronized void unlock() {		
		if(queue.size() > 0) queue.remove(queue.get(0));                      // Checking if "queue list aka flags" contains elements,if so removing the first one.
		isLocked = false;                                                     // Denoting that a lock has been released.
		notifyAll();                                                          // Notifying all waiting elements.
	}
	
	public int getSize() {
		return queue.size();
	}
}

class Flag{}

