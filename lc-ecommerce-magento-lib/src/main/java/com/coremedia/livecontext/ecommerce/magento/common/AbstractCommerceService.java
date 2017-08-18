package com.coremedia.livecontext.ecommerce.magento.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;

public abstract class AbstractCommerceService {

  private CommerceBeanHelper commerceBeanHelper;
  private CommerceCache commerceCache;


  public CommerceBeanHelper getCommerceBeanHelper() {
    return commerceBeanHelper;
  }


  public void setCommerceBeanHelper(CommerceBeanHelper commerceBeanHelper) {
    this.commerceBeanHelper = commerceBeanHelper;
  }


  public CommerceCache getCommerceCache() {
    return commerceCache;
  }


  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

}
