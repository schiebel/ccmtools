
#include <SimpleComputerAssembly.h>

int main(int argc, char** argv)
{
  // Debug tools:
  // We use debug tools defined in the WX::Utils package.
  Debug::instance().set_global(true);

  int error=0;
  SimpleComputerAssembly assembly;

  try {
    assembly.build();

    cout << "== Begin Test Case =============================================" << endl;
    SmartPtr<Cpu> myCpu(dynamic_cast<Cpu*>(assembly.getSessionFacade().ptr()));
    SmartPtr<ProgrammingInterface> prg;
    prg = myCpu->provide_prg();

    prg->execute_cmd("read");
    prg->execute_cmd("write");
    prg->execute_cmd("shutdown");
    
    cout << "== End Test Case ===============================================" << endl;

    assembly.tear_down();
  } 
  catch (LocalComponents::CreateFailure& e) {
    cout << "TEST: can't create assembly!" << endl;
    error = -1;
  }
  catch ( LocalComponents::NotImplemented& e ) {
    cout << "TEST: function not implemented: " << e.what (  ) << endl;
    error = -1;
  }
  catch ( ... )  {
    cout << "TEST: there is something wrong!" << endl;
    error = -1;
  }

  if (error < 0) {
    return error;
  }
}
