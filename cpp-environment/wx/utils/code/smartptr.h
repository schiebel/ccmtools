// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: smartptr.h,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#ifndef wx_utils_code_smartptr_h
#define wx_utils_code_smartptr_h

#include "linkassert.h"
#include <iostream>

namespace WX {
namespace Utils {


/** 

    \brief Base class for all classes which are supposed to be pointed
    to by a smart pointer. Actually doesn't do anything, but rather
    provides the interface.

    \ingroup utils_code

    \author Joerg Faschingbauer

    \b COPYING \b A \b REFERENCE \b COUNTED \b THING

    Copying a refcounted thing is not straightforward yet it is
    simple. In an ideal world you would pass a class object either by
    copy or by using a smart pointer. The former method does not care
    about anything, it just copies, whereas the latter method
    maintains a reference count inside the object that is being
    passed.

    Everything is fine if you don't intermix both methods (which you
    shouldn't unless there is need to AND you know what you're
    doing). It gets more complicated if, for example,

    \b COPY \b CONSTRUCTOR

    we take a copy of a smartpointed object and pass that copy around
    using a smart pointer: we get a smart pointer xp that points to
    some object, say *x.

    \code
    SmartPtr<X> xp = get_object();
    \endcode

    Now assume that *x, after execution of that statement, has a
    refcount of 2 (x->refcount()==2). In other words, another smart
    pointer (other than xp) points to it somewhere outside our scope.

    We now copy that object *x (not the smart pointer xp) into another
    object, *y.

    \code
    X* y = new X(*xp.ptr());
    \endcode

    The question is: what reference count must *y now have? Obviously,
    since the reference count reflects the number of smart pointers
    pointing at it (well, ideally; one can always dirtily ref() and
    unref() by hand), the reference count of *y must be ZERO.

    Otherwise, if we would pass *y around using smart pointers (which
    would then be responsible for cleanup), we would leak memory.

    \code
    XPtr yp = XPtr(y);
    pass_around(yp); // leak
    \endcode

    \b ASSIGNMENT \b OPERATOR
    
    Now for the assignment operator. Suppose we get an object *x which
    is pointed to by, say, 2 smart pointers (x->refcount()==2), one of
    them being xp.

    \code
    SmartPtr<X> xp = get_object();
    \endcode

    Now, eagerly crying for subtle bugs, we want to replace the
    content of *x with that of some other object newx of class X.

    \code
    X newx(...);
    *xp.ptr() = newx;
    \endcode

    The question is: what must be the reference count of *x?
    Obviously, since the number of smart pointers pointing at it does
    not change, the reference count must not change, it must still be
    2 as before the assignment.

*/

class RefCounted {
public:
   RefCounted() : refcount_(0) {}
   virtual ~RefCounted() {}

   //@{ see the big note above for copy semantics.
   RefCounted(const RefCounted&) : refcount_(0) {}
   RefCounted& operator=(const RefCounted&) { return *this; }
   //@}

   /// increment reference count
   virtual void ref() { refcount_++; }
   /// decrement reference count
   virtual void unref() { if (!--refcount_) delete this; }

   int refcount() const { return refcount_; }

