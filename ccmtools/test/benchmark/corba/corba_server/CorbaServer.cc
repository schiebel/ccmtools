/* -*- mode: C++; c-basic-offset: 4 -*-
 *
 *  server.cc
 *
 *  The simplest CORBA program using the Naming Service
 ***/

#include <cstdlib>
#include <iostream>
#include <string>

#include <WX/Utils/Timer.h>
#include <WX/Utils/TimerEvaluation.h>

#include <CORBA.h>
#include <coss/CosNaming.h>

#include <Benchmark.h>
#include <BenchmarkImpl.h>

using namespace std;

//==============================================================================
//  Server main function()
//==============================================================================
int main (int argc, char** argv)
{
    WX::Utils::Timer globalTimer;
    WX::Utils::TimerEvaluation eval;
    globalTimer.start();

    CORBA::ORB_var orb;
    CosNaming::NamingContextExt_var naming;
    
    // Initialize ORB
    int argc_ = 3;
    char* argv_[] = {
        "",
        "-ORBInitRef",
        "NameService=corbaloc:iiop:1.2@localhost:5050/NameService"
    };
    orb = CORBA::ORB_init(argc_, argv_);
    
    // Get reference to initial naming context
    CORBA::Object_var nameobj = orb->resolve_initial_references("NameService");
    naming = CosNaming::NamingContextExt::_narrow(nameobj);



    try { // Server part
        
        // Obtain a reference to the RootPOA and its Manager
        CORBA::Object_var poaobj = orb->resolve_initial_references("RootPOA");
        PortableServer::POA_var poa = PortableServer::POA::_narrow(poaobj);
        
        // Activate POA manager
        PortableServer::POAManager_var mgr = poa->the_POAManager();
        mgr->activate ();

        // Create the servant object
        BenchmarkImpl* servant = new BenchmarkImpl();

        // Activate the Servant
        CORBA::Object_var obj = poa->servant_to_reference(servant);

        // Register object reference with the Nameing Service
        CosNaming::Name name;
        name.length(1);
        name[0].id = CORBA::string_dup("BenchmarkCorbaServer");
        naming->rebind(name,obj);

        // Activate the POA and start serving requests
        cout << "Benchmark CORBA Server is running..." << endl;
	orb->run();
    }
    catch(const CORBA::Exception &) {
        cerr << "Aut'sch: CORBA exception!" << endl;
        return 1;
    }
}
