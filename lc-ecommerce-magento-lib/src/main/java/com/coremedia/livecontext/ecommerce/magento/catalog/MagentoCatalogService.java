package com.coremedia.livecontext.ecommerce.magento.catalog;

import com.coremedia.livecontext.ecommerce.catalog.Catalog;
import com.coremedia.livecontext.ecommerce.catalog.CatalogAlias;
import com.coremedia.livecontext.ecommerce.catalog.CatalogId;
import com.coremedia.livecontext.ecommerce.catalog.CatalogService;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
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
import com.coremedia.livecontext.ecommerce.search.SearchFacet;
import com.coremedia.livecontext.ecommerce.search.SearchResult;
import com.google.common.collect.ImmutableMap;
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
import java.util.Optional;

import static com.coremedia.blueprint.base.livecontext.util.CommerceServiceHelper.getServiceProxyForStoreContext;
import static java.util.stream.Collectors.toMap;

/**
 * Magento catalog service transforming the catalog rest resource into a LiveContext CatalogService instance.
 */
public class MagentoCatalogService extends AbstractCommerceService implements CatalogService {

  private static final Logger LOG = LoggerFactory.getLogger(MagentoCatalogService.class);

  private CatalogResource catalogResource;
  private CommerceIdProvider commerceIdProvider;
  private String magentoCatalogRootId;

  @Nonnull
  @Override
  public CatalogService withStoreContext(StoreContext storeContext) {
    return getServiceProxyForStoreContext(storeContext, this, CatalogService.class);
  }

  @Nullable
  @Override
  public Product findProductById(@Nonnull CommerceId id, @Nonnull StoreContext storeContext) {
    LOG.info("findProductById() {}", id);
    StoreContext context = MagentoStoreContextHelper.getCurrentContext();
    ProductCacheKey cacheKey = new ProductCacheKey(null, context, id, catalogResource, getCommerceCache());
    ProductDocument delegate = getCommerceCache().get(cacheKey);
    return getCommerceBeanHelper().createBeanFor(delegate, Product.class);
  }

  @Nullable
  @Override
  public Product findProductBySeoSegment(@Nonnull String seoSegment, @Nonnull StoreContext storeContext) {
    LOG.info("findProductBySeoSegment() {}", seoSegment);
    return null;
  }

  @Nullable
  @Override
  public ProductVariant findProductVariantById(@Nonnull CommerceId id, @Nonnull StoreContext storeContext) {
    LOG.info("findProductVariantById() {}", id);
    return null;
  }

  @Nonnull
  @Override
  public List<Product> findProductsByCategory(@Nonnull Category category) {
    LOG.info("findProductsByCategory() {}: {}", category.getDisplayName(), category);

    CommerceId categoryId = category.getId();

    // Number 2 is the default category and contains ALL products which will overload the browsing of the catalog
    if (categoryId.getExternalId().get().endsWith("/2")) {
      return Collections.emptyList();
    }

    // This is the technical root an doesn't contain any products - promised!
    if (categoryId.getExternalId().get().endsWith("/1")) {
      return Collections.emptyList();
    }

    StoreContext context = MagentoStoreContextHelper.getCurrentContext();
    LOG.info("findProductsByCategory() context={}", context);
    ProductsbyCategoryCacheKey cacheKey = new ProductsbyCategoryCacheKey(context, categoryId, catalogResource,
            getCommerceCache());
    List<ProductDocument> productDocuments = Collections.emptyList();
    try {
      productDocuments = getCommerceCache().get(cacheKey);
    } catch (Exception e) {
      LOG.error("findProductsByCategory()", e);
    }

    // Re-Cache Products separately and use them
    CatalogAlias catalogAlias = categoryId.getCatalogAlias();
    List<ProductDocument> products = new ArrayList<>(productDocuments.size());
    for (ProductDocument product : productDocuments) {
      CommerceId commerceId = commerceIdProvider.formatProductId(catalogAlias, product.getId());
      ProductCacheKey productKey = new ProductCacheKey(product, context, commerceId, catalogResource, getCommerceCache());
      ProductDocument p = getCommerceCache().get(productKey);
      products.add(p);
    }

    return getCommerceBeanHelper().createBeansFor(products, Product.class);
  }

  @Nonnull
  @Override
  public List<Category> findTopCategories(@Nonnull CatalogAlias catalogAlias, @Nonnull StoreContext storeContext) {
    LOG.info("findTopCategories() {}", storeContext);
    List<Category> result = new ArrayList<>(1);
    result.add(findRootCategory(catalogAlias, storeContext));
    return result;
  }

  private Category getCategoryFromCacheKey(CategoryCacheKey cacheKey) {
    CategoryDocument delegate = getCommerceCache().get(cacheKey);
    return getCommerceBeanHelper().createBeanFor(delegate, Category.class);
  }

  @Nonnull
  @Override
  public Category findRootCategory(@Nonnull CatalogAlias catalogAlias, @Nonnull StoreContext storeContext) {
    LOG.info("findRootCategory()");
    StoreContext context = MagentoStoreContextHelper.getCurrentContext();
    LOG.debug("findRootCategory() context={}", (context == null ? null : context.getStoreName()));

    CategoryDocument rootDocument = null;
    synchronized (this) {
      if (magentoCatalogRootId == null) {
        try {
          rootDocument = catalogResource.getRootCategory(storeContext.getStoreId());
          magentoCatalogRootId = rootDocument.getId();
        } catch (Throwable e) {
          magentoCatalogRootId = "2";
          LOG.error("findRootCategory()", e);
        }
      }

      CommerceId rootId = CommerceIdHelper.formatCategoryId(catalogAlias, magentoCatalogRootId);
      LOG.info("findRootCategory() {} -  {}", magentoCatalogRootId, rootId);
      CategoryCacheKey cacheKey = new CategoryCacheKey(rootDocument, context, rootId, catalogResource, getCommerceCache());
      return getCategoryFromCacheKey(cacheKey);
    }
  }

