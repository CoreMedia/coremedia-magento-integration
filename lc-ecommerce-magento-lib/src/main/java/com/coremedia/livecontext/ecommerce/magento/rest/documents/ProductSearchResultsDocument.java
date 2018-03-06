package com.coremedia.livecontext.ecommerce.magento.rest.documents;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * JavaBean describing a result of a product search as returned from a magento catalog.
 * Used for mapping REST resource results to local documents.
 */
public class ProductSearchResultsDocument extends AbstractMagentoDocument {

  @JsonProperty("items")
  private List<ProductDocument> items;

  @JsonProperty("search_criteria")
  private Object searchCriteria;

  /**
   * Total count.
   */
  @JsonProperty("total_count")
  private int totalCount;

  public List<ProductDocument> getItems() {
    return items;
  }

  public void setItems(List<ProductDocument> items) {
    this.items = items;
  }

  public Object getSearchCriteria() {
    return searchCriteria;
  }

  public void setSearchCriteria(Object searchCriteria) {
    this.searchCriteria = searchCriteria;
  }

  public int getTotalCount() {
    return totalCount;
  }

  public void setTotalCount(int totalCount) {
    this.totalCount = totalCount;
  }
}
