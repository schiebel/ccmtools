// $Id: performance1.hxx,v 1.2 2004/02/29 11:26:47 rlechner Exp $

{
    SmartPtr<Line> theLine = myGrfx->provide_line();
    clock_t clockStart = clock();
    for( int counter=0; counter<10000000; counter++ )
    {
        theLine.ptr()->move(123.456, counter);
    }
    clock_t clockEnd = clock();
    double cpu_time_used = double(clockEnd-clockStart) / CLOCKS_PER_SEC * 1000.0;
    cout << "  time: " << cpu_time_used << "ms" << endl;
}
