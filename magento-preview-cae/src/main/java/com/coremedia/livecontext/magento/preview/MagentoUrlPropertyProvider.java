package com.coremedia.livecontext.magento.preview;

import com.coremedia.cap.common.IdHelper;
import com.coremedia.cap.content.Content;
import com.coremedia.cap.content.ContentRepository;
import com.coremedia.cap.content.ContentType;
import com.coremedia.livecontext.ecommerce.common.CommercePropertyProvider;
import com.coremedia.livecontext.ecommerce.magento.common.MagentoStoreContextHelper;
import com.coremedia.livecontext.handler.LiveContextPageHandlerBase;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.util.UriComponentsBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * Generates Magento URLs for CAE Links.
 */
public class MagentoUrlPropertyProvider implements CommercePropertyProvider {

  private static final String CM_EXTERNAL_PAGE = "CMExternalPage";
  private static final String EXTERNAL_ID = "externalId";
  private static final String SEGMENT = "segment";

  private ContentRepository contentRepository;

  @Nullable
  @Override
  public Object provideValue(@Nonnull Map<String, Object> parameters) {
    String externalId = (String) parameters.get(LiveContextPageHandlerBase.URL_PROVIDER_URL_TEMPLATE);
    if(externalId != null) {
      Content page = getPageForExternalId(externalId);
      String resultUrl = getBaseUrl() + "cm-page?externalRef=cm-" + IdHelper.parseContentId(page.getId()) + "&seoSegment=" + page.getProperties().get(SEGMENT);
      return UriComponentsBuilder.fromUriString(resultUrl).build();
    }
    return null;
  }

  /**
   * Returns the content that matches the given external id.
   */
  private Content getPageForExternalId(String externalId) {
    if(externalId == null) {
      return null;
    }

    ContentType externalPageType = contentRepository.getContentType(CM_EXTERNAL_PAGE);
    Set<Content> instances = externalPageType.getInstances();
    for (Content instance : instances) {
      if(externalId.equals(instance.getProperties().get(EXTERNAL_ID))) {
        return instance;
      }
    }
    return null;
  }

  /**
   * Returns the base Magento base url
   * retrieved from the current {@link com.coremedia.livecontext.ecommerce.common.StoreContext}.
   */
  private String getBaseUrl() {
    return MagentoStoreContextHelper.getBaseLinkUrl(MagentoStoreContextHelper.getCurrentContext());
  }

  @Required
  public void setContentRepository(ContentRepository contentRepository) {
    this.contentRepository = contentRepository;
  }
}
