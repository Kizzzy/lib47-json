package cn.kizzzy.har;

public class Request {
    public String method;
    public String url;
    public String httpVersion;
    public Cookie[] cookies;
    public Header[] headers;
    public QueryString[] queryString;
    public PostData postData;
    public int headersSize;
    public int bodySize;
    public String comment;
}
