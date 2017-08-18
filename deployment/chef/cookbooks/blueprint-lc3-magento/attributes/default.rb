rewrite_log_level = node['apache']['version'] == '2.4' ? 'trace1' : 0
#<> convenience property to configure a test system for local apache development against a remote system, do not set this attribute in recipes or use it in recipes, use the concrete attributes instead
default['blueprint']['livecontext']['magento']['cms_host'] = 'localhost'
# Magento configuration
default['blueprint']['livecontext']['magento']['host'] = 'magentohost'
default['blueprint']['livecontext']['magento']['admin_uri'] = '/admin_ktrri0'
default['blueprint']['livecontext']['magento']['application.properties']['livecontext.magento.host'] = node['blueprint']['livecontext']['magento']['host']
default['blueprint']['livecontext']['magento']['application.properties']['livecontext.cookie.domain'] = ".#{node['fqdn']}"
default['blueprint']['livecontext']['magento']['application.properties']['blueprint.host.helios'] = "preview.#{node['blueprint']['hostname']}"

default['blueprint']['livecontext']['magento']['virtual_host']['delivery']['cluster']['default']['host'] = node['blueprint']['livecontext']['magento']['cms_host']
default['blueprint']['livecontext']['magento']['virtual_host']['delivery']['cluster']['default']['port'] = '42180'
default['blueprint']['livecontext']['magento']['virtual_host']['delivery']['context'] = 'blueprint'
default['blueprint']['livecontext']['magento']['virtual_host']['delivery']['rewrite_log_level'] = rewrite_log_level
default['blueprint']['livecontext']['magento']['virtual_host']['delivery']['sites']['apparel']['server_name'] = "apparel.#{node['blueprint']['hostname']}"
default['blueprint']['livecontext']['magento']['virtual_host']['delivery']['sites']['apparel']['default_site'] = 'apparelhomepage'
#<> The id property of the CMSite content associated with this site
default['blueprint']['livecontext']['magento']['virtual_host']['delivery']['sites']['apparel']['site_id'] = 'Magento-Apparel-UK-Site-ID'

default['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['server_name'] = "shop-preview-apparel.#{node['blueprint']['hostname']}"
default['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['time_travel_alias'] = "shop-preview-apparel.#{node['blueprint']['hostname']}"
default['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['server_aliases'] = []
default['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['rewrite_log_level'] = rewrite_log_level

default['blueprint']['livecontext']['magento']['virtual_host']['shop']['server_name'] = "shop-apparel.#{node['blueprint']['hostname']}"
default['blueprint']['livecontext']['magento']['virtual_host']['shop']['time_travel_alias'] = "shop-apparel.#{node['blueprint']['hostname']}"
default['blueprint']['livecontext']['magento']['virtual_host']['shop']['server_aliases'] = []
default['blueprint']['livecontext']['magento']['virtual_host']['shop']['rewrite_log_level'] = rewrite_log_level

# set this to true to disable SSLProxyVerify, SSLProxyCheckPeerCN, SSLProxyCheckPeerName
default['blueprint']['livecontext']['magento']['ssl_proxy_verify'] = true
