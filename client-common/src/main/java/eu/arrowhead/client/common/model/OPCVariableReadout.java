/*
 * Technology Transfer System S.r.l.
 */
package eu.arrowhead.client.common.model;

/**
 *
 * @author rovere
 */
public class OPCVariableReadout {

    private String type;

    private String value;

    private long sourceTimestamp;

    private long serverTimestamp;

    private long statusCode;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getSourceTimestamp() {
        return sourceTimestamp;
    }

    public void setSourceTimestamp(long sourceTimestamp) {
        this.sourceTimestamp = sourceTimestamp;
    }

    public long getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(long serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }

    public long getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(long statusCode) {
        this.statusCode = statusCode;
    }

}
