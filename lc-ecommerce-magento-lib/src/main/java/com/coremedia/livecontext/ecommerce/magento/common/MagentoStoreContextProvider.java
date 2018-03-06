package com.coremedia.livecontext.ecommerce.magento.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractStoreContextProvider;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.blueprint.base.settings.SettingsService;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.multisite.Site;
import com.coremedia.cap.multisite.SitesService;
import com.coremedia.cap.struct.Struct;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.common.StoreContextProvider;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.StoreConfigDocument;
import com.coremedia.livecontext.ecommerce.magento.rest.resources.StoreConfigResource;
import org.apache.commons.lang3.LocaleUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static com.coremedia.blueprint.base.livecontext.ecommerce.common.StoreContextImpl.LOCALE;

/**
 * {@link StoreContextProvider} implementation for Magento store contexts.
 * <p/>
 * This provider fetches additional store configuration settings directly from the configured Magento store
 * via the Magento REST-API using the LiveContext setting <code>livecontext.store.id</code> as the store's code.
 * <p/>
 * If no store code is provided in the LiveContext settings, this provider uses a fallback store code
 * that can be configured with the <code>livecontext.magento.default.store.code</code> Spring property.
 * <p/>
 * The default store code in magento is <code>default</code>.
 */
public class MagentoStoreContextProvider extends AbstractStoreContextProvider {

  private static final Logger LOG = LoggerFactory.getLogger(MagentoStoreContextProvider.class);

  /**
   * Resource used to fetch store configuration settings.
   */
  private StoreConfigResource storeConfigResource;

  private CommerceCache commerceCache;

  /**
   * Default magento store code. Stores have an id and a code in magento. This is the code.
   * <p/>
   * Can be configured via Spring property <code>livecontext.magento.default.store.code</code>.
   * Defaults to <code>default</code>.
   */
  private String defaultStoreCode;

  /**
   * Default store locale.
   * <p/>
   * Can be configured via Spring property <code>livecontext.magento.default.locale</code>.
   * Defaults to <code>en_US</code>.
   */
  private String defaultStoreLocale;

  /**
   * Creates the {@link StoreContext} using the LiveContext settings
   * as well as fetching additional values from directly from the Magento REST-API.
   */
  protected StoreContext internalCreateContext(@Nonnull Site site) {
    LOG.info("internalCreateContext() {}", site);

    SettingsService settingsService = getSettingsService();
    Object[] beans = {site};
    String code = settingsService.settingWithDefault("livecontext.store.code", String.class, getDefaultStoreCode(),
            beans);
    String name = settingsService.setting("livecontext.store.name", String.class, beans);

    LOG.info("internalCreateContext() code={} name={}", code, name);
    // Fetch additional store configuration
    StoreConfigDocument storeConfig = null;
    if (StringUtils.isNotBlank(code)) {
      storeConfig = storeConfigResource.getStoreConfig(code); //TODO add cache key
    }

    LOG.info("internalCreateContext() config={}", storeConfig);
    StoreContext context = MagentoStoreContextHelper.buildContext(site, name, storeConfig);

    if (context.getLocale() == null) {
      // Set default locale
      context.put(LOCALE, LocaleUtils.toLocale(getDefaultStoreLocale()));
    }

    LOG.info("internalCreateContext() context={}", context);
    return context;
  }

  @Required
  public void setStoreConfigResource(StoreConfigResource storeConfigResource) {
    this.storeConfigResource = storeConfigResource;
  }

  public CommerceCache getCommerceCache() {
    return commerceCache;
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  @Value("${livecontext.magento.default.store.code:default}")
  public void setDefaultStoreCode(String defaultStoreCode) {
    this.defaultStoreCode = defaultStoreCode;
  }

  public String getDefaultStoreLocale() {
    return defaultStoreLocale;
  }

  @Value("${livecontext.magento.default.locale:en_US}")
  public void setDefaultStoreLocale(String defaultStoreLocale) {
    this.defaultStoreLocale = defaultStoreLocale;
  }

  @Override
  public void setSitesService(SitesService ss) {
    LOG.info("setSitesService()");
    super.setSitesService(ss);
  }

  @Override
  public void setSettingsService(SettingsService ss) {
    LOG.info("setSettingsService()");
    super.setSettingsService(ss);
  }

  @Override
  public SettingsService getSettingsService() {
    LOG.info("getSettingsService()");
    return super.getSettingsService();
  }

  @Override
  protected void updateStoreConfigFromRepository(@Nonnull Struct struct, @Nonnull Map<String, Object> map,
                                                 @Nonnull Site site) {
    LOG.info("updateStoreConfigFromRepository() {} / {}", struct, map.keySet());
    super.updateStoreConfigFromRepository(struct, map, site);
  }

  @Override
  protected void readStoreConfigFromSpring(@Nullable String string, @Nonnull Map<String, Object> map) {
    LOG.info("readStoreConfigFromSpring() {}", string);
    super.readStoreConfigFromSpring(string, map);
  }

  @Override
  public StoreContext createContext(@Nonnull Site site) {
    LOG.debug("createContext() {}", site);
    StoreContext result = super.createContext(site);
    LOG.info("createContext() result={}", result != null ? result.getStoreName() : null);
    return result;
  }

  @Nullable
  @Override
  public StoreContext findContextByContent(@Nonnull Content content) {
    LOG.info("findContextByContent() {}", content.getPath());
    return super.findContextByContent(content);
  }

  @Nullable
  @Override
  public StoreContext findContextBySite(@Nullable Site site) {
    LOG.debug("findContextBySite() {}", site);
    StoreContext result = super.findContextBySite(site);
    LOG.info("findContextBySite() result={}", result != null ? result.getStoreName() : null);
    return result;
  }

  @Nullable
  @Override
  public StoreContext findContextBySiteId(@Nonnull String id) {
    LOG.info("findContextBySiteId() {}", id);
    return super.findContextBySiteId(id);
  }

  @Override
  public void setStoreConfigurations(Map<String, Map<String, String>> map) {
    LOG.info("setStoreConfigurations() {}", map.keySet());
    super.setStoreConfigurations(map);
  }

  public String getDefaultStoreCode() {
    return defaultStoreCode;
  }
}
