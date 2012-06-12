import java.io.IOException;
import java.math.BigInteger;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class PusherJavaClient {

	//Adjust this stuff for your app
	private final static String APP_ID = "YOUR_APP_ID";
	private final static String API_KEY = "YOUR_API_KEY";
	private final static String API_SECRET = "YOUR_API_SECRET";
	
	private final static int connectionTimeout = 5000;
    	private final static int socketTimeout = 5000;
    	
    	private final static String HOST = "api.pusherapp.com";

	private static String md5Hash(String data) {
		return DigestUtils.md5Hex(data);
	}
	
	private static String hmacsha256(String data) {
		try {
			final SecretKeySpec signingKey = new SecretKeySpec(API_SECRET.getBytes(), "HmacSHA256");
			final Mac mac = Mac.getInstance("HmacSHA256");
			mac.init(signingKey);

			byte[] digest = mac.doFinal(data.getBytes("UTF-8"));
			digest = mac.doFinal(data.getBytes());

			BigInteger bigInteger = new BigInteger(1, digest);
			return String.format("%0" + (digest.length << 1) + "x", bigInteger);
		} catch (Exception e) {
			throw new RuntimeException("Some bullshit happened. Are you using a newish JVM?", e);
		}
	}
	
	private static String buildURIPath(String channelName) {
		String path = "/apps/" + APP_ID + "/channels/" + channelName + "/events";
		return path;
	}

	private static String buildUrl(String channelName) {
		String url = "http://" + HOST + buildURIPath(channelName);
		return url;
	}

	private static String buildAuthenticationSignature(String uriPath, String query) {
		String signature = "POST\n" + uriPath + "\n" + query;
		return hmacsha256(signature);
	}
	
	public static int triggerPush(String channel, String event, String jsonData) throws IOException {
		StringBuffer query = new StringBuffer();
		query.append("auth_key=" + API_KEY);
	    	query.append("&auth_timestamp=" + String.valueOf(System.currentTimeMillis() / 1000));
	    	query.append("&auth_version=1.0");
	    	query.append("&body_md5=" + md5Hash(jsonData));
	    	query.append("&name=" + event);
	    	
	    	String signature = buildAuthenticationSignature(buildURIPath(channel), query.toString());
	    	query.append("&auth_signature=" + signature);
	    	
	    	String fullUrl = buildUrl(channel) + "?" + query;
		
		HttpParams httpParams = new BasicHttpParams();
		
	    	HttpConnectionParams.setConnectionTimeout(httpParams, connectionTimeout);
	    	HttpConnectionParams.setConnectionTimeout(httpParams, socketTimeout);
	    	
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
		HttpPost httpPost = new HttpPost(fullUrl);
		
 		httpPost.addHeader("Content-Type", "application/json");
		httpPost.setEntity(new StringEntity(jsonData));
		
		HttpResponse httpResponse = httpClient.execute(httpPost);
		
		int statusCode = httpResponse.getStatusLine().getStatusCode();
		
		httpClient.getConnectionManager().shutdown();
		
		return statusCode; // should be 202 ACCEPTED
	}
}