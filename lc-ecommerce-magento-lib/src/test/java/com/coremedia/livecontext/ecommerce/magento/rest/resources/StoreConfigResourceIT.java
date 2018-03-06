package com.coremedia.livecontext.ecommerce.magento.rest.resources;

import com.coremedia.livecontext.ecommerce.magento.rest.documents.StoreConfigDocument;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration test to access store configs.
 */
class StoreConfigResourceIT extends MagentoResourceITBase {

  private static final Logger LOG = LoggerFactory.getLogger(StoreConfigResourceIT.class);

  @InjectMocks
  private StoreConfigResource storeConfigResource = new StoreConfigResource(); // NOPMD - this is actually is used

  @Test
  void testGetAllStoreConfigs() {
    List<StoreConfigDocument> configs = storeConfigResource.getStoreConfigs();
    assertThat(configs).as("Unexpected number of configs encountered.").hasSize(1);
  }

  @Test
  void testGetStoreConfig() {
    StoreConfigDocument config = storeConfigResource.getStoreConfig("default");
    assertThat(config.getCode()).as("Unexpected result code for store config queried via its code.").isEqualTo("default");
  }
}
