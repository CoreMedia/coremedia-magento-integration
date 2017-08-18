include_recipe 'coremedia-proxy::default'
include_recipe 'blueprint-base::default'

coremedia_proxy_webapp 'candy-magento-commerce-shop-preview' do
  server_name "candy-#{node['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['server_name']}"
  time_travel_alias "candy-#{node['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['time_travel_alias']}"
  magento_host node['blueprint']['livecontext']['magento']['host']
  rewrite_log_level node['blueprint']['livecontext']['magento']['virtual_host']['shop-preview']['rewrite_log_level']
  rewrite_template 'rewrite/shop.erb'
  proxy_template 'proxy/shop.erb'
  proxy_template_cookbook 'blueprint-lc3-magento'
  site_server_name node['blueprint']['proxy']['virtual_host']['preview']['server_name']
  preview true
  headers [%q(
  SetEnvIf Remote_Addr "(.*)" devaddr=$1
  RequestHeader set X-FragmentHostDevelopment http://%{devaddr}e:40980/blueprint/servlet/service/fragment/
)]
  ssl_proxy_verify false
end

%w(preview studio-preview).each do |candy_setup|
  node.force_default['blueprint']['proxy']['candy_properties'][candy_setup]['blueprint.site.mapping.apparel'] = "https://candy-preview.#{node['blueprint']['hostname']}"
  node.force_default['blueprint']['proxy']['candy_properties'][candy_setup]['livecontext.apache.magento.host'] = "candy-shop-preview-apparel.#{node['blueprint']['hostname']}"
  node.force_default['blueprint']['proxy']['candy_properties'][candy_setup]['livecontext.magento.storeFrontUrl'] = "https://candy-shop-preview-apparel.#{node['blueprint']['hostname']}/yacceleratorstorefront/"
end
