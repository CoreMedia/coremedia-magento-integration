package com.coremedia.livecontext.ecommerce.magento.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.magento.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.CategoryProductLinkDocument;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.magento.rest.resources.CatalogResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * Cache key to implement at least time based caching of product related values
 * obtained via the external rest api, which might be slow.
 */
public class ProductsbyCategoryCacheKey extends AbstractMagentoDocumentCacheKey<List<ProductDocument>> {
  private static final Logger LOG = LoggerFactory.getLogger(ProductsbyCategoryCacheKey.class);

  private CatalogResource resource;


  public ProductsbyCategoryCacheKey(StoreContext context, String id, CatalogResource resource, CommerceCache commerceCache) {
    super(context, id, CONFIG_KEY_PRODUCTS_BY_CATEGORY, commerceCache);
    this.resource = resource;

    LOG.info("Created ProductsbyCategoryCacheKey: " + getCacheIdentifier());
    if (!CommerceIdHelper.isCategoryId(id)) {
      String msg = id + " (is not a category id)";
      LOG.warn(msg);
      throw new InvalidIdException(msg);
    }
  }


  @Override
  public void addExplicitDependency(List<ProductDocument> documents) {
    if (documents != null) {
      for (ProductDocument document : documents) {
        String dependencyId = document.getId();
        LOG.debug("addExplicitDependency() Adding dependency on {}", dependencyId);
        Cache.dependencyOn(dependencyId);
      }
    }
  }


  @Override
  public List<ProductDocument> computeValue(Cache cache) {
    List<CategoryProductLinkDocument> productLinks = resource.getLinkedProducts(CommerceIdHelper.convertToExternalId(id));
    List<ProductDocument> productskus = new ArrayList<>();

    //TODO: we don't use the bulk operation here since the result does not contain the category id
    for (CategoryProductLinkDocument link : productLinks) {
      ProductDocument productBySku= resource.getProductBySku(link.getSku());
      productskus.add(productBySku);
    }

    return productskus;
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
