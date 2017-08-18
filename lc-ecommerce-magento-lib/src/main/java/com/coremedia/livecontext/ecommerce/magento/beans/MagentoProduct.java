package com.coremedia.livecontext.ecommerce.magento.beans;

import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductAttribute;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.catalog.VariantFilter;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.inventory.AvailabilityInfo;
import com.coremedia.livecontext.ecommerce.magento.cache.ProductCacheKey;
import com.coremedia.livecontext.ecommerce.magento.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.magento.common.MagentoStoreContextHelper;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.ProductDocument;
import com.coremedia.xml.Markup;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Currency;
import java.util.List;
import java.util.Locale;
import java.util.Map;


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
  public void load() throws CommerceException {
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
    Category category = null;
    List<String> categoryIds = (List<String>) getDelegate().getCustomAttribute(CATEGORY_IDS);
    if (CollectionUtils.isNotEmpty(categoryIds)) {
      // Use the first category id
      category = getCatalogService().findCategoryById(categoryIds.get(0));
    }
    return category;
  }


  @Override
  public List<Category> getCategories() {
    List<Category> categories = new ArrayList<>();
    List<String> categoryIds = (List<String>) getDelegate().getCustomAttribute(CATEGORY_IDS);
    if (CollectionUtils.isNotEmpty(categoryIds)) {
      for (String categoryId : categoryIds) {
        Category c = getCatalogService().findCategoryById(categoryId);
        if (c != null) {
          categories.add(c);
        }
      }
    }
    return categories;
  }


  @Override
  public String getDefaultImageAlt() {
    return "Product Image";
  }


  @Override
  public String getDefaultImageUrl() {
    return getContext().get(MagentoStoreContextHelper.BASE_MEDIA_URL) + "catalog/product" + getDelegate().getCustomAttribute("image");
  }


  @Override
  public String getThumbnailUrl() {
    return getContext().get(MagentoStoreContextHelper.BASE_MEDIA_URL) + "catalog/product" + getDelegate().getCustomAttribute("thumbnail");
  }


  @Nonnull
  @Override
  public List<ProductAttribute> getDefiningAttributes() {
    return Collections.emptyList();
  }


  @Nonnull
  @Override
  public List<ProductAttribute> getDescribingAttributes() {
    return Collections.emptyList();
  }


  @Nonnull
  @Override
  public List<String> getVariantAxisNames() {
    return Collections.emptyList();
  }


  @Nonnull
  @Override
  public List<Object> getVariantAxisValues(@Nonnull String axisName, @Nullable List<VariantFilter> filters) {
    return Collections.emptyList();
  }


  @Nonnull
  @Override
  public List<Object> getVariantAxisValues(@Nonnull String axisName, @Nullable VariantFilter filter) {
    return Collections.emptyList();
  }


  @Nonnull
  @Override
  public List<Object> getAttributeValues(@Nonnull String attributeId) {
    return null;
  }


  @Nonnull
  @Override
  public List<ProductVariant> getVariants() {
    return Collections.emptyList();
  }


  @Nonnull
  @Override
  public List<ProductVariant> getVariants(@Nullable List<VariantFilter> filters) {
    return null;
  }


  @Nonnull
  @Override
  public List<ProductVariant> getVariants(@Nullable VariantFilter filter) {
    return Collections.emptyList();
  }


  @Nonnull
  @Override
  public Map<ProductVariant, AvailabilityInfo> getAvailabilityMap() {
    return Collections.emptyMap();
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


  @Override
  public CatalogPicture getCatalogPicture() {
    String baseUrl = MagentoStoreContextHelper.getBaseMediaUrl(getContext());
    return new CatalogPicture(baseUrl + "catalog/product" + getDelegate().getCustomAttribute("image"), null);
  }


  @Override
  public Content getPicture() {
    return null;
  }


  @Override
  public List<Content> getPictures() {
    return Collections.emptyList();
  }


  @Override
  public List<Content> getVisuals() {
    return Collections.emptyList();
  }


  @Override
  public List<Content> getDownloads() {
    return Collections.emptyList();
  }


  @Override
  public String getReference() {
    return CommerceIdHelper.formatProductId(getExternalId());
  }


  @Override
  public Locale getLocale() {
    return getContext().getLocale();
  }

}
