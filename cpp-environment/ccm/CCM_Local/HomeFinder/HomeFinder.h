//==============================================================================
// home finder
//==============================================================================

#ifndef __CCM__LOCAL__HOMEFINDER__H__
#define __CCM__LOCAL__HOMEFINDER__H__

#include <map>
#include <WX/Utils/smartptr.h>
#include <LocalComponents/CCM.h>

namespace CCM_Local {

typedef std::map<std::string, 
  WX::Utils::SmartPtr<LocalComponents::CCMHome> > HomePoolMap;

/***
 * This is a local version of a HomeFinder which returns local (C++) references
 * of component homes. We use a singleton pattern to implement the local
 * HomeFinder. Instance (  ) provides a pointer to the single instance.
 *
 * Note: this is a simple hard coded version - there will be a dynamic map...
 ***/
class HomeFinder
  : public LocalComponents::HomeFinder
{
 private:
  static LocalComponents::HomeFinder* instance;
  HomePoolMap HomePool;

 protected:
  HomeFinder (  );
  virtual ~HomeFinder (  );

 public:
  static LocalComponents::HomeFinder* Instance();

  // methods from HomeFinder
  WX::Utils::SmartPtr<LocalComponents::CCMHome>
  find_home_by_name(const std::string& name)
    throw(LocalComponents::HomeNotFound);

  WX::Utils::SmartPtr<LocalComponents::CCMHome>
  find_home_by_type(const std::string& home_repid)
    throw (LocalComponents::HomeNotFound);

  WX::Utils::SmartPtr<LocalComponents::CCMHome>
  find_home_by_component_type ( const std::string& comp_repid )
    throw (LocalComponents::HomeNotFound);

  // methods from HomeRegistration
  void register_home(WX::Utils::SmartPtr<LocalComponents::CCMHome> home_ref, 
		     const std::string& home_name);
  void unregister_home(WX::Utils::SmartPtr<LocalComponents::CCMHome> home_ref);
  void unregister_home(const std::string& home_name);
};

} // /namespace CCM_Local

#endif // __CCM__LOCAL__HOMEFINDER__H__


