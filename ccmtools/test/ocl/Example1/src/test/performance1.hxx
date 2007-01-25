// $Id: performance1.hxx,v 1.2 2004/02/29 11:26:47 rlechner Exp $

{
    SmartPtr<IntegerStack> theStack = myMath->provide_stack();
    //
    #define LOOP1 1000
    #define LOOP2 1000
    #include "perf1code.hxx"
    #undef LOOP2
    #undef LOOP1
    //
    #define LOOP1 10000
    #define LOOP2 100
    #include "perf1code.hxx"
    #undef LOOP2
    #undef LOOP1
}
