

import java.io.*;
import org.omg.CORBA.*;
import org.omg.CosNaming.*;

import CORBA_Stubs.*;

public class Client
{
    public static void main(String args[])
    {
	// A timer that measures the complete runtime of the client application
	Timer globalTimer = new Timer();
	globalTimer.startClock();
	
	try {
	    System.out.println(">>>> Start Test Client: Client.java");
	    if(args.length == 0) {
		System.out.println("Usage: java -cp $CLASSPATH Client"
				   + "-ORBInitialPort 5050 "
				   + "-ORBInitialHost localhost");
		return;
	    }

	    // Init ORB 
	    ORB orb = ORB.init(args, null);

            // Connect to the NameService
            org.omg.CORBA.Object nameObj = 
                orb.resolve_initial_references("NameService");
            NamingContextExt namingContext = 
		NamingContextExtHelper.narrow(nameObj);

            // Get a component home
            org.omg.CORBA.Object homeObj = 
                namingContext.resolve_str("TestHome:1.0");
            TestHome home = 
		CORBA_Stubs.TestHomeHelper.narrow(homeObj);

            // Create a component instance and ask for a facet
            Test component = home.create();
	    Benchmark bm = component.provide_bm();
            // Finish configuration phase
            component.configuration_complete();

	    System.out.println("--- Start Test Case ------------------------");

	    // A timer to measure call/response times
	    Timer timer = new Timer();
	    final long MAX_LOOP_COUNT = 10000;
	    final long SEQUENCE_SIZE_MAX = 1000;
	    final long SEQUENCE_SIZE_STEP = 100;

	    //----------------------------------------------------------
	    // ping test case
	    //----------------------------------------------------------

	    {
		// Ping
		System.out.println();
		System.out.print("Java Remote CCM Test: void f0() "); 
		
		timer.startClock();
		for(long counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
		    bm.f0();
		}
		timer.stopClock();
		timer.reportResult(MAX_LOOP_COUNT,1);
	    }


	    //----------------------------------------------------------
	    // in parameter test cases 
	    //----------------------------------------------------------

	    {
		// in long parameter
		System.out.println();
		System.out.print("Java Remote CCM Test: void f_in1(in long l1) "); 
		
		int value = 7;
		
		timer.startClock();
		for(int counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
		    bm.f_in1(value);
		}
		timer.stopClock();
		timer.reportResult(MAX_LOOP_COUNT,1);
	    }

	    {
		// in string parameter with increasing size
		System.out.println();
		for(int size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
		    System.out.print("Java Remote CCM Test: void f_in2(in string s1) "); 
		    
		    StringBuffer buffer = new StringBuffer(size);
		    for(int i=0; i<size; i++)
			buffer.append("X");
		    String value = buffer.toString();
		    
		    timer.startClock();
		    for(int counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
			bm.f_in2(value);
		    }
		    timer.stopClock();
		    timer.reportResult(MAX_LOOP_COUNT,size);
		}
	    }

	    {
		// in sequence of long parameter with increasing size
		System.out.println();
		for(int size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
		    System.out.print("Java Remote CCM Test: void f_in3(in LongList ll1) "); 
		    
		    int[] value = new int[size];
		    
		    for(int i=0; i<size; i++)
			value[i] = i;
		    
		    timer.startClock();
		    for(int counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
			bm.f_in3(value);
		    }
		    timer.stopClock();
		    timer.reportResult(MAX_LOOP_COUNT,size);
		}
	    }	


	    //----------------------------------------------------------
	    // inout parameter test cases 
	    //----------------------------------------------------------

	    {
		// inout long parameter
		System.out.println();
		System.out.print("Java Remote CCM Test: void f_inout1(inout long l1) "); 
		
		int value = 7;
		IntHolder valueInOut= new IntHolder(7);
		bm.long_attr(value);

		timer.startClock();
		for(int counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
		    bm.f_inout1(valueInOut);
		}
		timer.stopClock();
		timer.reportResult(MAX_LOOP_COUNT,1);
	    }

	    {
		// inout string parameter with increasing size
		System.out.println();
		for(int size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
		    System.out.print("Java Remote CCM Test: void f_inout2(inout string s1) "); 
		    
		    StringBuffer buffer = new StringBuffer(size);
		    for(int i=0; i<size; i++)
			buffer.append("X");
		    String value = buffer.toString();
		    StringHolder valueInOut = new StringHolder(value);
		    bm.string_attr(value);

		    timer.startClock();
		    for(int counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
			bm.f_inout2(valueInOut);
		    }
		    timer.stopClock();
		    timer.reportResult(MAX_LOOP_COUNT,size);
		}
	    }

	    {
		// inout sequence of long parameter with increasing size
		System.out.println();
		for(int size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
		    System.out.print("Java Remote CCM Test: void f_inout3(inout LongList ll1) "); 
		    
		    int[] value = new int[size];
		    for(int i=0; i<size; i++)
			value[i] = i;
		    LongListHolder valueInOut = new LongListHolder(value);
		    bm.LongList_attr(value);  

		    timer.startClock();
		    for(int counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
			bm.f_inout3(valueInOut);
		    }
		    timer.stopClock();
		    timer.reportResult(MAX_LOOP_COUNT,size);
		}
	    }	



	    //----------------------------------------------------------
	    // out parameters test cases 
	    //----------------------------------------------------------

	    {
		// out long parameter
		System.out.println();
		System.out.print("Java Remote CCM Test: void f_out1(out long l1) "); 
		
		int value = 7;
		IntHolder valueOut= new IntHolder(0);
		bm.long_attr(value);

		timer.startClock();
		for(int counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
		    bm.f_out1(valueOut);
		}
		timer.stopClock();
		timer.reportResult(MAX_LOOP_COUNT,1);
	    }

	    {
		// out string parameter with increasing size
		System.out.println();
		for(int size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
		    System.out.print("Java Remote CCM Test: void f_out2(out string s1) "); 
		    
		    StringBuffer buffer = new StringBuffer(size);
		    for(int i=0; i<size; i++)
			buffer.append("X");
		    String value = buffer.toString();
		    String defaultValue = "";
		    StringHolder valueOut = new StringHolder(defaultValue);
		    bm.string_attr(value);

		    timer.startClock();
		    for(int counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
			bm.f_out2(valueOut);
		    }
		    timer.stopClock();
		    timer.reportResult(MAX_LOOP_COUNT,size);
		}
	    }

	    {
		// out sequence of long parameter with increasing size
		System.out.println();
		for(int size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
		    System.out.print("Java Remote CCM Test: void f_out3(out LongList ll1) "); 
		    
		    int[] value = new int[size];
		    for(int i=0; i<size; i++)
			value[i] = i;
		    int[] defaultValue = new int[0];
		    LongListHolder valueOut = new LongListHolder(defaultValue);
		    bm.LongList_attr(value);  

		    timer.startClock();
		    for(int counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
			bm.f_out3(valueOut);
		    }
		    timer.stopClock();
		    timer.reportResult(MAX_LOOP_COUNT,size);
		}
	    }	


	    //----------------------------------------------------------
	    // return value test cases 
	    //----------------------------------------------------------
	    
	    {
		// long result
		System.out.println();
		System.out.print("Java Remote CCM Test: long f_ret1() "); 
		
		int value = 7;
		int result;
		bm.long_attr(value);

		timer.startClock();
		for(int counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
		    result = bm.f_ret1();
		}
		timer.stopClock();
		timer.reportResult(MAX_LOOP_COUNT,1);
	    }

	    {
		// string result with increasing size
		System.out.println();
		for(int size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
		    System.out.print("Java Remote CCM Test: string f_ret2() "); 
		    
		    StringBuffer buffer = new StringBuffer(size);
		    for(int i=0; i<size; i++)
			buffer.append("X");
		    String value = buffer.toString();
		    String result;
		    bm.string_attr(value);

		    timer.startClock();
		    for(int counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
			result = bm.f_ret2();
		    }
		    timer.stopClock();
		    timer.reportResult(MAX_LOOP_COUNT,size);
		}
	    }

	    {
		// out sequence of long parameter with increasing size
		System.out.println();
		for(int size=0; size<=SEQUENCE_SIZE_MAX; size+=SEQUENCE_SIZE_STEP) {
		    System.out.print("Java Remote CCM Test: LongList f_ret3() "); 
		    
		    int[] value = new int[size];
		    for(int i=0; i<size; i++)
			value[i] = i;
		    int[] result;
		    bm.LongList_attr(value);  

		    timer.startClock();
		    for(int counter=0; counter<MAX_LOOP_COUNT; counter++ ) {
			result = bm.f_ret3();
		    }
		    timer.stopClock();
		    timer.reportResult(MAX_LOOP_COUNT,size);
		}
	    }	


	    System.out.println("--- Stop Test Case -------------------------");

	    // Destroy component instance
	    component.remove();
        }
        catch(org.omg.CORBA.ORBPackage.InvalidName e) {
            e.printStackTrace();
        }
        catch(org.omg.CosNaming.NamingContextPackage.NotFound e) {
            e.printStackTrace();
        }
        catch(org.omg.CosNaming.NamingContextPackage.CannotProceed e) {
            e.printStackTrace();
        }
        catch(org.omg.CosNaming.NamingContextPackage.InvalidName e) {
            e.printStackTrace();
        }
        catch(Components.CreateFailure e) {
            e.printStackTrace();
        }
	
	System.out.println(">>>> Stop Test Client: Client.java");
	globalTimer.stopClock();
	globalTimer.reportResult(1,1);
    }
}
