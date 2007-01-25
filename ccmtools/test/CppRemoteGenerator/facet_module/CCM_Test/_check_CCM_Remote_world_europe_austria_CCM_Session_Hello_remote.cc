#include <cstdlib> 
#include <iostream>
#include <string>
#include <CCM_Utils/Debug.h>
#include <CCM/CCMContainer.h>

#include <CORBA.h>
#include <coss/CosNaming.h>

#include <CCM_Remote/world/europe/austria/CCM_Session_Hello/HelloHome_remote.h>
#include <world_europe_austria_Hello.h>

using namespace std;
using namespace CCM_Utils;

//==============================================================================
// Implementation of remote client test
//==============================================================================

int main (int argc, char *argv[])
{
  Debug::set_global(true); 
  DEBUGNL("C++_remote_test_client()");

  int argc_=3;
  char ns[200];
  sprintf(ns,"NameService=%s",getenv("CCM_NAME_SERVICE"));
  char* argv_[] = {"", "-ORBInitRef", ns };
  DEBUGNL(ns);

  // Initialize ORB 
  CORBA::ORB_var orb = CORBA::ORB_init(argc_, argv_);


  /**
   * Server-side code
   */ 
  CCM::register_all_factories (orb);

  int error = deploy_HelloHome(orb, "HelloHome:1.0");
  if(!error) {
    cout << "HelloHome stand-alone server is running..." << endl;
  }
  else {
    cerr << "ERROR: Can't start components!" << endl;
    assert(0);
  }

  // orb->run();
	

  /**
   * Client-side code
   */
  CORBA::Object_var obj = orb->resolve_initial_references ("NameService");
  CosNaming::NamingContextExt_var nc =
    CosNaming::NamingContextExt::_narrow (obj);

  // Deployment 

  // Find ComponentHomes in the Naming-Service
  obj = nc->resolve_str ("HelloHome:1.0");
  assert (!CORBA::is_nil (obj));
  ::world::europe::austria::HelloHome_var myHelloHome = ::world::europe::austria::HelloHome::_narrow (obj);

  // Create component instances
  ::world::europe::austria::Hello_var myHello = myHelloHome->create();

  // Provide facets   
  ::world::europe::austria::Console_var Consoleconsole = myHello->provide_console();


	
  myHello->configuration_complete();


  DEBUGNL("==== Begin Test Case =============================================" );    
  /* 
   * Test Case for: typedef sequence<long>
   */
  {
    ::world::europe::austria::LongList_var list_1 = new  ::world::europe::austria::LongList;
    ::world::europe::austria::LongList_var list_2 = new  ::world::europe::austria::LongList;
    list_1->length(5);
    list_2->length(5);
    for(int i=0;i<5;i++) {
      (*list_1)[i] = i;
      (*list_2)[i] = i+i;
    }
    
    ::world::europe::austria::LongList_var list_3;
    ::world::europe::austria::LongList_var list_r;
    
    list_r = Consoleconsole->foo1(list_1,list_2,list_3);
    
    for(CORBA::Long i=0;i<list_r->length();i++) {
      assert((*list_r)[i]== i);
    }
    for(CORBA::Long i=0;i<list_2->length();i++) {
      assert((*list_2)[i]== i);
    }
    for(CORBA::Long i=0;i<list_3->length();i++) {
      assert((*list_3)[i]== i+i);
    }
  }

  /* 
   * Test Case for: typedef sequence<string>
   */
  {
    ::world::europe::austria::StringList_var list_1 = new ::world::europe::austria::StringList;
    ::world::europe::austria::StringList_var list_2 = new ::world::europe::austria::StringList;
    list_1->length(5);
    list_2->length(5);
    for(int i=0;i<5;i++) {
      (*list_1)[i] = "egon";
      (*list_2)[i] = "andrea";
    }
    
    ::world::europe::austria::StringList_var list_3;
    ::world::europe::austria::StringList_var list_r;
    
    list_r = Consoleconsole->foo2(list_1,list_2,list_3);
    
    /*
    for(CORBA::Long i=0;i<list_r->length();i++) {
      assert((*list_r)[i]== i);
    }
    for(CORBA::Long i=0;i<list_2->length();i++) {
      assert((*list_2)[i]== i);
    }
    for(CORBA::Long i=0;i<list_3->length();i++) {
      assert((*list_3)[i]== i+i);
    }
    */
  }

  /*
   * Test Case for: struct Value { long id; string name; };
   */
  {
    ::world::europe::austria::Person p1;
    ::world::europe::austria::Person_var p2 = new ::world::europe::austria::Person;
    ::world::europe::austria::Person_var p3;
    ::world::europe::austria::Person_var result;
    
    p1.name = CORBA::string_dup("egon");   
    p1.id = 3;
    
    p2->name = CORBA::string_dup("andrea"); 
    p2->id = 23;
    
    result = Consoleconsole->foo3(p1,p2,p3);
    
    assert(strcmp(p3->name, "andrea") == 0);
    assert(strcmp(p2->name, "egon") == 0);
    assert(strcmp(result->name, "egonandrea") == 0);
  }

  /* 
   * Test Case for: typedef sequence<struct>
   */
  {
    ::world::europe::austria::PersonMap_var map_1 = new ::world::europe::austria::PersonMap;
    ::world::europe::austria::PersonMap_var map_2 = new ::world::europe::austria::PersonMap;
    map_1->length(5);
    map_2->length(5);
    for(int i=0;i<5;i++) {
      (*map_1)[i].name = "1";
      (*map_1)[i].id   = i;
      (*map_2)[i].name = "2";
      (*map_2)[i].id   = i+i;
    }
    
    ::world::europe::austria::PersonMap_var map_3;
    ::world::europe::austria::PersonMap_var map_r;
    
    map_r = Consoleconsole->foo4(map_1,map_2,map_3);
    
    for(CORBA::Long i=0;i<map_r->length();i++) {
      assert((*map_r)[i].id == i);
    }
    for(CORBA::Long i=0;i<map_2->length();i++) {
      assert((*map_2)[i].id == i);
    }
    for(CORBA::Long i=0;i<map_3->length();i++) {
      assert((*map_3)[i].id == i+i);
    }
  }


  DEBUGNL("==== End Test Case ===============================================" );    


  // Un-Deployment

  // Destroy component instances
  myHello->remove();
}
