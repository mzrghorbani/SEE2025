package interactions;

import skf.coder.HLAunicodeStringCoder;
import skf.model.interaction.annotations.InteractionClass;
import skf.model.interaction.annotations.Parameter;

@InteractionClass(name = "FederateMessage")
public class FederateMessage {

    @Parameter(name = "Sender", coder = HLAunicodeStringCoder.class)
    private String sender;

    @Parameter(name = "Receiver", coder = HLAunicodeStringCoder.class)
    private String receiver;

    @Parameter(name = "MessageType", coder = HLAunicodeStringCoder.class)
    private String messageType;

    @Parameter(name = "Content", coder = HLAunicodeStringCoder.class)
    private String content;

    public FederateMessage() { }

    public FederateMessage(String sender, String receiver, String messageType, String content) {
        this.sender = sender;
        this.receiver = receiver;
        this.messageType = messageType;
        this.content = content;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
