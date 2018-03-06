package com.coremedia.livecontext.ecommerce.magento.rest.documents;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * JavaBean describing a category taken from a magento catalog.
 * Used for mapping REST resource results to local documents.
 */
public class CategoryDocument extends AbstractMagentoDocument {

  @JsonProperty("parent_id")
  private String parentId;

  private String name;

  @JsonProperty("is_active")
  private boolean active;

  private int position;

  private int level;

  private String children;

  @JsonProperty("children_data")
  private List<CategoryDocument> subCategories;

  @JsonProperty("created_at")
  private String createdAt;

  @JsonProperty("updated_at")
  private String updatedAt;

  private String path;

  @JsonProperty("available_sort_by")
  private List<String> availableSortBy;

  @JsonProperty("include_in_menu")
  private boolean includeInMenu;

  public String getParentId() {
    return parentId;
  }

  public void setParentId(String parentId) {
    this.parentId = parentId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean isActive() {
    return active;
  }

  public void setActive(boolean active) {
    this.active = active;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(int level) {
    this.level = level;
  }

  public String getChildren() {
    return children;
  }

  public void setChildren(String children) {
    this.children = children;
  }

  public List<CategoryDocument> getSubCategories() {
    return subCategories;
  }

  public void setSubCategories(List<CategoryDocument> subCategories) {
    this.subCategories = subCategories;
  }

  public String getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(String createdAt) {
    this.createdAt = createdAt;
  }

  public String getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(String updatedAt) {
    this.updatedAt = updatedAt;
  }

  public List<String> getAvailableSortBy() {
    return availableSortBy;
  }

  public void setAvailableSortBy(List<String> availableSortBy) {
    this.availableSortBy = availableSortBy;
  }

  public boolean isIncludeInMenu() {
    return includeInMenu;
  }

  public void setIncludeInMenu(boolean includeInMenu) {
    this.includeInMenu = includeInMenu;
  }
}
