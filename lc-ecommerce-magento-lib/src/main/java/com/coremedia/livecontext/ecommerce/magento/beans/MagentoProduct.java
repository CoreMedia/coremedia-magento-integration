package com.coremedia.livecontext.ecommerce.magento.beans;

import com.coremedia.blueprint.base.livecontext.ecommerce.id.CommerceIdBuilder;
import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.catalog.VariantFilter;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;
import com.coremedia.livecontext.ecommerce.magento.cache.ProductCacheKey;
import com.coremedia.livecontext.ecommerce.magento.common.MagentoCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.magento.common.MagentoStoreContextHelper;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.ProductDocument;
import com.coremedia.xml.Markup;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.toList;

/**
 * Product implementation for magento commerce backends.
 */
public class MagentoProduct extends AbstractMagentoCommerceBean implements Product {

  private static final Logger LOG = LoggerFactory.getLogger(MagentoProduct.class);

  private static final String CATEGORY_IDS = "category_ids";
  private static final int STATUS_ONLINE = 1;

  @Override
  public ProductDocument getDelegate() {
    return (ProductDocument) super.getDelegate();
  }

  @Override
  public void load() {
    ProductCacheKey cacheKey = new ProductCacheKey(null, getContext(), getId(), getCatalogResource(), getCommerceCache());
    loadCached(cacheKey);
  }

  @Override
  public Currency getCurrency() {
    Object baseCurrencyCode = getContext().get(MagentoStoreContextHelper.BASE_CURRENCY_CODE);
    LOG.debug("getCurrency({}) {}", getId(), baseCurrencyCode);
    return Currency.getInstance("" + baseCurrencyCode);
  }

  @Override
  public String getName() {
    return getDelegate().getName();
  }

  @Override
  public Markup getShortDescription() {
    String description = (String) getDelegate().getCustomAttribute("description");
    return buildRichtextMarkup(description);
  }

  @Override
  public Markup getLongDescription() {
    return getShortDescription();
  }

  @Override
  public String getTitle() {
    return getName();
  }

  @Override
  public String getMetaDescription() {
    return "meta description";
  }

  @Override
  public String getMetaKeywords() {
    return "meta keywords";
  }

  @Override
  public BigDecimal getListPrice() {
    return BigDecimal.valueOf(getDelegate().getPrice());
  }

  @Override
  public BigDecimal getOfferPrice() {
    return getListPrice();
  }

  @Override
  public Category getCategory() {
    return getCategoryIds().stream()
            .findFirst()
            .filter(StringUtils::isNotBlank)
            .map(MagentoProduct::buildCategoryCommerceId)
            .map(this::findCategoryById)
            .orElse(null);
  }

  @Nonnull
  @Override
  public List<Category> getCategories() {
    return getCategoryIds().stream()
            .map(MagentoProduct::buildCategoryCommerceId)
            .map(this::findCategoryById)
            .filter(Objects::nonNull)
            .collect(toList());
  }

  @Nonnull
  private static CommerceId buildCategoryCommerceId(String categoryId) {
    return MagentoCommerceIdProvider
            .commerceId(CATEGORY)
            .withExternalId(categoryId)
            .build();
  }

  @Nullable
  private Category findCategoryById(@Nonnull CommerceId id) {
    StoreContext storeContext = getContext();
    return getCatalogService().findCategoryById(id, storeContext);
  }

  @Nonnull
  private List<String> getCategoryIds() {
    List<String> categoryIds = (List<String>) getDelegate().getCustomAttribute(CATEGORY_IDS);

    if (categoryIds == null) {
      return emptyList();
    }

    return categoryIds;
  }

  @Override
  public String getDefaultImageAlt() {
    return "Product Image";
  }

  @Override
  public String getDefaultImageUrl() {
    return getContext().get(MagentoStoreContextHelper.BASE_MEDIA_URL)
            + "catalog/product"
            + getDelegate().getCustomAttribute("image");
  }

  @Override
  public String getThumbnailUrl() {
    return getContext().get(MagentoStoreContextHelper.BASE_MEDIA_URL)
            + "catalog/product"
            + getDelegate().getCustomAttribute("thumbnail");
  }

  @Nonnull
  @Override
  public List<ProductAttribute> getDefiningAttributes() {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<ProductAttribute> getDescribingAttributes() {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<String> getVariantAxisNames() {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<Object> getVariantAxisValues(@Nonnull String axisName, @Nullable List<VariantFilter> filters) {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<Object> getVariantAxisValues(@Nonnull String axisName, @Nullable VariantFilter filter) {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<Object> getAttributeValues(@Nonnull String attributeId) {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<ProductVariant> getVariants() {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<ProductVariant> getVariants(@Nullable List<VariantFilter> filters) {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<ProductVariant> getVariants(@Nullable VariantFilter filter) {
    return emptyList();
  }

  @Nonnull
  @Override
  public Map<ProductVariant, AvailabilityInfo> getAvailabilityMap() {
    return emptyMap();
  }

  @Override
  public float getTotalStockCount() {
    return 0;
  }

  @Override
  public boolean isAvailable() {
    return getDelegate().getStatus() == STATUS_ONLINE;
  }

  @Override
  public boolean isVariant() {
    return false;
  }

  @Nonnull
  @Override
  public CatalogPicture getCatalogPicture() {
    String baseUrl = MagentoStoreContextHelper.getBaseMediaUrl(getContext());
    return new CatalogPicture(baseUrl + "catalog/product" + getDelegate().getCustomAttribute("image"), null);
  }

  @Override
  public Content getPicture() {
    return null;
  }

  @Nonnull
  @Override
  public List<Content> getPictures() {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<Content> getVisuals() {
    return emptyList();
  }

  @Nonnull
  @Override
  public List<Content> getDownloads() {
    return emptyList();
  }

  @Override
  @Nonnull
  public CommerceId getReference() {
    return CommerceIdBuilder
            .buildCopyOf(getId())
            .withExternalId(getExternalId())
            .build();
  }

  @Override
  public Locale getLocale() {
    return getContext().getLocale();
  }
}
