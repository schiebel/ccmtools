#ifndef _SUPER_TEST_ASSEMBLY_FACTORY_H
#define _SUPER_TEST_ASSEMBLY_FACTORY_H

#include <LocalComponents/CCM.h>
 
class SuperTestAssemblyFactory
: virtual public LocalComponents::AssemblyFactory
{
 public:
  SuperTestAssemblyFactory();
  virtual ~SuperTestAssemblyFactory();
  
  /*
   * Returns a new instance of TypesafeStocktakeAssembly which is used by
   * the Main component's home to establish a nested component.
   */
  virtual WX::Utils::SmartPtr<LocalComponents::Assembly> create()
    throw (LocalComponents::CreateFailure);
};
 
#endif  /* _SUPER_TEST_ASSEMBLY_FACTORY_H */
