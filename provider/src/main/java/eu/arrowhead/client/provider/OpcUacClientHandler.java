/*
 * Technology Transfer System S.r.l.
 */
package eu.arrowhead.client.provider;

import eu.arrowhead.client.common.model.OPCVariableReadout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.nodes.Node;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

/**
 *
 * @author rovere
 */
public class OpcUacClientHandler {

    private OpcUaClient client;

    private Map<String, VariableNodeHandler> nodes = new HashMap();

    public OpcUacClientHandler(OpcUaClient client) {
        this.client = client;
    }

    OPCVariableReadout readNode(NodeId nodeId) throws InterruptedException, ExecutionException {
        String id = nodeId.toParseableString();
        VariableNodeHandler node = nodes.get(id);
        if (node == null) {
            node = createVariableHandler(nodeId);
            nodes.put(id, node);
        }
        // synchronous read request via VariableNode
        OPCVariableReadout vr = readNode(node);
        return vr;
    }

    private VariableNodeHandler createVariableHandler(NodeId nodeId) throws InterruptedException, ExecutionException {
        VariableNode node = client.getAddressSpace().createVariableNode(nodeId);
        NodeId dataTypeId = node.getDataType().get();
        Node typeNode = client.getAddressSpace().createDataTypeNode(dataTypeId);
        String type = "undefined";
        type = typeNode.getBrowseName().get().getName();
        return new VariableNodeHandler(node, type);
    }

    private OPCVariableReadout readNode(VariableNodeHandler node) throws InterruptedException, ExecutionException {

        DataValue value = node.getNode().readValue().get();
        OPCVariableReadout vr = new OPCVariableReadout();
        vr.setType(node.getType());
        vr.setValue("null");
        if (value.getValue().isNotNull()) {
            vr.setValue(value.getValue().getValue().toString());
        }
        // add timestamps
        vr.setSourceTimestamp(value.getSourceTime().getUtcTime());
        vr.setServerTimestamp(value.getServerTime().getUtcTime());
        vr.setStatusCode(value.getStatusCode().getValue());
        return vr;
    }

    void disconnect() {
        try {
            client.disconnect().get();
            client = null;
        } catch (InterruptedException ex) {
            Logger.getLogger(OpcUacClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ExecutionException ex) {
            Logger.getLogger(OpcUacClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
