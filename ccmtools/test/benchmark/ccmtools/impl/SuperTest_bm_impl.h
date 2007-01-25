/**
 * CCM_Benchmark facet class definition.
 *
 * // TODO: WRITE YOUR DESCRIPTION HERE! 
 *
 * @author
 * @version 
 *
 * This class implements a facet's methods and attributes.
 **/

#ifndef __FACET__CCM_Local_bm__H__
#define __FACET__CCM_Local_bm__H__

#include <CCM_Local/Benchmark.h>
#include "SuperTest_impl.h"

namespace CCM_Local {
namespace CCM_Session_SuperTest {

class bm_impl
    : virtual public CCM_Benchmark
{
  protected:
    CCM_Local::CCM_Session_SuperTest::CCM_SuperTest_impl* component;
    long long_attr_;
    std::string string_attr_;
    LongList LongList_attr_;

  public:
    bm_impl(CCM_Local::CCM_Session_SuperTest::CCM_SuperTest_impl* component_impl);
    virtual ~bm_impl();


    virtual const long long_attr() const 
        throw(LocalComponents::CCMException);

    virtual void long_attr(const long value) 
        throw(LocalComponents::CCMException);

    virtual const std::string string_attr() const 
        throw(LocalComponents::CCMException);

    virtual void string_attr(const std::string value) 
        throw(LocalComponents::CCMException);

    virtual const LongList LongList_attr() const 
        throw(LocalComponents::CCMException);

    virtual void LongList_attr(const LongList value) 
        throw(LocalComponents::CCMException);



    virtual void f0() 
        throw (LocalComponents::CCMException);

    virtual void f_in1(const long l1) 
        throw (LocalComponents::CCMException);

    virtual void f_in2(const std::string& s1) 
        throw (LocalComponents::CCMException);

    virtual void f_in3(const LongList& ll1) 
        throw (LocalComponents::CCMException);

    virtual void f_inout1(long& l1) 
        throw (LocalComponents::CCMException);

    virtual void f_inout2(std::string& s1) 
        throw (LocalComponents::CCMException);

    virtual void f_inout3(LongList& ll1) 
        throw (LocalComponents::CCMException);

    virtual void f_out1(long& l1) 
        throw (LocalComponents::CCMException);

    virtual void f_out2(std::string& s1) 
        throw (LocalComponents::CCMException);

    virtual void f_out3(LongList& ll1) 
        throw (LocalComponents::CCMException);

    virtual long f_ret1() 
        throw (LocalComponents::CCMException);

    virtual std::string f_ret2() 
        throw (LocalComponents::CCMException);

    virtual LongList f_ret3() 
        throw (LocalComponents::CCMException);

};

} // /namespace CCM_Session_SuperTest
} // /namespace CCM_Local

#endif // __FACET__CCM_Local_bm__H__

