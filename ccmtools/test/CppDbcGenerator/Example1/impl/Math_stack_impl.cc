
/**
 * CCM_IntegerStack facet class implementation. 
 *
 * // TODO: WRITE YOUR DESCRIPTION HERE! 
 *
 * @author
 * @version 
 *
 * This class implements a facet's methods and attributes.
 **/

#include <cassert>
#include <iostream>
#include <WX/Utils/debug.h>

#include "Math_stack_impl.h"

using namespace std;
using namespace WX::Utils;

namespace CCM_Local {
namespace CCM_Session_Math {

stack_impl::stack_impl(CCM_Local::CCM_Session_Math::CCM_Math_impl* component_impl)
  : component(component_impl), maxSize_(123456)
{
    DEBUGNL("+stack_impl->stack_impl()");

    // OPTIONAL : IMPLEMENT ME HERE !
}

stack_impl::~stack_impl()
{
    DEBUGNL ( "-stack_impl->~stack_impl (  )" );

    // OPTIONAL : IMPLEMENT ME HERE !
}

const IntegerVector
stack_impl::field() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" stack_impl->field()");
    return field_;
}

void
stack_impl::field(const IntegerVector value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" stack_impl->field(value)");
    field_ = value;
}

const long
stack_impl::maxSize() const
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" stack_impl->maxSize()");
    return maxSize_;
}

void
stack_impl::maxSize(const long value)
    throw(LocalComponents::CCMException)
{
    DEBUGNL(" stack_impl->maxSize(value)");
    maxSize_ = value;
}

bool
stack_impl::isEmpty()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("stack_impl->isEmpty()");
    return field_.empty();
}

bool
stack_impl::isFull()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("stack_impl->isFull()");
    return field_.size()>=maxSize_;
}

void
stack_impl::push(const long value)
    throw (LocalComponents::CCMException)
{
    DEBUGNL("stack_impl->push(value)");
    field_.push_back(value);
}

long
stack_impl::pop()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("stack_impl->pop()");
    long result = field_.back();
    field_.pop_back();
    return result;
}

long
stack_impl::top()
    throw (LocalComponents::CCMException)
{
    DEBUGNL("stack_impl->top()");
    return field_.back();
}

} // /namespace CCM_Session_Math
} // /namespace CCM_Local
