/***
 * SimpleComputer Assembly
 *
 ***/

#include <WX/Utils/debug.h>
#include <WX/Utils/smartptr.h>

#include <LocalComponents/CCM.h>
#include <CCM_Local/HomeFinder.h>

#include <CCM_Local/SimpleComputer/CCM_Session_Keyboard/Keyboard_gen.h>
#include <CCM_Local/SimpleComputer/CCM_Session_Keyboard/KeyboardFactory_gen.h>

#include <CCM_Local/SimpleComputer/CCM_Session_BarcodeReader/BarcodeReader_gen.h>
#include <CCM_Local/SimpleComputer/CCM_Session_BarcodeReader/BarcodeReaderFactory_gen.h>

#include <CCM_Local/SimpleComputer/CCM_Session_Monitor/Monitor_gen.h>
#include <CCM_Local/SimpleComputer/CCM_Session_Monitor/MonitorFactory_gen.h>

#include <CCM_Local/SimpleComputer/CCM_Session_Printer/Printer_gen.h>
#include <CCM_Local/SimpleComputer/CCM_Session_Printer/PrinterFactory_gen.h>

#include <CCM_Local/SimpleComputer/CCM_Session_Beamer/Beamer_gen.h>
#include <CCM_Local/SimpleComputer/CCM_Session_Beamer/BeamerFactory_gen.h>

#include <CCM_Local/SimpleComputer/CCM_Session_Cpu/Cpu_gen.h>
#include <CCM_Local/SimpleComputer/CCM_Session_Cpu/CpuFactory_gen.h>

using namespace std;
using namespace WX::Utils;
using namespace CCM_Local;
using namespace SimpleComputer;
using namespace CCM_Session_Keyboard;
using namespace CCM_Session_BarcodeReader;
using namespace CCM_Session_Monitor;
using namespace CCM_Session_Printer;
using namespace CCM_Session_Beamer;
using namespace CCM_Session_Cpu;

class SimpleComputerAssembly
: public LocalComponents::Assembly
{
 private:
  SmartPtr<Cpu> myCpu;
  SmartPtr<Keyboard> myKeyboard;
  SmartPtr<BarcodeReader> myBarcodeReader;
  SmartPtr<Monitor> myMonitor;
  SmartPtr<Monitor> mySecondMonitor;
  SmartPtr<Printer> myPrinter;
  SmartPtr<Beamer> myBeamer;

  SmartPtr<InputDeviceConnector> input_device; 
  SmartPtr<OutputDeviceConnector> output_device_1;
  SmartPtr<OutputDeviceConnector> output_device_2;
  SmartPtr<OutputDeviceConnector> output_device_3;
  SmartPtr<OutputDeviceConnector> output_device_4;
  
  LocalComponents::Cookie Cpu_ck_out_port_01;
  LocalComponents::Cookie Cpu_ck_out_port_02;
  LocalComponents::Cookie Cpu_ck_out_port_03;
  LocalComponents::Cookie Cpu_ck_out_port_04;

  LocalComponents::AssemblyState state_;

 public:
  SimpleComputerAssembly(); 
  virtual ~SimpleComputerAssembly();
    
  virtual void build()
      throw (LocalComponents::CreateFailure);

  virtual void tear_down()
      throw (LocalComponents::RemoveFailure);

  virtual LocalComponents::AssemblyState get_state();

  virtual SmartPtr<LocalComponents::Object> getSessionFacade();
};
