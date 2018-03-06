package com.coremedia.livecontext.ecommerce.magento.beans;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceBean;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.AbstractCommerceCacheKey;
import com.coremedia.blueprint.base.livecontext.ecommerce.common.CommerceCache;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.AbstractMagentoDocument;
import com.coremedia.livecontext.ecommerce.magento.rest.resources.CatalogResource;
import com.coremedia.xml.Markup;
import com.coremedia.xml.MarkupFactory;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Required;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * Intermediate base class to extends commerce beans with generic methods needed when dealing with magento
 * as the commerce backend.
 */
public abstract class AbstractMagentoCommerceBean extends AbstractCommerceBean {

  private static final String URL_KEY = "url_key";

  private AbstractMagentoDocument delegate;
  private CommerceCache commerceCache;
  private CatalogResource catalogResource;

  public AbstractMagentoDocument getDelegate() {
    if (delegate == null) {
      load();
    }
    return delegate;
  }

  protected CommerceCache getCommerceCache() {
    return commerceCache;
  }

  /**
   * Sets a delegate as an arbitrarily backing object.
   *
   * @param delegate the arbitrarily backing object
   */
  public void setDelegate(AbstractMagentoDocument delegate) {
    this.delegate = delegate;
  }

  protected static Markup buildRichtextMarkup(String str, boolean escapeXml) {
    StringBuilder sb = new StringBuilder(256);
    sb.append("<div xmlns=\"http://www.coremedia.com/2003/richtext-1.0\" xmlns:xlink=\"http://www.w3.org/1999/xlink\">");  // NOSONAR

    if (StringUtils.isNotBlank(str)) {
      if (escapeXml) {
        // Remove trailing and ending <p> tags!
        if (str.startsWith("<p>")) {
          str = str.replaceFirst("<p>", "");
        }
        if (str.endsWith("</p>")) {
          str = str.substring(0, str.lastIndexOf("</p>"));
        }

        sb.append("<p>");  // NOSONAR
        sb.append(StringEscapeUtils.escapeXml(str));
        sb.append("</p>");  // NOSONAR
      } else {
        if (!str.startsWith("<p>")) {
          sb.append("<p>");
        }
        sb.append(str);
        if (!str.endsWith("</p>")) {
          sb.append("</p>");
        }
      }
    }

    sb.append("</div>");  // NOSONAR

    return MarkupFactory.fromString(sb.toString());
  }

  protected static Markup buildRichtextMarkup(String str) {
    return buildRichtextMarkup(str, true);
  }

  protected void loadCached(AbstractCommerceCacheKey cacheKey) {
    AbstractMagentoDocument delegate = (AbstractMagentoDocument) getCommerceCache().get(cacheKey);
    setDelegate(delegate);
  }

  @Override
  public String getExternalId() {
    return getDelegate().getId();
  }

  @Override
  public String getExternalTechId() {
    return getExternalId();
  }

  public String getSeoSegment() {
    String urlKey = (String) getDelegate().getCustomAttribute(URL_KEY);

    if (urlKey == null) {
      return null;
    }

    try {
      return URLEncoder.encode(urlKey, "UTF-8");
    } catch (UnsupportedEncodingException ignored) {
      return null;
    }
  }

  @Required
  public void setCommerceCache(CommerceCache commerceCache) {
    this.commerceCache = commerceCache;
  }

  public CatalogResource getCatalogResource() {
    return catalogResource;
  }

  @Required
  public void setCatalogResource(CatalogResource catalogResource) {
    this.catalogResource = catalogResource;
  }
}
