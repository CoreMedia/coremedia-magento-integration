# Description

This is the wrapper cookbook to deploy CoreMedia Blueprint
Livecontext for Magento.

# Requirements

## Platform:

*No platforms defined*

## Cookbooks:

* blueprint-base
* blueprint-tomcat
* blueprint-proxy
* coremedia-proxy (~> 0.3.2)

# Attributes

* `node['blueprint']['livecontext']['magento']['cms_host']` - convenience property to configure a test system for local apache development against a remote system, do not set this attribute in recipes or use it in recipes, use the concrete attributes instead. Defaults to `localhost`.
* `node['blueprint']['livecontext']['magento']['host']` -  Defaults to `magentohost`.
* `node['blueprint']['livecontext']['magento']['application.properties']['livecontext.magento.host']` -  Defaults to `node['blueprint']['livecontext']['magento']['host']`.
* `node['blueprint']['livecontext']['magento']['application.properties']['livecontext.cookie.domain']` -  Defaults to `.#{node['fqdn']}`.
* `node['blueprint']['livecontext']['magento']['virtual_host']['delivery']['cluster']['default']['host']` -  Defaults to `node['blueprint']['livecontext']['magento']['cms_host']`.
* `node['blueprint']['livecontext']['magento']['virtual_host']['delivery']['cluster']['default']['port']` -  Defaults to `42180`.
* `node['blueprint']['livecontext']['magento']['virtual_host']['delivery']['context']` -  Defaults to `blueprint`.
* `node['blueprint']['livecontext']['magento']['virtual_host']['delivery']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['server_name']` -  Defaults to `shop-preview-apparel.#{node['blueprint']['hostname']}`.
* `node['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['time_travel_alias']` -  Defaults to `shop-preview-apparel.#{node['blueprint']['hostname']}`.
* `node['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['livecontext']['magento']['virtual_host']['shop']['server_name']` -  Defaults to `shop-apparel.#{node['blueprint']['hostname']}`.
* `node['blueprint']['livecontext']['magento']['virtual_host']['shop']['time_travel_alias']` -  Defaults to `shop-apparel.#{node['blueprint']['hostname']}`.
* `node['blueprint']['livecontext']['magento']['virtual_host']['shop']['server_aliases']` -  Defaults to `[ ... ]`.
* `node['blueprint']['livecontext']['magento']['virtual_host']['shop']['rewrite_log_level']` -  Defaults to `rewrite_log_level`.
* `node['blueprint']['livecontext']['magento']['ssl_proxy_verify']` -  Defaults to `true`.

# Recipes

* [blueprint-lc3-magento::cae-live-config](#blueprint-lc3-magentocae-live-config) - This recipe configures the CoreMedia Blueprint Live CAE.
* [blueprint-lc3-magento::cae-preview-config](#blueprint-lc3-magentocae-preview-config) - This recipe configures the CoreMedia Blueprint Preview CAE.
* blueprint-lc3-magento::candy-shop-preview-proxy
* [blueprint-lc3-magento::delivery-proxy](#blueprint-lc3-magentodelivery-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint Live CAE.
* blueprint-lc3-magento::overview
* [blueprint-lc3-magento::shop-preview-proxy](#blueprint-lc3-magentoshop-preview-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Magento Preview Shop.
* [blueprint-lc3-magento::shop-proxy](#blueprint-lc3-magentoshop-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Magento Live Shop.
* [blueprint-lc3-magento::studio-config](#blueprint-lc3-magentostudio-config) - This recipe configures the CoreMedia Blueprint Studio.
* [blueprint-lc3-magento::studio-proxy](#blueprint-lc3-magentostudio-proxy) - This recipe installs virtual hosts for the CoreMedia Blueprint Studio.
* [blueprint-lc3-magento::test-data-config](#blueprint-lc3-magentotest-data-config)

## blueprint-lc3-magento::cae-live-config

This recipe configures the CoreMedia Blueprint Live CAE.

## blueprint-lc3-magento::cae-preview-config

This recipe configures the CoreMedia Blueprint Preview CAE.

## blueprint-lc3-magento::delivery-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint Live CAE.

## blueprint-lc3-magento::shop-preview-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Magento Preview Shop.

## blueprint-lc3-magento::shop-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Magento Live Shop.

## blueprint-lc3-magento::studio-config

This recipe configures the CoreMedia Blueprint Studio.

## blueprint-lc3-magento::studio-proxy

This recipe installs virtual hosts for the CoreMedia Blueprint Studio.

## blueprint-lc3-magento::test-data-config

This recipe sets properties only necessary if the test content is being used

# Author

Author:: CoreMedia (<support@coremedia.com>)
