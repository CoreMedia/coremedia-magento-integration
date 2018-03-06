package com.coremedia.livecontext.ecommerce.magento.rest.resources;

import com.coremedia.livecontext.ecommerce.magento.rest.MagentoRestConnector;
import org.springframework.beans.factory.annotation.Required;

public abstract class AbstractMagentoResource {

  private MagentoRestConnector connector;

  @Required
  public void setConnector(MagentoRestConnector connector) {
    this.connector = connector;
  }

  protected MagentoRestConnector getConnector() {
    return connector;
  }
}
