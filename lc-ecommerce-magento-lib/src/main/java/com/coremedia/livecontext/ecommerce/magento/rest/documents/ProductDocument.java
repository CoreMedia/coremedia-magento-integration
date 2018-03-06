package com.coremedia.livecontext.ecommerce.magento.rest.documents;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * JavaBean describing a product taken from a magento catalog.
 * Used for mapping REST resource results to local documents.
 */
public class ProductDocument extends AbstractMagentoDocument {

  /**
   * Sku.
   */
  @JsonProperty("sku")
  private String sku;

  /**
   * Name.
   */
  @JsonProperty("name")
  private String name;

  /**
   * Attribute set id.
   */
  @JsonProperty("attribute_set_id")
  private int attributeSetId;

  /**
   * Price.
   */
  @JsonProperty("price")
  private double price;

  /**
   * Status.
   * <p>
   * 1 = online
   * 2 = offline
   */
  @JsonProperty("status")
  private int status;

  /**
   * Visibility.
   */
  @JsonProperty("visibility")
  private int visibility;

  /**
   * Type id.
   */
  @JsonProperty("type_id")
  private String typeId;

  /**
   * Created date.
   */
  @JsonProperty("created_at")
  private String createdAt;

  /**
   * Updated date.
   */
  @JsonProperty("updated_at")
  private String updatedAt;

  /**
   * Weight.
   */
  @JsonProperty("weight")
  private double weight;

  /**
   * Product links info.
   */
  @JsonProperty("product_links")
  private List<CategoryProductLinkDocument> productLinks;

  /**
   * List of product options.
   */
  @JsonProperty("options")
  private List<Object> options;

  /**
   * Media gallery entries.
   */
  @JsonProperty("media_gallery_entries")
  private List<Object> mediaGalleryEntries;

  /**
   * List of product tier prices.
   */
  @JsonProperty("tier_prices")
  private List<Object> tierPrices;

  @Override
  public String getId() {
    return getSku();  // Needs to be sku since REST API does not work with the internal numeric id!!!
  }

  public String getInternalId() {
    return super.getId();
  }

  public String getSku() {
    return sku;
  }

  public void setSku(String sku) {
    this.sku = sku;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAttributeSetId() {
    return attributeSetId;
  }

  public void setAttributeSetId(int attributeSetId) {
    this.attributeSetId = attributeSetId;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public int getStatus() {
    return status;
  }

  public void setStatus(int status) {
    this.status = status;
  }

  public int getVisibility() {
    return visibility;
  }

  public void setVisibility(int visibility) {
    this.visibility = visibility;
  }

  public String getTypeId() {
    return typeId;
  }

  public void setTypeId(String typeId) {
    this.typeId = typeId;
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

  public double getWeight() {
    return weight;
  }

  public void setWeight(double weight) {
    this.weight = weight;
  }

  public List<CategoryProductLinkDocument> getProductLinks() {
    return productLinks;
  }

  public void setProductLinks(List<CategoryProductLinkDocument> productLinks) {
    this.productLinks = productLinks;
  }

  public List<Object> getOptions() {
    return options;
  }

  public void setOptions(List<Object> options) {
    this.options = options;
  }

  public List<Object> getMediaGalleryEntries() {
    return mediaGalleryEntries;
  }

  public void setMediaGalleryEntries(List<Object> mediaGalleryEntries) {
    this.mediaGalleryEntries = mediaGalleryEntries;
  }

  public List<Object> getTierPrices() {
    return tierPrices;
  }

  public void setTierPrices(List<Object> tierPrices) {
    this.tierPrices = tierPrices;
  }
}
