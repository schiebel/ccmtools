
#include <LocalComponents/CCM.h>
#include <CCM_Local/HomeFinder.h>
#include <WX/Utils/debug.h>
#include <WX/Utils/smartptr.h>

#include <CCM_Local/BigBusiness/CCM_Session_CarRental/CarRentalHome_gen.h>
#include <CCM_Local/BigBusiness/CCM_Session_CarRental/CarRental_gen.h>


using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace BigBusiness;
using namespace CCM_Session_CarRental;

int main(int argc, char *argv[])
{
  cout << ">>>> Start Test Client: " << __FILE__ << endl;

  // Debug tools:
  // We use debug tools defined in the WX::Utils package.
  Debug::instance().set_global(true);


  // Component bootstrap:
  // We get an instance of the local HomeFinder and register the deployed
  // component- and mirror component home.
  // Here we can also decide to use a Design by Contract component.  	
  int error = 0;
  LocalComponents::HomeFinder* homeFinder;
  homeFinder = HomeFinder::Instance (  );
  error  = local_deploy_CarRentalHome("CarRentalHome");
  if(error) {
    cerr << "BOOTSTRAP ERROR: Can't deploy component homes!" << endl;
    return(error);
  }


  // Component deployment:
  // We use the HomeFinder method find_home_by_name() to get a smart pointer 
  // to a component home. From a component home, we get a smart pointer to a 
  // component instance using the create() method.
  // Component and mirror component are connected via provide_facet() and 
  // connect() methods.
  // The last step of deployment is to call configuration_complete() that 
  // forces components to run the ccm_set_session_context() and ccm_activate() 
  // callback methods.
  try {
    SmartPtr<CarRentalHome> myCarRentalHome(dynamic_cast<CarRentalHome*>
      (homeFinder->find_home_by_name("CarRentalHome").ptr()));

    SmartPtr<CarRental> myCarRental = myCarRentalHome->create();

    SmartPtr<CCM_Local::BigBusiness::CustomerMaintenance> 
      maintenance = myCarRental->provide_maintenance();    
    SmartPtr<CCM_Local::BigBusiness::CustomerBusiness> 
      business = myCarRental->provide_business();

    myCarRental->configuration_complete();

  // Component test:
  // After component deployment, we can access components and their facets.
  // Usually, the test cases for facets and receptacles are implemened in the
  // mirror component. But for supported interfaces and component attributes, 
  // we can realize test cases in the following section.
    business->dollars_per_mile(5.5);
    
    CCM_Local::BigBusiness::Customer person;
    person.id = 1;
    person.first_name = "Franz";
    person.last_name = "Kafka";
    maintenance->createCustomer(person);

    business->addCustomerMiles(1, 120.0); 
    person = maintenance->retrieveCustomer(1);
    double dollars = business->getCustomerDollars(1); 

    cout << " Customer: " << person.first_name << " " << person.last_name << endl;
    cout << " Miles: " <<  person.mileage << endl;
    cout << " to pay: " << dollars << " Dollars" << endl;

    myCarRental->remove();
  } 
  catch ( ... )  {
    cout << "Client: there is something wrong!" << endl;
    error = -1;
  }

  // Component tear down:
  // Finally, the component and mirror component instances are disconnected 
  // and removed. Thus component homes can be undeployed.

  error += local_undeploy_CarRentalHome("CarRentalHome");
  if(error) {
    cerr << "TEARDOWN ERROR: Can't undeploy component homes!" << endl;
    return error;
  }
  cout << ">>>> Stop Test Client: " << __FILE__ << endl;
}
