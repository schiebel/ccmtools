// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: linkassert.h,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#ifndef wx_utils_code_linkassert_h
#define wx_utils_code_linkassert_h

namespace WX {
namespace Utils {

/** 

    \page page_utils_code_linkassert Link Time Assertions

    \see LTA_MEMDECL
    \see LTA_MEMDEF
    \see LTA_STATDEF

    \section utils_code_linkassert What is that?

    Best to explain the problem in C first because it is more explicit
    there.

    Suppose you have a header file containing the following
    declaration, plus a "member" function:

    \code
    struct foo {
       long l1;
       long l2;
    };

    extern void foo_print_l2(const struct foo*);
    \endcode

    In the implementation (the .c file), suppose you define
    <tt>foo_print_l2()</tt> as follows:

    \code
    void foo_print_l2(const struct foo* f) {
       printf ("%ld\n", f->l2) ;
    }
    \endcode

    Now, you change the declaration of <tt>struct foo</tt> to read

    \code
    struct foo {
       long l1;
       long l1_5;
       long l2;
    };
    \endcode

    (note the move of \c l2 by 1 long in the memory layout) and you do
    \em not recompile your .c file to tell the function
    <tt>foo_print_l2()</tt> to use the new offset of \c l2 from the
    base pointer, the function will print <tt>foo->l1_5</tt> instead
    of <tt>foo->l2</tt>, if it is linked with a fresh executable.

    (This whole thing applies to classes as well (which are structs
    basically)). A general precaution to this is to reference a
    variable from the .h file that is defined in the .c file.

    In C, before the change, this would read in the header file

    \code
    struct foo {
       long l1;
       long l2;
    };
    extern int foo_version1;

    // you have to explicitly reference the foo_version1 so that the
    // compiler does not ignore the declaration and thus does not
    // reference it
    static int foo_version = foo_version1;
    \endcode

    and in the .c file

    \code
    int foo_version1;
    \endcode

    If you change the memory layout of the struct, you somehow have to
    force a recompile of the .c file. Do this through a change of the
    name of the extern variable. In the .h file write

    \code
    struct foo {
       long l1;
       long l1_5;
       long l2;
    };
    extern int foo_version2;
    static int foo_version = foo_version2;
    \endcode

    In the .c file write

    \code
    int foo_version2;
    \endcode

    Thus, if you make the .h file publicly available, and an
    executable compiled with this new <tt>struct foo</tt> memory
    layout references the <tt>foo_print_l2()</tt> function, it must
    also reference the <tt>foo_version2</tt> extern variable. If the
    executable is linked with the old .o file, the linker won't be
    able to resolve the reference because the old .o file still
    contains the old variable <tt>foo_version1</tt>. We call this
    (most sophisticatedly) a <em>link time assertion</em>.

    In C++, this could be written as follows using static members. No
    matter how you view it, in C or C++, the principle is the same.

    The .h file:

    \code
    class Foo {
    public:
       // something ...
    public:
       static const char* version1;
    } ;
    static const char* Foo_version = Foo::version1;
    \endcode

    The .cc file:

    \code
    // we use a char* for the variable (formatted like this) because
    // this enables us to grep a binary for a particular class
    // version/revision
    const char* Foo::version1 = "Foo";
    \endcode

    \section utils_code_linkassert_when When to increment the version number

    To summarize, the rules of thumb as to when to change the <em>link
    time assertion symbol</em> (e.g. increment <tt>version1</tt> to
    <tt>version2</tt> in the example) are:

    <ul>

    <li>You added and/or removed a member variable</li>
    <li>You added and/or removed a virtual method (this changes the
      layout of the vtable (or so))</li>
    <li>You added and/or removed a base class (a base class can be
      thought of as an implicit member)</li>
    <li>You added and/or removed the <tt>virtual</tt> keyword to/from a
      base class</li>
    <li>You need not increment the version if you added/removed a
      non-virtual method.</li>
      
    </ul>


 */

/**

   \def LTA_MEMDECL(num)

   \ingroup utils_code

   \brief Declare class member with version number \a num.

   You write this macro in the class definition, about where you would
   declare the member (in fact, the macro declares the member for
   you). See \ref page_utils_code_linkassert for a description.

 */
#define LTA_MEMDECL(num) static const char* version##num

/**

   \def LTA_MEMDEF(class, num, rhs)

   \ingroup utils_code

   \brief Implement member of \a class with version number \a and the
   value \a rhs. 

   \a rhs is a string. You write this macro in the .cc file, just where
   you would implement the static member you declared with
   LTA_MEMDECL. See \ref page_utils_code_linkassert for a description.

 */
#define LTA_MEMDEF(class, num, rhs) const char* class::version##num = rhs

/**

   \def LTA_STATDEF(class, num)

   \ingroup utils_code

   \brief Reference the static member.

   Referencing the static member is necessary. It persuades the
   compiler to generate a reference to the member, and to not optimize
   it away. See \ref page_utils_code_linkassert for a description.

 */
#define LTA_STATDEF(class, num) static const char* class##_##version##num = class::version##num

} // /namespace
} // /namespace

#endif
