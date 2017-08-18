=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Magento Preview Shop.
#>
=end
include_recipe 'coremedia-proxy'

coremedia_proxy_webapp 'magento-commerce-shop-preview' do
  server_name node['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['server_name']
  server_aliases node['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['server_aliases']
  time_travel_alias node['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['time_travel_alias']
  magento_host node['blueprint']['livecontext']['magento']['host']
  rewrite_log_level node['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['rewrite_log_level']
  rewrite_template 'rewrite/shop.erb'
  proxy_template 'proxy/shop.erb'
  proxy_template_cookbook 'blueprint-lc3-magento'
  site_server_name node['blueprint']['proxy']['virtual_host']['preview']['server_name']
  preview true
  headers node['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['headers']
end
