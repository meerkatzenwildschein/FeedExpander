# FeedExpander
Feed expander is a service which provides a generic way to create rss or atom feeds containing the part of the linked page.
In other words, you can convert a cropped feed into a full feed.  

## Install
 * Take the expander.jar and the config.yml and copy them both together where you want to run the feed expander.
 * Edit the config.yml and create a htuser file if you like.
 * Start the service with java -jar expander.jar server config.yml

## Usage
  After starting the FeedExpander service, you can connect to it as configured with the config.yml. The
  default configuration uses http://localhost:8080/expand as base url. You have to add the url parameters `feedUrl` 
  containing the url which points to the url of the feed to be expanded. You also need the `includes` url parameter
  which describes the part of the page, linked in the feed entries / articles.
  
  The `includes` url paremeter expression, which selects a part of a html page describes a navigation path through the
  html tags. Each path element is separated by a slash and starts with the kind of element followed by a equality sign 
  followed by the search value.
  
  * `id=someId` selects the tag with the id `someId` somewhere in the page.
  * `tag=div 3` selects the third div came up from the previous element or the body element if no previous one was defined.
  * `tag=*article` selects the first article tag somewhere in the page.

## Examples
  Please note that the parameter values in the must be url encoded. You can use [url-encode-decode.com](www.url-encode-decode.com) for example. The following examples are randomly picked and only examples. There exists NO agreement with page
  proprietor which allows to expand their feeds for commercial or private reasons. 

  * golem.de feed at rss.golem.de/rss.php?feed=ATOM1.0 can be expanded by including the screen element, than the third div and the article element below `id=screen/tag=div 3/tag=article`.
    `http://localhost:8080/expand?feedUrl=http%3A%2F%2Frss.golem.de%2Frss.php%3Ffeed%3DATOM1.0&include=id%3Dscreen%2Ftag%3Ddiv+3%2Ftag%3Darticle`
  It is also possible to select the 'article' element directly with simply `tag=*article` which selects all articles where the first one is automatically used. If there're more than
  one, the right one can be selected with a separated number. For example with `tag=*article 1`.
    `http://localhost:8080/expand?feedUrl=http%3A%2F%2Frss.golem.de%2Frss.php%3Ffeed%3DATOM1.0&include=tag%3D%2Aarticle`
    
  * heise.de feed at heise.de.feedsportal.com/c/35207/f/653902/index.rss can be expanded by including article tag under the element with the id 'mitte_news' `id=mitte_news/tag=article`.
    `http://localhost:8080/expand?feedUrl=http%3A%2F%2Fheise.de.feedsportal.com%2Fc%2F35207%2Ff%2F653902%2Findex.rss&include=id%3Dmitte_news%2Ftag%3Darticle`
  The golem.de example will also works here.
    `http://localhost:8080/expand?feedUrl=http%3A%2F%2Fheise.de.feedsportal.com%2Fc%2F35207%2Ff%2F653902%2Findex.rss&include=tag%3D%2Aarticle`
    
  * fluter.de feed at fluter.de/de/?tpl=907 can be expanded by stating with the element with the id 'page-wrapper' and than taking the second div and than the first one. 
  Because the first div contains only the headline, the second and third div is also selected by repeating the expression before separated with a '|' pipe. 
    `http://localhost:8080/expand?feedUrl=http%3A%2F%2Fwww.fluter.de%2Fde%2F%3Ftpl%3D907&include=id=page-wrapper;tag=div%202;tag=div%201|id=page-wrapper;tag=div%202;tag=div%202|id=page-wrapper;tag=div%202;tag=div%203`
    