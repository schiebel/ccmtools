// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: doxytypes.h,v 1.1 2005/01/11 08:59:25 teiniker Exp $
//

namespace WX {
namespace Utils {

/**

   \defgroup utils_types Type framework

   \ingroup utils

   The type framework contains two categories of types: values (with
   base class Value) and types (with base class Type). Values are
   instances of types, and usually a Value instance knows the Type it
   is an instance of.

   A consequence of this is that the lifetime of a Type is longer than
   that of a Value. In other words, it is the programmer's
   responsibility to ensure that the Type instances in the program
   survive every Value instance. This is not as stringent with the
   "natural" types, such as integer (IntType) and string (StringType)
   where the corresponding value instances (IntValue and StringValue)
   do not have pointers to their types - this association exists only
   implicitly as there is only one IntType and StringType,
   logically. More complicated types however do have pointers to their
   types. For example, there is usually more than one instance of
   EnumerationType, each with a different name and different members,
   so every EnumerationValue must know the \em particular type it is
   an instance of, to delegate type checking.

   <I>As for type equality</I>: two types are equal if and only if
   they are the same instance. For example two different instances of
   type EnumerationType which have the same name and the same members
   are \em not equal. For one, this is for performance reasons (type
   comparison is much cheaper using pointer comparison than using
   string and vector comparison). For another, this is to enforce
   users to have some kind of type repository which makes always sense
   and is good design anyway.

 */

} // /namespace
} // /namespace
