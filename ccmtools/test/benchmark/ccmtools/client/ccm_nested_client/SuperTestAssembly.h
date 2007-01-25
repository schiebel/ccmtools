#ifndef _SUPER_TEST_ASSEMBLY_H
#define _SUPER_TEST_ASSEMBLY_H

#include <WX/Utils/debug.h>
#include <WX/Utils/smartptr.h>

#include <LocalComponents/CCM.h>
#include <CCM_Local/HomeFinder.h> 

#include <CCM_Local/CCM_Session_SuperTest/SuperTest_gen.h>
#include <CCM_Local/CCM_Session_SuperTest/SuperTestHome_gen.h>

#include <CCM_Local/CCM_Session_Test/Test_gen.h>
#include <CCM_Local/CCM_Session_Test/TestHome_gen.h>

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;

using namespace CCM_Session_SuperTest;
using namespace CCM_Session_Test;

class SuperTestAssembly
: public LocalComponents::Assembly
{
private:
  LocalComponents::AssemblyState state_;

  // SuperTest
  SmartPtr<SuperTest> outerSuperTestComponent;
  SmartPtr<CCM_Local::Benchmark> SuperTest_provides_mb;
  SmartPtr<CCM_Local::Benchmark> SuperTest_usess_mb;

  // Test
  SmartPtr<Test> innerTestComponent;
  SmartPtr<CCM_Local::Benchmark> Test_provides_bm;


 public:
  SuperTestAssembly();
  virtual ~SuperTestAssembly();
  
  /*
   * Creates required component servers, creates required containers, installs
   * required component homes, instantiates components, configures and 
   * interconnects them according to the assembly descriptor.
   */
  virtual void build()
    throw (LocalComponents::CreateFailure);
  
  /*
   * Build a component assembly based on a given facade component.
   *  
   * Note: This is an CCM extension to support nested components.
   */
  virtual void build(
    WX::Utils::SmartPtr<LocalComponents::CCMObject> facadeComponent)
    throw (LocalComponents::CreateFailure);
  
  /*
   * Call configuration_complete on every component instance in the assembly.
   *
   * Note: This is an CCM extension to support nested components.
   */
  virtual void configuration_complete();
  
  /*
   * Removes all connections between components and destroys all components,
   * homes, containers, and component servers that were created by the build 
   * operation.
   */
  virtual void tear_down()
    throw (LocalComponents::RemoveFailure);
  
  /*
   * Returns whether the assembly is active or inactive.
   */
  virtual LocalComponents::AssemblyState get_state();
};

#endif  /* _SUPER_TEST_ASSEMBLY_H */
