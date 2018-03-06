package com.coremedia.livecontext.ecommerce.magento.rest.resources;

import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.magento.rest.MagentoRestConnector;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Base class for all magento rest service connector integration test classes.
 * Hard codes a connction to a magento shop installed somewhere.
 */
public abstract class MagentoResourceITBase {

  @Spy
  protected MagentoRestConnector connector;

  @BeforeEach
  void setUp() {
    CommerceConnection cc = mock(CommerceConnection.class);
    StoreContext sc = mock(StoreContext.class);
    when(cc.getStoreContext()).thenReturn(sc);
    when(sc.getStoreId()).thenReturn("default");

    connector = new MagentoRestConnector();
    connector.setHost("10.2.2.64");
    connector.setBasePath("/rest");
    connector.setUser("coremedia");
    connector.setPassword("CoreMedia@2017");
    MockitoAnnotations.initMocks(this);
  }
}
