package com.coremedia.livecontext.ecommerce.magento.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanType;
import com.coremedia.livecontext.ecommerce.common.CommerceId;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.PRODUCT;

public class MagentoCommerceIdProvider extends BaseCommerceIdProvider {

  public static final String MAGENTO_VENDOR_PREFIX = "magento";

  public MagentoCommerceIdProvider() {
    super(CommerceIdHelper.MAGENTO);
  }

  @Nonnull
  public static CommerceIdBuilder commerceId(@Nonnull CommerceBeanType beanType) {
    return BaseCommerceIdProvider.commerceId(CommerceIdHelper.MAGENTO, beanType);
  }

  @Override
  @Nonnull
  public CommerceId formatCategoryTechId(@Nullable CatalogAlias catalogAlias, @Nonnull String techId) {
    return builder(CATEGORY).withCatalogAlias(catalogAlias).withExternalId(techId).build();
  }


  @Override
  @Nonnull
  public CommerceId formatProductTechId(@Nullable CatalogAlias catalogAlias, @Nonnull String productTechId) {
    return builder(PRODUCT).withCatalogAlias(catalogAlias).withExternalId(productTechId).build();
  }
}