  @Nonnull
  @Override
  public List<Category> findSubCategories(@Nonnull Category parentCategory) {
    StoreContext context = MagentoStoreContextHelper.getCurrentContext();
    CatalogAlias catalogAlias = context.getCatalogAlias();

    MagentoCategory mc = (MagentoCategory) parentCategory;
    CategoryDocument document = mc.getDelegate();
    String children = document.getChildren();

    List<CategoryDocument> subCategories = document.getSubCategories();
    LOG.info("findSubCategories({}) '{}' ({})", parentCategory.getId(), children,
            subCategories == null ? null : subCategories.size());

    List<Category> result;
    if (subCategories != null && children == null) {
      result = new ArrayList<>(subCategories.size());

      for (CategoryDocument c : subCategories) {
        CommerceId cid = CommerceIdHelper.formatCategoryId(catalogAlias, c.getId());
        CategoryCacheKey cacheKey = new CategoryCacheKey(c, context, cid, catalogResource, getCommerceCache());
        result.add(getCategoryFromCacheKey(cacheKey));
      }
    } else {
      String[] childrenIds = children.split(",");
      result = new ArrayList<>(childrenIds.length);
      if (StringUtils.hasText(children)) {
        // Be sure to use the cache key
        for (String id : childrenIds) {
          CommerceId cid = CommerceIdHelper.formatCategoryId(catalogAlias, id);
          result.add(findCategoryById(cid, context));
        }
      }
    }

    return result;
  }

  @Nullable
  @Override
  public Category findCategoryById(@Nonnull CommerceId id, @Nonnull StoreContext storeContext) {
    // dumb stuff so that the full hierarchy is always read and everything else is cached subsequently
    StoreContext context = MagentoStoreContextHelper.getCurrentContext();
    CategoryCacheKey cacheKey = new CategoryCacheKey(null, context, id, catalogResource, getCommerceCache());
    CategoryDocument delegate = getCommerceCache().get(cacheKey);
    return getCommerceBeanHelper().createBeanFor(delegate, Category.class);
  }

  @Nullable
  @Override
  public Category findCategoryBySeoSegment(@Nonnull String seoSegment, @Nonnull StoreContext storeContext) {
    LOG.info("findCategoryBySeoSegment() {}", seoSegment);
    return null;
  }

  @Nonnull
  @Override
  public SearchResult<Product> searchProducts(@Nonnull String searchTerm, @Nonnull Map<String, String> searchParams,
                                              @Nonnull StoreContext storeContext) {
    LOG.info("searchProducts() {}", searchTerm);

    StoreContext context = MagentoStoreContextHelper.getCurrentContext();
    ProductSearchResultsDocument searchResultDocument = getCatalogResource()
            .getProductsBySearchTerm(storeContext.getStoreId(), searchTerm, searchParams);

    List<ProductDocument> productDocuments = searchResultDocument.getItems();
    // Re-Cache Products separately and use them
    List<ProductDocument> products = new ArrayList<ProductDocument>(productDocuments.size());
    for (ProductDocument product : productDocuments) {
      CommerceId productId = commerceIdProvider.formatProductId(context.getCatalogAlias(), product.getId());
      ProductCacheKey productKey = new ProductCacheKey(product, context, productId, catalogResource, getCommerceCache());
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
  public Map<String, List<SearchFacet>> getFacetsForProductSearch(@Nonnull Category category,
                                                                  @Nonnull StoreContext storeContext) {
    String categoryId = category.getExternalTechId();

    Map<String, String> searchParams = ImmutableMap.of(
            CatalogService.SEARCH_PARAM_CATEGORYID, categoryId,
            "fields", "DEFAULT,facets",
            CatalogService.SEARCH_PARAM_PAGESIZE, "1",
            CatalogService.SEARCH_PARAM_PAGENUMBER, "1");

    SearchResult<Product> searchResult = searchProducts("*", searchParams, storeContext);

    return searchResult.getFacets().stream()
            .collect(toMap(SearchFacet::getLabel, SearchFacet::getChildFacets));
  }

  @Nonnull
  @Override
  public SearchResult<ProductVariant> searchProductVariants(@Nonnull String searchTerm,
                                                            @Nonnull Map<String, String> searchParams,
                                                            @Nonnull StoreContext storeContext) {
    LOG.info("searchProductVariants() {}", searchTerm);
    return new SearchResult<>();
  }

  @Nonnull
  @Override
  public List<Catalog> getCatalogs(@Nonnull StoreContext storeContext) {
    return Collections.emptyList();
  }

  @Nonnull
  @Override
  public Optional<Catalog> getCatalog(@Nonnull CatalogId catalogId, @Nonnull StoreContext storeContext) {
    return Optional.empty();
  }

  @Nonnull
  @Override
  public Optional<Catalog> getCatalog(@Nonnull CatalogAlias alias, @Nonnull StoreContext storeContext) {
    return Optional.empty();
  }

  @Nonnull
  @Override
  public Optional<Catalog> getDefaultCatalog(@Nonnull StoreContext storeContext) {
    return Optional.empty();
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
