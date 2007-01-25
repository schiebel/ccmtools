#ifndef __CCM_OCL__OCL_HELPERS__H__
#define __CCM_OCL__OCL_HELPERS__H__

/*$Id: OclHelpers.h,v 1.1 2005/01/11 09:06:56 teiniker Exp $*/


/******************************************************\
*                                                      *
*   helper functions and classes for OCL expressions   *
*                                                      *
\******************************************************/


#include <string>
#include <cstdlib>
#include <cmath>
#include <vector>


namespace CCM_OCL {

using std::string;
using std::vector;

typedef long    OCL_Integer;
typedef double  OCL_Real;
typedef string  OCL_String;
typedef bool    OCL_Boolean;
typedef size_t  OCL_Size;


OCL_String OCL_toUpper( const string& x );
OCL_String OCL_toLower( const string& x );

OCL_Integer OCL_div( OCL_Integer z, OCL_Integer n );
OCL_Integer OCL_mod( OCL_Integer z, OCL_Integer n );

OCL_Boolean OCL_equals( OCL_Real a, OCL_Real b );

int OCL_random( int lowerRange, int upperRange );


//----------------------------------------------------------------------------


template <class T>
bool OCL_includes( const vector<T>& obj, const T& value )
{
    const int size = obj.size();
    for( int index=0; index<size; index++ )
    {
        if( obj[index]==value )
        {
            return true;
        }
    }
    return false;
}

template <class T>
OCL_Integer OCL_count( const vector<T>& obj, const T& value )
{
    OCL_Integer result = 0;
    const int size = obj.size();
    for( int index=0; index<size; index++ )
    {
        if( obj[index]==value )
        {
            result++;
        }
    }
    return result;
}

template <class T>
bool OCL_includesAll( const vector<T>& obj, const vector<T>& x )
{
    const int size = x.size();
	for( int index=0; index<size; index++ )
	{
		if( !OCL_includes(obj, x[index]) )
		{
			return false;
		}
	}
	return true;
}

template <class T>
bool OCL_excludesAll( const vector<T>& obj, const vector<T>& x )
{
    const int size = x.size();
	for( int index=0; index<size; index++ )
	{
		if( OCL_includes(obj, x[index]) )
		{
			return false;
		}
	}
	return true;
}


//----------------------------------------------------------------------------


template <class T> class OCL_Collection : public vector<T>
{
public:
    OCL_Collection() : vector<T>()
    {}

    OCL_Collection( const vector<T>& x ) : vector<T>(x)
    {}

    OCL_Collection<T> including( const T& object ) const
    {
    	OCL_Collection<T> result(*this);
    	result.push_back(object);
    	return result;
    }

    OCL_Collection<T> excluding( const T& object ) const
    {
    	OCL_Collection<T> result;
        const int s = size();
    	for( int index=0; index<s; index++ )
    	{
    		const T& element = operator[](index);
    		if( element!=object )
    		{
    			result.push_back(element);
    		}
    	}
    	return result;
    }

    bool operator!=( const vector<T>& ref ) const
    {
		return !operator==(ref);
    }

    bool operator==( const vector<T>& ref ) const
    {
		if( size()!=ref.size() )
		{
			return false;
		}
        const int s = size();
		for( int index=0; index<s; index++ )
		{
			if( !(operator[](index)==ref[index]) )
			{
				return false;
			}
		}
		return true;
    }

    bool isUnique() const
    {
        const int s = size();
        for( int i=0; i<s; i++ )
        {
            for( int j=i+1; j<s; j++ )
            {
                if( operator[](i) == operator[](j) )
                {
                    return false;
                }
            }
        }
        return true;
    }
};


//----------------------------------------------------------------------------


template <class T> class OCL_Sequence : public OCL_Collection<T>
{
public:
    OCL_Sequence() : OCL_Collection<T>()
    {}

    OCL_Sequence( const vector<T>& x ) : OCL_Collection<T>(x)
    {}

    OCL_Sequence<T> including( const T& object ) const
    {
    	OCL_Sequence<T> result(*this);
    	result.push_back(object);
    	return result;
    }

    OCL_Sequence<T> excluding( const T& object ) const
    {
    	OCL_Sequence<T> result;
        const int s = size();
    	for( int index=0; index<s; index++ )
    	{
    		const T& element = operator[](index);
    		if( element!=object )
    		{
    			result.push_back(element);
    		}
    	}
    	return result;
    }

    void add( const T& value )
    {
        push_back(value);
    }

    void add( const vector<T>& src )
    {
        const int s = src.size();
        for( int index=0; index<s; index++ )
        {
            push_back(src[index]);
        }
    }

    bool operator!=( const OCL_Sequence<T>& ref ) const
    {
    	return OCL_Collection<T>::operator!=(ref);
    }

    bool operator==( const OCL_Sequence<T>& ref ) const
    {
    	return OCL_Collection<T>::operator==(ref);
    }
};


//----------------------------------------------------------------------------


template <class T> class OCL_Bag : public OCL_Collection<T>
{
public:
    OCL_Bag() : OCL_Collection<T>()
    {}

    OCL_Bag( const vector<T>& x ) : OCL_Collection<T>(x)
    {}

    OCL_Bag<T> including( const T& object ) const
    {
    	OCL_Bag<T> result(*this);
    	result.push_back(object);
    	return result;
    }

    OCL_Bag<T> excluding( const T& object ) const
    {
    	OCL_Bag<T> result;
        const int s = size();
    	for( int index=0; index<s; index++ )
    	{
    		const T& element = operator[](index);
    		if( element!=object )
    		{
    			result.push_back(element);
    		}
    	}
    	return result;
    }

    void add( const T& value )
    {
        push_back(value);
    }

    void add( const vector<T>& src )
    {
        const int s = src.size();
        for( int index=0; index<s; index++ )
        {
            push_back(src[index]);
        }
    }

    bool operator!=( const OCL_Bag<T>& ref ) const
    {
        return !operator==(ref);
    }

    bool operator==( const OCL_Bag<T>& ref ) const
    {
        if( size()!=ref.size() )
        {
            return false;
        }
        bool result = true;
        bool* markers = new bool[size()];
        const int s = size();
        memset(markers, false, s*sizeof(bool));
        for( int index1=0; index1<s; index1++ )
        {
            const T& element = operator[](index1);
            bool found = false;
            for( int index2=0; index2<s; index2++ )
            {
                if( !markers[index2] )
                {
                    if( element==ref[index2] )
                    {
                        markers[index2] = true;
                        found = true;
                        break;
                    }
                }
            }
            if( !found )
            {
                result = false;
                break;
            }
        }
        delete[] markers;
        return result;
    }
};


//----------------------------------------------------------------------------


template <class T> class OCL_Set : public OCL_Collection<T>
{
public:
    OCL_Set() : OCL_Collection<T>()
    {}

    OCL_Set( const OCL_Set<T>& x ) : OCL_Collection<T>(x)
    {}

    OCL_Set( const vector<T>& x ) : OCL_Collection<T>()
    {
        add(x);
    }

    OCL_Set<T> including( const T& object ) const
    {
    	OCL_Set<T> result(*this);
    	result.add(object);
    	return result;
    }

    OCL_Set<T> excluding( const T& object ) const
    {
    	OCL_Set<T> result;
        const int s = size();
    	for( int index=0; index<s; index++ )
    	{
    		const T& element = operator[](index);
    		if( element!=object )
    		{
    			result.push_back(element);
    		}
    	}
    	return result;
    }

    void add( const T& value )
    {
        if( !OCL_includes(*this,value) )
        {
            push_back(value);
        }
    }

    void add( const vector<T>& src )
    {
        const int s = src.size();
        for( int index=0; index<s; index++ )
        {
            add( src[index] );
        }
    }

    bool operator!=( const OCL_Set<T>& ref ) const
    {
    	return !operator==(ref);
    }

    bool operator==( const OCL_Set<T>& ref ) const
    {
        if( ref.size()!=size() )
        {
            return false;
        }
        const int s = size();
        for( int index=0; index<s; index++ )
        {
        	if( !OCL_includes(*this,ref[index]) )
        	{
        	    return false;
        	}
        }
        return true;
    }

    OCL_Set<T> operator-( const OCL_Set<T>& ref ) const
    {
    	OCL_Set<T> result;
        const int s = size();
    	for( int index=0; index<s; index++ )
    	{
    		const T& element = operator[](index);
        	if( !OCL_includes(ref,element) )
        	{
        	    result.push_back(element);
        	}
    	}
    	return result;
    }
};


//----------------------------------------------------------------------------


class OCL_Sequence_Integer : public OCL_Sequence<OCL_Integer>
{
public:
    OCL_Sequence_Integer() : OCL_Sequence<OCL_Integer>()
    {}

