package com.coremedia.livecontext.ecommerce.magento.cache;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.cache.Cache;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.AbstractMagentoDocument;
import com.google.common.base.Joiner;

public abstract class AbstractMagentoDocumentCacheKey<T extends Object> extends AbstractCommerceCacheKey<T> {

  private static final Joiner JOINER = Joiner.on(":").useForNull("undefined");

  public AbstractMagentoDocumentCacheKey(StoreContext context, CommerceId id, String configKey,
                                         CommerceCache commerceCache) {
    super(id.getExternalId().get(), context, configKey, commerceCache);
  }

  @Override
  public void addExplicitDependency(T document) {
    if (document instanceof AbstractMagentoDocument) {
      String dependencyId = ((AbstractMagentoDocument) document).getId();
      Cache.dependencyOn(dependencyId);
    }
  }

  @Override
  protected String getCacheIdentifier() {
    return JOINER.join(getCacheIdArgs());
  }

  public abstract Object[] getCacheIdArgs();
}
