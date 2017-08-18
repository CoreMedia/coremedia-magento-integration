package com.coremedia.livecontext.ecommerce.magento.rest.documents;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Resource representing a store configuration.
 */
public class StoreConfigDocument extends AbstractMagentoDocument {

  /**
   * Store id.
   */
  @JsonProperty("id")
  private String id;

  /**
   * Store code.
   */
  @JsonProperty("code")
  private String code;

  /**
   * Website id of the store.
   */
  @JsonProperty("website_id")
  private String websiteId;

  /**
   * Store locale.
   */
  @JsonProperty("locale")
  private String locale;

  /**
   * Base currency code.
   */
  @JsonProperty("base_currency_code")
  private String baseCurrencyCode;

  /**
   * Default display currency code.
   */
  @JsonProperty("default_display_currency_code")
  private String defaultDisplayCurrencyCode;

  /**
   * Timezone of the store.
   */
  @JsonProperty("timezone")
  private String timezone;

  /**
   * The unit of weight.
   */
  @JsonProperty("weight_unit")
  private String weightUnit;

  /**
   * Base URL for the store.
   */
  @JsonProperty("base_url")
  private String baseUrl;

  /**
   * Base link URL for the store.
   */
  @JsonProperty("base_link_url")
  private String baseLinkUrl;

  /**
   * Base static URL for the store.
   */
  @JsonProperty("base_static_url")
  private String baseStaticUrl;

  /**
   * Base media URL for the store.
   */
  @JsonProperty("base_media_url")
  private String baseMediaUrl;

  /**
   * Secure base URL for the store.
   */
  @JsonProperty("secure_base_url")
  private String secureBaseUrl;

  /**
   * Secure base link URL for the store.
   */
  @JsonProperty("secure_base_link_url")
  private String secureBaseLinkUrl;

  /**
   * Secure base static URL for the store.
   */
  @JsonProperty("secure_base_static_url")
  private String secureBaseStaticUrl;

  /**
   * Secure base media URL for the store.
   */
  @JsonProperty("secure_base_media_url")
  private String secureBaseMediaUrl;

  @Override
  public String getId() {
    return id;
  }

  @Override
  public void setId(String id) {
    this.id = id;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public String getWebsiteId() {
    return websiteId;
  }

  public void setWebsiteId(String websiteId) {
    this.websiteId = websiteId;
  }

  public String getLocale() {
    return locale;
  }

  public void setLocale(String locale) {
    this.locale = locale;
  }

  public String getBaseCurrencyCode() {
    return baseCurrencyCode;
  }

  public void setBaseCurrencyCode(String baseCurrencyCode) {
    this.baseCurrencyCode = baseCurrencyCode;
  }

  public String getDefaultDisplayCurrencyCode() {
    return defaultDisplayCurrencyCode;
  }

  public void setDefaultDisplayCurrencyCode(String defaultDisplayCurrencyCode) {
    this.defaultDisplayCurrencyCode = defaultDisplayCurrencyCode;
  }

  public String getTimezone() {
    return timezone;
  }

  public void setTimezone(String timezone) {
    this.timezone = timezone;
  }

  public String getWeightUnit() {
    return weightUnit;
  }

  public void setWeightUnit(String weightUnit) {
    this.weightUnit = weightUnit;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getBaseLinkUrl() {
    return baseLinkUrl;
  }

  public void setBaseLinkUrl(String baseLinkUrl) {
    this.baseLinkUrl = baseLinkUrl;
  }

  public String getBaseStaticUrl() {
    return baseStaticUrl;
  }

  public void setBaseStaticUrl(String baseStaticUrl) {
    this.baseStaticUrl = baseStaticUrl;
  }

  public String getBaseMediaUrl() {
    return baseMediaUrl;
  }

  public void setBaseMediaUrl(String baseMediaUrl) {
    this.baseMediaUrl = baseMediaUrl;
  }

  public String getSecureBaseUrl() {
    return secureBaseUrl;
  }

  public void setSecureBaseUrl(String secureBaseUrl) {
    this.secureBaseUrl = secureBaseUrl;
  }

  public String getSecureBaseLinkUrl() {
    return secureBaseLinkUrl;
  }

  public void setSecureBaseLinkUrl(String secureBaseLinkUrl) {
    this.secureBaseLinkUrl = secureBaseLinkUrl;
  }

  public String getSecureBaseStaticUrl() {
    return secureBaseStaticUrl;
  }

  public void setSecureBaseStaticUrl(String secureBaseStaticUrl) {
    this.secureBaseStaticUrl = secureBaseStaticUrl;
  }

  public String getSecureBaseMediaUrl() {
    return secureBaseMediaUrl;
  }

  public void setSecureBaseMediaUrl(String secureBaseMediaUrl) {
    this.secureBaseMediaUrl = secureBaseMediaUrl;
  }
}