    OCL_Sequence_Integer( const vector<OCL_Integer>& x ) : OCL_Sequence<OCL_Integer>(x)
    {}

    OCL_Sequence_Integer( OCL_Integer lower, OCL_Integer upper ) : OCL_Sequence<OCL_Integer>()
    {
        for( OCL_Integer value=lower; value<=upper; value++ )
        {
            push_back(value);
        }
    }
};


//----------------------------------------------------------------------------


template <class T> class OCL_Sortable_Sequence : public OCL_Sequence<T>
{
public:
    OCL_Sortable_Sequence() : OCL_Sequence<T>()
    {}

    OCL_Sortable_Sequence( const vector<T>& x ) : OCL_Sequence<T>(x)
    {}

    bool operator!=( const OCL_Sequence<T>& ref ) const
    {
        return OCL_Sequence<T>::operator!=(ref);
    }

    bool operator==( const OCL_Sequence<T>& ref ) const
    {
        return OCL_Sequence<T>::operator==(ref);
    }

    /* Sorts "*this" and "ref" by the values of "ref".
       We only use operator "<" of type "R".
    */
    template<class R>
    void sortBy( vector<R>& ref )
    {
        const int s = size();
        if( s<2 )
        {
            return;
        }
        vector<R> buffer1(ref);
        vector<T> buffer2(*this);
        sort(ref, 0, s-1, buffer1, buffer2);
    }

private:
    // Merge sort. We only use operator "<" of type "R".
    template<class R>
    void sort( vector<R>& ref, int start1, const int end2, vector<R>& buffer1, vector<T>& buffer2 )
    {
        const int s = end2-start1+1;
        if( s<2 )
        {
            return;
        }
        if( s==2 )
        {
            if( !(ref[start1]<ref[end2]) )
            {
                R h1 = ref[start1];
                T h2 = operator[](start1);
                ref[start1] = ref[end2];
                operator[](start1) = operator[](end2);
                ref[end2] = h1;
                operator[](end2) = h2;
            }
            return;
        }
        const int start2 = start1 + s/2;
        const int end1 = start2 - 1;
        sort(ref, start1, end1, buffer1, buffer2);
        sort(ref, start2, end2, buffer1, buffer2);
        int index1=start1, index2=start2, index3=0;
        while( index3<s )
        {
            if( index1>end1 )
            {
                buffer1[index3] = ref[index2];
                buffer2[index3] = operator[](index2);
                index2++;
            }
            else if( index2>end2 )
            {
                buffer1[index3] = ref[index1];
                buffer2[index3] = operator[](index1);
                index1++;
            }
            else
            {
                if( ref[index1]<ref[index2] )
                {
                    buffer1[index3] = ref[index1];
                    buffer2[index3] = operator[](index1);
                    index1++;
                }
                else
                {
                    buffer1[index3] = ref[index2];
                    buffer2[index3] = operator[](index2);
                    index2++;
                }
            }
            index3++;
        }
        for( index3=0; index3<s; index3++ )
        {
            ref[start1] = buffer1[index3];
            operator[](start1) = buffer2[index3];
            start1++;
        }
    }
};


//----------------------------------------------------------------------------


template <class T>
OCL_Set<T> OCL_union_Set( const vector<T>& set1, const vector<T>& set2 )
{
	OCL_Set<T> set3(set1);
	set3.add(set2);
	return set3;
}

template <class T>
OCL_Sequence<T> OCL_union_Sequence( const vector<T>& seq1, const vector<T>& seq2 )
{
	OCL_Sequence<T> seq3(seq1);
	seq3.add(seq2);
	return seq3;
}

template <class T>
OCL_Bag<T> OCL_union( const vector<T>& v1, const vector<T>& v2 )
{
	OCL_Bag<T> bag(v1);
	bag.add(v2);
	return bag;
}


template <class T>
OCL_Bag<T> OCL_intersection_Bag( const vector<T>& bag1, const vector<T>& bag2 )
{
	OCL_Bag<T> result;
	int s1 = bag1.size();
	int s2 = bag2.size();
	bool* markers = new bool[s2];
	memset(markers,false,s2*sizeof(bool));
	for( int index1=0; index1<s1; index1++ )
	{
		const T& element = bag1[index1];
		for( int index2=0; index2<s2; index2++ )
		{
			if( !markers[index2] )
			{
				if( bag2[index2]==element )
				{
					result.push_back(element);
					markers[index2] = true;
				}
			}
		}
	}
	delete[] markers;
	return result;
}

template <class T>
OCL_Set<T> OCL_intersection( const vector<T>& v1, const vector<T>& v2 )
{
	return OCL_Set<T>( OCL_intersection_Bag(v1, v2) );
}


template <class T>
OCL_Set<T> OCL_symmetricDifference( const OCL_Set<T>& set1, const OCL_Set<T>& set2 )
{
	OCL_Set<T> result;
	int index;
	for( index=0; index<set1.size(); index++ )
	{
		const T& element = set1[index];
		if( !OCL_includes(set2, element) )
		{
			result.push_back(element);
		}
	}
	for( index=0; index<set2.size(); index++ )
	{
		const T& element = set2[index];
		if( !OCL_includes(set1, element) )
		{
			result.push_back(element);
		}
	}
	return result;
}


template <class T>
OCL_Sequence<T> OCL_append( const vector<T>& seq, const T& element )
{
	OCL_Sequence<T> result(seq);
	result.push_back(element);
	return result;
}


template <class T>
OCL_Sequence<T> OCL_prepend( const vector<T>& seq, const T& element )
{
	OCL_Sequence<T> result;
	result.push_back(element);
	result.add(seq);
	return result;
}


template <class T>
OCL_Sequence<T> OCL_subSequence( const vector<T>& seq, OCL_Integer lower, OCL_Integer upper )
{
	OCL_Sequence<T> result;
	for( OCL_Integer index=lower-1; index<upper; index++ )
	{
		result.push_back(seq[index]);
	}
	return result;
}


} // /namespace CCM_OCL

#endif // __CCM_OCL__OCL_HELPERS__H__


