// -*- mode: C++; c-basic-offset: 3 -*-
// 
// $Id: debug.cc,v 1.1 2005/01/11 08:59:24 teiniker Exp $
//
#include "debug.h"

#include <set>
#include <iostream>

namespace WX {
namespace Utils {

using namespace std;

class DebugImpl : public set<string> {};

Debug instance_;

static const char* env_levels = "WX_DEBUG_LEVELS";

enum ParseState {
  PS_SPACE,
  PS_CHAR
};
static inline
bool
is_levelchar(
   char c)
{
   switch (c) {
      case '_': case ':':
      case 'A': case 'B': case 'C': case 'D': case 'E': case 'F':
      case 'G': case 'H': case 'I': case 'J': case 'K': case 'L':
      case 'M': case 'N': case 'O': case 'P': case 'Q': case 'R':
      case 'S': case 'T': case 'U': case 'V': case 'W': case 'X':
      case 'Y': case 'Z':

      case 'a': case 'b': case 'c': case 'd': case 'e': case 'f':
      case 'g': case 'h': case 'i': case 'j': case 'k': case 'l':
      case 'm': case 'n': case 'o': case 'p': case 'q': case 'r':
      case 's': case 't': case 'u': case 'v': case 'w': case 'x':
      case 'y': case 'z':

      case '0': case '1': case '2': case '3': case '4': case '5':
      case '6': case '7': case '8': case '9':

         return true;
   }
   return false;
}

Debug::Debug(
   bool read_env)
{
   impl_ = new DebugImpl;

   if (read_env) {
      const char* envlevels = getenv(env_levels);
      if (envlevels)
         parse_levels(envlevels);
   }
}

Debug::Debug(
   const string& levels,
   bool read_env)
{
   impl_ = new DebugImpl;

   parse_levels(levels);

   if (read_env) {
      const char* envlevels = getenv(env_levels);
      if (envlevels)
         parse_levels(envlevels);
   }
}

Debug::~Debug()
{
   if ( impl_ ) {
	delete impl_;
	impl_ = 0;
   }
}

void
Debug::add_level(
   const string& l)
{
   impl_->insert(l);
}

void
Debug::parse_levels(
   const string& levelstr)
{
   ParseState state = PS_SPACE;
   string::size_type beg;
   for (string::size_type i=0; i<levelstr.size(); i++) {
      char c = levelstr[i];
      switch (state) {
         case PS_SPACE: {
            if (is_levelchar(c)) {
               beg = i;
               state = PS_CHAR;
            }
            break;
         }
         case PS_CHAR: {
            if (!is_levelchar(c)) {
               impl_->insert(levelstr.substr(beg, i-beg));
               state = PS_SPACE;
            }
            break;
         }
      }
   }
   if (state == PS_CHAR)
      impl_->insert(levelstr.substr(beg, levelstr.size()-beg));
}

bool
Debug::have_level(
   const string& level)
{
   DebugImpl::const_iterator pos = impl_->find(level);
   return pos != impl_->end();
}

int
Debug::n_levels()
{
   return impl_->size();
}

Debug&
Debug::instance()
{
   return instance_;
}

} // /namespace
} // /namespace
