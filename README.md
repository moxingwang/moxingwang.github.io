# `我的网站`
[https://moxingwang.top](https://moxingwang.top)


---

##### `博客模板` 
* [http://theme-next.simpleyyt.com/getting-started.html](http://theme-next.simpleyyt.com/getting-started.html)
* [https://github.com/Simpleyyt/jekyll-theme-next](https://github.com/Simpleyyt/jekyll-theme-next)

* 去图标,_includes/_partials/head.html line 61

```$xslt
<!--
{% include _partials/head/external-fonts.html %}

{% assign font_awesome_uri = site.vendors._internal | append: '/font-awesome/css/font-awesome.min.css?v=4.6.2' | relative_url %}
{% if site.vendors.fontawesome %}
  {% assign font_awesome_uri = site.vendors.fontawesome %}
{% endif %}
<link href="{{ font_awesome_uri }}" rel="stylesheet" type="text/css" />-->
```
