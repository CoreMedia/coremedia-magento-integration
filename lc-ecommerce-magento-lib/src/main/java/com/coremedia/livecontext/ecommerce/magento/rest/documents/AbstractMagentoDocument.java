package com.coremedia.livecontext.ecommerce.magento.rest.documents;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class AbstractMagentoDocument {

  private static final String ATTRIBUTE_CODE = "attribute_code";
  private static final String ATTRIBUTE_VALUE = "value";

  /**
   * ID of the document.
   */
  @JsonProperty("id")
  private String id;

  /**
   * Map containing all custom attributes.
   */
  private Map<String, Object> customAttributes = new HashMap<>();

  /**
   * Map containing all extension attributes.
   */
//  @JsonProperty("extension_attributes")
  private Map<String, Object> extensionAttributes = new HashMap<>();

  /**
   * Map containing all unmapped attributes.
   */
  private Map<String, Object> unmappedAttributes = new HashMap<>();

  public Map<String, Object> getCustomAttributes() {
    return customAttributes;
  }

  @JsonProperty("custom_attributes")
  public void setCustomAttributes(List<LinkedHashMap<String, Object>> attributes) {
    Map<String, Object> result = new HashMap<>();
    for (LinkedHashMap<String, Object> attr : attributes) {
      String attrCode = (String) attr.get(ATTRIBUTE_CODE);
      Object attrValue = attr.get(ATTRIBUTE_VALUE);
      if (StringUtils.isNotBlank(attrCode)) {
        result.put(attrCode, attrValue);
      }
    }

    this.customAttributes = result;
  }

  public Object getCustomAttribute(String attributeCode) {
    return customAttributes.get(attributeCode);
  }

  public void setCustomAttributes(Map<String, Object> customAttributes) {
    this.customAttributes = customAttributes;
  }

  public Map<String, Object> getExtensionAttributes() {
    return extensionAttributes;
  }

  public void setExtensionAttributes(Map<String, Object> extensionAttributes) {
    this.extensionAttributes = extensionAttributes;
  }

  @JsonAnyGetter
  public Map<String, Object> unmapped() {
    return unmappedAttributes;
  }

  public Object get(String name) {
    return unmappedAttributes.get(name);
  }

  @JsonAnySetter
  public void set(String name, Object value) {
    unmappedAttributes.put(name, value);
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }
}
