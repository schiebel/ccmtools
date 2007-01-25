/***
 * SimpleComputer Assembly
 *
 ***/

#include "SimpleComputerAssembly.h"

#include <cassert>
#include <iostream>

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace SimpleComputer;
using namespace CCM_Session_Keyboard;
using namespace CCM_Session_Monitor;
using namespace CCM_Session_Printer;
using namespace CCM_Session_Cpu;


SimpleComputerAssembly::SimpleComputerAssembly() 
  : state_(LocalComponents::INACTIVE)
{
}


SimpleComputerAssembly::~SimpleComputerAssembly() 
{
}


void
SimpleComputerAssembly::build()
  throw (LocalComponents::CreateFailure)
{
  int error = 0;
  LocalComponents::HomeFinder* homeFinder;
  homeFinder = HomeFinder::Instance (  );

  error += local_deploy_KeyboardFactory("KeyboardFactory");
  error += local_deploy_BarcodeReaderFactory("BarcodeReaderFactory");
  error += local_deploy_MonitorFactory("MonitorFactory");
  error += local_deploy_PrinterFactory("PrinterFactory");
  error += local_deploy_BeamerFactory("BeamerFactory");
  error += local_deploy_CpuFactory("CpuFactory");
  if(error) {
    cerr << "BOOTSTRAP ERROR: Can't deploy component homes!" << endl;
    throw LocalComponents::CreateFailure();
  }

  try {
    SmartPtr<KeyboardFactory> myKeyboardFactory(dynamic_cast<KeyboardFactory*>
      (homeFinder->find_home_by_name("KeyboardFactory").ptr()));
    SmartPtr<BarcodeReaderFactory> myBarcodeReaderFactory(dynamic_cast<BarcodeReaderFactory*>
      (homeFinder->find_home_by_name("BarcodeReaderFactory").ptr()));
    SmartPtr<MonitorFactory> myMonitorFactory(dynamic_cast<MonitorFactory*>
      (homeFinder->find_home_by_name("MonitorFactory").ptr()));
    SmartPtr<PrinterFactory> myPrinterFactory(dynamic_cast<PrinterFactory*>
      (homeFinder->find_home_by_name("PrinterFactory").ptr()));
    SmartPtr<BeamerFactory> myBeamerFactory(dynamic_cast<BeamerFactory*>
      (homeFinder->find_home_by_name("BeamerFactory").ptr()));
    SmartPtr<CpuFactory> myCpuFactory(dynamic_cast<CpuFactory*>
      (homeFinder->find_home_by_name("CpuFactory").ptr()));

    myKeyboard = myKeyboardFactory->create();
    myBarcodeReader = myBarcodeReaderFactory->create();
    myMonitor = myMonitorFactory->create();
    mySecondMonitor = myMonitorFactory->create();
    myPrinter = myPrinterFactory->create();
    myBeamer = myBeamerFactory->create();
    myCpu = myCpuFactory->create();

    input_device = myKeyboard->provide_connector();    
    // input_device = myBarcodeReader->provide_connector();    

    output_device_1 = myMonitor->provide_connector();    
    output_device_2 = myPrinter->provide_connector(); 
    output_device_3 = myBeamer->provide_connector(); 
    output_device_4 = mySecondMonitor->provide_connector(); 
    
    myMonitor->Type("Sony 200sf");
    mySecondMonitor->Type("NEC");
    myPrinter->Type("HP 2100");
    myBeamer->Type("SunBeam");

    myCpu->connect_in_port(input_device);

    Cpu_ck_out_port_01 = myCpu->connect_out_port(output_device_1);
    Cpu_ck_out_port_02 = myCpu->connect_out_port(output_device_2);
    Cpu_ck_out_port_03 = myCpu->connect_out_port(output_device_3);
    Cpu_ck_out_port_04 = myCpu->connect_out_port(output_device_4);
    
    myKeyboard->configuration_complete();
    myBarcodeReader->configuration_complete();
    myMonitor->configuration_complete();
    mySecondMonitor->configuration_complete();
    myPrinter->configuration_complete();
    myBeamer->configuration_complete();
    myCpu->configuration_complete();
  } 
  catch ( LocalComponents::HomeNotFound ) {
    cout << "DEPLOYMENT ERROR: can't find a home!" << endl;
    error = -1;
  } 
  catch ( LocalComponents::NotImplemented& e ) {
    cout << "DEPLOYMENT ERROR: function not implemented: " << e.what (  ) << endl;
    error = -1;
  } 
  catch ( LocalComponents::InvalidName& e ) {
    cout << "DEPLOYMENT ERROR: invalid name during connection: " << e.what (  ) << endl;
    error = -1;
  }
  catch ( ... )  {
    cout << "DEPLOYMENT ERROR: there is something wrong!" << endl;
    error = -1;
  }
  if (error < 0) {
    throw  LocalComponents::CreateFailure();
  }
  else {
    state_ = LocalComponents::INSERVICE;
  }
}


void 
SimpleComputerAssembly::tear_down()
  throw (LocalComponents::RemoveFailure)
{
  int error = 0;

  try {

    myCpu->disconnect_in_port();
    myCpu->disconnect_out_port(Cpu_ck_out_port_01);
    myCpu->disconnect_out_port(Cpu_ck_out_port_02);
    myCpu->disconnect_out_port(Cpu_ck_out_port_03);
    myCpu->disconnect_out_port(Cpu_ck_out_port_04);

    myKeyboard->remove();
    myBarcodeReader->remove();
    myMonitor->remove();
    mySecondMonitor->remove();
    myPrinter->remove();
    myBeamer->remove();
    myCpu->remove();
  } 
  catch ( LocalComponents::HomeNotFound ) {
    cout << "TEARDOWN ERROR: can't find a home!" << endl;
    error = -1;
  } 
  catch ( LocalComponents::NotImplemented& e ) {
    cout << "TEARDOWN ERROR: function not implemented: " << e.what (  ) << endl;
    error = -1;
  } 
  catch ( ... )  {
    cout << "TEARDOWN ERROR: there is something wrong!" << endl;
    error = -1;
  }

  error += local_undeploy_KeyboardFactory("KeyboardFactory");
  error += local_undeploy_BarcodeReaderFactory("BarcodeReaderFactory");
  error += local_undeploy_MonitorFactory("MonitorFactory");
  error += local_undeploy_MonitorFactory("PrinterFactory");
  error += local_undeploy_BeamerFactory("BeamerFactory");
  error += local_undeploy_CpuFactory("CpuFactory");
  if(error) {
    cerr << "TEARDOWN ERROR: Can't undeploy component homes!" << endl;
    throw LocalComponents::RemoveFailure();   
  }
  else {
    state_ = LocalComponents::INACTIVE;
  }
}


LocalComponents::AssemblyState 
SimpleComputerAssembly::get_state()
{
  return state_;
}


SmartPtr<LocalComponents::Object> 
SimpleComputerAssembly::getSessionFacade()
{
  return myCpu;
}
