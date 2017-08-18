package com.coremedia.livecontext.magento.preview;

import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannelImpl;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.magento.common.MagentoStoreContextHelper;
import com.coremedia.objectserver.web.links.Link;
import com.google.common.base.Joiner;
import org.apache.commons.collections.CollectionUtils;
import org.apache.solr.common.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;


/**
 * Link scheme for Magento {@link CommerceBean}s.
 * Used to view commerce beans like categories and products through the redirecting CAE
 * in the Magento system.
 */
@Named
@Link
public class MagentoCommerceBeanLinkScheme {
  private static final Logger LOG = LoggerFactory.getLogger(MagentoCommerceBeanLinkScheme.class);
  private static final Joiner PATH_SEGMENT_JOINER = Joiner.on("/").skipNulls();
  private static final String URL_SUFFIX = ".html";

  /**
   * Builds a preview link for the provided {@link CMExternalChannel}.
   *
   * @param cmExternalChannel the category
   * @return URL to category preview links
   */
  @Link(type = LiveContextExternalChannelImpl.class, order = HIGHEST_PRECEDENCE + 1)
  public Object buildLinkForExternalChannel(LiveContextExternalChannelImpl cmExternalChannel, final HttpServletRequest request) {
    if (!isApplicable(request)) {
      return null;
    }
    Category category = cmExternalChannel.getCategory();
    List<Category> breadCrumbCategories = category.getBreadcrumb();
    List<String> pathSegments = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(breadCrumbCategories)) {
      for (Category c : breadCrumbCategories) {
        if (!c.isRoot()) { // root category shall not be part of the url
          pathSegments.add(c.getSeoSegment());
        }
      }
    }
    LOG.info("buildLinkForStudioPreview() {}", PATH_SEGMENT_JOINER.join(pathSegments) + URL_SUFFIX);
    return buildUrl(PATH_SEGMENT_JOINER.join(pathSegments));
  }

  /**
   * Builds page links
   * @param navigation the navigation to build the page link for
   * @param linkParameters additional link parameters
   * @return the generated CoreMedia page URL
   */
  @Link(type = CMExternalPage.class)
  public Object buildLinkForExternalPage(
          final CMExternalPage navigation,
          final Map<String, Object> linkParameters) {
    if(navigation.isRoot()) {
      return buildUrl(null);
    }

    return getBaseUrl() + "cm-page?externalRef=cm-" + navigation.getContentId() + "&seoSegment=" + navigation.getSegment();
  }


  /**
   * Builds a preview link for the provided {@link Category}.
   *
   * @param category the category
   * @return URL to category preview links
   */
  @Link(type = Category.class)
  public Object buildLinkForStudioPreview(Category category) {
    List<Category> breadCrumbCategories = category.getBreadcrumb();
    List<String> pathSegments = new ArrayList<>();
    if (CollectionUtils.isNotEmpty(breadCrumbCategories)) {
      for (Category c : breadCrumbCategories) {
        if (!c.isRoot()) { // root category shall not be part of the url
          pathSegments.add(c.getSeoSegment());
        }
      }
    }
    LOG.info("buildLinkForStudioPreview() {}", PATH_SEGMENT_JOINER.join(pathSegments) + URL_SUFFIX);
    return getBaseUrl() + PATH_SEGMENT_JOINER.join(pathSegments) + URL_SUFFIX;
  }


  /**
   * Builds a preview link for the provided {@link Product}.
   *
   * @param product the product
   * @return URL to product preview links
   */
  @Link(type = Product.class)
  public Object buildLinkForStudioPreview(Product product) {
    LOG.info("buildProductLinkForStudioPreview() {}", product);
    String productNameSegment = product.getSeoSegment();
    //if there is no URL segment available we use the same algorithm Magento does for empty SEO fields
    if(StringUtils.isEmpty(productNameSegment)) {
      productNameSegment = product.getName().toLowerCase().replaceAll(" ", "-");
    }

    LOG.info("buildLinkForStudioPreview() {}", productNameSegment + URL_SUFFIX);
    return getBaseUrl() + "/" + productNameSegment + URL_SUFFIX;
  }

  @Link(type = ProductInSite.class, order = HIGHEST_PRECEDENCE + 1)
  public Object buildPreviewLinkForProductInSite(ProductInSite productInSite, HttpServletRequest request,
                                                 HttpServletResponse response) {
    if (!isApplicable(request)) {
      return null;
    }

    return buildLinkForStudioPreview(productInSite.getProduct());
  }

  static boolean isApplicable(HttpServletRequest request) {
    return true; //Magento Commerce Led only
  }

  /**
   * Returns the base Magento base url
   * retrieved from the current {@link com.coremedia.livecontext.ecommerce.common.StoreContext}.
   *
   * @return
   */
  private String getBaseUrl() {
    return MagentoStoreContextHelper.getBaseLinkUrl(MagentoStoreContextHelper.getCurrentContext());
  }

  /**
   * Concatinates the url
   *
   * @param segmentString - the string part after the domain
   * @return The url baseUrl + segmentString + URL_SUFFIX
   */
  private String buildUrl(String segmentString) {
    if (segmentString != null && segmentString.length() > 0) {
      return getBaseUrl() + (getBaseUrl().endsWith("/") ? "" : "/") + segmentString + URL_SUFFIX;
    }

    return getBaseUrl() + (getBaseUrl().endsWith("/") ? "" : "/");
  }

}
