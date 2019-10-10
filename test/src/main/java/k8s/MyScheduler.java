package k8s;

import java.io.InputStream;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.CoreV1Api;
import io.kubernetes.client.models.V1Binding;
import io.kubernetes.client.models.V1Node;
import io.kubernetes.client.models.V1NodeList;
import io.kubernetes.client.models.V1ObjectMeta;
import io.kubernetes.client.models.V1ObjectReference;
import io.kubernetes.client.models.V1Pod;
import io.kubernetes.client.models.V1PodList;
import io.kubernetes.client.util.Config;

public class MyScheduler {

	public static void main(String[] args) throws Exception {

		String fileName = "classpath:/k8s/config";
		InputStream inputStream = ResourceRenderer.resourceLoader(fileName);
		ApiClient client = Config.fromConfig(inputStream);

		// 五分钟
		client.setConnectTimeout(5 * 60 * 1000);
		Configuration.setDefaultApiClient(client);
		CoreV1Api api = new CoreV1Api();
		V1PodList list = api.listPodForAllNamespaces(null, null, null, null, null, null, null, null, null);
		for (V1Pod item : list.getItems()) {
			if ("random-scheduler".equals(item.getSpec().getSchedulerName())
					&& StringUtils.isBlank(item.getSpec().getNodeName())) {
				System.out.println("符合调度要求的：" + item.getMetadata().getName());
				// Get all nodes
				V1NodeList listNode = api.listNode(null, null, null, null, null, null, null, null, null);
				/*
				 * for (V1Node node : listNode.getItems()) {
				 * System.out.println("nodename = "node.getMetadata().getName()); }
				 */
				int nodeSize = listNode.getItems().size();
				// 随机选中
				V1Node chosen = listNode.getItems().get(new Random().nextInt(nodeSize));
				System.out.println("选中的node:" + chosen.getMetadata().getName());

				//item.getSpec().setNodeName(chosen.getMetadata().getName());
				
				V1Binding body = new V1Binding();
				body.setApiVersion("v1");
				body.setKind("Binding");
				
				V1ObjectMeta metadata  = new V1ObjectMeta();
				metadata.setName(item.getMetadata().getName());
				body.setMetadata(metadata  );
				
				
				V1ObjectReference target = new V1ObjectReference();
				target.setApiVersion("v1");
				target.setKind("Node");
				target.setName(chosen.getMetadata().getName());
				body.setTarget(target );
				
					
				String pretty=null;
				String dryRun=null;
				api.createNamespacedPodBinding(item.getMetadata().getName(), item.getMetadata().getNamespace(),
						body, dryRun, true, pretty);
				System.out.println(item.getMetadata().getName()+" 调度至："+chosen.getMetadata().getName());

			}
		}

	}

}
