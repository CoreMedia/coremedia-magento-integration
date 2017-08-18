package com.coremedia.livecontext.ecommerce.magento.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.StoreConfigDocument;
import com.coremedia.livecontext.ecommerce.magento.rest.resources.StoreConfigResource;


/**
 * Cache key to implement at least time based caching of store configuration related values
 * obtained via the external rest api, which might be slow.
 */
public class StoreConfigCacheKey extends AbstractMagentoDocumentCacheKey<StoreConfigDocument> {

  private StoreConfigResource resource;


  public StoreConfigCacheKey(String code, StoreConfigResource resource, CommerceCache commerceCache) {
    super(null, code, CONFIG_KEY_STORE_INFO, commerceCache);
    this.resource = resource;
  }


  @Override
  public StoreConfigDocument computeValue(Cache cache) {
    return resource.getStoreConfig(id);
  }


  @Override
  public Object[] getCacheIdArgs() {
    return new Object[]{
            id,
            configKey
    };
  }

}
