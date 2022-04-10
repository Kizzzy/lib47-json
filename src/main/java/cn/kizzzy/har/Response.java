package cn.kizzzy.har;

public class Response {
    public int status;
    public String statusText;
    public String httpVersion;
    public Cookie[] cookies;
    public Header[] headers;
    public Content content;
    public String redirectURL;
    public int headersSize;
    public int bodySize;
    public String comment;
}
