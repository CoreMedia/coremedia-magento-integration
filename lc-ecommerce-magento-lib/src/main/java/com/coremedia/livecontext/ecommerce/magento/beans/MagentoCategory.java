package com.coremedia.livecontext.ecommerce.magento.beans;

import com.coremedia.cap.content.Content;
import com.coremedia.livecontext.ecommerce.asset.CatalogPicture;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.magento.cache.CategoryCacheKey;
import com.coremedia.livecontext.ecommerce.magento.common.MagentoCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.CategoryDocument;
import com.coremedia.xml.Markup;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.coremedia.livecontext.ecommerce.common.BaseCommerceBeanType.CATEGORY;
import static java.util.Collections.emptyList;

/**
 * Category implementation for magento backends.
 */
public class MagentoCategory extends AbstractMagentoCommerceBean implements Category {

  /**
   * ID of root catalog category.
   */
  private static final String ROOT_CATGORY_ID = "1";

  @Override
  public void load() {
    CategoryCacheKey cacheKey = new CategoryCacheKey(null, getContext(), getId(), getCatalogResource(), getCommerceCache());
    loadCached(cacheKey);
  }

  @Override
  public CategoryDocument getDelegate() {
    return (CategoryDocument) super.getDelegate();
  }

  @Override
  public String getName() {
    return getDelegate().getName();
  }

  @Override
  public Markup getShortDescription() {
    return buildRichtextMarkup("");
  }

  @Override
  public Markup getLongDescription() {
    return buildRichtextMarkup("");
  }

  @Override
  public String getThumbnailUrl() {
    return null;
  }

  @Override
  public String getDefaultImageUrl() {
    return null;
  }

  @Nonnull
  @Override
  public List<Category> getChildren() {
    return getCatalogService().findSubCategories(this);
  }

  @Nonnull
  @Override
  public List<Product> getProducts() {
    return getCatalogService().findProductsByCategory(this);
  }

  @Nullable
  @Override
  public Category getParent() {
    String parentId = getDelegate().getParentId();

    if (!StringUtils.isNotBlank(parentId) || isRoot()) {
      return null;
    }

    CommerceId parentCId = MagentoCommerceIdProvider
            .commerceId(CATEGORY)
            .withExternalId(parentId)
            .build();

    return getCatalogService().findCategoryById(parentCId, getContext());
  }

  @Nonnull
  @Override
  public List<Category> getBreadcrumb() {
    List<Category> result = new ArrayList<>();

    Category parent = getParent();
    if (parent != null) {
      result.addAll(parent.getBreadcrumb());
    }

    result.add(this);

    return result;
  }

  @Override
  public String getMetaDescription() {
    return null;
  }

  @Override
  public String getMetaKeywords() {
    return null;
  }

  @Override
  public String getTitle() {
    return getName();
  }

  @Nonnull
  @Override
  public String getDisplayName() {
    return getName();
  }

  @Override
  public CatalogPicture getCatalogPicture() {
    return null;
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
  public boolean isRoot() {
    return ROOT_CATGORY_ID.equals(getDelegate().getParentId());
  }

  @Override
  public Locale getLocale() {
    return null;
  }
}
