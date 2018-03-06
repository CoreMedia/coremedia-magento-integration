package com.coremedia.livecontext.ecommerce.magento.rest.documents;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Java representation of a magento category tree rest API JSON result.
 */
public class CategoryTreeDocument extends AbstractMagentoDocument {

  /**
   * Parent category ID.
   */
  @JsonProperty("parent_id")
  private int parentId;

  /**
   * Category name.
   */
  @JsonProperty("name")
  private String name;

  /**
   * Whether category is active.
   */
  @JsonProperty("is_active")
  private boolean isActive;

  /**
   * Category position.
   */
  @JsonProperty("position")
  private int position;

  /**
   * Category level.
   */
  @JsonProperty("level")
  private int level;

  /**
   * Product count.
   */
  @JsonProperty("product_count")
  private int productCount;

  @JsonProperty("children_data")
  private List<CategoryTreeDocument> children;
}
