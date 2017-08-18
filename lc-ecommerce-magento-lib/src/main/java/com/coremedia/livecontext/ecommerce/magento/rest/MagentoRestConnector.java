package com.coremedia.livecontext.ecommerce.magento.rest;

import com.google.common.base.Stopwatch;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * REST-Connector for Magento REST API.
 */
@Service
public class MagentoRestConnector {
  private static final Logger LOG = LoggerFactory.getLogger(MagentoRestConnector.class);

  private static final String AUTHORIZATION_HEADER = "Authorization";

  private static final String DEFAULT_API_VERSION = "V1";
  private static final String DEFAULT_PROTOCOL = "http";
  private static final String DEFAULT_BASE_PATH = "/rest";

  private String apiVersion = DEFAULT_API_VERSION;

  private String host = "MISSING_CONFIGURATION_FOR_MAGENTO_HOST";

  private String protocol = DEFAULT_PROTOCOL;

  private String basePath = DEFAULT_BASE_PATH;

  private String user = "MISSING_CONFIGURATION_FOR_MAGENTO_API_USER";

  private String password = "MISSING_CONFIGURATION_FOR_MAGENTO_API_USERS_PASSWORD";

  private String authToken;

  private RestTemplate restTemplate = new RestTemplate();


  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public void setBasePath(String basePath) {
    this.basePath = basePath;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public <T> T performGet(String resourcePath, Class<T> responseType) {
    return performGet(resourcePath, responseType, null, null);
  }


  public <T> T performGet(String resourcePath, Class<T> responseType, String storeCode) {
    return performGet(resourcePath, responseType, null, storeCode);
  }


  public <T> T performGet(String resourcePath, Class<T> responseType, MultiValueMap<String, String> queryParams) {
    return performGet(resourcePath, responseType, queryParams, null);
  }


  public <T> T performGet(String resourcePath, Class<T> responseType, MultiValueMap<String, String> queryParams, String storeCode) {
    // Need to authorize first?
    if (StringUtils.isBlank(authToken)) {
      authToken = fetchAuthToken();
    }

    UriComponentsBuilder uriComponentsBuilder = UriComponentsBuilder.newInstance()
            .scheme(protocol)
            .host(host)
            .path(basePath);

    if (StringUtils.isNotBlank(storeCode)) {
      uriComponentsBuilder.path("/" + storeCode);
    }

    uriComponentsBuilder.path("/" + apiVersion);

    if (queryParams != null) {
      uriComponentsBuilder.queryParams(queryParams);
    }

    // Add resource path
    uriComponentsBuilder.path(resourcePath);

    UriComponents uriComponents = uriComponentsBuilder.build();//.encode();
    String url = uriComponents.toString();

    Stopwatch stopwatch = null;
    if (LOG.isInfoEnabled()) {
      stopwatch = Stopwatch.createStarted();
    }

    HttpHeaders headers = new HttpHeaders();
    if (StringUtils.isNotBlank(authToken)) {
      headers.set(AUTHORIZATION_HEADER, "Bearer " + authToken);
    }

    HttpEntity<String> requestEntity = new HttpEntity<>(headers);

    LOG.info("performGet() calling {}", url);

    ResponseEntity<T> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, responseType);

    LOG.info("performGet() called {}", url);

    if (LOG.isInfoEnabled() && stopwatch != null && stopwatch.isRunning()) {
      stopwatch.stop();
      LOG.info("performGet() GET Request '{}' returned with HTTP status code: {} (took {})", url, responseEntity.getStatusCode(), stopwatch);
    }

    return responseEntity.getBody();
  }


  /**
   * Fetch the authentication token for magento's head token based authentication approach.
   * May return null on failure but most likely will throw a runtime exception if problems occur.
   *
   * @return token on success or null
   */
  public String fetchAuthToken() {
    UriComponents uriComponents = UriComponentsBuilder.newInstance()
            .scheme(protocol)
            .host(host)
            .path(basePath)
            .path("/" + apiVersion)
            .path("/integration/admin/token")
            .build()
            .encode();

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<String> requestEntity = new HttpEntity<>("{\"username\": \"" + user + "\", \"password\": \"" + password + "\"}", headers);

    String url = uriComponents.toString();

    LOG.info("fetchAuthToken() fetching token via {}.", url);
    ResponseEntity<String> response = restTemplate.postForEntity(url, requestEntity, String.class);

    if (response.getStatusCode().equals(HttpStatus.OK)) {
      String result = response.getBody();
      LOG.info("fetchAuthToken() done.");
      return result.replaceAll("\"", "");
    }
    else {
      LOG.warn("fetchAuthToken() Unable to request authentication token. Check credentials!");
    }

    return null;
  }



}
