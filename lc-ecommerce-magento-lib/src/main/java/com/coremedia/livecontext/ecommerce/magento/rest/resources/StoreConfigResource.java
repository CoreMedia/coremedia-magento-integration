package com.coremedia.livecontext.ecommerce.magento.rest.resources;

import com.coremedia.livecontext.ecommerce.magento.rest.documents.StoreConfigDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Arrays;
import java.util.List;

/**
 * Resource representing a store configuration.
 */
public class StoreConfigResource extends AbstractMagentoResource {

  private static final Logger LOG = LoggerFactory.getLogger(StoreConfigResource.class);

  private static final String STORES_CONFIG_PATH = "/store/storeConfigs";

  /**
   * Returns a list of all store configuration documents.
   *
   * @return list of Magento store config documents from the underlying resource
   */
  public List<StoreConfigDocument> getStoreConfigs() {
    StoreConfigDocument[] docs = getConnector().performGet(STORES_CONFIG_PATH, StoreConfigDocument[].class);
    return Arrays.asList(docs);
  }

  /**
   * Returns the store configuration for a store.
   *
   * @param code Magento store code to get config for
   * @return first store config from target system or null if not available for the given code
   */
  public StoreConfigDocument getStoreConfig(String code) {
    MultiValueMap queryParams = new LinkedMultiValueMap();
    queryParams.add("storeCodes[]", code); // Must be provided as an array query param

    StoreConfigDocument[] docs = getConnector()
            .performGet(STORES_CONFIG_PATH, StoreConfigDocument[].class, queryParams, null);

    if (docs == null) {
      return null;
    }

    LOG.info("getStoreConfig() Magento backend returned {} store configs.", docs.length);
    if (docs.length == 0) {
      return null;
    }

    return docs[0];
  }
}
