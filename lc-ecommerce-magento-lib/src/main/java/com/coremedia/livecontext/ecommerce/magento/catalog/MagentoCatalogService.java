package com.coremedia.livecontext.ecommerce.magento.catalog;

import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceException;
import com.coremedia.livecontext.ecommerce.common.CommerceIdProvider;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.magento.beans.MagentoCategory;
import com.coremedia.livecontext.ecommerce.magento.cache.CategoryCacheKey;
import com.coremedia.livecontext.ecommerce.magento.cache.ProductCacheKey;
import com.coremedia.livecontext.ecommerce.magento.cache.ProductsbyCategoryCacheKey;
import com.coremedia.livecontext.ecommerce.magento.common.AbstractCommerceService;
import com.coremedia.livecontext.ecommerce.magento.common.CommerceIdHelper;
import com.coremedia.livecontext.ecommerce.magento.common.MagentoStoreContextHelper;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.ProductSearchResultsDocument;
import com.coremedia.livecontext.ecommerce.magento.rest.resources.CatalogResource;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


/**
 * Magento catalog service transforming the catalog rest resource into a LiveContext CatalogService instance.
 */
public class MagentoCatalogService extends AbstractCommerceService implements CatalogService {
  private static final Logger LOG = LoggerFactory.getLogger(MagentoCatalogService.class);

  private CatalogResource catalogResource;
  private CommerceIdProvider commerceIdProvider;
  private String magentoCatalogRootId;

  @Nullable
  @Override
  public Product findProductById(@Nonnull String id) throws CommerceException {
    LOG.info("findProductById() {}", id);
    StoreContext context = MagentoStoreContextHelper.getCurrentContext();
    ProductCacheKey cacheKey = new ProductCacheKey(null, context, id, catalogResource, getCommerceCache());
    ProductDocument delegate = (ProductDocument) getCommerceCache().get(cacheKey);
    return getCommerceBeanHelper().createBeanFor(delegate, Product.class);
  }


  @Nullable
  @Override
  public Product findProductBySeoSegment(@Nonnull String seoSegment) throws CommerceException {
    LOG.info("findProductBySeoSegment() {}", seoSegment);
    return null;
  }


  @Nullable
  @Override
  public ProductVariant findProductVariantById(@Nonnull String id) throws CommerceException {
    LOG.info("findProductVariantById() {}", id);
    return null;
  }


  @Nonnull
  @Override
  public List<Product> findProductsByCategory(@Nonnull Category category) throws CommerceException {
    LOG.info("findProductsByCategory() {}: {}", category.getDisplayName(), category);
    // Number 2 is the default category and contains ALL products which will overload the browsing of the catalog
    if (category.getId().endsWith("/2")) {
      return Collections.emptyList();
    }
    // This is the technical root an doesn't contain any products - promised!
    if (category.getId().endsWith("/1")) {
      return Collections.emptyList();
    }

    StoreContext context = MagentoStoreContextHelper.getCurrentContext();
    LOG.info("findProductsByCategory() context={}", context);
    ProductsbyCategoryCacheKey cacheKey = new ProductsbyCategoryCacheKey(context, category.getId(), catalogResource, getCommerceCache());
    List<ProductDocument> productDocuments = Collections.emptyList();
    try {
      productDocuments = (List<ProductDocument>) getCommerceCache().get(cacheKey);
    } catch (Exception e) {
      LOG.error("findProductsByCategory()", e);
    }

    // Re-Cache Products separately and use them
    List<ProductDocument> products = new ArrayList<ProductDocument>(productDocuments.size());
    for (ProductDocument product : productDocuments) {
      String externalId = commerceIdProvider.formatProductId(product.getId());
      ProductCacheKey productKey = new ProductCacheKey(product, context, externalId, catalogResource, getCommerceCache());
      ProductDocument p = (ProductDocument) getCommerceCache().get(productKey);
      products.add(p);
    }
    return getCommerceBeanHelper().createBeansFor(products, Product.class);
  }


  @Nonnull
  @Override
  public List<Category> findTopCategories(Site site) throws CommerceException {
    LOG.info("findTopCategories() {}", site);
    List<Category> result = new ArrayList<>(1);
    result.add(findRootCategory());
    return result;
  }


  private Category getCategoryFromCacheKey(CategoryCacheKey cacheKey) {
    CategoryDocument delegate = (CategoryDocument) getCommerceCache().get(cacheKey);
    return getCommerceBeanHelper().createBeanFor(delegate, Category.class);
  }


  @Override
  public Category findRootCategory() throws CommerceException {
    LOG.info("findRootCategory()");
    StoreContext context = MagentoStoreContextHelper.getCurrentContext();
    LOG.debug("findRootCategory() context={}", (context == null ? null : context.getStoreName()));
    CategoryDocument rootDocument = null;
    synchronized (this) {
      if (magentoCatalogRootId == null) {
        try {
          rootDocument = catalogResource.getRootCategory();
          magentoCatalogRootId = rootDocument.getId();
        } catch (Throwable e) {
          magentoCatalogRootId = "2";
          LOG.error("findRootCategory()", e);
        }
      }
      String cid = CommerceIdHelper.formatCategoryId(CommerceIdHelper.convertToExternalId(magentoCatalogRootId));
      LOG.info("findRootCategory() {} -  {}", magentoCatalogRootId, cid);
      CategoryCacheKey cacheKey = new CategoryCacheKey(rootDocument, context, cid, catalogResource, getCommerceCache());
      return getCategoryFromCacheKey(cacheKey);
    }
  }


