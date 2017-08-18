=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint LiveContext Magento Live Shop.
#>
=end
include_recipe 'coremedia-proxy'

coremedia_proxy_webapp 'magento-commerce-shop' do
  server_name node['blueprint']['livecontext']['magento']['virtual_host']['shop']['server_name']
  server_aliases node['blueprint']['livecontext']['magento']['virtual_host']['shop']['server_aliases']
  time_travel_alias node['blueprint']['livecontext']['magento']['virtual_host']['shop']['time_travel_alias']
  magento_host node['blueprint']['livecontext']['magento']['host']
  rewrite_log_level node['blueprint']['livecontext']['magento']['virtual_host']['shop']['rewrite_log_level']
  rewrite_template 'rewrite/shop.erb'
  proxy_template 'proxy/shop.erb'
  proxy_template_cookbook 'blueprint-lc3-magento'
  site_server_name node['blueprint']['livecontext']['magento']['virtual_host']['delivery']['sites']['apparel']['server_name']
  headers node['blueprint']['livecontext']['magento']['virtual_host']['shop']['headers']
  ssl_proxy_verify node['blueprint']['livecontext']['magento']['ssl_proxy_verify']
end
