/*
 * Technology Transfer System S.r.l.
 */
package eu.arrowhead.client.provider;

import eu.arrowhead.client.common.model.OPCVariableReadout;
import java.io.File;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import org.eclipse.milo.opcua.sdk.client.OpcUaClient;
import org.eclipse.milo.opcua.sdk.client.api.config.OpcUaClientConfig;
import org.eclipse.milo.opcua.sdk.client.api.identity.AnonymousProvider;
import org.eclipse.milo.opcua.sdk.client.api.nodes.VariableNode;
import org.eclipse.milo.opcua.stack.client.UaTcpStackClient;
import org.eclipse.milo.opcua.stack.core.Stack;
import org.eclipse.milo.opcua.stack.core.security.SecurityPolicy;
import org.eclipse.milo.opcua.stack.core.types.builtin.DataValue;
import org.eclipse.milo.opcua.stack.core.types.builtin.LocalizedText;
import org.eclipse.milo.opcua.stack.core.types.builtin.NodeId;
import static org.eclipse.milo.opcua.stack.core.types.builtin.unsigned.Unsigned.uint;
import org.eclipse.milo.opcua.stack.core.types.structured.EndpointDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author rovere
 */
public class OPCUAReader {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private OpcUaClient client;

    private static OPCUAReader instance;

    public static OPCUAReader get() {
        if (instance == null) {
            instance = new OPCUAReader();
        }
        return instance;
    }

    public OPCVariableReadout read(String address, int ns, String varName) throws Exception {
        if (client == null) {
            client = createClientAndConnect(address);
        }

        return readNode(new NodeId(ns, varName));
    }

    public void dispose() {
        if (client != null) {
            try {
                client.disconnect().get();
                Stack.releaseSharedResources();
            } catch (InterruptedException | ExecutionException e) {
                logger.error("Error disconnecting:", e.getMessage(), e);
            }
        }
        client = null;

    }

    private OPCVariableReadout readNode(NodeId nodeId) throws InterruptedException, ExecutionException {

        // synchronous read request via VariableNode
        VariableNode node = client.getAddressSpace().createVariableNode(nodeId);
        DataValue value = node.readValue().get();
        OPCVariableReadout vr = new OPCVariableReadout();
        if (value.getValue().getDataType().isPresent()) {
            vr.setType(value.getValue().getDataType().get().toParseableString());
        } else {
            vr.setType("UNDEFINED");
        }
        Object ov = value.getValue().getValue();
        vr.setValue(ov != null ? ov.toString() : "null");
        return vr;
    }

    private  OpcUaClient createClientAndConnect(String address) throws Exception {
        File securityTempDir = new File(System.getProperty("java.io.tmpdir"), "security");
        if (!securityTempDir.exists() && !securityTempDir.mkdirs()) {
            throw new Exception("unable to create security dir: " + securityTempDir);
        }
        LoggerFactory.getLogger(getClass())
                .info("security temp dir: {}", securityTempDir.getAbsolutePath());

        SecurityPolicy securityPolicy = SecurityPolicy.None;

        EndpointDescription[] endpoints;

        String url = "opc.tcp://" + address;
        try {
            endpoints = UaTcpStackClient
                    .getEndpoints(url)
                    .get();
        } catch (Throwable ex) {
            // try the explicit discovery endpoint as well
            String discoveryUrl = url + "/discovery";
            logger.info("Trying explicit discovery URL: {}", discoveryUrl);
            endpoints = UaTcpStackClient
                    .getEndpoints(discoveryUrl)
                    .get();
        }

        EndpointDescription endpoint = Arrays.stream(endpoints)
                .filter(e -> e.getSecurityPolicyUri().equals(securityPolicy.getSecurityPolicyUri()))
                .findFirst().orElseThrow(() -> new Exception("no desired endpoints returned"));

        logger.info("Using endpoint: {} [{}]", endpoint.getEndpointUrl(), securityPolicy);

        OpcUaClientConfig config = OpcUaClientConfig.builder()
                .setApplicationName(LocalizedText.english("eclipse milo opc-ua client"))
                .setApplicationUri("urn:eclipse:milo:examples:client")
                //            .setCertificate(loader.getClientCertificate())
                //            .setKeyPair(loader.getClientKeyPair())
                .setEndpoint(endpoint)
                .setIdentityProvider(new AnonymousProvider())
                .setRequestTimeout(uint(5000))
                .build();
        OpcUaClient c = new OpcUaClient(config);
        c.connect().get();
        return c;
    }

}
