package com.coremedia.livecontext.ecommerce.magento.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.CurrentCommerceConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceId;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.magento.beans.AbstractMagentoCommerceBean;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.AbstractMagentoDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

/**
 * Utility class for {@link CommerceBean} creation.
 */
public class CommerceBeanHelper {

  private static final Logger LOG = LoggerFactory.getLogger(CommerceBeanHelper.class);

  private CommerceBeanFactory commerceBeanFactory;

  /**
   * Creates a {@link CommerceBean} for the given delegate and target class.
   *
   * @param delegate delegate {@link com.coremedia.livecontext.ecommerce.magento.rest.documents.AbstractMagentoDocument}
   * @param aClass   target class
   * @param <T>      type of the target {@link CommerceBean} class
   */
  public <T extends CommerceBean> T createBeanFor(AbstractMagentoDocument delegate, Class<T> aClass) {
    if (delegate == null) {
      return null;
    }

    StoreContext storeContext = CurrentCommerceConnection.get().getStoreContext();

    CommerceId commerceId = CommerceIdHelper.convertToInternalId(storeContext.getCatalogAlias(), delegate.getId(),
            aClass);

    AbstractMagentoCommerceBean bean = (AbstractMagentoCommerceBean) commerceBeanFactory
            .createBeanFor(commerceId, storeContext);

    LOG.debug("Created commerce bean for '{}'", commerceId);

    return aClass.cast(bean);
  }

  /**
   * Creates a list of {@link CommerceBean}s for the given delegates with the given target class.
   *
   * @param delegates list of delegates {@link AbstractMagentoDocument}
   * @param aClass    target class
   * @param <T>       type of the target {@link CommerceBean} class
   */
  public <T extends CommerceBean> List<T> createBeansFor(@Nonnull List<? extends AbstractMagentoDocument> delegates,
                                                         @Nonnull Class<T> aClass) {
    if (delegates.isEmpty()) {
      return emptyList();
    }

    return delegates.stream()
            .map(delegate -> createBeanFor(delegate, aClass))
            .filter(Objects::nonNull)
            .collect(collectingAndThen(toList(), Collections::unmodifiableList));
  }

  @Required
  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }
}
