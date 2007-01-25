public class Timer
{
    public Timer() 
    {
	clockStart_ = 0;
	clockStop_ = 0;
    }

    public void startClock()
    {
	clockStart_ = System.currentTimeMillis(); 
    }
  
    public void stopClock()
    {
	clockStop_ = System.currentTimeMillis(); 
    }
    
    void reportResult(long loops, long size)
    {
	System.out.print(" loops(" + loops + ") size(" + size + ") ");;
  	double realTime = ((clockStop_ - clockStart_)*1000.0)/loops;
 	System.out.println("real(" + realTime + ")us/call");
    }

    private long clockStart_;
    private long clockStop_;
}
