package com.coremedia.livecontext.ecommerce.magento.rest.documents;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * JavaBean describing a category product link taken from a magento catalog.
 * Used for mapping REST resource results to local documents.
 */
public class CategoryProductLinkDocument extends AbstractMagentoDocument {

  private String sku;
  private int position;

  @JsonProperty("category_id")
  private String categoryId;

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getCategoryId() {
    return categoryId;
  }

  public void setCategoryId(String categoryId) {
    this.categoryId = categoryId;
  }
}
