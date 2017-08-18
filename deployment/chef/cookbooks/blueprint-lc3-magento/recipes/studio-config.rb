=begin
#<
This recipe configures the CoreMedia Blueprint Studio.
#>
=end
node.default['blueprint']['webapps']['studio']['application.properties']['studio.previewUrlWhitelist'] = "*.#{node['blueprint']['hostname']}, #{node['blueprint']['livecontext']['magento']['host']}, #{node['blueprint']['livecontext']['magento']['host']}:80"
node.default['blueprint']['webapps']['studio']['application.properties']['livecontext.magento.host'] = node['blueprint']['livecontext']['magento']['host']
node.default['blueprint']['webapps']['studio']['application.properties']['livecontext.magento.basePath'] = node['blueprint']['livecontext']['magento']['basePath']
node.default['blueprint']['webapps']['studio']['application.properties']['livecontext.magento.user'] = node['blueprint']['livecontext']['magento']['user']
node.default['blueprint']['webapps']['studio']['application.properties']['livecontext.magento.password'] = node['blueprint']['livecontext']['magento']['password']

# inject magento configuration
node['blueprint']['livecontext']['magento']['application.properties'].each_pair do |k, v|
  node.default['blueprint']['webapps']['studio']['application.properties'][k] = v
end
