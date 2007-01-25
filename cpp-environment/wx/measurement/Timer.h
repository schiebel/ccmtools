// -*- mode: C++; c-basic-offset: 4 -*-

#ifndef __WX_UTILS_TIMER__H__
#define __WX_UTILS_TIMER__H__

#include <cstdlib> 
#include <string>
#include <iostream>

namespace WX {
namespace Utils {

    class Timer
    {
    public:
	Timer();
	virtual ~Timer();
	
	/**
	 * Starts a timer object by saving the current point in time in a set
	 * of *TimeStart attributes.
	 */
	virtual void start();
	
	
	/**
	 * Stops a timer object by saving the current point in time in a set
	 * of *TimeStop attributes.
	 */
	virtual void stop();
	
	/**
	 * Returns the real time in seconds calculated from the number of clock 
	 * ticks that have elapsed since an arbitrary point in the past.
	 */
	virtual double getRealTime() const;
	
	/**
	 * Returns the user time in seconds calculated from the CPU time spent 
	 * executing instructions of the calling process.
	 */
	virtual double getUserTime() const;
	
	/**
	 * Returns the system time in seconds calculated from the CPU time 
	 * spent in the system while executing tasks on behalf of the calling 
	 * process.
	 */
	virtual double getSystemTime() const;
	
	/**
	 * Returns a string representation of the real, user and system time
	 * stored in a timer object.
	 */
	virtual std::string toString() const;
	
    private:
	clock_t realTimeStart;
	clock_t userTimeStart;
	clock_t systemTimeStart;
	clock_t realTimeStop;
	clock_t userTimeStop;
	clock_t systemTimeStop;
    };
    
    std::ostream&
    operator<<(std::ostream& o, const Timer& value);
    
} // /namespace
} // /namespace

#endif /* __WX_UTILS_TIMER__H__ */
