<?php
namespace CoreMedia\DefaultWidget\Block\Widget;

use Magento\Widget\Block\BlockInterface;
use Magento\Framework\View\Element\Template;
use Magento\Cms\Model\Page;
use Magento\Framework\Registry;
use Magento\Backend\Block\Template\Context;
use Magento\Catalog\Model\ProductRepository;
use Magento\Framework\App\Request\Http;

class Content extends Template implements BlockInterface
{

    /**
     * @var Registry
     */
    protected $registry;

    /**
     * @var Page
     */
    protected $page;

    /**
     *
     * @var ProductRepository
     */
    protected $productRepository;

    public function __construct(Context $context, Page $page, Registry $registry, ProductRepository $repository, array $data = [])
    {
        parent::__construct($context, $data);
        $this->registry = $registry;
        $this->page = $page;
        $this->productRepository = $repository;
    }


    public function getShopUrl($jsonstring)
    {
        $json = json_decode($jsonstring);

        // renderType url
        // externalSeoSegment
        // contentId
        // productId
        // categoryId
        // objectType content|product
        // externalUriPath
        $url = "/";
        if ($json->{'objectType'} == "product")
        {
            $product = $this->productRepository->get($json->{'productId'});
            $url = $product->getUrlModel()->getUrl($product);
        }
        else if ($json->{'objectType'} == "category")
        {
            $categoryId = $json->{'categoryId'};
            $objectManager = \Magento\Framework\App\ObjectManager::getInstance();
            $categoryRepository = $objectManager->get('\Magento\Catalog\Model\CategoryRepository');
            $categoryHelper = $objectManager->get('\Magento\Catalog\Helper\Category');
            $category = $categoryRepository->get($categoryId);
            $url = $categoryHelper->getCategoryUrl($category);
        }
        else if($json->{'objectType'} == "content") {
            //TODO SEO URL rewriting
            $url = "cm-page?externalRef=" . $json->{'contentId'} . "&seoSegment=" . $json->{'externalSeoSegment'};
        }
        else if($json->{'objectType'} == "page") {
            //TODO SEO URL rewriting
            $pageUrl = $json->{'externalUriPath'};
            $url = $pageUrl;
        }
        else {
            $url .= $jsonstring;
        }

        //echo "<h6>Link Json is: " . $jsonstring . ", created link " . $url . "</h6>";
        return $url;
    }


    public function _toHtml()
    {
        $view = $this->getData('cm_view');
        $placement = $this->getData('cm_placement');
        $host = $this->_scopeConfig->getValue('backend/general/hostname', \Magento\Store\Model\ScopeInterface::SCOPE_STORE);

        //some dev sugar
        $objectManager = \Magento\Framework\App\ObjectManager::getInstance();
        $cookieManager = $objectManager->get('Magento\Framework\Stdlib\CookieManagerInterface');
        $cookieMetaDataFactory = $objectManager->get('Magento\Framework\Stdlib\Cookie\CookieMetadataFactory');
        $sessionManager = $objectManager->get('Magento\Framework\Session\SessionManagerInterface');

        $developmentHost = $cookieManager->getCookie('cmFragmentHost');
        if(isset($developmentHost)) {
            $host = $developmentHost;
        }

        //CAE Themeporter Supporter
        $duration = 86400;
        $userVariant = $this->getRequest()->getParam('userVariant');
        if(!isset($userVariant)) {
            $userVariant = $cookieManager->getCookie('userVariant');
        }
        $metaData = $cookieMetaDataFactory->createPublicCookieMetadata();
        $metaData->setPath($sessionManager->getCookiePath());
        $metaData->setDomain($sessionManager->getCookieDomain());
        $cookieManager->setPublicCookie('cmUserVariant', $userVariant, $metaData);

        //Prepare fragment request
        $schema = $this->_scopeConfig->getValue('backend/general/schema', \Magento\Store\Model\ScopeInterface::SCOPE_STORE);
        $context = $this->_scopeConfig->getValue('backend/general/urlprefix', \Magento\Store\Model\ScopeInterface::SCOPE_STORE);
        $category = $this->registry->registry('current_category');
        $product = $this->registry->registry('current_product');
        $externalRef = $this->getRequest()->getParam('externalRef');
        $locale = locale_get_default();
        $locale = str_replace("_POSIX", "", $locale);
        $locale = str_replace("_", "-", $locale);
        $pageId = "none";

        $url = $schema . "://" . $host . $context . $locale . "/params;placement=" . $placement;

        if (!is_null($view))
        {
            $url = $url . ";view=" . $view;
        }

        if(is_null($view) || ($view != 'externalHead' && $view != 'externalFooter')) {
            if (!is_null($product))
            {
                $url = $url . ";productId=" . $product->getSku();
            }
            if (isset($externalRef))
            {
                $url = $url . ";externalRef=" . $externalRef;
            }
            if (!is_null($category))
            {
                $url = $url . ";categoryId=" . $category->getId();
            }
        }

        if (!is_null($this->page))
        {
            $pageId = $this->page->getIdentifier();
        }

        $url = $url . ";pageId=" . $pageId;


        $cid = is_null($category) ? null : $category->getId();
        $pid = is_null($product) ? null : $product->getId();

        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_COOKIE, 'cmUserVariant=' . $userVariant);
        $body = curl_exec($ch);

        // Check for errors and display the error message
        if($errno = curl_errno($ch)) {
            $error_message = curl_strerror($errno);
            echo "<h6>cURL error ({$errno}):\n {$error_message}</h6>";
        }
        $http_code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);

        //echo '<h6>Fragment URI is: ' . $url . ' [' . $http_code . '] (Remove this from /var/www/html/example.com/public_html/app/code/CoreMedia/DefaultWidget/Block/Widget/Content.php)<h6>';

        $parts = preg_split("/(.lt;!--CM|CM--.gt;)/", "$body");
        $number = count($parts);

        $idx = 1;
        $converted = "$parts[0]";
        while ($idx < $number)
        {
            $jsonstring = str_replace("&quot;", "\"", $parts[$idx]);
            $link = $this->getShopUrl($jsonstring);
            $converted .= "$link";
            $idx++;
            $converted .= "$parts[$idx]";
            $idx++;
        }

        //return "$url fragment $converted end fragment";
        // return "$url fragment $body end fragment";
        return "$converted";
    }
}

