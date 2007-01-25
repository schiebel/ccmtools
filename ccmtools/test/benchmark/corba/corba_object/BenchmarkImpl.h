/* -*- mode: C++; c-basic-offset: 4 -*-
 *
 * Implementation of the Benchmark CORBA object.
 *
 **/

#ifndef __BENCHMARK_IMP_H__
#define __BENCHMARK_IMP_H__

#include "Benchmark.h"


class BenchmarkImpl
    : virtual public POA_Benchmark
{
private:
    CORBA::Long long_attr_;
    CORBA::String_var string_attr_;
    LongList_var LongList_attr_;

public:
    virtual CORBA::Long long_attr();
    virtual void long_attr( CORBA::Long value );
    virtual char* string_attr();
    virtual void string_attr( const char* value );
    virtual ::LongList* LongList_attr();
    virtual void LongList_attr( const ::LongList& value );

    virtual void f0();
    virtual void f_in1( CORBA::Long l1 );
    virtual void f_in2( const char* s1 );
    virtual void f_in3( const ::LongList& ll1 );
    virtual void f_inout1( CORBA::Long& l1 );
    virtual void f_inout2( char*& s1 );
    virtual void f_inout3( ::LongList& ll1 );
    virtual void f_out1( CORBA::Long_out l1 );
    virtual void f_out2( CORBA::String_out s1 );
    virtual void f_out3( ::LongList_out ll1 );
    virtual CORBA::Long f_ret1();
    virtual char* f_ret2();
    virtual ::LongList* f_ret3();
};

#endif
