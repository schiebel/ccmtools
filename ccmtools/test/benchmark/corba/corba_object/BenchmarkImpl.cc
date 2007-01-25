/* -*- mode: C++; c-basic-offset: 4 -*-
 *
 * Implementation of the Benchmark CORBA object.
 *
 **/

#include"BenchmarkImpl.h"

using namespace std;


CORBA::Long 
BenchmarkImpl::long_attr()
{
    return long_attr_;
}

void 
BenchmarkImpl::long_attr( CORBA::Long value )
{
    long_attr_ = value;
}

char* 
BenchmarkImpl::string_attr()
{
    return string_attr_._retn();
}

void 
BenchmarkImpl::string_attr( const char* value)
{
    string_attr_ = CORBA::string_dup(value);
}

::LongList* 
BenchmarkImpl::LongList_attr()
{
    LongList_var result = new LongList(LongList_attr_);
    return result._retn();
}

void 
BenchmarkImpl::LongList_attr( const ::LongList& value )
{
    LongList_attr_ = new LongList(value);
}

void 
BenchmarkImpl::f0()
{

}

void 
BenchmarkImpl::f_in1( CORBA::Long l1 )
{

}


void 
BenchmarkImpl::f_in2( const char* s1 )
{

}

void 
BenchmarkImpl::f_in3( const ::LongList& ll1 )
{

}


void 
BenchmarkImpl::f_inout1(CORBA::Long& l1 )
{
    l1 = long_attr_;
}

void 
BenchmarkImpl::f_inout2( char*& s1 )
{
    s1 = CORBA::string_dup(string_attr_);
}

void 
BenchmarkImpl::f_inout3( ::LongList& ll1 )
{
    ll1 = LongList_attr_.inout();
}


void 
BenchmarkImpl::f_out1(CORBA::Long_out l1 )
{
    l1 = long_attr_;
}

void 
BenchmarkImpl::f_out2( CORBA::String_out s1 )
{
    s1 = CORBA::string_dup(string_attr_);
}

void 
BenchmarkImpl::f_out3( ::LongList_out ll1 )
{
    ll1 = new LongList(LongList_attr_);
}


CORBA::Long 
BenchmarkImpl::f_ret1()
{
    return long_attr_;
}

char*
BenchmarkImpl::f_ret2()
{
    return CORBA::string_dup(string_attr_);
}

::LongList*
BenchmarkImpl::f_ret3()
{
    LongList_var result = new LongList(LongList_attr_);
    return result._retn();
}

