=begin
#<
This recipe configures the CoreMedia Blueprint Preview CAE.
#>
=end
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.magento.host'] = node['blueprint']['livecontext']['magento']['host']
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.magento.basePath'] = node['blueprint']['livecontext']['magento']['basePath']
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.magento.user'] = node['blueprint']['livecontext']['magento']['user']
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.magento.password'] = node['blueprint']['livecontext']['magento']['password']
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.apache.magento.host'] = "shop-preview-apparel.#{node['blueprint']['hostname']}"
node.default['blueprint']['webapps']['cae-preview']['application.properties']['livecontext.magento.storeFrontUrl'] = "https://shop-preview-apparel.#{node['blueprint']['hostname']}/yacceleratorstorefront/"

# inject wcs configuration
node['blueprint']['livecontext']['magento']['application.properties'].each_pair do |k, v|
  node.default['blueprint']['webapps']['cae-preview']['application.properties'][k] = v
end
