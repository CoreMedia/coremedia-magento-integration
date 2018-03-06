package com.coremedia.livecontext.ecommerce.magento.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl;
import com.coremedia.cap.multisite.Site;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.InvalidContextException;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.StoreConfigDocument;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import java.util.Currency;
import java.util.Optional;

/**
 * Magento specific {@link StoreContext} utilities.
 */
public class MagentoStoreContextHelper {

  public static final String CODE = "code";
  public static final String WEBSITE_ID = "websiteId";
  public static final String TIMEZONE = "timezone";
  public static final String BASE_CURRENCY_CODE = "baseCurrencyCode";
  public static final String DEFAULT_DISPLAY_CURRENCY_CODE = "defaultDisplayCurrencyCode";
  public static final String WEIGHT_UNIT = "weightUnit";
  public static final String BASE_URL = "base_url";
  public static final String BASE_LINK_URL = "base_link_url";
  public static final String BASE_STATIC_URL = "base_static_url";
  public static final String BASE_MEDIA_URL = "base_media_url";
  public static final String SECURE_BASE_URL = "secure_base_url";
  public static final String SECURE_BASE_LINK_URL = "secure_base_link_url";
  public static final String SECURE_BASE_STATIC_URL = "secure_base_static_url";
  public static final String SECURE_BASE_MEDIA_URL = "secure_base_media_url";

  private MagentoStoreContextHelper() {
  }

  public static StoreContext buildContext(Site site, String name, StoreConfigDocument storeConfig) {
    if (storeConfig == null) {
      throw new InvalidContextException("Store config document from magento backend must not be null.");
    }

    String id = storeConfig.getId();

    if (StringUtils.isBlank(id)) {
      throw new InvalidContextException("Store id must not be empty.");
    }

    if (StringUtils.isBlank(name)) {
      throw new InvalidContextException("Store name must not be empty.");
    }

    StoreContext context = StoreContextImpl.newStoreContext();
    context.put(StoreContextImpl.CATALOG_ID, storeConfig.getCode());
//    context.put(StoreContextImpl.STORE_ID, id);
    //TODO we use the name instead of the ID since '1' is conflicting with existing LC implementations in the one repo
    context.put(StoreContextImpl.STORE_ID, storeConfig.getCode());
    context.put(StoreContextImpl.STORE_NAME, name);
    context.put(CODE, storeConfig.getCode());

    if (site != null) {
      context.put(StoreContextImpl.SITE, site.getId());
    }

    context.put(StoreContextImpl.LOCALE, LocaleUtils.toLocale(storeConfig.getLocale()));
    context.put(WEBSITE_ID, storeConfig.getWebsiteId());
    context.put(TIMEZONE, storeConfig.getTimezone());

    // Currencies
    context.put(StoreContextImpl.CURRENCY, Currency.getInstance(storeConfig.getBaseCurrencyCode()));
    context.put(BASE_CURRENCY_CODE, Currency.getInstance(storeConfig.getBaseCurrencyCode()));
    context.put(DEFAULT_DISPLAY_CURRENCY_CODE, Currency.getInstance(storeConfig.getDefaultDisplayCurrencyCode()));
    context.put(WEIGHT_UNIT, storeConfig.getWeightUnit());

    // Base urls
    context.put(BASE_URL, storeConfig.getBaseUrl());
    context.put(BASE_LINK_URL, storeConfig.getBaseLinkUrl());
    context.put(BASE_STATIC_URL, storeConfig.getBaseStaticUrl());
    context.put(BASE_MEDIA_URL, storeConfig.getBaseMediaUrl());

    // Secure base urls
    context.put(SECURE_BASE_URL, storeConfig.getSecureBaseUrl());
    context.put(SECURE_BASE_LINK_URL, storeConfig.getSecureBaseLinkUrl());
    context.put(SECURE_BASE_STATIC_URL, storeConfig.getSecureBaseStaticUrl());
    context.put(SECURE_BASE_MEDIA_URL, storeConfig.getSecureBaseMediaUrl());

    return context;
  }

  // --- Using provided context ---

  public static String getStoreCode(StoreContext context) {
    return (String) context.get(CODE);
  }

  public static String getWebsiteId(StoreContext context) {
    return (String) context.get(WEBSITE_ID);
  }

  public static Currency getBaseCurrencyCode(StoreContext context) {
    return (Currency) context.get(BASE_CURRENCY_CODE);
  }

  public static Currency getDefaultDisplayCurrencyCode(StoreContext context) {
    return (Currency) context.get(DEFAULT_DISPLAY_CURRENCY_CODE);
  }

  public static String getTimezone(StoreContext context) {
    return (String) context.get(TIMEZONE);
  }

  public static String getWeightUnit(StoreContext context) {
    return (String) context.get(WEIGHT_UNIT);
  }

  public static String getBaseUrl(StoreContext context) {
    return (String) context.get(BASE_URL);
  }

  @Nonnull
  public static String getBaseLinkUrl(StoreContext context) {
    return schemeless((String) context.get(BASE_LINK_URL));
  }

  public static String getBaseStaticUrl(StoreContext context) {
    return (String) context.get(BASE_STATIC_URL);
  }

  @Nonnull
  public static String getBaseMediaUrl(StoreContext context) {
    return schemeless((String) context.get(BASE_MEDIA_URL));
  }

  public static StoreContext getCurrentContext() {
    return findCurrentContext().orElse(null);
  }

  public static void setCurrentContext(StoreContext context) {
    CurrentCommerceConnection.get().setStoreContext(context);
  }

  @Nonnull
  public static Optional<StoreContext> findCurrentContext() {
    return CurrentCommerceConnection.find().map(CommerceConnection::getStoreContext);
  }

  @Nonnull
  private static String schemeless(@Nonnull String url) {
    if (url.contains("http")) {
      return url.substring(url.indexOf(":") + 1, url.length());
    }

    return url;
  }
}
