package li.oleg.mypackage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class MyFairLockImplementation {
	
	private static Synchronizer sync = new Synchronizer();

	public static void main(String[] args) {
		
		Runnable worker = () -> {		
			Flag flag = new Flag();		                                 // Each Thread creates it's local flag (token) object.
			sync.lock(flag);                                                 // Invoking shared object's lock method.
			
			try {TimeUnit.SECONDS.sleep(1);}                                 // Critical section code.
			catch(InterruptedException e) {e.printStackTrace();}
			
			sync.unlock();			                                 //Once thread has passed the critical section it may "signal" to waiting threads.
			                                                                
		};
		
		IntStream.range(0,10).forEach(n -> new Thread(worker).start());          //Instantiating Thread objects using Java8 Stream object.
		
		try {TimeUnit.SECONDS.sleep(11);}
		catch(InterruptedException e) {e.printStackTrace();}		
		
		System.out.println(String.format("%1$d : Threads remained waiting.",sync.getSize()));
	}
}

//Locking class.
class Synchronizer{
	
	private boolean isLocked;                                                   // Synchronizer state.
	private List<Flag> queue = new ArrayList<>();                               // Queue of threads waiting to get pass.	
                                                                          
	//Locking monitor object.
	synchronized void lock(Flag flag) {					
		if(isLocked) queue.add(flag);
			
		while(isLocked || queue.contains(flag)) {                            // The condition to pass the get is 		                                        
			try {wait();}                                                //1.Lock must not be locked and  			                                                         
			catch(InterruptedException e) {e.printStackTrace();}         //2. Thread's flag must not exist in the "queue list" (Removed in unlock() method)
		}
		isLocked = true;                                                     //Each passing thread must denote that lock is locked (meaning there is a working thread in critical section).				          
	}
	
	//Releasing monitor object.
	synchronized void unlock() {		
		if(queue.size() > 0) queue.remove(queue.get(0));                      // Checking if there are more waiting Threads in the queue.If true remove first one.
		isLocked = false;                                                     // Denoting that a lock has been released.
		notifyAll();                                                          // Notifying all waiting elements.
	}
	
	public int getSize() {
		return queue.size();
	}
}

class Flag{}

