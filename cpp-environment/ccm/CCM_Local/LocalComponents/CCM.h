// $Id: CCM.h,v 1.1 2005/01/11 09:06:57 teiniker Exp $

/***
 * This header file defines a bunch of exceptions and interfaces which
 * are used and implemented by generated component code.
 * This file correspinds with the CCM.idl file that defines all execptions
 * and interfaces in terms of IDL.
 ***/

//==============================================================================
// Local C++ CCM interfaces
//==============================================================================

#ifndef __LOCAL__COMPONENTS__H__
#define __LOCAL__COMPONENTS__H__

#include <string>
#include <vector>
#include <stdexcept>
#include <iostream>

#include <WX/Utils/smartptr.h>
#include <WX/Utils/value.h>


namespace LocalComponents {

  //============================================================================
  // Exceptions
  //============================================================================

  /**
   * This is the base class of all local CCM related exceptions.
   * (compare with CORBA::Exception in the remote case).
   */
  class Exception 
    : public std::exception
  {
  public:
    Exception()
      throw()
      : message_("LocalComponents::Exception")
      {
      }

    Exception(const std::string& message)
      throw()
      : message_(message)
      {
      }

    virtual ~Exception()
      throw()
      {
      }

    virtual const char* what() const 
      throw()
      {
	return message_.c_str();
      }

  private:
    std::string message_;
  };


  /** 
   * This NotImplemented is not part of the CCM specification 
   */
  class NotImplemented
    : public Exception 
  {
  public:
    NotImplemented() 
      throw()
      : Exception("LocalComponents::NotImplemented") 
      {
      }

    NotImplemented(const std::string& message) 
      throw()
      : Exception(message) 
      {
      }
  };


  class InvalidName 
    : public Exception 
  {
  public:
    InvalidName() 
      throw()
      : Exception("LocalComponents::InvalidName" ) 
      {
      }
  };


  class HomeNotFound 
    : public Exception {
  public:
    HomeNotFound() 
      throw()
      : Exception("LocalComponents::HomeNotFound")
      {
      }
  };


  class AlreadyConnected 
    : public Exception 
  {
  public:
    AlreadyConnected()
      throw()
      : Exception("LocalComponents::AlreadyConnected")
      {
      }
  };


  class InvalidConnection 
    : public Exception 
  {
  public:
    InvalidConnection()
      throw()
      : Exception("LocalComponents::InvalidConnection")
      {
      }
  };


  class NoConnection 
    : public Exception 
  {
  public:
    NoConnection()
      throw()
      : Exception("LocalComponents::NoConnection")
      {
      }
  };


  class ExceededConnectionLimit 
    : public Exception 
  {
  public:
    ExceededConnectionLimit()
      throw()
      : Exception("LocalComponents::ExceededConnectionLimit")
      {
      }
  };


  class CookieRequired 
    : public Exception 
  {
  public:
    CookieRequired()
      throw()
      : Exception("LocalComponents::CookieRequired")
      {
      }
  };


  class IllegalState 
    : public Exception 
  {
  public:
    IllegalState()
      throw()
      : Exception("LocalComponents::IllegalState")
      {
      }
  };


  class InvalidConfiguration 
    : public Exception 
  {
  public: 
    InvalidConfiguration()
      throw()
      : Exception("LocalComponents::InvalidConfiguration")
      {
      }    
  };


  typedef unsigned long FailureReason;

  class CreateFailure 
    : public Exception 
  {
  public:
    CreateFailure () 
      throw()
      : Exception("LocalComponents::CreateFailure"), reason_(0) 
      {
      }

    CreateFailure(const FailureReason reason) 
      throw()
      : Exception("LocalComponents::CreateFailure"), reason_(reason) 
      {
      }
  private:
    FailureReason reason_;
  };


  class RemoveFailure 
    : public Exception 
  {
  public:
    RemoveFailure() 
      throw()
      : Exception("LocalComponents::RemoveFailure"), reason_(0) 
      {
      }

    RemoveFailure(const FailureReason reason) 
      throw()
      : Exception("LocalComponents::RemoveFailure"), reason_(reason) 
      {
      }

  private:
    FailureReason reason_;
  }; 
 

  enum CCMExceptionReason {
    SYSTEM_ERROR,
    CREATE_ERROR,
    REMOVE_ERROR,
    DUPLICATE_KEY,
    FIND_ERROR,
    OBJECT_NOT_FOUND,
    NO_SUCH_ENTITY,
    OCL_ERROR
  };