   //@{ simple wrappers to accommodate null pointers
   static void ref(RefCounted* r) { if (r) r->ref(); }
   static void unref(RefCounted* r) { if (r) r->unref(); }
   //@}

private:
   int refcount_;

public:
   LTA_MEMDECL(2);
};
LTA_STATDEF(RefCounted, 2);


/**

   \class SmartPtr

   \ingroup utils_code

   \brief Template class which manages pointers to RefCounted objects.

   \author Joerg Faschingbauer

    Smart pointers are supposed to be used in cases where you do not
    want to care about memory ownerships. For example, when you pass a
    pointer to an object to a function, you and the function have to
    agree upon who is responsible for deleting the object. You use a
    smart pointer to automatically accomplish that. You wrap a smart
    pointer around the raw pointer, and pass the smart pointer instead
    (of course, the function must be prepared to expect a smart
    pointer). 

    If you are not interested in the object after the call, you simply
    let the smart pointer go out of scope. The smart pointer's
    destructor will then decrement the reference count of the
    pointed-to object, and, if the reference count has become zero,
    delete it. 

    If you are interested in keeping the object, you store the smart
    pointer somewhere. This prevents the object's reference count from
    becoming zero.

    The function, on the other hand, may decide to store the smart
    pointer somewhere (and keep a reference on it), or to only
    temporarily use it and then forget about it (let it go out of
    scope).

 */
template <class T> class SmartPtr {
public:

   /** Default constructor. Initially a smart pointer points to
       nothing (i.e., has the value 0). */
   SmartPtr(): ptr_(0) {}

   /** "Eating" constructor. The object the argument points to is now
       managed by the smart pointer object. Hence it must not be
       deleted explicitly afterwards. */
   explicit SmartPtr(T* p): ptr_(p) { RefCounted::ref(ptr_); }

   /** Copy constructor, template version (to be able to assign
       derived pointees). Increments the reference count on the object
       by one. */
   template<class From>
   SmartPtr(const SmartPtr<From>& p): ptr_(0) { operator=(p); }

   /** Copy constructor, non-template version. Increments the
       reference count on the object by one.

       Implementor's note: one would think that the emplate version of
       the copy ctor should be used by the compiler to generate the
       default copy ctor. But that's not how C++ is defined - if we do
       not define an explicit non-template copy ctor, we would get a
       real default copy ctor which only copies the pointer and does
       no refcounting.
   */
   SmartPtr(const SmartPtr& p): ptr_(0) { operator=(p); }

   /** Destructor. Decrements the reference count by one. Deletes the
       object (better to say, the object deletes itself) if the
       reference count becomes zero. */
   ~SmartPtr() { RefCounted::unref(ptr_); }

   /** Assignment operator, template version. Same semantics as
       template copy constructor. */
   template<class From>
   SmartPtr&
   operator=(
      const SmartPtr<From>& p)
   {
      if (this->ptr() != p.ptr()) {
         RefCounted::ref(p.ptr());
         RefCounted::unref(this->ptr());
         ptr_ = p.ptr();
      }
      return *this;
   }

   /** Assignment operator, non-template version. Same semantics as
       copy constructor. 

       Implementor's note: we have to define a non-template version
       for the same reason why we have to define a non-template copy
       ctor.
   */
   SmartPtr&
   operator=(
      const SmartPtr& p)
   {
      if (this->ptr() != p.ptr()) {
         RefCounted::ref(p.ptr());
         RefCounted::unref(this->ptr());
         ptr_ = p.ptr();
      }
      return *this;
   }

   /** Acquire responsibility for the object (and increment its
       reference count). An eventual existing responsibility for a
       different object is canceled, and that reference is
       decremented. */
   template<class From>
   void eat(From* p)
   {
      if (p == ptr_) return;
      RefCounted::unref(ptr_);
      ptr_ = p;
      RefCounted::ref(p);
   }

   /** Become a NULL pointer. Unreference an object that we are
       pointing to. */
   void forget()
   {
      if (ptr_) {
         RefCounted::unref(ptr_);
         ptr_ = NULL;
      }
   }

   /** \name Smart pointer comparison */
   //@{
   operator bool() const {
      // Doze bullshit yells "Performance warning" if I don't cast
      // explicitly
      return static_cast<bool>(ptr_);
   }
   bool operator !() const { return !ptr_; }
   bool operator< (const SmartPtr& that) const { return ptr_< that.ptr_; }
   bool operator==(const SmartPtr& that) const { return ptr_==that.ptr_; }
   bool operator> (const SmartPtr& that) const { return ptr_> that.ptr_; }
   bool operator<=(const SmartPtr& that) const { return ptr_<=that.ptr_; }
   bool operator>=(const SmartPtr& that) const { return ptr_>=that.ptr_; }
   //@}

   /** A pointer to the object, modifiable. */
   T* ptr() const { return ptr_; }
   /** A pointer to the object, modifiable, trading off for
       readability. */
   T* operator->() { return ptr_; }
   /** A pointer to the object, non-modifiable. */
   const T* cptr() const { return ptr_; }
   /** A pointer to the object, non-modifiable, trading off for
       readability. */
   const T* operator->() const { return ptr_; }


private:
   T* ptr_;
};

} // /namespace
} // /namespace

#endif
