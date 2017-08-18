package com.coremedia.livecontext.ecommerce.magento.rest.resources;

import com.coremedia.livecontext.ecommerce.magento.rest.documents.CategoryDocument;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.CategoryProductLinkDocument;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.ProductDocument;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.ProductSearchResultsDocument;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


/**
 * Access wrapper for the catalog json resource of magento.
 */
public class CatalogResource extends AbstractMagentoResource {
  private static final Logger LOG = LoggerFactory.getLogger(CatalogResource.class);
  private static final int SEARCH_BATCH_SIZE = 25; // Number of filter queries

  private final static String CATEGORIES_PATH = "/categories";

  private final static String CATEGORIES_PRODUCTS_PATH = CATEGORIES_PATH + "/{categoryId}/products";

  private final static String PRODUCTS_PATH = "/products";

  private final static String PRODUCTS_SKU_PATH = PRODUCTS_PATH + "/{sku}";

  private final static String SEARCH_PATH = "/search";


  /**
   * Returns the root category of the catalog.
   *
   * @return root category as presented by the Magento REST API
   */
  public CategoryDocument getRootCategory() {
    String storeCode = getStoreCode();
    LOG.info("getRootCategory() {}", storeCode);
    return getConnector().performGet(CATEGORIES_PATH, CategoryDocument.class, storeCode);
  }


  /**
   * Returns a category by the given category id.
   *
   * @param categoryId
   * @return
   */
  public CategoryDocument getCategoryById(String categoryId) {
    String storeCode = getStoreCode();
    LOG.info("getCategoryById() {} in {}", categoryId, storeCode);
    return getConnector().performGet(CATEGORIES_PATH + "/" + categoryId, CategoryDocument.class, storeCode);
  }


  /**
   * Returns a list of category documents for the given id array.
   *
   * @param categoryIds array containing magento category ids
   * @return Cateory document instance resembling the given ids
   */
  public List<CategoryDocument> getCategories(String[] categoryIds) {
    List<CategoryDocument> categories = new ArrayList<>();

    for (String categoryId : categoryIds) {
      categories.add(getCategoryById(categoryId));
    }

    return categories;
  }


  public List<CategoryProductLinkDocument> getLinkedProducts(String categoryId) {
    String resourcePath = CATEGORIES_PRODUCTS_PATH.replace("{categoryId}", categoryId);
    CategoryProductLinkDocument[] links = getConnector().performGet(resourcePath, CategoryProductLinkDocument[].class, getStoreCode());
    return Arrays.asList(links);
  }


  public ProductDocument getProductBySku(String sku) {
    String resourcePath = PRODUCTS_SKU_PATH.replace("{sku}", sku);
    return getConnector().performGet(resourcePath, ProductDocument.class, getStoreCode());
  }


  public List<ProductDocument> getProductsBySku(List<String> skus) {
    List<ProductDocument> products = new ArrayList<>();
    List<List<String>> partitions = Lists.partition(skus, SEARCH_BATCH_SIZE);
    for (List<String> partition : partitions) {
      ProductSearchResultsDocument result = searchProductsBySku(partition);
      if (result != null && result.getTotalCount() > 0) {
        products.addAll(result.getItems());
      }
    }
    return products;
  }

  protected ProductSearchResultsDocument searchProductsBySku(List<String> skus) {
    MultiValueMap queryParams = new LinkedMultiValueMap();
    int filterIndex = 0;
    for (String sku : skus) {
      String searchFilterFieldParamName = "searchCriteria[filter_groups][0][filters][" + filterIndex + "][field]";
      String searchFilterValueParamName = "searchCriteria[filter_groups][0][filters][" + filterIndex + "][value]";

      queryParams.add(searchFilterFieldParamName, "sku");
      queryParams.add(searchFilterValueParamName, sku);

      filterIndex++;
    }
    return getConnector().performGet(PRODUCTS_PATH, ProductSearchResultsDocument.class, queryParams, getStoreCode());
  }

  public ProductSearchResultsDocument getProductsBySearchTerm(String searchTerm, Map<String, String> searchParams) {
    return searchProductsBySearchTerm(searchTerm, searchParams);
  }