  class CCMException 
    : public Exception 
  {
  public:
    CCMException() 
      throw()
      : Exception("LocalComponents::CCMException"), reason_(SYSTEM_ERROR) 
      {
      }
    
    CCMException(const CCMExceptionReason reason) 
      throw() 
      : Exception("LocalComponents::CCMException"), reason_(reason) 
      {
      }

    CCMException(const std::string& message,
		 const CCMExceptionReason reason=SYSTEM_ERROR ) 
      throw() 
      : Exception(message), reason_(reason) 
      {
      }

  private:
    CCMExceptionReason reason_;
  };


  //============================================================================
  // Common interfaces
  //============================================================================

  /***
   * This is the local substitute for the CORBA::Object interface.
   * Every interface inherits from Object!
   * Note: This interface is not part of the CCM specification!
   ***/
  class Object
    : virtual public WX::Utils::RefCounted {
  public:
    virtual ~Object (  ) {}

    // Simulates the CORBA::Object::get_component() operation defines
    // since CORBA 3.0 (CCM Spec. 1-9)	
    virtual WX::Utils::SmartPtr<Object> get_component() {
	return WX::Utils::SmartPtr<Object>();
    };	
  };


  /***
   * Enterprise Component is an empty callback interface that serves as common
   * base for all component implementations.
   * CCM Specification 3-39, 4-27
   ***/
  class EnterpriseComponent {
  public:
    virtual ~EnterpriseComponent (  ) {}
  };



  //============================================================================
  // Home interfaces
  //============================================================================
  class CCMObject;

  /***
   * The CCMHome interface is interited by the explicit home interface.
   *
   * CCM Specification 1-41
   * Light Weight CCM 4.1.7.5
   * Extension to CCM-Spec: CCMException to remove_component()
   ***/
  class CCMHome
    : virtual public WX::Utils::RefCounted
  {
  public:
    virtual ~CCMHome() {}

    virtual void remove_component(WX::Utils::SmartPtr<CCMObject> component)
        throw(CCMException, RemoveFailure) = 0;
  };


  /***
   * The KeylessCCMHome interface is inherited by the implicit home interface.
   *
   * CCM Specification  1-42
   *
   * Extension to CCM-Spec: CCMException to create_component()
   ***/
  class KeylessCCMHome {
  public:
    virtual ~KeylessCCMHome() {}

    virtual WX::Utils::SmartPtr<CCMObject> create_component()
        throw(CCMException, CreateFailure) = 0;
  };


  /***
   * The HomeExecutorBase is a common base for all home implementations.
   * CCM Specification 3-40
   ***/
  class HomeExecutorBase {
  public:
    virtual ~HomeExecutorBase() {}
  };


  /***
   * The HomeRegistration is an internal interface that may be used by the
   * CORBA component to register its home so it can be located by a
   * HomeFinder
   * CCM Specification 4-34
   ***/
  class HomeRegistration {
  public:
    virtual ~HomeRegistration() {}

    /* The register_home operation is used to register a component home
     * with the HomeFinder so it can by located by a component client.
     */
    virtual void register_home(WX::Utils::SmartPtr<CCMHome> home_ref, 
			       const std::string& home_name) = 0;

    /* The unregister_home operation is used to remove a component home
     * from the HomeFinder.
     */
    virtual void unregister_home(WX::Utils::SmartPtr<CCMHome> home_ref) = 0;

    /*
     * This unregister_home operation is used to remove a component home
     * (defined by the home_name) from the HomeFinder.
     * Note: this method is NOT defined in the CCM specification!!
     */
    virtual void unregister_home(const std::string& home_name) = 0;
  };


  /***
   * Clients can use the HomeFinder interface to obtain homes for particular
   * component types, of particularly homes, or homes that are bound to
   * specific names in a naming service.
   * CCM Spec. 1-42
   ***/
  class HomeFinder
    : public HomeRegistration {
  public:
    virtual ~HomeFinder() {}

    virtual WX::Utils::SmartPtr<LocalComponents::CCMHome>
    find_home_by_component_type(const std::string& comp_repid)
        throw(HomeNotFound) = 0;

    virtual WX::Utils::SmartPtr<LocalComponents::CCMHome>
    find_home_by_name(const std::string& name)
        throw(HomeNotFound) = 0;

    virtual WX::Utils::SmartPtr<LocalComponents::CCMHome>
      find_home_by_type(const std::string& home_repid)
        throw(HomeNotFound) = 0;
  };



  //============================================================================
  // Context interfaces
  //============================================================================

