package com.helger.peppol.app.config;

import com.helger.commons.annotations.IsSPIImplementation;
import com.helger.commons.microdom.convert.IMicroTypeConverterRegistrarSPI;
import com.helger.commons.microdom.convert.IMicroTypeConverterRegistry;
import com.helger.peppol.crm.CRMGroup;
import com.helger.peppol.crm.CRMGroupMicroTypeConverter;
import com.helger.peppol.crm.CRMSubscriber;
import com.helger.peppol.crm.CRMSubscriberMicroTypeConverter;

@IsSPIImplementation
public final class AppMicroTypeConverterRegistarSPI implements IMicroTypeConverterRegistrarSPI
{
  public void registerMicroTypeConverter (final IMicroTypeConverterRegistry aRegistry)
  {
    // CRM stuff
    aRegistry.registerMicroElementTypeConverter (CRMGroup.class, new CRMGroupMicroTypeConverter ());
    aRegistry.registerMicroElementTypeConverter (CRMSubscriber.class, new CRMSubscriberMicroTypeConverter ());
  }
}
