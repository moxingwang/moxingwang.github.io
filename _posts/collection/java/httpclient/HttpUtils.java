package com.chinaredstar.ordercenter.util;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultServiceUnavailableRetryStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class HttpUtils {

	// public static CookieStore cookieStore = null;
	// public static HttpClientContext context = null;

	public static String get(String url) {
		CloseableHttpClient httpClient = HttpUtils.getCloseableHttpClient();

		HttpGet httpGet = null;
		httpGet = new HttpGet(url);
		httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8");

		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity = httpResponse.getEntity();
		if (entity == null) {
			return null;
		}
		try {
			return EntityUtils.toString(entity);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String get(String url, Map<String, String> headers) {
		CloseableHttpClient httpClient = HttpUtils.getCloseableHttpClient();

		HttpGet httpGet = null;
		httpGet = new HttpGet(url);
		httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8");

		if (headers != null) {
			for (Entry<String, String> entry : headers.entrySet()) {
				httpGet.setHeader(entry.getKey(), entry.getValue());
			}
		}

		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity = httpResponse.getEntity();
		if (entity == null) {
			return null;
		}
		try {
			String result = EntityUtils.toString(entity);
			return result;
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String get(URI uri) {
		CloseableHttpClient httpClient = HttpUtils.getCloseableHttpClient();

		HttpGet httpGet = new HttpGet(uri);

		httpGet.setHeader(HttpHeaders.CONTENT_TYPE, "text/html; charset=utf-8");

		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpGet);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity = httpResponse.getEntity();
		if (entity == null) {
			return null;
		}
		try {
			return EntityUtils.toString(entity);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String post(String url, String text, Charset charset) {
		CloseableHttpClient httpClient = HttpUtils.getCloseableHttpClient();

		HttpPost httpPost = new HttpPost(url);

		StringEntity stringEntity = new StringEntity(text, ContentType.TEXT_HTML.withCharset(charset));
		httpPost.setEntity(stringEntity);

		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpEntity httpEntity = httpResponse.getEntity();

		try {
			return EntityUtils.toString(httpEntity);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String post(String url, String text, Charset charset, Map<String, String> headers) {
		CloseableHttpClient httpClient = HttpUtils.getCloseableHttpClient();

		HttpPost httpPost = new HttpPost(url);

		if (headers != null) {
			for (Entry<String, String> entryHeader : headers.entrySet()) {
				httpPost.addHeader(entryHeader.getKey(), entryHeader.getValue());
			}
		}

		StringEntity stringEntity = new StringEntity(text, ContentType.TEXT_HTML.withCharset(charset));
		httpPost.setEntity(stringEntity);

		HttpResponse httpResponse = null;
		try {
			httpResponse = httpClient.execute(httpPost);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HttpEntity httpEntity = httpResponse.getEntity();

		try {
			return EntityUtils.toString(httpEntity);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String post(String url, String text) {
		return post(url, text, Charset.defaultCharset());
	}

	public static String post(String url, String text, String charsetStr) {
		Charset charset = StringUtils.isBlank(charsetStr) ? Charset.defaultCharset() : Charset.forName(charsetStr);
		return post(url, text, charset);
	}

	public static String post(String url, Map<String, String> params, HttpClientContext httpClientContext, Charset charset) {
		CloseableHttpClient httpClient = HttpUtils.getCloseableHttpClient();

		HttpPost httpPost = new HttpPost(url);

		List<NameValuePair> nvps = new ArrayList<NameValuePair>();

		for (Entry<String, String> entry : params.entrySet()) {
			if (StringUtils.isNotBlank(entry.getValue())) {
				nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
		}
		httpPost.setEntity(new UrlEncodedFormEntity(nvps, charset));

		HttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();
		if (entity == null) {
			return null;
		}
		try {
			return EntityUtils.toString(entity, charset);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static HttpClientContext getHttpClientContext() {
		HttpClientContext context = HttpClientContext.create();
		Registry<CookieSpecProvider> registry = RegistryBuilder.<CookieSpecProvider>create().register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
				.register(CookieSpecs.BROWSER_COMPATIBILITY, new BrowserCompatSpecFactory()).build();
		context.setCookieSpecRegistry(registry);
		context.setCookieStore(new BasicCookieStore());
		return context;
	}

	public static String post(String url, Map<String, String> params, HttpClientContext context) {
		Charset charset = Charset.defaultCharset();
		return post(url, params, context, charset);
	}

	public static String post(String url, Map<String, String> params) {
		Charset charset = Charset.defaultCharset();
		return post(url, params, null, charset);
	}

	public static String generateQueryString(Map<String, String> params) {
		Set<String> segmentSet = new HashSet<String>();
		for (Entry<String, String> entry : params.entrySet()) {
			String segment = entry.getKey() + "=" + entry.getValue();
			segmentSet.add(segment);
		}
		return StringUtils.join(segmentSet.iterator(), "&");
	}

	public static String post(String url, Map<String, String> params, String charsetStr) {
		Charset charset = StringUtils.isBlank(charsetStr) ? Charset.defaultCharset() : Charset.forName(charsetStr);
		return post(url, params, null, charset);
	}

	public static String postJson(String url, String jsonStr) {
		CloseableHttpClient httpClient = HttpUtils.getCloseableHttpClient();

		HttpPost httpPost = new HttpPost(url);

		StringEntity stringEntiry = new StringEntity(jsonStr, ContentType.APPLICATION_JSON);
		httpPost.setEntity(stringEntiry);

		HttpResponse response = null;
		try {
			response = httpClient.execute(httpPost);
		} catch (IOException e) {
			e.printStackTrace();
		}
		HttpEntity entity = response.getEntity();
		if (entity == null) {
			return null;
		}
		try {
			return EntityUtils.toString(entity);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static CloseableHttpClient getCloseableHttpClient() {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

		httpClientBuilder.setServiceUnavailableRetryStrategy(new DefaultServiceUnavailableRetryStrategy(5, 1000));
		httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(5, true));

		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(-1).setConnectionRequestTimeout(-1).setSocketTimeout(-1).setCookieSpec(CookieSpecs.STANDARD).build();
		httpClientBuilder.setDefaultRequestConfig(requestConfig);

		CloseableHttpClient httpClient = httpClientBuilder.setConnectionManager(HttpUtils.getHttpConnectionManager()).build();
		return httpClient;
	}

	/**
	 * 获取安全套接字链接工厂
	 * 
	 * @return
	 */
	public static SSLConnectionSocketFactory getSSLConnectionSocketFactory() {

		SSLContextBuilder sslContextBuilder = new SSLContextBuilder();

		SSLContext sslcontext = null;
		try {
			sslcontext = sslContextBuilder.build();
		} catch (KeyManagementException | NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		}

		X509TrustManager tm = new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] x509Certificates, String s) throws java.security.cert.CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return new java.security.cert.X509Certificate[0];
			}
		};
		try {
			sslcontext.init(null, new TrustManager[] { tm }, null);
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		SSLConnectionSocketFactory sslcsf = new SSLConnectionSocketFactory(sslcontext, SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
		return sslcsf;
	}

	public static HttpClientConnectionManager getHttpConnectionManager() {
		RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create();
		registryBuilder.register("http", PlainConnectionSocketFactory.INSTANCE);
		registryBuilder.register("https", HttpUtils.getSSLConnectionSocketFactory());

		Registry<ConnectionSocketFactory> socketFactoryRegistry = registryBuilder.build();
		PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
		return connectionManager;
	}

}
