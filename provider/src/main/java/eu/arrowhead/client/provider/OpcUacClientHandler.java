/*
 * Technology Transfer System S.r.l.
 */
package eu.arrowhead.client.provider;

import eu.arrowhead.client.common.model.OPCVariableReadout;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;

/**
 *
 * @author rovere
 */
public class OpcUacClientHandler {

    private OpcUaClient client;

    private Map<String, VariableNode> nodes = new HashMap();

    public OpcUacClientHandler(OpcUaClient client) {
        this.client = client;
    }
    
    OPCVariableReadout readNode(NodeId nodeId) throws InterruptedException, ExecutionException {
        String id = nodeId.toParseableString();
        VariableNode node = nodes.get(id);
        if(node == null){
            node = client.getAddressSpace().createVariableNode(nodeId);
            nodes.put(id, node);
        }
        // synchronous read request via VariableNode
        OPCVariableReadout vr = readNode(node);
        return vr;
    } 
    
    private OPCVariableReadout readNode(VariableNode node) throws InterruptedException, ExecutionException{
        
        DataValue value = node.readValue().get();
        OPCVariableReadout vr = new OPCVariableReadout();
        vr.setType("null");
        vr.setValue("null");
        if (value.getValue().getDataType().isPresent()) {
            vr.setType(value.getValue().getDataType().get().toParseableString());
        }
        if (value.getValue().isNotNull()) {
            vr.setValue(value.getValue().getValue().toString());
        }
        // add timestamps
        vr.setSourceTimestamp(value.getSourceTime().getUtcTime());
        vr.setServerTimestamp(value.getServerTime().getUtcTime());
        vr.setStatusCode(value.getStatusCode().getValue());
        return vr;
    }
    
    void disconnect(){
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
