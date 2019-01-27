package cn.wen.office.dto;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlCData;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName = "xml")
public class MessageAutoResponseDTO {

    private String ToUserName;
    private String FromUserName;
    private Long CreateTime;
    private String MsgType;
    private String Content;

    @JacksonXmlProperty(localName = "ToUserName")
    @JacksonXmlCData
    public String getToUserName() {
        return ToUserName;
    }

    public void setToUserName(String toUserName) {
        ToUserName = toUserName;
    }

    @JacksonXmlProperty(localName = "FromUserName")
    @JacksonXmlCData
    public String getFromUserName() {
        return FromUserName;
    }

    public void setFromUserName(String fromUserName) {
        FromUserName = fromUserName;
    }

    @JacksonXmlProperty(localName = "CreateTime")
    public Long getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(Long createTime) {
        CreateTime = createTime;
    }

    @JacksonXmlProperty(localName = "MsgType")
    @JacksonXmlCData
    public String getMsgType() {
        return MsgType;
    }

    public void setMsgType(String msgType) {
        MsgType = msgType;
    }

    @JacksonXmlProperty(localName = "Content")
    @JacksonXmlCData
    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}