 /***
  * The CCMContext is an internal interface which provides a component instance
  * with access to the common container-provided runtime services.
  * It serves as a "bootstrap" to the various services the container provides
  * for the component.
  * CCM Spec. 4-22
  * Light Weight CCM 4.4.3.2
  ***/
  class CCMContext {
  public:
    virtual ~CCMContext() {}

    virtual HomeExecutorBase* get_CCM_home() = 0;
  };


  /***
   * The SessionContext is an internal interface which provides a component
   * instance with access to the container-provided runtime services. The
   * SessionContext enables the component to simply obtain all the references
   * it may require to implement its behavior.
   * CCM Spec. 4-27
   ***/
  class SessionContext : public CCMContext
    {
    public:
      virtual ~SessionContext() {}

      /***
       * The get_CCM_object operation is used to get the reference used to
       * invoke the component (component reference or facet reference. If this
       * operation is issued outside of the scope of a callback operation, the
       * IllegalState exception is returned.
       ***/
      virtual Object* get_CCM_object()
	throw (IllegalState) = 0;
    };



  //============================================================================
  // Component interfaces
  //============================================================================


  /***
   * The SessionComponent is a callback interface implemented by a session CORBA
   * component. It provides operations for disassociating a context with the
   * component and to manage servant lifetime for a session component.
   * CCM Specification 4-28
   ***/
  class SessionComponent
    : public EnterpriseComponent {
    public:
    virtual ~SessionComponent() {}

    /*
     * The set_session_context operation is used to set the SessionContext
     * of the component. The container calls this operation after a component
     * instance has been created.
     */
    virtual void set_session_context (LocalComponents::SessionContext* ctx)
      throw (CCMException) = 0;

    /*
     * The ccm_activate operation is called by the container to notify a
     * session component that is has been made active.
     */
    virtual void ccm_activate()
      throw (CCMException) = 0;

    /*
     * The ccm_passivate operation is called by the container to notify a
     * session component that it has been made inactive.
     */
    virtual void ccm_passivate()
      throw (CCMException) = 0;

    /*
     * The void ccm_remove operation is called by the container when the
     * servant is about to be destroyed.
     */
    virtual void ccm_remove()
      throw (CCMException) = 0;
  };



  /***
   * The SessionSynchronisation interface is a callback interface that may be
   * optionally be implemented by the session component. It permits the
   * component to be notified of transaction boundaries by the container.
   * CCM Specification 4-29
   ***/
  class SessionSynchronisation {
  public:
    virtual ~SessionSynchronisation (  ) {}

    virtual void after_begin (  )
      throw ( CCMException ) = 0;

    virtual void before_completion (  )
      throw ( CCMException ) = 0;

    virtual void after_completion ( bool committed )
      throw ( CCMException ) = 0;
  };



  typedef std::string FeatureName;
  typedef std::string RepositoryId;
  typedef std::vector<FeatureName> NameList;


  /***
   * The Navigation interface provides generic navigation capabilities. It is
   * inherited by all component interfaces, and may be optionally inherited by
   * any interface that is explicitly designed to be a facet interface for a
   * component.
   * CCM Specification 1-10
   * Light Weight CCM 4.1.4
   ***/
  class Navigation {
  public:
    virtual ~Navigation() {}

    /*
     * The provide_facet operation returns a reference to the facet
     * denoted by the name parameter. If the value of the name parameter does
     * not correspond to one of the component's facets, the InvalidName
     * exception shall be raised.
     */
    virtual WX::Utils::SmartPtr<Object> provide_facet(const std::string& name)
      throw(InvalidName) = 0;
  };


  /***
   * Cookie values are created by multiplex receptacles, and are used to
   * correlate a connect operation with a disconnect operation on multiplex
   * receptacles.
   * CCM Specification 1-18
   ***/

  typedef std::string OctetSeq;

  class Cookie {
  private:
    OctetSeq _cookieValue;
  public:
    Cookie() { 
      _cookieValue = ""; 
    }

    Cookie(const std::string& value) { 
      _cookieValue = value; 
    }

    virtual ~Cookie() {}

    bool operator< (const Cookie& ck) const { 
      return _cookieValue < ck._cookieValue; 
    }

  protected:
    virtual OctetSeq cookieValue() const {  
      return _cookieValue; 
    }

    virtual void cookieValue (const OctetSeq& cookieValue) { 
      _cookieValue = cookieValue; 
    }
  };


