My own implementation of FaiRLock object used in Java concurrency.
Implementation makes use of queue list that everey running Thread stores its flag there.
This implementation helps to pass securely critical sections and avoid race conditions.