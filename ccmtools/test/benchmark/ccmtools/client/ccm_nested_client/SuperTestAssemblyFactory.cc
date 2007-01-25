#include "SuperTestAssemblyFactory.h"
#include "SuperTestAssembly.h"

using namespace std;

SuperTestAssemblyFactory::SuperTestAssemblyFactory()
{
  cout << "+SuperTestAssemblyFactory::SuperTestAssemblyFactory()" << endl;
}
SuperTestAssemblyFactory::~SuperTestAssemblyFactory()
{
  cout << "-SuperTestAssemblyFactory::~SuperTestAssemblyFactory()" << endl;
}

WX::Utils::SmartPtr<LocalComponents::Assembly>
SuperTestAssemblyFactory::create()
  throw(LocalComponents::CreateFailure)
{
  cout << " SuperTestAssemblyFactory::create()" << endl;
  WX::Utils::SmartPtr<LocalComponents::Assembly> 
    assembly(new SuperTestAssembly());
  return assembly;
}
