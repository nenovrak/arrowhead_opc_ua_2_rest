/*
 * Technology Transfer System S.r.l.
 */
package eu.arrowhead.client.provider;

import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;

/**
 *
 * @author rovere
 */
public class VariableNodeHandler {

    private VariableNode node;

    private String type;

    public VariableNodeHandler(VariableNode node, String type) {
        this.node = node;
        this.type = type;
    }

    public VariableNode getNode() {
        return node;
    }

    public void setNode(VariableNode node) {
        this.node = node;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