  @Nonnull
  @Override
  public List<Category> findSubCategories(@Nonnull Category parentCategory) throws CommerceException {
    MagentoCategory mc = (MagentoCategory) parentCategory;
    CategoryDocument document = mc.getDelegate();
    String children = document.getChildren();
    List<CategoryDocument> subCategories = document.getSubCategories();
    LOG.info("findSubCategories({}) '{}' ({})", parentCategory.getId(), children, subCategories == null ? null : subCategories.size());
    List<Category> result;
    if (subCategories != null && children == null) {
      result = new ArrayList<>(subCategories.size());
      StoreContext context = MagentoStoreContextHelper.getCurrentContext();
      for (CategoryDocument c : subCategories) {
        String cid = CommerceIdHelper.formatCategoryId(CommerceIdHelper.convertToExternalId(c.getId()));
        CategoryCacheKey cacheKey = new CategoryCacheKey(c, context, cid, catalogResource, getCommerceCache());
        result.add(getCategoryFromCacheKey(cacheKey));
      }
    }
    else {
      String[] childrenIds = children.split(",");
      result = new ArrayList<>(childrenIds.length);
      if (StringUtils.hasText(children)) {
        // Be sure to use the cache key
        for (String cid : childrenIds) {
          result.add(findCategoryById(cid));
        }
      }
    }
    return result;
  }


  @Nullable
  @Override
  public Category findCategoryById(@Nonnull String id) throws CommerceException {
    // dumb stuff so that the full hierarchy is always read and everything else is cached subsequently
    LOG.info("findCategoryById({}) root is {}", id, findRootCategory().getId());
    String cid = CommerceIdHelper.formatCategoryId(CommerceIdHelper.convertToExternalId(id));
    LOG.info("findCategoryById({}) {}", cid, id);
    StoreContext context = MagentoStoreContextHelper.getCurrentContext();
    LOG.info("findCategoryById() context={}", (context == null ? null : context.getStoreName()));
    CategoryCacheKey cacheKey = new CategoryCacheKey(null, context, cid, catalogResource, getCommerceCache());
    CategoryDocument delegate = (CategoryDocument) getCommerceCache().get(cacheKey);
    return getCommerceBeanHelper().createBeanFor(delegate, Category.class);
  }


  @Nullable
  @Override
  public Category findCategoryBySeoSegment(@Nonnull String seoSegment) throws CommerceException {
    LOG.info("findCategoryBySeoSegment() {}", seoSegment);
    return null;
  }


  @Nonnull
  @Override
  public SearchResult<Product> searchProducts(@Nonnull String searchTerm, @Nullable Map<String, String> searchParams) throws CommerceException {
    LOG.info("searchProducts() {}", searchTerm);
    StoreContext context = MagentoStoreContextHelper.getCurrentContext();
    ProductSearchResultsDocument searchResultDocument = getCatalogResource().getProductsBySearchTerm(searchTerm, searchParams);

    List<ProductDocument> productDocuments = searchResultDocument.getItems();
    // Re-Cache Products separately and use them
    List<ProductDocument> products = new ArrayList<ProductDocument>(productDocuments.size());
    for (ProductDocument product : productDocuments) {
      String externalId = commerceIdProvider.formatProductId(product.getId());
      ProductCacheKey productKey = new ProductCacheKey(product, context, externalId, catalogResource, getCommerceCache());
      ProductDocument p = (ProductDocument) getCommerceCache().get(productKey);
      products.add(p);
    }

    List<Product> productList = getCommerceBeanHelper().createBeansFor(products, Product.class);
    SearchResult<Product> result = new SearchResult<>();
    result.setSearchResult(productList);
    result.setTotalCount(searchResultDocument.getTotalCount());
    result.setPageNumber(1);
    result.setPageSize(100);
    return result;
  }

  @Nonnull
  @Override
  public SearchResult<ProductVariant> searchProductVariants(@Nonnull String searchTerm, @Nullable Map<String, String> searchParams) throws CommerceException {
    LOG.info("searchProductVariants() {}", searchTerm);
    return null;
  }


  @Nonnull
  @Override
  public CatalogService withStoreContext(StoreContext storeContext) {
    LOG.debug("withStoreContext() {}", (storeContext != null ? storeContext.getStoreName() : null));
    // TODO: should not depend on local catalog resource but given store context.
    return this;
  }


  public CatalogResource getCatalogResource() {
    return catalogResource;
  }


  @Required
  public void setCatalogResource(CatalogResource catalogResource) {
    this.catalogResource = catalogResource;
  }


  @Required
  public void setCommerceIdProvider(CommerceIdProvider commerceIdProvider) {
    this.commerceIdProvider = commerceIdProvider;
  }

}
