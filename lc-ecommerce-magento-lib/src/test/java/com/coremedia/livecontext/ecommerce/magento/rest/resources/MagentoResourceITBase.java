package com.coremedia.livecontext.ecommerce.magento.rest.resources;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.magento.rest.MagentoRestConnector;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;


/**
 * Base class for all magento rest service connector integration test classes.
 * Hard codes a connction to a magento shop installed somewhere.
 */
public abstract class MagentoResourceITBase {

  @Spy
  protected MagentoRestConnector connector;


  public void init() {
    CommerceConnection cc = Mockito.mock(CommerceConnection.class);
    StoreContext sc = Mockito.mock(StoreContext.class);
    Mockito.when(cc.getStoreContext()).thenReturn(sc);
    Mockito.when(sc.getStoreId()).thenReturn("default");
    DefaultConnection.set(cc);

    connector = new MagentoRestConnector();
    connector.setHost("10.2.2.64");
    connector.setBasePath("/rest");
    connector.setUser("coremedia");
    connector.setPassword("CoreMedia@2017");
    MockitoAnnotations.initMocks(this);
  }

}
