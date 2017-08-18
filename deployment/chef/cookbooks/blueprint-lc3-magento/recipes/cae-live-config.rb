=begin
#<
This recipe configures the CoreMedia Blueprint Live CAE.
#>
=end

node.default['blueprint']['webapps']['cae-live']['application.properties']['livecontext.magento.host'] = node['blueprint']['livecontext']['magento']['host']
node.default['blueprint']['webapps']['cae-live']['application.properties']['livecontext.magento.basePath'] = node['blueprint']['livecontext']['magento']['basePath']
node.default['blueprint']['webapps']['cae-live']['application.properties']['livecontext.magento.user'] = node['blueprint']['livecontext']['magento']['user']
node.default['blueprint']['webapps']['cae-live']['application.properties']['livecontext.magento.password'] = node['blueprint']['livecontext']['magento']['password']
node.default['blueprint']['webapps']['cae-live']['application.properties']['livecontext.apache.magento.host'] = "shop-apparel.#{node['blueprint']['hostname']}"

# inject wcs configuration
node['blueprint']['livecontext']['magento']['application.properties'].each_pair do |k, v|
  node.default['blueprint']['webapps']['cae-live']['application.properties'][k] = v
end