  /***
   * The Receptacles interface provides generic operations for connecting
   * to a component's receptacles. The CCMObject interface is derived from
   * Receptacles.
   * CCM Specification 1-18
   * Light Weight CCM 4.1.5.3
   ***/
  class Receptacles {
  public:
    virtual ~Receptacles() {}

    /*
     * The connect() operation connects the object reference specified by
     * the connection parameter to the receptacle specified by the name
     * parameter on the target component.
     * multiplex receptacle: the operation returns a cookie value that can
     * be used subsequently to disconnect the object reference.
     * simplex receptacle: the return value is a nil.
     */
    virtual Cookie connect(const FeatureName& name, 
			    WX::Utils::SmartPtr<Object> connection)
      throw(InvalidName, 
	    InvalidConnection, 
	    AlreadyConnected, 
	    ExceededConnectionLimit) = 0;

    /*
     * Simplex receptacle: the operation will disassociate any object
     * reference currently connected to the receptacle - the cookie
     * parameter is ignored.
     * multiplex receptacle: the operation disassociates the object reference
     * associated with the cookie value from the receptacle.
     */
    virtual void disconnect(const FeatureName& name, 
			    const Cookie& ck)
      throw(InvalidName, 
	    InvalidConnection, 
	    CookieRequired, 
	    NoConnection) = 0;
  };


  /***
   * All interfaces for components types are derived from CCMObject.
   * CCM Specification 1-52
   * Light Weight CCM 4.1.11.1
   ***/
  class CCMObject : virtual public Object,
    public Navigation, public Receptacles {
    public:

    /*
     * The get_ccm_home() operation returns a CCMHome reference to the
     * home which manages this component.
     */
    virtual HomeExecutorBase* get_ccm_home() = 0;

    /*
     * This operation is called by a configurator to indicate that the
     * initial component configuration has completed.
     * If the component determines that it is not sufficiently configured
     * to allow normal client access, it raises the InvalidConfiguration
     * exception.
     */
    virtual void configuration_complete()
        throw(InvalidConfiguration) = 0;

    /*
     * This operation is used to delete a component.
     * Application failures during remove may raise the RemoveFailure
     * exception.
     */
    virtual void remove()
        throw(RemoveFailure) = 0;
  };



  //============================================================================
  // Component configuration
  //============================================================================

  class WrongComponentType {};

  /***
   * A configurator is an object that encapsulates a specific attribute
   * configuration that can be reproduced an many instances of a component type.
   * A configurator is intended to invoke attribute set operations on the target
   * component.
   * CCM Specification 1-47
   ***/
  class Configurator {
  public:
    virtual ~Configurator (  ) {}

    /*
     * The configure (  ) operation establishes its encapsulated configuration
     * on the target component. If the target component is not of the type
     * expected by the configurator, the operation shall raise the
     * WrongComponentType exception.
     */
    virtual void configure ( const CCMObject& comp )
      throw ( WrongComponentType ) = 0;
  };



  //============================================================================
  // Component assembling
  //============================================================================

  enum AssemblyState { INACTIVE,
		       INSERVICE};

  class InvalidLocation {};
  class InvalidAssembly {};


  /**
   * The Assembly interface represents an assembly instantion. It is used to
   * build up and tear down component assemblies.
   *      
   * CCM Specification 6-73
   **/
  class Assembly 
    : virtual public WX::Utils::RefCounted {
    public:

    /*
     * Creates required component servers, creates required containers, installs
     * required component homes, instantiates components, configures and 
     * interconnects them according to the assembly descriptor.
     */
    virtual void build()
        throw (CreateFailure) = 0;

    /*
     * Removes all connections between components and destroys all components,
     * homes, containers, and component servers that were created by the build 
     * operation.
     */
    virtual void tear_down()
        throw (RemoveFailure) = 0;

    /*
     * Returns whether the assembly is active or inactive.
     */
    virtual AssemblyState get_state() = 0;


    /*
     * Build a component assembly based on a given facade component.
     *  
     * Note: This is an CCM extension to support nested components.
     */
    virtual void build(WX::Utils::SmartPtr<CCMObject> facadeComponent)
        throw(CreateFailure) = 0;

    /*
     * Call configuration_complete on every component instance in the assembly.
     *
     * Note: This is an CCM extension to support nested components.
     */
    virtual void configuration_complete() = 0;
  };


  class AssemblyFactory
    : virtual public WX::Utils::RefCounted {
    public:
    virtual WX::Utils::SmartPtr<Assembly> create() 
        throw(CreateFailure) = 0;

  };

} // /namespace LocalComponents


#endif // __LOCAL__COMPONENTS__H__


