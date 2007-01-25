// -*- mode: C++; c-basic-offset: 4 -*-

#ifndef __WX_UTILS_TIMEREVALUATION__H__
#define __WX_UTILS_TIMEREVALUATION__H__

#include <cstdlib> 
#include <string>
#include <iostream>

#include "Timer.h"

namespace WX {
namespace Utils {

    class TimerEvaluation
    {
    public:
	TimerEvaluation();
	virtual ~TimerEvaluation();
	
	virtual std::string getTimerResult(const Timer& timer, 
					   const long loops, 
					   const long size);  
    };
    
} // /namespace
} // /namespace

#endif /* __WX_UTILS_TIMEREVALUATION__H__ */
