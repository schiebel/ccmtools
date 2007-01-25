// $Id: perf1code.hxx,v 1.1 2004/02/29 11:26:47 rlechner Exp $

{
    cout << "  " << LOOP1 << " loops with " << LOOP2 << " push/pop calls" << endl;
    clock_t clockStart = clock();
    for( int counter=0; counter<LOOP1; counter++ )
    {
        int index;
        for( index=0; index<LOOP2; index++ )
        {
            theStack->push(index);
        }
        for( index=0; index<LOOP2; index++ )
        {
            long dummy = theStack->pop();
        }
    }
    clock_t clockEnd = clock();
    double cpu_time_used = double(clockEnd-clockStart) / CLOCKS_PER_SEC * 1000.0;
    cout << "  time: " << cpu_time_used << "ms" << endl;
}
