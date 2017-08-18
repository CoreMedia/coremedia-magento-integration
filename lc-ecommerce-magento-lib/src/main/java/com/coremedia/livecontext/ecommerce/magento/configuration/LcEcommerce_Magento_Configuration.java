package com.coremedia.livecontext.ecommerce.magento.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * expose the library's default configuration
 */
@Configuration
@PropertySource("classpath:/framework/spring/lc-ecommerce-magento.properties")
class LcEcommerce_Magento_Configuration {

}


