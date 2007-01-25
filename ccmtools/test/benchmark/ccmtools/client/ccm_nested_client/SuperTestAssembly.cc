

#include "SuperTestAssembly.h"


using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;

SuperTestAssembly::SuperTestAssembly()
	: state_ (LocalComponents::INACTIVE)
{
}

SuperTestAssembly::~SuperTestAssembly()
{
}

void
SuperTestAssembly::build()
    throw (LocalComponents::CreateFailure)
{
	throw LocalComponents::CreateFailure();
}

void
SuperTestAssembly::build(
  WX::Utils::SmartPtr<LocalComponents::CCMObject> facadeComponent)
  throw (LocalComponents::CreateFailure)
{
  cout << " SuperTestAssembly::build()" << endl;

  int error = 0;
  LocalComponents::HomeFinder* homeFinder;
  homeFinder = HomeFinder::Instance ();
  
  try {
    // find home 
    SmartPtr<TestHome> 
      innerTestHome(dynamic_cast<TestHome*>
		 (homeFinder->find_home_by_name("TestHome").ptr()));

    outerSuperTestComponent = 
      SmartPtr<SuperTest>(dynamic_cast<SuperTest*>(facadeComponent.ptr()));
    innerTestComponent = innerTestHome->create();

    Test_provides_bm = innerTestComponent->provide_bm();

    SuperTest_provides_mb = outerSuperTestComponent->provide_bm();
    outerSuperTestComponent->connect_delegate(Test_provides_bm);
  }
  catch (LocalComponents::HomeNotFound) {
    cout << "DEPLOYMENT ERROR: can't find a home!" << endl;
    error = -1;
  }
  catch (LocalComponents::NotImplemented& e) {
    cout << "DEPLOYMENT ERROR: function not implemented: " 
	 << e.what () << endl;
    error = -1;
  }
  catch (LocalComponents::InvalidName& e) {
    cout << "DEPLOYMENT ERROR: invalid name during connection: " 
	 << e.what () << endl;
    error = -1;
  }
  catch (...) {
    cout << "DEPLOYMENT ERROR: there is something going wrong!" << endl;
    error = -1;
  }
  if (error < 0) {
    throw LocalComponents::CreateFailure();
  } else {
    state_ = LocalComponents::INSERVICE;
  }
}


void
SuperTestAssembly::configuration_complete()
{
  cout << "SuperTestAssembly::configuration_complete()" << endl;
  innerTestComponent->configuration_complete();
}


void
SuperTestAssembly::tear_down()
  throw (LocalComponents::RemoveFailure)
{
  cout << "SuperTestAssembly::tear_down()" << endl;
  int error = 0;

  try {
    outerSuperTestComponent->disconnect_delegate();
    innerTestComponent->remove();
  }
  catch (LocalComponents::HomeNotFound) {
    cout << "TEARDOWN ERROR: can't find a home!" << endl;
    error = -1;
  }
  catch (LocalComponents::NotImplemented& e) {
    cout << "TEARDOWN ERROR: function not implemented: " << e.what() 
	 << endl;
    error = -1;
  }
  catch (...) {
    cout << "TEARDOWN ERROR: there is something going wrong!" << endl;
    error = -1;
  }
  
  state_ = LocalComponents::INACTIVE;
}


LocalComponents::AssemblyState
SuperTestAssembly::get_state()
{
  return state_;
}