  /*    searchCriteria[filter_groups][0][filters][0][field]=name&
      searchCriteria[filter_groups][0][filters][0][value]=%25Leggings%25&
      searchCriteria[filter_groups][0][filters][0][condition_type]=like&
      searchCriteria[filter_groups][0][filters][1][field]=name&
      searchCriteria[filter_groups][0][filters][1][value]=%25Parachute%25&
      searchCriteria[filter_groups][0][filters][1][condition_type]=like*/
  protected ProductSearchResultsDocument searchProductsBySearchTerm(String searchTerm, Map<String, String> searchParams) {
    MultiValueMap queryParams = new LinkedMultiValueMap();
    int filterIndex = 0;

    String[] searchTerms = searchTerm.split(" ");
    //queryParams.add("searchCriteria[requestName]", "advanced_search_container");

    queryParams.add("searchCriteria[pageSize]", searchParams.containsKey("pageSize") ? searchParams.get("pageSize") : 10);
    queryParams.add("searchCriteria[currentPage]", searchParams.containsKey("pageNumber") ? searchParams.get("pageNumber") : 1);

    //filter by category (only if you're not the root category ;))
    if (searchParams.containsKey("categoryId") && !"2".equals(searchParams.get("categoryId"))) {
      String searchFilterFieldParamName = "searchCriteria[filter_groups][" + filterIndex + "][filters][0][field]";
      String searchFilterValueParamName = "searchCriteria[filter_groups][" + filterIndex + "][filters][0][value]";
      String searchFilterConditionParamName = "searchCriteria[filter_groups][" + filterIndex + "][filters][0][condition_type]";

      queryParams.add(searchFilterFieldParamName, "category_id");
      queryParams.add(searchFilterValueParamName, searchParams.get("categoryId"));
      queryParams.add(searchFilterConditionParamName, "eq");
      filterIndex++;
    }

        /*//sort options - not implemented in the studio so far
        if(searchParams.containsKey("sortBy")) {
            //may need to add this to all fields
            String sortAttribute = "id";
            String sortDirection = "ASC";
            //searchCriteria[sortOrders][0][field]=created_at& searchCriteria[sortOrders][0][direction]=DESC&
            //name, created at
            String searchOrderFieldParamName = "searchCriteria[sortOrders][" + filterIndex + "][filters][0][field]";
            //ASC | DESC
            String searchOrderDirectionParamName = "searchCriteria[sortOrders][" + filterIndex + "][filters][0][value]";
            queryParams.add(searchOrderFieldParamName, sortAttribute);
            queryParams.add(searchOrderDirectionParamName, sortDirection);
        }*/

    //add search term with AND connection
    for (String term : searchTerms) {

      String searchFilterFieldParamName = "searchCriteria[filter_groups][" + filterIndex + "][filters][0][field]";
      String searchFilterValueParamName = "searchCriteria[filter_groups][" + filterIndex + "][filters][0][value]";
      String searchFilterConditionParamName = "searchCriteria[filter_groups][" + filterIndex + "][filters][0][condition_type]";

      queryParams.add(searchFilterFieldParamName, "name");
      queryParams.add(searchFilterValueParamName, "%" + term + "%");
      queryParams.add(searchFilterConditionParamName, "like");

      filterIndex++;
    }

    //add technical filters to only search for visible items! AND connection
    String searchFilterFieldParamName = "searchCriteria[filter_groups][" + filterIndex + "][filters][0][field]";
    String searchFilterValueParamName = "searchCriteria[filter_groups][" + filterIndex + "][filters][0][value]";
    String searchFilterConditionParamName = "searchCriteria[filter_groups][" + filterIndex + "][filters][0][condition_type]";

    queryParams.add(searchFilterFieldParamName, "visibility");
    queryParams.add(searchFilterValueParamName, "4");
    queryParams.add(searchFilterConditionParamName, "eq");

    filterIndex++;

    searchFilterFieldParamName = "searchCriteria[filter_groups][" + filterIndex + "][filters][0][field]";
    searchFilterValueParamName = "searchCriteria[filter_groups][" + filterIndex + "][filters][0][value]";
    searchFilterConditionParamName = "searchCriteria[filter_groups][" + filterIndex + "][filters][0][condition_type]";

    queryParams.add(searchFilterFieldParamName, "status");
    queryParams.add(searchFilterValueParamName, "1");
    queryParams.add(searchFilterConditionParamName, "eq");

    filterIndex++;

       /* searchFilterFieldParamName = "searchCriteria[filter_groups][1][filters][" + filterIndex + "][field]";
        searchFilterValueParamName = "searchCriteria[filter_groups][1][filters][" + filterIndex + "][value]";
        searchFilterConditionParamName = "searchCriteria[filter_groups][1][filters][" + filterIndex + "][condition_type]";

        queryParams.add(searchFilterFieldParamName, "status");
        queryParams.add(searchFilterValueParamName, "4");
        queryParams.add(searchFilterConditionParamName, "eq");*/

    //http://magento.cmdemo.perficient.com/rest/default/V1/products/?searchCriteria[pageSize]=4&searchCriteria[currentPage]=1&searchCriteria[filter_groups][0][filters][0][field]=name&searchCriteria[filter_groups][0][filters][0][value]=%cm%&searchCriteria[filter_groups][0][filters][0][condition_type]=like
    //http://magento.cmdemo.perficient.com/rest/default/V1/search?searchCriteria[requestName]=advanced_search_container&searchCriteria[filter_groups][0][filters][0][field]=name&searchCriteria[filter_groups][0][filters][0][value]=wat&searchCriteria[filter_groups][0][filters][0][condition_type]=like
    return getConnector().performGet(PRODUCTS_PATH, ProductSearchResultsDocument.class, queryParams, getStoreCode());
  }

}
