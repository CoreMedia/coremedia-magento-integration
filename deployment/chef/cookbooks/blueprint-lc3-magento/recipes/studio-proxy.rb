=begin
#<
This recipe installs virtual hosts for the CoreMedia Blueprint Studio.
#>
=end

include_recipe 'coremedia-proxy'

coremedia_proxy_webapp 'magento-studio' do
  server_name node['blueprint']['livecontext']['magento']['virtual_host']['studio']['server_name']
  server_aliases node['blueprint']['livecontext']['magento']['virtual_host']['studio']['server_aliases']
  servlet_context node['blueprint']['livecontext']['magento']['virtual_host']['studio']['context']
  cluster('default' => { 'host' => node['blueprint']['livecontext']['magento']['virtual_host']['studio']['host'], 'port' => node['blueprint']['livecontext']['magento']['virtual_host']['studio']['port'] })
  rewrite_log_level node['blueprint']['livecontext']['magento']['virtual_host']['studio']['rewrite_log_level']
  proxy_template 'proxy/studio.erb'
  magento_host node['blueprint']['livecontext']['magento']['host']
  proxy_template_cookbook 'blueprint-lc3-magento'
  rewrite_template_cookbook 'blueprint-proxy'
  rewrite_template 'rewrite/studio.erb'
end
