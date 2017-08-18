package com.coremedia.livecontext.ecommerce.magento.rest.resources;

import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.magento.common.MagentoStoreContextHelper;
import com.coremedia.livecontext.ecommerce.magento.rest.MagentoRestConnector;
import org.springframework.beans.factory.annotation.Required;


public abstract class AbstractMagentoResource {
  private MagentoRestConnector connector;

  protected String getStoreCode() {
    StoreContext storeContext = MagentoStoreContextHelper.getCurrentContext();
    return (String) storeContext.get(MagentoStoreContextHelper.CODE);
  }

  @Required
  public void setConnector(MagentoRestConnector connector) {
    this.connector = connector;
  }

  protected MagentoRestConnector getConnector() {
    return connector;
  }
}
