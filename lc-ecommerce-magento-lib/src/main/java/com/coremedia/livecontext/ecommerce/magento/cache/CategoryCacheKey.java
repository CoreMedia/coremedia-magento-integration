package com.coremedia.livecontext.ecommerce.magento.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.magento.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.magento.rest.resources.CatalogResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cache key to implement at least time based caching of category related values
 * obtained via the external rest api, which might be slow.
 */
public class CategoryCacheKey extends AbstractMagentoDocumentCacheKey<CategoryDocument> {

  private static final Logger LOG = LoggerFactory.getLogger(CategoryCacheKey.class);

  private String storeId;
  private CatalogResource resource;
  private CategoryDocument categoryDocument;

  public CategoryCacheKey(CategoryDocument category, StoreContext context, CommerceId id, CatalogResource resource,
                          CommerceCache commerceCache) {
    super(context, id, CONFIG_KEY_CATEGORY, commerceCache);

    this.storeId = context.getStoreId();
    this.resource = resource;
    this.categoryDocument = category;

    LOG.info("Created CategoryCacheKey: {}", getCacheIdentifier());
    if (!CommerceIdHelper.isCategoryId(id.getExternalId().get())) {
      String msg = id + " (is not a category id)";
      LOG.warn(msg);
      throw new InvalidIdException(msg);
    }
  }

  @Override
  public CategoryDocument computeValue(Cache cache) {
    String externalId = CommerceIdHelper.convertToExternalId(id);
    LOG.info("computeValue() Using category cache: {}", externalId);

    if (categoryDocument != null) {
      return categoryDocument;
    }

    if (externalId.equals("ROOT")) {
      return resource.getRootCategory(storeId);
    }

    return resource.getCategoryById(storeId, externalId);
  }

  @Override
  public Object[] getCacheIdArgs() {
    return new Object[]{
            id,
            configKey,
            storeContext.getSiteId(),
            storeContext.getStoreId(),
            storeContext.getLocale()
    };
  }
}
