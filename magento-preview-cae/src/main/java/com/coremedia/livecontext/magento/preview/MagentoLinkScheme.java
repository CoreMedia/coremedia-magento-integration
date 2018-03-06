package com.coremedia.livecontext.magento.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.commercebeans.ProductInSite;
import com.coremedia.livecontext.contentbeans.CMExternalChannel;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.contentbeans.LiveContextExternalChannelImpl;
import com.coremedia.livecontext.ecommerce.catalog.Category;
import com.coremedia.livecontext.ecommerce.catalog.Product;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.magento.common.MagentoStoreContextHelper;
import com.coremedia.objectserver.web.links.Link;
import org.apache.solr.common.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.inject.Named;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static java.util.stream.Collectors.joining;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

/**
 * Link scheme for Magento {@link CommerceBean}s.
 * Used to view commerce beans like categories and products through the redirecting CAE
 * in the Magento system.
 */
@Named
@Link
public class MagentoLinkScheme {

  private static final Logger LOG = LoggerFactory.getLogger(MagentoLinkScheme.class);

  private static final String URL_SUFFIX = ".html";

  /**
   * Builds a preview link for the provided {@link CMExternalChannel}.
   *
   * @param cmExternalChannel the category
   * @return URL to category preview links
   */
  @Link(type = LiveContextExternalChannelImpl.class, order = HIGHEST_PRECEDENCE + 1)
  public Object buildLinkForExternalChannel(LiveContextExternalChannelImpl cmExternalChannel) {
    if (!isApplicable()) {
      return null;
    }

    Category category = cmExternalChannel.getCategory();
    return buildCategoryUrl(category);
  }

  /**
   * Builds page links
   *
   * @param navigation     the navigation to build the page link for
   * @param linkParameters additional link parameters
   * @return the generated CoreMedia page URL
   */
  @Link(type = CMExternalPage.class, order = HIGHEST_PRECEDENCE + 1)
  public Object buildLinkForExternalPage(CMExternalPage navigation, Map<String, Object> linkParameters) {
    if (navigation.isRoot()) {
      return getBaseUrl();
    }

    return getBaseUrl() + "cm-page?externalRef=cm-" + navigation.getContentId()
            + "&seoSegment=" + navigation.getSegment();
  }

  /**
   * Builds a preview link for the provided {@link Category}.
   *
   * @param category the category
   * @return URL to category preview links
   */
  @Link(type = Category.class, order = HIGHEST_PRECEDENCE + 1)
  public Object buildLinkForStudioPreview(Category category) {
    return buildCategoryUrl(category);
  }

  /**
   * Builds a preview link for the provided {@link Product}.
   *
   * @param product the product
   * @return URL to product preview links
   */
  @Link(type = Product.class, order = HIGHEST_PRECEDENCE + 1)
  public Object buildLinkForStudioPreview(Product product) {
    LOG.info("buildProductLinkForStudioPreview() {}", product);
    String productNameSegment = product.getSeoSegment();

    // If there is no URL segment available use the same algorithm
    // Magento uses for empty SEO fields.
    if (StringUtils.isEmpty(productNameSegment)) {
      productNameSegment = product.getName().toLowerCase().replaceAll(" ", "-");
    }

    return getBaseUrl() + productNameSegment + URL_SUFFIX;
  }

  @Link(type = ProductInSite.class, order = HIGHEST_PRECEDENCE + 1)
  public Object buildPreviewLinkForProductInSite(ProductInSite productInSite) {
    if (!isApplicable()) {
      return null;
    }

    return buildLinkForStudioPreview(productInSite.getProduct());
  }

  private static boolean isApplicable() {
    return CurrentCommerceConnection.find()
            .map(CommerceConnection::getVendorName)
            .filter(vendorName -> vendorName.contains("Magento"))
            .isPresent();
  }

  @Nonnull
  private static String buildCategoryUrl(@Nonnull Category category) {
    List<Category> breadCrumbCategories = category.getBreadcrumb();

    String pathSegments = breadCrumbCategories.stream()
            .filter(c -> !c.isRoot()) // root category shall not be part of the url
            .map(Category::getSeoSegment)
            .filter(Objects::nonNull)
            .collect(joining("/"));

    return getBaseUrl() + pathSegments + URL_SUFFIX;
  }

  /**
   * Returns the base Magento base URL
   * retrieved from the current {@link com.coremedia.livecontext.ecommerce.common.StoreContext}.
   */
  @Nonnull
  private static String getBaseUrl() {
    String baseUrl = MagentoStoreContextHelper.getBaseLinkUrl(MagentoStoreContextHelper.getCurrentContext());
    return baseUrl + ensureTrailingSlash(baseUrl);
  }

  @Nonnull
  private static String ensureTrailingSlash(@Nonnull String baseUrl) {
    return baseUrl.endsWith("/") ? "" : "/";
  }
}
