package com.coremedia.livecontext.magento.preview;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.blueprint.common.contentbeans.CMNavigation;
import com.coremedia.livecontext.contentbeans.CMExternalPage;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.magento.common.MagentoStoreContextHelper;
import com.coremedia.livecontext.fragment.links.transformers.resolvers.LiveContextLinkResolver;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;

public class MagentoLinkResolver implements LiveContextLinkResolver {

  @Override
  public boolean isApplicable(Object bean) {
    return CurrentCommerceConnection.find()
            .map(CommerceConnection::getVendorName)
            .filter(vendorName -> vendorName.contains("Magento"))
            .isPresent();
  }

  @Override
  public String resolveUrl(Object bean, String variant, CMNavigation navigation, HttpServletRequest request) {
    if (!(bean instanceof CMExternalPage)) {
      return null;
    }

    return getBaseUrl() + "cm-page?externalRef=cm-" + navigation.getContentId()
            + "&seoSegment=" + navigation.getSegment();
  }

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
