::world::europe::austria::Color consoleFacetAdapter::foo5(::world::europe::austria::Color c1, ::world::europe::austria::Color& c2, ::world::europe::austria::Color_out c3) throw(CORBA::SystemException)
{
  DEBUGNL(" consoleFacetAdapter::foo5()");

  CCM_Local::world::europe::austria::Color parameter_c1;
  switch(c1) {
    case ::world::europe::austria::red: 
      parameter_c1 = CCM_Local::world::europe::austria::red; 
      break;
    case ::world::europe::austria::green: 
      parameter_c1 = CCM_Local::world::europe::austria::green; 
      break;
    case ::world::europe::austria::blue: 
      parameter_c1 = CCM_Local::world::europe::austria::blue; 
      break;
    case ::world::europe::austria::black: 
      parameter_c1 = CCM_Local::world::europe::austria::black; 
      break;
    case ::world::europe::austria::orange: 
      parameter_c1 = CCM_Local::world::europe::austria::orange; 
      break;
  }

  CCM_Local::world::europe::austria::Color parameter_c2;
  switch(c2) {
    case ::world::europe::austria::red: 
      parameter_c2 = CCM_Local::world::europe::austria::red; 
      break;
    case ::world::europe::austria::green: 
      parameter_c2 = CCM_Local::world::europe::austria::green; 
      break;
    case ::world::europe::austria::blue: 
      parameter_c2 = CCM_Local::world::europe::austria::blue; 
      break;
    case ::world::europe::austria::black: 
      parameter_c2 = CCM_Local::world::europe::austria::black; 
      break;
    case ::world::europe::austria::orange: 
      parameter_c2 = CCM_Local::world::europe::austria::orange; 
      break;
  }

  CCM_Local::world::europe::austria::Color parameter_c3;


  CCM_Local::world::europe::austria::Color result;
  try {
    result = local_adapter->foo5( parameter_c1,  parameter_c2,  parameter_c3);
  }
  
  catch(...) {
    throw CORBA::SystemException();	
  }

  switch(parameter_c2) {
  case CCM_Local::world::europe::austria::red: 
    c2 = ::world::europe::austria::red; 
    break;
  case CCM_Local::world::europe::austria::green: 
    c2 = ::world::europe::austria::green; 
    break;
  case CCM_Local::world::europe::austria::blue: 
    c2 = ::world::europe::austria::blue; 
    break;
  case CCM_Local::world::europe::austria::black: 
    c2 = ::world::europe::austria::black; 
    break;
  case CCM_Local::world::europe::austria::orange: 
    c2 = ::world::europe::austria::orange; 
    break;
  }

  switch(parameter_c3) {
  case CCM_Local::world::europe::austria::red: 
    c3 = ::world::europe::austria::red; 
    break;
  case CCM_Local::world::europe::austria::green: 
    c3 = ::world::europe::austria::green; 
    break;
  case CCM_Local::world::europe::austria::blue: 
    c3 = ::world::europe::austria::blue; 
    break;
  case CCM_Local::world::europe::austria::black: 
    c3 = ::world::europe::austria::black; 
    break;
  case CCM_Local::world::europe::austria::orange: 
    c3 = ::world::europe::austria::orange; 
    break;
  }

  ::world::europe::austria::Color return_value;
  switch(result) {
  case CCM_Local::world::europe::austria::red: 
    return_value = ::world::europe::austria::red; 
    break;
  case CCM_Local::world::europe::austria::green: 
    return_value = ::world::europe::austria::green; 
    break;
  case CCM_Local::world::europe::austria::blue:
    return_value = ::world::europe::austria::blue; 
    break;
  case CCM_Local::world::europe::austria::black: 
    return_value = ::world::europe::austria::black; 
    break;
  case CCM_Local::world::europe::austria::orange: 
    return_value = ::world::europe::austria::orange; 
    break;
  }
  return return_value;
}
