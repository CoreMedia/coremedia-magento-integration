package com.coremedia.livecontext.ecommerce.magento.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.magento.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.magento.rest.resources.CatalogResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Cache key to implement at least time based caching of product related values
 * obtained via the external rest api, which might be slow.
 */
public class ProductCacheKey extends AbstractMagentoDocumentCacheKey<ProductDocument> {
  private static final Logger LOG = LoggerFactory.getLogger(ProductCacheKey.class);

  private CatalogResource resource;

  private ProductDocument productDocument;


  public ProductCacheKey(ProductDocument product, StoreContext context, String id, CatalogResource resource, CommerceCache commerceCache) {
    super(context, id, CONFIG_KEY_PRODUCT, commerceCache);
    this.resource = resource;
    this.productDocument = product;

    if (productDocument != null) {
      LOG.info("() url_key='{}'", productDocument.getCustomAttribute("url_key"));
    }
    if (!CommerceIdHelper.isSkuId(id)) {
      String msg = id + " (is neither a product nor sku id).";
      LOG.warn(msg);
      throw new InvalidIdException(msg);
    }
  }


  @Override
  public ProductDocument computeValue(Cache cache) {
    String externalId = CommerceIdHelper.convertToExternalId(id);
    return productDocument != null ? productDocument : resource.getProductBySku(externalId);
  }


  @Override
  public Object[] getCacheIdArgs() {
    return new Object[]{
            id,
            configKey,
            storeContext.getSiteId(),
            storeContext.getStoreId(),
            storeContext.getLocale(),
            storeContext.getCurrency()
    };
  }

}
