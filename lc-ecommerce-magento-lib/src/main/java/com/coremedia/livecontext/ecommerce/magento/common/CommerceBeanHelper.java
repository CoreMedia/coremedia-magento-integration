package com.coremedia.livecontext.ecommerce.magento.common;

import com.coremedia.blueprint.base.livecontext.ecommerce.common.DefaultConnection;
import com.coremedia.livecontext.ecommerce.common.CommerceBean;
import com.coremedia.livecontext.ecommerce.common.CommerceBeanFactory;
import com.coremedia.livecontext.ecommerce.common.CommerceConnection;
import com.coremedia.livecontext.ecommerce.common.StoreContext;
import com.coremedia.livecontext.ecommerce.magento.beans.AbstractMagentoCommerceBean;
import com.coremedia.livecontext.ecommerce.magento.rest.documents.AbstractMagentoDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


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
   * @return
   */
  public <T extends CommerceBean> T createBeanFor(AbstractMagentoDocument delegate, Class<T> aClass) {
    if (delegate == null) {
      return null;
    }

    String id = CommerceIdHelper.convertToInternalId(delegate.getId(), aClass);
    CommerceConnection connection = DefaultConnection.get();
    StoreContext storeContext = connection.getStoreContext();
    AbstractMagentoCommerceBean bean = (AbstractMagentoCommerceBean) commerceBeanFactory.createBeanFor(id, storeContext);

    LOG.debug("Created commerce bean for '{}'", id);

    return aClass.cast(bean);
  }


  /**
   * Creates a list of {@link CommerceBean}s for the given delegates with the given target class.
   *
   * @param delegates list of delegates {@link AbstractMagentoDocument}
   * @param aClass    target class
   * @param <T>       type of the target {@link CommerceBean} class
   * @return
   */
  public <T extends CommerceBean> List<T> createBeansFor(List<? extends AbstractMagentoDocument> delegates, Class<T> aClass) {
    if (delegates == null || delegates.isEmpty()) {
      return Collections.emptyList();
    }

    List<T> result = new ArrayList<>(delegates.size());
    for (AbstractMagentoDocument delegate : delegates) {
      T bean = createBeanFor(delegate, aClass);
      if (bean != null) {
        result.add(bean);
      }
    }

    return Collections.unmodifiableList(result);
  }


  public CommerceBeanFactory getCommerceBeanFactory() {
    return commerceBeanFactory;
  }


  public void setCommerceBeanFactory(CommerceBeanFactory commerceBeanFactory) {
    this.commerceBeanFactory = commerceBeanFactory;
  }

}
