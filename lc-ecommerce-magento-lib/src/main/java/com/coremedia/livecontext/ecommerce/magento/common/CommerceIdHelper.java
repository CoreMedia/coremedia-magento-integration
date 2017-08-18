package com.coremedia.livecontext.ecommerce.magento.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.BaseCommerceIdProvider;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.catalog.ProductVariant;
import com.coremedia.livecontext.ecommerce.common.InvalidIdException;
import com.coremedia.livecontext.ecommerce.p13n.Segment;
import org.apache.commons.lang3.StringUtils;


/**
 * Utility class to convert external Magento ids to internal commerce ids and vice versa.
 */
public final class CommerceIdHelper {

  private static final String MAGENTO_VENDOR_PREFIX = "mag";

  private static final BaseCommerceIdProvider INSTANCE = new BaseCommerceIdProvider();

  public static final String TECH_ID_PREFIX = "techId:";

  static {
    INSTANCE.setVendor(MAGENTO_VENDOR_PREFIX);
  }

  private CommerceIdHelper() {
  }


  public static String formatCategoryId(String externalId) {
    return convertToInternalId(externalId, Category.class);
  }


  public static String formatProductId(String externalId) {
    return convertToInternalId(externalId, Product.class);
  }

  public static boolean isTechId(String objectId) {
    return objectId != null && objectId.contains(TECH_ID_PREFIX);
  }

  /**
   * Converts a given (external) id to an internal id.
   *
   * @param externalId external id
   * @param typeOf
   * @return
   */
  public static String convertToInternalId(String externalId, Class typeOf) {

    if (ProductVariant.class.isAssignableFrom(typeOf)) {
      return INSTANCE.formatProductVariantId(externalId);
    }

    if (Product.class.isAssignableFrom(typeOf)) {
      return INSTANCE.formatProductId(externalId);
    }

    if (Category.class.isAssignableFrom(typeOf)) {
      return INSTANCE.formatCategoryId(externalId);
    }

    if (Segment.class.isAssignableFrom(typeOf)) {
      return INSTANCE.formatSegmentId(externalId);
    }

    throw new InvalidIdException("Unsupported bean type");
  }


  /**
   * Converts a given (internal) id to an external id.
   *
   * @param internalId internal id
   * @return
   */
  public static String convertToExternalId(String internalId) {
    String id = isInternalId(internalId) ? INSTANCE.parseExternalIdFromId(internalId) : internalId;

    if (CommerceIdHelper.isTechId(id)) {
      id = CommerceIdHelper.parseIdFromTechId(id);
    }

    return id;
  }


  public static boolean isSkuId(String id) {
    return StringUtils.isNoneBlank(id);
  }


  private static boolean isInternalId(String id) {
    return id.startsWith(MAGENTO_VENDOR_PREFIX);
  }


  public static boolean isCategoryId(String id) {
    return true;
  }

  public static String parseIdFromTechId(String techId) {
    if(techId.contains(TECH_ID_PREFIX)) {
      return techId.split(":")[1];
    }
    return techId;
  }
}
