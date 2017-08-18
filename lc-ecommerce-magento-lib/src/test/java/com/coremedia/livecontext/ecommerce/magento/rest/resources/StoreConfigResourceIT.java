package com.coremedia.livecontext.ecommerce.magento.rest.resources;

import com.coremedia.livecontext.ecommerce.magento.rest.documents.StoreConfigDocument;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;


/**
 * Integration test to access store configs.
 */
public class StoreConfigResourceIT extends MagentoResourceITBase {
  private static final Logger LOG = LoggerFactory.getLogger(StoreConfigResourceIT.class);


  @InjectMocks
  private StoreConfigResource storeConfigResource = new StoreConfigResource(); // NOPMD - this is actually is used


  @BeforeMethod
  public void init() {
    LOG.info("setUp()");
    super.init();
  }


  @Test
  public void testGetAllStoreConfigs() {
    List<StoreConfigDocument> configs = storeConfigResource.getStoreConfigs();
    Assert.assertEquals(configs.size(), 1, "Unexpected number of configs encountered.");
  }


  @Test
  public void testGetStoreConfig() {
    StoreConfigDocument config = storeConfigResource.getStoreConfig("default");
    Assert.assertEquals(config.getCode(), "default", "Unexpected result code for store config queried via its code.");
  }

}
